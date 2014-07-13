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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class PictureDownloader extends Activity {

    private static final String DEFAULT_PROTOCOL = "http://";

    private ImageView imageView;
    private EditText urlEditText;

    private boolean displayProtocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_downloader);

        imageView = (ImageView) findViewById(R.id.imageView);
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

        try {

            URL image = new URL(((EditText) findViewById(R.id.imageUrl)).getText().toString());

            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            AsyncTask downloadTask = new DownloadImage(progressBar).execute(image);

            Bitmap bm = null;

            try {
                bm = (Bitmap) downloadTask.get();

                if (bm != null) {
                    Bitmap resizedBitmap = getResizedBitmap(bm, imageView.getHeight(), imageView.getWidth());
                    imageView.setImageBitmap(resizedBitmap);
                } else {
                    Log.e("archee", "Bitmap object is null");
                }

            } catch (InterruptedException e) {
                Log.e("archee", "Bitmap download failed. " + e.getMessage());
            } catch (ExecutionException e) {
                Log.e("archee", "Bitmap download failed. " + e.getMessage());
            }

        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "Invalid URL!", Toast.LENGTH_SHORT).show();
        }

    }

    private static class DownloadImage extends AsyncTask<URL, Integer, Bitmap> {

        private ProgressBar progressBar;

        DownloadImage(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        private Bitmap getBitmapFromURL(URL url) {
            try {
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                Log.e("archee", "Bitmap download failed. " + e.getMessage());
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
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Bitmap aBitmap) {
            super.onPostExecute(aBitmap);
            progressBar.setVisibility(View.GONE);
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
    private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }

}
