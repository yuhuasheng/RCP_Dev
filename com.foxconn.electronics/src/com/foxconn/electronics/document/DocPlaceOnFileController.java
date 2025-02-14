package com.foxconn.electronics.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sound.midi.Soundbank;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.util.HttpUtil;
import com.foxconn.electronics.util.JDBCUtil;
import com.foxconn.electronics.util.TCUtil;
import com.foxconn.electronics.util.Util;
import com.foxconn.tcutils.util.AjaxResult;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpRequest;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.plm.tc.httpService.jhttp.RequestParam;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentProjectType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;
import com.teamdev.jxbrowser.deps.com.google.common.base.Objects;

@RequestMapping("/document")
public class DocPlaceOnFileController {
	
	private TCSession session;
	private String currentGroupName;
	private TCComponent[] selectTarget;
	private DocPlaceOnFileService docPlaceOnFileService;
	
	public DocPlaceOnFileController(AbstractAIFUIApplication app) throws TCException {
		this.session = (TCSession) app.getSession();
		docPlaceOnFileService = new DocPlaceOnFileService(session);
		currentGroupName = session.getGroup().getGroupName();
		InterfaceAIFComponent[] targetComponents = app.getTargetComponents();
		selectTarget = new TCComponent[targetComponents.length];
		for (int i = 0; i < targetComponents.length; i++) {
			TCComponent tcComponent = (TCComponent) targetComponents[i];
			selectTarget[i] = tcComponent;
		}
	}
	
