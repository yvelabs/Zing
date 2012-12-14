package com.yvelabs.zing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.yvelabs.zing.utils.ClipboardUtils;
import com.yvelabs.zing.utils.FileUtils;
import com.yvelabs.zing.utils.NotificationUtils;
import com.yvelabs.zing.utils.ObjectsUtils;
import com.yvelabs.zing.utils.PreferenceUtils;

public class ZingMainFunction {
	
	public void setSelectedToClipboard (Context zingMainActivityContxt, Map<String, String> selectedMap) {
		// 设置为当前剪切板
		ClipboardUtils.setCurrentClipContent(zingMainActivityContxt,
				selectedMap.get(CommonConstants.MAP_KEY_CONTENT));

		// 构造通知
		new NotificationUtils().notifySpecifyNotification(
				zingMainActivityContxt,
				ZingMainActivity.class,
				ClipboardUtils
						.getCurrentClipContent(zingMainActivityContxt));

		// Toast提示
		Toast.makeText(
				zingMainActivityContxt,
				ObjectsUtils.getSubString(selectedMap.get(CommonConstants.MAP_KEY_CONTENT),
						15), Toast.LENGTH_SHORT).show();
	}
	
	public void deleteAll (ArrayList<HashMap<String, String>> clipList, Context zingMainActivityContxt) {
		//ClipList 清空
		clipList = new ArrayList<HashMap<String, String>>();
		
		//剪切板 清空
		ClipboardUtils.setCurrentClipContent(zingMainActivityContxt, "");
		
		//文件 删除
		if (zingMainActivityContxt.getExternalFilesDir(CommonConstants.EXTERNAL_FILES_DIR_TYPE) != null) {
			FileUtils.deleteFile(zingMainActivityContxt.getExternalFilesDir(CommonConstants.EXTERNAL_FILES_DIR_TYPE).getAbsolutePath()
					+ CommonConstants.FILE_NAME);
		} else {
			//TODO 异常处理
		}
		
		//通知
		new NotificationUtils().notifySpecifyNotification(zingMainActivityContxt, ZingMainActivity.class, ClipboardUtils.getCurrentClipContent(zingMainActivityContxt));
		
		//Toast提示
		Toast.makeText(zingMainActivityContxt, zingMainActivityContxt.getString(R.string.all_be_deleted), Toast.LENGTH_SHORT).show();
		
		//关闭程序
	}
	
	public void deleteSelected (ArrayList<HashMap<String, String>> clipList, Context zingMainActivityContxt, HashMap<String, String> selectedMap) {
		String selectedContent = selectedMap.get(CommonConstants.MAP_KEY_CONTENT);
		
		//ClipList 在List中检查重复, 删除所有重复
		ObjectsUtils.deleteAllTheRepeat(clipList, selectedContent);
		
		//剪切板: 
		//判断删除的记录是否存在于剪切板中, 如果存在 剪切板更新新内容
		if (selectedContent.equals(ClipboardUtils.getCurrentClipContent(zingMainActivityContxt))) {
			//如果ClipList中有数据, 取Date最大的
			if (clipList != null && clipList.size() > 0) {
				Collections.sort(clipList, new ObjectsUtils().new SortByDate());
				ClipboardUtils.setCurrentClipContent(zingMainActivityContxt, clipList.get(0).get(CommonConstants.MAP_KEY_CONTENT));
			} else {
				//如果ClipList为空, 剪切板置空
				ClipboardUtils.setCurrentClipContent(zingMainActivityContxt, "");
			}
		}
		
		//文件  新ClipList写入文件
		try {
			FileUtils.writeExternalFile(zingMainActivityContxt.getExternalFilesDir(CommonConstants.EXTERNAL_FILES_DIR_TYPE).getAbsolutePath() + CommonConstants.FILE_NAME, 
					ObjectsUtils.arrayList2String(clipList));
		} catch (Exception e) {
			e.printStackTrace();
			//TODO 异常处理
		}
		
		//通知
		new NotificationUtils().notifySpecifyNotification(zingMainActivityContxt, ZingMainActivity.class, ClipboardUtils.getCurrentClipContent(zingMainActivityContxt));

		//Toast提示
		Toast.makeText(zingMainActivityContxt, ObjectsUtils.getSubString(selectedContent, 7) + " " + zingMainActivityContxt.getString(R.string.has_been_deleted), Toast.LENGTH_SHORT).show();
		
		//页面 根据新List刷新
	}
	
	public void addNewContent (ArrayList<HashMap<String, String>> clipList, Context zingMainActivityContxt, String newContent) {
		//ClipList:
		//检查重复, 删除所有重复
		ObjectsUtils.deleteAllTheRepeat(clipList, newContent);
		//将新内容加入ClipList
		HashMap<String, String> newContentMap = new HashMap<String, String>();
		newContentMap.put(CommonConstants.MAP_KEY_DATE, ObjectsUtils.getCurrentTime());
		newContentMap.put(CommonConstants.MAP_KEY_CONTENT, newContent);
		clipList.add(newContentMap);
		//按时间顺序对ClipList排序
		Collections.sort(clipList, new ObjectsUtils().new SortByDate());
		//控制数量
		ObjectsUtils.limitedList(clipList, PreferenceUtils.getMaxRowNumbers(zingMainActivityContxt));
		
		//文件 新ClipList写入文件
		try {
			FileUtils.writeExternalFile(zingMainActivityContxt.getExternalFilesDir(CommonConstants.EXTERNAL_FILES_DIR_TYPE).getAbsolutePath() + CommonConstants.FILE_NAME, 
					ObjectsUtils.arrayList2String(clipList));
		} catch (Exception e) {
			e.printStackTrace();
			//TODO 异常处理
		}
		
		//通知
		new NotificationUtils().notifySpecifyNotification(zingMainActivityContxt, ZingMainActivity.class, ClipboardUtils.getCurrentClipContent(zingMainActivityContxt));
	}
	
	public String getDetail (Context zingMainActivityContxt, HashMap<String, String> map) {
		Date date = new Date(Long.parseLong(map.get(CommonConstants.MAP_KEY_DATE)));
		SimpleDateFormat sdf = new SimpleDateFormat(CommonConstants.DATE_FORMAT_1);
		StringBuilder result = new StringBuilder();
		
		result.append(zingMainActivityContxt.getString(R.string.content)).append(": ").append("\r\n").append(map.get(CommonConstants.MAP_KEY_SHOW_CONTENT)).append("\r\n").append("\r\n");
		result.append(zingMainActivityContxt.getString(R.string.added_time)).append(": ").append(sdf.format(date));
		return result.toString();
	}

}
