package com.archee.picturedownloader.storage;

import java.util.Date;
import java.util.List;

/**
 * The top-level strategy interface that specifies methods to be overridden by concrete strategy classes
 */
public interface StorageStrategy {

    /*
    * Retrieves a list of entry history in an ordered manner.
    */
    List<Entry> getHistory();

    /*
    * Adds an entry to entry history.
    */
    void addEntry(String entry, Date now);
}
