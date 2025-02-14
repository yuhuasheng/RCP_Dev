package com.teamcenter.rac.workflow.commands.newprocess;

import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.TCTree;
import com.teamcenter.rac.kernel.ResourceMember;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentAssignmentList;
import com.teamcenter.rac.kernel.TCComponentAssignmentListType;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Separator;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.combobox.iComboBox;
import com.teamcenter.rac.workflow.commands.assignmentlist.CreateAssignmentListOperation;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class UserAssignAllTasksPanel extends JPanel {
	private TCSession session = null;

	private Frame parent = null;

	protected Registry reg = null;

	protected UserAssignmentListPanel resourcesPanel = null;

	protected TCComponentTaskTemplate processTemplate = null;

	protected TCComponentProcess selProcess = null;

	protected iComboBox assignmentList = null;

	private TCComponentAssignmentList currentList = null;

	private JCheckBox assignedCheckbox = null;

	public TCTree getProcessTreeView(){
		return resourcesPanel.getProcessTreeView();		
	}
	
	public UserAssignAllTasksPanel(TCSession paramTCSession) {
		super(true);
		this.session = paramTCSession;
		this.parent = null;
		this.processTemplate = null;
		this.selProcess = null;
		initialize();
	}

	public UserAssignAllTasksPanel(TCSession paramTCSession, TCComponentProcess paramTCComponentProcess) {
		super(true);
		this.session = paramTCSession;
		this.parent = null;
		this.processTemplate = null;
		this.selProcess = paramTCComponentProcess;
		initialize();
	}

	public UserAssignAllTasksPanel(TCSession paramTCSession, Frame paramFrame,
			TCComponentTaskTemplate paramTCComponentTaskTemplate) {
		super(true);
		this.session = paramTCSession;
		this.parent = paramFrame;
		this.processTemplate = paramTCComponentTaskTemplate;
		this.selProcess = null;
		initialize();
	}

	public void run() {
		setVisible(true);
	}

	public void open(TCComponentTaskTemplate paramTCComponentTaskTemplate) {
		this.processTemplate = paramTCComponentTaskTemplate;
		this.resourcesPanel.open(this.processTemplate);
		
		
		if (this.selProcess != null)
			this.resourcesPanel.applyProcess(this.selProcess);
		try {
			TCComponent[] arrayOfTCComponent = getAssignmentLists(this.assignedCheckbox.isSelected());
			this.assignmentList.removeAllItems();
			if (arrayOfTCComponent != null) {
				String[] arrayOfString = createRenderIcons(arrayOfTCComponent);
				this.assignmentList.addItems(arrayOfTCComponent, arrayOfString);
				this.assignmentList.sort(arrayOfString);
				this.assignmentList.validate();
			}
			
		} catch (Exception exception) {
			exception.printStackTrace();
			MessageBox.post(exception);
		}
	}

	public void clearPanel() {
		this.assignmentList.removeAllItems();
		this.resourcesPanel.clear();
	}

	private void initialize() {
		this.reg = Registry.getRegistry(this);
		AIFUtility.getActiveDesktop().setCursor(new Cursor(3));
		this.session.setStatus(this.reg.getString("loadingAssignDialog.MSG"));
		JLabel jLabel = new JLabel(this.reg.getString("assignmentList.NAME"));
		this.assignmentList = new iComboBox(false);
		this.assignmentList.setMaximumRowCount(10);
		this.assignmentList.getTextField().setColumns(23);
		this.assignmentList.setEnabled(true);
		this.assignedCheckbox = new JCheckBox(this.reg.getString("showAllLists"));
		this.assignedCheckbox.setEnabled(true);
		this.assignedCheckbox.setSelected(false);
		this.assignedCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				try {
					TCComponent[] arrayOfTCComponent = UserAssignAllTasksPanel.this.getAssignmentLists(UserAssignAllTasksPanel.this.assignedCheckbox.isSelected());
					if (arrayOfTCComponent != null) {
						UserAssignAllTasksPanel.this.assignmentList.removeAllItems();
						String[] arrayOfString = UserAssignAllTasksPanel.this.createRenderIcons(arrayOfTCComponent);
						UserAssignAllTasksPanel.this.assignmentList.addItems(arrayOfTCComponent, arrayOfString);
						UserAssignAllTasksPanel.this.assignmentList.sort(arrayOfString);
						UserAssignAllTasksPanel.this.assignmentList.validate();
					}
				} catch (Exception exception) {
				}
			}
		});
		this.resourcesPanel = new UserAssignmentListPanel(this.parent, this.session, true);
		
		
		try {
			if (this.processTemplate != null) {
				TCComponent[] arrayOfTCComponent = getAssignmentLists(this.assignedCheckbox.isSelected());
				if (arrayOfTCComponent != null) {
					this.assignmentList.removeAllItems();
					String[] arrayOfString = createRenderIcons(arrayOfTCComponent);
					this.assignmentList.addItems(arrayOfTCComponent, arrayOfString);
					this.assignmentList.sort(arrayOfString);
					this.assignmentList.validate();
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		this.assignmentList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				if (UserAssignAllTasksPanel.this.assignmentList.getSelectedItemCount() < 1)
					return;
				Object object = UserAssignAllTasksPanel.this.assignmentList.getSelectedItem();
				if (object instanceof TCComponentAssignmentList) {
					UserAssignAllTasksPanel.this.currentList = (TCComponentAssignmentList) object;
					UserAssignAllTasksPanel.this.resourcesPanel.load(UserAssignAllTasksPanel.this.currentList);
				}
			}
		});
		JPanel jPanel1 = new JPanel(new PropertyLayout(20, 10, 2, 2, 10, 2));
		jPanel1.add("1.1.right.top.preferred.preferred", jLabel);
		jPanel1.add("1.2.center.center.preferred.preferred", this.assignmentList);
		jPanel1.add("1.3.center.center.preferred.preferred", this.assignedCheckbox);
		JPanel jPanel2 = new JPanel(new VerticalLayout());
		jPanel2.add("top", new Separator());
		jPanel2.add("top", jPanel1);
		jPanel2.add("bottom", new Separator());
		setLayout(new VerticalLayout());
		add("top", jPanel2);
		add("unbound.bind", this.resourcesPanel);
		AIFUtility.getActiveDesktop().setCursor(new Cursor(0));
		this.session.setReadyStatus();
	}

	private TCComponent[] getAssignmentLists(boolean paramBoolean) throws Exception {
		TCComponent[] arrayOfTCComponent = null;
		try {
			if (paramBoolean) {
				TCComponentAssignmentListType tCComponentAssignmentListType = (TCComponentAssignmentListType) this.session.getTypeComponent("EPMAssignmentList");
				arrayOfTCComponent = tCComponentAssignmentListType.extent();
			} else {
				this.processTemplate.refresh();
				arrayOfTCComponent = this.processTemplate.getTCProperty("assignment_lists").getReferenceValueArray();
			}
			TCComponentType.cacheTCPropertiesSet(arrayOfTCComponent, new String[] { "shared", "list_name", "list_desc" }, true);
		} catch (Exception exception) {
			exception.printStackTrace();
			throw exception;
		}
		return arrayOfTCComponent;
	}

	public ResourceMember[] getSelectedResources() throws TCException {
		try {
			return this.resourcesPanel.getSelectedResources();
		} catch (TCException tCException) {
			throw tCException;
		}
	}

	private String[] createRenderIcons(TCComponent[] paramArrayOfTCComponent) {
		String[] arrayOfString = null;
		int i = (paramArrayOfTCComponent != null) ? paramArrayOfTCComponent.length : 0;
		if (i > 0) {
			arrayOfString = new String[i];
			String[] arrayOfString1 = { "shared" };
			String[][] arrayOfString2 = null;
			try {
				arrayOfString2 = TCComponentType.getPropertiesSet(Arrays.asList(paramArrayOfTCComponent),
						arrayOfString1);
				String str = null;
				for (int b = 0; b < i; b++) {
					str = arrayOfString2[b][0];
					if (str.equalsIgnoreCase("Y") || str.equalsIgnoreCase("True")) {
						arrayOfString[b] = "sharedList";
					} else {
						arrayOfString[b] = "blank";
					}
				}
			} catch (Exception exception) {
				MessageBox messageBox = new MessageBox(exception);
				messageBox.setVisible(true);
			}
		}
		return arrayOfString;
	}

	public TCComponentAssignmentList getListToSave() {
		return this.resourcesPanel.getListToSave();
	}

	public String getNameForNewList() {
		return this.resourcesPanel.getNameForNewList();
	}

	public TCComponentAssignmentList getSelectedList() {
		return this.currentList;
	}

	public void setTargetObjects(TCComponent[] paramArrayOfTCComponent) {
		this.resourcesPanel.setTargetObjects(paramArrayOfTCComponent);
	}

	public void saveModifyAssignmentList() {
		TCComponentAssignmentList tCComponentAssignmentList = getListToSave();
		if (tCComponentAssignmentList != null) {
			TCComponentTaskTemplate tCComponentTaskTemplate = null;
			try {
				tCComponentTaskTemplate = tCComponentAssignmentList.getProcessTemplate();
				CreateAssignmentListOperation createAssignmentListOperation = new CreateAssignmentListOperation(
						this.session, AIFUtility.getActiveDesktop(), tCComponentAssignmentList,
						tCComponentAssignmentList.getName(), tCComponentAssignmentList.getDescription(),
						tCComponentTaskTemplate, tCComponentAssignmentList.isShared(), getSelectedResources());
				this.session.queueOperationLater(createAssignmentListOperation);
			} catch (Exception exception) {
				exception.printStackTrace();
				MessageBox.post(exception);
			}
		} else {
			String str = getNameForNewList();
			if (str != null && str.length() > 0) {
				TCComponentAssignmentList tCComponentAssignmentList1 = getSelectedList();
				TCComponentTaskTemplate tCComponentTaskTemplate = null;
				try {
					tCComponentTaskTemplate = tCComponentAssignmentList1.getProcessTemplate();
					CreateAssignmentListOperation createAssignmentListOperation = new CreateAssignmentListOperation(
							this.session, AIFUtility.getActiveDesktop(), null, str,
							tCComponentAssignmentList1.getDescription(), tCComponentTaskTemplate,
							tCComponentAssignmentList1.isShared(), getSelectedResources());
					this.session.queueOperationLater(createAssignmentListOperation);
				} catch (Exception exception) {
					exception.printStackTrace();
					MessageBox.post(exception);
				}
			}
		}
	}
	
}
