package com.svgi.lectureschedule.model;

public class BookDetail {
    private String name;
    private String author;
    private int total;
    private int available;
    private String keptAt;

    public BookDetail() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public String getKeptAt() {
        return keptAt;
    }

    public void setKeptAt(String keptAt) {
        this.keptAt = keptAt;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }
}
