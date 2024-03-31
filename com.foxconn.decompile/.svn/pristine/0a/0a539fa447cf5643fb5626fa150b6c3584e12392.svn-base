package com.teamcenter.rac.workflow.commands.dotask;

import java.awt.Frame;

import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;

public class UserDoTaskCommand extends AbstractAIFCommand {
	public UserDoTaskCommand(Frame paramFrame, InterfaceAIFComponent paramInterfaceAIFComponent) {
		System.out.println("UserDoTaskCommand 1");
		
		setRunnable(new UserDoTaskDialog(paramFrame, paramInterfaceAIFComponent));				
	}

	public UserDoTaskCommand(InterfaceAIFComponent paramInterfaceAIFComponent) {
		System.out.println("UserDoTaskCommand 2");
		
		setRunnable(new UserDoTaskDialog(paramInterfaceAIFComponent));				
	}
}
