package com.uet.datn.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uet.datn.dto.ScheduleDTO;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
        init1();

        getCSRFToken();
    }

    public void init1(){
        client = credential.setCredentialForJenkins(helper.USERNAME, helper.PASSWORD);
        context = credential.setContext();
    }

    public List<String> getHistory(String jobName) throws IOException {
        return utils.readFile(jobName);
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

    public String getJobDetail(String jobName){
        JobDetail jobDetail = new JobDetail();
        String json = "";
        try {
            String url = helper.getJobDetailUrl(jobName);

            HttpGet getReq = new HttpGet(url);
            HttpResponse response = client.execute(getReq, context);
            HttpEntity entity = response.getEntity();
            json = EntityUtils.toString(response.getEntity());
            json = formatJson(json);
            ObjectMapper mapper = new ObjectMapper();
            jobDetail = mapper.readValue(json, JobDetail.class);

            maxBuild = jobDetail.getBuilds().size();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return json;
    }

    private String formatJson(String json){
        String[] splitJson = json.split(",");
        ArrayList<String> jsonList = new ArrayList<>(Arrays.asList(splitJson));
        jsonList.remove(1);
        jsonList.remove(1);
        jsonList.remove(1);
        jsonList.remove(1);

        json = "";
        for (int i = 0; i < jsonList.size(); i++){
            json += jsonList.get(i);
            if (i + 1 < jsonList.size()){
                json += ",";
            }
        }

        return json;
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

    public String[] formatLog(String log){
        return log.split("\r\n");
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
        String url = helper.getJobUrl(jobName, JenkinsHelper.Action.DELETE);
        System.out.println(url);
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
        int nextBuild = getBuildIndex(jobName);
        String urlJob = helper.getBuildJobUrl(jobName, buildToken);

        HttpGet getRequest = new HttpGet(urlJob);
        HttpResponse response = client.execute(getRequest, context);
        int status = response.getStatusLine().getStatusCode();

        if (status == 201){

            System.out.println("Job: " + jobName.toUpperCase() + " has built !!!");

            return getEstimateTime(jobName, nextBuild);
        }else{
            System.out.println("Exception !!!");
            return "failure";
        }
    }

    private int getBuildIndex(String jobName) throws IOException {
        String url = helper.getJobDetailUrl(jobName);
        JobDetail jobDetail = new JobDetail();

        HttpGet getReq = new HttpGet(url);
        HttpResponse response = client.execute(getReq, context);
        HttpEntity entity = response.getEntity();
        String json = EntityUtils.toString(response.getEntity());
        json = formatJson(json);
        ObjectMapper mapper = new ObjectMapper();
        jobDetail = mapper.readValue(json, JobDetail.class);

        return jobDetail.getNextBuildNumber();
    }

    private String getEstimateTime(String jobName, int index) throws IOException {
        String json = "";
        String url = helper.getEstimateTimeUrl(jobName, index);
        HttpGet getRequest = new HttpGet(url);

        while (true){
            System.out.println("XY");

            HttpResponse response = client.execute(getRequest, context);
            int status = response.getStatusLine().getStatusCode();

            if (status == 200){
                System.out.println("OK Xy");
                HttpEntity entity = response.getEntity();
                json = EntityUtils.toString(response.getEntity());
                break;
            }
            init1();
        }
        return json;
    }


//    <triggers>
//<hudson.triggers.SCMTrigger>
//<spec>* * * * *</spec>
//<ignorePostCommitHooks>false</ignorePostCommitHooks>
//</hudson.triggers.SCMTrigger>
//</triggers>

    // * * * * * : Build moi 5 phut
    // 20 16-17/1 * * 1-5: build moi 1 h 1 lan bat dau tu 16h20 - 17h20 cac ngay trong tuan
    public String scheduleSCMPolling(String jobName, String value) throws IOException {
        String json = getConfig(jobName);
        String[] xmls = json.split("\n");

        boolean isTrigger = false;
        boolean isExist = false;
        int triggerIndex = 0;
        for (int i = 0; i < xmls.length; i++){
            String tmp = xmls[i];
            if (tmp.length() == 10 && tmp.substring(1, 10).equals("triggers")){
                isTrigger = true;
                triggerIndex = i;
            }

            if(isTrigger == true && tmp.substring(1, 5).equals("spec") && tmp.charAt(5) != '/'){
                xmls[i] = "<spec>" + value +"</spec>";
                isExist = true;
                break;
            }
        }

        if (isTrigger && isExist) {
            //todo: Noi phan tu thanh string
            String ok = covertFromXML(new ArrayList<String>(Arrays.asList(xmls)));
            utils.writeFile("config.xml", ok);
            updateConfig(jobName);
            utils.deleteFile("config.xml");
            return "ok";

        }

        ArrayList<String> xmlsList = new ArrayList<>(Arrays.asList(xmls));
        ArrayList<String> component = triggerComponent(value, isTrigger);
        if (isTrigger == false){
            xmlsList.remove("</project>");
            for(String str:component){
                xmlsList.add(str);
            }
            xmlsList.add("</project>");
            //todo: Noi
            String ok = covertFromXML(xmlsList);
            utils.writeFile("config.xml", ok);
            updateConfig(jobName);
            utils.deleteFile("config.xml");
            return "ok";
        }

        if (isTrigger == true){
            for(int i = component.size() - 1; i >=0; i--){
                xmlsList.add(triggerIndex + 1, component.get(i));
            }

            String ok = covertFromXML(xmlsList);
            utils.writeFile("config.xml", ok);
            updateConfig(jobName);
            utils.deleteFile("config.xml");
            return "ok";
        }


        //todo: fix
        return "failure";
    }

    private static String covertFromXML(ArrayList<String> xml){
        String result = "";
        for(int i = 0; i < xml.size(); i++){
            result += xml.get(i);
            if (i + 1 < xml.size()) {
                result += "\n";
            }
        }

        return result;
    }

    private ArrayList<String> triggerComponent(String value, boolean flag){
        ArrayList<String> trigger = new ArrayList<>();

        if (flag == false){
            trigger.add("<triggers>");
        }

        trigger.add("<hudson.triggers.SCMTrigger>");
        trigger.add("<spec>" + value +"</spec>");
        trigger.add("<ignorePostCommitHooks>false</ignorePostCommitHooks>");
        trigger.add("</hudson.triggers.SCMTrigger>");

        if(flag == false){
            trigger.add("</triggers>");
        }

        return trigger;
    }


    private String getConfig(String jobName) throws IOException {
        String url = "http://localhost:8080/jobs/" + jobName + "/config.xml";
        HttpGet req = new HttpGet(url);
        req.setHeader(CSRFKey, CSRFToken);
        HttpResponse response = client.execute(req, context);
        HttpEntity entity = response.getEntity();
        String json = EntityUtils.toString(response.getEntity());
        return json;
    }

    public void processSchedule(ScheduleDTO scheduleDTO, boolean isRepeat) throws IOException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = simpleDateFormat.format(new Date()).toString();
        if(currentDate.equals(scheduleDTO.getDay())){
            String timeData = processTimeData(scheduleDTO.getHour(), scheduleDTO.getValueRepeat());
            scheduleSCMPolling(scheduleDTO.getJobName(), timeData);
        }else {
            if(isRepeat == false){
                writeFile(scheduleDTO);
                //toDo: Ham lap lich -> xong
            }

        }
    }

    //todo: sap xep folder cac thanh phan
    private void writeFile(ScheduleDTO scheduleDTO) throws IOException {
        String value = scheduleDTO.getDay() + "-" + scheduleDTO.getHour() + "-" + scheduleDTO.getValueRepeat();
        File file = new File("scheduler/" + scheduleDTO.getJobName());
        if (!file.exists()){
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file, true);
        fw.write(value + "\n");
        fw.close();
    }

    //todo: dùng để update lại history.
    public void writeFile(List<ScheduleDTO> scheduleDTOS) throws IOException {
        File file = new File(scheduleDTOS.get(0).getJobName());
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.close();
        if (scheduleDTOS.size() == 0)
            return;

        for (int i = 0; i < scheduleDTOS.size(); i++){
            writeFile(scheduleDTOS.get(i));
        }
    }

    public void updateH(List<String> strings){
        //convert DTO
        //goi Ham writeFile
    }
    //@schedule
    public void scheduler() throws IOException {
        // lay all cac file
        //doc tung file 1
        // so sanh voi ngay hien tai
        // update scm poll
        File folder = new File("/Users/you/folder/");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++){
            List<String> datas = utils.readFile(listOfFiles[i].getName());
            // tach date - so sanh
            for (int j = 0; j < datas.size(); j++){
                String date = datas.get(0).split("-")[0];
                String hour = datas.get(0).split("-")[1];
                String valueRepeat = datas.get(0).split("-")[2];
                ScheduleDTO scheduleDTO = new ScheduleDTO();
                scheduleDTO.setDay(date);
                scheduleDTO.setHour(hour);
                scheduleDTO.setValueRepeat(valueRepeat);
                processSchedule(scheduleDTO, true);
            }
        }
    }

    private String processTimeData(String hour, String repeat){
        LocalDateTime now = LocalDateTime.now();
        int currentDay = now.getDayOfMonth();
        int currentMonth = now.getMonthValue();
        String result = "TZ=Asia/Saigon H " + hour;

        if (repeat.equals("no")) {
            result += " " + currentDay + " " + currentMonth + " *";
            return result;
        }
        if (repeat.equals("repeatWeek")){
            int lastDay = currentDay + 7;
            if (lastDay > 30){
                lastDay = 30;
            }

            result += " " + currentDay + "-" + lastDay + " " + currentMonth + " *";
            return result;
        }

        if (repeat.equals("repeatMonth")){
            int lastDay = 30;
            result += " " + currentDay + "-" + lastDay + " " + currentMonth + " *";
            return result;
        }

        return result;
    }


    public static void main(String[] args) throws IOException, ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now.getDayOfMonth());
    }



}
