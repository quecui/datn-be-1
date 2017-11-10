package com.uet.datn.dto;

/**
 * Created by stormspirit on 11/7/2017.
 */
public class ScheduleDTO {
    private String day;
    private String hour;
    private String jobName;
    private String valueRepeat;

    public String getValueRepeat() {
        return valueRepeat;
    }

    public void setValueRepeat(String valueRepeat) {
        this.valueRepeat = valueRepeat;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
