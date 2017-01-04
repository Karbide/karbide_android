package com.karbide.bluoh.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.karbide.bluoh.dao.Content;
import com.karbide.bluoh.util.AppUtil;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class AppDatabaseHelper.
 */
public class AppDatabaseHelper extends SQLiteOpenHelper
{
	/** The Constant TAG. */
	private static final String TAG = AppDatabaseHelper.class.getSimpleName();

	/** The Constant DATABASE_NAME. */
	private static final String DATABASE_NAME = "BluohApp.db";

	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 1;

	/** The Constant TABLE_NAME_BOOKMARK. */
	private static final String TABLE_NAME_BOOKMARK = "BookmarkTable";

	/** The Constant COLUMN_DECK_ID. */
	private static final String COLUMN_DECK_ID = "_deckId";

	/** The Constant COLUMN_ARTICLE_JSON. */
	private static final String COLUMN_ARTICLE_JSON = "_articleJson";

	/** The Constant BOOKMARK_TABLE_CREATE. */
	private static final String BOOKMARK_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_BOOKMARK + " (" + COLUMN_DECK_ID + " INTEGER, " + COLUMN_ARTICLE_JSON + " VARCHAR);";

	/** The s instance. */
	private static AppDatabaseHelper sInstance = null;

	/**
	 * Instantiates a new app database helper.
	 * 
	 * @param context
	 *            the context
	 */
	private AppDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	/**
	 * On create.
	 * 
	 * @param db
	 *            the db
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(BOOKMARK_TABLE_CREATE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	/**
	 * On upgrade.
	 * 
	 * @param db
	 *            the db
	 * @param oldVersion
	 *            the old version
	 * @param newVersion
	 *            the new version
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	/**
	 * Open sq lite database.
	 * 
	 * @return the sQ lite database
	 */
	private SQLiteDatabase openSQLiteDatabase() {
		SQLiteDatabase db = null;
		try {
			db = getWritableDatabase();
		}
		catch (SQLException ex) {
			db = getReadableDatabase();
		}

		return db;
	}

	/**
	 * Gets the single instance of AppDatabaseHelper.
	 * 
	 * @param context
	 *            the context
	 * @return single instance of AppDatabaseHelper
	 */
	public synchronized static AppDatabaseHelper getInstance(Context context) {
		if (null == sInstance) {
			sInstance = new AppDatabaseHelper(context.getApplicationContext());
		}

		return sInstance;
	}
	/**
			* Adds the article.
	*
			* @param content
	*            the article summary
	* @param db
	*            the db
	* @return true, if successful
	*/
	public synchronized boolean addBookMark(Content content, SQLiteDatabase db) {
		boolean isAdded = false;
		boolean closeDBRequired = false;

		if (null == db) {
			db = openSQLiteDatabase();
			closeDBRequired = true;
		}

		if (null != db)
		{
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_DECK_ID, content.getDeckId());
			cv.put(COLUMN_ARTICLE_JSON, new Gson().toJson(content));

			StringBuilder sb = new StringBuilder(COLUMN_DECK_ID + "=?");
			String whereClause = sb.toString();
			String[] whereArgs = new String[] { Integer.toString(content.getDeckId()) };

			int rowsAffected = db.update(TABLE_NAME_BOOKMARK, cv, whereClause, whereArgs);
			if (rowsAffected > 0) {
				isAdded = true;
			}
			else {
				long rowId = db.insert(TABLE_NAME_BOOKMARK, null, cv);
				if (rowId != -1) {
					isAdded = true;
				}
			}

			if (closeDBRequired) {
				db.close();
			}
		}

		return isAdded;
	}


	public ArrayList<Content> getBookmarks()
	{
		ArrayList<Content> bookmarkList = new ArrayList<Content>();
		SQLiteDatabase db = openSQLiteDatabase();

		if (null != db)
		{
			String[] projection = new String[] { COLUMN_ARTICLE_JSON };
			Cursor cr = db.query(TABLE_NAME_BOOKMARK, projection, null, null, null, null, null);
			if (null != cr)
			{
				Gson gson = new Gson();
				if (cr.moveToFirst()) {
					do
					{
						String articleJson = cr.getString(0);
						bookmarkList.add(gson.fromJson(articleJson, Content.class));
						AppUtil.LogMsg(TAG, "Bookmark Article JSON-- article: " + articleJson);
					}
					while (cr.moveToNext());
				}
				cr.close();
			}
		}
		return bookmarkList;
	}

	public boolean isBookMarked(int deckId) {
		AppUtil.LogMsg(TAG, "getArticleForArticleId++ deck Id for bookmark check: " + deckId);
		SQLiteDatabase db = openSQLiteDatabase();

		if (null != db)
		{
			String[] projection = new String[] { COLUMN_ARTICLE_JSON };
			String selection = new String(COLUMN_DECK_ID + "=?");
			String[] selectionArgs = new String[] { Integer.toString(deckId) };

			Cursor cr = db.query(TABLE_NAME_BOOKMARK, projection, selection, selectionArgs, null, null, null);
			if (null != cr)
			{
				AppUtil.LogMsg(TAG, "Bookmark Article Cursor count: " + cr.getCount());
				if(cr.getCount()>0)
					return true;
				cr.close();
			}
		}
		return false;
	}


	public int deleteBookmark(int deckId) {
		AppUtil.LogMsg(TAG, "deleteBookmark++ deckId: " + deckId);

		int rowsEffected = 0;
		SQLiteDatabase db = openSQLiteDatabase();

		if (null != db) {
			String selection = new String(COLUMN_DECK_ID + "=?");
			String[] selectionArgs = new String[] { Integer.toString(deckId) };

			rowsEffected = db.delete(TABLE_NAME_BOOKMARK, selection, selectionArgs);
			//db.close();
		}

		AppUtil.LogMsg(TAG, "getBookmarkForDeckId-- deleteBookmarkForDeckId: " + rowsEffected);
		return rowsEffected;
	}

}
