package com.teamcenter.rac.workflow.commands.adhoc;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.commands.addresslist.AddressMember;
import com.teamcenter.rac.commands.addresslist.MemberListRenderer;
import com.teamcenter.rac.common.GenericUserSelectionPanel;
import com.teamcenter.rac.common.TCTreeNode;
import com.teamcenter.rac.common.organization.OrgObject;
import com.teamcenter.rac.common.organization.OrgTreePanel;
import com.teamcenter.rac.common.organization.OrgUserSelectionPanel;
import com.teamcenter.rac.common.organization.ProjectTeamSelectionPanel;
import com.teamcenter.rac.kernel.TCAttachmentScope;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentAliasList;
import com.teamcenter.rac.kernel.TCComponentAliasListType;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentGroupMemberType;
import com.teamcenter.rac.kernel.TCComponentGroupType;
import com.teamcenter.rac.kernel.TCComponentProfile;
import com.teamcenter.rac.kernel.TCComponentResourcePool;
import com.teamcenter.rac.kernel.TCComponentResourcePoolType;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentSignoff;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCSignoffOriginType;
import com.teamcenter.rac.kernel.TCTaskState;
import com.teamcenter.rac.util.ArraySorter;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Separator;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.scombobox.SearchableComboBox;
import com.teamcenter.services.rac.workflow.WorkflowService;
import com.teamcenter.services.rac.workflow._2008_06.Workflow;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
//import org.apache.log4j.Logger;

public class UserSignoffEditPanel extends JPanel {
	protected Object component;

	protected TCSession session;

	protected TCComponentUserType userType;

	protected TCComponentGroupType groupType;

	protected TCComponentGroupMemberType groupMemberType;

	protected TCComponentResourcePoolType resourcePoolType;

	protected TCComponentAliasListType aliasType;

	protected TCComponent[] addressListObjects;

	protected UserGenericUserSelectionPanel genericUserSelectionPanel;

	protected JList memberList;

	protected SearchableComboBox addressListCombo;

	protected JLabel addressListField;

	protected JPanel addressListPanel;

	protected JLabel iconLabel;

	protected Registry r = Registry.getRegistry(this);

	private int actionType = 0;

	private ButtonGroup buttonGroup;

	private JRadioButton[] actionButtons;

	private JLabel actionLabel = null;

	private JPanel actionPanel = null;

	private JPanel requirePanel = null;

	public JCheckBox requireCheckBox = null;

	JScrollPane listScroller = null;

	DefaultListModel actionListModel = null;

	private OrgUserSelectionPanel orgUserSelectionPanel;

	private OrgTreePanel orgTreePanel;

	protected AddressMember[] newMembers;

	static final int ACTION_PERFORM = 0;

	static final int ACTION_REVIEW = 1;

	static final int ACTION_ACKNOWLEDGE = 2;

	static final int ACTION_NOTIFY = 3;

	static final int ACTION_REQUIRED_REVIEW = 4;

	static final int ACTION_REQUIRED_ACKNOWLEDGE = 5;

	protected TCComponentUser currentUser;

	private ProjectTeamSelectionPanel projTeamSelPanel;

	private static Set assignedUserSignoffs;

	private boolean subGroupAllowedChangedFlag = false;

//	  private static final Logger logger = Logger.getLogger(SignoffEditPanel.class);
		
