package com.archee.picturedownloader;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class PictureDownloaderWear extends Activity implements WearableListView.ClickListener, MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks {

    public static final String TAG = PictureDownloaderWear.class.getSimpleName();
    public static final String SEND_ENTRY = "/com/archee/picturedownloader/entry";
    public static final String GET_ENTRIES = "/com/archee/picturedownloader/entries";

    private Adapter mAdapter;
    private List<String> mEntries = new ArrayList<String>();

    private GoogleApiClient mGoogleApiClient;
    private boolean mConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                WearableListView listView = (WearableListView) stub.findViewById(R.id.wearable_list);
                mAdapter = new Adapter(PictureDownloaderWear.this, mEntries);

                listView.setAdapter(mAdapter);
                listView.setClickListener(PictureDownloaderWear.this);
            }
        });

        mGoogleApiClient = buildApiClient();

        Toast.makeText(getApplicationContext(), "Welcome to PictureDownloader Wearable!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!mConnected) {
            mGoogleApiClient.connect();
            mConnected = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);

        if (mConnected) {
            mGoogleApiClient.disconnect();
            mConnected = false;
        }
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Adapter.ItemViewHolder itemHolder = (Adapter.ItemViewHolder) viewHolder;
        TextView entryTextview = itemHolder.textView;
        Log.d(TAG, "Entry was clicked! Contents: " + entryTextview.getText().toString());

        new SendEntryTask().execute(entryTextview.getText().toString());
    }

    @Override
    public void onTopEmptyRegionClick() {
        Log.i(TAG, "onTopEmptyRegionClick called!");
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(GET_ENTRIES)) {
            mEntries = parseUrlList(messageEvent);
            mAdapter.updateDataset(mEntries);


            Log.d(TAG, "Notifying adapter of dataset change...");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                    // Toast.makeText(getApplicationContext(), "History updated", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private List<String> parseUrlList(MessageEvent messageEvent) {
        final String data = new String(messageEvent.getData());
        Log.d(TAG, "Data from handheld: " + data);

        return Arrays.asList(data.split("\\$"));
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected: " + connectionHint);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended: " + cause);
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
    }

    private GoogleApiClient buildApiClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                // Request access only to the Wearable API
                .addApi(Wearable.API)
                .build();
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    private class SendEntryTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... entry) {
            Collection<String> nodes = getNodes();
            Log.d(TAG, "sendHistoryToWearable is sending a message to " + nodes.size() + " nodes.");

            for (String node : nodes) {
                Log.d(TAG, "Sending entry to this device node: " + node);

                PendingResult<MessageApi.SendMessageResult> result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node, SEND_ENTRY, entry[0].getBytes());

                result.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        Log.d(TAG, "Got a result \nRequestId:" + sendMessageResult.getRequestId() + "\nstatus: " + sendMessageResult.getStatus());
                    }
                });
            }

            return null;
        }
    }

    private static final class Adapter extends WearableListView.Adapter {
        private List<String> mDataset;
        private final Context mContext;
        private final LayoutInflater mInflater;

        public Adapter(Context context, List<String> dataset) {
            mContext = context;
            mDataset = dataset;
            mInflater = LayoutInflater.from(context);
        }

        public void updateDataset(List<String> newDataSet) {
            mDataset.clear();
            mDataset.addAll(newDataSet);
        }

        // Provide a reference to the type of views we're using
        public static class ItemViewHolder extends WearableListView.ViewHolder {
            private TextView textView;

            public ItemViewHolder(View itemView) {
                super(itemView);

                textView = (TextView) itemView.findViewById(R.id.entry);
            }
        }

        // Create new views for list items
        // (invoked by the WearableListView's layout manager)
        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Inflate our custom layout for list items
            return new ItemViewHolder(
                    mInflater.inflate(R.layout.entry_list_item, null));
        }

        // Replace the contents of a list item
        // Instead of creating new views, the list tries to recycle existing ones
        // (invoked by the WearableListView's layout manager)
        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder() was called...");

            // retrieve the text view
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            TextView view = itemHolder.textView;

            // replace text contents
            view.setText(mDataset.get(position));
            // replace list item's metadata
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}

