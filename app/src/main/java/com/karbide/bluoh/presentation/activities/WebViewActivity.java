package com.karbide.bluoh.presentation.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.karbide.bluoh.R;
import com.karbide.bluoh.dal.AppDatabaseHelper;
import com.karbide.bluoh.dao.core.Card;
import com.karbide.bluoh.presentation.components.CustomTextView;
import com.karbide.bluoh.util.AppUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by FourBrick on 5/12/2016.
 */
public class WebViewActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    private Toolbar mToolbar;
    private WebView webView;
    private ProgressBar progrssBar;
    private String url;
    private Card summary = null;
    private int deckId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras() != null)
        {
            Bundle bundle = getIntent().getExtras();
            AppUtil.LogError("DATA", bundle.getString("data"));
            summary = new Gson().fromJson(bundle.getString("data"), Card.class);
            deckId = bundle.getInt("deckId");
            url = summary.getUrl();
            if (url.substring(0, 4).equalsIgnoreCase("http") == false) {
                url = "http:\\" + url;
            }
        }
        getSupportActionBar().setTitle(""+summary.getTitle());

        CheckBox buttonLike = (CheckBox)findViewById(R.id.buttonLike);
        CheckBox buttonBookmark = (CheckBox)findViewById(R.id.buttonBookmark);
        ImageButton buttonShare = (ImageButton)findViewById(R.id.imageButtonShare);
        CustomTextView tvSource = (CustomTextView)findViewById(R.id.tvNewsSource);
        ImageView ivSourceImage = (ImageView)findViewById(R.id.ivSourceImage);
        LinearLayout llSourceImage = (LinearLayout)findViewById(R.id.llSourceImage);

        if(summary.getArticleSourceLogo()!= null)
        {
            Log.e("IMG", "SOURCE : "+summary.getArticleSourceLogo());
            tvSource.setVisibility(View.GONE);
            llSourceImage.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(summary.getArticleSourceLogo(), ivSourceImage);
        }
        else
        {
            tvSource.setVisibility(View.VISIBLE);
            llSourceImage.setVisibility(View.GONE);
            tvSource.setText("@"+summary.getArticleSourceName());
        }
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUtil.shareData(WebViewActivity.this, ""+summary.getTitle()+"\n"+summary.getUrl());
            }
        });

        buttonBookmark.setChecked(AppDatabaseHelper.getInstance(WebViewActivity.this).isBookMarked(deckId));
        buttonBookmark.setOnCheckedChangeListener(this);
        buttonLike.setOnCheckedChangeListener(this);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        progrssBar = (ProgressBar)findViewById(R.id.progressbar);
        webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress)
            {
                progrssBar.setProgress(progress);
            }
        });
        webView.setWebViewClient(new AppWebViewClients(progrssBar));
        webView.loadUrl(url);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }


    public class AppWebViewClients extends WebViewClient {
        private ProgressBar progressBar;

        public AppWebViewClients(ProgressBar progressBar) {
            this.progressBar=progressBar;
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
