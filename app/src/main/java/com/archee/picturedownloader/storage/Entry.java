package com.archee.picturedownloader.storage;

import java.util.Date;

/**
 * A POJO to represent a single entry.
 */
public class Entry {
    private String url;
    private Date date;

    public Entry(String url, Date date) {
        this.url = url;
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public Date getDate() {
        return date;
    }
}
