package com.foxconn.mechanism.pacmaterial;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.TableItem;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.mechanism.pacmaterial.PACImportBOMLoadingDialog.Action;
import com.foxconn.mechanism.util.ExcelUtils;
import com.foxconn.mechanism.util.TCUtil;
import com.foxconn.tcutils.util.CommonTools;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentProjectType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import www.fjj.com.HttpUtil;

public class PACImportBOMService {

	PACImportBOMDialog dialog;
	TCSession session;
	List<PACProjectBean> porjectByUser = new ArrayList<>();
	List<PACBOMBean> historyTopObject = new ArrayList<>();
	List<String> historyPN = new ArrayList<>();
	List<PACBOMBean> pacBOMBeanList = new ArrayList<>();
	boolean isSaveSuccess;
	String realUser;
	List<String> notAllowModify = new ArrayList<String>();
	
	public PACImportBOMService(PACImportBOMDialog dialog) {
		this.dialog = dialog;
		this.session = (TCSession) dialog.app.getSession();
	}
	
	public void onGUIInited() {
		porjectByUser.clear();
		historyTopObject.clear();
		pacBOMBeanList.clear();
		notAllowModify.clear();
		new PACImportBOMLoadingDialog(dialog.shell,"PAC物料导入正在初始化...",new Action() {
			@SuppressWarnings("resource")
			@Override
			public void run() {
				Workbook workbook = null;
				try {
					TCComponentDataset dataset = com.foxconn.tcutils.util.TCUtil.findDataset("PAC Historical Part .xls");
					String fileName = "";
					for (String name : dataset.getFileNames(null)) {
						fileName = name;
					}
					File dataFile = dataset.getFile(null,fileName);
					ExcelUtils excelUtils = new ExcelUtils();
					workbook = excelUtils.getLocalWorkbook(dataFile.getAbsolutePath());
					Sheet sheet = workbook.getSheetAt(0);
					int rowCount = sheet.getLastRowNum();
					for(int i=0;i<rowCount;i++) {
						Row row = sheet.getRow(i);
						historyPN.add(ExcelUtils.getCellValueToString(row.getCell(0)));						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}  finally {
					if(workbook!=null) {
						try {
							workbook.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				queryPorjectByUser();
				queryHistoryObject();
			}
		});
		for(PACProjectBean project:porjectByUser) {
			dialog.cmb_project.add(project.toString());
			dialog.cmb_project.setData(project.toString(),project);
		}
		if(dialog.cmb_project.getItemCount() > 0) {
			dialog.cmb_project.select(0);
		}
		onSelectedProject(null);
	}
	
	public void onImport(String path) {
		dialog.table.removeAll();
		pacBOMBeanList.clear();
		ExcelWriter reader = ExcelUtil.getWriter(path);
		try {
			reader.setSheet(1);
			realUser = ExcelUtils.getCellValueToString(reader.getCell(5, 0));
			if(StrUtil.isEmpty(realUser)) {
				MessageBox.post(AIFUtility.getActiveDesktop(), "未檢測到實際用户，導入後需手動維護", "info", MessageBox.INFORMATION);
			}else {
				ArrayList<String> userList = com.foxconn.tcutils.util.TCUtil.getLovValues(session, (TCComponentItemRevisionType) session.getTypeComponent("D9_PAC_PartRevision"), "d9_ActualUserID");
				String spasUserIdLov = userList.stream().filter(e -> e.contains(realUser)).findFirst().orElse(null);
                if (CommonTools.isEmpty(spasUserIdLov)){
                	MessageBox.post(AIFUtility.getActiveDesktop(), "實際用戶工號["+realUser+"]不存在，導入後需手動維護", "info", MessageBox.INFORMATION);
                	realUser = "";
                }else {
                	realUser = spasUserIdLov;
                }
			}
			
			int rowCount = reader.getPhysicalRowCount();
			for(int i=4;i<rowCount;i++) {
				PACBOMBean pacbomBean = new PACBOMBean();
				Cell cell = reader.getCell(0,i);
				if(cell==null||StrUtil.isEmpty(ExcelUtils.getCellValueToString(cell))){
					break;
				}
				pacbomBean.level = ExcelUtils.getCellValueToString(cell).trim();
				pacbomBean.pn = ExcelUtils.getCellValueToString(reader.getCell(1,i)).trim();
				pacbomBean.partType = ExcelUtils.getCellValueToString(reader.getCell(2,i)).trim();
				pacbomBean.description = ExcelUtils.getCellValueToString(reader.getCell(3,i)).trim();
				pacbomBean.qty = ExcelUtils.getCellValueToString(reader.getCell(4,i)).trim();
				pacbomBean.unit = ExcelUtils.getCellValueToString(reader.getCell(5,i)).trim();
				pacbomBean.remark = ExcelUtils.getCellValueToString(reader.getCell(6,i)).trim();
				pacBOMBeanList.add(pacbomBean);				
			}
			String queryCondition = buildQueryCondition(pacBOMBeanList);
			if(!queryCondition.isEmpty()) {
				TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件版本...", new String[] { "items_tag.item_id" }, new String[]{queryCondition});
				int length = executeQuery.length;
				for (int i = 0; i < length; i++) {
					TCComponentItemRevision item = (TCComponentItemRevision) executeQuery[i];
					PACBOMBean pacbomBean = findPACBOMBean(pacBOMBeanList,item.getProperty("item_id"));
					pacbomBean.itemRevision = item;
				}
			}
			for(PACBOMBean pacbomBean:pacBOMBeanList) {
				pacbomBean.initialProject = matchInitialProject(pacbomBean);
				pacbomBean.isReleased = TCUtil.isReleased(pacbomBean.itemRevision);
				TableItem tableItem = new TableItem(dialog.table, SWT.NONE);
				tableItem.setData("bean",pacbomBean);
				tableItem.setData("original",pacbomBean.initialProject);
				tableItem.setData("selected",0);				
				tableItem.setText(new String[] {pacbomBean.level,pacbomBean.pn,pacbomBean.partType,pacbomBean.description,pacbomBean.qty,pacbomBean.unit,pacbomBean.remark,pacbomBean.initialProject});
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			reader.close();
		}
		
	
	}
	
	@SuppressWarnings("deprecation")
	public boolean onSave() {
		
		int selectionIndex = dialog.cmb_project.getSelectionIndex();
		if(selectionIndex==-1) {
			MessageBox.post(AIFUtility.getActiveDesktop(), "未选择专案", "info", MessageBox.INFORMATION);
			return false;
		}
		
		String text = dialog.cmb_object_name.getText();
		if(text.isEmpty()) {
			MessageBox.post(AIFUtility.getActiveDesktop(), "未输入对象名称", "info", MessageBox.INFORMATION);
			return false;
		}
		
		int itemCount = dialog.table.getItemCount();
		if(itemCount==0) {
			MessageBox.post(AIFUtility.getActiveDesktop(), "表格内无数据，无法保存", "info", MessageBox.INFORMATION);
			return false;
		}
		Object data = dialog.cmb_object_name.getData(text);
		TableItem[] items = dialog.table.getItems();
		String initialProject = dialog.cmb_project.getText();
		String phase = dialog.cmb_phase.getText();
		new PACImportBOMLoadingDialog(dialog.shell,"PAC物料清单正在保存...",new Action() {

			@Override
			public void run() {				
				TCComponentBOMWindow bomWindow = null;
				TCComponentBOMLine topBomline = null;
				PACBOMBean pacbomBean = null;
				try {
					
					TCComponentItemRevision topCom = null;
					List<PACBOMBean> pacBOMBeanList = new ArrayList<>();
					for(int i=0;i<items.length;i++) {
						TableItem item = items[i];
						dialog.shell.getDisplay().syncExec(new Runnable() {
							@Override
							public void run() {
								pacBOMBeanList.add((PACBOMBean) item.getData("bean"));
							}
						});
					}
					
					if(data!=null) {
						// 修改
						pacbomBean = (PACBOMBean) data;
						TCComponentItemRevision itemRevision = pacbomBean.itemRevision;		
						String revTopPhase = itemRevision.getProperty("d9_ProjectPhase");
						if(Integer.parseInt(phase.substring(1))<Integer.parseInt(revTopPhase.substring(1))) {
							TCUtil.warningMsgBox("已有更高的阶段，不能修改低成阶段");
							return;
						}
						
						if(TCUtil.isReleased(itemRevision)){
							// 升版
							try {
								String versionRule = getVersionRule(itemRevision);
								String newRevId = com.foxconn.mechanism.util.TCUtil.reviseVersion(session, versionRule, itemRevision.getTypeObject().getName(),itemRevision.getUid()); // 返回指定版本规则的版本号
								itemRevision = itemRevision.saveAs(newRevId);
								pacbomBean.itemRevision = itemRevision;
								topCom = itemRevision;
							} catch (TCException e) {
								e.printStackTrace();
								TCUtil.errorMsgBox(e.getMessage());
							} 
						}else {
							topCom = ((PACBOMBean) data).itemRevision;
						}
						bomWindow = TCUtil.createBOMWindow(session);
						topBomline = TCUtil.getTopBomline(bomWindow, topCom);
						try {
							com.foxconn.tcutils.util.TCUtil.unpackageBOMStructure(topBomline);
							removeBomLine(topBomline,bomWindow);
							bomWindow.close();
						} catch (TCException e) {
							e.printStackTrace();
							TCUtil.errorMsgBox(e.getMessage());
						}
					}
					
//					if(true) {
//						return;
//					}
					
					// 创建对象
					// 新建主物料
					if(topCom==null) {
						pacbomBean = new PACBOMBean();
						pacbomBean.isTop = true;
						pacbomBean.partType = "Assembly";
						pacbomBean.description = text;
						pacbomBean.initialProject = initialProject;
						pacbomBean.actualUser = realUser;
						newItemRevision(pacbomBean);
						topCom = pacbomBean.itemRevision;
					}else {
						setProperty(topCom,"d9_ProjectPhase", phase);
						setProperty(topCom,"d9_ActualUserID", realUser);
					}
					try{
						dialog.folder.add("contents", topCom.getItem());
					}catch (Exception e) {
						e.printStackTrace();
					}
					for(int i=0;i<pacBOMBeanList.size();i++) {
						pacbomBean = pacBOMBeanList.get(i);
						TCComponentItemRevision itemRevision = pacbomBean.itemRevision;
						if(itemRevision==null) {
							// 新建子物料
							newItemRevision(pacbomBean);
						}else {
							if(!pacbomBean.isReleased&&initialProject.equals(pacbomBean.initialProject)) {
								setProperty(itemRevision,"d9_Remarks", pacbomBean.remark);
								setProperty(itemRevision,"d9_PartType", pacbomBean.partType);
								setProperty(itemRevision,"object_name", pacbomBean.description);
							}
							if(pacbomBean.isChange) {
								setProperty(itemRevision,"d9_InitialProject",pacbomBean.initialProject);
							}
						}
					}
					// 搭建bom
					Map<Integer,TCComponentBOMLine> bomLineMap = new HashMap<>();
					if(pacBOMBeanList.size()>0) {
						bomWindow = TCUtil.createBOMWindow(session);
						topBomline = TCUtil.getTopBomline(bomWindow, topCom);
						bomLineMap.put(1, topBomline);
						for(int i=0;i<pacBOMBeanList.size();i++) {
							pacbomBean = pacBOMBeanList.get(i);
							TCComponentItemRevision itemRevision = pacbomBean.itemRevision;							
							int level = Integer.parseInt(pacbomBean.level);
							TCComponentBOMLine parentBOMLine = bomLineMap.get(level);
							if(parentBOMLine==null || TCUtil.isReleased(parentBOMLine.getItemRevision())) {
								continue;
							}
							TCComponentBOMLine subBomLine = null;
							try {
								subBomLine = parentBOMLine.add(itemRevision.getItem(),itemRevision,null,false);
							} catch (TCException e) {
								addToNotModifyList(itemRevision.getProperty("item_id"));
								continue;
							}
							setProperty(subBomLine,"bl_uom", "Other");
							setProperty(subBomLine,"bl_quantity", pacbomBean.qty);
							System.out.println("层级["+level + "]设置["+subBomLine.toString()+"]的数量为："+pacbomBean.qty);
							bomLineMap.put(level+1, subBomLine);
						}
						
					}
				}catch(Exception e) {
					TCUtil.errorMsgBox(e.getMessage());
					e.printStackTrace();
					return;
				}finally {
					if(bomWindow!=null) {
						try {
							bomWindow.save();
							bomWindow.close();
						} catch (TCException e) {
							e.printStackTrace();
						}
					}
				}
				if(notAllowModify.isEmpty()) {
					TCUtil.infoMsgBox("保存成功");
				}else {
					String items = "\n";
					for(String itemId:notAllowModify) {
						items+= itemId;
						items+="\n";
					}
					MessageBox.post(AIFUtility.getActiveDesktop(), "您的賬號對以下物料對象無修改權限，請聯繫原設計者"+items, "info", MessageBox.INFORMATION);
				}
				isSaveSuccess = true;
			}
			
		});
		
		
		return isSaveSuccess;
	}
	
	private String buildQueryCondition(List<PACBOMBean> pacBOMBeanList) {
		String queryCondition = "";
		for(int i=0;i<pacBOMBeanList.size();i++) {
			if(i>0) {
				queryCondition+=";";
			}
			PACBOMBean item = pacBOMBeanList.get(i);
			queryCondition +=item.pn;
		}
		return queryCondition;
	}
	
	private PACBOMBean findPACBOMBean(List<PACBOMBean> pacBOMBeanList,String itemId) throws TCException {
		for(PACBOMBean item:pacBOMBeanList) {
			if(itemId.equals(item.pn)) {
				return item;
			}
		}
		return null;
	}
	
	private void newItemRevision(PACBOMBean pacbomBean) throws TCException {
		Map<String, Object> revisionMap = new HashMap<>();
		Map<String, Object> map = new HashMap<>();
		revisionMap.put("d9_PartType", pacbomBean.partType);
		dialog.shell.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if(pacbomBean.isTop) {
					revisionMap.put("d9_Level", dialog.cmb_level.getText());
					revisionMap.put("d9_ProjectPhase", dialog.cmb_phase.getText());
				}
				map.put("project", (PACProjectBean) dialog.cmb_project.getData(dialog.cmb_project.getText()));
			}
		});
		String description = pacbomBean.description;
		revisionMap.put("d9_InitialProject", pacbomBean.initialProject);
		if(pacbomBean.isTop) {
			description = revisionMap.get("d9_Level")+"-"+ description;
			revisionMap.put("d9_ActualUserID", pacbomBean.actualUser);
		}else {
			revisionMap.put("d9_Un", pacbomBean.unit);
		}
		revisionMap.put("object_name", description);
		revisionMap.put("d9_Remarks", pacbomBean.remark);
		TCComponentItem createCom = (TCComponentItem)TCUtil.createCom(session, "D9_PAC_Part",pacbomBean.isTop?null:pacbomBean.pn,revisionMap);
		createCom.setProperty("object_name", description);
		pacbomBean.itemRevision = createCom.getLatestItemRevision();
		if(pacbomBean.isTop) {
			// 指派专案
			TCComponentProject project = ((PACProjectBean) map.get("project")).getProject();
			project.assignToProject(new TCComponentItemRevision[] {pacbomBean.itemRevision});
		}
	}
	
	private void queryPorjectByUser() {
		try {
			TCComponentUser user = session.getUser();
			TCComponentProjectType projectType = (TCComponentProjectType) session
					.getTypeComponent(ITypeName.TC_Project);
			TCComponentProject[] allProjects = projectType.extent(user, true);
			String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site,"D9_SpringCloud_URL") + "/tc-service/project/queryProjectByPrivilegeUser?userId=" + user.getUserId();
			String json = HttpUtil.get(url);
			JSONArray privilegeList = JSONObject.parseObject(json).getJSONArray("data");

			if (allProjects.length > 0) {
				for (int i = 0; i < allProjects.length; i++) {
					TCComponentProject project = allProjects[i];
					String projectId = project.getProperty("project_id");
					String projectName = project.getProperty("object_name");
					JSONObject privilegeProject = findPrivilegeProject(privilegeList,projectId);
					if(privilegeProject!=null) {
						PACProjectBean projectInfo = new PACProjectBean();
						projectInfo.setId(projectId);
						projectInfo.setName(projectName);
						projectInfo.setProject(project);
						porjectByUser.add(projectInfo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JSONObject findPrivilegeProject(JSONArray privilegeList,String projectId) {
		for (int i = 0; i < privilegeList.size(); i++) {
			JSONObject jsonObject = privilegeList.getJSONObject(i);
			String id = jsonObject.getString("projectId");
			if(projectId.equals(id)) {
				return jsonObject;
			}
		}
		return null;
	}
	
	private void queryHistoryObject() {
		try {
			TCComponent[] executeQuery = TCUtil.executeQuery(session, "__D9_Find_PACPartRev", new String[]{"d9_PartType"}, new String[]{"Assembly"});
//			TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件版本...", new String[] { "items_tag.item_id" }, new String[]{"7G151-002"});
			int length = executeQuery.length;
			for (int i = 0; i < length; i++) {
				TCComponentItemRevision itemRevision = (TCComponentItemRevision) executeQuery[i];
				TCComponentItemRevision latestItemRevision = itemRevision.getItem().getLatestItemRevision();
				if(itemRevision.equals(latestItemRevision)) {
					PACBOMBean pacbomBean = new PACBOMBean();
					pacbomBean.uid = itemRevision.getUid();
					pacbomBean.objectName = itemRevision.getProperty("object_name");
					pacbomBean.initialProject = itemRevision.getProperty("d9_InitialProject");
					pacbomBean.level = itemRevision.getProperty("d9_Level");
					pacbomBean.itemRevision = itemRevision;
					historyTopObject.add(pacbomBean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String matchInitialProject(PACBOMBean pacbomBean) {
		TCComponentItemRevision itemRevision = (pacbomBean).itemRevision;
		if(itemRevision!=null) {
			try {
				String property = itemRevision.getProperty("d9_InitialProject");
				if(StrUtil.isNotEmpty(property)) {
					// 原值
					return property;
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		for (String hpn : historyPN) {
			if(pacbomBean.pn.equals(hpn)) {
				return "历史专案";
			}
		}
		return dialog.cmb_project.getText();
	}
	
	public void onSelectedLevel(CCombo c) {
		changeObjectList();
	}
	
	public void onSelectedProject(CCombo c) {
		changeObjectList();
	}
	
	private void changeObjectList() {
		String level = dialog.cmb_level.getText();
		String project = dialog.cmb_project.getText();
		dialog.cmb_object_name.removeAll();
		for(PACBOMBean pacBomBean : historyTopObject) {
			String revInitialProject = pacBomBean.initialProject;
			String revLevel = pacBomBean.level;
			if(level.equals(revLevel)&&project.equals(revInitialProject)) {
				String key = pacBomBean.objectName.substring(1+pacBomBean.objectName.indexOf("-"));
				dialog.cmb_object_name.add(key);
				dialog.cmb_object_name.setData(key,pacBomBean);
			}
		}
	}
	
	/**
	 * 返回升版规则
	 * 
	 * @param itemRev
	 * @return
	 * @throws TCException
	 */
	private String getVersionRule(TCComponentItemRevision itemRev) throws TCException {
		String version = itemRev.getProperty("item_revision_id");
		String versionRule = "";
		if (version.matches("[0-9]+")) { // 判断对象版本是否为数字版
			versionRule = "NN";
		} else if (version.matches("[a-zA-Z]+")) { // 判断对象版本是否为字母版
			versionRule = "@";
		}
		return versionRule;
	}
	
	private void setProperty(TCComponent tcComponent,String key,String value) {
		try {
			tcComponent.setProperty(key,value);
		} catch (TCException e) {
			try {
				if(tcComponent instanceof TCComponentItemRevision) {
					addToNotModifyList(tcComponent.getProperty("item_id"));
				}else {
					addToNotModifyList(tcComponent.getProperty("bl_item_item_id"));
				}
			} catch (TCException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void removeBomLine(TCComponentBOMLine parentBomLine,TCComponentBOMWindow bomWindow) throws TCException {
		if(TCUtil.isReleased(parentBomLine.getItemRevision())) {
			return;
		}
		AIFComponentContext[] children = parentBomLine.getChildren();
		for (AIFComponentContext aifComponentContext : children) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) aifComponentContext.getComponent();
			removeBomLine(bomLine,bomWindow);
			try {
				bomLine.cut();
			} catch (TCException e1) {
				addToNotModifyList(bomLine.getProperty("bl_item_item_id"));
			}
			bomWindow.save();
		}
	}
	
	private void addToNotModifyList(String s) {
		if(notAllowModify.contains(s)) {
			return;
		}
		notAllowModify.add(s);
	}
	
}
