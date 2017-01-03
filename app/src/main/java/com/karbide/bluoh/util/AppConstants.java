package com.karbide.bluoh.util;
import android.os.Environment;
/*
 * The Class AppConstants.
 * 
 */

public class AppConstants 
{
	/** The Constant LOG_FILENAME. */
	public static final String LOG_FILENAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bluohlogfile.txt";

	/** The _is logging enabled. */
	public static boolean _isLoggingEnabled = true;

	/** The _is file logging enabled. */
	public static boolean _isFileLoggingEnabled = false;

	/** The _is exception logging enabled. */
	public static boolean _isFileExceptionLoggingEnabled = false;

	/** The Constant AKD_PROFILE_PLAYSTORE_URL. */
	public static String BLUOH_PROFILE_PLAYSTORE_URL = "market://details?id=com.karbide.bluoh";
	/** The Constant AKD_PROFILE_PLAYSTORE_WEB_URL. */
	public static final String BLUOH_PROFILE_PLAYSTORE_WEB_URL = "https://play.google.com/store/apps/details?id=com.karbide.bluoh";

	public static String HOME_DATA_ENDPOINT = "deck?page=%s";
	public static String DECK_DATA_ENDPOINT = "deck/%s";
	public static String ADD_BOOKMARK = "bookmarks/";
	public static String GET_BOOKMARK = "bookmarks?page=%s";
	public static String LOGIN = "login/";
	public static String TRAFIC ="traffic/";
	public static String FEEDBACK ="feedback/";
	public static String DELETE_BOOKMARK_ENDPOINT = "bookmarks/%s/%s";
	public static String FEED_DATA_ENDPOINT = "feed/";

	public static final int GRID_SHOW_POSITION = 7;
	public static final int ADV_SHOW_POSITION = 10;
	public static final int ITEMS_IN_STACK = 10;
	public static final int GET_DATA_POSITION=5;

}
