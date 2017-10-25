package com.uet.datn.models.jenkins;

import java.util.List;

public class ListJob {
    private String _class;
    private List<Job> jobs;

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobList) {
        this.jobs = jobList;
    }
}
