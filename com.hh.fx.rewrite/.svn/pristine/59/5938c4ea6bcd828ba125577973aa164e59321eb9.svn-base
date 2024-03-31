package com.teamcenter.rac.workflow.commands.newprocess;

import java.beans.PropertyChangeEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

import com.hh.fx.rewrite.util.CheckUtil;
import com.hh.fx.rewrite.util.DBUtil;
import com.hh.fx.rewrite.util.GetPreferenceUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.workflow.commands.assignmentlist.AssignmentListPanel;
import com.teamcenter.rac.workflow.commands.assignmentlist.FinishTimeStorage;

public class UserNewProcessOperation extends NewProcessOperation {

	DBUtil  dbUtil = new DBUtil();
	GetPreferenceUtil getPreferenceUtil = new  GetPreferenceUtil();
	public UserNewProcessOperation(NewProcessDialog arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
		System.out.println("UserNewProcessOperation 1");
	}

	public UserNewProcessOperation(TCSession arg0, AIFDesktop arg1,
			String arg2, String arg3, TCComponentTaskTemplate arg4,
			TCComponent[] arg5, int[] arg6) {
		super(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
		// TODO Auto-generated constructor stub
		System.out.println("UserNewProcessOperation 2");
	}

	
	@Override
	public void executeOperation() throws Exception {
		 //TODO Auto-generated method stub
		TCSession localTCSession = (TCSession)getSession();
	    try
	    {
	    	System.out.println("executeOperation");
	    	System.out.println("processName ==" + processName);
	    	System.out.println("processTemplate ==" + processTemplate);
	      createNewProcess(localTCSession, processName, processDescription, processTemplate, attComps, attTypes);
	      HashMap<String, FinishTimeStorage> finishTimeMap = AssignmentListPanel.getFinishTimeMap();
	      
	      System.out.println("newProcess == "+newProcess);
	      System.out.println("newProcess type == "+newProcess.getTypeComponent().getTypeName());
	      System.out.println("finishTimeMap.size() == "+finishTimeMap.size());
	      
	      TCProperty rootTaskP = newProcess.getTCProperty("root_task");
	      TCComponent rootTask = rootTaskP.getReferenceValue();
	      TCProperty prop = rootTask.getTCProperty("child_tasks");
	      TCComponent[] coms = prop.getReferenceValueArray();
	      
	      FinishTimeStorage finishTimeStorage = null;
	      String taskTypeName = "";
	      String finishDateValue = "";
	      String preFinishDateValue = "";
	      
	      String childTaskTypeName = "";
	      String childTaskName = "";
	      
	      HashMap<String,String> dbInfo = getPreferenceUtil.getHashMapPreference(localTCSession, TCPreferenceService.TC_preference_site, 
	    		  "FX_DB_Info", "=");
	      
	      Connection conn = dbUtil.getConnection(dbInfo.get("IP"), dbInfo.get("UserName"), dbInfo.get("Password"),
	    		  dbInfo.get("SID"), dbInfo.get("Port"));
	      
	      String sql = "INSERT INTO PERFORMTASKTABLE VALUES(?,?,?,?,?,?)";
	      PreparedStatement stmt = conn.prepareStatement(sql);
	     
	      boolean isFirst = false;
	      for(int i=0;i<coms.length;i++) {
	    	  System.out.println("coms == "+coms[i].toDisplayString());
	    	  
	    	  childTaskTypeName = coms[i].getTypeComponent().getTypeName();
	    	  System.out.println("childTaskTypeName == "+childTaskTypeName);
	    	  childTaskName = coms[i].getProperty("object_name");
	    	  System.out.println("childTaskName == "+childTaskName);
	    	  
	    	  if(finishTimeMap.containsKey(childTaskName) ) {
	    		 
	    		  finishTimeStorage =  finishTimeMap.get(childTaskName);
	    		  taskTypeName =  finishTimeStorage.getTaskType();
	    		  finishDateValue = finishTimeStorage.getFinishDateValue();
	    		  preFinishDateValue = finishTimeStorage.getPreFinishDateValue();
	    		  
	    		  System.out.println("taskTypeName == "+taskTypeName);
	    		  System.out.println("finishDateValue == "+finishDateValue);
	    		  System.out.println("preFinishDateValue == "+preFinishDateValue);
	    		  CheckUtil.setByPass(true, localTCSession);
	    		  coms[i].setProperty("fx8_CompleteTime", finishDateValue);
	    		  coms[i].setProperty("fx8_PreCompleteTime", preFinishDateValue);
	    		  CheckUtil.setByPass(false, localTCSession);
	    		  
	    		  if("".equals(finishDateValue) && "".equals(preFinishDateValue)) {
	    			  isFirst = true;
	    			  continue;
	    		  }
	    		  //开始写入数据库
	    		  if(!isFirst) {
	    			 stmt.setString(1, coms[i].getUid());
	 	    		 stmt.setString(2, childTaskName);
	 	    		 stmt.setDate(3, new java.sql.Date(new Date().getTime()));
	 	    		 stmt.setString(4, preFinishDateValue);
	 	    		 stmt.setString(5, finishDateValue);
	 	    		 stmt.setString(6, "");
	 	    		 stmt.executeUpdate();
	    		  }
	    		 isFirst = false;
	    	  }
	      }
	      
	      
	      
	      if(stmt != null) {
	    	  stmt.close();
	      }
	      
	      if(conn != null) {
	    	  conn.close();
	      }
	    }
	    catch (TCException tcexception)
	    {
            MessageBox.post(desktop, tcexception);
            int ai[] = tcexception.getErrorCodes();
            int j = ai != null ? ai[0] : 0;
            successFlag = j == 33086;
            throw tcexception;
        }
	}

	@Override
	public void createNewProcess(TCSession arg0, String arg1, String arg2, TCComponentTaskTemplate arg3,
			TCComponent[] arg4, int[] arg5) throws TCException {
		// TODO Auto-generated method stub
			try {
				super.createNewProcess(arg0, arg1, arg2, arg3, arg4, arg5);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	

}
