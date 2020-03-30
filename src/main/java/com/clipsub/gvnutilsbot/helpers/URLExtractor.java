package com.clipsub.gvnutilsbot.helpers;

import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

import java.util.EnumSet;

public class URLExtractor {
    public static String extractUrl(String str) {
        try {
            LinkExtractor linkExtractor = LinkExtractor.builder()
                    .linkTypes(EnumSet.of(LinkType.URL))
                    .build();
            Iterable<LinkSpan> links = linkExtractor.extractLinks(str);
            LinkSpan link = links.iterator().next();

            return str.substring(link.getBeginIndex(), link.getEndIndex());
        } catch (Exception e) {
            return null;
        }
    }
}
