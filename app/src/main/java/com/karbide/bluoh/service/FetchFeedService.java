package com.karbide.bluoh.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.karbide.bluoh.util.AppConstants;
import com.karbide.bluoh.util.AppUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by cheta on 29-12-2016.
 */

public class FetchFeedService extends IntentService {

    private static final String TAG = "FetchFeedService";
    /**
     * The receiver where results are forwarded from this service.
     */
    protected ResultReceiver mReceiver;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public FetchFeedService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    /**
     * Tries to get the data. If successful, sends data to a
     * result receiver. If unsuccessful, sends an error message instead.
     * Note: We define a {@link ResultReceiver} in * MainActivity to process content
     * sent from this service.
     * <p>
     * This service calls this method from the default worker thread with the intent that started
     * the service. When this method returns, the service automatically stops.
     */
    @Override
    protected void onHandleIntent(Intent intent)
    {
        String errorMessage = "";
        mReceiver = intent.getParcelableExtra("resultReceiver");

        // Check if receiver was properly registered.
        if (mReceiver == null)
        {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }
        getGridData();

    }

    private void getGridData()
    {
        RequestParams rp = new RequestParams();

        Log.e("REQUEST Data","------------ Req Data");
        HttpClient.getWithSyncHttpClient(FetchFeedService.this,
                String.format(AppConstants.FEED_DATA_ENDPOINT), rp,
                new AsyncHttpResponseHandler()
        {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                Log.e("GOT Data","------------ Got Data");
                try
                {
                    String str = new String(responseBody, AppConstants.DEFAULT_ENCODING);
                    AppUtil.LogMsg("RESPONSE", "RESPONSE  SUCCESS"+statusCode+str);
                    if(statusCode == AppConstants.STATUS_CODE_SUCCESS)
                    {
                        deliverResultToReceiver(str);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try
                {
                    if(error!= null)
                    {
                        AppUtil.showToast(FetchFeedService.this, error.getMessage()+error.getLocalizedMessage());
                    }
                    else
                    {
                        AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR" + statusCode + error.getMessage());
                        String str = new String(responseBody, AppConstants.DEFAULT_ENCODING);
                        AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR" + statusCode + str);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void deliverResultToReceiver(String message) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString("result", message);
//            Handler mHandler = new Handler(getMainLooper());
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
               mReceiver.send(200, bundle);
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
