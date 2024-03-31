package com.foxconn.startUp;

import java.net.URL;
import java.net.URLConnection;
import com.foxconn.teamcenter.clientx.AppXSession;
import com.foxconn.util.CommonTools;
import com.teamcenter.soa.client.model.strong.User;

public class StartUp {

	private static AppXSession session = null;

	private static User user = null;

	private boolean isLogin = false;	

	private String tc_IP = ""; // TC服务器ip

	private String tc_USERNAME = ""; // TC登录用户名

	private String tc_PASSWORD = ""; // TC登录密码

	public StartUp(String tc_IP, String tc_USERNAME, String tc_PASSWORD) {
		this.tc_IP = tc_IP;
		this.tc_USERNAME = tc_USERNAME;
		this.tc_PASSWORD = tc_PASSWORD;
		// 连接TC
		connectTC();
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}	

	/**
	 * 连接TC
	 */
	private void connectTC() {
		System.out.println("【INFO】登录TC系统中...");
		// 1、判断网页是否连通
		try {
			URL url = new URL(tc_IP);
			URLConnection in = url.openConnection();
			in.connect();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("【ERROR】 登录TC系统失败，TC12连接地址:" + tc_IP + ", 链接失败，请联系管理员！");
			try {
				throw new Exception("【ERROR】登录TC系统失败，TC12连接地址:" + tc_IP + ", 链接失败，请联系管理员！");
			} catch (Exception e1) {
				e1.printStackTrace();
				System.err.println(CommonTools.getExceptionMsg(e1));
			}
		}
		session = new AppXSession(tc_IP);
		user = session.mylogin(tc_USERNAME, tc_PASSWORD, "", "");
		if (null == user) {
			System.err.println("【ERROR】 登录TC系统失败，请联系管理员！");
			try {
				throw new Exception("【ERROR】 登录TC系统失败，请联系管理员！");
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(CommonTools.getExceptionMsg(e));
			}
		} else {
			isLogin = true;
			System.out.println("【INFO】 通过SOA登录TC系统完毕....");
		}
	}

	/**
	 * 登出TC系统
	 */
	public void disconnectTC() {
		System.out.println("【INFO】 登出TC系统成功...");
		session.logout();
	}

	public String getTc_IP() {
		return tc_IP;
	}

	public String getTc_USERNAME() {
		return tc_USERNAME;
	}

	public String getTc_PASSWORD() {
		return tc_PASSWORD;
	}

}
