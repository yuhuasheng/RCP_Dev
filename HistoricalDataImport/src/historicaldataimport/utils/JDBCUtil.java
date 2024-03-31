package historicaldataimport.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.teamcenter.rac.aif.kernel.AbstractAIFSession;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCSession;

import historicaldataimport.domain.Constants;
import historicaldataimport.domain.Project2Info;
import historicaldataimport.domain.SPASInfo;

public class JDBCUtil{

	static {
		try {
			Class.forName(Constants.JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static Connection getConnection(){
		Connection conn = null;
		try {
			TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
			String serverAddress = session.getSoaConnection().getServerAddress();
			String url = Constants.JDBC_URL_POC;
			if(serverAddress.contains("192")) {
				url = Constants.JDBC_URL_PRD;
			}
			conn = DriverManager.getConnection(url, Constants.JDBC_USER, Constants.JDBC_PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static List<Project2Info> queryProjectInfo(List<String> spasIds){
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Project2Info> project2InfoList = new ArrayList<Project2Info>();
		
		StringBuilder params = new StringBuilder();
		for (int i = 0; i < spasIds.size(); i++) {
			if(i == 0) {
				params.append("?");
			}else {
				params.append(",?");
			}
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT TAB5.spas_id,TAB5.project_name, TAB5.product_line, TAB5.series_name, TAB6.name customer_name FROM (");
		sql.append("	SELECT TAB3.spas_id,TAB3.project_name, TAB3.name product_line, TAB4.series_name , TAB4.customer_id FROM (");
		sql.append("		SELECT TAB1.spas_id,TAB1.name project_name,TAB1.project_series_id,TAB2.NAME FROM (");
		sql.append("			SELECT id spas_id,name,project_series_id,product_line_id FROM view_platform_found WHERE ID IN");
		sql.append(" (" + params.toString() + ")");
		sql.append("		) TAB1 LEFT JOIN view_product_line TAB2 ON TAB1.product_line_id = TAB2.id ");
		sql.append("	) TAB3 LEFT JOIN view_project_series TAB4 ON TAB3.project_series_id = TAB4.id");
		sql.append(") TAB5 LEFT JOIN view_customer TAB6 ON TAB5.customer_id = TAB6.id");
		
		try {
			ps = conn.prepareStatement(sql.toString());
			if(spasIds != null && spasIds.size() > 0) {
				for (int i = 0; i < spasIds.size(); i++) {
					ps.setObject(i+1, spasIds.get(i));
				}
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				Project2Info project2Info = new Project2Info();
				project2Info.setSpasId(rs.getString("spas_id"));
				project2Info.setProjectName(rs.getString("project_name"));
				project2Info.setProductLine(rs.getString("product_line"));
				project2Info.setSeriesName(rs.getString("series_name"));
				project2Info.setCustomerName(rs.getString("customer_name"));
				project2InfoList.add(project2Info);
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
		return project2InfoList;
	}
	
	public static Map<String, List<String>> queryProjectPhase(List<String> spasIds){
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, List<String>> projectPhaseMap = new HashMap<String, List<String>>();
		
		StringBuilder params = new StringBuilder();
		for (int i = 0; i < spasIds.size(); i++) {
			if(i == 0) {
				params.append("?");
			}else {
				params.append(",?");
			}
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT TAB1.project_id,TAB2.phase_sn FROM (");
		sql.append("	SELECT project_id,phase_id FROM view_project_schedule WHERE project_id IN");
		sql.append(" (" + params.toString() + ")");
		sql.append(") TAB1 LEFT JOIN  view_product_line_phase TAB2 ON TAB1.phase_id = TAB2.id");
		
		try {
			ps = conn.prepareStatement(sql.toString());
			if(spasIds != null && spasIds.size() > 0) {
				for (int i = 0; i < spasIds.size(); i++) {
					ps.setObject(i+1, spasIds.get(i));
				}
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				String projectId = rs.getString("project_id");
				String phase = rs.getString("phase_sn");
				if(projectPhaseMap.containsKey(projectId)) {
					List<String> phaseList = projectPhaseMap.get(projectId);
					phaseList.add(phase);
				}else {
					List<String> phaseList = new ArrayList<String>();
					phaseList.add(phase);
					projectPhaseMap.put(projectId, phaseList);
				}
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
		return projectPhaseMap;
	}
	
	public static List<SPASInfo> querySpasInfo(String sql, List<String> params, String syncType){
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<SPASInfo> spasInfos = new ArrayList<SPASInfo>();
		
		try {
			ps = conn.prepareStatement(sql.toString());
			if(params != null && params.size() > 0) {
				for (int i = 0; i < params.size(); i++) {
					ps.setObject(i+1, params.get(i));
				}
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				SPASInfo spasInfo = new SPASInfo();
				String prefix = "s";
				if(syncType.equals(Constants.PROJECT_TYPE)) {
					prefix = "p";
				}
				spasInfo.setSpasId(prefix + rs.getString("id"));
				spasInfo.setName(rs.getString("name"));
				spasInfos.add(spasInfo);
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
		return spasInfos;
	}
	
	public static List<SPASInfo> queryProject2Info(List<String> spasIds){
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<SPASInfo> project2InfoList = new ArrayList<SPASInfo>();
		
		StringBuilder params = new StringBuilder();
		for (int i = 0; i < spasIds.size(); i++) {
			if(i == 0) {
				params.append("?");
			}else {
				params.append(",?");
			}
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ID, NAME FROM view_platform_found WHERE ID IN");
		sql.append(" (" + params.toString() + ")");
		
		try {
			ps = conn.prepareStatement(sql.toString());
			if(spasIds != null && spasIds.size() > 0) {
				for (int i = 0; i < spasIds.size(); i++) {
					ps.setObject(i+1, spasIds.get(i));
				}
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				SPASInfo project2Info = new SPASInfo();
				project2Info.setSpasId("p" + rs.getString("id"));
				project2Info.setName(rs.getString("name"));
				project2InfoList.add(project2Info);
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
		return project2InfoList;
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
