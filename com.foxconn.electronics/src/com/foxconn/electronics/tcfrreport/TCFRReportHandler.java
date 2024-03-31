package com.foxconn.electronics.tcfrreport;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class TCFRReportHandler extends AbstractHandler{
	
	
	@Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
		AbstractAIFUIApplication application = AIFUtility.getCurrentApplication();
		Shell shell = application.getDesktop().getShell();
		
		try {
			new TCFRReportDialog(application,shell);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }


}
