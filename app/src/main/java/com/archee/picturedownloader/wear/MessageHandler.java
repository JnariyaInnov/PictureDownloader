package com.archee.picturedownloader.wear;

import com.google.android.gms.wearable.MessageApi;

public interface MessageHandler {

    /**
     * Handles processed messages from a wearable device.
     * A message is considered processed when it has been acted upon in the {@link MessageApi.MessageListener#onMessageReceived(com.google.android.gms.wearable.MessageEvent)} method.
     * @param msg
     */
    void handleMessage(final String msg);

}
