package com.omni.omni.datamodel;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

public abstract class AbstractPost {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Date)
    private Date postDate;



    public String getId() {
        return id;
    }

    public AbstractPost setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public AbstractPost setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AbstractPost setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getPostDate() {
        return postDate;
    }

    public AbstractPost setPostDate(Date postDate) {
        this.postDate = postDate;
        return this;
    }
}
