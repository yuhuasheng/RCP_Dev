package com.foxconn.electronics.fw.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.electronics.fw.action.RelationFWAction;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class RelationFWHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		RelationFWAction action = new RelationFWAction(app, null, null);
		new Thread(action).start();
		return null;
	}

}
