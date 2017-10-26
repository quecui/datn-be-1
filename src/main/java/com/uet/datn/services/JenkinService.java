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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
    private DefaultHttpClient client;
    private BasicHttpContext context;
    private int maxBuild;

    public JenkinService(){
        credential = new Credential();
        helper = new JenkinsHelper();
        utils = new Utils();
        init();
    }

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
//        if (index > maxBuild || index == 0)
//            return "Over Max Size";

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

        System.out.println("get Log Sucess!!");
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
        String url = helper.getJobUrl(jobName, JenkinsHelper.Action.UPDATE);
        newDescription = "<description>" + newDescription + "</description>";

        if (updateByField("description", newDescription).equals("failure"))
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

    public String updateConfig(String jobName) throws IOException {
        String url = helper.getJobUrl(jobName, JenkinsHelper.Action.UPDATE);

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

    public String updateByField(String fieldName, String newValue) throws IOException {
        List<String> contents = utils.readFile("config-dir/config.xml");

        boolean isAddNew = true;

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
                isAddNew = false;
            }
        }

        if (isAddNew){
            int maxSize = contents.size() - 1;
            String tmp = contents.get(maxSize);
            contents.remove(maxSize);
            contents.add(newValue);
            contents.add(tmp);
        }

        if (!utils.writeFile("config-dir/config.xml", contents))
            return "failure";
        return "success";
    }

    public String createJob(String jobName) throws IOException {

        String url = helper.getJobUrl(jobName, JenkinsHelper.Action.CREATE);

        if (!updateCredentialIdToXml().equals("success"))
            return "failure";

        String buildToken = "<authToken>stormspirit</authToken>";
        if(updateByField("authToken", buildToken).equals("failure"))
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

        return updateByField("credentialsId", adminCredentialId);
    }

    public String deleteJob(String jobName) throws IOException {
        String url = helper.JENKINS_URL + helper.getJobUrl(jobName, JenkinsHelper.Action.DELETE);
        HttpPost postReq = new HttpPost(url);
        postReq.setHeader(CSRFKey, CSRFToken);

        HttpResponse response = client.execute(postReq, context);
        int status = response.getStatusLine().getStatusCode();

        if (status == 302){
            System.out.println("Delete Successfully !");
            return "success";
        }

        System.out.println("Job doesn't exist");
        return "failure";
    }

    public String buildNow(String jobName) throws IOException {
        String buildToken = "stack";
        String urlJob = helper.JENKINS_URL + "/job/" + jobName + "/build?token=" + buildToken;

        HttpGet getRequest = new HttpGet(urlJob);
        HttpResponse response = client.execute(getRequest, context);
        int status = response.getStatusLine().getStatusCode();

        if (status == 201){
            System.out.println("Job: " + jobName.toUpperCase() + " has built !!!");
            return "success";
        }else{
            System.out.println("Exception !!!");
            return "failure";
        }
    }

    // * * * * * : Build moi 5 phut
    // 20 16-17/1 * * 1-5: build moi 1 h 1 lan bat dau tu 16h20 - 17h20 cac ngay trong tuan
    public String scheduleSCMPolling(String jobName, String value) throws IOException {
        switch (value){
            case "1": value = "* * * * *"; break;

            case "2": value = "20 16-17/1 * * 1-5"; break;

            default: return "failure";
        }

        String url = helper.JENKINS_URL + "/job/" + jobName + "/polling";

        HttpPost postReq = new HttpPost(url);
        postReq.setHeader(CSRFKey, CSRFToken);
        List params = new ArrayList();
        params.add(new BasicNameValuePair("_.scmpoll_spec", value));
        UrlEncodedFormEntity paramEntity = new UrlEncodedFormEntity(params);

        postReq.setEntity(paramEntity);
        HttpResponse response = client.execute(postReq, context);
        int status = response.getStatusLine().getStatusCode();

        if (status == 200)
            return "success";

        return "failure";
    }

    public static void main(String[] args) throws IOException {
        JenkinService jenkinService = new JenkinService();

        jenkinService.init();
        jenkinService.createJob("hungpham2");
    }



}
