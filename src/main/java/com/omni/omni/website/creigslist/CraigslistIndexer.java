package com.omni.omni.website.creigslist;

import com.omni.omni.website.AbstractIndexer;
import com.omni.omni.website.creigslist.datamodel.CraigslistCategory;
import com.omni.omni.website.creigslist.datamodel.CraigslistLocation;
import com.omni.omni.website.creigslist.datamodel.CraigslistPost;
import com.omni.omni.website.creigslist.repository.CraigslistPostRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CraigslistIndexer extends AbstractIndexer<CraigslistPost> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CraigslistIndexer.class);

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");

    private CraigslistPostRepository craigslistPostRepository;

    public CraigslistIndexer(CraigslistPostRepository craigslistPostRepository) {

        this.craigslistPostRepository = craigslistPostRepository;
    }

    public void indexBoard(CraigslistLocation location, CraigslistCategory category) throws IOException {

        Document boardPage = Jsoup.connect(location.getUrl() + category.getUrl()).get();
        do {

            Elements postLinks = boardPage.select("a[class='result-title hdrlnk']");
            for (Element postLink : postLinks) {

                String postUrl = postLink.attr("href");
                try {
                    indexPost(location, category, postUrl);
                } catch (IOException e) {
                    LOGGER.error("Could not download post page: " + postUrl, e);
                }
            }

        } while ((boardPage = getNextPage(boardPage)) != null);
    }

    private Document getNextPage(Document currentPage) throws IOException {

        Elements nextButtonSearchResult = currentPage.select("a[class='button next']");
        if (!nextButtonSearchResult.isEmpty()) {

            String nextPageUrl = nextButtonSearchResult.first().attr("href");
            return Jsoup.connect(nextPageUrl).get();
        } else {

            return null;
        }
    }

    private void indexPost(CraigslistLocation location, CraigslistCategory category, String url) throws IOException {

        CraigslistPost post = new CraigslistPost();

        Document postPage = Jsoup.connect(url).get();

        post.setLocation(location.getUrl());
        post.setCategory(category.getUrl());
        post.setTitle(postPage.select("#titletextonly").first().ownText());
        post.setPrice(Float.valueOf(postPage.select("span[class='price']").first().ownText().replaceAll("\\D+","")));
        post.setDescription(postPage.select("#postingbody").first().ownText());

        String postDate = null;
        try {
            postDate = postPage.select("time").first().attr("datetime");
            post.setPostDate(DATE_FORMAT.parse(postDate));
        } catch (ParseException e) {
            LOGGER.warn("Unable to pare post date: {}", postDate);
        }

        craigslistPostRepository.save(post);
    }
}
