package com.foxconn.electronics.L10Ebom.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang.ArrayUtils;
import com.foxconn.electronics.L10Ebom.domain.L10EBOMBean;
import com.foxconn.electronics.L10Ebom.response.BaseResultBean;
import com.foxconn.electronics.L10Ebom.response.ExternalWireBean;
import com.foxconn.electronics.L10Ebom.response.OtherResultBean;
import com.foxconn.electronics.L10Ebom.response.PartResultBean;
import com.foxconn.electronics.L10Ebom.response.PowerLineBean;
import com.foxconn.electronics.L10Ebom.response.SemiResultBean;
import com.foxconn.electronics.domain.Constants;
import com.foxconn.electronics.matrixbom.service.ProductLineBOMService;
import com.foxconn.electronics.util.TCPropName;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.TCUtil;
import com.google.gson.Gson;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.soa.client.model.ModelObject;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

public class MntL10EbomService {
	private static TCSession			session;
	private ResourceBundle				reg;
	public TCComponentItemRevision		revisionApp;
	private Set<String>					eeTypeSet			= CollUtil.newHashSet("IO BD", "VIDEO&TUNER", "USB", "HEADPHONE", "LED BD", "RESET", "MOTHER", "DOWN CONVERTER", "Wifi&Bluetooth", "MAIN BD", "INTERFACE", "KEY&B", "IR", "LOGO", "LIGHT SENSOR", "LED driver BD", "LED Lighting BD", "LED Lighting  Driver BD", "AUDIO", "DPF", "OTHERS", "FRC", "Emitter BD", "DC JACK", "Bluetooth", "LED Lightbar(for LCM)", "Network Comm BD");
	private Set<String>					piTypeSet			= CollUtil.newHashSet("Power&PI&INV&LED CONV BD", "Ctrl POW&INV&LED CONV", "Safety POWER BD");

	public static final String[]		UPDATE_ITEM_ATTRI	= { "description", "sapDescription", "mfg", "mfgPn", "unit", "materialType", "materialGroup", "procurementType", "sapRev", "supplierZF" };
	public static final String[]		UPDATE_BOM_ATTRI	= { "findNum", "location", "qty", "packageType", "side",
//			"alternativeGroup", 
			"referenceDimension", "ownerDept" };

	private static Map<String, Object>	MAX_FINDNUMMAP		= new ConcurrentHashMap<String, Object>();

	public TCSession getSession() {
		return session;
	}

	public MntL10EbomService(TCComponentItemRevision revisionApp, ResourceBundle reg2) {
		this.revisionApp = revisionApp;
		this.reg = reg2;
		session = (TCSession) revisionApp.getSession();
	}

