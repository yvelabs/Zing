package com.yvelabs.zing.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.yvelabs.zing.CommonConstants;
import com.yvelabs.zing.R;

public class NotificationUtils {
	
	public Notification buildNotification (Context context, Class returnClass, int smallIcon, String title, String ContentText) {
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(smallIcon)
		        .setContentTitle(title)
		        .setContentText(ContentText);
		Intent resultIntent = new Intent(context, returnClass);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(returnClass);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		
		return mBuilder.build();
	}
	
	
	/**
	 * notify a notification
	 * @param context
	 * @param returnClass
	 * @param smallIcon
	 * @param title
	 * @param ContentText
	 */
	public void notifyNotification (Context context, Class returnClass, int smallIcon, String title, String ContentText, int notificationId) {
		
		NotificationManager mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		mNotificationManager.notify(notificationId, buildNotification(context, returnClass, smallIcon, title, ContentText));
	}
	
	public void notifySpecifyNotification (Context context, Class clazz, String contentText) {
		notifyNotification(context, clazz, R.drawable.ic_launcher, context.getString(R.string.current_clip_content), contentText, CommonConstants.MAIN_NOTIFICATION_ID);
	}
	
	/**
	 * remove notification by id
	 * @param notificationId
	 */
	public void removeNotification (Context context, int notificationId) {
		NotificationManager mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);
	}
	
	/**
	 * remove all notification
	 */
	public void removeNotification (Context context) {
		NotificationManager mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
	}
}
