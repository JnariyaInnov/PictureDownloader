package com.archee.picturedownloader.storage;

import android.content.Context;

import com.archee.picturedownloader.enums.StorageType;
import com.archee.picturedownloader.storage.impl.CacheStorage;
import com.archee.picturedownloader.storage.impl.DatabaseStorage;

/**
 * A class to create an instance of the chosen storage method.
 */
public class StorageFactory {

    public static Storage create(Context applicationContext, StorageType storageType) {
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
