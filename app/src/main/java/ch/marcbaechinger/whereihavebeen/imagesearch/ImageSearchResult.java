package ch.marcbaechinger.whereihavebeen.imagesearch;

import org.json.JSONArray;

public class ImageSearchResult {
    JSONArray matches;
    JSONArray pages;
    int currentPage;

    public ImageSearchResult(JSONArray matches, JSONArray pages, int currentPage) {
        this.matches = matches;
        this.pages = pages;
        this.currentPage = currentPage;
    }

    public JSONArray getMatches() {
        return matches;
    }
    public JSONArray getPages() {
        return pages;
    }
    public int getCurrentPage() {
        return currentPage;
    }
}
