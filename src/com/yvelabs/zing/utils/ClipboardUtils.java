package com.yvelabs.zing.utils;


import android.content.Context;
import android.text.ClipboardManager;

public class ClipboardUtils {

	public static String getCurrentClipContent(Context context) {
		ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		return clipboardManager.getText() == null ? null : clipboardManager
				.getText().toString();
	}

	public static void setCurrentClipContent(Context context, String clipContent) {
		ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		clipboardManager.setText(clipContent);
	}
}
