package com.hh.fx.rewrite;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.hh.fx.rewrite.util.FileStreamUtil;
import com.teamcenter.rac.aif.AIFClipboard;
import com.teamcenter.rac.aif.AIFPortal;
import com.teamcenter.rac.aif.AIFTransferable;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.create.BOCreateDefinitionFactory;
import com.teamcenter.rac.common.create.CreateInstanceInput;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.common.create.ICreateInstanceInput;
import com.teamcenter.rac.common.create.SOAGenericCreateHelper;
import com.teamcenter.rac.kernel.DeepCopyInfo;
import com.teamcenter.rac.kernel.IRelationName;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.ListOfValuesInfo;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.TCAccessControlService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentProcessType;
import com.teamcenter.rac.kernel.TCComponentProfile;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentPseudoFolder;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCComponentSignoff;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentTaskTemplateType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCComponentUserType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCSignoffOriginType;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.FileUtility;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.services.rac.core.LOVService;
import com.teamcenter.services.rac.core._2013_05.LOV.InitialLovData;
import com.teamcenter.services.rac.core._2013_05.LOV.LOVSearchResults;
import com.teamcenter.services.rac.core._2013_05.LOV.LOVValueRow;
import com.teamcenter.services.rac.core._2013_05.LOV.LovFilterData;
import com.teamcenter.services.rac.workflow.WorkflowService;
import com.teamcenter.services.rac.workflow._2007_06.Workflow.ReleaseStatusInput;
import com.teamcenter.services.rac.workflow._2007_06.Workflow.ReleaseStatusOption;
import com.teamcenter.services.rac.workflow._2008_06.Workflow;
import com.teamcenter.soa.client.model.ErrorStack;

/**
 * 
 * @author Handk
 *
 */
@SuppressWarnings("deprecation")
public class Utils {

	private static List<String> property_onlyone = null;
	private static Boolean DebugMode = null;

	static TCTextService tcTextService = null;

	private final static String ITEM_REVISION_TYPE = "ItemRevision";
	private static TCComponentItemRevisionType itemRevType = null;
	private static TCComponentItemType itemType = null;

	private static String Fnd0ListOfValuesDynamic = "Fnd0ListOfValuesDynamic";
	private static SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat simpleFormat2 = new SimpleDateFormat("yyyy/MM/dd");

	public static TCSession getTCSession() {
		return RACUIUtil.getTCSession();
	}

	public static String[] getPreferenceValueArray(String key, int location) {
		return getTCSession().getPreferenceService().getStringArray(location, key);
	}

	public static String getPreferenceValue(String key, int location) {
		return getTCSession().getPreferenceService().getString(location, key);
	}

	public static String newItemID(String itemTypeName) throws TCException {
		String itemID = "";
		TCComponentItemType tcc = (TCComponentItemType) getTCSession().getTypeComponent(itemTypeName);
		itemID = tcc.getNewID();
		return itemID;
	}

	public static void setItem2Folder(TCComponent item, TCComponentFolder folder) throws TCException {
		if (folder == null) {
			setItem2NewStuffFolder(item);
			return;
		}
		AIFComponentContext[] contents = folder.getChildren();
		for (AIFComponentContext c : contents) {
			TCComponent tcc = (TCComponent) c.getComponent();
			if (tcc == item) {
				return;
			}
		}
		folder.add("contents", item);
	}

	public static void setItem2Home(TCComponent item) throws TCException {
		TCComponentUser user = getTCSession().getUser();
		TCComponentFolder home = user.getHomeFolder();
		setItem2Folder(item, home);
	}

	public static void setItem2NewStuffFolder(TCComponent item) throws TCException {
		TCComponentUser user = getTCSession().getUser();
		TCComponentFolder newstuffFolder = user.getNewStuffFolder();
		setItem2Folder(item, newstuffFolder);
	}

	public static boolean isNull(String info) {
		return info == null || info.trim().length() < 1;
	}
	
	//获取对象前缀大写
	public static String getPrefix1(){
		String[] arr=getTCSession().getPreferenceService().getStringArray(TCPreferenceService.TC_preference_site, "HH_Prefix");
		return arr[0];
	}
	//获取对象前缀小写
     public static String getPrefix2(){
		String[] arr=getTCSession().getPreferenceService().getStringArray(TCPreferenceService.TC_preference_site, "HH_Prefix");
		return arr[1];
	}
	

