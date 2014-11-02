package com.archee.picturedownloader.storage;

import com.archee.picturedownloader.storage.domain.Entry;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * The top-level strategy interface that specifies methods to be overridden by concrete strategy classes
 */
public interface Storage {

    /*
    * Retrieves a list of entry history in an ordered manner.
    */
    Set<Entry> getHistory();

    /*
    * Adds an entry to entry history.
    */
    void addEntry(String url, Date now);
}
