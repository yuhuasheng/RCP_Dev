package com.foxconn.electronics.tclicensereport.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.electronics.tclicensereport.action.WorkDayImportAction;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class WorkDayImportHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		System.out.println("******** WorkDayImportHandler ********");
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		WorkDayImportAction action = new WorkDayImportAction(app, null, null);
		new Thread(action).start();
		return null;
	}

}
