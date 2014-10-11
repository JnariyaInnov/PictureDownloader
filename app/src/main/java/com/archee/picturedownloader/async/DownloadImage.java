package com.archee.picturedownloader.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImage extends AsyncTask<URL, Integer, Bitmap> {

    public static final String TAG = "PictureDownloader";

    private Button downloadButton;
    private ProgressBar progressBar;

    /**
     * Use this constructor if no View animations are needed during download.
     */
    public DownloadImage() {}

    /**
     * An optional constructor used for animating a download button and progress bar
     * while downloading an image.
     * @param downloadButton the button will be disabled and reenable during download
     * @param progressBar the progress bar will be displayed while download is occurring.
     */
    public DownloadImage(Button downloadButton, ProgressBar progressBar) {
        this.downloadButton = downloadButton;
        this.progressBar = progressBar;
    }

    private Bitmap getBitmapFromURL(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Log.e(TAG, "Bitmap download failed. " + e.getMessage());
            return null;
        }
    }

    @Override
    protected Bitmap doInBackground(URL... params) {
        return getBitmapFromURL(params[0]);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (downloadButton != null) {
            downloadButton.setVisibility(View.GONE);
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostExecute(Bitmap aBitmap) {
        super.onPostExecute(aBitmap);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (downloadButton != null) {
            downloadButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}