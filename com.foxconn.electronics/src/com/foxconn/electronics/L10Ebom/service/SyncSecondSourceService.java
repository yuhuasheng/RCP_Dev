﻿package com.foxconn.electronics.L10Ebom.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.foxconn.electronics.managementebom.secondsource.constants.AlternativeConstant;
import com.foxconn.electronics.managementebom.secondsource.constants.Search2ndSourceConstant;
import com.foxconn.electronics.managementebom.secondsource.domain.Sync2ndSourceInfo;
import com.foxconn.electronics.managementebom.secondsource.domain.Sync2ndSourceParams;
import com.foxconn.electronics.managementebom.secondsource.util.EBOMTreeTools;
import com.foxconn.electronics.managementebom.updatebom.domain.EBOMLineBean;
import com.foxconn.electronics.managementebom.updatebom.service.UpdateEBOMService;
import com.foxconn.electronics.util.CommonTools;
import com.foxconn.tcutils.constant.TCSearchEnum;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.TCUtil;
import com.google.gson.Gson;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
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

public class SyncSecondSourceService {

	private EBOMTreeTools ebomTreeTools;

	private TCSession session;

	private TCComponentBOMWindow EBOMWindow = null;

	private UpdateEBOMService updateEBOMService = null;

	public static final String[] ALL_PART_ATTRS = { "item_id", "item_revision_id", "d9_EnglishDescription",
			"d9_DescriptionSAP", "d9_ManufacturerID", "d9_ManufacturerPN", "d9_MaterialGroup", "d9_MaterialType",
			"d9_ProcurementMethods", "d9_Un", "release_status_list", "d9_SAPRev", "d9_SupplierZF" };

	public static final String[] ALL_BOM_ATTRS = { "bl_sequence_no", "bl_occ_d9_Location", "bl_occ_d9_AltGroup",
			"bl_quantity", "bl_occ_d9_ReferenceDimension" };

