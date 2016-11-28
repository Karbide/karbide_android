package com.karbide.bluoh;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karbide.bluoh.fragments.BookmarksFragment;
import com.karbide.bluoh.fragments.HomeFragment;
import com.karbide.bluoh.fragments.InviteFragment;
import com.karbide.bluoh.fragments.ShareFragment;
import com.karbide.bluoh.util.AppSharedPreference;
import com.karbide.bluoh.util.AppUtil;


public class MainActivity extends BaseActivity{
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private FragmentManager fragmentManager;
    private boolean mVisible;
    private View mContentView;
    private final Handler mHideHandler = new Handler();
    private AppBarLayout appBarLayout;
    private boolean isHome;
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
        subscribeToPushService();
        displayView(0);
    }

    public void initNavigationDrawer()
    {
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        AppUtil.applyCustomFontToNavigationMenu(MainActivity.this, navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id){
                    case R.id.icHome:
                        displayView(0);
                        break;
                    case R.id.icWishList:
                        displayView(1);
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
                    case R.id.icSetting:
                        displayView(5);
                        break;

                }
                return true;
            }
        });
       /* View header = navigationView.getHeaderView(0);
        TextView tv_email = (TextView)header.findViewById(R.id.tv_email);
        tv_email.setText("Basant Kumar");*/
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

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
                fragment = new HomeFragment();
                break;
            case 1:
                isHome =false;
                title = "BOOKMARKS";
                fragment = new BookmarksFragment();
                break;
            case 2:
                isHome =false;
                title = "INVITE";
                fragment = new ShareFragment();
//                AppUtil.shareData(MainActivity.this, "Hey , I found this amazing app, lets join Bluoh and have amazing experience !");
                break;
            case 3:
                isHome =false;
                title = "FEEDBACK";
                fragment = new InviteFragment();
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
//            fragmentTransaction.addToBackStack(title);
            fragmentTransaction.commit();
            if(isHome)
            {
                getSupportActionBar().setLogo(R.drawable.logo_header);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            else
            {
                getSupportActionBar().setDisplayUseLogoEnabled(false);
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                setTitle(title);
            }

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
                displayView(1);
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
}
