package com.foxconn.convert.handler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foxconn.convert.config.ConvertConstant;
import com.foxconn.convert.config.TCSOAClientConfigImpl;
import com.foxconn.plm.tcapi.constants.DatasetEnum;
import com.foxconn.plm.tcapi.constants.DatasetPropConstant;
import com.foxconn.plm.tcapi.constants.ItemRevPropConstant;
import com.foxconn.plm.tcapi.constants.TCPreferenceConstant;
import com.foxconn.plm.tcapi.domain.SPASUser;
import com.foxconn.plm.tcapi.mail.TCMail;
import com.foxconn.plm.tcapi.service.DatasetService;
import com.foxconn.plm.tcapi.service.EPMTaskService;
import com.foxconn.plm.tcapi.service.ITCSOAClientConfig;
import com.foxconn.plm.tcapi.service.TCSOAServiceFactory;
import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.plm.tcapi.utils.HttpUtil;
import com.foxconn.plm.tcapi.utils.TCPublicUtils;
import com.teamcenter.services.loose.core.SessionService;
import com.teamcenter.services.strong.administration.PreferenceManagementService;
import com.teamcenter.services.strong.cad.StructureManagementService;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core._2007_01.Session;
import com.teamcenter.services.strong.core._2007_01.Session.MultiPreferencesResponse;
import com.teamcenter.services.strong.query.SavedQueryService;
import com.teamcenter.services.strong.structuremanagement.StructureService;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.FileManagementUtility;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.Dataset;
import com.teamcenter.soa.client.model.strong.EPMTask;
import com.teamcenter.soa.client.model.strong.ItemRevision;
import com.teamcenter.soa.exceptions.NotLoadedException;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;

public class StpToJTHandler {
	
	private static final Logger log = LoggerFactory.getLogger(StpToJTHandler.class);
	
	private static Properties props = null;

	private static Properties tcProps = null;	
	
	private static String stepToJTExe = null;
	
	private static String stepToJTConfig = null;
	
	private static String previewServicePath = null;
	
	private static String OWNER = null;
	
	static {
		System.out.println("configurating log4j with log4j.properties");
		System.out.println(CommonTools.getPath(StpToJTHandler.class));
		props = CommonTools.getProperties(CommonTools.getPath(StpToJTHandler.class) + File.separator + "ConstantParams.properties");
		tcProps = CommonTools.getProperties(props.getProperty("tc_properties"));
		stepToJTExe = props.getProperty("step_to_jt_exe");
		stepToJTConfig = props.getProperty("step_to_jt_config");
		previewServicePath = props.getProperty("previewservicePath");
	}
	
