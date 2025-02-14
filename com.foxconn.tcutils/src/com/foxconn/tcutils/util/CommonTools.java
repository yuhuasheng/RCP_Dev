package com.foxconn.tcutils.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author 作者 Administrator
 * @version 创建时间：2021年12月6日 上午10:45:00 Description: 常用工具包
 */
public class CommonTools {

	/**
	 * 时间格式包含毫秒
	 */
	private static final String sdfm = "yyyy-MM-dd HH:mm:ss SSS";
	/**
	 * 普通的时间格式
	 */
	private static final String sdf = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 时间戳格式
	 */
	private static final String sd = "yyyyMMddHHmmss";

	/**
	 * 时间戳格式
	 */
	private static final String sd1 = "yyyyMMddHHmm";

	/**
	 * 检查是否为整型
	 */
	private static Pattern p = Pattern.compile("^\\d+$");

	/**
	 * 判断String类型的数据是否为空 null,""," " 为true "A"为false
	 *
	 * @return boolean
	 */
	public static boolean isEmpty(String str) {
		return (null == str || str.trim().length() == 0);
	}

	
	/**
	 * 判断String类型的数据是否为空 null,"", " " 为false "A", 为true
	 *
	 * @return boolean
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 判断list类型的数据是否为空 null,[] 为 true
	 *
	 * @return boolean
	 */
	public static boolean isEmpty(List<?> list) {
		return (null == list || list.size() == 0);
	}

	/**
	 * 判断list类型的数据是否为空 null,[] 为 false
	 *
	 * @return boolean
	 */
	public static boolean isNotEmpty(List<?> list) {
		return !isEmpty(list);
	}

	/**
	 * 判断Map类型的数据是否为空 null,[] 为true
	 *
	 * @return boolean
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return (null == map || map.size() == 0);
	}

	/**
	 * 判断map类型的数据是否为空 null,[] 为 false
	 *
	 * @return boolean
	 */
	public static boolean isNotEmpty(Map<?, ?> map) {
		return !isEmpty(map);
	}

	/**
	 * 
	 * @param 数组类型是否为空null,[] 为true
	 * @return
	 */
	public static boolean isEmpty(Object[] objects) {
		return (null == objects || objects.length == 0);
	}

	/**
	 * 数组类型是否为空null,[] 为false
	 * 
	 * @param objects
	 * @return
	 */
	public static boolean isNotEmpty(Object[] objects) {
		return !isEmpty(objects);
	}

	/**
	 * 
	 * @param 对象是否为空null,null为true
	 * @return
	 */
	public static boolean isEmpty(Object objects) {
		return null == objects;
	}

	/**
	 * 对象是否为空null,null为false
	 * 
	 * @param objects
	 * @return
	 */
	public static boolean isNotEmpty(Object objects) {
		return !isEmpty(objects);
	}

	/**
	 * 判断JSONObject类型的数据是否为空 null,[] 为true
	 *
	 * @return boolean
	 */
	public static boolean isEmpty(JSONObject json) {
		return (null == json || json.size() == 0);
	}

	/**
	 * 判断json类型的数据是否为空 null,[] 为 false
	 *
	 * @return boolean
	 */
	public static boolean isNotEmpty(JSONObject json) {
		return !isEmpty(json);
	}

	/**
	 * 字符串反转 如:入参为abc，出参则为cba
	 *
	 * @param str
	 * @return
	 */
	public static String reverse(String str) {
		if (isEmpty(str)) {
			return str;
		}
		return reverse(str.substring(1)) + str.charAt(0);
	}

	/**
	 * 获取当前long类型的的时间
	 *
	 * @return long
	 */
	public static long getNowLongTime() {
		return System.currentTimeMillis();
	}

	/**
	 * long类型的时间转换成 yyyyMMddHHmmss String类型的时间
	 *
	 * @param lo long类型的时间
	 * @return
	 */
	public static String longTime2StringTime(long lo) {
		return longTime2StringTime(lo, sd);
	}

	/**
	 * long类型的时间转换成自定义时间格式
	 *
	 * @param lo     long类型的时间
	 * @param format 时间格式
	 * @return String
	 */
	public static String longTime2StringTime(long lo, String format) {
		return new SimpleDateFormat(format).format(lo);
	}

	/**
	 * String类型的时间转换成 long
	 *
	 * @param
	 * @return String
	 * @throws ParseException
	 */
	public static long stringTime2LongTime(String time, String format) throws ParseException {
		if (isEmpty(format)) {
			format = sdf;
		}
		if (isEmpty(time)) {
			time = getNowTime(format);
		}
		SimpleDateFormat sd = new SimpleDateFormat(format);
		Date date = sd.parse(time);
		return date.getTime();
	}

	/**
	 * 格式化时间
	 *
	 * @param format1 之前的 时间格式
	 * @param format2 之后的 时间格式
	 * @param time    时间
	 * @return String
	 * @throws ParseException
	 */
	public static String formatTime(String format1, String format2, String time) throws ParseException {
		SimpleDateFormat d1 = new SimpleDateFormat(format1);
		SimpleDateFormat d2 = new SimpleDateFormat(format2);
		time = d2.format(d1.parse(time));
		return time;
	}

