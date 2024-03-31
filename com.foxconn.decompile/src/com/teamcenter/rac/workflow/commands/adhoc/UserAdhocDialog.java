package com.teamcenter.rac.workflow.commands.adhoc;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.InterfaceAIFOperationListener;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Separator;
import com.teamcenter.rac.util.UIUtilities;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.workflow.common.AbstractTaskDialog;
import java.awt.Dimension;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UserAdhocDialog extends AbstractTaskDialog implements InterfaceAIFOperationListener {
	protected TCSession session = null;

	protected TCComponentTask task = null;

	protected Frame parent = null;

	protected UserAdhocSignoffsPanel signoffPanel;

	protected NewAdhocSignoffsPanel newSignoffPanel;

	private Registry r;

	public UserAdhocDialog(Frame paramFrame, TCSession paramTCSession) {
		super(paramFrame, paramTCSession);
		this.session = paramTCSession;
		this.parent = paramFrame;
		initDialog();
	}

	public UserAdhocDialog(Frame paramFrame, TCComponentTask paramTCComponentTask) {
		super(paramFrame, paramTCComponentTask);
		this.task = paramTCComponentTask;
		this.session = paramTCComponentTask.getSession();
		this.parent = paramFrame;
		initDialog();
	}

	public UserAdhocDialog(TCComponentTask paramTCComponentTask) {
		super(null, paramTCComponentTask);
		this.task = paramTCComponentTask;
		this.session = paramTCComponentTask.getSession();
		initDialog();
	}

	protected void initDialog() {
		this.r = Registry.getRegistry(this);
		setTitle(this.r.getString("command.TITLE"));
		this.signoffPanel = null;
		String str = null;
		try {
			str = this.task.getTaskType();
		} catch (Exception exception) {
		}
		boolean bool1 = (str != null && str.equalsIgnoreCase("EPMRouteTask"));
		boolean bool2 = true;
		if (bool1) {
			String str1 = null;
			if (this.session != null) {
				TCPreferenceService tCPreferenceService = this.session.getPreferenceService();
				str1 = tCPreferenceService.getString(0, "WORKFLOW_new_route_task_panel");
			}
			if (str1 != null && str1.length() != 0 && str1.trim().toLowerCase().equals("off"))
				bool2 = false;
			if (bool2) {
				this.newSignoffPanel = new NewRouteTaskSignoffsPanel(this.parent, this.task);
				this.newSignoffPanel.setSplitPanelDivider(0.6D);
				this.newSignoffPanel.setOperationListener(this);
			} else {
				this.signoffPanel = new UserRouteTaskSignoffsPanel(this.parent, this.task);
				this.signoffPanel.setSplitPanelDivider(0.6D);
				this.signoffPanel.setOperationListener(this);
			}
		} else {
			this.signoffPanel = new UserAdhocSignoffsPanel(this.parent, this.task);
			this.signoffPanel.setOperationListener(this);
		}
		JButton jButton1 = null;
		JButton jButton2 = null;
		if (bool1 && bool2) {
			jButton1 = this.newSignoffPanel.getOkButton();
			jButton1.setVisible(this.newSignoffPanel.getApplyButton().isVisible());
			jButton2 = this.newSignoffPanel.getCancelButton();
		} else {
			jButton1 = this.signoffPanel.getOkButton();
			jButton1.setVisible(this.signoffPanel.getApplyButton().isVisible());
			jButton2 = this.signoffPanel.getCancelButton();
		}
		jButton2.setVisible(true);
		jButton2.addActionListener(new AbstractAIFDialog.IC_DisposeActionListener());
		JPanel jPanel = new JPanel(new VerticalLayout(7, 4, 4, 4, 4));
		getContentPane().add(jPanel);
		JLabel jLabel = new JLabel(this.r.getImageIcon("addhoc.ICON"), 0);
		jPanel.add("top.nobind.left", jLabel);
		jPanel.add("top.bind", new Separator());
		if (bool1 && bool2) {
			jPanel.add("unbound.bind.left", this.newSignoffPanel);
		} else {
			jPanel.add("unbound.bind.left", this.signoffPanel);
		}
		
		// recompile 20220323143000 : START
//		setMinimumSize(new Dimension(bool1 ? 600 : 550, 525));
		setMinimumSize(new Dimension(bool1 ? 1450 : 1450, 575));
		// 20220323143000 : END
		
		setPersistentDisplay(true);
		validate();
		pack();		
		// recompile 20220323143000 : START
//		UIUtilities.centerToScreen(this, 1.5D, 1.0D, 0.5D, 0.4D);
		UIUtilities.centerToScreen(this, 2.0D, 1.0D, 0.5D, 0.0D);
		// 20220323143000 : END
		
		// recompile 20220323143000 : START
//		setSize(bool1 ? 650 : 590, 575);
		setSize(bool1 ? 1450 : 1450, 575);
		// 20220323143000 : END
	}

	public void run() {
		setVisible(true);
	}

	public void startOperation(String paramString) {
	}

	public void endOperation() {
		disposeDialog();
	}
}