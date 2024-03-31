package com.teamcenter.rac.workflow.commands.resume;

import com.hh.fx.rewrite.util.DBUtil;
import com.hh.fx.rewrite.util.GetPreferenceUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.AbstractAIFDialog.IC_DisposeActionListener;
import com.teamcenter.rac.aif.InterfaceAIFOperationListener;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.log.Debug;
import com.teamcenter.rac.workflow.common.CommentsPanel;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class UserResumeDialog
  extends AbstractAIFDialog
  implements InterfaceAIFOperationListener
{
  private int PREFERED_WIDTH = 400;
  private int PREFERED_HIGHT = 150;
  private Registry appReg = null;
  private UserResumeOperation operation = null;
  private CommentsPanel textPanel = null;
  private TCComponentTask[] targets = null;
  private TCSession session = null;
  private String comments = null;
  private JButton okButton;
  private JButton clearButton;
  private JButton cancelButton;
  private AIFDesktop desktop = null;
  
  
  private JLabel finishDateLabel = null;
  private JLabel preFinishDateLabel = null;
  private JTextField finishDateText = null;
  private JTextField preFinishDateText = null;
  GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
  boolean isFindWorkflow = false;
  protected String finishDateValue = "";
  protected String preFinishDateValue = "";
  protected TCComponentTask targetTask = null;
  
  public UserResumeDialog(Frame paramFrame, TCComponentTask[] paramArrayOfTCComponentTask)
  {
    super(paramFrame, true);
    System.out.println("UserResumeDialog 1");
    targets = paramArrayOfTCComponentTask;
    if ((paramFrame instanceof AIFDesktop)) {
      desktop = ((AIFDesktop)paramFrame);
    }
    initDialog();
  }
  
  public UserResumeDialog(TCComponentTask[] paramArrayOfTCComponentTask)
  {
    super(true);
    System.out.println("UserResumeDialog 2");
    targets = paramArrayOfTCComponentTask;
    initDialog();
  }
  
  private void initDialog()
  {
    appReg = Registry.getRegistry(this);
    session = targets[0].getSession();
    setTitle(appReg.getString("command.TITLE"));
    JPanel localJPanel1 = new JPanel(new VerticalLayout(5, 2, 2, 2, 2));
    localJPanel1.setPreferredSize(new Dimension(PREFERED_WIDTH, PREFERED_HIGHT));
    textPanel = new CommentsPanel(targets);
    JPanel localJPanel2 = new JPanel(new ButtonLayout());
    okButton = new JButton(appReg.getString("ok.BUTTON"));
    okButton.setMnemonic(appReg.getString("ok.MNEMONIC").charAt(0));
    okButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
    	  UserResumeDialog.this.startResumeOperation();
      }
    });
    clearButton = new JButton(appReg.getString("clear.BUTTON"));
    clearButton.setMnemonic(appReg.getString("clear.MNEMONIC").charAt(0));
    clearButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent)
      {
        textPanel.clearComments();
      }
    });
    cancelButton = new JButton(appReg.getString("cancel.BUTTON"));
    cancelButton.setMnemonic(appReg.getString("cancel.MNEMONIC").charAt(0));
    cancelButton.addActionListener(new AbstractAIFDialog.IC_DisposeActionListener());
    
    //add by 李昂  20200623
    String rootTaskName = "";
    System.out.println("targets[0] == "+targets[0]);
    try {
		TCProperty prop = targets[0].getTCProperty("root_task");
		TCComponent rootTask = prop.getReferenceValue();
		rootTaskName = rootTask.getProperty("object_name");
	} catch (TCException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
    
    System.out.println("rootTaskName == "+rootTaskName);
    if(!"".equals(rootTaskName)) {
    	String[] addTaskNames = getPreferenceUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, 
    			"FX_Wrokflow_Add_Finish_Template");
    	
    	for(int i=0;i<addTaskNames.length;i++) {
    		if(rootTaskName.equals(addTaskNames[i])) {
    			isFindWorkflow = true;
    			break;
    		}
    	}
    	System.out.println("isFindWorkflow == "+isFindWorkflow);
    	targetTask = targets[0];
    	if(isFindWorkflow) {
    		try {
				if(targetTask.isTypeOf(new String[]{"EPMDoTask","EPMConditionTask",
							"EPMReviewTask","EPMAcknowledgeTask","EPMPerformSignoffTask"})) {
					
					if(targetTask.isTypeOf("EPMPerformSignoffTask")) {
						TCProperty prop = targetTask.getTCProperty("parent_task");
						targetTask = (TCComponentTask) prop.getReferenceValue();
					}
					
					System.out.println("targetTask == "+targetTask);
					JPanel localJPanel3 = new JPanel();
		            finishDateLabel = new JLabel("完成时间：");
		            finishDateLabel.setPreferredSize(new Dimension(100, 20));
		            finishDateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		            
		            preFinishDateLabel = new JLabel("提前完成时间：");
		            preFinishDateLabel.setPreferredSize(new Dimension(120, 20));
		            preFinishDateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		            
		            localJPanel3.add(finishDateLabel);
		            finishDateText = new JTextField(5);
		           
		            try {
		            	finishDateValue = targetTask.getProperty("fx8_CompleteTime");
		        	} catch (TCException e) {
		        		// TODO Auto-generated catch block
		        		e.printStackTrace();
		        	}
		            System.out.println("finishDateValue1 == "+finishDateValue);
		            finishDateText.setDocument(new NumberDocument());
		            finishDateText.setText(finishDateValue);
		            localJPanel3.add(finishDateText);
		            
		            localJPanel3.add(preFinishDateLabel);
		            preFinishDateText = new JTextField(5);
		            try {
		            	preFinishDateValue = targetTask.getProperty("fx8_PreCompleteTime");
		        	} catch (TCException e) {
		        		// TODO Auto-generated catch block
		        		e.printStackTrace();
		        	}
		            System.out.println("preFinishDateValue == "+preFinishDateValue);
		            preFinishDateText.setDocument(new NumberDocument());
		            preFinishDateText.setText(preFinishDateValue);
		            localJPanel3.add(preFinishDateText);
		            localJPanel1.add("unbound.bind.center.center",localJPanel3);
				}
			} catch (TCException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		
    	}
    }
    //----------------------------------------
    
    localJPanel2.add(okButton);
    localJPanel2.add(clearButton);
    localJPanel2.add(cancelButton);
    
    localJPanel1.add("unbound.bind.center.center", textPanel);
    localJPanel1.add("bottom.nobind.center.center", localJPanel2);
    localJPanel1.setMinimumSize(new Dimension(100, 50));
    
    getContentPane().setLayout(new FlowLayout());
    getContentPane().add(localJPanel1);
    setModal(false);
    validate();
  }
  
  public void run()
  {
    pack();
    centerToScreen();
    setVisible(true);
  }
  
  public void startOperation(String paramString)
  {
	  
	  
    cancelButton.setVisible(false);
    clearButton.setVisible(false);
    okButton.setVisible(false);
    validate();
  }
  
  public void endOperation()
  {
    if (operation != null)
    {
      operation.removeOperationListener(this);
      operation = null;
    }
    validate();
    disposeDialog();
  }
  
  private void startResumeOperation()
  {
    try
    {
    	if(preFinishDateText != null){
        	preFinishDateValue = preFinishDateText.getText();
    	}
    	if(finishDateText != null){
        	finishDateValue = finishDateText.getText();
    	}
    	
      comments = textPanel.getComments();
      //operation = new UserResumeOperation(desktop, targets, comments);
      operation = new UserResumeOperation(desktop, targets, comments,isFindWorkflow,this);
      operation.addOperationListener(this);
      session.queueOperation(operation);
    }
    catch (Exception localException)
    {
      Debug.printStackTrace("resumeoperation", localException);
      MessageBox localMessageBox = new MessageBox(localException);
      localMessageBox.setVisible(true);
      return;
    }
  }
  
  class NumberDocument extends PlainDocument {
	    public NumberDocument() {
	    }

	    public void insertString(int var1, String var2, AttributeSet var3) throws BadLocationException {
	        if (this.isNumeric(var2)) {
	            super.insertString(var1, var2, var3);
	        } else {
	            Toolkit.getDefaultToolkit().beep();
	        }

	    }

	    private boolean isNumeric(String var1) {
	        try {
	            Long.valueOf(var1);
	            return true;
	        } catch (NumberFormatException var3) {
	            return false;
	        }
	    }
	}
}

/* Location:           Z:\工作硬盘\项目代码\开发库\TC12.3\plugins\com.teamcenter.rac.tcapps_12000.3.0.jar
 * Qualified Name:     com.teamcenter.rac.workflow.commands.resume.ResumeDialog
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */