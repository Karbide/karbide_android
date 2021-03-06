package com.karbide.bluoh.presentation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;
import com.google.gson.Gson;
import com.karbide.bluoh.R;
import com.karbide.bluoh.dal.AppDatabaseHelper;
import com.karbide.bluoh.dao.HomeDataResponse;
import com.karbide.bluoh.dao.core.Bookmark;
import com.karbide.bluoh.dao.core.Card;
import com.karbide.bluoh.dao.core.Deck;
import com.karbide.bluoh.dao.core.TrafficData;
import com.karbide.bluoh.presentation.components.DepthVerticalPageTransformer;
import com.karbide.bluoh.presentation.components.VerticalViewPager;
import com.karbide.bluoh.presentation.viewadapters.HomePagerAdapter;
import com.karbide.bluoh.service.ArticleFeedResultReceiver;
import com.karbide.bluoh.service.BookmarksReceiverIntf;
import com.karbide.bluoh.service.BookmarksResultReceiver;
import com.karbide.bluoh.service.DataReceiverIntf;
import com.karbide.bluoh.service.FetchArticleService;
import com.karbide.bluoh.service.HttpClient;
import com.karbide.bluoh.service.ManageBookmarksService;
import com.karbide.bluoh.util.AppConstants;
import com.karbide.bluoh.util.AppUtil;
import com.karbide.bluoh.util.OnSwipeTouchListener;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class HomeFragment extends BaseFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        BookmarksReceiverIntf,
        DataReceiverIntf {

    public VerticalViewPager mainPager;
    private long startTime;
    private static final String Tag = "Home Fragment";
    private HomePagerAdapter homePageAdapter;
    private ArrayList<Deck> allDecks;
    private boolean isLast = false;
    boolean isFirstRequest = true;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private int pageNo = -1;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String homedata = getArguments().getString("homedata");
        mainPager = (VerticalViewPager) view.findViewById(R.id.mainPager);
        mainPager.setPageTransformer(false, new DepthVerticalPageTransformer());
        mainPager.setOffscreenPageLimit(AppConstants.ITEMS_IN_STACK);
        mainPager.setOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                AppUtil.LogError(Tag,"Position is "+position);
                if (position % AppConstants.GET_DATA_POSITION== 0 && !isLast)
                {
                    if(position / AppConstants.GET_DATA_POSITION>pageNo)
                    {
                        pageNo = position / AppConstants.GET_DATA_POSITION;
                        AppUtil.LogError(Tag,"GET DATA Position is "+position+" pageNo"+pageNo);
                        startArticleIntentService(String.valueOf(pageNo));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                AppUtil.LogError("STATE", "IS   " + state);
                if (state == VerticalViewPager.SCROLL_STATE_IDLE)
                    addTouchListener();
                else
                    removeTouchListener();
            }
        });

        if (homedata != null)
        {
            HomeDataResponse homeDataResponse = new Gson().fromJson(homedata, HomeDataResponse.class);
            startTime = Calendar.getInstance().getTimeInMillis();
            allDecks = homeDataResponse.getDeck();
            if(allDecks==null){
                allDecks = new ArrayList<Deck>();
            }
            isLast = homeDataResponse.getLast();
            homePageAdapter = new HomePagerAdapter(getActivity(), HomeFragment.this, allDecks, HomeFragment.this);
            mainPager.setAdapter(homePageAdapter);
            showNativeAd();
        } else {
            startArticleIntentService("0");
        }
    }

    private void addTouchListener() {
        mainPager.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeLeft() {
                AppUtil.showToast(getActivity(), "On swipe left");
            }

            public void onSwipeBottom() {
            }

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                }
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void removeTouchListener() {
        mainPager.setOnTouchListener(null);
    }

    private Bundle getBundle() {
        String data = new Gson().toJson(allDecks.get(mainPager.getCurrentItem()).getCards().get(0), Card.class);
        Bundle bundle = new Bundle();
        bundle.putString("data", "" + data);
        bundle.putInt("deckId", allDecks.get(mainPager.getCurrentItem()).getDeckId());
        return bundle;
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        int position = (Integer) compoundButton.getTag();
        switch (compoundButton.getId()) {
            case R.id.buttonBookmark:

                Bookmark bookmark = new Bookmark();
                String operationType = null;
                bookmark.setDeckId(allDecks.get(position).getDeckId());
                bookmark.setCardId(allDecks.get(position).getCards().get(0).getId());

                if (isChecked) {
                        operationType = AppConstants.BOOKMARK_UPDATE_OPERATION;
                        startManageBookmarkIntentService(operationType, bookmark);
                        AppDatabaseHelper.getInstance(getActivity()).addBookMark(allDecks.get(position), null);
                } else {
                        operationType = AppConstants.BOOKMARK_DELETE_OPERATION;
                        startManageBookmarkIntentService(operationType, bookmark);
                        AppDatabaseHelper.getInstance(getActivity()).deleteBookmark(allDecks.get(position).getDeckId());
                }



                break;

            case R.id.buttonLike:
                TrafficData trafficData = new TrafficData();
                trafficData.setType("" + allDecks.get(position).getDeckId());
                trafficData.setInfo("" + allDecks.get(position).getDeckId());
                if (isChecked) {
                    try {
                        trafficData.setActivity("likes");
                        updateTraficOnServer(trafficData);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        trafficData.setActivity("dislikes");
                        updateTraficOnServer(trafficData);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void updateTrafic(long seconds, int position) {
        TrafficData trafficData = new TrafficData();
        trafficData.setType("" + allDecks.get(position).getDeckId());
        trafficData.setInfo("" + allDecks.get(position).getDeckId());
        if (seconds > 10)
            trafficData.setActivity("Seen");
        else
            trafficData.setActivity("Flip");
        try {
            updateTraficOnServer(trafficData);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void updateTraficOnServer(TrafficData traficData) throws UnsupportedEncodingException {
        AppUtil.LogMsg("RESPONSE", "TRAFFIC_ENDPOINT JSON" + new Gson().toJson(traficData));
        StringEntity entity = new StringEntity(new Gson().toJson(traficData));
        HttpClient.postWithJsonSync(getActivity(), AppConstants.TRAFFIC_ENDPOINT, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String str = new String(responseBody, AppConstants.DEFAULT_ENCODING);
                    AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR" + statusCode + str);
                    if (statusCode == AppConstants.STATUS_CODE_SUCCESS) {
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                try {
                    if (error != null) {
                        AppUtil.showToast(getActivity(), "STATUS CODE" + statusCode + error.getMessage() + error.getLocalizedMessage());
                    } else {
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

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startArticleIntentService(String pageNo) {
        Intent intent = new Intent(getActivity(), FetchArticleService.class);
        ArticleFeedResultReceiver mResultReceiver = new ArticleFeedResultReceiver(new Handler(Looper.getMainLooper()));
        mResultReceiver.setReceiver(this);
        intent.putExtra("resultReceiver", mResultReceiver);
        intent.putExtra("pageNo", pageNo);
        getActivity().startService(intent);
    }


    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startManageBookmarkIntentService(String operationType, Bookmark bookmark) {
        Intent intent = new Intent(getActivity(), ManageBookmarksService.class);
        BookmarksResultReceiver mResultReceiver = new BookmarksResultReceiver(new Handler(Looper.getMainLooper()));
        mResultReceiver.setReceiver(this);
        intent.putExtra(AppConstants._resultReceiverBookmarks, mResultReceiver);
        intent.putExtra(AppConstants._operationType, operationType);
        intent.putExtra(AppConstants._bookmarkObj, bookmark);
        getActivity().startService(intent);
    }

    /**
     * Interface method implemented to get article data feed
     * @param resultCode
     * @param resultData
     */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        String responseData = resultData.getString(AppConstants._articleResponseData);
        startTime = Calendar.getInstance().getTimeInMillis();
        HomeDataResponse homeDataResponse = new Gson().fromJson(responseData, HomeDataResponse.class);
        isLast = homeDataResponse.getLast();
        if(allDecks==null){
            allDecks = new ArrayList<Deck>();
        }
        allDecks.addAll(homeDataResponse.getDeck());

        if (homePageAdapter == null){
            homePageAdapter = new HomePagerAdapter(getActivity(), HomeFragment.this, allDecks, HomeFragment.this);
            mainPager.setAdapter(homePageAdapter);
            showNativeAd();
        }else {
            homePageAdapter.notifyDataSetChanged();
        }

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
                AppUtil.LogMsg(Tag, operationType+ " BOOKMARKS SUCESSFULL");
        }else if(statusCode == AppConstants.STATUS_CODE_FAILURE) {
            AppUtil.LogMsg(Tag, operationType+ " BOOKMARKS FAILED");
        }else{
            AppUtil.LogMsg(Tag, operationType+ " BOOKMARKS FAILED WITH UNKONWN STATUS");
        }
    }

    private void showNativeAd() {
        final NativeAd nativeAd = new NativeAd(getActivity(), "800960263379262_814380178703937");
        nativeAd.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError error) {
                // Ad error callback
                Log.e(Tag, "Ad Loaded Error");
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                //AppUtil.showToast(getActivity(), "Ad Loaded Ad Loaded");
                homePageAdapter.nativeAd = nativeAd;
                homePageAdapter.loadedAd = ad;
                homePageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }
        });
        // Request an ad
        nativeAd.loadAd();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ic_gotoTop:
                if (mainPager.getCurrentItem() != 0)
                    mainPager.setCurrentItem(0, true);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}