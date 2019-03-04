package com.omni.omni.website.creigslist;

import com.google.common.collect.Lists;
import com.omni.omni.util.elasticsearch.RepositoryUtils;
import com.omni.omni.util.jsoup.SiblingMatcher;
import com.omni.omni.website.creigslist.datamodel.CraigslistCategory;
import com.omni.omni.website.creigslist.datamodel.CraigslistLocation;
import com.omni.omni.website.creigslist.repository.CraigslistCategoryRepository;
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

    private final CraigslistCategoryRepository craigslistCategoryRepository;
    private final CraigslistLocationRepository craigslistLocationRepository;

    private List<CraigslistLocation> locations;
    private List<CraigslistCategory> categories;

    public CraigslistSiteMap(CraigslistCategoryRepository craigslistCategoryRepository, CraigslistLocationRepository craigslistLocationRepository) throws IOException {

        this.craigslistCategoryRepository = craigslistCategoryRepository;
        this.craigslistLocationRepository = craigslistLocationRepository;

        if (craigslistLocationRepository.count() > 0) {
            //loadExistingConfiguration();
        } else {
            createNewConfiguration();
        }
    }

    private void loadExistingConfiguration() {

        // TODO this needs to be done with scroll and possibly need to limit data set (how much mem?)
        locations = RepositoryUtils.getAll(craigslistLocationRepository);
        categories = RepositoryUtils.getAll(craigslistCategoryRepository);

        System.out.println("");
    }

    private void createNewConfiguration() throws IOException {

        LOGGER.info("Building Craigslist site map...");

        LOGGER.debug("Parsing locations");
        locations = discoverLocations();
        craigslistLocationRepository.saveAll(locations);

        LOGGER.debug("Parsing categories");
        categories = discoverCategories(locations);
        craigslistCategoryRepository.saveAll(categories);

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

            LOGGER.debug("Discovering categories for location: {}", location.getName());
            Document page = Jsoup.connect(location.getUrl()).get();

            // Find all category groups
            Elements groupNameElements = page.select("#center h4");
            for (Element groupNameElement : groupNameElements) {

                String groupName = groupNameElement.wholeText();
                LOGGER.debug("Discovering categories for location: {}, group: {}", location.getName(), groupName);

                // Find the list div in the next sibling elements
                Element categoryHolderElement = SiblingMatcher.getNextMatchingSibling(groupNameElement, (element -> "cats".equalsIgnoreCase(element.className())));

                // Find all category links in this list
                Elements categoryElements = categoryHolderElement.select("ul li a");
                for (Element categoryElement : categoryElements) {

                    String url = categoryElement.attr("href").replace(location.getUrl(), "");
                    CraigslistCategory craigslistCategory = categoryMap.get(url);
                    if (craigslistCategory == null) {
                        craigslistCategory = new CraigslistCategory()
                                .setName(categoryElement.wholeText())
                                .setGroup(groupName)
                                .setUrl(url)
                                .setSupportedLocations(new ArrayList<>());

                        categoryMap.put(url, craigslistCategory);
                        categories.add(craigslistCategory);
                    }
                    craigslistCategory.getSupportedLocations().add(location.getUrl());

                    LOGGER.debug("Discovered category location: {}, group: {}, category: {}", location.getName(), groupName, craigslistCategory.getName());

                }
            }
        }

        return new ArrayList<>(categories);
    }
}
