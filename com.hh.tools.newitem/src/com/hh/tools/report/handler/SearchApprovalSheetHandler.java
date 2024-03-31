package com.hh.tools.report.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.hh.tools.report.action.SearchApprovalSheetAction;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class SearchApprovalSheetHandler extends AbstractHandler {

    private AbstractAIFUIApplication app = null;

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        // TODO Auto-generated method stub
        app = AIFUtility.getCurrentApplication();
        SearchApprovalSheetAction action = new SearchApprovalSheetAction(app, null, "");
        new Thread(action).start();
        return null;
    }

}
