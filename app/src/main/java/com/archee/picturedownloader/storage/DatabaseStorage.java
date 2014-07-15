package com.archee.picturedownloader.storage;

import android.content.Context;

import java.util.Date;
import java.util.List;

/**
 * Created by Archee on 7/13/2014.
 */
public class DatabaseStorage implements Storage {

    private Context applicationContext;

    protected DatabaseStorage(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public List<Entry> getHistory() {
        return null;
    }

    @Override
    public void addEntry(String entry, Date now) {

    }


}
