package com.foxconn.electronics.issuemanagement.handler;

import java.awt.Dimension;
import java.util.ResourceBundle;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.issuemanagement.controller.CreateIssueController;
import com.foxconn.electronics.issuemanagement.dialog.CreateIssueDialog;
import com.foxconn.tcutils.util.TCUtil;
import com.plm.tc.httpService.TCHttp;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;

import cn.hutool.core.text.StrBuilder;

public class CreateIssueHandle  extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		ResourceBundle reg	= ResourceBundle.getBundle("com.foxconn.electronics.issuemanagement.issuemanagement_locale");
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		TCSession session = (TCSession) app.getSession();
		
		Shell shell = app.getDesktop().getShell();
		Dimension dim = app.getDesktop().getSize();
		try {
			TCComponentUser user = session.getUser();
			String customer = user.getProperty("os_username");
			TCHttp http = TCHttp.startJHttp();
			String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site,"D9_SpringCloud_URL");
			//String url = "http://10.205.56.204:5173";
			StrBuilder sb = new StrBuilder(url)
					.append("/issue/createIssue").append("?port=").append(http.getPort()).append("&type=").append(customer)
					.append("&userUid=").append(user.getUid()).append("&groupUid=").append(session.getGroup().getUid());
			System.out.println(sb.toString());
			CreateIssueDialog dialog = new CreateIssueDialog(shell, dim, session, sb.toString(),reg);
			http.addHttpController(new CreateIssueController(session, reg,customer,dialog));
			dialog.initUI();
		}catch (Exception e) {
			
		}
		return null;
	}

}
