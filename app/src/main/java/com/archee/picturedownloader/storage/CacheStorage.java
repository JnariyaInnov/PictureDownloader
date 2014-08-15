package com.archee.picturedownloader.storage;

import android.content.Context;
import android.graphics.Picture;
import android.util.Log;

import com.archee.picturedownloader.PictureDownloader;
import com.archee.picturedownloader.utils.DateUtils;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSink;
import com.google.common.io.CharSource;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of cache storage.
 */
public class CacheStorage implements Storage {

    private static final String SEPARATOR = "$";
    private static final String CACHE_FILE = "entries";

    private File cacheFile;

    protected CacheStorage(Context applicationContext) {
        cacheFile = new File(applicationContext.getCacheDir(), CACHE_FILE);
    }

    @Override
    public List<Entry> getHistory() {
        List<Entry> history = Lists.newArrayList();

        try {
            CharSource cacheSource = Files.asCharSource(cacheFile, Charset.defaultCharset());
            List<String> lines = cacheSource.readLines();

            for (String line : lines) {
                Entry entry = parseEntryFromLine(line);

                if (entry != null) {
                    history.add(entry);
                }
            }
        } catch (IOException e) {
            Log.e(PictureDownloader.TAG, "There was an error reading from the cache: " + e.getMessage());
        }

        return history;
    }

    @Override
    public void addEntry(String entry, Date now) {
        CharSink cacheSink = Files.asCharSink(cacheFile, Charset.defaultCharset(), FileWriteMode.APPEND);

        try {
            cacheSink.write(entry + SEPARATOR + DateUtils.format(now) + "\n");
        } catch (IOException e) {
            Log.e(PictureDownloader.TAG, "There was an error writing to the cache: " + e.getMessage());
        }
    }

    /**
     *
     * @param line A string that is formatted in a way whereas it can be represented as an Entry
     * @return an Entry object, or null if there is a formatting error.
     */
    protected Entry parseEntryFromLine(String line) {
        Splitter splitter = Splitter.on(SEPARATOR);
        Iterator<String> it = splitter.split(line).iterator();

        if (it.hasNext()) {
            String url = it.next();
            String date = it.next();

            return new Entry(url, DateUtils.parse(date));
        }

        return null;
    }
}
