package com.karbide.bluoh.fragments;

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
import com.karbide.bluoh.database.AppDatabaseHelper;
import com.karbide.bluoh.datadownloader.ArticleFeedResultReceiver;
import com.karbide.bluoh.datadownloader.ArticlesDataReceiverIntf;
import com.karbide.bluoh.datadownloader.FetchArticleService;
import com.karbide.bluoh.datatypes.AddBookmark;
import com.karbide.bluoh.datatypes.Card;
import com.karbide.bluoh.datatypes.Content;
import com.karbide.bluoh.datatypes.HomeDataResponse;
import com.karbide.bluoh.datatypes.TrafficData;
import com.karbide.bluoh.ui.DepthVerticalPageTransformer;
import com.karbide.bluoh.ui.VerticalViewPager;
import com.karbide.bluoh.util.AppConstants;
import com.karbide.bluoh.util.AppUtil;
import com.karbide.bluoh.util.HttpUtils;
import com.karbide.bluoh.util.OnSwipeTouchListener;
import com.karbide.bluoh.viewadapters.HomePagerAdapter;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class HomeFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        ArticlesDataReceiverIntf {

    public VerticalViewPager mainPager;
    boolean isFirstRequest = true;
    private long startTime;
    private static final String Tag = "Home Fragment";
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private HomePagerAdapter homePageAdapter;
    private HomeDataResponse homeDataResponse;
    private ArrayList<Content> allDecks;
    private String homedata = null;

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
        homedata = getArguments().getString("homedata");
        mainPager = (VerticalViewPager) view.findViewById(R.id.mainPager);
        mainPager.setPageTransformer(false, new DepthVerticalPageTransformer());
        mainPager.setOffscreenPageLimit(AppConstants.ITEMS_IN_STACK);
        mainPager.setOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                long seconds = (Calendar.getInstance().getTimeInMillis()-startTime)/1000;
//                AppUtil.LogError("PAGE SELECTED", "PAGE SELECTED"+position+ " SECONDS:- "+seconds);
//                startTime = Calendar.getInstance().getTimeInMillis();
//                updateTrafic(seconds, position);
                if (position % AppConstants.GET_DATA_POSITION== 0 && homeDataResponse.getLast() == false)
                    startArticleIntentService(String.valueOf(position / AppConstants.GET_DATA_POSITION));
//                    getHomeData(String.valueOf(position/3));
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

        if (homedata != null) {
            homeDataResponse = new Gson().fromJson(homedata, HomeDataResponse.class);
            startTime = Calendar.getInstance().getTimeInMillis();
            allDecks = homeDataResponse.getContent();
            homePageAdapter = new HomePagerAdapter(getActivity(), HomeFragment.this, allDecks, HomeFragment.this);
            mainPager.setAdapter(homePageAdapter);
            showNativeAd();
        } else {
            startArticleIntentService("0");
            // - getHomeData("0");
        }
    }

    private void addTouchListener() {
        mainPager.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeLeft() {
                AppUtil.showToast(getActivity(), "On swipe left");
//                AppUtil.openNativeWebView(getActivity(), getBundle());
            }

            public void onSwipeBottom() {
            }

            public boolean onTouch(View v, MotionEvent event) {
//                AppUtil.showToast(getActivity(), "On touchhhhhhhhhhhh");
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

    private void updateBookmark(ArrayList<AddBookmark> bookmarks) throws UnsupportedEncodingException {
        showProgressDialog(R.string.please_wait);
        AppUtil.LogMsg("RESPONSE", "BOOKMARK JSON" + new Gson().toJson(bookmarks));
        StringEntity entity = new StringEntity(new Gson().toJson(bookmarks));
        HttpUtils.postWithJson(getActivity(), AppConstants.ADD_BOOKMARK, entity, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                hideProgressDialog();

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
                hideProgressDialog();
                try {
                    if (error != null) {
                        AppUtil.showToast(getActivity(), error.getMessage() + error.getLocalizedMessage());
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

    private void deleteBookmark(String deckId, String cardId) {
        showProgressDialog(R.string.please_wait);
        RequestParams rp = new RequestParams();
        HttpUtils.delete(getActivity(), String.format(AppConstants.DELETE_BOOKMARK_ENDPOINT, deckId, cardId), rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                hideProgressDialog();
                try {
                    String str = new String(responseBody, AppConstants.DEFAULT_ENCODING);
                    AppUtil.LogMsg("RESPONSE", "RESPONSE  ERROR" + statusCode + str);
                    if (statusCode == AppConstants.STATUS_CODE_SUCCESS) {
                        AppUtil.showToast(getActivity(), "Bookmark deleted");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                hideProgressDialog();
                try {
                    if (error != null) {
                        AppUtil.showToast(getActivity(), error.getMessage() + error.getLocalizedMessage());
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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        int position = (Integer) compoundButton.getTag();
        switch (compoundButton.getId()) {
            case R.id.buttonBookmark:
                if (isChecked) {
                    try {
                        ArrayList<AddBookmark> bookmark = new ArrayList<>();
                        AddBookmark addBookmark = new AddBookmark();
                        addBookmark.setDeckId(allDecks.get(position).getDeckId());
                        addBookmark.setCardId(allDecks.get(position).getCards().get(0).getId());
                        bookmark.add(addBookmark);
                        updateBookmark(bookmark);
                        AppDatabaseHelper.getInstance(getActivity()).addBookMark(allDecks.get(position), null);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                } else {
                    deleteBookmark(String.valueOf(allDecks.get(position).getDeckId()), "0");
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
        AppUtil.LogMsg("RESPONSE", "TRAFIC JSON" + new Gson().toJson(traficData));
        StringEntity entity = new StringEntity(new Gson().toJson(traficData));
        HttpUtils.postWithJson(getActivity(), AppConstants.TRAFIC, entity, new AsyncHttpResponseHandler() {
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
        intent.putExtra("pageno", pageNo);
        getActivity().startService(intent);
    }


    /**
     * Interface method implemented to get article data feed
     * @param resultCode
     * @param resultData
     */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        String responseData = resultData.getString("result");
        startTime = Calendar.getInstance().getTimeInMillis();
        homeDataResponse = new Gson().fromJson(responseData, HomeDataResponse.class);
        allDecks.addAll(homeDataResponse.getContent());

        if (homePageAdapter == null){
            homePageAdapter = new HomePagerAdapter(getActivity(), HomeFragment.this, allDecks, HomeFragment.this);
            mainPager.setAdapter(homePageAdapter);
            showNativeAd();
        }else {
            homePageAdapter.notifyDataSetChanged();
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