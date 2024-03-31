package com.teamcenter.rac.commands.project;

import com.teamcenter.rac.aif.AbstractAIFCommand;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.common.Activator;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.OSGIUtil;
import com.teamcenter.rac.util.Registry;
import java.awt.Frame;

public class UserProjectCommand extends AbstractAIFCommand {
	private TCComponent[] components;

	private String actionCode;

	private Frame parent;

	public UserProjectCommand(Frame paramFrame, AIFComponentContext[] paramArrayOfAIFComponentContext, String paramString) {

		System.out.println("==========UserProjectCommand 1==========");
		
		this.actionCode = paramString;
		this.parent = paramFrame;
		if (paramArrayOfAIFComponentContext != null && paramArrayOfAIFComponentContext.length > 0) {
			this.components = new TCComponent[paramArrayOfAIFComponentContext.length];
			for (int b = 0; b < paramArrayOfAIFComponentContext.length; b++) {
				TCComponent tCComponent = (TCComponent) paramArrayOfAIFComponentContext[b].getComponent();
				if (tCComponent instanceof com.teamcenter.rac.kernel.TCComponentCfgAttachmentLine)
					try {
						tCComponent = tCComponent.getUnderlyingComponent();
					} catch (TCException tCException) {
					}
				this.components[b] = tCComponent;
			}
		}
		try {
			TCSession tCSession = (TCSession) Activator.getDefault().getSession();
			if (this.actionCode.equals("AssignToProject") && showStrictHierarchicalMode() && tCSession.getCurrentProject() == null) {
				MessageBox messageBox = new MessageBox(this.parent,
						Registry.getRegistry(getClass()).getString("noSessionProgram"),
						Registry.getRegistry(getClass()).getString("noSessionProgram.TITLE"), 1);
				messageBox.setModal(true);
				messageBox.setVisible(true);
				return;
			}
			setRunnable(new UserProjectDialog(this.parent, this.components, this.actionCode));
		} catch (Exception exception) {
			MessageBox.post(exception);
		}
	}

	public UserProjectCommand(Frame paramFrame, TCComponent[] paramArrayOfTCComponent, String paramString) {
		
		System.out.println("==========UserProjectCommand 2==========");
		
		this.components = paramArrayOfTCComponent;
		this.actionCode = paramString;
		this.parent = paramFrame;
		try {
			setRunnable(new UserProjectDialog(this.parent, this.components, this.actionCode));
		} catch (Exception exception) {
			MessageBox.post(exception);
		}
	}

	private boolean showStrictHierarchicalMode() {
		TCPreferenceService tCPreferenceService = (TCPreferenceService) OSGIUtil.getService(Activator.getDefault(), TCPreferenceService.class);
		Boolean bool = tCPreferenceService.getLogicalValue("TC_use_strict_program_project_hierarchy");
		return (bool != null && bool.booleanValue());
	}
}
