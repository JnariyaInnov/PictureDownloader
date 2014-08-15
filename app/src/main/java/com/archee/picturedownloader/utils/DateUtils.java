package com.archee.picturedownloader.utils;

import android.util.Log;

import com.archee.picturedownloader.PictureDownloader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A utility class to help with parsing and formatting dates.
 */
public class DateUtils {

    private static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm";

    public static final SimpleDateFormat DEFAULT_FORMATTER = new SimpleDateFormat(DEFAULT_FORMAT);

    /**
     * Converts the given string into a java.util.Date object using the defined default format yyyy-MM-dd HH:mm
     * @param dateStr a string representing a date
     * @return the Date object created from the date string, or null if format incorrect.
     */
    public static Date parse(String dateStr) {
        Date parsedDate;

        try {
            parsedDate = DEFAULT_FORMATTER.parse(dateStr);
        } catch (ParseException e) {
            Log.e(PictureDownloader.TAG, "Error parsing date string: " + dateStr + " - returning current date...");
            parsedDate = new Date();
        }

        return parsedDate;
    }

    /**
     * Converts a java.util.Date object to a string using the defined default format yyyy-MM-dd HH:mm
     * @param date the date object to be formatted.
     * @return the formatted date string.
     */
    public static String format(Date date) {
        return DEFAULT_FORMATTER.format(date);
    }

    /**
     * No need for instantiation.
     */
    private DateUtils() {}
}
