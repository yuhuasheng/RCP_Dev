package com.foxconn.electronics.managementebom.export.bom.mnt;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class MntBomExportHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent arg0) {
		 AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		 MntBomExportAction action = new MntBomExportAction(app, null, null);
	     new Thread(action).start();
	     return null;
	}
}
