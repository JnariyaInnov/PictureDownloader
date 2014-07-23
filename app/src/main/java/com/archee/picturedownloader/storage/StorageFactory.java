package com.archee.picturedownloader.storage;

import android.content.Context;

import java.util.List;

/**
 * Created by Archee on 7/13/2014.
 */
public class StorageFactory {

    public static Storage getInstance(Context applicationContext, StorageType storageType) {
        switch (storageType) {
            case CACHE:
                return new CacheStorage(applicationContext);

            case DATABASE:
                return new DatabaseStorage(applicationContext);

            default:
                throw new AssertionError("Storage type not supported.");
        }
    }

    private StorageFactory() {}
}