	/**
	 * 获取当前选中对象
	 */
	@GetMapping("/getSelectTarget")
	public AjaxResult getSelectTarget() {
		AjaxResult result = null;
		try {
			ItemInfo[] itemInfos = new ItemInfo[selectTarget.length];
			for (int i = 0; i < selectTarget.length; i++) {
				TCComponent tcComponent = selectTarget[i];
				String puid = tcComponent.getUid();
				String id = tcComponent.getProperty("item_id");
				String type = tcComponent.getClassType();
				String name = tcComponent.getProperty("object_string");
				ItemInfo itemInfo = new ItemInfo();
				itemInfo.setPuid(puid);
				itemInfo.setId(id);
				itemInfo.setType(type);
				itemInfo.setName(name);
				itemInfos[i] = itemInfo;
			}
			result = AjaxResult.success(itemInfos);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取所有专案
	 */
	@GetMapping("/getPorjectByUser")
	public AjaxResult getPorjectByUser() {
		AjaxResult result = null;
		try {
			TCComponentUser user = session.getUser();
			user.refresh();
			TCComponentProjectType projectType = (TCComponentProjectType) session.getTypeComponent(ITypeName.TC_Project);			
	        TCComponentProject[] allProjects = projectType.extent(user, true);
	        String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL") + "/tc-service/project/queryProjectByPrivilegeUser?userId="+user.getUserId();
	        String json = HttpUtil.httpGet(url, 1000*10);
	        JSONArray privilegeList = JSONObject.parseObject(json).getJSONArray("data");	        
	        List<ProjectInfo> projectInfos = new ArrayList<>();
	        if(allProjects.length > 0) {
	        	for (int i = 0; i < allProjects.length; i++) {
		        	TCComponentProject project = allProjects[i];		        	
		        	String projectId = project.getProperty("project_id");
		        	String projectName = project.getProperty("object_name");
		        	JSONObject privilegeProject = findPrivilegeProject(privilegeList,projectId);
		        	if(privilegeProject!=null) {
		        		ProjectInfo projectInfo = new ProjectInfo();
			        	projectInfo.setId(projectId);
			        	projectInfo.setName(projectName);
			        	projectInfo.setSeries(privilegeProject.getString("projectSeries"));
			        	projectInfo.setProductLine(privilegeProject.getString("productLine"));
			        	projectInfos.add(projectInfo);
		        	}
				}
	        	result = AjaxResult.success(projectInfos);               
	        }else {
	        	result = AjaxResult.error(AjaxResult.STATUS_NO_RESULT,"当前用户未找到参与的项目！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getMessage());
		}
		return result;
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
	
	/**
	 * 根据文档获取指派的专案
	 * @return 
	 */
	@GetMapping("/getPorjectByDoc")
	public AjaxResult getPorjectByDoc() {
		AjaxResult result = null;
		try {
			//**********************获取选中对象已指派的专案ID**********************
			List<String> projectIdList = new ArrayList<String>();
			for (int i = 0; i < selectTarget.length; i++) {
				TCComponent tcComponent = selectTarget[i];
				String projectIdStr = tcComponent.getProperty("project_ids");
				if (!"".equals(projectIdStr)) {
					String[] projectIds = projectIdStr.split(",");
					for (int j = 0; j < projectIds.length; j++) {
						projectIdList.add(projectIds[j]);
					}
				}
			}
			if (projectIdList.size() == 0) {
				return result = AjaxResult.error(AjaxResult.STATUS_NO_RESULT,"未查询到指派的专案！");
			}
			//**********************多个专案可能存在指派相同的专案，去重**********************
			projectIdList = projectIdList.stream().distinct().collect(Collectors.toList());
			String projectIdStr = projectIdList.stream().collect(Collectors.joining(";"));
			TCComponent[] queryResult = TCUtil.executeQuery(session, "__D9_Find_Project", new String[] {"project_id"}, new String[] {projectIdStr});
    		ProjectInfo[] projectInfos = null;
    		if (queryResult.length > 0) {
    			projectInfos = new ProjectInfo[queryResult.length];
				for (int i = 0; i < queryResult.length; i++) {
					TCComponent tcComponent = queryResult[i];
					String projectId = tcComponent.getProperty("project_id");
					String projectName = tcComponent.getProperty("object_name");
					ProjectInfo projectInfo = new ProjectInfo();
		        	projectInfo.setId(projectId);
		        	projectInfo.setName(projectName);
		        	projectInfos[i] = projectInfo;
				}
				result = AjaxResult.success(projectInfos);
			}	
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getMessage());
		}
		return result;
	}
	
	@GetMapping("/assignedProject")
	public AjaxResult assignedProject(@RequestParam("projectIds") String projectIds, @RequestParam("addOrDel") String addOrDel) {
		AjaxResult result = null;
		try {
			TCUtil.setBypass(session);
			TCComponent[] queryResult = TCUtil.executeQuery(session, "__D9_Find_Project", new String[] {"project_id"}, new String[] {projectIds});
			for (int i = 0; i < queryResult.length; i++) {
				TCComponentProject project = (TCComponentProject) queryResult[i];
				if ("0".equals(addOrDel)) {
					project.assignToProject(selectTarget);
				}
				if ("1".equals(addOrDel)) {
					project.removeFromProject(selectTarget);
				}
			}
			result = AjaxResult.success("操作成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getMessage());
		}finally {
			TCUtil.closeBypass(session);
		}
		
		return result;
	}
	
	@GetMapping("/getProjectFolderStructure")
	public AjaxResult getProjectFolderStructure(@RequestParam("projectIds") String projectIds) {
		AjaxResult result = null;
		try {
			TCComponent[] queryResult = TCUtil.executeQuery(session, "__D9_Find_Project_Folder", new String[] {"d9_SPAS_ID"}, new String[] {projectIds});
			List<String> superUsers = TCUtil.getArrayPreference(TCPreferenceService.TC_preference_site, "D9_Project_Bypass_UserID");
			ProjectFolderStructureInfo[] projectFolderStructureInfos = new ProjectFolderStructureInfo[queryResult.length];
			for (int i = 0; i < queryResult.length; i++) {
				TCComponentFolder projectFolder = (TCComponentFolder) queryResult[i];
				String projectPuid = projectFolder.getUid();
				String projectName = projectFolder.getProperty("object_name");
				String projectType = projectFolder.getProperty("object_type");
				String projectId = projectFolder.getProperty("project_ids");
				ProjectFolderStructureInfo projectfolderStructureInfo = new ProjectFolderStructureInfo();
				projectfolderStructureInfo.setPuid(projectPuid);
				projectfolderStructureInfo.setName(projectName);
				projectfolderStructureInfo.setType(projectType);
				projectfolderStructureInfo.setProjectId(projectId);
				AIFComponentContext[] projectChildren = projectFolder.getChildren();
				for (int j = 0; j < projectChildren.length; j++) {
					TCComponent projectChildrenFolder =  (TCComponent) projectChildren[j].getComponent();
					String folderName = projectChildrenFolder.getProperty("object_name");					
					if (superUsers.contains(session.getUser().getUserId()) || folderName.equals(currentGroupName)) {
						ProjectFolderStructureInfo deptfolderStructureInfo = new ProjectFolderStructureInfo();
						String deptPuid = projectChildrenFolder.getUid();
						String deptName = projectChildrenFolder.getProperty("object_name");
						String deptType = projectFolder.getProperty("object_type");
						deptfolderStructureInfo.setPuid(deptPuid);
						deptfolderStructureInfo.setName(deptName);
						deptfolderStructureInfo.setType(deptType);
						List<ProjectFolderStructureInfo> children = projectfolderStructureInfo.getChildren();
						children.add(deptfolderStructureInfo);
						getFolderStructure(projectChildrenFolder,deptfolderStructureInfo);
					}
				}
				projectFolderStructureInfos[i] = projectfolderStructureInfo;
			}
			result = AjaxResult.success(projectFolderStructureInfos);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getMessage());
		}
		return result;
	}
	
	
	@SuppressWarnings("deprecation")
	@PostMapping("/addPlaceOnFile")
	public AjaxResult addPlaceOnFile(HttpRequest request) throws TCException {
		AjaxResult result = null;
		try {			
			TCUtil.setBypass(session);
			String body = request.getBody();
			JSONObject parseObject = JSONObject.parseObject(body);
			String itemPuids = parseObject.getString("itemPuids");
			JSONArray jsonArray = parseObject.getJSONArray("folderInfo");			
			String[] itemPuidArray = itemPuids.split(";");	
			for (int i = 0; i < itemPuidArray.length; i++) {
				String itemPuid = itemPuidArray[i];				
				TCComponent item = session.stringToComponent(itemPuid);
				for (int j = 0; j < jsonArray.size(); j++) {
					JSONObject folderInfo = jsonArray.getJSONObject(j);
					String fodlerId = folderInfo.getString("id");
					String path = folderInfo.getString("path");
					String phaseId = folderInfo.getString("phaseId");
					String parentId = folderInfo.getString("parentId");
					String projectId = folderInfo.getString("projectId");	
					String[] paths = item.getTCProperty("d9_LocationPath").getStringArrayValue();
					List<String> list = new ArrayList<String>();
					for(String pth:paths) {
						list.add(pth);
					}
					TCComponent folder = session.stringToComponent(fodlerId);	
					String type = folder.getType();
					if("Folder".equals(type)) {
						result = skuSchematicDiagramArchive(path,phaseId,item,list);
						if(result!=null) {
							return result;
						}	
					}
					folder.add("contents", item);
					new Thread() {
						public void run() {
							if (path.contains("Layout")) {
								docPlaceOnFileService.doPlacement(item, projectId, path);
							}
						}
					}.start();
					// 把新路径记录到属性
					if(!list.contains(path)) {
					   list.add(path);
					}
					paths = new String[list.size()];					
					list.toArray(paths);
					item.getTCProperty("d9_LocationPath").setStringValueArray(paths);	
				}
			}
			result = AjaxResult.success("归档成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getMessage());
		}finally {
			TCUtil.closeBypass(session);
		}
		return result;
	}
	
	/**
	 * 2023-04-27 李总提的需求：
	 * 用户组时monitor.EE||PSU时要在对应阶段的机种文件夹下归档
	 */
	private AjaxResult skuSchematicDiagramArchive(String path,String phaseId,TCComponent item,List<String> list) throws Exception {
		// 将item加入到指定的路径后，还有归档到报告路径
		String reportFolderName = null;
		TCComponentGroup currentGroup = session.getCurrentGroup();
		String fullName = currentGroup.getFullName();
		String[] split = fullName.split("\\.");
		String dept =  split[0];
		if("EE".equals(dept)) {
			reportFolderName = "EE Schematics(IF+Keypad)";
		}
		if("PSU".equals(dept)) {
			reportFolderName = "PI Schematics(PI)";
		}
		if(reportFolderName!=null) {
			if(path.contains(reportFolderName)){
//				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "机种下原理图无法归档，请从对应专案阶段的机种种类文件夹下归档");
				return null;
			}
			// 归档到报告路径
			TCComponentFolder phaseFolder = (TCComponentFolder) session.stringToComponent(phaseId);
			AIFComponentContext[] children = phaseFolder.getChildren();
			TCComponentFolder reportFolder = null;
			for(AIFComponentContext context : children) {
				String type = context.getComponent().getType();
				String name = context.getComponent().getProperty("object_name");
				if("D9_ArchiveFolder".equals(type)&&reportFolderName.equals(name)) {
					reportFolder = (TCComponentFolder) context.getComponent();
					break;
				}
			}
			if(reportFolder!=null) {
				
				try {
					reportFolder.add("contents", item);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// 拼接新路径
				String phaseName = phaseFolder.getProperty("object_name");
				String separator = phaseName;
				separator = separator.replace("(", "\\(");
				separator = separator.replace(")", "\\)");
				String reportArchivePath = path.split(separator)[0] + phaseName + "/" +reportFolderName;
				if(!list.contains(reportArchivePath)) {
					list.add(reportArchivePath);
				}
			}
		}
		return null;
	}
	
	/**
	 * 2023-04-27 李总提的需求：
	 * 用户组时monitor.EE||PSU时要移除机种文件夹下对应的Item
	 */
	private AjaxResult removeSkuSchematicDiagramArchive(String path,String phaseId,TCComponent item) throws Exception {
		// 将item移除到指定的路径后，还有移除到报告路径
		String reportFolderName = null;
		TCComponentGroup currentGroup = session.getCurrentGroup();
		String fullName = currentGroup.getFullName();
		String[] split = fullName.split("\\.");
		if(split.length < 3) {
			return null;
		}
		String dept =  split[0].toUpperCase();
		String bu =  split[2].toLowerCase();
		if("EE".equals(dept)&&"monitor".equals(bu)) {
			reportFolderName = "EE Schematics(IF+Keypad)";
		}
		if("PSU".equals(dept)&&"monitor".equals(bu)) {
			reportFolderName = "PI Schematics(PI)";
		}
		if(reportFolderName!=null) {
			// 归档到报告路径
			TCComponentFolder phaseFolder = (TCComponentFolder) session.stringToComponent(phaseId);
			AIFComponentContext[] children = phaseFolder.getChildren();
			TCComponentFolder reportFolder = null;
			for(AIFComponentContext context : children) {
				String type = context.getComponent().getType();
				String name = context.getComponent().getProperty("object_name");
				if("D9_ArchiveFolder".equals(type)&&reportFolderName.equals(name)) {
					reportFolder = (TCComponentFolder) context.getComponent();
					break;
				}
			}
			if(reportFolder!=null) {
				if(path.contains(reportFolderName)){
//					return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "机种下原理图无法移除，请从对应专案阶段的机种种类文件夹下移除");
					return null;
				}
				Map<String,String> map = new HashMap<String, String>();
				map.put("path", "");
				deleteReportItem(reportFolder,item,map);
				if(map.get("isDeleted")!=null) {
					// 拼接新路径
					String phaseName = phaseFolder.getProperty("object_name");
					String separator = phaseName;
					separator = separator.replace("(", "\\(");
					separator = separator.replace(")", "\\)");
					return AjaxResult.success( path.split(separator)[0] + phaseName + map.get("path"));
				}
				
			}
		}
		
		return null;
	}
	
	@PostMapping("/removePlaceOnFile")
	public AjaxResult removePlaceOnFile(HttpRequest request) throws TCException {
		AjaxResult result = null;
		try {
			TCUtil.setBypass(session);
			String body = request.getBody();
			JSONArray parseArray = JSONObject.parseArray(body);						
			for(int i=0;i<parseArray.size();i++) {
				JSONObject jsonObject = parseArray.getJSONObject(i);
				String itemId = jsonObject.getString("itemId");
				String folderId = jsonObject.getString("folderId");
				String path = jsonObject.getString("path");
				String reportArchivePath = null;
				String phaseId = jsonObject.getString("phaseId");
				TCComponent item = session.stringToComponent(itemId);
				TCComponent folder = session.stringToComponent(folderId);	
				String type = folder.getType();
				if("Folder".equals(type)) {
					result = removeSkuSchematicDiagramArchive(path,phaseId,item);
					if(result!=null) {
						String code = (String) result.get(AjaxResult.CODE_TAG);
						if(AjaxResult.STATUS_SUCCESS.equals(code)) {
							reportArchivePath = (String) result.get(AjaxResult.MSG_TAG);
						}else {
							return result;
						}
					}
				}
				folder.remove("contents", item);
				TCProperty tcProperty = item.getTCProperty("d9_LocationPath");
				if(tcProperty == null) {
					continue;
				}
				String[] values = tcProperty.getStringArrayValue();
				List<String> list = new ArrayList<String>();
				for(String pth:values) {
					list.add(pth);
				}
				Iterator<String> it = list.iterator();
				while (it.hasNext()) {
					String echo = it.next();
					if(Objects.equal(echo, path)||Objects.equal(echo, reportArchivePath)) {
						it.remove();
					}
				}
				values = new String[list.size()];
				list.toArray(values);
				tcProperty.setStringValueArray(values);	
			}
			result = AjaxResult.success("移除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getMessage());
		}finally {
			TCUtil.closeBypass(session);
		}
		return result;
	}
	
	private void deleteReportItem(TCComponentFolder parentFolder,TCComponent tcComponent,Map<String,String> map) throws TCException {
		
		String path = map.get("path");
		path += "/";
		path += parentFolder.getProperty("object_name");
		map.put("path", path);
		AIFComponentContext[] children = parentFolder.getChildren();
		for(AIFComponentContext context : children) {			
			if(context.getComponent() instanceof TCComponentItemRevision || context.getComponent() instanceof TCComponentItem) {
				if(context.getComponent().equals(tcComponent)) {
					// 删除
					parentFolder.remove("contents", tcComponent);
					map.put("isDeleted", "true");
					return;
				}
			}
		}
		
		for(AIFComponentContext context : children) {
			// 下一层
			if(map.get("isDeleted")!=null) {
				return;
			}
			if(context.getComponent() instanceof TCComponentFolder) {
				deleteReportItem((TCComponentFolder) context.getComponent(),tcComponent,map);
			}
		}	
		
	}
	
	private void getFolderStructure(TCComponent prentFolder, ProjectFolderStructureInfo  projectfolderStructureInfo) throws Exception {
		prentFolder.refresh();
		String puid = prentFolder.getUid();
		String name = prentFolder.getProperty("object_name");
		projectfolderStructureInfo.setPuid(puid);
		projectfolderStructureInfo.setName(name);
		List<ProjectFolderStructureInfo> projectfolderStructureInfoList = projectfolderStructureInfo.getChildren();
		AIFComponentContext[] children = prentFolder.getChildren();
		if (children.length > 0) {
			for (int i = 0; i < children.length; i++) {
				InterfaceAIFComponent component = children[i].getComponent();
				TCComponent folder = (TCComponent) component;
				String childrenPuid = folder.getUid();
				String childrenName = folder.getProperty("object_string");
				String childrenType = folder.getClassType();
				ProjectFolderStructureInfo folderStructureInfo = new ProjectFolderStructureInfo();
				folderStructureInfo.setPuid(childrenPuid);
				folderStructureInfo.setName(childrenName);
				folderStructureInfo.setType(childrenType);
				projectfolderStructureInfoList.add(folderStructureInfo);
				if(folder instanceof TCComponentItem || folder instanceof TCComponentItemRevision) {
					continue;
				}
				getFolderStructure(folder, folderStructureInfo);
			}
		}
	}
	
}
