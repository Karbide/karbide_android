package com.karbide.bluoh.datadownloader;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.gson.Gson;
import com.karbide.bluoh.R;
import com.karbide.bluoh.datatypes.HomeDataResponse;
import com.karbide.bluoh.fragments.HomeFragment;
import com.karbide.bluoh.util.AppConstants;
import com.karbide.bluoh.util.AppUtil;
import com.karbide.bluoh.util.HttpUtils;
import com.karbide.bluoh.viewadapters.HomePagerAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;


/**
 * Asynchronously handles an intent using a worker thread. Receives a ResultReceiver object and a
 * location through an intent. Tries to fetch the address for the location using a Geocoder, and
 * sends the result to the ResultReceiver.
 */
public class FetchArticleService extends IntentService
{
    private static final String TAG = "FetchAddressIS";
    /**
     * The receiver where results are forwarded from this service.
     */
    protected ResultReceiver mReceiver;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public FetchArticleService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    /**
     * Tries to get the location address using a Geocoder. If successful, sends an address to a
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
        String pageNo = intent.getStringExtra("pageno");
        // Check if receiver was properly registered.
        if (mReceiver == null)
        {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }
        getHomeData(pageNo);

    }


    private void getHomeData(String pageNo)
    {
        RequestParams rp = new RequestParams();
        HttpUtils.getWithSyncHttpClient(FetchArticleService.this, String.format(AppConstants.HOME_DATA_ENDPOINT, pageNo), rp, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try
                {
                    String str = new String(responseBody, "utf-8");
                    AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR"+statusCode+str);
                    if(statusCode == 200)
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
                        AppUtil.showToast(FetchArticleService.this, error.getMessage()+error.getLocalizedMessage());
                    }
                    else
                    {
                        AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR" + statusCode + error.getMessage());
                        String str = new String(responseBody, "utf-8");
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
                    mReceiver.send(100, bundle);
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}