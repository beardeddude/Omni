package com.omni.omni.website.creigslist.datamodel;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "craigslist_category", type = "category")
public class CraigslistCategory {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Keyword)
    private String group;

    @Field(type = FieldType.Keyword)
    private String url;

    @Field(type = FieldType.Keyword)
    private List<String> supportedLocations;


    public String getId() {
        return id;
    }

    public CraigslistCategory setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CraigslistCategory setName(String name) {
        this.name = name;
        return this;
    }

    public String getGroup() {
        return group;
    }

    public CraigslistCategory setGroup(String group) {
        this.group = group;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public CraigslistCategory setUrl(String url) {
        this.url = url;
        return this;
    }

    public List<String> getSupportedLocations() {
        return supportedLocations;
    }

    public CraigslistCategory setSupportedLocations(List<String> supportedLocations) {
        this.supportedLocations = supportedLocations;
        return this;
    }
}
