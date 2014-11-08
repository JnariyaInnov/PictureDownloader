package com.archee.picturedownloader.async;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImage extends AsyncTask<URL, Integer, ImageResponse> {

    public static final String TAG = "PictureDownloader";

    private ProgressBar progressBar;
    private AsyncImageCallback callback;

    /**
     * Use this constructor if no View animations are needed during download.
     */
    public DownloadImage() {}

    /**
     * An optional constructor used for animating a download button and progress bar
     * while downloading an image.
     * @param progressBar the progress bar will be displayed while download is occurring.
     */
    public DownloadImage(ProgressBar progressBar, AsyncImageCallback callback) {
        this.progressBar = progressBar;
        this.callback = callback;
    }

    private ImageResponse getBitmapFromURL(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            return new ImageResponse(url, BitmapFactory.decodeStream(input), connection.getResponseCode());
        } catch (IOException e) {
            Log.e(TAG, "Bitmap download failed. " + e.getMessage());
            return null;
        }
    }

    @Override
    protected ImageResponse doInBackground(URL... params) {
        return getBitmapFromURL(params[0]);
    }

    @Override
    protected void onPreExecute() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostExecute(ImageResponse response) {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        callback.onDownloadComplete(response);
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