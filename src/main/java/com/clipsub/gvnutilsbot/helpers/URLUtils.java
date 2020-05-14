package com.clipsub.gvnutilsbot.helpers;

import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

import java.util.EnumSet;
import java.util.List;

public class URLUtils {
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

    public static boolean containsLink(String str) {
        UrlDetector parser = new UrlDetector(str, UrlDetectorOptions.Default);
        List<Url> found = parser.detect();

        return found.size() > 0;
    }
}
