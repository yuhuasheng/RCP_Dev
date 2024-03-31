package com.foxconn.mechanism.hhpnmaterialapply.export.services;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.foxconn.mechanism.hhpnmaterialapply.constants.RelationConstant;
import com.foxconn.mechanism.hhpnmaterialapply.export.services.impl.DTExportServices;
import com.foxconn.mechanism.hhpnmaterialapply.export.services.impl.MNTExportServices;
import com.foxconn.mechanism.hhpnmaterialapply.export.services.impl.PRTExportServices;
import com.foxconn.mechanism.hhpnmaterialapply.export.util.TCPropertes;
import com.foxconn.mechanism.util.CommonTools;
import com.foxconn.mechanism.util.TCUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.soa.exceptions.NotLoadedException;

import cn.hutool.http.HttpUtil;

/**
 * 
 * @author Robert
 *
 */
public interface IExportServices {
	static final String D9_PREFIX = "d9_";
	static final String POC_STR = "IR_";

	public void setBOMLines(TCComponentBOMLine topBOMLine, List<TCComponentBOMLine> boms) throws Exception;

	public void dataHandle() throws Exception;

	public void generatePartsExcel(File targeFile) throws Exception;

	/**
	 * 取设计对象上面的图档
	 * 
	 * Robert 2022-4-11
	 * 
	 * @param ItemRevision
	 * @return
	 * @throws Exception
	 */
	public static File getModelJPEG(TCComponentItemRevision ItemRevision) throws Exception {
		TCComponent[] relatedComponents = null;
		relatedComponents = ItemRevision.getRelatedComponents("IMAN_reference"); // 获取引用伪文件夹下面的附件
		if (!checkJPG(relatedComponents)) { // 引用伪文件夹下没有数据集，或者数据集不为图片对象
			relatedComponents = ItemRevision.getRelatedComponents("IMAN_specification"); // 引用伪文件夹下面没有图片，则再获取规范关系下的缩略图
		}
		
		for (TCComponent tcComponent : relatedComponents) {
			if (tcComponent instanceof TCComponentDataset) {
				TCComponentDataset dataset = (TCComponentDataset) tcComponent;
				String objectName = dataset.getProperty("object_name");
				String objectType = dataset.getTypeObject().getName();
				TCComponentTcFile[] files = null;
				if (objectName.toLowerCase().endsWith(".png") || objectName.toLowerCase().endsWith(".jpg")
						|| objectName.toLowerCase().endsWith(".jpeg")) {
					files = dataset.getTcFiles();
					if (CommonTools.isNotEmpty(files)) {
						return files[0].getFmsFile();
					}
				} else if ("ProPrt".equalsIgnoreCase(objectType) || "ProAsm".equalsIgnoreCase(objectType)) {
					dataset.refresh();
					files = dataset.getTcFiles();
					if (files != null) {
						for (int i = 0; i < files.length; i++) {
							TCComponentTcFile tcFile = files[i];
							if ("image/jpeg".equalsIgnoreCase(tcFile.getProperty("mime_type"))) {
								return tcFile.getFmsFile();
							}
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 判断是否含有图片类型的数据集
	 * @param relatedComponents
	 * @return
	 * @throws TCException
	 */
	public static boolean checkJPG(TCComponent[] relatedComponents) throws TCException {
		boolean flag = false;
		if (CommonTools.isEmpty(relatedComponents)) {
			return flag;
		}
		for (TCComponent tcComponent : relatedComponents) {
			if (tcComponent instanceof TCComponentDataset) {
				TCComponentDataset dataset = (TCComponentDataset) tcComponent;
				String objectName = dataset.getProperty("object_name");
				if (objectName.toLowerCase().endsWith(".png") || objectName.toLowerCase().endsWith(".jpg")
						|| objectName.toLowerCase().endsWith(".jpeg")) { // 判断数据集名称是否含有图片后缀
					flag = true;
					break;
				}
			}
		}
		return flag;
	}
	
	
	/**
	 * 
	 * 取设计对象上面的表示关联的对�?
	 * 
	 * Robert 2022-4-11
	 * 
	 * @param itemRevision
	 * @return
	 * @throws TCException
	 */
	public static List<TCComponentItemRevision> getItemRevByRepresentation(TCComponentItemRevision itemRevision)
			throws TCException {
		itemRevision.refresh();
		TCComponent[] relatedComponents = itemRevision.getRelatedComponents(RelationConstant.REPRESENTATION_FOR);
		if (relatedComponents != null) {
			return Stream.of(relatedComponents).filter(e -> e instanceof TCComponentItemRevision)
					.map(e -> (TCComponentItemRevision) e).collect(Collectors.toList());
		}
		return null;
	}

	/**
	 * 
	 * Robert 2022-4-11
	 * 
	 * @param topBOMLine
	 * @return
	 * @throws NotLoadedException
	 * @throws TCException
	 */
	public static String getProjectName(TCComponentBOMLine topBOMLine) throws NotLoadedException, TCException {
		TCComponentItem topItem = topBOMLine.getItem();
		TCProperty props = topItem.getTCProperty("project_list");
		if (props != null) {
			TCComponent[] pjs = props.getReferenceValueArray();
			return Stream.of(pjs).map(e -> ((TCComponentProject) e).getProjectName()).collect(Collectors.joining(","));
		}
		return "";
	}

	/**
	 * 
	 * Robert 2022-4-11
	 * 
	 * @param topBOMLine
	 * @return
	 * @throws TCException
	 * @throws NotLoadedException 
	 */
	public static String getDefaultFileName(TCComponentBOMLine topBOMLine) throws TCException, NotLoadedException {
		String itemId = topBOMLine.getProperty("bl_item_item_id");
		return getProjectName(topBOMLine) + "_" + "partList" + "_" + formatNowDate("yyyyMMdd");
//		pppp + "_" + "partList" + "_" + rqi;
		// return itemId + " " + formatNowDate("yyyyMMdd");
//		return itemId + "_" + formatNowDate("yyyyMMdd");
	}

	public static String formatNowDate(String formatStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatStr);
		return formatter.format(LocalDateTime.now());
	}

	/**
	 * 
	 * Robert 2022-4-11
	 * 
	 * @param <T>
	 * @param bean
	 * @param tcObject
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws TCException
	 */
	public static <T> T tcPropMapping(T bean, TCComponent tcObject, String typeStr)
			throws IllegalArgumentException, IllegalAccessException, TCException {
		if (bean != null && tcObject != null) {
			Field[] fields = bean.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				TCPropertes tcPropName = fields[i].getAnnotation(TCPropertes.class);
				if (tcPropName != null) {
					String val = "";
					String propertyName = tcPropName.tcProperty();					
					String tctype = tcPropName.tcType();
					if (propertyName.length() == 0 || !tctype.equalsIgnoreCase(typeStr)) {
						continue;
					}
					int index = propertyName.indexOf(D9_PREFIX);
					if (index != -1) {
						String pocPropertyName = propertyName.substring(0, index + D9_PREFIX.length()) + POC_STR
								+ propertyName.substring(index + D9_PREFIX.length());
						if (tcObject.isValidPropertyName(pocPropertyName)) {
							propertyName = pocPropertyName;
						}
					}
					tcObject.refresh();
					
					if (tcObject.isValidPropertyName(propertyName)) {
						val = tcObject.getProperty(propertyName);
					} else {
						System.out.println("propertyName is not exist " + propertyName);
					}
					if (val != null && val.length() > 0) {
						fields[i].set(bean, val);
					} else {
						fields[i].set(bean, "");
					}
				}
			}
		}
		return bean;
	}

	/**
	 * 
	 * Robert 2022-4-11
	 * 
	 * @param bu
	 * @return
	 */
	public static IExportServices getInstance(String bu) {
		IExportServices instance = null;
		switch (bu.toUpperCase()) {
		case "DT":
			instance = new DTExportServices();
		case "PRT":
			instance = new PRTExportServices();
			break;
		case "MNT":
			instance = new MNTExportServices();
			break;
		default:
			break;
		}
		return instance;
	}

	public static void writeLog(TCComponentItemRevision itemRev, Date startDate, Date endDate, String function) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			String springUrl = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site,
					"D9_SpringCloud_URL");
			Map<String, Object> dataMap = new HashMap<>();
			TCComponentUser user = RACUIUtil.getTCSession().getUser();
			dataMap.put("project", itemRev.getProperty("project_ids"));
			dataMap.put("functionName", function);
			dataMap.put("creator", user.getUserId());
			dataMap.put("creatorName", user.getUserName());
			dataMap.put("itemId", itemRev.getProperty("item_id"));
			dataMap.put("rev", itemRev.getProperty("item_revision_id"));
			dataMap.put("startTime", sdf.format(startDate));
			dataMap.put("endTime", sdf.format(endDate));
			dataMap.put("revUid", itemRev.getUid());
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list.add(dataMap);
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			HttpUtil.post(springUrl + "/tc-integrate/actionlog/addlog", gson.toJson(list), 60 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
