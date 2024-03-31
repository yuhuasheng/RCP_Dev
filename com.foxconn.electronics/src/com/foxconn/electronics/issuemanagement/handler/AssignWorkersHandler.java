package com.foxconn.electronics.issuemanagement.handler;

import java.awt.Dimension;
import java.util.ResourceBundle;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.issuemanagement.controller.AssignWorkersController;
import com.foxconn.electronics.issuemanagement.dialog.AssignWorkersDialog;
import com.foxconn.tcutils.util.TCUtil;
import com.plm.tc.httpService.TCHttp;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentIssueReportRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;

public class AssignWorkersHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		ResourceBundle reg	= ResourceBundle.getBundle("com.foxconn.electronics.issuemanagement.issuemanagement_locale");
		AbstractAIFUIApplication app = (AbstractAIFUIApplication)AIFUtility.getCurrentApplication();
		TCSession session = (TCSession) app.getSession();
		Shell shell = app.getDesktop().getShell();
		Dimension dim = app.getDesktop().getSize();
		
		InterfaceAIFComponent targetComponent = app.getTargetComponent(); // 获取选中的目标对象
        
		if (!(targetComponent instanceof TCComponentIssueReportRevision)){
            TCUtil.warningMsgBox(reg.getString("SelectWarn3.MSG"), reg.getString("WARNING.MSG"));
            return null;
        }
		TCComponentItemRevision itemRevision = (TCComponentItemRevision) targetComponent;
        try {
        	String property = itemRevision.getProperty("is_modifiable");
        	if("否".equals(property)) {
        		TCUtil.warningMsgBox(reg.getString("SelectWarn4.MSG"), reg.getString("WARNING.MSG"));
                return null;
        	}
        	TCComponentUser user = session.getUser();
			String customer = user.getProperty("os_username");
        	TCHttp http = TCHttp.startJHttp();
			http.addHttpController(new AssignWorkersController(session, reg, itemRevision,customer));
			String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site,"D9_SpringCloud_URL") +"/issue/PersonnelAssignment?port="+ http.getPort();
			//String url = "http://10.203.163.43/issue/PersonnelAssignment?port=" + http.getPort();
			AssignWorkersDialog dialog = new AssignWorkersDialog(shell, dim, session, url,reg);
			dialog.initUI();
		} catch (Exception e) {
		  e.printStackTrace(); 
		}
		return null;
	}

}
