package com.teamcenter.rac.workflow.commands.conditiontask;

import com.foxconn.decompile.service.SecondSourceService;
import com.foxconn.decompile.util.FileStreamUtil;
import com.foxconn.decompile.util.TCUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.InterfaceAIFOperationListener;
import com.teamcenter.rac.aif.kernel.AIFComponentEvent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponentEventListener;
import com.teamcenter.rac.kernel.TCAttachmentScope;
import com.teamcenter.rac.kernel.TCCRDecision;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentActionHandler;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentRule;
import com.teamcenter.rac.kernel.TCComponentRuleHandler;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTaskState;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.FilterDocument;
import com.teamcenter.rac.util.HorizontalLayout;
import com.teamcenter.rac.util.InterfaceSignalOnClose;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Painter;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Separator;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.iTextArea;
import com.teamcenter.rac.util.log.Debug;
import com.teamcenter.rac.workflow.commands.digitalsign.PerformTaskUtil;
import com.teamcenter.rac.workflow.commands.save.SaveTaskOperation;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class UserConditionTaskPanel extends JPanel implements InterfaceAIFOperationListener, InterfaceAIFComponentEventListener, InterfaceSignalOnClose {
	protected JLabel icon;

	protected JLabel taskNameLabel;

	protected JTextField taskName;

	private JTextArea commentsTextArea;

	private JLabel commentsLabel;

	protected JLabel taskInstructionsLabel;

	protected JTextArea taskInstructions;

	protected iTextArea processDesc;

	private String oldDesc;

	protected JLabel taskResultLabel;

	protected ButtonGroup radioGroup;

	protected JRadioButton rbTrue;

	protected JRadioButton rbFalse;

	private JRadioButton unableToCompleteRadioButton;

	private boolean failurePathPresent = false;

	protected JRadioButton rbUnset;

	protected JComboBox resultComboBox;

	private ArrayList customResultArrayList;

	private ArrayList radioButtonList;

	protected String resultToSet = "4";

	protected JButton okButton;

	protected JButton cancelButton;

	private PerformTaskUtil performTask = null;

	protected SaveTaskOperation saveTaskOperation;

	protected TCComponentTask task;

	protected TCSession session;

	protected Registry registry;

	protected AIFDesktop desktop = null;

	protected boolean is_secure_task = false;

	protected AbstractAIFDialog parent;

	protected JLabel userPassword;

	protected JPasswordField passwordTextField;

	public static final String PERFORM_CONDITION_TASK_ICON_REG_KEY = "performConditionTask.ICON";

	public static final String TASK_NAME_LABEL_REG_KEY = "taskNameLabel";

	public static final String TASK_INSTRUCTIONS_LABEL_REG_KEY = "taskInstructionsLabel";

	public static final String PROCESS_DESC_LABEL_REG_KEY = "processDescLabel";

	public static final String TASK_RESULT_LABEL_REG_KEY = "taskResultLabel";

	public static final String REFRESH_DISPLAY_REG_KEY = "refreshDisplayMessage";

	public static final String UNABLE_COMPLETE_KEY = "unableToCompleteLabel";

	public static final String UNABLE_TO_COMPLETE_PROP_STRING = "unable_to_complete";

	public static final int TASK_COMMENT_SIZE = 4000;

	private final int[] EPMActions = { 1, 2, 4, 5, 6, 7, 8, 9, 100 };

	private final String checkConditionHandler = "EPM-check-condition";

	private static final String sourceTaskArg = "-source_task";

	private static final String decisionArg = "-decision";
    private boolean d9_2ndSourceChangeList=false;
	public UserConditionTaskPanel(AIFDesktop paramAIFDesktop, JPanel paramJPanel, TCComponentTask paramTCComponentTask) {
		this(paramAIFDesktop, (AbstractAIFDialog)null, paramTCComponentTask);
	}

	public UserConditionTaskPanel(AIFDesktop paramAIFDesktop, AbstractAIFDialog paramAbstractAIFDialog, TCComponentTask paramTCComponentTask) {
		super(new VerticalLayout(5, 2, 2, 2, 2));
		this.parent = paramAbstractAIFDialog;
		this.session = paramTCComponentTask.getSession();
		this.registry = Registry.getRegistry(this);
		this.task = paramTCComponentTask;
		this.desktop = paramAIFDesktop;
		try {
			this.failurePathPresent = this.task.getLogicalProperty("has_failure_paths");
		} catch (TCException tCException) {
			Debug.println(tCException.getClass().getName(), tCException);
		}
		initPanel();
		this.session.addAIFComponentEventListener(this);
	}

	private void initPanel() {
		try {
			JPanel jPanel1 = new JPanel(new PropertyLayout());
			JPanel jPanel2 = new JPanel(new ButtonLayout());
			this.radioButtonList = new ArrayList();
			this.icon = new JLabel(this.registry.getImageIcon("performConditionTask.ICON"), 0);
			this.taskNameLabel = new JLabel(this.registry.getString("taskNameLabel"));
			String str1 = this.task.getName();
			this.taskName = new JTextField(str1, 20);
			this.taskName.setEditable(false);
			this.commentsLabel = new JLabel(this.registry.getString("commentsLabel"));
			String str2 = TCSession.getServerEncodingName(this.session);
			this.commentsTextArea = new JTextArea(new FilterDocument(4000, str2), "", 3, 50);
			this.commentsTextArea.setEditable(true);
			this.commentsTextArea.setLineWrap(true);
			this.customResultArrayList = new ArrayList();
			this.taskInstructionsLabel = new JLabel(this.registry.getString("taskInstructionsLabel"));
			this.taskInstructions = new JTextArea(this.task.getInstructions(), 3, 50);
			this.taskInstructions.setEditable(false);
			this.taskInstructions.setLineWrap(true);
			JLabel jLabel = new JLabel(this.registry.getString("processDescLabel"));
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
			this.taskResultLabel = new JLabel(this.registry.getString("taskResultLabel"));
			JPanel jPanel3 = new JPanel(new HorizontalLayout(5, 0, 5, 0, 0));
			this.radioGroup = new ButtonGroup();
			this.rbTrue = new JRadioButton(this.registry.getString("true"));
			this.rbTrue.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent param1ItemEvent) {
					resultToSet = "true";
				}
			});
			this.rbFalse = new JRadioButton(this.registry.getString("false"));
			this.rbFalse.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent param1ItemEvent) {
					resultToSet = "false";
				}
			});
			this.rbUnset = new JRadioButton(this.registry.getString("unset"));
			this.rbUnset.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent param1ItemEvent) {
					resultToSet = "unset";
				}
			});
			this.unableToCompleteRadioButton = new JRadioButton(this.registry.getString("unableToCompleteLabel"));
			this.unableToCompleteRadioButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent param1ItemEvent) {
					resultToSet = "unableToComplete";
				}
			});
			this.unableToCompleteRadioButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent param1ActionEvent) {
					okButton.setEnabled(true);
				}
			});
			this.unableToCompleteRadioButton.setVisible(this.failurePathPresent);
			this.unableToCompleteRadioButton.setEnabled(false);
			setCustomResults(this.task.getTaskDefinition());
			this.resultComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent param1ItemEvent) {
					String str = resultComboBox.getSelectedItem().toString();
					str = str.toLowerCase();
					resultToSet = str;
				}
			});
			this.resultComboBox.setSize(100, 10);
			this.radioGroup.add(this.rbTrue);
			this.radioGroup.add(this.rbFalse);
			this.radioGroup.add(this.rbUnset);
			this.radioGroup.add(this.unableToCompleteRadioButton);
			jPanel3.setLayout(new GridLayout(this.customResultArrayList.size() / 2 + 1, 1));
			for (int b = 0; b < this.customResultArrayList.size(); b++) {
				final String tempResult = (String) this.customResultArrayList.get(b);
				if (!tempResult.equals("unset") && !tempResult.equals("")) {
					JRadioButton jRadioButton;
					if (tempResult.equals("true") || tempResult.equals("false")) {
						jRadioButton = new JRadioButton(this.registry.getString(tempResult));
					} else {
						jRadioButton = new JRadioButton(tempResult);
					}
					jRadioButton.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent param1ItemEvent) {
							resultToSet = tempResult;
						}
					});
					this.radioGroup.add(jRadioButton);
					jPanel3.add(jRadioButton);
					this.radioButtonList.add(jRadioButton);
				}
			}
			jPanel3.add(this.rbUnset);
			jPanel3.add(this.unableToCompleteRadioButton);
			if (this.parent != null) {
				this.okButton = new JButton(this.registry.getString("ok"));
				this.okButton.setMnemonic(this.registry.getString("ok.MNEMONIC").charAt(0));
			} else {
				this.okButton = new JButton(this.registry.getString("apply"));
				this.okButton.setMnemonic(this.registry.getString("apply.MNEMONIC").charAt(0));
			}
			this.okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent param1ActionEvent) {
					if (!updateProcessDescription())
						return;
									
					if(d9_2ndSourceChangeList&&!(resultToSet.equalsIgnoreCase("unset"))) {
						try {
							//this.resultToSet.equals("unset")
							System.out.print(resultToSet.equalsIgnoreCase("unset"));
						    TCComponent[] coms = task.getRelatedComponents("root_target_attachments");						
						    new SecondSourceService().genChangeList(task.getSession(), coms);					
						}catch(Exception e) {}
					}
					startConditionTaskOperation();
					
					// recompile 20220323143000 : START
					sendMail();														
					// 20220323143000 : END
				}
			});
			this.okButton.setEnabled(false);
			this.okButton.setVisible(false);
			jPanel2.add(this.okButton);
			if (this.parent != null) {
				this.cancelButton = new JButton(this.registry.getString("cancel"));
				this.cancelButton.setMnemonic(this.registry.getString("cancel.MNEMONIC").charAt(0));
				this.cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent param1ActionEvent) {
						parent.disposeDialog();
					}
				});
				this.cancelButton.setEnabled(true);
				this.cancelButton.setVisible(true);
				jPanel2.add(this.cancelButton);
			}
			this.commentsTextArea.setText(this.task.getStringProperty("comments"));
			try {
				this.is_secure_task = this.task.isSecureTask();
				if (this.is_secure_task) {
					this.userPassword = new JLabel(this.registry.getString("passwordLabel"));
					this.passwordTextField = new JPasswordField(20) {
						public void paint(Graphics param1Graphics) {
							super.paint(param1Graphics);
							Painter.paintIsRequired(this, param1Graphics);
						}
					};
				}
			} catch (TCException tCException) {
				tCException.printStackTrace();
			}
			add("top.nobind.left", this.icon);
			add("top.bind", new Separator());
			jPanel1.add("1.1.right.top.preferred.preferred", this.taskNameLabel);
			jPanel1.add("1.2.center.center.preferred.preferred", this.taskName);
			jPanel1.add("2.1.right.top.preferred.preferred", this.taskInstructionsLabel);
			jPanel1.add("2.2.center.center.resizable.resizable", new JScrollPane(this.taskInstructions));
			jPanel1.add("3.1.right.top.preferred.preferred", jLabel);
			jPanel1.add("3.2.center.center.resizable.preferred", new JScrollPane(this.processDesc));
			jPanel1.add("4.1.right.top.preferred.preferred", this.commentsLabel);
			jPanel1.add("4.2.center.center.resizable.resizable", new JScrollPane(this.commentsTextArea));
			jPanel1.add("5.1.right.top.preferred.preferred", this.taskResultLabel);
			jPanel1.add("5.2.center.center.preferred.resizable", new JScrollPane(jPanel3));
			if (this.is_secure_task) {
				jPanel1.add("6.1.right.top.preferred.preferred", this.userPassword);
				jPanel1.add("6.2.center.center.preferred.preferred", this.passwordTextField);
			}
			add("bottom.bind.center.top", jPanel2);
			add("bottom.bind", new Separator());
			add("unbound.bind.center.top", jPanel1);
			setState();
			TCComponentActionHandler[] actionHandlers =  this.task.getActionHandlers(TCComponentTask.COMPLETE_ACTION);
			for (int i = 0; i < actionHandlers.length; i++) {
				String actionName = actionHandlers[i].getProperty("object_name");
			     if (actionName.equals("D9_2ndSourceChangeList")) {
				   System.out.println("===========2ndSourceChangeList=================");
				    d9_2ndSourceChangeList=true;
			     }
			}
			System.out.print(actionHandlers);
		} catch (Exception exception) {
			displayEx(exception);
		}
	}

	public void displayEx(Exception paramException) {
		MessageBox messageBox = null;
		if (this.parent != null) {
			messageBox = new MessageBox(this.parent, paramException);
			messageBox.setModal(true);
		} else {
			messageBox = new MessageBox(Utilities.getCurrentFrame(), paramException);
			messageBox.setModal(true);
		}
		messageBox.setVisible(true);
	}

	public void setFocus() {
		if (this.rbTrue.isEnabled()) {
			this.rbTrue.requestFocus();
		} else if (this.parent != null) {
			this.cancelButton.requestFocus();
		}
	}

	public void setState() {
		try {
			String str;
			try {
				str = this.task.getProperty("task_result");
			} catch (TCException tCException) {
				displayEx(tCException);
				return;
			}
			if (str.equals("unableToComplete")) {
				this.unableToCompleteRadioButton.setSelected(true);
			} else if (!checkRadioButtons(str)) {
				this.rbUnset.setSelected(true);
			}
			TCTaskState tCTaskState = this.task.getState();
			if (tCTaskState == TCTaskState.STARTED || tCTaskState == TCTaskState.PENDING) {
				boolean bool = this.task.isValidPerformer();
				TCProperty tCProperty = this.task.getTCProperty("condition_result");
				int i = tCProperty.getIntValue();
				if (i != 2 || !bool) {
					if (this.is_secure_task)
						this.passwordTextField.setEnabled(false);
					this.rbTrue.setEnabled(false);
					this.rbFalse.setEnabled(false);
					this.rbUnset.setEnabled(false);
					this.unableToCompleteRadioButton.setEnabled(false);
					this.okButton.setEnabled(true);
					if (!bool) {
						this.okButton.setVisible(false);
					} else {
						this.okButton.setVisible(true);
						if (this.is_secure_task)
							this.passwordTextField.setEnabled(true);
					}
				} else if (this.task.isValidPerformer()) {
					if (this.is_secure_task)
						this.passwordTextField.setEnabled(true);
					this.rbTrue.setEnabled(true);
					this.rbFalse.setEnabled(true);
					this.rbUnset.setEnabled(true);
					this.unableToCompleteRadioButton.setEnabled(true);
					this.okButton.setEnabled(true);
					this.okButton.setVisible(true);
				}
			} else if (tCTaskState == TCTaskState.FAILED) {
				boolean bool = this.task.isValidPerformer();
				if (this.is_secure_task)
					this.passwordTextField.setEnabled(false);
				this.rbTrue.setEnabled(false);
				this.rbFalse.setEnabled(false);
				this.rbUnset.setEnabled(false);
				this.unableToCompleteRadioButton.setEnabled(false);
				this.unableToCompleteRadioButton.setSelected(true);
				this.okButton.setEnabled(false);
				if (!bool) {
					this.okButton.setVisible(false);
				} else {
					this.okButton.setVisible(true);
				}
				for (int b = 0; b < this.radioButtonList.size(); b++) {
					JRadioButton jRadioButton = (JRadioButton) this.radioButtonList.get(b);
					jRadioButton.setEnabled(false);
				}
			} else {
				if (this.is_secure_task)
					this.passwordTextField.setEnabled(false);
				this.rbTrue.setEnabled(false);
				this.rbFalse.setEnabled(false);
				this.rbUnset.setEnabled(false);
				this.unableToCompleteRadioButton.setEnabled(false);
				this.okButton.setVisible(false);
				for (int b = 0; b < this.radioButtonList.size(); b++) {
					JRadioButton jRadioButton = (JRadioButton) this.radioButtonList.get(b);
					jRadioButton.setEnabled(false);
				}
			}
			this.commentsTextArea.setEditable(!(!this.rbTrue.isEnabled() && !this.rbFalse.isEnabled()
					&& !this.rbUnset.isEnabled() && (!this.unableToCompleteRadioButton.isVisible()
							|| !this.unableToCompleteRadioButton.isEnabled())));
			if (this.parent != null)
				this.cancelButton.setEnabled(true);
		} catch (Exception exception) {
			displayEx(exception);
			return;
		}
	}

	private boolean checkRadioButtons(String paramString) {
		String str = this.registry.getString(paramString);
		for (int b = 0; b < this.radioButtonList.size(); b++) {
			JRadioButton jRadioButton = (JRadioButton) this.radioButtonList.get(b);
			String str1 = jRadioButton.getText();
			if (str1.equalsIgnoreCase(str)) {
				jRadioButton.setSelected(true);
				return true;
			}
		}
		return false;
	}

	public void startOperation(String paramString) {
		if (this.parent != null)
			this.cancelButton.setEnabled(false);
		this.okButton.setEnabled(false);
		revalidate();
		repaint();
	}

	public void endOperation() {
		boolean bool = true;
		if (this.performTask != null) {
			AbstractAIFOperation abstractAIFOperation = this.performTask.getOperation();
			Object object = abstractAIFOperation.getOperationResult();
			if (object != null)
				bool = false;
			abstractAIFOperation.removeOperationListener(this);
			this.performTask = null;
		}
		if (this.parent != null && bool) {
			this.parent.disposeDialog();
		} else {
			setState();
			setFocus();
			revalidate();
			repaint();
		}
	}

	private void startConditionTaskOperation() {
		try {
			Object abstractAIFDialog = this.desktop;
			String str = null;
			if (this.parent != null)
				abstractAIFDialog = this.parent;
			if (this.is_secure_task)
				str = getPassword();
			if (this.resultToSet.contains(","))
				this.resultToSet.replace(",", "\\,");
			if (this.unableToCompleteRadioButton.isSelected()) {
				initiateOperation((Window)abstractAIFDialog, str, this.resultToSet, "FAILURE_OPERATION");
				return;
			}
			if (this.resultToSet.equals("unset")) {
				this.saveTaskOperation = new SaveTaskOperation(this.desktop, new TCComponentTask[] { this.task },
						this.commentsTextArea.getText());
				this.session.queueOperation(this.saveTaskOperation);
				if (this.parent != null)
					this.parent.disposeDialog();
				return;
			}
			this.task.clearCache("task_result");
			this.task.setProperty("task_result", this.resultToSet);
			TCTaskState tCTaskState = this.task.getState();
			if (tCTaskState == TCTaskState.STARTED && !this.resultToSet.equals("unset")) {
				initiateOperation((Window)abstractAIFDialog, str, this.resultToSet, "COMPLETE_OPERATION");
			} else if (this.parent != null) {
				this.parent.disposeDialog();
			}
		} catch (Exception exception) {
			displayEx(exception);
		}
	}

	private void initiateOperation(Window paramWindow, String paramString1, String paramString2, String paramString3) {
		this.performTask = new PerformTaskUtil(paramWindow, this.task, this.commentsTextArea.getText(), paramString1, paramString2, this, paramString3);
		this.performTask.executeOperation();
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
			displayEx(exception);
			return false;
		}
		return true;
	}

	private void setCustomResults(TCComponentTaskTemplate paramTCComponentTaskTemplate) {
		try {
			TCComponentTaskTemplate[] arrayOfTCComponentTaskTemplate = null;
			String str = getCheckCondition(paramTCComponentTaskTemplate, paramTCComponentTaskTemplate.getRoot());
			if (!str.isEmpty()) {
				TCComponentTaskTemplate[] arrayOfTCComponentTaskTemplate1 = paramTCComponentTaskTemplate
						.getSuccessors();
				if (arrayOfTCComponentTaskTemplate1 != null && arrayOfTCComponentTaskTemplate1.length > 0) {
					arrayOfTCComponentTaskTemplate = new TCComponentTaskTemplate[arrayOfTCComponentTaskTemplate1.length
							+ 1];
					for (int b1 = 0; b1 < arrayOfTCComponentTaskTemplate1.length; b1++)
						arrayOfTCComponentTaskTemplate[b1] = arrayOfTCComponentTaskTemplate1[b1];
				} else {
					arrayOfTCComponentTaskTemplate = new TCComponentTaskTemplate[1];
				}
				if (arrayOfTCComponentTaskTemplate.length > 0)
					arrayOfTCComponentTaskTemplate[arrayOfTCComponentTaskTemplate.length
							- 1] = paramTCComponentTaskTemplate.getRoot();
			} else {
				arrayOfTCComponentTaskTemplate = paramTCComponentTaskTemplate.getSuccessors();
			}
			for (int b = 0; b < arrayOfTCComponentTaskTemplate.length; b++)
				setCustomResultArrayList(paramTCComponentTaskTemplate, arrayOfTCComponentTaskTemplate[b]);
		} catch (Exception exception) {
			Debug.println(exception.getClass().getName(), exception);
		}
		this.customResultArrayList.add("unset");
		this.resultComboBox = new JComboBox(this.customResultArrayList.toArray());
	}

	private void setCustomResultArrayList(TCComponentTaskTemplate paramTCComponentTaskTemplate1, TCComponentTaskTemplate paramTCComponentTaskTemplate2) {
		String str = getCheckCondition(paramTCComponentTaskTemplate1, paramTCComponentTaskTemplate2);
		str = str.replace("\\,", "\\|");
		String[] arrayOfString = str.split(",");
		for (int b = 0; b < arrayOfString.length; b++) {
			String str1 = arrayOfString[b];
			str1 = str1.replace("=", "");
			if (!this.customResultArrayList.contains(str1)) {
				str1 = str1.replace("\\|", ",");
				this.customResultArrayList.add(str1);
			}
		}
		Collections.sort(this.customResultArrayList);
	}

	public String getCheckCondition(TCComponentTaskTemplate paramTCComponentTaskTemplate1, TCComponentTaskTemplate paramTCComponentTaskTemplate2) {
		TCComponentTaskTemplate tCComponentTaskTemplate = null;
		TCComponentRule[] arrayOfTCComponentRule = null;
		TCComponentRuleHandler[] arrayOfTCComponentRuleHandler = null;
		boolean bool = false;
		String str = "";
		try {
			tCComponentTaskTemplate = paramTCComponentTaskTemplate2;
			if (tCComponentTaskTemplate == null)
				return "";
			for (int b = 0; b < this.EPMActions.length; b++) {
				arrayOfTCComponentRule = tCComponentTaskTemplate.getRules(b);
				for (int b1 = 0; b1 < arrayOfTCComponentRule.length; b1++) {
					arrayOfTCComponentRuleHandler = arrayOfTCComponentRule[b1].getRuleHandlers();
					for (int b2 = 0; b2 < arrayOfTCComponentRuleHandler.length; b2++) {
						String[] arrayOfString1 = null;
						String[] arrayOfString2 = arrayOfTCComponentRuleHandler[b2].getArguments();
						if (arrayOfTCComponentRuleHandler[b2].toString().equals("EPM-check-condition")) {
							if (arrayOfString2.length > 0)
								arrayOfString1 = extractTaskName(arrayOfString2);
							if (arrayOfString1 != null && arrayOfString1.length > 0 && arrayOfString1[0]
									.equalsIgnoreCase(paramTCComponentTaskTemplate1.getRealName())) {
								String str1 = String.valueOf(arrayOfString1[0]) + "=" + arrayOfString1[1];
								str = extractResults(str1, arrayOfString1[0]);
								bool = true;
								break;
							}
						}
					}
					if (bool)
						break;
				}
				if (bool)
					break;
			}
		} catch (TCException tCException) {
			MessageBox messageBox = new MessageBox(tCException);
			messageBox.setVisible(true);
			return "";
		}
		return str;
	}

	private String[] extractTaskName(String[] paramArrayOfString) {
		if (paramArrayOfString == null)
			return null;
		String[] arrayOfString = new String[2];
		for (int b = 0; b < paramArrayOfString.length; b++) {
			String[] arrayOfString1 = paramArrayOfString[b].split("=", 2);
			if (arrayOfString1[0].equalsIgnoreCase("-source_task")) {
				arrayOfString[0] = arrayOfString1[1].trim();
			} else if (arrayOfString1[0].equalsIgnoreCase("-decision")) {
				arrayOfString[1] = arrayOfString1[1].trim();
			}
		}
		return arrayOfString;
	}

	private String extractResults(String paramString1, String paramString2) {
		String str = paramString1.replaceFirst(paramString2, "");
		int i = paramString2.length() + str.length();
		int j = i - paramString1.length();
		if (j > 0)
			str = str.substring(j);
		if (str.startsWith(","))
			str = str.replaceFirst(",", "");
		return str.trim();
	}

	public String getPassword() {
		return new String(this.passwordTextField.getPassword());
	}

	public void processComponentEvents(final AIFComponentEvent[] componentEvents) {
		String str = this.registry.getString("refreshDisplayMessage");
		this.session.setReadyStatus();
		this.session.queueOperation(new AbstractAIFOperation(str) {
			public void executeOperation() {
				processComponentEventsRequest(componentEvents);
			}
		});
	}

	private void processComponentEventsRequest(AIFComponentEvent[] paramArrayOfAIFComponentEvent) {
		try {
			if (this.task != null) {
				AIFComponentEvent[] arrayOfAIFComponentEvent;
				int i = (arrayOfAIFComponentEvent = paramArrayOfAIFComponentEvent).length;
				for (int b = 0; b < i; b++) {
					AIFComponentEvent aIFComponentEvent = arrayOfAIFComponentEvent[b];
					InterfaceAIFComponent interfaceAIFComponent = aIFComponentEvent.getComponent();
					if (interfaceAIFComponent instanceof TCComponentTask
							&& aIFComponentEvent instanceof com.teamcenter.rac.aif.kernel.AIFComponentChangeEvent
							&& interfaceAIFComponent.equals(this.task)) {
						updateConditionTaskPanel(this.task);
						revalidate();
						repaint();
						break;
					}
				}
			}
		} catch (Exception exception) {
			MessageBox.post(exception);
		}
	}

	private void updateConditionTaskPanel(TCComponentTask paramTCComponentTask) throws TCException {
		String str1 = paramTCComponentTask.getName();
		String str2 = paramTCComponentTask.getStringProperty("comments");
		this.taskInstructions.setText(paramTCComponentTask.getInstructions());
		this.taskName.setText(str1);
		this.commentsTextArea.setText(str2);
		setState();
	}

	public void closeSignaled() {
		detachListeners();
	}

	protected void detachListeners() {
		this.session.removeAIFComponentEventListener(this);
	}
	
	// recompile 20220323143000 : START
	private void sendMail() {
		try {			
			if (task != null) {				
				TCComponent[] tcCompArr = task.getAttachments(TCAttachmentScope.GLOBAL, 1);
				for (TCComponent tcComponent : tcCompArr) {
					TCComponentItemRevision itemRev = null;

					if (tcComponent instanceof TCComponentItem) {
						itemRev = ((TCComponentItem) tcComponent).getLatestItemRevision();
					}
					if (tcComponent instanceof TCComponentItemRevision) {
						itemRev = (TCComponentItemRevision) tcComponent;
					}
					
					if (null == itemRev) 
						continue;
					
					Map<String, List<List<String>>> pubMailMapForMore = TCUtil.parseLstToMap(itemRev);
					
					List<String> projectLst = null;
					List<String> itemLst = null;
					
					Map<String, List<String>> pubMailMapForSimple = new LinkedHashMap<String, List<String>>();
					
					for (Map.Entry<String, List<List<String>>> entry : pubMailMapForMore.entrySet()) {
						List<List<String>> valueLst = entry.getValue();
						if (3 == valueLst.size()) {
							projectLst = valueLst.get(0);
							itemLst = valueLst.get(1);
							
							pubMailMapForSimple.put(entry.getKey(), valueLst.get(2));
						}
					}
						
					String[] mailContentArr = new String[12];
					mailContentArr[0] = registry.getString("mailConent1");
					mailContentArr[1] = registry.getString("mailConent2");
					mailContentArr[2] = registry.getString("mailConent3");
					mailContentArr[3] = registry.getString("mailConent4");
					mailContentArr[4] = registry.getString("mailConent5");
					mailContentArr[5] = registry.getString("mailConent6");
					mailContentArr[6] = registry.getString("mailConent7");
					mailContentArr[7] = registry.getString("mailConent8");
					mailContentArr[8] = registry.getString("mailConent9");
					mailContentArr[9] = registry.getString("mailConent10");
					mailContentArr[10] = registry.getString("mailConent11");
					mailContentArr[11] = registry.getString("mailConent12");
					
					String[] notifyContentArr = new String[12];
					notifyContentArr[0] = registry.getString("mailConent1.notify");
					notifyContentArr[1] = registry.getString("mailConent2.notify");
					notifyContentArr[2] = registry.getString("mailConent3.notify");
					notifyContentArr[3] = registry.getString("mailConent4.notify");
					notifyContentArr[4] = registry.getString("mailConent5.notify");
					notifyContentArr[5] = registry.getString("mailConent6.notify");
					notifyContentArr[6] = registry.getString("mailConent7.notify");
					notifyContentArr[7] = registry.getString("mailConent8.notify");
					notifyContentArr[8] = registry.getString("mailConent9.notify");
					notifyContentArr[9] = registry.getString("mailConent10.notify");
					notifyContentArr[10] = registry.getString("mailConent11.notify");
					notifyContentArr[11] = registry.getString("mailConent12.notify");
					
					String[] finishContentArr = new String[9];
					finishContentArr[0] = registry.getString("mailConent1.finish");
					finishContentArr[1] = registry.getString("mailConent2.finish");
					finishContentArr[2] = registry.getString("mailConent3.finish");
					finishContentArr[3] = registry.getString("mailConent4.finish");
					finishContentArr[4] = registry.getString("mailConent5.finish");
					finishContentArr[5] = registry.getString("mailConent6.finish");
					finishContentArr[6] = registry.getString("mailConent7.finish");
					finishContentArr[7] = registry.getString("mailConent8.finish");
					finishContentArr[8] = registry.getString("mailConent9.finish");
					
					Map<String, String> nodeMap = getTaskNode(task);
					List<TCComponent> taskList = getTaskList(task);
					if(TCUtil.noSendMail(nodeMap, task)) return;
					List<String> tempProjectLst = projectLst;
					List<String> tempItemLst = itemLst;
					new Thread(new Runnable() {
						public void run() {
							try {
								TCUtil.sendMailForApprove(
										registry.getString("actionLog.title"), 
										mailContentArr, 
										notifyContentArr,
										finishContentArr,
										nodeMap, 
										taskList,
										tempProjectLst, 
										tempItemLst, 
										null, 
										null, 
										pubMailMapForSimple);	
							} catch (Exception e) {
								e.printStackTrace();
							}						
						}

					}).start();
				}																			
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	private Map<String, String> getTaskNode(TCComponentTask tcCompTask) {
		Map<String, String> retMap = new LinkedHashMap<String, String>();
		
		try {
			if (tcCompTask != null) {	
				TCComponent[] tcComponentArr = tcCompTask.getReferenceListProperty("successors");
				for (TCComponent tcComponentTask : tcComponentArr) {
					if (tcComponentTask instanceof TCComponentTask) {						
						List<TCComponent> nextNodeLst = new ArrayList<TCComponent>();
						String taskType = tcComponentTask.getProperty("task_type");
						if ("EPMOrTask".equals(taskType)) {
							nextNodeLst.clear();
							getNextNode(tcComponentTask.getReferenceListProperty("successors"), nextNodeLst);														
						} else {
							nextNodeLst.clear();
							nextNodeLst.add(tcComponentTask);							
						}	
						
						if (nextNodeLst != null && nextNodeLst.size() > 0) {
							for (TCComponent tcCompNextNode : nextNodeLst) {
								String nextNodeTaskType = tcCompNextNode.getProperty("task_type");
								if (this.resultToSet.equals(tcCompNextNode.getProperty("object_name"))) {
									retMap.put(tcCompNextNode.getProperty("object_name"), nextNodeTaskType);
								} else if (this.resultToSet.startsWith("To ")) {
									String tempStr = this.resultToSet.substring(3);
									if (tempStr.equals(tcCompNextNode.getProperty("object_name"))) {
										retMap.put(tcCompNextNode.getProperty("object_name"), nextNodeTaskType);
									}
								} else if (this.resultToSet.startsWith("Return to ")) {
									String tempStr = this.resultToSet.substring(10);
									if (tempStr.equals(tcCompNextNode.getProperty("object_name"))) {
										retMap.put(tcCompNextNode.getProperty("object_name"), nextNodeTaskType);
									}
								}
							}
						}
					}
				}								
			}
								
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return retMap;
	}
	
	private void getNextNode(TCComponent[] tcComponentArr, Map<String, String> nextNodeMap) {		
		try {
			for (TCComponent tcComponentTask : tcComponentArr) {
				String taskType = tcComponentTask.getProperty("task_type");
				if ("EPMOrTask".equals(taskType)) {
					getNextNode(tcComponentTask.getReferenceListProperty("successors"), nextNodeMap);
				} else if (!"EPMOrTask".equals(taskType)) {
					nextNodeMap.put(tcComponentTask.getProperty("object_name"), taskType);
				}
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getNextNode(TCComponent[] tcComponentArr, List<TCComponent> nextNodeLst) {		
		try {
			for (TCComponent tcComponentTask : tcComponentArr) {
				String taskType = tcComponentTask.getProperty("task_type");
				if ("EPMOrTask".equals(taskType)) {
					getNextNode(tcComponentTask.getReferenceListProperty("successors"), nextNodeLst);
				} else if (!"EPMOrTask".equals(taskType)) {
					nextNodeLst.add(tcComponentTask);
				}
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 20220323143000 : END
	
	// 202309060859 : START
	private List<TCComponent> getTaskList(TCComponentTask tcCompTask) {
		List<TCComponent> list = new ArrayList<TCComponent>();		
		
		try {
			if (tcCompTask != null) {				
				TCComponent[] tcComponentArr = tcCompTask.getReferenceListProperty("successors");
				for (TCComponent tcComponentTask : tcComponentArr) {
					if (tcComponentTask instanceof TCComponentTask) {						
						String taskType = tcComponentTask.getProperty("task_type");
						if ("EPMOrTask".equals(taskType)) {
							getNextTask(tcComponentTask.getReferenceListProperty("successors"), list);														
						} else {
							list.add(tcComponentTask);							
						}						
					}
				}								
			}
								
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	private void getNextTask(TCComponent[] tcComponentArr, List<TCComponent> list) {		
		try {
			for (TCComponent tcComponentTask : tcComponentArr) {
				String taskType = tcComponentTask.getProperty("task_type");
				if ("EPMOrTask".equals(taskType)) {
					getNextNode(tcComponentTask.getReferenceListProperty("successors"), list);
				} else if (!"EPMOrTask".equals(taskType)) {
					list.add(tcComponentTask);
				}
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 202309060910 : END
}
