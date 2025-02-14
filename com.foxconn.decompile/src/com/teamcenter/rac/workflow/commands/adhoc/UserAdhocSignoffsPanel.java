package com.teamcenter.rac.workflow.commands.adhoc;

import com.foxconn.decompile.util.CommonTools;
import com.foxconn.decompile.util.FileStreamUtil;
import com.foxconn.decompile.util.SPASUser;
import com.foxconn.decompile.util.TCUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.InterfaceAIFOperationListener;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.AIFComponentEvent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponentEventListener;
import com.teamcenter.rac.kernel.TCAttachmentScope;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentAliasList;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentPerson;
import com.teamcenter.rac.kernel.TCComponentProfile;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentResourcePool;
import com.teamcenter.rac.kernel.TCComponentSignoff;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTaskState;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.FilterDocument;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.ImageUtilities;
import com.teamcenter.rac.util.InterfaceSignalOnClose;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Painter;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Separator;
import com.teamcenter.rac.util.SplitPane;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.iTextArea;
import com.teamcenter.rac.util.log.Debug;
import com.teamcenter.rac.workflow.commands.digitalsign.PerformTaskUtil;
import com.teamcenter.rac.workflow.commands.newprocess.ProcessFormTableRow;
import com.teamcenter.rac.workflow.common.CommentsPanel;
import com.teamcenter.services.rac.core.DataManagementService;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
//import org.apache.log4j.Logger;

public class UserAdhocSignoffsPanel extends JPanel implements InterfaceAIFComponentEventListener, InterfaceSignalOnClose {
	protected TCSession session = null;

	protected TCComponentTask task = null;

	protected Frame parent = null;

	protected SignoffTree signoffTree;

	protected SplitPane splitPane;

	private boolean is_secure_task = false;

	private String UnsetResult = "Unset";

	protected JLabel userPassword;

	protected JPasswordField passwordTextField;

	protected JCheckBox adhocDoneCheckBox;

	private JTextField quorumField;

	private JTextField quorumPercentField;

	private JRadioButton quorumRadioButton;

	private JRadioButton quorumPercentRadioButton;

	private JLabel percentLabel;

	private JButton addButton;

	private JButton modifyButton;

	private JButton removeButton;

	private JButton applyButton;

	private JButton okButton;

	private JButton cancelButton;

	private UserSignoffEditPanel currentPanel;

	private TitledBorder memberPanelTitleBorder;

	private JPanel memberPanel;

	protected JPanel quorumPanel;

	protected JPanel quorumPercentPanel;

	private JPanel requiredPanel;

	private JPanel passwordPanel;

	private JCheckBox requiredCheckBox;

	protected String quorumAllString;

	protected CommentsPanel commentsPanel;

	private iTextArea processDesc;

	private String oldDesc;

	protected JCheckBox waitForUndecidedReviewers;

//	  private static final Logger logger = Logger.getLogger(AdhocSignoffsPanel.class);

	protected Registry r;

	public static final String REFRESH_DISPLAY_REG_KEY = "refreshDisplayMessage";

	public static final String PROCESS_DESC_LABEL_REG_KEY = "processDescLabel";

	public static final String ADHOC_FINISHED_TOOL_TIP_REG_KEY = "adhocFinished";

	Set assignedUserSignoffs = new HashSet();

	private boolean userSignOffsPopulated = false;

	private PerformTaskUtil performTask = null;

	private int currentQuorum = 0;

	InterfaceAIFOperationListener m_lsnr = null;
	
	// recompile 20220323143000 : START
	protected List<TCComponent> m_tcCompLst = null;
	// 20220323143000 : END

	public UserAdhocSignoffsPanel(TCSession paramTCSession, Frame paramFrame) {
		this.session = paramTCSession;
		this.parent = paramFrame;
		initializePanel();
		this.session.addAIFComponentEventListener(this);
	}

	public String getPassword() {
		return new String(this.passwordTextField.getPassword());
	}

	public UserAdhocSignoffsPanel(TCSession paramTCSession) {
		this(paramTCSession, null);
	}

	public UserAdhocSignoffsPanel(Frame paramFrame, TCComponentTask paramTCComponentTask) {
		this.task = paramTCComponentTask;
		this.session = paramTCComponentTask.getSession();
		this.parent = paramFrame;
		initializePanel();
		loadTask(this.task);
		this.session.addAIFComponentEventListener(this);
	}

	public UserAdhocSignoffsPanel(TCComponentTask paramTCComponentTask) {
		this(null, paramTCComponentTask);
	}

	public UserAdhocSignoffsPanel(AIFDesktop paramAIFDesktop, JPanel paramJPanel,
			TCComponentTask paramTCComponentTask) {
		this(paramAIFDesktop, paramTCComponentTask);
	}