	public AjaxResult getPartNum(String uid, String partUid, String type) {
		TCComponent obj = TCUtil.loadObjectByUid(uid);
		if (!(obj instanceof TCComponentItemRevision)) {
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, reg.getString("selectErr3.MSG"));
		}
		TCComponentItemRevision itemRev = (TCComponentItemRevision) obj;
		String objType = itemRev.getTypeObject().getName();
		if (!"D9_L10EBOMReqRevision".equals(objType)) {
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, reg.getString("selectErr4.MSG"));
		}
		List<?> resultList = null;
		switch (type){
			case "part":
				resultList = getPartList(itemRev, "D9_BOMReq_L10B_REL");
				break;
			case "semi":
				resultList = getSemiList(itemRev, "D9_BOMReq_L10C_REL");
				break;
			case "panel":
				resultList = getBasePartList(itemRev, "D9_BOMReq_L10D_REL");
				break;
			case "packing":
				resultList = getBasePartList(itemRev, "D9_BOMReq_L10E_REL");
				break;
			case "me":
				resultList = getBasePartList(itemRev, "D9_BOMReq_L10F_REL");
				break;
			case "meMuxiliary":
				resultList = getBasePartList(itemRev, "D9_BOMReq_L10G_REL");
				break;
			case "ee":
				resultList = getOtherPartList(itemRev, "D9_BOMReq_L10H_REL");
				break;
			case "externalWire":
				resultList = getExternalWireList(itemRev, partUid, "D9_BOMReq_L10I_REL");
				break;
			case "pi":
				resultList = getOtherPartList(itemRev, "D9_BOMReq_L10J_REL");
				break;
			case "powerLine":
				resultList = getPowerLineList(itemRev, partUid, "D9_BOMReq_L10K_REL");
				break;
			default:
				resultList = Collections.emptyList();
				break;
		}

		AjaxResult ajaxResult = AjaxResult.success(resultList);
		System.out.println("ajaxResult = " + ajaxResult);

		return ajaxResult;
	}

	public AjaxResult addPart(String uid, String type) {
		TCComponent obj = TCUtil.loadObjectByUid(uid);
		if (!(obj instanceof TCComponentItemRevision)) {
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, reg.getString("selectErr3.MSG"));
		}
		TCComponentItemRevision itemRev = (TCComponentItemRevision) obj;
		String objType = itemRev.getTypeObject().getName();
		if (!"D9_L10EBOMReqRevision".equals(objType)) {
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, reg.getString("selectErr4.MSG"));
		}
		try {
			String projectIds = itemRev.getProperty("project_ids");
			String function = "";
			String relation = "";
			if ("packing".equals(type)) {
				relation = "D9_BOMReq_L10E_REL";
				function = "__D9_Find_Packing";

			} else if ("ee".equals(type)) {
				relation = "D9_BOMReq_L10H_REL";
				function = "__D9_Find_PCA";
			} else if ("pi".equals(type)) {
				relation = "D9_BOMReq_L10J_REL";
				function = "__D9_Find_PCA";
			} else if ("me".equals(type)) {
				// relation = "D9_BOMReq_L10F_REL";
				return AjaxResult.success(true);
			} else {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "param type is error");
			}
			// 查詢需要的對象，並將對象的最新版本的數據掛載到文件夾下
			Map<String, TCComponentItemRevision> map = getCurrentPart(itemRev, relation);
			TCComponent[] executeQuery = TCUtil.executeQuery(session, function, new String[] { "project_list.project_id" }, new String[] { projectIds });
			if (executeQuery != null && executeQuery.length > 0) {
				for (int i = 0; i < executeQuery.length; i++) {

					TCComponentItemRevision lastRevision = null;
					if (executeQuery[i] instanceof TCComponentItem) {
						lastRevision = ((TCComponentItem) executeQuery[i]).getLatestItemRevision();
					} else {
						continue;
					}

					if ("ee".equals(type) && !eeTypeSet.contains(lastRevision.getProperty("d9_PCBAssyClassification_L6"))) {
						continue;
					}
					if ("pi".equals(type) && !piTypeSet.contains(lastRevision.getProperty("d9_PCBAssyClassification_L6"))) {
						continue;
					}
					String itemId = lastRevision.getProperty("item_id");
					if (ObjectUtil.isNotNull(map) && ObjectUtil.isNotNull(map.get(itemId))) {
						map.remove(itemId);
					}
					map.put(itemId, lastRevision);
				}
				itemRev.setRelated(relation, map.values().toArray(new TCComponent[map.keySet().size()]));
			}
			return AjaxResult.success(true);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "");
		}
	}

	public AjaxResult deletePart(String uid, String type, String partUids) {
		TCComponent obj = TCUtil.loadObjectByUid(uid);
		if (!(obj instanceof TCComponentItemRevision)) {
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, reg.getString("selectErr3.MSG"));
		}
		List<String> list = StrSplitter.splitTrim(partUids, ",", true);
		Set<String> set = new HashSet<String>();
		for (String str : list) {
			TCComponent part = TCUtil.loadObjectByUid(str);
			if (!(part instanceof TCComponentItemRevision)) {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, reg.getString("selectErr3.MSG"));
			}
			set.add(part.getUid());
		}
		TCComponentItemRevision itemRev = (TCComponentItemRevision) obj;
		String objType = itemRev.getTypeObject().getName();
		if (!"D9_L10EBOMReqRevision".equals(objType)) {
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, reg.getString("selectErr4.MSG"));
		}
		String relation = "";
		switch (type){
			case "part":
				relation = "D9_BOMReq_L10B_REL";
				break;
			case "semi":
				relation = "D9_BOMReq_L10C_REL";
				break;
			case "panel":
				relation = "D9_BOMReq_L10D_REL";
				break;
			case "packing":
				relation = "D9_BOMReq_L10E_REL";
				break;
			case "me":
				relation = "D9_BOMReq_L10F_REL";
				break;
			case "meMuxiliary":
				relation = "D9_BOMReq_L10G_REL";
				break;
			case "ee":
				relation = "D9_BOMReq_L10H_REL";
				break;
			case "externalWire":
				relation = "D9_BOMReq_L10I_REL";
				break;
			case "pi":
				relation = "D9_BOMReq_L10J_REL";
				break;
			case "powerLine":
				relation = "D9_BOMReq_L10K_REL";
				break;
			default:
				break;
		}
		if (StrUtil.isBlank(relation)) {
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "param type is error");
		}
		try {
			Map<String, TCComponentItemRevision> map = getCurrentPart(itemRev, relation);
			List<TCComponentItemRevision> collect = map.values().stream().filter(item -> {
				return !set.contains(item.getUid());
			}).collect(Collectors.toList());
			itemRev.setRelated(relation, collect.toArray(new TCComponent[collect.size()]));
			return AjaxResult.success(true);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "");
		}
	}

	public AjaxResult batchAddPart(String uid, String type, String partUids) {
		try {
			TCComponent obj = TCUtil.loadObjectByUid(uid);
			if (!(obj instanceof TCComponentItemRevision)) {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, reg.getString("selectErr3.MSG"));
			}
			if (StrUtil.isBlank(partUids)) {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "param partUids cannot blank");
			}
			List<String> list = StrSplitter.splitTrim(partUids, ",", true);
			Map<String, TCComponentItemRevision> map = new HashMap<String, TCComponentItemRevision>();
			for (String str : list) {
				TCComponent part = TCUtil.loadObjectByUid(str);
				if (!(part instanceof TCComponentItemRevision)) {
					return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, reg.getString("selectErr3.MSG"));
				}
				TCComponentItemRevision itemRevision = (TCComponentItemRevision) part;
				map.put(itemRevision.getProperty("item_id"), itemRevision);
			}
			TCComponentItemRevision itemRev = (TCComponentItemRevision) obj;
			String objType = itemRev.getTypeObject().getName();
			if (!"D9_L10EBOMReqRevision".equals(objType)) {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, reg.getString("selectErr4.MSG"));
			}
			String relation = "";
			switch (type){
				case "part":
					relation = "D9_BOMReq_L10B_REL";
					break;
				case "semi":
					relation = "D9_BOMReq_L10C_REL";
					break;
				case "panel":
					relation = "D9_BOMReq_L10D_REL";
					break;
				case "packing":
					relation = "D9_BOMReq_L10E_REL";
					break;
				case "me":
					relation = "D9_BOMReq_L10F_REL";
					break;
				case "meMuxiliary":
					relation = "D9_BOMReq_L10G_REL";
					break;
				case "ee":
					relation = "D9_BOMReq_L10H_REL";
					break;
				case "externalWire":
					relation = "D9_BOMReq_L10I_REL";
					break;
				case "pi":
					relation = "D9_BOMReq_L10J_REL";
					break;
				case "powerLine":
					relation = "D9_BOMReq_L10K_REL";
					break;
				default:
					break;
			}
			if (StrUtil.isBlank(relation)) {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "param type is error");
			}
			Map<String, TCComponentItemRevision> oldMap = getCurrentPart(itemRev, relation);
			for (String key : oldMap.keySet()) {
				if (ObjectUtil.isNull(map.get(key))) {
					map.put(key, oldMap.get(key));
				}
			}
			itemRev.setRelated(relation, map.values().toArray(new TCComponent[map.values().size()]));
			return AjaxResult.success(true);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "");
		}
	}

	public AjaxResult searchPart(String itemId) {
		try {
			TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件 ID", new String[] { "item_id" }, new String[] { itemId + "*" });
			List<BaseResultBean> list = new ArrayList<>(executeQuery.length);
			if (executeQuery != null && executeQuery.length > 0) {
				for (int i = 0; i < executeQuery.length; i++) {
					TCComponentItem item = null;
					if (executeQuery[i] instanceof TCComponentItem) {
						item = (TCComponentItem) executeQuery[i];
					} else {
						continue;
					}
					TCComponentItemRevision itemRevision = item.getLatestItemRevision();
					BaseResultBean bean = new BaseResultBean();
					bean.setItem("" + (i + 1));
					bean.setCode(itemRevision.getProperty("item_id"));
					bean.setDesc(itemRevision.getProperty("d9_EnglishDescription"));
					bean.setVersion(itemRevision.getProperty("item_revision_id"));
					bean.setRelease(TCUtil.isReleased(itemRevision));

					// String desc =
					// itemRevision.getProperty("d9_EnglishDescription");
					String sapDescription = itemRevision.getProperty("d9_DescriptionSAP");
					String mfg = itemRevision.getProperty("d9_ManufacturerID");
					String mfgPn = itemRevision.getProperty("d9_ManufacturerPN");
					String unit = itemRevision.getProperty("d9_Un");
					String materialType = itemRevision.getProperty("d9_MaterialType");
					String materialGroup = itemRevision.getProperty("d9_MaterialGroup");
					String procurementType = itemRevision.getProperty("d9_ProcurementMethods");
					String sapRev = itemRevision.getProperty("d9_SAPRev");
					String supplierZF = itemRevision.getProperty("d9_SupplierZF");
					bean.setSapDescription(sapDescription);
					bean.setMfg(mfg);
					bean.setMfgPn(mfgPn);
					bean.setUnit(unit);
					bean.setMaterialType(materialType);
					bean.setMaterialGroup(materialGroup);
					bean.setProcurementType(procurementType);
					bean.setSapRev(sapRev);
					bean.setSupplierZF(supplierZF);

					bean.setQty(1);
					bean.setUid(itemRevision.getUid());
					list.add(bean);
				}
				return AjaxResult.success(list);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return AjaxResult.success(Collections.EMPTY_LIST);
	}

	private Map<String, TCComponentItemRevision> getCurrentPart(TCComponentItemRevision itemRev, String relation) throws Exception {
		Map<String, TCComponentItemRevision> resultMap = new HashMap<String, TCComponentItemRevision>();
		TCComponent[] relatedComponents = itemRev.getRelatedComponents(relation);
		if (relatedComponents == null || relatedComponents.length == 0) {
			return resultMap;
		}
		for (int i = 0; i < relatedComponents.length; i++) {
			TCComponentItemRevision itemRevision = null;
			if (relatedComponents[i] instanceof TCComponentItemRevision) {
				itemRevision = (TCComponentItemRevision) relatedComponents[i];
			} else {
				continue;
			}
			String itemId = itemRevision.getProperty("item_id");
			resultMap.put(itemId, itemRevision);
		}
		return resultMap;
	}

	private List<OtherResultBean> getOtherPartList(TCComponentItemRevision itemRev, String relation) {
		try {
			TCComponent[] relatedComponents = itemRev.getRelatedComponents(relation);
			if (relatedComponents == null || relatedComponents.length == 0) {
				return Collections.emptyList();
			}
			List<OtherResultBean> list = new ArrayList<>(relatedComponents.length);
			for (int i = 0; i < relatedComponents.length; i++) {
				TCComponentItemRevision newItemR = null;
				if (relatedComponents[i] instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRevision = (TCComponentItemRevision) relatedComponents[i];
					newItemR = itemRevision.getItem().getLatestItemRevision();
					
					if(itemRevision != newItemR) {
						itemRev.remove(relation, itemRevision);
						itemRev.add(relation, newItemR);
					}
					
				} else {
					continue;
				}
				BaseResultBean baseBean = getBaseResultBean(newItemR, i);
				if (ObjectUtil.isNotNull(baseBean)) {
					OtherResultBean bean = new OtherResultBean();
					BeanUtil.copyProperties(baseBean, bean);
					bean.setModelName(newItemR.getProperty("d9_FoxconnModelName"));
					bean.setDerivativeType(newItemR.getProperty("d9_DerivativeTypeDC"));
					bean.setPacType(newItemR.getProperty("d9_PCBAssyClassification_L6"));
					list.add(bean);
				}
			}
			return list;
		} catch (TCException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	private List<BaseResultBean> getBasePartList(TCComponentItemRevision itemRev, String relation) {
		try {
			TCComponent[] relatedComponents = itemRev.getRelatedComponents(relation);
			if (relatedComponents == null || relatedComponents.length == 0) {
				return Collections.emptyList();
			}
			List<BaseResultBean> list = new ArrayList<>(relatedComponents.length);
			for (int i = 0; i < relatedComponents.length; i++) {
				TCComponentItemRevision newItemR = null;
				if (relatedComponents[i] instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRevision = (TCComponentItemRevision) relatedComponents[i];
					newItemR = itemRevision.getItem().getLatestItemRevision();
					
					if(itemRevision != newItemR) {
						itemRev.remove(relation, itemRevision);
						itemRev.add(relation, newItemR);
					}
				} else {
					continue;
				}
				BaseResultBean bean = getBaseResultBean(newItemR, i);
				if (ObjectUtil.isNotNull(bean)) {
					list.add(bean);
				}
			}
			return list;
		} catch (TCException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	private List<PartResultBean> getPartList(TCComponentItemRevision itemRev, String relation) {
		try {
			TCUtil.setBypass(session);
			TCComponent[] relatedComponents = itemRev.getRelatedComponents(relation);
			if (relatedComponents == null || relatedComponents.length == 0) {
				return Collections.emptyList();
			}
			ArrayList<TCComponentItemRevision> list_itemRevisions = new ArrayList<TCComponentItemRevision>();
			boolean isSaveAs = false;
			for (int i = 0; i < relatedComponents.length; i++) {
				TCComponentItemRevision saveAsRev = null;
				if (relatedComponents[i] instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRevision = (TCComponentItemRevision) relatedComponents[i];

					boolean isReleased = TCUtil.isReleased(itemRevision);
					if (isReleased) {
						TCComponentItemRevision latestItemRevision = itemRevision.getItem().getLatestItemRevision();
						boolean latestRevReleased = TCUtil.isReleased(latestItemRevision);
						if (latestRevReleased) {
							saveAsRev = itemRevision.saveAs("");
						} else {
							saveAsRev = latestItemRevision;
						}

						isSaveAs = true;
					} else {
						saveAsRev = itemRevision;
					}
				} else {
					continue;
				}
				list_itemRevisions.add(saveAsRev);
			}
			if (list_itemRevisions != null && list_itemRevisions.size() > 0 && isSaveAs) {
				TCComponentItemRevision[] array = list_itemRevisions.toArray(new TCComponentItemRevision[] {});
				itemRev.setRelated(relation, array);
			}

			List<PartResultBean> list = new ArrayList<>(relatedComponents.length);
			for (int i = 0; i < list_itemRevisions.size(); i++) {
				TCComponentItemRevision itemRevision = list_itemRevisions.get(i);

				BaseResultBean baseBean = getBaseResultBean(itemRevision, i);
				if (ObjectUtil.isNotNull(baseBean)) {
					PartResultBean bean = new PartResultBean();
					BeanUtil.copyProperties(baseBean, bean);
					bean.setD9_CustomerModelName(itemRevision.getProperty("d9_CustomerModelName"));
					bean.setD9_FoxconnModelName(itemRevision.getProperty("d9_FoxconnModelName"));
					bean.setD9_FinishPNDesc(itemRevision.getProperty("d9_FinishPNDesc"));
					bean.setD9_ShippingArea(itemRevision.getProperty("d9_ShippingArea"));
					bean.setD9_PowerLineType(itemRevision.getProperty("d9_PowerLineType"));
					bean.setD9_PCBAInterface(itemRevision.getProperty("d9_PCBAInterface"));
					bean.setD9_IsSpeaker(itemRevision.getProperty("d9_IsSpeaker"));
					bean.setD9_Color(itemRevision.getProperty("d9_Color"));

					bean.setD9_WireType(itemRevision.getProperty("d9_WireType"));
					bean.setD9_Other(itemRevision.getProperty("d9_Other"));
					bean.setD9_ShipSize(itemRevision.getProperty("d9_ShipSize"));
					bean.setD9_ShipType(itemRevision.getProperty("d9_ShipType"));
					bean.setD9_RefMaterialPN(itemRevision.getProperty("d9_RefMaterialPN"));
					bean.setRemark(itemRevision.getProperty("d9_Remarks"));

					list.add(bean);
				}
			}
			return list;
		} catch (TCException e) {
			e.printStackTrace();
		} finally {
			TCUtil.closeBypass(session);
		}
		return Collections.emptyList();
	}

	private List<SemiResultBean> getSemiList(TCComponentItemRevision itemRev, String relation) {
		try {
			TCComponent[] relatedComponents = itemRev.getRelatedComponents(relation);
			if (relatedComponents == null || relatedComponents.length == 0) {
				return Collections.emptyList();
			}
			List<SemiResultBean> list = new ArrayList<>(relatedComponents.length);
			for (int i = 0; i < relatedComponents.length; i++) {
				TCComponentItemRevision newItemR = null;
				if (relatedComponents[i] instanceof TCComponentItemRevision) {
					TCComponentItemRevision itemRevision = (TCComponentItemRevision) relatedComponents[i];
					newItemR = itemRevision.getItem().getLatestItemRevision();
					if(itemRevision != newItemR) {
						itemRev.remove(relation, itemRevision);
						itemRev.add(relation, newItemR);
					}
				} else {
					continue;
				}
				BaseResultBean baseBean = getBaseResultBean(newItemR, i);
				if (ObjectUtil.isNotNull(baseBean)) {
					SemiResultBean bean = new SemiResultBean();
					BeanUtil.copyProperties(baseBean, bean);
					bean.setType(newItemR.getProperty("d9_ChineseDescription"));
					bean.setRemark(newItemR.getProperty("d9_Remarks"));
					list.add(bean);
				}
			}
			return list;
		} catch (TCException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	private BaseResultBean getBaseResultBean(TCComponentItemRevision itemRevision, int i) {
		try {
			BaseResultBean bean = new BaseResultBean();
			bean.setItem("" + (i + 1));
			bean.setCode(itemRevision.getProperty("item_id"));
			bean.setDesc(itemRevision.getProperty("d9_EnglishDescription"));
			bean.setVersion(itemRevision.getProperty("item_revision_id"));
			bean.setRelease(TCUtil.isReleased(itemRevision));
			bean.setQty(1);
			bean.setUid(itemRevision.getUid());
			bean.setSapDescription(itemRevision.getProperty("d9_DescriptionSAP"));
			bean.setMfg(itemRevision.getProperty("d9_ManufacturerID"));
			bean.setMfgPn(itemRevision.getProperty("d9_ManufacturerPN"));
			bean.setMaterialGroup(itemRevision.getProperty("d9_MaterialGroup"));
			bean.setMaterialType(itemRevision.getProperty("d9_MaterialType"));
			bean.setUnit(itemRevision.getProperty("d9_Un"));
			bean.setProcurementType(itemRevision.getProperty("d9_ProcurementMethods"));
			bean.setTempPN(itemRevision.getProperty("d9_TempPN"));
			bean.setSapRev(itemRevision.getProperty("d9_SAPRev"));
			bean.setSupplierZF(itemRevision.getProperty("d9_SupplierZF"));
			bean.setObject_type(itemRevision.getProperty("object_type"));

			return bean;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<ExternalWireBean> getExternalWireList(TCComponentItemRevision itemRev, String partUid, String relation) {
		try {
			// Type d9_Type, 客户信息 d9_Customer

			String d9_Customer = itemRev.getProperty("d9_Customer");
			if (partUid != null && !"".equals(partUid)) {
				TCComponent partObj = TCUtil.loadObjectByUid(partUid);
				if (partObj != null) {
					String d9_WireType = partObj.getProperty("d9_WireType");
					if (d9_WireType.contains("USB")) {
						d9_WireType = d9_WireType.replaceAll("USB", "USB;TYPE C");
					}

					if (d9_WireType.contains(",")) {
						d9_WireType = d9_WireType.replaceAll(",", ";");
					}
					System.out.println("d9_Customer = " + d9_Customer + ",d9_WireType = " + d9_WireType);

					TCComponent[] executeQuery = TCUtil.executeQuery(session, "__D9_Find_MNTMaterialGroup", new String[] { "d9_Type", "d9_Customer" }, new String[] { d9_WireType, d9_Customer });
					LinkedHashSet<TCComponentItemRevision> set = new LinkedHashSet<>();
					if (executeQuery != null && executeQuery.length > 0) {
						for (int i = 0; i < executeQuery.length; i++) {
							TCComponentItemRevision itemRevision = null;
							if (executeQuery[i] instanceof TCComponentItemRevision) {
								itemRevision = (TCComponentItemRevision) executeQuery[i];

								AIFComponentContext[] children = itemRevision.getChildren("ps_children");
								for (int j = 0; j < children.length; j++) {
									InterfaceAIFComponent component = children[j].getComponent();
									if (component instanceof TCComponentItemRevision) {
										TCComponentItemRevision itemRevision2 = (TCComponentItemRevision) component;
										//itemRevision2 = itemRevision2.getItem().getLatestItemRevision();
										
										if (!set.contains(itemRevision2)) {
											set.add(itemRevision2);
										}
									}
								}
							}
						}
					}

					if (set != null && set.size() > 0) {
						List<ExternalWireBean> list = new ArrayList<>(set.size());
						int i = 0;
						for (TCComponentItemRevision itemRevision : set) {
							BaseResultBean baseBean = getBaseResultBean(itemRevision, i);

							if (ObjectUtil.isNotNull(baseBean)) {
								ExternalWireBean bean = new ExternalWireBean();
								BeanUtil.copyProperties(baseBean, bean);
								bean.setD9_Type(itemRevision.getProperty("d9_Type"));
								bean.setD9_SubClass_L6(itemRevision.getProperty("d9_SubClass_L6"));
								list.add(bean);
							}
							i++;
						}
						return list;
					}
				}
			}

			return null;
		} catch (TCException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	private List<PowerLineBean> getPowerLineList(TCComponentItemRevision itemRev, String partUid, String relation) {
		try {
			// Type d9_Type, 客户信息 d9_Customer, 物料类型 d9_MaterialType

			String d9_Customer = itemRev.getProperty("d9_Customer");
			if (partUid != null && !"".equals(partUid)) {
				TCComponent partObj = TCUtil.loadObjectByUid(partUid);
				if (partObj != null) {
					String d9_MaterialType = "電源綫";
					// String d9_MaterialType =
					// partObj.getProperty("d9_MaterialType");
					System.out.println("d9_Customer = " + d9_Customer + ",d9_MaterialType = " + d9_MaterialType);

					TCComponent[] executeQuery = TCUtil.executeQuery(session, "__D9_Find_MNTMaterialGroup", new String[] { "d9_MaterialType", "d9_Customer" }, new String[] { d9_MaterialType, d9_Customer });

					LinkedHashSet<TCComponentItemRevision> set = new LinkedHashSet<>();
					if (executeQuery != null && executeQuery.length > 0) {
						for (int i = 0; i < executeQuery.length; i++) {
							TCComponentItemRevision itemRevision = null;
							if (executeQuery[i] instanceof TCComponentItemRevision) {
								itemRevision = (TCComponentItemRevision) executeQuery[i];

								AIFComponentContext[] children = itemRevision.getChildren("ps_children");
								for (int j = 0; j < children.length; j++) {
									InterfaceAIFComponent component = children[j].getComponent();
									if (component instanceof TCComponentItemRevision) {
										TCComponentItemRevision itemRevision2 = (TCComponentItemRevision) component;
										//itemRevision2 = itemRevision2.getItem().getLatestItemRevision();
										if (!set.contains(itemRevision2)) {
											set.add(itemRevision2);
										}
									}
								}
							}
						}
					}

					if (set != null && set.size() > 0) {
						List<PowerLineBean> list = new ArrayList<>(set.size());
						int i = 0;
						for (TCComponentItemRevision itemRevision : set) {
							BaseResultBean baseBean = getBaseResultBean(itemRevision, i);

							if (ObjectUtil.isNotNull(baseBean)) {
								PowerLineBean bean = new PowerLineBean();
								BeanUtil.copyProperties(baseBean, bean);
								bean.setD9_Type(itemRevision.getProperty("d9_Type"));
								bean.setD9_ShipmentCountry_L6(itemRevision.getProperty("d9_ShipmentCountry_L6"));
								bean.setD9_CustomerPN(itemRevision.getProperty("d9_CustomerPN"));

								list.add(bean);
							}
							i++;
						}
						return list;
					}
				}
			}

			return null;
		} catch (TCException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	/**
	 * 初始化展示BOM
	 * @param uid
	 * @param all
	 * @return
	 */
	public AjaxResult initBOMLine(String uid, boolean all) {
		TCComponent obj = TCUtil.loadObjectByUid(uid);
		if (obj instanceof TCComponentItemRevision) {

			TCComponentItemRevision revision = (TCComponentItemRevision) obj;
			try {
				TCComponentGroup currentGroup = session.getCurrentGroup();
				String group = currentGroup.getFullName();
				if (group.contains(".")) {
					group = group.substring(0, group.indexOf("."));
				}
				// System.out.println("group = "+group);

				// 初始化
				L10EBOMBean topebom = getTopBOMLine(revision, all, group);
				if ("BOM".equals(group)) {
					topebom.setIsCanDcn(true);
				} else {
					topebom.setIsCanDcn(false);
				}

				boolean isBOMViewWFTask = IsBOMViewWFTask(revisionApp, group);
				System.out.println("isBOMViewWFTask = " + isBOMViewWFTask);
				topebom.setIsBOMViewWFTask(isBOMViewWFTask);

				AjaxResult ajaxResult = AjaxResult.success(topebom);
				System.out.println("ajaxResult = " + ajaxResult);
				System.out.println("uid = " + uid);
				return ajaxResult;
			} catch (TCException e) {
				e.printStackTrace();
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "初始化加载BOM失败！");
	}

	/**
	 * 展示下层BOM
	 * @param uid
	 * @return
	 */
	public AjaxResult expansionChile(String uid, String key) {

		TCComponentBOMWindow bomwindow = null;
		try {
			TCComponent itemRev = session.getComponentManager().getTCComponent(uid);
			if (itemRev instanceof TCComponentItemRevision) {

				TCComponentItemRevision revision = (TCComponentItemRevision) itemRev;

				bomwindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine bomLine = bomwindow.setWindowTopLine(revision.getItem(), revision, null, null);
				unPack(bomLine);

				AIFComponentContext[] childrens = bomLine.getChildren();
				ArrayList<L10EBOMBean> listEbom = new ArrayList<L10EBOMBean>();
				if (childrens != null && childrens.length > 0) {
					for (int i = 0; i < childrens.length; i++) {
						TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
						L10EBOMBean ebom = new L10EBOMBean(children);
						ebom.setItemRevUid(uid);
						ebom.setParentItem(revision.getProperty("item_id"));
						ebom.setBomId(key);

						if (children.hasSubstitutes()) {
							TCComponentBOMLine[] listSubstitutes = children.listSubstitutes();
							for (TCComponentBOMLine subBomline : listSubstitutes) {
								L10EBOMBean subebom = new L10EBOMBean(subBomline);
								subebom.setParentRevUid(ebom.getItemRevUid());
								subebom.setParentItem(ebom.getItem());
								subebom.setBomId(key);
								subebom.setIsSecondSource(true);
								ebom.addSecondSource(subebom);
							}
						}

						listEbom.add(ebom);
					}
				}

				AjaxResult ajaxResult = AjaxResult.success(listEbom);
				System.out.println("ajaxResult = " + ajaxResult);

				return ajaxResult;

			}
		} catch (TCException e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		} finally {
			try {
				bomwindow.close();
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "展开BOM失败！");

	}

	/**
	 * 删除结构
	 * @param topebom
	 * @throws TCException
	 */
	public void cutBOMChile(TCComponentBOMLine topebom, String item) throws TCException {
		AIFComponentContext[] childrens = topebom.getChildren();
		if (childrens != null && childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
				String bl_item_item_id = children.getProperty("bl_item_item_id");
				if (item.equals(bl_item_item_id)) {
					children.cut();
					return;
				}
			}
		}
	}

	/**
	 * 搭建BOM
	 * @param childs
	 * @param topebom
	 * @throws Exception
	 */
	public void addBOMChile(TCComponentBOMLine topebom, L10EBOMBean ebomBean) throws Exception {
		String childUid = ebomBean.getItemRevUid();
		TCComponentItemRevision itemRev = (TCComponentItemRevision) session.getComponentManager().getTCComponent(childUid);
		
		if (itemRev != null) {

			//判断零件属性是否修改
			// "description", "sapDescription", "mfg", "mfgPn", "unit", "materialType", "materialGroup", "procurementType", "sapRev", "supplierZF" 
			
			String d9_EnglishDescription = ebomBean.getDescription();
			String d9_DescriptionSAP = ebomBean.getSapDescription();
			String d9_ManufacturerID = ebomBean.getMfg();
			String d9_ManufacturerPN = ebomBean.getMfgPn();
			String d9_Un = ebomBean.getUnit();
			String d9_MaterialType = ebomBean.getMaterialType();
			String d9_MaterialGroup = ebomBean.getMaterialGroup();
			String d9_ProcurementMethods = ebomBean.getProcurementType();
			String d9_SAPRev = ebomBean.getSapRev();
			String d9_SupplierZF = ebomBean.getSupplierZF();
			
			String[] itemName = new String[] {"d9_EnglishDescription","d9_DescriptionSAP","d9_ManufacturerID",
					"d9_ManufacturerPN","d9_Un","d9_MaterialType","d9_MaterialGroup",
					"d9_ProcurementMethods","d9_SAPRev","d9_SupplierZF"};
			String[] properties = itemRev.getProperties(itemName);
			if(properties !=null && properties.length > 0) {
				if(! (properties[0].equals(d9_EnglishDescription) && properties[1].equals(d9_DescriptionSAP) && properties[2].equals(d9_ManufacturerID) 
					&& properties[3].equals(d9_ManufacturerPN) && properties[4].equals(d9_Un) && properties[5].equals(d9_MaterialType) && properties[6].equals(d9_MaterialGroup)
					&& properties[7].equals(d9_ProcurementMethods) && properties[8].equals(d9_SAPRev) && properties[9].equals(d9_SupplierZF)) ){
					
					itemRev = getLastRevision(itemRev);
					
					String item = ebomBean.getItem();
					String type = itemRev.getType();
					
					if (item.startsWith("7140") && "D9_VirtualPartRevision".equals(type)) {
						if (revisionApp != null) {
							TCComponent[] relatedComponents = revisionApp.getRelatedComponents("D9_BOMReq_L10C_REL"); // 半成品
							String item_id = itemRev.getProperty("item_id");
							for (int j = 0; j < relatedComponents.length; j++) {
								if (relatedComponents[j] instanceof TCComponentItemRevision) {
									TCComponentItemRevision cuTcComponentItemRevision = (TCComponentItemRevision) relatedComponents[j];
									String item_id_1 = cuTcComponentItemRevision.getProperty("item_id");
									if (item_id.equals(item_id_1)) {
										revisionApp.remove("D9_BOMReq_L10C_REL", cuTcComponentItemRevision);
										revisionApp.add("D9_BOMReq_L10C_REL", itemRev);
										break;
									}
								}
							}
						}
					}
				}
			}
			
			
			TCComponentBOMLine bomLine = topebom.add(itemRev.getItem(), itemRev, null, false);
			setProItemValue(ebomBean, itemRev);
			setProBomValue(ebomBean, bomLine);
		}
	}

	/**
	 * 保存整个BOM结构，重新搭建BOM
	 * @param data
	 */
	public AjaxResult saveBOMChile(ArrayList<L10EBOMBean> rootBeanList) throws TCException {
		TCComponentBOMWindow bomwindow = null;
		TCComponentItemRevision topitemRev = null;
		try {
			// 先处理删除数据
			for (int i = 0; i < rootBeanList.size(); i++) {
				L10EBOMBean rootBean = rootBeanList.get(i);
				Boolean isCutTree = rootBean.getIsCutTree();
				String parentRevUid = rootBean.getParentRevUid();

				String item = rootBean.getItem();

				if (parentRevUid != null && !"".equals(parentRevUid)) {
					topitemRev = (TCComponentItemRevision) session.getComponentManager().getTCComponent(parentRevUid);
				}
				if (topitemRev != null) {
					bomwindow = TCUtil.createBOMWindow(session);

					TCComponentBOMLine topebom = bomwindow.setWindowTopLine(topitemRev.getItem(), topitemRev, null, null);
					unPack(topebom);

					if (isCutTree) {
						cutBOMChile(topebom, item);
						bomwindow.save();
					}
				}
			}

			// 再处理新增和修改的数据
			for (int i = 0; i < rootBeanList.size(); i++) {
				L10EBOMBean rootBean = rootBeanList.get(i);

				Boolean isModifyItem = rootBean.getIsModifyItem();
				Boolean isAddTree = rootBean.getIsAddTree();
				String parentRevUid = rootBean.getParentRevUid();

				String item = rootBean.getItem();

				// 处理新增
				if (isAddTree) {
					if (parentRevUid != null && !"".equals(parentRevUid)) {
						topitemRev = (TCComponentItemRevision) session.getComponentManager().getTCComponent(parentRevUid);
						if (topitemRev != null) {
							topitemRev = getLastRevision(topitemRev);

							String type = topitemRev.getType();
							if (item.startsWith("8") && "D9_FinishedPartRevision".equals(type)) {

								if (revisionApp != null) {
									if (topitemRev != null) {
										TCComponent[] relatedComponents = revisionApp.getRelatedComponents("D9_BOMReq_L10B_REL"); // 半成品
										String item_id = topitemRev.getProperty("item_id");
										for (int j = 0; j < relatedComponents.length; j++) {
											if (relatedComponents[j] instanceof TCComponentItemRevision) {
												TCComponentItemRevision cuTcComponentItemRevision = (TCComponentItemRevision) relatedComponents[j];
												String item_id_1 = cuTcComponentItemRevision.getProperty("item_id");
												if (item_id.equals(item_id_1)) {
													revisionApp.remove("D9_BOMReq_L10B_REL", cuTcComponentItemRevision);
													revisionApp.add("D9_BOMReq_L10B_REL", topitemRev);
													break;
												}
											}
										}
									}
								}
							} else if (item.startsWith("7140") && "D9_VirtualPartRevision".equals(type)) {

								if (revisionApp != null) {
									if (topitemRev != null) {
										TCComponent[] relatedComponents = revisionApp.getRelatedComponents("D9_BOMReq_L10C_REL"); // 半成品
										String item_id = topitemRev.getProperty("item_id");
										for (int j = 0; j < relatedComponents.length; j++) {
											if (relatedComponents[j] instanceof TCComponentItemRevision) {
												TCComponentItemRevision cuTcComponentItemRevision = (TCComponentItemRevision) relatedComponents[j];
												String item_id_1 = cuTcComponentItemRevision.getProperty("item_id");
												if (item_id.equals(item_id_1)) {
													revisionApp.remove("D9_BOMReq_L10C_REL", cuTcComponentItemRevision);
													revisionApp.add("D9_BOMReq_L10C_REL", topitemRev);
													break;
												}
											}
										}
									}
								}
							}
						}
					}

					bomwindow = TCUtil.createBOMWindow(session);

					TCComponentBOMLine topebom = bomwindow.setWindowTopLine(topitemRev.getItem(), topitemRev, null, null);
					unPack(topebom);

					addBOMChile(topebom, rootBean);
					bomwindow.save();

				} else if (isModifyItem) {
					// 替代料属性修改
					Boolean isSecondSource = rootBean.getIsSecondSource();
					if (isSecondSource) {
						String itemRevUid = rootBean.getItemRevUid();
						if (itemRevUid != null && !"".equals(itemRevUid)) {
							TCComponentItemRevision itemRev = (TCComponentItemRevision) session.getComponentManager().getTCComponent(itemRevUid);

							itemRev = getLastRevision(itemRev);

							if (itemRev != null) {
								setProItemValue(rootBean, itemRev);
							}
						}
					} else {
						if (parentRevUid != null && !"".equals(parentRevUid)) {
							topitemRev = (TCComponentItemRevision) session.getComponentManager().getTCComponent(parentRevUid);

							if (topitemRev != null) {
								bomwindow = TCUtil.createBOMWindow(session);
								TCComponentBOMLine topebom = bomwindow.setWindowTopLine(topitemRev.getItem(), topitemRev, null, null);

								unPack(topebom);

								AIFComponentContext[] childrens = topebom.getChildren();
								if (childrens != null && childrens.length > 0) {
									for (int j = 0; j < childrens.length; j++) {
										TCComponentBOMLine children = (TCComponentBOMLine) childrens[j].getComponent();
										String bl_item_item_id = children.getProperty("bl_item_item_id");
										if (item.equals(bl_item_item_id)) {
											TCComponentItemRevision itemRev = children.getItemRevision();
											itemRev = getLastRevision(itemRev);
											
											setProItemValue(rootBean, itemRev);
											setProBomValue(rootBean, children);
											bomwindow.save();
											break;
										}
									}
								}
							}
						}
					}
				}
			}

			return AjaxResult.success("保存BOM成功！");
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
		} finally {
			if (bomwindow != null)
				bomwindow.close();
		}
	}

	public TCComponentItemRevision getLastRevision(TCComponentItemRevision rev) throws TCException {
		boolean released = TCUtil.isReleased(rev);
		if (released) {
			TCComponentItemRevision latestItemRevision = rev.getItem().getLatestItemRevision();
			boolean latestRevReleased = TCUtil.isReleased(latestItemRevision);

			TCComponentItemRevision saveAsRev = null;
			if (latestRevReleased) {
				saveAsRev = latestItemRevision.saveAs("");
			} else {
				saveAsRev = latestItemRevision;
			}
			rev = saveAsRev;
		}
		return rev;
	}

	/**
	 * 设置零件属性
	 * @param itemBean
	 * @param itemRev
	 * @throws Exception
	 */
	private void setProItemValue(L10EBOMBean itemBean, TCComponentItemRevision itemRev) throws Exception {
		boolean isWrite = TCUtil.checkOwninguserisWrite(session, itemRev);
		if (isWrite) {
			HashMap<String, String> itemMap = new HashMap<String, String>();
			for (String fieldName : UPDATE_ITEM_ATTRI) {
				Field field = itemBean.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				TCPropName tcProp = field.getAnnotation(TCPropName.class);
				if (tcProp != null) {
					String tcPropName = tcProp.value();
					Object value = field.get(itemBean) == null ? "" : field.get(itemBean);
					itemMap.put(tcPropName, value + "");
				}
			}
			if (itemMap != null && itemMap.size() > 0) {
				itemRev.setProperties(itemMap);
			}
		}
	}

	/**
	 * 设置结构属性
	 * @param itemBean
	 * @param tcbom
	 * @throws Exception
	 */
	private void setProBomValue(L10EBOMBean itemBean, TCComponentBOMLine tcbom) throws Exception {
		HashMap<String, String> bomMap = new HashMap<String, String>();
		for (String fieldName : UPDATE_BOM_ATTRI) {
			Field field = itemBean.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			TCPropName tcProp = field.getAnnotation(TCPropName.class);
			if (tcProp != null) {
				String tcPropName = tcProp.value();
				Object value = field.get(itemBean) == null ? "" : field.get(itemBean);
				bomMap.put(tcPropName, value + "");
			}
		}
		if (bomMap != null && bomMap.size() > 0) {
			tcbom.setProperties(bomMap);
		}
	}

	/**
	 * 添加BOM，添加一行
	 * @param data
	 * @return
	 * @throws TCException
	 */
	public AjaxResult addBOM(String data) throws TCException {
		Gson gson = new Gson();
		L10EBOMBean rootBean = gson.fromJson(data, L10EBOMBean.class);
		String itemRevUid = rootBean.getItemRevUid();
		String parentRevUid = rootBean.getParentRevUid();
		String quantity = rootBean.getQty();
		if (quantity == null || "".equals(quantity)) {
			quantity = "1";
		}

		TCComponentItemRevision itemRev = (TCComponentItemRevision) session.getComponentManager().getTCComponent(itemRevUid);
		TCComponentItemRevision parentRev = (TCComponentItemRevision) session.getComponentManager().getTCComponent(parentRevUid);

		if (itemRev != null && parentRev != null) {
			TCComponentBOMWindow bomwindow = null;
			try {
				bomwindow = TCUtil.createBOMWindow(session);
				TCComponentBOMLine topebom = bomwindow.setWindowTopLine(parentRev.getItem(), parentRev, null, null);
				TCComponentBOMLine bomLine = topebom.add(itemRev.getItem(), itemRev, null, false);

				bomLine.setProperty("bl_quantity", "" + quantity);
				bomwindow.save();

				L10EBOMBean ebom = new L10EBOMBean(bomLine);
				ebom.setParentRevUid(parentRevUid);
				ebom.setParentItem(parentRev.getProperty("item_id"));

				AjaxResult ajaxResult = AjaxResult.success(ebom);
				return ajaxResult;
			} catch (Exception e) {
				e.printStackTrace();
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getLocalizedMessage());
			} finally {
				bomwindow.close();
			}
		}
		return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, reg.getString("SelectWarn1.MSG"));

	}

	public void setMaxFindNumMap(L10EBOMBean rootBean) throws TCException {
		new Thread(() -> {
			try {
				ExecutorService es = Executors.newFixedThreadPool(4);
				setMaxFindNumMap(rootBean, es);
				es.shutdown();
			} catch (TCException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void setMaxFindNumMap(L10EBOMBean rootBean, ExecutorService es) throws TCException {
		String itemUid = rootBean.getItemRevUid();
		if (!MAX_FINDNUMMAP.containsKey(itemUid)) {
			TCComponentItemRevision itemRev = (TCComponentItemRevision) session.getComponentManager().getTCComponent(itemUid);
			if (TCUtil.isReleased(itemRev)) {
				MAX_FINDNUMMAP.put(itemUid, es.submit(() -> getMaxFindNum(itemRev)));
			} else {
				TCComponentItemRevision baseOnRev = getPreviousRev(itemRev);
				if (baseOnRev != null) {
					MAX_FINDNUMMAP.put(itemUid, es.submit(() -> getMaxFindNum(baseOnRev)));
				} else {
					MAX_FINDNUMMAP.put(itemUid, new int[] { 0, 0 });
				}
			}
		}
		for (L10EBOMBean cBomBean : rootBean.getChilds()) {
			if (cBomBean.getChilds().size() > 0) {
				setMaxFindNumMap(cBomBean, es);
			}
		}
	}

	public TCComponentItemRevision getPreviousRev(TCComponentItemRevision itemRev) throws TCException {
		TCProperty p = itemRev.getTCProperty("revision_list");
		TCComponent[] tccom = p.getReferenceValueArray();
		if (tccom != null && tccom.length > 1) {
			int index = ArrayUtils.indexOf(tccom, itemRev);
			if (index > 0) {
				return (TCComponentItemRevision) tccom[index - 1];
			}
		}
		return null;
	}

	public int[] getMaxFindNum(String itemRevUid) throws Exception {
		int max[] = new int[2];
		Object result = MAX_FINDNUMMAP.get(itemRevUid);
		if (result != null) {
			if (result instanceof int[]) {
				max = (int[]) result;
			} else {
				max = ((Future<int[]>) result).get();
			}
		} else {
			TCComponentItemRevision itemRev = (TCComponentItemRevision) session.getComponentManager().getTCComponent(itemRevUid);
			if (TCUtil.isReleased(itemRev)) {
				max = getMaxFindNum(itemRev);
			} else {
				TCComponentItemRevision baseOnRev = getPreviousRev(itemRev);
				if (baseOnRev != null) {
					max = getMaxFindNum(baseOnRev);
				}
			}
			MAX_FINDNUMMAP.put(itemRevUid, max);
		}
		return max;
	}

	public int[] getMaxFindNum(TCComponentItemRevision baseOnRev) {
		TCComponentBOMWindow bomWindow = null;
		int[] result = new int[2];
		result[0] = 0;
		result[1] = 0;
		try {
			bomWindow = TCUtil.createBOMWindow(session);
			bomWindow.setWindowTopLine(null, baseOnRev, null, null);
			TCComponentBOMLine topBomLine = bomWindow.getTopBOMLine();
			AIFComponentContext[] componmentContext = topBomLine.getChildren();
			if (componmentContext != null && componmentContext.length > 0) {
				AIFComponentContext lastAIFCom = componmentContext[componmentContext.length - 1];
				TCComponentBOMLine bomLine = (TCComponentBOMLine) lastAIFCom.getComponent();
				String findNum = bomLine.getProperty("bl_sequence_no");
				result[0] = Integer.parseInt(findNum);
				Integer maxAlt = Stream.of(componmentContext).map(e -> {
					TCComponentBOMLine bom = (TCComponentBOMLine) e.getComponent();
					try {
						String altGroup = bom.getProperty("bl_occ_d9_AltGroup");
						if (!TCUtil.isNull(altGroup)) {
							return Integer.parseInt(altGroup);
						}
					} catch (TCException e1) {
						e1.printStackTrace();
					}
					return 0;
				}).max(Integer::compare).get();
				if (maxAlt != null) {
					result[1] = maxAlt.intValue();
				}
				return result;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (bomWindow != null) {
				try {
					bomWindow.close();
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public L10EBOMBean getTopBOMLine(TCComponentItemRevision revision, boolean all, String group) throws TCException {
		TCComponentBOMWindow bomwindow = null;
		L10EBOMBean topebom = null;

		try {
			bomwindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine topBomline = bomwindow.setWindowTopLine(revision.getItem(), revision, null, null);

			topebom = new L10EBOMBean(topBomline);

			if (all) {
				topebom = getALLBOMLine(topebom, topebom.getItem());
			} else {
				topebom = getALLBOMLine(topebom, topebom.getItem(), group);
			}

		} catch (TCException e) {
			e.printStackTrace();
		} finally {
			bomwindow.close();
		}

		return topebom;
	}

	/**
	 * 解包
	 * @param bomLine
	 * @throws TCException
	 */
	public void unPack(TCComponentBOMLine bomLine) throws TCException {

		bomLine.refresh();
		AIFComponentContext[] childrens_Packed = bomLine.getChildren();
		for (AIFComponentContext aifchildren : childrens_Packed) {
			TCComponentBOMLine children = (TCComponentBOMLine) aifchildren.getComponent();
			if (children.isPacked()) {
				children.unpack();
				children.refresh();
			}
		}
		bomLine.refresh();
	}

	// 展开替代料
	public L10EBOMBean getSubstitutesBOMLine(TCComponentBOMLine subBomline, L10EBOMBean panBean, String key) throws TCException {
		unPack(subBomline);

		AIFComponentContext[] subChildrens = subBomline.getChildren();
		if (subChildrens != null && subChildrens.length > 0) {
			for (int i = 0; i < subChildrens.length; i++) {
				TCComponentBOMLine children = (TCComponentBOMLine) subChildrens[i].getComponent();

				String bl_item_item_id = children.getProperty("bl_item_item_id");
				System.out.println("bl_item_item_id = " + bl_item_item_id);

				L10EBOMBean subebom = new L10EBOMBean(children);
				subebom.setParentRevUid(panBean.getItemRevUid());
				subebom.setParentItem(panBean.getItem());
				subebom.setBomId(key);
				subebom.setIsSecondSource(false);

				if (children.isSubstitute()) {

					TCComponentBOMLine[] listSubstitutes = children.listSubstitutes();
					for (TCComponentBOMLine subChildren : listSubstitutes) {
						L10EBOMBean subebomChildren = new L10EBOMBean(subChildren);
						subebomChildren.setParentRevUid(panBean.getItemRevUid());
						subebomChildren.setParentItem(panBean.getItem());
						subebomChildren.setBomId(key);
						subebomChildren.setIsSecondSource(true);

						subebom.addSecondSource(subebomChildren);
					}

				}

				panBean.addChild(subebom);
			}
		}

		return panBean;
	}

	// 展开全部 2023-9-26
	public L10EBOMBean getALLBOMLine_old(L10EBOMBean panBean, String key) throws TCException {
		TCComponentBOMLine bomLine = panBean.bomLine;
		unPack(bomLine);

		String d9_MaterialGroup = bomLine.getItemRevision().getProperty("d9_MaterialGroup");
		if (!"B8X80".equals(d9_MaterialGroup)) {
			if (bomLine.hasSubstitutes()) {
				TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();
				for (TCComponentBOMLine subBomline : listSubstitutes) {
					L10EBOMBean subebom = new L10EBOMBean(subBomline);
					subebom.setParentRevUid(panBean.getItemRevUid());
					subebom.setParentItem(panBean.getItem());
					subebom.setBomId(key);
					subebom.setIsSecondSource(true);
					panBean.addSecondSource(subebom);
				}
			}

			AIFComponentContext[] childrens = bomLine.getChildren();

			if (childrens != null && childrens.length > 0) {
				for (int i = 0; i < childrens.length; i++) {
					TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
					L10EBOMBean ebom = new L10EBOMBean(children);
					ebom.setParentRevUid(panBean.getItemRevUid());
					ebom.setParentItem(panBean.getItem());
					ebom.setBomId(key);
					panBean.addChild(ebom);
					getALLBOMLine(ebom, key + "$" + ebom.getItem());
				}
			}
		}

		return panBean;
	}

	// 展开全部
	public L10EBOMBean getALLBOMLine(L10EBOMBean panBean, String key) throws TCException {
		TCComponentBOMLine bomLine = panBean.bomLine;
		unPack(bomLine);

		String d9_MaterialGroup = bomLine.getItemRevision().getProperty("d9_MaterialGroup");
		if (!"B8X80".equals(d9_MaterialGroup)) {
			if (bomLine.hasSubstitutes()) {
				TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();
				for (TCComponentBOMLine subBomline : listSubstitutes) {
					L10EBOMBean subebom = new L10EBOMBean(subBomline);
					subebom.setParentRevUid(panBean.getItemRevUid());
					subebom.setParentItem(panBean.getItem());
					subebom.setBomId(key);
					subebom.setIsSecondSource(true);

					getALLBOMLine(subebom, key + "$" + subebom.getItem());
					panBean.addSecondSource(subebom);
				}
			}

			AIFComponentContext[] childrens = bomLine.getChildren();

			if (childrens != null && childrens.length > 0) {
				for (int i = 0; i < childrens.length; i++) {
					TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
					L10EBOMBean ebom = new L10EBOMBean(children);
					ebom.setParentRevUid(panBean.getItemRevUid());
					ebom.setParentItem(panBean.getItem());
					ebom.setBomId(key);
					panBean.addChild(ebom);
					getALLBOMLine(ebom, key + "$" + ebom.getItem());
				}
			}
		}

		return panBean;
	}

	// 展开自己的零件
	public L10EBOMBean getALLBOMLine(L10EBOMBean panBean, String key, String group) throws TCException {
		TCComponentBOMLine bomLine = panBean.bomLine;
		unPack(bomLine);
		String d9_MaterialGroup = bomLine.getItemRevision().getProperty("d9_MaterialGroup");
		if (!"B8X80".equals(d9_MaterialGroup)) {
			if (bomLine.hasSubstitutes()) {
				TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();
				for (TCComponentBOMLine subBomline : listSubstitutes) {
					L10EBOMBean subebom = new L10EBOMBean(subBomline);
					subebom.setParentRevUid(panBean.getItemRevUid());
					subebom.setParentItem(panBean.getItem());
					subebom.setBomId(key);
					subebom.setIsSecondSource(true);

					getALLBOMLine(subebom, key + "$" + subebom.getItem(), group);
					panBean.addSecondSource(subebom);
				}
			}

			AIFComponentContext[] childrens = bomLine.getChildren();
			if (childrens != null && childrens.length > 0) {
				for (int i = 0; i < childrens.length; i++) {
					TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
					L10EBOMBean ebom = new L10EBOMBean(children);
					String ownerDept = ebom.getOwnerDept();
					if (group.equals(ownerDept)) {
						ebom.setParentRevUid(panBean.getItemRevUid());
						ebom.setParentItem(panBean.getItem());
						ebom.setBomId(key);
						panBean.addChild(ebom);
						getALLBOMLine(ebom, key + "$" + ebom.getItem(), group);
					}
				}
			}
		}
		return panBean;
	}

	public AjaxResult updateProp(String uid, String remark) {
		TCComponent obj = TCUtil.loadObjectByUid(uid);
		if (!(obj instanceof TCComponentItemRevision)) {
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, reg.getString("selectErr3.MSG"));
		}
		TCComponentItemRevision itemRevision = (TCComponentItemRevision) obj;
		try {
			itemRevision.setProperty("d9_Remarks", remark);
			return AjaxResult.success(true);
		} catch (TCException e) {
			e.printStackTrace();
		}
		return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "Update Failed");
	}

	public AjaxResult createDcn(String uid) {
		TCComponent obj = TCUtil.loadObjectByUid(uid);
		if (!(obj instanceof TCComponentItemRevision)) {
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, reg.getString("selectErr3.MSG"));
		}
		TCComponentItemRevision itemRev = (TCComponentItemRevision) obj;
		String objType = itemRev.getTypeObject().getName();
		if (!"D9_L10EBOMReqRevision".equals(objType)) {
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, reg.getString("selectErr4.MSG"));
		}
		try {
			TCUtil.setBypass(session);
			// 查詢是否啟動FXN44_MNT L10 EBOM製作申請流程
			TCComponent[] components = itemRev.getRelatedComponents("process_stage_list");
			if (components == null || components.length == 0) {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "該製作申請單尚未啟動製作申請流程");
			}
			TCComponent processComponent = null;
			for (int i = 0; i < components.length; i++) {
				TCComponent component = components[i];
				String name = component.getProperty("object_name");
				if ("FXN44_MNT L10 EBOM製作申請流程".equals(name)) {
					processComponent = component;
					break;
				}
			}
			if (ObjectUtil.isNull(processComponent)) {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "該製作申請單尚未啟動製作申請流程");
			}
			// 判斷流程是否執行到6-BOMTeam會簽
			boolean flag = false;
			TCComponent[] tcComponents = processComponent.getRelatedComponents("fnd0StartedTasks");

			for (int i = 0; i < tcComponents.length; i++) {
				TCComponent component = tcComponents[i];
				String name = component.getProperty("object_name");
				if ("6-BOMTeam會簽".equals(name)) {
					flag = true;
				}
			}
			if (!flag) {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "當前流程未處於BOMTeam會簽階段，不能創建DCN");
			}
			// 判断制作申请单是否指派专案
			TCComponent[] projectList = itemRev.getRelatedComponents("project_list");
			if (ObjectUtil.isNull(projectList) || projectList.length == 0) {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "未指派項目，請先指派項目");
			}
			TCComponent project = projectList[0];
			TCComponentFolder dcnFolder = getDcnFolderByProject(project);
			String objectName = itemRev.getProperty("object_name");

			TCComponent[] relatedComponents = itemRev.getRelatedComponents("D9_BOMReq_L10B_REL");
			if (ObjectUtil.isNull(relatedComponents) || relatedComponents.length == 0) {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "未查詢到成品料號");
			}
			// 取实际工作者
			String actualUser = getActualUser(itemRev, "6-BOMTeam");
			boolean isFirstRev = isFirstRev(relatedComponents);
			String dcnName = objectName + (isFirstRev ? "初版發行" : "變更發行");
			TCComponentItem dcnItem = ProductLineBOMService.createItem(session, "", "", dcnName, "D9_MNT_DCN");
			dcnFolder.add(Constants.CONTENTS, dcnItem);
			// 指派专案
			ProductLineBOMService.assignedProject(session, dcnItem, new ModelObject[] { project });
			Set<TCComponent> allSet = new HashSet<TCComponent>();
			for (int i = 0; i < relatedComponents.length; i++) {
				Set<TCComponent> set = getAllBomObj((TCComponentItemRevision) relatedComponents[i]);
				allSet.addAll(set);
			}
			TCComponentItemRevision dcnItemRevision = dcnItem.getLatestItemRevision();
			dcnItemRevision.setProperty("object_desc", dcnName);
			for (TCComponent tcComponent : allSet) {
				TCComponentItemRevision itemRevision = (TCComponentItemRevision) tcComponent;
				if (!TCUtil.isReleased(itemRevision)) {
					dcnItemRevision.add("CMHasSolutionItem", itemRevision);
					TCComponentItemRevision previousRevision = com.foxconn.electronics.util.TCUtil.getPreviousRevision(itemRevision);
					if (ObjectUtil.isNotNull(previousRevision) && TCUtil.isReleased(previousRevision)) {
						dcnItemRevision.add("CMHasProblemItem", previousRevision);
					}
				}
			}
			dcnItemRevision.setProperty("d9_ActualUserID", actualUser);
			itemRev.add("D9_BOMReq_L10A_REL", dcnItemRevision);
			return AjaxResult.success(true);
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, e.getMessage());
		} finally {
			TCUtil.closeBypass(session);
		}
	}

	/**
	 * 根據流程信息查詢實際工作者
	 * @param itemRev
	 * @return
	 * @throws Exception
	 */
	private String getActualUser(TCComponentItemRevision itemRev, String flowName) throws Exception {
		itemRev.refresh();

		TCComponentForm mailForm = com.foxconn.tcutils.util.TCUtil.getMailForm(itemRev);
		if (mailForm != null) {
			TCComponent[] taskTable = mailForm.getRelatedComponents("d9_TaskTable");
			if (taskTable != null && taskTable.length > 0) {
				for (int i = 0; i < taskTable.length; i++) {
					String d9_ProcessNode = taskTable[i].getProperty("d9_ProcessNode");
					String d9_TCUser = taskTable[i].getProperty("d9_TCUser");
					if (d9_ProcessNode.startsWith(flowName)) {
						TCComponent[] executeQuery = TCUtil.executeQuery(session, "零组件 ID", new String[] { "item_id" }, new String[] { d9_TCUser });

						if (executeQuery != null && executeQuery.length > 0) {
							for (int j = 0; j < executeQuery.length; j++) {
								String type = executeQuery[j].getType();
								if ("D9_L5_Part".equals(type)) {
									continue;
								}

								return executeQuery[j].getProperty("d9_UserInfo");
							}
						}
					}
				}
			}
		}

		throw new TCException("未查询到流程设置的实际工作者");
	}

	/**
	 * 查询当前版本是否有上一个版本及上一个版本是否有bom，如果没有则是第一次发行
	 * @param relatedComponents
	 * @return
	 * @throws TCException
	 */
	private boolean isFirstRev(TCComponent[] relatedComponents) throws TCException {
		if (ObjectUtil.isNull(relatedComponents) || relatedComponents.length == 0) {
			throw new TCException("对象数组不能为空");
		}
		for (int i = 0; i < relatedComponents.length; i++) {
			if (!(relatedComponents[i] instanceof TCComponentItemRevision)) {
				continue;
			}
			TCComponentItemRevision itemRev = (TCComponentItemRevision) relatedComponents[i];
			TCComponentItemRevision previousRevision = com.foxconn.electronics.util.TCUtil.getPreviousRevision(itemRev);
			if (ObjectUtil.isNotNull(previousRevision)) {
				TCComponent[] relateds = previousRevision.getRelatedComponents("view");
				if (ObjectUtil.isNotNull(relateds) && relateds.length > 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 查询项目文件夹的企业知识库及DCN文件夹
	 * @param project
	 * @return
	 * @throws TCException
	 */
	private TCComponentFolder getDcnFolderByProject(TCComponent project) throws TCException {
		try {
			String projectId = project.getProperty("project_id");
			// 查询专案文件夹
			TCComponent[] executeQuery = TCUtil.executeQuery(session, "__D9_Find_Project_Folder", new String[] { "d9_SPAS_ID" }, new String[] { projectId });
			if (ObjectUtil.isNull(executeQuery) || executeQuery.length == 0) {
				throw new TCException("未查询到专案文件夹");
			}
			TCComponentFolder tcFolder = (TCComponentFolder) executeQuery[0];
			// 查询专案文件夹下的企业知识库
			TCComponentFolder parentFolder = getChildFolder(tcFolder, "D9_WorkAreaFolder", null);
			if (ObjectUtil.isNull(parentFolder)) {
				throw new TCException("未查询到企业知识库文件夹");
			}
			TCComponentFolder dcnFolder = getChildFolder(parentFolder, "D9_WorkAreaFolder", "DCN");
			if (ObjectUtil.isNull(dcnFolder)) {
				dcnFolder = com.foxconn.electronics.util.TCUtil.createFolder(session, "D9_WorkAreaFolder", "DCN");
				// 给文件夹指派专案
				dcnFolder.add("project_list", project);
				parentFolder.add(Constants.CONTENTS, dcnFolder);
			}
			return dcnFolder;
		} catch (Exception e) {
			e.printStackTrace();
			throw new TCException(e.getMessage());
		}
	}

	/**
	 * 查询文件夹下指定文件类型和名称的文件夹，如果文件名不传查询满足条件的第一个文件夹
	 * @param folder
	 * @param type
	 * @return
	 * @throws TCException
	 */
	private TCComponentFolder getChildFolder(TCComponentFolder folder, String type, String name) throws TCException {
		AIFComponentContext[] childrenFolders = folder.getRelated(Constants.CONTENTS);
		if (ObjectUtil.isNull(childrenFolders) || childrenFolders.length == 0) {
			return null;
		}
		for (int i = 0; i < childrenFolders.length; i++) {
			TCComponent component = (TCComponent) childrenFolders[i].getComponent();
			String folderType = component.getType();
			if (!folderType.equals(type)) {
				continue;
			}
			if (StrUtil.isBlank(name)) {
				return (TCComponentFolder) component;
			}
			String folderName = component.getProperty(Constants.OBJECT_NAME);
			if (folderName.equals(name)) {
				return (TCComponentFolder) component;
			}
		}
		return null;
	}

	private Set<TCComponent> getAllBomObj(TCComponentItemRevision revision) throws TCException {
		TCComponentBOMWindow bomwindow = null;
		Set<TCComponent> set = new HashSet<TCComponent>();
		try {
			bomwindow = TCUtil.createBOMWindow(session);
			TCComponentBOMLine topBomline = bomwindow.setWindowTopLine(revision.getItem(), revision, null, null);
			getALLBOMLine(topBomline, set);

		} catch (TCException e) {
			e.printStackTrace();
		} finally {
			bomwindow.close();
		}
		return set;
	}

	public void getALLBOMLine(TCComponentBOMLine topBomline, Set<TCComponent> set) throws TCException {
		topBomline.refresh();
		TCComponentItemRevision itemRevision = topBomline.getItemRevision();
		set.add(itemRevision);
		String materialGroup = itemRevision.getProperty("d9_MaterialGroup");
		if ("B8X80".equals(materialGroup)) {
			return;
		}
		if (topBomline.hasSubstitutes()) {
			TCComponentBOMLine[] listSubstitutes = topBomline.listSubstitutes();
			for (TCComponentBOMLine subBomline : listSubstitutes) {
				set.add(subBomline.getItemRevision());
			}
		}
		AIFComponentContext[] childrens = topBomline.getChildren();
		if (childrens != null && childrens.length > 0) {
			for (int i = 0; i < childrens.length; i++) {
				TCComponentBOMLine children = (TCComponentBOMLine) childrens[i].getComponent();
				set.add(children.getItemRevision());
				getALLBOMLine(children, set);
			}
		}
	}

	public static void loadAllProperties(TCComponentBOMLine bomLine, TCComponentItemRevision itemRev, String[] partAtrrs, String[] bomAttrs) throws TCException, InterruptedException {
		com.teamcenter.services.strong.core.DataManagementService dmService = com.teamcenter.services.strong.core.DataManagementService.getService(session.getSoaConnection());
		if (bomLine != null && bomAttrs != null && bomAttrs.length > 0) {
			dmService.getProperties(new TCComponentBOMLine[] { bomLine }, bomAttrs);
		}

		if (itemRev != null && partAtrrs != null && partAtrrs.length > 0) {
			dmService.getProperties(new TCComponentItemRevision[] { itemRev }, partAtrrs);
		}
	}

	public boolean IsBOMViewWFTask(TCComponentItemRevision revision, String group) throws TCException {
		TCComponent[] wfRelation = revision.getRelatedComponents("fnd0StartedWorkflowTasks");
		if (wfRelation == null || wfRelation.length == 0) {
			return false;
		} else {
			// TCComponent getwfRelation = getwfRelation(wfRelation);
			String current_name = wfRelation[0].getProperty("current_name");
			if (current_name.equals("FXN44_MNT L10 EBOM製作申請流程")) {
				for (int i = 1; i < wfRelation.length; i++) {
//            		 0-PM	PM
//            		 1-PM主管	PM
//            		 2-BOMTeam	BOM
//            		 3-Panel CE	Panel CE
//            		 4-PM	PM
//            		 5-BOM製作(PI)	PSU
//            		 5-BOM製作(EE)	EE
//            		 5-BOM製作(ME)	ME
//            		 5-BOM製作(PA)	PA
//            		 5-BOM製作(CE)	CE
//            		 6-BOMTeam會簽	BOM
					String property = wfRelation[i].getProperty("current_name");

					System.out.println("i = " + i);
					System.out.println("current_name = " + property);
					System.out.println("group = " + group);

					if ("0-PM".equals(property) || "1-PM主管".equals(property) || "4-PM".equals(property)) {
						if ("PM".equals(group)) {
							return true;
						}
					} else if ("2-BOMTeam".equals(property) || "6-BOMTeam會簽".equals(property)) {
						if ("BOM".equals(group)) {
							return true;
						}
					} else if ("3-Panel CE".equals(property)) {
						if ("Panel CE".equals(group)) {
							return true;
						}
					} else if (property.contains("5-BOM製作")) {
						if ("PSU".equals(group) || "EE".equals(group) || "ME".equals(group) || "PA".equals(group) || "CE".equals(group)) {
							return true;
						}
					}

				}

			} else {
				return false;
			}

		}

		return false;
	}

}
