package com.omni.omni.website.creigslist.repository;

import com.omni.omni.website.creigslist.datamodel.CraigslistLocation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CraigslistLocationRepository extends ElasticsearchRepository<CraigslistLocation, String> {
}
