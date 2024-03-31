package com.foxconn.notify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;

public class SendScheduleNotify {
	
	private static final Logger log = LoggerFactory.getLogger(SendScheduleNotify.class);
	private static Properties props = null;
	
	static {
		System.out.println(getPath(SendScheduleNotify.class));
		props = getProperties(getPath(SendScheduleNotify.class) + File.separator + "ConstantParams.properties");
	}
	
	
	public static void main(String[] args) {
		log.info("========== SendScheduleNotify start ==========");
		if (null == args || args.length <= 0) {
			return;
		}
		
		JSONObject params = getParams(args);
		log.info("==>> params: " + params.toJSONString());
		System.out.println("==>> params: " + params.toJSONString());
		
		String ip = props.get("ip") == null ? "" : props.get("ip").toString();
		log.info("==>> ip: " + ip);
		System.out.println("==>> ip: " + ip);
		
		String serviceName = props.get("service") == null ? "" : props.get("service").toString();
		log.info("==>> serviceName: " + serviceName);
		System.out.println("==>> serviceName: " + serviceName);
		
		String requestPath = props.get("requestPath") == null ? "" : props.get("requestPath").toString();
		log.info("==>> requestPath: " + requestPath);
		System.out.println("==>> requestPath: " + requestPath);
		
		String url = "http://" + ip + ":8354/meet/sendMeetEmail";
//		String result = HttpRequest.post(url).body(params.toJSONString()).execute().body();		
//		String url = "http://127.0.0.1:8354/meet/sendMeetEmail";
		new Thread() {
			public void run() {
				String result = HttpRequest.post(url).body(params.toJSONString()).execute().body();
				log.info("==>> result: " + result);
				System.out.println("==>> result: " + result);
			}
		}.start();
		
		log.info("========== SendScheduleNotify end ==========");
	}
	
	
	private static JSONObject getParams(String[] args) {
		JSONObject obj = new JSONObject();
		for (String str : args) {
			obj.put(str.split("=")[0], str.split("=")[1]);
		}
//		obj.put("scheduleUid", "QdiRiiea4VtjAC");
//		obj.put("scheduleName", "TCFR test0328");
//		obj.put("endDate", "2023/3/28");
//		obj.put("taskName", "測試action測試action");
		return obj;
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
}
