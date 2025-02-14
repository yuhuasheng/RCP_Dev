package com.foxconn.tcutils.util;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

public class HttpUtil {
	
	/**
    *
    * @param url 链接地址
    * @param params 填充在url中的参数
    * @param useProxy 是否使用代理
    * @param socketTimeout 超时时间
    * @param proxyHost 代理地址
    * @param proxyPort 代理端口号
    * @return
    */
   public static String httpGet(String url, String params, String useProxy, int socketTimeout, String proxyHost, String proxyPort) {
       String requestUrl = url;
       if (CommonTools.isNotEmpty(params)) {
           requestUrl = url + "?" + params;
       }
       String respData = null;
       System.out.println("httpGet req is " + params);
       HttpRequest httpRequest = HttpRequest.get(requestUrl).timeout(socketTimeout).header("token","application/json");
       if ("Y".equalsIgnoreCase(useProxy)) {
    	   System.out.println(String.format("使用代理"));
           httpRequest.setProxy(new Proxy(Proxy.Type.HTTP,
                   new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort))));
       }
       respData = httpRequest.execute().body();       
       System.out.println(String.format("HttpsUtil:httpGet | 请求信息：%s | 响应信息: %s", httpRequest.getUrl(), respData));
       return respData;
   }



   /**
    *
    * @param url 链接地址
    * @param params 填充在url中的参数
    * @param sendBodyData body
    * @param useProxy 是否使用代理
    * @param socketTimeout 超时时间
    * @param proxyHost 代理地址
    * @param proxyPort 代理端口号
    * @return
    */
   public static String httpPost(String url, String params, String sendBodyData, String useProxy, int socketTimeout, String proxyHost, String proxyPort) {
       String requestUrl = url;
       if (CommonTools.isNotEmpty(params)) {
           requestUrl = url + "?" + params;
       }
       String respData = null;
       System.out.println("httpPost req is " + sendBodyData);
       HttpRequest httpRequest = HttpRequest.post(requestUrl).timeout(socketTimeout).header("Content-Type", "application/json");
       if ("Y".equalsIgnoreCase(useProxy)) {
    	   System.out.println(String.format("使用代理"));
           httpRequest.setProxy(new Proxy(Proxy.Type.HTTP,
                   new InetSocketAddress(proxyHost, Integer.parseInt(proxyPort))));
       }
       if (CommonTools.isNotEmpty(sendBodyData)) {
           httpRequest.body(sendBodyData);
       }
       
       respData = httpRequest.execute().body();
       System.out.println(String.format("HttpsUtil:httpPost | 请求信息：%s | 响应信息: %s", httpRequest.getUrl(), respData));
       return respData;
   }
   
}
