package com.uet.datn.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uet.datn.helpers.JenkinsHelper;
import com.uet.datn.helpers.Utils;
import com.uet.datn.helpers.validator.Credential;
import com.uet.datn.models.jenkins.Job;
import com.uet.datn.models.jenkins.JobDetail;
import com.uet.datn.models.jenkins.ListJob;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@DependsOn("Credential")
@Service
public class JenkinService {

   @Autowired
    private Credential credential;

    @Autowired
    private JenkinsHelper helper;

    @Autowired
    private Utils utils;

    private String CSRFToken = "";
    private String CSRFKey = "";
    private DefaultHttpClient client;;
    private BasicHttpContext context;
    private int maxBuild;

    public void init(){
        client = credential.setCredentialForJenkins(helper.USERNAME, helper.PASSWORD);
        context = credential.setContext();

        getCSRFToken();
    }

    private String getCSRFToken(){
        try {
            String url = helper.getCSRFTokenUrl();

            HttpGet getReq = new HttpGet(url);
            HttpResponse response = client.execute(getReq, context);

            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(response.getEntity());
            System.out.println(json);

            CSRFToken = json.split(":")[1];
            CSRFKey = json.split(":")[0];
        }catch (Exception e){
            System.out.println("Csrf Exception");
        }

        return CSRFKey;
    }

    public ListJob getListJob(){
        ListJob jobs = new ListJob();

        try {
            String url = helper.getAllJobUrl();

            HttpGet getReq = new HttpGet(url);
            HttpResponse response = client.execute(getReq, context);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(response.getEntity());

            ObjectMapper mapper = new ObjectMapper();
            jobs = mapper.readValue(json, ListJob.class);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return jobs;
    }

    public JobDetail getJobDetail(String jobName){
        JobDetail jobDetail = new JobDetail();

        try {
            String url = helper.getJobDetailUrl(jobName);

            HttpGet getReq = new HttpGet(url);
            HttpResponse response = client.execute(getReq, context);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(response.getEntity());

            ObjectMapper mapper = new ObjectMapper();
            jobDetail = mapper.readValue(json, JobDetail.class);

            maxBuild = jobDetail.getBuilds().size();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return jobDetail;
    }

    public String getLogJob(String jobName, int index){
        if (index > maxBuild || index == 0)
            return "Over Max Size";

        String log = "";

        try {
            String url = helper.getLogUrl(jobName, index);

            HttpGet req = new HttpGet(url);
            req.setHeader(CSRFKey, CSRFToken);
            HttpResponse response = client.execute(req, context);

            HttpEntity entity = response.getEntity();
            log = EntityUtils.toString(response.getEntity());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return log;
    }

    public String getAndWriteXmlFile(String jobName){
        String url = helper.getConfigXmlUrl(jobName);
        String textXML = "";

        try {
            HttpGet req = new HttpGet(url);
            req.setHeader(CSRFKey, CSRFToken);
            HttpResponse response = client.execute(req, context);

            HttpEntity entity = response.getEntity();
            textXML = EntityUtils.toString(response.getEntity());

            utils.writeFile("upload-dir/config.xml", textXML);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return textXML;
    }

    public String updateDescription(String jobName, String newDescription) throws IOException {
        String url = helper.getDescriptionUrl(jobName, JenkinsHelper.Action.UPDATE);
        newDescription = "<description>" + newDescription + "</description>";

        List<String> content = updateByField("description", newDescription);
        if (!utils.writeFile("upload-dir/config.xml", content))
            return "failure";

        HttpPost postReq = new HttpPost(url);
        postReq.setHeader(CSRFKey, CSRFToken);
        postReq.setHeader("Content-Type", "text/xml");

        File file = new File("upload-dir/config.xml");
        byte[] bFile = Files.readAllBytes(file.toPath());
        HttpEntity entity = new ByteArrayEntity(bFile);
        postReq.setEntity(entity);

        HttpResponse response = client.execute(postReq, context);
        int status = response.getStatusLine().getStatusCode();

        if (status == 200){
            return "success";
        }else {
            return "failure";
        }
    }

    public List<String> updateByField(String fieldName, String newValue) throws IOException {
        List<String> contents = utils.readFile("config-dir/config.xml");

        for (int i = 0; i < contents.size(); i++){
            String tmp = contents.get(i);
            tmp = tmp.trim();

            int start = 1;
            int end = 1;

            for (int j = 1; j < tmp.length(); j++){
                if (tmp.charAt(j) == '>'){
                    end = j;
                    break;
                }
            }

            String childTmp = tmp.substring(start, end);
            if (childTmp.equals(fieldName)){
                contents = utils.replace(contents, i, newValue);
                return contents;
            }
        }

        return new ArrayList<String>();
    }

    public String createJob(String jobName) throws IOException {
//        CSRFKey = "Jenkins-Crumb";
//        CSRFToken = "e4dda56a4142808e3c946b6d337ca467";
//        helper = new JenkinsHelper();
//        credential = new Credential();
//        client = credential.setCredentialForJenkins(helper.USERNAME, helper.PASSWORD);
//        context = credential.setContext();

        String url = helper.getJobUrl(jobName, JenkinsHelper.Action.CREATE);

        if (!updateCredentialIdToXml().equals("success"))
            return "failure";

        HttpPost postReq = new HttpPost(url);
        postReq.setHeader("Content-Type", "text/xml");
        postReq.setHeader(CSRFKey, CSRFToken);

        File file = new File("config-dir/config.xml");
        byte[] bFile = Files.readAllBytes(file.toPath());
        HttpEntity entity = new ByteArrayEntity(bFile);
        postReq.setEntity(entity);

        HttpResponse response = client.execute(postReq, context);
        int status = response.getStatusLine().getStatusCode();

        if(status == 200){
            System.out.println("Create Job Successfully !!!");
            return "success";
        }else {
            System.out.println("Fail to create job !!! with code = " + status);
            return "failure";
        }
    }

    private String updateCredentialIdToXml() throws IOException {
        String adminCredentialId = "<credentialsId>" + helper.CREDENTIAL_ID + "</credentialsId>";

        List<String> content = updateByField("credentialsId", adminCredentialId);
        if (!utils.writeFile("config-dir/config.xml", content))
            return "failure";

        return "success";
    }


    public static void main(String[] args) throws IOException {
        JenkinService jenkinService = new JenkinService();
        System.out.println(jenkinService.updateCredentialIdToXml());
        jenkinService.createJob("test123");
    }

}
