package com.hh.tools.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.hh.tools.newitem.DBUtil;
import com.hh.tools.newitem.GetPreferenceUtil;
import com.teamcenter.rac.kernel.TCPreferenceService;

public class CallProgramTest {
    static DBUtil dbUtil = new DBUtil();
    static Connection conn = null;
    static Statement stmt = null;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();

//		HashMap dbInfo = getPreferenceUtil.getHashMapPreference(session,
//				TCPreferenceService.TC_preference_site, "FX_DB_Info", "=");
//		String ip = (String) dbInfo.get("IP");
//		String username = (String) dbInfo.get("UserName");
//		String password = (String) dbInfo.get("Password");
//		String sid = (String) dbInfo.get("SID");
//		String port = (String) dbInfo.get("Port");
//		System.out.println("ip == "+ip);
//		System.out.println("username == "+username);
//		System.out.println("password == "+password);
//		System.out.println("sid == "+sid);
//		System.out.println("port == "+port);
        String ip = "10.203.163.132";
        String username = "FOXCONN";
        String password = "FOXCONN";
        String sid = "TCQA";
        String port = "1521";
        System.out.println("查询开始时间=="+dateFormat.format(new Date()));
        try {
            if (conn == null || conn.isClosed()) {
                conn = dbUtil.getConnection(ip, username, password, sid, port);
            }
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            String sql;
            // 若记录不存在则新建记录	
            sql = "select * from HH_PROGRAM_USER where POS_USERNAME = 'F2760367'";
            System.out.println("sql1 == " + sql);
//			fileStreamUtil.writeData(printStream, "sql1 == "+sql);
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                System.out.println("==" + rs.getString("PPROJECT_NAME"));
                System.out.println("==" + rs.getString("POBJECT_NAME"));
                System.out.println("==" + rs.getString("PPRG0CUSTOMER"));
            }
            System.out.println("查询结束时间=="+dateFormat.format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
