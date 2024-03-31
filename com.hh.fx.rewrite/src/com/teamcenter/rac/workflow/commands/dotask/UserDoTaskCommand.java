package com.teamcenter.rac.workflow.commands.dotask;

import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import java.awt.Frame;

public class UserDoTaskCommand extends AbstractAIFCommand {

	public UserDoTaskCommand(Frame paramFrame, InterfaceAIFComponent paramInterfaceAIFComponent)
	  {
		System.out.println("UserDoTaskCommand1");
	    setRunnable(new UserDoTaskDialog(paramFrame, paramInterfaceAIFComponent));
	  }

	  public UserDoTaskCommand(InterfaceAIFComponent paramInterfaceAIFComponent)
	  {
		System.out.println("UserDoTaskCommand2");
	    setRunnable(new UserDoTaskDialog(paramInterfaceAIFComponent));
	  }
	
}
