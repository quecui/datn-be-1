package com.uet.datn.controllers;

import com.uet.datn.models.jenkins.Job;
import com.uet.datn.models.jenkins.JobDetail;
import com.uet.datn.models.jenkins.ListJob;
import com.uet.datn.services.ExcelService;
import com.uet.datn.services.JenkinService;
import com.uet.datn.services.MainService;
import com.uet.datn.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class Controller {
    @Autowired
    private StorageService storageService;

    @Autowired
    private MainService mainService;

    @Autowired
    private JenkinService jenkinService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return storageService.store(file);
    }

    @RequestMapping(value = "/process", method = RequestMethod.GET)
    public String processFile(){
        return mainService.process();
    }

    @RequestMapping(value = "/jenkins/jobs", method = RequestMethod.GET)
    public List<Job> getAllJobs(){
        return jenkinService.getListJob().getJobs();
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}", method = RequestMethod.GET)
    public JobDetail showDetail(@PathVariable("jobName") String jobName){
        return jenkinService.getJobDetail(jobName);
    }

    @RequestMapping(value = "/jenkins", method = RequestMethod.GET)
    public String init(){
        jenkinService.init();

        return "success";
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}/logs/{index}", method = RequestMethod.GET)
    public String getLogDetail(@PathVariable("jobName") String jobName, @PathVariable("index") int index){
        return jenkinService.getLogJob(jobName, index);
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}/config", method = RequestMethod.GET)
    public String showConfigXML(@PathVariable("jobName") String jobName){
        return jenkinService.getAndWriteXmlFile(jobName);
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}/{description}", method = RequestMethod.POST)
    public String updateDescription(@PathVariable("jobName") String jobName, @PathVariable("description") String description) throws IOException {
        return jenkinService.updateDescription(jobName, description);
    }

}
