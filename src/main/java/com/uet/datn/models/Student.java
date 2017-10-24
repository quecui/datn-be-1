package com.uet.datn.models;

public class Student {
    private String studentName;
    private String linkGithub;
    private String successeTask;
    private String errorTask;
    private String errorMessage;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getLinkGithub() {
        return linkGithub;
    }

    public void setLinkGithub(String linkGithub) {
        this.linkGithub = linkGithub;
    }

    public String getSuccesseTask() {
        return successeTask;
    }

    public void setSuccesseTask(String successeTask) {
        this.successeTask = successeTask;
    }

    public String getErrorTask() {
        return errorTask;
    }

    public void setErrorTask(String errorTask) {
        this.errorTask = errorTask;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
