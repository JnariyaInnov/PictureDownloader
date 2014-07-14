package storage;

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

    public List<Entry> getHistory() {
        return strategy.getHistory();
    }

    public void addEntry(String entry) {
        strategy.addEntry(entry, new Date());
    }

    public static StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }

        return instance;
    }

    public void setStorageMethod(int storageMethod) {
        switch (storageMethod) {
            case STORAGE_CACHE:
                strategy = new CacheStorage();
                break;
            case STORAGE_DB:
                strategy = new DatabaseStorage();
                break;
            default:
                throw new AssertionError("Storage type not supported.");
        }
    }

    private StorageManager() {
        setStorageMethod(DEFAULT_STORAGE);
    }

    @Override
    public String toString() {
        return "[Storage Manager] Storage strategy: " + strategy.toString();
    }

}
