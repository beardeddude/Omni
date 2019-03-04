package com.omni.omni.website.creigslist.repository;

import com.omni.omni.website.creigslist.datamodel.CraigslistCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.stream.Stream;

public interface CraigslistCategoryRepository extends ElasticsearchRepository<CraigslistCategory, String> {

    //@Query("{\"bool\" : {\"must\" : {\"term\" : {\"name\" : \"?0\"}}}}")
    Stream<CraigslistCategory> findByName(String name);

    //@Query("{\"bool\" : {\"must\" : {\"term\" : {\"group\" : \"?0\"}}}}")
    Stream<CraigslistCategory> findByGroup(String group);

    //@Query("{\"bool\" : {\"must\" : {\"term\" : {\"url\" : \"?0\"}}}}")
    Stream<CraigslistCategory> findByUrl(String url);

}
