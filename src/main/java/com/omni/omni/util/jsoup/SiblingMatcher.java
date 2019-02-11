package com.omni.omni.util.jsoup;

import org.jsoup.nodes.Element;

public class SiblingMatcher {

    public static Element getNextMatchingSibling(Element element, ElementFilter filter) {

        Element nextElement = null;
        do {
            nextElement = element.nextElementSibling();
        } while (nextElement != null && !filter.matches(nextElement));

        return nextElement;
    }


    public interface ElementFilter {
        boolean matches(Element element);
    }
}
