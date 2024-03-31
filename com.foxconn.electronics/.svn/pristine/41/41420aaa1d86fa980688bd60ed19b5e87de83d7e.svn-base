package com.foxconn.electronics.dcnreport.batcheditornewmoldfee.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.dialog.BatchEditorNewMoldFeeDialog;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class BatchEditorNewMoldFeeHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		Shell shell = app.getDesktop().getShell();
		new BatchEditorNewMoldFeeDialog(app, shell);
		return null;
	}

}
