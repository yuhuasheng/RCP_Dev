package com.foxconn.electronics.convertebom;

import java.util.Arrays;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.domain.Constants;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.pse.AbstractPSEApplication;

public class ConvertEBOMHandler2 extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {		
		AbstractPSEApplication pseApp = (AbstractPSEApplication) AIFUtility.getCurrentApplication();
		Shell shell = pseApp.getDesktop().getShell();
		try {
			TCComponentBOMLine designTopBOMLine = pseApp.getTopBOMLine();
			designTopBOMLine.refresh();
			TCComponentItemRevision itemRevision = designTopBOMLine.getItemRevision();
			boolean isReleased = TCUtil.isReleased(itemRevision);
			if(!isReleased) {
				throw new Exception("当前原理图未发布！");
			}
			new ConvertEBOMDialog2(shell,pseApp,designTopBOMLine);
		} catch (Exception e) {
			MessageDialog.openInformation(shell, "提示", e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
