package com.archee.picturedownloader.storage;

import android.content.Context;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.CharSource;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

/**
 * Created by Archee on 7/13/2014.
 */
public class CacheStorage implements Storage {

    private static final String SEPARATOR = "&";

    private List<Entry> history = Lists.newArrayList();
    private Context applicationContext;

    @Override
    public List<Entry> getHistory() {
        // read from cache

        File cacheDirectory = applicationContext.getCacheDir();

        try {
            List<String> lines = Files.readLines(cacheDirectory, Charsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }


        return history;
    }

    @Override
    public void addEntry(String entry, Date now) {
        // add entry to cache
    }

    private Entry createEntry(String line) {
        // Solit string to create an entry
    }

    protected CacheStorage(Context applicationContext) {
        this.applicationContext = applicationContext;
    }
}
