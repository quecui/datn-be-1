package com.uet.datn.controllers;

import com.uet.datn.dto.ScheduleDTO;
import com.uet.datn.models.jenkins.Job;
import com.uet.datn.models.jenkins.JobDetail;
import com.uet.datn.models.jenkins.ListJob;
import com.uet.datn.services.ExcelService;
import com.uet.datn.services.JenkinService;
import com.uet.datn.services.MainService;
import com.uet.datn.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8090")
@RestController
public class Controller {
    @Autowired
    private StorageService storageService;

    @Autowired
    private MainService mainService;

    @Autowired
    private JenkinService jenkinService;

    @RequestMapping(value = "/jenkins/file", method = RequestMethod.POST)
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return storageService.store(file);
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}/file", method = RequestMethod.GET)
    public String processFile(@PathVariable("jobName") String jobName, int flag) throws IOException, InterruptedException {
        return mainService.process(jobName, flag);
    }

    @RequestMapping(value = "/jenkins/jobs", method = RequestMethod.GET)
    public List<Job> getAllJobs(){
        return jenkinService.getListJob().getJobs();
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}", method = RequestMethod.GET)
    public String showDetail(@PathVariable("jobName") String jobName){
        return jenkinService.getJobDetail(jobName);
    }

    @RequestMapping(value = "/jenkins", method = RequestMethod.GET)
    public String init(){
        jenkinService.init();

        return "success";
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}/logs/{index}", method = RequestMethod.GET)
    public String[] getLogDetail(@PathVariable("jobName") String jobName, @PathVariable("index") int index){
        return jenkinService.formatLog(jenkinService.getLogJob(jobName, index));
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}/config", method = RequestMethod.GET)
    public String showConfigXML(@PathVariable("jobName") String jobName){
        return jenkinService.getAndWriteXmlFile(jobName);
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}/{description}", method = RequestMethod.POST)
    public String updateDescription(@PathVariable("jobName") String jobName, @PathVariable("description") String description) throws IOException {
        return jenkinService.updateDescription(jobName, description);
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}", method = RequestMethod.DELETE)
    public String deleteJob(@PathVariable("jobName") String jobName) throws IOException {
        return jenkinService.deleteJob(jobName);
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}/build", method = RequestMethod.POST)
    public String buildJob(@PathVariable("jobName") String jobName) throws IOException {
        return jenkinService.buildNow(jobName);
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}/schedule/{value}", method = RequestMethod.POST)
    public String scheduleSCMPolling(@PathVariable("jobName") String jobName, @PathVariable("value") String value) throws IOException {
        return jenkinService.scheduleSCMPolling(jobName, value);
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}/schedule", method = RequestMethod.POST)
    public String scheduleSCMPoll(@RequestBody ScheduleDTO scheduleDTO) throws IOException {
        jenkinService.processSchedule(scheduleDTO, false);
        return "ok";
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}/schedule/history")
    public List<String> getScheduleHistory(@PathVariable("jobName") String jobName) throws IOException {
        return jenkinService.getHistory(jobName);
    }

    @RequestMapping(value = "/jenkins/jobs/{jobName}/schedule/history", method = RequestMethod.PUT)
    public String updateScheduleHistory(@RequestBody List<String> strings, @PathVariable("jobName") String jobName){
        jenkinService.updateH(strings);
        return "ok";
    }


}