	public static void main(String[] args) throws Exception {
		log.info("========== StpToJTHandler start ==========");
//		args = new String[] { "-taskUid=gHpR0th$4VtjAC" };
		if (null == args || args.length <= 0) {
			return;
		}
		
		String taskUid = "";
		for (String arg : args) {
			System.out.println(arg);
			if (arg.startsWith("-taskUid")) {
				taskUid = arg.split("=")[1].trim();
				System.out.println("==>> taskUid: " + taskUid);
			}
		}
		
		if (CommonTools.isEmpty(taskUid)) {
			throw new Exception("【ERROR】 获取输入参数taskUid发生错误！");
		}
		
		String processName = null; // 流程名称
		ItemRevision stpItemRev = null;
		String stpItemId = null; // layout对象版本ID
		String stpVersion = null; // layout对象版本号
		String absoluteStpFilePath = null; // stp文件的绝对路径
		String stpFileName = null; // stp文件名称
		String email = null; // 对象版本所有者
		String to = props.getProperty("to_userId"); // 邮件接受者
		String bodymsg = null; // 邮件正文内容
		List<String> springUrlList = null;
		List<String> stpToJtList = null;
		ITCSOAClientConfig tcSOAClientConfig = new TCSOAClientConfigImpl(tcProps.getProperty("TC_IP"), tcProps.getProperty("TC_USERNAME"), tcProps.getProperty("TC_PASSWORD")); // 登录TC系统		
		TCSOAServiceFactory tcsoaServiceFactory = new TCSOAServiceFactory(tcSOAClientConfig);
		
		DataManagementService dmService = tcsoaServiceFactory.getDataManagementService();	
		SavedQueryService savedQueryService = tcsoaServiceFactory.getSavedQueryService();
		StructureManagementService smService = tcsoaServiceFactory.getStructureManagementService(); 
		StructureService strucService = tcsoaServiceFactory.getStructureService();
		SessionService sessionService= tcsoaServiceFactory.getSessionService();
		PreferenceManagementService pfService = tcsoaServiceFactory.getPreferenceManagementService();
		
		try {			
			TCPublicUtils.byPass(tcsoaServiceFactory.getSessionService(), true); // 开启旁路
			ModelObject object = TCPublicUtils.findObjectByUID(dmService, taskUid); // 获取任务对象
			if (CommonTools.isEmpty(object)) {
				throw new Exception("【ERROR】 获取流程对象发生错误！");
			}
						
			EPMTask rootTask = EPMTaskService.getEPMTask(dmService, object); // 获取流程根任务
			processName = EPMTaskService.getProcessName(dmService, rootTask); // 获取流程名称
			log.info("【INFO】 流程名称为: " + processName);			
			
			springUrlList = TCPublicUtils.getTCPreferences(pfService, TCPreferenceConstant.D9_SPRINGCLOUD_URL);
			if (CommonTools.isEmpty(springUrlList)) {
				throw new Exception("【ERROR】 TC首选项: " + TCPreferenceConstant.D9_SPRINGCLOUD_URL + ", 不存在");
			}
			
			stpToJtList = TCPublicUtils.getTCPreferences(pfService,  TCPreferenceConstant.D9_ITEM_STEPTOJT_TYPE);
			if (CommonTools.isEmpty(springUrlList)) {
				throw new Exception("【ERROR】 TC首选项: " + TCPreferenceConstant.D9_ITEM_STEPTOJT_TYPE + ", 不存在");
			}
			
			
			ModelObject[] rootTargetAttachments = EPMTaskService.getRootTargetAttachments(dmService, rootTask); // 获取目标对象数组
			if (CommonTools.isEmpty(rootTargetAttachments)) {
				throw new Exception("【ERROR】 流程名称为: " + processName + ", 目标对象数组为空！");
			}
			
			
			Map<ModelObject, String> modelObjectTypeMapp = TCPublicUtils.getModelObjectTypeMapp(rootTargetAttachments); // 获取对象/对象类型映射
			if (CommonTools.isEmpty(modelObjectTypeMapp)) {
				throw new Exception("【ERROR】 流程名称为: " + processName + ", 获取目标对象数组类型失败！");
			}
						
						
			stpItemRev = getStpItemRev(modelObjectTypeMapp, stpToJtList);
			if (CommonTools.isEmpty(stpItemRev)) {
				throw new Exception("【ERROR】流程名称为:" + processName + ", 目标对象数组的对象版本类型不符合本次操作要求，请重新选择");
			}		
			
			
			OWNER = TCPublicUtils.getOwnUser(dmService, stpItemRev);
			
			stpItemId = TCPublicUtils.getPropStr(dmService, stpItemRev, ItemRevPropConstant.ITEM_ID);
			log.info("【INFO】 stpItemId:" + stpItemId);
			stpVersion = TCPublicUtils.getPropStr(dmService, stpItemRev, ItemRevPropConstant.ITEM_REVISION_ID);
			log.info("【INFO】stpVersion:" + stpVersion);	
			
//			String actualUser = TCPublicUtils.getActualUser(tcsoaServiceFactory.getDataManagementService(), stpItemRev);	
			String actualUser = null;
			if (CommonTools.isNotEmpty(actualUser)) {
				List<SPASUser> spasEmail = TCPublicUtils.getSpasEmail(springUrlList.get(0), ConvertConstant.GETTEAMROSTERBYEMPID, actualUser.split("\\|")[0]);
				email = spasEmail.get(0).getNotes();
			} else {
				Map<String, String> emailMap = TCPublicUtils.getEmail(savedQueryService, dmService, Arrays.asList(OWNER));
				if (CommonTools.isEmpty(emailMap)) {
					log.warn("【WARN】 流程名称为: " + processName + ", 获取" +"零组件ID为: "+ stpItemId + ", 版本号为: " + stpVersion +", 所有者邮箱失败...");					
				} else {
					email = TCMail.generateToUser(emailMap);
				}			
								
			}			
			
			to = email == null ? "" + to : email + to;
			
			List<String> fmsUrlTCPreferences = TCPublicUtils.getTCPreferences(pfService, TCPreferenceConstant.FMS_BOOTSTRAP_URLS);
			if (CommonTools.isEmpty(fmsUrlTCPreferences)) {
				throw new Exception("【ERROR】 TC首选项: " + TCPreferenceConstant.FMS_BOOTSTRAP_URLS + ", 不存在");
			}
			String fmsUrl = fmsUrlTCPreferences.get(0);
			log.info("【INFO】 fmsUrl: " + fmsUrl);
			
			FileManagementUtility fmUtility = tcsoaServiceFactory.getFileManagementUtility(fmsUrl);
			
			String dir = CommonTools.getFilePath(ConvertConstant.STPFOLDER); // 查找保存Placement文件的文件夹
			log.info("【INFO】 filePath: " + dir);			
			
			CommonTools.deletefile(dir); // 删除文件夹下面的所有文件
			
			ModelObject[] imanSpecification = TCPublicUtils.getPropModelObjectArray(dmService, stpItemRev, ItemRevPropConstant.IMAN_SPECIFICATION);
			if (CommonTools.isNotEmpty(imanSpecification)) {
				Map<Dataset, String> datasetMap = DatasetService.getDataset(dmService, imanSpecification);
				if (CommonTools.isEmpty(datasetMap)) {
					throw new Exception("【ERROR】 流程名称为: " + processName + ", 获取Stp数据集失败...");
				}
				
				absoluteStpFilePath = getDatasetFile(dmService,fmUtility, tcSOAClientConfig.getConnection(), 
						datasetMap, dir); // 下载stp文件，返回文件的绝对路径
				if (CommonTools.isEmpty(absoluteStpFilePath)) {
					throw new Exception("【ERROR】 流程名称为: " + processName + ", 下载stp数据集文件失败...");
				}
				stpFileName = absoluteStpFilePath.substring(absoluteStpFilePath.lastIndexOf(File.separator) + 1);
				log.info("【INFO】 Stp文件的绝对路径为: " + absoluteStpFilePath);
			}
			
			handlerStpToJT(dir); // 执行stp转jt操作
			
			List<String> fileLists = CommonTools.getAllFiles(dir, null);
			String jtFilePath = getFilePath(fileLists, true, false);
			if (CommonTools.isEmpty(jtFilePath)) {
				throw new Exception("【ERROR】 流程名称为: " + processName + ", stp文件转jt文件失败...");
			}
			
			generateJTDataset(dmService, fmUtility, processName, stpItemRev, jtFilePath, stpFileName);
			
			handlerJTToJpg(dir, jtFilePath); // 执行jt转jpg
			
			fileLists = CommonTools.getAllFiles(dir, null);
			String jpgFilePath = getFilePath(fileLists, false, true);
			if (CommonTools.isEmpty(jpgFilePath)) {
				throw new Exception("【ERROR】 流程名称为: " + processName + ", stp文件转jt文件失败...");
			}
			
			generateImageDataset(dmService, fmUtility, processName, stpItemRev, jpgFilePath, stpFileName); // 添加Image数据集
			
			bodymsg = "<html><head></head><body><h3 style=\"font-family: 宋体;  font-size:15px;\">" + "流程為: " + processName + ", 圖號為: "
					+ stpItemId + ", 版本號為: " + stpVersion + ", Stp转JT完成，請登錄TC進行查看，謝謝！！"
					+ "</h3></body></html>";
		} catch (Exception e) {
			e.printStackTrace();
			bodymsg = "<html><head></head><body><h3 style=\"font-family: 宋体;  font-size:15px;\">" + "流程為: " + processName + ", 圖號為: "
					+ stpItemId + ", 版本號為: " + stpVersion + ", Stp转JT存在错误完成，請登錄TC進行查看，謝謝！！"
					+ "</h3></body></html>";
		} finally {
			if (CommonTools.isNotEmpty(bodymsg)) {				
				HashMap<String, String> httpmap = new HashMap<String, String>();
				String requestPath = springUrlList.get(0);
				httpmap.put("requestPath", requestPath);
				httpmap.put("ruleName", "/tc-mail/teamcenter/sendMail3");
				httpmap.put("sendTo", to);
				httpmap.put("subject", props.getProperty("subject"));
				httpmap.put("htmlmsg", bodymsg);
				String result = TCMail.sendMail(httpmap);
				log.info("【INFO】 邮件发送结果: " + result);
			}
			
			TCPublicUtils.byPass(tcsoaServiceFactory.getSessionService(), false); // 关闭旁路
			tcSOAClientConfig.destroy(); // 登出系统
			log.info("========== StpToJTHandler end ==========");
		}
	}
	
	
	/**
	 * 获取stp对象版本
	 * @param objs
	 * @return
	 */
	private static ItemRevision getStpItemRev(Map<ModelObject, String> targetMap, List<String> stpToJtList) {
		ItemRevision itemRev = null;
		boolean flag = false;
		for (String str : stpToJtList) {
			log.info("==>> 首选项的值为: " + str);
			for (Map.Entry<ModelObject, String> entry : targetMap.entrySet()) {
				String value = entry.getValue();
				if (str.trim().equals(value)) {
					itemRev = (ItemRevision) entry.getKey();
					flag = true;
					break;
				}
			}
			if (flag) {
				break;
			}
		}
		return itemRev;
	}
	
	
	/**
	 * 下载数据集
	 * @param dmService
	 * @param fmUtility
	 * @param map
	 * @param dir
	 * @return
	 * @throws NotLoadedException 
	 */
	private static String getDatasetFile(DataManagementService dmService, FileManagementUtility fmUtility, Connection connection, Map<Dataset, String> map, String dir) throws NotLoadedException {
		String absoluteFilePath = null;
		String ip = null;
		String fileVersionId = null;
		String url = null;
		for (Map.Entry<Dataset, String> entry : map.entrySet()) {
			boolean isStp = false;
			boolean isStpLink = false;
			String objectName = entry.getValue();
			Dataset dataset = entry.getKey();
			String objectType = dataset.getTypeObject().getName();
			String fileExtensions = null;
			if (DatasetEnum.STP.type().equals(objectType) && objectName.contains(DatasetEnum.STP.fileExtensions())) {
				fileExtensions = DatasetEnum.STP.fileExtensions();
				isStp = true;
			}
			
			if (DatasetEnum.STPLINK.type().equals(objectType) && objectName.contains(DatasetEnum.STPLINK.fileExtensions())) { // 数据集类型为HTML的超链接，文件名含有
				fileExtensions = DatasetEnum.STPLINK.fileExtensions();
				isStpLink = true;
			}
			if (CommonTools.isEmpty(fileExtensions)) {
				continue;
			}
			
			log.info("【INFO】 数据集名称为:  " + objectName + ", 正在下载...");
			log.info("【INFO】 数据集类型为:  " + objectType);			
			
			if (isStp) {
				absoluteFilePath = DatasetService.downloadDataset(dataset, dmService, fmUtility, fileExtensions, dir); // 下载数据集
			} else if (isStpLink) {
				if (CommonTools.isEmpty(ip)) {
					ip = TCPublicUtils.getFileServerIp(connection); // 获取文件系统ip
				}				
				fileVersionId = TCPublicUtils.getPropStr(dmService, dataset, DatasetPropConstant.OBJECT_DESC);
				url = "http://" + ip + ":8019/downloadFile?fileVersionId=" + fileVersionId;
				absoluteFilePath = DatasetService.downloadDatasetByUrl(dmService, dataset, dir, objectName, url);
			}
			
			if (CommonTools.isEmpty(absoluteFilePath)) {
				log.info("【ERROR】 数据集名称为: " + objectName + ", 下载失败啦！");
			} else {
				log.info("【INFO】 数据集名称为: " + objectName + ", 下载成功...");
			}
		}
		return absoluteFilePath;
	}
	
	
	/**
	 * 执行stp转jt操作
	 * @param stpFilePath
	 * @param dir
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void handlerStpToJT(String dir) throws IOException, InterruptedException {
		log.info("【INFO】 dir is: " + dir);
		
		String command = stepToJTExe + " -z " + stepToJTConfig + " -d " + dir + " -o " + dir;
		log.info("【INFO】 command: " + command);
		CommonTools.doCallBat(command);
	}

	
	
	/**
	 * 执行jt转jpg
	 * @param dir
	 * @param jtFilePath
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void handlerJTToJpg(String dir, String jtFilePath) throws IOException, InterruptedException {
		log.info("【INFO】 dir is: " + dir);
		log.info("【INFO】 jtFilePath is: " + jtFilePath);
		
		String command = previewServicePath + " " + dir + " " + dir + " JPG 500x500px -combine -f " + jtFilePath;
		log.info("【INFO】 command: " + command);
		CommonTools.doCallBat(command);
	}
	
	
	/**
	 * 生成JT数据集
	 * @param dmService
	 * @param fmUtility
	 * @param processName
	 * @param itemRev
	 * @param jtFilePath
	 * @param stpFileName
	 * @throws Exception
	 */
	private static void generateJTDataset(DataManagementService dmService, FileManagementUtility fmUtility, String processName, ItemRevision itemRev, String jtFilePath, String stpFileName) throws Exception {
		log.info("【INFO】 jtFilePath: " + jtFilePath);
		ModelObject[] imanRendering = TCPublicUtils.getPropModelObjectArray(dmService, itemRev, ItemRevPropConstant.IMAN_RENDERING);
		String JTDsName = stpFileName.replace(DatasetEnum.STP.fileExtensions(), DatasetEnum.JT.fileExtensions());
		Map<Dataset, String> datasetMap = DatasetService.getDataset(dmService, imanRendering);
		Dataset JTDataset = checkDataset(dmService, datasetMap, JTDsName, true, false);
		if (CommonTools.isEmpty(JTDataset)) {
			JTDataset = DatasetService.createDataset(dmService, itemRev, JTDsName, DatasetEnum.JT.type(), DatasetEnum.JT.relationType());
			if (CommonTools.isEmpty(JTDataset)) {
				log.info("【ERROR】 流程名称为: " + processName + ", 生成JT数据集失败...");
				throw new Exception("【ERROR】 流程名称为: " + processName + ",  生成JT数据集失败...");
			}
		} else {
			DatasetService.removeFileFromDataset(dmService, JTDataset, DatasetEnum.JT.refName());
		}
		
		Boolean check = DatasetService.addDatasetFile(fmUtility, dmService, JTDataset, jtFilePath, DatasetEnum.JT.refName(), false);
		if (!check) {
			throw new Exception("【ERROR】 流程名称为: " + processName + ", JT数据集添加附件失败...");
		}
	}
	
	
	
