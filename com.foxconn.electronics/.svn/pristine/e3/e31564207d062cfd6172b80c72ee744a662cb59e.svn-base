package com.foxconn.electronics.issuemanagement.handler;

import java.util.ResourceBundle;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.electronics.issuemanagement.dialog.SubmitToRPARunnable;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentIssueReport;
import com.teamcenter.rac.kernel.TCComponentIssueReportRevision;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class SubmitToRPAHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		ResourceBundle reg = ResourceBundle.getBundle("com.foxconn.electronics.issuemanagement.issuemanagement_locale");
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		InterfaceAIFComponent targetComponent = app.getTargetComponent();
		if (!(targetComponent instanceof TCComponentItem) && !(targetComponent instanceof TCComponentItemRevision)){
            TCUtil.warningMsgBox(reg.getString("SelectWarn1.MSG"), reg.getString("WARNING.MSG"));
            return null;
        }
		try {
			TCComponentItemRevision itemRevision = null;
			if(targetComponent instanceof TCComponentItem) {
				if(!(targetComponent instanceof TCComponentIssueReport)) {
					TCUtil.warningMsgBox(reg.getString("SelectWarn2.MSG"), reg.getString("WARNING.MSG"));
		            return null;
				}
				TCComponentItem item = (TCComponentItem)targetComponent;
				itemRevision = item.getLatestItemRevision();
			}else {
				if(!(targetComponent instanceof TCComponentIssueReportRevision)) {
					TCUtil.warningMsgBox(reg.getString("SelectWarn3.MSG"), reg.getString("WARNING.MSG"));
		            return null;
				}
				itemRevision = (TCComponentItemRevision) targetComponent;
			}
			new Thread(new SubmitToRPARunnable(itemRevision,reg)).start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
