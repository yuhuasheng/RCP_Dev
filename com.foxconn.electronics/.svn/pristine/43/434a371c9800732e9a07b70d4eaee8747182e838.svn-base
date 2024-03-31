package com.foxconn.electronics.ftereport.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.electronics.ftereport.dialog.NonFTEReportDialog;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.ui.common.RACUIUtil;

public class NonFTEReportHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		try {
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
			String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL") + "/FteBenefitReport";
			System.out.println("fte benefit report  url :: " + url);
			new NonFTEReportDialog(app, app.getDesktop().getShell(), url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
