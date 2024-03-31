package com.foxconn.mechanism.hhpnmaterialapply.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Log {

	public static String fileAbsolutePath = null; // 日志文件存放的绝对路径
	
	private static String dir = null;	
	private static String fileName = null;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void init(String filePath, String filename) {
		try {
			createFolder(filePath);
			dir = filePath;
			fileName = filename;
			fileAbsolutePath = dir + fileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void createFolder(String filePath) {
		File folder = new File(filePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	public static void log(String log) {
		try {
			createFolder(dir);
			PrintWriter pw = new PrintWriter(new FileWriter(fileAbsolutePath, true));
			addTime();
			pw.println(log);
			pw.flush();
			pw.close();
			System.out.println(log);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void log(String log, Exception e) {
		try {
			createFolder(dir);
			PrintWriter pw = new PrintWriter(new FileWriter(fileAbsolutePath, true));
			addTime();
			pw.println(log);
			pw.flush();
			pw.close();
			System.out.println(log);
			printException(e);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	public static void printException(Exception e) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(fileAbsolutePath, true));
		e.printStackTrace();
		addTime();
		e.printStackTrace(pw);
		pw.flush();
		pw.close();
	}

	private static void addTime() throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(fileAbsolutePath, true));
		Date date = new Date();
//		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		pw.print(sdf.format(date));
		pw.print(" ");
		pw.flush();
		pw.close();
	}
	
	

}
