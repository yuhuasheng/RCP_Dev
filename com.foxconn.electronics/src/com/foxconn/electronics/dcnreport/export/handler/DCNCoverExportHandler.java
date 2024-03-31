package com.foxconn.electronics.dcnreport.export.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.electronics.dcnreport.export.action.DCNCoverExportAction;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class DCNCoverExportHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		DCNCoverExportAction action = new DCNCoverExportAction(app, null, null);
		new Thread(action).start();
		return null;
	}

}
