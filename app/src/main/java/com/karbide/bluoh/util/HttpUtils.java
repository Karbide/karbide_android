package com.karbide.bluoh.util;

import android.content.Context;

import com.karbide.bluoh.datatypes.LoginResponse;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.entity.StringEntity;

public class HttpUtils
{
    private static final String BASE_URL = "http://api.chequemate.io/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(Context ctx, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        LoginResponse userInfo = AppSharedPreference.getInstance(ctx).getUserInfo();
        client.setBasicAuth(userInfo.getUsername(),userInfo.getPassword());
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context ctx,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        LoginResponse userInfo = AppSharedPreference.getInstance(ctx).getUserInfo();
        client.setBasicAuth(userInfo.getUsername(),userInfo.getPassword());
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(Context ctx,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        LoginResponse userInfo = AppSharedPreference.getInstance(ctx).getUserInfo();
        client.setBasicAuth(userInfo.getUsername(),userInfo.getPassword());
        client.delete(getAbsoluteUrl(url), params, responseHandler);
    }


    public static void getByUrl(Context ctx,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        LoginResponse userInfo = AppSharedPreference.getInstance(ctx).getUserInfo();
        client.setBasicAuth(userInfo.getUsername(),userInfo.getPassword());
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(Context ctx,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        LoginResponse userInfo = AppSharedPreference.getInstance(ctx).getUserInfo();
        client.setBasicAuth(userInfo.getUsername(),userInfo.getPassword());
        client.post(url, params, responseHandler);
    }

    public static void postWithJson(Context ctx, String url, StringEntity stringEntity, AsyncHttpResponseHandler responseHandler) {
        LoginResponse userInfo = AppSharedPreference.getInstance(ctx).getUserInfo();
        if(userInfo!=null) {
            client.setBasicAuth(userInfo.getUsername(), userInfo.getPassword());
            AppUtil.LogError("URL", " User ID" + userInfo.getUsername() + " PASSWORD:= " + userInfo.getPassword());
        }
        AppUtil.LogError("URL", "URL IS "+getAbsoluteUrl(url));
        client.post(ctx, getAbsoluteUrl(url), stringEntity, "application/json", responseHandler);
    }

    public static void testNotification(Context context, AsyncHttpResponseHandler responseHandler)
    {
        RequestParams param = new RequestParams();
        param.put("gcm_id", ""+AppSharedPreference.getInstance(context).getGcmId());
        client.post("http://aapkadriver.com/fcm/fcm.php", param, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}