	protected void initializePanel() {
		// recompile 20220323143000 : START
		m_tcCompLst = getItem();		
		// 20220323143000 : END
		
		this.r = Registry.getRegistry(this);
		this.quorumAllString = this.r.getString("quorumAllString");
		setLayout(new VerticalLayout(7, 4, 4, 4, 4));
		this.splitPane = new SplitPane(0);
		this.signoffTree = new SignoffTree(this.parent, this.task) {
			protected void afterTreeChangeAction() {
				UserAdhocSignoffsPanel.this.validateButtons();
			}
		};
		JScrollPane jScrollPane1 = new JScrollPane(this.signoffTree);
		this.signoffTree.setVisibleRowCount(8);		
		jScrollPane1.setPreferredSize(new Dimension(80, 80));
		this.signoffTree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent param1TreeSelectionEvent) {
				UserAdhocSignoffsPanel.this.session.queueOperation(
						new AbstractAIFOperation(UserAdhocSignoffsPanel.this.r.getString("settingMemberData.MSG")) {
							public void executeOperation() {
								UserAdhocSignoffsPanel.this.validatePanels();
							}
						});
			}
		});
		JPanel jPanel1 = new JPanel(new VerticalLayout());
		boolean bool = this.signoffTree.isPreferenceSettingTrue("EPM_adhoc_signoffs", "ON");
		ImageIcon imageIcon1 = this.r.getUnionImageIcon("checked.ICON", "finish.ICON", getBackground());
		ImageIcon imageIcon2 = this.r.getUnionImageIcon("unChecked.ICON", "finish.ICON", getBackground());
		this.adhocDoneCheckBox = new JCheckBox(this.r.getString("adhocDone"), imageIcon2);
		this.adhocDoneCheckBox.setSelectedIcon(imageIcon1);
		this.adhocDoneCheckBox.setDisabledSelectedIcon(ImageUtilities.createGrayscaleImageIcon(imageIcon1));
		this.adhocDoneCheckBox.setDisabledIcon(ImageUtilities.createGrayscaleImageIcon(imageIcon2));
		this.adhocDoneCheckBox.setToolTipText(this.r.getString("adhocFinished"));
		this.adhocDoneCheckBox.setSelected(false);
		this.adhocDoneCheckBox.setVisible(bool);
		this.adhocDoneCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				UserAdhocSignoffsPanel.this.setAdhocDoneAttribute();
				UserAdhocSignoffsPanel.this.validateButtons();
			}
		});
		JLabel jLabel1 = new JLabel(this.r.getString("quorum"));
		jLabel1.setToolTipText(this.r.getString("quorum.TIP"));
		JLabel jLabel2 = new JLabel(this.r.getString("quorumPercent"));
		jLabel2.setToolTipText(this.r.getString("quorumPercent.TIP"));
		this.quorumRadioButton = new JRadioButton(this.r.getString("quorumNumber"));
		this.quorumRadioButton.setToolTipText(this.r.getString("quorum.TIP"));
		this.quorumRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				UserAdhocSignoffsPanel.this.quorumField.setEnabled(true);
				UserAdhocSignoffsPanel.this.quorumPercentField.setEnabled(false);
				UserAdhocSignoffsPanel.this.quorumPercentField.setText("");
			}
		});
		this.quorumPercentRadioButton = new JRadioButton(this.r.getString("quorumPercent"));
		this.quorumPercentRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				UserAdhocSignoffsPanel.this.quorumField.setEnabled(false);
				UserAdhocSignoffsPanel.this.quorumPercentField.setEnabled(true);
				UserAdhocSignoffsPanel.this.quorumField.setText("");
			}
		});
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(this.quorumRadioButton);
		buttonGroup.add(this.quorumPercentRadioButton);
		FilterDocument filterDocument1 = new FilterDocument("0123456789");
		filterDocument1.setUnacceptedBeginChars("0");
		filterDocument1.setNegativeAccepted(false);
		this.quorumField = new JTextField(3);
		this.quorumField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
			}
		});
		this.quorumPercentField = new JTextField(3);
		this.quorumPercentField.setEnabled(false);
		this.quorumField.setDocument(filterDocument1);
		this.quorumField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent param1FocusEvent) {
			}

			public void focusLost(FocusEvent param1FocusEvent) {
				Component component = param1FocusEvent.getOppositeComponent();
				boolean bool = true;
				if (component != null && component instanceof JButton)
					bool = false;
				if (bool)
					UserAdhocSignoffsPanel.this.performSetQuorumAction();
			}
		});
		FilterDocument filterDocument2 = new FilterDocument("0123456789");
		filterDocument2.setNegativeAccepted(false);
		filterDocument2.setNumberLimits(1.0D, 100.0D);
		this.quorumPercentField.setDocument(filterDocument2);
		this.quorumPercentField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent param1FocusEvent) {
			}

			public void focusLost(FocusEvent param1FocusEvent) {
				Component component = param1FocusEvent.getOppositeComponent();
				boolean bool = true;
				if (component != null && component instanceof JButton)
					bool = false;
				if (bool)
					UserAdhocSignoffsPanel.this.performSetQuorumAction();
			}
		});
		this.quorumField.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent param1KeyEvent) {
			}

			public void keyPressed(KeyEvent param1KeyEvent) {
				int i = param1KeyEvent.getKeyCode();
				if (i == 40 || i == 10 || i == 27)
					UserAdhocSignoffsPanel.this.signoffTree.requestFocus();
			}

			public void keyReleased(KeyEvent param1KeyEvent) {
			}
		});
		TCPreferenceService tCPreferenceService = this.session.getPreferenceService();
		String str1 = tCPreferenceService.getStringValue("WRKFLW_allow_quorum_override");
		if (str1 != null && !Boolean.valueOf(str1).booleanValue()) {
			this.quorumRadioButton.setEnabled(false);
			this.quorumPercentRadioButton.setEnabled(false);
			this.quorumField.setEnabled(false);
			this.quorumPercentField.setEnabled(false);
		}
		TCProperty tCProperty = null;
		try {
			tCProperty = this.task.getTCProperty("wait_for_all_reviewers");
		} catch (TCException tCException) {
			tCException.printStackTrace();
//			Logger.getLogger(UserAdhocSignoffsPanel.class).error(tCException.getLocalizedMessage(), tCException);
		}
		String str2 = tCProperty.getPropertyDescriptor().getDisplayName();
		this.waitForUndecidedReviewers = new JCheckBox(str2);
		setWaitForUndecidedReviewersBox();
		this.quorumPanel = new JPanel(new HorizontalLayout(0, 2, 10, 2, 2));
		this.quorumPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), this.r.getString("quorum")));
		this.percentLabel = new JLabel(this.r.getString("percentLabel"));
		this.commentsPanel = new CommentsPanel();
		this.commentsPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), this.r.getString("comments")));
		try {
			this.commentsPanel.getTextArea().setText(this.task.getStringProperty("comments"));
			this.commentsPanel.getTextArea().addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent param1FocusEvent) {
				}

				public void focusLost(FocusEvent param1FocusEvent) {
					UserAdhocSignoffsPanel.this.setCommentsAttribute();
				}
			});
		} catch (TCException tCException) {
			tCException.printStackTrace();
//			logger.error("Exception", tCException);
		}
		JLabel jLabel3 = new JLabel(this.r.getString("processDescLabel"));
		try {
			this.oldDesc = this.task.getProcess().getInstructions();
			this.processDesc = new iTextArea(this.oldDesc, 2, 50);
			this.processDesc.setLengthLimit(240);
			this.processDesc.setLineWrap(true);
			this.processDesc.setWrapStyleWord(true);
			if (this.task.getProcess().okToModify()) {
				this.processDesc.setEditable(true);
			} else {
				this.processDesc.setEditable(false);
			}
			this.processDesc.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent param1KeyEvent) {
					UserAdhocSignoffsPanel.this.applyButton.setEnabled(true);
				}
			});
		} catch (TCException tCException) {
		}
		this.quorumPanel.add("left", this.quorumRadioButton);
		this.quorumPanel.add("left", this.quorumField);
		this.quorumPanel.add("left", new JLabel("       "));
		this.quorumPanel.add("left", this.quorumPercentRadioButton);
		this.quorumPanel.add("left", this.quorumPercentField);
		this.quorumPanel.add("left", this.percentLabel);
		JScrollPane jScrollPane2 = new JScrollPane(this.processDesc);
		jScrollPane2.setPreferredSize(new Dimension(50, 50));
		try {
			this.is_secure_task = this.task.isSecureTask();
			if (this.is_secure_task) {
				this.userPassword = new JLabel(this.r.getString("passwordLabel"));
				this.passwordTextField = new JPasswordField(20) {
					public void paint(Graphics param1Graphics) {
						super.paint(param1Graphics);
						Painter.paintIsRequired(this, param1Graphics);
					}
				};
				this.passwordPanel = new JPanel(new HorizontalLayout(4, 1, 5, 1, 3));
				this.passwordPanel.add("bottom.bind.left", this.userPassword);
				this.passwordPanel.add("bottom.bind.right", this.passwordTextField);
				jPanel1.add("bottom.bind.left", this.passwordPanel);
			}
		} catch (TCException tCException) {
		}
		jPanel1.add("bottom.bind.left", this.adhocDoneCheckBox);
		jPanel1.add("bottom.bind.left", this.waitForUndecidedReviewers);
		jPanel1.add("bottom.bind.right", this.commentsPanel);
		jPanel1.add("bottom.bind.right", this.quorumPanel);
		jPanel1.add("bottom.bind.right", jScrollPane2);
		jPanel1.add("bottom.bind.right", jLabel3);
		jPanel1.add("unbound", jScrollPane1);
		jPanel1.setPreferredSize(new Dimension(200, 300));
		this.memberPanel = new JPanel(new VerticalLayout(2, 2, 10, 2, 2));
		this.memberPanelTitleBorder = new TitledBorder(BorderFactory.createEtchedBorder(), "");
		this.memberPanel.setBorder(this.memberPanelTitleBorder);
		this.requiredPanel = new JPanel(new HorizontalLayout(0, 10, 0, 0, 0));
		this.requiredPanel.setVisible(true);
		this.requiredCheckBox = new JCheckBox(this.r.getString("required"), false);
		this.requiredCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent param1ItemEvent) {
				JCheckBox jCheckBox = (JCheckBox) param1ItemEvent.getItem();
				if (jCheckBox.isSelected()) {
					UserAdhocSignoffsPanel.this.requiredCheckBox.setSelected(true);
				} else {
					UserAdhocSignoffsPanel.this.requiredCheckBox.setSelected(false);
				}
			}
		});
		this.requiredPanel.add("left.nobind", this.requiredCheckBox);				
		// recompile 20220323143000 : START		
		try {
			this.currentPanel = new UserSignoffEditPanel(this.session, signoffTree, task.getProperty("parent_name"), getProjectId());
		} catch (TCException e) {
			e.printStackTrace();
		}
		// 20220323143000 : END		
		this.currentPanel.setVisible(false);
		JPanel jPanel2 = new JPanel(new ButtonLayout());
		this.addButton = new JButton(this.r.getString("addButton"));
		this.addButton.setVisible(false);
		this.addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				if (UserAdhocSignoffsPanel.this.currentPanel != null) {
					TCComponent[] arrayOfTCComponent = UserAdhocSignoffsPanel.this.currentPanel.getNewMembers();
					
					// recompile 20220323143000 : START
					addPubMailInfo(arrayOfTCComponent);	
					// 20220323143000 : END
															
					for (int b = 0; arrayOfTCComponent != null && b < arrayOfTCComponent.length; b++) {
						try {
							String[] arrayOfString1 = { "user_name", "the_user" };
							String[] arrayOfString2 = { "the_group", "the_role" };
							ArrayList arrayList = new ArrayList();
							arrayList.add(arrayOfTCComponent[b]);
							TCComponentType.getPropertiesSet(arrayList, arrayOfString1);
							TCComponentType.getTCPropertiesSet(arrayList, arrayOfString2);
						} catch (Exception exception) {
							exception.printStackTrace();
//							logger.error("Exception", exception);
						}
						if (UserAdhocSignoffsPanel.this.requiredCheckBox.isSelected()) {
							UserAdhocSignoffsPanel.this.signoffTree.performAddSignoff(UserAdhocSignoffsPanel.this.signoffTree.getSelectedNode(), arrayOfTCComponent[b], true,null);
							///UserAdhocSignoffsPanel.this.signoffTree.performAddSignoff(
							//		UserAdhocSignoffsPanel.this.signoffTree.getSelectedNode(), arrayOfTCComponent[b], true);
							UserAdhocSignoffsPanel.this.requiredCheckBox.setSelected(false);
						} else {
							UserAdhocSignoffsPanel.this.signoffTree.performAddSignoff(UserAdhocSignoffsPanel.this.signoffTree.getSelectedNode(), arrayOfTCComponent[b], false,null);
							
							//UserAdhocSignoffsPanel.this.signoffTree.performAddSignoff(
								//	UserAdhocSignoffsPanel.this.signoffTree.getSelectedNode(), arrayOfTCComponent[b], false);
						}
						if (arrayOfTCComponent[b] instanceof TCComponentGroupMember)
							UserAdhocSignoffsPanel.this.assignedUserSignoffs.add(arrayOfTCComponent[b]);
					}
					SignoffTreeNode signoffTreeNode = UserAdhocSignoffsPanel.this.signoffTree.getSelectedNode();
					TCComponent tCComponent = signoffTreeNode.getTCComponent();
					if (tCComponent instanceof com.teamcenter.rac.kernel.TCComponentProfile && arrayOfTCComponent != null) {
						UserAdhocSignoffsPanel.this.currentPanel.addToSelectedProfileChildCount(arrayOfTCComponent.length);
						UserAdhocSignoffsPanel.this.currentPanel.validateButtons();
					}
					SignoffEditPanel.setAssignedUserSignOffs(UserAdhocSignoffsPanel.this.assignedUserSignoffs);
					UserAdhocSignoffsPanel.this.currentPanel.clearOrgTreeSelection();
				}
			}
		});
		this.modifyButton = new JButton(this.r.getString("modifyButton"));
		this.modifyButton.setVisible(false);
		this.modifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				if ((UserAdhocSignoffsPanel.this.signoffTree.getSelectedNode().getUserObject() instanceof TaskSignoff || UserAdhocSignoffsPanel.this.signoffTree.getSelectedNode().getUserObject() instanceof com.teamcenter.rac.kernel.TCComponentAliasList)
						&& UserAdhocSignoffsPanel.this.currentPanel != null) {
					TCComponent[] arrayOfTCComponent = UserAdhocSignoffsPanel.this.currentPanel.getNewMembers();
					for (int b = 0; arrayOfTCComponent != null && b < arrayOfTCComponent.length; b++) {
						if (UserAdhocSignoffsPanel.this.requiredCheckBox.isSelected()) {
							UserAdhocSignoffsPanel.this.signoffTree.performModifySignoff(
									UserAdhocSignoffsPanel.this.signoffTree.getSelectedNode(), arrayOfTCComponent[b], true);
							UserAdhocSignoffsPanel.this.requiredCheckBox.setSelected(false);
						} else {
							UserAdhocSignoffsPanel.this.signoffTree.performModifySignoff(
									UserAdhocSignoffsPanel.this.signoffTree.getSelectedNode(), arrayOfTCComponent[b], false);
						}
						if (arrayOfTCComponent[b] instanceof TCComponentGroupMember)
							UserAdhocSignoffsPanel.this.assignedUserSignoffs.add(arrayOfTCComponent[b]);
					}
				}
				try {
					TCComponentSignoff tCComponentSignoff = UserAdhocSignoffsPanel.this.signoffTree.getSelectedNode().getSignoff();
					if (tCComponentSignoff != null) {
						TCComponentGroupMember tCComponentGroupMember = tCComponentSignoff.getGroupMember();
						if (tCComponentGroupMember != null)
							UserAdhocSignoffsPanel.this.assignedUserSignoffs.remove(tCComponentGroupMember);
					}
				} catch (TCException tCException) {
					tCException.printStackTrace();
//					logger.error(tCException.getClass().getName(), tCException);
				}
				SignoffEditPanel.setAssignedUserSignOffs(UserAdhocSignoffsPanel.this.assignedUserSignoffs);
			}
		});
		this.removeButton = new JButton(this.r.getString("removeButton"));
		this.removeButton.setVisible(false);
		this.removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				if ((signoffTree.getSelectedNode().getUserObject() instanceof TaskSignoff 
						|| signoffTree.getSelectedNode().getUserObject() instanceof TCComponentAliasList
						|| signoffTree.getSelectedNode().getUserObject() instanceof TCComponentResourcePool)
						&& currentPanel != null) {
					
					// recompile 20220323143000 : START
					removePubMailInfo();
					// 20220323143000 : END	
					
					signoffTree.performRemoveSignoff(signoffTree.getSelectedNode());
					if (signoffTree.getSelectedNode().getParent() instanceof TCComponentProfile) {
						currentPanel.removeFromSelectedProfileChildCount(1);
						currentPanel.validateButtons();
					}
				}
				try {
					TCComponentSignoff tCComponentSignoff = UserAdhocSignoffsPanel.this.signoffTree.getSelectedNode().getSignoff();
					if (tCComponentSignoff != null) {
						TCComponentGroupMember tCComponentGroupMember = tCComponentSignoff.getGroupMember();
						if (tCComponentGroupMember != null)
							assignedUserSignoffs.remove(tCComponentGroupMember);
					}
				} catch (TCException tCException) {
					tCException.printStackTrace();
//					logger.error(tCException.getClass().getName(), tCException);
				}
				SignoffEditPanel.setAssignedUserSignOffs(assignedUserSignoffs);
			}
		});
		jPanel2.add(this.addButton);
		jPanel2.add(this.removeButton);
		jPanel2.add(this.modifyButton);
		this.memberPanel.add("bottom.nobind", jPanel2);
		this.memberPanel.add("bottom.bind", new Separator());
		this.memberPanel.add("bottom.bind", this.requiredPanel);
		this.memberPanel.add("unbound.bind", this.currentPanel);
		JScrollPane jScrollPane3 = new JScrollPane(jPanel1);
		this.splitPane.setLeftComponent(jScrollPane3);
		this.splitPane.setRightComponent(this.memberPanel);
		this.splitPane.setDividerSize(2);		
		// recompile 20220323143000 : START
