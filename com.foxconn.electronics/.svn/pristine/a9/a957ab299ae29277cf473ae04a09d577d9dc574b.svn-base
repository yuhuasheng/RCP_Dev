package com.foxconn.electronics.L10Ebom.handler;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.L10Ebom.action.L10EBOMApplyFormAction;
import com.foxconn.electronics.L10Ebom.dialog.L10EBOMApplyFormDialog;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class L10EBOMApplyFormHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {	
//		System.out.println(arg0);
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
//		L10EBOMApplyFormAction action = new L10EBOMApplyFormAction(app, null, source.toString());
//		new Thread(action).start();
		Shell shell = app.getDesktop().getShell();		
		new L10EBOMApplyFormDialog(app, shell, null);
		return null;
	}

}
