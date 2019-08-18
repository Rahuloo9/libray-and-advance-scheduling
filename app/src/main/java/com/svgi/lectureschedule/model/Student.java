package com.svgi.lectureschedule.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Student implements Serializable {
    private String name, email, branch, collage, batch, year;
    private ArrayList<String> isssuedBooks;

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
        isssuedBooks = new ArrayList<>();
    }

    public ArrayList<String> getIsssuedBooks() {
        return isssuedBooks;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public void setIsssuedBooks(ArrayList<String> isssuedBooks) {
        this.isssuedBooks = isssuedBooks;
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