//		this.splitPane.setDividerLocation(0.5D);
		this.splitPane.setDividerLocation(0.28D);
		// 20220323143000 : END
		jPanel2 = new JPanel(new ButtonLayout());
		this.applyButton = new JButton(this.r.getString("apply"));
		this.applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				if (!UserAdhocSignoffsPanel.this.updateProcessDescription())
					return;
				UserAdhocSignoffsPanel.this.performChangeTaskStatusAction(false);
				UserAdhocSignoffsPanel.this.applyButton.setEnabled(false);
			}
		});
		this.okButton = new JButton(this.r.getString("ok"));
		this.okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				if (!UserAdhocSignoffsPanel.this.updateProcessDescription())
					return;
				
				// recompile 20220323143000 : START
				try {
					boolean blnFlag = checkPublicMailInfo(Arrays.asList(task.getProperty("parent_name")));
					if (!blnFlag) {
						int option = javax.swing.JOptionPane.showConfirmDialog(null, "未分配實際工作者郵箱,是否繼續?", "提示", javax.swing.JOptionPane.YES_NO_OPTION);
						if (option == javax.swing.JOptionPane.NO_OPTION) {					
							return;
						}
					}
					savePublicMailInfo();	
				} catch (TCException e) {
					e.printStackTrace();
				}				
				// 20220323143000 : END
				
				UserAdhocSignoffsPanel.this.performChangeTaskStatusAction(true);																			
			}
		});
		this.cancelButton = new JButton(this.r.getString("close"));		
		jPanel2.add(this.okButton);
		jPanel2.add(this.applyButton);
		jPanel2.add(this.cancelButton);
		this.applyButton.setVisible(SignoffTree.isTaskStarted(this.task));
		this.okButton.setVisible(false);
		this.cancelButton.setVisible(false);
		this.okButton.setEnabled(false);
		this.applyButton.setEnabled(false);
		add("unbound.bind", this.splitPane);
		add("bottom.bind", jPanel2);
		try {
			String str = tCPreferenceService.getStringValue("WRKFLW_task_complete");
			if (Boolean.valueOf(str).booleanValue() && this.task.isValidPerformer()) {
				String str3 = this.task.getStringProperty("task_result");
				if (str3.equalsIgnoreCase(this.UnsetResult))
					this.task.setStringProperty("task_result", "Completed");
			}
		} catch (Exception exception) {
			MessageBox.post(exception);
		}
				
		setPreferredSize(new Dimension(500, 425));
	}

	private void setWaitForUndecidedReviewersBox() {
		String str1 = "0";
		try {
			str1 = this.task.getProperty("wait_for_all_reviewers");
		} catch (TCException tCException) {
			Debug.println(tCException.getClass().getName(), tCException);
		}
		if (str1.equals("0")) {
			this.waitForUndecidedReviewers.setSelected(false);
		} else if (str1.equals("1")) {
			this.waitForUndecidedReviewers.setSelected(true);
		}
		TCPreferenceService tCPreferenceService = this.session.getPreferenceService();
		String str2 = tCPreferenceService.getStringValue("WRKFLW_allow_wait_for_undecided_override");
		if (!Boolean.valueOf(str2).booleanValue())
			this.waitForUndecidedReviewers.setEnabled(false);
		this.waitForUndecidedReviewers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent param1ActionEvent) {
				String str = "0";
				if (UserAdhocSignoffsPanel.this.waitForUndecidedReviewers.isSelected())
					str = "1";
				try {
					UserAdhocSignoffsPanel.this.task.setProperty("wait_for_all_reviewers", str);
				} catch (TCException tCException) {
					Debug.println(tCException.getClass().getName(), tCException);
				}
			}
		});
	}

	private void populateAssignedUserSignOffs(SignoffTreeNode paramSignoffTreeNode) {
		Enumeration enumeration = paramSignoffTreeNode.children();
		while (enumeration != null && enumeration.hasMoreElements()) {
			SignoffTreeNode signoffTreeNode = (SignoffTreeNode) enumeration.nextElement();
			if (signoffTreeNode.getChildCount() > 0) {
				populateAssignedUserSignOffs(signoffTreeNode);
				continue;
			}
			try {
				TCComponentSignoff tCComponentSignoff = signoffTreeNode.getSignoff();
				TCComponentGroupMember tCComponentGroupMember = null;
				if (tCComponentSignoff != null)
					tCComponentGroupMember = signoffTreeNode.getSignoff().getGroupMember();
				if (tCComponentGroupMember != null)
					this.assignedUserSignoffs.add(tCComponentGroupMember);
			} catch (TCException tCException) {
				tCException.printStackTrace();
//				logger.error(tCException.getClass().getName(), tCException);
			}
		}
	}

	public void loadTask(TCComponentTask paramTCComponentTask) {
		if (paramTCComponentTask == null)
			return;
		this.task = paramTCComponentTask;
		this.session.queueOperation(new AbstractAIFOperation(this.r.getString("loadSignoffTree.MSG"), false) {
			public void executeOperation() {
				if (UserAdhocSignoffsPanel.this.session != null)
					UserAdhocSignoffsPanel.this.loadSignoffsData();
			}
		});
	}

	public void setSplitPanelDivider(double paramDouble) {
		this.splitPane.setDividerLocation(paramDouble);
	}

	protected void loadSignoffsData() {
		setAdhocCheckBox();
	    try
	    {
	      if ((this.task.getState() == TCTaskState.COMPLETED) || (this.task.getState() == TCTaskState.FAILED))
	      {
	        this.adhocDoneCheckBox.setEnabled(false);
	        this.quorumField.setEnabled(false);
	        this.quorumPercentField.setEnabled(false);
	        this.quorumRadioButton.setEnabled(false);
	        this.quorumPercentRadioButton.setEnabled(false);
	      }
	    }
	    catch (Exception localException1)
	    {
	    	localException1.printStackTrace();
//	      logger.error("Exception", localException1);
	    }
	    try
	    {
	      TCProperty localTCProperty = this.task.getTCProperty("signoff_quorum");
	      int i = -100;
	      if (localTCProperty != null)
	      {
	        i = localTCProperty.getIntValue();
	        if (Debug.isOn("signoff,epm,signoffquorum"))
	          Debug.println("Got [signoff_quorum] " + i + " for task " + this.task.getUid() + " [" + this.task.toString() + "]");
	      }
	      String str1 = (new StringBuilder()).append(i).toString();
	      if (i < 0)
	      {
	        str1 = str1.replace("-", "");
	        this.quorumPercentField.setText(str1);
	        this.quorumPercentField.setEnabled(true);
	        this.quorumField.setEnabled(false);
	        this.quorumField.setText("");
	        this.quorumPercentRadioButton.setSelected(true);
	        this.currentQuorum = Integer.parseInt(str1);
	      }
	      else if (i == 0)
	      {
	        this.quorumPercentField.setText("100");
	        this.quorumPercentField.setEnabled(true);
	        this.quorumField.setEnabled(false);
	        this.quorumField.setText("");
	        this.quorumPercentRadioButton.setSelected(true);
	        performSetQuorumAction();
	        this.currentQuorum = 100;
	      }
	      else
	      {
	        this.quorumField.setText((new StringBuilder()).append(i).toString());
	        this.quorumField.setEnabled(true);
	        this.quorumPercentField.setEnabled(false);
	        this.quorumPercentField.setText("");
	        this.quorumRadioButton.setSelected(true);
	        this.currentQuorum = i;
	      }
	      TCTaskState localTCTaskState = this.task.getState();
	      if (localTCTaskState == TCTaskState.COMPLETED)
	      {
	        this.quorumField.setEnabled(false);
	        this.quorumPercentField.setEnabled(false);
	      }
	      TCPreferenceService localTCPreferenceService = this.session.getPreferenceService();
	      String str2 = localTCPreferenceService.getStringValue("WRKFLW_allow_quorum_override");
	      if ((str2 != null) && (!Boolean.valueOf(str2).booleanValue()))
	      {
	        this.quorumField.setEnabled(false);
	        this.quorumPercentField.setEnabled(false);
	        this.quorumRadioButton.setEnabled(false);
	        this.quorumPercentRadioButton.setEnabled(false);
	      }
	    }
	    catch (Exception localException2)
	    {
	    	localException2.printStackTrace();
//	      logger.error("Exception", localException2);
	    }
	    SwingUtilities.invokeLater(new Runnable()
	    {
	      public void run()
	      {
	        try
	        {
	          int[] arrayOfInt = UserAdhocSignoffsPanel.this.signoffTree.getSelectionRows();
	          if ((arrayOfInt == null) || ((arrayOfInt != null) && (arrayOfInt.length == 0)))
	          {
	            SignoffTreeNode localSignoffTreeNode = UserAdhocSignoffsPanel.this.signoffTree.getRootNode();
	            if (localSignoffTreeNode.getChildCount() > 0)
	            {
	              TreeNode localTreeNode = localSignoffTreeNode.getLastChild();
	              UserAdhocSignoffsPanel.this.signoffTree.setSelectionRow(localSignoffTreeNode.getIndex(localTreeNode));
	            }
	          }
	        }
	        catch (Exception localException)
	        {
	          localException.printStackTrace();
	        }
	      }
	    });
	    validateButtons();
	}

	private void setAdhocCheckBox() {
		if (this.adhocDoneCheckBox.isVisible())
			try {
				String str = "Unset";
				boolean bool = false;
				TCProperty tCProperty = this.task.getTCProperty("task_result");
				str = tCProperty.getStringValue();
				if (str.equals("Completed"))
					bool = true;
				this.adhocDoneCheckBox.setSelected(bool);
			} catch (Exception exception) {
				exception.printStackTrace();
//				logger.error("Exception", exception);
			}
	}

	protected void setAdhocDoneAttribute() {
		if (this.adhocDoneCheckBox.isVisible())
			try {
				String str = "Unset";
				boolean bool = this.adhocDoneCheckBox.isSelected();
				if (bool)
					str = "Completed";
				TCProperty tCProperty = this.task.getTCProperty("task_result");
				tCProperty.setStringValue(str);
			} catch (Exception exception) {
				exception.printStackTrace();
//				logger.error("Exception", exception);
			}
	}

	protected void setCommentsAttribute() {
		if (this.adhocDoneCheckBox.isVisible())
			try {
				this.task.setStringProperty("comments", this.commentsPanel.getComments());
			} catch (Exception exception) {
				exception.printStackTrace();
//				logger.error("Exception", exception);
			}
	}

	private void validateButtons() {
		TCTaskState tCTaskState = null;
		boolean bool = true;
		boolean bool1 = false;
		try {
			if (this.task != null) {
				try {
					String[] arrayOfString = { "responsible_party", "active_surrogate" };
					this.task.clearCache(arrayOfString);
					this.task.getRelatedList(new String[] { "responsible_party", "active_surrogate" });
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				tCTaskState = this.task.getState();
				bool1 = this.task.isValidPerformer();
				TCComponent[] arrayOfTCComponent = this.task.getAttachments(TCAttachmentScope.LOCAL, 4);
				if (arrayOfTCComponent != null && arrayOfTCComponent.length >= this.task.getQuorum()) {
					bool = true;
				} else {
					bool = false;
				}
			}
			TreePath treePath = this.signoffTree.getSelectionPath();
			SignoffTreeNode signoffTreeNode = null;
			boolean bool2 = (bool1 && tCTaskState != null);
			boolean bool3 = (bool2 && ((tCTaskState != null && tCTaskState.equals(TCTaskState.STARTED)) || tCTaskState.equals(TCTaskState.PENDING)));
			if (treePath != null) {
				signoffTreeNode = (SignoffTreeNode) treePath.getLastPathComponent();
				if (signoffTreeNode != null) {
					TCComponentSignoff tCComponentSignoff = signoffTreeNode.getSignoff();
					this.addButton.setVisible((signoffTreeNode.isAddVisible() && this.currentPanel.isVisible()));
					this.addButton.setEnabled((signoffTreeNode.isAddEnabled() && bool3 && this.currentPanel.isEnabled()));
					this.removeButton.setVisible((signoffTreeNode.isRemoveVisible() && this.currentPanel.isVisible()));
					boolean bool5 = false;
					if (tCComponentSignoff != null)
						bool5 = tCComponentSignoff.isDecisionRequiredUnModifiable();
					this.removeButton.setEnabled((signoffTreeNode.isRemoveEnabled() && bool2 && !bool5));
					this.modifyButton.setEnabled((signoffTreeNode.isModifyEnabled() && bool2));
					if (tCComponentSignoff != null) {
						this.requiredCheckBox.setSelected(tCComponentSignoff.isDecisionRequired());
						if (bool5) {
							this.requiredCheckBox.setEnabled(false);
						} else {
							this.requiredCheckBox.setEnabled(true);
						}
					} else {
						this.requiredCheckBox.setSelected(false);
						this.requiredCheckBox.setEnabled(true);
					}
				}
			} else {
				this.addButton.setVisible(false);
				this.removeButton.setVisible(false);
				this.modifyButton.setVisible(false);
				this.requiredCheckBox.setSelected(false);
				this.requiredCheckBox.setEnabled(true);
			}
			this.adhocDoneCheckBox.setEnabled(bool2);
			boolean bool4 = (bool1 && tCTaskState == TCTaskState.STARTED && this.signoffTree.isAllProfilesDone() && (!this.adhocDoneCheckBox.isVisible() || this.adhocDoneCheckBox.isSelected()) && bool);
			this.okButton.setEnabled(bool4);
			this.applyButton.setEnabled(bool4);
		} catch (Exception exception) {
			exception.printStackTrace();
//			logger.error("Exception while validating buttons", exception);
		}
	}

	private void validatePanels() {
		TreePath treePath = this.signoffTree.getSelectionPath();
		SignoffTreeNode signoffTreeNode1 = null;
		if (treePath != null)
			signoffTreeNode1 = (SignoffTreeNode) treePath.getLastPathComponent();
		this.currentPanel.setVisible(true);
		this.memberPanel.setVisible((treePath != null && treePath.getPathCount() > 1));
		if (!this.userSignOffsPopulated) {
			populateAssignedUserSignOffs(this.signoffTree.getRootNode());
			SignoffEditPanel.setAssignedUserSignOffs(this.assignedUserSignoffs);
			this.userSignOffsPopulated = true;
		}
		if (signoffTreeNode1 != null && treePath.getPathCount() == 2) {
			this.currentPanel.setComponent(signoffTreeNode1, signoffTreeNode1.getSignoffType(), this.task);
		} else if (signoffTreeNode1 != null) {
			this.currentPanel.setComponent(signoffTreeNode1, signoffTreeNode1.getTCComponent(), this.task);
		}
		final SignoffTreeNode selectedNode = signoffTreeNode1;
		this.session.queueOperation(new AbstractAIFOperation() {
			public void executeOperation() {
				if (selectedNode != null) {
					UserAdhocSignoffsPanel.this.currentPanel.setIcon(selectedNode.getIcon());
					UserAdhocSignoffsPanel.this.memberPanelTitleBorder.setTitle(selectedNode.getNodeFullName());
				}
				UserAdhocSignoffsPanel.this.validateButtons();
				UserAdhocSignoffsPanel.this.memberPanel.validate();
				UserAdhocSignoffsPanel.this.memberPanel.repaint();
			}
		});
	}

	private void performChangeTaskStatusAction(final boolean isOkToComplete) {
		this.session.queueOperation(new AbstractAIFOperation(this.r.getString("settingTaskStatus.MSG")) {
			public void executeOperation() {
				boolean bool = (UserAdhocSignoffsPanel.this.adhocDoneCheckBox.isVisible() && !UserAdhocSignoffsPanel.this.adhocDoneCheckBox.isSelected());
				// recompile 20220323143000 : START
				bool = true;
				// 20220323143000 : END				
				try {
					if (bool) {
						UserAdhocSignoffsPanel.this.updateQuorum();
						TCTaskState tCTaskState = UserAdhocSignoffsPanel.this.task.getState();
						if (tCTaskState == TCTaskState.STARTED) {
							if (Debug.isOn("epm,signoff"))
								Debug.println("Task " + UserAdhocSignoffsPanel.this.task.getUid() + " ["
										+ UserAdhocSignoffsPanel.this.task.toString()
										+ "]'s [done] flag is [true],state is [EPM_started], try to perform COMPLETE_ACTION");
							UserAdhocSignoffsPanel.this.triggerCompleteAction(isOkToComplete);
							UserAdhocSignoffsPanel.this.adhocDoneCheckBox.setEnabled(false);
							UserAdhocSignoffsPanel.this.applyButton.setEnabled(false);
							UserAdhocSignoffsPanel.this.okButton.setEnabled(false);
						}
					} else if (Debug.isOn("epm,signoff")) {
						Debug.println("Task " + UserAdhocSignoffsPanel.this.task.getUid() + " ["
								+ UserAdhocSignoffsPanel.this.task.toString()
								+ "]'s [done] flag is [false], do not try to complete the task");
					}
				} catch (Exception exception) {
					if (Debug.isOn("exception,tcexception,traceback,epm,signoff")) {
						Debug.println("Caught exception in performChangeTaskStatusAction()");
						if (Debug.isOn("traceback"))
							Debug.printStackTrace(exception);
					}
					MessageBox.post(UserAdhocSignoffsPanel.this.parent, exception);
					UserAdhocSignoffsPanel.this.refreshPanel();
				}
			}
		});
	}

	protected void triggerCompleteAction(boolean paramBoolean) throws TCException {
		initiateOperation(paramBoolean);
	}

	private void initiateOperation(boolean paramBoolean) {
		String str = null;
		if (this.is_secure_task)
			str = getPassword();
		if (paramBoolean) {
			this.performTask = new PerformTaskUtil(this.parent, this.task, this.commentsPanel.getComments(), str, "Completed", this.m_lsnr, null);
		} else {
			this.performTask = new PerformTaskUtil(this.parent, this.task, this.commentsPanel.getComments(), str, "Completed");
		}
		this.performTask.executeOperation();
	}

	private void performSetQuorumAction() {
		this.session.queueOperation(new AbstractAIFOperation(this.r.getString("savingQuorum.MSG")) {
			public void executeOperation() {
				try {
					UserAdhocSignoffsPanel.this.updateQuorum();
				} catch (Exception exception) {
					if (Debug.isOn("tcexception,exception,traceback,signoff,epm,signoffquorum")) {
						Debug.println("Caught exception in performSetQuorumAction() while setting signoff quorum");
						if (Debug.isOn("traceback"))
							Debug.printStackTrace(exception);
					}
					MessageBox.post(UserAdhocSignoffsPanel.this.parent, exception);
					UserAdhocSignoffsPanel.this.refreshPanel();
				}
			}
		});
	}

	void updateQuorum() throws Exception {
		String str = "";
		if (this.quorumRadioButton.isSelected()) {
			if (this.quorumField.getText().equals("All")) {
				str = "-100";
			} else {
				if (this.quorumField.getText().equals("0")) {
					this.quorumField.setText("-100");
					return;
				}
				str = this.quorumField.getText();
				if (str == null || str.length() <= 0) {
					str = this.quorumAllString;
					this.quorumField.setText("-100");
				}
			}
		} else if (this.quorumPercentRadioButton.isSelected()) {
			str = this.quorumPercentField.getText();
			if (str != null && str.length() > 0) {
				str = "-" + str;
			} else {
				str = this.quorumAllString;
			}
		}
		if (str != null && this.task != null) {
			int i = -100;
			if (containsOnlyNumbers(str)) {
				if (!str.equalsIgnoreCase(this.quorumAllString))
					i = Integer.parseInt(str);
			} else {
				throw new TCException(this.r.getString("notValidQuorum"));
			}
			if (i != this.currentQuorum) {
				TCProperty tCProperty = this.task.getTCProperty("signoff_quorum");
				if (tCProperty != null) {
					if (Debug.isOn("signoff,epm,signoffquorum"))
						Debug.println("Set [signoff_quorum] to " + i + " for task " + this.task.getUid() + " ["
								+ this.task.toString() + "]");
					tCProperty.setIntValue(i);
					this.currentQuorum = i;
				}
			}
		}
	}

	protected void refreshPanel() {
		loadSignoffsData();
	}

	private boolean containsOnlyNumbers(String paramString) {
		if (paramString == null || paramString.length() == 0)
			return false;
		if (paramString.equalsIgnoreCase("All"))
			return true;
		char c = paramString.charAt(0);
		if (!Character.isDigit(c) && (c != '-' || paramString.length() == 1))
			return false;
		for (int b = 1; b < paramString.length(); b++) {
			if (!Character.isDigit(paramString.charAt(b)))
				return false;
		}
		return true;
	}

	private boolean updateProcessDescription() {
		try {
			String str = this.processDesc.getText();
			if (this.oldDesc == null) {
				if (str != null)
					this.task.getProcess().setStringProperty("object_desc", str);
			} else if (!this.oldDesc.equals(str)) {
				this.task.getProcess().setStringProperty("object_desc", str);
			}
		} catch (Exception exception) {
			MessageBox.post(this.parent, exception);
			return false;
		}
		return true;
	}

	public JButton getOkButton() {
		return this.okButton;
	}

	public JButton getApplyButton() {
		return this.applyButton;
	}

	public JButton getCancelButton() {
		return this.cancelButton;
	}

	public void processComponentEvents(final AIFComponentEvent[] componentEvents) {
		String str = this.r.getString("refreshDisplayMessage");
		this.session.setReadyStatus();
		this.session.queueOperation(new AbstractAIFOperation(str, false) {
			public void executeOperation() {
				UserAdhocSignoffsPanel.this.processComponentEventsRequest(componentEvents);
			}
		});
	}

	private void processComponentEventsRequest(AIFComponentEvent[] paramArrayOfAIFComponentEvent) {
		try {
			if (this.task != null)
				for (int b = 0; b < paramArrayOfAIFComponentEvent.length; b++) {
					InterfaceAIFComponent interfaceAIFComponent = paramArrayOfAIFComponentEvent[b].getComponent();
					if ((interfaceAIFComponent instanceof TCComponentTask
							|| paramArrayOfAIFComponentEvent[b] instanceof com.teamcenter.rac.aif.kernel.AIFComponentChangeEvent)
							&& interfaceAIFComponent.equals(this.task)) {
						updateAdhocSignoffPanel(this.task);
						loadTask(this.task);
						revalidate();
						repaint();
						break;
					}
				}
		} catch (Exception exception) {
			MessageBox.post(exception);
		}
	}

	private void updateAdhocSignoffPanel(TCComponentTask paramTCComponentTask) throws TCException {
		String str1 = paramTCComponentTask.getStringProperty("comments");
		this.commentsPanel.getTextArea().setText(str1);
		String str2 = "0";
		try {
			str2 = paramTCComponentTask.getProperty("wait_for_all_reviewers");
		} catch (TCException tCException) {
			Debug.println(tCException.getClass().getName(), tCException);
		}
		if (str2.equals("0")) {
			this.waitForUndecidedReviewers.setSelected(false);
		} else if (str2.equals("1")) {
			this.waitForUndecidedReviewers.setSelected(true);
		}
	}

	public void closeSignaled() {
		detachListeners();
	}

	protected void detachListeners() {
		this.session.removeAIFComponentEventListener(this);
	}

	public void setOperationListener(InterfaceAIFOperationListener paramInterfaceAIFOperationListener) {
		this.m_lsnr = paramInterfaceAIFOperationListener;
	}
	
	// recompile 20220323143000 : START				
	public List<TCComponent> getItem() {
		
		try {
			TCComponent[] tcCompArr = task.getAttachments(TCAttachmentScope.GLOBAL, 1);
			
			return Arrays.asList(tcCompArr).stream().filter(e -> (e instanceof TCComponentItem || e instanceof TCComponentItemRevision)).collect(Collectors.toList());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String getProjectId() {
		try {
			if (m_tcCompLst != null) {
				
				return m_tcCompLst.stream().map(e -> {
					try {
						TCComponentItemRevision itemRev = null;
						
						if(e instanceof TCComponentItem){
							itemRev = ((TCComponentItem)e).getLatestItemRevision();
						}
						if(e instanceof TCComponentItemRevision){
							itemRev = (TCComponentItemRevision)e;
						}
						
						return TCUtil.getProjects(itemRev).stream().map(o -> {
														
							try {
								TCComponentProject prjOfItemRev = (TCComponentProject)o;
								if (prjOfItemRev != null) {									
									TCProperty propOfItemRev = prjOfItemRev.getTCProperty("project_id");
									
									return propOfItemRev.getStringValue();
								}
							} catch (TCException e3) {
								e3.printStackTrace();
							}								
								
							return "";
																											
						}).collect(Collectors.toList()).stream().collect(Collectors.joining(",")); 
					} catch (TCException e2) {
						e2.printStackTrace();
					}
					
					return "";
					
				}).collect(Collectors.toList()).stream().filter(CommonTools.distinctByKey(str -> str)).collect(Collectors.joining(","));	
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return "";
	}
	
	public String getProjectName() {
		try {
			if (m_tcCompLst != null) {
				
				return m_tcCompLst.stream().map(e -> {
					try {
						TCComponentItemRevision itemRev = null;
						
						if(e instanceof TCComponentItem){
							itemRev = ((TCComponentItem)e).getLatestItemRevision();
						}
						if(e instanceof TCComponentItemRevision){
							itemRev = (TCComponentItemRevision)e;
						}
						
						return TCUtil.getProjects(itemRev).stream().map(o -> {
														
							try {
								TCComponentProject prjOfItemRev = (TCComponentProject)o;
								if (prjOfItemRev != null) {									
									TCProperty propOfItemRev = prjOfItemRev.getTCProperty("object_name");
//									TCProperty propOfItemRev = prjOfItemRev.getTCProperty("project_id");
									
									return propOfItemRev.getStringValue();
								}
							} catch (TCException e3) {
								e3.printStackTrace();
							}								
								
							return "";
																											
						}).collect(Collectors.toList()).stream().collect(Collectors.joining(",")); 
					} catch (TCException e2) {
						e2.printStackTrace();
					}
					
					return "";
					
				}).collect(Collectors.toList()).stream().collect(Collectors.joining(","));	
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return "";
	}
	
	private String getItemName() {
		try {			
			if (m_tcCompLst != null) {				
				return m_tcCompLst.stream().map(o -> {
					try {
						TCComponent targetComp = (TCComponent) o;
						
//						return targetComp.getProperty("object_name");
						return targetComp.getProperty("item_id") + "|" + targetComp.getProperty("object_name");
					} catch (TCException e) {
						e.printStackTrace();
					}
					
					return "";
					
				}).collect(Collectors.toList()).stream().collect(Collectors.joining(","));
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	private String getItemId() {
		try {			
			if (m_tcCompLst != null) {				
				return m_tcCompLst.stream().map(o -> {
					try {
						TCComponent targetComp = (TCComponent) o;
						
						return targetComp.getProperty("item_id");
					} catch (TCException e) {
						e.printStackTrace();
					}
					
					return "";
					
				}).collect(Collectors.toList()).stream().collect(Collectors.joining(","));
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	private String getItemUid() {
		try {			
			if (m_tcCompLst != null) {				
				return m_tcCompLst.stream().map(o -> {
					try {
						TCComponent targetComp = (TCComponent) o;
						
						return targetComp.getUid();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					return "";
					
				}).collect(Collectors.toList()).stream().collect(Collectors.joining(","));
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	private int getLstIndex(List<List<String>> tableLst) {
		int retCode = -1;
		
		for (int i = 0; i < tableLst.size(); i++) {
			List<String> rowLst = tableLst.get(i);
			
			if (4 == rowLst.size()) {													
//				String tableInfo = rowLst.get(1);
//				String tableInfoForPart = tableInfo.split("/")[tableInfo.split("/").length-1];
//				String nodeInfo = signoffTree.getSelectedNode().toString();
//				String nodeInfoForPart = nodeInfo.split(" - ")[0];								
//				if (nodeInfoForPart.equals(tableInfoForPart)) {
//					return i;
//				}
				
				String tableInfo = rowLst.get(1);
				String nodeInfo = TCUtil.parseBracket(TCUtil.parseNode(signoffTree.getSelectedNode().toString()));
				if (tableInfo.equals(nodeInfo)) {
					return i;
				}
			}
		}
		
		return retCode;
	}
	
	private boolean checkPublicMailInfo(List<String> startNodeLst) {		
		try {
			int rowCount = currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.table.getRowCount();
			if (0 == rowCount || (1 == rowCount && "".equals(currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.tableModel.getValueAt(0, 0).toString())))
				return false;
			
			for (String startNode : startNodeLst) {
				for (int i = 0; i < rowCount; i++) {
					if (startNode.equals(currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.tableModel.getValueAt(i, 0).toString()) && 
							"".equals(currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.tableModel.getValueAt(i, 3).toString())) {
						return false;
					}
				}
			}							
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	
	private void savePublicMailInfo() {
		try {											
			Map<String, List<String>> pubMailMap = new LinkedHashMap<String, List<String>>();
			if (m_tcCompLst.size() > 0) {
				pubMailMap = TCUtil.getPublicMailInfo(m_tcCompLst.get(0));
			}
			int rowCount = currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.table.getRowCount();
			int colCount = currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.table.getColumnCount();
			if (rowCount > 0) {
				
				for (int i = 0; i < rowCount; i++) {
					List<String> pubMailLst = new ArrayList<String>();
					if (colCount > 0) {
						for (int j = 0; j < colCount; j++) {
							pubMailLst.add(currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.tableModel.getValueAt(i, j).toString());
						}
						String key = currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.tableModel.getValueAt(i, 0).toString();
						
						if (pubMailMap.containsKey(key)) {
							List<String> valueLst = pubMailMap.get(key);
							if (4 == pubMailLst.size() && 4 == valueLst.size()) {
								valueLst.set(1, ("".equals(valueLst.get(1)) ? pubMailLst.get(1) : (valueLst.get(1) +","+pubMailLst.get(1))));
								valueLst.set(2, ("".equals(valueLst.get(2)) ? pubMailLst.get(2) : (valueLst.get(2) +","+pubMailLst.get(2))));
								valueLst.set(3, ("".equals(valueLst.get(3)) ? pubMailLst.get(3) : (valueLst.get(3) +","+pubMailLst.get(3))));
								pubMailMap.put(key, valueLst);
							}
						} else {
							pubMailMap.put(key, pubMailLst);
						}														
					}				
				}
				
				String projectNames = getProjectName();
				String itemNames = getItemName();
				ArrayList<ProcessFormTableRow> arrayList = new ArrayList<ProcessFormTableRow>();
				
				if (pubMailMap != null && pubMailMap.size() > 0) {
					String taskName = "";
					String publicUser = "";
					String realUser = "";
					String publicMail = "";
					for (Map.Entry<String, List<String>> entry : pubMailMap.entrySet()) {
						List<String> tableInfoLst = entry.getValue();
						if (4 == tableInfoLst.size()) {
							taskName = tableInfoLst.get(0);
							publicUser = tableInfoLst.get(1);
							realUser = tableInfoLst.get(2);
							publicMail = tableInfoLst.get(3);
						}
						
						ProcessFormTableRow row = new ProcessFormTableRow();
						row.setD9_ProcessNode(taskName);
						row.setD9_TCUser(publicUser);
						row.setD9_ActualUserName(realUser);
						row.setD9_ActualUserMail(publicMail);
						
						arrayList.add(row);
						
					}
				}
				
				DataManagementService dmService = DataManagementService.getService(session);

				if (m_tcCompLst != null) {
					for (InterfaceAIFComponent tcIAComponent : m_tcCompLst) {
						TCComponent targetComp = (TCComponent) tcIAComponent;
						targetComp.setRelated("IMAN_external_object_link", new TCComponent[] {});
						TCComponentForm createForm = (TCComponentForm)com.foxconn.tcutils.util.TCUtil.getPublicMailForm(dmService, "publicMail", "D9_TaskForm", "IMAN_external_object_link", targetComp, Boolean.TRUE);
						
						createForm.setRelated("d9_TaskTable", new TCComponent[] {});
						
						for (int i = 0; i < arrayList.size(); i++) {
							ProcessFormTableRow tableRow = arrayList.get(i);
							String d9_ProcessNode = tableRow.getD9_ProcessNode();
							String d9_TCUser = tableRow.getD9_TCUser();
							String d9_ActualUserName = tableRow.getD9_ActualUserName();
							String d9_ActualUserMail = tableRow.getD9_ActualUserMail();
							
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("d9_ProcessNode", d9_ProcessNode);
							map.put("d9_TCUser", d9_TCUser);
							map.put("d9_ActualUserName", d9_ActualUserName);
							map.put("d9_ActualUserMail", d9_ActualUserMail);
							
							TCComponent createTableRow = com.foxconn.tcutils.util.TCUtil.createTableRow(session, "D9_TaskTableRow", map);
							createForm.add("d9_TaskTable", createTableRow);
						}
						
						if(projectNames !=null && !"".equals(projectNames)) {
							TCProperty d9_ProjectList = createForm.getTCProperty("d9_ProjectList");
							String[] split = projectNames.split(",");
							d9_ProjectList.setStringValueArray(split);
							createForm.setTCProperty(d9_ProjectList);
						}
						
						if(itemNames !=null && !"".equals(itemNames)) {
							TCProperty d9_ProcessTarget = createForm.getTCProperty("d9_ProcessTarget");
							String[] split1 = itemNames.split(",");
							d9_ProcessTarget.setStringValueArray(split1);
							createForm.setTCProperty(d9_ProcessTarget);
						}
					}
				}
				
				
				String[] mailContentArr = new String[12];
				mailContentArr[0] = r.getString("mailConent1");
				mailContentArr[1] = r.getString("mailConent2");
				mailContentArr[2] = r.getString("mailConent3");
				mailContentArr[3] = r.getString("mailConent4");
				mailContentArr[4] = r.getString("mailConent5");
				mailContentArr[5] = r.getString("mailConent6");
				mailContentArr[6] = r.getString("mailConent7");
				mailContentArr[7] = r.getString("mailConent8");
				mailContentArr[8] = r.getString("mailConent9");
				mailContentArr[9] = r.getString("mailConent10");
				mailContentArr[10] = r.getString("mailConent11");
				mailContentArr[11] = r.getString("mailConent12");
				
				String[] notifyContentArr = new String[12];
				notifyContentArr[0] = r.getString("mailConent1.notify");
				notifyContentArr[1] = r.getString("mailConent2.notify");
				notifyContentArr[2] = r.getString("mailConent3.notify");
				notifyContentArr[3] = r.getString("mailConent4.notify");
				notifyContentArr[4] = r.getString("mailConent5.notify");
				notifyContentArr[5] = r.getString("mailConent6.notify");
				notifyContentArr[6] = r.getString("mailConent7.notify");
				notifyContentArr[7] = r.getString("mailConent8.notify");
				notifyContentArr[8] = r.getString("mailConent9.notify");
				notifyContentArr[9] = r.getString("mailConent10.notify");
				notifyContentArr[10] = r.getString("mailConent11.notify");
				notifyContentArr[11] = r.getString("mailConent12.notify");
				
				String[] finishContentArr = new String[9];
				finishContentArr[0] = r.getString("mailConent1.finish");
				finishContentArr[1] = r.getString("mailConent2.finish");
				finishContentArr[2] = r.getString("mailConent3.finish");
				finishContentArr[3] = r.getString("mailConent4.finish");
				finishContentArr[4] = r.getString("mailConent5.finish");
				finishContentArr[5] = r.getString("mailConent6.finish");
				finishContentArr[6] = r.getString("mailConent7.finish");
				finishContentArr[7] = r.getString("mailConent8.finish");
				finishContentArr[8] = r.getString("mailConent9.finish");
								
				Map<String, String> nodeMap = new LinkedHashMap<String, String>();
				List<TCComponent> taskList = new ArrayList<TCComponent>();
				
//				TCComponent tcComponentTask = task.getReferenceProperty("parent_name");
				TCComponent tcComponentTask = task.getReferenceProperty("parent_task");
				nodeMap.put(tcComponentTask.getProperty("object_name"), tcComponentTask.getProperty("task_type"));								
				taskList.add(tcComponentTask);
			    if(TCUtil.noSendMail(nodeMap, task)) return;
				Map<String, List<String>> tempPubMailMap = pubMailMap;
				new Thread(new Runnable() {
					public void run() {
						try {
							TCUtil.sendMailForApprove(
									r.getString("actionLog.title"), 
									mailContentArr,
									notifyContentArr,
									finishContentArr,
									nodeMap, 
									taskList,
									Arrays.asList(getProjectName().split(",")), 
									Arrays.asList(getItemName().split(",")), 
									Arrays.asList(getItemId().split(",")),
									Arrays.asList(getItemUid().split(",")),
									tempPubMailMap);	
						} catch (Exception e) {
							e.printStackTrace();
						}						
					}
				}).start();								
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	
	
	
	private void addPubMailInfo(TCComponent[] arrayOfTCComponent) {
		try {			
			List<List<String>> oldTableLst = currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.getTabelInfo();
			for (TCComponent tcComponent : arrayOfTCComponent) {
				if (tcComponent != null && tcComponent instanceof TCComponentGroupMember) {
					TCComponent tcCompUser = tcComponent.getReferenceProperty("user");
					if (tcCompUser != null && tcCompUser instanceof TCComponentUser) {
						
						boolean blnFlag = true;
						String itemActualUserId = getItemActualUserIds();
						if (!TCUtil.isNull(itemActualUserId)) {
							List<SPASUser> actualUserLst = TCUtil.getActualUserInfo(RACUIUtil.getTCSession(), itemActualUserId);
							if (null == actualUserLst || 0 == actualUserLst.size()) {
								blnFlag = true;
							} else {
								blnFlag = false;
								
								oldTableLst.add(Arrays.asList(task.getProperty("parent_name"), 
//										TCUtil.parseNode(tcComponent.getProperty("object_name")), 
										tcCompUser.getProperty("user_id"),
										actualUserLst.get(0).getName(), 
										actualUserLst.get(0).getNotes()));
								
//								currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.m_rowMap.put(task.getProperty("parent_name") + TCUtil.parseNode(tcComponent.getProperty("object_name")),
								currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.m_rowMap.put(task.getProperty("parent_name") + tcCompUser.getProperty("user_id"),
										Arrays.asList(task.getProperty("parent_name"), 
//										TCUtil.parseNode(tcComponent.getProperty("object_name")),
												tcCompUser.getProperty("user_id"),
												actualUserLst.get(0).getName(), 
												actualUserLst.get(0).getNotes()));
							}
						}
											
						if (blnFlag) {
							TCComponent tcCompPerson = tcCompUser.getReferenceProperty("person");
							if (tcCompPerson != null && tcCompPerson instanceof TCComponentPerson) {																								
								oldTableLst.add(Arrays.asList(task.getProperty("parent_name"), 
//										TCUtil.parseNode(tcComponent.getProperty("object_name")), 
										tcCompUser.getProperty("user_id"),
										tcCompPerson.getProperty("user_name"), 
										tcCompPerson.getProperty("PA9")));
								
//								currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.m_rowMap.put(task.getProperty("parent_name") + TCUtil.parseNode(tcComponent.getProperty("object_name")),
								currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.m_rowMap.put(task.getProperty("parent_name") + tcCompUser.getProperty("user_id"),
										Arrays.asList(task.getProperty("parent_name"), 
//										TCUtil.parseNode(tcComponent.getProperty("object_name")),
												tcCompUser.getProperty("user_id"),
										tcCompPerson.getProperty("user_name"), 
										tcCompPerson.getProperty("PA9")));
							}
						}						
					}
				}																				
			}
//			System.out.println("oldTableLst is : " + oldTableLst.toString());
			currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.initializePanel(oldTableLst);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void removePubMailInfo() {
		try {			
			List<List<String>> oldTableLst = currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.getTabelInfo();
			int lstIndex = getLstIndex(oldTableLst);
			if (lstIndex > -1 && lstIndex < oldTableLst.size()) {
				if (oldTableLst.size()-1 >= 0) {
					oldTableLst.remove(lstIndex);
					JTable table = currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.table;
					TableModel tableModel = table.getModel();
					String node = tableModel.getValueAt(lstIndex, 0).toString();
					String publicAccount = tableModel.getValueAt(lstIndex, 1).toString();
					String realAccount = tableModel.getValueAt(lstIndex, 2).toString();
					String realMail = tableModel.getValueAt(lstIndex, 3).toString();
					currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.m_rowMap.values().removeIf(value -> {
						return (node.equals(value.get(0)) && publicAccount.equals(value.get(1)) && realAccount.equals(value.get(2)) && realMail.equals(value.get(3)));
					});
				}
			}
			currentPanel.genericUserSelectionPanel.publicAccountPanel.publicMailPanel.initializePanel(oldTableLst);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected String getItemActualUserIds() {
		try {			
			if (m_tcCompLst != null) {				
				return m_tcCompLst.stream().map(o -> {
					try {
						TCComponent targetComp = (TCComponent) o;
						
						return targetComp.getProperty("d9_ActualUserID");
					} catch (TCException e) {
						e.printStackTrace();
					}
					
					return "";
					
				}).collect(Collectors.toList()).stream().collect(Collectors.joining(","));
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}
	// 20220323143000 : END
	
}