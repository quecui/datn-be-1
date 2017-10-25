package com.uet.datn.models.jenkins;

public class QueueItem {
    String _class;
    long id;
    int idLegacy;
    boolean blocked;
    boolean buildable;
    boolean stuck;
    long inQueueSince;
    String inQueueForString;
    String params;

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIdLegacy() {
        return idLegacy;
    }

    public void setIdLegacy(int idLegacy) {
        this.idLegacy = idLegacy;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isBuildable() {
        return buildable;
    }

    public void setBuildable(boolean buildable) {
        this.buildable = buildable;
    }

    public boolean isStuck() {
        return stuck;
    }

    public void setStuck(boolean stuck) {
        this.stuck = stuck;
    }

    public long getInQueueSince() {
        return inQueueSince;
    }

    public void setInQueueSince(long inQueueSince) {
        this.inQueueSince = inQueueSince;
    }

    public String getInQueueForString() {
        return inQueueForString;
    }

    public void setInQueueForString(String inQueueForString) {
        this.inQueueForString = inQueueForString;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
