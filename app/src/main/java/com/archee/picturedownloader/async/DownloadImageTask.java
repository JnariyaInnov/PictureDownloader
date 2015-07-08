package com.archee.picturedownloader.async;

import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ProgressBar;

import com.google.common.base.Optional;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImageTask extends AsyncTask<URL, Integer, ImageResponse> {

    public static final String TAG = "PictureDownloader";

    private static final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private static final int cacheSize = maxMemory / 8;
    private static LruCache<String, ImageResponse> imageCache = new LruCache<String, ImageResponse>(cacheSize);

    private ProgressBar progressBar;
    private DownloadCompleteHandler callback;

    /**
     * Use this constructor if no View animations are needed during download.
     */
    public DownloadImageTask() {}

    /**
     * An optional constructor used for animating a download button and progress bar
     * while downloading an image.
     * @param progressBar the progress bar will be displayed while download is occurring.
     */
    public DownloadImageTask(ProgressBar progressBar, DownloadCompleteHandler callback) {
        this.progressBar = progressBar;
        this.callback = callback;
    }

    private ImageResponse getBitmapFromURL(URL url) {
        Optional<ImageResponse> cachedImageResponse = Optional.fromNullable(imageCache.get(url.toString()));

        if (cachedImageResponse.isPresent()) {
            return cachedImageResponse.get();
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            ImageResponse imageResponse = new ImageResponse(url, BitmapFactory.decodeStream(input), connection.getResponseCode());
            imageCache.put(url.toString(), imageResponse);

            return imageResponse;
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