	/**
	 * 时间补全 例如将2018-04-04补全为2018-04-04 00:00:00.000
	 *
	 * @param time 补全的时间
	 * @return
	 */
	public static String complementTime(String time) {
		return complementTime(time, sdfm, 1);

	}

	/**
	 * 时间补全 例如将2018-04-04补全为2018-04-04 00:00:00.000
	 *
	 * @param time   补全的时间
	 * @param format 补全的格式
	 * @param type   类型 1:起始;2:终止
	 * @return
	 */
	public static String complementTime(String time, String format, int type) {
		if (isEmpty(time) || isEmpty(format)) {
			return null;
		}
		int tlen = time.length();
		int flen = format.length();
		int clen = flen - tlen;
		if (clen <= 0) {
			return time;
		}
		StringBuffer sb = new StringBuffer(time);
		if (clen == 4) {
			if (type == 1) {
				sb.append(".000");
			} else {
				sb.append(".999");
			}
		} else if (clen == 9) {
			if (type == 1) {
				sb.append(" 00:00:00");
			} else {
				sb.append(" 23:59:59");
			}
		} else if (clen == 13) {
			if (type == 1) {
				sb.append(" 00:00:00.000");
			} else {
				sb.append(" 23:59:59.999");
			}
		}
		return sb.toString();

	}

	/**
	 * 获取当前String类型的的时间 使用默认格式 yyyy-MM-dd HH:mm:ss
	 *
	 * @return String
	 */
	public static String getNowTime() {
		return getNowTime(sdf);
	}

	public static String getNowTime2() {
		return getNowTime(sd1);
	}

	/**
	 * 获取当前String类型的的时间(自定义格式)
	 *
	 * @param format 时间格式
	 * @return String
	 */
	public static String getNowTime(String format) {
		return new SimpleDateFormat(format).format(new Date());
	}

	/**
	 * 获取当前Timestamp类型的的时间
	 *
	 * @return Timestamp
	 */
	public static Timestamp getTNowTime() {
		return new Timestamp(getNowLongTime());
	}

	/**
	 * 获取的String类型的当前时间并更改时间
	 *
	 * @param number 要更改的的数值
	 * @param format 更改时间的格式 如yyyy-MM-dd HH:mm:ss
	 * @param type   更改时间的类型 时:h; 分:m ;秒:s
	 * @return String
	 */
	public static String changeTime(int number, String format, String type) {
		return changeTime(number, format, type, "");
	}

	/**
	 * 获取的String类型时间并更改时间
	 *
	 * @param number 要更改的的数值
	 * @param format 更改时间的格式
	 * @param type   更改时间的类型 。时:h; 分:m ;秒:s
	 * @param time   更改的时间 没有则取当前时间
	 * @return String
	 */
	public static String changeTime(int number, String format, String type, String time) {
		if (isEmpty(time)) { // 如果没有设置时间则取当前时间
			time = getNowTime(format);
		}
		SimpleDateFormat format1 = new SimpleDateFormat(format);
		Date d = null;
		Calendar ca = null;
		String backTime = null;
		try {
			d = format1.parse(time);
			ca = Calendar.getInstance(); // 定义一个Calendar 对象
			ca.setTime(d);// 设置时间
			if ("h".equals(type)) {
				ca.add(Calendar.HOUR, number);// 改变时
			} else if ("m".equals(type)) {
				ca.add(Calendar.MINUTE, number);// 改变分
			} else if ("s".equals(type)) {
				ca.add(Calendar.SECOND, number);// 改变秒
			}
			backTime = format1.format(ca.getTime()); // 转化为String 的格式
		} catch (Exception e) {
			e.printStackTrace();
		}
		return backTime;
	}

