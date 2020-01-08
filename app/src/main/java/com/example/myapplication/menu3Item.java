package com.example.myapplication;

public class menu3Item {
    private String title;
    private String img_url;
    private String detail_link;
    private String release;
    private String director;


    public menu3Item(String title, String imgurl, String link, String release, String director){
        this.title = title;
        this.img_url = imgurl;
        this.detail_link = link;
        this.release = release;
        this.director = director;
    }


    public String getTitle() {
        return title;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getDetail_link() {
        return detail_link;
    }

    public String getRelease() {
        return release;
    }

    public String getDirector() {
        return director;
    }
}
