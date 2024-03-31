package com.teamcenter.rac.workflow.commands.conditiontask;

import java.awt.Frame;

import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;

public class UserConditionTaskCommand extends AbstractAIFCommand {
	public UserConditionTaskCommand(Frame paramFrame, InterfaceAIFComponent paramInterfaceAIFComponent) {		
		System.out.println("UserConditionTaskCommand 1");
		
		setRunnable(new UserConditionTaskDialog(paramFrame, paramInterfaceAIFComponent));
	}

	public UserConditionTaskCommand(InterfaceAIFComponent paramInterfaceAIFComponent) {		
		System.out.println("UserConditionTaskCommand 2");
		
		setRunnable(new UserConditionTaskDialog(paramInterfaceAIFComponent));
	}
}
