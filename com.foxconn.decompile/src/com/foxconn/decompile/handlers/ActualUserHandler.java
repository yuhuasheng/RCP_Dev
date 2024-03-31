package com.foxconn.decompile.handlers;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.decompile.util.FileStreamUtil;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.services.rac.core.DataManagementService;

public class ActualUserHandler extends AbstractHandler {
	public AbstractAIFUIApplication	app;
	public TCSession				session	= null;

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		app = AIFUtility.getCurrentApplication();
		session = (TCSession) app.getSession();
		try {
			TCUtil.setBypass(session);
			
			DataManagementService dmService = DataManagementService.getService(session);
			InterfaceAIFComponent[] targetComponents = app.getTargetComponents();
			if(targetComponents!=null && targetComponents.length > 0) {
				for (int i = 0; i < targetComponents.length; i++) {
					TCComponentItemRevision itemRev = null;
					if(targetComponents[i] instanceof TCComponentItemRevision) {
						itemRev = (TCComponentItemRevision)targetComponents[i];
					} else if(targetComponents[i] instanceof TCComponentItem) {
						TCComponentItem item = (TCComponentItem)targetComponents[i];
						itemRev = item.getLatestItemRevision();
					}
					
					if(itemRev != null) {
						TCComponentDataset tcDataset = TCUtil.findDataSet(itemRev, "IMAN_external_object_link", "publicMail.txt");
						if (tcDataset != null) {
							String fullFilePath = TCUtil.downloadFile(tcDataset, System.getenv("TEMP"), ".txt", "Text", "", false);
							List<String> contentLst = FileStreamUtil.getContent(new File(fullFilePath), "");
							FileStreamUtil.deleteFile(fullFilePath);
							Map<String, List<List<String>>> pubMailMapForMore = FileStreamUtil.parseLstToMap(contentLst);
							
							List<String> projectLst = null;
							List<String> itemLst = null;
							
							if(pubMailMapForMore != null && pubMailMapForMore.size() > 0) {
								itemRev.setRelated("IMAN_external_object_link", new TCComponent[] {});
								TCComponentForm createForm = (TCComponentForm)com.foxconn.tcutils.util.TCUtil.getPublicMailForm(dmService, "publicMail", "D9_TaskForm", "IMAN_external_object_link", itemRev, Boolean.TRUE);
								createForm.setRelated("d9_TaskTable", new TCComponent[] {});
								
								for (Map.Entry<String, List<List<String>>> entry : pubMailMapForMore.entrySet()) {
									List<List<String>> valueLst = entry.getValue();
									if (valueLst !=null && 3 == valueLst.size()) {
										projectLst = valueLst.get(0);
										itemLst = valueLst.get(1);
										
										List<String> userLst = valueLst.get(2);
										if (userLst !=null && 4 == userLst.size()) {
											String d9_ProcessNode = userLst.get(0);
											String d9_TCUser = userLst.get(1);
											String d9_ActualUserName = userLst.get(2);
											String d9_ActualUserMail = userLst.get(3);
											
											
											HashMap<String, String> map = new HashMap<String, String>();
											map.put("d9_ProcessNode", d9_ProcessNode);
											map.put("d9_TCUser", d9_TCUser);
											map.put("d9_ActualUserName", d9_ActualUserName);
											map.put("d9_ActualUserMail", d9_ActualUserMail);
											
											TCComponent createTableRow = com.foxconn.tcutils.util.TCUtil.createTableRow(session, "D9_TaskTableRow", map);
											createForm.add("d9_TaskTable", createTableRow);
											
										}
									}
								}
								
								
								if(projectLst !=null && projectLst.size() > 0) {
									TCProperty d9_ProjectList = createForm.getTCProperty("d9_ProjectList");
									String[] array = projectLst.<String>toArray(new String[projectLst.size()]);
									d9_ProjectList.setStringValueArray(array);
									createForm.setTCProperty(d9_ProjectList);
								}
								
								if(itemLst !=null && itemLst.size() > 0) {
									TCProperty d9_ProcessTarget = createForm.getTCProperty("d9_ProcessTarget");
									String[] array = itemLst.<String>toArray(new String[projectLst.size()]);
									d9_ProcessTarget.setStringValueArray(array);
									createForm.setTCProperty(d9_ProcessTarget);
								}
								
							}
						}
					}
				}
			} else {
				MessageBox.post("請選擇版本對象！","提示",MessageBox.INFORMATION);
				return null;
			}
			MessageBox.post("已將選中所有對象的 TXT轉為Form表單！","提示",MessageBox.INFORMATION);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			TCUtil.closeBypass(session);
		}
		return null;
	}

	

}
