package com.foxconn.dp.plm.syncdoc.teamcenter.clientx;

import java.net.URL;
import java.net.URLConnection;

import com.teamcenter.soa.client.model.strong.User;

public class TCSystem {

	public static AppXSession loginTC(String host,String username,String password) throws Exception {		
        try {
        	URL url = new URL(host);
            URLConnection in = url.openConnection();
            in.connect();
        } catch (Exception e) {
        	throw new Exception(e.getMessage());
        }
        AppXSession session = new AppXSession(host);
        User user = session.myLogin(username, password);
        if (user == null) {
        	throw new Exception("tc login failed...");
        }
        System.out.println("tc login successful...");
        return session;
    }
	
	public static void logoutTC(AppXSession session) {
        if (session != null) {
            session.logout();
            System.out.println("tc logout successful...");
        }
    }
}
