﻿package com.foxconn.electronics.managementebom.secondsource.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.d9.services.rac.cust.D9FoxconnSoaCustService;
import com.foxconn.electronics.managementebom.secondsource.constants.Search2ndSourceConstant;
import com.foxconn.electronics.managementebom.secondsource.domain.SelectSync2ndSourceBean;
import com.foxconn.electronics.managementebom.secondsource.domain.Sync2ndSourceInfo;
import com.foxconn.electronics.managementebom.secondsource.domain.Sync2ndSourceParams;
import com.foxconn.electronics.managementebom.secondsource.util.EBOMTreeTools;
import com.foxconn.electronics.managementebom.updatebom.domain.EBOMLineBean;
import com.foxconn.electronics.managementebom.updatebom.service.UpdateEBOMService;
import com.foxconn.electronics.util.CommonTools;
import com.foxconn.electronics.util.TCUtil;
import com.foxconn.tcutils.constant.TCSearchEnum;
import com.foxconn.tcutils.util.AjaxResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentProjectType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;

public class SyncSecondSourceService {	
	
	private EBOMTreeTools ebomTreeTools;	
	
	private TCSession session;
	
	private TCComponentBOMWindow EBOMWindow = null;	
	
	private UpdateEBOMService updateEBOMService = null;
	private D9FoxconnSoaCustService foxconnSoaCustService = null;
	
	  
	private TCComponentItemRevision topItemRev = null;	
	private String sourceBomItemIds = null; // 源机种零组件ID
	
	public static final String[] ALL_PART_ATTRS = {"item_id", "item_revision_id", "d9_EnglishDescription", "d9_DescriptionSAP", "d9_ManufacturerID", "d9_ManufacturerPN", "d9_MaterialGroup", "d9_MaterialType", 
													"d9_ProcurementMethods", "d9_Un", "release_status_list", "d9_SAPRev", "d9_SupplierZF", "D9_HasSourceBOM_REL"};
	
	public static final String[] ALL_BOM_ATTRS = {"bl_sequence_no", "bl_occ_d9_Location", "bl_occ_d9_AltGroup", "bl_quantity", "bl_occ_d9_ReferenceDimension"};
	
