package com.karbide.bluoh.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.gson.Gson;
import com.karbide.bluoh.dao.core.Bookmark;
import com.karbide.bluoh.util.AppConstants;
import com.karbide.bluoh.util.AppUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

import static com.karbide.bluoh.util.AppConstants.BOOKMARK_GET_OPERATION;
import static com.karbide.bluoh.util.AppConstants._bookmarkObj;
import static com.karbide.bluoh.util.AppConstants._bookmarkPgNo;
import static com.karbide.bluoh.util.AppConstants._bookmarksResponseData;

/**
 * Created by cheta on 03-01-2017.
 */

public class ManageBookmarksService extends IntentService {

    private static final String TAG = "ManageBookmarksService";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public ManageBookmarksService() {
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

        final ResultReceiver mReceiver = intent.getParcelableExtra(AppConstants._resultReceiverBookmarks);
        final String operationType = intent.getStringExtra(AppConstants._operationType);
        Log.e(TAG,"-------------- BOOKMARK INTENT FIRED for operation "+operationType);

        // Check if receiver was properly registered.
        if (mReceiver == null)
        {
            Log.e(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }
        if(operationType.equalsIgnoreCase(BOOKMARK_GET_OPERATION)){
            final int pageNo = Integer.parseInt(intent.getStringExtra(_bookmarkPgNo));
            getBookmark(mReceiver, pageNo);
        }else{
            final Bookmark bookmark = (Bookmark)intent.getSerializableExtra(_bookmarkObj);
            if(operationType.equalsIgnoreCase(AppConstants.BOOKMARK_UPDATE_OPERATION)){
                updateBookmark(mReceiver, bookmark);
            }else if(operationType.equalsIgnoreCase(AppConstants.BOOKMARK_DELETE_OPERATION)){
                deleteBookmark(mReceiver, bookmark);
            }
        }
    }

    private void getBookmark(final ResultReceiver resultReceiver, int pageNo)
    {
        RequestParams rp = new RequestParams();

        Log.e(TAG,"Bookmark Req Data");

        HttpClient.getWithSyncHttpClient(this, String.format(AppConstants.GET_BOOKMARK_ENDPOINT, pageNo), rp, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                try
                {
                    String str = new String(responseBody, AppConstants.DEFAULT_ENCODING);
                    AppUtil.LogMsg(TAG, "RESPONSE "+AppConstants.BOOKMARK_GET_OPERATION+ " BOOKMARKS SUCCESS STATUS CODE "+statusCode+" RESPONSE RCV "+str);
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants._operationType,AppConstants.BOOKMARK_GET_OPERATION);
                    bundle.putString(_bookmarksResponseData, str);
                    resultReceiver.send(AppConstants.STATUS_CODE_SUCCESS, bundle);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                try
                {
                    String str = new String(responseBody, AppConstants.DEFAULT_ENCODING);
                    AppUtil.LogMsg(TAG, "RESPONSE "+AppConstants.BOOKMARK_GET_OPERATION+ " BOOKMARKS STATUS FAILURE CODE "+statusCode+" RESPONSE RCV "+str);
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants._operationType,AppConstants.BOOKMARK_GET_OPERATION);
                    bundle.putString(AppConstants._bookmarksResponseData,str);
                    resultReceiver.send(AppConstants.STATUS_CODE_FAILURE, bundle);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void updateBookmark(final ResultReceiver resultReceiver, Bookmark bookmark)  {

        ArrayList <Bookmark> bookmarks = new  ArrayList();
        bookmarks.add(bookmark);
        AppUtil.LogMsg(TAG, "UPDATE BOOKMARK JSON" + new Gson().toJson(bookmarks));

        StringEntity entity = null;
        try {
            entity = new StringEntity(new Gson().toJson(bookmarks));
        }catch (UnsupportedEncodingException ex){
            ex.printStackTrace();
        }

        HttpClient.postWithJsonSync(this, AppConstants.ADD_BOOKMARK_ENDPOINT, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    String str = new String(responseBody, AppConstants.DEFAULT_ENCODING);
                    AppUtil.LogMsg(TAG, "RESPONSE "+AppConstants.BOOKMARK_UPDATE_OPERATION+ " BOOKMARKS SUCCESS STATUS CODE "+statusCode+" RESPONSE RCV "+str);
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants._operationType,AppConstants.BOOKMARK_UPDATE_OPERATION);
                    bundle.putString(AppConstants._bookmarksResponseData, str);
                    resultReceiver.send(AppConstants.STATUS_CODE_SUCCESS, bundle);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String str = new String(responseBody, AppConstants.DEFAULT_ENCODING);
                    AppUtil.LogMsg(TAG, "RESPONSE "+AppConstants.BOOKMARK_UPDATE_OPERATION+ " BOOKMARKS FAILURE STATUS CODE "+statusCode+" RESPONSE RCV "+str);
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants._operationType,AppConstants.BOOKMARK_UPDATE_OPERATION);
                    bundle.putString(AppConstants._bookmarksResponseData, str);
                    resultReceiver.send(AppConstants.STATUS_CODE_FAILURE, bundle);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void deleteBookmark(final ResultReceiver resultReceiver, Bookmark bookmark) {

        int deckId = bookmark.getDeckId();
        String cardId = bookmark.getCardId();

        RequestParams rp = new RequestParams();
        HttpClient.deleteSync(this, String.format(AppConstants.DELETE_BOOKMARK_ENDPOINT, deckId, cardId), rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String str = new String(responseBody, AppConstants.DEFAULT_ENCODING);
                    AppUtil.LogMsg(TAG, "RESPONSE "+AppConstants.BOOKMARK_DELETE_OPERATION+ " BOOKMARKS SUCCESS STATUS CODE "+statusCode+" RESPONSE RCV "+str);
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants._operationType,AppConstants.BOOKMARK_DELETE_OPERATION);
                    bundle.putString(AppConstants._bookmarksResponseData, str);
                    resultReceiver.send(AppConstants.STATUS_CODE_SUCCESS, bundle);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    String str = new String(responseBody, AppConstants.DEFAULT_ENCODING);
                    AppUtil.LogMsg(TAG, "RESPONSE "+AppConstants.BOOKMARK_DELETE_OPERATION+ " BOOKMARKS FAILURE STATUS CODE "+statusCode+" RESPONSE RCV "+str);
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants._operationType,AppConstants.BOOKMARK_DELETE_OPERATION);
                    bundle.putString(AppConstants._bookmarksResponseData, str);
                    resultReceiver.send(AppConstants.STATUS_CODE_SUCCESS, bundle);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
