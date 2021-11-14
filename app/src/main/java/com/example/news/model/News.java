package com.example.news.model;

public class News {
    private String source_id;
    private String source_name;
    private String author;
    private String title;
    private String description;
    private String url;
    private String url_image;
    private String published_at;
    private String content;

    public News(String vsource_id, String vsource_name, String vauthor, String vtitle, String vdescription, String vurl, String vurl_image, String vpublished_at, String vcontent) {
        this.source_id = vsource_id;
        this.source_name = vsource_name;
        this.author = vauthor;
        this.title = vtitle;
        this.description = vdescription;
        this.url = vurl;
        this.url_image = vurl_image;
        this.published_at = vpublished_at;
        this.content = vcontent;
    }

    public String getSourceId() {
        return source_id;
    }

    public String getSourceName() {
        return source_name;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlImage() {
        return url_image;
    }

    public String getPublishedAt() {
        return published_at;
    }

    public String getContent() {
        return content;
    }
}
