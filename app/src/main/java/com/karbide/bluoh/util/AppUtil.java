package com.karbide.bluoh.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import com.karbide.bluoh.WebViewActivity;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by PC-2 on 9/18/2016.
 */
public class AppUtil
{


    /**
     * Log msg.
     *
     * @param tag
     *            the tag
     * @param msg
     *            the msg
     */
    public static void LogMsg(String tag, String msg) {
        Log.e(tag, msg);
        if (AppConstants._isLoggingEnabled == true) {
            if (AppConstants._isFileLoggingEnabled) {
                Log.e(tag, msg);
                writeToFile(AppConstants.LOG_FILENAME, tag + ":" + msg + "\n");
            }
        }
    }


    /**
     * Write to file.
     *
     * @param fileName
     *            the file name
     * @param content
     *            the content
     */
    public static void writeToFile(String fileName, String content) {
        try {
            FileWriter fw = new FileWriter(fileName, true);
            fw.write(content);

            fw.flush();
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            AppUtil.LogException(ex);
        }
    }

    /**
     * Log error.
     *
     * @param tag
     *            the tag
     * @param msg
     *            the msg
     */
    public static void LogError(String tag, String msg) {
        if (AppConstants._isLoggingEnabled == true) {
            Log.e(tag, msg);
            if (AppConstants._isFileLoggingEnabled) {
                Log.e(tag, msg);
                writeToFile(AppConstants.LOG_FILENAME, tag + ":" + msg + "\n");
            }
        }
    }


    /**
     * Log exception.
     *
     * @param ex the ex
     */
    public static void LogException(Throwable ex) {
        if (true == AppConstants._isFileExceptionLoggingEnabled == true) {
            final Writer writer = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(writer);

            ex.printStackTrace(printWriter);
            String stackTrace = writer.toString();
            printWriter.close();

            AppUtil.writeToFile(AppConstants.LOG_FILENAME, stackTrace);
        }
    }

    /**
     * Load json from asset.
     *
     * @param context
     *            the context
     * @param jsonFileName
     *            the json file name
     * @return the string
     */
    public static String loadJSONFromAsset(Context context, String jsonFileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(jsonFileName);
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, AppConstants.DEFAULT_ENCODING);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            AppUtil.LogException(ex);
            return null;
        }
        return json;
    }


    public static void applyFontToMenuItem(Context ctx, MenuItem mi) {
        Typeface font = Typeface.createFromAsset(ctx.getAssets(), "Roboto-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }


    public static void applyCustomFontToNavigationMenu(Context ctx, NavigationView navView)
    {
        Menu m = navView.getMenu();
        for (int i=0;i<m.size();i++)
        {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    AppUtil.applyFontToMenuItem(ctx, subMenuItem);
                }
            }
            //the method we have create in activity
            AppUtil.applyFontToMenuItem(ctx, mi);
        }
    }

    public static String getDateFormat(String date)
    {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM", Locale.ENGLISH);
        try {
            Date date1=inputFormat.parse(date);
            return simpleDateFormat.format(date1).toString().toUpperCase();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }


    /**
     * Gets the application version.
     *
     * @param context
     *            the context
     * @return the application version
     */
    public static int getApplicationVersion(Context context) {
        int appVersion = 0;

        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion = pInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException ex) {
            ex.printStackTrace();
            AppUtil.LogException(ex);
        }
        return appVersion;
    }

    /**
     * Launch play store bluoh page.
     *
     * @param context the context
     */
    public static void launchPlayStorePage(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.BLUOH_PROFILE_PLAYSTORE_URL));
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException ex) {
            openUrlInWeb(context, AppConstants.BLUOH_PROFILE_PLAYSTORE_WEB_URL);
        }
    }


    /**
     * Open url in web.
     *
     * @param context the context
     * @param url the url
     */
    public static void openUrlInWeb(Context context, String url) {
        if (url.substring(0, 4).equalsIgnoreCase("http") == false) {
            url = "http:\\" + url;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    public static void openNativeWebView(Context context, Bundle url)
    {
       /* if (url.substring(0, 4).equalsIgnoreCase("http") == false) {
            url = "http:\\" + url;
        }*/

        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtras(url);
        context.startActivity(intent);
    }



    public static void shareData(Context ctx, String data)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, ""+data);
        sendIntent.setType("text/plain");
        ctx.startActivity(sendIntent);
    }


    public static void showToast(Context ctx, String data)
    {
        Toast.makeText(ctx, data, Toast.LENGTH_LONG).show();
    }
}
