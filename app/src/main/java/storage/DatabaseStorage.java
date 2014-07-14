package storage;

import java.util.Date;
import java.util.List;

/**
 * Created by Archee on 7/13/2014.
 */
public class DatabaseStorage implements StorageStrategy {
    @Override
    public List<Entry> getHistory() {
        return null;
    }

    @Override
    public void addEntry(String entry, Date now) {

    }
}
