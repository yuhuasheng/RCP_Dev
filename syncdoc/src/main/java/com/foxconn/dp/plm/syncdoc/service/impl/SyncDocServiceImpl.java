package com.foxconn.dp.plm.syncdoc.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.hutool.log.LogFactory;
import com.foxconn.dp.plm.syncdoc.App;
import com.foxconn.dp.plm.syncdoc.SyncRunnable;
import com.foxconn.dp.plm.syncdoc.domain.DMSDocInfo;
import com.foxconn.dp.plm.syncdoc.domain.DocRevInfo;
import com.foxconn.dp.plm.syncdoc.domain.DocRevInfo1;
import com.foxconn.dp.plm.syncdoc.domain.FolderStructure;
import com.foxconn.dp.plm.syncdoc.domain.ItemInfo;
import com.foxconn.dp.plm.syncdoc.domain.Project;
import com.foxconn.dp.plm.syncdoc.domain.ProjectFolderRefInfo;
import com.foxconn.dp.plm.syncdoc.domain.ProjectInfo;
import com.foxconn.dp.plm.syncdoc.service.SyncDocService;
import com.foxconn.dp.plm.syncdoc.teamcenter.clientx.AppXSession;
import com.foxconn.dp.plm.syncdoc.utils.TCUtils;
import com.teamcenter.services.strong.administration.PreferenceManagementService;
import com.teamcenter.services.strong.administration._2012_09.PreferenceManagement;
import com.teamcenter.services.strong.administration._2012_09.PreferenceManagement.GetPreferencesResponse;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.Dataset;
import com.teamcenter.soa.client.model.strong.Folder;
import com.teamcenter.soa.client.model.strong.Item;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.client.model.strong.TC_Project;
import com.teamcenter.soa.client.model.strong.WorkspaceObject;

public class SyncDocServiceImpl implements SyncDocService {
	
	private static final SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Map<String, List<String>> noTcAccDeptMap = new HashMap<String, List<String>>();
	
	static {
		getNoTcAccDept(App.tcSession);
	}
	
	@Override
	public void syncFolders() throws Exception {
		getNoTcAccDept(App.tcSession);//获取无TC账号部门
		List<String> spasIdList = App.syncDocMapper.getTCProjectFolderSPASId();

		//过滤p1925之前的专案(测试区p1925之前的专案不在同步)
//		List<String> spasIdList = filterProject(spasIdList1);
//		String str = "p2091, p2094, p2050, p2093, p2096, p2052, p2095, p2051, p2010, p2053, p2056, p2058, p1963, p2018, p1965, p1964, p2019, p1925, p1969, p1968, p1927, p1926, p1929, p1370222, p1928, p2061, p2063, p2062, p2021, p2020, p2064, p2067, p2069, p2068, p1971, p1976, p1931, p1975, p1978, p1977, p1979, p2070, p2072, p2071, p2074, p2073, p2076, p2075, p2078, p2077, p13701, p2079, p2037, p2039, p2081, p2080, p2083, p2082, p2085, p2084, p2040, p2087, p2043, p2086, p2042, p2089, p2045, p2088, p2044, p2047, p2046, p2049, p2048, p1958, p1957, p1959";
//		Set<String> set = Stream.of(str.split(",")).map(String::trim).collect(Collectors.toSet());
//		Set<String> old = CollUtil.newHashSet("p2076");
//		List<String> spasIdList = spasIdList1.stream().filter(item -> old.contains(item)).collect(Collectors.toList());

//		for (int i = 0; i < spasIdList.size(); i++) {
//			String spasId = spasIdList.get(i);
//			int dmsProjectFolderId = App.syncDocMapper.getDMSProjectFolderId(spasId);
//			List<Integer> dmsProjectFolderParentId = App.syncDocMapper.getDMSProjectFolderParentId(dmsProjectFolderId);
//			List<Integer> dmsProjectFolderChildId = App.syncDocMapper.getDMSProjectFolderChildId(dmsProjectFolderId);
//			List<Integer> hebing = new ArrayList<Integer>();
//			hebing.addAll(dmsProjectFolderParentId);
//			hebing.addAll(dmsProjectFolderChildId);
//			List<Integer> collect = hebing.stream().distinct().collect(Collectors.toList());
//			App.syncDocMapper.updateDMSFolderRef(dmsProjectFolderChildId);
//			App.sqlSession.commit();
//		}

		LogFactory.get().info("TC系统专案数量：" + spasIdList.size());
		LogFactory.get().info("TC系统专案唯一标识【SPAS_ID】：" + spasIdList);

		//sysnNoAccountData(spasIdList);
		syncProjectFolder();
		getProjectFolderStructureDifferenceData(spasIdList);
		getProjectDocStructureDifferenceData(spasIdList);
		getProjectDocRevStructureDifferenceData(spasIdList);
		getProjectDocRevIssueStateDifferenceData(spasIdList);
//		threadSync(spasIdList);
	}

	private void threadSync(List<String> spasIdList){
		ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(500));

		CountDownLatch countDownLatch = new CountDownLatch(spasIdList.size());
		for (String spasId : spasIdList) {
			poolExecutor.execute(new SyncRunnable(spasId,countDownLatch,this));
		}
		try {
			// 等待計數器歸零
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		poolExecutor.shutdown();
		poolExecutor.shutdownNow();
	}
	
	private List<String> filterProject(List<String> spasIdList){
		Set<String> spasIds = new HashSet<>();
		for (int i = 0; i < spasIdList.size(); i++) {
			String spasId = spasIdList.get(i);
			int spasIdSub = Integer.valueOf(spasId.substring(1, spasId.length()));
			if(spasIdSub >= 1925) {
				spasIds.add(spasId);
			}
		}
		return new ArrayList<>(spasIds);
	}

	public void sysnNoAccountData(List<String> spasIdList) throws Exception{
		Map<String, List<String>> buAndDepartmen = getNoAccountDepartmen(App.tcSession);
		for (Entry<String, List<String>> i : buAndDepartmen.entrySet()) {
			String key = i.getKey();
			List<String> value = i.getValue();
			System.out.println("bu:" + key + ",departmen" + value);
		}
		for (int i = 0; i < spasIdList.size(); i++) {
			String spasId = spasIdList.get(i);
			int dmsProjectCount = App.syncDocMapper.getDMSProjectCount(spasId);
			if(dmsProjectCount == 0) {
				continue;
			}
			ModelObject[] queryResult = TCUtils.executeQuery(App.tcSession,"__D9_Find_Series_Folder", 
					new String[] {"D9_PlatformFoundFolder:contents.d9_SPAS_ID"}, new String[] {spasId});
			if (queryResult.length == 0) {
				continue;
			}
			Folder folder = (Folder) queryResult[0];
			TCUtils.loadProperty(App.tcSession, folder, "object_desc");
			String bu = folder.get_object_desc();
			
			if("MNT".equals(bu)) {
				continue;
			}
			List<String> departments = null;
			if ("DT".equals(bu)) {
				departments = buAndDepartmen.get("DT");
			}
			if ("PRT".equals(bu)) {
				departments = buAndDepartmen.get("PRT");
			}
			if(departments == null) {
				continue;
			}
			int dmsProjectFolderId = App.syncDocMapper.getDMSProjectFolderId(spasId);
			List<Integer> dmsNoAccountDataId = App.syncDocMapper.getDMSNoAccountDataId(dmsProjectFolderId,departments);
			if(dmsNoAccountDataId != null && dmsNoAccountDataId.size() != 0) {
				App.syncDocMapper.setDMSNoAccountDataSource(dmsNoAccountDataId);
			}
			List<Integer> dmsHasAccountDataId = App.syncDocMapper.getDMSHasAccountDataId(dmsProjectFolderId,departments);
			if(dmsHasAccountDataId != null && dmsHasAccountDataId.size() != 0) {
				App.syncDocMapper.setDMSHasAccountDataSource(dmsHasAccountDataId);
			}
		}
		App.sqlSession.commit();
	}
	
