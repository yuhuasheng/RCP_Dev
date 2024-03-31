package com.teamcenter.rac.workflow.commands.dotask;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.workflow.common.AbstractTaskDialog;
import java.awt.Frame;

public class UserDoTaskDialog extends AbstractTaskDialog {
	private TCComponentTask task;

	private Registry registry;

	protected Frame parent;

	private UserDoTaskPanel parentPanel;

	private AIFDesktop desktop = null;

	public static final String COMMAND_TITLE_REG_KEY = "command.TITLE";

	public UserDoTaskDialog(Frame paramFrame, InterfaceAIFComponent paramInterfaceAIFComponent) {
		super(paramFrame, paramInterfaceAIFComponent);
		this.parent = paramFrame;
		if (paramInterfaceAIFComponent instanceof TCComponentTask)
			this.task = (TCComponentTask) paramInterfaceAIFComponent;
		if (paramFrame instanceof AIFDesktop)
			this.desktop = (AIFDesktop) paramFrame;
		initDialog();
	}

	public UserDoTaskDialog(InterfaceAIFComponent paramInterfaceAIFComponent) {
		super(paramInterfaceAIFComponent);
		if (paramInterfaceAIFComponent instanceof TCComponentTask)
			this.task = (TCComponentTask) paramInterfaceAIFComponent;
		initDialog();
	}

	protected void initDialog() {
		try {
			this.registry = Registry.getRegistry(this);
			setTitle(this.registry.getString("command.TITLE"));
			this.parentPanel = new UserDoTaskPanel(this.desktop, this, this.task);
			getContentPane().add(this.parentPanel);
		} catch (Exception exception) {
			MessageBox messageBox = null;
			if (this.parent != null) {
				messageBox = new MessageBox(this.parent, exception);
				messageBox.setModal(true);
			} else {
				messageBox = new MessageBox(exception);
			}
			messageBox.setVisible(true);
			return;
		}
		pack();
		centerToScreen(1.5D, 1.0D);
	}

	public void run() {
		setVisible(true);
		this.parentPanel.setFocus();
	}

	public AIFDesktop getDesktop() {
		return this.desktop;
	}
}
