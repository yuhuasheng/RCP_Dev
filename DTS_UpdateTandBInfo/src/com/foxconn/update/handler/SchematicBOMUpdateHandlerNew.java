package com.foxconn.update.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.plm.tcapi.constants.BOMLinePropConstant;
import com.foxconn.plm.tcapi.constants.DatasetEnum;
import com.foxconn.plm.tcapi.constants.DatasetPropConstant;
import com.foxconn.plm.tcapi.constants.ItemRevPropConstant;
import com.foxconn.plm.tcapi.constants.TCPreferenceConstant;
import com.foxconn.plm.tcapi.constants.TCSearchEnum;
import com.foxconn.plm.tcapi.mail.TCMail;
import com.foxconn.plm.tcapi.service.BOMService;
import com.foxconn.plm.tcapi.service.DatasetService;
import com.foxconn.plm.tcapi.service.ITCSOAClientConfig;
import com.foxconn.plm.tcapi.service.ItemService;
import com.foxconn.plm.tcapi.service.TCSOAServiceFactory;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.foxconn.update.config.TCSOAClientConfigImpl;
import com.foxconn.update.constants.ConstantsEnum;
import com.foxconn.update.constants.TCMailFileConstant;
import com.foxconn.update.mapper.DispatcherServiceMapper;
import com.foxconn.update.util.ExcelPlacementDiffUtil;
import com.foxconn.update.util.ExcelPlacementFileTools;
import com.foxconn.update.util.MyBatisUtil;
import com.foxconn.update.util.TxtPlacementDiffUtil;
import com.foxconn.update.util.TxtPlacementFileTools;
import com.google.gson.Gson;
import com.teamcenter.services.loose.core.SessionService;
import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.query.SavedQueryService;
import com.teamcenter.services.strong.structuremanagement.StructureService;
import com.teamcenter.soa.client.FileManagementUtility;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.Dataset;
import com.teamcenter.soa.client.model.strong.Group;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.client.model.strong.User;
import com.teamcenter.soa.exceptions.NotLoadedException;

import cn.hutool.http.HttpUtil;

public class SchematicBOMUpdateHandlerNew {
	
	private static final Logger log = LoggerFactory.getLogger(SchematicBOMUpdateHandlerNew.class);
	
	private static Properties props = null;
	
	private static Properties tcProps = null;
	
	private static String PLACEMENTOWNER = ""; // Placement文件所有者
	
	private static String EEOWNER = ""; // D9_EE_SchemRevision的所有者
	
	private static String placementType = null;  // placement文件的类型	
	
	private static String to = null;
	
	private static ItemRevision layoutItemRev = null;
	
	private static String layoutItemId = null;
	
	private static String layoutVersion = null;
	
