package com.foxconn.sync.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.teamcenter.soa.client.Connection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.constants.ConstantsEnum;
import com.foxconn.constants.DatasetEnum;
import com.foxconn.constants.TCPreferenceConstant;
import com.foxconn.mapper.CreoReviseDispatcherMapper;
import com.foxconn.startUp.StartUp;
import com.foxconn.teamcenter.clientx.AppXSession;
import com.foxconn.util.CommonTools;
import com.foxconn.util.HttpUtils;
import com.foxconn.util.MyBatisUtil;
import com.foxconn.util.TCUtil;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.internal.strong.core.ICTService;
import com.teamcenter.services.internal.strong.core._2011_06.ICT;
import com.teamcenter.services.internal.strong.core._2011_06.ICT.Arg;
import com.teamcenter.services.internal.strong.core._2011_06.ICT.Array;
import com.teamcenter.services.internal.strong.core._2011_06.ICT.Entry;
import com.teamcenter.services.internal.strong.core._2011_06.ICT.InvokeICTMethodResponse;
import com.teamcenter.services.internal.strong.core._2011_06.ICT.Structure;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.Dataset;
import com.teamcenter.soa.client.model.strong.DispatcherRequest;
import com.teamcenter.soa.client.model.strong.Group;
import com.teamcenter.soa.client.model.strong.ImanFile;
import com.teamcenter.soa.client.model.strong.Item;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.client.model.strong.PSBOMViewRevision;
import com.teamcenter.soa.client.model.strong.User;
import com.teamcenter.soa.exceptions.NotLoadedException;

import cn.hutool.http.HttpUtil;

import com.teamcenter.services.strong.core._2007_06.DataManagement;
import com.teamcenter.services.strong.core._2007_06.DataManagement.WhereReferencedByRelationNameResponse;

/**
 * @author 作者 Administrator
 * @version 创建时间：2021年12月10日 下午3:37:03 Description:
 */
public class CreoSyncDrawVer {

//	private static final Logger log = Logger.getLogger(CreoSyncDrawVer.class);

	private static final Logger log = LoggerFactory.getLogger(CreoSyncDrawVer.class);
	
	private static List<String> exceptFileList = new ArrayList<String>(); // 存放ipemimport命名执行except参数的值

	private static Properties props = null;
	
	private static Properties tcProps = null;
	
