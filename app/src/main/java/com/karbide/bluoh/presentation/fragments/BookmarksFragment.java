package com.karbide.bluoh.presentation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.karbide.bluoh.R;
import com.karbide.bluoh.dao.HomeDataResponse;
import com.karbide.bluoh.dao.core.Card;
import com.karbide.bluoh.dao.core.Deck;
import com.karbide.bluoh.presentation.components.DepthVerticalPageTransformer;
import com.karbide.bluoh.presentation.components.VerticalViewPager;
import com.karbide.bluoh.presentation.viewadapters.HomePagerAdapter;
import com.karbide.bluoh.service.BookmarksReceiverIntf;
import com.karbide.bluoh.service.BookmarksResultReceiver;
import com.karbide.bluoh.service.ManageBookmarksService;
import com.karbide.bluoh.util.AppConstants;
import com.karbide.bluoh.util.AppUtil;
import com.karbide.bluoh.util.OnSwipeTouchListener;
import java.util.ArrayList;

public class BookmarksFragment extends BaseFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, BookmarksReceiverIntf
{
    private VerticalViewPager mainPager;
    private ArrayList<Deck> allDecks;
    private TextView tvBlankTemplate;
    private HomePagerAdapter homePageAdapter;
    private boolean isLast = false;
    private final String Tag = "BookmarksFragment";

    @Override
    public void onResume()
    {
        super.onResume();
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
        if(getArguments() != null) {
            HomeDataResponse homeDataResponse = new Gson().fromJson(getArguments().getString("bookmark"), HomeDataResponse.class);
            if(homeDataResponse !=null && homeDataResponse.getDeck()!=null) {
                isLast = homeDataResponse.getLast();
                allDecks = homeDataResponse.getDeck();
                Log.e(Tag, allDecks.size()+"");
                setBookmarkData();
            }else{
                isLast = false;
                Toast.makeText(this.getContext(),"No Bookmarks",Toast.LENGTH_SHORT);
                Log.e(Tag, "No bookmarks");
            }
        }
        else
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
            mainPager.setOffscreenPageLimit(AppConstants.ITEMS_IN_STACK);
            mainPager.setOnPageChangeListener(new VerticalViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
                @Override
                public void onPageSelected(int position) {
                    if(position%AppConstants.ITEMS_IN_STACK == 0 &&
                            !isLast)
                        getBookMark(String.valueOf(position/AppConstants.ITEMS_IN_STACK));
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
                    AppUtil.openNativeWebView(getActivity(),getBundle(mainPager.getCurrentItem()));
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

    private void getBookMark(String pageNo){
        startBookmarksIntentService("GET",pageNo);
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startBookmarksIntentService(String operationType, String pageNo) {
        Intent intent = new Intent(getActivity(), ManageBookmarksService.class);
        BookmarksResultReceiver mResultReceiver = new BookmarksResultReceiver(new Handler(Looper.getMainLooper()));
        mResultReceiver.setReceiver(this);
        intent.putExtra(AppConstants._resultReceiverBookmarks, mResultReceiver);
        intent.putExtra(AppConstants._bookmarkPgNo, pageNo);
        intent.putExtra(AppConstants._operationType, operationType);
        getActivity().startService(intent);
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
            if(operationType.equalsIgnoreCase(AppConstants.BOOKMARK_GET_OPERATION)){
                HomeDataResponse homeDataResponse = new Gson().fromJson(responseData, HomeDataResponse.class);
                if(homeDataResponse !=null && homeDataResponse.getDeck()!=null) {
                    isLast = homeDataResponse.getLast();
                    if (allDecks == null || allDecks.size() == 0) {
                        allDecks = homeDataResponse.getDeck();
                        setBookmarkData();
                    } else {
                        allDecks.addAll(homeDataResponse.getDeck());
                        homePageAdapter.notifyDataSetChanged();
                    }
                }
            }else{
                AppUtil.LogMsg(Tag, AppConstants._operationType+ " BOOKMARKS SUCESSFULL");
            }
        }else if(statusCode == AppConstants.STATUS_CODE_FAILURE) {
            AppUtil.LogMsg(Tag, AppConstants._operationType+ " BOOKMARKS FAILED");
        }else{
            AppUtil.LogMsg(Tag, AppConstants._operationType+ " BOOKMARKS FAILED WITH UNKONWN STATUS");
        }
    }

    private Bundle getBundle(int position){
        String data = new Gson().toJson(allDecks.get(position).getCards().get(0), Card.class);
        Bundle bundle = new Bundle();
        bundle.putString("data", ""+data);
        bundle.putInt("deckId", allDecks.get(position).getDeckId());
        return bundle;
    }

}