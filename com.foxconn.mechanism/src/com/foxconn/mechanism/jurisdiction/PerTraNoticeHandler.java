package com.foxconn.mechanism.jurisdiction;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.pse.AbstractPSEApplication;

public class PerTraNoticeHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		Registry reg = Registry.getRegistry("com.foxconn.mechanism.jurisdiction.jurisdiction");
		AbstractPSEApplication pseApp = (AbstractPSEApplication) AIFUtility.getCurrentApplication();
		Shell shell = pseApp.getDesktop().getShell();
		InterfaceAIFComponent targetComponent = pseApp.getTargetComponent();
		if(targetComponent == null){
			MessageDialog.openInformation(shell, reg.getString("prompt"), reg.getString("promptInfo1"));
			return null;
		}
		if(targetComponent instanceof TCComponentBOMLine == false){
			MessageDialog.openInformation(shell, reg.getString("prompt"), reg.getString("promptInfo1"));
			return null;
		}
		new PerTraNoticeDialog(pseApp, shell, reg);
		return null;
	}

}