	/**
	 * 两个日期带时间比较 第二个时间大于第一个则为true，否则为false
	 *
	 * @param
	 * @return boolean
	 * @throws ParseException
	 */
	public static boolean isCompareDay(String time1, String time2, String format) {
		if (isEmpty(format)) {// 如果没有设置格式使用默认格式
			format = sdf;
		}
		SimpleDateFormat s1 = new SimpleDateFormat(format);
		Date t1 = null;
		Date t2 = null;
		try {
			t1 = s1.parse(time1);
			t2 = s1.parse(time2);
			return t2.after(t1);// 当 t2 大于 t1 时，为 true，否则为 false
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 获取几天之前的时间
	 *
	 * @param day
	 * @return
	 * @since 1.8
	 */
	public static String getMinusDays(int day) {
		return getMinusDays(day, sdf);
	}

	/**
	 * 获取几天之前的时间
	 *
	 * @param day
	 * @param format
	 * @return
	 * @since 1.8
	 */
	public static String getMinusDays(int day, String format) {
		return LocalDateTime.now().minusDays(day).format(DateTimeFormatter.ofPattern(format));
	}

	/**
	 * 获取几天之后的时间
	 *
	 * @param day
	 * @return
	 * @since 1.8
	 */
	public static String getPlusDays(int day) {
		return getPlusDays(day, sdf);
	}

	/**
	 * 获取几天之后的时间
	 *
	 * @param day
	 * @param format
	 * @return
	 * @since 1.8
	 */
	public static String getPlusDays(int day, String format) {
		return LocalDateTime.now().plusDays(day).format(DateTimeFormatter.ofPattern(format));
	}

	/**
	 * 获取几天之后的时间
	 *
	 * @param
	 * @return
	 * @since 1.8
	 */
	public static String getPlusMonths(int month) {
		return getPlusMonths(month, sdf);
	}

	/**
	 * 获取几月之后的时间
	 *
	 * @param
	 * @param format
	 * @return
	 * @since 1.8
	 */
	public static String getPlusMonths(int month, String format) {
		return LocalDateTime.now().plusMonths(month).format(DateTimeFormatter.ofPattern(format));
	}

	/**
	 * 增加月份
	 *
	 * @param time  格式为yyyy-MM-dd
	 * @param month 增加月份
	 * @return
	 */
	public static String addPlusMonths(String time, int month) {
		return LocalDate.parse(time).plusMonths(month).toString();
	}

	/**
	 * 时间相比得月份 如果是201711和201801相比，返回的结果是2 前面的时间要小于后面的时间
	 *
	 * @param month   格式为yyyyMM
	 * @param toMonth 格式为yyyyMM
	 * @return
	 * @since jdk 1.8
	 */
	public static int diffMonth(String month, String toMonth) {
		int year1 = Integer.parseInt(month.substring(0, 4));
		int month1 = Integer.parseInt(month.substring(4, 6));
		int year2 = Integer.parseInt(month.substring(0, 4));
		int month2 = Integer.parseInt(month.substring(4, 6));
		LocalDate ld1 = LocalDate.of(year1, month1, 01);
		LocalDate ld2 = LocalDate.of(year2, month2, 01);
		return Period.between(ld1, ld2).getMonths();
	}

	/**
	 * 判断是否为整型
	 *
	 * @param
	 * @return boolean
	 */
	public static boolean isInteger(String str) {
		Matcher m = p.matcher(str);
		return m.find();
	}

	/**
	 * 判断字符串是否全部为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		// Pattern pattern = Pattern.compile("^-?[0-9]+"); //这个也行
		Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");// 这个也行
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 自定义位数产生随机数字
	 *
	 * @param
	 * @return String
	 */
	public static String random(int count) {
		char start = '0';
		char end = '9';
		Random rnd = new Random();
		char[] result = new char[count];
		int len = end - start + 1;
		while (count-- > 0) {
			result[count] = (char) (rnd.nextInt(len) + start);
		}
		return new String(result);
	}

	/**
	 * 获取自定义长度的随机数(含字母)
	 *
	 * @param len 长度
	 * @return String
	 */
	public static String random2(int len) {
		int random = Integer.parseInt(random(5));
		Random rd = new Random(random);
		final int maxNum = 62;
		StringBuffer sb = new StringBuffer();
		int rdGet;// 取得随机数
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
				't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
				'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9' };
		int count = 0;
		while (count < len) {
			rdGet = Math.abs(rd.nextInt(maxNum));// 生成的数最大为62-1
			if (rdGet >= 0 && rdGet < str.length) {
				sb.append(str[rdGet]);
				count++;
			}
		}
		return sb.toString();
	}

	/**
	 * 获取本机ip
	 *
	 * @return String
	 * @throws UnknownHostException
	 */
	public static String getLocalHostIp() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}

	/**
	 * Object 转换为 String
	 *
	 * @param
	 * @return String
	 */
	public static String toString(Object obj) {
		return JSON.toJSONString(obj);
	}

	/**
	 * JSON 转换为 JavaBean
	 *
	 * @param json
	 * @param t
	 * @return <T>
	 */
	public static <T> T toBean(JSONObject json, Class<T> t) {
		return JSON.toJavaObject(json, t);
	}

	/**
	 * JSON 字符串转换为 JavaBean
	 *
	 * @param str
	 * @param t
	 * @return <T>
	 */
	public static <T> T toBean(String str, Class<T> t) {
		return JSON.parseObject(str, t);
	}

	/**
	 * JSON 字符串 转换成JSON格式
	 *
	 * @param
	 * @return JSONObject
	 */
	public static JSONObject toJson(String str) {
		if (isEmpty(str)) {
			return new JSONObject();
		}
		return JSON.parseObject(str);

	}

	/**
	 * JavaBean 转化为JSON
	 *
	 * @param t
	 * @return
	 */
	public static JSONObject toJson(Object t) {
		if (null == t || "".equals(t)) {
			return new JSONObject();
		}
		return (JSONObject) JSON.toJSON(t);
	}

