package com.foxconn.mechanism.downloadrename;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

public class DownloadRenameHandler extends AbstractHandler {

	@Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
		AbstractAIFApplication app = AIFUtility.getCurrentApplication();
        DownloadRenameAction action = new DownloadRenameAction(app, null, null);
		new Thread(action).start();
        return null;
    }

}
