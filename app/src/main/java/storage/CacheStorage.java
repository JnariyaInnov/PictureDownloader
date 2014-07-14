package storage;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Archee on 7/13/2014.
 */
public class CacheStorage implements StorageStrategy {

    private List<Entry> history = Lists.newArrayList();

    @Override
    public List<Entry> getHistory() {
        // read from cache

        return history;
    }

    @Override
    public void addEntry(String entry, Date now) {
        // add entry to cache
    }
}
