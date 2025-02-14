package com.foxconn.mechanism.mntL5PartsManagement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.mechanism.pacmaterial.PACBOMBean;
import com.foxconn.mechanism.pacmaterial.PACImportBOMLoadingDialog;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.AbstractAIFSession;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentContextList;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.services.rac.core.DataManagementService;

public class MNTL5PartsHandler extends AbstractHandler{
	
	TCSession session;
	
	TCComponentItemRevision itemRevision = null;
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		try {
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
			Shell shell = app.getDesktop().getShell();
			session = (TCSession) app.getSession();
			InterfaceAIFComponent[] aifCom = app.getTargetComponents();
			// 檢查選定的類型必須是
			for(int i=0;i<aifCom.length;i++ ) {
				InterfaceAIFComponent aif = aifCom[i];
				if(aif instanceof TCComponentItemRevision) {
					itemRevision = (TCComponentItemRevision) aif;
				}else if(aif instanceof TCComponentBOMLine){
					itemRevision = ((TCComponentBOMLine) aif).getItemRevision();
				}else if(aif instanceof TCComponentItem) {
					itemRevision = ((TCComponentItem) aif).getLatestItemRevision();
				}else {
					MessageBox.post(String.format("選擇的第%d個对象不是版本对象",i+1), "檢查", MessageBox.INFORMATION);
					return null;
				}
				String type = itemRevision.getType();
				if(!"D9_VirtualPartRevision".equals(type)&&!"D9_CommonPartRevision".equals(type)){
					MessageBox.post(String.format("選擇的第%d個对象不是機構件也不是虛擬件",i+1), "檢查", MessageBox.INFORMATION);
					return null;
				}
			}
			boolean confirm = MessageDialog.openQuestion(shell, "詢問",String.format("你選擇了%d個對象，確定要創建相同ID的物料嗎？",aifCom.length));
			if(confirm) {
				new PACImportBOMLoadingDialog(shell, "正在創建相同ID物料",new PACImportBOMLoadingDialog.Action() {
					
					@Override
					public void run() {					
						try {
							TCUtil.setBypass(session);
							for(int i=0;i<aifCom.length;i++ ) {
								execute(itemRevision);
							}
						}catch (Exception e) {
							e.printStackTrace();
							MessageBox.post(e.toString(), "錯誤", MessageBox.ERROR);
						}finally {
							TCUtil.closeBypass(session);
						}
					}
				});
				// 刷新結構管理
				InterfaceAIFComponent aif = aifCom[0];
				if(aif instanceof TCComponentBOMLine) {
					TCComponentBOMLine bomline = (TCComponentBOMLine) aif;
					bomline.window().refresh();
				}
				MessageBox.post(shell, "已創建"+aifCom.length+"個物料在HOME下", "提示", MessageBox.INFORMATION);
			}
		}catch (Exception e) {
			MessageBox.post(e.toString(), "錯誤", MessageBox.ERROR);
		}
		return null;
	}
	
	private void execute(TCComponentItemRevision itemRevision) throws Exception {
		TCComponentBOMWindow bomWindow = TCUtil.createBOMWindow(session);
		TCComponentBOMWindow bomWindow2 = TCUtil.createBOMWindow(session);
		try {
			
			TCComponentBOMLine topBomline = TCUtil.getTopBomline(bomWindow, itemRevision);
			TCComponentItemRevision createOrGetItemRevision = createOrGetItemRevision(topBomline.getItemRevision());
			TCComponentFolder homeFolder = session.getUser().getHomeFolder();
			try {
				homeFolder.add("contents", createOrGetItemRevision.getItem());
			}catch (TCException e) {
				e.printStackTrace();
			}
			TCComponentBOMLine topBomline2 = TCUtil.getTopBomline(bomWindow2, createOrGetItemRevision);
			buildBom(topBomline,topBomline2);
			bomWindow.save();
			bomWindow2.save();
			try {
				AIFComponentContext[] relatedList = itemRevision.getRelated("TC_Is_Represented_By");
				for(AIFComponentContext r : relatedList) {
					createOrGetItemRevision.add("TC_Is_Represented_By", (TCComponent)r.getComponent());
				}				
			}catch (TCException e) {
				e.printStackTrace();
			}
		}finally {
			bomWindow2.close();
			bomWindow.close();
		}
	}
	
	private void buildBom(TCComponentBOMLine parent1,TCComponentBOMLine parent2) throws Exception {
		for(AIFComponentContext aif:parent1.getChildren()) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) aif.getComponent();
			TCComponentBOMLine addedBomLine = addBomLine(bomLine,parent2,false);
			TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();
			for(TCComponentBOMLine subBomLine:listSubstitutes) {
				addBomLine(subBomLine,addedBomLine,true);
			}
			buildBom(bomLine, addedBomLine);
		}
	}	
	
	private TCComponentBOMLine addBomLine(TCComponentBOMLine bomLine,TCComponentBOMLine parent,boolean isSub) throws Exception {
		TCComponentItemRevision itemRevision = bomLine.getItemRevision();
		TCComponentItemRevision createOrGetItemRevision = createOrGetItemRevision(itemRevision);
		AIFComponentContext[] children = parent.getChildren();
		for(AIFComponentContext child:children) {
			TCComponentBOMLine childBomLine = (TCComponentBOMLine) child.getComponent();
			if(childBomLine.getItemRevision().equals(createOrGetItemRevision)){
				return childBomLine;
			}
		}
		TCComponentBOMLine add = parent.add(createOrGetItemRevision.getItem(),createOrGetItemRevision,null,isSub);
		add.setProperty("bl_sequence_no", bomLine.getProperty("bl_sequence_no"));
		add.setProperty("bl_quantity", bomLine.getProperty("bl_quantity"));
		String uom = bomLine.getProperty("bl_uom");
		if("Other".equals(uom)) {
			add.setProperty("bl_uom", "Other");
		}		
		try {
			AIFComponentContext[] relatedList = itemRevision.getRelated("TC_Is_Represented_By");
			for(AIFComponentContext r : relatedList) {
				createOrGetItemRevision.add("TC_Is_Represented_By", (TCComponent)r.getComponent());
			}				
		}catch (TCException e) {
			e.printStackTrace();
		}
		return add;
	}
	
	
	
	private TCComponentItemRevision createOrGetItemRevision(TCComponentItemRevision itemRevision) throws Exception {
		Map<String, String> map = readAttributesToCopy(itemRevision);
		String itemId = itemRevision.getProperty("item_id");
		TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件版本...", new String[] { "items_tag.item_id","object_type" }, new String[]{itemId,"D9_L5_PartRevision"});
		int length = executeQuery.length;
		if(length==0) {
			// 創建一個
			TCComponentItem tccom = (TCComponentItem)TCUtil.createCom(session, "D9_L5_Part",itemId,null);
			TCComponentItemRevision createdItemRevision = tccom.getLatestItemRevision();
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {			
				String key = iterator.next();
				String value = map.get(key);
				createdItemRevision.setProperty(key,value);	
			}
			return createdItemRevision;
		}else {
			return (TCComponentItemRevision) executeQuery[0];
		}
	}
	
	// 顧問提供的新的設置方法
	private void setProp(TCComponent tc,String key,String value) {
		DataManagementService dm=DataManagementService.getService(session);
		com.teamcenter.services.rac.core._2007_01.DataManagement.VecStruct vec=new com.teamcenter.services.rac.core._2007_01.DataManagement.VecStruct();
		vec.stringVec=new String[] {value};
		Map map=new HashMap<String, com.teamcenter.services.rac.core._2007_01.DataManagement.VecStruct>();
		map.put(key.trim(),vec);
		com.teamcenter.rac.kernel.ServiceData res=dm.setProperties(new TCComponent[] {tc}, map);
		System.out.println("res error=size"+res.sizeOfPartialErrors());
	}
	
	private Map<String,String> readAttributesToCopy(TCComponentItemRevision itemRevision) throws TCException{
		Map<String,String> map = new HashMap<>();
//		map.put("item_id", itemRevision.getProperty("item_id"));
		map.put("d9_EnglishDescription",itemRevision.getProperty("d9_EnglishDescription"));
		map.put("d9_Un", itemRevision.getProperty("d9_Un"));
		map.put("d9_SupplierZF", itemRevision.getProperty("d9_SupplierZF"));
		map.put("d9_ManufacturerID", itemRevision.getProperty("d9_ManufacturerID"));
		map.put("d9_ManufacturerPN", itemRevision.getProperty("d9_ManufacturerPN"));
		map.put("d9_ChineseDescription", itemRevision.getProperty("d9_ChineseDescription"));
		map.put("d9_MaterialGroup", itemRevision.getProperty("d9_MaterialGroup"));
		map.put("d9_MaterialType", itemRevision.getProperty("d9_MaterialType"));
		map.put("d9_ProcurementMethods", itemRevision.getProperty("d9_ProcurementMethods"));
		return map;		
	}

}
