package com.archee.picturedownloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import com.archee.picturedownloader.storage.Storage;
import com.archee.picturedownloader.storage.StorageFactory;


public class PictureDownloader extends Activity {

    public static final String TAG = "PictureDownloader";
    private static final String DEFAULT_PROTOCOL = "http://";

    private EditText urlEditText;

    private boolean displayProtocol;
    private Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_downloader);

        // Storage object user for persisting data
        storage = StorageFactory.getInstance(getApplicationContext(), Storage.STORAGE_CACHE);

        urlEditText = (EditText) findViewById(R.id.imageUrl);
        displayProtocol = true;
    }

    public void setDefaultProtocol(View view) {
        if (displayProtocol) {
            urlEditText.setText(DEFAULT_PROTOCOL);
            displayProtocol = false;
            urlEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            urlEditText.setSelection(urlEditText.getText().length());
        }
    }

    public void onDownloadPress(View view) {
        String imageUrlStr = ((EditText) findViewById(R.id.imageUrl)).getText().toString();

        try {
            // Attempt to download image in a background thread.
            URL imageUrl = new URL(imageUrlStr);
            AsyncTask downloadTask = new DownloadImage(this).execute(imageUrl);

            try {
                // Retrieve downloaded image from background thread, if there is a result.
                Bitmap bm = (Bitmap) downloadTask.get();

                if (bm != null) {
                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
                    Bitmap resizedBitmap = getResizedBitmap(bm, imageView.getHeight(), imageView.getWidth());
                    imageView.setImageBitmap(resizedBitmap);

                    storage.addEntry(imageUrlStr, new Date());
                } else {
                    Log.e(TAG, "Bitmap object is null");
                }

            } catch (InterruptedException e) {
                Log.e(TAG, "Bitmap download failed. " + e.getMessage());
            } catch (ExecutionException e) {
                Log.e(TAG, "Bitmap download failed. " + e.getMessage());
            }

        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "Invalid URL!", Toast.LENGTH_SHORT).show();
        }

    }

    private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // Resize the bitmap
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        // Recreate the new bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
    }

    private static class DownloadImage extends AsyncTask<URL, Integer, Bitmap> {

        private ProgressBar progressBar;
        private Button downloadButton;

        DownloadImage(Activity mainActivity) {
            this.progressBar = (ProgressBar) mainActivity.findViewById(R.id.progressBar);
            this.downloadButton = (Button) mainActivity.findViewById(R.id.downloadButton);
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
            downloadButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Bitmap aBitmap) {
            super.onPostExecute(aBitmap);
            progressBar.setVisibility(View.GONE);
            downloadButton.setVisibility(View.VISIBLE);
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
}
