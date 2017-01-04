package com.karbide.bluoh;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;

import com.google.gson.Gson;
import com.karbide.bluoh.dal.AppDatabaseHelper;
import com.karbide.bluoh.dao.Bookmark;
import com.karbide.bluoh.dao.Card;
import com.karbide.bluoh.dao.Content;
import com.karbide.bluoh.dao.DeckDetailResponse;
import com.karbide.bluoh.ui.DepthVerticalPageTransformer;
import com.karbide.bluoh.ui.VerticalViewPager;
import com.karbide.bluoh.util.AppConstants;
import com.karbide.bluoh.util.AppUtil;
import com.karbide.bluoh.service.HttpClient;
import com.karbide.bluoh.util.OnSwipeTouchListener;
import com.karbide.bluoh.viewadapters.DeckVerticalPagerAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class DeckDetailActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private Toolbar toolbar;
    private VerticalViewPager mainPager;
    private List<Card> allCards;
    private int deckId = -1;
    private String title = null;
    private Content deckContent;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(getIntent().getExtras() != null)
        {
            deckId = getIntent().getIntExtra("deckId", -1);
            title = getIntent().getStringExtra("title");
            deckContent = new Gson().fromJson(getIntent().getStringExtra("content"), Content.class);
            AppUtil.LogMsg("TITLE", "TITLE IS "+ title);
            setTitle(""+title);
        }

        mainPager = (VerticalViewPager)findViewById(R.id.mainPager);
        mainPager.setPageTransformer(false, new DepthVerticalPageTransformer());
        mainPager.setOffscreenPageLimit(3);
        mainPager.setOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position)
            {
                if(position==allCards.size()+1)
                    DeckDetailActivity.this.finish();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mainPager.setOnTouchListener(new OnSwipeTouchListener(this)
        {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
            }
            public void onSwipeLeft()
            {
                if(allCards != null && allCards.size()>0)
                    AppUtil.openNativeWebView(DeckDetailActivity.this, getBundle(mainPager.getCurrentItem()));
//                    AppUtil.openNativeWebView(DeckDetailActivity.this, allCards.get(mainPager.getCurrentItem()).getUrl());
            }
            public void onSwipeBottom() {
            }

            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    AppUtil.LogMsg("TOUCH", "TOUCH");
                }
                return gestureDetector.onTouchEvent(event);
            }
        });

        getDeckData(String.valueOf(deckId));
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDeckData(final String deckId)
    {
        showProgressDialog(R.string.please_wait);
        RequestParams rp = new RequestParams();
        HttpClient.get(DeckDetailActivity.this, String.format(AppConstants.DECK_DATA_ENDPOINT, deckId), rp, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                hideProgressDialog();
                try
                {
                    String responseData = new String(responseBody, AppConstants.DEFAULT_ENCODING);
                    AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR"+statusCode+responseData);
                    if(statusCode == AppConstants.STATUS_CODE_SUCCESS)
                    {
                        DeckDetailResponse response = new Gson().fromJson(responseData, DeckDetailResponse.class);
                        allCards = response.getCards();
                        AppUtil.LogError("ALL DECKS", "ALL DECKS:- "+allCards.size());
                        DeckVerticalPagerAdapter adapter = new DeckVerticalPagerAdapter(DeckDetailActivity.this, DeckDetailActivity.this, response, deckContent);
                        mainPager.setAdapter(adapter);
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                hideProgressDialog();
                try
                {
                    if(error!= null)
                    {
                        AppUtil.showToast(DeckDetailActivity.this, error.getMessage()+error.getLocalizedMessage());
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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
    {
        int position = (Integer)compoundButton.getTag();
        switch (compoundButton.getId())
        {
            case R.id.buttonBookmark:
                if (isChecked)
                {
                    try
                    {
                        ArrayList<Bookmark> bookmark = new ArrayList<>();
                        Bookmark addBookmark = new Bookmark();
                        addBookmark.setDeckId(deckContent.getDeckId());
                        addBookmark.setCardId(allCards.get(position).getId());
                        bookmark.add(addBookmark);
                        updateBookmark(bookmark);
                        AppDatabaseHelper.getInstance(this).addBookMark(deckContent, null);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                else
                    AppDatabaseHelper.getInstance(this).deleteBookmark(deckContent.getDeckId());
                break;
        }
    }

    private void updateBookmark(ArrayList<Bookmark> bookmarks) throws UnsupportedEncodingException
    {
        showProgressDialog(R.string.please_wait);
        AppUtil.LogMsg("RESPONSE", "BOOKMARK JSON"+new Gson().toJson(bookmarks));
        StringEntity entity = new StringEntity(new Gson().toJson(bookmarks));
        HttpClient.postWithJson(DeckDetailActivity.this, AppConstants.ADD_BOOKMARK_ENDPOINT, entity,new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                hideProgressDialog();
                try
                {
                    String str = new String(responseBody, AppConstants.DEFAULT_ENCODING);
                    AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR"+statusCode+str);
                    if(statusCode == AppConstants.STATUS_CODE_SUCCESS)
                    {

                    }
                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                hideProgressDialog();
                try
                {
                    if(error!= null)
                    {
                        AppUtil.showToast(DeckDetailActivity.this, error.getMessage()+error.getLocalizedMessage());
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

    private Bundle getBundle(int position)
    {
        String data = new Gson().toJson(deckContent.getCards().get(position), Card.class);
        Bundle bundle = new Bundle();
        bundle.putString("data", ""+data);
        bundle.putInt("deckId", deckContent.getDeckId());
        return bundle;
    }

}
