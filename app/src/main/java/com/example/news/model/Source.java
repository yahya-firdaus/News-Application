package com.example.news.model;

public class Source {
    private String id;
    private String name;
    private String description;
    private String url;
    private String category;
    private String language;
    private String country;

    public Source(String vid, String vname, String vdescription, String vurl, String vcategory, String vlanguage, String vcountry) {
        this.id = vid;
        this.name = vname;
        this.description = vdescription;
        this.url = vurl;
        this.category = vcategory;
        this.language = vlanguage;
        this.country = vcountry;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getCategory() {
        return category;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }
}
