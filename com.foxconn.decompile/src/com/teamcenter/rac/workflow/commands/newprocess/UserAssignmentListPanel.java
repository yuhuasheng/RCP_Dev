package com.teamcenter.rac.workflow.commands.newprocess;

import com.foxconn.decompile.util.CommonTools;
import com.foxconn.decompile.util.SPASUser;
import com.foxconn.decompile.util.TCUtil;
import com.foxconn.decompile.util.WorkGroup;
import com.foxconn.tcutils.constant.GroupEnum;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.common.AIFTreeNode;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.ExpansionRule;
import com.teamcenter.rac.common.TCTree;
import com.teamcenter.rac.common.TCTreeNode;
import com.teamcenter.rac.common.TCTypeRenderer;
import com.teamcenter.rac.kernel.ResourceMember;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentAssignmentList;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentProfile;
import com.teamcenter.rac.kernel.TCComponentResourcePool;
import com.teamcenter.rac.kernel.TCComponentRole;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTaskState;
import com.teamcenter.rac.kernel.TCTypeService;
import com.teamcenter.rac.kernel.TypeInfo;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.ConfirmationDialog;
import com.teamcenter.rac.util.FilterDocument;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Separator;
import com.teamcenter.rac.util.SplitPane;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.workflow.commands.newprocess.UserAssignmentListPanel.AssignmentListHelper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Stream;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
//import org.apache.log4j.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

public class UserAssignmentListPanel extends JPanel {
	private Frame parent = null;

	protected TCTree processTreeView = null;

	protected TCComponentTaskTemplate procTemplate = null;

	protected TCComponentAssignmentList assignmentList = null;

	protected SplitPane splitPane = null;

	protected JScrollPane treeScrollPane = null;

	protected UserSignoffEditPanel currentPanel;

	private TitledBorder memberPanelTitleBorder = null;

	private JPanel memberPanel = null;

	private JButton addButton = null;

	private JButton modifyButton = null;

	private JButton removeButton;

	private JButton copyButton;

	private JButton pasteButton;

	protected Registry reg;

	protected String quorumAllString;

	protected JLabel percentLabel;

	protected JLabel ackpercentLabel;

	protected JRadioButton quorumRadioButton;

	protected JRadioButton quorumPercentRadioButton;

	protected JCheckBox waitForUndecidedReviewersChk;

	protected JPanel reviewQuorumPanel;

	protected JPanel acknowQuorumPanel;

	protected AssignmentListHelper helperObject;

	protected boolean editingValue = false;

	protected JTextField quorumField;

	protected JTextField quorumPercentField;

	protected JTextField ackQuorumField;

	protected JTextField ackPercentQuorumField;

	protected JRadioButton ackRadioButton;

	protected JRadioButton ackPercentRadioButton;

	private TitledBorder revQuorumTitleBorder;

	private TitledBorder ackQuorumTitleBorder;

	private TCTreeNode nodeCopied = null;

	private boolean isModifiable = false;

	protected JCheckBox saveEdits = null;

	protected JCheckBox saveAsList = null;

	protected iTextField newListName = null;

	private boolean forAssign = false;

	private boolean allowAdhocUsers = true;

	private boolean allowResourcePools = true;

	private Vector completedTemplates = null;

	private Vector resourcePoolTasks = null;

	AssignmentListHelper helper_Obj;

	private TCComponent[] targetComps;

	private static final short ASMT_LIST_NAME_SIZE = 32;

	Set assignedUserSignoffs = null;

	protected TCSession session = null;

	public TCTree getProcessTreeView() {
		return processTreeView;
	}
//	private static final Logger logger = Logger.getLogger(UserAssignmentListPanel.class);

	public UserAssignmentListPanel(Frame paramFrame, TCSession paramTCSession, boolean paramBoolean) {
		this.parent = paramFrame;
		this.procTemplate = null;
		this.assignmentList = null;
		this.session = paramTCSession;
		this.forAssign = paramBoolean;
		this.completedTemplates = new Vector();
		initializePanel();
	}

	public UserAssignmentListPanel(Frame paramFrame, TCComponentTaskTemplate paramTCComponentTaskTemplate) {
		this.parent = paramFrame;
		this.procTemplate = paramTCComponentTaskTemplate;
		this.assignmentList = null;
		this.session = paramTCComponentTaskTemplate.getSession();
		this.forAssign = false;
		this.completedTemplates = new Vector();

		initializePanel();
	}

	public UserAssignmentListPanel(Frame paramFrame, TCComponentAssignmentList paramTCComponentAssignmentList) {
		this.parent = paramFrame;
		this.assignmentList = paramTCComponentAssignmentList;
		this.forAssign = false;
		this.completedTemplates = new Vector();
		initializePanel();
	}