	public static void createNewDataset(TCComponentDataset templateDataset, TCComponentItemRevision rev,
			String newDatasetName) {
		if (templateDataset == null || rev == null || isNull(newDatasetName)) {
			return;
		}
		String referenceName = "";
		try {
			GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
			HashMap<String,String> map = getPreferenceUtil.getHashMapPreference(Utils.getTCSession(), TCPreferenceService.TC_preference_site, "FX8_DatasetType", "=");
			String dataType = templateDataset.getType();
			System.out.println("dataType ==" + dataType);
//			String mapValue = map.get(dataType.toLowerCase());
//			System.out.println("mapValue ==" + mapValue);
//			if(!"".equals(mapValue) && mapValue != null){
//				referenceName = mapValue;
//			}
			for(String mapValue : map.keySet()){
				System.out.println("mapValue ==" + mapValue);
				if(dataType.toLowerCase().contains(mapValue)){
					referenceName = map.get(mapValue);
				}
			}
			
			
			
//			if (dataType.toLowerCase().contains("excel")) {
//				referenceName = "excel";
//			} else if(dataType.toLowerCase().contains("word")) {
//				referenceName = "word";
//			}else if(dataType.toLowerCase().contains("image")) {
//				referenceName = "Image";
//			}else if(dataType.toLowerCase().contains("pdf")) {
//				referenceName = "PDF_Reference";
//			}else if(dataType.toLowerCase().contains("powerpoint")) {
//				referenceName = "powerpoint";
//			}else if(dataType.toLowerCase().contains("proprt")) {
//				referenceName = "PrtFile";
//			}else if(dataType.toLowerCase().contains("proasm")) {
//				referenceName = "AsmFile";
//			}
			System.out.println("referenceName ==" + referenceName);
			TCComponentDatasetType datasetType = (TCComponentDatasetType) templateDataset.getTypeComponent();
			TCComponentDataset dataset = datasetType.create(newDatasetName, newDatasetName, dataType);
			File file = exportFileToPath(templateDataset, referenceName);
			File newFile = copyFileWithNewName(file, newDatasetName);
			dataset.setFiles(new String[] { newFile.getAbsolutePath() }, new String[] { referenceName });

			if (dataset != null) {
				rev.add(IRelationName.IMAN_specification, dataset);
			}
			if (file.exists()) {
				file.delete();
			}
			if (newFile.exists()) {
				newFile.delete();
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
	}

	public static void renameTcDatasetName(TCComponentDataset dataset, String referenceName, String newName)
			throws TCException {
		if (dataset == null || isNull(newName) || isNull(referenceName)) {
			return;
		}
		TCComponentTcFile[] tcfiles = dataset.getTcFiles();
		if (tcfiles == null || tcfiles.length == 0) {
			return;
		}
		File file = exportFileToPath(dataset, referenceName);
		File newFile = copyFileWithNewName(file, newName);
		dataset.removeFiles(referenceName);
		dataset.setFiles(new String[] { newFile.getAbsolutePath() }, new String[] { referenceName });
		if (file.exists()) {
			file.delete();
		}
		if (newFile.exists()) {
			newFile.delete();
		}

	}

	public static File copyFileWithNewName(File file, String newName) {
		File res = null;
		if (file == null) {
			return null;
		}
		String fileExt = getExtensionName(file.getName());
		res = new File(file.getParent() + File.separator + newName + "." + fileExt);
		try {

			FileUtility.copyFile(file, res);
			// Files.copy(file.toPath(), res.toPath());
		} catch (Exception e) {
			e.printStackTrace();
			res = null;
		}
		if (res.exists()) {
			return res;
		} else {
			return null;
		}
	}

	public static TCComponentDataset copyDatasetWithNewName(TCComponentDataset dataset, String newDatasetName,
			String newFileName) {
		if (dataset == null || isNull(newDatasetName)) {
			return null;
		}
		
		TCComponentDataset newDataset = null;
		try {
			newDataset = dataset.saveAs(newDatasetName);
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
			TCSession session = (TCSession) app.getSession();
			String filePath = DownloadDataset.downloadFile(dataset, true);
			File file = new File(filePath);
			String fileName = file.getName();
			if(newDataset == null) {
				String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
				if (suffix.equalsIgnoreCase("xls")) {
					newDataset = CreateObject.createDataSet(session, filePath, "MSExcel", fileName, "excel");
				} else if (suffix.equalsIgnoreCase("doc")) {
					newDataset = CreateObject.createDataSet(session, filePath, "MSWord", fileName, "word");
				} else if (suffix.equalsIgnoreCase("xlsx")) {
					newDataset = CreateObject.createDataSet(session, filePath, "MSExcelX", fileName, "excel");
				} else if (suffix.equalsIgnoreCase("docx")) {
					newDataset = CreateObject.createDataSet(session, filePath, "MSWordX", fileName, "word");
				} else if(suffix.equalsIgnoreCase(".prt")){
					
				}else if (suffix.equalsIgnoreCase("pdf")) {
					newDataset = CreateObject.createDataSet(session, filePath, "PDF", fileName,
							"PDF_Reference");
				} else { // 其他
					newDataset = CreateObject.createDataSet(session, filePath, "Image", fileName, "Image");
				}
			}
		}
		
		return newDataset;
//		String referenceName = "";
//		try {
//			String dataType = dataset.getType();
//			TCComponentDatasetDefinition datasetDefinition = dataset.getDatasetDefinitionComponent();
//			NamedReferenceContext[] arrayOfNamedReferenceContext = datasetDefinition.getNamedReferenceContexts();
//			for (NamedReferenceContext temp : arrayOfNamedReferenceContext) {
//				print2Console(temp.getNamedReference());
//			}
//			referenceName = DatasetReferenceName.getReferenceName(dataType);
//			if (Utils.isNull(referenceName)) {
//				return null;
//			}
//			// if (dataType.toLowerCase().contains("excel")) {
//			// referenceName = "excel";
//			// } else if (dataType.toLowerCase().contains("word")) {
//			// referenceName = "word";
//			// } else
//			// referenceName = null;
//			// if (referenceName == null)
//			// return null;
//			TCComponentDatasetType datasetType = (TCComponentDatasetType) dataset.getTypeComponent();
//			TCComponentDataset newDataset = datasetType.create(newDatasetName, newDatasetName, dataType);
//			File file = exportFileToPath(dataset, referenceName);
//			File newFile;
//			if (isNull(newFileName)) {
//				newFile = file;
//			} else {
//				newFile = copyFileWithNewName(file, newFileName);
//			}
//			newDataset.setFiles(new String[] { newFile.getAbsolutePath() }, new String[] { referenceName });
//
//			if (file.exists()) {
//				file.delete();
//			}
//			if (newFile.exists()) {
//				newFile.delete();
//			}
//			return newDataset;
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//		return null;
	}

	public static String getExtensionName(String filename) {
		if (!Utils.isNull(filename)) {
			int dot = filename.lastIndexOf('.');
			if (dot > -1 && dot < (filename.length() - 1)) {
				return filename.substring(dot + 1);
			}
		}
		return null;
	}

	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	public static File exportFileToPath(TCComponentDataset dataset, String nameRef) {
		return exportFileToPath(dataset, nameRef, System.getProperty("java.io.tmpdir"));
	}

	public static File exportFileToPath(TCComponentDataset dataset, String nameRef, String folderPath) {
		try {
			File exportFile = dataset.getFile(nameRef, dataset.getTcFiles()[0].toString(), folderPath);
			return exportFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static File exportFileToPath(TCComponentDataset dataset, String nameRef, File folder) {
		return exportFileToPath(dataset, nameRef, folder.getAbsolutePath());
	}

	public static void infoMessage(String message) {
		if (isNull(message)) {
			return;
		}
		MessageBox.post(message, "WARN...", MessageBox.INFORMATION);
	}

	public static void showMessage(String message) {
		if (isNull(message)) {
			return;
		}
		JOptionPane.showMessageDialog(null, message, "WARN...", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static TCComponent createCom(TCSession session,String itemTypeName,String itemID,String name,String revisionID,Map<String,String> revisionMap) {		
		IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session,itemTypeName);
		CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);

		
		
		createInstanceInput.add("item_id", itemID);
		createInstanceInput.add("object_name", name);
		
		IBOCreateDefinition createDefinitionRev = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session,itemTypeName+"Revision");
		CreateInstanceInput createInstanceInputRev = new CreateInstanceInput(createDefinitionRev);

		if(revisionMap == null){
			revisionMap=new HashMap<String,String>();
		}
		for(Entry<String,String> entry:revisionMap.entrySet()){
			String p=entry.getKey();
			String v=entry.getValue();
			createInstanceInputRev.add(p, v);
			System.out.println("p== "+p);
			System.out.println("v== "+v);
		}
		createInstanceInputRev.add("item_revision_id", revisionID);
		ArrayList iputList = new ArrayList();

		iputList.add(createInstanceInput);

		ArrayList list = new ArrayList(0);
		list.addAll(iputList);

		createInstanceInput.addSecondaryCreateInput(createDefinition.REVISION, createInstanceInputRev);
		TCComponent obj = null;
		List comps = null;
		try {
			comps = SOAGenericCreateHelper.create(session,
					createDefinition, list);
			obj = (TCComponent) comps.get(0);
		} catch (TCException e) {
//			createCom(session, itemType, objName, partSource, materialType, state, size, unit, productLine, midClass, littleClass);
			e.printStackTrace();
		}
		return obj;
	}

	public static TCComponentItem createItem(TCSession session,String itemTypeName,String itemID,String name,String revisionID,Map<String,String>itemMap,Map<String,String> revisionMap){
		try{
//		TCComponentItemType itemType=(TCComponentItemType)Utils.getTCSession().getTypeComponent(itemTypeName);
//		TCComponentItemRevisionType itemRevisionType=itemType.getItemRevisionType();
//		String itemRevisionTypeName=itemRevisionType.getTypeName();
		BOCreateDefinitionFactory createDefinitionFactory=BOCreateDefinitionFactory.getInstance();
		
		IBOCreateDefinition itemCreateDefinition=createDefinitionFactory.getCreateDefinition(Utils.getTCSession(),itemTypeName);
		CreateInstanceInput createInstanceInput = new CreateInstanceInput(itemCreateDefinition);
		
		if(itemMap==null){
			itemMap=new HashMap<String,String>();
		}
		itemMap.put("item_id",itemID);
		itemMap.put("object_name", name);
		itemMap.put("object_desc", "");
		for(Entry<String,String> entry:itemMap.entrySet()){
			String p=entry.getKey();
			String v=entry.getValue();
			createInstanceInput.add(p, v);
		}
		
		
		List<IBOCreateDefinition> secondaryCreateDefinitionList=itemCreateDefinition.getSecondaryCreateDefinition("revision");
		if(secondaryCreateDefinitionList!=null && secondaryCreateDefinitionList.size()>0){
			IBOCreateDefinition revisionCreateDefinition=secondaryCreateDefinitionList.get(0);
			CreateInstanceInput revisionInstanceInput=new CreateInstanceInput(revisionCreateDefinition);
			if(revisionMap==null){
				revisionMap=new HashMap<String,String>();
			}
			itemMap.put("object_name", name);
			itemMap.put("object_desc", "");
			itemMap.put("item_revision_id", revisionID);
			for(Entry<String,String> entry:revisionMap.entrySet()){
				String p=entry.getKey();
				String v=entry.getValue();
				revisionInstanceInput.add(p, v);
			}
			createInstanceInput.add("revision", revisionInstanceInput);
		}
		List<ICreateInstanceInput> list=new ArrayList<ICreateInstanceInput>();
		list.add(createInstanceInput);
		List<TCComponent> componentList=SOAGenericCreateHelper.create(session, itemCreateDefinition, list);
		
		if(componentList!=null && componentList.size()>0){
			TCComponent component=componentList.get(0);
			if(component instanceof TCComponentItem){
				return (TCComponentItem)component;
			}
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static TCComponentFolder createFolder(TCSession session, String type, String name) {
		ArrayList iputList = new ArrayList();
		IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session,
				type);
		CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);
		createInstanceInput.add("object_name", name);
		iputList.add(createInstanceInput);
		List comps = null;
		TCComponentFolder folder = null;
		try {
			comps = SOAGenericCreateHelper.create(session, createDefinition, iputList);
			folder = (TCComponentFolder) comps.get(0);
		} catch (TCException e) {
			e.printStackTrace();
		}
		return folder;
	}

	public static TCComponentProcess createProcess(String workflowTemplateName, String processName, String desc,
			TCComponent[] att) {
		try {

			TCComponentTaskTemplateType templateType = (TCComponentTaskTemplateType) getTCSession()
					.getTypeComponent(TCComponentTaskTemplateType.EPM_TASKTEMPLATE_TYPE);

			TCComponentTaskTemplate template = templateType.find(workflowTemplateName, 0);
			if (template == null) {
				return null;
			}
			int[] attType = null;
			if (att != null && att.length > 0) {
				attType = new int[att.length];
				for (int i = 0; i < att.length; i++) {
					attType[i] = 1;
				}
			}
			TCComponentProcessType processType = (TCComponentProcessType) getTCSession().getTypeComponent("Job");
			TCComponentProcess process = (TCComponentProcess) processType.create(processName, desc == null ? "" : desc,
					template, att, attType);
			return process;
		} catch (TCException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<TCComponentSignoff> createSignoff(TCComponentTask sstTask, TCComponent groupmember,
			TCSignoffOriginType signoffOriginType, TCComponentProfile profile) {
		String str = "SOA_EPM_ORIGIN_UNDEFINED";
		int i = 0;
		if (signoffOriginType == TCSignoffOriginType.PROFILE) {
			str = "SOA_EPM_SIGNOFF_ORIGIN_PROFILE";
		}
		WorkflowService localWorkflowService = WorkflowService.getService(getTCSession());
		Workflow.CreateSignoffInfo signoffInfo = new Workflow.CreateSignoffInfo();
		signoffInfo.signoffMember = groupmember;
		signoffInfo.signoffAction = "SOA_EPM_Review";
		signoffInfo.originType = str;
		signoffInfo.origin = profile;
		Workflow.CreateSignoffInfo[] arrayOfCreateSignoffInfo = new Workflow.CreateSignoffInfo[1];
		arrayOfCreateSignoffInfo[0] = signoffInfo;
		Workflow.CreateSignoffs createSignoffs = new Workflow.CreateSignoffs();
		createSignoffs.signoffInfo = arrayOfCreateSignoffInfo;
		createSignoffs.task = sstTask;
		Workflow.CreateSignoffs[] arrayOfCreateSignoffs = new Workflow.CreateSignoffs[1];
		arrayOfCreateSignoffs[0] = createSignoffs;
		List<TCComponentSignoff> list = null;
		try {
			ServiceData localServiceData = localWorkflowService.addSignoffs(arrayOfCreateSignoffs);
			ErrorStack es;
			String[] messages;

			if (localServiceData.sizeOfPartialErrors() > 0) {
				es = localServiceData.getPartialError(localServiceData.sizeOfPartialErrors() - 1);
				messages = ((ErrorStack) es).getMessages();
				StringBuilder localStringBuilder = new StringBuilder();
				for (int k = 0; k < messages.length - 1; k++) {
					localStringBuilder.append(messages[k]);
				}
				MessageBox.post(messages[(messages.length - 1)], localStringBuilder.toString(), " ", 1);
			} else if ((i = localServiceData.sizeOfCreatedObjects()) > 0) {
				list = new ArrayList<TCComponentSignoff>(i);
				TCComponent tcc = null;
				for (int j = 0; j < i; j++) {
					tcc = localServiceData.getCreatedObject(j);
					if (tcc instanceof TCComponentSignoff) {
						list.add((TCComponentSignoff) tcc);
					}
				}
			}
		} catch (Exception localException) {
			MessageBox.post(localException.getMessage(), localException, "", 1);
		}
		return list;
	}



	public static List<String> getOnlyOnePropertyList() {
		if (property_onlyone == null) {
			String[] temp = Utils.getPreferenceValueArray("casc_OnlyOne", TCPreferenceService.TC_preference_site);
			if (temp == null || temp.length < 1) {
				property_onlyone = new ArrayList<String>();
			} else {
				property_onlyone = Arrays.asList(temp);
			}
		}
		return property_onlyone;
	}

	public static String getTextValue(String name) {
		if (tcTextService == null) {
			tcTextService = getTCSession().getTextService();
		}
		String res = null;
		try {
			String value = tcTextService.getTextValue(name);
			if (isNull(value)) {
				res = name;
			} else {
				res = value;
			}
		} catch (TCException e) {
			res = name;
			e.printStackTrace();
		}
		return res;
	}

	public static List<InterfaceAIFComponent> search(String searchName, String[] keys, String[] values) {
		List<InterfaceAIFComponent> res;
		InterfaceAIFComponent[] temp;
		try {
			/*print2Console(searchName);
			print2Console(Arrays.asList(keys));
			print2Console(Arrays.asList(values));*/
			temp = getTCSession().search(searchName, keys, values);
			if (temp == null || temp.length < 1) {
				res = new ArrayList<InterfaceAIFComponent>();
			} else {
				res = Arrays.asList(temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			res = new ArrayList<InterfaceAIFComponent>();
		}

		return res;
	}

	public static boolean isReleased(TCComponent com) {
		if (com == null) {
			return false;
		}
		try {
			com.refresh();
			TCComponent[] relStatus = com.getReferenceListProperty("release_status_list");
			if (relStatus != null && relStatus.length > 0) {
				return true;
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isReleased(TCComponent tcc, String[] statusNameArray) {
		if (tcc == null || statusNameArray == null || statusNameArray.length < 1) {
			return false;
		}
		try {
			tcc.refresh();
			TCComponent[] relStatus = tcc.getReferenceListProperty("release_status_list");
			if (relStatus != null && relStatus.length > 0) {
				TCComponent status = relStatus[relStatus.length - 1];
				String name = status.getTCProperty("name").getStringValue();
				if (isNull(name)) {
					return false;
				}
				for (String statusName : statusNameArray) {
					if (!isNull(statusName) && name.equalsIgnoreCase(statusName)) {
						return true;
					}
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isReleased(TCComponent tcc, String statusName) {
		if (tcc == null) {
			return false;
		}
		try {
			tcc.refresh();
			TCComponent[] relStatus = tcc.getReferenceListProperty("release_status_list");
			if (relStatus != null && relStatus.length > 0) {
				TCComponent status = relStatus[relStatus.length - 1];
				String name = status.getTCProperty("name").getStringValue();
				if (!isNull(name) && name.equalsIgnoreCase(statusName)) {
					return true;
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getReleasedName(TCComponent tcc) {
		if (tcc == null) {
			return null;
		}
		try {
			tcc.refresh();
			TCComponent[] relStatus = tcc.getReferenceListProperty("release_status_list");
			if (relStatus != null && relStatus.length > 0) {
				TCComponent status = relStatus[relStatus.length - 1];
				String name = status.getTCProperty("name").getStringValue();
				return name;
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean addTCComponentStatusByName(TCComponent tcc, String statusName) {
		if (isReleased(tcc, statusName)) {
			return true;
		}
		try {
			byPass(true);
			WorkflowService workflowServices = WorkflowService.getService(getTCSession());

			ReleaseStatusOption releaseStatusOption = new ReleaseStatusOption();
			releaseStatusOption.existingreleaseStatusTypeName = null;
			releaseStatusOption.newReleaseStatusTypeName = statusName;
			releaseStatusOption.operation = "Append";
			ReleaseStatusOption[] operation = { releaseStatusOption };

			ReleaseStatusInput releaseStatusInput = new ReleaseStatusInput();
			releaseStatusInput.objects = new TCComponent[] { tcc };
			releaseStatusInput.operations = operation;
			ReleaseStatusInput[] input = { releaseStatusInput };
			byPass(true);
			workflowServices.setReleaseStatus(input);
			// workflowServices.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			byPass(false);
		}
		return true;
	}

	public static boolean checkWritePrivilege(TCComponent component) {
		if (component == null) {
			return false;
		}
		try {
			return getTCSession().getTCAccessControlService().checkPrivilege(component, "WRITE");
		} catch (TCException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isOwner(TCComponent component) {
		if (component == null) {
			return false;
		}
		try {
			TCComponent tcc = component.getReferenceProperty("owning_user");
			if (tcc == null) {
				return false;
			}
			if (tcc instanceof TCComponentUser) {
				TCComponentUser owner = (TCComponentUser) tcc;
				if (owner.getUserId().equals(getTCSession().getUser().getUserId())) {
					return true;
				}
			}
			return false;
		} catch (TCException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean checkChangePrivilege(TCComponent component) {
		if (component == null) {
			return false;
		}
		try {
			return getTCSession().getTCAccessControlService().checkPrivilege(component, "CHANGE");
		} catch (TCException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void byPass(boolean on) {
		TCUserService service = null;
		service = getTCSession().getUserService();
		try {
			if (service != null) {
				if (on) {
					service.call("cust_set_bypass", new Object[] { "true" });
				} else {
					service.call("cust_set_bypass", new Object[] { "false" });
				}
			}
		} catch (TCException e) {
		}

	}

	public static String getTempDir() {
		return System.getProperty("java.io.tmpdir");
	}

	public static String getNextRevID(String currentRevID) {
		char[] chars = currentRevID.toCharArray();
		if (chars[chars.length - 1] == '9' || chars[chars.length - 1] == 'Z') {
			if (chars.length > 1) {
				if (chars[chars.length - 1] == '9') {
					chars[chars.length - 1] = '0';
				} else if (chars[chars.length - 1] == 'Z') {
					chars[chars.length - 1] = 'A';
				}
				chars[chars.length - 2] = (char) (((int) chars[chars.length - 2]) + 1);
			} else {
				return null;
			}
		} else {
			chars[chars.length - 1] = (char) (((int) chars[chars.length - 1]) + 1);
		}
		return String.valueOf(chars);
	}

	public static String fillIn(String no, String key, int num) {
		if (Utils.isNull(no)) {
			return no;
		}
		String res;
		int n = num - no.length();
		if (n > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < n; i++) {
				sb.append(key);
			}
			res = sb.toString() + no;
		} else {
			res = no;
		}
		return res;
	}

	public static void print2Console(String message) {
		if (Utils.isNull(message)) {
			return;
		}
		/*if (DebugMode == null) {
			initDubugMode();
		}*/
		print2Console(message, true);
	}

	public static void print2Console(List<String> message) {
		if (message == null || message.size() < 1) {
			return;
		}
		/*if (DebugMode == null) {

			initDubugMode();
		}*/
		print2Console(message, true);
	}

	/*public static void initDubugMode() {
		String preName = "CUST_DebugMode";
		TCPreferenceService preService = getTCSession().getPreferenceService();
		try {
			DebugMode = preService.getLogicalValue(preName);
		} catch (Exception e) {
			e.printStackTrace();
			DebugMode = Boolean.TRUE;
		}
	}*/

	public static void print2Console(String message, Boolean show) {
		if (Utils.isNull(message)) {
			return;
		}
		if (show) {
			System.err.println(message);
		}
	}

	public static void print2Console(List<String> message, Boolean show) {
		if (message == null || message.size() < 1) {
			return;
		}
		if (show) {
			System.err.println(message);
		}
	}

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public static void addClipboard(TCComponent obj) {
//		if (obj == null) {
//			return;
//		}
//		List list = new ArrayList();
//		list.add(obj);
//		addClipboard(list);
//	}

//	@SuppressWarnings("rawtypes")
//	public static void addClipboard(List list) {
//		if (list == null || list.size() < 1) {
//			return;
//		}
//		AIFClipboard aifclipboard = AIFPortal.getClipboard();
//		AIFTransferable aiftransferable = new AIFTransferable(list);
//		aifclipboard.setContents(aiftransferable, null);
//	}

	public static void changeOwner(TCComponent component, TCComponentUser user) {
		if (user == null || component == null) {
			return;
		}

		try {
			// TCComponentUser owningUser = (TCComponentUser)
			// component.getReferenceProperty("owning_user");
			// TCComponentUser thisUser=getTCSession().getUser();
			// if(!thisUser.getUserId().equals(owningUser.getUserId())) return;
			byPass(true);
			TCComponentGroup defaultGroup = (TCComponentGroup) user.getTCProperty("default_group").getReferenceValue();
			component.changeOwner(user, defaultGroup);
		} catch (TCException e) {
			e.printStackTrace();
		} finally {
			byPass(false);
		}
	}

	public static void changeOwner(TCComponent component, String userID) {
		if (component == null) {
			return;
		}
		if (isNull(userID)) {
			return;
		}
		try {
			TCComponentUserType userType = (TCComponentUserType) getTCSession().getTypeComponent("User");
			TCComponentUser user = userType.find(userID);
			if (user != null) {
				changeOwner(component, user);
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
	}

	public static void reSetTaskResponsibleParty(TCComponentTask task) {
		if (task == null) {
			return;
		}
		TCComponentUser thisUser = getTCSession().getUser();
		try {
			TCComponent responsible = task.getResponsibleParty();
			if (!responsible.getUid().equals(thisUser.getUid())) {
				byPass(true);
				task.setResponsibleParty(thisUser);
				byPass(false);
			}
			TCComponentTask[] subTasks = task.getSubtasks();
			if (subTasks != null && subTasks.length > 0) {
				for (TCComponentTask subTask : subTasks) {
					reSetTaskResponsibleParty(subTask);
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
	}

	public static Date formatDate(String dateStr) {
		try {
			return simpleFormat.parse(dateStr);
		} catch (ParseException e) {
			try {
				return simpleFormat2.parse(dateStr);
			} catch (ParseException e1) {
			}
		}
		return null;
	}

	public static String formatDate(Date date) {
		if (date == null) {
			return "";
		}

		return simpleFormat.format(date);
	}

	public static int getIntStr(String numStr) {
		if (isNull(numStr)) {
			return -1;
		}
		String[] temps = numStr.split("\\.");
		int result = 0;
		try {
			result = Integer.parseInt(temps[0]);
			return result;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static int getIntStr2(String numStr) {
		if (isNull(numStr)) {
			return 0;
		}
		numStr = numStr.trim();
		String[] temps = numStr.split("\\.");
		int result = 0;
		try {
			result = Integer.parseInt(temps[0]);
		} catch (NumberFormatException e) {
		}
		if (result == 0) {
			numStr = temps[0];
			int length = numStr.length();
			int i = 0;
			StringBuffer sb = new StringBuffer();
			for (i = length - 1; i >= 0; i--) {
				char ch = numStr.charAt(i);
				if (Character.isDigit(ch)) {
					sb.append(ch);
				} else {
					break;
				}
			}
			if (sb.length() < 1) {
				return 0;
			}

			numStr = sb.toString();
			sb.setLength(0);
			length = numStr.length();
			boolean first = true;
			for (i = length - 1; i >= 0; i--) {
				char ch = numStr.charAt(i);
				if (first) {
					if ('0' == ch) {
						continue;
					} else {
						sb.append(ch);
						first = false;
					}
				} else {
					sb.append(ch);
				}
			}
			if (sb.length() < 1) {
				return 0;
			}

			try {
				result = Integer.parseInt(sb.toString());
			} catch (NumberFormatException e) {
			}
			return result;

		}
		return result;
	}

	public static boolean is03(File excelFile) {
		if (excelFile == null) {
			return false;
		}
		String fileName = excelFile.getName();
		return fileName.toLowerCase().endsWith("xls");
	}

	public static TCComponentItemRevision findItemRev(String itemID, String revID) throws TCException {
		if (itemRevType == null) {
			itemRevType = (TCComponentItemRevisionType) getTCSession().getTypeComponent(ITEM_REVISION_TYPE);
		}
		TCComponentItemRevision[] revs = itemRevType.findRevisions(itemID, revID);
		if (revs == null || revs.length < 1) {
			return null;
		}
		return revs[0];
	}

	public static TCComponentItem findItem(String itemId) throws TCException {
		if (isNull(itemId)) {
			return null;
		}
		if (itemType == null) {
			itemType = (TCComponentItemType) getTCSession().getTypeComponent(ITypeName.Item);
		}

		TCComponentItem[] resultItems = itemType.findItems(itemId);
		if (resultItems == null || resultItems.length < 1) {
			return null;
		}
		return resultItems[0];
	}

	public static TCComponentItem createItem(String itemId, String itemTypeName, String itemName) {
		if (Utils.isNull(itemId)) {
			itemId = null;
		}
		try {
			if (itemType == null) {
				itemType = (TCComponentItemType) getTCSession().getTypeComponent(ITypeName.Item);
			}
			String currentRevId = itemType.getNewRev(null);
//			TCComponentItem currentItem = itemType.create(itemId, currentRevId, itemTypeName, itemName, "", null);
			TCComponentItem currentItem = itemType.create(
			          "", // itemId
			          "", // revId
			          itemType.getTypeName(), "", "",
			          null, null, null);
			return currentItem;

		} catch (TCException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void checkChildRealationRevExist(TCComponent parentComp, TCComponent childComp, String relation)
			throws TCException {
		if (isNull(relation)) {
			return;
		}
		if (parentComp == null || childComp == null) {
			return;
		}
		TCComponent[] childComps = parentComp.getRelatedComponents(relation);
		boolean has = false;
		for (TCComponent oneComponent : childComps) {
			if (oneComponent == childComp) {
				has = true;
				break;
			}
		}
		if (!has) {
			parentComp.add(relation, childComp);
		}
	}
	
	
	
	//给选中对象关联创建对象
	public static void connectRelate(String[] relates,
			TCComponentItemRevision targetRev,
			TCComponentItemRevision currentRev) {
		if (relates != null && relates.length != 0) {
			for (int i = 0; i < relates.length; i++) {
				System.out.println("targetRelates[i] == " + relates[i]);
				String errorMsg = "";
				String targetRelate = relates[i];
				if (targetRelate.contains("=")) {
					String[] temp = targetRelate.split("=");
					if (temp.length == 2) {
						String value1 = temp[0];
						String value2 = temp[1];
						TCComponent targetCom = null;
						TCComponent currentCom = null;
						if (value1.contains(".")) {
							String[] temp1 = value1.split("\\.");
							if (temp1.length == 2) {
								String tValue1 = temp1[0];
								String tValue2 = temp1[1];
								if ("revision".equals(tValue1)) {
									targetCom = targetRev;
								} else if ("item".equals(tValue1)) {
									try {
										targetCom = targetRev.getItem();
									} catch (Exception e) {
										e.printStackTrace();
										errorMsg = "【ERROR】获取" + targetRev
												+ "的 Item 失败";
										System.out.println(errorMsg);
										continue;
									}
								} else {
									errorMsg = "【ERROR】字段" + tValue1
											+ "在字段" + targetRelate + "中有误";
									System.out.println(errorMsg);
									continue;
								}

								if ("revision".equals(value2)) {
									currentCom = currentRev;
								} else if ("item".equals(value2)) {
									try {
										currentCom = currentRev.getItem();
									} catch (Exception e) {
										e.printStackTrace();
										errorMsg = "【ERROR】获取" + currentRev
												+ "的 Item 失败";
										System.out.println(errorMsg);
										continue;
									}
								} else {
									errorMsg = "【ERROR】字段" + value2 + "在字段"
											+ value1 + "中有误";
									System.out.println(errorMsg);
									continue;
								}

								if (currentCom != null && targetCom != null) {
									try {
										errorMsg = "【INFO】" + targetCom
												+ "以关系 " + tValue2
												+ " 挂载对象 " + currentCom
												+ " 开始！";
										System.out.println(errorMsg);
										targetCom.add(tValue2, currentCom);
										errorMsg = "【INFO】" + targetCom
												+ "以关系 " + tValue2
												+ " 挂载对象 " + currentCom
												+ " 成功！";
										System.out.println(errorMsg);
									} catch (TCException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										errorMsg = "【ERROR】" + e.getError();
										System.out.println(errorMsg);
										continue;
									}
								} else {
									errorMsg = "【ERROR】无法获取选择对象或创建对象";
									System.out.println(errorMsg);
								}
							} else {
								errorMsg = "【ERROR】当前字段" + value1
										+ "不符合要求，以.分割数量不等于2";
								System.out.println(errorMsg);
							}
						} else {
							errorMsg = "【ERROR】当前字段" + value1
									+ "不符合要求，不包含.";
							System.out.println(errorMsg);
						}
					} else {
						errorMsg = "【ERROR】当前字段" + targetRelate
								+ "不符合要求，以：分割数量不等于2";
						System.out.println(errorMsg);
					}

				} else {
					errorMsg = "【ERROR】当前字段" + targetRelate + "不符合要求，没有包含=";
					System.out.println(errorMsg);
				}
			}
		}
	}
	
	
	//对属性进行映射
	public static void mapProp(String[] relates,
			TCComponentItemRevision targetRev,
			TCComponentItemRevision currentRev,TCComponentProject targetProject) {
		if (relates != null && relates.length != 0) {
			TCComponentItem currentItem = null;
			try {
				currentItem = currentRev.getItem();
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("【ERROR】获取创建对象的ITEM失败");
				return;
			}
			TCComponentItem targetItem = null;
			try {
				targetItem = targetRev.getItem();
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("【ERROR】获取选中对象的ITEM失败");
				return;
			}
			for (int i = 0; i < relates.length; i++) {
				System.out.println("relates[i] == " + relates[i]);
				String mapProp = relates[i];
				String errorMsg = "";
				if (mapProp.contains("=")) {
					String[] temp = mapProp.split("=");
					if (temp.length == 2) {
						String value1 = temp[0];
						String value2 = temp[1];
						TCProperty targetProp = null;
						TCProperty currentProp = null;
						TCComponent targetCom = null;
						TCComponent currentCom = null;
						if (value1.contains(":")) {
							String[] targetValue = value1.split(":");
							if (targetValue.length == 2) {
								String tValue1 = targetValue[0];
								String tValue2 = targetValue[1];
								if ("item".equals(tValue1)) {
									targetCom = targetItem;

								} else if ("revision".equals(tValue1)) {
									targetCom = targetRev;
								} else if("project".equals(tValue1)){
									targetCom = targetProject;
								} else {
									errorMsg = "【ERROR】字段" + value1 + "在字段"
											+ tValue1 + "中有误";
									System.out.println(errorMsg);
									continue;
								}

								if (targetCom != null) {
									try {
										targetProp = targetCom
												.getTCProperty(tValue2);
									} catch (TCException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										errorMsg = "【ERROR】从" + targetCom
												+ "中获取属性" + tValue2
												+ "失败 ，失败信息:"
												+ e.getError();
										System.out.println(errorMsg);
										continue;
									}
								} else {
									errorMsg = "【ERROR】获取选择对象为空！";
									System.out.println(errorMsg);
									continue;
								}
							} else {
								errorMsg = "【ERROR】当前字段" + value1
										+ "不符合要求，以:分割数量不等于2";
								System.out.println(errorMsg);
								continue;
							}
						} else {
							errorMsg = "【ERROR】当前字段" + value1
									+ "不符合要求，不包含：";
							System.out.println(errorMsg);
							continue;
						}

						if (value2.contains(":")) {
							String[] currentValue = value2.split(":");
							if (currentValue.length == 2) {
								String cValue1 = currentValue[0];
								String cValue2 = currentValue[1];
								if ("item".equals(cValue1)) {
									currentCom = currentItem;
								} else if ("revision".equals(cValue1)) {
									currentCom = currentRev;
								} else {
									errorMsg = "【ERROR】字段" + value2 + "在字段"
											+ cValue1 + "中有误";
									System.out.println(errorMsg);
									continue;
								}

								if (currentCom != null) {
									try {
										currentProp = currentCom
												.getTCProperty(cValue2);
									} catch (TCException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
										errorMsg = "【ERROR】从" + currentCom
												+ "中获取属性" + cValue2
												+ "失败 ，失败信息:"
												+ e.getError();
										System.out.println(errorMsg);
										continue;
									}
								} else {
									errorMsg = "【ERROR】获取创建对象为空！";
									System.out.println(errorMsg);
									continue;
								}
							} else {
								errorMsg = "【ERROR】当前字段" + value2
										+ "不符合要求，以:分割数量不等于2";
								System.out.println(errorMsg);
								continue;
							}
						} else {
							errorMsg = "【ERROR】当前字段" + value2
									+ "不符合要求，不包含：";
							System.out.println(errorMsg);
							continue;
						}

						if (currentProp != null && targetProp != null) {
							try {
								errorMsg = "【INFO】将选择对象上的属性"
										+ targetProp.getPropertyName()
										+ "往创建对象"
										+ currentProp.getPropertyName()
										+ "开始！";
								System.out.println(errorMsg);
								if(currentProp.isNotArray()) {
									System.out.println("not array");
									currentProp.setPropertyData(targetProp
											.getPropertyData());
									currentCom.setTCProperty(currentProp);
								} else {
									System.out.println("is array");
									if(targetProp.getPropertyType() == TCProperty.PROP_string) {
										System.out.println("PROP_string");
										currentProp.setPropertyArrayData(targetProp.getStringArrayValue());
										currentCom.setTCProperty(currentProp);
									} else if(targetProp.getPropertyType() == TCProperty.PROP_untyped_reference){
										System.out.println("PROP_untyped_reference");
										currentProp.setPropertyArrayData(targetProp.getReferenceValueArray());
										currentCom.setTCProperty(currentProp);
									} else if(targetProp.getPropertyType() == TCProperty.PROP_typed_reference){
										System.out.println("PROP_typed_reference");
										TCComponent[] coms = targetProp.getReferenceValueArray();
									
										ArrayList list= new ArrayList();
										for(int m=0;m<coms.length;m++) {
											
											String typeName = coms[m].getTypeComponent().getTypeName();
											
											TCComponent newCom = createCom(typeName);
											
											TCProperty[] props = coms[m].getAllTCProperties();
											for(int x=0;x<props.length;x++) {
												String propName = props[x].getPropertyName();
												
												if(propName.startsWith(Utils.getPrefix2())) {
													TCProperty prop = newCom.getTCProperty(propName);
													if(props[x].isNotArray()) {
														
														prop.setPropertyData(props[x].getPropertyData());
													} else {
														prop.setPropertyArrayData((Object[])props[x].getPropertyData());
													}
													newCom.setTCProperty(prop);
												}
											}
											list.add(newCom);
											
										}
										currentProp.setPropertyArrayData(list.toArray());
										currentCom.setTCProperty(currentProp);
									} else if(targetProp.getPropertyType() == TCProperty.PROP_untyped_reference){
										System.out.println("PROP_untyped_reference");
										TCComponent[] coms = targetProp.getReferenceValueArray();
										currentProp.setPropertyArrayData(coms);
										currentCom.setTCProperty(currentProp);
									} else {
										System.out.println("PropertyType == "+targetProp.getPropertyType());
									}
									
								}
								
								errorMsg = "【INFO】将选择对象上的属性"
										+ targetProp.getPropertyName()
										+ "往创建对象"
										+ currentProp.getPropertyName()
										+ "成功！";
								System.out.println(errorMsg);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								errorMsg = "【ERROR】将选择对象上的属性"
										+ targetProp.getPropertyName()
										+ "往创建对象"
										+ currentProp.getPropertyName()
										+ "失败，失败信息:" + e.getMessage();
								System.out.println(errorMsg);
								continue;
							}
						} else {
							errorMsg = "【ERROR】从对象上获取属性为空";
							System.out.println(errorMsg);
							continue;
						}
					} else {
						errorMsg = "【ERROR】当前字段" + mapProp
								+ "不符合要求，以=分割数量不等于2";
						System.out.println(errorMsg);
						continue;
					}
				} else {
					errorMsg = "【ERROR】当前字段" + mapProp + "不符合要求，不包含=";
					System.out.println(errorMsg);
				}
			}
		}
	}
	
	
	//创建对象
	public static TCComponent createCom(String tableRowName) {
		// TODO Auto-generated method stub
		TCSession session =(TCSession) AIFUtility.getCurrentApplication().getSession();
		IBOCreateDefinition createDefinition = BOCreateDefinitionFactory
				.getInstance().getCreateDefinition(session,
						tableRowName);
		CreateInstanceInput createInstanceInput = new CreateInstanceInput(
				createDefinition);
		ArrayList iputList = new ArrayList();
		iputList.add(createInstanceInput);
		TCComponent obj = null;
		List comps = null;
		try {
			comps = SOAGenericCreateHelper.create(session,
					createDefinition, iputList);
			obj = (TCComponent) comps.get(0);
		} catch (TCException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
	

	private void saveTable(TCComponentItemRevision itemRev,TCProperty prop,ArrayList list,ArrayList propList,TCSession session,String tableTypeName) {
		try {
			FileStreamUtil fileStreamUtil = new FileStreamUtil();
			String logFile = fileStreamUtil.getTempPath("TablePanel");
			PrintStream printStream = fileStreamUtil.openStream(logFile);
			fileStreamUtil.writeData(printStream, "tableTypeName == "+tableTypeName);
			if (prop.getPropertyType() == TCProperty.PROP_typed_reference) {
				if (list != null && list.size() > 0) {
					ArrayList comList = new ArrayList();
					for (int i = 0; i < list.size(); i++) {
						ArrayList datalist = (ArrayList) list.get(i);
						IBOCreateDefinition createDefinition = BOCreateDefinitionFactory
								.getInstance().getCreateDefinition(session,
										tableTypeName);
						CreateInstanceInput createInstanceInput = new CreateInstanceInput(
								createDefinition);
						ArrayList iputList = new ArrayList();
						for(int j=0;j<datalist.size();j++) {
							createInstanceInput.add(propList.get(j).toString(), datalist.get(j));
						}
						
						iputList.add(createInstanceInput);
						TCComponent obj = null;
						List comps = null;
						try {
							comps = SOAGenericCreateHelper.create(session,
									createDefinition, iputList);
							obj = (TCComponent) comps.get(0);
							fileStreamUtil.writeData(printStream, "obj == "+ obj);
							comList.add(obj);
							fileStreamUtil.writeData(printStream, "comList add == "+obj);
						} catch (TCException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					prop.setPropertyArrayData(comList.toArray());
					itemRev.setTCProperty(prop);
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			
		}
	}
	//判断是否有权限
	public static boolean hasWritePrivilege(TCSession session,InterfaceAIFComponent comp) {
		try {
			TCAccessControlService accessControlService = session
					.getTCAccessControlService();
			TCComponent component = null;

			if (comp instanceof TCComponentBOMLine) {
				// 检查bomview
				TCComponentBOMLine bomline = (TCComponentBOMLine) comp;
				component  = bomline.getItemRevision();
				TCComponent[] bvs = component
						.getRelatedComponents("structure_revisions");
				if (bvs != null && bvs.length > 0) {
					if(bvs[0]!=null)
					{
						component = bvs[0];
					}
				}
			}
			else if(comp instanceof TCComponentPseudoFolder)
			{
				TCComponentPseudoFolder pseudoFolder = (TCComponentPseudoFolder)comp;
				TCComponent owningTarget = pseudoFolder.getOwningComponent();
				component = owningTarget;
			}
			else{
				component = (TCComponent) comp;
			}
			TCComponent[] accessors = { session.getUser(),
					session.getCurrentGroup(), session.getCurrentRole() };
			
			boolean accessRight = accessControlService.checkAccessorsPrivilege(
					accessors,  component,  "WRITE" );
			return accessRight;
		} catch (TCException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}
	public static void insertRow(Sheet sheet, int insertRowIndex, int rowNum) {
		sheet.shiftRows(insertRowIndex, sheet.getLastRowNum(), rowNum);
		Row tempRow = sheet.getRow(insertRowIndex + rowNum);
		for (int i = insertRowIndex; i < insertRowIndex + rowNum; i++) {
			Row row = sheet.createRow(i);
			for (int j = 0; j < tempRow.getLastCellNum(); j++) {
				Cell tempCell = tempRow.getCell(j);
				if (tempCell != null) {
					row.createCell(j).setCellStyle(tempCell.getCellStyle());
				}
			}
		}
	}
	public static ArrayList<String> getLOVList(TCComponentListOfValues listOfValues) {
		ArrayList<String> lovValues = new ArrayList<String>();
		if (listOfValues == null) {
			return lovValues;
		}
		try {
			String lovType = listOfValues.getProperty("lov_type");

			if (Fnd0ListOfValuesDynamic.equals(lovType)) {
				
				String valueProperty=listOfValues.getProperty("fnd0lov_value");
				String descProperty=listOfValues.getProperty("fnd0lov_desc");
				String typeProperty=listOfValues.getProperty("fnd0query_type");
				LOVService lovService = LOVService.getService(Utils.getTCSession());

				InitialLovData input = new InitialLovData();
				LovFilterData filter = new LovFilterData();
				filter.order = 0;
				filter.numberToReturn = 100;
				filter.maxResults = 100;

				input.lov = listOfValues;
				input.filterData = filter;

				LOVSearchResults lovResult = lovService.getInitialLOVValues(input);
				if (lovResult.serviceData.sizeOfPartialErrors() < 1) {
					for (LOVValueRow row : lovResult.lovValues) {
						@SuppressWarnings("unchecked")
						Map<String, String[]> map = row.propDisplayValues;
						for (Entry<String, String[]> entry : map.entrySet()) {
							String key=entry.getKey();
							if(key.equals(valueProperty)){
								String[] values = entry.getValue();
								if (values != null && values.length > 0 && lovValues.contains(values[0]) == false) {
									lovValues.add(values[0]);
								}
							}
						}
					}
				}
			} else {
				ListOfValuesInfo lovi = listOfValues.getListOfValues();
				String[] displayValues = lovi.getLOVDisplayValues();
				for (String displayValue : displayValues) {
					String realValue = lovi.getRealValue(displayValue).toString();
					if (lovValues.contains(realValue) == false) {
						lovValues.add(realValue);
					}
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}

		return lovValues;
	}
	//将对象发送到结构管理器
	public static TCComponentBOMLine openBomWindow(TCComponent com){
		TCComponentBOMLine topBomline = null;
		try {
			TCComponentItemRevision rev = null;
			TCComponentItem item = null;
			TCComponentRevisionRuleType imancomponentrevisionruletype = (TCComponentRevisionRuleType) Utils.getTCSession()
					.getTypeComponent("RevisionRule");
			TCComponentRevisionRule imancomponentrevisionrule = imancomponentrevisionruletype.getDefaultRule();
			TCComponentBOMWindowType imancomponentbomwindowtype = (TCComponentBOMWindowType) Utils.getTCSession()
					.getTypeComponent("BOMWindow");
			TCComponentBOMWindow imancomponentbomwindow = imancomponentbomwindowtype
					.create(imancomponentrevisionrule);
			if(com instanceof TCComponentItem){
				item = (TCComponentItem) com;
				topBomline = imancomponentbomwindow.setWindowTopLine(item, item.getLatestItemRevision(), null, null);
			}
			else if(com instanceof TCComponentItemRevision){
				rev = (TCComponentItemRevision) com;
				topBomline = imancomponentbomwindow.setWindowTopLine(rev.getItem(), rev, null, null);
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return topBomline;
	}
	//判断字符串是否为空
	public static boolean isStringNull(String str){
		if(!"".equals(str) && str != null){
			return false;
		}
		return true;
	}
	// 升版
	public static TCComponentItemRevision doRevise(TCComponentItemRevision itemRev)
			throws Exception {
		System.out.println("doRevise");
			System.out.println("升版 ==== " + itemRev);
			boolean isModify = false;
			String item_id = itemRev.getProperty("item_id");
			String itemRevId = itemRev.getProperty("item_revision_id");
			String object_name = itemRev.getProperty("object_name");
			System.out.println("itemRevId == " + itemRevId);
			try {

				isModify = itemRev.getItem().okToModify();
			} catch (TCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (isModify) {
				DeepCopyInfo copyInfo = new DeepCopyInfo(itemRev,
						DeepCopyInfo.COPY_AS_OBJECT_ACTION, "", null, true,
						true, true);
				DeepCopyInfo[] copyInfos = new DeepCopyInfo[1];
				copyInfos[0] = copyInfo;
				TCComponentItemRevisionType itemRevType = (TCComponentItemRevisionType) itemRev
						.getTypeComponent();
				// String newRevId = itemRevType.getNewRevAlt(itemRev,
				// itemRevId);
				// String newRevId = itemRev.getNewRevAlt(itemRevId);
				String newRevId = itemRevType.getItemType().getNewRev(
						itemRev.getItem());
				System.out.println("newRevId == " + newRevId);
				try {
					TCComponentItemRevision newItemRev = itemRev.saveAs(
							newRevId, object_name, "", true, copyInfos);
					System.out.println("newItemRev == " + newItemRev);
					return newItemRev; 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return itemRev; 
	}
}
