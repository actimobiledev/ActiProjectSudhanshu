package com.actiknow.actiproject.model;

/**
 * Created by l on 17/01/2018.
 */

public class AcceptedJobs {
    int id,job_post,job_hire;
    String job_id,title,snippet,country,status,budget,job_url;

    public AcceptedJobs(int id, String job_id, String title, String budget , String snippet, String country, String status , int job_post, int job_hire, String job_url) {
        this.id = id;
        this.job_id = job_id;
        this.title = title;
        this.snippet = snippet;
        this.country = country;
        this.status = status;
        this.budget = budget;
        this.job_post = job_post;
        this.job_hire = job_hire;
        this.job_url = job_url;
    }

    public String getJob_url() {
        return job_url;
    }

    public void setJob_url(String job_url) {
        this.job_url = job_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJob_id() {
        return job_id;
    }

    public void setJob_id(String job_id) {
        this.job_id = job_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public int getJob_post() {
        return job_post;
    }

    public void setJob_post(int job_post) {
        this.job_post = job_post;
    }

    public int getJob_hire() {
        return job_hire;
    }

    public void setJob_hire(int job_hire) {
        this.job_hire = job_hire;
    }
}


