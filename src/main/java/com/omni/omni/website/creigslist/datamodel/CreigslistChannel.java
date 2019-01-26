package com.omni.omni.website.creigslist.datamodel;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "creigslist_channel", type = "channel")
public class CreigslistChannel {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Keyword)
    private List<String> supportedLocations;


    public String getId() {
        return id;
    }

    public CreigslistChannel setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CreigslistChannel setName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getSupportedLocations() {
        return supportedLocations;
    }

    public CreigslistChannel setSupportedLocations(List<String> supportedLocations) {
        this.supportedLocations = supportedLocations;
        return this;
    }
}
