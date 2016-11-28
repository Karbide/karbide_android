package com.karbide.bluoh.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.karbide.bluoh.R;
import com.karbide.bluoh.datatypes.Content;
import com.karbide.bluoh.datatypes.HomeDataResponse;
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

import cz.msebera.android.httpclient.Header;


public class BookmarksFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
    private VerticalViewPager mainPager;
    private ArrayList<Content> allDecks;
    private TextView tvBlankTemplate;
    private HomePagerAdapter homePageAdapter;
    private HomeDataResponse homeDataResponse;
    boolean isFirstRequest = true;
    @Override
    public void onResume()
    {
        super.onResume();
//        ((MainActivity)getActivity()).setOtherTitle("BOOKMARKS");
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_bookmark,null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mainPager = (VerticalViewPager)view.findViewById(R.id.mainPager);
        tvBlankTemplate = (TextView)view.findViewById(R.id.tvBlankTemplate);
//        allDecks = AppDatabaseHelper.getInstance(getActivity()).getBookmarks();

        getBookMark("0");


    }

    @Override
    public void onClick(View view) {

    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }


    private void setBookmarkData()
    {
        if(allDecks!=null && allDecks.size()>0)
        {
            mainPager.setVisibility(View.VISIBLE);
            tvBlankTemplate.setVisibility(View.GONE);
            mainPager.setPageTransformer(false, new DepthVerticalPageTransformer());
            mainPager.setOffscreenPageLimit(3);
            mainPager.setOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
                @Override
                public void onPageSelected(int position) {
                    if(position%3 == 0 && homeDataResponse.getLast() == false)
                        getBookMark(String.valueOf(position/3));
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            mainPager.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
                public void onSwipeTop() {
                }

                public void onSwipeRight() {
                }

                public void onSwipeLeft()
                {
                    AppUtil.openNativeWebView(getActivity(), allDecks.get(mainPager.getCurrentItem()).getCards().get(0).getUrl());
//                    AppUtil.openUrlInWeb(getActivity(), allDecks.get(mainPager.getCurrentItem()).getCards().get(0).getUrl());
                }

                public void onSwipeBottom() {
                }

                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    }
                    return gestureDetector.onTouchEvent(event);
                }
            });
            homePageAdapter = new HomePagerAdapter(getActivity(), BookmarksFragment.this, allDecks, BookmarksFragment.this);
            mainPager.setAdapter(homePageAdapter);
        }
        else
        {
            mainPager.setVisibility(View.GONE);
            tvBlankTemplate.setVisibility(View.VISIBLE);
        }
    }
    private void getBookMark(String pageNo)
    {
        showProgressDialog(R.string.please_wait);
        RequestParams rp = new RequestParams();
        HttpUtils.get(getActivity(), String.format(AppConstants.GET_BOOKMARK, pageNo), rp, new AsyncHttpResponseHandler()
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
                            allDecks = homeDataResponse.getContent();
                            setBookmarkData();
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
}