package com.foxconn.mechanism.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileStreamUtil {

	private String filePath = "";
	private String fileName = "";

	// 打开流处理
	public PrintStream openStream(String path) {
		File f = new File(path);
		BufferedWriter bw = null;
		PrintStream stream  = null;
		try {
			if (!f.exists())
				f.createNewFile();
//			FileWriter fw = new FileWriter(f);
//			bw = new BufferedWriter(fw);
			stream = new PrintStream(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream;
	}

	// 写入数据
	public void writeData(PrintStream write, String str) {
		try {
			write.println(str);
//			System.out.println(str);
//			write.println(System.getProperty("line.separator"));
//			write.write(str);
//			write.write(System.getProperty("line.separator"));
			write.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 关闭流
	public void close(PrintStream write) {
		try {
			if (write != null) {
				write.flush();
				write.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 得到temp目录下的txt文件
	public String getTempPath(String name) {
		String sNowTime = getNowTime();
//		String s = System.getenv("TEMP") + File.separator + name + sNowTime + ".txt";
		String s = System.getenv("TEMP") + File.separator + name + ".txt";
		filePath = System.getenv("TEMP");
//		fileName = name + sNowTime + ".txt";
		fileName = name + ".txt";
		
		return s;
	}

	public String getFilename() {
		return this.fileName;
	}
	
	public String getFilePath() {
		return this.filePath;
	}

	// 获取系统当前时间
	public String getNowTime() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(new Date().getTime());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		return dateFormat.format(c.getTime());
	}
	
	public String getSuffix(String fileName){
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);  
		return suffix;
	}
	
	public boolean existFile(String path) {
		File f = new File(path);
		return f.exists();
	}
	
	public static boolean deleteFile(String path) {
		try {
			File f = new File(path);
			return f.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * 读取文件信息到List
	 * 
	 * @param fileNow
	 * @param strCharsetName
	 * @return
	 * @throws Exception
	 * @author:
	 */
	public static List<String> getContent(File fileNow, String strCharsetName) throws Exception {
		List<String> retLst = new ArrayList<String>();

		String strLine = null;
		BufferedReader br = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;

		try {
			fis = new FileInputStream(fileNow);
			if (strCharsetName != null && strCharsetName.length() > 0) {
				isr = new InputStreamReader(fis, strCharsetName);
			} else {
				isr = new InputStreamReader(fis);
			}
			br = new BufferedReader(isr); // 解决控制台乱码问题
			while ((strLine = br.readLine()) != null) {
				retLst.add(strLine);
			}
		} finally {
			if (fis != null)
				fis.close();
			if (isr != null)
				isr.close();
			if (br != null)
				br.close();
		}

		return retLst;
	}
	
	public static Map<String, List<List<String>>> parseLstToMap(List<String> contentLst) {
		Map<String, List<List<String>>> retMap = new LinkedHashMap<String, List<List<String>>>();
		
		if (null == contentLst || 0 == contentLst.size()) return retMap;
		
		for (String content : contentLst) {
			String[] lineArr = content.split("=");
			if (2 == lineArr.length) {
				List<List<String>> mapValLst = new ArrayList<List<String>>();
				List<String> valLst = new ArrayList<String>();
				
				String lineKey = lineArr[0];
				String lineValue = lineArr[1];
				if (!"".equals(lineValue)) {
					String[] pubUserArr = lineValue.split("##");
					if (2 == pubUserArr.length) {						
						String[] projectInfoArr = pubUserArr[0].split(";");
						if (2 == projectInfoArr.length || 3 == projectInfoArr.length) {
							String projectName = projectInfoArr[0];							
							String itemName = projectInfoArr[1];
							String pubUser = (2 == projectInfoArr.length) ? "" : projectInfoArr[2];
							
							mapValLst.add(Arrays.asList(projectName.split(",")));
							mapValLst.add(Arrays.asList(itemName.split(",")));
							
							valLst.add(lineKey);
							valLst.add(pubUser);
						}															

						String[] realUserArr = pubUserArr[1].split("%%");
						if (0 == realUserArr.length || 2 == realUserArr.length) {
							String realUser = (0 == realUserArr.length) ? "" : realUserArr[0];
							String realMail = (0 == realUserArr.length) ? "" : realUserArr[1];
							
							valLst.add(realUser);
							valLst.add(realMail);
						}
						
						mapValLst.add(valLst);
					}
				}
								
				retMap.put(lineKey, mapValLst);
			}
		}
		
		return retMap;
	}
	
}
