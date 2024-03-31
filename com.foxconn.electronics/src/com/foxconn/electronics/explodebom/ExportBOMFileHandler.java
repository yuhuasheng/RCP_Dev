package com.foxconn.electronics.explodebom;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.Registry;

public class ExportBOMFileHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		Registry reg = Registry.getRegistry("com.foxconn.electronics.explodebom.explodebom");
		
		AbstractPSEApplication app = (AbstractPSEApplication) AIFUtility.getCurrentApplication();
		Shell shell = app.getDesktop().getShell();
		InterfaceAIFComponent targetComponent = app.getTargetComponent();
		if(targetComponent == null){
			MessageDialog.openInformation(shell, reg.getString("prompt"), reg.getString("promptInfo1"));
			return null;
		}
		
		if(targetComponent instanceof TCComponentBOMLine == false){
			MessageDialog.openInformation(shell, reg.getString("prompt"), reg.getString("promptInfo1"));
			return null;
		}
		
		try {
			TCComponentBOMLine topBOMLine = app.getBOMWindow().getTopBOMLine();
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		new ExportBOMFileDialog(app, shell, reg);
		
		return null;
	}

}
