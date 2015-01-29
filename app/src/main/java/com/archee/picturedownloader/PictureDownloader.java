package com.archee.picturedownloader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.archee.picturedownloader.async.AsyncImageCallback;
import com.archee.picturedownloader.async.DownloadImage;
import com.archee.picturedownloader.async.ImageResponse;
import com.archee.picturedownloader.storage.domain.Entry;
import com.archee.picturedownloader.storage.Storage;
import com.archee.picturedownloader.storage.StorageFactory;
import com.archee.picturedownloader.enums.StorageType;
import com.archee.picturedownloader.views.ClearableEditText;
import com.archee.picturedownloader.views.ListViewActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;


public class PictureDownloader extends Activity implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "PictureDownloader";
    public static final String EXTRA_HISTORY = "com.archee.picturedownloader.HISTORY";
    public static final String EXTRA_URL = "com.archee.picturedownloader.URL";
    public static final String SEND_ENTRY = "/com/archee/picturedownloader/entry";
    public static final String GET_ENTRIES = "/com/archee/picturedownloader/entries";

    private static final String DEFAULT_PROTOCOL = "http://";

    private ClearableEditText urlEditText;
    private ProgressBar progressBar;
    private Button downloadButton;
    private ImageView imageView;

    private boolean displayProtocol;
    private static Storage storage;
    private GoogleApiClient mGoogleApiClient;
    private boolean mConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Get references to UI components
        urlEditText = (ClearableEditText) findViewById(R.id.imageUrl);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        downloadButton = (Button) findViewById(R.id.downloadButton);
        imageView = (ImageView) findViewById(R.id.imageView);

        // Storage object user for persisting data
        storage = StorageFactory.create(getApplicationContext(), StorageType.DATABASE);

        // Auto-populate the URL protocol in text box when pressed
        displayProtocol = true;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mConnected) {
            mGoogleApiClient.connect();
        }

        sendHistoryToWearable();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mConnected) {
            mGoogleApiClient.disconnect();
        }
    }

    public void setDefaultProtocol(View view) {
        if (displayProtocol) {
            urlEditText.setText(DEFAULT_PROTOCOL);
            displayProtocol = false;
            urlEditText.setSelection(urlEditText.getText().length());
        }
    }

    public void onClearPress(View view) {
        urlEditText.clearText();
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

    @Override // ConnectionCallbacks
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "Google API Client was connected");
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override // ConnectionCallbacks
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Connection to Google API client was suspended: " + cause);
    }

    @Override // OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection to Google API client has failed");
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
    }

    @Override // MessageListener
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent);

        if (messageEvent.getPath().equals(SEND_ENTRY)) {
            byte[] payload = messageEvent.getData();
            final String entryStr = new String(payload);
            Log.i(TAG, "Received entry from wearable: " + entryStr);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    urlEditText.setText(entryStr);
                    onDownloadPress(null); // press download button
                }
            });
        }
    }

    private void sendHistoryToWearable() {
        Log.i(TAG, "Sending History to wearable...");
        new SendEntryTask().execute();
    }

    private byte[] createHistoryPayload() {
        Set<Entry> history = storage.getHistory();
        StringBuilder sb = new StringBuilder();

        for (Entry entry : history) {
            sb.append(entry);
            sb.append("$");
        }

        return sb.toString().getBytes();
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

                storage.addEntry(response.getUrl().toString(), new Date());
                sendHistoryToWearable();
            } else {
                Log.e(TAG, "Bitmap object is null");
            }
        }
    }

    private class SendEntryTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Collection<String> nodes = getNodes();
            Log.d(TAG, "sendHistoryToWearable is sending a message to " + nodes.size() + " nodes.");
            for (String node : nodes) {
                Wearable.MessageApi.sendMessage(mGoogleApiClient, node, GET_ENTRIES, createHistoryPayload());
            }

            return null;
        }

        private Collection<String> getNodes() {
            HashSet<String> results = new HashSet<String>();
            NodeApi.GetConnectedNodesResult nodes =
                    Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

            for (Node node : nodes.getNodes()) {
                Log.d(TAG, "Node display name: " + node.getDisplayName());
                results.add(node.getId());
            }

            return results;
        }
    }
}
