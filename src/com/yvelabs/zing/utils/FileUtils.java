package com.yvelabs.zing.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import android.os.Environment;

public class FileUtils {

	public static void deleteFile(String path) {
		File targetFile = new File(path);
		deleteFile(targetFile);
	}
	
	public static void deleteFile(File file) {
		if (file.exists())
			file.delete();
	}

	/**
	 * write external file (delete the old and create a new one)
	 * @param path
	 * @param content
	 * @throws IOException
	 */
	public static void writeExternalFile(String path, String content) throws IOException {
		// 如果手机插入了SD卡，而且应用程序具有访问SD的权限
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File targetFile = new File(path);
			deleteFile(targetFile);
			targetFile.createNewFile();

			// 以指定文件创建 RandomAccessFile对象,第一个参数是文件名称，第二个参数是读写模式
			RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
			// 输出文件内容
			raf.write(content.getBytes());
			raf.close();
		}
	}

	public String readExternalFile(String path) throws FileNotFoundException,
			IOException {
		// 如果手机插入了SD卡，而且应用程序具有访问SD的权限
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File targetFile = new File(path);
			if (targetFile.exists() == false) {
				return null;
			}
			
			// 获取指定文件对应的输入流
			FileInputStream fis = new FileInputStream(path);
			// 将指定输入流包装成BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			StringBuilder sb = new StringBuilder("");
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		}
		return null;
	}
}
