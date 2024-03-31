package com.foxconn.electronics.dcnreport.dcncostimpact;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class DCNCostImpactHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		System.out.println("************ DCNCostImpactHandler ************");
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		Shell shell = app.getDesktop().getShell();
		try {
			new DCNCostImpactDialog(shell, app);
		} catch (TCException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
