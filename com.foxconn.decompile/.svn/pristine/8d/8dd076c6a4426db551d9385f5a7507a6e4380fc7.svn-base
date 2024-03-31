package com.teamcenter.rac.commands.project;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.InterfaceAIFOperationListener;
import com.teamcenter.rac.common.TCTypeRenderer;
import com.teamcenter.rac.explorer.common.ISearchResultNavigator;
import com.teamcenter.rac.explorer.common.NavigatorPanel;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.HyperLink;
import com.teamcenter.rac.util.Instancer;
import com.teamcenter.rac.util.MLabel;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Separator;
import com.teamcenter.rac.util.UIUtilities;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.VerticalLayout;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

public class UserProjectDialog extends AbstractAIFDialog
		implements InterfaceAIFOperationListener, PropertyChangeListener {
	private TCComponent[] arrayTargets;

	private NavigatorPanel navigatorPanel;

	private ISearchResultNavigator resultNavigator;

	private String actionCode;

	private Frame parent;

	private AbstractAIFOperation queuedOperation;

	private TCSession session;

	private Registry appReg;

	private JPanel projSelectionPanel;

	private boolean doAll = false;

	private JCheckBox contentsCB;

	private JCheckBox folderCB;

	private JComboBox typesCombo;

	private JComboBox levelsCombo;

	private JButton okButton;

	private JButton applyButton;

	private JButton cancelButton;

	private boolean dispose = false;

	private int dimensionX = 400;

	private HyperLink moreLabel;

	private int orientation = 2;

	private int suggestedHorizontalAlignment = 1;

	private int suggestedVerticalAlignment = 1;

	public UserProjectDialog(Frame paramFrame, TCComponent[] paramArrayOfTCComponent, String paramString) {
		super(paramFrame, true);
		
		System.out.println("==========UserProjectDialog 1==========");
		
		this.parent = paramFrame;
		this.arrayTargets = paramArrayOfTCComponent;
		this.actionCode = paramString;
		this.navigatorPanel = null;
		this.session = this.arrayTargets[0].getSession();
		initializeDialog();
	}

	public UserProjectDialog(Frame paramFrame, NavigatorPanel paramNavigatorPanel, String paramString) {
		super(paramFrame, true);
		
		System.out.println("==========UserProjectDialog 2==========");
		
		this.parent = paramFrame;
		this.arrayTargets = null;
		this.actionCode = paramString;
		this.navigatorPanel = paramNavigatorPanel;
		this.session = paramNavigatorPanel.getSession();
		initializeDialog();
	}

	public UserProjectDialog(Frame paramFrame, ISearchResultNavigator paramISearchResultNavigator, String paramString) {
    super(paramFrame, true);
    this.parent = paramFrame;
    this.arrayTargets = null;
    this.actionCode = paramString;
    this.resultNavigator = paramISearchResultNavigator;
    this.session = this.resultNavigator.getSession();
    initializeDialog();
  }

	private void initializeDialog() {
		this.appReg = Registry.getRegistry(this);
		ImageIcon imageIcon = null;
		boolean bool1 = false;
		String str = null;
		boolean bool2 = false;
		if (this.navigatorPanel == null && this.resultNavigator == null) {
			bool1 = (this.arrayTargets.length > 1);
		} else {
			bool1 = true;
		}
		Object[] arrayOfObject = { TCComponentProject.getDisplayName(this.session) };
		if (this.actionCode.equals("AssignToProject")) {
			if (bool1) {
				setTitle(MessageFormat.format(this.appReg.getString("assignToProjectCommands.TITLE"), arrayOfObject));
				str = MessageFormat.format(
						this.appReg.getString("selectProjectForAssigns.TITLE", "Select Projects to Assign Objects to"),
						arrayOfObject);
			} else {
				setTitle(MessageFormat.format(this.appReg.getString("assignToProjectCommand.TITLE"), arrayOfObject));
				str = MessageFormat.format(
						this.appReg.getString("selectProjectForAssign.TITLE", "Select Projects to Assign Object to"),
						arrayOfObject);
			}
			imageIcon = this.appReg.getImageIcon("assignToProject.ICON");
		} else {
			if (bool1) {
				setTitle(MessageFormat.format(this.appReg.getString("removeFromProjectCommands.TITLE"), arrayOfObject));
				str = MessageFormat.format(this.appReg.getString("selectProjectsForRemoves.TITLE",
						"Select Projects to Remove Objects from"), arrayOfObject);
			} else {
				setTitle(MessageFormat.format(this.appReg.getString("removeFromProjectCommand.TITLE"), arrayOfObject));
				str = MessageFormat.format(
						this.appReg.getString("selectProjectsForRemove.TITLE", "Select Projects to Remove Object from"),
						arrayOfObject);
			}
			imageIcon = this.appReg.getImageIcon("removeFromProject.ICON");
			bool2 = true;
		}
		JPanel jPanel1 = new JPanel(new HorizontalLayout());
		JLabel jLabel = new JLabel(imageIcon, 0);
		jPanel1.add("left.nobind.left.center", jLabel);
		JPanel jPanel2 = null;
		if (this.navigatorPanel == null && this.resultNavigator == null)
			jPanel2 = createComponentIconPanel(bool1);
		boolean bool3 = areFolderObjects();
		if (this.arrayTargets != null && !bool3) {			
//			this.projSelectionPanel = (JPanel) Instancer.newInstance("com.teamcenter.rac.project.ProjectSelectionPanel", new Object[] { this.session, str, Boolean.valueOf(false), Boolean.valueOf(bool2), this.arrayTargets });
			this.projSelectionPanel = (JPanel) new UserProjectSelectionPanel(this.session, str, null, Boolean.valueOf(false), Boolean.valueOf(bool2), this.arrayTargets);
		} else {
//			this.projSelectionPanel = (JPanel) Instancer.newInstance("com.teamcenter.rac.project.ProjectSelectionPanel", new Object[] { this.session, str, Boolean.valueOf(false), Boolean.valueOf(bool2) });
			this.projSelectionPanel = (JPanel) new UserProjectSelectionPanel(this.session, str, null, Boolean.valueOf(false), Boolean.valueOf(bool2));
		}
		this.projSelectionPanel.addPropertyChangeListener(this);
		JPanel jPanel3 = null;
		if (this.navigatorPanel != null || this.resultNavigator != null)
			jPanel3 = createAssignQueryResultButtonPanel();
		JPanel jPanel4 = null;
		if ((this.navigatorPanel == null || this.resultNavigator == null) && bool3)
			jPanel4 = createAssignFolderCheckBoxPanel();
		JPanel jPanel5 = null;
		if ((this.navigatorPanel == null || this.resultNavigator == null) && areBOMLines())
			jPanel5 = createBOMLinePanel();
		JPanel jPanel6 = new JPanel(new ButtonLayout());
		this.okButton = new JButton(this.appReg.getString("ok"));
		this.okButton.setMnemonic(this.appReg.getString("ok.MNEMONIC").charAt(0));
		this.okButton.setEnabled(false);
		this.okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				startProjectOperation();
				dispose = true;
			}
		});
		this.applyButton = new JButton(this.appReg.getString("apply"));
		this.applyButton.setMnemonic(this.appReg.getString("apply.MNEMONIC").charAt(0));
		this.applyButton.setEnabled(false);
		this.applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				startProjectOperation();
				dispose = false;
			}
		});
		this.cancelButton = new JButton(this.appReg.getString("cancel"));
		this.cancelButton.setMnemonic(this.appReg.getString("cancel.MNEMONIC").charAt(0));
		this.cancelButton.addActionListener(new AbstractAIFDialog.IC_DisposeActionListener());
		jPanel6.add(this.okButton);
		jPanel6.add(this.applyButton);
		jPanel6.add(this.cancelButton);
		JPanel jPanel7 = new JPanel(new VerticalLayout(5, 5, 5, 5, 5));
		getContentPane().add(jPanel7);
		jPanel7.add("top.nobind.left", jPanel1);
		jPanel7.add("top.bind", new Separator());
		if (jPanel2 != null)
			jPanel7.add("top.nobind.left", jPanel2);
		jPanel7.add("unbound.bind", this.projSelectionPanel);
		jPanel7.add("bottom.bind.center.top", jPanel6);
		jPanel7.add("bottom.bind", new Separator());
		if (jPanel3 != null) {
			jPanel7.add("bottom", jPanel3);
		} else if (jPanel4 != null) {
			jPanel7.add("bottom", jPanel4);
		} else if (jPanel5 != null) {
			jPanel7.add("bottom", jPanel5);
		}
		setDefaultCloseOperation(0);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent param1WindowEvent) {
				disposeDialog();
			}
		});
		pack();
		
		// recompile : START
		setMinimumSize(new Dimension(1300, 800));
		setSize(1450, 575);
		UIUtilities.centerToScreen(this, 2.0D, 1.2D, 0.5D, 0.0D);
