package com.foxconn.mechanism.util;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.ServiceException;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.create.BOCreateDefinitionFactory;
import com.teamcenter.rac.common.create.CreateInstanceInput;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.common.create.SOAGenericCreateHelper;
import com.teamcenter.rac.kernel.DeepCopyInfo;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.InterfaceServerConnection;
import com.teamcenter.rac.kernel.NamedReferenceContext;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.TCAccessControlService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinition;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinitionType;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.kernel.TCTypeService;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.services.internal.loose.core._2008_06.DispatcherManagement;
import com.teamcenter.services.internal.rac.core.DispatcherManagementService;
import com.teamcenter.services.internal.rac.core._2008_06.DispatcherManagement.QueryDispatcherRequestsArgs;
import com.teamcenter.services.internal.rac.core._2008_06.DispatcherManagement.QueryDispatcherRequestsOutput;
import com.teamcenter.services.internal.rac.core._2008_06.DispatcherManagement.QueryDispatcherRequestsResponse;
import com.teamcenter.services.rac.administration.PreferenceManagementService;
import com.teamcenter.services.rac.administration._2012_09.PreferenceManagement.SetPreferencesAtLocationsIn;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2006_03.DataManagement.GenerateItemIdsAndInitialRevisionIdsProperties;
import com.teamcenter.services.rac.core._2006_03.DataManagement.GenerateItemIdsAndInitialRevisionIdsResponse;
import com.teamcenter.services.rac.core._2006_03.DataManagement.ItemIdsAndInitialRevisionIds;
import com.teamcenter.services.rac.core._2008_06.DispatcherManagement.CreateDispatcherRequestArgs;
import com.teamcenter.services.rac.core._2013_05.DataManagement.GenerateNextValuesIn;
import com.teamcenter.services.rac.core._2013_05.DataManagement.GenerateNextValuesResponse;
import com.teamcenter.services.rac.core._2013_05.DataManagement.GeneratedValue;
import com.teamcenter.services.rac.core._2013_05.DataManagement.GeneratedValuesOutput;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
@Deprecated
public class TCUtil {
	static TCTextService tcTextService = null;
	private static TCComponentItemRevisionType itemRevType = null;
	private static TCComponentDatasetType datasetType = null;
	private static TCComponentItemType itemType;
	private final static String ITEM_REVISION_TYPE = "ItemRevision";
	private static final String REGEXNUM = "[0-9]+";
	private final static String REGEXLETTER = "[a-zA-Z]+";	

