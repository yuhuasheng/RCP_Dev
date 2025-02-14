package com.foxconn.electronics.managementebom.createDCN;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.foxconn.electronics.domain.Constants;
import com.foxconn.electronics.util.CommonTools;
import com.foxconn.electronics.util.TCUtil;
import com.foxconn.electronics.util.Util;
import com.foxconn.tcutils.util.AjaxResult;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProcessType;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentTaskTemplateType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core._2006_03.DataManagement.CreateItemsResponse;
import com.teamcenter.services.strong.core._2006_03.DataManagement.ExtendedAttributes;
import com.teamcenter.services.strong.core._2006_03.DataManagement.ItemProperties;
import com.teamcenter.services.strong.core._2013_05.DataManagement.GenerateNextValuesIn;
import com.teamcenter.services.strong.core._2013_05.DataManagement.GenerateNextValuesResponse;
import com.teamcenter.services.strong.core._2013_05.DataManagement.GeneratedValue;
import com.teamcenter.services.strong.core._2013_05.DataManagement.GeneratedValuesOutput;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.Type;
import com.teamcenter.soa.client.model.strong.Item;

import cn.hutool.core.io.FileUtil;
/**
 * CMHasProblemItem 问题项
 * CMHasImpactedItem 受影响项
 * CMHasSolutionItem 解决方案项
 * @author Oz
 *
 */
@RequestMapping("/electronics")
public class DCNController {
	private TCComponentBOMLine rootLine;

	public DCNController(TCComponentBOMWindow bomWindow) throws TCException {
		this.rootLine = bomWindow.getTopBOMLine();
	}

