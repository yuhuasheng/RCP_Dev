package com.origin.custom.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.origin.custom.handler.newitem.NewItemCustomAction;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;

public class NewItemHandler extends AbstractHandler {

	@SuppressWarnings("deprecation")
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		// TODO Auto-generated method stub
		AbstractAIFUIApplication abstractAIFUIApplication = AIFUtility
				.getCurrentApplication();

		TCSession session = (TCSession) abstractAIFUIApplication.getSession();
		
		TCUserService userService = session.getUserService();
		
		try {
			Object call = userService.call("recurse_bom", new String[] {"xYlRBmee4VtjAC"});
			System.out.println(call.toString());
		} catch (TCException e) {
			e.printStackTrace();
		}
		
//		NewItemCustomAction newItemAction = new NewItemCustomAction(
//				abstractAIFUIApplication, null);
//
//		Thread t = new Thread(newItemAction);
//		t.start();
		return null;
	}

}
