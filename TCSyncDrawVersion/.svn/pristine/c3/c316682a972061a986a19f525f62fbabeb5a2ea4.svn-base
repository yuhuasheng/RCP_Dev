package com.foxconn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

/**
* @author 作者 Administrator
* @version 创建时间：2022年1月6日 下午2:55:28
* Description: 请求工具类
*/
public class HttpUtils {
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		HashMap httpmap = new HashMap();
//		String content = "<html><head></head><body>" 
//				+ "<div style=\"font-family: 宋体;  font-size:15px; \">"+"Dear W0109012(沈林麗) 用户，" + "</div><br/>"
//				+ "<div style=\"font-family: 宋体;  font-size:15px; \">" + "您有代办事项需要处理，请登陆下方Teamcenter賬號进行查看，谢谢！" +"</div>" 
//				+ "<div style=\"font-family: 宋体;  font-size:15px; \">" + "<strong>Teamcenter账号：</strong>"+"tcadmin" + "</div>"  
//				+ "<div style=\"font-family: 宋体;  font-size:15px; \">" + "<strong>时间表名称：</strong>"+"TCFR test0328" + "</div>" 
//				+ "<div style=\"font-family: 宋体;  font-size:15px; \">" + "<strong>时间表任务：</strong>"+"第三個任務" + "</div>" 
//				+ "<div style=\"font-family: 宋体;  font-size:15px; \">" + "<strong>Due date：</strong>"+"2023/3/28" + "</div>" 
//				+ "<div style=\"font-family: 宋体;  font-size:15px; \">" + "<strong>任务路径:</strong>"+"D事業群企業知識庫/專案知識庫" + "</div>" 
//				+ "<div style=\"font-family: 宋体;  font-size:15px; \">" + "<strong>专案系列:</strong>"+"AIC" + "</div>" 
//				+ "<div style=\"font-family: 宋体;  font-size:15px; \">" + "<strong>专案名:</strong>"+"CAC 3" + "</div>" 
//				+ "<div style=\"font-family: 宋体;  font-size:15px; \">" + "<strong>专案阶段:</strong>"+"P2" + "</div>" 
//				+ "</body></html>";
		
//		String content = "<html><head></head><body><h3 style=\"font-family: 宋体;  font-size:15px;\">" + "<font> "+"Dear W0109012(沈林麗) 用户，" + "</font>" 
//		+ "<br>" + "<p> "+"您有代办事项需要处理，请登陆下方Teamcenter賬號进行查看，谢谢！" +"</p>" + "<br>" + "Teamcenter账号：" + "<p>"+"tcadmin" + "</p>"+ "<br>" + "时间表名称: TCFR test0328" + "<br>" + "时间表任务: 第三個任務" + "<br>" + "任务路径: D事業群企業知識庫/專案知識庫" 
//				+ "<br>" + "专案系列: AIC" + "<br>" + "专案名: CAC 3" + "<br>" + "专案阶段: P2" + "</h3></body></html>";
		String content = "<html><head></head><body><h3 style=\"color:#FF0000; font-family=黑体\">富士康武汉科技园</h3></body></html>";
		httpmap.put("requestPath", "http://10.203.163.243:"); 
		httpmap.put("ruleName", "80/tc-mail/teamcenter/sendMail3");
//		httpmap.put("requestPath", "http://localhost:"); 
//		httpmap.put("ruleName", "80/tc-mail/teamcenter/sendMail3");
//		httpmap.put("ruleName", "8821/teamcenter/sendMail3");
		httpmap.put("sendTo","eva.xy.wang@foxconn.com");
		httpmap.put("sendCc","hua-sheng.yu@foxconn.com");
		httpmap.put("subject","43测试主题");
		httpmap.put("htmlmsg",content);
		httpmap.put("fromName","富士康科技有限公司");	
		List<String> attachmentList = new ArrayList<String>();
//		attachmentList.add("D:\\maxnerva\\TC\\51CTO&時代光華課程學習SOP20220106.V7.pdf");
		
//		httpmap.put("requestPath", "http://127.0.0.1:8320"); 
//		httpmap.put("ruleName", "/spas/getTeamRoster");
//		httpmap.put("platformFoundIds", "sendMail3");
//		httpmap.put("sendTo","hua-sheng.yu@foxconn.com");
//		httpmap.put("sendCc","hua-sheng.yu@foxconn.com");
//		httpmap.put("subject","43测试主题");
//		httpmap.put("htmlmsg",content);
//		httpmap.put("fromName","富士康科技有限公司");	
		
		String result = httpPost(httpmap, attachmentList);
		System.out.println(result);
		
//		httpGet("http://10.203.163.243:8019/downloadFile", "fileVersionId=56381");
	}
	
	/**
	 * post请求的方式
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
	 * post请求的方式
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
//			ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
//			ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
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
     * @param url    路径
     * @return
     */
    public static String httpGet(String url, String param){
    	//get请求返回结果
        String strResult = "";

        try {
        	String realUrl = url + "?" + param;           
        	CloseableHttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(realUrl);
            HttpResponse response = client.execute(request);

          //请求发送成功，并得到响应
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            	HttpEntity entity = response.getEntity();
            	if (entity.isStreaming()) {
            		InputStream inputStream = entity.getContent();
            		System.out.println(123);
				}
            	//读取服务器返回过来的json字符串数据
//                strResult = EntityUtils.toString(response.getEntity());
              //把json字符串转换成json对象
                //JSONObject jsonResult = JSONObject.fromObject(strResult);
                url = URLDecoder.decode(url, "UTF-8");
            }
        }
        catch (IOException e) {
        	e.printStackTrace();
        	strResult = "failure";
            System.out.println("发送get请求失败:" + url);
        }

        return strResult;
    }
    
}
