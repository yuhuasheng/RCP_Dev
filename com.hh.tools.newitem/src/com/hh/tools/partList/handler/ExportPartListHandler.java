package com.hh.tools.partList.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import com.hh.tools.partList.action.ExportPartListAction;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 *
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ExportPartListHandler extends AbstractHandler {
    AbstractAIFUIApplication app = null;

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        app = AIFUtility.getCurrentApplication();
        ExportPartListAction action = new ExportPartListAction(app, null, "");
        new Thread(action).start();
        return null;
    }
}