	static {
		System.out.println("configurating log4j with log4j.properties");
		System.out.println(CommonTools.getPath(SchematicBOMUpdateHandlerNew.class));
		
		props = CommonTools.getProperties(CommonTools.getPath(SchematicBOMUpdateHandlerNew.class) + File.separator + "ConstantParams.properties");
		tcProps = CommonTools.getProperties(props.getProperty("tc_properties"));
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		String startTime = CommonTools.getCurrentTimems();
		log.info("==>> startTime: " + startTime);
		log.info("========== SchematicBOMUpdateHandler start ==========");
//		args = new String[] {"-LayoutRevUid=gthVL2gHppJG1D", "-EERevUid=SQmV7gHRppJG1D"};
		if (CommonTools.isEmpty(args)) {
			return;
		}
		
		Map<String, String> params = getParams(args);
		String layoutRevUid = params.get("LayoutRevUid") == null ? "" : params.get("LayoutRevUid").trim();
		System.out.println("==>> layoutRevUid: " + layoutRevUid);
		String EERevUid = params.get("EERevUid") == null ? "" : params.get("EERevUid").trim();	
		System.out.println("==>> EERevUid: " + EERevUid);
		if (CommonTools.isEmpty(layoutRevUid) || CommonTools.isEmpty(EERevUid)) {
			throw new Exception("【ERROR】 获取输入参数LayoutRevUid或EERevUid发生错误！");
		}	
		
//		List<String> EERevUidList = new ArrayList<String>();
//		Gson gson = new Gson();
//		EERevUidList = gson.fromJson(EERevUid, EERevUidList.getClass());
		List<String> EERevUidList = Arrays.asList(EERevUid.split(";"));
		ItemRevision EEItemRev = null;
		String EEItemId = null; // EE对象版本ID
		String EEVersion = null; // EE对象版本号
		String bodymsg = null; // 邮件正文内容
		to = props.getProperty("to_userId"); // 邮件接受者
		List<String> attachmentList = new ArrayList<String>(); // 存放邮件附件信息的集合
		Map<ModelObject, String> ownMap = null;
		String requestPath = null;
		
		ITCSOAClientConfig tcSOAClientConfig = new TCSOAClientConfigImpl(tcProps.getProperty("TC_IP"), tcProps.getProperty("TC_USERNAME"), tcProps.getProperty("TC_PASSWORD")); // 登录TC系统
		TCSOAServiceFactory tcsoaServiceFactory = new TCSOAServiceFactory(tcSOAClientConfig);
		
		DataManagementService dmService = tcsoaServiceFactory.getDataManagementService();	
		SavedQueryService savedQueryService = tcsoaServiceFactory.getSavedQueryService();
		StructureManagementService smService = tcsoaServiceFactory.getStructureManagementService(); 
		StructureService strucService = tcsoaServiceFactory.getStructureService();
		SessionService sessionService= tcsoaServiceFactory.getSessionService();
		
		TCPublicUtils.byPass(sessionService, true); // 开启旁路
		
		layoutItemRev = (ItemRevision) TCPublicUtils.findObjectByUID(dmService, layoutRevUid);
		if (CommonTools.isEmpty(layoutItemRev)) {
			throw new Exception("【ERROR】 获取Layout对象版本失败");
		}
		
		layoutItemId = TCPublicUtils.getPropStr(dmService, layoutItemRev, ItemRevPropConstant.ITEM_ID);
		log.info("【INFO】 layoutItemId:" + layoutItemId);
		layoutVersion = TCPublicUtils.getPropStr(dmService, layoutItemRev, ItemRevPropConstant.ITEM_REVISION_ID);
		log.info("【INFO】layoutVersion:" + layoutVersion);
		
		List<String> fmsUrlTCPreferences = TCPublicUtils.getTCPreferences(tcsoaServiceFactory.getPreferenceManagementService(), TCPreferenceConstant.FMS_BOOTSTRAP_URLS);
		if (CommonTools.isEmpty(fmsUrlTCPreferences)) {
			throw new Exception("【ERROR】 TC首选项: " + TCPreferenceConstant.FMS_BOOTSTRAP_URLS + ", 不存在");
		}
		String fmsUrl = fmsUrlTCPreferences.get(0);
		log.info("【INFO】 fmsUrl: " + fmsUrl);
		
//		fmsUrl = props.getProperty("fms_url");
//		log.info("【INFO】 fmsUrl: " + fmsUrl);
		FileManagementUtility fmUtility = tcsoaServiceFactory.getFileManagementUtility(fmsUrl);
		
		List<String> list = TCPublicUtils.getTCPreferences(tcsoaServiceFactory.getPreferenceManagementService(), TCPreferenceConstant.D9_SPRINGCLOUD_URL);
		if (CommonTools.isEmpty(list)) {
			throw new Exception("【ERROR】 TC首选项: " + TCPreferenceConstant.D9_SPRINGCLOUD_URL + ", 不存在");
		}
		requestPath = list.get(0);
		
		try {			
			String dir = CommonTools.getFilePath(ConstantsEnum.EDAPLACEMENTFOLDER.value()); // 查找保存Placement文件的文件夹 
			log.info("【INFO】 filePath: " + dir);
			CommonTools.deletefile(dir); // 删除文件夹下面的所有文件
			
			String placementFilePath = getPlacement(dmService, fmUtility, dir);
			if (CommonTools.isEmpty(placementFilePath)) {
				throw new Exception("【ERROR】 零组件ID为: " + layoutItemId + ", 版本号为: "+ layoutVersion + ", 下载placement文件失败");
			}
			
			for (String uid : EERevUidList) {
				try {
					EEItemRev = (ItemRevision) TCPublicUtils.findObjectByUID(dmService, uid);
					if (CommonTools.isEmpty(EEItemRev)) {
						throw new Exception("【ERROR】 获取EE对象版本失败...");
					}
					
					EEOWNER = TCPublicUtils.getOwnUser(dmService, EEItemRev); // 获取EE对象版本所有者
					EEItemId = TCPublicUtils.getPropStr(dmService, EEItemRev, ItemRevPropConstant.ITEM_ID);
					log.info("【INFO】 EEItemId:" + EEItemId);
					EEVersion = TCPublicUtils.getPropStr(dmService, EEItemRev, ItemRevPropConstant.ITEM_REVISION_ID);
					log.info("【INFO】EEVersion:" + EEVersion);
					
					// 获取EE和Layout负责人邮箱
					Map<String, String> emailMap = TCPublicUtils.getEmail(savedQueryService, dmService, Arrays.asList(EEOWNER, PLACEMENTOWNER));
					if (CommonTools.isEmpty(emailMap)) {
						log.error("【ERROR】 获取EE和layout负责人邮箱失败...");
					}
					
					String email = TCMail.generateToUser(emailMap);
					to = email == null ? "" + to : email + to;				
					List<ModelObject> itemRevList = new ArrayList<ModelObject>();
					List<String> propertyList = new ArrayList<String>();
					
					// 获取BOMLine集合清单，需要解包的BOMLine将其解包
					Boolean check = BOMService.getBOMWindowInfo(smService, strucService, dmService, EEItemRev, 3, itemRevList, propertyList);
					if (!check || itemRevList.size() == 1) { // 获取BOMLine失败, 不予处理; 假如没有BOM结构，也不予处理
						throw new Exception("【ERROR】 EE零组件ID为: " + EEItemId + ", 版本号为: "+ EEVersion + ", 获取BOMLine集合信息失败！");
					}				
					ownMap = ItemService.getModelObjectOwner(dmService, itemRevList); // 记录对象版本和所有者
					if (CommonTools.isEmpty(ownMap)) {
						throw new Exception("【ERROR】 EE零组件ID为: " + EEItemId + ", 版本号为: "+ EEVersion + ", 获取对象版本所有者失败...");
					}
					
					log.info("******** 执行更改BOMLine属性之前所有者明细  start ********.");
					printCurObjOwner(dmService, ownMap);
					log.info("******** 执行更改BOMLine属性之前所有者明细  end ********.");
					
					Map<String, Object> map = generateDiffFile(fmUtility, dmService, layoutItemRev, propertyList, dir, placementFilePath);
					if (map.get("diffFilePath") != null) {
						String diffFilePath = map.get("diffFilePath").toString();
						attachmentList.add(diffFilePath);
						generateDifferDataset(dmService, fmUtility, EEItemRev, EEItemId, EEVersion, diffFilePath); // 生成差异性数据集
					}
					
					if (map.get("placementMap") != null) {
						Map<String, List<String>> placementMap = (Map<String, List<String>>) map.get("placementMap");
						List<String> errList = BOMService.modifyBOMLineInfo(smService, strucService, dmService, EEItemRev, placementMap, BOMLinePropConstant.BL_REF_DESIGNATOR + "=", 3);
						if (CommonTools.isNotEmpty(errList)) {						
							String errorAbsoluteFilePath = TCMail.generateMailFile(errList, dir, EEItemId + "_" + EEVersion + "_" + TCMailFileConstant.ERRORRECORDFILENAME); // 生成修改失败错误文件
							attachmentList.add(errorAbsoluteFilePath);
						}
					}
					
					bodymsg = "<html><head></head><body><h3 style=\"font-family: 宋体;  font-size:15px;\">" + "Layout零組件ID為: "+ layoutItemId + ", Layout版本號為: " + layoutVersion +", EE零組件ID為: "
							+ EEItemId + ", 版本號為: " + EEVersion + ", 原理圖元件正反面信息餘Layout正反面信息同步完成，請登錄TC進行查看，謝謝！！"
							+ "</h3></body></html>";
				} catch (Exception e) {
					e.printStackTrace();
					log.error(CommonTools.getExceptionMsg(e));
					bodymsg = "<html><head></head><body><h3 style=\"font-family: 宋体;  font-size:15px;\">" + "Layout零組件ID為: "+ layoutItemId + ", Layout版本號為: " + layoutVersion +", EE零組件ID為: "
							+ EEItemId + ", 版本號為: " + EEVersion + ", 原理圖元件正反面信息餘Layout正反面信息同步存在錯誤，請登錄TC進行查看，謝謝！！"
							+ "</h3></body></html>";
				} finally {
					try {
						if (CommonTools.isNotEmpty(ownMap)) {
							ownMap = batchChangerOwner(savedQueryService, dmService, ownMap);							
							log.info("******** 执行更改BOMLine属性之后所有者明细  start ********.");
							printCurObjOwner(tcsoaServiceFactory.getDataManagementService(), ownMap);
							log.info("******** 执行更改BOMLine属性之后所有者明细  end ********.");
							
							if (!checkChangerOwnerResult(ownMap)) {
								throw new Exception("图号为: " + EEItemId + ", 版本号为: " + EEVersion + ", 批量改回对象/对象版本所有权发生错误！");
							}
							
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.err.println(CommonTools.getExceptionMsg(e));
						bodymsg = "<html><head></head><body><h3 style=\"font-family: 宋体;  font-size:15px;\">" + "Layout零組件ID為: "+ layoutItemId + ", Layout版本號為: " + layoutVersion +", EE零組件ID為: "
								+ EEItemId + ", 版本號為: " + EEVersion + ", 原理圖元件正反面信息餘Layout正反面信息同步存在錯誤，請登錄TC進行查看，謝謝！！"
								+ "</h3></body></html>";
					}
					
					
					
					if (CommonTools.isNotEmpty(bodymsg)) {
						HashMap<String, String> httpmap = new HashMap<String, String>();
						httpmap.put("requestPath", requestPath);
						httpmap.put("ruleName", "/tc-mail/teamcenter/sendMail3");
						httpmap.put("sendTo", to);
						httpmap.put("subject", props.getProperty("subject"));
						httpmap.put("htmlmsg", bodymsg);
						String result = TCMail.sendMail(httpmap, attachmentList);
						log.info("【INFO】 邮件发送结果: " + result);					
					}					
				}				
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(CommonTools.getExceptionMsg(e));
		} finally {
			TCPublicUtils.byPass(sessionService, false); // 关闭旁路
			tcSOAClientConfig.destroy(); // 登出系统
			String endTime = CommonTools.getCurrentTimems();
			log.info("==>> endTime: " + endTime);	
			JSONObject jsonObject = getPlacementDispatcherInfo(tcsoaServiceFactory.getDataManagementService(), layoutItemRev, layoutItemId, layoutVersion);
			if (CommonTools.isEmpty(jsonObject)) {
				return;
			}
			jsonObject.put("startTime", startTime);
			jsonObject.put("endTime", endTime);			 
			JSONArray jsonObjectArray=new JSONArray();
			jsonObjectArray.add(jsonObject);
			String rs = HttpUtil.post(requestPath +  "/tc-integrate/actionlog/addlog", jsonObjectArray.toJSONString());
			log.info("==>> rs: " + rs);
			log.info("========== SchematicBOMUpdateHandler end ==========");
		}
	}
	
	
	/**
	 * 生成差异性文件
	 * @param fmUtility
	 * @param dmService
	 * @param layoutItemRev
	 * @param propertyList
	 * @param dir
	 * @param filePath
	 * @param layoutItemId
	 * @param layoutVersion
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Object> generateDiffFile(FileManagementUtility fmUtility, DataManagementService dmService,ItemRevision layoutItemRev, List<String> propertyList, String dir, String filePath) throws Exception {
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, List<String>> placementMap = null;
		String diffFilePath = null;
		if ("Excel".equals(placementType)) {
			placementMap = ExcelPlacementFileTools.analysePlacementExcel(filePath); // 解析Excel Placement文件
			if (CommonTools.isEmpty(placementMap)) {
				throw new Exception("【ERROR】 零组件ID为: " + layoutItemId + ", 版本号为: "+ layoutVersion + ", 解析Placement文件失败...");
			}
			
			diffFilePath = ExcelPlacementDiffUtil.diffWithItemRevision(dmService, propertyList, placementMap, dir, layoutItemRev);
			if (CommonTools.isEmpty(diffFilePath)) {
				log.info("【INFO】 placement和Design-BOM不存在差异");
			} 
		} else if ("txt".equals(placementType)) {
			placementMap = TxtPlacementFileTools.analysePlacementTxt(filePath); // 解析Txt Placement文件
			if (CommonTools.isEmpty(placementMap)) {
				throw new Exception("【ERROR】 零组件ID为: " + layoutItemId + ", 版本号为: "+ layoutVersion + ", 解析Placement文件失败...");
			}
			
			diffFilePath = TxtPlacementDiffUtil.diffWithItemRevision(dmService, propertyList, placementMap, dir, layoutItemRev);
			if (CommonTools.isEmpty(diffFilePath)) {
				log.info("【INFO】 placement和Design-BOM不存在差异");
			}
		}
		
		retMap.put("diffFilePath", diffFilePath);
		retMap.put("placementMap", placementMap);
		return retMap;
	}
	
	
	/**
	 * 生成差异性报表数据集
	 * 
	 * @param attachmentContentList
	 * @param processName
	 * @param parentItemRev
	 * @param parentId
	 * @param parentVersion
	 * @param diffFilePath
	 * @return
	 * @throws NotLoadedException
	 * @throws Exception
	 */
	private static void generateDifferDataset(DataManagementService dmService, FileManagementUtility fmUtility, ItemRevision EEItemRev, String EEItemId,
			String EEVersion, String diffFilePath) throws NotLoadedException, Exception {
		Map<Dataset, String> datasetMap = null;
		log.info("【INFO】 diffFilePath: " + diffFilePath);
		// 获取IMAN_Specification的值
		ModelObject[] imanSpecification = TCPublicUtils.getPropModelObjectArray(dmService, EEItemRev, ItemRevPropConstant.IMAN_SPECIFICATION);
		// 获取数据集文件
		datasetMap = DatasetService.getDataset(dmService, imanSpecification);
		String dsName = EEItemId + "_" + EEVersion + "_" + CommonTools.getNowTime2() + "_" + ConstantsEnum.DIFFERNAME.value();
		// 获取差异性报表数据集
		Dataset differDataset = getDifferDataset(dmService, datasetMap, dsName);
		if (CommonTools.isEmpty(differDataset)) { // 判断差异性报表Text数据集是否存在
			// 创建数据集
			differDataset = DatasetService.createDataset(dmService, EEItemRev, dsName, 
					DatasetEnum.TXT.type(), DatasetEnum.TXT.relationType());
			if (CommonTools.isEmpty(differDataset)) {
				log.info("【ERROR】 零组件ID为: " + EEItemId + ", 版本号为: "+ EEVersion + ", 生成差异性报表失败...");
				throw new Exception("【ERROR】 零组件ID为: " + EEItemId + ", 版本号为: "+ EEVersion + ", 生成差异性报表失败...");
			}			
		} else {	
			DatasetService.removeFileFromDataset(dmService, differDataset, DatasetEnum.TXT.refName()); // 数据集若存在，则先移除掉命名的引用下的数据集
		}
		
		Boolean check = DatasetService.addDatasetFile(fmUtility, dmService, differDataset, diffFilePath, DatasetEnum.TXT.refName(), true); // 数据集添加物理文件	 		
		if (!check) {
			throw new Exception("【ERROR】 零组件ID为: " + EEItemId + ", 版本号为: "+ EEVersion + ", 差异性报表数据集添加附件失败...");
		}
	}
	
	
	
	/**
	 * 判断差异性报表Text数据集是否存在
	 * 
	 * @param map
	 * @return
	 * @throws NotLoadedException
	 */
	@SuppressWarnings("unused")
	private static Dataset getDifferDataset(DataManagementService dataManagementService, Map<Dataset, String> map,
			String dsName) throws NotLoadedException {
		if (CommonTools.isEmpty(map)) {
			return null;
		}
		Dataset differDataset = null;
		for (Map.Entry<Dataset, String> entry : map.entrySet()) {
			Dataset dataset = entry.getKey();
			String objectType = dataset.getTypeObject().getName();
			String objectName = TCPublicUtils.getPropStr(dataManagementService, dataset,
					DatasetPropConstant.OBJECT_NAME);
			if (DatasetEnum.TXT.type().equals(objectType) && dsName.equals(objectName)) { // 判断数据集类型和数据集名称是否是创建出来的差异性报表数据集
				differDataset = dataset;
				break;
			}
		}
		return differDataset;
	}
	
	
	/**
	 * 获取placement文件
	 * @param dmService
	 * @param fmUtility
	 * @param itemRev
	 * @param itemId
	 * @param version
	 * @param dir
	 * @return
	 * @throws Exception
	 */
	private static String getPlacement(DataManagementService dmService, FileManagementUtility fmUtility, String dir) throws Exception {
		String filePath = null; // placement文件的绝对路径 
		ModelObject[] EDAHasDerivedDatasetObjects = TCPublicUtils.getPropModelObjectArray(dmService, layoutItemRev, ItemRevPropConstant.EDAHASDERIVEDDATASET);
		if (CommonTools.isNotEmpty(EDAHasDerivedDatasetObjects)) { // 判断是否含有TXT placement文件
			Map<Dataset, String> datasetMap = DatasetService.getDataset(dmService, EDAHasDerivedDatasetObjects);
			if (CommonTools.isEmpty(datasetMap)) {
				throw new Exception("【ERROR】 零组件ID为: " + layoutItemId + ", 版本号为: "+ layoutVersion + ", 获取TXT Placement数据集失败...");
			}
			
			filePath = getDatasetFile(dmService, fmUtility, datasetMap, dir);
			if (CommonTools.isEmpty(filePath)) {
				throw new Exception("【ERROR】 零组件ID为: " + layoutItemId + ", 版本号为: "+ layoutVersion + ", 下载TXT Placement数据集文件失败...");
			}
			log.info("【INFO】 Placement文件的绝对路径为: " + filePath);
		}
		
		if (CommonTools.isEmpty(filePath)) { // 如果没有获取到TXT placement文件，需要从规范关系下获取
			ModelObject[] imanSpecification = TCPublicUtils.getPropModelObjectArray(dmService, layoutItemRev, ItemRevPropConstant.IMAN_SPECIFICATION);
			Map<Dataset, String> datasetMap = DatasetService.getDataset(dmService, imanSpecification);
			if (CommonTools.isEmpty(datasetMap)) {
				throw new Exception("【ERROR】 零组件ID为: " + layoutItemId + ", 版本号为: "+ layoutVersion + ", 获取Excel Placement数据集失败...");
			}
			
			filePath = getDatasetFile(dmService, fmUtility, datasetMap, dir);
			if (CommonTools.isEmpty(filePath)) {
				throw new Exception("【ERROR】 零组件ID为: " + layoutItemId + ", 版本号为: "+ layoutVersion + ", 下载Excel Placement数据集文件失败...");
			}
			log.info("【INFO】 Placement文件的绝对路径为: " + filePath);			
		}
		return filePath;
	}
	
	
	
	/**
	 * 下载Placement数据集
	 * 
	 * @param map      数据集集合
	 * @param filePath 数据集文件存放的物理路径
	 * @return
	 */
	private static String getDatasetFile(DataManagementService dataManagementService,
			FileManagementUtility fileManagementUtility, Map<Dataset, String> map, String dir) {
		String absoluteFilePath = "";
		for (Map.Entry<Dataset, String> entry : map.entrySet()) {
			String objectName = entry.getValue();
			Dataset dataset = entry.getKey();
			String objectType = dataset.getTypeObject().getName();
			String fileExtensions = null;
			if (DatasetEnum.MSExcel.type().equals(objectType) && objectName.contains("Location")) {
				fileExtensions = DatasetEnum.MSExcel.fileExtensions();
				placementType = "Excel";
			} else if (DatasetEnum.MSExcelX.type().equals(objectType) && objectName.contains("Location")) {
				fileExtensions = DatasetEnum.MSExcelX.fileExtensions();
				placementType = "Excel";
			} else if (DatasetEnum.D9_EDAPlacement.type().equals(objectType)) {
				fileExtensions = DatasetEnum.D9_EDAPlacement.fileExtensions();
				placementType = "txt";
			}
			if (CommonTools.isEmpty(fileExtensions)) {
				continue;
			}			
			log.info("【INFO】 数据集名称为:  " + objectName + ", 正在下载...");
			log.info("【INFO】 数据集类型为:  " + objectType);
			PLACEMENTOWNER = TCPublicUtils.getOwnUser(dataManagementService, dataset); // 获取Placement数据集的所有者
			// 下载数据集
			absoluteFilePath = DatasetService.downloadDataset(dataset, dataManagementService, fileManagementUtility, fileExtensions, dir);
			if (CommonTools.isEmpty(absoluteFilePath)) {
				log.info("【ERROR】 数据集名称为: " + objectName + ", 下载失败啦！");
			} else {
				log.info("【INFO】 数据集名称为: " + objectName + ", 下载成功...");
			}
		}
		return absoluteFilePath;
	}
	
	
	/**
	 * 打印当前对象版本所属的所有者信息
	 * 
	 * @param ownMap
	 */
	private static void printCurObjOwner(DataManagementService dataManagementService, Map<ModelObject, String> ownMap) {
		ownMap.forEach((key, value) -> {
			ModelObject obj = key;
			String owner = value;
			try {
				String itemId = TCPublicUtils.getPropStr(dataManagementService, obj, ItemRevPropConstant.ITEM_ID);
				String version = TCPublicUtils.getPropStr(dataManagementService, obj, ItemRevPropConstant.ITEM_REVISION_ID);
				log.info("【INFO】 零组件ID为: " + itemId + ", 版本号为: " + version + ", 当前用户所有者为: " + owner);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * 批量更改对象/对象版本所有权
	 * 
	 * @param ownMap
	 * @return
	 * @throws NotLoadedException
	 */
	private static Map<ModelObject, String> batchChangerOwner(SavedQueryService savedQueryService, DataManagementService dmService,
			Map<ModelObject, String> ownMap) throws NotLoadedException {
		Map<ModelObject, String> recordOwnMap = new LinkedHashMap<ModelObject, String>();
		for (Map.Entry<ModelObject, String> entry : ownMap.entrySet()) {
			ModelObject object = entry.getKey();
			String itemId = TCPublicUtils.getPropStr(dmService, object, ItemRevPropConstant.ITEM_ID);
			String version = TCPublicUtils.getPropStr(dmService, object, ItemRevPropConstant.ITEM_REVISION_ID);
//			String ownUser = "20286";
			String ownUser = entry.getValue();
			String msg = ownUser;
			ModelObject[] userObjects = TCPublicUtils.executequery(savedQueryService,
					dmService, TCSearchEnum.__WEB_FIND_USER.queryName(),
					TCSearchEnum.__WEB_FIND_USER.queryParams(), new String[] { ownUser }); // 获取user对象
			if (CommonTools.isEmpty(userObjects)) { // 判断是否为空
				msg = "【ERROR】: " + ownUser + ", 用户在Teamcenter不存在";
				recordOwnMap.put(object, msg);
				continue;
			}
			User user = (User) userObjects[0];
			Group group = (Group) user.get_default_group();
			// 获取对象当前的所有者
			String str = TCPublicUtils.getOwnUser(dmService, object);
			if (ownUser.equals(str)) { // 如果当前用户的所有者和ownUser一致，无需更改
				recordOwnMap.put(object, msg);
				continue;
			}
			// 更改对象版本所有权
			if (!TCPublicUtils.changeOwnShip(dmService, object, user, group)) {
				msg = "【ERROR】 零组件ID为: " + itemId + ", 版本号为: " + version + ", 修改对象版本的所有权失败";
				recordOwnMap.put(object, msg);
				log.info(msg);
			} else {
				recordOwnMap.put(object, msg);
			}
		}
		return recordOwnMap;
	}

	/**
	 * 判断更改所有权的结果
	 * 
	 * @param changerOwnerResultMap
	 * @return
	 */
	private static boolean checkChangerOwnerResult(Map<ModelObject, String> changerOwnerResultMap) {
		if (CommonTools.isEmpty(changerOwnerResultMap)) {
			return false;
		}
		boolean flag = true;
		for (Map.Entry<ModelObject, String> entry : changerOwnerResultMap.entrySet()) {
			String msg = entry.getValue();
			if (msg.contains("【ERROR】")) {
				flag = false;
				break;
			}
		}
		return flag;
	}
	
	
	/**
	 * 返回placement更新信息
	 * @param layoutItemRev
	 * @param layoutItemId
	 * @param layoutVersion
	 * @return
	 * @throws NotLoadedException
	 */
	private static JSONObject getPlacementDispatcherInfo(DataManagementService dmService, ItemRevision layoutItemRev, String layoutItemId, String layoutVersion) throws NotLoadedException {
		JSONObject jsonObject = new JSONObject();		
		DispatcherServiceMapper dispatcherServiceMapper = MyBatisUtil.getSqlSession().getMapper(DispatcherServiceMapper.class);		
		List<Map> list = dispatcherServiceMapper.getPlacementDispatcherList(layoutItemRev.getUid());
		if (CommonTools.isEmpty(list)) {
			return null;
		}
		
		list.removeIf(e -> e.get("primaryUid") == null || "".equals(e.get("primaryUid").toString().trim())); // 移除主对象uid为空的记录
		
		Map map = checkPlacementRecord(dmService, list, layoutItemId, layoutVersion);
		if (CommonTools.isEmpty(map)) {
			return null;
		}
		
		jsonObject.put("functionName", "Placement信息更新進Design BOM時間");
		String actualUser = getActualUser(dmService, layoutItemRev);
		if (CommonTools.isEmpty(actualUser)) {
			jsonObject.put("creator", map.get("userId").toString().trim());
			jsonObject.put("creatorName", map.get("userName").toString().trim());
		} else {
			jsonObject.put("creator", actualUser.split("\\|")[0]);
			jsonObject.put("creatorName", actualUser.split("\\|")[1]);
		}
		
		jsonObject.put("project", getProjectId(dmService, layoutItemRev));
		jsonObject.put("itemId",  map.get("itemId").toString().trim());
		jsonObject.put("rev",  map.get("rev").toString().trim());
		jsonObject.put("revUid",  map.get("revUid").toString().trim());
		return jsonObject;
	}
	
	
	/**
	 * 获取实际用户信息
	 * @param layoutItemRev
	 * @return
	 * @throws NotLoadedException
	 */
	private static String getActualUser(DataManagementService dmService, ItemRevision layoutItemRev) throws NotLoadedException {
		TCPublicUtils.refreshObject(dmService, layoutItemRev);
		String str = TCPublicUtils.getPropStr(dmService, layoutItemRev, ItemRevPropConstant.D9_ACTUALUSERID);
		if (CommonTools.isEmpty(str)) {
			return null;
		}
		
		if (str.indexOf("(") == -1) {
			return "";
		}
		
		String userId = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
		String userName = str.substring(0, str.indexOf("("));
		return userId + "|" + userName;
	}
	
	
	/**
	 * 校验记录
	 * @param list
	 * @param layoutItemId
	 * @param layoutVersion
	 * @return
	 * @throws NotLoadedException
	 */
	private static Map checkPlacementRecord(DataManagementService dmService, List<Map> list, String layoutItemId, String layoutVersion) throws NotLoadedException {
		for (Map map : list) {
			String primaryUid = map.get("primaryUid").toString().trim();
			ItemRevision itemRev = (ItemRevision)TCPublicUtils.findObjectByUID(dmService, primaryUid);
			String itemId = TCPublicUtils.getPropStr(dmService, itemRev, ItemRevPropConstant.ITEM_ID);
			String version = TCPublicUtils.getPropStr(dmService, itemRev, ItemRevPropConstant.ITEM_REVISION_ID);
			if (layoutItemId.equals(itemId) && layoutVersion.equals(version)) {
				map.put("itemId", itemId);
				map.put("rev", version);
				map.put("revUid", primaryUid);
				return map;
			}
		}
		return null;
	}
	
	
	/**
	 * 返回专案ID
	 * @param layoutItemRev
	 * @return
	 * @throws NotLoadedException
	 */
	private static String getProjectId(DataManagementService dmService, ItemRevision layoutItemRev) throws NotLoadedException {
		TCPublicUtils.refreshObject(dmService, layoutItemRev);
		return TCPublicUtils.getPropStr(dmService, layoutItemRev, ItemRevPropConstant.PROJECT_IDS);		
	}
	
	
	/**
	 * 获取输入参数
	 * @param args
	 * @return
	 */
	private static Map<String, String> getParams(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		Optional<String> layoutFindAny = Stream.of(args).filter(arg -> {
			return arg.startsWith("-LayoutRevUid");
		}).findAny();
		
		if (layoutFindAny.isPresent()) {
			map.put("LayoutRevUid", layoutFindAny.get().split("=")[1]);
		}
		Optional<String> EEFindAny = Stream.of(args).filter(arg -> {
			return arg.startsWith("-EERevUid");
		}).findAny();
		
		if (EEFindAny.isPresent()) {
			map.put("EERevUid", EEFindAny.get().split("=")[1]);
		}		
		return map;
	}	
}
