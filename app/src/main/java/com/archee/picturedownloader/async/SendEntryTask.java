package com.archee.picturedownloader.async;

import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

public class SendEntryTask extends AsyncTask<Void, Void, Void> {

    private GoogleApiClient mGoogleApiClient;
    private String mDataPath;
    private byte[] mPayload;

    public SendEntryTask(GoogleApiClient googleApiClient, String dataPath, byte[] payload) {
        mGoogleApiClient = googleApiClient;
        mDataPath = dataPath;
        mPayload = payload;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Collection<String> nodes = getNodes();

        for (String node : nodes) {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, node, mDataPath, mPayload);
        }

        return null;
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
}
