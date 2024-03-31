package com.hh.tools.dashboard.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.hh.tools.dashboard.action.DashboardAction;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class DashboardHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        AbstractAIFApplication app = AIFUtility.getCurrentApplication();
        DashboardAction action = new DashboardAction(app, null);
        new Thread(action).start();
        return null;
    }
}
