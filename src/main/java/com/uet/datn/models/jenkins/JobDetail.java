package com.uet.datn.models.jenkins;

import java.util.List;

public class JobDetail {
    private String _class;
    private List<Action> actions;
    private String description;
    private String displayName;
    private String displayNameOrNull;
    private String fullDisplayName;
    private String fullName;
    private String name;
    private String url;
    private String buildable;
    private List<Build> builds;
    private String color;
    private Build firstBuild;
    private List<HealthReport> healthReport;
    private boolean inQueue;
    private boolean keepDependencies;
    private Build lastBuild;
    private Build lastCompletedBuild;
    private Build lastFailedBuild;
    private String lastStableBuild;
    private String lastSuccessfulBuild;
    private String lastUnstableBuild;
    private Build lastUnsuccessfulBuild;
    private int nextBuildNumber;
    private List<Action> property;
    private QueueItem queueItem;
    private boolean concurrentBuild;
    private List<DownstreamProjects> downstreamProjects;
    private Action scm;
    private List<DownstreamProjects> upstreamProjects;

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayNameOrNull() {
        return displayNameOrNull;
    }

    public void setDisplayNameOrNull(String displayNameOrNull) {
        this.displayNameOrNull = displayNameOrNull;
    }

    public String getFullDisplayName() {
        return fullDisplayName;
    }

    public void setFullDisplayName(String fullDisplayName) {
        this.fullDisplayName = fullDisplayName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBuildable() {
        return buildable;
    }

    public void setBuildable(String buildable) {
        this.buildable = buildable;
    }

    public List<Build> getBuilds() {
        return builds;
    }

    public void setBuilds(List<Build> builds) {
        this.builds = builds;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Build getFirstBuild() {
        return firstBuild;
    }

    public void setFirstBuild(Build firstBuild) {
        this.firstBuild = firstBuild;
    }

    public List<HealthReport> getHealthReport() {
        return healthReport;
    }

    public void setHealthReport(List<HealthReport> healthReport) {
        this.healthReport = healthReport;
    }

    public boolean isInQueue() {
        return inQueue;
    }

    public void setInQueue(boolean inQueue) {
        this.inQueue = inQueue;
    }

    public boolean isKeepDependencies() {
        return keepDependencies;
    }

    public void setKeepDependencies(boolean keepDependencies) {
        this.keepDependencies = keepDependencies;
    }

    public Build getLastBuild() {
        return lastBuild;
    }

    public void setLastBuild(Build lastBuild) {
        this.lastBuild = lastBuild;
    }

    public Build getLastCompletedBuild() {
        return lastCompletedBuild;
    }

    public void setLastCompletedBuild(Build lastCompletedBuild) {
        this.lastCompletedBuild = lastCompletedBuild;
    }

    public Build getLastFailedBuild() {
        return lastFailedBuild;
    }

    public void setLastFailedBuild(Build lastFailedBuild) {
        this.lastFailedBuild = lastFailedBuild;
    }

    public String getLastStableBuild() {
        return lastStableBuild;
    }

    public void setLastStableBuild(String lastStableBuild) {
        this.lastStableBuild = lastStableBuild;
    }

    public String getLastSuccessfulBuild() {
        return lastSuccessfulBuild;
    }

    public void setLastSuccessfulBuild(String lastSuccessfulBuild) {
        this.lastSuccessfulBuild = lastSuccessfulBuild;
    }

    public String getLastUnstableBuild() {
        return lastUnstableBuild;
    }

    public void setLastUnstableBuild(String lastUnstableBuild) {
        this.lastUnstableBuild = lastUnstableBuild;
    }

    public Build getLastUnsuccessfulBuild() {
        return lastUnsuccessfulBuild;
    }

    public void setLastUnsuccessfulBuild(Build lastUnsuccessfulBuild) {
        this.lastUnsuccessfulBuild = lastUnsuccessfulBuild;
    }

    public int getNextBuildNumber() {
        return nextBuildNumber;
    }

    public void setNextBuildNumber(int nextBuildNumber) {
        this.nextBuildNumber = nextBuildNumber;
    }

    public List<Action> getProperty() {
        return property;
    }

    public void setProperty(List<Action> property) {
        this.property = property;
    }

    public QueueItem getQueueItem() {
        return queueItem;
    }

    public void setQueueItem(QueueItem queueItem) {
        this.queueItem = queueItem;
    }

    public boolean isConcurrentBuild() {
        return concurrentBuild;
    }

    public void setConcurrentBuild(boolean concurrentBuild) {
        this.concurrentBuild = concurrentBuild;
    }

    public List<DownstreamProjects> getDownstreamProjects() {
        return downstreamProjects;
    }

    public void setDownstreamProjects(List<DownstreamProjects> downstreamProjects) {
        this.downstreamProjects = downstreamProjects;
    }

    public Action getScm() {
        return scm;
    }

    public void setScm(Action scm) {
        this.scm = scm;
    }

    public List<DownstreamProjects> getUpstreamProjects() {
        return upstreamProjects;
    }

    public void setUpstreamProjects(List<DownstreamProjects> upstreamProjects) {
        this.upstreamProjects = upstreamProjects;
    }
}
