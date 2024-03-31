package com.teamcenter.rac.workflow.commands.resume;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;

import com.hh.fx.rewrite.util.CheckUtil;
import com.hh.fx.rewrite.util.DBUtil;
import com.hh.fx.rewrite.util.GetPreferenceUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.log.Debug;

public class UserResumeOperation extends AbstractAIFOperation {
	private TCComponentTask[] resumeTargets = null;
	private String commentsStr = null;
	private boolean isFindWorkflow = false;
	private String finishDateValue = "";
	private String preFinishDateValue = "";
	private TCSession session = null;
	private TCComponentTask targetTask = null;

	public UserResumeOperation(AIFDesktop paramAIFDesktop, TCComponentTask[] paramArrayOfTCComponentTask,
			String paramString) {
		System.out.println("UserResumeOperation 1");
		resumeTargets = paramArrayOfTCComponentTask;
		commentsStr = paramString;
		session = resumeTargets[0].getSession();
	}

	public UserResumeOperation(AIFDesktop desktop, TCComponentTask[] paramArrayOfTCComponentTask, String paramString,
			boolean isFindWorkflow, UserResumeDialog userResumeDialog) {
		// TODO Auto-generated constructor stub
		System.out.println("UserResumeOperation 2");
		resumeTargets = paramArrayOfTCComponentTask;
		commentsStr = paramString;
		this.isFindWorkflow = isFindWorkflow;
		finishDateValue = userResumeDialog.finishDateValue;
		System.out.println("finishDateValue ==" + finishDateValue);
		preFinishDateValue = userResumeDialog.preFinishDateValue;
		System.out.println("preFinishDateValue ==" + preFinishDateValue);
		session = resumeTargets[0].getSession();
		targetTask = userResumeDialog.targetTask;
	}

	public void executeOperation() throws Exception {
		for (int i = 0; i < resumeTargets.length; i++) {
			try {
				resumeTargets[i].performAction(7, commentsStr);
				if (Debug.isOn("performaction")) {
					Debug.println("====> After perform RESUME action...");
				}

			} catch (TCException localTCException) {
				MessageBox localMessageBox = new MessageBox(localTCException);
				localMessageBox.setModal(true);
				localMessageBox.setVisible(true);
				Debug.printStackTrace("performaction", localTCException);
				throw localTCException;
			}
		}

		// 后处理 - 完成时间处理
		doPerformTaskTable();

	}

	private void doPerformTaskTable() {
		// TODO Auto-generated method stub
		try {
			if (isFindWorkflow && targetTask != null) {
				System.out.println("finishDateValue == " + finishDateValue);
				System.out.println("preFinishDateValue == " + preFinishDateValue);
				DBUtil dbUtil = new DBUtil();
				GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
				HashMap<String, String> dbInfo = getPreferenceUtil.getHashMapPreference(session,
						TCPreferenceService.TC_preference_site, "FX_DB_Info", "=");

				Connection conn = dbUtil.getConnection(dbInfo.get("IP"), dbInfo.get("UserName"), dbInfo.get("Password"),
						dbInfo.get("SID"), dbInfo.get("Port"));

				String findSql = "SELECT * FROM PERFORMTASKTABLE WHERE PUID = ?";
				String insertSql = "INSERT INTO PERFORMTASKTABLE VALUES(?,?,?,?,?,?)";
				String updateSql = "UPDATE PERFORMTASKTABLE SET ADVANCETIME = ?,COMPLETETIME = ?,STARTDATE = ?,TASKSTATUS = NULL"
						+ " WHERE PUID = ?";
				String deleteSql = "DELETE PERFORMTASKTABLE WHERE PUID = ?";

				PreparedStatement findStmt = conn.prepareStatement(findSql);
				PreparedStatement inserStmt = conn.prepareStatement(insertSql);
				PreparedStatement updateStmt = conn.prepareStatement(updateSql);
				PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);

				String uid = targetTask.getUid();
				System.out.println("UID == " + uid);

				findStmt.setString(1, uid);
				ResultSet rs = findStmt.executeQuery();

				if (rs.next()) { // 有数据
					if ("".equals(finishDateValue) && "".equals(preFinishDateValue)) {
						deleteStmt.setString(1, uid);
						deleteStmt.executeUpdate();
					} else {
						updateStmt.setString(1, preFinishDateValue);
						updateStmt.setString(2, finishDateValue);
						updateStmt.setDate(3, new java.sql.Date(new Date().getTime()));
						updateStmt.setString(4, uid);
						updateStmt.executeUpdate();
					}
				} else { // 无数据
					if ("".equals(finishDateValue) && "".equals(preFinishDateValue)) {

					} else {
						inserStmt.setString(1, uid);
						inserStmt.setString(2, resumeTargets[0].getProperty("object_name"));
						inserStmt.setDate(3, new java.sql.Date(new Date().getTime()));
						inserStmt.setString(4, preFinishDateValue);
						inserStmt.setString(5, finishDateValue);
						inserStmt.setString(6, "");
						inserStmt.executeUpdate();
					}
				}

				if (rs != null) {
					rs.close();
				}

				if (findStmt != null) {
					findStmt.close();
				}

				if (deleteStmt != null) {
					deleteStmt.close();
				}

				if (updateStmt != null) {
					updateStmt.close();
				}

				if (inserStmt != null) {
					inserStmt.close();
				}

				if (conn != null) {
					conn.close();
				}

				CheckUtil.setByPass(true, session);
				resumeTargets[0].setProperty("fx8_CompleteTime", finishDateValue);
				resumeTargets[0].setProperty("fx8_PreCompleteTime", preFinishDateValue);
				CheckUtil.setByPass(false, session);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/*
 * Location:
 * Z:\工作硬盘\项目代码\开发库\TC12.3\plugins\com.teamcenter.rac.tcapps_12000.3.0.jar
 * Qualified Name: com.teamcenter.rac.workflow.commands.resume.ResumeOperation
 * Java Class Version: 8 (52.0) JD-Core Version: 0.7.1
 */