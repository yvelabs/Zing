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
		// ����ֻ�������SD��������Ӧ�ó�����з���SD��Ȩ��
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File targetFile = new File(path);
			deleteFile(targetFile);
			targetFile.createNewFile();

			// ��ָ���ļ����� RandomAccessFile����,��һ���������ļ����ƣ��ڶ��������Ƕ�дģʽ
			RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");
			// ����ļ�����
			raf.write(content.getBytes());
			raf.close();
		}
	}

	public String readExternalFile(String path) throws FileNotFoundException,
			IOException {
		// ����ֻ�������SD��������Ӧ�ó�����з���SD��Ȩ��
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File targetFile = new File(path);
			if (targetFile.exists() == false) {
				return null;
			}
			
			// ��ȡָ���ļ���Ӧ��������
			FileInputStream fis = new FileInputStream(path);
			// ��ָ����������װ��BufferedReader
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
