package com.karbide.bluoh.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.karbide.bluoh.dao.LoginResponse;

public class AppSharedPreference {

	private static SharedPreferences sSharedPreferences;
	private static Editor editor;
	private static Context _context;
	private static final String PREF_NAME = "MyPreference";
	private static AppSharedPreference uniqInstance;

	@SuppressLint("CommitPrefEdits")
	public static synchronized AppSharedPreference getInstance(Context context) {

		if (uniqInstance == null) {
			_context = context;

			sSharedPreferences = _context.getSharedPreferences(PREF_NAME,
					Context.MODE_PRIVATE);
			editor = sSharedPreferences.edit();
			uniqInstance = new AppSharedPreference();
		}
		return uniqInstance;
	}


	public String getGcmId()
	{
		return sSharedPreferences.getString("gcmid", "");
	}

	public void saveGcmId(String name) {
		editor.putString("gcmid", name);
		editor.commit();
	}

	public LoginResponse getUserInfo()
	{
		String data = sSharedPreferences.getString("userDetail", "");
		if(data != null)
			return new Gson().fromJson(data, LoginResponse.class);
		else
			return null;
	}

	public void saveUserDetail(String userDetail) {
		editor.putString("userDetail", userDetail);
		editor.commit();
	}
}
