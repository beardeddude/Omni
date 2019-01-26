package com.omni.omni.website.creigslist;

import com.omni.omni.website.AbstractWebsite;
import com.omni.omni.website.creigslist.datamodel.CreigslistPost;

public class Creigslist extends AbstractWebsite<CreigslistPost> {

    public Creigslist() {
        super("Creigslist", new CreigslistIndexer(), new CreigslistViewer());
    }

}
