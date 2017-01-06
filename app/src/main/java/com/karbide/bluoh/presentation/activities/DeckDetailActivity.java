package com.karbide.bluoh.presentation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;

import com.google.gson.Gson;
import com.karbide.bluoh.R;
import com.karbide.bluoh.dal.AppDatabaseHelper;
import com.karbide.bluoh.dao.core.Bookmark;
import com.karbide.bluoh.dao.core.Card;
import com.karbide.bluoh.dao.core.Deck;
import com.karbide.bluoh.presentation.components.DepthVerticalPageTransformer;
import com.karbide.bluoh.presentation.components.VerticalViewPager;
import com.karbide.bluoh.presentation.viewadapters.DeckVerticalPagerAdapter;
import com.karbide.bluoh.service.BookmarksReceiverIntf;
import com.karbide.bluoh.service.BookmarksResultReceiver;
import com.karbide.bluoh.service.HttpClient;
import com.karbide.bluoh.service.ManageBookmarksService;
import com.karbide.bluoh.util.AppConstants;
import com.karbide.bluoh.util.AppUtil;
import com.karbide.bluoh.util.OnSwipeTouchListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class DeckDetailActivity extends BaseActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, BookmarksReceiverIntf{
    private Toolbar toolbar;
    private VerticalViewPager mainPager;
    private List<Card> allCards;
    private int deckId = -1;
    private String title = null;
    private Deck deckDeck;
    private final String TAG = "DeckDetailsActivity";

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
            deckDeck = new Gson().fromJson(getIntent().getStringExtra("content"), Deck.class);
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
        HttpClient.getWithSyncHttpClient(DeckDetailActivity.this, String.format(AppConstants.DECK_DATA_ENDPOINT, deckId), rp, new AsyncHttpResponseHandler()
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
                        Deck response = new Gson().fromJson(responseData, Deck.class);
                        allCards = response.getCards();
                        AppUtil.LogError("ALL DECKS", "ALL DECKS:- "+allCards.size());
                        DeckVerticalPagerAdapter adapter = new DeckVerticalPagerAdapter(DeckDetailActivity.this, DeckDetailActivity.this, response, deckDeck);
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

                Bookmark bookmark = new Bookmark();
                bookmark.setDeckId(deckDeck.getDeckId());
                bookmark.setCardId(allCards.get(position).getId());

                if (isChecked) {
                        startManageBookmarkIntentService(AppConstants.BOOKMARK_UPDATE_OPERATION, bookmark);
                        AppDatabaseHelper.getInstance(this).addBookMark(deckDeck, null);
                }else {
                        startManageBookmarkIntentService(AppConstants.BOOKMARK_DELETE_OPERATION, bookmark);
                        AppDatabaseHelper.getInstance(this).deleteBookmark(deckDeck.getDeckId());
                }
                break;
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startManageBookmarkIntentService(String operationType, Bookmark bookmark) {
        Intent intent = new Intent(this, ManageBookmarksService.class);
        BookmarksResultReceiver mResultReceiver = new BookmarksResultReceiver(new Handler(Looper.getMainLooper()));
        mResultReceiver.setReceiver(this);
        intent.putExtra(AppConstants._resultReceiverBookmarks, mResultReceiver);
        intent.putExtra(AppConstants._operationType, operationType);
        intent.putExtra(AppConstants._bookmarkObj, bookmark);
        startService(intent);
    }

    /**
     * Interface method implemented to get article data feed
     * @param statusCode
     * @param resultData
     */
    @Override
    public void onReceiveBookmarkResult(int statusCode, Bundle resultData) {
        String responseData = resultData.getString(AppConstants._bookmarksResponseData);
        String operationType = resultData.getString(AppConstants._operationType);

        if(statusCode==AppConstants.STATUS_CODE_SUCCESS){
            AppUtil.LogMsg(TAG,operationType + " BOOKMARKS SUCESSFULL");
        }else if(statusCode == AppConstants.STATUS_CODE_FAILURE) {
            AppUtil.LogMsg(TAG, operationType + " BOOKMARKS FAILED");
        }else{
            AppUtil.LogMsg(TAG, operationType+ " BOOKMARKS FAILED WITH UNKONWN STATUS");
        }
    }

    private Bundle getBundle(int position)
    {
        String data = new Gson().toJson(deckDeck.getCards().get(position), Card.class);
        Bundle bundle = new Bundle();
        bundle.putString("data", ""+data);
        bundle.putInt("deckId", deckDeck.getDeckId());
        return bundle;
    }

}
