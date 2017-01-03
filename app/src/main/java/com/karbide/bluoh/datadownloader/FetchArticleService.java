package com.karbide.bluoh.datadownloader;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.karbide.bluoh.util.AppConstants;
import com.karbide.bluoh.util.AppUtil;
import com.karbide.bluoh.util.HttpUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;


/**
 * Asynchronously handles an intent using a worker thread. Receives a ResultReceiver object and a
 * location through an intent. Tries to fetch the address for the location using a Geocoder, and
 * sends the result to the ResultReceiver.
 */
public class FetchArticleService extends IntentService
{
    private static final String TAG = "FetchArticleService";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public FetchArticleService() {
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
        final ResultReceiver mReceiver = intent.getParcelableExtra("resultReceiver");
        String pageNo = intent.getStringExtra("pageno");
        // Check if receiver was properly registered.
        if (mReceiver == null)
        {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }
        getHomeData(mReceiver, pageNo);

    }

    private void getHomeData(final ResultReceiver resultReceiver, String pageNo)
    {
        RequestParams rp = new RequestParams();

        Log.e("REQUEST Data","------------ Req Data");
        HttpUtils.getWithSyncHttpClient(FetchArticleService.this, String.format(AppConstants.HOME_DATA_ENDPOINT, pageNo), rp, new AsyncHttpResponseHandler()
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
                        Bundle bundle = new Bundle();
                        bundle.putString("result", str);
                        resultReceiver.send(100, bundle);
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
                        String str = new String(responseBody, AppConstants.DEFAULT_ENCODING);
                        AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR" + statusCode + str);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}