	public UserSignoffEditPanel(TCSession paramTCSession, SignoffTree signoffTree, String nodeName, String projectId) {
		// recompile 20220323143000 : START
		// new add Constructor Parameter signoffTree
		// 20220323143000 : END
		this.session = paramTCSession;
		try {
			this.userType = (TCComponentUserType) this.session.getTypeComponent("User");
			this.groupType = (TCComponentGroupType) this.session.getTypeComponent("Group");
			this.aliasType = (TCComponentAliasListType) this.session.getTypeComponent("ImanAliasList");
			this.groupMemberType = (TCComponentGroupMemberType) this.session.getTypeComponent("GroupMember");
			this.resourcePoolType = (TCComponentResourcePoolType) this.session.getTypeComponent("ResourcePool");
			this.currentUser = this.session.getUser();
		} catch (TCException tCException) {
			tCException.printStackTrace();
//	      logger.error("Exception", tCException);
		}		
		// recompile 20220323143000 : START
		this.genericUserSelectionPanel = new UserGenericUserSelectionPanel(this.session, signoffTree, nodeName, projectId);
		// 20220323143000 : END
		this.orgUserSelectionPanel = this.genericUserSelectionPanel.getOrgUserSelectionPanel();
		this.orgTreePanel = this.orgUserSelectionPanel.getOrgTreePanel();
		this.projTeamSelPanel = this.genericUserSelectionPanel.getProjectTeamSelectionPanel();
		TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent param1TreeSelectionEvent) {
				UserSignoffEditPanel.this.genericUserSelectionPanel.setAssignedUserSignOffs(assignedUserSignoffs);
				String str = UserSignoffEditPanel.this.genericUserSelectionPanel.checkSignOffsSelectedAreValid();
				if (str != null && !str.equals("")) {
					UserSignoffEditPanel.this.disableButtons(str);
				} else {
					JPanel jPanel = (JPanel) UserSignoffEditPanel.this.getParent().getParent().getParent().getParent();
					if (jPanel instanceof UserAdhocSignoffsPanel) {
						UserAdhocSignoffsPanel adhocSignoffsPanel = (UserAdhocSignoffsPanel) jPanel;
						TCComponentTask tCComponentTask = adhocSignoffsPanel.task;
						TCTaskState tCTaskState = null;
						boolean bool = false;
						try {
							tCTaskState = tCComponentTask.getState();
							bool = tCComponentTask.isValidPerformer();
						} catch (Exception exception) {
							exception.printStackTrace();
//							logger.error(exception.getClass().getName(), exception);
						}
						if (bool && tCTaskState == TCTaskState.STARTED)
							UserSignoffEditPanel.this.validateButtons();
					} else {
						UserSignoffEditPanel.this.genericUserSelectionPanel.enableButtons();
					}
				}
			}
		};
		this.orgTreePanel.getOrgTree().addTreeSelectionListener(treeSelectionListener);
		this.projTeamSelPanel.getTeamRoleUserTree().addTreeSelectionListener(treeSelectionListener);
		this.actionButtons = new JRadioButton[3];
		for (int b = 0; b < 3; b++) {
			JRadioButton jRadioButton = new JRadioButton();
			this.actionButtons[b] = jRadioButton;
			jRadioButton.setVisible(false);
		}
		this.actionPanel = new JPanel(new PropertyLayout(10, 10, 5, 5, 5, 5));
		this.actionLabel = new JLabel(this.r.getString("action"));
		this.actionPanel.add(this.actionLabel);
		this.actionPanel.add("1.1.right.center.preferred.preferred", this.actionLabel);
		this.actionPanel.add("1.2.left.center.preferred.preferred", this.actionButtons[0]);
		this.actionPanel.add("1.3.left.center.preferred.preferred", this.actionButtons[1]);
		this.actionPanel.add("1.4.left.center.preferred.preferred", this.actionButtons[2]);
		this.requirePanel = new JPanel();
		this.requireCheckBox = new JCheckBox("Required", false);
		this.requireCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent param1ItemEvent) {
				JCheckBox jCheckBox = (JCheckBox) param1ItemEvent.getItem();
				if (jCheckBox.isSelected()) {
					UserSignoffEditPanel.this.requireCheckBox.setSelected(true);
					if (UserSignoffEditPanel.this.actionType == 1) {
						UserSignoffEditPanel.this.actionType = 4;
					} else if (UserSignoffEditPanel.this.actionType == 2) {
						UserSignoffEditPanel.this.actionType = 5;
					}
				} else {
					UserSignoffEditPanel.this.requireCheckBox.setSelected(false);
					if (UserSignoffEditPanel.this.actionType == 4) {
						UserSignoffEditPanel.this.actionType = 1;
					} else if (UserSignoffEditPanel.this.actionType == 5) {
						UserSignoffEditPanel.this.actionType = 2;
					}
				}
			}
		});
		this.requirePanel.add(this.requireCheckBox);
		this.actionPanel.add("1.5.right.center.preferred.preferred", this.requirePanel);
		this.genericUserSelectionPanel.add(this.actionPanel, "South");
		this.actionLabel.setVisible(false);
		setLayout(new VerticalLayout());
		this.iconLabel = new JLabel("", 0);
		add("top.nobind.left", this.iconLabel);
		add("top.bind", new Separator());
		JPanel jPanel = getPanel();
		if (jPanel != null)
			add("unbound.bind", jPanel);
	}

	public void setComponent(SignoffTreeNode paramSignoffTreeNode, Object paramObject, TCComponentTask paramTCComponentTask) {
		this.component = paramObject;
		boolean bool = !(!(paramObject instanceof TCComponentAliasList) && paramObject != TCSignoffOriginType.ALIASLIST && paramObject != TCSignoffOriginType.ADDRESSLIST);
		this.genericUserSelectionPanel.setVisible((!bool && paramObject != null && paramObject != TCSignoffOriginType.PROFILE));
		this.addressListPanel.setVisible(bool);
		ArrayList arrayList = new ArrayList();
		try {
			TCComponent[] arrayOfTCComponent;
			for (arrayOfTCComponent = paramTCComponentTask.getAttachments(TCAttachmentScope.GLOBAL, 1); arrayOfTCComponent == null || arrayOfTCComponent.length == 0; arrayOfTCComponent = paramTCComponentTask.getAttachments(TCAttachmentScope.GLOBAL, 1)) {
				paramTCComponentTask = paramTCComponentTask.getParent();
				if (paramTCComponentTask == null)
					break;
			}
			for (int b = 0; arrayOfTCComponent != null && b < arrayOfTCComponent.length; b++)
				arrayList.add(arrayOfTCComponent[b]);
		} catch (TCException tCException) {
			tCException.printStackTrace();
//			logger.error(tCException.getClass().getName(), tCException);
		}
		this.genericUserSelectionPanel.setTargetObjects(arrayList);
		setButtonValidationProps(paramObject, paramSignoffTreeNode);
		clearProjTreeSelection();
		setAddressListPanel();
		if (paramObject != null && !(paramObject instanceof TCComponentAliasList)) {
			TCComponentUser tCComponentUser = this.currentUser;
			TCComponentGroup tCComponentGroup = null;
			TCComponentRole tCComponentRole = null;
			boolean bool1 = false;
			try {
				if (paramObject instanceof TCComponentSignoff) {
					SignoffTreeNode signoffTreeNode = (SignoffTreeNode) paramSignoffTreeNode.getParent();
					TCComponentGroup tCComponentGroup1 = null;
					TCComponentRole tCComponentRole1 = null;
					if (signoffTreeNode.getUserObject() instanceof TCComponentProfile) {
						TCComponentProfile tCComponentProfile = (TCComponentProfile) signoffTreeNode.getTCComponent();
						tCComponentGroup1 = tCComponentProfile.getGroup();
						tCComponentRole1 = tCComponentProfile.getRole();
						bool1 = tCComponentProfile.isSubgroupAllowed();
					} else {
						tCComponentGroup1 = null;
						tCComponentRole1 = null;
						bool1 = true;
					}
					TCComponentSignoff tCComponentSignoff = (TCComponentSignoff) paramObject;
					TCComponent tCComponent = tCComponentSignoff.getMember();
					if (tCComponent instanceof TCComponentGroupMember) {
						TCComponentGroupMember tCComponentGroupMember = (TCComponentGroupMember) tCComponent;
						tCComponentGroup = tCComponentGroupMember.getGroup();
						tCComponentRole = tCComponentGroupMember.getRole();
						tCComponentUser = tCComponentGroupMember.getUser();
					} else if (tCComponent instanceof TCComponentResourcePool) {
						TCComponentResourcePool tCComponentResourcePool = (TCComponentResourcePool) tCComponent;
						tCComponentGroup = tCComponentResourcePool.getGroup();
						tCComponentRole = tCComponentResourcePool.getRole();
						tCComponentUser = null;
						if (tCComponentResourcePool.isAllMemberResPool()) {
							this.genericUserSelectionPanel.selectAllmemberResPoolRadio(true);
							this.genericUserSelectionPanel.selectAnyMemberResPoolRadio(false);
						} else {
							this.genericUserSelectionPanel.selectAllmemberResPoolRadio(false);
							this.genericUserSelectionPanel.selectAnyMemberResPoolRadio(true);
						}
						if (tCComponentGroup == null) {
							this.genericUserSelectionPanel.selectAnyGrpRadio(true);
							this.genericUserSelectionPanel.selectSpecificGrpRadio(false);
						} else {
							this.genericUserSelectionPanel.selectAnyGrpRadio(false);
							this.genericUserSelectionPanel.selectSpecificGrpRadio(true);
						}
					}
					TCSignoffOriginType tCSignoffOriginType = tCComponentSignoff.getOriginType();
					if (tCSignoffOriginType == TCSignoffOriginType.PROFILE
							|| tCSignoffOriginType == TCSignoffOriginType.ALIASLIST
							|| tCSignoffOriginType == TCSignoffOriginType.ADDRESSLIST)
						if (tCSignoffOriginType != TCSignoffOriginType.ALIASLIST
								&& tCSignoffOriginType != TCSignoffOriginType.ADDRESSLIST) {
							this.genericUserSelectionPanel.setEnabled(true);
						} else {
							this.genericUserSelectionPanel.setEnabled(false);
						}
					setOrgPanelSelection(tCComponentGroup1, tCComponentRole1, tCComponentUser, bool1, tCComponentGroup,tCComponentRole, tCComponentUser, paramTCComponentTask, true);
					setProjectTeamFilter(null, null, false);
				} else if (paramObject instanceof TCComponentResourcePool) {
					TCComponentResourcePool tCComponentResourcePool = (TCComponentResourcePool) paramObject;
					tCComponentGroup = tCComponentResourcePool.getGroup();
					tCComponentRole = tCComponentResourcePool.getRole();
					bool1 = true;
					setOrgPanelSelection(tCComponentGroup, tCComponentRole, tCComponentUser, bool1, tCComponentGroup, tCComponentRole, tCComponentUser, paramTCComponentTask, true);
					this.genericUserSelectionPanel.selectAllmemberResPoolRadio(true);
					this.genericUserSelectionPanel.selectAnyMemberResPoolRadio(false);
					if (tCComponentGroup == null) {
						this.genericUserSelectionPanel.selectAnyGrpRadio(true);
						this.genericUserSelectionPanel.selectSpecificGrpRadio(false);
					} else {
						this.genericUserSelectionPanel.selectAnyGrpRadio(false);
						this.genericUserSelectionPanel.selectSpecificGrpRadio(true);
					}
					setProjectTeamFilter(null, null, false);
				} else if (paramObject instanceof TCComponentProfile) {
					TCComponentProfile tCComponentProfile = (TCComponentProfile) paramObject;
					tCComponentGroup = tCComponentProfile.getGroup();
					tCComponentRole = tCComponentProfile.getRole();
					bool1 = tCComponentProfile.isSubgroupAllowed();
					setOrgPanelSelection(tCComponentGroup, tCComponentRole, tCComponentUser, bool1, tCComponentGroup, tCComponentRole, tCComponentUser, paramTCComponentTask, true);
					setProjectTeamFilter(tCComponentGroup, tCComponentRole, bool1);
				} else if (paramObject instanceof TCSignoffOriginType) {
					TCSignoffOriginType tCSignoffOriginType = (TCSignoffOriginType) paramObject;
					if (tCSignoffOriginType.getIntValue() != 0) {
						setOrgPanelSelection(tCComponentGroup, tCComponentRole, tCComponentUser, bool1, tCComponentGroup, tCComponentRole, tCComponentUser, paramTCComponentTask, true);
						setProjectTeamFilter(null, null, false);
					}
				}
			} catch (Exception exception) {
				clearOrgTreeSelection();
				exception.printStackTrace();
//				logger.error("Exception", exception);
			}
		}
		this.session.queueOperation(new AbstractAIFOperation() {
			public void executeOperation() {
				UserSignoffEditPanel.this.validateButtons();
			}
		});
	}

	public void setComponent(TCComponentAliasList paramTCComponentAliasList) {
		this.newMembers = null;
		if (paramTCComponentAliasList == null)
			return;
		this.addressListField.setText(paramTCComponentAliasList.toString());
		try {
			AddressMember[] arrayOfAddressMember = AddressMember.getAddressMembersForList(this.session,
					paramTCComponentAliasList);
			if (arrayOfAddressMember == null)
				return;
			this.newMembers = arrayOfAddressMember;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					DefaultListModel defaultListModel = (DefaultListModel) UserSignoffEditPanel.this.memberList.getModel();
					defaultListModel.removeAllElements();
					int i = UserSignoffEditPanel.this.newMembers.length;
					if (i > 0) {
						for (int j = i - 1; j >= 0; j--)
							UserSignoffEditPanel.this.addMemberToList(UserSignoffEditPanel.this.newMembers[j]);
						UserSignoffEditPanel.this.memberList.validate();
					}
				}
			});
		} catch (Exception exception) {
			exception.printStackTrace();
//			logger.error("Exception", exception);
		}
	}

	public void setNewComponent(TCTreeNode paramTCTreeNode, TCComponent paramTCComponent,
			TCComponentProfile paramTCComponentProfile, Vector paramVector, boolean paramBoolean1,
			boolean paramBoolean2, TCComponent[] paramArrayOfTCComponent) {
		this.addressListPanel.setVisible(false);
		this.genericUserSelectionPanel.setVisible(true);
		ArrayList arrayList = new ArrayList();
		int b;
		for (b = 0; paramArrayOfTCComponent != null && b < paramArrayOfTCComponent.length; b++)
			arrayList.add(paramArrayOfTCComponent[b]);
		if (arrayList != null && arrayList.size() > 0)
			this.genericUserSelectionPanel.setTargetObjects(arrayList);
		this.actionLabel.setVisible(true);
		this.buttonGroup = new ButtonGroup();
		for (b = 0; b < 3; b++)
			this.actionPanel.remove(this.actionButtons[b]);
		Vector vector = (Vector) paramVector.clone();
		int i;
		for (i = vector.size() - 1; i >= 0; i--) {
			final int j = ((Integer) vector.elementAt(i)).intValue();
			if (j == 4 || j == 5)
				vector.remove(i);
		}
		for (i = 0; i < vector.size(); i++) {
			JRadioButton jRadioButton = new JRadioButton();
			final int at = ((Integer) vector.elementAt(i)).intValue();
			this.actionButtons[i] = jRadioButton;
			jRadioButton.setText(translate2Text(at));
			jRadioButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent param1ActionEvent) {
					if (at == 1 && UserSignoffEditPanel.this.isSignoffRequired()) {
						UserSignoffEditPanel.this.actionType = 4;
					} else if (at == 2 && UserSignoffEditPanel.this.isSignoffRequired()) {
						UserSignoffEditPanel.this.actionType = 5;
					} else {
						UserSignoffEditPanel.this.actionType = at;
					}
				}
			});
			String str = "1." + (i + 2) + ".left.center.preferred.preferred";
			this.actionPanel.add(str, jRadioButton);
			this.buttonGroup.add(jRadioButton);
			jRadioButton.setVisible(true);
			if (i == 0)
				jRadioButton.setSelected(true);
		}
		i = ((Integer) paramVector.elementAt(0)).intValue();
		if (i == 1 && isSignoffRequired()) {
			this.actionType = 4;
		} else if (i == 2 && isSignoffRequired()) {
			this.actionType = 5;
		} else {
			this.actionType = i;
		}
		if (i == 0) {
			this.requireCheckBox.setVisible(false);
		} else {
			this.requireCheckBox.setVisible(true);
		}
		this.actionPanel.setVisible(true);
		this.actionPanel.revalidate();
		TCComponentUser tCComponentUser = null;
		setButtonValidationProps(paramTCComponent, paramTCTreeNode);
		clearProjTreeSelection();
		if (paramTCComponent != null && paramTCComponent instanceof TCComponentUser) {
			tCComponentUser = (TCComponentUser) paramTCComponent;
			setOrgPanelSelection(null, null, tCComponentUser, true, null, null, null, null, false);
			setProjectTeamFilter(null, null, false);
		} else if (paramTCComponent != null) {
			tCComponentUser = this.currentUser;
			TCComponentGroup tCComponentGroup = null;
			TCComponentRole tCComponentRole = null;
			boolean bool = false;
			try {
				if (paramTCComponent instanceof TCComponentGroupMember) {
					TCTreeNode tCTreeNode = (TCTreeNode) paramTCTreeNode.getParent();
					TCComponentGroup tCComponentGroup1 = null;
					TCComponentRole tCComponentRole1 = null;
					if (tCTreeNode.getUserObject() instanceof TCComponentProfile) {
						TCComponentProfile tCComponentProfile = (TCComponentProfile) tCTreeNode.getComponent();
						tCComponentGroup1 = tCComponentProfile.getGroup();
						tCComponentRole1 = tCComponentProfile.getRole();
						bool = tCComponentProfile.isSubgroupAllowed();
					} else {
						tCComponentGroup1 = null;
						tCComponentRole1 = null;
						bool = true;
					}
					TCComponentGroupMember tCComponentGroupMember = (TCComponentGroupMember) paramTCComponent;
					tCComponentGroup = tCComponentGroupMember.getGroup();
					tCComponentRole = tCComponentGroupMember.getRole();
					tCComponentUser = tCComponentGroupMember.getUser();
					setOrgPanelSelection(tCComponentGroup1, tCComponentRole1, tCComponentUser, bool, tCComponentGroup, tCComponentRole, tCComponentUser, null, true);
					setProjectTeamFilter(null, null, false);
				} else if (paramTCComponent instanceof TCComponentResourcePool) {
					TCTreeNode tCTreeNode = (TCTreeNode) paramTCTreeNode.getParent();
					TCComponentGroup tCComponentGroup1 = null;
					TCComponentRole tCComponentRole1 = null;
					if (tCTreeNode.getUserObject() instanceof TCComponentProfile) {
						TCComponentProfile tCComponentProfile = (TCComponentProfile) tCTreeNode.getComponent();
						tCComponentGroup1 = tCComponentProfile.getGroup();
						tCComponentRole1 = tCComponentProfile.getRole();
						bool = tCComponentProfile.isSubgroupAllowed();
					} else {
						tCComponentGroup1 = null;
						tCComponentRole1 = null;
						bool = true;
					}
					TCComponentResourcePool tCComponentResourcePool = (TCComponentResourcePool) paramTCComponent;
					tCComponentGroup = tCComponentResourcePool.getGroup();
					tCComponentRole = tCComponentResourcePool.getRole();
					tCComponentUser = null;
					if (tCComponentResourcePool.isAllMemberResPool()) {
						this.genericUserSelectionPanel.selectAllmemberResPoolRadio(true);
						this.genericUserSelectionPanel.selectAnyMemberResPoolRadio(false);
					} else {
						this.genericUserSelectionPanel.selectAllmemberResPoolRadio(false);
						this.genericUserSelectionPanel.selectAnyMemberResPoolRadio(true);
					}
					if (tCComponentGroup == null) {
						this.genericUserSelectionPanel.selectAnyGrpRadio(true);
						this.genericUserSelectionPanel.selectSpecificGrpRadio(false);
					} else {
						this.genericUserSelectionPanel.selectAnyGrpRadio(false);
						this.genericUserSelectionPanel.selectSpecificGrpRadio(true);
					}
					setOrgPanelSelection(tCComponentGroup1, tCComponentRole1, null, bool, tCComponentGroup, tCComponentRole, tCComponentUser, null, true);
					setProjectTeamFilter(null, null, false);
				} else if (paramTCComponent instanceof TCComponentProfile) {
					TCComponentProfile tCComponentProfile = (TCComponentProfile) paramTCComponent;
					tCComponentGroup = tCComponentProfile.getGroup();
					tCComponentRole = tCComponentProfile.getRole();
					bool = tCComponentProfile.isSubgroupAllowed();
					setOrgPanelSelection(tCComponentGroup, tCComponentRole, null, bool, tCComponentGroup, tCComponentRole, tCComponentUser, null, true);
					setProjectTeamFilter(tCComponentGroup, tCComponentRole, bool);
				}
			} catch (Exception exception) {
				clearOrgTreeSelection();
				exception.printStackTrace();
//				logger.error("Exception", exception);
			}
		}
		try {
			boolean bool = false;
			for (int b1 = 0; paramArrayOfTCComponent != null && b1 < paramArrayOfTCComponent.length; b1++) {
				if (paramArrayOfTCComponent[b1].getReferenceListProperty("project_list").length != 0) {
					bool = true;
					break;
				}
			}
			if (!bool)
				this.genericUserSelectionPanel.addAllUserProjects();
		} catch (TCException tCException) {
			tCException.printStackTrace();
//			Logger.getLogger(SignoffEditPanel.class).error(tCException.getLocalizedMessage(), tCException);
		}
		validateButtons();
	}

	public void resetComponentPanel() {
		this.addressListPanel.setVisible(false);
		this.genericUserSelectionPanel.setVisible(false);
	}

	public void setReadOnly(boolean paramBoolean) {
	}

	public void setIcon(Icon paramIcon) {
		this.iconLabel.setIcon(paramIcon);
	}

	protected JPanel getPanel() {
		this.addressListPanel = new JPanel(new VerticalLayout());
		JPanel jPanel1 = new JPanel(new HorizontalLayout());
		this.addressListCombo = new SearchableComboBox();
		this.addressListCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				Object object = UserSignoffEditPanel.this.addressListCombo.getSelectedItem();
				if (object instanceof TCComponentAliasList)
					UserSignoffEditPanel.this.setComponent((TCComponentAliasList) object);
			}
		});
		this.addressListField = new JLabel();
		jPanel1.add("left.nobind", new JLabel(String.valueOf(this.r.getString("addressList")) + ":"));
		jPanel1.add("unbound.bind", this.addressListCombo);
		DefaultListModel defaultListModel = new DefaultListModel();
		this.memberList = new JList(defaultListModel);
		this.memberList.setCellRenderer(new MemberListRenderer());
		JScrollPane jScrollPane = new JScrollPane(this.memberList);
		this.memberList.setVisibleRowCount(15);
		this.addressListPanel.add("top.bind", jPanel1);
		this.addressListPanel.add("unbound.bind", jScrollPane);
		JPanel jPanel2 = new JPanel(new VerticalLayout());
		jPanel2.add("unbound.bind", this.addressListPanel);
		jPanel2.add("unbound.bind", this.genericUserSelectionPanel);
		this.genericUserSelectionPanel.setVisible(false);
		if (this.addressListCombo.getItemCount() > 0)
			this.addressListCombo.setSelectedIndex(0);
		return jPanel2;
	}

	protected void setAddressListPanel() {
		if (this.addressListCombo != null) {
			try {
				if (this.aliasType != null && this.addressListObjects == null)
					this.addressListObjects = this.aliasType.extent();
				if (this.addressListObjects != null && this.addressListObjects.length > 0) {
					ArrayList arrayList = new ArrayList(this.addressListObjects.length);
					TCComponent[] arrayOfTCComponent;
					int i = (arrayOfTCComponent = this.addressListObjects).length;
					for (int b = 0; b < i; b++) {
						TCComponent tCComponent = arrayOfTCComponent[b];
						arrayList.add(tCComponent);
					}
					TCComponentType.getPropertiesSet(arrayList, new String[] { "alName" });
					ArraySorter.sort(this.addressListObjects);
					this.addressListCombo.setItems(this.addressListObjects, false);
					Object object = this.addressListCombo.getItemAt(0);
					if (object instanceof TCComponentAliasList)
						setComponent((TCComponentAliasList) object);
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	protected void setOrgPanelSelection(TCComponentGroup paramTCComponentGroup1, TCComponentRole paramTCComponentRole1,
			TCComponentUser paramTCComponentUser1, boolean paramBoolean1, TCComponentGroup paramTCComponentGroup2,
			TCComponentRole paramTCComponentRole2, TCComponentUser paramTCComponentUser2,
			TCComponentTask paramTCComponentTask, boolean paramBoolean2) {
		if (this.orgTreePanel != null && this.orgTreePanel.isShowing()) {
			boolean bool = false;
			bool = this.orgTreePanel.checkIfReloadTree(paramTCComponentGroup1, paramTCComponentRole1);
			if (!bool && this.subGroupAllowedChangedFlag)
				bool = true;
			this.subGroupAllowedChangedFlag = false;
			if (bool) {
				this.orgTreePanel.filterOrgTreeForProfile(paramTCComponentGroup1, paramTCComponentRole1,
						paramTCComponentUser1, paramBoolean1, paramTCComponentGroup2, paramTCComponentRole2,
						paramTCComponentUser2, paramBoolean2);
			} else {
				this.orgTreePanel.findAndSelectMatchingNode(paramTCComponentGroup2, paramTCComponentRole2, paramTCComponentUser2);
			}
		}
	}

	private void setProjectTeamFilter(TCComponentGroup paramTCComponentGroup, TCComponentRole paramTCComponentRole,
			boolean paramBoolean) {
		if (this.projTeamSelPanel != null)
			this.projTeamSelPanel.filterProjectTreeForProfile(paramTCComponentGroup, paramTCComponentRole, paramBoolean);
	}

	private void addMemberToList(AddressMember paramAddressMember) {
		DefaultListModel defaultListModel = (DefaultListModel) this.memberList.getModel();
		int i = defaultListModel.getSize();
		int j = paramAddressMember.getType();
		String str = paramAddressMember.toString();
		int b;
		for (b = 0; b < i; b++) {
			AddressMember addressMember = (AddressMember) defaultListModel.getElementAt(b);
			int k = addressMember.getType();
			String str1 = addressMember.toString();
			if (j < k || (j == k && str.compareTo(str1) <= 0))
				break;
		}
		defaultListModel.insertElementAt(paramAddressMember, b);
	}

	public TCComponent[] getNewMembers() {
		TCComponent tCComponent = null;
		ArrayList arrayList = new ArrayList();
		Object object = null;
		TCComponent[] arrayOfTCComponent = null;
		if (this.addressListPanel.isVisible()) {
			Object object1 = this.addressListCombo.getSelectedItem();
			if (object1 instanceof TCComponentAliasList && object1 != this.component) {
				tCComponent = (TCComponent) object1;
				arrayOfTCComponent = new TCComponent[] { tCComponent };
			}
			return arrayOfTCComponent;
		}
		OrgObject[] arrayOfOrgObject = this.genericUserSelectionPanel.getSelectedObjects();
		if (arrayOfOrgObject == null || arrayOfOrgObject.length == 0)
			return null;
		for (int b = 0; b < arrayOfOrgObject.length; b++) {
//			TCComponentResourcePool tCComponentResourcePool;
			TCComponentGroup tCComponentGroup = arrayOfOrgObject[b].getGroup();
			TCComponentRole tCComponentRole = arrayOfOrgObject[b].getRole();
			TCComponentUser tCComponentUser = arrayOfOrgObject[b].getUser();
			try {
				if (tCComponentUser != null) {
					TCComponentGroupMember[] arrayOfTCComponentGroupMember = this.groupMemberType.getGroupMembers(tCComponentUser, tCComponentGroup, tCComponentRole);
					if (arrayOfTCComponentGroupMember != null && arrayOfTCComponentGroupMember[0] != object)
						tCComponent = arrayOfTCComponentGroupMember[0];
				} else {
					int i = this.genericUserSelectionPanel.getActiveTabIndex();
					boolean bool = false;
					if (i == 0) {
						bool = this.orgUserSelectionPanel.getOrgTreePanel().isAnyGroupResourcePool();
					} else if (i == 1) {
						bool = this.projTeamSelPanel.getAnyGrpResourcePoolButton().isSelected();
					}
					if (tCComponentRole != null && bool)
						tCComponentGroup = null;
					Workflow.GroupRoleRef[] arrayOfGroupRoleRef = new Workflow.GroupRoleRef[1];
					arrayOfGroupRoleRef[0] = new Workflow.GroupRoleRef();
					(arrayOfGroupRoleRef[0]).groupTag = tCComponentGroup;
					(arrayOfGroupRoleRef[0]).roleTag = tCComponentRole;
					(arrayOfGroupRoleRef[0]).allowSubGroup = 1;
					if (i == 0) {
						if (this.orgUserSelectionPanel.isAllowAllTypeResoucePoolSelection() && this.orgTreePanel.isAllMembersResourcePool()) {
							(arrayOfGroupRoleRef[0]).isAllMembers = 1;
						} else {
							(arrayOfGroupRoleRef[0]).isAllMembers = 0;
						}
					} else if (i == 1) {
						if (this.projTeamSelPanel.isAllMembersResourcePool()) {
							(arrayOfGroupRoleRef[0]).isAllMembers = 1;
						} else {
							(arrayOfGroupRoleRef[0]).isAllMembers = 0;
						}
					}
					WorkflowService workflowService = WorkflowService.getService(this.session);
					Workflow.GetResourcePoolOutput getResourcePoolOutput = workflowService.getResourcePool(arrayOfGroupRoleRef);
					TCComponentResourcePool tCComponentResourcePool1 = (getResourcePoolOutput.resourcePoolInfo[0]).resourcePoolTag;
					if (object != tCComponentResourcePool1)
						tCComponent = tCComponentResourcePool1;
				}
			} catch (Exception exception) {
				exception.printStackTrace();
//				logger.error("Exception", exception);
			}
			arrayList.add(tCComponent);
		}
		return (arrayList.size() > 0) ? (TCComponent[]) arrayList.toArray(new TCComponent[arrayList.size()]) : null;
	}

	public int getActionType() {
		return this.actionType;
	}

	public void enableButtons() {
		JPanel jPanel = (JPanel) getParent().getParent().getParent().getParent();
		if (jPanel instanceof UserAdhocSignoffsPanel) {
			UserAdhocSignoffsPanel adhocSignoffsPanel = (UserAdhocSignoffsPanel) jPanel;
			TCComponentTask tCComponentTask = adhocSignoffsPanel.task;
			try {
				if (tCComponentTask != null && tCComponentTask.isValidPerformer())
					this.genericUserSelectionPanel.enableButtons();
			} catch (Exception exception) {
				exception.printStackTrace();
//				logger.error(exception.getClass().getName(), exception);
			}
		} else {
			this.genericUserSelectionPanel.enableButtons();
		}
	}

	public void disableButtons(String paramString) {
		this.genericUserSelectionPanel.disableButtons(paramString);
	}

	public void setButtonValidationProps(Object paramObject, DefaultMutableTreeNode paramDefaultMutableTreeNode) {
		try {
			this.genericUserSelectionPanel.setSelectedSignoffGroupMember(null);
			if (paramObject instanceof TCComponentGroupMember || paramObject instanceof TCComponentResourcePool) {
				this.genericUserSelectionPanel.setSignOffType("TCComponentGroupMember");
				Object object = null;
				int i = 0;
				if (paramDefaultMutableTreeNode instanceof TCTreeNode) {
					TCTreeNode tCTreeNode = (TCTreeNode) paramDefaultMutableTreeNode.getParent();
					object = tCTreeNode.getUserObject();
					i = tCTreeNode.getChildCount();
				} else if (paramDefaultMutableTreeNode instanceof SignoffTreeNode) {
					SignoffTreeNode signoffTreeNode = (SignoffTreeNode) paramDefaultMutableTreeNode.getParent();
					object = signoffTreeNode.getUserObject();
					i = signoffTreeNode.getChildCount();
				}
				if (object instanceof TCComponentProfile) {
					TCComponentProfile tCComponentProfile = (TCComponentProfile) object;
					this.genericUserSelectionPanel.setAssignedUserSignOffs(assignedUserSignoffs);
					int j = tCComponentProfile.getNumberToSignoff();
					this.genericUserSelectionPanel.setSignoffRequired(j);
					int k = j - i;
					this.genericUserSelectionPanel.setSelectedProfileChildCount(i);
					this.genericUserSelectionPanel.setNumSignOffsPending(k);
					this.genericUserSelectionPanel.setProfileRoleComp(tCComponentProfile.getRole());
					this.genericUserSelectionPanel.setProfileGroupComp(tCComponentProfile.getGroup());
					this.genericUserSelectionPanel.isSubGroupsAllowed(tCComponentProfile.isSubgroupAllowed());
					this.orgTreePanel.getOrgTree().getSelectionModel().setSelectionMode(1);
					this.projTeamSelPanel.getTeamRoleUserTree().getSelectionModel().setSelectionMode(1);
					if (tCComponentProfile.getGroup() != null) {
						this.orgTreePanel.getAnyGrpResourcePoolButton().setEnabled(false);
						this.orgTreePanel.getSpecificGrpResourcePoolButton().setSelected(true);
						this.projTeamSelPanel.getSpecificGrpResourcePoolButton().setSelected(true);
						this.projTeamSelPanel.getAnyGrpResourcePoolButton().setEnabled(false);
					} else {
						this.orgTreePanel.getAnyGrpResourcePoolButton().setEnabled(true);
						this.orgTreePanel.getAnyGrpResourcePoolButton().setSelected(true);
						this.projTeamSelPanel.getAnyGrpResourcePoolButton().setSelected(true);
						this.projTeamSelPanel.getAnyGrpResourcePoolButton().setEnabled(true);
					}
				} else {
					setDefaultProperties((DefaultMutableTreeNode) paramDefaultMutableTreeNode.getParent(), 1);
					if (paramObject instanceof TCComponentGroupMember)
						this.genericUserSelectionPanel.setSelectedSignoffGroupMember((TCComponentGroupMember) paramObject);
				}
			} else if (paramObject instanceof TCComponentSignoff) {
				this.genericUserSelectionPanel.setSignOffType("TCComponentSignoff");
				TCComponentSignoff tCComponentSignoff = (TCComponentSignoff) paramObject;
				TCSignoffOriginType tCSignoffOriginType = tCComponentSignoff.getOriginType();
				if (tCSignoffOriginType == TCSignoffOriginType.PROFILE) {
					SignoffTreeNode signoffTreeNode = (SignoffTreeNode) paramDefaultMutableTreeNode.getParent();
					TCComponentProfile tCComponentProfile = (TCComponentProfile) signoffTreeNode.getUserObject();
					this.genericUserSelectionPanel.setAssignedUserSignOffs(assignedUserSignoffs);
					int i = tCComponentProfile.getNumberToSignoff();
					this.genericUserSelectionPanel.setSignoffRequired(i);
					int j = i - signoffTreeNode.getChildCount();
					this.genericUserSelectionPanel.setSelectedProfileChildCount(signoffTreeNode.getChildCount());
					this.genericUserSelectionPanel.setNumSignOffsPending(j);
					this.genericUserSelectionPanel.setProfileRoleComp(tCComponentProfile.getRole());
					this.genericUserSelectionPanel.setProfileGroupComp(tCComponentProfile.getGroup());
					this.genericUserSelectionPanel.isSubGroupsAllowed(tCComponentProfile.isSubgroupAllowed());
					this.orgTreePanel.getOrgTree().getSelectionModel().setSelectionMode(1);
					this.projTeamSelPanel.getTeamRoleUserTree().getSelectionModel().setSelectionMode(1);
					if (tCComponentProfile.getGroup() != null) {
						this.orgTreePanel.getAnyGrpResourcePoolButton().setEnabled(false);
						this.orgTreePanel.getSpecificGrpResourcePoolButton().setSelected(true);
						this.projTeamSelPanel.getSpecificGrpResourcePoolButton().setSelected(true);
						this.projTeamSelPanel.getAnyGrpResourcePoolButton().setEnabled(false);
					} else {
						this.orgTreePanel.getAnyGrpResourcePoolButton().setEnabled(true);
						this.orgTreePanel.getAnyGrpResourcePoolButton().setSelected(true);
						this.projTeamSelPanel.getAnyGrpResourcePoolButton().setSelected(true);
						this.projTeamSelPanel.getAnyGrpResourcePoolButton().setEnabled(true);
					}
				} else {
					setDefaultProperties((DefaultMutableTreeNode) paramDefaultMutableTreeNode.getParent(), 1);
				}
				if (tCSignoffOriginType == TCSignoffOriginType.ALIASLIST || tCSignoffOriginType == TCSignoffOriginType.ADDRESSLIST)
					this.genericUserSelectionPanel.setSignOffType("AliasList");
				this.genericUserSelectionPanel.setSelectedSignoffGroupMember(tCComponentSignoff.getGroupMember());
			} else if (paramObject instanceof TCComponentProfile) {
				this.genericUserSelectionPanel.setSignOffType("TCComponentProfile");
				TCComponentProfile tCComponentProfile = (TCComponentProfile) paramObject;
				int i = tCComponentProfile.getNumberToSignoff();
				this.genericUserSelectionPanel.setAssignedUserSignOffs(assignedUserSignoffs);
				int j = i - paramDefaultMutableTreeNode.getChildCount();
				this.genericUserSelectionPanel.setSelectedProfileChildCount(paramDefaultMutableTreeNode.getChildCount());
				this.genericUserSelectionPanel.setNumSignOffsPending(j);
				this.genericUserSelectionPanel.setSignoffRequired(i);
				this.genericUserSelectionPanel.setProfileRoleComp(tCComponentProfile.getRole());
				this.genericUserSelectionPanel.setProfileGroupComp(tCComponentProfile.getGroup());
				boolean bool = this.genericUserSelectionPanel.getSubGroupsAllowed();
				if (bool != tCComponentProfile.isSubgroupAllowed())
					this.subGroupAllowedChangedFlag = true;
				this.genericUserSelectionPanel.isSubGroupsAllowed(tCComponentProfile.isSubgroupAllowed());
				this.orgTreePanel.getOrgTree().getSelectionModel().setSelectionMode(4);
				this.projTeamSelPanel.getTeamRoleUserTree().getSelectionModel().setSelectionMode(1);
				if (tCComponentProfile.getGroup() != null) {
					this.orgTreePanel.getAnyGrpResourcePoolButton().setEnabled(false);
					this.orgTreePanel.getSpecificGrpResourcePoolButton().setSelected(true);
					this.projTeamSelPanel.getSpecificGrpResourcePoolButton().setSelected(true);
					this.projTeamSelPanel.getAnyGrpResourcePoolButton().setEnabled(false);
				} else {
					this.orgTreePanel.getAnyGrpResourcePoolButton().setEnabled(true);
					this.orgTreePanel.getAnyGrpResourcePoolButton().setSelected(true);
					this.projTeamSelPanel.getAnyGrpResourcePoolButton().setSelected(true);
					this.projTeamSelPanel.getAnyGrpResourcePoolButton().setEnabled(true);
				}
			} else if (paramObject instanceof TCComponentAliasList) {
				this.genericUserSelectionPanel.setSignOffType("TCComponentAliasList");
				setDefaultProperties(paramDefaultMutableTreeNode, 4);
			} else {
				this.genericUserSelectionPanel.setSignOffType(null);
				setDefaultProperties(paramDefaultMutableTreeNode, 4);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
//			logger.error("Exception", exception);
		}
	}

	private void setDefaultProperties(DefaultMutableTreeNode paramDefaultMutableTreeNode, int paramInt) {
		this.genericUserSelectionPanel.setNumSignOffsPending(10000000);
		this.genericUserSelectionPanel.setSignoffRequired(10000000);
		this.genericUserSelectionPanel.setSelectedProfileChildCount(0);
		this.genericUserSelectionPanel.setProfileRoleComp(null);
		this.genericUserSelectionPanel.setProfileGroupComp(null);
		this.genericUserSelectionPanel.isSubGroupsAllowed(true);
		this.orgTreePanel.enableResourcePoolSection(true);
		this.orgTreePanel.getOrgTree().getSelectionModel().setSelectionMode(paramInt);
		this.projTeamSelPanel.getTeamRoleUserTree().getSelectionModel().setSelectionMode(paramInt);
	}

	public void addToSelectedProfileChildCount(int paramInt) {
		this.genericUserSelectionPanel.addToSelectedProfileChildCount(paramInt);
		int i = this.genericUserSelectionPanel.getSelectedProfilePendingSignoffcount();
		this.genericUserSelectionPanel.setNumSignOffsPending(--i);
	}

	public void removeFromSelectedProfileChildCount(int paramInt) {
		this.genericUserSelectionPanel.removeFromSelectedProfileChildCount(paramInt);
		int i = this.genericUserSelectionPanel.getSelectedProfilePendingSignoffcount();
		this.genericUserSelectionPanel.setNumSignOffsPending(++i);
	}

	public void validateButtons() {
		this.genericUserSelectionPanel.validateButtons();
	}

	public void clearOrgTreeSelection() {
		if (this.orgTreePanel != null)
			this.orgTreePanel.getOrgTree().clearSelection();
	}

	public void clearProjTreeSelection() {
		if (this.projTeamSelPanel != null)
			this.projTeamSelPanel.getTeamRoleUserTree().clearSelection();
	}

	private String translate2Text(int paramInt) {
		return (paramInt == 0) ? this.r.getString("perform")
				: ((paramInt == 1 || paramInt == 4) ? this.r.getString("review")
						: ((paramInt == 3) ? this.r.getString("notify") : this.r.getString("acknowledge")));
	}

	public static void setAssignedUserSignOffs(Set paramSet) {
		assignedUserSignoffs = paramSet;
	}

	public void setAllResourcePool(boolean paramBoolean) {
		this.projTeamSelPanel.getAllMemberResPoolRadio().setEnabled(paramBoolean);
		this.orgTreePanel.getAllMemberResPoolRadio().setEnabled(paramBoolean);
	}

	public boolean isSignoffRequired() {
		return this.requireCheckBox.isSelected();
	}
}