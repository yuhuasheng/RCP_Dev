package com.foxconn.sdebom.export;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.LegacyHandlerMediator;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.ui.common.RACUIUtil;


public class HPEBOMExportHandler extends AbstractHandler{
	@Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			AbstractAIFUIApplication currentApplication = AIFUtility.getCurrentApplication();
			if(currentApplication instanceof AbstractPSEApplication) {
				AbstractPSEApplication app = (AbstractPSEApplication) currentApplication;
				Shell shell = app.getDesktop().getShell();
				TCComponentBOMWindow bomWindow = app.getBOMWindow();
				TCComponentBOMLine topBOMLine = bomWindow.getTopBOMLine();
				new HPEBOMExportDialog(shell,topBOMLine,"PSE");
			} else {
				LegacyHandlerMediator app = (LegacyHandlerMediator) currentApplication;
				Shell shell = app.getDesktop().getShell();
				TCComponent targetComponent = (TCComponent) app.getTargetComponent();
				if(targetComponent == null) {
					MessageDialog.openWarning(shell, "Warn", "请选择BOM Virtual Top Revision类型对象再执行导出操作！");
					return null;
				}
				String type = targetComponent.getType();
				if(!"D9_BOMTopNodeRevision".equals(type)) {
					MessageDialog.openWarning(shell, "Warn", "请选择BOM Virtual Top Revision类型对象再执行导出操作！");
					return null;
				}
				
				TCComponentBOMWindow createBOMWindow = TCUtil.createBOMWindow(RACUIUtil.getTCSession());
				TCComponentBOMLine topBomline = TCUtil.getTopBomline(createBOMWindow, targetComponent);
				new HPEBOMExportDialog(shell,topBomline,"My Teamcenter");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }
}
