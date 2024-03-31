package com.foxconn.mechanism.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class GetTest
{
    public static void main(String[] args)
    {
    	String result = sendGet("http://10.203.163.43/tc-integrate/pnms/getHHPNInfo", "hhpn=7B175C400-HV7-G");    	
    	System.out.println(result);
//    	Gson gson = new Gson();
//    	HashMap map = new HashMap();
//    	map = gson.fromJson(result, map.getClass());
//    	map.forEach((key, value) -> {
//            System.out.println("key=" + key + ",value=" + value);
//        });
//    	System.out.println(map);
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
            // 获取所有响应头字段
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
        // 使用finally块来关闭输入流
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