	public void open(TCComponentTaskTemplate paramTCComponentTaskTemplate) {
		this.procTemplate = paramTCComponentTaskTemplate;
		if (this.session == null)
			this.session = paramTCComponentTaskTemplate.getSession();
		this.processTreeView.setEnabled(false);
		this.processTreeView.setSelectionRow(0);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UserAssignmentListPanel.this.processTreeView.setRootVisible(true);
				UserAssignmentListPanel.this.setTreeContents();
				TreePath[] arrayOfTreePath = new TreePath[1];
				if (UserAssignmentListPanel.this.processTreeView.getRowCount() > 0) {
					arrayOfTreePath[0] = UserAssignmentListPanel.this.processTreeView.getPathForRow(0);
					UserAssignmentListPanel.this.processTreeView.expandBelow(arrayOfTreePath);
					UserAssignmentListPanel.this.processTreeView
							.setSelectedNode(UserAssignmentListPanel.this.processTreeView.getRootNode());
				}
				UserAssignmentListPanel.this.processTreeView.setEnabled(true);
				
			}
		});
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// 流程节点自动指派人
				try {
					Thread.sleep(500);
					
					TCComponentGroup currentGroup = session.getCurrentGroup();
					String group = currentGroup.getProperty("full_name");
					System.out.println("group = " + group);

					if (group.contains("Monitor") || group.contains("dba")) {
						System.out.println("流程节点自动指派人 --");
						UserAssignmentListPanel.this.addUserNode();
						System.out.println("流程节点自动指派人 --结束");
					}

					System.out.println("流程节点自动指派人 --DT");
					addUserNode_DT();
					System.out.println("流程节点自动指派人 --DT结束");
					
				} catch (TCException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
	}

	public void load(TCComponentAssignmentList paramTCComponentAssignmentList) {
		this.quorumField.setVisible(false);
		this.quorumRadioButton.setVisible(false);
		this.quorumPercentRadioButton.setVisible(false);
		this.quorumPercentField.setVisible(false);
		this.percentLabel.setVisible(false);
		this.ackQuorumField.setVisible(false);
		this.ackPercentQuorumField.setVisible(false);
		this.ackRadioButton.setVisible(false);
		this.ackPercentRadioButton.setVisible(false);
		this.ackpercentLabel.setVisible(false);
		this.waitForUndecidedReviewersChk.setVisible(false);
		this.reviewQuorumPanel.setVisible(false);
		this.acknowQuorumPanel.setVisible(false);
		this.saveEdits.setSelected(false);
		this.saveEdits.setEnabled(false);
		this.saveAsList.setSelected(false);
		this.saveAsList.setEnabled(false);
		this.processTreeView.setEnabled(false);
		this.assignmentList = paramTCComponentAssignmentList;
		try {
			if (this.procTemplate == null)
				this.procTemplate = paramTCComponentAssignmentList.getProcessTemplate();
			if (this.session == null)
				this.session = paramTCComponentAssignmentList.getSession();
			this.isModifiable = paramTCComponentAssignmentList.okToModify();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UserAssignmentListPanel.this.loadAssignmentList();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						UserAssignmentListPanel.this.processTreeView.setEnabled(true);
					}
				});
			}
		});
	}

	public void applyProcess(TCComponentProcess paramTCComponentProcess) {
		final TCComponentProcess selectedProcess = paramTCComponentProcess;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UserAssignmentListPanel.this.applyProcessStatus(selectedProcess);
			}
		});
	}

	protected void initializePanel() {
		this.reg = Registry.getRegistry(this);
		setLayout(new VerticalLayout(7, 4, 4, 4, 4));
		this.splitPane = new SplitPane(0);
		JPanel jPanel1 = new JPanel(new HorizontalLayout(220, 0, 0, 0, 0));
		Dimension dimension1 = new Dimension(420, 20);
		jPanel1.setMaximumSize(dimension1);
		jPanel1.setAlignmentX(0.0F);
		JLabel jLabel1 = new JLabel(this.reg.getString("resources"));
		JLabel jLabel2 = new JLabel(this.reg.getString("action"));
		JLabel jLabel3 = new JLabel(this.reg.getString("required"));
		Font font = jLabel1.getFont();
		font = new Font(font.getName(), 1, font.getSize());
		jLabel1.setFont(font);
		jLabel2.setFont(font);
		jLabel3.setFont(font);
		Dimension dimension2 = new Dimension(60, 20);
		jLabel1.setMaximumSize(dimension2);
		jLabel1.setMinimumSize(dimension2);
		jLabel1.setPreferredSize(dimension2);
		jLabel1.setHorizontalAlignment(0);
		Dimension dimension3 = new Dimension(125, 20);
		jLabel2.setMaximumSize(dimension3);
		jLabel2.setMinimumSize(dimension3);
		jLabel2.setPreferredSize(dimension3);
		Dimension dimension4 = new Dimension(50, 20);
		jLabel3.setMaximumSize(dimension4);
		jLabel3.setMinimumSize(dimension4);
		jLabel3.setPreferredSize(dimension4);
		jPanel1.add("left.bind", jLabel1);
		jPanel1.add("left.bind", jLabel2);
		jPanel1.add("left.bind", jLabel3);
		this.treeScrollPane = new JScrollPane(20, 30);
		createTreeView();
		setTreeViewContents();
		JPanel jPanel2 = new JPanel();
		jPanel2.setLayout(new BoxLayout(jPanel2, 1));
		jPanel2.add(jPanel1);
		jPanel2.add(this.treeScrollPane);
		ResourceTreeCellRender resourceTreeCellRender = new ResourceTreeCellRender();
		ResourceTreeCellEditor resourceTreeCellEditor = new ResourceTreeCellEditor(this.processTreeView);
		this.processTreeView.setEditable(true);
		this.processTreeView.setCellRenderer(resourceTreeCellRender);
		this.processTreeView.setCellEditor(resourceTreeCellEditor);
		this.processTreeView.setVisibleRowCount(15);
		this.processTreeView.getSelectionModel().setSelectionMode(1);
		JPanel jPanel3 = new JPanel(new VerticalLayout());
		this.copyButton = new JButton(this.reg.getImageIcon("copy.ICON"));
		this.copyButton.setMargin(new Insets(0, 0, 0, 0));
		this.copyButton.setToolTipText(this.reg.getString("copyAction.TIP"));
		this.pasteButton = new JButton(this.reg.getImageIcon("paste.ICON"));
		this.pasteButton.setMargin(new Insets(0, 0, 0, 0));
		this.pasteButton.setToolTipText(this.reg.getString("pasteAction.TIP"));
		this.copyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				if (UserAssignmentListPanel.this.processTreeView == null)
					return;
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getSelectedNode();
				if (aIFTreeNode == null)
					return;
				Object object = aIFTreeNode.getUserObject();
				if (object != null && object instanceof UserAssignmentListPanel.AssignmentListHelper
						&& ((UserAssignmentListPanel.AssignmentListHelper) object).getNodeType() == 1)
					UserAssignmentListPanel.this.nodeCopied = (TCTreeNode) aIFTreeNode;
			}
		});
		this.pasteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getSelectedNode();
				if (UserAssignmentListPanel.this.nodeCopied != null) {
					int i = 2;
					if (UserAssignmentListPanel.this.forAssign
							&& UserAssignmentListPanel.this.isTaskCompleted((TCTreeNode) aIFTreeNode))
						i = ConfirmationDialog.post(UserAssignmentListPanel.this.parent,
								UserAssignmentListPanel.this.reg.getString("taskCompleted.TITLE"),
								UserAssignmentListPanel.this.reg.getString("taskHasCompleted.MSG"));
					if (i == 2) {
						UserAssignmentListPanel.AssignmentListHelper assignmentListHelper = (UserAssignmentListPanel.AssignmentListHelper) UserAssignmentListPanel.this.nodeCopied
								.getUserObject();
						if (UserAssignmentListPanel.this.isValidAddAction((TCTreeNode) aIFTreeNode,
								assignmentListHelper.getNodeComponent())
								&& UserAssignmentListPanel.this.verifyUserProfile((TCTreeNode) aIFTreeNode,
										assignmentListHelper.getNodeComponent())) {
							int j = UserAssignmentListPanel.this.currentPanel.getActionType();
							UserAssignmentListPanel.this.addMemberNode(aIFTreeNode,
									assignmentListHelper.getNodeComponent(), j);
							UserAssignmentListPanel.this.expandNode(aIFTreeNode);
							UserAssignmentListPanel.this.enableSaveEdits();
						}
					}
				} else {
					MessageBox.post(UserAssignmentListPanel.this.reg.getString("nothingWasCopied.MSG"),
							UserAssignmentListPanel.this.reg.getString("Information"), 2);
				}
			}
		});
		JPanel jPanel4 = new JPanel(new ButtonLayout());
		this.copyButton.setEnabled(false);
		this.pasteButton.setEnabled(false);
		jPanel4.add(this.copyButton);
		jPanel4.add(this.pasteButton);
		jPanel3.add("bottom.bind.center", setQuorum());
		jPanel3.add("bottom.center", jPanel4);
		jPanel3.add("unbound", jPanel2);
		this.memberPanel = new JPanel(new VerticalLayout(2, 2, 10, 2, 2));
		this.memberPanelTitleBorder = new TitledBorder(BorderFactory.createEtchedBorder(), "");
		this.currentPanel = new UserSignoffEditPanel(this.session);
		this.currentPanel.setVisible(false);
		JPanel jPanel5 = new JPanel(new ButtonLayout());
		this.addButton = new JButton(this.reg.getString("addButton"));
		this.addButton.setVisible(false);
		this.addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getSelectedNode();
				TCComponent[] arrayOfTCComponent = UserAssignmentListPanel.this.currentPanel.getNewMembers();
				for (int b = 0; arrayOfTCComponent != null && b < arrayOfTCComponent.length; b++) {
					int i = 2;
					if (UserAssignmentListPanel.this.forAssign
							&& UserAssignmentListPanel.this.isTaskCompleted((TCTreeNode) aIFTreeNode))
						i = ConfirmationDialog.post(UserAssignmentListPanel.this.parent,
								UserAssignmentListPanel.this.reg.getString("taskCompleted.TITLE"),
								UserAssignmentListPanel.this.reg.getString("taskHasCompleted.MSG"));
					if (i == 2 && UserAssignmentListPanel.this.isValidAddAction((TCTreeNode) aIFTreeNode,
							arrayOfTCComponent[b])) {
						int j = UserAssignmentListPanel.this.currentPanel.getActionType();
						UserAssignmentListPanel.this.addMemberNode(aIFTreeNode, arrayOfTCComponent[b], j);

						UserAssignmentListPanel.this.expandNode(aIFTreeNode);
						if (j == 4 || j == 5)
							UserAssignmentListPanel.this.currentPanel.requireCheckBox.setSelected(false);
						UserAssignmentListPanel.this.enableSaveEdits();
					}
				}
				TCComponent tCComponent = (TCComponent) aIFTreeNode.getComponent();
				if (tCComponent instanceof TCComponentProfile && arrayOfTCComponent != null) {
					UserAssignmentListPanel.this.currentPanel.addToSelectedProfileChildCount(arrayOfTCComponent.length);
					UserAssignmentListPanel.this.currentPanel.validateButtons();
				}
				UserAssignmentListPanel.this.currentPanel.clearOrgTreeSelection();

			}
		});
		this.modifyButton = new JButton(this.reg.getString("modify"));
		this.modifyButton.setVisible(false);
		this.modifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				AIFTreeNode aIFTreeNode1 = UserAssignmentListPanel.this.processTreeView.getSelectedNode();
				AIFTreeNode aIFTreeNode2 = (AIFTreeNode) aIFTreeNode1.getParent();
				TCComponent[] arrayOfTCComponent = UserAssignmentListPanel.this.currentPanel.getNewMembers();
				for (int b = 0; arrayOfTCComponent != null && b < arrayOfTCComponent.length; b++) {
					int i = 2;
					if (UserAssignmentListPanel.this.forAssign
							&& UserAssignmentListPanel.this.isTaskCompleted((TCTreeNode) aIFTreeNode1))
						i = ConfirmationDialog.post(UserAssignmentListPanel.this.parent,
								UserAssignmentListPanel.this.reg.getString("taskCompleted.TITLE"),
								UserAssignmentListPanel.this.reg.getString("taskHasCompleted.MSG"));
					if (i == 2 && UserAssignmentListPanel.this.isValidAddAction((TCTreeNode) aIFTreeNode1,
							arrayOfTCComponent[b])) {
						int j = UserAssignmentListPanel.this.currentPanel.getActionType();
						Vector vector = new Vector();
						vector.add(Integer.valueOf(j));
						UserAssignmentListPanel.AssignmentListHelper assignmentListHelper = new UserAssignmentListPanel.AssignmentListHelper(
								arrayOfTCComponent[b], j, vector);
						TCTreeNode tCTreeNode = new TCTreeNode(assignmentListHelper);
						tCTreeNode.setNodeIcon(assignmentListHelper.getNodeIcon());
						ImageIcon imageIcon = assignmentListHelper.getNodeIcon();
						if (arrayOfTCComponent[b] instanceof TCComponentResourcePool)
							imageIcon = UserAssignmentListPanel.this.reg.getImageIcon("group.ICON");
						tCTreeNode.setNodeIcon(imageIcon);
						UserAssignmentListPanel.this.processTreeView.clearSelection();
						UserAssignmentListPanel.this.processTreeView.removeNode(aIFTreeNode1);
						UserAssignmentListPanel.this.processTreeView.insertNode(tCTreeNode, aIFTreeNode2, 0, false);
						UserAssignmentListPanel.this.enableSaveEdits();
						TCComponent tCComponent = ((UserAssignmentListPanel.AssignmentListHelper) aIFTreeNode1
								.getUserObject()).getNodeComponent();
						if (tCComponent instanceof TCComponentGroupMember) {
							UserAssignmentListPanel.this.assignedUserSignoffs.remove(tCComponent);
							UserSignoffEditPanel
									.setAssignedUserSignOffs(UserAssignmentListPanel.this.assignedUserSignoffs);
						}
						if (arrayOfTCComponent[b] instanceof TCComponentGroupMember) {
							UserAssignmentListPanel.this.assignedUserSignoffs.add(arrayOfTCComponent[b]);
							UserSignoffEditPanel
									.setAssignedUserSignOffs(UserAssignmentListPanel.this.assignedUserSignoffs);
						}
						if (j == 4 || j == 5)
							UserAssignmentListPanel.this.currentPanel.requireCheckBox.setSelected(false);
					}
				}
				UserAssignmentListPanel.this.currentPanel.clearOrgTreeSelection();
			}
		});
		this.removeButton = new JButton(this.reg.getString("removeButton"));
		this.removeButton.setVisible(false);
		this.removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getSelectedNode();
				UserAssignmentListPanel.this.processTreeView.clearSelection();
				UserAssignmentListPanel.this.processTreeView.removeNode(aIFTreeNode);
				UserAssignmentListPanel.this.enableSaveEdits();
				if (aIFTreeNode.getParent() instanceof TCComponentProfile) {
					UserAssignmentListPanel.this.currentPanel.removeFromSelectedProfileChildCount(1);
					UserAssignmentListPanel.this.currentPanel.validateButtons();
				}
				TCComponent tCComponent = ((UserAssignmentListPanel.AssignmentListHelper) aIFTreeNode.getUserObject())
						.getNodeComponent();
				if (tCComponent instanceof TCComponentGroupMember) {
					UserAssignmentListPanel.this.assignedUserSignoffs.remove(tCComponent);
					UserSignoffEditPanel.setAssignedUserSignOffs(UserAssignmentListPanel.this.assignedUserSignoffs);
				}
			}
		});
		jPanel5.add(this.addButton);
		jPanel5.add(this.removeButton);
		jPanel5.add(this.modifyButton);
		this.memberPanel.add("bottom.nobind", jPanel5);
		this.memberPanel.add("bottom.bind", new Separator());
		this.memberPanel.add("unbound.bind", this.currentPanel);
		this.splitPane.setLeftComponent(jPanel3);
		this.splitPane.setRightComponent(this.memberPanel);
		this.splitPane.setDividerSize(2);
		this.splitPane.setDividerLocation(0.5D);
		JPanel jPanel6 = new JPanel(new HorizontalLayout());
		this.saveEdits = new JCheckBox(this.reg.getString("saveEdits"));
		this.saveAsList = new JCheckBox(this.reg.getString("saveAsList"));
		this.saveAsList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				UserAssignmentListPanel.this.newListName
						.setEditable(UserAssignmentListPanel.this.saveAsList.isSelected());
				UserAssignmentListPanel.this.newListName
						.setEnabled(UserAssignmentListPanel.this.saveAsList.isSelected());
			}
		});
		this.newListName = new iTextField(25, 32, true);
		this.saveEdits.setEnabled(false);
		this.saveAsList.setEnabled(false);
		this.newListName.setEnabled(false);
		if (this.forAssign) {
			jPanel6.add("left.bind", this.saveEdits);
			jPanel6.add("right.bind", this.newListName);
			jPanel6.add("right.nobind", this.saveAsList);
		}
		try {
			TCPreferenceService tCPreferenceService = this.session.getPreferenceService();
			if (this.forAssign) {
				String str = null;
				str = tCPreferenceService.getStringValue("EPM_adhoc_signoffs");
				if (str != null && str.compareToIgnoreCase("OFF") == 0) {
					this.allowAdhocUsers = false;
					str = null;
				}
				this.allowResourcePools = false;
				str = tCPreferenceService.getStringValue("EPM_allow_resource_pool_for_assign");
				if (str != null && str.compareToIgnoreCase("TRUE") == 0)
					this.allowResourcePools = true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		add("bottom.bind", jPanel6);
		add("unbound.bind", this.splitPane);
		setPreferredSize(new Dimension(500, 350));
	}

	public void addMemberNode(TreeNode paramTreeNode, TCComponent paramTCComponent, int paramInt) {
		TCTreeNode tCTreeNode1 = (TCTreeNode) paramTreeNode;
		Vector vector = getValidActions(tCTreeNode1);
		AssignmentListHelper assignmentListHelper = new AssignmentListHelper(paramTCComponent, paramInt, vector);
		TCTreeNode tCTreeNode2 = new TCTreeNode(assignmentListHelper);
		ImageIcon imageIcon = assignmentListHelper.getNodeIcon();
		if (paramTCComponent instanceof TCComponentResourcePool)
			imageIcon = this.reg.getImageIcon("group.ICON");
		tCTreeNode2.setNodeIcon(imageIcon);
		this.processTreeView.insertNode(tCTreeNode2, tCTreeNode1, 0, false);
		if (paramTCComponent instanceof TCComponentGroupMember) {
			if (this.assignedUserSignoffs != null) {
				this.assignedUserSignoffs.add(paramTCComponent);
				UserSignoffEditPanel.setAssignedUserSignOffs(this.assignedUserSignoffs);
			}
		}
	}

	public void addMemberNode(TreeNode paramTreeNode, TCComponent paramTCComponent, int paramInt1, int paramInt2,
			int paramInt3) {
		TCTreeNode tCTreeNode1 = (TCTreeNode) paramTreeNode;
		Vector vector = getValidActions(tCTreeNode1);
		AssignmentListHelper assignmentListHelper = new AssignmentListHelper(paramTCComponent, paramInt1, vector);
		TCTreeNode tCTreeNode2 = new TCTreeNode(assignmentListHelper);
		ImageIcon imageIcon = assignmentListHelper.getNodeIcon();
		if (paramTCComponent instanceof TCComponentResourcePool)
			imageIcon = this.reg.getImageIcon("group.ICON");
		tCTreeNode2.setNodeIcon(imageIcon);
		this.processTreeView.insertNode(tCTreeNode2, tCTreeNode1, 0, false);
	}

	public boolean isValidAddAction(TCTreeNode paramTCTreeNode, TCComponent paramTCComponent) {
		boolean bool = !isMemberExist(paramTCTreeNode, paramTCComponent);
		try {
			if (bool) {
				TCComponent tCComponent = paramTCTreeNode.getComponent();
				if (tCComponent != null && tCComponent instanceof TCComponentTaskTemplate) {
					int i = paramTCTreeNode.getChildCount();
					for (int b = 0; b < i; b++) {
						Object object = ((TCTreeNode) paramTCTreeNode.getChildAt(b)).getUserObject();
						if (object != null && object instanceof AssignmentListHelper) {
							AssignmentListHelper assignmentListHelper = (AssignmentListHelper) object;
							if (assignmentListHelper.getNodeType() == 1) {
								MessageBox.post(this.parent, this.reg.getString("canNotAddMore"),
										this.reg.getString("error.TITLE"), 1);
								bool = false;
								break;
							}
						}
					}
				} else if (tCComponent != null && tCComponent instanceof TCComponentProfile) {
					TCComponentProfile tCComponentProfile = (TCComponentProfile) tCComponent;
					if (tCComponentProfile != null
							&& tCComponentProfile.getNumberToSignoff() <= paramTCTreeNode.getChildCount()) {
						MessageBox.post(this.parent, this.reg.getString("canNotAddMoreProfiles"),
								this.reg.getString("error.TITLE"), 1);
						return false;
					}
					TCTreeNode tCTreeNode1 = (TCTreeNode) paramTCTreeNode.getParent();
					TCTreeNode tCTreeNode2 = (TCTreeNode) tCTreeNode1.getNextSibling();
					bool = !isMemberExist(tCTreeNode2, paramTCComponent);
				} else if (!this.allowAdhocUsers && tCComponent == null) {
					MessageBox.post(this.parent, this.reg.getString("adhocUserNotAllowed"),
							this.reg.getString("Information"), 2);
					bool = false;
				} else if (this.allowAdhocUsers && tCComponent == null) {
					TCTreeNode tCTreeNode = (TCTreeNode) paramTCTreeNode.getPreviousSibling();
					if (tCTreeNode != null) {
						int i = tCTreeNode.getChildCount();
						for (int b = 0; b < i; b++) {
							TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);
							bool = !isMemberExist(tCTreeNode1, paramTCComponent);
							if (!bool)
								return bool;
						}
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return bool;
	}

	public boolean isTaskCompleted(TCTreeNode paramTCTreeNode) {
		boolean bool = true;
		if (this.completedTemplates.size() <= 0)
			return false;
		try {
			TCComponent tCComponent = paramTCTreeNode.getComponent();
			if (tCComponent != null && tCComponent instanceof TCComponentTaskTemplate) {
				bool = this.completedTemplates.contains(tCComponent);
			} else if (tCComponent != null && tCComponent instanceof TCComponentProfile) {
				TCTreeNode tCTreeNode = (TCTreeNode) paramTCTreeNode.getParent().getParent();
				bool = isTaskCompleted(tCTreeNode);
			} else {
				TCTreeNode tCTreeNode = (TCTreeNode) paramTCTreeNode.getParent();
				bool = isTaskCompleted(tCTreeNode);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return bool;
	}

	public void clear() {
		this.processTreeView.setRootVisible(false);
		this.processTreeView.removeAllChildren(this.processTreeView.getRootNode());
		this.processTreeView.setSelectionPath(null);
		this.currentPanel.setVisible(false);
		validateButtons();
	}

	private boolean isMemberExist(TCTreeNode paramTCTreeNode, TCComponent paramTCComponent) {
		boolean bool = false;
		if (paramTCComponent instanceof TCComponentResourcePool)
			return bool;
		int i = paramTCTreeNode.getChildCount();
		try {
			for (int b = 0; b < i; b++) {
				TCTreeNode tCTreeNode = (TCTreeNode) paramTCTreeNode.getChildAt(b);
				AssignmentListHelper assignmentListHelper = (AssignmentListHelper) tCTreeNode.getUserObject();
				TCComponent tCComponent = assignmentListHelper.getNodeComponent();
				if (tCComponent == paramTCComponent) {
					bool = true;
					break;
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		if (bool)
			MessageBox.post(this.parent, this.reg.getString("memberExisting"), this.reg.getString("error.TITLE"), 1);
		return bool;
	}

	public ResourceMember[] getSelectedResources() throws TCException {
		ResourceMember[] arrayOfResourceMember = null;
		Vector vector = new Vector();
		if (this.resourcePoolTasks == null) {
			this.resourcePoolTasks = new Vector();
		} else {
			this.resourcePoolTasks.removeAllElements();
		}
		TCTreeNode tCTreeNode = (TCTreeNode) this.processTreeView.getRootNode();
		int i = tCTreeNode.getChildCount();
		for (int b = 0; b < i; b++) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);
			Vector vector1 = getSelectedResourceForTemplate(tCTreeNode1);
			if (vector1 != null && vector1.size() > 0)
				for (int b1 = 0; b1 < vector1.size(); b1++)
					vector.add(vector1.get(b1));
		}
		if (this.resourcePoolTasks != null && this.resourcePoolTasks.size() > 0) {
			String str1 = new String();
			String str2 = new String();
			for (int b1 = 0; b1 < this.resourcePoolTasks.size(); b1++)
				str2 = (str2 = String.valueOf(str2) + this.resourcePoolTasks.get(b1).toString()).valueOf(str2) + "\n";
			String str3 = (str1 = String.valueOf(this.reg.getString("resourcepoolAssignments.MSG")) + "\n" + str2)
					.valueOf(str2) + "\n" + this.reg.getString("resourcepoolAssignmentConfirmation.MSG");
			int j = 2;
			j = ConfirmationDialog.post(this.parent, this.reg.getString("resourcepoolAssignments.TITLE"), str3);
			if (j != 2)
				throw new TCException(str1);
		}
		arrayOfResourceMember = new ResourceMember[vector.size()];
		vector.toArray(arrayOfResourceMember);
		return arrayOfResourceMember;
	}

	private Vector getSelectedResourceForTemplate(TCTreeNode paramTCTreeNode) {
		Vector vector1 = new Vector();
		int i = -100;
		int j = -100;
		int k = 0;
		Vector vector2 = new Vector();
		Vector vector3 = new Vector();
		Vector vector4 = new Vector();
		TCComponentTaskTemplate tCComponentTaskTemplate = (TCComponentTaskTemplate) paramTCTreeNode.getComponent();
		try {
			int[] arrayOfInt = getSignoffQuorum(tCComponentTaskTemplate);
			i = arrayOfInt[0];
			j = arrayOfInt[1];
		} catch (TCException tCException) {
			tCException.printStackTrace();
//			Logger.getLogger(UserAssignmentListPanel.class).error(tCException.getLocalizedMessage(), tCException);
		}
		int m = paramTCTreeNode.getChildCount();
		int n;
		for (n = 0; n < m; n++) {
			TCTreeNode tCTreeNode = (TCTreeNode) paramTCTreeNode.getChildAt(n);
			Object object = tCTreeNode.getUserObject();
			if (object instanceof AssignmentListHelper) {
				int b;
				int i2;
				int i1;
				TCComponent tCComponent;
				AssignmentListHelper assignmentListHelper = (AssignmentListHelper) object;
				switch (assignmentListHelper.getNodeType()) {
				case 1:
					tCComponent = assignmentListHelper.getNodeComponent();
					if (!this.allowResourcePools && tCComponent instanceof TCComponentResourcePool)
						this.resourcePoolTasks.addElement(tCComponentTaskTemplate);
					vector3.add(tCComponent);
					vector2.add(Integer.valueOf(0));
					vector4.add(null);
					break;
				case -1:
					i1 = tCTreeNode.getChildCount();
					for (i2 = 0; i2 < i1; i2++) {
						TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(i2);
						TCComponentProfile tCComponentProfile = (TCComponentProfile) tCTreeNode1.getComponent();
						int i3 = tCTreeNode1.getChildCount();
						for (int b1 = 0; b1 < i3; b1++) {
							TCTreeNode tCTreeNode2 = (TCTreeNode) tCTreeNode1.getChildAt(b1);
							Object object1 = tCTreeNode2.getUserObject();
							if (object1 != null && object1 instanceof AssignmentListHelper) {
								TCComponent tCComponent1 = ((AssignmentListHelper) object1).getNodeComponent();
								int i4 = ((AssignmentListHelper) object1).getNodeAction();
								if (!this.allowResourcePools && tCComponent1 instanceof TCComponentResourcePool)
									this.resourcePoolTasks.addElement(tCComponentTaskTemplate);
								vector3.add(tCComponent1);
								vector2.add(Integer.valueOf(i4));
								vector4.add(tCComponentProfile);
							}
						}
					}
					break;
				case 3:
					i2 = tCTreeNode.getChildCount();
					for (b = 0; b < i2; b++) {
						TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);
						Object object1 = tCTreeNode1.getUserObject();
						if (object1 != null && object1 instanceof AssignmentListHelper) {
							TCComponent tCComponent1 = ((AssignmentListHelper) object1).getNodeComponent();
							int i3 = ((AssignmentListHelper) object1).getNodeAction();
							if (!this.allowResourcePools && tCComponent1 instanceof TCComponentResourcePool)
								this.resourcePoolTasks.addElement(tCComponentTaskTemplate);
							vector3.add(tCComponent1);
							vector2.add(Integer.valueOf(i3));
							vector4.add(null);
						}
					}
					i = assignmentListHelper.getRevQuorum();
					j = assignmentListHelper.getAckQuorum();
					k = assignmentListHelper.getWaitForUndecidedReviewers();
					break;
				}
			} else {
				Vector vector = getSelectedResourceForTemplate(tCTreeNode);
				if (vector != null && vector.size() > 0)
					for (int b = 0; b < vector.size(); b++)
						vector1.add(vector.get(b));
			}
		}
		if (vector3.size() > 0 && vector4.size() > 0 && vector2.size() > 0) {
			n = vector3.size();
			TCComponent[] arrayOfTCComponent = new TCComponent[n];
			TCComponentProfile[] arrayOfTCComponentProfile = new TCComponentProfile[n];
			Integer[] arrayOfInteger = new Integer[n];
			vector3.toArray(arrayOfTCComponent);
			vector4.toArray(arrayOfTCComponentProfile);
			vector2.toArray(arrayOfInteger);
			ResourceMember resourceMember = new ResourceMember(tCComponentTaskTemplate, arrayOfTCComponent,
					arrayOfTCComponentProfile, arrayOfInteger, i, j, k);
			vector1.add(resourceMember);
		}
		return vector1;
	}

	protected void setTreeFilterProperties() {
		this.processTreeView.setCachingTCProperties(new String[] { "object_name", "icon_key", "predecessors",
				"successors", "subtask_template", "task_type", "parent_task_template1", "fnd0origin_uid" });
		ExpansionRule expansionRule = new ExpansionRule() {
			public boolean isValidChild(InterfaceAIFComponent param1InterfaceAIFComponent) {
				boolean bool = true;
				try {
					TCComponentTaskTemplate tCComponentTaskTemplate = (TCComponentTaskTemplate) param1InterfaceAIFComponent;
					String str = tCComponentTaskTemplate.getType();
					if (str.compareTo("EPMSyncTaskTemplate") == 0 || str.compareTo("EPMOrTaskTemplate") == 0) {
						bool = false;
					} else {
						TCComponentTaskTemplate tCComponentTaskTemplate1 = (TCComponentTaskTemplate) tCComponentTaskTemplate
								.getParent();
						String str1 = tCComponentTaskTemplate1.getType();
						if (str1.compareTo("EPMRouteTaskTemplate") == 0 || str1.compareTo("EPMReviewTaskTemplate") == 0
								|| str1.compareTo("EPMAcknowledgeTaskTemplate") == 0)
							bool = false;
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				return bool;
			}

			public AIFComponentContext[] getFilteredChildren(AIFComponentContext[] param1ArrayOfAIFComponentContext) {
				TCComponentType.cacheTCPropertiesSet(param1ArrayOfAIFComponentContext,
						new String[] { "parent_task_template1" }, true);
				return super.getFilteredChildren(param1ArrayOfAIFComponentContext);
			}
		};
		this.processTreeView.setExpansionRule(expansionRule);
		TCTypeService tCTypeService = null;
		TypeInfo typeInfo = null;
		try {
			tCTypeService = this.session.getTypeService();
			typeInfo = tCTypeService.getTypesInfoForClass("EPMTaskTemplate");
		} catch (TCException tCException) {
			MessageBox messageBox = new MessageBox(tCException);
			messageBox.setVisible(true);
			return;
		}
		String[] arrayOfString = typeInfo.getTypeNames();
		if (arrayOfString != null) {
			String[] arrayOfString1;
			int i = (arrayOfString1 = arrayOfString).length;
			for (int b = 0; b < i; b++) {
				String str = arrayOfString1[b];
				expansionRule.addRule(str, "subtask_template");
			}
		}
	}

	private void createTreeView() {
		if (this.processTreeView == null) {
			this.processTreeView = new TCTree(null, this.session, false, true, false) {
				public String getToolTipText(MouseEvent param1MouseEvent) {
					if (getRowForLocation(param1MouseEvent.getX(), param1MouseEvent.getY()) == -1)
						return null;
					TreePath treePath = getPathForLocation(param1MouseEvent.getX(), param1MouseEvent.getY());
					Object object = treePath.getLastPathComponent();
					if (object instanceof TCTreeNode) {
						TCTreeNode tCTreeNode = (TCTreeNode) object;
						if (tCTreeNode.getChildCount() == 0)
							return object.toString();
					}
					return null;
				}
			};
			this.processTreeView.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent param1TreeSelectionEvent) {
					Utilities.invokeLater(new Runnable() {
						public void run() {
							UserAssignmentListPanel.this.validatePanels();
						}
					});
				}
			});
			setTreeFilterProperties();
			this.session.addAIFComponentEventListener(this.processTreeView);
		}
		if (this.processTreeView != null)
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					UserAssignmentListPanel.this.setTreeContents();
					TreePath[] arrayOfTreePath = new TreePath[1];
					if (UserAssignmentListPanel.this.processTreeView.getRowCount() > 0) {
						arrayOfTreePath[0] = UserAssignmentListPanel.this.processTreeView.getPathForRow(0);
						UserAssignmentListPanel.this.processTreeView.expandBelow(arrayOfTreePath);
						UserAssignmentListPanel.this.processTreeView.expandToLevel(-1);
						UserAssignmentListPanel.this.processTreeView
								.setSelectedNode(UserAssignmentListPanel.this.processTreeView.getRootNode());
					}
				}
			});
	}

	private void populateAssignedUserSignOffs(TCTreeNode paramTCTreeNode) {
		Enumeration enumeration = paramTCTreeNode.children();
		while (enumeration != null && enumeration.hasMoreElements()) {
			TCTreeNode tCTreeNode = (TCTreeNode) enumeration.nextElement();
			if (tCTreeNode.getChildCount() > 0) {
				populateAssignedUserSignOffs(tCTreeNode);
				continue;
			}
			if (!(tCTreeNode.getUserObject() instanceof TCComponentProfile)
					&& tCTreeNode.getUserObject() instanceof AssignmentListHelper) {
				AssignmentListHelper assignmentListHelper = (AssignmentListHelper) tCTreeNode.getUserObject();
				TCComponent tCComponent = assignmentListHelper.getNodeComponent();
				if (tCComponent != null && tCComponent instanceof TCComponentGroupMember)
					this.assignedUserSignoffs.add(tCComponent);
			}
		}
	}

	private void getTaskNode(TCTreeNode paramTCTreeNode, ArrayList paramArrayList) {
		if (paramArrayList.size() == 0)
			if (paramTCTreeNode.getUserObject() instanceof AIFComponentContext) {
				AIFComponentContext aIFComponentContext = (AIFComponentContext) paramTCTreeNode.getUserObject();
				if (aIFComponentContext.getComponent() != null
						&& aIFComponentContext.getComponent() instanceof TCComponentTaskTemplate) {
					paramArrayList.add(paramTCTreeNode);
				} else {
					getTaskNode((TCTreeNode) paramTCTreeNode.getParent(), paramArrayList);
				}
			} else if (paramTCTreeNode.getUserObject() instanceof AssignmentListHelper) {
				AssignmentListHelper assignmentListHelper = (AssignmentListHelper) paramTCTreeNode.getUserObject();
				TCComponent tCComponent = assignmentListHelper.getNodeComponent();
				if (tCComponent != null && tCComponent instanceof TCComponentTaskTemplate) {
					paramArrayList.add(paramTCTreeNode);
				} else {
					getTaskNode((TCTreeNode) paramTCTreeNode.getParent(), paramArrayList);
				}
			}
	}

	private void validatePanels() {
		if (this.currentPanel == null || this.memberPanel == null)
			return;
		TreePath treePath = this.processTreeView.getSelectionPath();
		int i = -1;
		TCTreeNode tCTreeNode1 = null;
		Object object1 = null;
		Object object2 = null;
		if (treePath != null)
			object2 = treePath.getLastPathComponent();
		this.quorumRadioButton.setVisible(false);
		this.quorumField.setVisible(false);
		this.quorumPercentRadioButton.setVisible(false);
		this.quorumPercentField.setVisible(false);
		this.percentLabel.setVisible(false);
		this.ackQuorumField.setVisible(false);
		this.ackPercentQuorumField.setVisible(false);
		this.ackRadioButton.setVisible(false);
		this.ackPercentRadioButton.setVisible(false);
		this.ackpercentLabel.setVisible(false);
		this.waitForUndecidedReviewersChk.setVisible(false);
		this.reviewQuorumPanel.setVisible(false);
		this.acknowQuorumPanel.setVisible(false);
		this.copyButton.setEnabled(false);
		this.pasteButton.setEnabled(false);
		if (object2 != null && object2 instanceof TCTreeNode) {
			tCTreeNode1 = (TCTreeNode) object2;
		} else {
			return;
		}
		this.currentPanel.setVisible(true);
		ArrayList arrayList = new ArrayList();
		getTaskNode(tCTreeNode1, arrayList);
		this.assignedUserSignoffs = new HashSet();
		if (arrayList.size() > 0)
			populateAssignedUserSignOffs((TCTreeNode) arrayList.get(0));
		UserSignoffEditPanel.setAssignedUserSignOffs(this.assignedUserSignoffs);
		boolean bool = (treePath != null && treePath.getPathCount() > 1);
		this.memberPanel.setVisible(bool);
		if (!bool) {
			// recompile 20220323143000 : START
			UserAssignmentListPanel.pubMail = "";
			UserAssignmentListPanel.taskName = "";
			UserAssignmentListPanel.this.currentPanel.clearPubMailContent();
			// 20220323143000 : END
			return;
		}
		displayQuorum();
		setWaitForUndecidedReviewersBox();
		this.currentPanel.setAllResourcePool(true);
		object1 = tCTreeNode1.getUserObject();
		if (object1 != null && object1 instanceof AssignmentListHelper) {
			AssignmentListHelper assignmentListHelper = (AssignmentListHelper) object1;
			TCComponent tCComponent = assignmentListHelper.getNodeComponent();
			if (assignmentListHelper.nodeAction > 3) {
				this.currentPanel.requireCheckBox.setSelected(true);
			} else {
				this.currentPanel.requireCheckBox.setSelected(false);
			}
			if (tCComponent != null) {
				TCComponentProfile tCComponentProfile = null;
				this.currentPanel.resetComponentPanel();
				if (tCTreeNode1.getChildCount() == 0 && treePath.getPathCount() >= 5) {
					TreeNode treeNode = tCTreeNode1.getParent();
					TCComponent tCComponent1 = ((TCTreeNode) treeNode).getComponent();
					if (tCComponent1 instanceof TCComponentProfile)
						tCComponentProfile = (TCComponentProfile) tCComponent1;
				}
				this.currentPanel.setNewComponent(tCTreeNode1, tCComponent, tCComponentProfile,
						assignmentListHelper.getValidActions(), !this.forAssign, !this.forAssign, this.targetComps);
				this.copyButton.setEnabled(true);
				this.pasteButton.setEnabled(false);
			} else if (tCComponent == null) {
				try {
					i = assignmentListHelper.getNodeType();
					if (i == 3) {
						this.currentPanel.setNewComponent(tCTreeNode1, this.session.getUser(), null,
								assignmentListHelper.getValidActions(), !this.forAssign, !this.forAssign,
								this.targetComps);
						this.copyButton.setEnabled(false);
						this.pasteButton.setEnabled(true);
					} else {
						this.currentPanel.resetComponentPanel();
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		} else {
			TCComponent tCComponent = ((TCTreeNode) object2).getComponent();
			if (tCComponent instanceof TCComponentTaskTemplate) {
				try {
					Vector vector = new Vector();
					vector.add(Integer.valueOf(0));
					this.currentPanel.setNewComponent(tCTreeNode1, this.session.getUser(), null, vector,
							!this.forAssign, !this.forAssign, this.targetComps);
					this.currentPanel.setAllResourcePool(false);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				this.copyButton.setEnabled(false);
				if (((TCTreeNode) object2).getParent() != null)
					this.pasteButton.setEnabled(true);
			} else if (tCComponent instanceof TCComponentProfile) {
				Vector vector = new Vector();
				try {
					TreeNode treeNode1 = tCTreeNode1.getParent();
					TreeNode treeNode2 = null;
					if (treeNode1 != null)
						treeNode2 = treeNode1.getParent();
					if (treeNode2 != null && treeNode2 instanceof TCTreeNode) {
						TCComponent tCComponent1 = ((TCTreeNode) treeNode2).getComponent();
						if (tCComponent1 != null
								&& tCComponent1.getType().compareTo("EPMAcknowledgeTaskTemplate") == 0) {
							vector.add(Integer.valueOf(2));
						} else {
							vector.add(Integer.valueOf(1));
						}
					} else {
						vector.add(Integer.valueOf(1));
					}
					this.currentPanel.setNewComponent(tCTreeNode1, tCComponent, null, vector, !this.forAssign,
							!this.forAssign, this.targetComps);
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				this.copyButton.setEnabled(false);
				this.pasteButton.setEnabled(true);
			} else {
				this.currentPanel.resetComponentPanel();
			}
		}
		final TCTreeNode selectedNode = tCTreeNode1;
		Utilities.invokeLater(new Runnable() {
			public void run() {
				if (selectedNode != null) {
					UserAssignmentListPanel.this.currentPanel.setIcon(selectedNode.getNodeIcon());
					UserAssignmentListPanel.this.memberPanelTitleBorder.setTitle(selectedNode.toString());
				}
				UserAssignmentListPanel.this.validateButtons();
				UserAssignmentListPanel.this.memberPanel.validate();
				UserAssignmentListPanel.this.memberPanel.repaint();
				UserAssignmentListPanel.this.splitPane.revalidate();
				UserAssignmentListPanel.this.splitPane.repaint();
			}
		});
	}

	private TCTreeNode getTemplateNodeForSelectedNode(TreeNode paramTreeNode) {
		TCTreeNode tCTreeNode = null;
		if (paramTreeNode != null && paramTreeNode instanceof TCTreeNode) {
			tCTreeNode = (TCTreeNode) paramTreeNode;
			for (Object object = tCTreeNode.getUserObject(); object != null
					&& object instanceof AssignmentListHelper; object = tCTreeNode.getUserObject())
				tCTreeNode = (TCTreeNode) tCTreeNode.getParent();
			TCComponent tCComponent = tCTreeNode.getComponent();
			if (!(tCComponent instanceof TCComponentTaskTemplate))
				tCTreeNode = getTemplateNodeForSelectedNode(tCTreeNode.getParent());
		}
		return tCTreeNode;
	}

	private TCTreeNode getUserNodeForTemplateNode(TCTreeNode paramTCTreeNode) {
		TCTreeNode tCTreeNode = null;
		if (paramTCTreeNode != null && paramTCTreeNode.getChildCount() >= 1) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) paramTCTreeNode.getLastChild();
			if (isMatch(tCTreeNode1, this.reg.getString("users")))
				tCTreeNode = tCTreeNode1;
		}
		return tCTreeNode;
	}

	private void displayQuorum() {
		AIFTreeNode aIFTreeNode = this.processTreeView.getSelectedNode();
		TCTreeNode tCTreeNode = getTemplateNodeForSelectedNode(aIFTreeNode);
		if (tCTreeNode != null) {
			TCTreeNode tCTreeNode1 = getUserNodeForTemplateNode(tCTreeNode);
			if (tCTreeNode1 != null) {
				TCComponent tCComponent = tCTreeNode.getComponent();
				String str1 = tCComponent.getType();
				TCPreferenceService tCPreferenceService = this.session.getPreferenceService();
				String str2 = tCPreferenceService.getStringValue("WRKFLW_allow_wait_for_undecided_override");
				if (!Boolean.valueOf(str2).booleanValue())
					this.waitForUndecidedReviewersChk.setEnabled(false);
				if (str1.compareTo("EPMReviewTaskTemplate") == 0) {
					this.quorumRadioButton.setVisible(true);
					this.quorumField.setVisible(true);
					this.quorumPercentRadioButton.setVisible(true);
					this.quorumPercentField.setVisible(true);
					this.percentLabel.setVisible(true);
					this.reviewQuorumPanel.setVisible(true);
					this.waitForUndecidedReviewersChk.setVisible(true);
				} else if (str1.compareTo("EPMAcknowledgeTaskTemplate") == 0) {
					this.ackQuorumField.setVisible(true);
					this.ackPercentQuorumField.setVisible(true);
					this.ackRadioButton.setVisible(true);
					this.ackPercentRadioButton.setVisible(true);
					this.ackpercentLabel.setVisible(true);
					this.acknowQuorumPanel.setVisible(true);
				} else if (str1.compareTo("EPMRouteTaskTemplate") == 0) {
					this.quorumRadioButton.setVisible(true);
					this.quorumField.setVisible(true);
					this.quorumPercentRadioButton.setVisible(true);
					this.quorumPercentField.setVisible(true);
					this.percentLabel.setVisible(true);
					this.ackQuorumField.setVisible(true);
					this.ackPercentQuorumField.setVisible(true);
					this.ackRadioButton.setVisible(true);
					this.ackPercentRadioButton.setVisible(true);
					this.ackpercentLabel.setVisible(true);
					this.waitForUndecidedReviewersChk.setVisible(true);
					this.reviewQuorumPanel.setVisible(true);
					this.acknowQuorumPanel.setVisible(true);
				}
				String str3 = this.session.getPreferenceService().getStringValue("WRKFLW_allow_quorum_override");
				if (str3 != null && !Boolean.valueOf(str3).booleanValue()) {
					this.quorumRadioButton.setEnabled(false);
					this.quorumPercentRadioButton.setEnabled(false);
					this.quorumField.setEnabled(false);
					this.quorumPercentField.setEnabled(false);
					this.ackQuorumField.setEnabled(false);
					this.ackPercentQuorumField.setEnabled(false);
					this.ackRadioButton.setEnabled(false);
					this.ackPercentRadioButton.setEnabled(false);
				}
				Object object = tCTreeNode1.getUserObject();
				if (object != null && object instanceof AssignmentListHelper) {
					AssignmentListHelper assignmentListHelper = (AssignmentListHelper) object;
					Integer integer1 = Integer.valueOf(assignmentListHelper.getRevQuorum());
					if (this.quorumField.isVisible())
						if (integer1.intValue() > 0) {
							this.quorumRadioButton.doClick();
							this.quorumField.setText(integer1.toString());
						} else {
							String str = integer1.toString();
							str = str.replace("-", "");
							this.quorumPercentRadioButton.doClick();
							this.quorumPercentField.setText(str);
						}
					Integer integer2 = Integer.valueOf(assignmentListHelper.getAckQuorum());
					if (this.ackQuorumField.isVisible())
						if (integer2.intValue() > 0) {
							this.ackRadioButton.doClick();
							this.ackQuorumField.setText(integer2.toString());
						} else {
							String str = integer2.toString();
							str = str.replace("-", "");
							this.ackPercentRadioButton.doClick();
							this.ackPercentQuorumField.setText(str);
						}
				}
			}
		}
	}

	private Vector getValidActions(TreeNode paramTreeNode) {
		Vector vector = new Vector();
		if (paramTreeNode != null && paramTreeNode instanceof TCTreeNode) {
			TCTreeNode tCTreeNode = (TCTreeNode) paramTreeNode;
			Object object = tCTreeNode.getUserObject();
			if (object != null && object instanceof AssignmentListHelper) {
				vector = ((AssignmentListHelper) object).getValidActions();
			} else {
				TCComponent tCComponent = tCTreeNode.getComponent();
				if (tCComponent instanceof TCComponentProfile) {
					Integer integer = null;
					TreeNode treeNode1 = tCTreeNode.getParent();
					TreeNode treeNode2 = null;
					if (treeNode1 != null)
						treeNode2 = treeNode1.getParent();
					if (treeNode2 != null && treeNode2 instanceof TCTreeNode) {
						TCComponent tCComponent1 = ((TCTreeNode) treeNode2).getComponent();
						if (tCComponent1 != null
								&& tCComponent1.getType().compareTo("EPMAcknowledgeTaskTemplate") == 0) {
							integer = Integer.valueOf(2);
						} else {
							integer = Integer.valueOf(1);
						}
					}
					if (integer != null) {
						vector.add(integer);
					} else {
						vector.add(Integer.valueOf(1));
					}
				} else if (tCComponent instanceof TCComponentTaskTemplate) {
					Integer integer = Integer.valueOf(0);
					vector.add(integer);
				}
			}
		}
		return vector;
	}

	private void validateButtons() {
		TreePath treePath = this.processTreeView.getSelectionPath();
		TCTreeNode tCTreeNode = null;
		AssignmentListHelper assignmentListHelper = null;
		if (treePath != null) {
			if (treePath.getLastPathComponent() instanceof TCTreeNode) {
				tCTreeNode = (TCTreeNode) treePath.getLastPathComponent();
				if (tCTreeNode.getUserObject() instanceof AssignmentListHelper)
					assignmentListHelper = (AssignmentListHelper) tCTreeNode.getUserObject();
			}
			if (assignmentListHelper != null) {
				int i = assignmentListHelper.getNodeType();
				this.addButton.setVisible(((i == 3 || i == 2) && this.currentPanel.isVisible()));
				this.removeButton.setVisible((i == 1 && this.currentPanel.isVisible()));
				this.modifyButton.setVisible((i == 1 && this.currentPanel.isVisible()));
			} else {
				this.addButton.setVisible(false);
				this.removeButton.setVisible(false);
				this.modifyButton.setVisible(false);
			}
			if (treePath.getLastPathComponent() instanceof TCTreeNode) {
				TCTreeNode tCTreeNode1 = (TCTreeNode) treePath.getLastPathComponent();
				if (tCTreeNode1.getLevel() > 0 && tCTreeNode1.getComponent() != null)
					this.addButton.setVisible(true);
			}
		} else {
			this.addButton.setVisible(false);
			this.removeButton.setVisible(false);
			this.modifyButton.setVisible(false);
		}
	}

	private void loadAssignmentList() {
		this.processTreeView.removeAllChildren(this.processTreeView.getRootNode());
		if (this.assignmentList == null && this.procTemplate == null)
			return;
		setTreeContents();
		this.session.queueOperation(new AbstractAIFOperation(this.reg.getString("loadingTemplate.MSG")) {
			public void executeOperation() {
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getRootNode();
				aIFTreeNode.setNodeIcon(UserAssignmentListPanel.this.reg.getImageIcon("process.ICON"));
				UserAssignmentListPanel.this.setTreeDisplay((TCTreeNode) aIFTreeNode);
			}
		});
		this.session.queueOperation(new AbstractAIFOperation(this.reg.getString("loadingResources.MSG")) {
			public void executeOperation() {
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getRootNode();
				UserAssignmentListPanel.this.applyResources((TCTreeNode) aIFTreeNode);
			}
		});
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UserAssignmentListPanel.this.processTreeView.clearSelection();
					UserAssignmentListPanel.this.processTreeView.setRootVisible(true);
					TreePath[] arrayOfTreePath = new TreePath[1];
					if (UserAssignmentListPanel.this.processTreeView.getRowCount() > 0) {
						UserAssignmentListPanel.this.processTreeView.updateUI();
						arrayOfTreePath[0] = UserAssignmentListPanel.this.processTreeView.getPathForRow(0);
						UserAssignmentListPanel.this.processTreeView.expandBelow(arrayOfTreePath);
						UserAssignmentListPanel.this.processTreeView.expandToLevel(-1);
						UserAssignmentListPanel.this.processTreeView
								.setSelectedNode(UserAssignmentListPanel.this.processTreeView.getRootNode());
						UserAssignmentListPanel.this.processTreeView.revalidate();
						UserAssignmentListPanel.this.processTreeView.repaint();
					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		});
	}

	private void setTreeDisplay(TCTreeNode paramTCTreeNode) {
		int i = paramTCTreeNode.getChildCount();
		for (int b = 0; b < i; b++) {
			TCTreeNode tCTreeNode = (TCTreeNode) paramTCTreeNode.getChildAt(b);
			try {
				TCComponentTaskTemplate tCComponentTaskTemplate = null;
				TCComponent tCComponent = tCTreeNode.getComponent();
				if (tCComponent instanceof TCComponentTaskTemplate) {
					tCComponentTaskTemplate = (TCComponentTaskTemplate) tCComponent;
					setTreeDisplay(tCTreeNode);
				}
				if (tCComponentTaskTemplate != null) {
					TCProperty tCProperty = null;
					try {
						tCProperty = tCComponentTaskTemplate.getTCProperty("wait_for_all_reviewers");
					} catch (TCException tCException) {
						tCException.printStackTrace();
//						Logger.getLogger(UserAssignmentListPanel.class).error(tCException.getLocalizedMessage(), tCException);
					}
					String str1 = tCProperty.getPropertyDescriptor().getDisplayName();
					this.waitForUndecidedReviewersChk.setText(str1);
					String str2 = tCComponentTaskTemplate.getType();
					if (str2.compareTo("EPMReviewTaskTemplate") == 0) {
						tCTreeNode.removeAllChildren();
						TCComponent[] arrayOfTCComponent = getSignoffProfiles(tCComponentTaskTemplate);
						if (arrayOfTCComponent != null && arrayOfTCComponent.length > 0) {
							ImageIcon imageIcon = this.reg.getImageIcon("EPMSignoffProfile.ICON");
							AssignmentListHelper assignmentListHelper = new AssignmentListHelper(
									this.reg.getString("profiles"), imageIcon, -1);
							TCTreeNode tCTreeNode1 = new TCTreeNode(assignmentListHelper);
							tCTreeNode1.setNodeIcon(assignmentListHelper.getNodeIcon());
							tCTreeNode.add(tCTreeNode1);
							TCComponent[] arrayOfTCComponent1;
							int j = (arrayOfTCComponent1 = arrayOfTCComponent).length;
							for (int b1 = 0; b1 < j; b1++) {
								TCComponent tCComponent1 = arrayOfTCComponent1[b1];
								TCTreeNode[] arrayOfTCTreeNode = new TCTreeNode[1];
								arrayOfTCTreeNode[0] = new TCTreeNode(tCComponent1);
								tCTreeNode1.add(arrayOfTCTreeNode[0]);
							}
						}
						if (this.allowAdhocUsers) {
							Vector vector = new Vector();
							vector.add(Integer.valueOf(1));
							vector.add(Integer.valueOf(4));
							AssignmentListHelper assignmentListHelper = new AssignmentListHelper(
									this.reg.getString("users"), this.reg.getImageIcon("user.ICON"), 3, vector);
							int[] arrayOfInt = getSignoffQuorum(tCComponentTaskTemplate);
							assignmentListHelper.setRevQuorum(arrayOfInt[0]);
							int j = getWaitUndecidedReviewers(tCComponentTaskTemplate);
							assignmentListHelper.setWaitForUndecidedReviewers(j);
							TCTreeNode tCTreeNode1 = new TCTreeNode(assignmentListHelper);
							tCTreeNode1.setNodeIcon(assignmentListHelper.getNodeIcon());
							tCTreeNode.add(tCTreeNode1);
						}
					} else if (str2.compareTo("EPMAcknowledgeTaskTemplate") == 0) {
						tCTreeNode.removeAllChildren();
						TCComponent[] arrayOfTCComponent = getSignoffProfiles(tCComponentTaskTemplate);
						if (arrayOfTCComponent != null && arrayOfTCComponent.length > 0) {
							ImageIcon imageIcon = this.reg.getImageIcon("EPMSignoffProfile.ICON");
							AssignmentListHelper assignmentListHelper = new AssignmentListHelper(
									this.reg.getString("profiles"), imageIcon, -1);
							TCTreeNode tCTreeNode1 = new TCTreeNode(assignmentListHelper);
							tCTreeNode1.setNodeIcon(assignmentListHelper.getNodeIcon());
							tCTreeNode.add(tCTreeNode1);
							TCComponent[] arrayOfTCComponent1;
							int j = (arrayOfTCComponent1 = arrayOfTCComponent).length;
							for (int b1 = 0; b1 < j; b1++) {
								TCComponent tCComponent1 = arrayOfTCComponent1[b1];
								TCTreeNode[] arrayOfTCTreeNode = new TCTreeNode[1];
								arrayOfTCTreeNode[0] = new TCTreeNode(tCComponent1);
								tCTreeNode1.add(arrayOfTCTreeNode[0]);
							}
						}
						if (this.allowAdhocUsers) {
							Vector vector = new Vector();
							vector.add(Integer.valueOf(2));
							vector.add(Integer.valueOf(5));
							AssignmentListHelper assignmentListHelper = new AssignmentListHelper(
									this.reg.getString("users"), this.reg.getImageIcon("user.ICON"), 3, vector);
							int[] arrayOfInt = getSignoffQuorum(tCComponentTaskTemplate);
							assignmentListHelper.setAckQuorum(arrayOfInt[1]);
							TCTreeNode tCTreeNode1 = new TCTreeNode(assignmentListHelper);
							tCTreeNode1.setNodeIcon(assignmentListHelper.getNodeIcon());
							tCTreeNode.add(tCTreeNode1);
						}
					} else if (str2.compareTo("EPMRouteTaskTemplate") == 0) {
						tCTreeNode.removeAllChildren();
						TCComponentTaskTemplate[] arrayOfTCComponentTaskTemplate = tCComponentTaskTemplate
								.getSubtaskDefinitions();
						TCComponent[] arrayOfTCComponent1 = getSignoffProfiles(arrayOfTCComponentTaskTemplate[0]);
						if (arrayOfTCComponent1 != null && arrayOfTCComponent1.length > 0) {
							AssignmentListHelper assignmentListHelper = new AssignmentListHelper(
									this.reg.getString("profiles"), this.reg.getImageIcon("user.ICON"), -1);
							TCTreeNode tCTreeNode1 = new TCTreeNode(assignmentListHelper);
							tCTreeNode1.setNodeIcon(assignmentListHelper.getNodeIcon());
							tCTreeNode.add(tCTreeNode1);
							TCComponent[] arrayOfTCComponent;
							int j = (arrayOfTCComponent = arrayOfTCComponent1).length;
							for (int b1 = 0; b1 < j; b1++) {
								TCComponent tCComponent1 = arrayOfTCComponent[b1];
								TCTreeNode[] arrayOfTCTreeNode = new TCTreeNode[1];
								arrayOfTCTreeNode[0] = new TCTreeNode(tCComponent1);
								tCTreeNode1.add(arrayOfTCTreeNode[0]);
							}
						}
						TCComponent[] arrayOfTCComponent2 = getSignoffProfiles(arrayOfTCComponentTaskTemplate[1]);
						if (arrayOfTCComponent2 != null && arrayOfTCComponent2.length > 0) {
							AssignmentListHelper assignmentListHelper = new AssignmentListHelper(
									this.reg.getString("profiles"), this.reg.getImageIcon("user.ICON"), -1);
							TCTreeNode tCTreeNode1 = new TCTreeNode(assignmentListHelper);
							tCTreeNode1.setNodeIcon(assignmentListHelper.getNodeIcon());
							tCTreeNode.add(tCTreeNode1);
							TCComponent[] arrayOfTCComponent;
							int j = (arrayOfTCComponent = arrayOfTCComponent2).length;
							for (int b1 = 0; b1 < j; b1++) {
								TCComponent tCComponent1 = arrayOfTCComponent[b1];
								TCTreeNode[] arrayOfTCTreeNode = new TCTreeNode[1];
								arrayOfTCTreeNode[0] = new TCTreeNode(tCComponent1);
								tCTreeNode1.add(arrayOfTCTreeNode[0]);
							}
						}
						if (this.allowAdhocUsers) {
							Vector vector = new Vector();
							vector.add(Integer.valueOf(1));
							vector.add(Integer.valueOf(2));
							vector.add(Integer.valueOf(3));
							AssignmentListHelper assignmentListHelper = new AssignmentListHelper(
									this.reg.getString("users"), this.reg.getImageIcon("user.ICON"), 3, vector);
							int[] arrayOfInt = getSignoffQuorum(tCComponentTaskTemplate);
							assignmentListHelper.setRevQuorum(arrayOfInt[0]);
							assignmentListHelper.setAckQuorum(arrayOfInt[1]);
							int j = getWaitUndecidedReviewers(tCComponentTaskTemplate);
							assignmentListHelper.setWaitForUndecidedReviewers(j);
							TCTreeNode tCTreeNode1 = new TCTreeNode(assignmentListHelper);
							tCTreeNode1.setNodeIcon(this.reg.getImageIcon("user.ICON"));
							tCTreeNode.add(tCTreeNode1);
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
				break;
			}
		}
	}

	private void applyResources(TCTreeNode paramTCTreeNode) {
		ResourceMember[] arrayOfResourceMember1 = null;
		try {
			arrayOfResourceMember1 = this.assignmentList.getDetails();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		ResourceMember[] arrayOfResourceMember2;
		int i = (arrayOfResourceMember2 = arrayOfResourceMember1).length;
		for (int b = 0; b < i; b++) {
			ResourceMember resourceMember = arrayOfResourceMember2[b];
			TCTreeNode tCTreeNode = findNode(paramTCTreeNode, resourceMember.getTaskTemplate(), true);
			if (tCTreeNode == null)
				try {
					TCProperty tCProperty = resourceMember.getTaskTemplate().getTCProperty("fnd0origin_uid");
					if (tCProperty != null) {
						String str = tCProperty.getStringValue();
						tCTreeNode = findNode(paramTCTreeNode, str, true);
					}
					if (tCTreeNode == null)
						tCTreeNode = findNode(paramTCTreeNode, resourceMember.getTaskTemplate().getName(), true);
				} catch (Exception exception) {
					tCTreeNode = null;
				}
			if (tCTreeNode != null) {
				TCComponent[] arrayOfTCComponent = resourceMember.getResources();
				TCComponentProfile[] arrayOfTCComponentProfile = resourceMember.getProfiles();
				Integer[] arrayOfInteger = resourceMember.getActions();
				int j = resourceMember.getReviewQuorum();
				int k = resourceMember.getAcknowQuorum();
				int m = resourceMember.getWaitForUndecidedReviewers();
				TCTreeNode tCTreeNode1 = getUserNodeForTemplateNode(tCTreeNode);
				if (tCTreeNode1 != null) {
					AssignmentListHelper assignmentListHelper = (AssignmentListHelper) tCTreeNode1.getUserObject();
					assignmentListHelper.setRevQuorum(j);
					assignmentListHelper.setAckQuorum(k);
					assignmentListHelper.setOrigRevQuorum(j);
					assignmentListHelper.setOrigAckQuorum(k);
					assignmentListHelper.setWaitForUndecidedReviewers(m);
				}
				for (int b1 = 0; b1 < arrayOfInteger.length; b1++) {
					TCTreeNode tCTreeNode2;
					switch (arrayOfInteger[b1].intValue()) {
					case 0:
						addMemberNode(tCTreeNode, arrayOfTCComponent[b1], 0);
						break;
					case 1:
						if (arrayOfTCComponentProfile[b1] != null) {
							TCTreeNode tCTreeNode3 = findNode(tCTreeNode, this.reg.getString("profiles"), false);
							if (tCTreeNode3 != null) {
								TCTreeNode tCTreeNode4 = findNode(tCTreeNode3, arrayOfTCComponentProfile[b1], false);
								if (tCTreeNode4 == null)
									try {
										tCTreeNode4 = findNode(tCTreeNode3, arrayOfTCComponentProfile[b1].toString(),
												true);
									} catch (Exception exception) {
										tCTreeNode4 = null;
									}
								if (tCTreeNode4 != null)
									addMemberNode(tCTreeNode4, arrayOfTCComponent[b1], 1);
							}
							break;
						}
						if (tCTreeNode1 != null)
							addMemberNode(tCTreeNode1, arrayOfTCComponent[b1], 1, j, -1);
						break;
					case 2:
						if (arrayOfTCComponentProfile[b1] != null) {
							TCTreeNode tCTreeNode3 = findNode(tCTreeNode, this.reg.getString("profiles"), false);
							if (tCTreeNode3 != null) {
								TCTreeNode tCTreeNode4 = findNode(tCTreeNode3, arrayOfTCComponentProfile[b1], false);
								if (tCTreeNode4 == null)
									try {
										tCTreeNode4 = findNode(tCTreeNode3, arrayOfTCComponentProfile[b1].toString(),
												true);
									} catch (Exception exception) {
										tCTreeNode4 = null;
									}
								if (tCTreeNode4 != null)
									addMemberNode(tCTreeNode4, arrayOfTCComponent[b1], 2);
							}
							break;
						}
						if (tCTreeNode1 != null) {
							addMemberNode(tCTreeNode1, arrayOfTCComponent[b1], 2, -1, k);
							AssignmentListHelper assignmentListHelper = (AssignmentListHelper) tCTreeNode1
									.getUserObject();
							assignmentListHelper.setRevQuorum(j);
							assignmentListHelper.setAckQuorum(k);
						}
						break;
					case 3:
						if (tCTreeNode1 != null)
							addMemberNode(tCTreeNode1, arrayOfTCComponent[b1], arrayOfInteger[b1].intValue());
						break;
					case 4:
						if (arrayOfTCComponentProfile[b1] != null) {
							TCTreeNode tCTreeNode3 = findNode(tCTreeNode, this.reg.getString("profiles"), false);
							if (tCTreeNode3 != null) {
								TCTreeNode tCTreeNode4 = findNode(tCTreeNode3, arrayOfTCComponentProfile[b1], false);
								if (tCTreeNode4 == null)
									try {
										tCTreeNode4 = findNode(tCTreeNode3, arrayOfTCComponentProfile[b1].toString(),
												true);
									} catch (Exception exception) {
										tCTreeNode4 = null;
									}
								if (tCTreeNode4 != null)
									addMemberNode(tCTreeNode4, arrayOfTCComponent[b1], 4);
							}
							break;
						}
						tCTreeNode2 = findNode(tCTreeNode, this.reg.getString("users"), false);
						if (tCTreeNode2 != null)
							addMemberNode(tCTreeNode2, arrayOfTCComponent[b1], 4, j, -1);
						break;
					case 5:
						if (arrayOfTCComponentProfile[b1] != null) {
							tCTreeNode2 = findNode(tCTreeNode, this.reg.getString("profiles"), false);
							if (tCTreeNode2 != null) {
								TCTreeNode tCTreeNode3 = findNode(tCTreeNode2, arrayOfTCComponentProfile[b1], false);
								if (tCTreeNode3 == null)
									try {
										tCTreeNode3 = findNode(tCTreeNode2, arrayOfTCComponentProfile[b1].toString(),
												true);
									} catch (Exception exception) {
										tCTreeNode3 = null;
									}
								if (tCTreeNode3 != null)
									addMemberNode(tCTreeNode3, arrayOfTCComponent[b1], 5);
							}
							break;
						}
						tCTreeNode2 = findNode(tCTreeNode, this.reg.getString("users"), false);
						if (tCTreeNode2 != null) {
							addMemberNode(tCTreeNode2, arrayOfTCComponent[b1], 5, -1, k);
							AssignmentListHelper assignmentListHelper = (AssignmentListHelper) tCTreeNode2
									.getUserObject();
							assignmentListHelper.setRevQuorum(j);
							assignmentListHelper.setAckQuorum(k);
						}
						break;
					}
				}
			}
		}
	}

	private void applyProcessStatus(TCComponentProcess paramTCComponentProcess) {
		if (paramTCComponentProcess != null && this.processTreeView != null) {
			try {
				TCComponentTask[] arrayOfTCComponentTask1 = paramTCComponentProcess.getRootTask().getSubtasks();
				TCComponentType.cacheTCPropertiesSet(arrayOfTCComponentTask1, new String[] { "state", "task_template" },
						true);
				TCComponentTask[] arrayOfTCComponentTask2;
				int i = (arrayOfTCComponentTask2 = arrayOfTCComponentTask1).length;
				for (int b = 0; b < i; b++) {
					TCComponentTask tCComponentTask = arrayOfTCComponentTask2[b];
					if (tCComponentTask.getState() == TCTaskState.COMPLETED) {
						this.completedTemplates.add(tCComponentTask.getTaskDefinition());
					} else if (tCComponentTask.isTypeOf("EPMReviewTask")
							|| tCComponentTask.isTypeOf("EPMAcknowledgeTask")) {
						TCComponentTask[] arrayOfTCComponentTask3 = tCComponentTask.getSubtasks();
						TCComponentTask[] arrayOfTCComponentTask4;
						int j = (arrayOfTCComponentTask4 = arrayOfTCComponentTask3).length;
						for (int b1 = 0; b1 < j; b1++) {
							TCComponentTask tCComponentTask1 = arrayOfTCComponentTask4[b1];
							if (tCComponentTask1.isTypeOf("EPMSelectSignoffTask")) {
								if (tCComponentTask1.getState() == TCTaskState.COMPLETED)
									this.completedTemplates.add(tCComponentTask.getTaskDefinition());
								break;
							}
						}
					} else if (tCComponentTask.isTypeOf("EPMRouteTask")) {
						TCComponentTask[] arrayOfTCComponentTask3 = tCComponentTask.getSubtasks();
						TCComponentTask[] arrayOfTCComponentTask4;
						int j = (arrayOfTCComponentTask4 = arrayOfTCComponentTask3).length;
						for (int b1 = 0; b1 < j; b1++) {
							TCComponentTask tCComponentTask1 = arrayOfTCComponentTask4[b1];
							if (tCComponentTask1.isTypeOf("EPMReviewTask")) {
								TCComponentTask[] arrayOfTCComponentTask = tCComponentTask1.getSubtasks();
								for (int b2 = 0; b2 < arrayOfTCComponentTask.length; b2++) {
									if (arrayOfTCComponentTask3[b2].isTypeOf("EPMSelectSignoffTask")) {
										if (arrayOfTCComponentTask3[b2].getState() == TCTaskState.COMPLETED)
											this.completedTemplates.add(tCComponentTask.getTaskDefinition());
										break;
									}
								}
								break;
							}
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			this.processTreeView.setRootVisible(true);
			this.processTreeView.repaint();
			this.processTreeView.revalidate();
		} else {
			return;
		}
	}

	private void setTreeContents() {
		if (this.procTemplate != null) {
			try {
				TCComponentTaskTemplate[] arrayOfTCComponentTaskTemplate = this.procTemplate.getSubtaskDefinitions();
				TCComponentType.cacheTCPropertiesSet(arrayOfTCComponentTaskTemplate,
						new String[] { "parent_task_template1", "subtask_template" }, true);
			} catch (TCException tCException) {
				tCException.printStackTrace();
//				logger.error(tCException.getClass().getName(), tCException);
			}
			this.processTreeView.setRoot(this.procTemplate, true);
		}
		AIFTreeNode aIFTreeNode = this.processTreeView.getRootNode();
		aIFTreeNode.setNodeIcon(this.reg.getImageIcon("process.ICON"));
		loadChildrenContents(aIFTreeNode);
		this.session.queueOperation(new AbstractAIFOperation(this.reg.getString("loadingTemplate.MSG")) {
			public void executeOperation() {
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getRootNode();
				aIFTreeNode.setNodeIcon(UserAssignmentListPanel.this.reg.getImageIcon("process.ICON"));
				UserAssignmentListPanel.this.setTreeDisplay((TCTreeNode) aIFTreeNode);
				UserAssignmentListPanel.this.loadChildrenContents(aIFTreeNode);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						UserAssignmentListPanel.this.processTreeView.expandToLevel(-1);
						UserAssignmentListPanel.this.processTreeView.revalidate();
						UserAssignmentListPanel.this.processTreeView.repaint();
					}
				});
			}
		});
	}

	private void loadChildrenContents(AIFTreeNode paramAIFTreeNode) {
		Object[] arrayOfObject = null;
		arrayOfObject = paramAIFTreeNode.getChildNodes();
		if (arrayOfObject != null)
			for (int b = 0; b < arrayOfObject.length; b++) {
				this.processTreeView.processLoadChildren((AIFTreeNode) arrayOfObject[b], false);
				loadChildrenContents((AIFTreeNode) arrayOfObject[b]);
			}
	}

	private void setTreeViewContents() {
		if (this.processTreeView != null)
			this.treeScrollPane.getViewport().setView(this.processTreeView);
		this.treeScrollPane.getViewport().setViewPosition(new Point(0, 0));
		this.treeScrollPane.revalidate();
		this.treeScrollPane.repaint();
	}

	private TCComponent[] getSignoffProfiles(TCComponentTaskTemplate paramTCComponentTaskTemplate) {
		TCComponent[] arrayOfTCComponent = null;
		try {
			TCComponentTaskTemplate[] arrayOfTCComponentTaskTemplate = paramTCComponentTaskTemplate
					.getSubtaskDefinitions();
			if (arrayOfTCComponentTaskTemplate != null && arrayOfTCComponentTaskTemplate.length == 2) {
				TCComponentType.cacheTCPropertiesSet(new Object[] { arrayOfTCComponentTaskTemplate[0] },
						new String[] { "signoff_profiles", "wait_for_all_reviewers", "review_task_quorum" }, true);
				TCProperty tCProperty = arrayOfTCComponentTaskTemplate[0].getTCProperty("signoff_profiles");
				arrayOfTCComponent = tCProperty.getReferenceValueArray();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return arrayOfTCComponent;
	}

	private TCTreeNode findNode(TCTreeNode paramTCTreeNode, String paramString, boolean paramBoolean) {
		TCTreeNode tCTreeNode = null;
		if (paramTCTreeNode == null || paramString == null)
			return null;
		int i = paramTCTreeNode.getChildCount();
		int b;
		for (b = 0; b < i; b++) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) paramTCTreeNode.getChildAt(b);
			if (isMatch(tCTreeNode1, paramString)) {
				tCTreeNode = tCTreeNode1;
				break;
			}
		}
		if (paramBoolean)
			for (b = 0; tCTreeNode == null && b < i; b++) {
				TCTreeNode tCTreeNode1 = (TCTreeNode) paramTCTreeNode.getChildAt(b);
				tCTreeNode = findNode(tCTreeNode1, paramString, true);
			}
		return tCTreeNode;
	}

	private TCTreeNode findNode(TCTreeNode paramTCTreeNode, TCComponent paramTCComponent, boolean paramBoolean) {
		TCTreeNode tCTreeNode = null;
		if (paramTCTreeNode == null || paramTCComponent == null)
			return null;
		int i = paramTCTreeNode.getChildCount();
		int b;
		for (b = 0; b < i; b++) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) paramTCTreeNode.getChildAt(b);
			if (isMatch(tCTreeNode1, paramTCComponent)) {
				tCTreeNode = tCTreeNode1;
				break;
			}
		}
		if (paramBoolean)
			for (b = 0; tCTreeNode == null && b < i; b++) {
				TCTreeNode tCTreeNode1 = (TCTreeNode) paramTCTreeNode.getChildAt(b);
				tCTreeNode = findNode(tCTreeNode1, paramTCComponent, true);
			}
		return tCTreeNode;
	}

	private boolean isMatch(TCTreeNode paramTCTreeNode, TCComponent paramTCComponent) {
		boolean bool = false;
		if (paramTCComponent == paramTCTreeNode.getComponent()) {
			bool = true;
		} else {
			Object object = paramTCTreeNode.getUserObject();
			if (object instanceof AssignmentListHelper
					&& ((AssignmentListHelper) object).getNodeComponent() == paramTCComponent)
				bool = true;
		}
		return bool;
	}

	private boolean isMatch(TCTreeNode paramTCTreeNode, String paramString) {
		boolean bool = false;
		if (paramString != null) {
			TCComponentTaskTemplate tCComponentTaskTemplate = null;
			TCComponent tCComponent = paramTCTreeNode.getComponent();
			if (tCComponent instanceof TCComponentTaskTemplate)
				tCComponentTaskTemplate = (TCComponentTaskTemplate) tCComponent;
			String str = null;
			try {
				TCProperty tCProperty = tCComponentTaskTemplate.getTCProperty("fnd0origin_uid");
				if (tCProperty != null)
					str = tCProperty.getStringValue();
			} catch (Exception exception) {
			}
			if (str != null && str.compareToIgnoreCase(paramString) == 0) {
				bool = true;
			} else if (paramTCTreeNode.toString().compareToIgnoreCase(paramString) == 0) {
				bool = true;
			}
		}
		return bool;
	}

	private void setWaitForUndecidedReviewersBox() {
		int i = 0;
		AIFTreeNode aIFTreeNode = this.processTreeView.getSelectedNode();
		TCTreeNode tCTreeNode1 = getTemplateNodeForSelectedNode(aIFTreeNode);
		TCTreeNode tCTreeNode2 = getUserNodeForTemplateNode(tCTreeNode1);
		Object object = null;
		if (tCTreeNode2 != null)
			object = tCTreeNode2.getUserObject();
		if (object != null && object instanceof AssignmentListHelper) {
			this.helperObject = (AssignmentListHelper) object;
			i = this.helperObject.getWaitForUndecidedReviewers();
			if (i == 0) {
				this.waitForUndecidedReviewersChk.setSelected(false);
			} else if (i == 1) {
				this.waitForUndecidedReviewersChk.setSelected(true);
			}
			this.waitForUndecidedReviewersChk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent param1ActionEvent) {
					int b = 0;
					if (UserAssignmentListPanel.this.waitForUndecidedReviewersChk.isSelected()) {
						b = 1;
					} else {
						b = 0;
					}
					UserAssignmentListPanel.this.helperObject.setWaitForUndecidedReviewers(b);
				}
			});
		}
	}

	private JPanel setQuorum() {
		this.quorumAllString = this.reg.getString("quorumAllString");
		JPanel jPanel1 = new JPanel(new VerticalLayout());
		this.reviewQuorumPanel = new JPanel(new HorizontalLayout(2));
		this.acknowQuorumPanel = new JPanel(new HorizontalLayout(2));
		JPanel jPanel2 = new JPanel(new VerticalLayout());
		FilterDocument filterDocument1 = new FilterDocument("0123456789");
		FilterDocument filterDocument2 = new FilterDocument("0123456789");
		filterDocument1.setUnacceptedBeginChars("0");
		filterDocument2.setUnacceptedBeginChars("0");
		filterDocument1.setNegativeAccepted(false);
		filterDocument2.setNegativeAccepted(false);
		this.quorumField = new JTextField(3);
		this.quorumPercentField = new JTextField(3);
		this.reg = Registry.getRegistry(this);
		this.quorumRadioButton = new JRadioButton(this.reg.getString("quorumNumber"));
		this.quorumRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				UserAssignmentListPanel.this.quorumField.setEnabled(true);
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getSelectedNode();
				TCTreeNode tCTreeNode1 = UserAssignmentListPanel.this.getTemplateNodeForSelectedNode(aIFTreeNode);
				TCTreeNode tCTreeNode2 = UserAssignmentListPanel.this.getUserNodeForTemplateNode(tCTreeNode1);
				Object object = null;
				if (tCTreeNode2 != null)
					object = tCTreeNode2.getUserObject();
				if (object != null && object instanceof UserAssignmentListPanel.AssignmentListHelper) {
					UserAssignmentListPanel.AssignmentListHelper assignmentListHelper = (UserAssignmentListPanel.AssignmentListHelper) object;
					Integer integer = Integer.valueOf(assignmentListHelper.getRevQuorum());
					if (integer.intValue() > 0)
						UserAssignmentListPanel.this.quorumField.setText(integer.toString());
				}
				UserAssignmentListPanel.this.quorumPercentField.setEnabled(false);
				UserAssignmentListPanel.this.quorumPercentField.setText("");
			}
		});
		this.quorumPercentRadioButton = new JRadioButton(this.reg.getString("quorumPercent"));
		this.quorumPercentRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				UserAssignmentListPanel.this.quorumPercentField.setEnabled(true);
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getSelectedNode();
				TCTreeNode tCTreeNode1 = UserAssignmentListPanel.this.getTemplateNodeForSelectedNode(aIFTreeNode);
				TCTreeNode tCTreeNode2 = UserAssignmentListPanel.this.getUserNodeForTemplateNode(tCTreeNode1);
				Object object = null;
				if (tCTreeNode2 != null)
					object = tCTreeNode2.getUserObject();
				if (object != null && object instanceof UserAssignmentListPanel.AssignmentListHelper) {
					UserAssignmentListPanel.AssignmentListHelper assignmentListHelper = (UserAssignmentListPanel.AssignmentListHelper) object;
					Integer integer = Integer.valueOf(assignmentListHelper.getRevQuorum());
					if (integer.intValue() < 0) {
						String str = integer.toString();
						str = str.replace("-", "");
						UserAssignmentListPanel.this.quorumPercentField.setText(str);
					}
				}
				UserAssignmentListPanel.this.quorumField.setEnabled(false);
				UserAssignmentListPanel.this.quorumField.setText("");
				UserAssignmentListPanel.this.editingValue = true;
			}
		});
		ButtonGroup buttonGroup1 = new ButtonGroup();
		buttonGroup1.add(this.quorumRadioButton);
		buttonGroup1.add(this.quorumPercentRadioButton);
		this.quorumField.setDocument(filterDocument1);
		this.quorumField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent param1FocusEvent) {
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getSelectedNode();
				TCTreeNode tCTreeNode1 = UserAssignmentListPanel.this.getTemplateNodeForSelectedNode(aIFTreeNode);
				TCTreeNode tCTreeNode2 = UserAssignmentListPanel.this.getUserNodeForTemplateNode(tCTreeNode1);
				Object object = null;
				if (tCTreeNode2 != null)
					object = tCTreeNode2.getUserObject();
				if (object != null && object instanceof UserAssignmentListPanel.AssignmentListHelper)
					UserAssignmentListPanel.this.helper_Obj = (UserAssignmentListPanel.AssignmentListHelper) object;
			}

			public void focusLost(FocusEvent param1FocusEvent) {
				UserAssignmentListPanel.this.performSetQuorumAction(1,
						UserAssignmentListPanel.this.quorumField.getText(), UserAssignmentListPanel.this.helper_Obj);
			}
		});
		FilterDocument filterDocument3 = new FilterDocument("0123456789");
		filterDocument3.setNegativeAccepted(false);
		filterDocument3.setNumberLimits(1.0D, 100.0D);
		this.quorumPercentField.setDocument(filterDocument3);
		this.quorumPercentField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent param1FocusEvent) {
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getSelectedNode();
				TCTreeNode tCTreeNode1 = UserAssignmentListPanel.this.getTemplateNodeForSelectedNode(aIFTreeNode);
				TCTreeNode tCTreeNode2 = UserAssignmentListPanel.this.getUserNodeForTemplateNode(tCTreeNode1);
				Object object = null;
				if (tCTreeNode2 != null)
					object = tCTreeNode2.getUserObject();
				if (object != null && object instanceof UserAssignmentListPanel.AssignmentListHelper)
					UserAssignmentListPanel.this.helper_Obj = (UserAssignmentListPanel.AssignmentListHelper) object;
			}

			public void focusLost(FocusEvent param1FocusEvent) {
				UserAssignmentListPanel.this.performSetQuorumAction(1,
						UserAssignmentListPanel.this.quorumPercentField.getText(),
						UserAssignmentListPanel.this.helper_Obj);
			}
		});
		this.waitForUndecidedReviewersChk = new JCheckBox(this.reg.getString("WaitForUndecidedReviewers"));
		this.ackQuorumField = new JTextField(3) {
			public void setText(String param1String) {
				if (param1String != null && param1String.equals("-100")) {
					UserAssignmentListPanel.this.ackPercentQuorumField.setText("100");
					UserAssignmentListPanel.this.ackPercentRadioButton.setSelected(true);
					UserAssignmentListPanel.this.ackQuorumField.setEnabled(false);
					UserAssignmentListPanel.this.ackPercentQuorumField.setEnabled(true);
					super.setText("");
				} else {
					super.setText(param1String);
				}
			}
		};
		this.ackQuorumField.setDocument(filterDocument2);
		this.ackQuorumField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent param1FocusEvent) {
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getSelectedNode();
				TCTreeNode tCTreeNode1 = UserAssignmentListPanel.this.getTemplateNodeForSelectedNode(aIFTreeNode);
				TCTreeNode tCTreeNode2 = UserAssignmentListPanel.this.getUserNodeForTemplateNode(tCTreeNode1);
				Object object = null;
				if (tCTreeNode2 != null)
					object = tCTreeNode2.getUserObject();
				if (object != null && object instanceof UserAssignmentListPanel.AssignmentListHelper)
					UserAssignmentListPanel.this.helper_Obj = (UserAssignmentListPanel.AssignmentListHelper) object;
			}

			public void focusLost(FocusEvent param1FocusEvent) {
				UserAssignmentListPanel.this.performSetQuorumAction(2,
						UserAssignmentListPanel.this.ackQuorumField.getText(), UserAssignmentListPanel.this.helper_Obj);
			}
		});
		this.ackPercentQuorumField = new JTextField(3);
		FilterDocument filterDocument4 = new FilterDocument("0123456789");
		filterDocument4.setNegativeAccepted(false);
		filterDocument4.setNumberLimits(1.0D, 100.0D);
		this.ackPercentQuorumField.setDocument(filterDocument4);
		this.ackPercentQuorumField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent param1FocusEvent) {
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getSelectedNode();
				TCTreeNode tCTreeNode1 = UserAssignmentListPanel.this.getTemplateNodeForSelectedNode(aIFTreeNode);
				TCTreeNode tCTreeNode2 = UserAssignmentListPanel.this.getUserNodeForTemplateNode(tCTreeNode1);
				Object object = null;
				if (tCTreeNode2 != null)
					object = tCTreeNode2.getUserObject();
				if (object != null && object instanceof UserAssignmentListPanel.AssignmentListHelper)
					UserAssignmentListPanel.this.helper_Obj = (UserAssignmentListPanel.AssignmentListHelper) object;
			}

			public void focusLost(FocusEvent param1FocusEvent) {
				UserAssignmentListPanel.this.performSetQuorumAction(2,
						UserAssignmentListPanel.this.ackPercentQuorumField.getText(),
						UserAssignmentListPanel.this.helper_Obj);
			}
		});
		this.ackRadioButton = new JRadioButton(this.reg.getString("quorumNumber"));
		this.ackRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				UserAssignmentListPanel.this.ackQuorumField.setEnabled(true);
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getSelectedNode();
				TCTreeNode tCTreeNode1 = UserAssignmentListPanel.this.getTemplateNodeForSelectedNode(aIFTreeNode);
				TCTreeNode tCTreeNode2 = UserAssignmentListPanel.this.getUserNodeForTemplateNode(tCTreeNode1);
				Object object = null;
				if (tCTreeNode2 != null)
					object = tCTreeNode2.getUserObject();
				if (object != null && object instanceof UserAssignmentListPanel.AssignmentListHelper) {
					UserAssignmentListPanel.AssignmentListHelper assignmentListHelper = (UserAssignmentListPanel.AssignmentListHelper) object;
					Integer integer = Integer.valueOf(assignmentListHelper.getAckQuorum());
					if (integer.intValue() > 0)
						UserAssignmentListPanel.this.ackQuorumField.setText(integer.toString());
				}
				UserAssignmentListPanel.this.ackPercentQuorumField.setEnabled(false);
				UserAssignmentListPanel.this.ackPercentQuorumField.setText("");
			}
		});
		this.ackPercentRadioButton = new JRadioButton(this.reg.getString("quorumPercent"));
		this.ackPercentRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				UserAssignmentListPanel.this.ackPercentQuorumField.setEnabled(true);
				AIFTreeNode aIFTreeNode = UserAssignmentListPanel.this.processTreeView.getSelectedNode();
				TCTreeNode tCTreeNode1 = UserAssignmentListPanel.this.getTemplateNodeForSelectedNode(aIFTreeNode);
				TCTreeNode tCTreeNode2 = UserAssignmentListPanel.this.getUserNodeForTemplateNode(tCTreeNode1);
				Object object = null;
				if (tCTreeNode2 != null)
					object = tCTreeNode2.getUserObject();
				if (object != null && object instanceof UserAssignmentListPanel.AssignmentListHelper) {
					UserAssignmentListPanel.AssignmentListHelper assignmentListHelper = (UserAssignmentListPanel.AssignmentListHelper) object;
					Integer integer = Integer.valueOf(assignmentListHelper.getAckQuorum());
					if (integer.intValue() < 0) {
						String str = integer.toString();
						str = str.replace("-", "");
						UserAssignmentListPanel.this.ackPercentQuorumField.setText(str);
					}
				}
				UserAssignmentListPanel.this.ackQuorumField.setEnabled(false);
				UserAssignmentListPanel.this.ackQuorumField.setText("");
				UserAssignmentListPanel.this.editingValue = true;
			}
		});
		ButtonGroup buttonGroup2 = new ButtonGroup();
		buttonGroup2.add(this.ackRadioButton);
		buttonGroup2.add(this.ackPercentRadioButton);
		this.quorumField.setEnabled(true);
		this.ackQuorumField.setEnabled(true);
		this.percentLabel = new JLabel(this.reg.getString("percentLabel"));
		this.ackpercentLabel = new JLabel(this.reg.getString("percentLabel"));
		this.revQuorumTitleBorder = new TitledBorder(BorderFactory.createEtchedBorder(), "");
		String str1 = this.reg.getString("revQuorumLabelKey");
		this.reviewQuorumPanel.setBorder(this.revQuorumTitleBorder);
		this.revQuorumTitleBorder.setTitle(str1);
		this.reviewQuorumPanel.add("left", this.quorumRadioButton);
		this.reviewQuorumPanel.add("left", this.quorumField);
		this.reviewQuorumPanel.add("left", this.quorumPercentRadioButton);
		this.reviewQuorumPanel.add("left", this.quorumPercentField);
		this.reviewQuorumPanel.add("left", this.percentLabel);
		jPanel2.add("left", this.waitForUndecidedReviewersChk);
		this.ackQuorumTitleBorder = new TitledBorder(BorderFactory.createEtchedBorder(), "");
		String str2 = this.reg.getString("ackQuorumLabelKey");
		this.acknowQuorumPanel.setBorder(this.ackQuorumTitleBorder);
		this.ackQuorumTitleBorder.setTitle(str2);
		this.acknowQuorumPanel.add("left", this.ackRadioButton);
		this.acknowQuorumPanel.add("left", this.ackQuorumField);
		this.acknowQuorumPanel.add("left", this.ackPercentRadioButton);
		this.acknowQuorumPanel.add("left", this.ackPercentQuorumField);
		this.acknowQuorumPanel.add("left", this.ackpercentLabel);
		jPanel1.add("bottom.bind.center.center", jPanel2);
		jPanel1.add("bottom.bind.center.center", this.acknowQuorumPanel);
		jPanel1.add("bottom.bind.center.center", this.reviewQuorumPanel);
		return jPanel1;
	}

	private void performSetQuorumAction(int paramInt, String paramString,
			AssignmentListHelper paramAssignmentListHelper) {
		if (paramAssignmentListHelper != null) {
			if (paramString.compareTo(this.quorumAllString) == 0 || paramString == null || paramString.equals(""))
				paramString = "-100";
			if (paramInt == 1) {
				if (paramString.equals("-100")) {
					if (this.quorumRadioButton.isSelected())
						this.quorumPercentRadioButton.doClick();
					this.quorumPercentField.setText("100");
				} else if (this.quorumPercentRadioButton.isSelected()) {
					paramString = "-" + paramString;
				}
				Integer integer = Integer.valueOf(Integer.parseInt(paramString));
				paramAssignmentListHelper.setRevQuorum(integer.intValue());
			} else if (paramInt == 2) {
				if (paramString.equals("-100")) {
					if (this.ackRadioButton.isSelected())
						this.ackPercentRadioButton.doClick();
					this.ackPercentQuorumField.setText("100");
				} else if (this.ackPercentRadioButton.isSelected()) {
					paramString = "-" + paramString;
				}
				Integer integer = Integer.valueOf(Integer.parseInt(paramString));
				paramAssignmentListHelper.setAckQuorum(integer.intValue());
			}
		}
	}

	private int[] getSignoffQuorum(TCComponentTaskTemplate paramTCComponentTaskTemplate) throws TCException {
		int[] arrayOfInt = new int[2];
		arrayOfInt[0] = -100;
		arrayOfInt[1] = -100;
		try {
			String str = paramTCComponentTaskTemplate.getType();
			if (str.compareTo("EPMReviewTaskTemplate") == 0) {
				TCComponentTaskTemplate[] arrayOfTCComponentTaskTemplate = paramTCComponentTaskTemplate
						.getSubtaskDefinitions();
				if (arrayOfTCComponentTaskTemplate != null && arrayOfTCComponentTaskTemplate.length == 2) {
					TCProperty tCProperty = arrayOfTCComponentTaskTemplate[0].getTCProperty("review_task_quorum");
					if (tCProperty != null)
						arrayOfInt[0] = tCProperty.getIntValue();
				}
			} else if (str.compareTo("EPMAcknowledgeTaskTemplate") == 0) {
				TCComponentTaskTemplate[] arrayOfTCComponentTaskTemplate = paramTCComponentTaskTemplate
						.getSubtaskDefinitions();
				if (arrayOfTCComponentTaskTemplate != null && arrayOfTCComponentTaskTemplate.length == 2) {
					TCProperty tCProperty = arrayOfTCComponentTaskTemplate[0].getTCProperty("review_task_quorum");
					if (tCProperty != null)
						arrayOfInt[1] = tCProperty.getIntValue();
				}
			} else if (str.compareTo("EPMRouteTaskTemplate") == 0) {
				TCComponentTaskTemplate[] arrayOfTCComponentTaskTemplate = paramTCComponentTaskTemplate
						.getSubtaskDefinitions();
				if (arrayOfTCComponentTaskTemplate != null && arrayOfTCComponentTaskTemplate.length == 3) {
					int[] arrayOfInt1 = getSignoffQuorum(arrayOfTCComponentTaskTemplate[0]);
					int[] arrayOfInt2 = getSignoffQuorum(arrayOfTCComponentTaskTemplate[1]);
					arrayOfInt[0] = arrayOfInt1[0];
					arrayOfInt[1] = arrayOfInt2[1];
				}
			}
		} catch (TCException tCException) {
			throw tCException;
		}
		return arrayOfInt;
	}

	private int getWaitUndecidedReviewers(TCComponentTaskTemplate paramTCComponentTaskTemplate) throws TCException {
		int i = 0;
		try {
			String str = paramTCComponentTaskTemplate.getType();
			if (str.compareTo("EPMReviewTaskTemplate") == 0) {
				TCComponentTaskTemplate[] arrayOfTCComponentTaskTemplate = paramTCComponentTaskTemplate
						.getSubtaskDefinitions();
				if (arrayOfTCComponentTaskTemplate != null && arrayOfTCComponentTaskTemplate.length == 2) {
					TCProperty tCProperty = arrayOfTCComponentTaskTemplate[0].getTCProperty("wait_for_all_reviewers");
					if (tCProperty != null)
						i = tCProperty.getIntValue();
				}
			} else if (str.compareTo("EPMRouteTaskTemplate") == 0) {
				TCComponentTaskTemplate[] arrayOfTCComponentTaskTemplate = paramTCComponentTaskTemplate
						.getSubtaskDefinitions();
				if (arrayOfTCComponentTaskTemplate != null && arrayOfTCComponentTaskTemplate.length == 3)
					i = getWaitUndecidedReviewers(arrayOfTCComponentTaskTemplate[0]);
			}
		} catch (TCException tCException) {
			throw tCException;
		}
		return i;
	}

	public void expandNode(final AIFTreeNode node) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				TreePath treePath = UserAssignmentListPanel.this.getPathForNode(node);
				if (UserAssignmentListPanel.this.processTreeView.isCollapsed(treePath))
					UserAssignmentListPanel.this.processTreeView.expandPath(treePath);
				UserAssignmentListPanel.this.processTreeView.setSelectedNode(node);
			}
		});
	}

	public TreePath getPathForNode(AIFTreeNode paramAIFTreeNode) {
		return (paramAIFTreeNode == null) ? null : new TreePath(paramAIFTreeNode.getPath());
	}

	public TCComponentAssignmentList getListToSave() {
		return (this.isModifiable && this.saveEdits.isSelected()) ? this.assignmentList : null;
	}

	private void enableSaveEdits() {
		if (this.assignmentList != null) {
			this.saveEdits.setEnabled(this.isModifiable);
			this.saveAsList.setEnabled(!this.isModifiable);
		} else {
			this.saveEdits.setEnabled(false);
			this.saveAsList.setEnabled(false);
		}
	}

	public String getNameForNewList() {
		return (!this.isModifiable && this.saveAsList.isSelected()) ? this.newListName.getText() : null;
	}

	public boolean verifyUserProfile(TCTreeNode paramTCTreeNode, TCComponent paramTCComponent) {
		boolean bool = true;
		try {
			TCComponent tCComponent = paramTCTreeNode.getComponent();
			if (tCComponent != null && tCComponent instanceof TCComponentProfile) {
				TCComponentProfile tCComponentProfile = (TCComponentProfile) tCComponent;
				if (tCComponentProfile != null && paramTCComponent != null) {
					TCComponentGroup tCComponentGroup = tCComponentProfile.getGroup();
					TCComponentRole tCComponentRole = tCComponentProfile.getRole();
					String str1 = null;
					String str2 = null;
					if (tCComponentGroup != null)
						str1 = tCComponentGroup.toString();
					if (tCComponentRole != null)
						str2 = tCComponentRole.toString();
					String str3 = null;
					String str4 = null;
					if (paramTCComponent instanceof TCComponentGroupMember) {
						TCComponentGroupMember tCComponentGroupMember = (TCComponentGroupMember) paramTCComponent;
						str3 = tCComponentGroupMember.getGroup().toString();
						str4 = tCComponentGroupMember.getRole().toString();
					} else if (paramTCComponent instanceof TCComponentResourcePool) {
						TCComponentResourcePool tCComponentResourcePool = (TCComponentResourcePool) paramTCComponent;
						str3 = tCComponentResourcePool.getGroup().toString();
						str4 = tCComponentResourcePool.getRole().toString();
					}
					if (str3 != null && str1 != null && !str1.equals(str3))
						bool = false;
					if (str4 != null && str2 != null && !str2.equals(str4))
						bool = false;
					if (!bool)
						MessageBox.post(this.parent, this.reg.getString("userDoesNotBelongToProfile"),
								this.reg.getString("error.TITLE"), 1);
				}
			}
		} catch (Exception exception) {
		}
		return bool;
	}

	public void setTargetObjects(TCComponent[] paramArrayOfTCComponent) {
		this.targetComps = paramArrayOfTCComponent;
	}

	public class AssignmentListHelper {
		public static final int PERFORM = 0;

		public static final int REVIEW = 1;

		public static final int ACKNOW = 2;

		public static final int NOTIFY = 3;

		public static final int REVIEW_REQUIRED = 4;

		public static final int ACKNOW_REQUIRED = 5;

		public static final int PROFILE_PARENT_NODE = -1;

		public static final int PROFILE_NODE = 0;

		public static final int RESOURCE_NODE = 1;

		public static final int PROFILE_USER_NODE = 2;

		public static final int ADHOC_USER_NODE = 3;

		protected TCComponent nodeComponent = null;

		protected String nodeTitle = null;

		protected int WaitForUndecidedReviewers = 0;

		protected int nodeType = -1;

		protected int nodeAction = -1;

		protected ImageIcon nodeIcon = null;

		protected int revQuorum = -100;

		protected int ackQuorum = -100;

		protected int origRevQuorum = -100;

		protected int origAckQuorum = -100;

		protected Vector validActions = null;

		public AssignmentListHelper(String param1String, ImageIcon param1ImageIcon, int param1Int) {
			this(param1String, param1ImageIcon, param1Int, null);
		}

		public AssignmentListHelper(String param1String, ImageIcon param1ImageIcon, int param1Int,
				Vector param1Vector) {
			this.nodeTitle = param1String;
			this.nodeIcon = param1ImageIcon;
			this.nodeType = param1Int;
			this.validActions = param1Vector;
		}

		public AssignmentListHelper(TCComponent param1TCComponent, ImageIcon param1ImageIcon, int param1Int,
				Vector param1Vector) {
			this.nodeComponent = param1TCComponent;
			this.nodeIcon = param1ImageIcon;
			this.nodeType = 1;
			this.nodeAction = param1Int;
			this.validActions = param1Vector;
		}

		public AssignmentListHelper(TCComponent param1TCComponent, int param1Int, Vector param1Vector) {
			this(param1TCComponent, TCTypeRenderer.getIcon(param1TCComponent), param1Int, param1Vector);
		}

		public TCComponent getNodeComponent() {
			return this.nodeComponent;
		}

		public String getNodeTitle() {
			return this.nodeTitle;
		}

		public int getNodeType() {
			return this.nodeType;
		}

		public ImageIcon getNodeIcon() {
			return this.nodeIcon;
		}

		public int getNodeAction() {
			return this.nodeAction;
		}

		public Vector getValidActions() {
			return this.validActions;
		}

		public void setValidActions(Vector param1Vector) {
			this.validActions = param1Vector;
		}

		public void setAckQuorum(int param1Int) {
			this.ackQuorum = param1Int;
		}

		public void setRevQuorum(int param1Int) {
			this.revQuorum = param1Int;
		}

		public int getAckQuorum() {
			return this.ackQuorum;
		}

		public int getRevQuorum() {
			return this.revQuorum;
		}

		public void setWaitForUndecidedReviewers(int param1Int) {
			this.WaitForUndecidedReviewers = param1Int;
		}

		public int getWaitForUndecidedReviewers() {
			return this.WaitForUndecidedReviewers;
		}

		public String toString() {
			String str = null;
			if (this.nodeTitle != null) {
				str = this.nodeTitle;
			} else {
				try {
					if (this.nodeComponent instanceof TCComponentGroupMember) {
						TCComponentGroupMember tCComponentGroupMember = (TCComponentGroupMember) this.nodeComponent;
						str = String.valueOf(tCComponentGroupMember.getUser().toString()) + "/"
								+ tCComponentGroupMember.getGroup().toString() + "/"
								+ tCComponentGroupMember.getRole().toString();
					} else if (this.nodeComponent instanceof TCComponentResourcePool) {
						TCComponentResourcePool tCComponentResourcePool = (TCComponentResourcePool) this.nodeComponent;
						boolean bool = false;
						try {
							bool = tCComponentResourcePool.isAllMemberResPool();
						} catch (TCException tCException) {
							tCException.printStackTrace();
//							logger.error(tCException.getClass().getName(), tCException);
						}
						String str1 = null;
						if (tCComponentResourcePool.getGroup() == null) {
							str1 = "*";
						} else {
							str1 = tCComponentResourcePool.getGroup().toString();
						}
						String str2 = null;
						if (tCComponentResourcePool.getRole() == null) {
							str2 = "*";
						} else {
							str2 = tCComponentResourcePool.getRole().toString();
						}
						if (bool) {
							str = "ALL/" + str1 + "/" + str2;
						} else {
							str = "*/" + str1 + "/" + str2;
						}
					} else {
						str = this.nodeComponent.toString();
					}
				} catch (Exception exception) {
					str = this.nodeComponent.toString();
				}
			}

			return str;
		}

		public boolean isActionShow() {
			return (this.nodeType == 1);
		}

		public void setIcon(ImageIcon param1ImageIcon) {
			this.nodeIcon = param1ImageIcon;
		}

		public int compareTo(Object param1Object) {
			int i = toString().compareTo(param1Object.toString());
			if (param1Object instanceof TCTreeNode) {
				TCTreeNode tCTreeNode = (TCTreeNode) param1Object;
				Object object = tCTreeNode.getUserObject();
				if (object != null) {
					boolean _tmp = object instanceof AssignmentListHelper;
				}
			}
			return i;
		}

		public int getOrigRevQuorum() {
			return this.origRevQuorum;
		}

		public void setOrigRevQuorum(int param1Int) {
			this.origRevQuorum = param1Int;
		}

		public int getOrigAckQuorum() {
			return this.origAckQuorum;
		}

		public void setOrigAckQuorum(int param1Int) {
			this.origAckQuorum = param1Int;
		}

		public void setNodeAction(int param1Int) {
			this.nodeAction = param1Int;
		}
	}

	public class ResourceTreeCellEditor extends AbstractCellEditor implements TreeCellEditor, ItemListener {
		private UserAssignmentListPanel.ResourceTreeCellRender renderer;

		private TCTree tree;

		protected Registry r;

		public ResourceTreeCellEditor(final TCTree tree) {
			this.renderer = new UserAssignmentListPanel.ResourceTreeCellRender();
			this.tree = null;
			this.tree = tree;
			this.r = Registry.getRegistry(this);
			MouseAdapter mouseAdapter = new MouseAdapter() {
				public void mousePressed(MouseEvent param2MouseEvent) {
					int i = tree.getRowForLocation(param2MouseEvent.getX(), param2MouseEvent.getY());
					TreePath treePath = tree.getPathForLocation(param2MouseEvent.getX(), param2MouseEvent.getY());
					if (i != -1 && param2MouseEvent.getClickCount() == 1)
						tree.startEditingAtPath(treePath);
				}
			};
			tree.addMouseListener(mouseAdapter);
			this.renderer.requiredCheckBox.addItemListener(this);
		}

//		public ResourceTreeCellEditor(TCTree arg2)
//	    {
//	      TCTree localTCTree;
//	      this.tree = localTCTree;
//	      this.r = Registry.getRegistry(this);
//	      1 local1 = new MouseAdapter(localTCTree)
//	      {
//	        public void mousePressed(MouseEvent paramMouseEvent)
//	        {
//	          int i = this.val$tree.getRowForLocation(paramMouseEvent.getX(), paramMouseEvent.getY());
//	          TreePath localTreePath = this.val$tree.getPathForLocation(paramMouseEvent.getX(), paramMouseEvent.getY());
//	          if ((i != -1) && (paramMouseEvent.getClickCount() == 1))
//	            this.val$tree.startEditingAtPath(localTreePath);
//	        }
//	      };
//	      localTCTree.addMouseListener(local1);
//	      this.renderer.requiredCheckBox.addItemListener(this);
//	    }

		public void itemStateChanged(ItemEvent param1ItemEvent) {
			JCheckBox jCheckBox = (JCheckBox) param1ItemEvent.getItem();
			UserAssignmentListPanel.AssignmentListHelper assignmentListHelper = this.renderer.helperObj;
			if (assignmentListHelper != null) {
				int i = assignmentListHelper.getNodeAction();
				if (jCheckBox.isSelected()) {
					if (i == 1)
						assignmentListHelper.setNodeAction(4);
					if (i == 2)
						assignmentListHelper.setNodeAction(5);
				} else {
					if (i == 4)
						assignmentListHelper.setNodeAction(1);
					if (i == 5)
						assignmentListHelper.setNodeAction(2);
				}
			}
			this.renderer.revalidate();
			this.renderer.repaint();
		}

		public Object getCellEditorValue() {
			return (TCTreeNode) this.tree.getLastSelectedPathComponent();
		}

		public Component getTreeCellEditorComponent(JTree param1JTree, Object param1Object, boolean param1Boolean1,
				boolean param1Boolean2, boolean param1Boolean3, int param1Int) {
			return this.renderer.getTreeCellRendererComponent(param1JTree, param1Object, param1Boolean1, param1Boolean2,
					param1Boolean3, param1Int, false);
		}

		public boolean shouldSelectCell(EventObject param1EventObject) {
			return true;
		}

		public boolean isCellEditable(EventObject param1EventObject) {
			return true;
		}
	}

	public class ResourceTreeCellRender extends JPanel implements TreeCellRenderer {
		private final int levelDifference = 20;

		private final int Node_Text_Max_Len = 220;

		protected boolean selected;

		protected Color textSelectionColor = Registry
				.getColorForSwingComponent("ResourceTreeCellRenderer.textSelection");

		protected Color textNonSelectionColor = Registry
				.getColorForSwingComponent("ResourceTreeCellRenderer.textNonSelection");

		protected Color backgroundSelectionColor = Registry
				.getColorForSwingComponent("ResourceTreeCellRenderer.backgroundSelection");

		protected Color backgroundNonSelectionColor = Registry
				.getColorForSwingComponent("ResourceTreeCellRenderer.backgroundNonSelectionColor");

		protected JLabel label = new JLabel();

		protected JPanel buttonPanel;

		protected JLabel actionList;

		private JLabel labelAction;

		private FontMetrics metrics;

		protected JCheckBox requiredCheckBox;

		protected UserAssignmentListPanel.AssignmentListHelper helperObj = null;

		public ResourceTreeCellRender() {
			super(new GridLayout(1, 2));
			this.label.setHorizontalAlignment(2);
			add(this.label);
			Font font = this.label.getFont();
			font = new Font(font.getName(), 1, font.getSize());
			this.labelAction = new JLabel(UserAssignmentListPanel.this.reg.getString("action"));
			this.labelAction.setFont(font);
			this.actionList = new JLabel();
			this.metrics = getFontMetrics(font);
			this.buttonPanel = new JPanel(new HorizontalLayout(0, 5, 2, 2, 2));
			this.buttonPanel.setBackground(getBackgroundNonSelectionColor());
			this.requiredCheckBox = new JCheckBox();
			this.requiredCheckBox.setBackground(getBackgroundNonSelectionColor());
			this.requiredCheckBox.setVisible(false);
			this.buttonPanel.add("left.bind", this.actionList);
			this.buttonPanel.add("lef.bind", this.requiredCheckBox);
			this.actionList.setVisible(true);
			font = this.label.getFont();
			this.metrics = getFontMetrics(font);
			add(this.buttonPanel);
		}

		public Color getTextSelectionColor() {
			return this.textSelectionColor;
		}

		public Color getTextNonSelectionColor() {
			return this.textNonSelectionColor;
		}

		public Color getBackgroundSelectionColor() {
			return this.backgroundSelectionColor;
		}

		public Color getBackgroundNonSelectionColor() {
			return this.backgroundNonSelectionColor;
		}

		public Component getTreeCellRendererComponent(JTree param1JTree, Object param1Object, boolean param1Boolean1,
				boolean param1Boolean2, boolean param1Boolean3, int param1Int, boolean param1Boolean4) {
			Icon icon = null;
			if (param1Boolean1) {
				setForeground(getTextSelectionColor());
				setBackground(getBackgroundSelectionColor());
				this.label.setForeground(getTextSelectionColor());
				this.label.setBackground(getBackgroundSelectionColor());
			} else {
				setForeground(getTextNonSelectionColor());
				setBackground(getBackgroundNonSelectionColor());
				this.label.setForeground(getTextNonSelectionColor());
				this.label.setBackground(getBackgroundNonSelectionColor());
			}
			this.selected = param1Boolean1;
			setText(param1Object.toString());
			int b = 0;
			int i = -1;
			setLabelSize(b, param1Object);
			boolean bool = false;
			if (param1Object instanceof TCTreeNode) {
				TCTreeNode tCTreeNode = (TCTreeNode) param1Object;
				Object object = tCTreeNode.getUserObject();
				if (object != null && object instanceof UserAssignmentListPanel.AssignmentListHelper) {
					UserAssignmentListPanel.AssignmentListHelper assignmentListHelper = (UserAssignmentListPanel.AssignmentListHelper) object;
					assignmentListHelper = (UserAssignmentListPanel.AssignmentListHelper) object;
					icon = assignmentListHelper.getNodeIcon();
					bool = assignmentListHelper.isActionShow();
					i = assignmentListHelper.getNodeAction();
					assignmentListHelper.getNodeType();
				} else {
					TCComponent tCComponent = tCTreeNode.getComponent();
					if (tCComponent instanceof TCComponentTaskTemplate) {
						this.requiredCheckBox.setVisible(false);
						if (UserAssignmentListPanel.this.completedTemplates != null
								&& UserAssignmentListPanel.this.completedTemplates.contains(tCComponent))
							this.label.setForeground(Color.RED);
					}
				}
				icon = tCTreeNode.getNodeIcon();
			}
			setActionLabel(i, bool);
			if (icon == null)
				icon = getDefaultIcon(param1Object);
			this.label.setIcon(icon);
			this.label.revalidate();
			this.label.repaint();
			revalidate();
			repaint();
			return this;
		}

		private Icon getDefaultIcon(Object param1Object) {
			ImageIcon imageIcon = null;
			Registry registry = Registry.getRegistry(this);
			if (param1Object instanceof TCTreeNode) {
				TCTreeNode tCTreeNode = (TCTreeNode) param1Object;
				TCComponent tCComponent = tCTreeNode.getComponent();
				if (tCTreeNode.getLevel() == 0) {
					imageIcon = registry.getImageIcon("process.ICON");
				} else {
					imageIcon = TCTypeRenderer.getIcon(tCComponent);
				}
			}
			return imageIcon;
		}

		private void setActionLabel(int param1Int, boolean param1Boolean) {
			String str = null;
			switch (param1Int) {
			case 0:
				str = UserAssignmentListPanel.this.reg.getString("perform");
				this.requiredCheckBox.setSelected(false);
				this.requiredCheckBox.setEnabled(true);
				break;
			case 1:
				str = UserAssignmentListPanel.this.reg.getString("review");
				this.requiredCheckBox.setSelected(false);
				this.requiredCheckBox.setEnabled(true);
				break;
			case 2:
				str = UserAssignmentListPanel.this.reg.getString("acknow");
				this.requiredCheckBox.setSelected(false);
				this.requiredCheckBox.setEnabled(true);
				break;
			case 3:
				str = UserAssignmentListPanel.this.reg.getString("notify");
				this.requiredCheckBox.setSelected(false);
				this.requiredCheckBox.setEnabled(false);
				break;
			case 4:
				str = UserAssignmentListPanel.this.reg.getString("review");
				this.requiredCheckBox.setSelected(true);
				this.requiredCheckBox.setEnabled(true);
				break;
			case 5:
				str = UserAssignmentListPanel.this.reg.getString("acknow");
				this.requiredCheckBox.setSelected(true);
				this.requiredCheckBox.setEnabled(true);
				break;
			}
			this.actionList.setText(str);
			this.actionList.setVisible(param1Boolean);
			if (param1Int == 0) {
				this.requiredCheckBox.setVisible(false);
			} else {
				this.requiredCheckBox.setVisible(param1Boolean);
			}
		}

		private void setLabelSize(int param1Int, Object param1Object) {
			Dimension dimension = null;
			int i = 20 * param1Int;
			int j = 220 - i;
			if (param1Int != 0 && !this.actionList.isVisible()) {
				int i1 = this.metrics.stringWidth(param1Object.toString()) + 5 + 16;
				j = (j > i1) ? j : i1;
			}
			int k = 15;
			int m = (this.label.getPreferredSize()).height;
			int n = (this.buttonPanel.getPreferredSize()).height;
			k = (m > k) ? m : k;
			k = (n > k) ? n : k;
			dimension = new Dimension(j, k);
			this.label.setPreferredSize(dimension);
		}

		private void setText(String param1String) {
			this.label.setText(param1String);
		}
	}

	// recompile 20220323143000 : START
	protected static String pubMail = "";
	protected static String taskName = "";
	// 20220323143000 : END

	private boolean isMemberExist_1(TCTreeNode paramTCTreeNode, TCComponent paramTCComponent) {
		boolean bool = false;
		if (paramTCComponent instanceof TCComponentResourcePool)
			return bool;
		int i = paramTCTreeNode.getChildCount();
		try {
			for (int b = 0; b < i; b++) {
				TCTreeNode tCTreeNode = (TCTreeNode) paramTCTreeNode.getChildAt(b);
				AssignmentListHelper assignmentListHelper = (AssignmentListHelper) tCTreeNode.getUserObject();
				TCComponent tCComponent = assignmentListHelper.getNodeComponent();
				if (tCComponent == paramTCComponent) {
					bool = true;
					break;
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return bool;
	}

	/**
	 * 自动指派节点审批人
	 */
	public void addUserNode() {
		try {
			TCUtil.setBypass(session); // 开启旁路
			TCTreeNode tCTreeNode = (TCTreeNode) UserAssignmentListPanel.this.getProcessTreeView().getRootNode();
			//tCTreeNode.refresh();
			String string = tCTreeNode.toString();
			HashMap<String, WorkGroup> workUser = UserNewProcessDialog.workUser;
			String userId = UserNewProcessDialog.actualUserIds;
			String projectId = UserNewProcessDialog.projectId;
			System.out.println("---------->" + string + ",userId = " + userId);
			if (string.equals("FXN02_Document Level2 Review Process")
					|| string.equals("FXN03_Document Level3 Review Process")
					|| string.equals("FXN06_Design Release Process")
					|| string.equals("FXN13_Design Mass production Release Process")
					|| string.equals("FXN15_Document Obsolete Level2 Process")) {

				FXN02_Document(tCTreeNode, workUser, userId, projectId);
			} else if (string.equals("FXN34_MNT Material Application Process")) {
				FXN34_MNT(tCTreeNode, workUser, userId, projectId);
			} else if (string.equals("FXN08_MNT EE Schematic Release Process")) {
				FXN08_MNT(tCTreeNode, workUser, userId, projectId);
			} else if (string.equals("FXN19_MNT DCN Process - Update")
					|| string.equals("FXN19_MNT DCN Process - Update for PSU")) {
				FXN19_MNT(tCTreeNode, workUser, userId, projectId);
			} else if (string.contains("FXN31_MNT BOM CoWork Process")
					|| string.contains("FXN31_MNT BOM CoWork Process for PSU")) {
				FXN31_MNT(tCTreeNode, workUser, userId, projectId);
			} else if (string.equals("FXN35_MNT PCB Layout Boardfile Release Process")) {
				FXN35_MNT(tCTreeNode, workUser, userId, projectId);
			} else if (string.equals("FXN38_MNT DCN Quick Released Process")) {
				FXN38_MNT(tCTreeNode, workUser, userId, projectId);
			} else if (string.equals("FXN39_Design Obsolete Level1 Review Process")
					|| string.equals("FXN40_Design Obsolete Process")) {
				FXN39_Design(tCTreeNode, workUser, userId, projectId);
			} else if (string.equals("FXN41_MNT PCBA BOM製作申請流程")) {
				FXN41_MNT(tCTreeNode, workUser, userId, projectId);
			} else if (string.equals("FXN42_MNT Cable Design Release Process")) {
				FXN42_MNT(tCTreeNode, workUser, userId, projectId);
			}

		} catch (TCException e) {
			e.printStackTrace();
		} finally {
			TCUtil.closeBypass(session);
		}

		
	}

	/**
	 * 自动指派节点审批人
	 */
	public void addUserNode_DT() {
		try {
			TCUtil.setBypass(session); // 开启旁路
			TCTreeNode tCTreeNode = (TCTreeNode) this.getProcessTreeView().getRootNode();
			tCTreeNode.refresh();
			String string = tCTreeNode.toString();

			String[] autoAssignWorkflowTemplates = UserExtNewProcessDialog.autoAssignWorkflowTemplates;
			if (UserExtNewProcessDialog.checkProcessName(autoAssignWorkflowTemplates, string)) {
				autoAssignWorkflow(tCTreeNode, string);
			}
		} catch (TCException e) {
			e.printStackTrace();
		} finally {
			TCUtil.closeBypass(session);
		}

	}

	public void FXN02_Document(TCTreeNode tCTreeNode, HashMap<String, WorkGroup> workUser, String userId,
			String projectId) throws TCException {
		int tCTreeNo = tCTreeNode.getChildCount();
		for (int b = 0; b < tCTreeNo; b++) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);
			TCComponentTaskTemplate task = (TCComponentTaskTemplate) tCTreeNode1.getComponent();
			String name = task.getName();
			System.out.println("name = " + name);
			//tCTreeNode1.refresh();
			Object[] childNodes = tCTreeNode1.getChildNodes();

			if (!"Approve".equalsIgnoreCase(name) && !"Review".equalsIgnoreCase(name)) {
				continue;
			}
			int j = UserAssignmentListPanel.this.currentPanel.getActionType();

			if (childNodes != null && childNodes.length > 0) {

				if (workUser != null && !"".equals(userId)) {
					WorkGroup workGroup = workUser.get(userId);
					if (workGroup != null) {
						WorkGroup tcUser = null;
						if ("Approve".equalsIgnoreCase(name)) {
							tcUser = workGroup.getApproveTcUser();
						} else if ("Review".equalsIgnoreCase(name)) {
							tcUser = workGroup.getReviewTcUser();
						}

						if (tcUser != null) {
							String workId = tcUser.getWorkId();
							System.out.println("workId = " + workId);

							TCComponentUserType typeComponentUser = (TCComponentUserType) session
									.getTypeComponent("User");
							TCComponentUser finduser = typeComponentUser.find(workId.toString());
							if (finduser == null)
								continue;
							TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
							addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
						}
					}
				}
			}
		}

	}

	public void FXN34_MNT(TCTreeNode tCTreeNode, HashMap<String, WorkGroup> workUser, String userId, String projectId)
			throws TCException {

		int tCTreeNo = tCTreeNode.getChildCount();
		for (int b = 0; b < tCTreeNo; b++) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);
			TCComponentTaskTemplate task = (TCComponentTaskTemplate) tCTreeNode1.getComponent();
			String name = task.getName();
			System.out.println("name = " + name);
			//tCTreeNode1.refresh();
			Object[] childNodes = tCTreeNode1.getChildNodes();

			if (!"OS Review".equalsIgnoreCase(name) && !"Lead Review".equalsIgnoreCase(name)
					&& !"ME Receive".equalsIgnoreCase(name)) {
				continue;
			}

			if (childNodes != null && childNodes.length > 0) {
				if ("Lead Review".equalsIgnoreCase(name)) {
					if (workUser != null && !"".equals(userId)) {
						WorkGroup workGroup_Lead = workUser.get(userId);
						if (workGroup_Lead != null) {
							WorkGroup tcUser_Lead = null;
							tcUser_Lead = workGroup_Lead.getReviewTcUser();

							if (tcUser_Lead != null) {
								String workId = tcUser_Lead.getWorkId();
								System.out.println("workId = " + workId);

								TCComponentUserType typeComponentUser = (TCComponentUserType) session
										.getTypeComponent("User");
								TCComponentUser finduser = typeComponentUser.find(workId.toString());
								if (finduser == null)
									continue;
								TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
								addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
							}
						}
					}
				} else if ("OS Review".equalsIgnoreCase(name)) {
					TCComponentUser finduser = null;
					TCComponentGroup currentGroup = session.getCurrentGroup();
					String full_name = currentGroup.getProperty("full_name");
					
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					if("PA.R&D.Monitor.D_Group".equals(full_name)) {
						finduser = typeComponentUser.find("501734");
					} else {
						finduser = typeComponentUser.find("C0103270");
					}
					
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);

				} else if ("ME Receive".equalsIgnoreCase(name)) {

					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("19263");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
				}
			}else {
				System.out.println("childNodes == null && childNodes.length == 0");
			}
		}

	}

	public void FXN08_MNT(TCTreeNode tCTreeNode, HashMap<String, WorkGroup> workUser, String userId, String projectId)
			throws TCException {
		int tCTreeNo = tCTreeNode.getChildCount();
		for (int b = 0; b < tCTreeNo; b++) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);
			TCComponentTaskTemplate task = (TCComponentTaskTemplate) tCTreeNode1.getComponent();
			String name = task.getName();
			System.out.println("name = " + name);
			//tCTreeNode1.refresh();
			Object[] childNodes = tCTreeNode1.getChildNodes();

			// 3-Layout審核 4-Safety審核 1-EE/PSU主管審核 & 2-ME上傳DXF
			if (!"3-Layout Review".equalsIgnoreCase(name) && !"4-Safety Review".equalsIgnoreCase(name)
					&& !"1-EE/PSU主管審核 & 2-ME上傳DXF".equalsIgnoreCase(name)) {
				continue;
			}
			ArrayList<SPASUser> teamRosterUsers = UserNewProcessDialog.teamRosterUsers;
			if (childNodes != null && childNodes.length > 0) {

				if ("1-EE/PSU主管審核 & 2-ME上傳DXF".equalsIgnoreCase(name)) {
					if (workUser != null && !"".equals(userId)) {
						WorkGroup workGroup = workUser.get(userId);
						if (workGroup != null) {
							WorkGroup tcUser = null;
							String meUser = "";

							if (teamRosterUsers == null) {
								System.out.println("teamRosterUsers == null");
								continue;
							}

							for (SPASUser user : teamRosterUsers) {
								String sectionName = user.getSectionName();
								if (sectionName.contains("ME")) {
									meUser = user.getWorkId();
									break;
								}
							}
							if (!"".equals(meUser)) {
								// 2023/8/23 08流程发起报错修改
								WorkGroup meUserWorkGroup = workUser.get(meUser);
								tcUser = meUserWorkGroup.getReviewTcUser();
								if (tcUser != null) {
									String workId = tcUser.getWorkId();
									System.out.println("workId = " + workId);

									TCComponentUserType typeComponentUser = (TCComponentUserType) session
											.getTypeComponent("User");
									TCComponentUser finduser = typeComponentUser.find(workId.toString());
									if (finduser == null)
										continue;

									TCComponentGroupMember[] getGroupMembers1 = finduser.getGroupMembers();
									addMemberNode(childNodes, tCTreeNode1, getGroupMembers1);
								}
							} else {
								System.out.println(" teamRosterUsers 中未找到 ME的用户");
							}

							// tcUser = workGroup.getApproveTcUser();
							tcUser = workGroup.getReviewTcUser();
							if (tcUser != null) {
								String workId = tcUser.getWorkId();
								System.out.println("workId = " + workId);

								TCComponentUserType typeComponentUser = (TCComponentUserType) session
										.getTypeComponent("User");
								TCComponentUser finduser = typeComponentUser.find(workId.toString());
								if (finduser == null)
									continue;
								TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
								addMemberNode(childNodes, tCTreeNode1, getGroupMembers);

							}
						}
					}
				} else if ("3-Layout Review".equalsIgnoreCase(name)) {
					System.out.println("使用默认人25266");
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("25266");
					if (finduser == null)
						continue;
					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);

				} else if ("4-Safety Review".equalsIgnoreCase(name)) {
					System.out.println("使用默认人19262");
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("19262");
					if (finduser == null)
						continue;
					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);

				}
			}
		}
	}

	public void FXN19_MNT_20230831(TCTreeNode tCTreeNode, HashMap<String, WorkGroup> workUser, String userId,
			String projectId) throws TCException {
		int tCTreeNo = tCTreeNode.getChildCount();
		for (int b = 0; b < tCTreeNo; b++) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);
			TCComponentTaskTemplate task = (TCComponentTaskTemplate) tCTreeNode1.getComponent();
			String name = task.getName();
			System.out.println("name = " + name);
			//tCTreeNode1.refresh();
			Object[] childNodes = tCTreeNode1.getChildNodes();
			if (!"CE Review".equalsIgnoreCase(name) && !"Initial confirm".equalsIgnoreCase(name)
					&& !"Safety Review".equalsIgnoreCase(name) && !"Leader approve".equalsIgnoreCase(name)
					&& !"BOM Team Execute DCN".equalsIgnoreCase(name) && !"PSU Review".equalsIgnoreCase(name)) {
				continue;
			}
			int j = UserAssignmentListPanel.this.currentPanel.getActionType();
			ArrayList<SPASUser> teamRosterUsers = UserNewProcessDialog.teamRosterUsers;

			if (childNodes != null && childNodes.length > 0) {
				if ("CE Review".equalsIgnoreCase(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("F3100555");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);

				} else if ("Initial confirm".equals(name) || "PSU Review".equalsIgnoreCase(name)) {
					String tc_user = userId;
					String userId_dcn = UserNewProcessDialog.actualUserIds_dcn;
					if (!"".equals(userId_dcn)) {
						WorkGroup workGroup_dcn = workUser.get(userId_dcn);
						if (workGroup_dcn != null) {
							String codeGroup = workGroup_dcn.getCodeGroup();
							if (codeGroup.contains("EE") || codeGroup.contains("PSU")) {
								tc_user = userId_dcn;
								System.out.println("userId_dcn = " + userId_dcn);
							}
						}
					}

					System.out.println("tc_user = " + tc_user);

					WorkGroup workGroup = workUser.get(tc_user);
					if (workGroup != null) {
						WorkGroup approveTcUser = workGroup.getReviewTcUser();
						String meUser = approveTcUser.getWorkId();
						if (!"".equals(meUser)) {
							TCComponentUserType typeComponentUser = (TCComponentUserType) session
									.getTypeComponent("User");
							TCComponentUser finduser = typeComponentUser.find(meUser);
							if (finduser == null)
								continue;

							TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
							addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
						}
					}
				} else if ("Safety Review".equals(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("19262");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
				} else if ("BOM Team Execute DCN".equals(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("C0103270");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
				}

			} else if ("Leader approve".equals(name)) {
				// DO任务单独处理
				String tc_user = userId;
				String userId_dcn = UserNewProcessDialog.actualUserIds_dcn;
				if (!"".equals(userId_dcn)) {

					WorkGroup workGroup_dcn = workUser.get(userId_dcn);
					if (workGroup_dcn != null) {
						String codeGroup = workGroup_dcn.getCodeGroup();
						if (codeGroup.contains("EE") || codeGroup.contains("PSU")) {
							tc_user = userId_dcn;
							System.out.println("userId_dcn = " + userId_dcn);
						}
					}
				}
				System.out.println("tc_user = " + tc_user);

				WorkGroup workGroup = workUser.get(tc_user);
				if (workGroup != null) {
					WorkGroup tcUser = workGroup.getApproveTcUser();
					if (tcUser != null) {
						String workId = tcUser.getWorkId();
						System.out.println("workId = " + workId);

						TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
						TCComponentUser finduser = typeComponentUser.find(workId.toString());
						if (finduser == null)
							continue;

						TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();

						UserAssignmentListPanel.this.addMemberNode(tCTreeNode1, getGroupMembers[0], 1);
						UserAssignmentListPanel.this.expandNode(tCTreeNode1);
						UserAssignmentListPanel.this.enableSaveEdits();

						TCComponent tCComponent = (TCComponent) tCTreeNode1.getComponent();
						if (tCComponent instanceof TCComponentProfile && getGroupMembers != null) {
							UserAssignmentListPanel.this.currentPanel
									.addToSelectedProfileChildCount(getGroupMembers.length);
							UserAssignmentListPanel.this.currentPanel.validateButtons();
						}
						UserAssignmentListPanel.this.currentPanel.clearOrgTreeSelection();
					}
				}
			}
		}

	}

	public void FXN19_MNT(TCTreeNode tCTreeNode, HashMap<String, WorkGroup> workUser, String userId, String projectId)
			throws TCException {
		int tCTreeNo = tCTreeNode.getChildCount();
		for (int b = 0; b < tCTreeNo; b++) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);
			TCComponentTaskTemplate task = (TCComponentTaskTemplate) tCTreeNode1.getComponent();
			String name = task.getName();
			System.out.println("name = " + name);
			//tCTreeNode1.refresh();
			Object[] childNodes = tCTreeNode1.getChildNodes();

			if (!"CE Review".equalsIgnoreCase(name) && !"Initial confirm".equalsIgnoreCase(name)
					&& !"Safety Review".equalsIgnoreCase(name) && !"Leader approve".equalsIgnoreCase(name)
					&& !"BOM Team Execute DCN".equalsIgnoreCase(name) && !"PSU Review".equalsIgnoreCase(name)) {
				continue;
			}
			int j = UserAssignmentListPanel.this.currentPanel.getActionType();
			ArrayList<SPASUser> teamRosterUsers = UserNewProcessDialog.teamRosterUsers;

			if (childNodes != null && childNodes.length > 0) {
				if ("CE Review".equalsIgnoreCase(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("F3100555");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);

				} else if ("Initial confirm".equals(name)) {
					String userId2 = session.getUser().getUserId();
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find(userId2);
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);

				} else if ("PSU Review".equalsIgnoreCase(name)) {
					String tc_user = userId;
					String userId_dcn = UserNewProcessDialog.actualUserIds_dcn;
					if (!"".equals(userId_dcn)) {
						WorkGroup workGroup_dcn = workUser.get(userId_dcn);
						if (workGroup_dcn != null) {
							String codeGroup = workGroup_dcn.getCodeGroup();
							if (codeGroup.contains("EE") || codeGroup.contains("PSU")) {
								tc_user = userId_dcn;
								System.out.println("userId_dcn = " + userId_dcn);
							}
						}
					}

					System.out.println("tc_user = " + tc_user);

					WorkGroup workGroup = workUser.get(tc_user);
					if (workGroup != null) {
						WorkGroup approveTcUser = workGroup.getReviewTcUser();
						String meUser = approveTcUser.getWorkId();
						if (!"".equals(meUser)) {
							TCComponentUserType typeComponentUser = (TCComponentUserType) session
									.getTypeComponent("User");
							TCComponentUser finduser = typeComponentUser.find(meUser);
							if (finduser == null)
								continue;

							TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
							addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
						}
					}
				} else if ("Safety Review".equals(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("19262");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
				} else if ("BOM Team Execute DCN".equals(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("C0103270");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
				}

			} else if ("Leader approve".equals(name)) {
				// DO任务单独处理
				String tc_user = userId;
				String userId_dcn = UserNewProcessDialog.actualUserIds_dcn;
				if (!"".equals(userId_dcn)) {

					WorkGroup workGroup_dcn = workUser.get(userId_dcn);
					if (workGroup_dcn != null) {
						String codeGroup = workGroup_dcn.getCodeGroup();
						if (codeGroup.contains("EE") || codeGroup.contains("PSU")) {
							tc_user = userId_dcn;
							System.out.println("userId_dcn = " + userId_dcn);
						}
					}
				}
				System.out.println("tc_user = " + tc_user);

				WorkGroup workGroup = workUser.get(tc_user);
				if (workGroup != null) {
					WorkGroup tcUser = workGroup.getApproveTcUser();
					if (tcUser != null) {
						String workId = tcUser.getWorkId();
						System.out.println("workId = " + workId);

						TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
						TCComponentUser finduser = typeComponentUser.find(workId.toString());
						if (finduser == null)
							continue;

						TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();

						UserAssignmentListPanel.this.addMemberNode(tCTreeNode1, getGroupMembers[0], 1);
						UserAssignmentListPanel.this.expandNode(tCTreeNode1);
						UserAssignmentListPanel.this.enableSaveEdits();

						TCComponent tCComponent = (TCComponent) tCTreeNode1.getComponent();
						if (tCComponent instanceof TCComponentProfile && getGroupMembers != null) {
							UserAssignmentListPanel.this.currentPanel
									.addToSelectedProfileChildCount(getGroupMembers.length);
							UserAssignmentListPanel.this.currentPanel.validateButtons();
						}
						UserAssignmentListPanel.this.currentPanel.clearOrgTreeSelection();
					}
				}
			}
		}
	}

	public void FXN31_MNT(TCTreeNode tCTreeNode, HashMap<String, WorkGroup> workUser, String userId, String projectId)
			throws TCException {
	
		int tCTreeNo = tCTreeNode.getChildCount();
		for (int b = 0; b < tCTreeNo; b++) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);
			TCComponentTaskTemplate task = (TCComponentTaskTemplate) tCTreeNode1.getComponent();
			String name = task.getName();
			System.out.println("name = " + name);
			//tCTreeNode1.refresh();
			
			Object[] childNodes = tCTreeNode1.getChildNodes();
			if (!"1-CE Review".equalsIgnoreCase(name) && !"2-EE Review".equalsIgnoreCase(name)
					&& !"2.1-EE/PI Leader Review".equalsIgnoreCase(name)
					&& !"2-PSU Review".equalsIgnoreCase(name) && !"7-BOM Team Review".equalsIgnoreCase(name)
					&& !"4-FW Review".equalsIgnoreCase(name) && !"6-Safety Review".equalsIgnoreCase(name)) {
				continue;
			}
			
			
			if (childNodes != null && childNodes.length > 0) {
				if ("1-CE Review".equalsIgnoreCase(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("F3100555");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);

				} else if ("4-FW Review".equals(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("22456");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);

				} else if ("6-Safety Review".equals(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("19262");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
				} else if ("7-BOM Team Review".equals(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("C0103270");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);

				} else if ("2-EE Review".equals(name) || "2-PSU Review".equalsIgnoreCase(name)) {
					
					String userId2 = session.getUser().getUserId();
					WorkGroup tcUser = workUser.get(userId2);
					if (tcUser != null) {
						String workId = tcUser.getWorkId();
						System.out.println("workId = " + workId);

						TCComponentUserType typeComponentUser = (TCComponentUserType) session
								.getTypeComponent("User");
						TCComponentUser finduser = typeComponentUser.find(workId.toString());
						if (finduser == null)
							continue;

						TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
						addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
					}
				} else if("2.1-EE/PI Leader Review".equalsIgnoreCase(name)) {
					
					WorkGroup tcUser = workUser.get(userId);
					if (tcUser != null) {
						WorkGroup reviewTcUser = tcUser.getReviewTcUser();
						if(reviewTcUser != null) {
							String workId = reviewTcUser.getWorkId();
							System.out.println("workId = " + workId);

							TCComponentUserType typeComponentUser = (TCComponentUserType) session
									.getTypeComponent("User");
							TCComponentUser finduser = typeComponentUser.find(workId.toString());
							if (finduser == null)
								continue;

							TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
							addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
						}
					}
				}
			}else {
				System.out.println("childNodes == null || childNodes.length == 0");
			}
		}

	}

	public void FXN35_MNT(TCTreeNode tCTreeNode, HashMap<String, WorkGroup> workUser, String userId, String projectId)
			throws TCException {
		int tCTreeNo = tCTreeNode.getChildCount();
		for (int b = 0; b < tCTreeNo; b++) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);
			TCComponentTaskTemplate task = (TCComponentTaskTemplate) tCTreeNode1.getComponent();
			String name = task.getName();
			System.out.println("name = " + name);
			//tCTreeNode1.refresh();
			ArrayList<SPASUser> teamRosterUsers = UserNewProcessDialog.teamRosterUsers;
			
			Object[] childNodes = tCTreeNode1.getChildNodes();

			if (!"1-Layout Review".equalsIgnoreCase(name) && !"2-EE Review".equalsIgnoreCase(name)
					&& !"3-Safety Review".equalsIgnoreCase(name) && !"4-ME Review".equalsIgnoreCase(name)) {
				continue;
			}
			if (childNodes != null && childNodes.length > 0) {
				if ("1-Layout Review".equalsIgnoreCase(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("25266");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);

				} else if ("2-EE Review".equalsIgnoreCase(name)) {
					// 改为用户手选

//					String tc_user = session.getUser().getUserId();
//					System.out.println("tc_user = "+tc_user);
//					
//					WorkGroup workGroup = workUser.get(tc_user);
//					if(workGroup!=null) {
//						WorkGroup approveTcUser = workGroup.getReviewTcUser();
//						String meUser = approveTcUser.getWorkId();
//						if(!"".equals(meUser)) {
//							TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
//							TCComponentUser finduser = typeComponentUser.find(meUser);
//							if(finduser == null)
//								continue;
//							
//							TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
//							addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
//						}
//					}

				} else if ("3-Safety Review".equalsIgnoreCase(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("19262");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
				} else if ("4-ME Review".equalsIgnoreCase(name)) {
					String teamRosterWkId = "";
					if (teamRosterUsers != null && teamRosterUsers.size() > 0) {
						for (SPASUser user : teamRosterUsers) {
							String sectionName = user.getSectionName();
							if (sectionName.contains("ME")) {
								teamRosterWkId = user.getWorkId();
								break;
							}
						}
					}
					if ("".equals(teamRosterWkId))
						continue;

					WorkGroup workGroup = workUser.get(teamRosterWkId);
					if (workGroup != null) {
						WorkGroup tcUser = workGroup.getReviewTcUser();
						if (tcUser != null) {
							String workId = tcUser.getWorkId();
							System.out.println("workId = " + workId);

							TCComponentUserType typeComponentUser = (TCComponentUserType) session
									.getTypeComponent("User");
							TCComponentUser finduser = typeComponentUser.find(workId.toString());
							if (finduser == null)
								continue;

							TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
							addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
						}
					}
				}
			}
		}
	}

	public void FXN38_MNT(TCTreeNode tCTreeNode, HashMap<String, WorkGroup> workUser, String userId, String projectId)
			throws TCException {
		int tCTreeNo = tCTreeNode.getChildCount();
		for (int b = 0; b < tCTreeNo; b++) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);
			TCComponentTaskTemplate task = (TCComponentTaskTemplate) tCTreeNode1.getComponent();
			String name = task.getName();
			System.out.println("name = " + name);
			//tCTreeNode1.refresh();
			Object[] childNodes = tCTreeNode1.getChildNodes();
			if (!"CE Check".equalsIgnoreCase(name) && !"BOMTeam Confirm".equalsIgnoreCase(name)) {
				continue;
			}
			if (childNodes != null && childNodes.length > 0) {
				if ("CE Check".equalsIgnoreCase(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("F3100555");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);

				} else if ("BOMTeam Confirm".equalsIgnoreCase(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("C0103270");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
				}
			}
		}
	}

	public void FXN39_Design(TCTreeNode tCTreeNode, HashMap<String, WorkGroup> workUser, String userId,
			String projectId) throws TCException {
		int tCTreeNo = tCTreeNode.getChildCount();
		for (int b = 0; b < tCTreeNo; b++) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);
			TCComponentTaskTemplate task = (TCComponentTaskTemplate) tCTreeNode1.getComponent();
			String name = task.getName();
			System.out.println("name = " + name);
			//tCTreeNode1.refresh();
			Object[] childNodes = tCTreeNode1.getChildNodes();
			if (!"Review".equalsIgnoreCase(name) && !"修改组件".equalsIgnoreCase(name)) {
				continue;
			}

			ArrayList<SPASUser> teamRosterUsers = UserNewProcessDialog.teamRosterUsers;
			if (childNodes != null && childNodes.length > 0) {
				if ("Review".equalsIgnoreCase(name)) {
					WorkGroup workGroup = workUser.get(userId);
					if (workGroup != null) {
						WorkGroup tcUser = workGroup.getReviewTcUser();
						if (tcUser != null) {
							String workId = tcUser.getWorkId();
							System.out.println("workId = " + workId);

							TCComponentUserType typeComponentUser = (TCComponentUserType) session
									.getTypeComponent("User");
							TCComponentUser finduser = typeComponentUser.find(workId.toString());
							if (finduser == null)
								continue;

							TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
							addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
						}
					}
				} else if ("修改组件".equalsIgnoreCase(name)) {
					TCComponentUser finduser = session.getUser();
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
				}
			}
		}
	}

	public void FXN41_MNT(TCTreeNode tCTreeNode, HashMap<String, WorkGroup> workUser, String userId, String projectId)
			throws TCException {
		int tCTreeNo = tCTreeNode.getChildCount();
		for (int b = 0; b < tCTreeNo; b++) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);
			TCComponentTaskTemplate task = (TCComponentTaskTemplate) tCTreeNode1.getComponent();
			String name = task.getName();
			System.out.println("name = " + name);

			//tCTreeNode1.refresh();
			Object[] childNodes = tCTreeNode1.getChildNodes();
			if (!"1-BOM Team申請PCBA&FW料號".equalsIgnoreCase(name) && !"4-BOMTeam拋轉SAP".equalsIgnoreCase(name)) {
				continue;
			}

			ArrayList<SPASUser> teamRosterUsers = UserNewProcessDialog.teamRosterUsers;
			if (childNodes != null && childNodes.length > 0) {

				if ("1-BOM Team申請PCBA&FW料號".equalsIgnoreCase(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("C0103270");
					if (finduser == null)
						continue;
					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
				} else if ("4-BOMTeam拋轉SAP".equalsIgnoreCase(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("C0103270");
					if (finduser == null)
						continue;

					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					UserAssignmentListPanel.this.addMemberNode(tCTreeNode1, getGroupMembers[0], 1);
					UserAssignmentListPanel.this.expandNode(tCTreeNode1);
					UserAssignmentListPanel.this.enableSaveEdits();

					TCComponent tCComponent = (TCComponent) tCTreeNode1.getComponent();
					if (tCComponent instanceof TCComponentProfile && getGroupMembers != null) {
						UserAssignmentListPanel.this.currentPanel
								.addToSelectedProfileChildCount(getGroupMembers.length);
						UserAssignmentListPanel.this.currentPanel.validateButtons();
					}
					UserAssignmentListPanel.this.currentPanel.clearOrgTreeSelection();

				}
			}
		}
	}

	public void FXN42_MNT(TCTreeNode tCTreeNode, HashMap<String, WorkGroup> workUser, String userId, String projectId)
			throws TCException {
		int tCTreeNo = tCTreeNode.getChildCount();
		for (int b = 0; b < tCTreeNo; b++) {
			TCTreeNode tCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(b);
			TCComponentTaskTemplate task = (TCComponentTaskTemplate) tCTreeNode1.getComponent();
			String name = task.getName();
			System.out.println("name = " + name);

			//tCTreeNode1.refresh();
			Object[] childNodes = tCTreeNode1.getChildNodes();

			if (!"EE Manager Review".equalsIgnoreCase(name) && !"CE Manager Review".equalsIgnoreCase(name)
					&& !"EE Spec check".equalsIgnoreCase(name) && !"ME Spec check".equalsIgnoreCase(name)
					&& !"CE FXN Apply".equalsIgnoreCase(name) && !"EE Design(Rework)".equalsIgnoreCase(name)) {
				continue;
			}

			ArrayList<SPASUser> teamRosterUsers = UserNewProcessDialog.teamRosterUsers;
			if (childNodes != null && childNodes.length > 0) {
				if ("EE Manager Review".equalsIgnoreCase(name) || "EE Spec check".equalsIgnoreCase(name)) {
					// String getUserId = session.getUser().getUserId();
					WorkGroup workGroup = workUser.get(userId);
					if (workGroup != null) {
						WorkGroup tcUser = workGroup.getApproveTcUser();
						if (tcUser != null) {
							String workId = tcUser.getWorkId();
							System.out.println("workId = " + workId);

							TCComponentUserType typeComponentUser = (TCComponentUserType) session
									.getTypeComponent("User");
							TCComponentUser finduser = typeComponentUser.find(workId.toString());
							if (finduser == null)
								continue;

							TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
							addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
						}
					}
				} else if ("CE Manager Review".equalsIgnoreCase(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("10486");
					if (finduser == null)
						continue;
					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
				} else if ("ME Spec check".equalsIgnoreCase(name)) {

					if (teamRosterUsers != null && teamRosterUsers.size() > 0) {
						for (SPASUser user : teamRosterUsers) {
							String sectionName = user.getSectionName();
							if (sectionName.contains("ME")) {
								String userid = user.getWorkId();
								
								WorkGroup workGroup = workUser.get(userid);
								if (workGroup != null) {
									WorkGroup tcUser = workGroup.getApproveTcUser();
									if (tcUser != null) {
										String workId = tcUser.getWorkId();
										System.out.println("workId = " + workId);
										
										TCComponentUserType typeComponentUser = (TCComponentUserType) session
												.getTypeComponent("User");
										TCComponentUser finduser = typeComponentUser.find(workId.toString());
										if (finduser == null)
											continue;

										TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
										addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
										break;
										
									}
								}
							}
						}
					}

				} else if ("CE FXN Apply".equalsIgnoreCase(name)) {
					TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
					TCComponentUser finduser = typeComponentUser.find("F3100555");
					if (finduser == null)
						continue;
					TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
					addMemberNode(childNodes, tCTreeNode1, getGroupMembers);
				}
			}

			if ("EE Design(Rework)".equalsIgnoreCase(name)) {
				// DO任务单独处理

				// String getUserId = session.getUser().getUserId();
				WorkGroup workGroup = workUser.get(userId);
				if (workGroup != null) {
					WorkGroup tcUser = workGroup.getReviewTcUser();
					if (tcUser != null) {
						String workId = tcUser.getWorkId();
						System.out.println("workId = " + workId);

						TCComponentUserType typeComponentUser = (TCComponentUserType) session.getTypeComponent("User");
						TCComponentUser finduser = typeComponentUser.find(workId.toString());
						if (finduser == null)
							continue;

						TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();

						UserAssignmentListPanel.this.addMemberNode(tCTreeNode1, getGroupMembers[0], 1);
						UserAssignmentListPanel.this.expandNode(tCTreeNode1);
						UserAssignmentListPanel.this.enableSaveEdits();

						TCComponent tCComponent = (TCComponent) tCTreeNode1.getComponent();
						if (tCComponent instanceof TCComponentProfile && getGroupMembers != null) {
							UserAssignmentListPanel.this.currentPanel
									.addToSelectedProfileChildCount(getGroupMembers.length);
							UserAssignmentListPanel.this.currentPanel.validateButtons();
						}
						UserAssignmentListPanel.this.currentPanel.clearOrgTreeSelection();
					}
				}
			}
		}
	}

	/**
	 * 允许指派当前用户的流程模板和节点名
	 * 
	 * @param tCTreeNode
	 * @throws TCException
	 */
	public void autoAssignWorkflow(TCTreeNode tCTreeNode, String processName) throws TCException {
		tCTreeNode.refresh();
		String string = tCTreeNode.toString();
//		String processName = tCTreeNode.toString();
		String currentUserId = session.getUser().getUserId();
		System.out.println("==>> currentUserId: " + currentUserId);
		Optional<String> findFirst = Stream.of(UserExtNewProcessDialog.autoAssignWorkflowTemplates)
				.filter(str -> str.contains(processName)).findFirst();
		String value = null;
		if (findFirst.isPresent()) {
			value = findFirst.get();
		}

		if (CommonTools.isEmpty(value)) {
			return;
		}

		List<String> autoAssignNodeNameList = new ArrayList<String>(
				Arrays.asList(value.trim().split("=")[1].split(",")));
		for (String currentNode : autoAssignNodeNameList) {
			int iCount = tCTreeNode.getChildCount();
			for (int i = 0; i < iCount; i++) {
				TCTreeNode iTCTreeNode1 = (TCTreeNode) tCTreeNode.getChildAt(i);
				iTCTreeNode1.refresh();
				TCComponentTaskTemplate task = (TCComponentTaskTemplate) iTCTreeNode1.getComponent();
				String name = task.getName();
				System.out.println("name = " + name);

				if (!currentNode.startsWith(name)) {
					continue;
				}

				if (currentNode.indexOf("##") != -1) {
					currentUserId = currentNode.substring(currentNode.indexOf("##") + 2);
				}
				iTCTreeNode1.refresh();
				Object[] childNodes = iTCTreeNode1.getChildNodes();
				int jCount = iTCTreeNode1.getChildCount();
				System.out.println("==>> jCount: " + jCount);
				for (int j = 0; j < jCount; j++) {
					TCTreeNode jTCTreeNode1 = (TCTreeNode) iTCTreeNode1.getChildAt(j);
					Object object1 = jTCTreeNode1.getUserObject();

					if (object1 instanceof AssignmentListHelper) {
						AssignmentListHelper assignmentListHelper1 = (AssignmentListHelper) object1;
						if (null == assignmentListHelper1) {
							continue;
						}

						TCComponent component1 = assignmentListHelper1.getNodeComponent();
						if (component1 != null && component1 instanceof TCComponentGroupMember) {
							System.out.println("==>> 当前已经指派");
							continue;
						}

						if (component1 != null && component1 instanceof TCComponentResourcePool) {
							System.out.println("==>> 当前类型不对");
							continue;
						}

						String title = assignmentListHelper1.getNodeTitle();
						if ("profiles".equalsIgnoreCase(title)) {
							System.out.println("==>> 当前为概要表");
							continue;
						}

						if ("users".equalsIgnoreCase(title)) {
							TCComponentUserType typeComponentUser = (TCComponentUserType) session
									.getTypeComponent("User");
							TCComponentUser finduser = typeComponentUser.find(currentUserId);
							if (finduser == null) {
								continue;
							}

							System.out.println("==>> 开始添加默认人");
							TCComponentGroupMember[] getGroupMembers = finduser.getGroupMembers();
							TCComponentGroupMember defaultGroupMember = null;
							if (currentUserId.equals(session.getUser().getUserId())) {
								defaultGroupMember = getDefaultGroupMember(getGroupMembers);
							} else {
								defaultGroupMember = getGroupMembers[0];
							}

							if (CommonTools.isEmpty(defaultGroupMember)) {
								System.out.println("当前用户" + currentUserId + ", 不存在任何组中");
								continue;
							}
							addMemberNode(jTCTreeNode1, iTCTreeNode1, defaultGroupMember, getGroupMembers.length);

							System.out.println("==>> 开始添加默认人");
						}
					}
				}
			}
		}
	}

	/**
	 * 获取默认的账号所在的组
	 * 
	 * @param getGroupMembers
	 * @return
	 * @throws TCException
	 */
	private TCComponentGroupMember getDefaultGroupMember(TCComponentGroupMember[] getGroupMembers) throws TCException {
		String groupName = UserExtNewProcessDialog.group;
		Optional<TCComponentGroupMember> findFirst = Stream.of(getGroupMembers).filter(member -> {
			try {
				return member.getGroup().getFullName().equalsIgnoreCase(groupName);
			} catch (TCException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}

		}).findFirst();

		if (findFirst.isPresent()) {
			return findFirst.get();
		}

		return null;
	}

	private void addMemberNode(TCTreeNode childNode, TCTreeNode tCTreeNode1, TCComponentGroupMember getGroupMember,
			int length) {
		boolean bool = !isMemberExist_1((TCTreeNode) childNode, getGroupMember);
		if (bool) {
			UserAssignmentListPanel.this.addMemberNode((TreeNode) childNode, getGroupMember, 1);
			UserAssignmentListPanel.this.expandNode((AIFTreeNode) childNode);
			UserAssignmentListPanel.this.enableSaveEdits();

			TCComponent tCComponent = (TCComponent) tCTreeNode1.getComponent();
			if (tCComponent instanceof TCComponentProfile && getGroupMember != null) {
				UserAssignmentListPanel.this.currentPanel.addToSelectedProfileChildCount(length);
				UserAssignmentListPanel.this.currentPanel.validateButtons();
			}
			UserAssignmentListPanel.this.currentPanel.clearOrgTreeSelection();

		}
	}

	public void addMemberNode(Object[] childNodes, TCTreeNode tCTreeNode1, TCComponentGroupMember[] getGroupMembers) {
		boolean bool = !isMemberExist_1((TCTreeNode) childNodes[0], getGroupMembers[0]);
		if (bool) {
			UserAssignmentListPanel.this.addMemberNode((TreeNode) childNodes[0], getGroupMembers[0], 1);
			UserAssignmentListPanel.this.expandNode((AIFTreeNode) childNodes[0]);
			UserAssignmentListPanel.this.enableSaveEdits();

			TCComponent tCComponent = (TCComponent) tCTreeNode1.getComponent();
			if (tCComponent instanceof TCComponentProfile && getGroupMembers != null) {
				UserAssignmentListPanel.this.currentPanel.addToSelectedProfileChildCount(getGroupMembers.length);
				UserAssignmentListPanel.this.currentPanel.validateButtons();
			}
			UserAssignmentListPanel.this.currentPanel.clearOrgTreeSelection();
		}
	}
}
