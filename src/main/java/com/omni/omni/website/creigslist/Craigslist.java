package com.omni.omni.website.creigslist;

import com.omni.omni.website.AbstractWebsite;
import com.omni.omni.website.creigslist.datamodel.CraigslistCategory;
import com.omni.omni.website.creigslist.datamodel.CraigslistLocation;
import com.omni.omni.website.creigslist.datamodel.CraigslistPost;
import com.omni.omni.website.creigslist.repository.CraigslistCategoryRepository;
import com.omni.omni.website.creigslist.repository.CraigslistLocationRepository;
import com.omni.omni.website.creigslist.repository.CraigslistPostRepository;
import org.elasticsearch.index.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class Craigslist extends AbstractWebsite<CraigslistPost> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Craigslist.class);


    private final ThreadPoolTaskScheduler taskExecutor;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CraigslistCategoryRepository craigslistCategoryRepository;
    private final CraigslistLocationRepository craigslistLocationRepository;
    private final CraigslistPostRepository craigslistPostRepository;
    private CraigslistSiteMap craigslistSiteMap;


    public Craigslist(ThreadPoolTaskScheduler taskExecutor,
                      ElasticsearchTemplate elasticsearchTemplate,
                      CraigslistCategoryRepository craigslistCategoryRepository,
                      CraigslistLocationRepository craigslistLocationRepository,
                      CraigslistPostRepository craigslistPostRepository) throws IOException {

        super("Creigslist", new CraigslistIndexer(taskExecutor, craigslistPostRepository), new CraigslistViewer());
        this.taskExecutor = taskExecutor;
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.craigslistCategoryRepository = craigslistCategoryRepository;
        this.craigslistLocationRepository = craigslistLocationRepository;
        this.craigslistPostRepository = craigslistPostRepository;

        this.craigslistSiteMap = new CraigslistSiteMap(craigslistCategoryRepository, craigslistLocationRepository);


        taskExecutor.execute(()->{
            //Testing
            taskExecutor.scheduleAtFixedRate(()->{((CraigslistIndexer)getIndexer()).indexPostsQueue();}, 10000);

            Map<String, CraigslistLocation> locations = craigslistLocationRepository.findByRegion("US").collect(Collectors.toMap(CraigslistLocation::getUrl, Function.identity()));
            Stream<CraigslistCategory> categories = craigslistCategoryRepository.findByGroup("for sale");

            List<Future> runningBoards = new ArrayList<>();
            categories.forEach((category) -> {

                for (String locationUrl : category.getSupportedLocations()) {

                    CraigslistLocation location = locations.get(locationUrl);
                    if (location != null) {
                        runningBoards.add(taskExecutor.submit(()->{
                            try {
                                LOGGER.info("Indexing board {} -> {}", location.getName(), category.getName());
                                ((CraigslistIndexer)getIndexer()).indexBoard(location, category);
                            } catch (IOException e) {
                                LOGGER.error("Failed to index board {}", location.getName() + " -> " + category.getName());
                            }
                        }));
                    }

                    while(runningBoards.size() > 20) {
                        try {
                            runningBoards.removeIf(future -> future.isDone());
                            if(runningBoards.size() > 20) Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            });

            categories.close();

            System.out.println("end");
        });

    }

}
