package com.uet.datn.services;

import com.uet.datn.helpers.Utils;
import com.uet.datn.models.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MainService {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private JenkinService jenkinService;

    @Autowired
    private Utils utils;


    //toDo: get thong tin system tu file propeties: window, unix -> chay cmd or shell

    public String process(String jobName, int flag) throws IOException, InterruptedException {
//        excelService.processFile();
//        List<Student> students =  excelService.getStudentList();
//
//        if(flag == 1){ // java web project
//            //toDo: add to cmd
//        }else { //java normal
//
//        }

        //Dành cho test
        List<Student> students = new ArrayList<>();
        Student student = new Student();
        student.setStudentName("Phạm Thế Hùng");
        student.setLinkGithub("https://github.com/quecui/testJunit.git");
        students.add(student);

        Student student2 = new Student();
        student2.setStudentName("Phạm Ngọc Chinh");
        student2.setLinkGithub("https://github.com/nhs3108/SpringWebDemo.git");
        students.add(student2);

        Student student3 = new Student();
        student3.setStudentName("Phạm Truong");
        student3.setLinkGithub("https://github.com/nhs3108/SpringWebDemo122.git");
        students.add(student3);


        jobName = "Test_For_Student";
        jenkinService = new JenkinService();
        utils = new Utils();

      //  jenkinService.createJob(jobName); //tao job + template config.
        for (int i = 0; i < students.size(); i++){
            String successBuild = "Success: ";
            String failBuild = "Failure: ";
            students.get(i).setSuccesseTask(successBuild);
            students.get(i).setErrorMessage(failBuild);
            jenkinService.updateByField("url", "<url>" + students.get(i).getLinkGithub() + "</url>");
            jenkinService.updateConfig(jobName);

            jenkinService.buildNow(jobName);
            Thread.sleep(10000);

            jenkinService = new JenkinService();
            String log = jenkinService.getLogJob(jobName, 4); //+1

            String result = getStatusResult(log);
            if (result.equals("SUCCESS\r")) {
                successBuild += "Build: SUCCESS |-| ";
                //toDo: Test API. and update resultBuild
                students.get(i).setSuccesseTask(successBuild);
            }else {
                failBuild += findError(log);
                students.get(i).setErrorMessage(failBuild);
            }
        }

        //toDo: Ghi ket qua ra file txt
        utils.writeFile(students, "config-dir/result.txt");

        return "ok";
    }

    public String findError(String log) throws IOException {
        String errorMessage = "Build Fail: ";

        utils.writeFile("config-dir/log.txt", log);
        List<String> logs = utils.readFile("config-dir/log.txt");

        for(int i = 0; i < logs.size(); i++){
            String[] tmpArr = logs.get(i).split(":");
            if (tmpArr[0].equals("ERROR") || tmpArr[0].equals("FATAL")){
                errorMessage += logs.get(i) + "  --  ";
            }
        }

        return errorMessage + "\n";
    }

    public String getStatusResult(String log){
        int start = 0;
        int end = log.length() - 1;
        String result = "";

        for (int i = end; i >= end / 2; i--){
            if (log.charAt(i) == ' '){
                start = i + 1;
                result = log.substring(start, end);
                break;
            }
        }

        return result;
    }

    private String genScriptForWebProject(){
        String script = "mvn package ";

        return script;
    }

    private String genScriptForCoreProject(){
        String script = "";

        return script;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        MainService mainService = new MainService();
        mainService.process("x", 2);
    }

}
