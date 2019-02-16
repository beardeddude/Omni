package com.omni.omni.website.creigslist;

import com.omni.omni.website.AbstractWebsite;
import com.omni.omni.website.creigslist.datamodel.CraigslistCategory;
import com.omni.omni.website.creigslist.datamodel.CraigslistLocation;
import com.omni.omni.website.creigslist.datamodel.CraigslistPost;
import com.omni.omni.website.creigslist.repository.CraigslistCategoryRepository;
import com.omni.omni.website.creigslist.repository.CraigslistLocationRepository;
import com.omni.omni.website.creigslist.repository.CraigslistPostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class Craigslist extends AbstractWebsite<CraigslistPost> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Craigslist.class);


    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CraigslistCategoryRepository craigslistCategoryRepository;
    private final CraigslistLocationRepository craigslistLocationRepository;
    private final CraigslistPostRepository craigslistPostRepository;
    private CraigslistSiteMap craigslistSiteMap;


    public Craigslist(ElasticsearchTemplate elasticsearchTemplate,
                      CraigslistCategoryRepository craigslistCategoryRepository,
                      CraigslistLocationRepository craigslistLocationRepository,
                      CraigslistPostRepository craigslistPostRepository) throws IOException {

        super("Creigslist", new CraigslistIndexer(craigslistPostRepository), new CraigslistViewer());
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.craigslistCategoryRepository = craigslistCategoryRepository;
        this.craigslistLocationRepository = craigslistLocationRepository;
        this.craigslistPostRepository = craigslistPostRepository;

        this.craigslistSiteMap = new CraigslistSiteMap(craigslistCategoryRepository, craigslistLocationRepository);

        CraigslistLocation location =  craigslistLocationRepository.findAll().iterator().next();
        CraigslistCategory category = craigslistCategoryRepository.findAll().iterator().next();
        ((CraigslistIndexer)getIndexer()).indexBoard(location, category);
    }

}
