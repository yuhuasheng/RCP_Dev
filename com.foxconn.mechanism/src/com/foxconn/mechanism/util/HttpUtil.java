package com.foxconn.mechanism.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.HashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class HttpUtil {
	
	public static void main(String[] args) throws IOException {
//		String result = sendGet("http://10.203.163.43/tc-integrate/pnms/getHHPNInfo", "hhpn=7B175C400-HV7-G");  
//		String result = post("http://10.203.163.43/tc-integrate/pnms/getHHPNInfo", "hhpn=7B175C400-HV7-G");
		String result = httpGet("http://10.203.163.43/tc-integrate/pnms/getHHPNInfo", "hhpn=7B175C400-HV7-G"); 
    	System.out.println(result);
	}
	
	
    public static String post(String actionUrl, String params) throws IOException
    {
    	String serverURL = actionUrl;
        StringBuffer sbf = new StringBuffer();
        String strRead = null;
        URL url = new URL(serverURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");// 请求post方式
        connection.setDoInput(true);
        connection.setDoOutput(true);
        // header内的的参数在这里set
        // connection.setRequestProperty("key", "value");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.connect();
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        // body参数 放这釿
        writer.write(params);
        writer.flush();
        InputStream is = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        while ((strRead = reader.readLine()) != null)
        {
            sbf.append(strRead);
            sbf.append("\r\n");
        }
        reader.close();
        connection.disconnect();
        String results = sbf.toString();
        return results;
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
			System.out.println("params: " + params);
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
     * 发送get请求
     * @param url    路径
     * @return
     */
    public static String httpGet(String url, String param){
    	//get请求返回结果
        String strResult = "";

        try {
        	String realUrl = url + "?" + param;           
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(realUrl);
            HttpResponse response = client.execute(request);

          //请求发送成功，并得到响应
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            	//读取服务器返回过来的json字符串数据
                strResult = EntityUtils.toString(response.getEntity());
              //把json字符串转换成json对象
                //JSONObject jsonResult = JSONObject.fromObject(strResult);
                url = URLDecoder.decode(url, "UTF-8");
            }
        }
        catch (IOException e) {
        	e.printStackTrace();
        	strResult = "failure";
            System.out.println("get�����ύʧ��:" + url);
        }

        return strResult;
    }
    
    
	public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try
        {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
         // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
         // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接 connection.connect();
            // 获取所有响应头字段
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                result += line;
            }
        }
        catch (Exception e)
        {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
            return "failure";
        }
        //使用finally块来关闭输入流
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
            }
            catch (Exception e2)
            {
                e2.printStackTrace();
            }
        }
        return result;
    }
	 
}
