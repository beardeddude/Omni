package com.omni.omni.website.creigslist;

import com.omni.omni.website.AbstractIndexer;
import com.omni.omni.website.creigslist.datamodel.CraigslistCategory;
import com.omni.omni.website.creigslist.datamodel.CraigslistLocation;
import com.omni.omni.website.creigslist.datamodel.CraigslistPost;
import com.omni.omni.website.creigslist.repository.CraigslistPostRepository;
import org.elasticsearch.tasks.Task;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CraigslistIndexer extends AbstractIndexer<CraigslistPost> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CraigslistIndexer.class);

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");

    private final CraigslistPostRepository craigslistPostRepository;
    private final ThreadPoolTaskScheduler taskExecutor;
    private final BlockingQueue<CraigslistPost> indexQueue = new LinkedBlockingQueue<>();

    public CraigslistIndexer(ThreadPoolTaskScheduler taskExecutor, CraigslistPostRepository craigslistPostRepository) {

        this.taskExecutor = taskExecutor;
        this.craigslistPostRepository = craigslistPostRepository;
    }

    public void indexBoard(CraigslistLocation location, CraigslistCategory category) throws IOException {

        Document boardPage = Jsoup.connect(location.getUrl().replaceAll("/$","") + category.getUrl()).get();
        do {

            Elements postLinks = boardPage.select("a[class='result-title hdrlnk']");
            for (Element postLink : postLinks) {

                String postUrl = postLink.attr("href");
                taskExecutor.execute(() -> {
                    try {
                        indexPost(location, category, postUrl);
                    } catch (IOException e) {
                        LOGGER.error("Could not download post page: " + postUrl, e);
                    }
                });
            }

        } while ((boardPage = getNextPage(location, boardPage)) != null);
    }

    private Document getNextPage(CraigslistLocation location, Document currentPage) throws IOException {

        Elements nextButtonSearchResult = currentPage.select("a[class='button next']");
        if (!nextButtonSearchResult.isEmpty()) {

            String nextPageUrl = nextButtonSearchResult.first().attr("href");
            return Jsoup.connect(location.getUrl().replaceAll("/$","") + nextPageUrl).get();
        } else {

            return null;
        }
    }

    private void indexPost(CraigslistLocation location, CraigslistCategory category, String url) throws IOException {

        CraigslistPost post = new CraigslistPost();

        long time = System.currentTimeMillis();
        Document postPage = Jsoup.connect(url).get();
        //LOGGER.info("fetch : " + (System.currentTimeMillis() - time));

        time = System.currentTimeMillis();
        post.setLocation(location.getUrl());
        post.setCategory(category.getUrl());

        try {
            post.setTitle(postPage.select("#titletextonly").first().ownText());
        } catch (Exception e) {
            LOGGER.warn("Failed to get title");
        }

        try {
            post.setPrice(Float.valueOf(postPage.select("span[class='price']").first().ownText().replaceAll("\\D+","")));
        } catch (Exception e) {
            LOGGER.warn("Failed to get price");
        }

        try {
            post.setDescription(postPage.select("#postingbody").first().ownText());
        } catch (Exception e) {
            LOGGER.warn("Failed get description");
        }


        String postDate = null;
        try {
            postDate = postPage.select("time").first().attr("datetime");
            post.setPostDate(DATE_FORMAT.parse(postDate));
        } catch (ParseException e) {
            LOGGER.warn("Unable to pare post date: {}", postDate);
        }
        //LOGGER.info("parse : " + (System.currentTimeMillis() - time));

        time = System.currentTimeMillis();
        indexQueue.offer(post);
        //LOGGER.info("index : " + (System.currentTimeMillis() - time));

        LOGGER.info("Indexed post " + post.getTitle());
    }

    public void indexPostsQueue() {

        if(indexQueue.isEmpty())return;

        LOGGER.info("Indexing queue: {} posts", indexQueue.size());
        List<CraigslistPost> saveList = new ArrayList<>(indexQueue.size());
        indexQueue.drainTo(saveList);

        craigslistPostRepository.saveAll(saveList);
    }
}
