package com.karbide.bluoh.service;

import android.content.Context;
import com.karbide.bluoh.dao.LoginResponse;
import com.karbide.bluoh.util.AppSharedPreference;
import com.karbide.bluoh.util.AppUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import cz.msebera.android.httpclient.entity.StringEntity;

import static com.karbide.bluoh.util.AppConstants.SERVER_BASE_URL;

public class HttpClient
{
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static AsyncHttpClient syncclient = new SyncHttpClient();

    public static void get(Context ctx, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        LoginResponse userInfo = AppSharedPreference.getInstance(ctx).getUserInfo();
        client.setBasicAuth(userInfo.getUsername(),userInfo.getPassword());
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getWithSyncHttpClient(Context ctx, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {

        AppUtil.LogError("URL", "URL IS "+getAbsoluteUrl(url));
        LoginResponse userInfo = AppSharedPreference.getInstance(ctx).getUserInfo();
        syncclient.setBasicAuth(userInfo.getUsername(),userInfo.getPassword());
        syncclient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void deleteSync(Context ctx,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        LoginResponse userInfo = AppSharedPreference.getInstance(ctx).getUserInfo();
        syncclient.setBasicAuth(userInfo.getUsername(),userInfo.getPassword());
        syncclient.delete(getAbsoluteUrl(url), params, responseHandler);
    }


    public static void postWithJson(Context ctx, String url, StringEntity stringEntity, AsyncHttpResponseHandler responseHandler) {
        LoginResponse userInfo = AppSharedPreference.getInstance(ctx).getUserInfo();
        if(userInfo!=null) {
            client.setBasicAuth(userInfo.getUsername(), userInfo.getPassword());
            AppUtil.LogError("URL ", "User ID " + userInfo.getUsername() + " PASSWORD:= " + userInfo.getPassword());
        }
        AppUtil.LogError("URL", "URL IS "+getAbsoluteUrl(url));
        client.post(ctx, getAbsoluteUrl(url), stringEntity, "application/json", responseHandler);
    }

    public static void postWithJsonSync(Context ctx, String url, StringEntity stringEntity, AsyncHttpResponseHandler responseHandler) {
        LoginResponse userInfo = AppSharedPreference.getInstance(ctx).getUserInfo();
        if(userInfo!=null) {
            syncclient.setBasicAuth(userInfo.getUsername(), userInfo.getPassword());
            AppUtil.LogError("URL ", "User ID " + userInfo.getUsername() + " PASSWORD:= " + userInfo.getPassword());
        }
        AppUtil.LogError("URL", "URL IS "+getAbsoluteUrl(url));
        syncclient.post(ctx, getAbsoluteUrl(url), stringEntity, "application/json", responseHandler);
    }


    public static void testNotification(Context context, AsyncHttpResponseHandler responseHandler){
        RequestParams param = new RequestParams();
        param.put("gcm_id", ""+AppSharedPreference.getInstance(context).getGcmId());
        client.post("http://aapkadriver.com/fcm/fcm.php", param, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return SERVER_BASE_URL + relativeUrl;
    }
}