	public SyncSecondSourceService() {
		session = RACUIUtil.getTCSession();
		ebomTreeTools = new EBOMTreeTools(session);
		ebomTreeTools.setLevel("L10");
		try {
			this.updateEBOMService = new UpdateEBOMService();
		} catch (TCException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取同步2nd source参数
	 * 
	 * @param uid
	 * @return
	 */
	public AjaxResult getSync2ndSourceParams(String syncFrom) {
		Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
		try {
			List<Sync2ndSourceParams> projectList = Collections.synchronizedList(new ArrayList<Sync2ndSourceParams>());
			TCComponentUser user = session.getUser();
			TCComponentProjectType projectType = (TCComponentProjectType) session
					.getTypeComponent(ITypeName.TC_Project);
			TCComponentProject[] projects = projectType.extent(user, true);
			Stream.of(projects).forEach(project -> {
				Sync2ndSourceParams params = new Sync2ndSourceParams();
				params.setProjectID(project.getProjectID().toUpperCase());
				params.setProjectName(project.getProjectName());
				projectList.add(params);
			});
//			paramMap.put("projectName", getMatGroupListByLevel());
			paramMap.put("projectName", projectList);
			return new AjaxResult(AjaxResult.STATUS_SUCCESS, "查询成功", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new AjaxResult(AjaxResult.STATUS_SERVER_ERROR, "查询失败", null);
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
	 * 
	 * @param uid
	 * @return
	 */
	public AjaxResult getSingle2ndSourceEBOMStruct(String uid) {
		Sync2ndSourceInfo topBomInfo = null;
		try {
			TCComponent obj = TCUtil.loadObjectByUid(uid);
			if (obj instanceof TCComponentItemRevision) {

				TCComponentItemRevision revision = (TCComponentItemRevision) obj;
				EBOMWindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine topBomLine = EBOMWindow.setWindowTopLine(revision.getItem(), revision, null, null);

//				TCComponentBOMLine topBomLine = (TCComponentBOMLine) session.getComponentManager().getTCComponent(uid);
//				EBOMWindow = topBomLine.window();
				if (EBOMWindow == null) {
					return AjaxResult.error(AjaxResult.STATUS_NO_RESULT, "BOMWindow已经被关闭");
				}

				ebomTreeTools.setCurrentProjectID(getProjectId(topBomLine).toUpperCase());

				topBomInfo = ebomTreeTools.getSingle2ndSourceStruct(topBomLine);
				if (CommonTools.isEmpty(topBomInfo)) {
					throw new Exception("遍历BOMLine失败");
				}
			}
			return new AjaxResult(AjaxResult.STATUS_SUCCESS, "查询成功", topBomInfo);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "BOM数据获取失败");
		}
	}

	/**
	 * 获取含有替代料的BOM结构
	 * 
	 * @param topLine
	 * @return
	 * @throws TCException
	 */
	public Sync2ndSourceInfo get2ndSourceStruct(TCComponentBOMLine topLine) throws TCException {
		topLine.refresh();
		Sync2ndSourceInfo rootBean = new Sync2ndSourceInfo(topLine);

		String d9_MaterialGroup = topLine.getItemRevision().getProperty("d9_MaterialGroup");
		System.out.println(d9_MaterialGroup);

		if (!"B8X80".equals(d9_MaterialGroup)) {

			AIFComponentContext[] componmentContext = null;
			boolean hasChildren = topLine.hasChildren();
			if (hasChildren) {
				componmentContext = topLine.getChildren();
			}
			AtomicBoolean verCheckFlag = new AtomicBoolean(false);
			String[] verCheckResult = { "" };
			if (CommonTools.isNotEmpty(componmentContext)) {
				Stream.of(componmentContext).forEach(e -> {
					try {
						TCComponentBOMLine bomLine = (TCComponentBOMLine) e.getComponent();
						if (!bomLine.isSubstitute()) {

							Sync2ndSourceInfo bomBean = get2ndSourceStruct(bomLine);
							if (bomLine.hasSubstitutes()) {
								if (CommonTools.isEmpty(verCheckResult[0])) {
									TCComponentBOMLine parentBomLine = bomLine.parent();
									if (CommonTools.isNotEmpty(parentBomLine)) {
										ebomTreeTools.checkVerCountByLock(parentBomLine.getItemRevision(),
												verCheckResult, verCheckFlag);
									}
								}

								bomBean.setParentItem(rootBean.getItem());
								bomBean.setVerNote(verCheckResult[0]); // 判断此对象版本是否为初版

//							TCComponentBOMLine parentBomLine = bomLine.parent();
//							if (CommonTools.isNotEmpty(parentBomLine)) {
//								bomBean.setVerNote(checkVerCount(parentBomLine.getItemRevision())); // 判断此对象版本是否为初版
//							}

								if (CommonTools.isEmpty(bomBean.getAlternativeGroup())) {
									String altGroup = ebomTreeTools.getAltGroupByRule(bomBean.getFindNum());
									if (CommonTools.isEmpty(altGroup)) {
										throw new Exception(
												"零组件ID为: " + bomBean.getItem() + ", 版本号为: " + bomBean.getVersion()
														+ " findNum为: " + bomBean.getFindNum() + ", 获取替代料群组失败!");
									}
									bomBean.setAlternativeGroupNew(altGroup);
								}

								bomBean.setAlternativeCode(AlternativeConstant.PRI); // 含有替代料才显示主料字段
								bomBean.setUsageProb(100); // 有替代料则填写
								TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();
								List<Sync2ndSourceInfo> subBeanList = new ArrayList<Sync2ndSourceInfo>();
								String matGroupItemId = "";
								for (TCComponentBOMLine subBomline : listSubstitutes) {
									Sync2ndSourceInfo subBean = ebomTreeTools.tcPropMapping(new Sync2ndSourceInfo(), subBomline);
									subBean.setParentItem(rootBean.getItem());
									if (CommonTools.isEmpty(matGroupItemId)) {
										matGroupItemId = ebomTreeTools.getMatGroupItemId(subBean); // 返回当前替代料所在专案群组ItemId
									}
									subBean.setMaterialGroupItemId(matGroupItemId);
									subBean.setAlternativeGroupNew(bomBean.getAlternativeGroup());
									subBean.setAlternativeCode(AlternativeConstant.ALT);
									subBean.setLocation("");
									subBean.setIsSub(true);
									subBean.setCheckStates(true);
									subBeanList.add(subBean);

									// 2023_10_23_09:52 添加获取替代料BOM Start
									AIFComponentContext[] subComponmentContext = subBomline.getChildren();
									if (CommonTools.isNotEmpty(subComponmentContext)) {
										Sync2ndSourceInfo subChildBean = get2ndSourceStruct(subBomline);
										List<Sync2ndSourceInfo> subChilds = subChildBean.getChilds();
										subChilds.forEach(bean -> {
											subBean.addChild(bean);
										});

									}
									// 2023_10_23_13:59 添加获取替代料BOM End

								}
								bomBean.setSubstitutesList(subBeanList);
								bomBean.setMaterialGroupItemId(matGroupItemId);
							}
							rootBean.addChild(bomBean);
						}
					} catch (Exception e1) {
						throw new RuntimeException(e1);
					}
				});
				if (rootBean.getChilds().size() > 0) {
					rootBean.getChilds().sort(Comparator.comparing(Sync2ndSourceInfo::getFindNum));
				}
			}
		}
		return rootBean;
	}

	/**
	 * 获取含有替代料的BOM结构
	 * 
	 * @param uid
	 * @return
	 */
	public AjaxResult get2ndSourceEBOMStruct(String uid) {
		Sync2ndSourceInfo topBomInfo = null;
		try {
			long startTime = System.currentTimeMillis();
			TCComponent obj = TCUtil.loadObjectByUid(uid);
			if (obj instanceof TCComponentItemRevision) {

				TCComponentItemRevision revision = (TCComponentItemRevision) obj;
				EBOMWindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine topBomLine = EBOMWindow.setWindowTopLine(revision.getItem(), revision, null, null);

//				TCComponentBOMLine topBomLine = (TCComponentBOMLine) session.getComponentManager().getTCComponent(uid);
//				EBOMWindow = topBomLine.window();
				if (EBOMWindow == null) {
					return AjaxResult.error(AjaxResult.STATUS_NO_RESULT, "BOMWindow已经被关闭");
				}

				updateEBOMService.loadAllProperties(topBomLine, ALL_PART_ATTRS, ALL_BOM_ATTRS);
				String currentProjectID = getProjectId(topBomLine); // 返回专案ID

				System.out.println("==>> currentProjectID: " + currentProjectID);
				ebomTreeTools.setCurrentProjectID(currentProjectID.toUpperCase());

				topBomInfo = get2ndSourceStruct(topBomLine);
				if (CommonTools.isEmpty(topBomInfo)) {
					throw new Exception("遍历BOMLine失败");
				}
			}
			System.out.println("get2ndSourceEBOMStruct cast time ::  " + (System.currentTimeMillis() - startTime));
			return new AjaxResult(AjaxResult.STATUS_SUCCESS, "查询成功", topBomInfo);

		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
		}

	}

	/**
	 * 返回项目ID
	 * 
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
			Sync2ndSourceInfo topBomInfo = null;
			TCComponent obj = TCUtil.loadObjectByUid(uid);
			if (obj instanceof TCComponentItemRevision) {

				TCComponentItemRevision revision = (TCComponentItemRevision) obj;
				EBOMWindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine topBomLine = EBOMWindow.setWindowTopLine(revision.getItem(), revision, null, null);

//				TCComponentBOMLine topBomLine = (TCComponentBOMLine) session.getComponentManager().getTCComponent(uid);
//				EBOMWindow = topBomLine.window();
				if (EBOMWindow == null) {
					return AjaxResult.error(AjaxResult.STATUS_NO_RESULT, "BOMWindow已经被关闭");
				}

				ebomTreeTools.setSyncFrom(syncFrom);
				ebomTreeTools.setProjectID(projectID);

				topBomInfo = ebomTreeTools.getSingleSync2ndSourceStruct(topBomLine);
				if (CommonTools.isEmpty(topBomInfo)) {
					throw new Exception("ͬ同步单阶2nd Source 失败");
				}
			}
//			ebomTreeTools.setSingleAltGroup(topBomInfo, false); // Set 2nd Source GroupId
			return AjaxResult.success("查询成功", topBomInfo);

		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
		}
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
			Sync2ndSourceInfo topBomInfo = null;

			TCComponent obj = TCUtil.loadObjectByUid(uid);
			if (obj instanceof TCComponentItemRevision) {

				TCComponentItemRevision revision = (TCComponentItemRevision) obj;
				EBOMWindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine topBomLine = EBOMWindow.setWindowTopLine(revision.getItem(), revision, null, null);

//				TCComponentBOMLine topBomLine = (TCComponentBOMLine) session.getComponentManager().getTCComponent(uid);
//				EBOMWindow = topBomLine.window();
				if (EBOMWindow == null) {
					return AjaxResult.error(AjaxResult.STATUS_NO_RESULT, "BOMWindow已经被关闭");
				}

				updateEBOMService.loadAllProperties(topBomLine, ALL_PART_ATTRS, ALL_BOM_ATTRS);

				ebomTreeTools.setSyncFrom(syncFrom);
				ebomTreeTools.setProjectID(projectID);

				topBomInfo = getSync2ndSourceStruct(topBomLine);
				if (CommonTools.isEmpty(topBomInfo)) {
					throw new Exception("遍历BOMLine失败");
				}
			}
			System.out.println("getSync2ndSourceInfo cast time ::  " + (System.currentTimeMillis() - startTime));

			System.out.println(AjaxResult.success("查询成功", topBomInfo));

			return AjaxResult.success("查询成功", topBomInfo);

		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "BOM数据获取失败");
		}
	}

	/**
	 * 获取同步替代料群组的BOM结构树
	 * 
	 * @param topLine
	 * @param altGroup
	 * @return
	 * @throws TCException
	 */
	public Sync2ndSourceInfo getSync2ndSourceStruct(TCComponentBOMLine topLine) throws TCException {
//		topLine.unpack();
		topLine.refresh();
//		topLine.clearCache();
		Sync2ndSourceInfo rootBean = new Sync2ndSourceInfo(topLine);

		String d9_MaterialGroup = topLine.getItemRevision().getProperty("d9_MaterialGroup");
		System.out.println(d9_MaterialGroup);

		if (!"B8X80".equals(d9_MaterialGroup)) {

			AIFComponentContext[] componmentContext = null;
			boolean hasChildren = topLine.hasChildren();
			if (hasChildren) {
//			componmentContext = topLine.getPreviousChildren();
//			if (componmentContext == null) {
//				componmentContext = topLine.getChildren();
//			}
				componmentContext = topLine.getChildren();
			}

//		rootBean.setAlternativeCode(AlternativeConstant.PRI);

			AtomicBoolean verCheckFlag = new AtomicBoolean(false);
			String[] verCheckResult = { "" };
			if (CommonTools.isNotEmpty(componmentContext)) {
				Stream.of(componmentContext).forEach(e -> {
					try {
						TCComponentBOMLine bomLine = (TCComponentBOMLine) e.getComponent();
						if (!bomLine.isSubstitute()) {
							Sync2ndSourceInfo bomBean = getSync2ndSourceStruct(bomLine);
							bomBean.setParentItem(rootBean.getItem());
							List<Sync2ndSourceInfo> subBeanList = ebomTreeTools.getTotal2ndSourceSubList(bomLine, bomBean, true);
							if (CommonTools.isNotEmpty(subBeanList)) {
								if (CommonTools.isEmpty(verCheckResult[0])) {
									TCComponentBOMLine parentBomLine = bomLine.parent();
									if (CommonTools.isNotEmpty(parentBomLine)) {
										ebomTreeTools.checkVerCountByLock(parentBomLine.getItemRevision(), verCheckResult, verCheckFlag);
									}
								}

								bomBean.setVerNote(verCheckResult[0]); // 判断此对象版本是否为初版
								
//							TCComponentBOMLine parentBomLine = bomLine.parent();
//							if (CommonTools.isNotEmpty(parentBomLine)) {
//								bomBean.setVerNote(checkVerCount(parentBomLine.getItemRevision())); // 判断此对象版本是否为初版
//							}

								String altGroup = ebomTreeTools.getAltGroupByRule(bomBean.getFindNum());
								if (CommonTools.isEmpty(altGroup)) {
									throw new Exception(
											"零组件ID为: " + bomBean.getItem() + ", 版本号为: " + bomBean.getVersion()
													+ " findNum为: " + bomBean.getFindNum() + ", 获取替代料群组失败!");
								}

								bomBean.setAlternativeGroupNew(altGroup);
								subBeanList.forEach(bean -> {
									bean.setAlternativeGroupNew(bomBean.getAlternativeGroup());
								});

								bomBean.setAlternativeCode(AlternativeConstant.PRI);
								bomBean.setUsageProb(100);
								bomBean.setMaterialGroupItemId(subBeanList.get(0).getMaterialGroupItemId()); // 主料设置替代料群组ItemId

							}

							// 2023_10_23_15:19 同步添加替代料BOM Start
							if (bomLine.hasSubstitutes()) {
								TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();
								for (TCComponentBOMLine subBomline : listSubstitutes) {
									AIFComponentContext[] subComponmentContext = subBomline.getChildren();
									if (CommonTools.isNotEmpty(subComponmentContext)) {
										Sync2ndSourceInfo subChildBean = getSync2ndSourceStruct(subBomline);
										List<Sync2ndSourceInfo> subChilds = subChildBean.getChilds();
										Optional<Sync2ndSourceInfo> findAny = subBeanList.stream().filter(bean -> {
											return bean.getItemRevUid().equals(subChildBean.getItemRevUid());
										}).findAny();

										if (findAny.isPresent()) {
											Sync2ndSourceInfo find = findAny.get();
											subChilds.forEach(bean -> {
												find.addChild(bean);
											});
										}
									}
								}
							}

							// 2023_10_23_16:26 同步添加替代料BOM End

							bomBean.setSubstitutesList(subBeanList);
							rootBean.addChild(bomBean);
						}
					} catch (Exception e1) {
						throw new RuntimeException(e1);
					}
				});
				if (rootBean.getChilds().size() > 0) {
					rootBean.getChilds().sort(Comparator.comparing(Sync2ndSourceInfo::getFindNum));
				}
			}
		}
		return rootBean;
	}

	/**
	 * 查询2nd Source替代料列表(暂时没用)
	 * 
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
	 * 保存2nd Source替代料信息
	 * 
	 * @param data
	 * @return
	 * @throws TCException
	 */
	public AjaxResult save2ndSourceSubList(String data) throws TCException {
		Gson gson = new Gson();
		Sync2ndSourceInfo topBomInfo = gson.fromJson(data, Sync2ndSourceInfo.class);
		System.out.println(gson.toJson(topBomInfo));
		EBOMLineBean rootBean = null;
		try {
			com.foxconn.mechanism.util.TCUtil.setBypass(session);
			TCComponentBOMLine topLine = (TCComponentBOMLine) session.getComponentManager()
					.getTCComponent(topBomInfo.getBomLineUid());
			EBOMWindow = topLine.window();
			if (CommonTools.isEmpty(EBOMWindow)) {
				throw new Exception("BOMWindow已经被关闭");
			}

//			ebomTreeTools.setAltGroupNew(topBomInfo, true);
			ebomTreeTools.setAltGroup(topBomInfo);
			ebomTreeTools.setEBOMWindow(EBOMWindow);

			TCComponentBOMLine rootBomLine = ebomTreeTools.checkTopLineRevise(topBomInfo); // 判断顶阶BOMLine是否需要升版
			if (CommonTools.isNotEmpty(rootBomLine)) {
				topLine = rootBomLine;
			}
			rootBean = new EBOMLineBean(topLine);
			rootBean.setIsNewVersion(UpdateEBOMService.isNewRevsion(topLine.getItemRevision()));
			ebomTreeTools.saveBOMTree(topBomInfo, "");
			return AjaxResult.success("替代料群组保存成功", rootBean);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
		} finally {
			EBOMWindow.save();
			com.foxconn.mechanism.util.TCUtil.closeBypass(session);
		}
	}
}