	private static void generateImageDataset(DataManagementService dmService, FileManagementUtility fmUtity, String processName, ItemRevision itemRev, String imageFilePath, String stpFileName) throws Exception {
		log.info("【INFO】 imageFilePath: " + imageFilePath);
		ModelObject[] imanSpec = TCPublicUtils.getPropModelObjectArray(dmService, itemRev, ItemRevPropConstant.IMAN_SPECIFICATION);
		String imageDsName = getImageFileName(imageFilePath, stpFileName);
		if (CommonTools.isEmpty(imageDsName)) {
			log.info("【ERROR】 流程名称为: " + processName + ", 生成图像数据集名称失败...");
			throw new Exception("【ERROR】 流程名称为: " + processName + ", 生成图像数据集名称失败...");
		}
		
		Map<Dataset, String> datasetMap = DatasetService.getDataset(dmService, imanSpec);
		Dataset imageDataset = checkDataset(dmService, datasetMap, imageDsName, false, true);
		if (CommonTools.isEmpty(imageDataset)) {
			imageDataset = DatasetService.createDataset(dmService, itemRev, imageDsName, DatasetEnum.Image.type(), DatasetEnum.Image.relationType());
			if (CommonTools.isEmpty(imageDataset)) {
				log.info("【ERROR】 流程名称为: " + processName + ", 生成Image数据集失败...");
				throw new Exception("【ERROR】 流程名称为: " + processName + ",  生成Image数据集失败...");
			}
		} else {
			DatasetService.removeFileFromDataset(dmService, imageDataset, DatasetEnum.Image.refName());
		}
		
		Boolean check = DatasetService.addDatasetFile(fmUtity, dmService, imageDataset, imageFilePath, DatasetEnum.Image.refName(), false);
		if (!check) {
			throw new Exception("【ERROR】 流程名称为: " + processName + ", Image数据集添加附件失败...");
		}
	}
	
	
	/**
	 * 获取图像数据集名称
	 * @param imageFilePath
	 * @param stpFileName
	 * @return
	 */
	private static String getImageFileName(String imageFilePath, String stpFileName) {
		log.info("==>> imageFilePath: " + imageFilePath);
		log.info("==>> stpFileName: " + stpFileName);
		String imageDsName = null;
		String fileExtensions = DatasetEnum.Image.fileExtensions();		
		log.info("==>> fileExtensions: " + fileExtensions);
		String[] split = fileExtensions.split("\\|");
		for (String str : split) {
			if (imageFilePath.contains(str)) {
				imageDsName = stpFileName.replace(DatasetEnum.STP.fileExtensions(), str);
				break;
			}
		}
		
		return imageDsName;
	}
	
	
	/**
	 * 校验JT/Image数据集是否存在
	 * @param dmService
	 * @param map
	 * @param dsName
	 * @return
	 * @throws NotLoadedException
	 */
	private static Dataset checkDataset(DataManagementService dmService, Map<Dataset, String> map, String dsName, boolean jt, boolean image) throws NotLoadedException {
		if (CommonTools.isEmpty(map)) {
			return null;
		}
		
		Dataset resultDataset = null;
		for (Map.Entry<Dataset, String> entry : map.entrySet()) {
			Dataset dataset = entry.getKey();
			String objectType = dataset.getTypeObject().getName();
			String objectName = TCPublicUtils.getPropStr(dmService, dataset, DatasetPropConstant.OBJECT_NAME);
			if (DatasetEnum.JT.type().equalsIgnoreCase(objectType) && dsName.equalsIgnoreCase(objectName) && jt) {
				resultDataset = dataset;
				break;
			} else if (DatasetEnum.Image.type().equalsIgnoreCase(objectType) && dsName.equalsIgnoreCase(objectName) && image) {
				resultDataset = dataset;
				break;
			}
		}
		return resultDataset;
	}
	
	
	
	
	/**
	 * 获取转换好的JT/Image文件
	 * @param list
	 * @return
	 */
 	private static String getFilePath(List<String> list, boolean jt, boolean image) {
 		if (jt) {
 			Optional<String> findAny = list.stream().filter(str -> str.contains(DatasetEnum.JT.fileExtensions())).findAny();
 			if (findAny.isPresent()) {
 				return findAny.get();
 			}
		} else if (image) {
			Optional<String> findAny = list.stream().filter(str -> {
				String fileExtensions = DatasetEnum.Image.fileExtensions();		
				String[] split = fileExtensions.split("\\|");
				for (int i = 0; i < split.length; i++) {
					if (str.contains(split[i])) {
						return true;
					}
				}				
				return false;
			}).findAny();
			
			if (findAny.isPresent()) {
				return findAny.get();
			}
		}
		
		return null;
	}
	
	
}
