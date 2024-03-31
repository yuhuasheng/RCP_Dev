package com.teamcenter.rac.workflow.commands.dotask;

import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.hh.foxconn.digbom.action.CheckDigBomAction;
import com.hh.foxconn.l5ecncheck.action.L5ECNCheckAction;
import com.hh.fx.rewrite.util.ProgressBarThread;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.InterfaceAIFOperationListener;
import com.teamcenter.rac.aif.kernel.AIFComponentChangeEvent;
import com.teamcenter.rac.aif.kernel.AIFComponentEvent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponentEventListener;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentActionHandler;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTaskState;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.FilterDocument;
import com.teamcenter.rac.util.InterfaceSignalOnClose;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Painter;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.Separator;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.iTextArea;
import com.teamcenter.rac.workflow.commands.digitalsign.PerformTaskUtil;

public class UserDoTaskPanel extends JPanel
	implements InterfaceAIFOperationListener, InterfaceAIFComponentEventListener, InterfaceSignalOnClose{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea taskName;
	  private JTextArea taskInstructions;
	  private iTextArea processDesc;
	  private String oldDesc;
	  private String oldComment;
	  ButtonGroup completeButtonGroup;
	  private JRadioButton completeRadioButton;
	  private JRadioButton unableToCompleteRadioButton;
	  private JTextArea commentsTextArea;
	  private JLabel commentsLabel;
	  private boolean failurePathPresent = false;
	  private JButton okButton;
	  private JButton cancelButton;
	  private PerformTaskUtil performTask = null;
	  private TCComponentTask task;
	  private TCSession session;
	  private Registry registry;
	  private AIFDesktop desktop = null;
	  private boolean is_secure_task = false;
	  protected AbstractAIFDialog parent;
	  protected JLabel userPassword;
	  protected JPasswordField passwordTextField;
	  public static final String PERFORM_DO_TASK_ICON_REG_KEY = "performDoTask.ICON";
	  public static final String TASK_NAME_LABEL_REG_KEY = "taskNameLabel";
	  public static final String TASK_INSTRUCTIONS_LABEL_REG_KEY = "taskInstructionsLabel";
	  public static final String DONE_BOX_LABEL_REG_KEY = "doneLabel";
	  public static final String REFRESH_DISPLAY_REG_KEY = "refreshDisplayMessage";
	  public static final String COMPLETE_KEY = "completeLabel";
	  public static final String UNABLE_COMPLETE_KEY = "unableToCompleteLabel";
	  public static final String PROCESS_DESC_LABEL_REG_KEY = "processDescLabel";
	  public static final int TASK_COMMENT_SIZE = 4000;
	  
	  private boolean isActionHandler = true;
 
	
	
	 public UserDoTaskPanel(AIFDesktop paramAIFDesktop, JPanel paramJPanel, TCComponentTask paramTCComponentTask)
	  {
	    this(paramAIFDesktop, (AbstractAIFDialog)null, paramTCComponentTask);
	  }

	 public UserDoTaskPanel(AIFDesktop paramAIFDesktop, AbstractAIFDialog paramAbstractAIFDialog, TCComponentTask paramTCComponentTask)
	  {
		super(new VerticalLayout(5, 2, 2, 2, 2));
	    parent = paramAbstractAIFDialog;
	    session = paramTCComponentTask.getSession();
	    registry = Registry.getRegistry(this);
	    task = paramTCComponentTask;
	    desktop = paramAIFDesktop;
	    try
	    {
	      failurePathPresent = task.getLogicalProperty("has_failure_paths");
	    }
	    catch (TCException localTCException) {}
	    initPanel();
	    session.addAIFComponentEventListener(this);
	  }
	
	 private void initPanel()
	  {
	    try
	    {
	      JPanel localJPanel1 = new JPanel(new PropertyLayout());
	      JPanel localJPanel2 = new JPanel(new ButtonLayout());
	      JLabel localJLabel1 = new JLabel(registry.getImageIcon("performDoTask.ICON"), 0);
	      JLabel localJLabel2 = new JLabel(registry.getString("taskNameLabel"));
	      String str1 = task.getName();
	      taskName = new JTextArea(str1, 1, 50);
	      taskName.setEditable(false);
	      taskName.setLineWrap(true);
	      taskName.setBackground(getBackground());
	      commentsLabel = new JLabel(registry.getString("commentsLabel"));
	      String str2 = TCSession.getServerEncodingName(session);
	      commentsTextArea = new JTextArea(new FilterDocument(4000, str2), "", 3, 50);
	      JLabel localJLabel3 = new JLabel(registry.getString("taskInstructionsLabel"));
	      taskInstructions = new JTextArea(task.getInstructions(), 3, 50);
	      taskInstructions.setEditable(false);
	      taskInstructions.setLineWrap(true);
	      JLabel localJLabel4 = new JLabel(registry.getString("processDescLabel"));
	      oldDesc = task.getProcess().getInstructions();
	      processDesc = new iTextArea(oldDesc, 2, 50);
	      processDesc.setLengthLimit(240);
	      processDesc.setLineWrap(true);
	      processDesc.setWrapStyleWord(true);
	      if (task.getProcess().okToModify()) {
	        processDesc.setEditable(true);
	      } else {
	        processDesc.setEditable(false);
	      }
	      processDesc.addKeyListener(new KeyAdapter()
	      {
	        @Override
			public void keyReleased(KeyEvent paramAnonymousKeyEvent)
	        {
	          okButton.setEnabled(true);
	        }
	      });
	      taskInstructions.setWrapStyleWord(true);
	      completeButtonGroup = new ButtonGroup();
	      JPanel localJPanel3 = new JPanel();
	      JLabel localJLabel5 = new JLabel(registry.getString("doneLabel"));
	      completeRadioButton = new JRadioButton(registry.getString("completeLabel"));
	      completeRadioButton.addActionListener(new ActionListener()
	      {
	        @Override
			public void actionPerformed(ActionEvent paramAnonymousActionEvent)
	        {
	          okButton.setEnabled(true);
	          setResult(true);
	        }
	      });
	      unableToCompleteRadioButton = new JRadioButton(registry.getString("unableToCompleteLabel"));
	      unableToCompleteRadioButton.setVisible(failurePathPresent);
	      unableToCompleteRadioButton.addActionListener(new ActionListener()
	      {
	        @Override
			public void actionPerformed(ActionEvent paramAnonymousActionEvent)
	        {
	          okButton.setEnabled(true);
	          setResult(false);
	        }
	      });
	      localJPanel3.add(completeRadioButton);
	      localJPanel3.add(unableToCompleteRadioButton);
	      completeButtonGroup.add(completeRadioButton);
	      completeButtonGroup.add(unableToCompleteRadioButton);
	      if (parent != null)
	      {
	        okButton = new JButton(registry.getString("ok"));
	        okButton.setMnemonic(registry.getString("ok.MNEMONIC").charAt(0));
	      }
	      else
	      {
	        okButton = new JButton(registry.getString("apply"));
	        okButton.setMnemonic(registry.getString("apply.MNEMONIC").charAt(0));
	      }
	      okButton.addActionListener(new ActionListener()
	      {
	        @Override
			public void actionPerformed(ActionEvent paramAnonymousActionEvent)
	        {
	        	new Thread(new Runnable() {					
					@Override
					public void run() {
						checkAction();
					}
				}).start();
	        }			
	      });
	      okButton.setEnabled(false);
	      okButton.setVisible(false);
	      localJPanel2.add(okButton);
	      if (parent != null)
	      {
	        cancelButton = new JButton(registry.getString("cancel"));
	        cancelButton.setMnemonic(registry.getString("cancel.MNEMONIC").charAt(0));
	        cancelButton.addActionListener(new ActionListener()
	        {
	          @Override
			public void actionPerformed(ActionEvent paramAnonymousActionEvent)
	          {
	            parent.disposeDialog();
	          }
	        });
	        cancelButton.setEnabled(true);
	        cancelButton.setVisible(true);
	        localJPanel2.add(cancelButton);
	      }
	      oldComment = task.getProperty("comments");
	      commentsTextArea.setText(oldComment);
	      try
	      {
	        is_secure_task = task.isSecureTask();
	        if (is_secure_task)
	        {
	          userPassword = new JLabel(registry.getString("passwordLabel"));
	          passwordTextField = new JPasswordField(20)
	          {
	            @Override
				public void paint(Graphics paramAnonymousGraphics)
	            {
	              super.paint(paramAnonymousGraphics);
	              Painter.paintIsRequired(this, paramAnonymousGraphics);
	            }
	          };
	        }
	      }
	      catch (TCException localTCException) {}
	      add("top.nobind.left", localJLabel1);
	      add("top.bind", new Separator());
	      localJPanel1.add("1.1.right.top.preferred.preferred", localJLabel2);
	      localJPanel1.add("1.2.center.center.resizable.preferred", new JScrollPane(this.taskName));
	      localJPanel1.add("2.1.right.top.preferred.preferred", localJLabel3);
	      localJPanel1.add("2.2.center.center.resizable.resizable", new JScrollPane(this.taskInstructions));
	      localJPanel1.add("3.1.right.top.preferred.preferred", localJLabel4);
	      localJPanel1.add("3.2.center.center.resizable.preferred", new JScrollPane(this.processDesc));
	      localJPanel1.add("4.1.right.top.preferred.preferred", this.commentsLabel);
	      localJPanel1.add("4.2.center.center.resizable.resizable", new JScrollPane(this.commentsTextArea));
	      localJPanel1.add("5.1.right.top.preferred.preferred", localJLabel5);
	      localJPanel1.add("5.2.center.center.preferred.preferred", localJPanel3);
	      if (this.is_secure_task)
	      {
	        localJPanel1.add("6.1.right.top.preferred.preferred", this.userPassword);
	        localJPanel1.add("6.2.center.center.preferred.preferred", this.passwordTextField);
	      }
	      add("bottom.bind.center.top", localJPanel2);
	      add("bottom.bind", new Separator());
	      add("unbound.bind.center.top", localJPanel1);
	      setState();
	    }
	    catch (Exception localException)
	    {
	      displayEx(localException);
	      return;
	    }
	  }
	
	 public void displayEx(Exception paramException)
	  {
	    MessageBox localMessageBox = null;
	    if (parent != null)
	    {
	      localMessageBox = new MessageBox(parent, paramException);
	      localMessageBox.setModal(true);
	    }
	    else
	    {
	      localMessageBox = new MessageBox(Utilities.getCurrentFrame(), paramException);
	      localMessageBox.setModal(true);
	    }
	    localMessageBox.setVisible(true);
	  }
	  
	  public void setFocus()
	  {
	    if (completeRadioButton.isEnabled()) {
	      completeRadioButton.requestFocus();
	    } else if (unableToCompleteRadioButton.isEnabled()) {
	      unableToCompleteRadioButton.requestFocus();
	    } else if (parent != null) {
	      cancelButton.requestFocus();
	    }
	  }
	  
	  public void setState()
	  {
	    try
	    {
	      TCTaskState localTCTaskState = task.getState();
	      commentsTextArea.setEditable(false);
	      if (localTCTaskState == TCTaskState.STARTED)
	      {
	        completeButtonGroup.remove(completeRadioButton);
	        completeButtonGroup.remove(unableToCompleteRadioButton);
	        completeRadioButton.setSelected(false);
	        unableToCompleteRadioButton.setSelected(false);
	        completeButtonGroup.add(completeRadioButton);
	        completeButtonGroup.add(unableToCompleteRadioButton);
	        commentsTextArea.setEditable(true);
	        TCProperty localTCProperty = task.getTCProperty("task_result");
	        String str = localTCProperty.getStringValue();
	        if (str.equals("Completed"))
	        {
	          completeRadioButton.setSelected(true);
	          completeRadioButton.setEnabled(true);
	          unableToCompleteRadioButton.setEnabled(true);
	          okButton.setEnabled(true);
	          okButton.setVisible(true);
	          if (task.isValidPerformer())
	          {
	            if (is_secure_task) {
	              passwordTextField.setEnabled(true);
	            }
	          }
	          else if (is_secure_task) {
	            passwordTextField.setEnabled(false);
	          }
	        }
	        else if (str.equals("UnableToComplete"))
	        {
	          completeRadioButton.setSelected(false);
	          completeRadioButton.setEnabled(true);
	          unableToCompleteRadioButton.setSelected(true);
	          unableToCompleteRadioButton.setEnabled(true);
	          okButton.setEnabled(true);
	          okButton.setVisible(true);
	          if (task.isValidPerformer())
	          {
	            if (is_secure_task) {
	              passwordTextField.setEnabled(true);
	            }
	          }
	          else if (is_secure_task) {
	            passwordTextField.setEnabled(false);
	          }
	        }
	        else if (task.isValidPerformer())
	        {
	          if (is_secure_task) {
	            passwordTextField.setEnabled(true);
	          }
	          completeRadioButton.setEnabled(true);
	          unableToCompleteRadioButton.setEnabled(true);
	          okButton.setEnabled(false);
	          okButton.setVisible(true);
	        }
	        else
	        {
	          if (is_secure_task) {
	            passwordTextField.setEnabled(false);
	          }
	          completeRadioButton.setEnabled(false);
	          unableToCompleteRadioButton.setEnabled(false);
	          okButton.setVisible(false);
	        }
	      }
	      else if (localTCTaskState == TCTaskState.FAILED)
	      {
	        completeRadioButton.setSelected(false);
	        unableToCompleteRadioButton.setSelected(true);
	        completeRadioButton.setSelected(false);
	        completeRadioButton.setEnabled(false);
	        unableToCompleteRadioButton.setEnabled(false);
	        unableToCompleteRadioButton.setSelected(true);
	        okButton.setEnabled(false);
	        okButton.setVisible(true);
	        if (is_secure_task) {
	          passwordTextField.setEnabled(false);
	        }
	      }
	      else if (localTCTaskState == TCTaskState.COMPLETED)
	      {
	        completeRadioButton.setEnabled(false);
	        unableToCompleteRadioButton.setEnabled(false);
	        okButton.setEnabled(false);
	        okButton.setVisible(true);
	      }
	      else
	      {
	        if (is_secure_task) {
	          passwordTextField.setEnabled(false);
	        }
	        completeRadioButton.setEnabled(false);
	        unableToCompleteRadioButton.setEnabled(false);
	        okButton.setVisible(false);
	        if (localTCTaskState == TCTaskState.COMPLETED) {
	          completeRadioButton.setSelected(true);
	        } else {
	          completeRadioButton.setSelected(false);
	        }
	      }
	      if (parent != null) {
	        cancelButton.setEnabled(true);
	      }
	    }
	    catch (Exception localException)
	    {
	      displayEx(localException);
	      return;
	    }
	  }
	  
	  @Override
	public void startOperation(String paramString)
	  {
	    if (parent != null) {
	      cancelButton.setEnabled(false);
	    }
	    okButton.setEnabled(false);
	    commentsTextArea.setEditable(false);
	    revalidate();
	    repaint();
	  }
	  
	  @Override
	public void endOperation()
	  {
	    int i = 1;
	    if (performTask != null)
	    {
	      AbstractAIFOperation localAbstractAIFOperation = performTask.getOperation();
	      Object localObject = localAbstractAIFOperation.getOperationResult();
	      if (localObject != null) {
	        i = 0;
	      }
	      localAbstractAIFOperation.removeOperationListener(this);
	      performTask = null;
	    }
	    if ((parent != null) && (i != 0))
	    {
	      parent.disposeDialog();
	    }
	    else
	    {
	      setState();
	      setFocus();
	      revalidate();
	      repaint();
	    }
	  }
	  
	  private void startDoTaskOperation()
	  {
	    Object localObject = desktop;
	    String str = null;
	    if (parent != null) {
	      localObject = parent;
	    }
	    if (is_secure_task) {
	      str = getPassword();
	    }
				    
	    if (completeRadioButton.isSelected()) {
	      initiateOperation((Window)localObject, str, "Completed", "COMPLETE_OPERATION");
	    } else if (unableToCompleteRadioButton.isSelected()) {
	      initiateOperation((Window)localObject, str, "UnableToComplete", "FAILURE_OPERATION");
	    } else if (parent != null) {
	      parent.disposeDialog();
	    }
	  }
	  

	private void initiateOperation(Window paramWindow, String paramString1, String paramString2, String paramString3)
	  {
	    performTask = new PerformTaskUtil(paramWindow, task, commentsTextArea.getText(), paramString1, paramString2, this, paramString3);
	    performTask.executeOperation();
	  }
	  
	  private boolean updateProcessDescription()
	  {
	    try
	    {
	      String str = processDesc.getText();
	      if (oldDesc == null)
	      {
	        if (str != null) {
	          task.getProcess().setStringProperty("object_desc", str);
	        }
	      }
	      else if (!oldDesc.equals(str)) {
	        task.getProcess().setStringProperty("object_desc", str);
	      }
	    }
	    catch (Exception localException)
	    {
	      displayEx(localException);
	      return false;
	    }
	    return true;
	  }
	  
	  public String getPassword()
	  {
	    return new String(passwordTextField.getPassword());
	  }
	  
	  @Override
	public void processComponentEvents(final AIFComponentEvent[] paramArrayOfAIFComponentEvent)
	  {
	    String str = registry.getString("refreshDisplayMessage");
	    session.setReadyStatus();
	    session.queueOperation(new AbstractAIFOperation(str)
	    {
	      @Override
		public void executeOperation()
	        throws Exception
	      {
	    	  UserDoTaskPanel.this.processComponentEventsRequest(paramArrayOfAIFComponentEvent);
	      }
	    });
	  }
	  
	  private void processComponentEventsRequest(AIFComponentEvent[] paramArrayOfAIFComponentEvent)
	  {
	    try
	    {
	      if (task != null)
	      {
	        AIFComponentEvent[] arrayOfAIFComponentEvent;
	        int j = (arrayOfAIFComponentEvent = paramArrayOfAIFComponentEvent).length;
	        for (int i = 0; i < j; i++)
	        {
	          AIFComponentEvent localAIFComponentEvent = arrayOfAIFComponentEvent[i];
	          InterfaceAIFComponent localInterfaceAIFComponent = localAIFComponentEvent.getComponent();
	          if (((localInterfaceAIFComponent instanceof TCComponentTask)) && ((localAIFComponentEvent instanceof AIFComponentChangeEvent)) && (localInterfaceAIFComponent.equals(task)))
	          {
	            updateDoTaskPanel(task);
	            revalidate();
	            repaint();
	            break;
	          }
	        }
	      }
	    }
	    catch (Exception localException)
	    {
	      MessageBox.post(localException);
	    }
	  }
	  
	  private void updateDoTaskPanel(TCComponentTask paramTCComponentTask)
	    throws TCException
	  {
	    String str1 = paramTCComponentTask.getName();
	    String str2 = paramTCComponentTask.getStringProperty("comments");
	    taskInstructions.setText(paramTCComponentTask.getInstructions());
	    taskName.setText(str1);
	    if (!oldComment.equals(str2))
	    {
	      oldComment = str2;
	      commentsTextArea.setText(oldComment);
	    }
	  }
	  
	  @Override
	public void closeSignaled()
	  {
	    detachListeners();
	  }
	  
	  protected void detachListeners()
	  {
	    session.removeAIFComponentEventListener(this);
	  }
	  
	  public boolean isFailurePathPresent()
	  {
	    return failurePathPresent;
	  }
	  
	  public void setFailurePathPresent(boolean paramBoolean)
	  {
	    failurePathPresent = paramBoolean;
	  }
	  
	  public void setResult(boolean paramBoolean)
	  {
	    
	      String str = null;
	      if (paramBoolean) {
	        str = "Completed";
	      } else {
	        str = "UnableToComplete";
	      }
	      try {
			task.setStringProperty("task_result", str);
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	  }

	  //搭建BOM
	  public static TCComponentBOMLine createBOMLine(TCSession session,TCComponentItemRevision itemRev) throws Exception{
			TCComponentRevisionRuleType revisionType = (TCComponentRevisionRuleType) session.getTypeComponent("RevisionRule");
			TCComponentRevisionRule  revisionRule = revisionType.getDefaultRule();
			TCComponentBOMWindowType bomWindowType = (TCComponentBOMWindowType)session.getTypeComponent("BOMWindow");
			TCComponentBOMWindow bomWindow = bomWindowType.create(revisionRule);
			TCComponentBOMLine topBOMLine = bomWindow.setWindowTopLine(itemRev.getItem(), itemRev, null, null);
			return topBOMLine;		
		}
	  
	  private void checkAction() {
			 if (!UserDoTaskPanel.this.updateProcessDescription()) {
		            return;
		          }
		          try {
		        	  boolean flag = false;
		        	  boolean isL5ECNCheck = false;
		        	  TCComponentActionHandler[] actionHandlers = task.getActionHandlers(TCComponentTask.START_ACTION);
		        	  if(actionHandlers.length>0){
		        		  for (TCComponentActionHandler tcComponentActionHandler : actionHandlers) {
		        			  String actionName = tcComponentActionHandler.getProperty("object_name");
		        			  if("FX_CheckDIGBOM".equals(actionName)){
		        				  flag = true;
		        				  break;
		        			  }else if("FX_L5ECNCheck".equals(actionName)){
		        				  isL5ECNCheck = true;
		        				  break;
		        			  }
						}
		        	  }
		        	  System.out.println("flag=="+flag);
		        	  if(flag){        		 
								try {
									TCComponentTask rootTask = task.getRoot();
									TCComponent[] components = rootTask.getRelatedComponents("root_target_attachments");
									TCComponentBOMLine bomLine = null;
									if(components.length>0){
										for (TCComponent component : components) {
											if(component instanceof TCComponentItemRevision){
												TCComponentItemRevision itemRev = (TCComponentItemRevision)component;
												try {
													bomLine = createBOMLine(session, itemRev);
													break;
												} catch (Exception e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
											}
										}
									}
									if(bomLine==null){
										MessageBox.post("流程目标中未找到符合要求的BOMLine，请检查",
												"警告", MessageBox.WARNING);
										return;
									}
									ProgressBarThread progressBar = new ProgressBarThread("提示", "正在检查DIG BOM");
									progressBar.start();
									
									CheckDigBomAction checkAction = new CheckDigBomAction(AIFUtility.getCurrentApplication(), null, "",bomLine);
									Boolean isRequire = checkAction.startCheck();
									System.out.println("isRequire=="+isRequire);
									if(!isRequire){
										progressBar.stopBar();
										MessageBox.post("缺少必填项，请检查",
												"警告", MessageBox.WARNING);
										return;
									}else{
										progressBar.stopBar();
									}
									
								} catch (TCException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
		        	  }else if(isL5ECNCheck){
		        		  TCComponentTask rootTask = task.getRoot();
		        		  TCComponent[] components = rootTask.getRelatedComponents("root_target_attachments");
		        		  if(components.length>0){
								for (TCComponent component : components) {
									if("FX8_L5ECN".equals(component.getType())){
										ProgressBarThread progressBar = new ProgressBarThread("提示", "正在检查必填项");
										progressBar.start();
										L5ECNCheckAction checkAction = new L5ECNCheckAction(AIFUtility.getCurrentApplication(), null,(TCComponentItem)component);
										Boolean check = checkAction.startCheck();
										System.out.println("check=="+check);
										if(!check){		
											progressBar.stopBar();											
											return;
										}else{
											progressBar.stopBar();
										}
										break;
									}
								}
		        		  }
		        	  }
		        			}catch (Exception e) {
		        				e.printStackTrace();
		        			} 	          
		          UserDoTaskPanel.this.startDoTaskOperation();
			
		}

}
