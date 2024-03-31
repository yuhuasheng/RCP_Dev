package com.teamcenter.rac.workflow.commands.newperformsignoff;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.kernel.TCComponentSignoff;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.util.Registry;
import java.awt.Dialog;
import java.awt.Window;
import java.io.PrintStream;
import javax.swing.JTable;

public class UserNewSignoffDecisionPanel extends NewSignoffDecisionPanel
{
  public UserNewSignoffDecisionPanel(Window arg0, TCComponentTask arg1, TCComponentSignoff[] arg2, NewPerformSignoffTaskPanel arg3)
  {
    super(arg0, arg1, arg2, arg3);
    System.out.println("UserNewSignoffDecisionPanel");
  }

  public void setUpDecisionDialog(int paramInt, JTable paramJTable)
  {
    doDecisionDialog(paramInt, paramJTable, "DecisionDialog");
  }

  private void doDecisionDialog(int paramInt, JTable paramJTable, String dialogName)
  {
    TCComponentSignoff localTCComponentSignoff = null;
    if (paramInt >= 0) {
      localTCComponentSignoff = getSignoffInRow(paramInt);
    }
    if ((localTCComponentSignoff == null) || (paramInt == -1)) {
      return;
    }
    if (getUnderLine(paramInt, 2))
    {
      Object[] arrayOfObject = null;
      if ((this.m_window instanceof Dialog))
        arrayOfObject = new Object[] { (Dialog)this.m_window, this.psTask, localTCComponentSignoff };
      else {
        arrayOfObject = new Object[] { (AIFDesktop)this.m_window, this.psTask, localTCComponentSignoff };
      }
      System.out.println("弹框页面:" + dialogName);
      this.decisionDlg = ((DecisionDialog)this.registry.newInstanceFor(dialogName, arrayOfObject));
      this.decisionDlg.setLocationRelativeTo(paramJTable);
      this.decisionDlg.constructDialog();
      if ((!DecisionDialog.isMineToPerform(localTCComponentSignoff)) && (!this.decisionDlg.isActiveSurrogate()))
      {
        this.decisionDlg.disposeDialog();
        return;
      }
      this.decisionDlg.setModal(true);
      this.decisionDlg.setVisible(true);
    }
  }
}
