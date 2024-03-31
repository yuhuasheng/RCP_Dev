package com.hh.tools.report.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.hh.tools.report.action.ConnectorReportAction;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

//Ý”³öCONNECTORˆó±í
public class ConnectorReportHandler extends AbstractHandler {

    private AbstractAIFApplication app = null;

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        // TODO Auto-generated method stub
        app = AIFUtility.getCurrentApplication();
        ConnectorReportAction action = new ConnectorReportAction(app, null, "");
        new Thread(action).start();
        return null;
    }

}
