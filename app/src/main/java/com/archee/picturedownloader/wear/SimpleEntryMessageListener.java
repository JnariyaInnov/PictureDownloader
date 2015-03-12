package com.archee.picturedownloader.wear;

import android.util.Log;

import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;

public class SimpleEntryMessageListener implements MessageApi.MessageListener {

    private static final String TAG = SimpleEntryMessageListener.class.getSimpleName();

    private String mDataPath;
    private MessageHandler mMessageHandler;

    public SimpleEntryMessageListener(String dataPath, MessageHandler messageHandler) {
        mDataPath = dataPath;
        mMessageHandler = messageHandler;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d(TAG, "onMessageReceived: " + messageEvent);

        if (messageEvent.getPath().equals(mDataPath)) {
            byte[] payload = messageEvent.getData();
            final String entryStr = new String(payload);
            Log.i(TAG, "Received entry from wearable: " + entryStr);

            mMessageHandler.handleStringMessage(entryStr);
        }
    }
}
