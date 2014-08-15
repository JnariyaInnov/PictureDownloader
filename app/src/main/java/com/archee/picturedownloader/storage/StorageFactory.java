package com.archee.picturedownloader.storage;

import android.content.Context;

/**
 * A class to create an instance of the chosen storage method.
 */
public class StorageFactory {

    public static Storage getInstance(Context applicationContext, StorageType storageType) {
        switch (storageType) {
            case CACHE:
                return new CacheStorage(applicationContext);

            case DATABASE:
                return new DatabaseStorage(applicationContext);

            default:
                throw new IllegalArgumentException("Storage type not supported.");
        }
    }

    private StorageFactory() {}
}
