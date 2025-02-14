package com.foxconn.electronics.cablematerial;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.collection.CollUtil;

public class CreateCableMaterialHandler extends AbstractHandler{
	TCSession session;
	AbstractAIFUIApplication app;
	String url;
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {

		url = TCUtil.getPreference(null ,TCPreferenceService.TC_preference_site, "D9_PNMS_Address");
		Registry reg = Registry.getRegistry("com.foxconn.electronics.cablematerial.cablematerial");
		
		app = AIFUtility.getCurrentApplication();
		session = (TCSession) app.getSession();
		
		TCComponent targetComponent = (TCComponent) app.getTargetComponent();
		if (targetComponent == null) {
			TCUtil.infoMsgBox(reg.getString("infoMsg1"));
			return null;
		}
		try {
			if (targetComponent instanceof TCComponentBOMLine) {
				targetComponent = ((TCComponentBOMLine)targetComponent).getItemRevision();
			}
			String itemRevType = targetComponent.getType();
			String itemId = "";
			TCUtil.setBypass(session);
			List<String> arrayPreference = TCUtil.getArrayPreference(TCPreferenceService.TC_preference_site, "D9_ApplyHHPN_Type");
			if (arrayPreference.contains(itemRevType)) {
				
				if("DocumentRevision".equals(itemRevType)) {
					handleDocumentRevision(targetComponent);
					return null;
				}
				
				String CABDesignId = targetComponent.getProperty("item_id");
				
				if(CABDesignId.contains("@")) {
					Runtime runtime = Runtime.getRuntime();
					runtime.exec("C:/Windows/System32/cmd.exe /k start " + url + CABDesignId);
					return null;
				}
				
				// 恢复游离数据
				String recoveryIds = "";
				for (int i = 0; i < 100; i++) {
					if(i!=0) {
						recoveryIds+=";";
					}
					recoveryIds+=(CABDesignId+"@"+i);
				}
				TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件版本...", new String[]{"items_tag.item_id"}, new String[]{recoveryIds});
				int length = executeQuery.length;
				TCComponentItemRevision lastItemRevision = null;
				int maxId = 0;
				for (int i = 0; i < length; i++) {
					TCComponentItemRevision itemRevision = (TCComponentItemRevision) executeQuery[i];
					String id = itemRevision.getProperty("item_id");
					String[] split = id.split("@");
					int n = Integer.parseInt(split[split.length-1]);
					if(maxId<n) {
						maxId=n;
						lastItemRevision = itemRevision;
					}
					try {
						itemRevision.add("TC_Is_Represented_By", targetComponent);
					} catch (Exception e) {
						System.out.println("該對象已加入到表示中");
					}
				}
				
				MessageBox msgBox = new MessageBox(app.getDesktop().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				msgBox.setText(reg.getString("createCableMaterial"));
				msgBox.setMessage("已經申請了"+length+"個物料了，是否要申請新的物料？");
				int rc = msgBox.open();
				if (rc == SWT.YES) {
					itemId = CABDesignId + "@" + (length + 1);
					TCComponentItem newItem = TCUtil.createItem(session, itemId, "", itemId, "D9_CommonPart");
					TCComponentItemRevision latestItemRevision = newItem.getLatestItemRevision();
					String object_name = targetComponent.getProperty("object_name");
					latestItemRevision.setProperty("d9_EnglishDescription", object_name);
					latestItemRevision.add("TC_Is_Represented_By", targetComponent);
					String desc = targetComponent.getProperty("d9_CCGroupID");
					System.out.println("desc:" + desc);
					latestItemRevision.setProperty("d9_CCGroupID", desc);
				} else if (rc == SWT.NO) {
					itemId = lastItemRevision.getProperty("item_id");
				}																				
			}else if ("D9_CommonPartRevision".equals(itemRevType)) {
				itemId = targetComponent.getProperty("item_id");
			}else if ("D9_L5_PartRevision".equals(itemRevType)) {
				itemId = targetComponent.getProperty("item_id");
			}else {
				TCUtil.infoMsgBox(reg.getString("infoMsg1"));
				return null;
			}
			
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("C:/Windows/System32/cmd.exe /k start " + url + itemId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			TCUtil.closeBypass(session);
		}
		
		return null;
	}
	
	void handleDocumentRevision(TCComponent targetComponent) throws Exception {
		AIFComponentContext[] related = ((TCComponentItemRevision)targetComponent).getItem().whereReferencedByTypeRelation(new String[] {"D9_CommonPartRevision"},new String[] {"D9_CommonPartSpec_REL"});
		String itemId = null;
		TCComponentItem newItem;
		if(related.length != 0) {
			List<String> requestedList = new ArrayList<String>();
			for (int i = 0; i < related.length; i++) {
				TCComponent relate = (TCComponent) related[i].getComponent();
				itemId = relate.getProperty("item_id");
				if(itemId.contains("@")){
					requestedList.add(itemId);
				} 
			}
			MessageBox msgBox = new MessageBox(app.getDesktop().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			msgBox.setText("申請鴻海料號");
			String join = CollUtil.join(requestedList, "\n");
			msgBox.setMessage("已經申請了 "+requestedList.size()+" 個物料了\n"+join+"\n是否要申請新的物料？");
			int rc = msgBox.open();
			if (rc == SWT.NO) {
				return;
			}
		}
		itemId = targetComponent.getProperty("item_id") + "@" + (related.length + 1);
		newItem = TCUtil.createItem(session, itemId, "", itemId, "D9_CommonPart");
		TCComponentItemRevision latestItemRevision = newItem.getLatestItemRevision();
		latestItemRevision.add("D9_CommonPartSpec_REL", ((TCComponentItemRevision)targetComponent).getItem());
		String object_name = targetComponent.getProperty("object_name");
		latestItemRevision.setProperty("d9_EnglishDescription", object_name);
		// 将新创建的Item添加到Home
		TCComponentFolder homeFolder = TCUtil.getTCSession().getUser().getHomeFolder();
		homeFolder.add("contents", newItem);
		homeFolder.refresh();
		Runtime runtime = Runtime.getRuntime();
		runtime.exec("C:/Windows/System32/cmd.exe /k start " + url + itemId);
	}

}
