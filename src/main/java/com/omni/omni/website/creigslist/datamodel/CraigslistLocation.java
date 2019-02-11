package com.omni.omni.website.creigslist.datamodel;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "craigslist_locations", type = "location")
public class CraigslistLocation {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Keyword)
    private String subRegion;

    @Field(type = FieldType.Keyword)
    private String region;

    @Field(type = FieldType.Keyword, index = false)
    private String url;


    public String getId() {
        return id;
    }

    public CraigslistLocation setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CraigslistLocation setName(String name) {
        this.name = name;
        return this;
    }

    public String getSubRegion() {
        return subRegion;
    }

    public CraigslistLocation setSubRegion(String subRegion) {
        this.subRegion = subRegion;
        return this;
    }

    public String getRegion() {
        return region;
    }

    public CraigslistLocation setRegion(String region) {
        this.region = region;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public CraigslistLocation setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CraigslistLocation that = (CraigslistLocation) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (subRegion != null ? !subRegion.equals(that.subRegion) : that.subRegion != null) return false;
        if (region != null ? !region.equals(that.region) : that.region != null) return false;
        return url != null ? url.equals(that.url) : that.url == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (subRegion != null ? subRegion.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
