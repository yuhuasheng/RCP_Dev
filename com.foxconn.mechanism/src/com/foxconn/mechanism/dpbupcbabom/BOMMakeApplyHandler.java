package com.foxconn.mechanism.dpbupcbabom;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class BOMMakeApplyHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		System.out.println("************ DPBU PCBA BOM 制作申请 Handler ************");
		String functionName = (String) arg0.getParameters().get("function_name");
		System.out.println("==>> functionName: " + functionName);		
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		BOMMakeApplyAction action = new BOMMakeApplyAction(app, null, null, functionName);
		new Thread(action).start();
		return null;
	}

}
