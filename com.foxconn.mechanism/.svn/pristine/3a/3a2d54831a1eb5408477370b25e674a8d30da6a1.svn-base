package com.foxconn.mechanism.hhpnmaterialapply.export.services.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.foxconn.mechanism.hhpnmaterialapply.export.constants.DTDesignField;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTBOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTDataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTMISCDataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTPLDataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt.DTSMDataModel;
import com.foxconn.mechanism.util.CommonTools;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;

import cn.hutool.json.JSONUtil;

public class DTExportTools {

	public static List<DTBOMInfo> getBOMLineList(List<DTBOMInfo> list, TCComponentBOMLine topBomLine,
			boolean optionalPartFlag) throws TCException {
		if (list == null) {
			list = new ArrayList<DTBOMInfo>();
		}
		topBomLine.refresh();
		TCComponentItemRevision itemRevision = (TCComponentItemRevision) topBomLine
				.getRelatedComponent("bl_line_object");
		String itemId = itemRevision.getProperty("item_id");
		System.out.println("==>> 当前设计对象ID为: " + itemId);
		String version = itemRevision.getProperty("item_revision_id");
		System.out.println("==>> 当前设计对象ID为: " + version);
		DTBOMInfo dtbomInfo = new DTBOMInfo();
//		DTPLDataModel dtDataModel = new DTPLDataModel();
		dtbomInfo.setBomLine(topBomLine);
		checkPartType(itemRevision, dtbomInfo); // 核对类型		
		if (itemId.startsWith(DTDesignField.OPTIONALPARTITEMIDPREFIX) || optionalPartFlag) { // 判断图号前缀是否以ME-AS03-开头
			dtbomInfo.setOptionalType(DTDesignField.OPTIONALPART);
//			optionalType = DTDesignField.OPTIONALPART;			
			optionalPartFlag = true;
		} else {
			dtbomInfo.setOptionalType(DTDesignField.NOTOPTIONALPART);
		}
		list.add(dtbomInfo);
		AIFComponentContext[] childBomlines = topBomLine.getChildren();
		if (CommonTools.isNotEmpty(childBomlines)) {
			for (AIFComponentContext aifComponentContext : childBomlines) {
				TCComponentBOMLine childBomline = (TCComponentBOMLine) aifComponentContext.getComponent();
				getBOMLineList(list, childBomline, optionalPartFlag);
			}
		}
		return list;
	}

	/**
	 * 核对类型
	 * 
	 * @param itemRevision
	 * @param dtbomInfo
	 * @throws TCException
	 */
	private static void checkPartType(TCComponentItemRevision itemRevision, DTBOMInfo dtbomInfo) throws TCException {
		boolean flag = false;
		String d9PartType = itemRevision.getProperty("d9_PartType");
		String itemId = itemRevision.getProperty("item_id");
		if (DTDesignField.PL.equals(d9PartType)) {
			dtbomInfo.setPartType(DTDesignField.PL);
			flag = true;
		} else if (DTDesignField.SM.equals(d9PartType)) {
			dtbomInfo.setPartType(DTDesignField.SM);
			flag = true;
		} else if (DTDesignField.MISC.equals(d9PartType)) {
			dtbomInfo.setPartType(DTDesignField.MISC);
			flag = true;
		}
		if (!flag) { // 作为第一轮是否匹配成功的标识
			if (itemId.startsWith(DTDesignField.PLITEMIDPREFIX)) {
				dtbomInfo.setPartType(DTDesignField.PL);
			} else if (itemId.startsWith(DTDesignField.SMITEMIDPREFIX)) {
				dtbomInfo.setPartType(DTDesignField.SM);
			} else {
				Optional<String> findAny = Stream.of(DTDesignField.MISCITEMIDPREFIX).filter(str -> {					
					return itemId.startsWith(str);
				}).findAny();
				
				if (!findAny.isPresent()) { 
					dtbomInfo.setPartType(DTDesignField.MISC);
				}
//				if (!itemId.startsWith(DTDesignField.MISCITEMIDPREFIX)) {
//					dtbomInfo.setPartType(DTDesignField.MISC);
//				}				
			}
		}
	}

