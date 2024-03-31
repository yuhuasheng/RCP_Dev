package com.foxconn.mechanism.digitalsignature;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class DigitalSignaturelCancleHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		
		AbstractAIFApplication app = AIFUtility.getCurrentApplication();
		DigitalSignatureCancleAction action = new DigitalSignatureCancleAction(app, null, null);
		new Thread(action).start();
		return null;
	}

}
