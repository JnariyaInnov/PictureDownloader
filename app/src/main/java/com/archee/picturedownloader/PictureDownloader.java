package com.archee.picturedownloader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.archee.picturedownloader.async.AsyncImageCallback;
import com.archee.picturedownloader.async.DownloadImage;
import com.archee.picturedownloader.async.ImageResponse;
import com.archee.picturedownloader.storage.domain.Entry;
import com.archee.picturedownloader.storage.Storage;
import com.archee.picturedownloader.storage.StorageFactory;
import com.archee.picturedownloader.enums.StorageType;
import com.archee.picturedownloader.views.ListViewActivity;


public class PictureDownloader extends Activity {

    public static final String TAG = "PictureDownloader";
    public static final String EXTRA_HISTORY = "com.archee.picturedownloader.HISTORY";
    public static final String EXTRA_URL = "com.archee.picturedownloader.URL";

    private static final String DEFAULT_PROTOCOL = "http://";

    private EditText urlEditText;
    private ProgressBar progressBar;
    private Button downloadButton;
    private ImageView imageView;

    private boolean displayProtocol;
    private Storage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Get references to UI components
        urlEditText = (EditText) findViewById(R.id.imageUrl);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        downloadButton = (Button) findViewById(R.id.downloadButton);
        imageView = (ImageView) findViewById(R.id.imageView);

        // Storage object user for persisting data
        storage = StorageFactory.create(getApplicationContext(), StorageType.DATABASE);

        // Auto-populate the URL protocol in text box when pressed
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
        String imageUrlStr = urlEditText.getText().toString();

        try {
            URL imageUrl = new URL(imageUrlStr);

            // Attempt to download image in a background thread.
            new DownloadImage(progressBar, new ImageDownloadHandler()).execute(imageUrl);

        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "Invalid URL!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onHistoryPress(View view) {
        Set<Entry> history = storage.getHistory();

        if (!history.isEmpty()) {
            // Convert history List to ArrayList so we can pass it in the intent.
            ArrayList<Entry> historyArrayList = new ArrayList<Entry>();
            historyArrayList.addAll(history);

            // Create intent with history list and pass it to ListViewActivity
            Intent intent = new Intent(this, ListViewActivity.class);
            intent.putParcelableArrayListExtra(PictureDownloader.EXTRA_HISTORY, historyArrayList);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(this, "There is no history", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            String url = data.getStringExtra(PictureDownloader.EXTRA_URL);
            urlEditText.setText(url);
            displayProtocol = false;
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

    /**
     * A callback handler that gets called from the AsyncTask when the image download completes.
     */
    private class ImageDownloadHandler implements AsyncImageCallback {
        @Override
        public void onDownloadComplete(ImageResponse response) {
            if (response != null && response.getResponseCode() != 404) {
                Bitmap resizedBitmap = getResizedBitmap(response.getImage(), imageView.getHeight(), imageView.getWidth());
                imageView.setImageBitmap(resizedBitmap);

                Log.d(PictureDownloader.TAG, "image URL: " + response.getUrl().toString());
                storage.addEntry(response.getUrl().toString(), new Date());
            } else {
                Log.e(TAG, "Bitmap object is null");
            }
        }
    }
}
