package com.yvelabs.zing.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import com.yvelabs.zing.CommonConstants;
import com.yvelabs.zing.R;

public class PreferenceUtils {

	public static String getString(Context context, int key,
			String defaultString) {
		return PreferenceManager
				.getDefaultSharedPreferences(context)
				.getString(context.getResources().getString(key), defaultString);
	}

	public static boolean getBoolean(Context context, int key,
			boolean defaultBoolean) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getBoolean(context.getResources().getString(key),
						defaultBoolean);
	}

	public static int getMaxRowNumbers(Context context) {
		return Integer.parseInt(getString(context,
				R.string.set_max_row_numbers_key,
				CommonConstants.CLIP_LIST_LIMIT_DEFAULT + ""));
	}

	public static String getLanguage(Context context) {
		boolean customerLanguage = getBoolean(context, R.string.customer_language_key, false);

		if (customerLanguage == true){
			return getString(context, R.string.select_language_key, "en");
		}
		
		return null;
	}

}
