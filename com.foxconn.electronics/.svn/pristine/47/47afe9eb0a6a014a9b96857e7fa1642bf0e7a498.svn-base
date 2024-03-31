package com.foxconn.electronics.tclicensereport.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCSession;

public class TcLicenseHistoryReportHandle extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		try {
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();	
			Shell shell = app.getDesktop().getShell();
			TCSession session = (TCSession) app.getSession();
			String preference = TCUtil.getPreference(session, 4, "D9_SpringCloud_URL");		
			String url = preference + "/LicenseVisualizationBoard?isHistory=1";
			System.out.println(url);
			TcLicenseReportDialog licenseReportDialog = new TcLicenseReportDialog(shell, url);
			licenseReportDialog.initUI("HistoryLicenseTitle");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
