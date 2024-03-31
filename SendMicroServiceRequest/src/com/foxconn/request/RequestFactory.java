package com.foxconn.request;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;



public class RequestFactory {
	private static final Logger log = LoggerFactory.getLogger(RequestFactory.class);
	
	public static void main(String[] args) {
		log.info("========== RequestFactory start ==========");
		if (null == args || args.length <= 0) {
			return;
		}
		
		JSONObject params = getParams(args);
		log.info("==>> params: " + params.toJSONString());
		
		String requestType = params.get("type") == null ? "" : params.get("type").toString();
		
		String url = getUrlRequestPath(params);
		if (null == url || "".equals(url)) {
			return;
		}
		log.info("==>> url: " + url);
		if ("GET".equals(requestType)) {
			params.remove("type");
			new Thread() {
				public void run() {
					httpGet(params, url);
				}
			}.start();
			
		} else if ("POST".equals(requestType)) {
			params.remove("type");
			httpPost(params, url);
//			new Thread() {
//				public void run() {
//					httpPost(params, url);
//				}
//			}.start();			
		}
	}
	
	/**
	 * 获取请求路径参数
	 * @param params
	 * @param url
	 */
	private static String getUrlRequestPath(JSONObject params) {
		String url = null;
		Iterator<Entry<String, Object>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> next = it.next();
			if (next.getKey().equals("url")) {
				url = (String) next.getValue();
				it.remove();
			} 
		}
		return url;
	}
	
	
	
	private static String httpGet(JSONObject params, String url) {
		String result = null;
		try {
			String requestUrl = url;
			Iterator<Entry<String, Object>> it = params.entrySet().iterator();
			int k = 0;
			while (it.hasNext()) {
				Entry<String, Object> next = it.next();
				if (k == 0) {
					requestUrl = url + "?" + next.getKey() + "=" + next.getValue();
				} else {
					requestUrl += "&" + next.getKey() + "=" + next.getValue();
				}
			}
			
			HttpResponse response = HttpRequest.get(requestUrl).timeout(5000).header("Content-Type", "application/json").execute();
			result = response.body();
			log.info("==>> result: " + result);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("url 请求失败: " + e.getLocalizedMessage());
		}	
		return result;
	}
	
	
	/**
	 * 发送post请求
	 * @param params
	 * @param url
	 */
	private static String httpPost(JSONObject params, String url) {
		String result = null;
		try {
			Map<String, String> heads = new HashMap<String, String>(10);
			// 使用json发送请求，下面的是必须的
			heads.put("Content-Type", "application/json");
			HttpResponse response = HttpRequest.post(url).headerMap(heads, false).body(params.toJSONString()).timeout(5000).execute();
			result = response.body();
			log.info("==>> result: " + result);			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("url 请求失败: " + e.getLocalizedMessage());
		}	
		
		return result;
		
	}
	
	
	
	/**
	 * 获取请求参数
	 * @param args
	 * @return
	 */
	private static JSONObject getParams(String[] args) {
		JSONObject obj = new JSONObject();
		for (String str : args) {
			obj.put(str.split("=")[0], str.split("=")[1]);
		}
		
//		obj.put("type", "POST");
//		obj.put("url", "http://10.203.163.243/tc-integrate/meet/sendMeetEmail");
//		obj.put("scheduleUid", "SAlRyG574VtjAC");
//		obj.put("scheduleName", "TCFR test03301852");
//		obj.put("endDate", "2023/03/29");
//		obj.put("taskName", "第一个任务");
		return obj;
	}
}
