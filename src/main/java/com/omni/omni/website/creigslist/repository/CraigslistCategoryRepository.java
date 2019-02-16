package com.omni.omni.website.creigslist.repository;

import com.omni.omni.website.creigslist.datamodel.CraigslistCategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CraigslistCategoryRepository extends ElasticsearchRepository<CraigslistCategory, String> {
}
