package com.hh.fx.rewrite.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.teamcenter.rac.kernel.TCPreferenceService;

public class DBUtil {
	
	//获取数据库链接
	public Connection getConnection(String ip,String username,String password,String sid,String port) {
		String driverName = "oracle.jdbc.driver.OracleDriver";
		String urlPath = "jdbc:oracle:thin:@"+ip+":"+port+":"+sid;
		System.out.println("urlPath == "+urlPath);
		Connection conn = null;
		try {
			Class.forName(driverName);
			conn = DriverManager.getConnection(urlPath,username,password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	//获取查询集
	public Statement  getStatment(Connection conn) {
		
		Statement stmt = null;
		try {
			if(conn != null) {
				stmt = conn.createStatement();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stmt;
	}
	
	//获取结果集
	public ResultSet getResultSet(Statement stmt,String sql) throws Exception{
		ResultSet rs = null;
		
		
			if(stmt!=null) {
				rs=stmt.executeQuery(sql);
			}
			
		
		return rs;
	}
	
	//插入数据
	public void insertDB(Statement stmt,String sql) {
		try {
			if(stmt!=null) {
				stmt.executeUpdate(sql);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//设置是否自动提交
	public void setAutoCommit(Connection conn,boolean autoCommit) {
		try {
			conn.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//回滚
	public void rollback(Connection conn) {
		try {
			conn.rollback();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//提交
	public void commit(Connection conn) {
		try {
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//关闭数据库
	public void closeDB(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
