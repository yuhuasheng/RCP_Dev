package com.foxconn.mechanism.custommaterial.custommaterialbatchimport;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PlatformUI;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.pse.AbstractPSEApplication;

public class CustomMaterialBatchImportHandler  extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
//		try {
//			PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(true, true, m -> {
//			    m.beginTask("export part list " + "", IProgressMonitor.UNKNOWN);
			    try {
					new CustomMaterialBatchImportDialog(app, app.getDesktop().getShell());
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//			    m.done();
//			});
//		} catch (InvocationTargetException | InterruptedException e) {
//	    	 e.printStackTrace();
//		}
		 
		return null;
	}

}
