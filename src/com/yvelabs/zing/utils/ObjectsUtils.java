package com.yvelabs.zing.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yvelabs.zing.CommonConstants;

public class ObjectsUtils {

	/**
	 * 删除中所有与content相同的项
	 * @param arrayList
	 * @param content
	 */
	public static void deleteAllTheRepeat (ArrayList<HashMap<String, String>> arrayList, String content) {
		if (content == null || content.length() <= 0) return;
		if (arrayList == null || arrayList.size() <= 0) return; 
		
		for (int i = 0 ; i < arrayList.size() ; i ++) {
			Map map = arrayList.get(i);
			
			if (content.equals(map.get("CONTENT").toString())) {
				arrayList.remove(map);
				i--;
			}
		}
	}
	
	/**
	 * 控制ArrayList数量
	 * @param arrayList
	 * @param limit
	 */
	public static void limitedList (ArrayList<HashMap<String, String>> arrayList, int limit) {
		if (limit <= 0) {
			arrayList.removeAll(arrayList);
			return ;
		}
		if (arrayList == null || arrayList.size() <= 0) return;
		if (limit >= arrayList.size()) return;
		
		int originalSize = arrayList.size();
		
		for (int i = 0 ; i < originalSize - limit; i ++) {
			arrayList.remove(originalSize - 1 - i);
		}
	}
	
	/**
	 * split
	 * @param str
	 * @param delimiter
	 * @return
	 */
	public static List<String> mySplit (String str, String delimiter) {
		List<String> resultList = new ArrayList<String>();
		
		while (str.length() > 0) {
			if (str.indexOf(delimiter) < 0) {
				resultList.add(str);
				break;
			}
			else{
				resultList.add(str.substring(0, str.indexOf(delimiter)));
				str = str.substring(str.indexOf(delimiter) + delimiter.length());
			}
		}
		
		return resultList;
	}
	
	/**
	 * 指定长度字符串
	 * @param str
	 * @param len
	 * @return
	 */
	public static String getSubString (String str, int len) {
		if (str == null || str.length() <= 0) return str;
		if (str.length() <= len) return str;
		
		return str.substring(0, len - 3) + "...";
	}
	
	/**  
     * <pre>  
     * 以二进制方式克隆对象  
     * </pre>  
     * @param src 被克隆的对象  
     * @return 克隆的对象  
     */  
	public static final Object byteClone(Object src) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(src);
			out.close();
			ByteArrayInputStream bin = new ByteArrayInputStream(
					baos.toByteArray());
			ObjectInputStream in = new ObjectInputStream(bin);
			Object clone = in.readObject();
			in.close();
			return (clone);
		} catch (ClassNotFoundException e) {
			throw new InternalError(e.toString());
		} catch (StreamCorruptedException e) {
			throw new InternalError(e.toString());
		} catch (IOException e) {
			throw new InternalError(e.toString());
		}
	}
	
	/**
	 * 
	 * @param str
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> string2ArrayList (String str) {
		ArrayList<HashMap<String, String>> resultList = new ArrayList<HashMap<String,String>>();
		if (str == null || str.length() <= 0) return resultList;
		
		HashMap<String, String> map = null;
		
		List<String> strList = mySplit(str, CommonConstants.SPLIT_DELIMITER_1); 
		
		for (String subStr1 : strList) {
			map = new HashMap<String, String>();
			List<String> subStr2 = mySplit(subStr1, CommonConstants.SPLIT_DELIMITER_2);
			map.put(CommonConstants.MAP_KEY_DATE, subStr2.get(0));
			map.put(CommonConstants.MAP_KEY_CONTENT, subStr2.get(1));
			resultList.add(map);
		}
		
		return resultList;
	}
	
	/**
	 * 
	 * @param arrayList
	 * @return
	 */
	public static String arrayList2String (ArrayList<HashMap<String, String>> arrayList) {
		if (arrayList == null || arrayList.size() <= 0) return null;
		StringBuilder resultString = null;
		
		for (HashMap<String, String> map : arrayList) {
			if (resultString == null) {
				if (map.get(CommonConstants.MAP_KEY_DATE) != null && map.get(CommonConstants.MAP_KEY_DATE).length() > 0 && 
						map.get(CommonConstants.MAP_KEY_CONTENT) != null && map.get(CommonConstants.MAP_KEY_CONTENT).length() > 0) {
					resultString = new StringBuilder();
					resultString.append(map.get(CommonConstants.MAP_KEY_DATE)).append(CommonConstants.SPLIT_DELIMITER_2).append(map.get(CommonConstants.MAP_KEY_CONTENT));
				}
			} else {
				if (map.get(CommonConstants.MAP_KEY_DATE) != null && map.get(CommonConstants.MAP_KEY_DATE).length() > 0 && 
						map.get(CommonConstants.MAP_KEY_CONTENT) != null && map.get(CommonConstants.MAP_KEY_CONTENT).length() > 0) {
					resultString.append(CommonConstants.SPLIT_DELIMITER_1).append(map.get(CommonConstants.MAP_KEY_DATE)).append(CommonConstants.SPLIT_DELIMITER_2).append(map.get(CommonConstants.MAP_KEY_CONTENT));
				}
			}
		}
		
		return resultString == null ? null : resultString.toString();
	}
	
	public class SortByDate implements Comparator<Map<String, String>> {
		@Override
		public int compare(Map<String, String> lhs, Map<String, String> rhs) {

			long lhsInt = Long.parseLong(lhs.get(CommonConstants.MAP_KEY_DATE));
			long rhsInt = Long.parseLong(rhs.get(CommonConstants.MAP_KEY_DATE));
			long result = lhsInt - rhsInt;
			return result > 0 ? -1 : result < 0 ? 1 : 0;
		}
	}
	
	public static String getCurrentTime () {
		return String.valueOf(System.currentTimeMillis());
	}
    
}
