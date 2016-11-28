package com.karbide.bluoh.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.google.gson.Gson;
import com.karbide.bluoh.R;
import com.karbide.bluoh.database.AppDatabaseHelper;
import com.karbide.bluoh.datatypes.AddBookmark;
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


public class HomeFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private VerticalViewPager mainPager;
    private ArrayList<Content> allDecks;
    private HomePagerAdapter homePageAdapter;
    private HomeDataResponse homeDataResponse;
    boolean isFirstRequest = true;
    private long startTime;

    @Override
    public void onResume()
    {
        super.onResume();
//        ((MainActivity)getActivity()).setHomeTitle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainPager = (VerticalViewPager)view.findViewById(R.id.mainPager);
        mainPager.setPageTransformer(false, new DepthVerticalPageTransformer());
        mainPager.setOffscreenPageLimit(3);
        mainPager.setOnPageChangeListener(new VerticalViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                long seconds = (Calendar.getInstance().getTimeInMillis()-startTime)/1000;
                AppUtil.LogError("PAGE SELECTED", "PAGE SELECTED"+position+ " SECONDS:- "+seconds);
                startTime = Calendar.getInstance().getTimeInMillis();
                updateTrafic(seconds, position);
                if(position%3 == 0 && homeDataResponse.getLast() == false)
                    getHomeData(String.valueOf(position/3));
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });

        mainPager.setOnTouchListener(new OnSwipeTouchListener(getActivity())
        {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
            }
            public void onSwipeLeft()
            {
                AppUtil.openNativeWebView(getActivity(), allDecks.get(mainPager.getCurrentItem()).getCards().get(0).getUrl());
//                AppUtil.openUrlInWeb(getActivity(),allDecks.get(mainPager.getCurrentItem()).getCards().get(0).getUrl());
            }
            public void onSwipeBottom() {
            }

            public boolean onTouch(View v, MotionEvent event)
            {
               if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                }
                return gestureDetector.onTouchEvent(event);
            }
        });
        getHomeData("0");
    }

    @Override
    public void onClick(View view) {

    }

    private void getHomeData(String pageNo)
    {
        if (isFirstRequest)
            showProgressDialog(R.string.please_wait);
        RequestParams rp = new RequestParams();
        HttpUtils.get(getActivity(), String.format(AppConstants.HOME_DATA_ENDPOINT, pageNo), rp, new AsyncHttpResponseHandler()
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
                        homeDataResponse = new Gson().fromJson(str, HomeDataResponse.class);
                        if(isFirstRequest)
                        {
                            startTime = Calendar.getInstance().getTimeInMillis();
                            allDecks = homeDataResponse.getContent();
                            homePageAdapter = new HomePagerAdapter(getActivity(), HomeFragment.this, allDecks, HomeFragment.this);
                            mainPager.setAdapter(homePageAdapter);
                        }
                        else
                        {
                            allDecks.addAll(homeDataResponse.getContent());
                            homePageAdapter.notifyDataSetChanged();
                        }
                        isFirstRequest = false;
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
                        AppUtil.showToast(getActivity(), error.getMessage()+error.getLocalizedMessage());
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

    private void updateBookmark(ArrayList<AddBookmark> bookmarks) throws UnsupportedEncodingException
    {
        showProgressDialog(R.string.please_wait);
        AppUtil.LogMsg("RESPONSE", "BOOKMARK JSON"+new Gson().toJson(bookmarks));
        StringEntity entity = new StringEntity(new Gson().toJson(bookmarks));
        HttpUtils.postWithJson(getActivity(), AppConstants.ADD_BOOKMARK, entity,new AsyncHttpResponseHandler()
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
                        AppUtil.showToast(getActivity(), error.getMessage()+error.getLocalizedMessage());
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

    private void deleteBookmark(String deckId, String cardId)
    {
        showProgressDialog(R.string.please_wait);
        RequestParams rp = new RequestParams();
        HttpUtils.delete(getActivity(), String.format(AppConstants.DELETE_BOOKMARK_ENDPOINT, deckId,cardId), rp, new AsyncHttpResponseHandler()
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
                        AppUtil.showToast(getActivity(), "Bookmark deleted");
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
                        AppUtil.showToast(getActivity(), error.getMessage()+error.getLocalizedMessage());
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
                }
                else
                {
                    deleteBookmark(String.valueOf(allDecks.get(position).getDeckId()), "0");
                    AppDatabaseHelper.getInstance(getActivity()).deleteBookmark(allDecks.get(position).getDeckId());
                }
                break;

            case R.id.buttonLike:
                TrafficData trafficData = new TrafficData();
                trafficData.setType(""+allDecks.get(position).getDeckId());
                trafficData.setInfo(""+allDecks.get(position).getDeckId());
                if (isChecked)
                {
                    try
                    {
                        trafficData.setActivity("likes");
                        updateTraficOnServer(trafficData);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try
                    {
                        trafficData.setActivity("dislikes");
                        updateTraficOnServer(trafficData);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void updateTrafic(long seconds, int position)
    {
        TrafficData trafficData = new TrafficData();
        trafficData.setType(""+allDecks.get(position).getDeckId());
        trafficData.setInfo(""+allDecks.get(position).getDeckId());
        if(seconds>10)
            trafficData.setActivity("Seen");
        else
            trafficData.setActivity("Flip");
        try {
            updateTraficOnServer(trafficData);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    private void updateTraficOnServer(TrafficData traficData) throws UnsupportedEncodingException
    {
        AppUtil.LogMsg("RESPONSE", "TRAFIC JSON"+new Gson().toJson(traficData));
        StringEntity entity = new StringEntity(new Gson().toJson(traficData));
        HttpUtils.postWithJson(getActivity(), AppConstants.TRAFIC, entity,new AsyncHttpResponseHandler()
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

                    }
                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                try
                {
                    if(error!= null)
                    {
                        AppUtil.showToast(getActivity(), "STATUS CODE"+statusCode+error.getMessage()+error.getLocalizedMessage());
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
}