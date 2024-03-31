package com.teamcenter.rac.workflow.commands.adhoc;

import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.kernel.TCAttachmentScope;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import java.awt.Frame;

public class UserAdhocCommand extends AbstractAIFCommand {
	public UserAdhocCommand(Frame paramFrame, TCComponentTask paramTCComponentTask) {
		
		System.out.println("UserAdhocCommand 1");
		
		if (paramTCComponentTask == null) {
			Registry registry = Registry.getRegistry(this);
			MessageBox.post(paramFrame, registry.getString("noObjectsSelected"), registry.getString("adhoc.TITLE"), 1);
		} else {
			setRunnable(new UserAdhocDialog(paramFrame, paramTCComponentTask));
		}
	}

	public UserAdhocCommand(TCComponentTask paramTCComponentTask) {
		this(null, paramTCComponentTask);
		
		System.out.println("UserAdhocCommand 2");
	}
}
