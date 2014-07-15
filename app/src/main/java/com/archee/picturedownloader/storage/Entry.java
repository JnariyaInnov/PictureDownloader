package com.archee.picturedownloader.storage;

import java.util.Date;

/**
 * Created by Archee on 7/13/2014.
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
