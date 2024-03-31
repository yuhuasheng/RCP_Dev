package com.foxconn.electronics.L10Ebom.handler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class AutomaticBOMConstructionHandler extends AbstractHandler {
	public AbstractAIFUIApplication app;
	public TCSession session = null;
	public int maximum = 1;
	public ProgressMonitorDialog progressMonitorDialog;
	
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {	
		System.out.println("自动搭建BOM");
		app = AIFUtility.getCurrentApplication();
		InterfaceAIFComponent[] targetComponent = app.getTargetComponents();
		session = (TCSession) app.getSession();
		
		if(targetComponent == null || targetComponent.length <= 2) {
			MessageDialog.openInformation(null, "提示", "请选择 成品料号(8/7148/7149开头) + FINAL ASSY料号(71407开头) + PANEL料号(71408开头) + PACK ASSY料号(713开头) ，再 实现自动搭建BOM！");
			return null;
		}
		
		if(targetComponent !=null && targetComponent.length > 0) {
			maximum = targetComponent.length + 1;
		}
		
		IRunnableWithProgress iRunnableWithProgress = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				monitor.beginTask("自动搭建BOM, 请等待...", maximum);
				try {
					createBOMLineList(monitor);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				monitor.done();
				progressMonitorDialog.setBlockOnOpen(false);
				
//				MessageDialog.openInformation(null, "提示", "自动搭建BOM完成！");
			}
		};
		
		try {
			progressMonitorDialog = new ProgressMonitorDialog(AIFUtility.getActiveDesktop().getShell());
			progressMonitorDialog.run(true, false, iRunnableWithProgress);
			
			MessageDialog.openInformation(null, "提示", "自动搭建BOM完成！");
		} catch (InvocationTargetException | InterruptedException e) {
			progressMonitorDialog.setBlockOnOpen(false);
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void createBOMLineList(IProgressMonitor monitor) {
		//成品BOM料号
		ArrayList<TCComponentItemRevision> finishedPartList = new ArrayList<>();
		//半成品料号
		TCComponentItemRevision finalASSY = null;
		TCComponentItemRevision panelASSY = null;
		//ArrayList<TCComponentItemRevision> virtualPart = new ArrayList<>();
		//Packing ASSY料号
		ArrayList<TCComponentItemRevision> packingASSYPartList = new ArrayList<>();
		//PANEL料号
		ArrayList<TCComponentItemRevision> panelPartList = new ArrayList<>();
		
		
		InterfaceAIFComponent[] targetComponent = app.getTargetComponents();
		for (int i = 0; i < targetComponent.length; i++) {
			try {
				InterfaceAIFComponent component= targetComponent[i];
				TCComponentItemRevision itemRevision = null;
				if(component instanceof TCComponentItemRevision) {
					itemRevision = (TCComponentItemRevision) component;
					itemRevision = itemRevision.getItem().getLatestItemRevision();
				}else if(component instanceof TCComponentItem) {
					TCComponentItem item = (TCComponentItem) component;
					itemRevision = item.getLatestItemRevision();
				}
				if(itemRevision == null)
					continue;
				
				boolean isReleased = TCUtil.isReleased(itemRevision);
				if(isReleased) {
					itemRevision = itemRevision.saveAs(null);
				}
				
				String object_type = itemRevision.getType();
				String item_id = itemRevision.getProperty("item_id");
				if("D9_VirtualPartRevision".equals(object_type)) {
					if(item_id.startsWith("71407")) {
						finalASSY = itemRevision;
						continue;
					} else if(item_id.startsWith("71408")) {
						panelASSY = itemRevision;
						continue;
					} else if(item_id.startsWith("713")) {
						packingASSYPartList.add(itemRevision);
						continue;
					} 
				} else if("D9_FinishedPartRevision".equals(object_type) && (item_id.startsWith("8") || item_id.startsWith("X") || item_id.startsWith("7148"))) {
					finishedPartList.add(itemRevision);
					continue;
				} else if("D9_CommonPartRevision".equals(object_type)) {
					if(item_id.startsWith("7604")) {
						panelPartList.add(itemRevision);
						continue;
					}
				}
				
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
		
		if(finishedPartList !=null && finishedPartList.size() > 0) {
			for (int i = 0; i < finishedPartList.size(); i++) {
				TCComponentBOMWindow bomwindow = TCUtil.createBOMWindow(session);
				TCComponentItemRevision finished = finishedPartList.get(i);
				
				try {
					monitor.worked(1);
					createBOMLine(bomwindow, finished, finalASSY, panelASSY, panelPartList, packingASSYPartList);
				} catch (TCException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						bomwindow.save();
						bomwindow.close();
					} catch (TCException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
	public void createBOMLine(TCComponentBOMWindow bomwindow, TCComponentItemRevision finished, TCComponentItemRevision finalASSY, TCComponentItemRevision panelASSY, ArrayList<TCComponentItemRevision> panelPartList, ArrayList<TCComponentItemRevision> packingASSYPartList) throws TCException {
		TCComponentBOMLine topBomline = bomwindow.setWindowTopLine(finished.getItem(), finished, null, null);
		AIFComponentContext[] childrens = topBomline.getChildren();
		if(childrens != null && childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
				children.cut();
			}
			bomwindow.save();
		}
		
		if(packingASSYPartList!=null && packingASSYPartList.size() > 0) {
			for (int i = 0; i < packingASSYPartList.size(); i++) {
				TCComponentItemRevision itemrev = packingASSYPartList.get(i);
				if(isContainsBOMLine(topBomline, itemrev)) 
					continue;
				topBomline.add(itemrev.getItem(), itemrev, null, false);
			}
		}
		
		if(finalASSY != null) {
			TCComponentBOMLine addFinalASSY = topBomline.add(finalASSY.getItem(), finalASSY, null, false);
			if(panelASSY != null) {
				if(isContainsBOMLine(addFinalASSY, panelASSY)) 
					return;
				TCComponentBOMLine addPanelASSY = addFinalASSY.add(panelASSY.getItem(), panelASSY, null, false);
				
				if(panelPartList != null && panelPartList.size() > 0) {
					for (int i = 0; i < panelPartList.size(); i++) {
						TCComponentItemRevision panelPart = panelPartList.get(i);
						
						if(isContainsBOMLine(addPanelASSY, panelPart)) 
							continue;
						addPanelASSY.add(panelPart.getItem(), panelPart, null, false);
					}
				}
			}
		}
	}
	
	
	public boolean isContainsBOMLine(TCComponentBOMLine topBomline, TCComponentItemRevision itemrev) throws TCException {
		AIFComponentContext[] childrens = topBomline.getChildren();
		if(childrens != null && childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
				String bl_item_item_id = children.getItemRevision().getProperty("bl_item_item_id");
				String item_id = itemrev.getProperty("item_id");
				if(item_id.equals(bl_item_item_id)) {
					return true;
				}
			}
		}
		return false;
	}

}
