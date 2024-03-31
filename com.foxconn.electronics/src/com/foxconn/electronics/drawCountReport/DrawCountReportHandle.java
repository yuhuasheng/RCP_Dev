package com.foxconn.electronics.drawCountReport;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCSession;

public class DrawCountReportHandle extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		try {
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();	
			Shell shell = app.getDesktop().getShell();
			TCSession session = (TCSession) app.getSession();
			String preference = TCUtil.getPreference(session, 4, "D9_SpringCloud_URL");		
			String url = preference + "/3DDrawingReport";
			System.out.print("============="+url);
			DrawCountReportDialog drawCountReportDialog = new DrawCountReportDialog(shell, url);
			drawCountReportDialog.initUI("DrawCountReportTitle");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
