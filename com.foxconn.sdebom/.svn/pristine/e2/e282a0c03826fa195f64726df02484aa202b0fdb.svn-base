package com.foxconn.sdebom.dtl5ebom.handler;

import java.awt.Dimension;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.sdebom.dtl5ebom.dialog.DtL5EbomDialog;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class DtL5EbomHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		try {
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
			Shell shell = app.getDesktop().getShell();
			Dimension dim = app.getDesktop().getSize();
			new DtL5EbomDialog(app, shell, dim);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