	static {
//		System.out.println("configuration log4j with log4j.properties");
		System.out.println(CommonTools.getPath(CreoSyncDrawVer.class));
		props = CommonTools.getProperties(CommonTools.getPath(CreoSyncDrawVer.class) + 
				File.separator + "ConstantParams.properties");
//		PropertyConfigurator.configure(props.getProperty("log4_properties"));
		tcProps = CommonTools.getProperties(props.getProperty("tc_properties"));
	}
	
	
	public static void main(String[] args) throws Exception {
		log.info("=================CreoSyncDrawVer start=================");	
		String startTime = CommonTools.getCurrentTimems();
		log.info("==>> startTime: " + startTime);	
//		args = new String[] { "-userId=f1412749", "-ItemRevUID=CTgNNyQ4ppJG1D" };
		if (null == args || args.length <= 0) {
			return;
		}
		String userId = "";
		String itemRevUID = "";
		for (String arg : args) {
			System.out.println(arg);
			if (arg.startsWith("-userId")) {
				userId = arg.split("=")[1].trim();
				System.out.println("==>> userId: " + userId);
			} else if (arg.startsWith("-ItemRevUID")) {
				itemRevUID = arg.split("=")[1].trim();
				System.out.println("==>> itemRevUID: " + itemRevUID);
			}
		}

		if (CommonTools.isEmpty(userId)) {
			throw new Exception("【ERROR】 获取输入参数userId发生错误！");
		}
		if (CommonTools.isEmpty(itemRevUID)) {
			throw new Exception("【ERROR】 获取输入参数对象版本uid发生错误！");
		}
		if (CommonTools.isNotEmpty(exceptFileList)) {
			exceptFileList.clear();
		}
		String filePath = null;
		String msg = null;
		Boolean check = null;
		String item_id = null;
		String item_revision_id = null;
		String to = props.getProperty("to_userId");
		Map<ModelObject, String> ownMap = null; // 对象版本所有者集合
		Map<ModelObject, String> changerOwnerMap = null;
		Map<ModelObject, String> changerOwnerMapRecovery = null;
		List<ItemRevision> revisionList = null;
		ItemRevision itemRev = null;
		// 登录TC
		StartUp startUp = new StartUp(tcProps.getProperty("TC_IP"), tcProps.getProperty("TC_USERNAME"), tcProps.getProperty("TC_PASSWORD"));
		boolean isLogin = startUp.isLogin();
		log.info("【INFO】 isLogin: " + isLogin);
		try {

			// 开启旁路
			TCUtil.byPass(true);
			// 获取父对象版本
			itemRev = getParentItemRev(itemRevUID);
			if (CommonTools.isEmpty(itemRev)) {
				throw new Exception("【ERROR】 获取对象版本发生错误！");
			}
			// 获取对象版本字段信息
			Map<String, String> map = getItemRevInfo(TCUtil.getDataManagementService(), itemRev);
			if (CommonTools.isEmpty(map)) {
				throw new Exception("【ERROR】 对象版本字段信息获取失败");
			}
			item_id = map.get("item_id").trim();
			log.info("【INFO】 item_id: " + item_id);
			item_revision_id = map.get("item_revision_id").trim();
			log.info("【INFO】 item_revision_id: " + item_revision_id);

			// 查找保存Creo3D图纸的文件夹
			filePath = CommonTools.getFilePath(ConstantsEnum.CREOFOLDNAME.value());
			System.out.println("【INFO】 filePath: " + filePath);

			// 删除文件夹下面的所有文件
			CommonTools.deletefile(filePath);

			// 获取对象版本所有者的邮箱
			String email = TCUtil.getEmail(TCUtil.getDataManagementService(), userId);

			to = email == null ? "" + to : email + "," +to;

			// 判断是否含有装配类型数据集
			String modelType = checkProAsm(TCUtil.getDataManagementService(), itemRev);
			if (CommonTools.isEmpty(modelType)) {
				throw new Exception("图号为: " + item_id + ", 版本号为: " + item_revision_id + ", 获取模型类型失败，不包含装配/零组件图任意一种，请确认！");
			}

			// 将Creo3D图档导出到本地
			if (exportCreo(filePath, map.get("item_id"), map.get("item_revision_id"), startUp.getTc_USERNAME(),
					startUp.getTc_PASSWORD(), props, modelType)) { // 发送邮件
				// 判断装配图纸是否已经完全导出
				if (!CommonTools.checkFolder(filePath)) {
					throw new Exception("图号为: " + item_id + ", 版本号为: " + item_revision_id + ", 图纸导出到本地文件夹" + filePath + "失败！");
				}
			}

			// 获取BOMLine信息,返回对象版本集合
			revisionList = TCUtil.getBOMWindowInfo(TCUtil.getDataManagementService(), TCUtil.getConnection(), itemRev);
			if (CommonTools.isEmpty(revisionList)) {
				throw new Exception("图号为: " + item_id + ", 版本号为: " + item_revision_id + ", 获取BOMLine集合信息失败！");
			}

			// 挑选出数据集/BOM视图对象版本所有者属于userId的集合
			Map<ModelObject, String> datasetOwnerMap = filterCurrentOwnerModelObj(revisionList, userId, itemRev,
					filePath);
			if (CommonTools.isEmpty(datasetOwnerMap)) {
				throw new Exception(
						"图号为: " + item_id + ", 版本号为: " + item_revision_id + ", 获取数据集所有者为当前用户: " + userId + ", 出现错误");
			}
			
//			List<String> fmsUrlTCPreferences = TCUtil.getTcPreference(TCUtil.getConnection(), TCPreferenceConstant.FMS_BOOTSTRAP_URLS, "site");
//			if (CommonTools.isEmpty(fmsUrlTCPreferences)) {
//				throw new Exception("【ERROR】 TC首选项: " + TCPreferenceConstant.FMS_BOOTSTRAP_URLS + ", 不存在");
//			}
//			
//			String fmsUrl = fmsUrlTCPreferences.get(0);
//			log.info("【INFO】 fmsUrl: " + fmsUrl);
			String fmsUrl = props.getProperty("fms_url");
			log.info("【INFO】 fmsUrl: " + fmsUrl);
			// 下载数据集drw文件
//			getDatasetFile(TCUtil.getDataManagementService(), TCUtil.getConnection(), datasetMap, filePath);
			check = getDatasetFileNew(TCUtil.getDataManagementService(), TCUtil.getConnection(), datasetOwnerMap, filePath, fmsUrl);
			if (!check) {
				throw new Exception("图号为: " + item_id + ", 版本号为: " + item_revision_id + ", 下载drw数据集失败");
			}

			// 批量更改数据集/BOM视图对象版本所有者(StartUp.getTc_USERNAME())
			changerOwnerMap = batchChangerOwner(TCUtil.getDataManagementService(), TCUtil.getConnection(),
					datasetOwnerMap, startUp.getTc_USERNAME(), false);

			log.info("******** 执行导入import命令之前所有者明细  start ********.");
			printCurrentModelObjOwner(TCUtil.getDataManagementService(), changerOwnerMap);
			log.info("******** 执行导入import命令之前所有者明细  end ********.");

			if (!checkChangerOwnerResult(changerOwnerMap)) { // 判断是否含有更改失败的错误信息
				throw new Exception("图号为: " + item_id + ", 版本号为: " + item_revision_id + ", 批量更改对象/对象版本所有权发生错误！");
			}

			// 移除掉pdf/dxf数据集
//			check = deleteDataset(changerOwnerMap, TCUtil.getDataManagementService());
//			if (!check) {
//				throw new Exception("图号为: " + item_id + ", 版本号为: " + item_revision_id + ",  删除pdf/dxf数据集失败");
//			}

			// 导入Creo模型
			if (!importCreo(filePath, startUp.getTc_USERNAME(), startUp.getTc_PASSWORD(), props)) { 				
				throw new Exception(
						"图号为: " + item_id + ", 版本号为: " + item_revision_id + ", 本地文件夹" + filePath + "导入到Teamcenter中失败！");
			}

			// Creo模型导入成功后，需要将本地文件删除
			CommonTools.deletefile(filePath);

			msg = "<html><head></head><body><h3 style=\"font-family: 宋体;  font-size:15px;\">" + "图号为: " + item_id + ", 版本号为: " + item_revision_id 
					+ ", Creo3D和2D图纸升版成功，请登录TC系统进行查看，谢谢！"	+ "</h3></body></html>";
			
			// 发送Creo3D和2D图纸升版成功的邮件
//			msg = "图号为: " + item_id + ", 版本号为: " + item_revision_id + ", Creo3D和2D图纸升版成功，请登录TC系统进行查看，谢谢！";
		} catch (Exception e) {
			e.printStackTrace();
			log.info(CommonTools.getExceptionMsg(e));
//			msg = "图号为: " + item_id + ", 版本号为: " + item_revision_id + " Creo3D和2D图纸升版失败，请联系TC管理员进行处理，谢谢！";
			msg = "<html><head></head><body><h3 style=\"font-family: 宋体;  font-size:15px;\">" + "图号为: " + item_id + ", 版本号为: " + item_revision_id 
					+ " Creo3D和2D图纸升版失败，请联系TC管理员进行处理，谢谢！"	+ "</h3></body></html>";
		} finally {
			if (!CommonTools.isEmpty(changerOwnerMap)) { // 作为判断出对象版本的所有者是否发生更改
				// 将导入成功的数据集的所有者修改回来(传递进来的参数userId)
				try {
					// 挑选出数据集所有者属于startUp.getTc_USERNAME()的集合
//					Map<ModelObject, String> datasetOwnerMap = filterCurrentOwnerModelObj(revisionList, startUp.getTc_USERNAME(), itemRev, item_id, item_revision_id); // 备注需要重新获取一下对象版本集合所有的数据集，因为dxf和pdf数据集在ipemimport.bat执行前进项删除过，ipemimporrt.bat执行导入动作后，对象版本会自动创建出dxf和pdf数据集文件
					changerOwnerMapRecovery = batchChangerOwner(TCUtil.getDataManagementService(),
							TCUtil.getConnection(), changerOwnerMap, userId, true);
					log.info("******** 执行导入import命令之后所有者明细  start ********");
					printCurrentModelObjOwner(TCUtil.getDataManagementService(), changerOwnerMapRecovery);
					log.info("******** 执行导入import命令之后所有者明细  end ********");

					if (!checkChangerOwnerResult(changerOwnerMapRecovery)) {
						throw new Exception(
								"图号为: " + item_id + ", 版本号为: " + item_revision_id + ", 批量改回对象/对象版本所有权发生错误！");
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.info("【ERROR】 批量改回对象/对象版本所有权发生错误！");
					log.info(CommonTools.getExceptionMsg(e));
					msg = "<html><head></head><body><h3 style=\"font-family: 宋体;  font-size:15px;\">" + "图号为: " + item_id + ", 版本号为: " + item_revision_id 
							+ " Creo3D和2D图纸升版失败，请联系TC管理员进行处理，谢谢！"	+ "</h3></body></html>";
				}
			}
			
			List<String> list = TCUtil.getTcPreference(AppXSession.getConnection(), TCPreferenceConstant.D9_SPRINGCLOUD_URL, "site");
			String requestPath = list.get(0);
			
			if (CommonTools.isNotEmpty(msg)) {
				HashMap<String, String> httpmap = new HashMap<String, String>();
//				httpmap.put("requestPath", "http://10.203.163.43:");
//				httpmap.put("ruleName", "80/tc-mail/teamcenter/sendMail3");
				httpmap.put("requestPath", requestPath);
				httpmap.put("ruleName", "/tc-mail/teamcenter/sendMail3");
				httpmap.put("sendTo", to);
				httpmap.put("subject", props.getProperty("subject"));
				httpmap.put("htmlmsg", msg);
				String result = sendMail(httpmap); // 发送邮件
				log.info("【INFO】 邮件发送结果: " + result);			
			}
			// 关闭旁路
			try {
				TCUtil.byPass(false);
			} catch (ServiceException e) {				
				e.printStackTrace();
			}
			// 登出TC
			startUp.disconnectTC();
			
			String endTime = CommonTools.getCurrentTimems();
			log.info("==>> endTime: " + endTime);			
			
			JSONObject jsonObject = getCreoReviseDispatcherInfo(TCUtil.getDataManagementService(),itemRev, item_id, item_revision_id);
			if (CommonTools.isEmpty(jsonObject)) {
				return;
			}
			jsonObject.put("startTime", startTime);
			jsonObject.put("endTime", endTime);			
			JSONArray jsonObjectArray=new JSONArray();
			jsonObjectArray.add(jsonObject);
			String rs = HttpUtil.post(requestPath +  "/tc-integrate/actionlog/addlog", jsonObjectArray.toJSONString());
			log.info("==>> rs: " + rs);
			log.info("=================CreoSyncDrawVer end=================");

		}

	}

	/**
	 * 返回专案ID
	 * @param layoutItemRev
	 * @return
	 * @throws NotLoadedException
	 */
	private static String getProjectId(DataManagementService dataManagementService, ItemRevision itemRev) throws NotLoadedException {
		dataManagementService.refreshObjects(new ModelObject[] { itemRev });
		dataManagementService.getProperties(new ModelObject[] { itemRev }, new String[] { "project_ids"});	
		return itemRev.get_project_ids();
	}
	
	/**
	 * 返回Creo升版Dispatcher信息
	 * @param dataManagementService
	 * @param itemRev
	 * @param parentItemId
	 * @param parentVersion
	 * @return
	 * @throws NotLoadedException
	 */
	private static JSONObject getCreoReviseDispatcherInfo(DataManagementService dataManagementService, ItemRevision itemRev, String parentItemId, String parentVersion) throws NotLoadedException {
		JSONObject jsonObject = new JSONObject();
		CreoReviseDispatcherMapper creoReviseDispatcherMapper = MyBatisUtil.getSqlSession().getMapper(CreoReviseDispatcherMapper.class);
		List<Map> list = creoReviseDispatcherMapper.getCreoReviseDispatcherList(itemRev.getUid());
		if (CommonTools.isEmpty(list)) {
			return null;
		}
		list.removeIf(e -> e.get("primaryUid") == null || "".equals(e.get("primaryUid").toString().trim())); // 移除主对象uid为空的记录
		
		Map map = checkCreoReviseRecord(dataManagementService, list, parentItemId, parentVersion);
		if (CommonTools.isEmpty(map)) {
			return null;
		}
		
		jsonObject.put("functionName", "跨部門協同：人工整理設計數據時間");
		String actualUser = getActualUser(dataManagementService, itemRev);
		if (CommonTools.isEmpty(actualUser)) {
			jsonObject.put("creator", map.get("userId").toString().trim());
			jsonObject.put("creatorName", map.get("userName").toString().trim());
		} else {
			jsonObject.put("creator", actualUser.split("\\|")[0]);
			jsonObject.put("creatorName", actualUser.split("\\|")[1]);
		}
		jsonObject.put("project", getProjectId(dataManagementService, itemRev));
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
	private static String getActualUser(DataManagementService dataManagementService, ItemRevision itemRev) throws NotLoadedException {
		dataManagementService.refreshObjects(new ModelObject[] { itemRev });
		dataManagementService.getProperties(new ModelObject[] { itemRev }, new String[] { "d9_ActualUserID"});	
		String str = itemRev.getPropertyObject("d9_ActualUserID").getStringValue();
		if (CommonTools.isEmpty(str)) {
			return null;
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
	private static Map checkCreoReviseRecord(DataManagementService dataManagementService, List<Map> list, String parentItemId, String parentVersion) throws NotLoadedException {
		for (Map map : list) {
			String primaryUid = map.get("primaryUid").toString().trim();
			ServiceData data = dataManagementService.loadObjects(new String[] { primaryUid });
			ItemRevision itemRev = (ItemRevision) data.getPlainObject(0);
			dataManagementService.refreshObjects(new ModelObject[] { itemRev });
			dataManagementService.getProperties(new ModelObject[] { itemRev }, new String[] { "item_id", "item_revision_id"});			
			String itemId = itemRev.get_item_id();
			String version = itemRev.get_item_revision_id();
			if (parentItemId.equals(itemId) && parentVersion.equals(version)) {
				map.put("itemId", itemId);
				map.put("rev", version);
				map.put("revUid", primaryUid);
				return map;
			}
		}
		return null;
	}
	
	
	/**
	 * 挑选出数据集/BOM视图对象版本所有者属于userId的集合
	 * 
	 * @param userId  用户id
	 * @param itemRev 父对象版本
	 * @param itemId  父对象版本ID
	 * @param version 父对象版本号
	 * @return
	 * @throws Exception
	 * @throws NotLoadedException
	 */
	private static Map<ModelObject, String> filterCurrentOwnerModelObj(List<ItemRevision> revisionList, String userId,
			ItemRevision itemRev, String filePath) throws Exception, NotLoadedException {
		// 获取所有的零组件ID对应的获取对象数据集和BOM视图对象版本集合
		Map<String, List<ModelObject>> datasetMap = getModelObjList(TCUtil.getDataManagementService(),
				TCUtil.getConnection(), revisionList);
		if (CommonTools.isEmpty(datasetMap)) {
			log.info("【ERROR】 获取关系属性 IMAN_specification/IMAN_Rendering/structure_revisions失败！");
			return null;
		}

		// 获取数据集和BOM视图对象版本集合
		List<ModelObject> totalDatasetList = TCUtil.getTotalDatasetList(datasetMap);
		if (CommonTools.isEmpty(totalDatasetList)) {
			log.info("【ERROR】 整合所有的数据集失败！");
			return null;
		}
		// 获取数据集/BOM视图对象版本集合
		Map<ModelObject, String> datasetOwnerMap = TCUtil.getModelObjectOwn(TCUtil.getDataManagementService(),
				totalDatasetList.toArray(new ModelObject[totalDatasetList.size()]));
		if (CommonTools.isEmpty(datasetOwnerMap)) {
			log.info("【ERROR】 获取对象版本所有者失败！");
			return null;
		}
		// 获取不是当前用户的所有数据集物理文件路径
//		getNotCurUserFileAbsolutePath(TCUtil.getDataManagementService(), userId, datasetOwnerMap, filePath);

		// 过滤掉不属于当前用户的数据集/BOM视图对象版本
		datasetOwnerMap = parseMapForFilter(userId, datasetOwnerMap);

		return datasetOwnerMap;
	}

	/**
	 * 获取父对象版本
	 * 
	 * @return
	 */
	private static ItemRevision getParentItemRev(String itemRevUID) {
		ServiceData data = TCUtil.getDataManagementService().loadObjects(new String[] { itemRevUID });

		ModelObject obj = data.getPlainObject(0);

		return (ItemRevision) obj;
	}

	/**
	 * 获取对象版本字段信息
	 * 
	 * @return
	 * @throws NotLoadedException
	 */
	private static Map<String, String> getItemRevInfo(DataManagementService dataManagementService, ItemRevision itemRev)
			throws NotLoadedException {
		Map<String, String> map = new HashMap<>();
		dataManagementService.refreshObjects(new ModelObject[] { itemRev });
		dataManagementService.getProperties(new ModelObject[] { itemRev },
				new String[] { "item_id", "item_revision_id" });
		String itemId = itemRev.get_item_id();
		String version = itemRev.get_item_revision_id();
		map.put("item_id", itemId);
		map.put("item_revision_id", version);
		return map;
	}

	/**
	 * 导出Creo3D图档
	 *
	 * @param path             文件存放路径
	 * @param item_id          ID
	 * @param item_revision_id 版本号
	 * @param username         用户名
	 * @param password         密码
	 * @return
	 */
	private static Boolean exportCreo(String path, String item_id, String item_revision_id, String username,
			String password, Properties props, String modelType) {
		try {
			log.info("【INFO】pItemRevId is: " + item_id);
			log.info("【INFO】pItemRevsion is: " + item_revision_id);
			log.info("【INFO】 path is: " + path);
			String batName = props.getProperty("ipemexport").substring(
					props.getProperty("ipemexport").lastIndexOf("\\") + 1, props.getProperty("ipemexport").length());
			String comand = props.getProperty("ipemexport") + " " + "-u=" + username + " " + "-p=" + password + " "
					+ "-item_id=" + item_id + " " + "-item_revision_id=" + item_revision_id + " " + "-model_type="
					+ modelType + " " + "-export_dir=" + path + " " + "-export=true -export_related=true -delete_files";
			log.info("【INFO】 comand: " + comand);
			return CommonTools.doCallBat(comand, batName);
		} catch (Exception e) {
			e.printStackTrace();
			log.info(CommonTools.getExceptionMsg(e));
			log.info("【ERROR】 图号为: " + item_id + ", 版本号为: " + item_revision_id + ", 导出失败！");
			return false;
		}
	}

	/**
	 * 导入Creo模型到Teamcenter中
	 * 
	 * @param path     文件路径
	 * @param username 用户名
	 * @param password 密码
	 * @return
	 */
	private static Boolean importCreo(String path, String username, String password, Properties props) {
		try {
			String command = null;
			log.info("【INFO】 path is: " + path);
			String batName = props.getProperty("ipemimport").substring(
					props.getProperty("ipemimport").lastIndexOf("\\") + 1, props.getProperty("ipemimport").length());
			File dir = new File(path);
			String exceptParams = "";
			if (CommonTools.isNotEmpty(exceptFileList)) {
				for (String str : exceptFileList) {
					exceptParams += "-except=" + str + " ";
				}
			}
//			if (dir.isDirectory() && dir.listFiles().length > 0) {
//				String[] filelist = dir.list();
//				for (int i = 0; i < filelist.length; i++) {
//					File file = new File(path + File.separator + filelist[i]);
//					String fileName = file.getName();
//					if (fileName.contains(DatasetEnum.PROFRM.fileExtensions())) {
//						exceptParams += "-except=" + file.getAbsolutePath() + " ";
//					}
//				}
//			}

			if (CommonTools.isNotEmpty(exceptParams)) {
				command = props.getProperty("ipemimport") + " " + "-u=" + username + " " + "-p=" + password + " "
						+ exceptParams + "-overwrite" + " " + path;
			} else {
				command = props.getProperty("ipemimport") + " " + "-u=" + username + " " + "-p=" + password + " "
						+ "-overwrite" + " " + path;
			}
			log.info("【INFO】 command: " + command);
			return CommonTools.doCallBat(command, batName);
		} catch (Exception e) {
			e.printStackTrace();
			log.info(CommonTools.getExceptionMsg(e));
			log.info("【ERROR】 文件夹路径为: " + path + ", 数据集文件导入Teamcenter失败！");
			return false;
		}
	}

	/**
	 * 判断是否含有装配类型数据集
	 * 
	 * @param dataManagementService
	 * @param itemRev
	 * @return
	 * @throws NotLoadedException
	 */
	private static String checkProAsm(DataManagementService dataManagementService, ItemRevision itemRev)
			throws NotLoadedException {
		String modelType = null;
		dataManagementService.refreshObjects(new ModelObject[] { itemRev });
		dataManagementService.getProperties(new ModelObject[] { itemRev },
				new String[] { "IMAN_specification", "IMAN_Rendering", "item_id", "item_revision_id" });
		ModelObject[] specifications = itemRev.get_IMAN_specification();
		ModelObject[] renderings = itemRev.get_IMAN_Rendering();
		if (CommonTools.isEmpty(specifications) && CommonTools.isEmpty(renderings)) {
			return null;
		}
		List<Dataset> specificationList = TCUtil.getDataset(dataManagementService, specifications);
		List<Dataset> renderingList = TCUtil.getDataset(dataManagementService, renderings);
		if (CommonTools.isEmpty(specificationList) && CommonTools.isEmpty(renderingList)) {
			return null;
		}
		List<Dataset> list = Stream.of(specificationList, renderingList).flatMap(x -> x.stream())
				.collect(Collectors.toList());
		for (Dataset dataset : list) {
			String objectType = dataset.getTypeObject().getName();
			if (objectType.equals(DatasetEnum.PROASM.type())) { // 判断是否含含有装配数据集
				modelType = DatasetEnum.PROASM.type();
				break;
			} else if (objectType.equals(DatasetEnum.PROPRT.type())) {
				modelType = DatasetEnum.PROPRT.type();
			}
		}
		return modelType;
	}

	/**
	 * 获取对象数据集和BOM视图对象版本集合
	 * 
	 * @param dataManagementService
	 * @param connection
	 * @param resultList
	 * @param userId
	 * @param filePath
	 * @return
	 * @throws NotLoadedException
	 */
	private static Map<String, List<ModelObject>> getModelObjList(DataManagementService dataManagementService,
			Connection connection, List<ItemRevision> resultList) throws NotLoadedException {
		Map<String, List<ModelObject>> map = new LinkedHashMap<String, List<ModelObject>>();
		for (ItemRevision itemRev : resultList) {
			// 获取对象版本的所有者
//			String ownUserId = TCUtil.getOwnUser(dataManagementService, itemRev);
//			if (CommonTools.isEmpty(ownUserId)) {
//				continue;
//			}
//			if (ownUserId.equals(userId)) {
			dataManagementService.refreshObjects(new ModelObject[] { itemRev });
			dataManagementService.getProperties(new ModelObject[] { itemRev }, new String[] { "IMAN_specification",
					"IMAN_Rendering", "item_id", "item_revision_id", "structure_revisions" });
			ModelObject[] specifications = itemRev.get_IMAN_specification();
			ModelObject[] renderings = itemRev.get_IMAN_Rendering();
			PSBOMViewRevision[] structureRevisions = itemRev.get_structure_revisions(); // BOM视图版本数组
			String itemId = itemRev.get_item_id();
			String version = itemRev.get_item_revision_id();
//				log.info("【INFO】 图号:" + itemId);
			if (CommonTools.isEmpty(specifications) && CommonTools.isEmpty(renderings)
					&& CommonTools.isEmpty(structureRevisions)) {
				continue;
			}
			List<ModelObject> specificationList = TCUtil.getModelObjects(dataManagementService, specifications);
			List<ModelObject> renderingList = TCUtil.getModelObjects(dataManagementService, renderings);
			List<ModelObject> structureList = TCUtil.getModelObjects(dataManagementService, structureRevisions);
			if (CommonTools.isEmpty(specificationList) && CommonTools.isEmpty(renderingList)
					&& CommonTools.isEmpty(structureList)) {
				continue;
			}
			List<ModelObject> list = new ArrayList<ModelObject>();
			list = Stream.of(list, specificationList).flatMap(x -> x.stream()).collect(Collectors.toList());
			list = Stream.of(list, renderingList).flatMap(x -> x.stream()).collect(Collectors.toList());
			list = Stream.of(list, structureList).flatMap(x -> x.stream()).collect(Collectors.toList());
			map.put(itemId + "/" + version, list);

//			}
		}
		return map;
	}

	/**
	 * 批量下载数据集drw文件
	 * 
	 * @param dataManagementService
	 * @param list
	 * @throws NotLoadedException
	 */
	private static boolean getDatasetFileNew(DataManagementService dataManagementService, Connection connection,
			Map<ModelObject, String> map, String filePath, String fmsUrl) throws NotLoadedException {
		Boolean flag = true;
		for (Map.Entry<ModelObject, String> entry : map.entrySet()) {
			if (!(entry.getKey() instanceof Dataset)) {
				continue;
			}
			Dataset dataset = (Dataset) entry.getKey();
			String fileExtensions = ""; // 文件后缀
			String objectType = dataset.getTypeObject().getName();
			if (DatasetEnum.DRW.type().equals(objectType)) {
				dataManagementService.refreshObjects(new ModelObject[] { dataset });
				dataManagementService.getProperties(new ModelObject[] { dataset }, new String[] { "object_name" });
				String objectName = dataset.get_object_name();
				log.info("【INFO】 数据集名称为: " + objectName + ", 类型为: " + objectType + ", 正在下载中...");
				fileExtensions = DatasetEnum.DRW.fileExtensions();
				// 下载drw数据集
				String absoluteFilePath = TCUtil.downloadDataset(dataManagementService, connection, dataset,
						fileExtensions, filePath, fmsUrl);
				if (CommonTools.isEmpty(absoluteFilePath)) {
					log.info("【ERROR】  数据集名称为: " + objectName + ", 类型为: " + objectType + ", 下载失败啦！");
					flag = false;
					break;
				} else {
					log.info("【INFO】 数据集名称为: " + objectName + ", 类型为: " + objectType + ", 下载成功, 文件的路径为: "
							+ absoluteFilePath);
					// 下载frm图框文件
					downloadFrmFile(dataManagementService, connection, dataset, filePath, fmsUrl);
				}
			}
		}
		return flag;
	}

	/**
	 * 下载frm图框文件
	 * 
	 * @param dataManagementService 工具类
	 * @param connection            连接
	 * @param dataset               数据集
	 * @param dir                   路径
	 * @throws NotLoadedException
	 */
	private static void downloadFrmFile(DataManagementService dataManagementService, Connection connection,
			Dataset dataset, String filePath, String fmsUrl) throws NotLoadedException {
		dataManagementService.getProperties(new ModelObject[] { dataset }, new String[] { "Pro2_format" });
		ModelObject[] modelObjects = dataset.getPropertyObject("Pro2_format").getModelObjectArrayValue();
		if (CommonTools.isEmpty(modelObjects)) {
			return;
		}
		ItemRevision itemRev = null;
		for (ModelObject modelObject : modelObjects) {
			if (modelObject instanceof ItemRevision) {
				itemRev = (ItemRevision) modelObject;
				break;
			}
		}
		if (CommonTools.isEmpty(itemRev)) {
			return;
		}
		dataManagementService.refreshObjects(new ModelObject[] { itemRev });
		dataManagementService.getProperties(new ModelObject[] { itemRev }, new String[] { "IMAN_specification" });
		ModelObject[] imanSpecifications = itemRev.get_IMAN_specification();
		if (CommonTools.isEmpty(imanSpecifications)) {
			return;
		}
		for (ModelObject obj : imanSpecifications) {
			if (!(obj instanceof Dataset)) {
				continue;
			}
			Dataset dataset1 = (Dataset) obj;
			String fileExtensions = ""; // 文件后缀
			String objectType = dataset1.getTypeObject().getName();
			if (DatasetEnum.PROFRM.type().equals(objectType)) {
				dataManagementService.refreshObjects(new ModelObject[] { dataset1 });
				dataManagementService.getProperties(new ModelObject[] { dataset1 }, new String[] { "object_name" });
				String objectName = dataset1.get_object_name();
				log.info("【INFO】 数据集名称为: " + objectName + ", 类型为: " + objectType + ", 正在下载中...");
				fileExtensions = DatasetEnum.PROFRM.fileExtensions();
				// 下载frm数据集
				String absoluteFilePath = TCUtil.downloadDataset(dataManagementService, connection, dataset1,
						fileExtensions, filePath, fmsUrl);
				if (CommonTools.isEmpty(absoluteFilePath)) {
					log.info("【ERROR】  数据集名称为: " + objectName + ", 类型为: " + objectType + ", 下载失败啦！");
				} else {
					log.info("【INFO】 数据集名称为: " + objectName + ", 类型为: " + objectType + ", 下载成功, 文件的路径为: " + absoluteFilePath);
					if (!exceptFileList.contains(absoluteFilePath)) {
						exceptFileList.add(absoluteFilePath);
					}
				}
			}
		}
	}

	/**
	 * 移除PDF/DXF数据集
	 * 
	 * @param map
	 * @param dataManagementService
	 * @throws NotLoadedException
	 */
	private static boolean deleteDataset(Map<ModelObject, String> map, DataManagementService dataManagementService)
			throws NotLoadedException {
		boolean flag = true;
		try {
			for (Map.Entry<ModelObject, String> entry : map.entrySet()) {
				boolean deleteFlag = false;
				if (!(entry.getKey() instanceof Dataset)) {
					continue;
				}
				Dataset dataset = (Dataset) entry.getKey();
				dataManagementService.refreshObjects(new ModelObject[] { dataset });
				dataManagementService.getProperties(new ModelObject[] { dataset }, new String[] { "object_name" });
				String objectName = dataset.get_object_name();
				String objectType = dataset.getTypeObject().getName();
				if (objectType.equals(DatasetEnum.PDF.type())) {
					TCUtil.removeFileFromDataset(dataManagementService, dataset, DatasetEnum.PDF.refName());
					deleteFlag = true;
				} else if (objectType.equals(DatasetEnum.DXF.type())) {
					TCUtil.removeFileFromDataset(dataManagementService, dataset, DatasetEnum.DXF.refName());
					deleteFlag = true;
				}
//				if (deleteFlag) {
//					ServiceData data = TCUtil.getWhereReferences(dataManagementService, dataset, 1); //  通过影响分析获取引用对象
//					if (CommonTools.isEmpty(data)) {
//						log.error("【INFO】 数据集名称为: " + objectName + ", 类型为: " + objectType + ", 通过影响分析获取对象版本失败...");
//						flag = false;
//						break;
//					}
//					ItemRevision itemRev = (ItemRevision) data.getPlainObject(0);
//					// 删除对象版本和数据集的关系
//					TCUtil.deleteRelation(dataManagementService, itemRev, dataset, "IMAN_Rendering");
//					// 删除数据集
//					dataManagementService.deleteObjects(new ModelObject[] { dataset });
//					log.info("【INFO】 数据集名称为: " + objectName + ", 类型为: " + objectType + ", 删除成功...");
//					System.out.println("【INFO】 数据集名称为: " + objectName + ", 类型为: " + objectType + ", 删除成功...");
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(CommonTools.getExceptionMsg(e));
			flag = false;
		}
		return flag;
	}

	/**
	 * 批量下载数据集dxf,drw,pdf文件
	 * 
	 * @param dataManagementService
	 * @param list
	 * @throws NotLoadedException
	 */
	private static void getDatasetFile(DataManagementService dataManagementService, Connection connection,
			Map<String, List<Dataset>> map, String filePath, String fmsUrl) throws NotLoadedException {
		if (CommonTools.isEmpty(map)) {
			return;
		}
		for (Map.Entry<String, List<Dataset>> entry : map.entrySet()) {
			String key = entry.getKey();
			List<Dataset> list = entry.getValue();
			for (Dataset dataset : list) {
				String fileExtensions = ""; // 文件后缀
				dataManagementService.refreshObjects(new ModelObject[] { dataset });
				dataManagementService.getProperties(new ModelObject[] { dataset }, new String[] { "object_name" });
				String objectName = dataset.get_object_name();
				System.out.println("【INFO】 objectName: " + objectName);
				String objectType = dataset.getTypeObject().getName().toLowerCase();
				System.out.println("【INFO】 objectType: " + objectType);
				for (ConstantsEnum vaConstantsEnum : ConstantsEnum.values()) {
					if (objectType.equals(vaConstantsEnum.value().split("=")[0])) {
						System.out.println("【INFO】 ID为: " + key + ", 正在下载文件...");
						System.out.println("【INFO】 ID为: " + key + ", 正在下载文件...");
						fileExtensions = vaConstantsEnum.value().split("=")[1];
						// 下载数据集
						String absoluteFilePath = TCUtil.downloadDataset(dataManagementService, connection, dataset,
								fileExtensions, filePath, fmsUrl);
						if (CommonTools.isEmpty(absoluteFilePath)) {
							System.out.println("【ERROR】 " + absoluteFilePath + " 下载失败啦！");
						} else {
							System.out.println("【INFO】  " + absoluteFilePath + " 下载成功！");
						}
					}
				}

			}
		}
	}

	/**
	 * 批量更改所有权
	 * 
	 * @param userId                用户id
	 * @param dataManagementService 工具类
	 * @param connection            连接
	 * @param resultList            对象版本集合
	 * @param newUserId             所有权需要更改到哪个用户
	 * @return
	 * @throws NotLoadedException
	 */
	private static Boolean batchChangerOwner(String userId, DataManagementService dataManagementService,
			Connection connection, List<ItemRevision> resultList) throws NotLoadedException {
		Boolean flag = true;
		for (ItemRevision itemRev : resultList) {
			// 获取对象版本的所有者
//			String ownUserId = TCUtil.getOwnUser(dataManagementService, itemRev);
//			if (CommonTools.isEmpty(ownUserId)) {
//				continue;
//			}
//			if (ownUserId.equals(userId)) {
			ModelObject[] userObjects = TCUtil.executequery("D9_find_user", new String[] { "UserID" },
					new String[] { userId }, connection, dataManagementService);
			if (CommonTools.isEmpty(userObjects)) { // 判断是否为空
				flag = false;
				break;
			}
			User user = (User) userObjects[0];
			Group group = (Group) user.get_default_group();
			dataManagementService.refreshObjects(new ModelObject[] { itemRev });
			dataManagementService.getProperties(new ModelObject[] { itemRev }, new String[] { "items_tag" });
			Item item = itemRev.get_items_tag();
			// 更改对象所有权
//				if (!TCUtil.changeOwnShip(dataManagementService, item, user, group)) {
//					flag = false;
//					break;
//				}
			// 更改对象版本所有权
			if (!TCUtil.changeOwnShip(dataManagementService, itemRev, user, group)) {
				flag = false;
				break;
			}

		}
//		}
		return flag;
	}

	/**
	 * 批量更改所有权，记录所有者信息
	 * 
	 * @param userId                用户id
	 * @param dataManagementService 工具类
	 * @param connection            连接
	 * @param resultList            对象版本集合
	 * @param newUserId             所有权需要更改到哪个用户
	 * @param changeDisPahcherFlag  用于是否需要更改DispatherRequest对象所有者
	 * @return
	 * @throws NotLoadedException
	 * @throws ServiceException
	 */
	private static Map<ModelObject, String> batchChangerOwner(DataManagementService dataManagementService,
			Connection connection, Map<ModelObject, String> ownMap, String newUserId, boolean changeDisPahcherFlag)
			throws NotLoadedException, ServiceException {
		ModelObject[] userObjects = TCUtil.executequery("__WEB_find_user", new String[] { "User ID" },
				new String[] { newUserId }, connection, dataManagementService);
		if (CommonTools.isEmpty(userObjects)) {
			log.info("【ERROR】获取用户: " + newUserId + ", 用户对象失败...");
			return null;
		}
		User user = (User) userObjects[0];
		Group group = (Group) user.get_default_group();
		Map<ModelObject, String> recordOwnMap = new LinkedHashMap<ModelObject, String>();
		for (Map.Entry<ModelObject, String> entry : ownMap.entrySet()) {
//			boolean flag = false; // 用于通过影响分析获取引用对象，进而获取 DispatherRequest对象
			ModelObject object = entry.getKey();
			String msg = newUserId;
			dataManagementService.refreshObjects(new ModelObject[] { object });
			dataManagementService.loadObjects(new String[] { object.getUid() });
			dataManagementService.getProperties(new ModelObject[] { object },
					new String[] { "object_name", "object_type" });
			String objectName = object.getPropertyObject("object_name").getStringValue();
			String objectType = object.getPropertyObject("object_type").getStringValue();
			if (object instanceof PSBOMViewRevision) {
				PSBOMViewRevision psbomViewRevision = (PSBOMViewRevision) object;
				// 判断BOM视图版本是否已经签出
				TCUtil.checkin(dataManagementService, connection, psbomViewRevision);
				// 更改BOM视图对象版本所有者
				if (!TCUtil.changeOwnShip(dataManagementService, psbomViewRevision, user, group)) {
					msg = "【ERROR】 " + "对象名称为:  " + objectName + ", 类型为:  " + objectType + "更改所有权失败！";
					log.info(msg);
				}
			} else if (object instanceof Dataset) {
				Dataset dataset = (Dataset) object;
				// 判断数据集是否已经签出
				TCUtil.checkin(dataManagementService, connection, dataset);
				// 更改数据集所有者
				if (!TCUtil.changeOwnShip(dataManagementService, object, user, group)) {
					msg = "【ERROR】 " + "数据集名称为:  " + objectName + ", 类型为:  " + objectType + ", 更改所有权失败！";
					recordOwnMap.put(dataset, msg);
					log.info(msg);
					continue;
				}
				if (changeDisPahcherFlag) {
					ItemRevision itemRev = TCUtil.getWhereReferences(dataManagementService, dataset, 1); // 通过影响分析获取引用对象
					dataManagementService.getProperties(new ModelObject[] { itemRev },
							new String[] { "item_id", "item_revision_id" });
					String itemId = itemRev.get_item_id();
					String version = itemRev.get_item_revision_id();
					Map<ModelObject, String> whereReferencesMap = TCUtil.getWhereReferencesByPOM(dataManagementService,
							itemRev); // 通过命名的引用的"引用POM"关系获取DispatherRequest对象
					if (CommonTools.isEmpty(whereReferencesMap)) {
						recordOwnMap.put(object, msg);
						continue;
					}
					for (Map.Entry<ModelObject, String> whereReferencesEntry : whereReferencesMap.entrySet()) {
						String info = null;
						DispatcherRequest dispatcherRequest = (DispatcherRequest) whereReferencesEntry.getKey();
						String dispatcherUser = TCUtil.getOwnUser(dataManagementService, dispatcherRequest);
						if (dispatcherUser.equals(newUserId)) { // 如果DispatcherRequest对象所有者和newUserId一致
							recordOwnMap.put(dispatcherRequest, msg);
							continue;
						}
						if (!TCUtil.changeOwnShip(dataManagementService, dispatcherRequest, user, group)) { // 更改DispatcherRequest对象所有者
							info = "【ERROR】 零组件ID为: " + itemId + ", 版本号为: " + version
									+ ", 修改关联的DispatcherRequest对象所有权失败, 对象版本uid为: " + dispatcherRequest.getUid();
							recordOwnMap.put(dispatcherRequest, info);
							log.info(info);
						} else {
							recordOwnMap.put(dispatcherRequest, msg);
						}
					}
				}
			}
			recordOwnMap.put(object, msg);
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
	 * 打印当前对象所属的所有者信息
	 * 
	 * @param changerOwnerMap
	 */
	private static void printCurrentModelObjOwner(DataManagementService dataManagementService,
			Map<ModelObject, String> changerOwnerMap) {
		changerOwnerMap.forEach((key, value) -> {
			ModelObject obj = key;
			String owner = value;
			dataManagementService.getProperties(new ModelObject[] { obj },
					new String[] { "object_name", "object_type", "object_string" });
			String objectName = null;
			String objectType = null;
			try {
				if (obj instanceof DispatcherRequest) {
					objectName = obj.getPropertyObject("object_string").getStringValue();
					log.info("【INFO】 DispatcherRequest对象名称为: " + objectName + ", 当前用户所有者为: " + owner);
				} else {
					objectName = obj.getPropertyObject("object_name").getStringValue();
					objectType = obj.getPropertyObject("object_type").getStringValue();
					log.info("【INFO】 " + "数据集名称为:  " + objectName + ", 类型为:  " + objectType + ", 当前用户所有者为:" + owner);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * 过滤掉不属于当前用户的对象版本
	 * 
	 * @param userId
	 * @param ownMap
	 * @return
	 */
	private static Map<ModelObject, String> parseMapForFilter(String userId, Map<ModelObject, String> ownMap) {
		ownMap = ownMap.entrySet().stream().filter((e) -> e.getValue().equals(userId))
				.collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
		return ownMap;
	}

	/**
	 * 获取不是当前用户的所有数据集物理文件路径
	 * 
	 * @param userId
	 * @param ownMap
	 * @return
	 * @throws NotLoadedException
	 */
	private static void getNotCurUserFileAbsolutePath(DataManagementService dataManagementService, String userId,
			Map<ModelObject, String> ownMap, String filePath) throws NotLoadedException {
		Map<ModelObject, String> map = ownMap.entrySet().stream().filter((e) -> !e.getValue().equals(userId))
				.collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
		for (Map.Entry<ModelObject, String> entry : map.entrySet()) {
			ModelObject obj = entry.getKey();
			if (!(obj instanceof Dataset)) {
				continue;
			}
			Dataset dataset = (Dataset) obj;
			String fileExtensions = "";
			String objectType = dataset.getTypeObject().getName();
			if (DatasetEnum.PROASM.type().equals(objectType)) {
				fileExtensions = DatasetEnum.PROASM.fileExtensions();
			} else if (DatasetEnum.PROPRT.type().equals(objectType)) {
				fileExtensions = DatasetEnum.PROPRT.fileExtensions();
			}
			dataManagementService.refreshObjects(new ModelObject[] { dataset });
			dataManagementService.getProperties(new ModelObject[] { dataset }, new String[] { "ref_list" });
			ModelObject[] dsfiles = dataset.get_ref_list();
			if (CommonTools.isEmpty(dsfiles)) {
				return;
			}
			ImanFile dsFile = null;
			for (int i = 0; i < dsfiles.length; i++) {
				if (!(dsfiles[i] instanceof ImanFile)) {
					continue;
				}
				dsFile = (ImanFile) dsfiles[i];
				dataManagementService.refreshObjects(new ModelObject[] { dsFile });
				dataManagementService.getProperties(new ModelObject[] { dsFile },
						new String[] { "original_file_name" });
				String fileName = dsFile.get_original_file_name();
				if (!fileName.toLowerCase().contains(fileExtensions)) {
					continue;
				}
				File file = null;
				if (filePath.endsWith("\\")) {
					file = new File(filePath + fileName);
				} else {
					file = new File(filePath + File.separator + fileName);
				}
				if (file.exists()) { // 判断文件是否存在
					String fileAbsolutePath = file.getAbsolutePath();
					if (!exceptFileList.contains(fileAbsolutePath)) {
						exceptFileList.add(fileAbsolutePath);
					}
				}
			}
		}
	}

	/**
	 * 记录邮件正文
	 * 
	 * @param filePath 文件路径
	 * @param content  正文内容
	 * @param itemId   ID
	 * @param version  版本号
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static String recordBody(String filePath, String content, String itemId, String version) {
		try {
			String txtFilePath = filePath + "\\" + itemId + "_" + version + "_body" + ".txt";
			File file = new File(txtFilePath);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			System.out.println("==========start writing txtFile==========");
			// 将内容写入到文本中
			write(txtFilePath, content);
			System.out.println("==========ending writing txtFile==========");
			return file.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 讲内容写入到文件中
	 * 
	 * @param fileName
	 * @param content
	 */
	public static void write(String fileName, String content) {
		System.out.println("==>> writing content is: " + content);
		try {
			// UTF-8,防止乱码
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"),
					1024);
			writer.write(content + "\r\n");
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * 发送邮件(包含附件)
	 * 
	 * @param mailMap 邮件基本参数
	 * @param attachmentList 附件参数
	 * @return
	 */
	public static String sendMail(HashMap<String, String> mailMap, List<String> attachmentList) {
		return HttpUtils.httpPost(mailMap, attachmentList);
	}
	
	/**
	 * 发送邮件(不包含附件)
	 * @param mailMap
	 * @return
	 */
	public static String sendMail(HashMap<String, String> mailMap) {
		return HttpUtils.httpPost(mailMap);
	}
	
	
}
