package com.uet.datn.helpers;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service("JenkinsHelper")
public class JenkinsHelper {

    public final String JENKINS_URL = "http://localhost:8080";
    public final String USERNAME = "admin";
    public final String PASSWORD = "hunghp1502";
//    public final String CREDENTIAL_ID = "d02b13e3-6cea-47f1-b63d-6ac2c21ae5db";
    public final String CREDENTIAL_ID = "867bfe13-043d-41ab-b37f-c2bca486f1c6";
    public enum Action {CREATE, ENABLE, DISABLE, DELETE, UPDATE, VIEW};

    private final String JOB = "/api/json?tree=jobs[name,color,url,description]";
    private final String CRUMB = "/crumbIssuer/api/xml?xpath=concat(//crumbRequestField,%22:%22,//crumb)";
    private final String RESTART = "/restart";
    private final String PLUGIN = "/pluginManager/prevalidateConfig";
    private final String USER = "/credentials/store/system/domain/_/createCredentials";

    public String getBuildJobUrl(String jobName, String buildToken){
        return JENKINS_URL + "/job/" + jobName + "/build?token=" + buildToken;
    }

    public String getAllJobUrl(){
        return JENKINS_URL + JOB;
    }

    public String getJobDetailUrl(String jobName){
        return JENKINS_URL + "/job/" + jobName + "/api/json?pretty=true";
    }

    public String getUserUrl(){
        return JENKINS_URL + USER;
    }

    public String getJobUrl(String jobName, Action action){
        switch (action){
            case CREATE: return JENKINS_URL + "/createItem?name=" + jobName;

            case DELETE: return JENKINS_URL + "/job/" + jobName + "/doDelete";

            case ENABLE: return JENKINS_URL + "/job/" + jobName + "/enable";

            case DISABLE: return JENKINS_URL + "/job/" + jobName + "/disable";

            case UPDATE: return JENKINS_URL + "/job/" + jobName + "/config.xml";

            case VIEW: return JENKINS_URL + "/job/demo/description";

            default: return "No Matched";
        }
    }

    public String getCSRFTokenUrl(){
        return JENKINS_URL + CRUMB;
    }

    public String getSCMPollingUrl(String jobName){
        return JENKINS_URL + "/job/" + jobName + "/polling";
    }

    public String getJobCopierUrl(String oldJob, String newJob){
        return JENKINS_URL + "/createItem?name=" + oldJob + "&mode=copy&from=" + newJob;
    }

    public String getShutDownUrl(){
        return JENKINS_URL + "/quietDown";
    }

    public String getLogUrl(String jobName, int index){
        return JENKINS_URL + "/job/" + jobName + "/" + index + "/logText/progressiveText?start=0";
    }

    public String getPluginUrl(){
        return JENKINS_URL + PLUGIN;
    }

    public String getConfigXmlUrl(String jobName){
        return JENKINS_URL + "/job/" + jobName + "/config.xml";
    }

    public String getEstimateTimeUrl(String jobName, int index){
        return JENKINS_URL + "/job/" + jobName + "/" + index + "/api/json?pretty=true";
    }
}
