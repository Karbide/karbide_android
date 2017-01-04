package com.karbide.bluoh.datadownloader;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * Created by cheta on 03-01-2017.
 */

public class BookmarksResultReceiver extends ResultReceiver {

    private DataReceiverIntf mReceiver;

    public BookmarksResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(DataReceiverIntf receiver) {
        mReceiver = receiver;
    }

    /**
     * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData)
    {
        Log.d("BOOKMARKS", "onReceiveResult start");
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }

        Log.d("BOOKMARKS", "onReceiveResult end");
    }


}
