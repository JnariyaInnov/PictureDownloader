package com.archee.picturedownloader.storage;

import android.content.Context;

import java.util.Date;
import java.util.List;

/**
 * Created by Archee on 7/13/2014.
 */
public class StorageManager {
    public static final int STORAGE_CACHE = 1;
    public static final int STORAGE_DB = 2;

    private static final int DEFAULT_STORAGE = STORAGE_CACHE;

    private static StorageManager instance;
    private StorageStrategy strategy;
    private Context applicationContext;

    public List<Entry> getHistory() {
        return strategy.getHistory();
    }

    public void addEntry(String entry) {
        strategy.addEntry(entry, new Date());
    }

    public static StorageManager getInstance(Context context) {
        if (instance == null) {
            instance = new StorageManager(context);
        }

        return instance;
    }

    public void setStorageMethod(int storageMethod) {
        switch (storageMethod) {
            case STORAGE_CACHE:
                strategy = new CacheStorage(applicationContext);
                break;
            case STORAGE_DB:
                strategy = new DatabaseStorage(applicationContext);
                break;
            default:
                throw new AssertionError("Storage type not supported.");
        }
    }

    private StorageManager(Context context) {
        applicationContext = context;
        setStorageMethod(DEFAULT_STORAGE);
    }

    @Override
    public String toString() {
        return "[Storage Manager] Storage strategy: " + strategy.toString();
    }

}
