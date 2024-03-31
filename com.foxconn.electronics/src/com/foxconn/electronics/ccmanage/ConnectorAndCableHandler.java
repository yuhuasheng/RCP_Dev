package com.foxconn.electronics.ccmanage;

import java.awt.Dimension;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import com.foxconn.electronics.util.TCUtil;
import com.plm.tc.httpService.TCHttp;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCSession;


public class ConnectorAndCableHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {		
		try {
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();	
			Shell shell = app.getDesktop().getShell();
			Dimension dim = app.getDesktop().getSize();
			TCSession session = (TCSession) app.getSession();	
			TCComponentUser user = session.getUser();
			String userId = user.getProperty("user_id");
			System.out.println("==>> userId: " + userId);
			TCComponentRole currentRole = session.getCurrentRole();	
			TCComponentGroup group = session.getGroup();	
			String groupName = group.getProperty("object_string");
			System.out.println("==>> group is " + groupName);
			String roleName = currentRole.getProperty("role_name");
			System.out.println("==>> roleName is " + roleName);
			String preference = TCUtil.getPreference(session, 4, "D9_SpringCloud_URL");	
			TCHttp http = TCHttp.startJHttp();
			String url = preference + "/ConnectorAndCable?port=" + http.getPort() + "&group=" + groupName + "&userId=" + userId;
			System.out.println(url);
			ConnectorAndCableDialog connectorAndCableDialog = new ConnectorAndCableDialog(shell, dim, session, url);
	        connectorAndCableDialog.initUI();
	        http.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return null;
	}
	
	

}
