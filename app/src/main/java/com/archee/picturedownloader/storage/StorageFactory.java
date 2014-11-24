package com.archee.picturedownloader.storage;

import android.content.Context;

import static com.google.common.base.Preconditions.checkNotNull;

import com.archee.picturedownloader.enums.StorageType;
import com.archee.picturedownloader.storage.impl.CacheStorage;
import com.archee.picturedownloader.storage.impl.DatabaseStorage;

/**
 * A class to create an instance of the chosen storage method.
 */
public class StorageFactory {

    private static Storage storage;

    public static Storage create(Context applicationContext, StorageType storageType) {
        switch (storageType) {
            case CACHE:
                storage = new CacheStorage(applicationContext);
                return storage;

            case DATABASE:
                storage = new DatabaseStorage(applicationContext);
                return storage;

            default:
                throw new IllegalArgumentException("Storage type not supported.");
        }
    }

    public static Storage getStorage() {
        checkNotNull(storage);

        return storage;
    }

    private StorageFactory() {}
}
