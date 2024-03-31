package com.foxconn.electronics.project;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.document.ItemInfo;
import com.foxconn.electronics.document.ProjectInfo;
import com.foxconn.electronics.util.HttpUtil;
import com.foxconn.electronics.util.JDBCUtil;
import com.foxconn.electronics.util.TCUtil;
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
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentProjectType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;

@RequestMapping("/project")
public class Controller {
	
	private TCSession session;
	
	private TCComponentBOMLine[] selectTarget;
	
	public Controller(AbstractAIFUIApplication app) throws TCException {
		this.session = (TCSession) app.getSession();
		InterfaceAIFComponent[] targetComponents = app.getTargetComponents();
		selectTarget = new TCComponentBOMLine[targetComponents.length];
		for (int i = 0; i < targetComponents.length; i++) {
			TCComponentBOMLine tcComponent = (TCComponentBOMLine) targetComponents[i];
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
				TCComponent tcComponent = selectTarget[i].getItemRevision();
				String projectIdStr = tcComponent.getProperty("project_ids");
				if (!"".equals(projectIdStr)) {
					String[] projectIds = projectIdStr.split(",");
					for (int j = 0; j < projectIds.length; j++) {
						projectIdList.add(projectIds[j]);
					}
				}
			}
			if (projectIdList.size() == 0) {
				return result = AjaxResult.success("未查询到指派的专案！");
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
	
	/**
	 * 获取所有专案
	 */
	@GetMapping("/getPorjectByUser")
	public AjaxResult getPorjectByUser() {
		AjaxResult result = null;
		try {
			TCComponentUser user = session.getUser();
			List<String> selectList = new ArrayList<>();
	        for (int i = 0; i < selectTarget.length; i++) {
				TCComponentBOMLine tcComponent = (TCComponentBOMLine) selectTarget[i];
				selectList.add(tcComponent.getItemRevision().getProperty("object_string"));
			}
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
		        	if(privilegeList.contains(projectId)) {
		        		ProjectInfo projectInfo = new ProjectInfo();
			        	projectInfo.setId(projectId);
			        	projectInfo.setName(projectName);
			        	projectInfos.add(projectInfo);
		        	}
				}
	        	JSONObject jsonObject = new JSONObject();
	        	jsonObject.put("projectList", projectInfos);
	        	jsonObject.put("selectedList", selectList);
	        	result = AjaxResult.success(jsonObject);                     
	        }else {
	        	result = AjaxResult.error(AjaxResult.STATUS_NO_RESULT,"当前用户未找到参与的项目！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getMessage());
		}
		return result;
	}
	
	@PostMapping("/assignedProject")
	public AjaxResult assignedProject(HttpRequest request){
		AjaxResult result = null;
		try {
			JSONObject parseObject = JSONObject.parseObject(request.getBody());
			JSONArray projectList = parseObject.getJSONArray("projectList");
			String objectType =parseObject.getString("objectType");
			String recursion =parseObject.getString("recursion");
			for (int i = 0; i < projectList.size(); i++) {
				JSONObject jsonObject = projectList.getJSONObject(i);
				String projectIds = jsonObject.getString("projectIds");
				String addOrDel = jsonObject.getString("addOrDel");
				TCComponent[] projectResult = TCUtil.executeQuery(session, "__D9_Find_Project", new String[] {"project_id"}, new String[] {projectIds});
				for (int j = 0; j < projectResult.length; j++) {
					TCComponentProject project = (TCComponentProject) projectResult[j];
					List<TCComponent> list = new ArrayList<>();
					for (int k = 0; k < selectTarget.length; k++) {
						TCComponentBOMLine tcComponent = (TCComponentBOMLine) selectTarget[k];
						if("0".equals(recursion)) {
							list.add("0".equals(objectType)?tcComponent.getItem():tcComponent.getItemRevision());
						}else {
							list = recursion(tcComponent,objectType);	
						}					
					}
					TCComponent[] arrayComponents = new TCComponent[list.size()];
					list.toArray(arrayComponents);		
					if ("0".equals(addOrDel)) {
						project.assignToProject(arrayComponents);				
					}
					if ("1".equals(addOrDel)) {
						project.removeFromProject(arrayComponents);
					}
				}
			}			
			
			result = AjaxResult.success("操作成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getMessage());
		}
		return result;
	}
	
	private List<TCComponent> recursion(TCComponentBOMLine parent,String objectType) throws TCException {
		List<TCComponent> list = new ArrayList<>();
		list.add("0".equals(objectType)?parent.getItem():parent.getItemRevision());
		AIFComponentContext[] children = parent.getChildren();
		if(children.length==0) {
			return list;
		}
		for (int i = 0; i < children.length; i++) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine)children[i].getComponent();
			list.add("0".equals(objectType)?bomLine.getItem():bomLine.getItemRevision());
			recursion(bomLine,objectType);
		}		
		return list;
		
	}
}
