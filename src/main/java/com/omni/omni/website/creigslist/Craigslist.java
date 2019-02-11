package com.omni.omni.website.creigslist;

import com.omni.omni.website.AbstractWebsite;
import com.omni.omni.website.creigslist.datamodel.CraigslistPost;
import com.omni.omni.website.creigslist.repository.CraigslistChannelRepository;
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
    private final CraigslistChannelRepository craigslistChannelRepository;
    private final CraigslistLocationRepository craigslistLocationRepository;
    private final CraigslistPostRepository craigslistPostRepository;
    private CraigslistSiteMap craigslistSiteMap;


    public Craigslist(ElasticsearchTemplate elasticsearchTemplate,
                      CraigslistChannelRepository craigslistChannelRepository,
                      CraigslistLocationRepository craigslistLocationRepository,
                      CraigslistPostRepository craigslistPostRepository) throws IOException {

        super("Creigslist", new CraigslistIndexer(), new CraigslistViewer());
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.craigslistChannelRepository = craigslistChannelRepository;
        this.craigslistLocationRepository = craigslistLocationRepository;
        this.craigslistPostRepository = craigslistPostRepository;

        this.craigslistSiteMap = new CraigslistSiteMap(craigslistChannelRepository, craigslistLocationRepository);
    }

}
