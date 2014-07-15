package com.archee.picturedownloader.storage;

import android.content.Context;

import com.google.common.collect.Lists;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

/**
 * Created by Archee on 7/13/2014.
 */
public class CacheStorage implements Storage {

    private List<Entry> history = Lists.newArrayList();
    private Context applicationContext;

    @Override
    public List<Entry> getHistory() {
        // read from cache

        File cacheDirectory = applicationContext.getCacheDir();

        CharSource cache = Files.asCharSource(cacheDirectory, Charset.defaultCharset());

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
