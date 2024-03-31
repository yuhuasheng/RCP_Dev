package com.foxconn.electronics.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.foxconn.electronics.domain.CableInfo;
import com.foxconn.electronics.domain.ConnectorInfo;
import com.foxconn.electronics.domain.Constants;
import com.teamcenter.rac.kernel.TCPreferenceService;

import cn.hutool.core.codec.Base64;

public class JDBCUtil{
	
	static String jdbcUrl;
	static String username;
	static String password;
	

	static {
		try {
			List<String> arrayPreference = TCUtil.getArrayPreference(TCPreferenceService.TC_preference_site,"D9_DB_XPLM");			
			for(String preference: arrayPreference) {
				if(preference.startsWith("username=")) {
					username = preference.split("username=")[1];
				}else if(preference.startsWith("password=")) {
					password = preference.split("password=")[1];
				}else if(preference.startsWith("url=")) {
					jdbcUrl = preference.split("url=")[1];
				}
			}
			username = Base64.decodeStr(username);
			password = Base64.decodeStr(password);
			jdbcUrl = Base64.decodeStr(jdbcUrl);
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static Connection getConnection(){
		Connection conn = null;
		try {
			String url = jdbcUrl;
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	@SuppressWarnings("resource")
	public static List<Object> queryCoCaInfos(String sql1,String sql2){
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Object> coCaInfos = new ArrayList<Object>();
		
		try {
			ps = conn.prepareStatement(sql1);
			rs = ps.executeQuery();
			while (rs.next()) {
				ConnectorInfo connectorInfo = new ConnectorInfo();
				connectorInfo.setId(rs.getInt("id"));
				connectorInfo.setHhPN(rs.getString("hh_pn"));
				connectorInfo.setDescription(rs.getString("description"));
				connectorInfo.setSupplier(rs.getString("supplier"));
				connectorInfo.setGroupId(rs.getInt("group_id"));
				coCaInfos.add(connectorInfo);
			}
			
			ps = conn.prepareStatement(sql2);
			rs = ps.executeQuery();
			while (rs.next()) {
				CableInfo cableInfo = new CableInfo();
				cableInfo.setId(rs.getInt("id"));
				cableInfo.setHhPN(rs.getString("hh_pn"));
				String designPN = rs.getString("design_pn");
				if (designPN == null) {
					designPN = "";
				}
				cableInfo.setDesignPN(designPN); 
				cableInfo.setDescription(rs.getString("description"));
				cableInfo.setSupplier(rs.getString("supplier"));
				cableInfo.setGroupId(rs.getInt("group_id"));
				coCaInfos.add(cableInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				closeResource(conn, ps, rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return coCaInfos;
	}
	
	public static int queryCoCaInfoCount(String sql) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int num = 0;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				num = rs.getInt("num");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}try {
			closeResource(conn, ps, rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return num;
	}
	
	public static String queryHHPNEmptyByGroupId(String sql) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String id = "";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				id = rs.getString("id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}try {
			closeResource(conn, ps, rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
		
	}
	
	public static int addDelCoCaInfo(String sql) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		int affectedRow = 0;
		try {
			ps = conn.prepareStatement(sql);
			affectedRow = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				closeResource(conn, ps, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return affectedRow;
	}
	
	public static String queryGroupIdByPN(String hhpn, String type) {

		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		String groupId = "";
		
		StringBuilder sbSql = new StringBuilder();
		if ("Connector".equals(type)) {
			sbSql.append("select group_id from connector_table where 1 = 1");
		}else if ("Cable".equals(type)) {
			sbSql.append("select group_id from cable_table where 1 = 1");
		}
		
		if (!"".equals(hhpn)) {
			String[] split = hhpn.split(";");
			sbSql.append(" and ");
			String like="";
			for(int i=0;i<split.length;i++) {
				if(i==0) {
					like += " hh_pn like ? ";
				}else {
					like +=" or hh_pn like ?";
				}
			}			
			like ="("+like+")";
			sbSql.append(like);
		}
		
		String sql = sbSql.toString();
		try {
			ps = conn.prepareStatement(sql);
			if (!"".equals(hhpn)) {
				String[] split = hhpn.split(";");
				for(int i=0;i<split.length;i++) {
					ps.setString(i+1, "%" + split[i] + "%");
				}	
			}
			rs = ps.executeQuery();
			int i = -1;
			while (rs.next()) {
				i++;
				if(i!=0) {
					groupId += ",";
				}			
				groupId += rs.getString("group_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				closeResource(conn, ps, rs);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return groupId;
	}
	
	public static int queryMaxGroupId(String sql) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int maxGroupId = 0;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				maxGroupId = rs.getInt("groupId");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}try {
			closeResource(conn, ps, rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maxGroupId;
	}
	
	public static int updateConnectorOrCable(String cable,String hhpn,String desc,String supplier) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		int maxGroupId = 0;
		try {
			String sql = "update connector_table set DESCRIPTION = ? ,SUPPLIER=? where HH_PN = ?";
			if("1".equals(cable)) {
				sql = "update cable_table set DESCRIPTION = ? ,SUPPLIER=? where HH_PN = ?";
			}
			ps = conn.prepareStatement(sql);
			ps.setString(1, desc);
			ps.setString(2, supplier);
			ps.setString(3, hhpn);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}try {
			closeResource(conn, ps,null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maxGroupId;
	}

	private static void closeResource(Connection conn, PreparedStatement ps, ResultSet rs) throws SQLException {
		if(rs != null) {
			rs.close();
		}
		if(ps != null) {
			ps.close();
		}
		if(conn != null) {
			conn.close();
		}
	}
}
