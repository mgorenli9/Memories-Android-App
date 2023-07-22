package com.mehmet.memories.model;

public class Post {

    public String email;
    public String comment;
    public String title;
    public String downloadUrl;

    public Post(String email, String comment, String title, String downloadUrl) {
        this.email = email;
        this.comment = comment;
        this.title = title;
        this.downloadUrl = downloadUrl;
    }

}
