package com.foxconn.plm.tcapi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

/**
* @author infodba
* @version 创建时间：2021年12月27日 下午6:56:35
* @Description
*/
public class HttpUtil {
	
	
	public static void main(String[] args) throws IOException {
		Object obj = httpGet("http://10.203.163.243:8019/downloadFile?fileVersionId=5638");
//		Object obj = httpGet("http://127.0.0.1:8019/downloadFile", "fileVersionId=5638");
		InputStream inputStream = (InputStream)obj;
		String filePath = "D:\\test\\test.xlsx"; 
		String downloadFile = CommonTools.downloadFile(inputStream, "D:\\test\\", "test.xlsx");
//		CommonTools.checkDamageFile(filePath);
	}
	
	
	/**
	 * post请求的方式(不含附件)
	 * 
	 * @param map
	 * @return
	 */
	public static String httpPost(HashMap map) {
		String content = "";
		try {
			String ruleName = map.get("ruleName").toString().trim();
			String requestPath = map.get("requestPath").toString().trim();
			String url = requestPath + ruleName;
			System.out.println(url);
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			// httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
			Gson gson = new Gson();
			if (map == null) {
				System.out.println("null");  
				return "";
			}
			System.out.println("map=" + map);
			String params = gson.toJson(map);
			StringBody contentBody = new StringBody(params, CharsetUtils.get("UTF-8"));
			// 以浏览器兼容模式访问,否则就算指定编码格式,中文文件名上传也会乱码
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);	
			
			builder.addPart("data", contentBody);
			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entitys = response.getEntity();
				if (entitys != null) {
					content = EntityUtils.toString(entitys);
				}
			}
			httpClient.getConnectionManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("post request commit error" + e);
			System.out.println("post request to microservice failure, please check microservice");
		}
		return content;
	}
	
	
	/**
	 * post请求的方式(增加传递附件)
	 * 
	 * @param map
	 * @return
	 */
	public static String httpPost(HashMap map, List<String> attachmentList) {
		String content = "";
		try {
			String ruleName = map.get("ruleName").toString().trim();
			String requestPath = map.get("requestPath").toString().trim();
			String url = requestPath + ruleName;
			System.out.println(url);
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			// httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
			Gson gson = new Gson();
			if (map == null) {
				System.out.println("null");  
				return "";
			}
			System.out.println("map=" + map);
			String params = gson.toJson(map);
			StringBody contentBody = new StringBody(params, CharsetUtils.get("UTF-8"));
			// 以浏览器兼容模式访问,否则就算指定编码格式,中文文件名上传也会乱码
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			if (CommonTools.isNotEmpty(attachmentList)) {
				for (String fileName : attachmentList) {
					FileInputStream is = new FileInputStream(new File(fileName));
					/* 绑定文件参数，传入文件流和contenttype，此处也可以继续添加其他formdata参数 */
					builder.addBinaryBody("file", is, ContentType.MULTIPART_FORM_DATA,
							URLEncoder.encode(fileName.substring(fileName.lastIndexOf("\\") + 1), "utf-8"));
				}
			}						
			builder.addPart("data", contentBody);
			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entitys = response.getEntity();
				if (entitys != null) {
					content = EntityUtils.toString(entitys);
				}
			}
			httpClient.getConnectionManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("post request commit error" + e);
			System.out.println("post request to microservice failure, please check microservice");
		}
		return content;
	}

	
	/**
	 * 发送get请求
	 * @param <T>
	 * @param url
	 * @param params
	 * @return
	 */
	public static Object httpGet(String url) {
		String result = "";
		InputStream inputStream = null;
		try {			           
        	CloseableHttpClient client = HttpClients.createDefault();
        	HttpGet httpGet = new HttpGet(url);
        	
        	HttpResponse response = client.execute(httpGet);
        	if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 请求发送成功，并得到响应
        		HttpEntity entity = response.getEntity();
        		if (entity.isStreaming()) {
        			if (response.containsHeader("error")) {
        				return null;
					}        			
        			inputStream = entity.getContent();
        			return inputStream;
				} else {
					result = EntityUtils.toString(response.getEntity());
					return result;
				}
        	}
        	
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("get request commit error" + e);
			System.out.println("get request to microservice failure, please check microservice");
		}
		return null;
	}
}
