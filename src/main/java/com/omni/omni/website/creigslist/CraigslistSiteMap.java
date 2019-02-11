package com.omni.omni.website.creigslist;

import com.omni.omni.util.jsoup.SiblingMatcher;
import com.omni.omni.website.creigslist.datamodel.CraigslistCategory;
import com.omni.omni.website.creigslist.datamodel.CraigslistLocation;
import com.omni.omni.website.creigslist.repository.CraigslistChannelRepository;
import com.omni.omni.website.creigslist.repository.CraigslistLocationRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CraigslistSiteMap {

    private final static Logger LOGGER = LoggerFactory.getLogger(CraigslistSiteMap.class);

    private static final String SITES_PAGE = "https://www.craigslist.org/about/sites";

    private final CraigslistChannelRepository craigslistChannelRepository;
    private final CraigslistLocationRepository craigslistLocationRepository;

    private List<CraigslistLocation> locations;
    private List<CraigslistCategory> categories;

    public CraigslistSiteMap(CraigslistChannelRepository craigslistChannelRepository, CraigslistLocationRepository craigslistLocationRepository) throws IOException {

        this.craigslistChannelRepository = craigslistChannelRepository;
        this.craigslistLocationRepository = craigslistLocationRepository;

        if (craigslistLocationRepository.count() > 0) {
            loadExistingConfiguration();
        } else {
            createNewConfiguration();
        }
    }

    private void loadExistingConfiguration() {

    }

    private void createNewConfiguration() throws IOException {

        LOGGER.info("Building Craigslist site map...");

        LOGGER.debug("Parsing locations");
        locations = discoverLocations();
        locations.stream().forEach((location)-> craigslistLocationRepository.save(location));

        LOGGER.debug("Parsing categories");
        categories = discoverCategories(locations);
        categories.forEach((category -> craigslistChannelRepository.save(category)));

        LOGGER.info("Found {} locations and {} categories", locations.size(), categories.size());
    }

    /**
     * Scrapes the Craigslist sites page to find all locations
     */
    private List<CraigslistLocation> discoverLocations() throws IOException {

        List<CraigslistLocation> locations = new ArrayList<>();

        Document page = Jsoup.connect(SITES_PAGE).get();

        // Find all of the h1 region headers
        Elements regions = page.select("body article section h1");
        for (Element regionElement : regions) {

            String region = regionElement.ownText();

            // The subRegions are not in an inner div but instead come after the region header in a div element
            Element nextElement = SiblingMatcher.getNextMatchingSibling(regionElement, (element -> "div".equalsIgnoreCase(element.tagName())
                    && "colmask".equalsIgnoreCase(element.className())));

            // Find all subRegion headers for this region
            Elements subRegionElements = nextElement.select("div h4");
            for (Element subRegionElement : subRegionElements) {

                String subRegion = subRegionElement.ownText();

                // Locations are not in an inner div but instead come after the subRegion header in a ul element
                Element locationListElement = SiblingMatcher.getNextMatchingSibling(subRegionElement, (element -> "ul".equalsIgnoreCase(element.tagName())));

                // Find all location links for this subRegion and record them to the database
                Elements locationElements = locationListElement.select("li a");
                locationElements.stream().forEach((locationElement) -> {

                    locations.add(new CraigslistLocation()
                            .setRegion(region)
                            .setSubRegion(subRegion)
                            .setName(locationElement.ownText())
                            .setUrl(locationElement.attr("href")));
                });
            }
        }

        return locations;
    }

    private List<CraigslistCategory> discoverCategories(Collection<CraigslistLocation> locations) throws IOException {

        Map<String, CraigslistCategory> categoryMap = new HashMap<>();
        Set<CraigslistCategory> categories= new HashSet<>();

        for (CraigslistLocation location : locations) {

            Document page = Jsoup.connect(location.getUrl()).get();

            // Find all category groups
            Elements groupNameElements = page.select("#center h4");
            for (Element groupNameElement : groupNameElements) {

                String groupName = groupNameElement.ownText();

                // Find the list div in the next sibling elements
                Element categoryHolderElement = SiblingMatcher.getNextMatchingSibling(groupNameElement, (element -> "cats".equalsIgnoreCase(element.className())));

                // Find all category links in this list
                Elements categoryElements = categoryHolderElement.select("ul li a");
                for (Element categoryElement : categoryElements) {

                    String url = categoryElement.attr("href");
                    CraigslistCategory craigslistCategory = categoryMap.get(url);
                    if (craigslistCategory == null) {
                        craigslistCategory = new CraigslistCategory()
                                .setName(categoryElement.ownText())
                                .setGroup(groupName)
                                .setUrl(url)
                                .setSupportedLocations(new ArrayList<>());
                    }
                    craigslistCategory.getSupportedLocations().add(location.getUrl());

                    categories.add(craigslistCategory);
                }
            }
        }

        return new ArrayList<>(categories);
    }
}