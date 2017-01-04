package com.karbide.bluoh.presentation.activities;

import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import io.fabric.sdk.android.Fabric;

// TODO: Auto-generated Javadoc

/**
 * The Class BluohApplication.
 */
public class BluohApplication extends Application {
    public static Context context;
    /** The instance. */
    private static BluohApplication _instance = null;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate()
    {
        _instance = this;
        context = getApplicationContext();
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        initImageLoader(context);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        AppEventsLogger.activateApp(this);
    }


    public static void initImageLoader(Context context) {
        BitmapFactory.Options resizeOptions = new BitmapFactory.Options();
        resizeOptions.inSampleSize = 3; // decrease size 3 times
        resizeOptions.inScaled = true;
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .decodingOptions(resizeOptions)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.defaultDisplayImageOptions(options);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        ImageLoader.getInstance().init(config.build());
    }


    /**
     * Gets the app instance.
     *
     * @return the app instance
     */
    public static BluohApplication getAppInstance() {
        return _instance;
    }
}
