package com.omni.omni.website.creigslist.repository;

import com.omni.omni.website.creigslist.datamodel.CraigslistPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CraigslistPostRepository extends ElasticsearchRepository<CraigslistPost, String> {
}
