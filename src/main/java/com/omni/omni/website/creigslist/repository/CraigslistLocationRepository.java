package com.omni.omni.website.creigslist.repository;

import com.omni.omni.website.creigslist.datamodel.CraigslistLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.stream.Stream;

public interface CraigslistLocationRepository extends ElasticsearchRepository<CraigslistLocation, String> {

    //@Query("{\"bool\" : {\"must\" : {\"term\" : {\"name\" : \"?0\"}}}}")
    Stream<CraigslistLocation> findByName(String name);

    //@Query("{\"bool\" : {\"must\" : {\"term\" : {\"region\" : \"?0\"}}}}")
    Stream<CraigslistLocation> findByRegion(String region);

}
