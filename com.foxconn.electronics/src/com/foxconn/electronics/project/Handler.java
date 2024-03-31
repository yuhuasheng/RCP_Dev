package com.foxconn.electronics.project;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.mechanism.util.TCUtil;
import com.plm.tc.httpService.TCHttp;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.ui.common.RACUIUtil;

public class Handler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		try {
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();	
			TCHttp http = TCHttp.startJHttp();
	        http.addHttpController(new Controller(app));
	        String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL") + "/ProjectAssigned?port="+http.getPort();
	        System.out.println(url);
			new ProjectDialog(app.getDesktop().getShell(),app,url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	

}
