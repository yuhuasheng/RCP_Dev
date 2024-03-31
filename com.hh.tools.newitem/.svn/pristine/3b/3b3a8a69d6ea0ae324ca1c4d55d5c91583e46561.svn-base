package com.hh.tools.importBOM.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.hh.tools.newitem.DBUtil;
import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

public class RsscSkuBomCheckUtil {

    private Connection conn;

    private Statement stmt;

    private DBUtil dbUtil;

    public RsscSkuBomCheckUtil(TCSession session) {
        DBUtil dbUtil = new DBUtil();
        GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
        HashMap dbInfo = getPreferenceUtil.getHashMapPreference(session, TCPreferenceService.TC_preference_site,
                "HH_ModuleList_Info", "=");
        String ip = (String) dbInfo.get("IP");
        String username = (String) dbInfo.get("UserName");
        String password = (String) dbInfo.get("Password");
        String sid = (String) dbInfo.get("SID");
        String port = (String) dbInfo.get("Port");
        conn = dbUtil.getConnection(ip, username, password, sid, port);
        if (conn == null) {
        	Utils.infoMessage("Êý¾Ý¿âÁ´½ÓÊ§°Ü£¡");
        } else {
            stmt = dbUtil.getStatment(conn);
        }
    }

    public String searchRsscSkuBomPuid(String partNum) {
        ResultSet rs = null;
        try {
            String puid = null;
            String sql = "SELECT PUID FROM FX8_LXSKUPARTDREVISIONVIEW WHERE FX_PARTNUMBER = '" + partNum + "'";
            rs = dbUtil.getResultSet(stmt, sql);
            while (rs.next()) {
                puid = rs.getString("PUID");
            }
            return puid;
        } catch (Exception e) {
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public String searchRsscSkuBomStatus(String puid) {
        ResultSet rs = null;
        try {
            String status = null;
            String sql = "SELECT PNAME FROM FX_STATUSVIEW WHERE PUID = '" + puid + "'";
            rs = dbUtil.getResultSet(stmt, sql);
            while (rs.next()) {
                status = rs.getString("PNAME");
            }
            return status;
        } catch (Exception e) {
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
