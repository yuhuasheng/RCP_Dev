package com.foxconn.electronics.managementebom.dcnChange.mnt;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.tcutils.util.IndeterminateLoadingDialog;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class ChangeHandler extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException
    {
        AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
        Shell shell = app.getDesktop().getShell();
        new IndeterminateLoadingDialog(shell,"正在導出ChangeList...",new IndeterminateLoadingDialog.Action() {

			@Override
			public void run() {

				new ChangeAction(app);
			}
        });
        return null;
    }
}