	/**
	 * JSON 字符串转换为 HashMap
	 *
	 * @param json - String
	 * @return Map
	 */
	@SuppressWarnings("rawtypes")
	public static Map toMap(String json) {
		if (isEmpty(json)) {
			return new HashMap();
		}
		return JSON.parseObject(json, HashMap.class);
	}

	/**
	 * 将map转化为string
	 *
	 * @param m
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String toString(Map m) {
		return JSONObject.toJSONString(m);
	}

	/**
	 * String转换为数组
	 *
	 * @param text
	 * @return
	 */
	public static <T> Object[] toArray(String text) {
		return toArray(text, null);
	}

	/**
	 * String转换为数组
	 *
	 * @param text
	 * @return
	 */
	public static <T> Object[] toArray(String text, Class<T> clazz) {
		return JSON.parseArray(text, clazz).toArray();
	}

	/**
	 * name1=value1&name2=value2格式的数据转换成json数据格式
	 *
	 * @param str
	 * @return
	 */
	public static JSONObject str2Json(String str) {
		if (isEmpty(str)) {
			return new JSONObject();
		}
		JSONObject json = new JSONObject();
		String[] str1 = str.split("&");
		String str3 = "", str4 = "";
		if (null == str1 || str1.length == 0) {
			return new JSONObject();
		}
		for (String str2 : str1) {
			str3 = str2.substring(0, str2.lastIndexOf("="));
			str4 = str2.substring(str2.lastIndexOf("=") + 1, str2.length());
			json.put(str3, str4);
		}
		return json;
	}

	/**
	 * json数据格式 转换成name1=value1&name2=value2格式
	 *
	 * @param
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String json2Str(JSONObject json) {
		if (isEmpty(json)) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		Iterator it = json.entrySet().iterator(); // 定义迭代器
		while (it.hasNext()) {
			Entry er = (Entry) it.next();
			sb.append(er.getKey());
			sb.append("=");
			sb.append(er.getValue());
			sb.append("&");
		}
		sb.delete(sb.length() - 1, sb.length()); // 去掉最后的&
		return sb.toString();
	}

	/**
	 * 将JDBC查询的数据转换成List类型
	 *
	 * @param
	 * @return List
	 * @throws SQLException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List convertList(ResultSet rs) throws SQLException {
		if (null == rs) {
			return new ArrayList<>();
		}
		List list = new ArrayList();
		ResultSetMetaData md = rs.getMetaData();
		int columnCount = md.getColumnCount();
		while (rs.next()) {
			JSONObject rowData = new JSONObject();
			for (int i = 1; i <= columnCount; i++) {
				rowData.put(md.getColumnName(i), rs.getObject(i));
			}
			list.add(rowData);
		}
		return list;
	}

	/**
	 * MD5加密
	 *
	 * @param message
	 * @return
	 */
	public static String md5Encode(String message) {
		byte[] secretBytes = null;
		try {
			secretBytes = MessageDigest.getInstance("md5").digest(message.getBytes());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("没有md5这个算法！");
		}
		String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
		// 如果生成数字未满32位，需要前面补0
		int length = 32 - md5code.length();
		for (int i = 0; i < length; i++) {
			md5code = "0" + md5code;
		}
		return md5code;
	}

	/**
	 * 十进制转二进制
	 *
	 * @param n
	 * @return
	 */
	public static String decToBinary(int n) {
		String str = "";
		while (n != 0) {
			str = n % 2 + str;
			n = n / 2;
		}
		return str;
	}

	/**
	 * 二进制转十进制
	 *
	 * @param
	 * @return
	 */
	public static int binaryToDec(char[] cs) {
		return binaryToDec(cs);
	}

	/**
	 * 二进制转十进制
	 *
	 * @param
	 * @return
	 */
	public static int binaryToDec(String cs) {
		return new BigInteger(new String(cs), 2).intValue();
	}

