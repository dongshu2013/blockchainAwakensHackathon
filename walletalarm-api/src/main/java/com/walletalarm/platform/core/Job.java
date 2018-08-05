package com.walletalarm.platform.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public class Job {
    private int jobId;
    private JobType type;
    private JobStatus status;
    private String comment;
    private Timestamp createdTime;
    private Timestamp modifiedTime;
    private boolean isActive;

    public Job() {
    }

    public Job(int jobId, JobType type, JobStatus status, String comment, Timestamp createdTime, Timestamp modifiedTime,
               boolean isActive) {
        this.jobId = jobId;
        this.type = type;
        this.status = status;
        this.comment = comment;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.isActive = isActive;
    }

    @JsonProperty
    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    @JsonProperty
    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    @JsonProperty
    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    @JsonProperty
    public Timestamp getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Timestamp modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @JsonProperty
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @JsonProperty
    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    @JsonProperty
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
