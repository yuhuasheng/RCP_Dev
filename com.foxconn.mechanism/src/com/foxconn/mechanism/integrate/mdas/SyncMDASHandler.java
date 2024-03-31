package com.foxconn.mechanism.integrate.mdas;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

/**
 * 
 * @author wt00110
 *  mads 集成菜单入口
 */
public class SyncMDASHandler extends AbstractHandler {
	AbstractAIFUIApplication app = null;
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {	
		app = AIFUtility.getCurrentApplication();
		SynMDASAction action = new SynMDASAction(app, null, "");
		new Thread(action).start();
		return null;
	}
}
