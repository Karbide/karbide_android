package com.karbide.bluoh.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * Created by cheta on 03-01-2017.
 */

public class BookmarksResultReceiver extends ResultReceiver {

    private BookmarksReceiverIntf mReceiver;

    private String TAG ="BookmarksResultReceiver";

    public BookmarksResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(BookmarksReceiverIntf receiver) {
        mReceiver = receiver;
    }

    /**
     * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData)
    {
        Log.d(TAG, "onBookmarksReceiveResult start");
        if (mReceiver != null) {
            mReceiver.onReceiveBookmarkResult(resultCode, resultData);
        }
        Log.d(TAG, "onBookmarksReceiveResult end");
    }


}
