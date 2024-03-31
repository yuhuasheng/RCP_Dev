package com.foxconn.electronics.certificate;

import java.awt.EventQueue;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class CerImportHandle extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException
    {
        AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
        Shell shell = app.getDesktop().getShell();
        CerImportDialog dialog = new CerImportDialog(shell);
        dialog.open();
        return null;
    }
}
