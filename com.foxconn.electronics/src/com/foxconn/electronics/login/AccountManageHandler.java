package com.foxconn.electronics.login;

import java.util.ResourceBundle;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.login.dialog.AccountManagerDialog;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;

public class AccountManageHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		ResourceBundle reg	= ResourceBundle.getBundle("com.foxconn.electronics.login.login_locale");
		AbstractAIFUIApplication app = (AbstractAIFUIApplication)AIFUtility.getCurrentApplication();
		TCSession session = (TCSession) app.getSession();
		Shell shell = app.getDesktop().getShell();
		try {
			String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site,"D9_SpringCloud_URL")
					+"/accountManage";
			new AccountManagerDialog(shell, session, url, reg);
		} catch (Exception e) {
			
		}
		return null;
	}

}
