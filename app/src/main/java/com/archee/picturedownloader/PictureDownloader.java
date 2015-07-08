package com.archee.picturedownloader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.archee.picturedownloader.async.DownloadCompleteHandler;
import com.archee.picturedownloader.async.DownloadImageTask;
import com.archee.picturedownloader.async.ImageResponse;
import com.archee.picturedownloader.async.SendEntryTask;
import com.archee.picturedownloader.storage.domain.Entry;
import com.archee.picturedownloader.storage.Storage;
import com.archee.picturedownloader.storage.StorageFactory;
import com.archee.picturedownloader.enums.StorageType;
import com.archee.picturedownloader.views.ClearableEditText;
import com.archee.picturedownloader.views.ListViewActivity;
import com.archee.picturedownloader.wear.MessageHandler;
import com.archee.picturedownloader.wear.SimpleEntryMessageListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;


public class PictureDownloader extends Activity {

    public static final String TAG = "PictureDownloader";
    public static final String EXTRA_HISTORY = "com.archee.picturedownloader.HISTORY";
    public static final String EXTRA_URL = "com.archee.picturedownloader.URL";
    public static final String EXTRA_IMAGE = "com.archee.picturedownloader.image";
    public static final String SEND_ENTRY = "/com/archee/picturedownloader/entry";
    public static final String GET_ENTRIES = "/com/archee/picturedownloader/entries";
    public static final int DOWNLOAD_COMPLETE_NOTIF = 1;

    private static final String DEFAULT_PROTOCOL = "http://";

    private ClearableEditText urlEditText;
    private ProgressBar progressBar;
    private Button downloadButton;
    private ImageView imageView;
    private boolean displayProtocol;
    private static Storage storage;

    /* Google Wearable API stuff */
    private GoogleApiClient mGoogleApiClient;
    private boolean mConnected = false;
    private MessageApi.MessageListener mEntryMessageListener = new SimpleEntryMessageListener(SEND_ENTRY, new SimpleEntryMessageHandler());
    private WearableConnectionHandler mConnectionHandler = new WearableConnectionHandler();

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
                .addConnectionCallbacks(mConnectionHandler)
                .addOnConnectionFailedListener(mConnectionHandler)
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
            new DownloadImageTask(progressBar, new ImageDownloadHandler()).execute(imageUrl);

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

    private void sendHistoryToWearable() {
        Log.i(TAG, "Sending History to wearable...");
        new SendEntryTask(mGoogleApiClient, GET_ENTRIES, createHistoryPayload()).execute();
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

    private class SimpleEntryMessageHandler implements MessageHandler {
        @Override
        public void handleStringMessage(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    urlEditText.setText(message);
                    onDownloadPress(null); // press download button
                }
            });
        }
    }

    /**
     * A callback handler that gets called from the AsyncTask when the image download completes.
     */
    private class ImageDownloadHandler implements DownloadCompleteHandler {

        private static final String RATING = "rating";
        private static final String RATING_ITEM = "item";
        private static final String RATING_LIKE = "like";
        private static final String RATING_DISLIKE = "dislike";
        private static final String RATING_FAVORITE = "favorite";
        private static final String RATING_COMMENT = "comment";

        @Override
        public void onDownloadComplete(ImageResponse response) {
            if (response != null && response.getResponseCode() != 404) {
                Bitmap resizedBitmap = getResizedBitmap(response.getImage(), imageView.getHeight(), imageView.getWidth());
                imageView.setImageBitmap(resizedBitmap);

                storage.addEntry(response.getUrl().toString(), new Date());
                sendHistoryToWearable();

                NotificationCompat.Builder mainNotifBuilder = new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(android.R.drawable.ic_menu_gallery)
                        .setContentTitle("Picture download complete.")
                        .setTicker("Your picture download is complete.")
                        .setVibrate(new long[]{0, 300, 100, 300});

                Bitmap wearImage = getResizedBitmap(response.getImage(), 640, 400);
                Notification secondPage = new NotificationCompat.Builder(getApplicationContext())
                        .extend(new NotificationCompat.WearableExtender().setHintShowBackgroundOnly(true))
                        .build();

                NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender()
                        .addPage(secondPage)
                        .setBackground(wearImage)
                        .addActions(buildNotificationActionList(response.getUrl().toString()));

                Notification mainNotif = mainNotifBuilder.extend(wearableExtender).build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.notify(DOWNLOAD_COMPLETE_NOTIF, mainNotif);
            } else {
                Log.e(TAG, "Bitmap object is null");
            }
        }

        @TargetApi(20)
        private List<NotificationCompat.Action> buildNotificationActionList(String urlToRate) {

            RemoteInput remoteInput = new RemoteInput.Builder(RATING_COMMENT)
                    .setLabel("Any thoughts on this picture?")
                    .build();

            Intent likeActionIntent = new Intent(getApplicationContext(), PictureRatingService.class).putExtra(RATING, RATING_LIKE).putExtra(RATING_ITEM, urlToRate);
            Intent dislikeActionIntent = new Intent(getApplicationContext(), PictureRatingService.class).putExtra(RATING, RATING_DISLIKE).putExtra(RATING_ITEM, urlToRate);
            Intent favoriteActionIntent = new Intent(getApplicationContext(), PictureRatingService.class).putExtra(RATING, RATING_FAVORITE).putExtra(RATING_ITEM, urlToRate);
            Intent commentActionIntent = new Intent(getApplicationContext(), PictureRatingService.class).putExtra(RATING, RATING_COMMENT).putExtra(RATING_ITEM, urlToRate);
            PendingIntent likeActionPendingIntent = PendingIntent.getService(getApplicationContext(), 0, likeActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent dislikeActionPendingIntent = PendingIntent.getService(getApplicationContext(), 1, dislikeActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent favoriteActionPendingIntent = PendingIntent.getService(getApplicationContext(), 2, favoriteActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent commentActionPendingIntent = PendingIntent.getService(getApplicationContext(), 3, commentActionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action.Builder likeAction = new NotificationCompat.Action.Builder(R.drawable.ic_action_good, "Like", likeActionPendingIntent);
            NotificationCompat.Action.Builder dislikeAction = new NotificationCompat.Action.Builder(R.drawable.ic_action_bad, "Dislike", dislikeActionPendingIntent);
            NotificationCompat.Action.Builder favoriteAction = new NotificationCompat.Action.Builder(R.drawable.ic_action_favorite, "Favorite", favoriteActionPendingIntent);
            NotificationCompat.Action.Builder commentAction = new NotificationCompat.Action.Builder(R.drawable.ic_action_chat, "Comment", commentActionPendingIntent)
                    .addRemoteInput(remoteInput);

            return Arrays.asList(likeAction.build(), dislikeAction.build(), favoriteAction.build(), commentAction.build());
        }
    }

    private class WearableConnectionHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        @Override // ConnectionCallbacks
        public void onConnected(Bundle connectionHint) {
            Log.d(TAG, "Google API Client was connected");
            Wearable.MessageApi.addListener(mGoogleApiClient, mEntryMessageListener);
        }

        @Override // ConnectionCallbacks
        public void onConnectionSuspended(int cause) {
            Log.d(TAG, "Connection to Google API client was suspended: " + cause);
        }

        @Override // OnConnectionFailedListener
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e(TAG, "Connection to Google API client has failed");
            Wearable.MessageApi.removeListener(mGoogleApiClient, mEntryMessageListener);
        }
    }
}
