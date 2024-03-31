package com.foxconn.mechanism.exportcreomodel;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class ExportCeroModelHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		System.out.println("========导出Creo模型Handler========");
		AbstractAIFApplication app = AIFUtility.getCurrentApplication();
		ExportCeroModelAction action = new ExportCeroModelAction(app, null, null);
		new Thread(action).start();
		return null;
	}

}
