package com.teamcenter.rac.workflow.commands.newperformsignoff;

import javax.swing.JPanel;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.kernel.TCComponentTask;

public class UserNewPerformSignoffTaskPanel extends NewPerformSignoffTaskPanel {

	public UserNewPerformSignoffTaskPanel(AIFDesktop paramAIFDesktop,
			JPanel paramJPanel, TCComponentTask paramTCComponentTask) {
		super(paramAIFDesktop, paramJPanel, paramTCComponentTask);
		// TODO Auto-generated constructor stub
		System.out.println("UserNewPerformSignoffTaskPanel 1");
	}

	public UserNewPerformSignoffTaskPanel(AIFDesktop paramAIFDesktop,
			NewPerformSignoffDialog paramNewPerformSignoffDialog,
			TCComponentTask paramTCComponentTask) {
		super(paramAIFDesktop, paramNewPerformSignoffDialog, paramTCComponentTask);
		// TODO Auto-generated constructor stub
		System.out.println("UserNewPerformSignoffTaskPanel 2");
	}

	
}
