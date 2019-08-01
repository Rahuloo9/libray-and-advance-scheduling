package com.svgi.lectureschedule.feature;

import java.io.Serializable;

public class Student implements Serializable {
    private String name,email,branch,collage,batch,year;

    public Student(String name, String email, String branch, String collage, String batch, String year) {
        this.name = name;
        this.email = email;
        this.branch = branch;
        this.collage = collage;
        this.batch = batch;
        this.year = year;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Student() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCollage() {
        return collage;
    }

    public void setCollage(String collage) {
        this.collage = collage;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
