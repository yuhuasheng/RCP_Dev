package com.teamcenter.rac.issuemanager.handlers.reviewissue;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.SelectionHelper;
import com.teamcenter.rac.issuemanager.IssueHelper;
import com.teamcenter.rac.issuemanager.Messages;
import com.teamcenter.rac.issuemanager.dialogs.reviewissue.ReviewIssueDialog;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentIssueReportRevision;
import com.teamcenter.rac.util.MessageBox;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class ReviewIssueHandler extends AbstractHandler {
	
	 public ReviewIssueHandler() {
		 
	    }

	    public Object execute(ExecutionEvent var1) throws ExecutionException {
	    	System.out.println("==>> Start ReviewIssueHandler Handler");
	        InterfaceAIFComponent[] var2 = SelectionHelper.getTargetComponents(HandlerUtil.getCurrentSelection(var1));
	        boolean var3 = false;
	        if (var2 != null) {
	            InterfaceAIFComponent[] var7 = var2;
	            int var6 = var2.length;

	            for(int var5 = 0; var5 < var6; ++var5) {
	                InterfaceAIFComponent var4 = var7[var5];
	                if (var4 != null && var4 instanceof TCComponentIssueReportRevision) {
	                    if (IssueHelper.getInstance().isReadyToReview((TCComponent)var4)) {
							ReviewIssueDialog var8 = new ReviewIssueDialog(new Shell(), var4);
	                        var8.open();
	                    }

	                    var3 = true;
	                    break;
	                }
	            }
	        }

	        if (!var3) {
	            MessageBox.post(HandlerUtil.getActiveShellChecked(var1), Messages.getString("ReviewIssue.TypeError.MSG"), Messages.getString("error.TITLE"), 1);
	        }

	        return null;
	    }
}
