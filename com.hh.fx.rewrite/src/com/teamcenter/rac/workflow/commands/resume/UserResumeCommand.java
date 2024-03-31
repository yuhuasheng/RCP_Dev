package com.teamcenter.rac.workflow.commands.resume;

import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.kernel.TCComponentTask;
import java.awt.Frame;

public class UserResumeCommand
  extends AbstractAIFCommand
{
  public UserResumeCommand(Frame paramFrame, TCComponentTask[] paramArrayOfTCComponentTask)
  {
	  System.out.println("UserResumeCommand 1");
    if ((paramArrayOfTCComponentTask != null) && (paramArrayOfTCComponentTask.length > 0)) {
      setRunnable(new UserResumeDialog(paramFrame, paramArrayOfTCComponentTask));
    }
  }
  
  public UserResumeCommand(TCComponentTask[] paramArrayOfTCComponentTask)
  {
	  System.out.println("UserResumeCommand 2");
    if ((paramArrayOfTCComponentTask != null) && (paramArrayOfTCComponentTask.length > 0)) {
      setRunnable(new UserResumeDialog(paramArrayOfTCComponentTask));
    }
  }
}