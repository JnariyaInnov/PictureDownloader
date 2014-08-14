package com.archee.picturedownloader.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.archee.picturedownloader.PictureDownloader;
import com.archee.picturedownloader.utils.DateUtils;
import com.google.common.collect.Lists;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Implementation of database storage.
 */
public class DatabaseStorage implements Storage {

    private Context applicationContext;
    private SQLiteDatabase mDBWrite, mDBRead;

    protected DatabaseStorage(Context applicationContext) {
        this.applicationContext = applicationContext;
        this.mDBWrite = new PictureDBHelper(applicationContext).getWritableDatabase();
        this.mDBRead = new PictureDBHelper(applicationContext).getReadableDatabase();
    }

    @Override
    public List<Entry> getHistory() {
        List<Entry> entries = Lists.newArrayList();

        // Perform Select query to get all rows from DB.
        Cursor c = mDBRead.query(PictureDBHelper.ENTRIES_TABLE, new String[] {PictureDBHelper.PICTURE_URL, PictureDBHelper.ENTRY_DATE},
                null, null, null, null, null);
        c.moveToNext();

        // Iterate all rows returned from DB and store as Entries in a list.
        while (c.getPosition() != c.getCount()) {
            String url = c.getString(0);
            Date date;

            try {
                date = DateUtils.DEFAULT_FORMATTER.parse(c.getString(1));
            } catch (ParseException e) {
                Log.e(PictureDownloader.TAG, "Error parsing date string from DB, using today's date.");
                date = new Date();
            }

            entries.add(new Entry(url, date));
            c.moveToNext();
        }

        c.close();
        return entries;
    }

    @Override
    public void addEntry(String entry, Date now) {
        ContentValues values = new ContentValues();
        values.put(PictureDBHelper.PICTURE_URL, entry);
        values.put(PictureDBHelper.ENTRY_DATE, DateUtils.DEFAULT_FORMATTER.format(now));

        mDBWrite.insert(PictureDBHelper.ENTRIES_TABLE, null, values);
    }

    private class PictureDBHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "picture_db";
        private static final String ENTRIES_TABLE = "entries";
        private static final int VERSION = 1;

        private static final String PICTURE_URL = "url";
        private static final String ENTRY_DATE = "date";

        private static final String CREATE_CMD = "CREATE TABLE " + ENTRIES_TABLE + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PICTURE_URL + " TEXT, " +
                ENTRY_DATE + " TEXT )";

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
    }
}
