package com.foxconn.convert.config;


import java.net.URL;
import java.net.URLConnection;

import com.foxconn.plm.tcapi.service.ITCSOAClientConfig;
import com.foxconn.plm.tcapi.soa.client.AppXSession;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.strong.User;

public class TCSOAClientConfigImpl implements ITCSOAClientConfig {
	
	private AppXSession session;
	
	private User user;    
	    
	private String tc_IP = ""; // TC服务器ip

	private String tc_USERNAME = ""; // TC登录用户名

	private String tc_PASSWORD = ""; // TC登录密码
	
	
	public TCSOAClientConfigImpl(String tc_IP, String tc_USERNAME, String tc_PASSWORD) {
		super();
		this.tc_IP = tc_IP;
		this.tc_USERNAME = tc_USERNAME;
		this.tc_PASSWORD = tc_PASSWORD;
		initTCConnect();
	}

	public void initTCConnect() {
		System.out.println("【INFO】登录TC系统中...");
		try {
			URL url = new URL(tc_IP);
			URLConnection in = url.openConnection();
			in.connect();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("【ERROR】 登录TC系统失败，TC12连接地址:" + tc_IP + ", 链接失败，请联系管理员！");
			try {
				throw new Exception("【ERROR】登录TC系统失败，TC12连接地址:" + tc_IP + ", 链接失败，请联系管理员！");
			} catch (Exception e1) {
				e1.printStackTrace();
				System.out.println(e1.getStackTrace());
			}
		}
		
		session = new AppXSession(tc_IP);
		user = session.login(tc_USERNAME, tc_PASSWORD, "", "");	
		if (null == user) {
			System.out.println("【ERROR】 登录TC系统失败，请联系管理员！");
			try {
				throw new Exception("【ERROR】 登录TC系统失败，请联系管理员！");
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(CommonTools.getExceptionMsg(e));
			}
		} else {
			System.out.println("【INFO】 通过SOA登录TC系统完毕....");
		} 
	}
	
	
	@Override
	public Connection getConnection() {
		return session.getConnection();
	}

	@Override
	public void destroy() {
		System.out.println("【WARN】 destroy tc connection !!!");
		session.logout();
	}

}
