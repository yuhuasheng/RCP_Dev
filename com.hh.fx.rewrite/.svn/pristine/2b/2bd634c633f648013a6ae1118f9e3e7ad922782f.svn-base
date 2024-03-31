package com.teamcenter.rac.workflow.commands.dotask;

import java.awt.Frame;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

/**
 * 
 * @author Handk
 *
 */
public class UserDoTaskDialog extends DoTaskDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Registry registry;
	private AIFDesktop desktop = null;
	private UserDoTaskPanel parentPanel;
	private TCComponentTask task;

	public UserDoTaskDialog(Frame paramFrame, InterfaceAIFComponent paramInterfaceAIFComponent) {		
		super(paramFrame, paramInterfaceAIFComponent);
	    this.parent = paramFrame;
	    if (paramInterfaceAIFComponent instanceof TCComponentTask)
	      task = ((TCComponentTask)paramInterfaceAIFComponent);
	    if (paramFrame instanceof AIFDesktop)
	     desktop = ((AIFDesktop)paramFrame);				
		getContentPane().removeAll();
		initDialog();
	}

	public UserDoTaskDialog(InterfaceAIFComponent paramInterfaceAIFComponent) {
		super(paramInterfaceAIFComponent);
	    if (paramInterfaceAIFComponent instanceof TCComponentTask)
	      this.task = ((TCComponentTask)paramInterfaceAIFComponent);
		getContentPane().removeAll();
		initDialog();
	}

	protected void initDialog() {
		try
	    {
	      this.registry = Registry.getRegistry(this);
	      setTitle(this.registry.getString("command.TITLE"));
	      if(this.desktop!=null&&this!=null&&this.task!=null){
	    	  this.parentPanel = new UserDoTaskPanel(this.desktop, this, this.task);	    	  
		      getContentPane().add(this.parentPanel);
	      }	     
	    }
	    catch (Exception localException)
	    {
	      MessageBox localMessageBox = null;
	      if (this.parent != null)
	      {
	        localMessageBox = new MessageBox(this.parent, localException);
	        localMessageBox.setModal(true);
	      }
	      else
	      {
	        localMessageBox = new MessageBox(localException);
	      }
	      localMessageBox.setVisible(true);
	      return;
	    }
	    pack();
	    centerToScreen(1.5D, 1.0D);
	}
	
	public void run()
	 {
	     setVisible(true);
	     parentPanel.setFocus();
	 }

	 public AIFDesktop getDesktop()
	 {
	     return desktop;
	 }
	

}
