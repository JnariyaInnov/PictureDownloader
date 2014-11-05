package com.archee.picturedownloader.storage.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.archee.picturedownloader.storage.domain.Entry;
import com.archee.picturedownloader.storage.Storage;
import com.archee.picturedownloader.utils.DateUtils;
import com.google.common.collect.Sets;

import java.util.Date;
import java.util.Set;

/**
 * Implementation of database storage.
 */
public class DatabaseStorage implements Storage {

    private SQLiteDatabase mDBWrite, mDBRead;

    public DatabaseStorage(Context applicationContext) {
        this.mDBWrite = new PictureDBHelper(applicationContext).getWritableDatabase();
        this.mDBRead = new PictureDBHelper(applicationContext).getReadableDatabase();
    }

    @Override
    public Set<Entry> getHistory() {
        Set<Entry> entries = Sets.newHashSet();

        // Perform Select query to get all rows from DB.
        Cursor c = mDBRead.query(true, PictureDBHelper.ENTRIES_TABLE, new String[] {PictureDBHelper.COL_PICTURE_URL, PictureDBHelper.COL_ENTRY_DATE},
                null, null, null, null, null, null);
        c.moveToNext();

        // Iterate all rows returned from DB and store as Entries in a list.
        while (c.getPosition() != c.getCount()) {
            String url = c.getString(0);
            Date date = DateUtils.parse(c.getString(1));

            entries.add(new Entry(url, date));
            c.moveToNext();
        }

        c.close();
        return entries;
    }

    @Override
    public void addEntry(String entry, Date now) {
        ContentValues values = new ContentValues();
        values.put(PictureDBHelper.COL_PICTURE_URL, entry);
        values.put(PictureDBHelper.COL_ENTRY_DATE, DateUtils.format(now));

        mDBWrite.insert(PictureDBHelper.ENTRIES_TABLE, null, values);
    }

    private class PictureDBHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "picture_db";
        private static final String ENTRIES_TABLE = "entries";
        private static final int VERSION = 1;

        private static final String COL_PICTURE_URL = "url";
        private static final String COL_ENTRY_DATE = "date";

        private static final String CREATE_CMD = "CREATE TABLE " + ENTRIES_TABLE + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PICTURE_URL + " TEXT, " +
                COL_ENTRY_DATE + " TEXT )";

        public PictureDBHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_CMD);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ENTRIES_TABLE);

            this.onCreate(db);
        }

        public int getRecordCount() {
            return (int) DatabaseUtils.queryNumEntries(mDBRead, ENTRIES_TABLE);
        }
    }
}
