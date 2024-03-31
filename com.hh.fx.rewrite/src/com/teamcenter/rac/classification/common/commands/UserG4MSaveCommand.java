package com.teamcenter.rac.classification.common.commands;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import com.hh.fx.rewrite.util.DBUtil;
import com.hh.fx.rewrite.util.GetPreferenceUtil;
import com.teamcenter.rac.classification.common.AbstractG4MContext;
import com.teamcenter.rac.kernel.TCClassificationService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.ics.ICSApplicationObject;
import com.teamcenter.rac.kernel.ics.ICSProperty;
import com.teamcenter.rac.kernel.ics.ICSPropertyDescription;

public class UserG4MSaveCommand extends G4MSaveCommand {

	public UserG4MSaveCommand(AbstractG4MContext paramAbstractG4MContext,
			String paramString, Boolean paramBoolean) {
		super(paramAbstractG4MContext, paramString, paramBoolean);
		// TODO Auto-generated constructor stub
		System.out.println("UserG4MSaveCommand  1 == "+paramBoolean+",paramString == "+paramString);
	}

	public UserG4MSaveCommand(AbstractG4MContext paramAbstractG4MContext,
			String paramString) {
		super(paramAbstractG4MContext, paramString);
		// TODO Auto-generated constructor stub
		System.out.println("UserG4MSaveCommand  2 == "+paramString);
	}

	@Override
	protected void postSaveSteps() {
		// TODO Auto-generated method stub
		System.out.println("m_context.getMode() == "+m_context.getMode());
		//4�޸ģ�8�½�
		
		super.postSaveSteps();
		
		if(m_context.getMode() == 4) {
			try {
				
				TCComponent com = m_context.getClassifiedComponent();
				System.out.println("com == "+com);
				
				if(!com.isTypeOf("EDAComp Revision")) {
					System.out.println("�������ͷ�EDAComp Revision��������д˲���");
					return;
				}
				
				TCSession session  = m_context.getSession();
				TCClassificationService icsServer = m_context.getClassificationService();
				ICSApplicationObject icsAppObj = null;
				try {
					icsAppObj = icsServer
							.newICSApplicationObject("ICM");
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String dbName = "";
				String tableName = "";

				ICSApplicationObject applicationObject = m_context.getICSApplicationObject();
				System.out.println("applicationObject == "+applicationObject);
				
				String className = applicationObject.getView().getClassName();
				System.out.println("className == "+className);
				
				tableName = className;
				
				String classId = applicationObject.getClassId();
				System.out.println("classId == "+classId);

				
				String[] parents = applicationObject.getParents(classId);
				
				String dbId = parents[parents.length - 1];
				System.out.println("dbId == "+dbId);
				
				
				try {
					icsAppObj.setView(dbId);
					dbName = icsAppObj.getView().getClassName();
					System.out.println("dbName == "+dbName);
					

				} catch (TCException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				
				
				
				
				String uid = com.getUid();
				System.out.println("uid == "+uid);
				
				if("".equals(dbName) || "".equals(tableName)) {
					System.out.println("���ݿ������ݿ���ϢΪ�գ��޷����в���");
					return;
				}
				
				if("".equals(uid)) 
				{
					System.out.println("����"+com+"��UID��Ϣ���޷����в���");
					return;
				}
				
				String valueName = com.getProperty("object_name");
				String partNumber = com.getProperty("item_id");
				String partVersion = com.getProperty("item_revision_id");
				System.out.println("valueName == "+valueName);
				System.out.println("partNumber == "+partNumber);
				System.out.println("partVersion == "+partVersion);
				
				//�������ݿ�
				String prefereName = "FX_"+dbName+"_DB_Info";
				GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
				HashMap<String,String> dbInfo = getPreferenceUtil.getHashMapPreference(session, TCPreferenceService.TC_preference_all, 
						prefereName, "=");
				if(dbInfo == null) {
					System.out.println("��ѡ�"+prefereName+"��δ�����Ϣ");
				}
				//1.�ж����ݿ��Ƿ��������
				DBUtil dbUtil = new DBUtil();
				Connection conn = dbUtil.getConnection(dbInfo.get("IP"), dbInfo.get("USERNAME"), 
						dbInfo.get("PASSWORD"), dbInfo.get("SID"), dbInfo.get("PORT"));
				if(conn == null) {
					System.out.println("���ݿ�����ʧ��");
					return;
				}
				
				
				//2.�ж����ݿ���Ƿ����
				
				ResultSet rs  = conn.getMetaData().getTables(null, null,  tableName, null );
				if(!rs.next()) {
					System.out.println("���ݿ�������޷����в���");
					rs.close();
					conn.close();
					return;
				}
				
				rs.close();
				
				
				//3.�ж�UID�Ƿ���ڣ����ڸ��£����������
				
				ICSProperty[] properties = applicationObject.getProperties();
				ICSPropertyDescription[] icsDescriptions = applicationObject.getView().getPropertyDescriptions();
				
				String value = "";
				int id = 0;
				String name ="";
				
				
				Statement stmt = conn.createStatement();
				String findUidSql = "SELECT * FROM "+tableName+" WHERE "
						+ "PART_NUMBER = '"+partNumber+"' AND VERSION = '"+partVersion+"'";
				rs = stmt.executeQuery(findUidSql);
				String sql = "";
				if(rs.next()) { //������
					//UPDATE TABLE SET XX='XX' AND XX = 'XX' WHERE PUID = 'XX'
					sql = "UPDATE "+tableName+" SET ";
					for(int i=0;i<properties.length;i++ ) {
						value = properties[i].getValue();
						id = properties[i].getId();
						name = icsDescriptions[i].getName();
						
						System.out.println("name == "+name);
						System.out.println("id == "+id);
						System.out.println("value == "+value);
						if("PART_NUMBER".equals(name) || "VALUE".equals(name)) {
							continue;
						}
						sql = sql + ","+ name + " = '"+value+"' ";
					}
					sql =  sql + ",VALUE = '"+valueName+"' ";
					sql = sql.replaceFirst(",", "");
					sql = sql + " WHERE PART_NUMBER = '"+partNumber+"' AND VERSION = '"+partVersion+"'";
					
				} else { //������
					//INSERT INTO TABLE('','') VALUES('','') 
					String keyNames= "(";
					String valueNames = "(";
					for(int i=0;i<properties.length;i++ ) {
						value = properties[i].getValue();
						id = properties[i].getId();
						name = icsDescriptions[i].getName();
						
						System.out.println("name == "+name);
						System.out.println("id == "+id);
						System.out.println("value == "+value);
						if("PART_NUMBER".equals(name) || "VALUE".equals(name)) {
							continue;
						}
						keyNames = keyNames + ","+name+"";
						valueNames = valueNames + ",'"+value+"'";
					}
					keyNames = keyNames + ",VALUE,PART_NUMBER,VERSION";
					keyNames = keyNames.replaceFirst(",", "");
					keyNames = keyNames + ")";
					
					valueNames = valueNames + ",'"+valueName+"','"+partNumber+"','"+partVersion+"'";
					valueNames = valueNames.replaceFirst(",", "");
					valueNames = valueNames + ")";
					
					sql = "INSERT INTO "+tableName+" "+keyNames+" VALUES "+valueNames;
				}
				
				System.out.println("sql == "+sql);
				
				stmt.executeUpdate(sql);
				
				if(stmt != null) {
					stmt.close();
				}
				
				if(rs != null) {
					rs.close();
				}
				
				if(conn != null) {
					conn.close();
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			
			
		}
	}

	
	
}
