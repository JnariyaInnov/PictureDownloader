package com.archee.picturedownloader.storage;

import android.content.Context;

import java.util.List;

/**
 * Created by Archee on 7/13/2014.
 */
public class StorageFactory {

    public static Storage getInstance(Context applicationContext, int storageType) {
        switch (storageType) {
            case Storage.STORAGE_CACHE:
                return new CacheStorage(applicationContext);

            case Storage.STORAGE_DB:
                return new DatabaseStorage(applicationContext);

            default:
                throw new AssertionError("Storage type not supported.");
        }
    }

    private StorageFactory() {}
}