	/**
	 * 对数据先排序，然后进行分组By PartType And OptionalType
	 * 
	 * @param dtDataModelList
	 */
	public static Map<String, List<DTDataModel>> groupByPartTypeAndOptionalType(List<DTDataModel> dtDataModelList) {
		List<DTDataModel> collect = dtDataModelList.stream()
				.sorted(Comparator.comparing(DTDataModel::getOptionalType, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList()); // 进行升序排序
//		System.out.println(collect);

//		Map<String, List<DTDataModel>> map = dtDataModelList.stream()
//		           .sorted(Comparator.comparing(DTDataModel::getOptionalType,Comparator.nullsLast(String::compareTo)))
//		           .collect(Collectors.groupingBy( dtDataModel -> dtDataModel.getPartType() + '-' + dtDataModel.getOptionalType()));
		Map<String, List<DTDataModel>> resultMap = new LinkedHashMap<String, List<DTDataModel>>();
		collect.forEach(dtDataModel -> {
			List<DTDataModel> dtbomInfoList = resultMap.get(dtDataModel.getPartType() + "-" + dtDataModel.getOptionalType());
			if (dtbomInfoList == null) {
				dtbomInfoList = new ArrayList<DTDataModel>();
				dtbomInfoList.add(dtDataModel);
				resultMap.put(dtDataModel.getPartType() + "-" + dtDataModel.getOptionalType(), dtbomInfoList);
			} else {
				dtbomInfoList.add(dtDataModel);
			}
		});
//		System.out.println("排序后指定字段分组>:" + JSONUtil.toJsonPrettyStr(resultMap));
		return resultMap;
	}

	/**
	 * 对数据进行分组ByPartType
	 * 
	 * @param list
	 * @return
	 */
	public static Map<String, List<DTDataModel>> groupByPartType(List<DTDataModel> list) {
		Map<String, List<DTDataModel>> resultMap = new LinkedHashMap<String, List<DTDataModel>>();
		list.forEach(dtDataModel -> {
			List<DTDataModel> dtbomInfoList = resultMap.get(dtDataModel.getPartType());
			if (dtbomInfoList == null) {
				dtbomInfoList = new ArrayList<DTDataModel>();
				dtbomInfoList.add(dtDataModel);
				resultMap.put(dtDataModel.getPartType(), dtbomInfoList);
			} else {
				dtbomInfoList.add(dtDataModel);
			}
		});
		return resultMap;
	}

	/**
	 * 对数据进行分组ByPartType
	 * 
	 * @param list
	 * @return
	 */
	public static Map<String, List<DTDataModel>> groupByPartTypeAndOptionalTypeNew(List<DTDataModel> list) {
		Map<String, List<DTDataModel>> resultMap = new LinkedHashMap<String, List<DTDataModel>>();
		list.forEach(dtDataModel -> {
			List<DTDataModel> dtbomInfoList = resultMap
					.get(dtDataModel.getPartType() + "-" + dtDataModel.getOptionalType());
			if (dtbomInfoList == null) {
				dtbomInfoList = new ArrayList<DTDataModel>();
				dtbomInfoList.add(dtDataModel);
				resultMap.put(dtDataModel.getPartType() + "-" + dtDataModel.getOptionalType(), dtbomInfoList);
			} else {
				dtbomInfoList.add(dtDataModel);
			}
		});
		return resultMap;
	}
	
	
	/**
	 * 通过零组件ID对数据进行分组
	 * @param list
	 * @return
	 */
	public static Map<String, List<DTDataModel>> groupByItemId(List<DTDataModel> list) {
		Map<String, List<DTDataModel>> resultMap = new LinkedHashMap<String, List<DTDataModel>>();
		list.forEach(dtDataModel -> {			
			if (dtDataModel instanceof DTPLDataModel) {
				DTPLDataModel dtplDataModel = (DTPLDataModel) dtDataModel;	
				dtDataModel = dtplDataModel;
				List<DTDataModel> dtplbomInfoList = resultMap.get(dtplDataModel.getItemID());
				if (dtplbomInfoList == null) {
					dtplbomInfoList = new ArrayList<>();
					dtplbomInfoList.add(dtDataModel);	
					resultMap.put(dtplDataModel.getItemID(), dtplbomInfoList);
				} else {
					dtplbomInfoList.add(dtDataModel);
				}
			} else if (dtDataModel instanceof DTSMDataModel) {
				DTSMDataModel dtsmDataModel = (DTSMDataModel) dtDataModel;
				dtDataModel = dtsmDataModel;
				List<DTDataModel> dtsmbomInfoList = resultMap.get(dtsmDataModel.getItemID());
				if (dtsmbomInfoList == null) {
					dtsmbomInfoList = new ArrayList<>();
					dtsmbomInfoList.add(dtDataModel);	
					resultMap.put(dtsmDataModel.getItemID(), dtsmbomInfoList);
				} else {
					dtsmbomInfoList.add(dtDataModel);
				}				
			} else if (dtDataModel instanceof DTMISCDataModel) {
				DTMISCDataModel dtmiscDataModel = (DTMISCDataModel) dtDataModel;
				dtDataModel = dtmiscDataModel;
				List<DTDataModel> dtmiscbomInfoList = resultMap.get(dtmiscDataModel.getItemID());
				if (dtmiscbomInfoList == null) {
					dtmiscbomInfoList = new ArrayList<>();
					dtmiscbomInfoList.add(dtDataModel);	
					resultMap.put(dtmiscDataModel.getItemID(), dtmiscbomInfoList);
				} else {
					dtmiscbomInfoList.add(dtDataModel);
				}	
			}
		});
		return resultMap;
	}
}
