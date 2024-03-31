package com.foxconn.electronics.setMailGroup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.alibaba.fastjson.JSONArray;
import com.foxconn.mechanism.util.HttpUtil;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
/**
 * 
 * @author wt00110
 *    物料轉廠
 */
public class SetMailGroupHandler extends AbstractHandler {
	AbstractAIFUIApplication app = null;
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {	
		Registry reg = null;
		app = AIFUtility.getCurrentApplication();
		reg = Registry.getRegistry("com.foxconn.electronics.setMailGroup.setMailGroup");
		InterfaceAIFComponent[] aifComs = app.getTargetComponents();
		
	
	     TCSession session=(TCSession)app.getSession();
		 String url="";
		  //通過首選項獲取url				  
	     url=TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
	     try {
	    	 String bu="DT";
	    	 String gn=session.getCurrentGroup().getFullName();
			 if(gn.contains("Monitor")) {
					bu="MNT";
			 }else if(gn.contains("DeskTop")) {
					bu="DT";
			 }else if(gn.contains("Printer")) {
					bu="PRT";
			 }
	        String userId =session.getUser().getUserId();	
		    new SetMailGroupDialog(app,null,url+"/MailGroup?&empId="+userId+"&bu="+bu);
	     }catch(Exception e) {}
		
	
		return null;
	}
	
	
}
