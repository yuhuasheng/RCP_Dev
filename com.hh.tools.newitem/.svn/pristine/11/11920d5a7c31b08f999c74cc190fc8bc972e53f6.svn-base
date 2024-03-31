package com.hh.tools.report.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.hh.tools.report.action.ComplianceReportAction;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

//环保认证清单
public class ComplianceReportHandler extends AbstractHandler {

    private AbstractAIFApplication app = null;

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        // TODO Auto-generated method stub
        app = AIFUtility.getCurrentApplication();
        ComplianceReportAction action = new ComplianceReportAction(app, null, "");
        new Thread(action).start();
        return null;
    }
}