	public SyncSecondSourceService() {
		session = RACUIUtil.getTCSession();
		ebomTreeTools = new EBOMTreeTools(session);
		ebomTreeTools.setLevel("L6");
		try {
			this.updateEBOMService = new UpdateEBOMService();
			foxconnSoaCustService = D9FoxconnSoaCustService.getService(session);
		} catch (TCException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 *  获取同步2nd source参数
	 * 
	 * @param uid
	 * @return
	 */
	public AjaxResult getSync2ndSourceParams(String syncFrom) {
		Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
		try {
//			List<Sync2ndSourceParams> projectList = Collections.synchronizedList(new ArrayList<Sync2ndSourceParams>());
//			TCComponentUser user = session.getUser();
//			TCComponentProjectType projectType = (TCComponentProjectType) session.getTypeComponent(ITypeName.TC_Project);
//			TCComponentProject[] projects = projectType.extent(user, true);
//			Stream.of(projects).forEach(project -> {
//				Sync2ndSourceParams params = new Sync2ndSourceParams();
//				params.setProjectID(project.getProjectID().toUpperCase());
//				params.setProjectName(project.getProjectName());
//				projectList.add(params);
//			});	
			paramMap.put("projectName", getMatGroupListByLevel());
//			paramMap.put("projectName", projectList);
			return new AjaxResult(AjaxResult.STATUS_SUCCESS, "查询成功", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new AjaxResult(AjaxResult.STATUS_SERVER_ERROR, "查询失败", null);
	}

	
	public String getSync2ndSourceParamsSOA(String data) {
		String result = foxconnSoaCustService.callAwcMethod("get2ndSourceParams", data);
		System.out.println("==>> result: " + result);
		return result;
	}
	
	/**
	 * 获取指派到物料群组的专案信息
	 * @return
	 * @throws Exception
	 */
	public List<Sync2ndSourceParams> getMatGroupListByLevel() throws Exception {
		List<String> queryNames = new ArrayList<>();
		List<String> queryValues = new ArrayList<>();
		
		queryNames.add(Search2ndSourceConstant.ID);
		queryValues.add("*" + ebomTreeTools.getLevel() + "-P" + "*");
		
		TCComponent[] results = TCUtil.executeQuery(session, Search2ndSourceConstant.FIND_MATERIALGROUPBYID, queryNames.toArray(new String[0]), queryValues.toArray(new String[0]));		
		if (CommonTools.isEmpty(results)) {
			return null;
		}
		
		List<TCComponent> filterList = Stream.of(results).filter(e -> {
			try {
				String itemId = ((TCComponentItemRevision) e).getProperty("item_id");
				if (itemId.split("-").length == 4) {
					return true;
				}
				return false;
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
		}).collect(Collectors.toList());
		
		filterList = filterList.stream().filter(CommonTools.distinctByKey(e -> {
			try {
				String itemId = ((TCComponentItemRevision) e).getProperty("item_id");
				return itemId.split("-")[2];
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
		})).collect(Collectors.toList());		
		
		List<Sync2ndSourceParams> list = new ArrayList<Sync2ndSourceParams>();
		
//		 Stream.of(results).forEach(e -> {			
		filterList.forEach(e -> {			
			try {
				TCComponentItemRevision itemRev = (TCComponentItemRevision) e;
				String itemId = itemRev.getProperty("item_id");
				String[] split = itemId.split("-");
				Optional<String> findAny = Stream.of(split).filter(str -> {					
					return str.toUpperCase().startsWith("P");
					
				}).findAny();
				if (findAny.isPresent()) {
					String value = findAny.get();
					String[] split2 = value.split(",");
					for (String s : split2) {
						TCComponent[] projects = TCUtil.executeQuery(session,TCSearchEnum.D9_FIND_PROJECT.queryName(), TCSearchEnum.D9_FIND_PROJECT.queryParams(), new String[] {s});		
						if (CommonTools.isEmpty(projects)) {
							continue;
						}
						TCComponentProject p = (TCComponentProject) projects[0];
						String projectId = p.getProjectID();
						if (projectId.equals(ebomTreeTools.getCurrentProjectID())) {
							continue;
						}
						
						Sync2ndSourceParams params = new Sync2ndSourceParams();
						params.setProjectID(projectId);
						params.setProjectName(p.getProjectName());
						list.add(params);
					}
					
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		 
		return list.stream().filter(CommonTools.distinctByKey(bean -> bean.getProjectID())).collect(Collectors.toList());
	}
	
	
	/**
	 * 获取单阶含有替代料的BOM结构 
	 * @param uid
	 * @return
	 */
	public AjaxResult getSingle2ndSourceEBOMStruct(String uid) {
		try {
			TCComponentBOMLine topBomLine = (TCComponentBOMLine) session.getComponentManager().getTCComponent(uid);
			EBOMWindow = topBomLine.getCachedWindow();			
			if (EBOMWindow == null) {
				return AjaxResult.error(AjaxResult.STATUS_NO_RESULT,"BOMWindow已经被关闭");
			}
			
			ebomTreeTools.setCurrentProjectID(getProjectId(topBomLine).toUpperCase());
			
			Sync2ndSourceInfo topBomInfo = ebomTreeTools.getSingle2ndSourceStruct(topBomLine);
			if (CommonTools.isEmpty(topBomInfo)) {
				throw new Exception("遍历BOMLine失败");				
			}
			
			return new AjaxResult(AjaxResult.STATUS_SUCCESS, "查询成功", topBomInfo);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,"BOM数据获取失败");
		}
	}
	
	
	/**
	 * 获取含有替代料的BOM结构 
	 * @param uid
	 * @return
	 */
	public String get2ndSourceEBOMStructSOA(String uid) {
		long startTime = System.currentTimeMillis();
		String result = foxconnSoaCustService.callAwcMethod("get2ndSourceStruct", uid);			
		System.out.println("get2ndSourceEBOMStructSOA cast time ::  " + (System.currentTimeMillis() - startTime));
		return result;
	}
	
	
	/**
	 * 获取含有替代料的BOM结构 
	 * @param uid
	 * @return
	 */
	public AjaxResult get2ndSourceEBOMStruct(String uid) {
		try {
			long startTime = System.currentTimeMillis();
			TCComponentBOMLine topBomLine = (TCComponentBOMLine) session.getComponentManager().getTCComponent(uid);
			topItemRev = topBomLine.getItemRevision();
			topItemRev.refresh();
			EBOMWindow = topBomLine.window();
			if (EBOMWindow == null) {
				return AjaxResult.error(AjaxResult.STATUS_NO_RESULT,"BOMWindow已经被关闭");
			}
			
//			topBomLine.refresh();
//            com.foxconn.tcutils.util.TCUtil.unpackageBOMStructure(topBomLine);
//            topBomLine.refresh();
            
			
			updateEBOMService.loadAllProperties(topBomLine, ALL_PART_ATTRS, ALL_BOM_ATTRS);
			String currentProjectID = getProjectId(topBomLine); // 返回专案ID

			System.out.println("==>> currentProjectID: " + currentProjectID);
			ebomTreeTools.setCurrentProjectID(currentProjectID.toUpperCase());			
			
			TCComponent[] relatedComponents = topItemRev.getRelatedComponents("D9_HasSourceBOM_REL");
			Map<String, TCComponentItemRevision> sourceBomMap = EBOMTreeTools.getTotalSourceBomMap(session, relatedComponents); // 获取所有的源机种BOM集合
			ebomTreeTools.setSourceBomMap(sourceBomMap);

//			sourceBomItemIds = getSourceBomItemIds(relatedComponents);
			
			
			Sync2ndSourceInfo topBomInfo = ebomTreeTools.get2ndSourceStruct(topBomLine, true);			
			if (CommonTools.isEmpty(topBomInfo)) {
				throw new Exception("遍历BOMLine失败");				
			}	
			
			topBomInfo.setEnable(false); // 设置顶层BOMLine是无法添加右键和取消复选按钮
			System.out.println("get2ndSourceEBOMStruct cast time ::  " + (System.currentTimeMillis() - startTime));
			return new AjaxResult(AjaxResult.STATUS_SUCCESS, "查询成功", topBomInfo);			
			
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getLocalizedMessage());
		}
		
	}
	
	
	/**
	 * 获取源机种BOM零组件ID
	 * @param relatedComponents
	 * @return
	 */
	private String getSourceBomItemIds(TCComponent[] relatedComponents) {
		if (CommonTools.isNotEmpty(relatedComponents)) {
			List<String> collect = Stream.of(relatedComponents).map(e -> {
				if (e instanceof TCComponentItemRevision) {
					try {
						return e.getProperty("item_id");
					} catch (TCException e1) {
						e1.printStackTrace();
					}
				}
				return "";
			}).collect(Collectors.toList());
			
			return collect.stream().collect(Collectors.joining(","));
		}
		
		return "";
	}
	
	
	/**
	 *返回项目ID
	 * @param topBomLine
	 * @return
	 * @throws TCException
	 */
	private String getProjectId(TCComponentBOMLine topBomLine) throws TCException {
		TCComponentItem topItem = topBomLine.getItem();
		TCProperty props = topItem.getTCProperty("project_list");
		if (CommonTools.isNotEmpty(props)) {
			TCComponent[] pjs = props.getReferenceValueArray();
			return Stream.of(pjs).map(e -> ((TCComponentProject) e).getProjectID()).collect(Collectors.joining(","));
		}
		return "";
	}
	
	
	public AjaxResult getSingleSync2ndSourceStruct(String uid, String syncFrom, String projectID) {
		try {
			TCComponentBOMLine topBomLine = (TCComponentBOMLine) session.getComponentManager().getTCComponent(uid);
			EBOMWindow = topBomLine.getCachedWindow();
			if (EBOMWindow == null) {
				return AjaxResult.error(AjaxResult.STATUS_NO_RESULT,"BOMWindow已经被关闭");
			}
			
			ebomTreeTools.setSyncFrom(syncFrom);
			ebomTreeTools.setProjectID(projectID);
			
			Sync2ndSourceInfo topBomInfo = ebomTreeTools.getSingleSync2ndSourceStruct(topBomLine);
			if (CommonTools.isEmpty(topBomInfo)) {
				throw new Exception("ͬ同步单阶2nd Source 失败");				
			}
			

//			ebomTreeTools.setSingleAltGroup(topBomInfo, false); // Set 2nd Source GroupId
			return  AjaxResult.success( "查询成功", topBomInfo);

		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getLocalizedMessage());
		}
	}
	
	
	
	public String getSync2ndSourceInfoSOA(String data) {
		long startTime = System.currentTimeMillis();
		String result = foxconnSoaCustService.callAwcMethod("getSync2ndSourceStruct", data);
		System.out.println("getSync2ndSourceInfoSOA cast time ::  " + (System.currentTimeMillis() - startTime));
		return result;
	}
	
	
	/**
	 * 获取同步2nd Source json数据集合
	 * 
	 * @param uid
	 * @param syncFrom
	 * @param projectName
	 */
	public AjaxResult getSync2ndSourceInfo(String uid, String syncFrom, String projectID) {
		try {	
			long startTime = System.currentTimeMillis();
			TCComponentBOMLine topBomLine = (TCComponentBOMLine) session.getComponentManager().getTCComponent(uid);
			EBOMWindow = topBomLine.window();
			if (EBOMWindow == null) {
				return AjaxResult.error(AjaxResult.STATUS_NO_RESULT,"BOMWindow已经被关闭");
			}
			
//			topBomLine.refresh();
//	        com.foxconn.tcutils.util.TCUtil.unpackageBOMStructure(topBomLine);
//	        topBomLine.refresh();
	            
			updateEBOMService.loadAllProperties(topBomLine, ALL_PART_ATTRS, ALL_BOM_ATTRS);
			
			ebomTreeTools.setSyncFrom(syncFrom);
			ebomTreeTools.setProjectID(projectID);
			
			Sync2ndSourceInfo topBomInfo = ebomTreeTools.getSync2ndSourceStruct(topBomLine, true, true);
			if (CommonTools.isEmpty(topBomInfo)) {
				throw new Exception("遍历BOMLine失败");				
			}			
			
			topBomInfo.setEnable(false); // 设置顶层BOMLine是无法添加右键和取消复选按钮
//			ebomTreeTools.setAltGroupNew(topBomInfo, false); //设置替代料群组编号
			System.out.println("getSync2ndSourceInfo cast time ::  " + (System.currentTimeMillis() - startTime));
			return  AjaxResult.success("查询成功", topBomInfo);

		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,"BOM数据获取失败");
		} 
	}	
	
	
	public String getSelectSync2ndSourceInfoSOA(String data) {
		long startTime = System.currentTimeMillis();
		String result = foxconnSoaCustService.callAwcMethod("getSelectSync2ndSourceStruct", data);
		System.out.println("getSelectSync2ndSourceInfoSOA cast time ::  " + (System.currentTimeMillis() - startTime));
		return result;
	}

	public AjaxResult getSelectSync2ndSourceInfo(String body) {
		List<Sync2ndSourceInfo> resultList = new ArrayList<Sync2ndSourceInfo>();
		try {
			long startTime = System.currentTimeMillis();
			JSONObject jsonObject = (JSONObject) com.alibaba.fastjson.JSON.parse(body);
			String data = jsonObject.getString("data");
			List<SelectSync2ndSourceBean> list = JSONObject.parseArray(data, SelectSync2ndSourceBean.class);
			ebomTreeTools.setSyncFrom(jsonObject.getString("syncFrom"));
			ebomTreeTools.setProjectID(jsonObject.getString("projectID"));
			TCComponentBOMLine bomLine = null;
			TCComponentBOMLine parentBomLine = null;
			for (SelectSync2ndSourceBean bean : list) {
				String parentUid = bean.getParentUid();
				String bomLineUid = bean.getBomUid();
				parentBomLine = (TCComponentBOMLine) session.getComponentManager().getTCComponent(parentUid);
				bomLine = (TCComponentBOMLine) session.getComponentManager().getTCComponent(bomLineUid);
				EBOMWindow = bomLine.window();
				if (EBOMWindow == null) {
					return AjaxResult.error(AjaxResult.STATUS_NO_RESULT,"BOMWindow已经被关闭");
				}
				
				updateEBOMService.loadAllProperties(bomLine, ALL_PART_ATTRS, ALL_BOM_ATTRS);			
				
				Sync2ndSourceInfo bomBean = ebomTreeTools.getSelectSync2ndSourceStruct(parentBomLine, bomLine);			
				
				resultList.add(bomBean);
			}
			
			System.out.println("getSync2ndSourceInfo cast time ::  " + (System.currentTimeMillis() - startTime));
			return  AjaxResult.success("查询成功", resultList);
			
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,"BOM数据获取失败");
		}
	}
	
	
	/**
	 * 查询2nd Source替代料列表(暂时没用)
	 * @param HHPN
	 * @param mfg
	 * @param mfgPN
	 * @return
	 * @throws Exception 
	 */

	public List<Sync2ndSourceInfo> searchSync2ndSubList(String HHPN, String mfg, String mfgPN) throws Exception {

		List<String> queryNames = new ArrayList<>();
		List<String> queryValues = new ArrayList<>();
		if (CommonTools.isNotEmpty(HHPN)) {
			queryNames.add(Search2ndSourceConstant.HHPN);
			queryValues.add(HHPN + "*");
		}
		if (CommonTools.isNotEmpty(mfg)) {
			queryNames.add(Search2ndSourceConstant.MFG);
			queryValues.add(mfg + "*");
		}
		if (CommonTools.isNotEmpty(mfgPN)) {
			queryNames.add(Search2ndSourceConstant.MFG_PN);
			queryValues.add(mfgPN + "*");
		}
		System.out.println("find parts param: " + HHPN + "   " + mfg + "    " + mfgPN);

		TCComponent[] results = TCUtil.executeQuery(session, Search2ndSourceConstant.FIND_PARTS,
				queryNames.toArray(new String[0]), queryValues.toArray(new String[0]));
		if (CommonTools.isEmpty(results)) {
			return null;
		}
		return Stream.of(results).map(e -> {
			Sync2ndSourceInfo bean = null;
			try {
				TCComponentItemRevision itemRev = ((TCComponentItem) e).getLatestItemRevision();
				bean = EBOMTreeTools.tcPropMapping(new Sync2ndSourceInfo(), itemRev);
				bean.setItemRevUid(itemRev.getUid());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return bean;
		}).collect(Collectors.toList());
	} 
	
	
	/**
	 * 编写通过源机种来判断是否可以右键添加2nd Source接口
	 * @param pItemUid
	 * @param pItemId
	 * @return
	 */
	public AjaxResult checkAdd2ndSourceBySourceBom(String pItemUid, String pItemId) {
		try {
			System.out.println("checkAdd2ndSourceBySourceBom pItemUid :: " + pItemUid);
			System.out.println("checkAdd2ndSourceBySourceBom pItemId :: " + pItemId);
			Map<String, TCComponentItemRevision> sourceBomMap = ebomTreeTools.getSourceBomMap();
			if (CommonTools.isNotEmpty(sourceBomMap)) {
				if (CommonTools.isNotEmpty(sourceBomMap.get(pItemUid))) {
					return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,"零组件ID为: " + pItemId + ", 已经存在源机种中: " + ", 无法添加2nd Source");
				}
			} 
			return AjaxResult.success();
		} catch (Exception e) {
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,"判断是否可以添加2nd Source失败");
		}		
		
	}
	
	
	public String save2ndSourceSubListSOA(String data) throws TCException {
		long startTime = System.currentTimeMillis();
		String result = foxconnSoaCustService.callAwcMethod("save2ndSource", data);
		System.out.println("==>> result: " + result);
		cn.hutool.json.JSONObject obj = JSONUtil.parseObj(result);
		
		cn.hutool.json.JSONObject dataObj = obj.getJSONObject("data");
		String bomLineUid = dataObj.getStr("bomLineUid");
		String itemRevUid = dataObj.getStr("itemRevUid");
		Boolean hasRevise = dataObj.getBool("hasRevise");
		if (hasRevise) {
			try {
				TCComponentBOMLine topLine = (TCComponentBOMLine) session.getComponentManager().getTCComponent(bomLineUid);	
				TCComponentItemRevision topItemRev = (TCComponentItemRevision) session.getComponentManager().getTCComponent(itemRevUid);
				TCComponentBOMWindow bomWindow = topLine.getCachedWindow();
				topLine = bomWindow.setWindowTopLine(null, topItemRev, null, null);
				String uid = topLine.getUid();
				dataObj.set("bomLineUid", uid);
				dataObj.set("uid", uid);
				obj.set("data", dataObj);
				result = JSONUtil.toJsonStr(obj);
				bomWindow.refresh();
		        bomWindow.clearCache();
			} catch (Exception e) {
				e.printStackTrace();
				obj.set("code", AjaxResult.STATUS_SERVER_ERROR);
				obj.set("msg", e.getLocalizedMessage());
				return JSONUtil.toJsonStr(obj);
			}
		} 		
		System.out.println("save2ndSourceSubListSOA cast time :: " + (System.currentTimeMillis() - startTime));
		return result;		
	}
	
	/**
	 * 保存2nd Source替代料信息
	 * @param data
	 * @return
	 * @throws TCException 
	 */
	public AjaxResult save2ndSourceSubList(String data) throws TCException {		
		Gson gson = new Gson();
		System.out.println(data);
		Sync2ndSourceInfo topBomInfo = gson.fromJson(data, Sync2ndSourceInfo.class);		
		System.out.println(gson.toJson(topBomInfo));
		EBOMLineBean rootBean = null;
		try {
			com.foxconn.mechanism.util.TCUtil.setBypass(session);
			TCComponentBOMLine topLine = (TCComponentBOMLine) session.getComponentManager().getTCComponent(topBomInfo.getBomLineUid());
			EBOMWindow = topLine.window();
			if (CommonTools.isEmpty(EBOMWindow)) {
				throw new Exception("BOMWindow已经被关闭");
			}	
			// 新增获取当前专案
			String currentProjectID = getProjectId(topLine); // 返回专案ID
			System.out.println("==>> currentProjectID: " + currentProjectID);
			ebomTreeTools.setCurrentProjectID(currentProjectID.toUpperCase());
			
//			ebomTreeTools.setAltGroupNew(topBomInfo, true);
			ebomTreeTools.setAltGroup(topBomInfo);
			ebomTreeTools.setEBOMWindow(EBOMWindow);	
			
			TCComponentBOMLine rootBomLine = ebomTreeTools.checkTopLineRevise(topBomInfo); //判断顶阶BOMLine是否需要升版
			if (CommonTools.isNotEmpty(rootBomLine)) {
				topLine = rootBomLine;
			}
			rootBean = new EBOMLineBean(topLine);
			rootBean.setIsNewVersion(UpdateEBOMService.isNewRevsion(topLine.getItemRevision()));
			ebomTreeTools.checkMerge(topBomInfo); // 判断位置,用量是否需要合并
			ebomTreeTools.saveBOMTree(topBomInfo, "");	
			return AjaxResult.success("替代料群组保存成功", rootBean);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,e.getLocalizedMessage());
		} finally {
			EBOMWindow.save();	
			EBOMWindow.refresh();
			EBOMWindow.clearCache();
			com.foxconn.mechanism.util.TCUtil.closeBypass(session);
		} 
	}
}
