package com.example.myapplication;

public class menu1Item {
    private String title;
    private String img_url;
    private String content;
    private String genre;
    private String rating;
    private String sysdate;

    public menu1Item(String title, String img_url, String content, String genre, String rating, String sysdate) {
        this.title = title;
        this.img_url = img_url;
        this.content = content;
        this.genre = genre;
        this.rating = rating;
        this.sysdate = sysdate;
    }
    public menu1Item(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSysdate() {
        return sysdate;
    }

    public void setSysdate(String sysdate) {
        this.sysdate = sysdate;
    }
}