	@SuppressWarnings("deprecation")
	@GetMapping("/createDCN")
	public AjaxResult createDCN() {
		try {
			TCUtil.setBypass(TCUtil.getTCSession());
			List<TCComponentItemRevision> solutionList = new ArrayList<>();
			List<TCComponentItemRevision> impactList = new ArrayList<>();
			getItemRevisionList(rootLine, solutionList, impactList);
//			if(true) {
//				return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,"服務器繁忙，請稍後重試");
//			}
			if (solutionList.size() == 0) {
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR,"沒有變更，無法創建DCN");
			}
			
			String rootItem = rootLine.getItemRevision().getProperty("item_id");
			System.out.println(rootItem);
			boolean isRootExsitChange = solutionList.stream().filter(e->{
				try {
					System.out.println(e.getProperty("item_id"));
					return e.getProperty("item_id").equals(rootItem);
				} catch (TCException e1) {
					e1.printStackTrace();
				}
				return false;
			}).findAny().isPresent();
			
			boolean firsRev = isFirstRev(rootLine) && isRootExsitChange;
			TCComponentItemRevision itemRevision = rootLine.getItemRevision();
			String dept = null;
			String projectId = "";
			// dept = "MNT";
			try {
				projectId = itemRevision.getItem().getProperty("project_ids").split(",")[0];
				TCComponent[] executeQuery = TCUtil.executeQuery(TCUtil.getTCSession(), "__D9_Find_Project_Folder",new String[] { "d9_SPAS_ID" }, new String[] { projectId });
				TCComponentFolder tcFolder = (TCComponentFolder) executeQuery[0];
				TCComponentFolder seriseFolder =  (TCComponentFolder) tcFolder.whereReferenced()[0].getComponent();
				dept = seriseFolder.getProperty("object_desc");
				if (!"MNT".equals(dept) && !"PRT".equals(dept)) {
					return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR,"未指派項目，請先指派項目");
				}
			} catch (Exception e) {
				e.printStackTrace();
				return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR,"未指派項目，請先指派項目");
			}
			itemRevision.refresh();
			AIFComponentContext[] whereReferenced = itemRevision.whereReferenced();
			for (AIFComponentContext aifComponentContext : whereReferenced) {
				InterfaceAIFComponent component = aifComponentContext.getComponent();
				if (component instanceof TCComponentItemRevision) {
					TCComponentItemRevision revision = (TCComponentItemRevision) component;
					revision = revision.getItem().getLatestItemRevision();
					Type typeObject = revision.getTypeObject();
					String className = typeObject.getClassName();
					if (("D9_" + dept + "_DCNRevision").equals(className)) {
						String property2 = revision.getProperty("sequence_id");
						System.out.println(property2);
						if (!TCUtil.isReleased(revision)) {
							String property = revision.getProperty("object_string");
							return AjaxResult.error("請先發行  " + property, "");
						}
					}
				}
			}
			// try{
			// checkColumn(rootLine);
			// }catch (Exception e) {
			// return AjaxResult.error(e.getMessage());
			// }
			String tcObjTypeString = dept.equals("MNT") ? "D9_MNT_DCN" : "D9_PRT_DCN";
			TCUtil.setBypass(TCUtil.getTCSession());
			HashMap<String, Object> revisionMap = new HashMap<String, Object>();
			String pcaNum = rootLine.getItemRevision().getProperty("object_name");
			String name = pcaNum + (firsRev ? "初版發行" : "變更發行");
			revisionMap.put("object_name", name);
			revisionMap.put("object_desc", name);
			revisionMap.put("CreateInput", "D9_MNT_DCNCreI");
			TCComponentItem dcnItem = (TCComponentItem) TCUtil.createCom(TCUtil.getTCSession(), tcObjTypeString, "",
						revisionMap);
			String dcnNo = dcnItem.getProperty("item_id");
			TCComponentForm form = null;
			TCComponentItemRevision dcnItemRevision = DCNService.getFirstRevision(dcnItem);
			TCComponent[] relatedComponents = dcnItemRevision.getRelatedComponents("IMAN_specification");
			if (CommonTools.isNotEmpty(relatedComponents)) {
				for (TCComponent tcComponent : relatedComponents) {
					if (!(tcComponent instanceof TCComponentForm)) {
						continue;
					}
					form = (TCComponentForm) tcComponent;
					if (!"D9_MNT_DCNForm".equals(form.getTypeObject().getName())) {
						continue;
					}
					break;
				}
			}
			String errorLog = "";
			for (TCComponentItemRevision echoItemRevision : solutionList) {
				try {
					dcnItemRevision.add("CMHasSolutionItem", echoItemRevision);
				} catch (Exception e) {
					String msg = "";
					String dcn = findDCN(echoItemRevision);
					if(dcn==null) {
						msg = e.getMessage();
					}else {
						String item = echoItemRevision.getProperty("object_string");
						msg = item +"在"+dcn+"的解决方案项中，该物件处于未发布状态，请先发行";
					}
					errorLog += msg + "\n";
					removeImpact(impactList,echoItemRevision);
				}
			}
			for (TCComponentItemRevision echoItemRevision : impactList) {
				try {
					dcnItemRevision.add("CMHasProblemItem", echoItemRevision);
				} catch (Exception e) {
					e.printStackTrace();
					return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,echoItemRevision.getProperty("object_string") + "无法添加到问题项，它可能与其他DCN冲突。");
				}
			}
			
			if(!errorLog.isEmpty()) {
				File tempFile = FileUtil.createTempFile();
				FileUtil.writeString(errorLog, tempFile,StandardCharsets.UTF_8);
				String absolutePath = tempFile.getAbsolutePath();
				System.out.println("創建DCN有錯誤："+absolutePath);
//				TCComponentDataset createDataSet = TCUtil.createDataSet(TCUtil.getTCSession(), absolutePath, "Text", "ErrorLog.txt", "Text");				
//				dcnItemRevision.add("IMAN_specification", createDataSet);
			}
			
			TCComponent[] project = TCUtil.executeQuery(TCUtil.getTCSession(), "__D9_Find_Project",
					new String[] { "project_id" }, new String[] { projectId });
			TCUtil.assignedProject(TCUtil.getTCSession(), dcnItem, project[0]);
			
			TCComponent[] queryReslut = TCUtil.executeQuery(TCUtil.getTCSession(), Constants.FIND_PROJECT_FOLDER, 
					new String[] {Constants.SPAS_ID}, new String[] { projectId });			
			TCComponentFolder dcnFolder = TCUtil.getProjectFolderByFolderNameAndCategory(
					(TCComponentFolder)queryReslut[0], "D9_WorkAreaFolder", "DCN", 2);
			String projectFolderName = ((TCComponentFolder)queryReslut[0]).getProperty("object_name");
			if (CommonTools.isNotEmpty(form)) {
				form.setProperty("d9_ModelName",projectFolderName);
			}
			String folderName = "";
			if (dcnFolder != null) {
				folderName = dcnFolder.getProperty("object_name");
				dcnFolder.add("contents", dcnItem);
				dcnFolder.refresh();
			} else {
				TCComponentFolder newStuffFolder = TCUtil.getTCSession().getUser().getNewStuffFolder();
				folderName = newStuffFolder.getProperty("object_name");
				newStuffFolder.add("contents", dcnItem);
				newStuffFolder.refresh();
			}
			
			return AjaxResult.success("( " + dcnNo + "|" + name + " )已保存到" + projectFolderName + "的" + folderName + "文件夹下");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			TCUtil.closeBypass(TCUtil.getTCSession());
		}
		return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR,"服務器繁忙，請稍後重試");
	}
	
	public static String findDCN(TCComponentItemRevision itemRevision) throws TCException {
		AIFComponentContext[] whereReferenced = itemRevision.whereReferenced();
		for (AIFComponentContext aifComponentContext : whereReferenced) {
			InterfaceAIFComponent component = aifComponentContext.getComponent();
			if (component instanceof TCComponentItemRevision) {
				TCComponentItemRevision revision = (TCComponentItemRevision) component;
				revision = revision.getItem().getLatestItemRevision();
				Type typeObject = revision.getTypeObject();
				String className = typeObject.getClassName();
//				if (("D9_" + dept + "_DCNRevision").equals(className)) {
				if (className.endsWith("DCNRevision")) {
					return revision.getProperty("object_string");
				}
			}
		}
		return null;
	}
	
	public static void removeImpact(List<TCComponentItemRevision> impactList,TCComponentItemRevision sulutionRevision) throws TCException {
		for(int i=0;i<impactList.size();i++) {
			TCComponentItemRevision impactRevision = impactList.get(i);
			String itemId = impactRevision.getProperty("item_id");
			String property = sulutionRevision.getProperty("item_id");
			if(itemId.equals(property)) {
				impactList.remove(i);
				break;
			}
		}
	}

	public static void getItemRevisionList(TCComponentBOMLine bomLine, List<TCComponentItemRevision> solutionList,List<TCComponentItemRevision> impactList) throws TCException {
		TCComponentItemRevision itemRevision = bomLine.getItemRevision();
		
		boolean isExsit = solutionList.stream().filter(e -> {
			return e.equals(itemRevision);
		}).findAny().isPresent();
		
		if (isExsit) {
			return;
		}
		if (!TCUtil.isReleased(itemRevision)) {
			solutionList.add(itemRevision);
			TCComponentItemRevision previousRevision = DCNService.getPreviousRevision(itemRevision);
			if (previousRevision != null) {
				impactList.add(previousRevision);
			}
		}
		if (bomLine.hasSubstitutes()) {
			TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();
			for (TCComponentBOMLine tcComponentBOMLine : listSubstitutes) {
				getItemRevisionList(tcComponentBOMLine, solutionList, impactList);
			}
		}
		AIFComponentContext[] children = bomLine.getChildren();
		for (int i = 0; i < children.length; i++) {
			TCComponentBOMLine subBomLine = (TCComponentBOMLine) children[i].getComponent();
			getItemRevisionList(subBomLine, solutionList, impactList);
		}
	}

	private void checkColumn(TCComponentBOMLine bomLine) throws Exception {
		TCComponentItemRevision itemRevision = bomLine.getItemRevision();
		String mg = itemRevision.getProperty("d9_MaterialGroup");
		String mt = itemRevision.getProperty("d9_MaterialType");
		String un = itemRevision.getProperty("d9_Un");
		String pm = itemRevision.getProperty("d9_ProcurementMethods");
		if (Util.isEmpty(mg) || Util.isEmpty(mt) || Util.isEmpty(un) || Util.isEmpty(pm)) {
			throw new Exception(itemRevision.getProperty("item_id") + " 有必填欄位未填寫，請填寫后重試");
		}
		if (bomLine.hasSubstitutes()) {
			TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();
			for (TCComponentBOMLine tcComponentBOMLine : listSubstitutes) {
				checkColumn(tcComponentBOMLine);
			}
		}
		AIFComponentContext[] children = bomLine.getChildren();
		for (int i = 0; i < children.length; i++) {
			TCComponentBOMLine subBomLine = (TCComponentBOMLine) children[i].getComponent();
			checkColumn(subBomLine);
		}
	}

	private List<TCComponentItemRevision> getAllItemRevision(TCComponentBOMLine bomLine) throws TCException {
		List<TCComponentItemRevision> list = new ArrayList<>();
		if (!TCUtil.isReleased(bomLine.getItemRevision())) {
			list.add(bomLine.getItemRevision());
		}
		AIFComponentContext[] children = bomLine.getChildren();
		for (int i = 0; i < children.length; i++) {
			TCComponentBOMLine subBomLine = (TCComponentBOMLine) children[i].getComponent();
			list.addAll(getAllItemRevision(subBomLine));
		}
		return list;
	}

	public static boolean isFirstRev(TCComponentBOMLine rootLine) throws TCException {
		TCComponent[] revions = rootLine.getItem().getRelatedComponents("revision_list");
		if (revions.length == 1) {
			return true;
		}
		TCComponentItemRevision previousRevision = DCNService.getPreviousRevision(rootLine.getItemRevision());
		TCComponentBOMLine topBomLine = TCUtil.openBomWindow(TCUtil.getTCSession(), previousRevision);
		return topBomLine.getChildrenCount() == 0;
	}

	/**
	 * 解包全部BOMLine
	 * 
	 * @param designBOMTopLine
	 * @throws Exception
	 */
	private void allBOMLineUnpackage(TCComponentBOMLine designBOMTopLine) throws Exception {
		AIFComponentContext[] children = designBOMTopLine.getChildren();
		for (int i = 0; i < children.length; i++) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
			if (bomLine.isPacked()) {
				bomLine.unpack();
			}
			allBOMLineUnpackage(bomLine);
		}
	}

	public static Item createItems(TCSession session, String itemId, String itemType, String itemName,
			Map<String, String> propMap) throws ServiceException {
		DataManagementService dmService = DataManagementService.getService(session.getSoaConnection());
		ItemProperties[] itemProps = new ItemProperties[1];
		ItemProperties itemProperty = new ItemProperties();
		itemProperty.clientId = itemId + "--" + getRandomNumber();
		itemProperty.itemId = itemId;
		itemProperty.revId = "01";
		itemProperty.name = itemName;
		itemProperty.type = itemType;
		itemProperty.description = "";
		itemProperty.uom = "";
		itemProperty.extendedAttributes = new ExtendedAttributes[1];
		ExtendedAttributes theExtendedAttr = new ExtendedAttributes();
		theExtendedAttr.attributes = propMap;
		theExtendedAttr.objectType = itemType;
		itemProperty.extendedAttributes[0] = theExtendedAttr;
		itemProps[0] = itemProperty;
		CreateItemsResponse response = dmService.createItems(itemProps, null, "");
		ServiceData serviceData = response.serviceData;
		if (serviceData.sizeOfPartialErrors() > 0) {
			throw new ServiceException(serviceData.getPartialError(0).toString());
		}
		return response.output[0].item;
	}

	public static String getRandomNumber() {
		String str = "ABCDEFJHIJKLMNOPQRSTUVWXYZ0123456789";
		String uuid = new String();
		for (int i = 0; i < 4; i++) {
			char ch = str.charAt(new Random().nextInt(str.length()));
			uuid += ch;
		}
		return uuid;
	}

	/**
	 * 调用示例： generateIdBak(TCUtil.getTCSession(),"DCN-","D9_MNT_DCN");
	 * 
	 * @param session
	 * @param ruleMapping
	 * @param type
	 * @return
	 */
	public static String generateIdBak(TCSession session, String ruleMapping, String type) {
		String id = "";
		DataManagementService dataManagementService = DataManagementService.getService(session.getSoaConnection());
		GenerateNextValuesIn[] ins = new GenerateNextValuesIn[1];
		GenerateNextValuesIn in = new GenerateNextValuesIn();
		ins[0] = in;
		in.businessObjectName = type;
		in.clientId = "AutoAssignRAC";
		in.operationType = 1;
		Map<String, String> map = new HashMap<String, String>();
		String prefix = "&quot;";
		String suffix = "-&quot;NNNNN";
		String mode = prefix + ruleMapping + suffix;
		System.out.println("==>> mode: " + mode);
		map.put("item_id", mode);
		in.propertyNameWithSelectedPattern = map;
		GenerateNextValuesResponse response = dataManagementService.generateNextValues(ins);
		GeneratedValuesOutput[] outputs = response.generatedValues;
		for (GeneratedValuesOutput result : outputs) {
			Map<String, GeneratedValue> resultMap = result.generatedValues;
			GeneratedValue generatedValue = resultMap.get("item_id");
			id = generatedValue.nextValue;
			System.out.println("==>> result " + id);
		}
		return id;
	}

	private void createWorkflow(TCComponentItemRevision[] itemRevisons, String taskTemplate) throws Exception {
		TCComponentTaskTemplateType taskTemplateType = (TCComponentTaskTemplateType) TCUtil.getTCSession()
				.getTypeComponent("EPMTaskTemplate");
		TCComponentTaskTemplate[] taskTemplates = taskTemplateType.getProcessTemplates(false, false,
				(TCComponent[]) null, (String[]) null, (String) null);
		TCComponentTaskTemplate tCComponentTaskTemplate = null;
		for (TCComponentTaskTemplate t : taskTemplates) {
			System.out.println(t.getName());
			if (taskTemplate.equalsIgnoreCase(t.getName())) {
				tCComponentTaskTemplate = t;
				break;
			}
		}
		TCComponentProcessType localTCComponentProcessType = (TCComponentProcessType) TCUtil.getTCSession()
				.getTypeComponent("Job");
		int[] var7 = new int[itemRevisons.length];
		Arrays.fill(var7, 1);
		localTCComponentProcessType.create("" + new Date().getTime(), taskTemplate, tCComponentTaskTemplate,
				itemRevisons, var7);
	}
	
	
}
