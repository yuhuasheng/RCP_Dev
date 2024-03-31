package com.origin.custom.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import com.origin.custom.handler.newitem.NewItemCustomAction;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class NewItemHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		// TODO Auto-generated method stub
		AbstractAIFUIApplication abstractAIFUIApplication = AIFUtility
				.getCurrentApplication();

		NewItemCustomAction newItemAction = new NewItemCustomAction(
				abstractAIFUIApplication, null);

		Thread t = new Thread(newItemAction);
		t.start();
		return null;
	}

}
