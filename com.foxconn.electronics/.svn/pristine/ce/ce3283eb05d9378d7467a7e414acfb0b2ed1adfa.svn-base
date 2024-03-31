package com.foxconn.electronics.managementebom.copybom;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class CopyBOMHandler extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException
    {
        AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
        new CopyBOMDialog(app.getDesktop().getShell()).open();
        return null;
    }
}
