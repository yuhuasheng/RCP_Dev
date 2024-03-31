package com.foxconn.electronics.matrixbom.handler;

import java.util.ResourceBundle;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.matrixbom.export.MatrixBOMImputDialog;
import com.foxconn.electronics.matrixbom.export.PackingMatrixImput20231117;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.ui.common.RACUIUtil;

public class MatrixBOMImputHandler extends AbstractHandler{
	
	 
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			ResourceBundle reg	= ResourceBundle.getBundle("com.foxconn.electronics.matrixbom.matrixbom_locale");
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();	
			InterfaceAIFComponent targetComponent = app.getTargetComponent();
			String matrixType = targetComponent.getProperty("d9_MatrixType");
			if("MNT Packing Matrix".equals(matrixType)) {
				Shell shell = app.getDesktop().getShell();
				
				String d9_ActualUserID = targetComponent.getProperty("d9_ActualUserID");
				if(d9_ActualUserID != null && !"".equals(d9_ActualUserID)) {
					//new PackingMatrixImput(shell,RACUIUtil.getTCSession(), app);
					new PackingMatrixImput20231117(shell,RACUIUtil.getTCSession(), app ,d9_ActualUserID);
				} else {
					MessageDialog.openInformation(shell, "提示", reg.getString("imputMessage"));
				}
				
			}else {
				new MatrixBOMImputDialog(app.getDesktop().getShell(),RACUIUtil.getTCSession(), app);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null;
	}
}
