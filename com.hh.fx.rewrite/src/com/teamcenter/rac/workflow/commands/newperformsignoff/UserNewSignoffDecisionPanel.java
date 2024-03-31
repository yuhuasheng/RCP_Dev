package com.teamcenter.rac.workflow.commands.newperformsignoff;

import java.awt.Dialog;
import java.awt.Window;

import javax.swing.JTable;

import com.hh.fx.rewrite.util.Utils;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentActionHandler;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentSignoff;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.workflow.commands.newperformsignoff.DecisionDialog;
import com.teamcenter.rac.workflow.commands.newperformsignoff.NewPerformSignoffTaskPanel;
import com.teamcenter.rac.workflow.commands.newperformsignoff.NewSignoffDecisionPanel;

public class UserNewSignoffDecisionPanel extends NewSignoffDecisionPanel {

	TCComponentSignoff[] paramArrayOfTCComponentSignoff = null;
	TCComponent edaComp = null;
	public UserNewSignoffDecisionPanel(Window paramWindow,
			TCComponentTask paramTCComponentTask,
			TCComponentSignoff[] paramArrayOfTCComponentSignoff,
			NewPerformSignoffTaskPanel paramNewPerformSignoffTaskPanel) {
		super(paramWindow, paramTCComponentTask, paramArrayOfTCComponentSignoff,
				paramNewPerformSignoffTaskPanel);
		// TODO Auto-generated constructor stub
		System.out.println("UserNewSignoffDecisionPanel");

		
	}
	@Override
	public void setUpDecisionDialog(final int paramInt, final JTable paramJTable) {
		// TODO Auto-generated method stub
		
		
		//"DecisionDialog"
		//"EDACompDecisionDialog"
		
		if(isEDAComp()) {//
			new Thread(new Runnable() {
				public void run() {
					doDecisionDialog(paramInt,paramJTable,"EDACompDecisionDialog");
				}
			}).start();
		} 
		// 判断是否替代料审核
		else if (isFX8ManagerItem()) {
			new Thread(new Runnable() {
				public void run() {
					doDecisionDialog(paramInt,paramJTable,"FX8ManagerItemCheckDecisionDialog");
				}
			}).start();
		}else {
			doDecisionDialog(paramInt,paramJTable,"DecisionDialog");
		}

	}

	private boolean isEDAComp() {
		// TODO Auto-generated method stub
		boolean isEDAComp = false;
		try {

			// 流程签发页面
			TCComponent[] coms = psTask.getRelatedComponents("root_target_attachments");
			for (int i = 0; i < coms.length; i++) {//针对多个电子料
				System.out.println("对象类型 ==" + coms[i].getType());
				if(coms[i] instanceof TCComponentItemRevision){
					if(coms[i].isTypeOf("EDAComp Revision")){
						edaComp = coms[i];
						isEDAComp = true;
					}else{
						isEDAComp = false;
						break;
					}
				}
//				if (coms[i].isTypeOf("EDAComp Revision")) {
//					edaComp = coms[i];
//					isEDAComp = true;
//					break;
//				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return isEDAComp;
	}
	
	// 判断发起工作流程的对象 是否为替代料类型
	private boolean isFX8ManagerItem() {
		boolean isPrjPart = false;
		try {
			// 流程签发页面
			TCComponent[] coms = psTask.getRelatedComponents("root_target_attachments");
			for (int i = 0; i < coms.length; i++) {
				System.out.println("对象类型 ==" + coms[i].getType());
				if (coms[i].isTypeOf("FX8_ManagerItemRevision")) {
					isPrjPart = true;
					break;
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return isPrjPart;
	}
	
	private void doDecisionDialog(int paramInt, JTable paramJTable, String dialogName) {
		// TODO Auto-generated method stub
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
	      if ((m_window instanceof Dialog)) {
	        arrayOfObject = new Object[] { (Dialog)m_window, psTask, localTCComponentSignoff };
	      } else {
	        arrayOfObject = new Object[] { (AIFDesktop)m_window, psTask, localTCComponentSignoff };
	      }
	      System.out.println("弹框页面:" + dialogName);
	      decisionDlg = ((DecisionDialog)registry.newInstanceFor(dialogName, arrayOfObject));
	      decisionDlg.setLocationRelativeTo(paramJTable);
	      decisionDlg.constructDialog();
	      if ((!DecisionDialog.isMineToPerform(localTCComponentSignoff)) && (!decisionDlg.isActiveSurrogate()))
	      {
	        decisionDlg.disposeDialog();
	        return;
	      }
	      decisionDlg.setModal(true);
	      decisionDlg.setVisible(true);
	    }
	}

	
	
}
