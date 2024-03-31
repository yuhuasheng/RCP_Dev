package com.foxconn.mechanism.dtpac.matmaintain.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.mechanism.dtpac.matmaintain.dialog.PACMatMaintainDialog;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.pse.AbstractPSEApplication;

public class PACMatMaintainHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AbstractPSEApplication app = (AbstractPSEApplication) AIFUtility.getCurrentApplication();
		Shell shell = app.getDesktop().getShell();
		new PACMatMaintainDialog(app, shell);
		return null;
	}

}
