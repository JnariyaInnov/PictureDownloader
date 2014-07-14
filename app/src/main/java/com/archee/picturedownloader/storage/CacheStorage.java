package com.archee.picturedownloader.storage;

import android.content.Context;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Archee on 7/13/2014.
 */
public class CacheStorage implements StorageStrategy {

    private List<Entry> history = Lists.newArrayList();
    private Context applicationContext;

    @Override
    public List<Entry> getHistory() {
        // read from cache

        File cacheDirectory = applicationContext.getCacheDir();

        return history;
    }

    @Override
    public void addEntry(String entry, Date now) {
        // add entry to cache
    }

    protected CacheStorage(Context applicationContext) {
        this.applicationContext = applicationContext;
    }
}
