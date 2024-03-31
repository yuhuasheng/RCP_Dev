package com.hh.tools.dataset.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.hh.tools.dataset.action.DatesetDownloadAction;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class DatesetDownloadHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
//		System.out.println("Dateset download action");
        AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
        DatesetDownloadAction datesetDownloadAction = new DatesetDownloadAction(app, null, "");
        new Thread(datesetDownloadAction).start();
        return null;
    }

}
