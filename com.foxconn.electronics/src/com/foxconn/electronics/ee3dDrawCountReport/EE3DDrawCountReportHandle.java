package com.foxconn.electronics.ee3dDrawCountReport;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCSession;

public class EE3DDrawCountReportHandle extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		try {
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();	
			Shell shell = app.getDesktop().getShell();
			TCSession session = (TCSession) app.getSession();
			String preference = TCUtil.getPreference(session, 4, "D9_SpringCloud_URL");		
			String url = preference + "/EE3DDrawingReport";
			System.out.print("============="+url);
			EE3DDrawCountReportDialog drawCountReportDialog = new EE3DDrawCountReportDialog(shell, url);
			drawCountReportDialog.initUI("DrawCountReportTitle");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
