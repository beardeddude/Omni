package com.omni.omni.website;

import com.omni.omni.datamodel.AbstractPost;

public abstract class AbstractWebsite<T extends AbstractPost> {

    private final String name;
    private final AbstractIndexer<T> indexer;
    private final AbstractViewer<T> viewer;

    protected AbstractWebsite(String name, AbstractIndexer<T> indexer, AbstractViewer<T> viewer) {

        if (name == null || indexer == null || viewer == null) {
           throw new RuntimeException("All parameters are required when constructing a website");
        }

        this.name = name;
        this.indexer = indexer;
        this.viewer = viewer;
    }

    public String getName() {
        return name;
    }

    public AbstractIndexer<T> getIndexer() {
        return indexer;
    }

    public AbstractViewer<T> getViewer() {
        return viewer;
    }


}
