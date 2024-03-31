package com.foxconn.mechanism.jurisdiction;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class SendExternalEmailTest {

	public static void main(String[] args) {
		sendExternalEmail("chen.zhang@foxconn.com",
				"【Teamcenter】请『聶玲(W0101701)』登陆TC系统完成『P1012-BPS FY21 G8  Z1 TWR—Serenity（GalacticaZ）(RKL)』相关设计工作",
				"admin (admin)-003112/01;1|ID分支"); 
	}
	
	/**
	 * 发送外部邮箱
	 * @param to
	 * @param title
	 * @param content
	 */
	private static void sendExternalEmail(String to, String title, String content) {
		String state = "";
		try {
			Map<String, String> httpmap = new HashMap<String, String>();
		    httpmap.put("url", "http://10.203.163.43:80/tc-mail/teamcenter/sendMail2");
			httpmap.put("host", "10.134.28.97");
			httpmap.put("from", "cmm-it-plm@mail.foxconn.com");
			httpmap.put("to", to);
			httpmap.put("title", title);
			httpmap.put("content", content);

			String url = httpmap.get("url");
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			Gson gson = new Gson();
			String params = gson.toJson(httpmap);
			ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
			StringBody contentBody = new StringBody(params,contentType);
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);					
			builder.addPart("data", contentBody);
			HttpEntity entity = builder.build();
			httpPost.setEntity(entity);
			HttpResponse response = httpClient.execute(httpPost);
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
				HttpEntity entitys = response.getEntity();
				if (entitys != null) {
					state = EntityUtils.toString(entitys);
				}
			}
			httpClient.getConnectionManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
