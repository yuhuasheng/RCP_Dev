package com.foxconn.sdebom.batcheditorebom.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.sdebom.batcheditorebom.dialog.BatchEditorEbomDialog;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.pse.AbstractPSEApplication;

public class BatchEditorEbomHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		System.out.println("************ BatchEditorEbomHandler ************");
		AbstractPSEApplication pseApp = (AbstractPSEApplication) AIFUtility.getCurrentApplication();
		Shell shell = pseApp.getDesktop().getShell();		
		new BatchEditorEbomDialog(pseApp, shell);		
		return null;
	}

}