	/**
	 * 获取程序桌面
	 * 
	 * @return
	 */
	public static AIFDesktop getDesktop() {
		return AIFDesktop.getActiveDesktop();
	}
	
	
	/**
	 * 弹出消息框
	 * 
	 * @return
	 */
	public static void infoMsgBox(String info) {
		MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, "弹出消息框",
				MessageBox.INFORMATION);
	}

	public static void warningMsgBox(String info) {
		MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, "警告", MessageBox.WARNING);
	}

	public static void errorMsgBox(String info) {
		MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, "错误", MessageBox.ERROR);
	}

	public static void centerShell(Shell shell) {
		int width = shell.getMonitor().getClientArea().width;
		int height = shell.getMonitor().getClientArea().height;
		int x = shell.getSize().x;
		int y = shell.getSize().y;
		if (x > width) {
			shell.getSize().x = width;
		}
		if (y > height) {
			shell.getSize().y = height;
		}
		shell.setLocation((width - x) / 2, (height - y) / 2);
	}

	
	/**
	 * 打开文件选择器
	 * @return	文件路径
	 */ 
	public static String openFileChooser(Shell shell){
		FileDialog fileDialog = new FileDialog(shell);
		fileDialog.setFilterPath(getSystemDesktop());
		fileDialog.setFilterNames(new String[] {"Microsoft Excel(*.xlsx)","Microsoft Excel(*.xls)"});
		fileDialog.setFilterExtensions(new String[] {"*.xlsx","*.xls"});
		return fileDialog.open();
	}
	
	
	/**
	 * 获取系统桌面路径
	 * @return
	 */
	public static String getSystemDesktop() {
		File desktopDir = FileSystemView.getFileSystemView() .getHomeDirectory();
		return desktopDir.getAbsolutePath();
	}
	
	
	public static TCSession getTCSession() {
		return RACUIUtil.getTCSession();
	}

	/**
	 * 创建对象
	 * 
	 * @param itemId
	 * @param itemTypeName
	 * @param itemName
	 * @return
	 */
	public static TCComponentItem createItem(String itemId, String itemTypeName, String itemName) {
		if (CommonTools.isEmpty(itemId)) {
			itemId = null;
		}
		try {
			if (itemType == null) {
				itemType = (TCComponentItemType) getTCSession().getTypeComponent(ITypeName.Item);
			}
			String currentRevId = itemType.getNewRev(null);
			TCComponentItem currentItem = itemType.create(itemId, currentRevId, itemTypeName, itemName, "", null);
			return currentItem;

		} catch (TCException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *创建对象
	 * 
	 * @param itemId
	 * @param itemTypeName
	 * @param itemName
	 * @return
	 * @throws TCException
	 */
	public static TCComponentItem createItem(String itemId, String itemTypeName, String itemName, String ruleMapping,
			String itemTypeRevName) throws TCException {
		if (CommonTools.isEmpty(itemId)) {
			itemId = null;
		}
		if (itemType == null) {
			itemType = (TCComponentItemType) getTCSession().getTypeComponent(ITypeName.Item);
		}
//		String currentRevId = itemType.getNewRev(null);
		String currentRevId = generateVersion(getTCSession(), ruleMapping, itemTypeRevName);
		TCComponentItem currentItem = itemType.create(itemId, currentRevId, itemTypeName, itemName, "", null);
		return currentItem;
	}

	/**
	 * 查找对象版本
	 * 
	 * @param itemId  ID
	 * @param version 版本
	 * @return
	 * @throws TCException
	 */
	public static TCComponentItemRevision findItemRev(String itemId, String version) throws TCException {
		if (itemRevType == null) {
			itemRevType = (TCComponentItemRevisionType) getTCSession().getTypeComponent(ITEM_REVISION_TYPE);
		}
		TCComponentItemRevision[] revs = itemRevType.findRevisions(itemId, version);
		if (revs == null || revs.length < 1) {
			return null;
		}
		return revs[0];
	}

	/**
	 * 查找数据集
	 * 
	 * @param datasetName 数据集名称
	 * @return
	 * @throws TCException
	 */
	public static TCComponentDataset findDataset(String datasetName) throws TCException {
		if (null == datasetType) {
			datasetType = (TCComponentDatasetType) getTCSession().getTypeComponent(ITypeName.Dataset);
		}
		TCComponentDataset dataset = datasetType.find(datasetName);
		if (null == dataset) {
			return null;
		}
		return dataset;
	}

	public static TCComponentItem findItem(String itemID) throws TCException {
		if (itemType == null) {
			itemType = (TCComponentItemType) getTCSession().getTypeComponent(ITypeName.Item);
		}
		TCComponentItem item = itemType.find(itemID);
		return item;
	}

	public static void openBroswer(String webSite) {
		try {
			Desktop desktop = Desktop.getDesktop();
			if (Desktop.isDesktopSupported() && desktop.isSupported(Desktop.Action.BROWSE)) {
				URI uri = new URI(webSite);
				desktop.browse(uri);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据分隔符获取hashmap值
	 * 
	 * @param session
	 * @param scope          首选项级别
	 * @param preferenceName 首选项名称
	 * @param separate
	 * @return
	 */
	public static HashMap<String, String> getHashMapPreference(TCSession session, int scope, String preferenceName,
			String separate) {
		String[] strArray = getArrayByPreference(session, scope, preferenceName);
		HashMap map = new HashMap();
		String key = "";
		String value = "";
		String[] tempArray = null;
		String temp = "";
		int length = 0;
		if (strArray == null || strArray.length == 0) {
			return null;
		}
		for (int i = 0; i < strArray.length; i++) {
			tempArray = strArray[i].split(separate);
			length = tempArray.length;
			if (length > 0) {
				if (length == 2) {
					map.put(tempArray[0], tempArray[1]);
				} else {
					temp = "";
					for (int j = 0; j < length - 1; j++) {
						temp = temp + tempArray[j + 1];
					}
					map.put(tempArray[0], temp);
				}
			} else {
				map.put(strArray[i], strArray[i]);
			}
		}
		return map;
	}

	/**
	 * 获取数组类型首选项值
	 * 
	 * @param session
	 * @param scope          首选项级别
	 * @param preferenceName 首选项名称
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String[] getArrayByPreference(TCSession session, int scope, String preferenceName) {
		try {
		TCPreferenceService preferenceService = session.getPreferenceService();
		preferenceService.refresh();
		String[] strArray = preferenceService.getStringArray(scope, preferenceName);
		return strArray;
		}catch(Exception e) {
			System.out.println(e);
		}
		return null;
	}

	/**
	 * 获取数组类型首选项值
	 * 
	 * @param session
	 * @param scope          首选项级别
	 * @param preferenceName 首选项名称
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static List<String> getArrayPreference(TCSession session, int scope, String preferenceName) {
		try {
			TCPreferenceService tCPreferenceService=session.getPreferenceService();
			tCPreferenceService.refresh();
		    String[] array = session.getPreferenceService().getStringArray(scope, preferenceName);
		    return Arrays.asList(array);
		}catch(Exception e) {
			System.out.print(e);
		}
		return null;
	}

	/**
	 * 获取字符串类型首选项值
	 * 
	 * @param session
	 * @param scope
	 * @param preferenceName
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getPreference(TCSession session, int scope, String preferenceName) {
		try {
			TCPreferenceService tCPreferenceService=session.getPreferenceService();
			tCPreferenceService.refresh();
		    return tCPreferenceService.getString(scope, preferenceName);
		}catch(Exception e) {
			System.out.print(e);
		}
		return null;
	}

	/**
	 * 判断是否为空
	 * 
	 * @param info
	 * @return
	 */
	public static boolean isNull(String info) {
		return info == null || info.trim().length() < 1;
	}

	public static boolean isNull(Map map) {
		return map == null || map.size() < 1;
	}

	/**
	 * 获取异常信息
	 * 
	 * @param e
	 * @return String
	 */
	public static String getExceptionMsg(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	/**
	 * 返回生成的流水号▏
	 * 
	 * @param session
	 * @return
	 */
	public static String generateIdBak(TCSession session, String ruleMapping) {
		String id = "";
		DataManagementService dataManagementService = DataManagementService.getService(session);
		GenerateNextValuesIn[] ins = new GenerateNextValuesIn[1];
		GenerateNextValuesIn in = new GenerateNextValuesIn();
		ins[0] = in;
		in.businessObjectName = "Design";
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

	/**
	 * 按照版本规则返回版本号
	 * 
	 * @param session
	 * @param ruleMapping
	 * @return
	 */
	public static String generateVersion(TCSession session, String ruleMapping, String itemTypeRevName) {
		String version = null;
		DataManagementService dataManagementService = DataManagementService.getService(session);
		GenerateNextValuesIn[] ins = new GenerateNextValuesIn[1];
		GenerateNextValuesIn in = new GenerateNextValuesIn();
		ins[0] = in;
//		in.businessObjectName = "CommercialPart Revision";
		in.businessObjectName = itemTypeRevName;
		in.clientId = "AutoAssignRAC";
		in.operationType = 1;
		Map<String, String> map = new HashMap<String, String>();
		map.put("item_revision_id", ruleMapping);
		in.propertyNameWithSelectedPattern = map;
		GenerateNextValuesResponse response = dataManagementService.generateNextValues(ins);
		GeneratedValuesOutput[] outputs = response.generatedValues;
		for (GeneratedValuesOutput result : outputs) {
			Map<String, GeneratedValue> resultMap = result.generatedValues;
			GeneratedValue generatedValue = resultMap.get("item_revision_id");
			version = generatedValue.nextValue;
		}
		return version;
	}

	public static String reviseVersion(TCSession session, String ruleMapping, String itemTypeRevName,
			String itemRevUid) {
		String version = null;
		try {
			DataManagementService dataManagementService = DataManagementService.getService(session);
			GenerateNextValuesIn[] ins = new GenerateNextValuesIn[1];
			GenerateNextValuesIn in = new GenerateNextValuesIn();
			ins[0] = in;
			in.businessObjectName = itemTypeRevName;
			in.clientId = "AutoAssignRAC";
			in.operationType = 2;

			Map<String, String> map = new HashMap<String, String>();
			map.put("item_revision_id", ruleMapping);
			in.propertyNameWithSelectedPattern = map;

			Map<String, String> map1 = new HashMap<String, String>();
			map1.put("sourceObject", itemRevUid);
			in.additionalInputParams = map1;

			GenerateNextValuesResponse response = dataManagementService.generateNextValues(ins);
			GeneratedValuesOutput[] outputs = response.generatedValues;
			for (GeneratedValuesOutput result : outputs) {
				Map<String, GeneratedValue> resultMap = result.generatedValues;
				GeneratedValue generatedValue = resultMap.get("item_revision_id");
				version = generatedValue.nextValue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return version;
	}

	/**
	 * 设置匹配规则
	 * 
	 * @param session
	 * @param mode
	 */
	public static boolean setRule(TCSession session, String mode, String itemType) {
		try {
			PreferenceManagementService preferenceManagementService = PreferenceManagementService.getService(session);
			SetPreferencesAtLocationsIn[] setPreferencesAtLocationsIns = new SetPreferencesAtLocationsIn[1];
			SetPreferencesAtLocationsIn in = new SetPreferencesAtLocationsIn();
			setPreferencesAtLocationsIns[0] = in;
			in.location.location = "User";
			// in.location.object
			in.preferenceInputs.preferenceName = "TC_LAST_USED_PATTERNS";
			// in.preferenceInputs.values = new String[] {"isTrue:" + itemType
			// +"Design:item_id:" + "\""+ mode + "\"" + "nnnnn"};
			in.preferenceInputs.values = new String[] {
					"isTrue:" + itemType + ":item_id:" + "\"" + mode + "\"" + "nnnnn" };
			ServiceData serviceData = preferenceManagementService
					.setPreferencesAtLocations(setPreferencesAtLocationsIns);
			if (serviceData.sizeOfPartialErrors() > 0) {
				throw new ServiceException("SessionService setbypass returned a partial error.");
			}
			return true;
		} catch (Exception e) {
			e.getMessage();
			return false;
		}
	}

	/**
	 * 生成流水码
	 * 
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unlikely-arg-type")
	public static Map<String, String> generateId(TCSession session, String itemType) {
		DataManagementService dataManagementService = DataManagementService.getService(session);
		GenerateItemIdsAndInitialRevisionIdsProperties[] generateItemIdsAndInitialRevisionIdsProperties = new GenerateItemIdsAndInitialRevisionIdsProperties[1];
		GenerateItemIdsAndInitialRevisionIdsProperties in = new GenerateItemIdsAndInitialRevisionIdsProperties();
		in.itemType = itemType;
		in.count = 1;
		generateItemIdsAndInitialRevisionIdsProperties[0] = in;
		// generateItemIdsAndInitialRevisionIdsProperties.item.
		GenerateItemIdsAndInitialRevisionIdsResponse response = dataManagementService
				.generateItemIdsAndInitialRevisionIds(generateItemIdsAndInitialRevisionIdsProperties);
		Map<String, String> resultMap = new HashMap<String, String>();
		Map<BigInteger, ItemIdsAndInitialRevisionIds[]> map = response.outputItemIdsAndInitialRevisionIds;
		for (Entry<BigInteger, ItemIdsAndInitialRevisionIds[]> entry : map.entrySet()) {
			System.out.println("key = " + entry.getKey());
			ItemIdsAndInitialRevisionIds[] outputs = entry.getValue();
			for (ItemIdsAndInitialRevisionIds result : outputs) {
				String newItemId = result.newItemId;
				System.out.println("==>> newItemId: " + newItemId);
				String newRevId = result.newRevId;
				System.out.println("==>> newRevId: " + newRevId);
				resultMap.put("id", newItemId);
				resultMap.put("version", newRevId);
			}
		}
		return resultMap;
	}

	/**
	 * 返回查询条件的真实属性名，提出不同的环境登录TC对于查询条件造成的干扰
	 * 
	 * @param name
	 * @return
	 */
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

	/**
	 * 搜索
	 * 
	 * @param searchName
	 * @param keys
	 * @param values
	 * @return
	 */
	public static List<InterfaceAIFComponent> search(String searchName, String[] keys, String[] values) {
		List<InterfaceAIFComponent> res;
		InterfaceAIFComponent[] temp;
		try {
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

	public static TCComponent[] executeQuery(TCSession session, String queryName, String[] keys, String[] values)
			throws Exception {
		TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
		TCComponentQuery query = (TCComponentQuery) imancomponentquerytype.find(queryName);
		TCQueryClause[] elements = query.describe();
		for (TCQueryClause element : elements) {
			// System.out.println(element.getAttributeName());
		}
		if (keys.length != values.length) {
			throw new Exception("queryAttributies length is not equal queryValues length");
		}
		String[] queryAttributeDisplayNames = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			for (TCQueryClause element : elements) {
				if (element.getAttributeName().equals(keys[i])) {
					queryAttributeDisplayNames[i] = element.getUserEntryNameDisplay();
				}
			}
			if (queryAttributeDisplayNames[i] == null || queryAttributeDisplayNames[i].equals("")) {
				throw new Exception("queryAttribute\"" + keys[i] + "不存在");
			}
		}
		// System.out.println("queryAttributeDisplayNames:" +
		// Arrays.toString(queryAttributeDisplayNames));
		// System.out.println("queryValues:" + Arrays.toString(values));
		return query.execute(queryAttributeDisplayNames, values);
	}

	/**
	 * 判断对象是否有写的权限
	 * 
	 * @param session
	 * @param com
	 * @return
	 */
	public static boolean checkUserPrivilege(TCSession session, TCComponent com) {
		boolean isWrite = false;
		try {
			TCAccessControlService service = session.getTCAccessControlService();
			// true代表有写的权限
			// false代表没有写的权限
			isWrite = service.checkPrivilege(com, TCAccessControlService.WRITE);
		} catch (TCException e) {
			e.printStackTrace();
		}
		return isWrite;
	}

	/**
	 * 下载数据集
	 * 
	 * @param dataset           数据集
	 * @param dir               文件存放路径
	 * @param fileExtensions    文件后缀名
	 * @param refName           命名的引用
	 * @param itemRevObjectName 对象版本名称
	 * @param rename            下载的数据集是否需要重命名 false代表无需重命名, true需要重命名
	 * @return
	 */
	public static String downloadFile(TCComponentDataset dataset, String dir, String fileExtensions, String refName,
			String itemRevObjectName, boolean rename) {
		File newFile = null;
		File exportFile = null;
		try {
			TCComponentTcFile[] tcfiles = dataset.getTcFiles();
			if (tcfiles == null || tcfiles.length == 0) {
				return "";
			}
			TCComponentTcFile tcfile = null;
			String fileName = null;
			for (int i = 0; i < tcfiles.length; i++) {
				tcfile = tcfiles[i];
				fileName = tcfile.getProperty("original_file_name");
//				System.out.println("==>> Physical file: " + fileName);
				if (fileName.toLowerCase().contains(fileExtensions)) {
					break;
				}
			}
			if (null == tcfile) {
				return "";
			}
			
			if (StrUtil.isBlank(fileName)) {
				return "";
			}
//			exportFile = dataset.getFile(refName, tcfile.toString(), dir);
			exportFile=tcfile.getFmsFile();				
							
			String newFileName = null;
			if (rename) {
				if (dir.endsWith("\\")) {
					newFileName = dir + itemRevObjectName + fileExtensions;
				} else {
					newFileName = dir + File.separator + itemRevObjectName + fileExtensions;
				}
				newFile = new File(newFileName);
				if (exportFile.exists() && exportFile.isFile()) {
					if (newFile.exists()) { // 判断新文件是否存在
						newFile.delete();
					}
					exportFile.renameTo(newFile);
				}
				return newFile.getAbsolutePath();
			} else if (StrUtil.isNotBlank(dir)) {
				if (dir.endsWith("\\")) {
					newFileName = dir + fileName;
				} else {
					newFileName = dir + File.separator + fileName;
				}
				
				newFile = new File(newFileName);
				if (exportFile.exists() && exportFile.isFile()) {
					if (newFile.exists()) { // 判断新文件是否存在
						newFile.delete();
					}
					exportFile.renameTo(newFile);
				}
				return newFile.getAbsolutePath();
			}			
			
			return exportFile.getAbsolutePath();
		} catch (TCException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 下载数据集
	 * 
	 * @param dataset        数据集
	 * @param dir            文件存放路径
	 * @param isCopy         是否复制
	 * @param fileExtensions 文件后缀名
	 * @param rename         下载的数据集是否需要重命名 false代表无需重命名, true需要重命名
	 * @return
	 */
	public static String downloadFile(TCComponentDataset dataset, String dir, boolean isCopy, String fileExtensions,
			TCSession session, String itemRevObjectName, boolean rename) {
		try {
			File newfile = null;
			File tempfile = null;
			String fileName = "";
			// 开启旁路
			// TCUtil.byPass(true);
			// session.enableBypass(true);
			TCComponentTcFile[] files = dataset.getTcFiles();
			if (files == null || files.length == 0) {
				return "";
			}
			// if (files.length == 1) {
			// exportFile = files[0].getFile(dir, rename);
			// return exportFile.getAbsolutePath();
			// }
			int k = 0;
			for (int i = 0; i < files.length; i++) {
				TCComponentTcFile onetcfile = files[i];
				fileName = onetcfile.getProperty("original_file_name");
				System.out.println("==>> Physical file: " + fileName);
				if (!fileName.toLowerCase().contains(fileExtensions)) {
					continue;
				}
				tempfile = onetcfile.getFmsFile();
				if (rename) {
					fileName = itemRevObjectName + "-" + String.valueOf(++k) + fileExtensions;
				}
				if (isCopy) {
					newfile = new File(dir + File.separator + fileName);
					// 判断文件是否存在
					if (newfile.exists()) {
						newfile.delete();
					}
					copyFile(tempfile, newfile);
				}
				// exportFile = files[0].getFile(dir, filename);
			}
			if (isCopy) {
				return newfile.getAbsolutePath();
			} else {
				return tempfile.getAbsolutePath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭旁路
			// TCUtil.byPass(false);
		}
		return "";
	}

	/**
	 * 复制文件
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File targetFile) throws IOException {
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
					} else if (!isNull(statusName) && name.replace(" ", "").equals(statusName)) {
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


	
	
	
	/**
	 *获取项目信息
	 * 
	 * @param TCComponent
	 * @return List<TCComponent>
	 */
	public static List<TCComponent> getProjects(TCComponent component) {
		List<TCComponent> prjLst = new ArrayList<TCComponent>();
		TCProperty props;
		try {
			props = component.getTCProperty("project_list");
			if (null == props) {
				return prjLst;
			}
			TCComponent[] coms = props.getReferenceValueArray();
			if (null == coms || coms.length < 1) {
				return prjLst;
			}
			prjLst = Arrays.asList(coms);
		} catch (TCException e) {
			e.printStackTrace();
		}
		return prjLst;
	}

	/**
	 * 获取Item
	 * 
	 * @param TCComponent
	 * @return TCComponent
	 */
	public static TCComponent getItem(TCComponent itemRevComp) {
		try {
			if (itemRevComp instanceof TCComponentItemRevision) {
				TCComponentItemRevision itemRev = (TCComponentItemRevision) itemRevComp;
				if (itemRev != null) {
					return itemRev.getItem();
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断指定对象关系是否存在
	 * 
	 * @param folder
	 * @param itemComp
	 * @param sRel
	 * @return boolean
	 */
	public static boolean isExistItem(TCComponentFolder folder, TCComponent itemComp, String sRel) {
		try {
			TCComponent[] items = folder.getRelatedComponents(sRel);
			for (TCComponent tcComp : items) {
				TCComponentItem tempItem = null;
				if (tcComp instanceof TCComponentItem) {
					tempItem = (TCComponentItem) tcComp;
				} else if (tcComp instanceof TCComponentItemRevision) {
					tempItem = ((TCComponentItemRevision) tcComp).getItem();
				}
				if (tempItem != null) {
					if (tempItem.getUid().equals(itemComp.getUid())) {
						return true;
					}
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 开旁路
	 * 
	 * @param session
	 * @throws Exception
	 */
	public static void setBypass(TCSession session) {
		try {
			TCUserService userService = session.getUserService();
			userService.call("set_bypass", new String[] { "" });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭旁路
	 * 
	 * @param session
	 * @throws Exception
	 */
	public static void closeBypass(TCSession session) {
		try {
			TCUserService userService = session.getUserService();
			userService.call("close_bypass", new String[] { "" });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
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

	/*
	 *将对象发送到结构管理器
	 */
	public static TCComponentBOMLine openBomWindow(TCSession session, TCComponent com) {
		TCComponentBOMLine topBomline = null;
		try {
			TCComponentItemRevision rev = null;
			TCComponentItem item = null;
			TCComponentRevisionRuleType imancomponentrevisionruletype = (TCComponentRevisionRuleType) session
					.getTypeComponent("RevisionRule");
			TCComponentRevisionRule imancomponentrevisionrule = imancomponentrevisionruletype.getDefaultRule();
			TCComponentBOMWindowType imancomponentbomwindowtype = (TCComponentBOMWindowType) session
					.getTypeComponent("BOMWindow");
			TCComponentBOMWindow imancomponentbomwindow = imancomponentbomwindowtype.create(imancomponentrevisionrule);
			if (com instanceof TCComponentItem) {
				item = (TCComponentItem) com;
				topBomline = imancomponentbomwindow.setWindowTopLine(item, item.getLatestItemRevision(), null, null);
			} else if (com instanceof TCComponentItemRevision) {
				rev = (TCComponentItemRevision) com;
				topBomline = imancomponentbomwindow.setWindowTopLine(rev.getItem(), rev, null, null);
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return topBomline;
	}

	/**
	 * 
	 * @param rootLine
	 * @param lines
	 * @param unpacked 是否解包 true 解包 false 不解包
	 * @return
	 * @throws TCException
	 */
	public static List<TCComponentBOMLine> getTCComponmentBOMLines(TCComponentBOMLine rootLine,
			List<TCComponentBOMLine> lines, boolean unpacked) throws TCException {
		if (lines == null) {
			lines = new ArrayList<TCComponentBOMLine>();
		}
		AIFComponentContext[] componmentContext = rootLine.getChildren();
		if (componmentContext != null) {
			for (int i = 0; i < componmentContext.length; i++) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) componmentContext[i].getComponent();
				if (unpacked) {
					if (bomLine.isPacked()) {
						TCComponentBOMLine[] packedLines = bomLine.getPackedLines();
						bomLine.unpack();
						if (packedLines != null && packedLines.length > 0) {
							lines.add(bomLine);
							lines.addAll(Arrays.asList(packedLines));
						} else {
							lines.add(bomLine);
						}
					} else {
						lines.add(bomLine);
					}
				} else {
					lines.add(bomLine);
				}
				getTCComponmentBOMLines(bomLine, lines, unpacked);
			}
		}
		return lines;
	}

	/**
	 *获取TC服务器主机信息
	 * 
	 * @return
	 * @throws TCException
	 */
	public static InterfaceServerConnection getServer(TCSession session) throws TCException {
		InterfaceServerConnection serverConnection = session.getServerConnection();
		System.out.println("==>> 当前登陆服务器主机ip为: " + serverConnection.getHost());
		System.out.println("==>> 当前登陆服务器主机名为: " + serverConnection.getName());
		return serverConnection;
	}

	/**
	 *获取数据集文件
	 * 
	 * @param ds
	 * @param fileExtensions
	 * @return
	 */
	public static File getDataSetFile(TCComponentDataset ds, String fileExtensions) {
		File file = null;
		TCComponentTcFile tcfile = null;
		try {
			TCComponentTcFile[] tcFiles = ds.getTcFiles();
			if (tcFiles.length > 0) {
				for (int i = 0; i < tcFiles.length; i++) {					
					String fileName = tcFiles[i].getProperty("original_file_name");
					if (fileName.toLowerCase().endsWith(fileExtensions)) {
						tcfile = tcFiles[i];
						break;
					}
				}
			}

			if (CommonTools.isNotEmpty(tcfile)) {
				file = tcfile.getFmsFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * 创建数据集
	 * 
	 */
	public static TCComponentDataset createDataSet(TCSession session, String selectFilepath, String dsType,
			String dsName, String ref_type) {
		TCComponentDataset dataset = null;
		try {
			TCTypeService type = session.getTypeService();
			TCComponentDatasetType datasettype = (TCComponentDatasetType) type.getTypeComponent("Dataset");
			dataset = datasettype.create(dsName, "", dsType);
			String p[] = new String[1];
			String n[] = new String[1];
			p[0] = selectFilepath;
//			System.out.println("selectFilepath == " + selectFilepath);
			n[0] = ref_type;
//			System.out.println("ref_type == " + ref_type);
			dataset.setFiles(p, n);
		} catch (TCException e) {
			MessageBox.post("数据集创建错误！", "提示", 2);
			e.printStackTrace();
		}
		return dataset;
	}

	/**
	 * 更新数据集
	 * 
	 * @return
	 */
	public static void updateDataset(TCSession session, TCComponent itemComp, String fileFullPath) throws TCException {
		try {
			session.enableBypass(true);
			TCComponentItemRevision itemRev = (TCComponentItemRevision) itemComp;
			TCComponentDataset findDataSet = TCUtil.findDataSet((TCComponentItemRevision) itemComp,
					fileFullPath.split("\\\\")[fileFullPath.split("\\\\").length - 1]);
			if (findDataSet != null) {
				updateDataset(session, findDataSet, fileFullPath);
			}
		} finally {
			session.enableBypass(false);
		}
	}

	public static void updateDataset(TCSession session, TCComponentDataset dataset, String filePath) {
		try {
			TCComponentDatasetDefinitionType dsdefType = (TCComponentDatasetDefinitionType) session
					.getTypeComponent("DatasetType");
			TCComponentDatasetDefinition definition = dsdefType.find(dataset.getType());
			NamedReferenceContext[] contexts = definition.getNamedReferenceContexts();
			String namedReference = contexts[0].getNamedReference();
//			System.out.println("namedReference==" + namedReference);

			dataset.removeNamedReference(namedReference);

			dataset.setFiles(new String[] { filePath }, new String[] { namedReference });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param itemRev
	 * @param findFileName
	 * @return TCComponentDataset
	 * @throws TCException
	 */
	public static TCComponentDataset findDataSet(TCComponentItemRevision itemRev, String findFileName)
			throws TCException {
		TCComponent[] datesets = itemRev.getRelatedComponents("IMAN_specification");
		for (TCComponent tcComponent : datesets) {
			if (tcComponent instanceof TCComponentDataset) {
				TCComponentDataset tcComponentDataset = (TCComponentDataset) tcComponent;
				for (String fileName : tcComponentDataset.getFileNames(null)) {
					if (fileName.equalsIgnoreCase(findFileName)) {
						return tcComponentDataset;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 链接数据集
	 * 
	 * @return
	 */
	public static void linkDataSet(TCSession session, TCComponent itemComp, String fileFullPath, String fileName)
			throws TCException {
		try {
			session.enableBypass(true);
			TCComponentItemRevision itemRev = (TCComponentItemRevision) itemComp;
			TCComponentDataset findDataSet = TCUtil.findDataSet((TCComponentItemRevision) itemComp,
					fileFullPath.split("\\\\")[fileFullPath.split("\\\\").length - 1]);
			if (findDataSet != null) {
				itemRev.remove("IMAN_specification", findDataSet);
			}

			TCComponentDataset dataSet = TCUtil.createDataSet(session, fileFullPath, "Text", fileName, "Text");
			if (itemComp instanceof TCComponentItemRevision) {
				itemRev.add("IMAN_specification", dataSet);
			}
		} finally {
			session.enableBypass(false);
		}
	}

	/**
	 * 创建对象	
	 */
	public static TCComponent createCom(TCSession session, String itemTypeName, String itemID, String name,
			String revisionID, Map<String, String> revisionMap) {
		IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session,
				itemTypeName);
		CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);

		createInstanceInput.add("item_id", itemID);
		createInstanceInput.add("object_name", name);

		IBOCreateDefinition createDefinitionRev = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session,
				itemTypeName + "Revision");
		CreateInstanceInput createInstanceInputRev = new CreateInstanceInput(createDefinitionRev);

		if (revisionMap == null) {
			revisionMap = new HashMap<String, String>();
		}
		createInstanceInputRev.add("item_revision_id", revisionID);
		for (Entry<String, String> entry : revisionMap.entrySet()) {
			String p = entry.getKey();
			String v = entry.getValue();
			if (isNull(v)) {
				continue;
			}
			createInstanceInputRev.add(p, v);
		}

		ArrayList iputList = new ArrayList();
		iputList.add(createInstanceInput);

		ArrayList list = new ArrayList(0);
		list.addAll(iputList);

		createInstanceInput.addSecondaryCreateInput(createDefinition.REVISION, createInstanceInputRev);
		TCComponent obj = null;
		List comps = null;
		try {
			comps = SOAGenericCreateHelper.create(session, createDefinition, list);
			obj = (TCComponent) comps.get(0);
		} catch (TCException e) {
			e.printStackTrace();
		}

		return obj;
	}

	/**
	 * 创建对象	
	 */
	public static TCComponent createCom(TCSession session, String itemTypeName, String itemID,
			Map<String, Object> revisionMap) throws TCException {
		IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session,
				itemTypeName);
		CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);

		if (!isNull(itemID))
			createInstanceInput.add("item_id", itemID);
//		createInstanceInput.add("object_name", name);

		IBOCreateDefinition createDefinitionRev = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session,
				itemTypeName + "Revision");
		CreateInstanceInput createInstanceInputRev = new CreateInstanceInput(createDefinitionRev);

		if (revisionMap == null) {
			revisionMap = new HashMap<String, Object>();
		}
//		createInstanceInputRev.add("item_revision_id", revisionID);
		for (Entry<String, Object> entry : revisionMap.entrySet()) {
			String p = entry.getKey();
			Object v = entry.getValue();
			if (null == v) {
				continue;
			}
			createInstanceInputRev.add(p, v);
		}

		ArrayList iputList = new ArrayList();
		iputList.add(createInstanceInput);

		ArrayList list = new ArrayList(0);
		list.addAll(iputList);

		createInstanceInput.addSecondaryCreateInput(createDefinition.REVISION, createInstanceInputRev);
		TCComponent obj = null;
		List comps = null;
//		try {
		comps = SOAGenericCreateHelper.create(session, createDefinition, list);
		obj = (TCComponent) comps.get(0);
//		} catch (TCException e) {
//			e.printStackTrace();
//		}

		return obj;
	}

	public static TCComponent createCom(TCSession session, String typeName) {
		// TODO Auto-generated method stub
		IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session,
				typeName);
		CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);
		ArrayList iputList = new ArrayList();
		iputList.add(createInstanceInput);

		TCComponent obj = null;
		List comps = null;
		try {
			comps = SOAGenericCreateHelper.create(session, createDefinition, iputList);
			obj = (TCComponent) comps.get(0);
		} catch (TCException e) {
			e.printStackTrace();
		}

		return obj;
	}



	public static TCComponentBOMWindow createBOMWindow(TCSession session) {
		TCComponentBOMWindow window = null;
		try {
			TCComponentRevisionRuleType imancomponentrevisionruletype = (TCComponentRevisionRuleType) session
					.getTypeComponent("RevisionRule");
			TCComponentRevisionRule imancomponentrevisionrule = imancomponentrevisionruletype.getDefaultRule();
			TCComponentBOMWindowType imancomponentbomwindowtype = (TCComponentBOMWindowType) session
					.getTypeComponent("BOMWindow");
			window = imancomponentbomwindowtype.create(imancomponentrevisionrule);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return window;
	}

	public static TCComponentBOMLine getTopBomline(TCComponentBOMWindow bomWindow, TCComponent com) {
		TCComponentBOMLine topBomline = null;
		try {
			TCComponentItemRevision rev = null;
			TCComponentItem item = null;
			if (com instanceof TCComponentItem) {
				item = (TCComponentItem) com;
				topBomline = bomWindow.setWindowTopLine(item, item.getLatestItemRevision(), null, null);
			} else if (com instanceof TCComponentItemRevision) {
				rev = (TCComponentItemRevision) com;
				topBomline = bomWindow.setWindowTopLine(rev.getItem(), rev, null, null);
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return topBomline;
	}

	/**
	 * 匹配字符串是否全部为数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean matcherNum(String str) {
		return str.matches(REGEXNUM);
	}

	/**
	 * 匹配字符串是否全部为字母
	 * 
	 * @param str
	 * @return
	 */
	public static boolean matcherLetter(String str) {
		return str.matches(REGEXLETTER);
	}

	/**
	 * 去掉字符串中的前后空格、回车、换行符、制表符 value
	 *
	 * @return
	 */
	public static String removeBlank(String value) {
		String result = "";
		if (value != null) {
			Pattern p = Pattern.compile("|\t|\r|\n");
			Matcher m = p.matcher(value);
			result = m.replaceAll("");
			result = result.trim();
		} else {
			result = value;
		}
		return result;
	}
	
	
	/**
	 * 判断字符串是否为日期格式
	 * @param strDate
	 * @return
	 */
	public static boolean isDate(String strDate) {
		if (CommonTools.isEmpty(strDate)) {
			return false;
		}

		Pattern pattern = Pattern.compile(
				"^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))"
						+ "[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))"
						+ "[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|("
						+ "[1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
		Matcher matcher = pattern.matcher(strDate);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	
	public static File callTemplate(TCSession session, String templateName) {
		TCComponentDatasetType dsType = null;
		File file = null;
		try {
			dsType = (TCComponentDatasetType) session.getTypeComponent("Dataset");
			TCComponentDataset templeDataset = dsType.find(templateName);
			if (CommonTools.isEmpty(templeDataset)) {
				MessageBox.post("找不到" + templateName + " 模板", "錯誤", MessageBox.ERROR);
				return null;
			}
			file = getDataSetFile(templeDataset, ".xlsx");			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (CommonTools.isEmpty(file)) {
			MessageBox.post("獲取模板“Excel”相關物理文件失敗", "錯誤", MessageBox.ERROR);
			return null;
		}
		return file;
	}
	
	
	public static File openSaveFileDialog(Shell shell, String fileName) {
		File destFile = null;
		FileDialog dlg = new FileDialog(shell, SWT.SAVE);	
		dlg.setFilterPath(getSystemDesktop());	
		System.out.println("==>> fileName: " + fileName);
		dlg.setFileName(CommonTools.removeFileSpecicalStr(fileName));		
		dlg.setFilterExtensions(new String[] {"*.xlsx"}); //设置打开文件扩展名
		dlg.setFilterNames(new String[] {"Excel Files (*.xlsx)"}); // 输入文件名时不用加.xlsx		
		boolean done = false;
		String saveFileName = null;
		while (!done) {
			saveFileName = dlg.open();
			if (CommonTools.isEmpty(saveFileName)) {
				org.eclipse.swt.widgets.MessageBox msg = new org.eclipse.swt.widgets.MessageBox(dlg.getParent(), SWT.ICON_WARNING| SWT.YES); // User has cancelled, so quit and return
				msg.setText("提示");
				msg.setMessage("你取消了保存文件");
				done = msg.open() == SWT.YES;
                done = true;
			} else {
				File file = new File(saveFileName);
				if (file.exists()) { // 判斷文件是否已經存在
					org.eclipse.swt.widgets.MessageBox msg = new org.eclipse.swt.widgets.MessageBox(dlg.getParent(), SWT.ICON_WARNING | SWT.YES | SWT.NO); // The file already exists; asks for confirmation
					msg.setText("提示");
					msg.setMessage(saveFileName + " 已經存在，是否要替換它？");
					done = msg.open() == SWT.YES; // If they click Yes, drop out. If they click No, redisplay the File Dialog
				} else {
					done = true; //不存在文件名重複，可以保存
				}
			}
		}
		if (CommonTools.isNotEmpty(saveFileName)) {
			destFile = new File(saveFileName);
		}
		return destFile;
	}
}