//		centerToScreen(0.5D, 2.0D);
		// recompile : END
		
//		centerToScreen(1.0D, 0.7D);
	}

	private boolean areFolderObjects() {
		boolean bool = false;
		if (this.arrayTargets != null) {
			TCComponent[] arrayOfTCComponent;
			int i = (arrayOfTCComponent = this.arrayTargets).length;
			for (int b = 0; b < i; b++) {
				TCComponent tCComponent = arrayOfTCComponent[b];
				try {
					TCComponentType tCComponentType = tCComponent.getTypeComponent();
					if (tCComponentType.isTypeOf("Folder")) {
						bool = true;
						break;
					}
				} catch (TCException tCException) {
				}
			}
		}
		return bool;
	}

	private boolean areBOMLines() {
		boolean bool = false;
		if (this.arrayTargets != null) {
			bool = true;
			TCComponent[] arrayOfTCComponent;
			int i = (arrayOfTCComponent = this.arrayTargets).length;
			for (int b = 0; b < i; b++) {
				TCComponent tCComponent = arrayOfTCComponent[b];
				if (!(tCComponent instanceof TCComponentBOMLine)) {
					bool = false;
					break;
				}
			}
		}
		return bool;
	}

	private JPanel createAssignFolderCheckBoxPanel() {
		Object[] arrayOfObject = { TCComponentProject.getDisplayName(this.session) };
		this.contentsCB = new JCheckBox(this.appReg.getString("folderContents.LABEL", "Contents"), true);
		this.contentsCB
				.setToolTipText(MessageFormat.format(this.appReg.getString("folderContents.TIP"), arrayOfObject));
		this.folderCB = new JCheckBox(this.appReg.getString("folderObject.LABEL", "Folder"), false);
		this.folderCB.setToolTipText(MessageFormat.format(this.appReg.getString("folderObject.TIP"), arrayOfObject));
		JPanel jPanel = new JPanel(new HorizontalLayout());
		jPanel.add("left", this.contentsCB);
		jPanel.add("left", this.folderCB);
		return jPanel;
	}

	private JPanel createAssignQueryResultButtonPanel() {
		JRadioButton jRadioButton1 = new JRadioButton(
				this.appReg.getString("assignAllButton.LABEL", "All Found Objects"), this.doAll);
		jRadioButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				doAll = true;
			}
		});
		JRadioButton jRadioButton2 = new JRadioButton(this.appReg.getString("assignLoadedButton.LABEL", "Current Page"),
				!this.doAll);
		jRadioButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				doAll = false;
			}
		});
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(jRadioButton1);
		buttonGroup.add(jRadioButton2);
		JPanel jPanel = new JPanel(new HorizontalLayout());
		jPanel.add("left", jRadioButton2);
		jPanel.add("left", jRadioButton1);
		return jPanel;
	}

	private JPanel createBOMLinePanel() {
		String str1 = this.appReg.getString("parentToAssign.LABEL", "Parent to Assign");
		Object[] arrayOfObject = { TCComponentProject.getDisplayName(this.session) };
		this.typesCombo = new JComboBox();
		this.typesCombo
				.setToolTipText(MessageFormat.format(this.appReg.getString("parentToAssign.TIP"), arrayOfObject));
		this.typesCombo.addItem(this.appReg.getString("BOMLineParentItem.LABEL"));
		if (!isRevisionUnconfigured())
			this.typesCombo.addItem(this.appReg.getString("BOMLineParentItemRev.LABEL"));
		this.typesCombo.setSelectedIndex(0);
		String str2 = this.appReg.getString("BOMPropagationLevel.LABEL", "Propagation Levels");
		this.levelsCombo = new JComboBox();
		this.levelsCombo.setToolTipText(this.appReg.getString("BOMPropagationLevel.TIP"));
		this.levelsCombo.addItem("0");
		this.levelsCombo.addItem(this.appReg.getString("BOMLinePropagationAll.LABEL"));
		this.levelsCombo.setSelectedIndex(0);
		this.levelsCombo.setEditable(true);
		JPanel jPanel = new JPanel(new PropertyLayout());
		jPanel.add("1.1.right.center", new JLabel(str1));
		jPanel.add("1.2.left.center", this.typesCombo);
		jPanel.add("2.1.right.center", new JLabel(str2));
		jPanel.add("2.2.left.center", this.levelsCombo);
		return jPanel;
	}

	private JPanel createComponentIconPanel(boolean paramBoolean) {
		JPanel jPanel1 = new JPanel(new PropertyLayout());
		TCComponent tCComponent = this.arrayTargets[0];
		ImageIcon imageIcon = null;
		imageIcon = TCTypeRenderer.getIcon(tCComponent, false);
		JLabel jLabel1 = new JLabel(imageIcon);
		String str = tCComponent.toString();
		JLabel jLabel2 = new JLabel(str);
		jPanel1.add("1.1.right.center.preferred.preferred", jLabel1);
		jPanel1.add("1.2.right.center.preferred.preferred", jLabel2);
		JPanel jPanel2 = new JPanel(new HorizontalLayout());
		jPanel2.add("left", jPanel1);
		if (paramBoolean) {
			this.moreLabel = new HyperLink(this.appReg.getString("more.LABEL", "..."));
			this.moreLabel.setEnabled(true);
			this.moreLabel.setUnderLine(false);
			this.moreLabel.setToolTipText(this.appReg.getString("more.TIP"));
			this.moreLabel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent param1ActionEvent) {
					showListObjectsDialog();
				}
			});
			jPanel2.add("left", this.moreLabel);
		}
		return jPanel2;
	}

	private void showListObjectsDialog() {
		ListObjectsDialog dlg = new ListObjectsDialog("", (JFrame) this.parent);
		Thread thread = new Thread(new Runnable() {
			public void run() {
				setWindowLocation(dlg);
				dlg.setVisible(true);
			}
		});
		thread.start();
	}

	private void setWindowLocation(Container paramContainer) {
		Point point1 = this.moreLabel.getLocationOnScreen();
		Dimension dimension1 = this.moreLabel.getSize();
		Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dimension3 = paramContainer.getPreferredSize();
		Point point2 = new Point(0, 0);
		for (int b = 0; b < 8; b++) {
			int i = ((b & 4) == 4) ? (3 - this.orientation) : this.orientation;
			int j = ((b & 2) == 2) ? (3 - this.suggestedVerticalAlignment) : this.suggestedVerticalAlignment;
			int k = ((b & 1) == 1) ? (3 - this.suggestedHorizontalAlignment) : this.suggestedHorizontalAlignment;
			if (i == 1) {
				if (k == 1) {
					point1.x -= dimension3.width;
				} else {
					point1.x += dimension1.width;
				}
				if (j == 1) {
					point2.y = point1.y;
				} else {
					point2.y = point1.y + dimension1.height - dimension3.height;
				}
			} else {
				if (k == 1) {
					point2.x = point1.x;
				} else {
					point2.x = point1.x + dimension1.width - dimension3.width;
				}
				if (j == 1) {
					point1.y -= dimension3.height;
				} else {
					point1.y += dimension1.height;
				}
			}
			if (point2.x > 0 && point2.y > 0 && point2.x + dimension3.width < dimension2.width
					&& point2.y + dimension3.height < dimension2.height) {
				paramContainer.setLocation(point2.x, point2.y);
				return;
			}
		}
		if (point2.x < 0) {
			point2.x = 0;
			point2.y -= dimension1.height;
		}
		if (point2.y < 0)
			point2.y = 0;
		paramContainer.setLocation(point2.x, point2.y);
	}

	private void startProjectOperation() {
		TCComponentProject[] arrayOfTCComponentProject = null;
		try {
			arrayOfTCComponentProject = (TCComponentProject[]) Utilities.invokeMethod(this.projSelectionPanel,
					"getSelectedProjects", new Object[0]);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		TCComponent[] arrayOfTCComponent = this.arrayTargets;
		String[] arrayOfString = null;
		if (arrayOfTCComponent == null && (this.navigatorPanel != null || this.resultNavigator != null))
			if (this.doAll) {
				if (this.navigatorPanel != null)
					arrayOfString = this.navigatorPanel.getAllFoundUids();
				if (this.resultNavigator != null) {
					List list = this.resultNavigator.getAllFoundObjects();
					arrayOfString = new String[list.size()];
					for (int b = 0; b < list.size(); b++) {
						TCComponent tCComponent = (TCComponent) list.get(b);
						String str = tCComponent.getUid();
						arrayOfString[b] = str;
					}
				}
				if (arrayOfString == null || arrayOfString.length == 0) {
					MessageBox.post(this.parent, this.appReg.getString("nothingFound.MESSAGE"),
							this.appReg.getString("error.TITLE"), 1);
					return;
				}
			} else {
				Vector vector = new Vector();
				if (this.navigatorPanel != null)
					vector = this.navigatorPanel.getCurrentLoadingObjects();
				if (this.resultNavigator != null) {
					List list = this.resultNavigator.getCurrentLoadingObjects();
					vector.addAll(list);
				}
				if (vector == null || vector.isEmpty()) {
					MessageBox.post(this.parent, this.appReg.getString("nothingFound.MESSAGE"),
							this.appReg.getString("error.TITLE"), 1);
					return;
				}
				arrayOfTCComponent = (TCComponent[]) vector.toArray(new TCComponent[vector.size()]);
			}
		if (arrayOfTCComponent != null) {
			int i = getFolderOption();
			int j = getBOMLineTypeOption();
			int k = getBOMLineLevel();
			if (i > 0) {
				this.queuedOperation = new ProjectOperation(arrayOfTCComponentProject, arrayOfTCComponent, i,
						this.actionCode);
			} else if (j >= 0) {
				this.queuedOperation = new ProjectOperation(arrayOfTCComponentProject, arrayOfTCComponent, j, k,
						this.actionCode);
			} else {
				this.queuedOperation = new ProjectOperation(arrayOfTCComponentProject, arrayOfTCComponent,
						this.actionCode);
			}
		} else {
			this.queuedOperation = new ProjectOperation(arrayOfTCComponentProject, arrayOfString, this.actionCode);
		}
		this.queuedOperation.addOperationListener(this);
		this.session.queueOperation(this.queuedOperation);
	}

	private int getFolderOption() {
		int b1 = 1;
		int b2 = 2;
		int b3 = 0;
		if (this.contentsCB != null && this.contentsCB.isSelected())
			b3 = b1;
		if (this.folderCB != null && this.folderCB.isSelected())
			b3 |= b2;
		return b3;
	}

	private int getBOMLineTypeOption() {
		return (this.typesCombo != null) ? this.typesCombo.getSelectedIndex() : -1;
	}

	private int getBOMLineLevel() {
		try {
			if (this.levelsCombo != null)
				return (this.levelsCombo.getSelectedIndex() >= 1) ? -1
						: Integer.parseInt((String) this.levelsCombo.getSelectedItem());
		} catch (Exception exception) {
			MessageBox.post(exception);
		}
		return -1;
	}

	public void startOperation(String paramString) {
		Object[] arrayOfObject = { TCComponentProject.getDisplayName(this.session) };
		setCursor(Cursor.getPredefinedCursor(3));
		this.projSelectionPanel.setCursor(Cursor.getPredefinedCursor(3));
		if (this.actionCode.equals("AssignToProject")) {
			this.session
					.setStatus(MessageFormat.format(this.appReg.getString("assignToProjects.MESSAGE"), arrayOfObject));
		} else {
			this.session.setStatus(
					MessageFormat.format(this.appReg.getString("removeFromProjects.MESSAGE"), arrayOfObject));
		}
		this.okButton.setVisible(false);
		this.applyButton.setVisible(false);
		this.cancelButton.setVisible(false);
		validate();
	}

	public void endOperation() {
		this.session.setStatus(this.appReg.getString("ready"));
		this.projSelectionPanel.setCursor(Cursor.getPredefinedCursor(0));
		setCursor(Cursor.getPredefinedCursor(0));
		this.okButton.setVisible(true);
		this.applyButton.setVisible(true);
		this.cancelButton.setVisible(true);
		validate();
		this.queuedOperation.removeOperationListener(this);
		if (this.dispose)
			disposeDialog();
	}

	public void run() {
		setVisible(true);
	}

	public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
		String str = paramPropertyChangeEvent.getPropertyName();
		if (str.equals("ProjectSelectionChanged")) {
			boolean bool = (paramPropertyChangeEvent.getNewValue() != null);
			this.okButton.setEnabled(bool);
			this.applyButton.setEnabled(bool);
		}
	}

	private boolean isRevisionUnconfigured() {
		if (this.arrayTargets[0] != null)
			try {
				TCComponentItemRevision tCComponentItemRevision = ((TCComponentBOMLine) this.arrayTargets[0])
						.getItemRevision();
				if (tCComponentItemRevision != null)
					return false;
			} catch (TCException tCException) {
			}
		return true;
	}

	private class ListObjectsDialog extends AbstractAIFDialog {
		private JPanel listPanel;

		private JButton closeButton;

		public ListObjectsDialog(String param1String, JFrame param1JFrame) {
			super(param1JFrame, param1String, true);
			setDefaultCloseOperation(1);
			this.listPanel = createSelectedComponentsPanel();
			this.listPanel.setPreferredSize(new Dimension(dimensionX / 2, 200));
			JPanel jPanel = new JPanel(new ButtonLayout());
			this.closeButton = new JButton(appReg.getString("closeButton.LABEL", "close"));
			this.closeButton.addActionListener(new AbstractAIFDialog.IC_DisposeActionListener());
			jPanel.add(this.closeButton);
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(this.listPanel, "Center");
			getContentPane().add(jPanel, "South");
			pack();
			centerToScreen();
		}

		private JPanel createSelectedComponentsPanel() {
			JPanel jPanel = new JPanel(new BorderLayout());
			jPanel.setBorder(BorderFactory.createTitledBorder(
					appReg.getString("selectedObjects.TITLE", "Selected Objects")));
			String str = "";
			if (arrayTargets != null) {
				int i = arrayTargets.length;
				for (int b = 0; b < i; b++) {
					if (b==0) {
						str = arrayTargets[b].toString();
					} else {
						str = String.valueOf(str) + " \n" + arrayTargets[b];
					}
				}
			}
			MLabel mLabel = new MLabel(str);
			JScrollPane jScrollPane = new JScrollPane(mLabel);
			jPanel.add(jScrollPane, "Center");
			return jPanel;
		}
	}
}
