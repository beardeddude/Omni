package com.omni.omni.website.creigslist.datamodel;

import com.omni.omni.datamodel.AbstractPost;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "craigslist_posts", type = "post")
public class CraigslistPost extends AbstractPost {

    @Field(type = FieldType.Keyword)
    private String location;

    @Field(type = FieldType.Keyword)
    private String category;

    public String getLocation() {
        return location;
    }

    public CraigslistPost setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public CraigslistPost setCategory(String category) {
        this.category = category;
        return this;
    }
}
