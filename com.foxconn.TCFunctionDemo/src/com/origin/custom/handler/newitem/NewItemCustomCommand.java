package com.origin.custom.handler.newitem;

import java.awt.Frame;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.InterfaceAIFOperationExecutionListener;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class NewItemCustomCommand extends AbstractAIFCommand implements
		InterfaceAIFOperationExecutionListener {

	public Frame parentFrame;
	public TCSession session;
	private AbstractAIFApplication application;
	public TCComponent targetArray;

	public NewItemCustomCommand(Frame frame,
			AbstractAIFApplication abstractaifapplication) {

		try {
			parentFrame = frame;
			application = abstractaifapplication;
			targetArray = (TCComponent) application.getTargetComponent();
			session = (TCSession) abstractaifapplication.getSession();
			
			if (targetArray != null) {
				NewItemCustomOperation operation = new NewItemCustomOperation(
						session, targetArray);
				operation.addOperationListener(this);//¼àÌý operation
				operation.executeOperation();
				
			}
		} catch (Exception exception) {
			MessageBox.post(frame, exception);
		}
	}

	@Override
	public void exceptionThrown(Exception arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endOperation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void startOperation(String arg0) {
		// TODO Auto-generated method stub

	}
}
