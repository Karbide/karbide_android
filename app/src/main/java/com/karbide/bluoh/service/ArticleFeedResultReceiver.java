package com.karbide.bluoh.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * Created by cheta on 29-12-2016.
 */

public class ArticleFeedResultReceiver extends ResultReceiver
{
    private DataReceiverIntf mReceiver;

    private static final String TAG = "ArticleFeedResultReceiver";

    public ArticleFeedResultReceiver(Handler handler) {
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
        Log.d("ARTICLE", "onReceiveResult start");
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }

        Log.d("ARTICLE", "onReceiveResult end");
    }
}