	private static void getNoTcAccDept(AppXSession session){
		try {
			String[] noTcAccDept = getTCPreferences(session,"D9_TC_NoAccount_Department");
			for (int i = 0; i < noTcAccDept.length; i++) {
				String[] buAndDept = noTcAccDept[i].split("=");
				String bu = buAndDept[0];
				String dept = buAndDept[1];
				List<String> deptList = Arrays.asList(dept.split(","));
				noTcAccDeptMap.put(bu, deptList);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getProjectBu(String spasId) throws Exception {
		ModelObject[] queryResult = TCUtils.executeQuery(App.tcSession,"__D9_Find_Series_Folder", 
				new String[] {"D9_PlatformFoundFolder:contents.d9_SPAS_ID"}, new String[] {spasId});
		if (queryResult.length == 0) {
			throw new Exception("【" + spasId + "】专案未找到BU.");
		}
		Folder folder = (Folder) queryResult[0];
		TCUtils.loadProperty(App.tcSession, folder, "object_desc");
		return folder.get_object_desc();
	}
	
	private Map<String, List<String>> getNoAccountDepartmen(AppXSession session) throws Exception{
		Map<String, List<String>> buAndDepartmen= new HashMap<String, List<String>>();
		String[] noAccountDepartment = getTCPreferences(session,"D9_TC_NoAccount_Department");
		for (int i = 0; i < noAccountDepartment.length; i++) {
			String buAndDepartment = noAccountDepartment[i];
			String[] buAndDepartment1 = buAndDepartment.split("=");
			String bu = buAndDepartment1[0];
			String department = buAndDepartment1[1];
			List<String> departmentList = Arrays.asList(department.split(","));
			if ("DT".equals(bu)) {
				buAndDepartmen.put("DT", departmentList);
			}
			if ("PRT".equals(bu)) {
				buAndDepartmen.put("PRT", departmentList);
			}
		}
		return buAndDepartmen;
	}
	
	@Override
	public void syncProjectFolder() throws Exception{
		LogFactory.get().info("同步TC系统专案开始...");
		
		//-------同步TC专案名称修改-----------
		List<ProjectInfo> tcdmsProjectInfo = App.syncDocMapper.getTCDMSProjectInfo();
		List<ProjectInfo> needChangeProject = new ArrayList<ProjectInfo>();
		for (int i = 0; i < tcdmsProjectInfo.size(); i++) {
			ProjectInfo projectInfo = tcdmsProjectInfo.get(i);
			String tcProjectName = projectInfo.getTcProjectName();
			String dmsProjectName = projectInfo.getDmsProjectName();
			if(!tcProjectName.equals(dmsProjectName)) {
				needChangeProject.add(projectInfo);
			}
		}
		if(needChangeProject.size() > 0) {
			App.syncDocMapper.changeProjectName(needChangeProject);
			App.syncDocMapper.changeFolderName(needChangeProject);
			App.sqlSession.commit();
		}
		//-------同步TC专案名称修改-----------
		
		Map<String, Object> procedureMap1 = new HashMap<>();
		procedureMap1.put("tc_to_docManSys", null);
		App.syncDocMapper.getProjectDifference(procedureMap1);
		List<Map<String, String>> tcToDocManSysList = (List<Map<String, String>>) procedureMap1.get("tc_to_docManSys");
		List<String> spasIdList = tcToDocManSysList.stream().flatMap(e->e.values().stream()).collect(Collectors.toList());
		
//		Iterator<String> it = spasIdList.iterator();
//		while (it.hasNext()) {
//			String spasId = it.next();
//			if("p1775".equals(spasId) || "p1776".equals(spasId) || "p1774".equals(spasId)
//					|| "p1778".equals(spasId) || "p1785".equals(spasId)) {
//				it.remove();
//			}
//		}

		LogFactory.get().info("专案差异数据数量：" + spasIdList.size());
		if(spasIdList == null || spasIdList.size() == 0) {
			LogFactory.get().info("同步TC系统专案结束...");
			return;
		}
		LogFactory.get().info("专案差异数据【SPAS_ID】：" + spasIdList);
		List<Project> projectInfoList = App.syncDocMapper.getProjectInfo(spasIdList);
		LogFactory.get().info("专案差异数据信息：" + projectInfoList);
		Map<String, String> procedureMap2 = new HashMap<>();
		for (int i = 0; i < projectInfoList.size(); i++) {
			Project projectInfo = projectInfoList.get(i);
			procedureMap2.clear();
			procedureMap2.put("spas_id", projectInfo.getSpasId());
			procedureMap2.put("proj_name", projectInfo.getName());
			App.syncDocMapper.TCProjectToDMS(procedureMap2);
		}
		LogFactory.get().info("同步TC系统专案结束...");
	}

	@Override
	public void getProjectFolderStructureDifferenceData(List<String> spasIds) throws Exception {
		LogFactory.get().info("同步专案结构开始...");
		for (int n = 0; n < spasIds.size(); n++) {
			String spasId = spasIds.get(n);
			LogFactory.get().info("同步【" + spasId +"】专案结构开始...");
			Map<String, Object> procedureMap = new HashMap<>();
			procedureMap.put("spas_id", spasId);
			procedureMap.put("tc_to_docManSys", null);
			procedureMap.put("docManSys_to_tc", null);
			App.syncDocMapper.getFolderStructureDifference(procedureMap);
			
			List<Map<String, String>> tcToDocManSysList = (List<Map<String, String>>) procedureMap.get("tc_to_docManSys");
			LogFactory.get().info("TC系统、文件管理系统，结构差异数据数量：" + tcToDocManSysList.size());
			List<FolderStructure> toDocManSysList = null;
			if(tcToDocManSysList.size() > 0) {
				LogFactory.get().info("TC系统、文件管理系统，结构差异数据信息：" + tcToDocManSysList);
				toDocManSysList = new ArrayList<FolderStructure>();
				for (int i = 0; i < tcToDocManSysList.size(); i++) {
					FolderStructure folderStructure = new FolderStructure();
					Map<String, String> tcToDocManSysMap = tcToDocManSysList.get(i);
					Object projLevel = tcToDocManSysMap.get("projLevel");
					folderStructure.setLevel(String.valueOf(projLevel));
					folderStructure.setParentName(tcToDocManSysMap.get("parentname"));
					folderStructure.setChildName(tcToDocManSysMap.get("childname"));
					folderStructure.setFolderPath(tcToDocManSysMap.get("pfolderpath"));
					toDocManSysList.add(folderStructure);
				}
				int dmsProjectId = App.syncDocMapper.getDMSProjectFolderId(spasId);
				syncFoldersFromTC(spasId,dmsProjectId, toDocManSysList);
				LogFactory.get().info("TC系统【"+ spasId +"】专案结构同步到文件管理系统结束...");
			}else {
				LogFactory.get().info("TC没有可同步的专案结构到文件管理系统...");
			}
			
			List<Map<String, String>> docManSysToTcList = (List<Map<String, String>>) procedureMap.get("docManSys_to_tc");
			LogFactory.get().info("文件管理系统、TC系统，结构差异数据数量：" + docManSysToTcList.size());
			List<FolderStructure> toTCList = null;
			if(docManSysToTcList.size() > 0) {
				LogFactory.get().info("文件管理系统、TC系统，结构差异数据信息：" + docManSysToTcList);
				toTCList = new ArrayList<FolderStructure>();
				for (int i = 0; i < docManSysToTcList.size(); i++) {
					FolderStructure folderStructure = new FolderStructure();
					Map<String, String> docManSysToTcMap = docManSysToTcList.get(i);
					Object projLevel = docManSysToTcMap.get("projLevel");
					folderStructure.setLevel(String.valueOf(projLevel));
					folderStructure.setParentName(docManSysToTcMap.get("parentname"));
					folderStructure.setChildName(docManSysToTcMap.get("childname"));
					folderStructure.setFolderPath(docManSysToTcMap.get("pfolderpath"));
					toTCList.add(folderStructure);
				}
				String tcProjectId = App.syncDocMapper.getTCProjectFolderPUID(spasId);
				syncFoldersToTC(spasId,tcProjectId, toTCList);
				LogFactory.get().info("文件管理系统【"+ spasId +"】专案结构同步到TC系统结束...");
			}else {
				LogFactory.get().info("文件管理系统没有可同步的专案结构到TC...");
			}
			LogFactory.get().info("同步【" + spasId +"】专案结构结束...");
		}
		LogFactory.get().info("同步专案结构结束...");
	}

	@Override
	public void syncFoldersToTC(String spasId, String tcProjectId, List<FolderStructure> toTCList) throws Exception {
		int projectFolderId = App.syncDocMapper.getDMSProjectFolderId(spasId);
		Map<String, Object> procedureMap = new HashMap<>();
		for (int i = 0; i < toTCList.size(); i++) {
			FolderStructure folderStructure = toTCList.get(i);
			String level = folderStructure.getLevel();
			String parentName = folderStructure.getParentName();
			String childName = folderStructure.getChildName();
			String folderPath = folderStructure.getFolderPath();
			System.out.println("projectFolderId：" + projectFolderId + ",level:" + level + ",parentName:" + parentName + ",childName:" + childName + ",folderPath:" + folderPath);
			ProjectFolderRefInfo dmsFolderRef = App.syncDocMapper.getDMSFolderRef(String.valueOf(projectFolderId),level,parentName,childName,folderPath);
			Integer folderId = dmsFolderRef.getFolderId();
			Integer refType = dmsFolderRef.getRefType();
			String refId = dmsFolderRef.getRefId();
			if(refType == 0) {
				if(refId == null) {
					//如果REF_TYPE等于0，REF_ID等于null(添加数据)
					procedureMap.clear();
					procedureMap.put("project_id", tcProjectId);
					procedureMap.put("structure_level", level);
					procedureMap.put("parent_name", parentName);
					procedureMap.put("child_name", childName);
					procedureMap.put("folder_path", folderPath);
					System.out.println("tcProjectId-->" + tcProjectId + ",level-->" + level + ",parentName-->" + parentName + ",childName-->" + childName + ",folderPath-->" + folderPath);
					App.syncDocMapper.DMSStructureToTC(procedureMap);
					String parentId = (String) procedureMap.get("parent_id");
					String folderPuid = TCUtils.createTCFolder(App.tcSession,parentId,childName);
					App.syncDocMapper.setFolderRefId(String.valueOf(folderId),folderPuid);
					App.sqlSession.commit();
				}else {
					//如果REF_TYPE等于0，REF_ID不等于null(修改数据)
					ModelObject obj = TCUtils.findObjectByPuid(App.tcSession, refId);
					if(obj != null) {
						TCUtils.setProperties(App.tcSession, obj, "object_name", childName);
					}else {
						procedureMap.clear();
						procedureMap.put("project_id", tcProjectId);
						procedureMap.put("structure_level", level);
						procedureMap.put("parent_name", parentName);
						procedureMap.put("child_name", childName);
						procedureMap.put("folder_path", folderPath);
						System.out.println("tcProjectId-->" + tcProjectId + ",level-->" + level + ",parentName-->" + parentName + ",childName-->" + childName + ",folderPath-->" + folderPath);
						App.syncDocMapper.DMSStructureToTC(procedureMap);
						String parentId = (String) procedureMap.get("parent_id");
						String folderPuid = TCUtils.createTCFolder(App.tcSession,parentId,childName);
						App.syncDocMapper.setFolderRefId(String.valueOf(folderId),folderPuid);
						App.sqlSession.commit();
					}
				}
			}
			if(refType == 1) {
				//如果等于1(删除数据)
				App.syncDocMapper.setFolderDelFlag(String.valueOf(folderId));
				App.sqlSession.commit();
			}
		}
	}

	@Override
	public void syncFoldersFromTC(String spasId, int dmsProjectId, List<FolderStructure> toDocManSysList) throws Exception {
		String tcProjectId = App.syncDocMapper.getTCProjectFolderPUID(spasId);
		Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < toDocManSysList.size(); i++) {
			FolderStructure folderStructure = toDocManSysList.get(i);
			String level = folderStructure.getLevel();
			String parentName = folderStructure.getParentName();
			String childName = folderStructure.getChildName();
			String folderPath = folderStructure.getFolderPath();
			System.out.println("tcProjectId-->" + tcProjectId + ",level-->" + level + ",parentName-->" + parentName + ",childName-->" + childName + ",folderPath-->" + folderPath);
			String tcFolderId = App.syncDocMapper.getTCFolderId(tcProjectId,level,parentName,childName,folderPath);	
			System.out.println("tcFolderId-->" + tcFolderId);
			Map<String, Object> folderDelFlag = App.syncDocMapper.getDMSFolderDelFlag(tcFolderId);
			//数据不存在(添加数据)
			if(folderDelFlag == null) {
				Integer refType = getRefType(spasId, folderPath);
				map.clear();
				map.put("project_id", dmsProjectId);
				map.put("structure_level", level);
				map.put("parent_name", parentName);
				map.put("child_name", childName);
				map.put("folder_path", folderPath);
				map.put("tc_folder_id", tcFolderId);
				map.put("ref_type", refType);
				System.out.println("dmsProjectId-->" + dmsProjectId + ",level-->" + level + ",parentName-->" 
				+ parentName + ",childName-->" + childName + ",folderPath-->" + folderPath + ",tcFolderId:" + tcFolderId);
				App.syncDocMapper.TCStructureToDMS(map); 
			}else {
				String flag = folderDelFlag.get("DEL_FLAG").toString();
				//存在数据，如果DEL_FLAG等于0(修改数据)
				if("0".equals(flag)) {
					// 3/16 新增修改数据逻辑
					if(!childName.equals(folderDelFlag.get("FLD_NAME"))){
						String fldSn = folderDelFlag.get("FLD_SN").toString();
						App.syncDocMapper.updateFolderName(childName,Long.parseLong(fldSn));
					}
					continue;
				}
				if ("1".equals(flag)) {
//					App.syncDocMapper.setFolderDelFlagNum(tcFolderId);
					ModelObject obj = TCUtils.findObjectByPuid(App.tcSession, tcFolderId);
					TCUtils.deleteObject(App.tcSession, new ModelObject[] {obj});
				}
			}
		}
	}
	
	private Integer getRefType(String spasId, String folderPath) throws Exception {
		Integer refType = 1;
		String bu = getProjectBu(spasId);
		List<String> noTcAccDeptList = noTcAccDeptMap.get(bu);
		if(noTcAccDeptList != null) {
			for (int j = 0; j < noTcAccDeptList.size(); j++) {
				String noTcAccDept = noTcAccDeptList.get(j);
				if(folderPath.contains(noTcAccDept)) {
					refType = 0;
					break;
				}
			}
		}
		return refType;
	}
	
	@Override
	public void getProjectDocStructureDifferenceData(List<String> spasIds) throws Exception {
		LogFactory.get().info("全部专案文档同步开始...");
		for (int n = 0; n < spasIds.size(); n++) {
			String spasId = spasIds.get(n);
			LogFactory.get().info("同步【" + spasId +"】专案文档开始...");
			String TCProjectFolderPUID = App.syncDocMapper.getTCProjectFolderPUID(spasId);
			LogFactory.get().info("TC系统专案ID:" + TCProjectFolderPUID);
			int DMSProjectFolderId = App.syncDocMapper.getDMSProjectFolderId(spasId);
			LogFactory.get().info("文件管理系统专案ID:" + DMSProjectFolderId);
			List<String> TCDocItemIds = App.syncDocMapper.getTCProjectFolderDocItemIds(TCProjectFolderPUID);
			LogFactory.get().info("TC专案【" + spasId +"】所有文档数量：" + TCDocItemIds.size());
			if(TCDocItemIds.size() > 0) {
				LogFactory.get().info("TC专案【" + spasId +"】所有文档ID：" + TCDocItemIds);
				List<String> tcToDocManSysList = new ArrayList<String>();
				for (int i = 0; i < TCDocItemIds.size(); i++) {
					String docItemId = TCDocItemIds.get(i);
					int count = App.syncDocMapper.getDMSItemCount(docItemId);
					if(count == 0) {
						tcToDocManSysList.add(docItemId);
					}
				}
				LogFactory.get().info("需同步到文件管理系统的文档数量：" + tcToDocManSysList.size());
				if(tcToDocManSysList.size() > 0) {
					LogFactory.get().info("需同步到文件管理系统的文档ID：" + tcToDocManSysList);
					syncDocumentFromTC(DMSProjectFolderId, TCProjectFolderPUID, tcToDocManSysList);
				}else {
					LogFactory.get().info("TC没有可同步的文档到文件管理系统...");
				}
			}else {
				LogFactory.get().info("TC没有可同步的文档到文件管理系统...");
			}
			
			List<String> DMSDocItemIds = App.syncDocMapper.getDMSProjectFolderDocItemIds(DMSProjectFolderId);
			LogFactory.get().info("文件管理系统专案【" + spasId +"】所有文档数量：" + DMSDocItemIds.size());
			if(DMSDocItemIds.size() > 0) {
				LogFactory.get().info("文件管理系统专案【" + spasId +"】所有文档ID：" + DMSDocItemIds);
				List<String> docManSysToTcList = new ArrayList<String>();
				for (int i = 0; i < DMSDocItemIds.size(); i++) {
					String docItemId = DMSDocItemIds.get(i);
					int count = App.syncDocMapper.getTCItemCount(TCProjectFolderPUID,docItemId);
					if(count == 0) {
						docManSysToTcList.add(docItemId);
					}
				}
				LogFactory.get().info("需同步到TC管理系统的文档数量：" + docManSysToTcList.size());
				if(docManSysToTcList.size() > 0) {
					LogFactory.get().info("需同步到TC管理系统的文档ID：" + docManSysToTcList);
					syncDocumentToTC(spasId,DMSProjectFolderId, TCProjectFolderPUID, docManSysToTcList);
				}else {
					LogFactory.get().info("文件管理系统没有可同步的文档到TC...");
				}
			}else {
				LogFactory.get().info("文件管理系统没有可同步的文档到TC...");
			}
			LogFactory.get().info("同步【" + spasId +"】专案文档结束...");
		}
		LogFactory.get().info("全部专案文档同步结束...");
	}

	@Override
	public void syncDocumentToTC(String spasId,int DMSProjectFolderId, String TCProjectFolderPUID, List<String> docManSysToTcList) throws Exception {
		Map<String, String> propMap = new HashMap<String, String>();
		for (int i = 0; i < docManSysToTcList.size(); i++) {
			String docId = docManSysToTcList.get(i);
			List<ItemInfo> itemInfos = App.syncDocMapper.getDMSItemInfo(String.valueOf(DMSProjectFolderId),docId);
			ItemInfo itemInfo = itemInfos.get(0);
			int level = itemInfo.getPLevel();
			String name = itemInfo.getPName();
			String path = itemInfo.getPPath();
			System.out.println("TCProjectFolderPUID：" + TCProjectFolderPUID + "，level：" + level + "，name：" + name + "，path：" + path);
			String itemParentId = App.syncDocMapper.getTCItemParentId(TCProjectFolderPUID,String.valueOf(level),name,path);
			if ("".equals(itemParentId) || itemParentId == null) {
				continue;
			}
			DMSDocInfo dmsDocInfo = App.syncDocMapper.getDMSDocInfo(docId);
			String docName = dmsDocInfo.getDocName();
			String docType = dmsDocInfo.getDocType();
			propMap.clear();
			propMap.put("d9_DocumentType", docType);
			try {
				Item newItem = null;
				ModelObject[] result = TCUtils.executeQuery(App.tcSession, "Item_Name_or_ID", new String[] {"item_id"}, new String[] {docId});
				if (result.length == 0) {
					newItem = TCUtils.createItems(App.tcSession, docId, "Document", docName, propMap);
				}else {
					newItem = (Item) result[0];
				}
				ItemRevision itemRev = TCUtils.getItemLatestRevision(App.tcSession, newItem);
				String docRev = "01";
				String realAuthor = App.syncDocMapper.getItemRevRealAuthor(docId);
				String uploadDate = App.syncDocMapper.getItemRevUploadDate(docId,docRev);
				TCUtils.setProperties(App.tcSession, itemRev, "d9_ActualUserID", realAuthor);
				TCUtils.setProperties(App.tcSession, itemRev, "d9_UploadDate", uploadDate);
				int desc = App.syncDocMapper.getDMSItemRevId(docId,docRev);
				String dmsFileName = App.syncDocMapper.getDMSFileName(docId,docRev);
				ModelObject obj = TCUtils.findObjectByPuid(App.tcSession, itemParentId);
				TCUtils.createDataset(App.tcSession, itemRev, dmsFileName, String.valueOf(desc));
				TCUtils.addContents(App.tcSession, obj, itemRev, "contents");
				ModelObject[] objs = TCUtils.executeQuery(App.tcSession, "__D9_Find_Project", new String[] {"project_id"}, new String[] {spasId});
				if(objs.length > 0) {
					TCUtils.assignedProject(App.tcSession, itemRev, (TC_Project)objs[0]);
				}
			} catch (Exception e) {
				LogFactory.get().error(e);
			}
		}
	}

	@Override
	public void syncDocumentFromTC(int DMSProjectFolderId, String TCProjectFolderPUID, List<String> tcToDocManSysList) throws Exception{
		Map<String, Object> procedureMap = new HashMap<>();
		for (int i = 0; i < tcToDocManSysList.size(); i++) {
			String itemId = tcToDocManSysList.get(i);
			List<ItemInfo> itemInfos = App.syncDocMapper.getTCItemInfo(TCProjectFolderPUID,itemId);
			ItemInfo itemInfo = itemInfos.get(0);
			int level = itemInfo.getPLevel() - 1;
			String name = itemInfo.getPName();
			String PPath = itemInfo.getPPath();
	    	String path = PPath.substring(0, PPath.lastIndexOf("->"));
	    	System.out.println("DMSProjectFolderId：" + DMSProjectFolderId + ",level：" + level + ",name：" + name + ",path：" + path);
			int itemParentId = App.syncDocMapper.getDMSItemParentId(String.valueOf(DMSProjectFolderId),String.valueOf(level),name,path);
			procedureMap.clear();
			procedureMap.put("ITEM_ID", itemId);
			procedureMap.put("PARENT_ID", itemParentId);
			if("SD3MBCPD002225".equals(itemId) || "SD3MBCPD002223".equals(itemId)
					|| "SD3MBCPD002222".equals(itemId) || "SD3MBCPD002224".equals(itemId)
					|| "SD3MBCPD002216".equals(itemId) 
					|| "SD3MBCPD002220".equals(itemId) || "SD3MBCPD002206".equals(itemId)
					|| "SD3MBCPD002204".equals(itemId) || "SD3MBCPD002208".equals(itemId)
					|| "SD3MBCPD002236".equals(itemId) || "SD3MBCPD002237".equals(itemId)
					|| "SD3MBCPD002235".equals(itemId) || "SD3MBCPD002243".equals(itemId)
					|| "SD3MBCPD002242".equals(itemId) || "SD3MBCPD002241".equals(itemId)
					|| "SD3MBCPD002239".equals(itemId) || "SD3MBCPD002238".equals(itemId)
					|| "SD3MBCPD002240".equals(itemId) || "SD3MBCPD002234".equals(itemId)
					|| "SD3MBCPD002231".equals(itemId) || "SD3MBCPD002230".equals(itemId)
					|| "SD3MBCPD002213".equals(itemId) || "SD3MBCPD002210".equals(itemId)
					|| "SD3MBCPD002214".equals(itemId) || "SD3MBCPD002232".equals(itemId)
					|| "SD3MBCPD002229".equals(itemId) || "SD3MBCPD002233".equals(itemId)
					|| "SD3MBCPD002221".equals(itemId) || "SD3MBCPD002200".equals(itemId) 
					|| "SD3MBCPD002218".equals(itemId) || "SD3MBCPD002217".equals(itemId)
					|| "SD3MBCPD002226".equals(itemId) || "SD3MBCPD002227".equals(itemId)
					|| "SD3MBCPD002228".equals(itemId) || itemId.contains("SD3MBCPD00218")
					|| itemId.contains("SD3MBCPD00219") || itemId.contains("SD3MBCPD00220")
					|| itemId.contains("SD3MBCPD00221") || itemId.contains("SD3MBCPD00222")
					|| itemId.contains("SD3MBCPD00223") || itemId.contains("SD3MBCPD00224")) {
				continue;
			}
			App.syncDocMapper.TCDocToDMS(procedureMap);
		}
	}
	
	@Override
	public void getProjectDocRevStructureDifferenceData(List<String> spasIds) throws Exception {
		LogFactory.get().info("全部专案文档版本同步开始...");
		for (int n = 0; n < spasIds.size(); n++) {
			String spasId = spasIds.get(n);
			LogFactory.get().info("同步【" + spasId +"】专案文档版本开始...");
			String TCProjectFolderPUID = App.syncDocMapper.getTCProjectFolderPUID(spasId);
			LogFactory.get().info("TC系统专案ID:" + TCProjectFolderPUID);
			int DMSProjectFolderId = App.syncDocMapper.getDMSProjectFolderId(spasId);
			LogFactory.get().info("文件管理系统专案ID:" + DMSProjectFolderId);
			
			List<DocRevInfo> TCDocRevInfo = getTCProjectDocRev(TCProjectFolderPUID);
			LogFactory.get().info("TC专案【" + spasId +"】下所有文档版本数量：" + TCDocRevInfo.size());
			LogFactory.get().info("TC专案【" + spasId +"】下所有文档版本信息：" + TCDocRevInfo);
			
			List<DocRevInfo> DMSDocRevInfo = App.syncDocMapper.getDMSProjectDocRev(DMSProjectFolderId);
			LogFactory.get().info("文件管理系统专案【" + spasId +"】下所有文档版本数量：" + DMSDocRevInfo.size());
			LogFactory.get().info("文件管理系统专案【" + spasId +"】下所有文档版本信息：" + DMSDocRevInfo);
			
			App.syncDocMapper.deleteTCDocRevInfo();
			App.sqlSession.commit();
			if(TCDocRevInfo.size() > 0) {
				App.syncDocMapper.addTCDocRevInfo(TCDocRevInfo);
				App.sqlSession.commit();
			}
			App.syncDocMapper.deleteDMSDocRevInfo();
			App.sqlSession.commit();
			if(DMSDocRevInfo.size() > 0) {
				App.syncDocMapper.addDMSDocRevInfo(DMSDocRevInfo);
				App.sqlSession.commit();
			}
			
			Map<String, Object> procedureMap = new HashMap<>();
			procedureMap.put("tc_to_docManSys", null);
			procedureMap.put("docManSys_to_tc", null);
			App.syncDocMapper.getDocRevDifferenceData(procedureMap);
			List<Map<String, String>> tcToDocManSysList = (List<Map<String, String>>) procedureMap.get("tc_to_docManSys");
			List<Map<String, String>> docManSysToTcList = (List<Map<String, String>>) procedureMap.get("docManSys_to_tc");
			
			List<DocRevInfo> tcToDocManSysDocRev = MapConvertObject(tcToDocManSysList);
			LogFactory.get().info("需同步到文件管理系统的文档版本数量：" + tcToDocManSysDocRev.size());
			LogFactory.get().info("需同步到文件管理系统的文档版本信息：" + tcToDocManSysDocRev);
			List<DocRevInfo> docManSysToTcDocRev = MapConvertObject(docManSysToTcList);
			LogFactory.get().info("需同步到TC系统的文档版本数量：" + docManSysToTcDocRev.size());
			LogFactory.get().info("需同步到TC系统的文档版本信息：" + docManSysToTcDocRev);
			
			if(tcToDocManSysDocRev.size() > 0) {
				syncDocRevFromTC(DMSProjectFolderId,tcToDocManSysDocRev);
			}else {
				LogFactory.get().info("TC系统没有可同步的文档版本到文件管理系统...");
			}
			
			if(docManSysToTcDocRev.size() > 0) {
				syncDocRevToTC(spasId,TCProjectFolderPUID,docManSysToTcDocRev);
			}else {
				LogFactory.get().info("文件管理系统没有可同步的文档版本到TC系统...");
			}
			LogFactory.get().info("同步【" + spasId +"】专案文档版本结束...");
		}
		LogFactory.get().info("全部专案文档版本同步结束...");
	}

	@Override
	public void syncDocRevToTC(String spasId,String TCProjectFolderPUID,List<DocRevInfo> differenceData) throws Exception {
		differenceData = differenceData.stream().sorted(Comparator.comparing(DocRevInfo::getDoc_rev)).collect(Collectors.toList());
		if(differenceData.size() > 0) {
			Map<String, Object> procedureMap = new HashMap<>();
			for (int i = 0; i < differenceData.size(); i++) {
				DocRevInfo docRevInfo = differenceData.get(i);
				String docId = docRevInfo.getDoc_id();
				String docRev = docRevInfo.getDoc_rev();
				String docPath = docRevInfo.getDoc_path();
				//0:文件系統;1:TC
				System.out.println("spasId：" + spasId + ",TCProjectFolderPUID:" + TCProjectFolderPUID + ",docId:" + docId + ",docRev:" + docRev + ",docPath:" + docPath);
				int itemRevSource = App.syncDocMapper.getItemRevSource(docId,docRev);
				if(itemRevSource == 0) {
					String TCParentFolderId = App.syncDocMapper.getTCParentFolderId(TCProjectFolderPUID,docPath);
					ModelObject TCParentFolder = TCUtils.findObjectByPuid(App.tcSession, TCParentFolderId);
					String frontDocRev = "0" +(Integer.valueOf(docRev) - 1);
					String rontDocRevPuid = App.syncDocMapper.getTCDocRevId(docId,frontDocRev);
					ModelObject obj = TCUtils.findObjectByPuid(App.tcSession, rontDocRevPuid);
					String itemRevName = App.syncDocMapper.getDMSItemRevName(docId,docRev);
					Integer tcItemRevProcessState = getTCItemRevProcessState(docId,frontDocRev);
					if(tcItemRevProcessState == 1) {
						String workflowName = "TCM Release Process：" + docId + "/" + frontDocRev;
						TCUtils.createNewProcess(App.tcSession, workflowName, "TCM Release Process", new ModelObject[] {obj});
					}
					System.out.println("itemRevName-->" + itemRevName + ",docRev->" + docRev);
					ItemRevision reviseItemRev = (ItemRevision) TCUtils.reviseItemRev(App.tcSession, obj, itemRevName, docRev);
					String uploadDate = App.syncDocMapper.getItemRevUploadDate(docId,docRev);
					TCUtils.setProperties(App.tcSession, reviseItemRev, "d9_UploadDate", uploadDate);
					int desc = App.syncDocMapper.getDMSItemRevId(docId,docRev);
					String dmsFileName = App.syncDocMapper.getDMSFileName(docId,docRev);
					TCUtils.createDataset(App.tcSession, reviseItemRev, dmsFileName, String.valueOf(desc));
					TCUtils.addContents(App.tcSession, TCParentFolder, reviseItemRev, "contents");
					ModelObject[] objs = TCUtils.executeQuery(App.tcSession, "__D9_Find_Project", new String[] {"project_id"}, new String[] {spasId});
					if(objs.length > 0) {
						TCUtils.assignedProject(App.tcSession, reviseItemRev, (TC_Project)objs[0]);
					}
				}else{
					int lastIndexOf = docPath.lastIndexOf("->");
					String pPath = docPath.substring(0, lastIndexOf);
					String cName = docPath.substring(lastIndexOf+2, docPath.length());
					procedureMap.clear();
					procedureMap.put("SPAS_ID", spasId);
					procedureMap.put("P_PATH", pPath);
					procedureMap.put("C_NAME", cName);
					procedureMap.put("DOC_ID", docId);
					App.syncDocMapper.setItemRevDelFlag(procedureMap);
				}
			}
		}
	}
	
	@Override
	public void syncDocRevFromTC(int DMSProjectFolderId,List<DocRevInfo> differenceData) throws Exception {
		Map<String, Object> procedureMap = new HashMap<>();
		for (int i = 0; i < differenceData.size(); i++) {
			DocRevInfo docRevInfo = differenceData.get(i);
			String docId = docRevInfo.getDoc_id();
			String frontDocRev = docRevInfo.getDoc_rev();
			String docPath = docRevInfo.getDoc_path();
			
			if("SD3MBCPD002225".equals(docId) || "SD3MBCPD002223".equals(docId)
					|| "SD3MBCPD002222".equals(docId) || "SD3MBCPD002224".equals(docId)
					|| "SD3MBCPD002216".equals(docId) 
					|| "SD3MBCPD002220".equals(docId) || "SD3MBCPD002206".equals(docId)
					|| "SD3MBCPD002204".equals(docId) || "SD3MBCPD002208".equals(docId)
					|| "SD3MBCPD002236".equals(docId) || "SD3MBCPD002237".equals(docId)
					|| "SD3MBCPD002235".equals(docId) || "SD3MBCPD002243".equals(docId)
					|| "SD3MBCPD002242".equals(docId) || "SD3MBCPD002241".equals(docId)
					|| "SD3MBCPD002239".equals(docId) || "SD3MBCPD002238".equals(docId)
					|| "SD3MBCPD002240".equals(docId) || "SD3MBCPD002234".equals(docId)
					|| "SD3MBCPD002231".equals(docId) || "SD3MBCPD002230".equals(docId)
					|| "SD3MBCPD002213".equals(docId) || "SD3MBCPD002210".equals(docId)
					|| "SD3MBCPD002214".equals(docId) || "SD3MBCPD002232".equals(docId)
					|| "SD3MBCPD002229".equals(docId) || "SD3MBCPD002233".equals(docId)
					|| "SD3MBCPD002221".equals(docId) || "SD3MBCPD002200".equals(docId) 
					|| "SD3MBCPD002218".equals(docId) || "SD3MBCPD002217".equals(docId)
					|| "SD3MBCPD002226".equals(docId) || "SD3MBCPD002227".equals(docId)
					|| "SD3MBCPD002228".equals(docId) || docId.contains("SD3MBCPD00218")
					|| docId.contains("SD3MBCPD00219") || docId.contains("SD3MBCPD00220")
					|| docId.contains("SD3MBCPD00221") || docId.contains("SD3MBCPD00222")
					|| docId.contains("SD3MBCPD00223") || docId.contains("SD3MBCPD00224")
					|| "SD3MBCPD007624".equals(docId) || "SD3MBCPD002230".equals(docId)) {
				continue;
			}
			String tcDocRevId = App.syncDocMapper.getTCDocRevId(docId, frontDocRev);
			System.out.println("tcDocRevId：" + tcDocRevId);
			if (tcDocRevId == null) {
				continue;
			}
			String itemRevOwning = App.syncDocMapper.getItemRevOwning(tcDocRevId);
			ModelObject object = null;
			//POC
			//String sapsUser = "spas";
			//PRD
			String sapsUser = "spas1";
			if(sapsUser.equals(itemRevOwning)) {
				try {
					object = TCUtils.findObjectByPuid(App.tcSession, tcDocRevId);
					if("01".equals(frontDocRev)) {
						List<ModelObject> objs = TCUtils.getObjWhereReferenced(App.tcSession, new WorkspaceObject[] {(WorkspaceObject) object}, 1);
						if(objs != null) {
							for (int j = 0; j < objs.size(); j++) {
								ModelObject obj = objs.get(j);
								if(obj instanceof Folder) {
									Folder folder = (Folder)obj;
									TCUtils.deleteContents(App.tcSession, folder, object);
								}
							}
						}
						List<Dataset> itemRevDataset = TCUtils.getItemRevDataset(App.tcSession, (ItemRevision)object, "HTML");
						for (int j = 0; j < itemRevDataset.size(); j++) {
							Dataset dataset = itemRevDataset.get(j);
							TCUtils.deleteObject(App.tcSession, new ModelObject[] {dataset});
						}
						object = TCUtils.getItemByRev(App.tcSession, (ItemRevision)object);
					}
					TCUtils.deleteObject(App.tcSession, new ModelObject[] {object});
				} catch (Exception e) {
					LogFactory.get().info(e);
				}
			}else{
				System.out.println("docId-->" + docId);
				int DMSDocSN = App.syncDocMapper.getDMSDocSN(docId);
				System.out.println("DMSProjectFolderId：" + DMSProjectFolderId + "，docPath：" + docPath);
				int parentFolderId = App.syncDocMapper.getParentFolderId(String.valueOf(DMSProjectFolderId),docPath);
				procedureMap.clear();
				procedureMap.put("DMSDocSN", DMSDocSN);
				procedureMap.put("docId", docId);
				procedureMap.put("docRev", frontDocRev);
				procedureMap.put("parentFolderId", parentFolderId);
				App.syncDocMapper.addDMSDocRev(procedureMap);
			}
		}
	}

	@Override
	public void getProjectDocRevIssueStateDifferenceData(List<String> spasIds) throws Exception{
		LogFactory.get().info("同步专案文档版本发布状态开始...");
		for (int n = 0; n < spasIds.size(); n++) {
			String spasId = spasIds.get(n);
			LogFactory.get().info("同步【" + spasId +"】专案文档版本发布状态开始...");
			String TCProjectFolderPUID = App.syncDocMapper.getTCProjectFolderPUID(spasId);
			LogFactory.get().info("TC系统专案ID:" + TCProjectFolderPUID);
			int DMSProjectFolderId = App.syncDocMapper.getDMSProjectFolderId(spasId);
			LogFactory.get().info("文件管理系统专案ID:" + DMSProjectFolderId);
			
			List<DocRevInfo> TCDocRevInfos = getTCProjectDocRev(TCProjectFolderPUID);
			LogFactory.get().info("TC专案【" + spasId +"】下所有文档版本数量：" + TCDocRevInfos.size());
			LogFactory.get().info("TC专案【" + spasId +"】下所有文档版本信息：" + TCDocRevInfos);
			
			List<DocRevInfo1> tcDocRevIssueStateData = getTCDocRevIssueStateData(TCDocRevInfos);
			LogFactory.get().info("TC专案【" + spasId +"】下所有文档版本有发布状态的数量：" + tcDocRevIssueStateData.size());
			LogFactory.get().info("TC专案【" + spasId +"】下所有文档版本有发布状态的信息：" + tcDocRevIssueStateData);
			List<DocRevInfo1> tcToDMSData = getTCToDMSData(tcDocRevIssueStateData);
			
			if(tcToDMSData.size() > 0) {
				syncDocRevIssueStateFromTC(tcToDMSData);
			}
			
			List<DocRevInfo> DMSDocRevInfos = App.syncDocMapper.getDMSProjectDocRev(DMSProjectFolderId);
			LogFactory.get().info("文件管理系统专案【" + spasId +"】下所有文档版本数量：" + DMSDocRevInfos.size());
			LogFactory.get().info("文件管理系统专案【" + spasId +"】下所有文档版本信息：" + DMSDocRevInfos);
			
			List<DocRevInfo1> dmsDocRevIssueStateData = getDMSDocRevIssueStateData(DMSDocRevInfos);
			LogFactory.get().info("文件管理系统专案【" + spasId +"】下所有文档版本有发布状态的数量：" + dmsDocRevIssueStateData.size());
			LogFactory.get().info("文件管理系统专案【" + spasId +"】下所有文档版本有发布状态的信息：" + dmsDocRevIssueStateData);
			List<DocRevInfo1> dmsToTCData = getDMSToTCData(dmsDocRevIssueStateData);
			
			if(dmsToTCData.size() > 0) {
				syncDocRevIssueStateToTC(dmsToTCData);
			}
		}
		LogFactory.get().info("同步专案文档版本发布状态结束...");
	}
	
	@Override
	public void syncDocRevIssueStateToTC(List<DocRevInfo1> differenceData) throws Exception{
		for (int i = 0; i < differenceData.size(); i++) {
			DocRevInfo1 docRevInfo1 = differenceData.get(i);
			String docNum = docRevInfo1.getDocId();
			String revNum = docRevInfo1.getDocRev();
			try {
				String itemRevPuid = App.syncDocMapper.getItemRevPuid(docNum,revNum);
				ModelObject obj = TCUtils.findObjectByPuid(App.tcSession, itemRevPuid);
				String workflowName = "TCM Release Process：" + docNum + "/" + revNum;
				TCUtils.createNewProcess(App.tcSession, workflowName, "TCM Release Process", new ModelObject[] {obj});
			} catch (Exception e) {
				LogFactory.get().info(e);
			}
		}
	}

	@Override
	public void syncDocRevIssueStateFromTC(List<DocRevInfo1> differenceData) throws Exception{
		Map<String, Object> procedureMap = new HashMap<>();
		for (int i = 0; i < differenceData.size(); i++) {
			DocRevInfo1 docRevInfo1 = differenceData.get(i);
			String docId = docRevInfo1.getDocId();
			String docRev = docRevInfo1.getDocRev();
			Integer issueState = docRevInfo1.getIssueState();
			int docSn = App.syncDocMapper.getDocSnByDocNum(docId);
			procedureMap.clear();
			procedureMap.put("DOC_SN", docSn);
			procedureMap.put("ITEM_REV", docRev);
			procedureMap.put("PROCESS_STATE", issueState);
			App.syncDocMapper.setDMSDocRevIssueState(procedureMap);
		}
	}
	
	private List<DocRevInfo> MapConvertObject(List<Map<String, String>> differenceData) throws Exception{
		List<DocRevInfo> docRevInfoList = new ArrayList<DocRevInfo>();
		for (int i = 0; i < differenceData.size(); i++) {
			Map<String, String> docRevInfoMap = differenceData.get(i);
			DocRevInfo docRevInfo = new DocRevInfo();
			for (Entry<String, String> map : docRevInfoMap.entrySet()) {
				String key = map.getKey();
				String value = map.getValue();
				if("doc_id".equals(key)) {
					docRevInfo.setDoc_id(value);
				}
				if("doc_rev".equals(key)) {
					docRevInfo.setDoc_rev(value);
				}
				if("doc_path".equals(key)) {
					docRevInfo.setDoc_path(value);
				}
			}
			docRevInfoList.add(docRevInfo);
		}
		return docRevInfoList;
	}
	
	private List<DocRevInfo> getTCProjectDocRev(String TCProjectFolderPUID) throws Exception{
		List<DocRevInfo> TCDocRevInfo = new ArrayList<DocRevInfo>();
		List<DocRevInfo> TCProjectDocRevInfo = App.syncDocMapper.getTCProjectDocRev(TCProjectFolderPUID);
		for (int i = 0; i < TCProjectDocRevInfo.size(); i++) {
			DocRevInfo docRevInfo = TCProjectDocRevInfo.get(i);
			String docId = docRevInfo.getDoc_id();
			String docRev = docRevInfo.getDoc_rev();
			String docPath = docRevInfo.getDoc_path();
			if(docRev == null) {
				int docRevCount = App.syncDocMapper.getDocRevCount(docId);
				for (int j = 1; j <= docRevCount; j++) {
					DocRevInfo docRevInfo2 = new DocRevInfo();
					docRevInfo2.setDoc_id(docId);
					docRevInfo2.setDoc_rev("0" + String.valueOf(j));
					docRevInfo2.setDoc_path(docPath);
					TCDocRevInfo.add(docRevInfo2);
				}
			}else {
				TCDocRevInfo.add(docRevInfo);
			}
		}
		return TCDocRevInfo;
	}
	
	private List<DocRevInfo1> getTCDocRevIssueStateData(List<DocRevInfo> TCDocRevInfos) throws Exception{
		List<DocRevInfo1> docRevInfo1s = new ArrayList<DocRevInfo1>();
		for (int i = 0; i < TCDocRevInfos.size(); i++) {
			DocRevInfo docRevInfo = TCDocRevInfos.get(i);
			String docId = docRevInfo.getDoc_id();
			String docRev = docRevInfo.getDoc_rev();
			Integer processState = getTCItemRevProcessState(docId,docRev);
			if(processState == null) {
				continue;
			}
			if(processState != 1) {
				DocRevInfo1 docRevInfo1 = new DocRevInfo1();
				docRevInfo1.setDocId(docId);
				docRevInfo1.setDocRev(docRev);
				docRevInfo1.setIssueState(processState);
				docRevInfo1s.add(docRevInfo1);
			}
		}
		return docRevInfo1s;
	}
	
	private List<DocRevInfo1> getTCToDMSData(List<DocRevInfo1> tcDocRevIssueStateData) throws Exception{
		List<DocRevInfo1> tcDocRevIssueStateData1 = new ArrayList<DocRevInfo1>();
		for (int i = 0; i < tcDocRevIssueStateData.size(); i++) {
			DocRevInfo1 docRevInfo1 = tcDocRevIssueStateData.get(i);
			String docId = docRevInfo1.getDocId();
			String docRev = docRevInfo1.getDocRev();
			Integer DMSIssueState = App.syncDocMapper.getDMSItemRevProcessState(docId,docRev);
			if(DMSIssueState == null) {
				continue;
			}
			if(DMSIssueState == 1) {
				tcDocRevIssueStateData1.add(docRevInfo1);
			}
		}
		return tcDocRevIssueStateData1;
	}
	
	private List<DocRevInfo1> getDMSToTCData(List<DocRevInfo1> dmsDocRevIssueStateData) throws Exception{
		List<DocRevInfo1> dmsDocRevIssueStateData1 = new ArrayList<DocRevInfo1>();
		for (int i = 0; i < dmsDocRevIssueStateData.size(); i++) {
			DocRevInfo1 docRevInfo1 = dmsDocRevIssueStateData.get(i);
			String docId = docRevInfo1.getDocId();
			String docRev = docRevInfo1.getDocRev();
			Integer processState = getTCItemRevProcessState(docId,docRev);
			if(processState == null) {
				continue;
			}
			if(processState == 1) {
				dmsDocRevIssueStateData1.add(docRevInfo1);
			}
		}
		return dmsDocRevIssueStateData1;
	}
	
	private List<DocRevInfo1> getDMSDocRevIssueStateData(List<DocRevInfo> DMSDocRevInfos) throws Exception{
		List<DocRevInfo1> docRevInfo1s = new ArrayList<DocRevInfo1>();
		for (int i = 0; i < DMSDocRevInfos.size(); i++) {
			DocRevInfo docRevInfo = DMSDocRevInfos.get(i);
			String docId = docRevInfo.getDoc_id();
			String docRev = docRevInfo.getDoc_rev();
			Integer DMSIssueState = App.syncDocMapper.getDMSItemRevProcessState(docId,docRev);
			if(DMSIssueState == null) {
				continue;
			}
			if(DMSIssueState == 0) {
				DocRevInfo1 docRevInfo1 = new DocRevInfo1();
				docRevInfo1.setDocId(docId);
				docRevInfo1.setDocRev(docRev);
				docRevInfo1.setIssueState(DMSIssueState);
				docRevInfo1s.add(docRevInfo1);
			}
		}
		return docRevInfo1s;
	}
	
	private Integer getTCItemRevProcessState(String docId,String docRev) throws Exception{
		Integer processState = 1;
		Integer itemRevProcessStateCount = App.syncDocMapper.getItemRevProcessStateCount(docId,docRev);
		if(itemRevProcessStateCount == 0) {
			processState = 1;
		}
		if(itemRevProcessStateCount > 0) {
			String itemRevProcessStateName = App.syncDocMapper.getItemRevProcessStateName(docId,docRev);
			if(itemRevProcessStateName.contains("Release")) {
				processState = 0;
			}
			if(itemRevProcessStateName.contains("Obsolete")) {
				processState = 2;
			}
			if(itemRevProcessStateName.contains("Pending")) {
				processState = 3;
			}
		}
		return processState;
	}
	
	public static String[] getTCPreferences(AppXSession soaSession, String prefername) throws Exception{
        PreferenceManagementService preferenmanagementservice = PreferenceManagementService.getService(AppXSession.getConnection());
        try{
            preferenmanagementservice.refreshPreferences();
        }catch (Exception e){
			LogFactory.get().info(e);
        }
        GetPreferencesResponse getpreferencesRes = preferenmanagementservice.getPreferences(new String[] { prefername }, false);
        PreferenceManagement.CompletePreference[] completePref = getpreferencesRes.response;
        String[] temps = null;
        if (completePref.length > 0) {
            PreferenceManagement.CompletePreference onecompletePref = completePref[0];
            PreferenceManagement.PreferenceValue prefvalue = onecompletePref.values;
            temps = prefvalue.values;
        }
        return temps;
    }

}
