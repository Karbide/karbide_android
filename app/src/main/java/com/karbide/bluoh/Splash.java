package com.karbide.bluoh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.karbide.bluoh.datatypes.FacebookData;
import com.karbide.bluoh.util.AppConstants;
import com.karbide.bluoh.util.AppSharedPreference;
import com.karbide.bluoh.util.AppUtil;
import com.karbide.bluoh.util.HttpUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Splash extends BaseActivity implements View.OnClickListener, FacebookCallback<LoginResult> {
    private static final int SPLASH_TIME = 2 * 1000;
    private ImageView imageView;
    private Button btnFacebokSignup;
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private LinearLayout fbLoginLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        subscribeToPushService();
        fbLoginLayout = (LinearLayout)findViewById(R.id.fbLoginLayout);
        imageView = (ImageView)findViewById(R.id.imvLogo);
        btnFacebokSignup = (Button) findViewById(R.id.btnFacebookSignup);
        AppUtil.LogError("TAG", ""+AppSharedPreference.getInstance(this).getGcmId());
        if(AppSharedPreference.getInstance(this).getUserInfo() != null)
        {
            fbLoginLayout.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_animation));
            openNextScreen();
        }
        else
        {
            fbLoginLayout.setVisibility(View.VISIBLE);
            btnFacebokSignup.setOnClickListener(this);
            loginManager = LoginManager.getInstance();
            callbackManager = CallbackManager.Factory.create();
        }
    }

    private void openNextScreen()
    {
        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                getHomeData("0");
            }
        }, SPLASH_TIME);
    }

    private void openHomeScreen(String homeData)
    {
        Intent intent = new Intent(Splash.this, MainActivity.class);
        intent.putExtra("homedata", homeData);
        startActivity(intent);
        Splash.this.finish();
    }

    private void getHomeData(String pageNo)
    {
        RequestParams rp = new RequestParams();
        HttpUtils.get(Splash.this, String.format(AppConstants.HOME_DATA_ENDPOINT, pageNo), rp, new AsyncHttpResponseHandler()
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
                        openHomeScreen(str);
                    }
                    else
                    {

                    }
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try
                {
                    if(error!= null)
                    {
                        AppUtil.showToast(Splash.this, error.getMessage()+error.getLocalizedMessage());
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

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnFacebookSignup:
                facebookLogin();
                break;
        }
    }

    private void facebookLogin()
    {
        loginManager.registerCallback(callbackManager, this);
        Collection<String> permissions = Arrays.asList("public_profile","email");
        loginManager.logInWithReadPermissions(this, permissions);
    }

    private void getFacebookProfileInfo(AccessToken accessToken)
    {
//        showProgressDialog(R.string.please_wait);
        Log.e("TAG", "GETTING INFORMATION ENTRY");
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,gender,email,picture.width(400).height(400)");
        GraphRequest request = GraphRequest.newGraphPathRequest(accessToken, "me", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                Log.e("TAG", "GETTING INFORMATION");
                try {
                    FacebookData data = new Gson().fromJson(graphResponse.getRawResponse(), FacebookData.class);
                    data.setGcmKey(AppSharedPreference.getInstance(Splash.this).getGcmId());
                    signupUser(data);
                    Log.e("tag", "Response" + new Gson().toJson(data));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("TAG", "GETTING INFORMATION FAILED");
                }
            }
        });
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onSuccess(LoginResult loginResult)
    {
        getFacebookProfileInfo(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {
        AppUtil.showToast(this, "Facebook login canceled");
    }

    @Override
    public void onError(FacebookException error) {
        AppUtil.showToast(this, "Facebook login error"+error.getMessage());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void signupUser(FacebookData fbData) throws UnsupportedEncodingException
    {
        showProgressDialog(R.string.please_wait);
        AppUtil.LogMsg("RESPONSE", "BOOKMARK JSON"+new Gson().toJson(fbData));
        StringEntity entity = new StringEntity(new Gson().toJson(fbData));
        HttpUtils.postWithJson(Splash.this, AppConstants.LOGIN, entity,new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                hideProgressDialog();
                try
                {
                    String str = new String(responseBody, "utf-8");
                    AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR"+statusCode+str);
                    if(statusCode == 200)
                    {
                        AppSharedPreference.getInstance(Splash.this).saveUserDetail(str);
                        AppUtil.showToast(Splash.this, "Facebook login success");
                        Intent intent = new Intent(Splash.this, MainActivity.class);
                        startActivity(intent);
                        Splash.this.finish();

                    }
                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                    AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR"+statusCode);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                hideProgressDialog();
                try
                {
                    if(error!= null)
                    {
                        AppUtil.showToast(Splash.this, error.getMessage()+error.getLocalizedMessage());
                        AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR" + statusCode + error.getMessage());
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

    private void subscribeToPushService()
    {
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        String token = FirebaseInstanceId.getInstance().getToken();
        if(token!=null) {
            Log.v("TAG", "gcm"+token);
            AppSharedPreference.getInstance(Splash.this).saveGcmId(token);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(tokenReceiver,
                new IntentFilter("tokenReceiver"));
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(tokenReceiver);
    }

    BroadcastReceiver tokenReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String token = intent.getStringExtra("token");
            if(token != null)
            {
                //send token to your server or what you want to do
                Log.e("TAG", "gcm"+token);
                AppSharedPreference.getInstance(Splash.this).saveGcmId(token);
            }
        }
    };
}
