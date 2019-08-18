package com.svgi.lectureschedule.model;

import java.util.Date;

public class BookIssuedDetail {
    private String author;
    private String BookId;
    private Date issueDate;
    private Date submitDate;
    private String title;

    public BookIssuedDetail(String author, String bookId, Date issueDate, Date submitDate, String title) {
        this.author = author;
        BookId = bookId;
        this.issueDate = issueDate;
        this.submitDate = submitDate;
        this.title = title;
    }

    public BookIssuedDetail() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookId() {
        return BookId;
    }

    public void setBookId(String bookId) {
        BookId = bookId;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }
}
