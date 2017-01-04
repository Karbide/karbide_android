package com.karbide.bluoh;

import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.karbide.bluoh.dao.HomeDataResponse;
import com.karbide.bluoh.fragments.BookmarksFragment;
import com.karbide.bluoh.fragments.HomeFragment;
import com.karbide.bluoh.fragments.FeedbackFragment;
import com.karbide.bluoh.fragments.ShareFragment;
import com.karbide.bluoh.util.AppConstants;
import com.karbide.bluoh.util.AppSharedPreference;
import com.karbide.bluoh.util.AppUtil;
import com.karbide.bluoh.service.HttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemLongClickListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends BaseActivity implements OnMenuItemClickListener, OnMenuItemLongClickListener {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private boolean mVisible;
    private View mContentView;
    private final Handler mHideHandler = new Handler();
    private AppBarLayout appBarLayout;
    private boolean isHome;
    private String homeData;
    private String bookmarkData;
    private ContextMenuDialogFragment mMenuDialogFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVisible = true;
        appBarLayout = (AppBarLayout)findViewById(R.id.appBarLayout);
        fragmentManager = getSupportFragmentManager();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initNavigationDrawer();
        if(getIntent().getExtras() != null)
            homeData = getIntent().getExtras().getString("homedata", null);
//        subscribeToPushService();
        displayView(0);
        initMenuFragment();

    }

    public void initNavigationDrawer()
    {
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        AppUtil.applyCustomFontToNavigationMenu(MainActivity.this, navigationView);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.icHome:
                        displayView(0);
                        break;
                    case R.id.icWishList:
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        getBookMark("0");
                        break;
                    case R.id.icInvite:
                        displayView(2);
                        break;
                    case R.id.icFeedBack:
                        displayView(3);
                        break;
                    case R.id.icRateUs:
                        displayView(4);
                        break;
                }
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    public void displayView(int position)
    {
        Fragment fragment = null;
        String title =  "BLUOH";
        switch (position)
        {
            case 0:
                isHome =true;
                Bundle bundle = new Bundle();
                bundle.putString("homedata", homeData);
                fragment = new HomeFragment();
                fragment.setArguments(bundle);
                break;
            case 1:
                isHome =false;
                title = "BOOKMARKS";
                Bundle bundlebm = new Bundle();
                bundlebm.putString("bookmark", bookmarkData);
                AppUtil.showToast(MainActivity.this, "BOOKMARK"+bookmarkData);
                fragment = new BookmarksFragment();
                fragment.setArguments(bundlebm);
                getBookMark("0");
                break;
            case 2:
                isHome =false;
                title = "INVITE";
                fragment = new ShareFragment();
                break;
            case 3:
                isHome =false;
                title = "FEEDBACK_ENDPOINT";
                fragment = new FeedbackFragment();
                break;
            case 4:
                AppUtil.launchPlayStorePage(MainActivity.this);
                break;
            case 5:
                break;
        }
        drawerLayout.closeDrawers();
        if (fragment != null)
        {

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerView, fragment, title);
            fragmentTransaction.commit();
        }
    }

    private void hideViews(AppBarLayout mToolbar) {
        mVisible = false;
        mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    private void showViews(AppBarLayout mToolbar) {
        mVisible = true;
        mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        mHideHandler.postDelayed(mHideRunnable, 1500);
        // Schedule a runnable to display UI elements after a delay
    }

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            toggle();
        }
    };

    public void toggle() {
        if (mVisible) {
            hideViews(appBarLayout);
        } else {
            showViews(appBarLayout);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miProfile:
//                displayView(1);
                getBookMark("0");
                break;
            case R.id.icOptionMenu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void subscribeToPushService()
    {
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        Log.d("AndroidBash", "Subscribed");
        String token = FirebaseInstanceId.getInstance().getToken();
        AppSharedPreference.getInstance(this).saveGcmId(token);
    }

    @Override
    public void onBackPressed()
    {
        if(drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawers();
            return;
        }
        else
        {
            if(!isHome) {
                displayView(0);
                return;
            }
        }
        super.onBackPressed();
    }


    private void getBookMark(String pageNo)
    {
        showProgressDialog(R.string.please_wait);
        RequestParams rp = new RequestParams();
        HttpClient.get(this, String.format(AppConstants.GET_BOOKMARK_ENDPOINT, pageNo), rp, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                hideProgressDialog();
                try
                {
                    String str = new String(responseBody, AppConstants.DEFAULT_ENCODING);
                    AppUtil.LogMsg("RESPONSE", "RESPONSE  BOOMARK MAIN"+statusCode+str);
                    if(statusCode == AppConstants.STATUS_CODE_SUCCESS)
                    {
                        HomeDataResponse homeData = new Gson().fromJson(str, HomeDataResponse.class);
                        if(homeData.getDeck() != null && homeData.getDeck().size()>0)
                        {
                            bookmarkData = str;
                            displayView(1);
                        }
                        else
                            AppUtil.showToast(MainActivity.this, "No bookmark found");
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
                        AppUtil.showToast(MainActivity.this, error.getMessage()+error.getLocalizedMessage());
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

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(true);
        menuParams.setAnimationDelay(0);
        menuParams.setAnimationDuration(50);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    private List<MenuObject> getMenuObjects() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

       /* MenuObject close = new MenuObject();
        BitmapDrawable bdClose = new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.icn_close));
        bdClose.setColorFilter(new PorterDuffColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY));
        close.setDrawable(bdClose);
        close.setDividerColor(R.color.colorPrimary);*/

        MenuObject send = new MenuObject("Daily Astrology");
        BitmapDrawable bSend = new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.icn_1));
        bSend.setColorFilter(new PorterDuffColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY));
        send.setDrawable(bSend);
        send.setDividerColor(R.color.colorPrimary);

        MenuObject like = new MenuObject("Jokes");
        BitmapDrawable b = new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.icn_2));
        b.setColorFilter(new PorterDuffColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY));
        like.setDrawable(b);
        like.setDividerColor(R.color.colorPrimary);


        MenuObject addFr = new MenuObject("Night Mode");
        BitmapDrawable bd = new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.icn_3));
        bd.setColorFilter(new PorterDuffColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY));
        addFr.setDrawable(bd);
        addFr.setDividerColor(R.color.colorPrimary);

        MenuObject addFav = new MenuObject("Text Mode");
        BitmapDrawable bdFav = new BitmapDrawable(getResources(),BitmapFactory.decodeResource(getResources(), R.drawable.icn_4));
        bdFav.setColorFilter(new PorterDuffColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY));
        addFav.setDrawable(bdFav);
        addFav.setDividerColor(R.color.colorPrimary);


//        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        return menuObjects;
    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {

    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {

    }
}