	/**
	 * 后台执行bat文件
	 * 
	 * @param batPath
	 * @param batName
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public static boolean doCallBat(String batPath, String batName) throws IOException, InterruptedException {
		Boolean flag = false;
		System.out.println("******** 【INFO】 " + batName + " bat File excute start ********");
		if (isEmpty(batName)) {
			batName = new File(batPath).getName();
		}
		List list = new ArrayList();
		ProcessBuilder processBuilder = null;
		Process process = null;
		String line = null;
		BufferedReader stdout = null;
		list.add("cmd");
		list.add("/c");
//		list.add("start");
		list.add(batPath);
		processBuilder = new ProcessBuilder(list);
		processBuilder.redirectErrorStream(true);
		process = processBuilder.start();
		stdout = new BufferedReader(new InputStreamReader(process.getInputStream(), "BIG5"));
		OutputStreamWriter os = new OutputStreamWriter(process.getOutputStream());
		while ((line = stdout.readLine()) != null) {
			System.out.println("【" + batName + "】:" + line);
//			System.out.println("【" + batName + "】:" + line);
			if (line.contains("Updating BOMs and other references") && "ipemimport.bat".equals(batName)) { // 代表执行
				flag = true;
			} else if ("ipemexport.bat".equals(batName)) {
				flag = true;
			} else if ("ending".equals(line)) {
				break;
			}
		}
		int ret = process.waitFor();
		stdout.close();
		System.out.println("******** 【INFO】 " + batName + "bat File excute finsh ********");
		return flag;
	}

	@SuppressWarnings("unused")
	public static String getFilePath(String foldName) {
		String tempPath = System.getProperty("java.io.tmpdir") + File.separator;
		System.out.println("【INFO】 tempPath: " + tempPath);
		File file = new File(tempPath + foldName);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}

	/**
	 * 删除某个文件夹下的所有文件
	 * 
	 * @param delpath String
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return boolean
	 */
	public static boolean deletefile(String delpath) throws Exception {
		try {
			File file = new File(delpath);
			// 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + File.separator + filelist[i]);
					if (!delfile.isDirectory()) {
						delfile.delete();
						System.out.println("【INFO】 " + delfile.getAbsolutePath() + "删除文件成功");
					} else if (delfile.isDirectory()) {
						deletefile(delpath + File.separator + filelist[i]);
					}
				}
//				log.info("【INFO】 " + file.getAbsolutePath() + "删除成功");
//				file.delete();
			}

		} catch (FileNotFoundException e) {
			System.out.println("【ERROR】 " + "deletefile() Exception:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 删除某个文件夹下的所有文件(包含进程正在使用的文件)
	 * 
	 * @param delpath String
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return boolean
	 */
	public static boolean deletefileNew(String delpath) {
		try {
			File file = new File(delpath);
			// 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + File.separator + filelist[i]);
					if (!delfile.isDirectory()) {
						boolean result = delfile.delete();
						if (!result) {
							System.gc();
							delfile.delete();
						}
						System.out.println("【INFO】 " + delfile.getAbsolutePath() + "删除文件成功");
					} else if (delfile.isDirectory()) {
						deletefile(delpath + File.separator + filelist[i]);
					}
				}
//				log.info("【INFO】 " + file.getAbsolutePath() + "删除成功");
//				file.delete();
			}
		} catch (Exception e) {
			System.out.println("【ERROR】 " + "deletefileNew() Exception:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 删除某个文件夹下的某种名称的文件
	 * 
	 * @param delpath String
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @return boolean
	 */
	public static boolean deletefile(String delpath, String fileName) {
		try {
			File file = new File(delpath);
			// 当且仅当此抽象路径名表示的文件存在且 是一个目录时，返回 true
			if (!file.isDirectory()) {
				file.delete();
			} else if (file.isDirectory()) {
				String[] filelist = file.list();
				for (int i = 0; i < filelist.length; i++) {
					File delfile = new File(delpath + File.separator + filelist[i]);
					if (!delfile.isDirectory()) {
						if (delfile.getAbsolutePath().contains(fileName)) { // 判断是否含有某种文件名
							delfile.delete();
							System.out.println("【INFO】 " + delfile.getAbsolutePath() + "删除文件成功");
						}
					} else if (delfile.isDirectory()) {
						deletefile(delpath + File.separator + filelist[i], fileName);
					}
				}
//				log.info("【INFO】 " + file.getAbsolutePath() + "删除成功");
//				file.delete();
			}

		} catch (Exception e) {
			System.out.println("【ERROR】 " + "deletefile() Exception:" + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 获取某种文件名的文件绝对路径
	 * 
	 * @param filePath
	 * @param fileName
	 * @param list
	 */
	public static void getFileList(String filePath, String fileName, List<String> list) {
		try {
			File dir = new File(filePath);
			if (dir.isDirectory()) {
				String[] filelist = dir.list();
				for (int i = 0; i < filelist.length; i++) {
					File file = new File(filePath + File.separator + filelist[i]);
					if (!file.isDirectory()) {
						String fileAbsolutePath = file.getAbsolutePath();
						if (fileAbsolutePath.contains(fileName)) { // 判断是否含有某种文件名
							if (!list.contains(fileAbsolutePath)) {
								list.add(fileAbsolutePath);
							}
						}
					} else if (file.isDirectory()) {
						getFileList(filePath + File.separator + filelist[i], fileName, list);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断文件夹是否有文件
	 * 
	 * @param filePath 文件夹路径
	 * @return
	 */
	public static boolean checkFolder(String filePath) {
		File file = new File(filePath);
		File[] listFiles = file.listFiles();
		if (listFiles.length > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取jar包所在的文件夹
	 * 
	 * @return
	 */
	public static String getPath(Class<?> class1) {
		System.out.println(class1.getProtectionDomain().getCodeSource().getLocation());
		String path = class1.getProtectionDomain().getCodeSource().getLocation().getPath();
		if (System.getProperty("os.name").contains("dows")) {
			path = path.substring(1, path.length());
		}
		if (path.contains("jar")) {
			path = path.substring(0, path.lastIndexOf("."));
			return path.substring(0, path.lastIndexOf("/"));
		}
		return path.replace("target/classes/", "");
	}

	/**
	 * 获取日志文件的绝对路径
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Properties getProperties(String filePath) {
		try {
			// 使用InPutStream流读取properties文件
			BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
			Properties props = new Properties();
			props.load(bufferedReader);
			return props;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new ConcurrentHashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	/**
	 * 基本方法的测试示例
	 *
	 * @param args
	 */
	@SuppressWarnings({ "rawtypes" })
	public static void main(String[] args) {
		/*
		 * String 和List 空数据判断
		 */
		String str1 = "";
		String str2 = " ";
		String str3 = null;
		String str4 = "a";
		List list = null;
		List<String> list2 = new ArrayList<String>();
		List<Object> list3 = new ArrayList<Object>();
		list3.add("a");

		System.out.println("str1 :" + isEmpty(str1)); // str1 :true
		System.out.println("str2 :" + isEmpty(str2)); // str2 :true
		System.out.println("str3 :" + isEmpty(str3)); // str3 :true
		System.out.println("str4 :" + isEmpty(str4)); // str4 :false
		System.out.println("list :" + isEmpty(list)); // list :true
		System.out.println("list2 :" + isEmpty(list2)); // list2 :true
		System.out.println("list3 :" + isEmpty(list3)); // list3 :false

		/*
		 * 时间
		 */
		long start = getNowLongTime();
		System.out.println("getNowTime():" + getNowTime()); // getNowTime():2017-09-26
		// 17:46:44
		System.out.println("getNowLongTime():" + getNowLongTime()); // getNowLongTime():1506419204920
		System.out.println("getNowTime(sdfm):" + getNowTime(sdfm)); // getNowTime(sdfm):2017-09-26
		// 17:46:44
		// 920
		System.out.println("当时时间向前推移30秒:" + changeTime(-30, sdf, "s")); // 2017-09-26
		// 17:46:14
		System.out.println("时间比较:" + isCompareDay(getNowTime(sdfm), changeTime(-30, sdf, "s"), "")); // 时间比较:false
		System.out.println("getTNowTime():" + getTNowTime()); // getTNowTime():2017-09-26
		// 17:46:44.921
		System.out.println("LongTime2StringTime():" + longTime2StringTime(start, sd)); // LongTime2StringTime():20170926174644

		/*
		 * 整型判断
		 */
		String st = "258369";
		String st2 = "258369A!@";
		String st3 = "258  369 ";
		System.out.println("st:" + isInteger(st)); // st:true
		System.out.println("st2:" + isInteger(st2)); // st2:false
		System.out.println("st3:" + isInteger(st3)); // st3:false

		/*
		 * 字符串反转
		 */
		String re = "abcdefg";
		System.out.println("字符串反转:" + reverse(re)); // 字符串反转:gfedcba

		/*
		 * 本机IP
		 */
		try {
			System.out.println("本机IP:" + getLocalHostIp()); // 本机IP:192.168.1.111
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		/*
		 * 随机数
		 */

		System.out.println("6位随机数:" + random(6)); // 6位随机数:222488
		System.out.println("10位随机数:" + random2(10)); // 10位随机数:ZwW0pmofjW

		/*
		 * JSON数据转换
		 */

		String value = "name1=value1&name2=value2&name3=value3";
		JSONObject json = new JSONObject();
		json.put("name1", "value1");
		json.put("name2", "value2");
		json.put("name3", "value3");
		System.out.println("value:" + value); // value:name1=value1&name2=value2&name3=value3
		System.out.println("str2Json:" + str2Json(value)); // str2Json:{"name1":"value1","name2":"value2","name3":"value3"}
		System.out.println("json:" + json.toJSONString()); // json:{"name1":"value1","name2":"value2","name3":"value3"}
		System.out.println("json2Str:" + json2Str(json)); // json2Str:name3=value3&name1=value1&name2=value2

		String jsonString = json.toJSONString();
		System.out.println("jsonString:" + jsonString); // {"name1":"value1","name2":"value2","name3":"value3"}
		System.out.println("toJson(jsonString):" + toJson(jsonString)); // toJson(jsonString):{"name1":"value1","name2":"value2","name3":"value3"}

		System.out.println("long TO String" + longTime2StringTime(32472115200L));
		System.out.println("long TO String" + longTime2StringTime(1513330097L));

		String time1 = "2018-04-04";
		String time2 = "2018-04-04 14:48:00";
		String time3 = "2018-04-04 14:48:00.000";
		System.out.println("时间补全:" + complementTime(time1, sdfm, 1));
		System.out.println("时间补全:" + complementTime(time2, sdfm, 2));
		System.out.println("时间补全:" + complementTime(time3, sdfm, 1));
		String time4 = addPlusMonths(time1, 2);
		System.out.println("增加之前的数据:" + time1 + "增加月份之后的数据:" + time4);
		System.out.println("相差月份:" + diffMonth("201711", "201801"));

		int l = 2;
		String string = "10101";
		System.out.println(l + " 十进制转二进制: " + decToBinary(l));

		System.out.println(string + " 二进制转十进制: " + binaryToDec(string));

	}

	public static File releaseFileToTemp(String innerPath) throws Exception {
		InputStream is = CommonTools.class.getResourceAsStream(innerPath);// 流式读取jar包内文件，使用classpath
		File f = new File(System.getProperty("java.io.tmpdir") + innerPath);// 指定输出文件
		File fp = new File(f.getParent());// 输出文件的父目录
		if (!fp.exists()) {// 父目录不存在时先创建
			fp.mkdirs();
		}
		if (!f.exists()) {// 文件不存在时先创建
			f.createNewFile();
		}
		OutputStream os = new FileOutputStream(f);// 创建输出流
		int index = 0;// 当前读取的位数
		byte[] bytes = new byte[1024];// 指定每次读取的位数，这里以1024为例
		// 开始读取文件，一遍读取一边输出文件。每次读取1024的长度并储存于bytes这个数组中，然后写入至目标文件
		while ((index = is.read(bytes)) != -1) {
			os.write(bytes, 0, index);// 输出文件，write()方法参数分别为：写入的数据、开始写入的位置（0为从开头写入），写入长度
		}
		// 关闭流并保存文件
		os.flush();
		os.close();
		is.close();
		return f;
	}

	public static boolean releaseFile(String innerPath, File outputFile) {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = CommonTools.class.getResourceAsStream(innerPath);// 流式读取jar包内文件，使用classpath
			File fp = new File(outputFile.getParent());// 输出文件的父目录
			if (!fp.exists()) {// 父目录不存在时先创建
				fp.mkdirs();
			}
			if (!outputFile.exists()) {// 文件不存在时先创建
				outputFile.createNewFile();
			}
			os = new FileOutputStream(outputFile);// 创建输出流
			int index = 0;// 当前读取的位数
			byte[] bytes = new byte[1024];// 指定每次读取的位数，这里以1024为例
			// 开始读取文件，一遍读取一边输出文件。每次读取1024的长度并储存于bytes这个数组中，然后写入至目标文件
			while ((index = is.read(bytes)) != -1) {
				os.write(bytes, 0, index);// 输出文件，write()方法参数分别为：写入的数据、开始写入的位置（0为从开头写入），写入长度
			}
			// 关闭流并保存文件
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	

	/**
	 *
	 * @param longStr 长字符串
	 * @param mixStr  子字符串
	 * @return 包含个数
	 */
	public static int countStr(String longStr, String mixStr) {
		// 如果确定传入的字符串不为空，可以把下面这个判断去掉，提高执行效率
		if (longStr == null || mixStr == null || "".equals(longStr.trim()) || "".equals(mixStr.trim())) {
			return 0;
		}
		int count = 0;
		int index = 0;
		while ((index = longStr.indexOf(mixStr, index)) != -1) {
			index = index + mixStr.length();
			count++;
		}
		return count;
	}
	
	 /**
     * 获取到后几位小数，保留小数点后几位由size决定
     */
    public static String formatDecimal(String val, int size) {
        if (!isNotEmpty(val))
            return "";

        BigDecimal db = new BigDecimal(val);
        db = db.setScale(size, RoundingMode.HALF_UP);
        return db.toString();
    }
    
    
    public static int getDecimalPlaces(String numberStr) {
    	Pattern pattern = Pattern.compile("\\.\\d+");
    	Matcher matcher = pattern.matcher(numberStr);
    	int decimalPlaces = 0;
    	if (matcher.find()) {
    		decimalPlaces = matcher.group().length() - 1;
    		System.out.println("==>> 小数点后的位数为: " + decimalPlaces);
    	} else {
			System.out.println("不是小数");
		}
    	
    	
    	return decimalPlaces;
    }
    
    
    /**
     * 去除字符串中的制表符\t,回车\n,换行\r
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (dest != null) {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll(" ");
        }
        return dest.trim(); // 去除字符串前后空格
    }
    
    /**
                * 移除特殊字符
     * @param str
     * @return
     */
    public static String removeFileSpecicalStr(String str) {
    	String dest = "";
    	if (dest != null) {
    		Pattern pattern = Pattern.compile("[\\s\\\\/:\\*\\?\\\"<>\\|]");
        	Matcher matcher = pattern.matcher(str);
        	dest = matcher.replaceAll(" ");
		}  	
    	return dest.trim();
    }
    
    /**
     * Excel中字母转数字
     * @param strColum
     * @return
     */
    public static int getColumIntByString(String strColum) {
        int intColum = 0;
        int lenth = strColum.length();
        for (int i = 0; i < lenth; i++) {
            // 公式：26^指数*字母的位数
            intColum = intColum + (int) (Math.pow(26, lenth - 1 - i) * (strColum.charAt(i) - 64));
        }
        return (intColum - 1);
    }
    
    public static String getUUID() {
    	return UUID.randomUUID().toString();
    }
    
    
    /**
     * 判断字符串是不是以数字开头
     * @param str
     * @return
     */
    public static boolean isStartWithNumber(String str) {
    	Pattern pattern = Pattern.compile("[0-9]*");
    	Matcher isNum = pattern.matcher(str.charAt(0) + "");
    	if (!isNum.matches()) {
			return false;
		}
    	return true;
    }
    
    
	public static boolean checkJPG(TCComponent[] relatedComponents) throws TCException {
		boolean flag = false;
		if (CommonTools.isEmpty(relatedComponents)) {
			return flag;
		}
		for (TCComponent tcComponent : relatedComponents) {
			if (tcComponent instanceof TCComponentDataset) {
				TCComponentDataset dataset = (TCComponentDataset) tcComponent;
				String objectName = dataset.getProperty("object_name");
				if (objectName.toLowerCase().endsWith(".png") || objectName.toLowerCase().endsWith(".jpg")
						|| objectName.toLowerCase().endsWith(".jpeg")) { 
					flag = true;
					break;
				}
			}
		}
		return flag;
	}
	public static File getModelJPEG_hp(TCComponentItemRevision ItemRevision) throws Exception {
		TCComponent[] relatedComponents = null;
		relatedComponents = ItemRevision.getRelatedComponents("IMAN_reference"); 
		if (!checkJPG(relatedComponents)) { 
			relatedComponents = ItemRevision.getRelatedComponents("IMAN_specification"); 
		}
		
		for (TCComponent tcComponent : relatedComponents) {
			if (tcComponent instanceof TCComponentDataset) {
				TCComponentDataset dataset = (TCComponentDataset) tcComponent;
				//String objectName = dataset.getProperty("object_name");
				String objectType = dataset.getTypeObject().getName();
				TCComponentTcFile[] files = null;
//				if (objectName.toLowerCase().endsWith(".png") || objectName.toLowerCase().endsWith(".jpg")
//						|| objectName.toLowerCase().endsWith(".jpeg")) {
				if(objectType.equals("Image")) {
					files = dataset.getTcFiles();
					if (CommonTools.isNotEmpty(files)) {
						return files[0].getFmsFile();
					}
				} else if ("ProPrt".equalsIgnoreCase(objectType) || "ProAsm".equalsIgnoreCase(objectType)) {
					dataset.refresh();
					files = dataset.getTcFiles();
					if (files != null) {
						for (int i = 0; i < files.length; i++) {
							TCComponentTcFile tcFile = files[i];
							if ("image/jpeg".equalsIgnoreCase(tcFile.getProperty("mime_type"))) {
								return tcFile.getFmsFile();
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	public static File getModelJPEG(TCComponentItemRevision ItemRevision) throws Exception {
		TCComponent[] relatedComponents = null;
		relatedComponents = ItemRevision.getRelatedComponents("IMAN_reference"); 
		if (!checkJPG(relatedComponents)) { 
			relatedComponents = ItemRevision.getRelatedComponents("IMAN_specification"); 
		}
		
		for (TCComponent tcComponent : relatedComponents) {
			if (tcComponent instanceof TCComponentDataset) {
				TCComponentDataset dataset = (TCComponentDataset) tcComponent;
				String objectName = dataset.getProperty("object_name");
				String objectType = dataset.getTypeObject().getName();
				TCComponentTcFile[] files = null;
				if (objectName.toLowerCase().endsWith(".png") || objectName.toLowerCase().endsWith(".jpg")
						|| objectName.toLowerCase().endsWith(".jpeg")) {
					files = dataset.getTcFiles();
					if (CommonTools.isNotEmpty(files)) {
						return files[0].getFmsFile();
					}
				} else if ("ProPrt".equalsIgnoreCase(objectType) || "ProAsm".equalsIgnoreCase(objectType)) {
					dataset.refresh();
					files = dataset.getTcFiles();
					if (files != null) {
						for (int i = 0; i < files.length; i++) {
							TCComponentTcFile tcFile = files[i];
							if ("image/jpeg".equalsIgnoreCase(tcFile.getProperty("mime_type"))) {
								return tcFile.getFmsFile();
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 字符串反转
	 * @param str
	 * @param firstIndex
	 * @param endIndex
	 * @return
	 */
	public static String reverse(String str, int firstIndex, int endIndex) {
		StringBuilder stringBuilder = new StringBuilder(str.length());
		stringBuilder.append(str.substring(0, firstIndex));
		for (int i = endIndex; i >= firstIndex; i--) {
			stringBuilder.append(str.charAt(i));
		}
		
		stringBuilder.append(str.substring(endIndex + 1, str.length()));
		return stringBuilder.toString();
	}
	
	
	/**
	 * 文件重命名
	 * @param oldPath
	 * @param newPath
	 */
	 public static void reNameFile(String oldPath, String newPath) {
	   File oldFile = new File(oldPath);
	   File newFile = new File(newPath);
	   if (newFile.exists()) {
		newFile.delete();
	   }
       boolean result = oldFile.renameTo(newFile);
       System.out.println("重命名的结果：" + result);
	 }
	 
	 
	 public static int getIndex(List<String> list, String value) {
			return IntStream.range(0, list.size()).filter(i -> value.equals(list.get(i))).findFirst().orElse(0);
	}
}
