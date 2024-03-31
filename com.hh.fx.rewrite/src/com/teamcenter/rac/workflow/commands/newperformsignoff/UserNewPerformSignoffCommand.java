package com.teamcenter.rac.workflow.commands.newperformsignoff;

import java.awt.Frame;

import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;

public class UserNewPerformSignoffCommand extends AbstractAIFCommand {

	public UserNewPerformSignoffCommand(Frame paramFrame,
			InterfaceAIFComponent paramInterfaceAIFComponent) {
		//super(paramFrame, paramInterfaceAIFComponent);
		// TODO Auto-generated constructor stub
		System.out.println("UserNewPerformSignoffCommand 1");
		setRunnable(new NewPerformSignoffDialog(paramFrame, paramInterfaceAIFComponent));
	}

}
