package com.foxconn.mechanism.batchChangePhase;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;

/**
 * 
 * @author wt00110
 *批量转阶段菜单 入口
 */
public class BatchChangePhaseHandler extends AbstractHandler {
	AbstractAIFUIApplication app = null;
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {	
		app = AIFUtility.getCurrentApplication();
		BatchChangePhaseAction action = new BatchChangePhaseAction(app, null, "");
		new Thread(action).start();
		return null;
	}
}
