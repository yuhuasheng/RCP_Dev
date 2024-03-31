package com.origin.custom.handler.newitem;

import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.util.MessageBox;

public class NewItemCustomAction extends AbstractAIFAction {

	public NewItemCustomAction(AbstractAIFUIApplication arg0, String arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			AbstractAIFCommand abstractaifcommand = new NewItemCustomCommand(
					parent, application);

			abstractaifcommand.executeModal();

		} catch (Exception exception) {
			MessageBox.post(parent, exception);
		}
	}

}
