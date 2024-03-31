package com.foxconn.mechanism.batchDownloadDataset;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.pse.AbstractPSEApplication;

public class BatchDownloadDatasetHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		
		Registry reg = Registry.getRegistry("com.foxconn.mechanism.batchDownloadDataset.batchDownloadDataset");
		
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
		
		try {
			TCComponentBOMLine topBOMLine = pseApp.getBOMWindow().getTopBOMLine();
			String projectId = topBOMLine.getItemRevision().getProperty("project_ids");
			System.out.println("projectIds--->" + projectId);
		} catch (TCException e) {
			e.printStackTrace();
		}

		new BatchDownloadDatasetDialog(pseApp, shell, reg);
		return null;
	}

}
