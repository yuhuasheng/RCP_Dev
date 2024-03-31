package com.foxconn.mechanism.hhpnmaterialapply.export;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.PlatformUI;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.pse.AbstractPSEApplication;

public class ExportHandle extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException
    {
        AbstractPSEApplication app = (AbstractPSEApplication) AIFUtility.getCurrentApplication();
        String bu = (String) arg0.getParameters().get("bu");
        try
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(true, true, m -> {
                m.beginTask("export part list " + "", IProgressMonitor.UNKNOWN);
                new ExportDialog(app, bu);
                m.done();
            });
        }
        catch (InvocationTargetException | InterruptedException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
