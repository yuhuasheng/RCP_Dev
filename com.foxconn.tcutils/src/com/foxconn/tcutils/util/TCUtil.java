package com.foxconn.tcutils.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.create.BOCreateDefinitionFactory;
import com.teamcenter.rac.common.create.CreateInstanceInput;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.common.create.ICreateInstanceInput;
import com.teamcenter.rac.common.create.SOAGenericCreateHelper;
import com.teamcenter.rac.ets.external.DispatcherRequestFactory;
import com.teamcenter.rac.ets.external.RequestResult;
import com.teamcenter.rac.kernel.DeepCopyInfo;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.ListOfValuesInfo;
import com.teamcenter.rac.kernel.NamedReferenceContext;
import com.teamcenter.rac.kernel.ServiceData;
import com.teamcenter.rac.kernel.TCAccessControlService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentContextList;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinition;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinitionType;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCComponentParticipant;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentProcessType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentTaskTemplateType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.kernel.TCTypeService;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core.LOVService;
import com.teamcenter.services.rac.core.ReservationService;
import com.teamcenter.services.rac.core._2006_03.DataManagement.GenerateItemIdsAndInitialRevisionIdsProperties;
import com.teamcenter.services.rac.core._2006_03.DataManagement.GenerateItemIdsAndInitialRevisionIdsResponse;
import com.teamcenter.services.rac.core._2006_03.DataManagement.ItemIdsAndInitialRevisionIds;
import com.teamcenter.services.rac.core._2007_01.DataManagement.CreateOrUpdateFormsResponse;
import com.teamcenter.services.rac.core._2007_01.DataManagement.FormInfo;
import com.teamcenter.services.rac.core._2008_06.DataManagement.AddParticipantOutput;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse;
import com.teamcenter.services.rac.core._2008_06.DataManagement.Participants;
import com.teamcenter.services.rac.core._2013_05.DataManagement.GenerateNextValuesIn;
import com.teamcenter.services.rac.core._2013_05.DataManagement.GenerateNextValuesResponse;
import com.teamcenter.services.rac.core._2013_05.DataManagement.GeneratedValue;
import com.teamcenter.services.rac.core._2013_05.DataManagement.GeneratedValuesOutput;
import com.teamcenter.services.rac.core._2013_05.LOV.InitialLovData;
import com.teamcenter.services.rac.core._2013_05.LOV.LOVSearchResults;
import com.teamcenter.services.rac.core._2013_05.LOV.LOVValueRow;
import com.teamcenter.services.rac.core._2013_05.LOV.LovFilterData;
import com.teamcenter.services.rac.workflow.WorkflowService;
import com.teamcenter.services.rac.workflow._2007_06.Workflow.ReleaseStatusInput;
import com.teamcenter.services.rac.workflow._2007_06.Workflow.ReleaseStatusOption;
import com.teamcenter.services.rac.workflow._2007_06.Workflow.SetReleaseStatusResponse;
import com.teamcenter.soa.client.model.ErrorStack;
import com.teamcenter.soa.client.model.Type;

import cn.hutool.core.util.StrUtil;

public class TCUtil
{
    private static TCComponentItemRevisionType itemRevType   = null;
    private static TCComponentDatasetType      datasetType   = null;
    private static TCComponentItemType         itemType;
    private static TCTextService               tcTextService = null;
    private static final String                D9_PREFIX     = "d9_";
    private static final String                POC_STR       = "IR_";

    public static AIFDesktop getDesktop()
    {
        return AIFDesktop.getActiveDesktop();
    }

    public static void centerShell(Shell shell)
    {
        int width = shell.getMonitor().getClientArea().width;
        int height = shell.getMonitor().getClientArea().height;
        int x = shell.getSize().x;
        int y = shell.getSize().y;
        if (x > width)
        {
            shell.getSize().x = width;
        }
        if (y > height)
        {
            shell.getSize().y = height;
        }
        shell.setLocation((width - x) / 2, (height - y) / 2);
    }

    /**
     * 打开文件选择器
     * 
     * @return 文件路径
     */
    public static String openFileChooser(Shell shell)
    {
        FileDialog fileDialog = new FileDialog(shell);
        fileDialog.setFilterPath(getSystemDesktop());
        fileDialog.setFilterNames(new String[] { "Microsoft Excel(*.xlsx)", "Microsoft Excel(*.xls)" });
        fileDialog.setFilterExtensions(new String[] { "*.xlsx", "*.xls" });
        return fileDialog.open();
    }

    
    public static String openCADFileChooser(Shell shell) {
    	FileDialog fileDialog = new FileDialog(shell);
    	fileDialog.setFilterPath(getSystemDesktop());
    	fileDialog.setFilterNames(new String[] {"CAD文件(*.dwg)", "CAD图形文件(*.dxf)"});
    	fileDialog.setFilterExtensions(new String[] {"*.dwg", "*.dxf"});
    	return fileDialog.open();
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
    /**
	 * 文件选择器
	 * @param fileName
	 * @return
	 */
	public static File openFileChooser(String fileName) {
		JFileChooser jFileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel", "xlsx");
		jFileChooser.setFileFilter(filter);
		jFileChooser.setSelectedFile(new File(fileName + ".xlsx"));
		int openDialog = jFileChooser.showSaveDialog(null);
		if (openDialog == JFileChooser.APPROVE_OPTION) {
			File file = jFileChooser.getSelectedFile();
			String fname = jFileChooser.getName(file);
			if (fname.indexOf(".xlsx") == -1) {
				file = new File(jFileChooser.getCurrentDirectory(), fname + ".xlsx");
			}
			return file;
		}
		return null;
	}
	
    /**
     * 获取系统桌面路径
     * 
     * @return
     */
    public static String getSystemDesktop()
    {
        File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
        return desktopDir.getAbsolutePath();
    }

    public static TCSession getTCSession()
    {
        return RACUIUtil.getTCSession();
    }

    /**
     * 弹出消息框
     * 
     * @return
     */
    public static void infoMsgBox(String info, String title)
    {
        if (CommonTools.isEmpty(title))
        {
            MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, "弹出消息框", MessageBox.INFORMATION);
        }
        else
        {
            MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, title, MessageBox.INFORMATION);
        }
    }

    public static void warningMsgBox(String info, String title)
    {
        if (CommonTools.isEmpty(title))
        {
            MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, "警告", MessageBox.WARNING);
        }
        else
        {
            MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, title, MessageBox.WARNING);
        }
    }

    public static void errorMsgBox(String info, String title)
    {
        if (CommonTools.isEmpty(title))
        {
            MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, "错误", MessageBox.ERROR);
        }
        else
        {
            MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, title, MessageBox.ERROR);
        }
    }

    
    public static void writeInfoLog(String message, StyledText styledText, StyleRange infoStyle) {
    	
    	Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				if (!styledText.isDisposed()) {
					String msg = "";
					if (!message.contains("INFO")) {
						msg = "【INFO】 " + message;
					} else {
						msg = message;
					}					
					styledText.append(msg + "\n");
					
					infoStyle.start = styledText.getCharCount() - msg.length() - 1;
					infoStyle.length = msg.length();
					styledText.setStyleRange(infoStyle);
				}
			}
		});
    }
    
    
    public static void writeWarnLog(String message, StyledText styledText, StyleRange warnStyle) {
    	
    	Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				if (!styledText.isDisposed()) {
					String msg = "";
					if (!message.contains("WARN")) {
						msg = "【WARN】 " + message;
					} else {
						msg = message;
					}
					styledText.append(msg + "\n");
					
					warnStyle.start = styledText.getCharCount() - msg.length() - 1;
					warnStyle.length = msg.length();
					styledText.setStyleRange(warnStyle);
				}
			}
		});
    }
    
    
    public static void writeErrorLog(String message, StyledText styledText, StyleRange errorStyle) {
    	
    	Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				if (!styledText.isDisposed()) {
					String msg = "";
					if (!message.contains("ERROR")) {
						msg = "【ERROR】 " + message;
					} else {
						msg = message;
					}					
					styledText.append(msg + "\n");
					
					errorStyle.start = styledText.getCharCount() - msg.length() - 1;
					errorStyle.length = msg.length();
					styledText.setStyleRange(errorStyle);
				}
			}
		});
    }
    
    
    /**
     * 创建零组件
     * 
     * @param itemId
     * @param itemTypeName
     * @param itemName
     * @return
     */
    public static TCComponentItem createItem(String itemId, String itemTypeName, String itemName)
    {
        if (CommonTools.isEmpty(itemId))
        {
            itemId = null;
        }
        try
        {
            if (itemType == null)
            {
                itemType = (TCComponentItemType) getTCSession().getTypeComponent(ITypeName.Item);
            }
            String currentRevId = itemType.getNewRev(null);
            TCComponentItem currentItem = itemType.create(itemId, currentRevId, itemTypeName, itemName, "", null);
            return currentItem;
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建对象
     * 
     * @param itemId
     * @param itemTypeName
     * @param itemName
     * @return
     * @throws TCException
     */
    public static TCComponentItem createItem(String itemId, String itemTypeName, String itemName, String ruleMapping, String itemTypeRevName) throws TCException
    {
        if (CommonTools.isEmpty(itemId))
        {
            itemId = null;
        }
        if (itemType == null)
        {
            itemType = (TCComponentItemType) getTCSession().getTypeComponent(ITypeName.Item);
        }
        
        // String currentRevId = itemType.getNewRev(null);
        String currentRevId = generateVersion(getTCSession(), ruleMapping, itemTypeRevName);
        TCComponentItem currentItem = itemType.create(itemId, currentRevId, itemTypeName, itemName, "", null);
        return currentItem;
    }

    
    /**
     * 創建文件夾
     * 
     * @param session
     * @param type
     * @param name
     * @return
     */
    public static TCComponentFolder createFolder(TCSession session, String type, String name)
    {
        ArrayList iputList = new ArrayList();
        IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session, type);
        CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);
        createInstanceInput.add("object_name", name);
        iputList.add(createInstanceInput);
        List comps = null;
        TCComponentFolder folder = null;
        try
        {
            comps = SOAGenericCreateHelper.create(session, createDefinition, iputList);
            folder = (TCComponentFolder) comps.get(0);
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return folder;
    }
    
    
    /**
     * 批量创建对象
     * @param session
     * @param type
     * @param map
     * @param length
     * @return
     * @throws ServiceException 
     */
    public static TCComponent[] createObjects(TCSession session, String type, List<Map<String, Object>> list) throws TCException, ServiceException {     	
		if (CommonTools.isEmpty(type)) {
			System.out.println("==>> type is null");
			return null;
		}
    	
    	DataManagementService dmService = DataManagementService.getService(session); 
    	int length = list.size();
    	CreateIn[] createIn = new CreateIn[length];
    	for (int i = 0; i < length; i++) {
    		createIn[i] = new CreateIn();
    		createIn[i].data.boName = type;
    		Map<String, Object> map = list.get(i);
    		Map<String, String> strMap = new HashMap<String, String>();
    		Map<String, BigInteger> intMap = new HashMap<String, BigInteger>();
    			
			map.forEach((key, value) -> {
				if (value instanceof String) {
					strMap.put(key, value == null ? "" : value.toString());
				} else if (value instanceof Integer) {
					Integer intValue = (Integer) value;
					intMap.put(key, BigInteger.valueOf(intValue));
				}
			});
    		createIn[i].data.stringProps = strMap; 
    		createIn[i].data.intProps = intMap;
		}
    	
    	CreateResponse response = dmService.createObjects(createIn);
    	int sizeOfPartialErrors = response.serviceData.sizeOfPartialErrors();
    	String errorMsg = "";
    	for (int i = 0; i < sizeOfPartialErrors; i++) {
			ErrorStack partialError = response.serviceData.getPartialError(i);
			String[] messages = partialError.getMessages();
			errorMsg += Stream.of(messages).collect(Collectors.joining(","));
			
		}
    	
    	if (CommonTools.isNotEmpty(errorMsg)) {
    		throw new TCException(errorMsg);
		}
    	
    	int count = response.output.length;
    	if (count > 0) {
    		TCComponent[] createComponents = new TCComponent[count];
    		for (int i = 0; i < count; i++) {
				createComponents[i] = response.output[i].objects[0];
			}
    		return createComponents;
		}
	
    	return null;    	
    }
    
    
    public static String generateId(TCSession session, String type) throws Exception {
    	DataManagementService dmService = DataManagementService.getService(session);
    	com.teamcenter.services.rac.core._2014_10.DataManagement.GenerateIdInput generateIdInput = new com.teamcenter.services.rac.core._2014_10.DataManagement.GenerateIdInput();
        com.teamcenter.services.rac.core._2008_06.DataManagement.CreateInput createInput = new com.teamcenter.services.rac.core._2008_06.DataManagement.CreateInput();
        createInput.boName = type;
        generateIdInput.quantity = 1;
        generateIdInput.propertyName = "item_id";
        generateIdInput.createInput = createInput;
        System.out.print(generateIdInput);
        com.teamcenter.services.rac.core._2014_10.DataManagement.GenerateIdsResponse response = dmService.generateIdsUsingIDGenerationRules(new com.teamcenter.services.rac.core._2014_10.DataManagement.GenerateIdInput[]{generateIdInput});
        String result = getErrorMsg(response.serviceData);
        if (CommonTools.isNotEmpty(result)) {
            throw new Exception(result);
        }
        String id = response.generateIdsOutput[0].generatedIDs[0]; // 生成流水码
        System.out.println("==>> id: " + id);
        return id;
    }    
    
   
    
    public static String generateId(TCSession session, String ruleMapping, String type) {
    	String id = "";
		DataManagementService dataManagementService = DataManagementService.getService(session);
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
    
    
    /**
	 * 生成流水码
	 * 
	 * @param session
	 * @return
	 */
	@SuppressWarnings("unlikely-arg-type")
	public static Map<String, String> generateIdNew(TCSession session, String itemType) {
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
     * 按照版本规则返回版本号
     * 
     * @param session
     * @param ruleMapping
     * @return
     */
    public static String generateVersion(TCSession session, String ruleMapping, String itemTypeRevName)
    {
        String version = null;
        DataManagementService dataManagementService = DataManagementService.getService(session);
        GenerateNextValuesIn[] ins = new GenerateNextValuesIn[1];
        GenerateNextValuesIn in = new GenerateNextValuesIn();
        ins[0] = in;
        // in.businessObjectName = "CommercialPart Revision";
        in.businessObjectName = itemTypeRevName;
        in.clientId = "AutoAssignRAC";
        in.operationType = 1;
        Map<String, String> map = new HashMap<String, String>();
        map.put("item_revision_id", ruleMapping);
        in.propertyNameWithSelectedPattern = map;
        GenerateNextValuesResponse response = dataManagementService.generateNextValues(ins);
        GeneratedValuesOutput[] outputs = response.generatedValues;
        for (GeneratedValuesOutput result : outputs)
        {
            Map<String, GeneratedValue> resultMap = result.generatedValues;
            GeneratedValue generatedValue = resultMap.get("item_revision_id");
            version = generatedValue.nextValue;
        }
        return version;
    }

    /**
     * 创建Item
     * 
     * @param session
     * @param itemId
     * @param itemRev
     * @param itemName
     * @param itemTypeName
     * @return
     */
    public static TCComponentItem create2Item(TCSession session, String itemId, String itemRev, String itemName, String itemTypeName)
    {
        TCComponentItem item = null;
        try
        {
            TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent(itemTypeName);
            if (itemId.equals(""))
            {
                itemId = itemType.getNewID();
            }
            if (itemRev.equals(""))
            {
                itemRev = itemType.getNewRev(null);
            }
            item = itemType.create(itemId, itemRev, itemTypeName, itemName, "", null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return item;
    }

    public static TCComponent loadObjectByUid(String uid)
    {
        DataManagementService dmService = DataManagementService.getService(getTCSession());
        ServiceData serviceData = dmService.loadObjects(new String[] { uid });
        if (serviceData != null && serviceData.sizeOfPlainObjects() > 0)
        {
            TCComponent modelObject = (TCComponent) serviceData.getPlainObject(0);
            dmService.refreshObjects(new TCComponent[] { modelObject });
            return modelObject;
        }
        return null;
    }

    /**
     * @param itemRev 对象版本
     * @return 返回升版之后的对象版本
     * @throws Exception *
     */
    public static TCComponentItemRevision doRevise(TCComponentItemRevision itemRev) throws Exception
    {
        System.out.println("doRevise");
        boolean isModify = false;
        String item_id = itemRev.getProperty("item_id");
        String itemRevId = itemRev.getProperty("item_revision_id");
        String object_name = itemRev.getProperty("object_name");
        System.out.println("itemRevId == " + itemRevId);
        try
        {
            isModify = itemRev.getItem().okToModify();
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        if (isModify)
        {
            DeepCopyInfo copyInfo = new DeepCopyInfo(itemRev, DeepCopyInfo.COPY_AS_OBJECT_ACTION, "", null, true, true, true);
            DeepCopyInfo[] copyInfos = new DeepCopyInfo[1];
            copyInfos[0] = copyInfo;
            TCComponentItemRevisionType itemRevType = (TCComponentItemRevisionType) itemRev.getTypeComponent();
            // String newRevId = itemRevType.getNewRevAlt(itemRev,
            // itemRevId);
            // String newRevId = itemRev.getNewRevAlt(itemRevId);
            String newRevId = itemRevType.getItemType().getNewRev(itemRev.getItem());
            System.out.println("newRevId == " + newRevId);
            TCComponentItemRevision newItemRev = itemRev.saveAs(newRevId, object_name, "", true, copyInfos);
            System.out.println("newItemRev == " + newItemRev);
            return newItemRev;
        }
        else
        {
            throw new Exception("權限不夠");
        }
    }

    /**
     * @param itemRev 对象版本
     * @param newRevId 版本号
     * @return
     * @throws Exception
     * 
     */
    public static TCComponentItemRevision doRevise(TCComponentItemRevision itemRev, String newRevId) throws Exception
    {
        System.out.println("doRevise");
        boolean isModify = false;
        String itemRevId = itemRev.getProperty("item_revision_id");
        String object_name = itemRev.getProperty("object_name");
        System.out.println("itemRevId == " + itemRevId);
        try
        {
            isModify = itemRev.getItem().okToModify();
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        if (isModify)
        {
            DeepCopyInfo copyInfo = new DeepCopyInfo(itemRev, DeepCopyInfo.COPY_AS_OBJECT_ACTION, "", null, true, true, true);
            DeepCopyInfo[] copyInfos = new DeepCopyInfo[1];
            copyInfos[0] = copyInfo;
            System.out.println("newRevId == " + newRevId);
            TCComponentItemRevision newItemRev = itemRev.saveAs(newRevId, object_name, "", true, copyInfos);
            System.out.println("newItemRev == " + newItemRev);
            return newItemRev;
        }
        else
        {
            throw new Exception("權限不夠");
        }
    }

    /**
     * 獲取靜態LOV 根据LOV名称获取值列表
     * 
     * @param lovName
     * @return
     */
    public static Object[] getLovValues(String lovName)
    {
        TCComponentListOfValues lov = TCComponentListOfValuesType.findLOVByName(getTCSession(), lovName);
        if (lov == null)
        {
            return new String[0];
        }
        try
        {
            Object[] objs = lov.getListOfValues().getListOfValues();
            return objs;
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return new String[0];
    }

    /**
     * 获取数组类型首选项值
     * 
     * @param session
     * @param scope 首选项级别
     * @param preferenceName 首选项名称
     * @return
     */
    @SuppressWarnings("deprecation")
    public static List<String> getArrayPreference(TCSession session, int scope, String preferenceName)
    {
        try
        {
            TCPreferenceService tCPreferenceService = session.getPreferenceService();
            tCPreferenceService.refresh();
            String[] array = tCPreferenceService.getStringArray(scope, preferenceName);
            return new ArrayList<String>(Arrays.asList(array));
//            return Arrays.asList(array);
        }
        catch (Exception e)
        {
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
    public static String getPreference(TCSession session, int scope, String preferenceName)
    {
        try
        {
            TCPreferenceService tCPreferenceService = session.getPreferenceService();
            tCPreferenceService.refresh();
            return tCPreferenceService.getString(scope, preferenceName);
        }
        catch (Exception e)
        {
            System.out.print(e);
        }
        return null;
    }

    /**
     * 
     * @param session
     * @param selectFilepath
     * @param dsType
     * @param dsName
     * @param ref_type
     * @return TCComponentDataset
     * @throws TCException
     */
    public static TCComponentDataset createDataSet(TCSession session, String selectFilepath, String dsType, String dsName, String ref_type) throws TCException
    {
        TCComponentDataset dataset = null;
        TCTypeService type = session.getTypeService();
        TCComponentDatasetType datasettype = (TCComponentDatasetType) type.getTypeComponent("Dataset");
        dataset = datasettype.create(dsName, "", dsType);
        String p[] = new String[1];
        String n[] = new String[1];
        p[0] = selectFilepath;
        // System.out.println("selectFilepath == " + selectFilepath);
        n[0] = ref_type;
        // System.out.println("ref_type == " + ref_type);
        dataset.setFiles(p, n);
        return dataset;
    }

    
    public static void updateDataset(TCSession session, TCComponent itemComp, String relName, String fileFullPath) throws TCException {
		try {
//			session.enableBypass(true);
			setBypass(session); // 开启旁路
			TCComponentItemRevision itemRev = (TCComponentItemRevision) itemComp;
			TCComponentDataset findDataSet = null;
			
			TCComponent[] datesets = itemRev.getRelatedComponents(relName);
			for (TCComponent tcComponent : datesets) {
				if (tcComponent instanceof TCComponentDataset) {
					findDataSet = (TCComponentDataset) tcComponent;
					break;
				}
			}
			
			//TCComponentDataset findDataSet = TCUtil.findDataSet((TCComponentItemRevision) itemComp, relName, fileFullPath.split("\\\\")[fileFullPath.split("\\\\").length - 1]);
			if (findDataSet != null) {
				updateDataset(session, findDataSet, fileFullPath);
			}
		} finally {
//			session.enableBypass(false);
			closeBypass(session); // 关闭旁路
		}
	}
	
	public static void updateDataset(TCSession session, TCComponentDataset dataset, String filePath) {
		try {
			TCComponentDatasetDefinitionType dsdefType = (TCComponentDatasetDefinitionType) session.getTypeComponent("DatasetType");
			TCComponentDatasetDefinition definition = dsdefType.find(dataset.getType());
			NamedReferenceContext[] contexts = definition.getNamedReferenceContexts();
			String namedReference = contexts[0].getNamedReference();
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
    public static TCComponentDataset findDataSet(TCComponentItemRevision itemRev, String findFileName) throws TCException
    {
        TCComponent[] datesets = itemRev.getRelatedComponents("IMAN_specification");
        for (TCComponent tcComponent : datesets)
        {
            if (tcComponent instanceof TCComponentDataset)
            {
                TCComponentDataset tcComponentDataset = (TCComponentDataset) tcComponent;
                for (String fileName : tcComponentDataset.getFileNames(null))
                {
                    if (fileName.equalsIgnoreCase(findFileName))
                    {
                        return tcComponentDataset;
                    }
                }
            }
        }
        return null;
    }

    
    public static TCComponentDataset findDataSet(TCComponentItemRevision itemRev, String relName, String findFileName)
			throws TCException {
		TCComponent[] datesets = itemRev.getRelatedComponents(relName);
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
    
    public static boolean isReleased(TCComponent com)
    {
        if (com == null)
        {
            return false;
        }
        try
        {
            com.refresh();
            TCComponent[] relStatus = com.getReferenceListProperty("release_status_list");
            if (relStatus != null && relStatus.length > 0)
            {
                return true;
            }
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isReleased(TCComponent tcc, String[] statusNameArray)
    {
        if (tcc == null || statusNameArray == null || statusNameArray.length < 1)
        {
            return false;
        }
        try
        {
            tcc.refresh();
            TCComponent[] relStatus = tcc.getReferenceListProperty("release_status_list");
            if (relStatus != null && relStatus.length > 0)
            {
                TCComponent status = relStatus[relStatus.length - 1];
                String name = status.getTCProperty("name").getStringValue();
                if (CommonTools.isEmpty(name))
                {
                    return false;
                }
                for (String statusName : statusNameArray)
                {
                    if (CommonTools.isNotEmpty(statusName) && name.equalsIgnoreCase(statusName))
                    {
                        return true;
                    }
                }
            }
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isReleased(TCComponent tcc, String statusName)
    {
        if (tcc == null)
        {
            return false;
        }
        try
        {
            tcc.refresh();
            TCComponent[] relStatus = tcc.getReferenceListProperty("release_status_list");
            if (relStatus != null && relStatus.length > 0)
            {
                TCComponent status = relStatus[relStatus.length - 1];
                String name = status.getTCProperty("name").getStringValue();
                if (CommonTools.isNotEmpty(name) && name.equalsIgnoreCase(statusName))
                {
                    return true;
                }
            }
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static String getReleasedName(TCComponent tcc)
    {
        if (tcc == null)
        {
            return null;
        }
        try
        {
            tcc.refresh();
            TCComponent[] relStatus = tcc.getReferenceListProperty("release_status_list");
            if (relStatus != null && relStatus.length > 0)
            {
                TCComponent status = relStatus[relStatus.length - 1];
                String name = status.getTCProperty("name").getStringValue();
                return name;
            }
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取异常信息
     * 
     * @param e
     * @return String
     */
    public static String getExceptionMsg(Exception e)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /*
     * 将对象发送到结构管理器
     * 
     * @param session
     * 
     * @param com
     * 
     * @return TCComponentBOMLine
     */
    public static TCComponentBOMLine openBomWindow(TCSession session, TCComponent com)
    {
        TCComponentBOMLine topBomline = null;
        try
        {
            TCComponentItemRevision rev = null;
            TCComponentItem item = null;
            TCComponentRevisionRuleType imancomponentrevisionruletype = (TCComponentRevisionRuleType) session.getTypeComponent("RevisionRule");
            TCComponentRevisionRule imancomponentrevisionrule = imancomponentrevisionruletype.getDefaultRule();
            TCComponentBOMWindowType imancomponentbomwindowtype = (TCComponentBOMWindowType) session.getTypeComponent("BOMWindow");
            TCComponentBOMWindow imancomponentbomwindow = imancomponentbomwindowtype.create(imancomponentrevisionrule);
            if (com instanceof TCComponentItem)
            {
                item = (TCComponentItem) com;
                topBomline = imancomponentbomwindow.setWindowTopLine(item, item.getLatestItemRevision(), null, null);
            }
            else if (com instanceof TCComponentItemRevision)
            {
                rev = (TCComponentItemRevision) com;
                topBomline = imancomponentbomwindow.setWindowTopLine(rev.getItem(), rev, null, null);
            }
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return topBomline;
    }

    /**
     * 创建BOMWindow
     * 
     * @param session
     * @return
     */
    public static TCComponentBOMWindow createBOMWindow(TCSession session)
    {
        TCComponentBOMWindow window = null;
        try
        {
            TCComponentRevisionRuleType imancomponentrevisionruletype = (TCComponentRevisionRuleType) session.getTypeComponent("RevisionRule");
            TCComponentRevisionRule imancomponentrevisionrule = imancomponentrevisionruletype.getDefaultRule();
            TCComponentBOMWindowType imancomponentbomwindowtype = (TCComponentBOMWindowType) session.getTypeComponent("BOMWindow");
            window = imancomponentbomwindowtype.create(imancomponentrevisionrule);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return window;
    }

    public static TCComponentBOMLine getTopBomline(TCComponentBOMWindow bomWindow, TCComponent com)
    {
        TCComponentBOMLine topBomline = null;
        try
        {
            if (bomWindow == null)
            {
                return topBomline;
            }
            TCComponentItemRevision rev = null;
            TCComponentItem item = null;
            if (com instanceof TCComponentItem)
            {
                item = (TCComponentItem) com;
                topBomline = bomWindow.setWindowTopLine(item, item.getLatestItemRevision(), null, null);
            }
            else if (com instanceof TCComponentItemRevision)
            {
                rev = (TCComponentItemRevision) com;
                topBomline = bomWindow.setWindowTopLine(rev.getItem(), rev, null, null);
            }
        }
        catch (TCException e)
        {
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
     * 调用查询构建器查询
     * 
     * @param session
     * @param queryName
     * @param keys
     * @param values
     * @return
     * @throws Exception
     */
    public static TCComponent[] executeQuery(TCSession session, String queryName, String[] keys, String[] values) throws Exception
    {
        TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
        TCComponentQuery query = (TCComponentQuery) imancomponentquerytype.find(queryName);
        if (keys.length != values.length)
        {
            throw new Exception("queryAttributies length is not equal queryValues length");
        }
        String[] queryAttributeDisplayNames = new String[keys.length];
        TCQueryClause[] elements = query.describe();
        for (int i = 0; i < keys.length; i++)
        {
            for (TCQueryClause element : elements)
            {
                //System.out.println("queryName: " + element.getAttributeName());
                //System.out.println("queryName2: " + element.getUserEntryNameDisplay());
                
                if (element.getAttributeName().equals(keys[i]))
                {
                    queryAttributeDisplayNames[i] = element.getUserEntryNameDisplay();
                }
            }
            if (queryAttributeDisplayNames[i] == null || queryAttributeDisplayNames[i].equals(""))
            {
                throw new Exception("queryAttribute\"" + keys[i] + "\"未找到对应的显示名称");
            }
        }
        // System.out.println("queryAttributeDisplayNames:" + Arrays.toString(queryAttributeDisplayNames));
        // System.out.println("queryValues:" + Arrays.toString(values));
        return query.execute(queryAttributeDisplayNames, values);
    }
    
    
    /**
     * 调用查询构建器查询
     * 本地化条目查询
     * 
     * @param session
     * @param queryName
     * @param keys
     * @param values
     * @return
     * @throws Exception
     */
    public static TCComponent[] query(TCSession session, String queryName, String[] keys, String[] values) throws Exception
    {
        TCComponentQueryType querytype = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
        TCComponentQuery query = (TCComponentQuery) querytype.find(queryName);
        querytype.clearCache();
        
        if(query != null) {
        	return query.execute(keys, values);
        }
        
        return null;
    }

    /**
     * 开旁路
     * 
     * @param session
     * @throws Exception
     */
    public static void setBypass(TCSession session)
    {
        try
        {
            TCUserService userService = session.getUserService();
            userService.call("set_bypass", new String[] { "" });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 关闭旁路
     * 
     * @param session
     * @throws Exception
     */
    public static void closeBypass(TCSession session)
    {
        try
        {
            TCUserService userService = session.getUserService();
            userService.call("close_bypass", new String[] { "" });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static TCComponentItem findItem(String itemID) throws TCException
    {
        TCComponentItemType itemType = (TCComponentItemType) getTCSession().getTypeComponent(ITypeName.Item);
        TCComponentItem item = itemType.find(itemID);
        return item;
    }

    public static TCComponentItemRevision findItemRevistion(String revId) throws TCException
    {
        TCComponent tcComponent = getTCSession().getComponentManager().getTCComponent(revId);
        return (TCComponentItemRevision) tcComponent;
    }

    public static TCComponentProcess createProcess(String workflowTemplateName, String processName, String desc, TCComponent[] att)
    {
        try
        {
            TCComponentTaskTemplateType templateType = (TCComponentTaskTemplateType) getTCSession().getTypeComponent(TCComponentTaskTemplateType.EPM_TASKTEMPLATE_TYPE);
            TCComponentTaskTemplate template = templateType.find(workflowTemplateName, 0);
            if (template == null)
            {
                return null;
            }
            int[] attType = null;
            if (att != null && att.length > 0)
            {
                attType = new int[att.length];
                for (int i = 0; i < att.length; i++)
                {
                    attType[i] = 1;
                }
            }
            TCComponentProcessType processType = (TCComponentProcessType) getTCSession().getTypeComponent("Job");
            TCComponentProcess process = (TCComponentProcess) processType.create(processName, desc == null ? "" : desc, template, att, attType);
            return process;
        }
        catch (TCException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String getTextValue(String name)
    {
        if (tcTextService == null)
        {
            tcTextService = getTCSession().getTextService();
        }
        String res = null;
        try
        {
            String value = tcTextService.getTextValue(name);
            if (CommonTools.isEmpty(value))
            {
                res = name;
            }
            else
            {
                res = value;
            }
        }
        catch (TCException e)
        {
            res = name;
            e.printStackTrace();
        }
        return res;
    }

    public static boolean checkUserPrivilege(TCSession session, TCComponent com)
    {
        boolean isWrite = false;
        try
        {
            TCAccessControlService service = session.getTCAccessControlService();
            isWrite = service.checkPrivilege(com, TCAccessControlService.WRITE);
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return isWrite;
    }

    public static < T > T tcPropMapping(T bean, TCComponent tcObject, String typeStr) throws IllegalArgumentException, IllegalAccessException, TCException
    {
        if (bean != null && tcObject != null)
        {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++)
            {
                fields[i].setAccessible(true);
                TCPropertes tcPropName = fields[i].getAnnotation(TCPropertes.class);
                if (tcPropName != null)
                {
                    Object val = "";
                    String propertyName = tcPropName.tcProperty();
                    String tctype = tcPropName.tcType();
                    if (propertyName.length() == 0 || !tctype.equalsIgnoreCase(typeStr))
                    {
                        continue;
                    }
                    int index = propertyName.indexOf(D9_PREFIX);
                    if (index != -1)
                    {
                        String pocPropertyName = propertyName.substring(0, index + D9_PREFIX.length()) + POC_STR + propertyName.substring(index + D9_PREFIX.length());
                        if (tcObject.isValidPropertyName(pocPropertyName))
                        {
                            propertyName = pocPropertyName;
                        }
                    }
                    tcObject.refresh();
                    if (tcObject.isValidPropertyName(propertyName))
                    {
                        val = tcObject.getProperty(propertyName);
                    }
                    else
                    {
                        System.out.println("==>> itemId: " + tcObject.getProperty("item_id"));
                        System.out.println("propertyName is not exist " + propertyName);
                    }
                    if (fields[i].getType() == Integer.class)
                    {
                        if (val.equals("") || val == null)
                        {
                            val = null;
                        }
                        else
                        {
                            val = Integer.parseInt((String) val);
                        }
                    }
                    fields[i].set(bean, val);
                }
            }
        }
        return bean;
    }

    public static < T > Map<String, String> getSavePropMap(T bean, String[] fieldNames) throws Exception
    {
        Map<String, String> saveMap = new HashMap<String, String>();
        for (String fieldName : fieldNames)
        {
            Field field = bean.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
            if (tcProp != null)
            {
                String tcPropName = tcProp.tcProperty();
                Object value = field.get(bean) == null ? "" : field.get(bean);
                saveMap.put(tcPropName, value + "");
            }
        }
        return saveMap;
    }

    /**
     * 通过uid获取对象
     * 
     * @param uid
     * @return
     * @throws TCException
     */
    public TCComponent getTcComponentByUid(String uid) throws TCException
    {
        return getTCSession().getComponentManager().getTCComponent(uid);
    }

    /**
     * 解包BOM结构
     * 
     * @param designBOMTopLine
     * @throws Exception
     */
    public static void unpackageBOMStructure(TCComponentBOMLine topBOMLine) throws Exception
    {
        AIFComponentContext[] children = topBOMLine.getChildren();
        for (int i = 0; i < children.length; i++)
        {
            TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
            if (bomLine.isPacked())
            {
                bomLine.unpack();
            }
            unpackageBOMStructure(bomLine);
        }
    }

    public static String post(String actionUrl, String params) throws IOException
    {
        String serverURL = actionUrl;
        StringBuffer sbf = new StringBuffer();
        String strRead = null;
        URL url = new URL(serverURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        // connection.setRequestProperty("key", "value");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.connect();
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        writer.write(params);
        writer.flush();
        InputStream is = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        while ((strRead = reader.readLine()) != null)
        {
            sbf.append(strRead);
            sbf.append("\r\n");
        }
        reader.close();
        connection.disconnect();
        return sbf.toString();
    }

    /**
     * 判断是否有写权限
     * 
     * @param session
     * @param component
     * @return
     */
    public static boolean checkOwninguserisWrite(TCSession session, TCComponent component)
    {
        boolean isWrite = false;
        try
        {
            TCAccessControlService service = session.getTCAccessControlService();
            isWrite = service.checkPrivilege(component, "WRITE");
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return isWrite;
    }

    /**
     * 返回升版规则
     * 
     * @param itemRev
     * @return
     * @throws TCException
     */
    public static String getVersionRule(TCComponentItemRevision itemRev) throws TCException
    {
        String version = itemRev.getProperty("item_revision_id");
        String versionRule = "";
        if (version.matches("[0-9]+"))
        { // 判断对象版本是否为数字版
            versionRule = "NN";
        }
        else if (version.matches("[a-zA-Z]+"))
        { // 判断对象版本是否为字母版
            versionRule = "@";
        }
        return versionRule;
    }

    public static String reviseVersion(TCSession session, String ruleMapping, String itemTypeRevName, String itemRevUid)
    {
        String version = null;
        try
        {
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
            for (GeneratedValuesOutput result : outputs)
            {
                Map<String, GeneratedValue> resultMap = result.generatedValues;
                GeneratedValue generatedValue = resultMap.get("item_revision_id");
                version = generatedValue.nextValue;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return version;
    }

    
    
    /**
     * 发送Dispatcher请求
     * 
     * @param provider the name of the provider that will process this request
     * @param service the name of the service that will process this request
     * @param priority the priority to assign to the request(0 LOW to 3 HIGH)
     * @param primaryObjects the array of primary objects
     * @param secondaryObjects the array of related secondary objects
     * @param startTime the time at which to start this request
     * @param interval the number of times to repeat this request 0 - no repeating 1 2 3 ... - number of times to repeat this task
     * @param endTime the time at which no more repeating requests of this same type will be processed. If the interval option for repeating is NOT
     *        selected, then this paramater is unused
     * @param type a string for use by the application creating the request for use in defining a type for the request (i.e. SYSTEM, THINCLIENT, etc.)
     * @param requestArgs the array of request arguments in the format of <KEY>=<VALUE> (i.e. NAME=John)
     * @return
     */
    public static boolean sendDispatcherRequest(String provider, String service, int priority, TCComponent[] primaryObjects, TCComponent[] secondaryObjects, String startTime, int interval, String endTime, String type, Map<String, String> requestArgs)
    {
        try
        {
            RequestResult request = DispatcherRequestFactory.createDispatcherRequest(provider, service, priority, primaryObjects, secondaryObjects, startTime, interval, endTime, type, requestArgs);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 新增或删除状态
     * 
     * @param wfService
     * @param tcComponents
     * @param statusName
     * @param operation
     * @return
     * @throws Exception
     */
    public static boolean setOrDelStatus(WorkflowService wfService, TCComponent[] tcComponents, String statusName, String operation) throws Exception
    {
        ReleaseStatusInput[] releaseStatusInputs = new ReleaseStatusInput[1];
        releaseStatusInputs[0] = new ReleaseStatusInput();
        ReleaseStatusOption[] releaseStatusOptions = new ReleaseStatusOption[1];
        releaseStatusOptions[0] = new ReleaseStatusOption();
        String appendStatus = "";
        String deleteStatus = "";
        if ("Append".equals(operation))
        {
            appendStatus = statusName;
        }
        if ("Delete".equals(operation))
        {
            deleteStatus = statusName;
        }
        releaseStatusOptions[0].newReleaseStatusTypeName = appendStatus;
        releaseStatusOptions[0].existingreleaseStatusTypeName = deleteStatus;
        releaseStatusOptions[0].operation = operation;
        releaseStatusInputs[0].objects = tcComponents;
        releaseStatusInputs[0].operations = releaseStatusOptions;
        SetReleaseStatusResponse response = wfService.setReleaseStatus(releaseStatusInputs);
        String result = getErrorMsg(response.serviceData);
        if (CommonTools.isNotEmpty(result))
        {
            throw new Exception(result);
        }
        return true;
    }

    /**
     * 返回错误信息
     *
     * @param data
     * @return
     */
    public static String getErrorMsg(ServiceData data)
    {
        String errorMsg = "";
        int errorSize = data.sizeOfPartialErrors();
        if (errorSize > 0)
        {
            for (int i = 0; i < errorSize; i++)
            {
                errorMsg += errorMsg + Arrays.toString(data.getPartialError(i).getMessages());
            }
        }
        return errorMsg;
    }

    public static TCComponentItemRevision getLatestReleased(TCComponentItemRevision itemRevision) throws TCException
    {
        TCComponent[] revions = itemRevision.getItem().getRelatedComponents("revision_list");
        for (int i = revions.length - 1; i >= 0; i--)
        {
            TCComponentItemRevision itemRev = (TCComponentItemRevision) revions[i];
            if (TCUtil.isReleased(itemRev))
            {
                return itemRev;
            }
        }
        return null;
    }

    /**
     * 判断是否为空
     * 
     * @param info
     * @return
     */
    public static boolean isNull(String info)
    {
        return info == null || info.trim().length() < 1;
    }

    public static boolean isNull(Map map)
    {
        return map == null || map.size() < 1;
    }

    public static File openSaveFileDialog(Shell shell, String fileName)
    {
        File destFile = null;
        FileDialog dlg = new FileDialog(shell, SWT.SAVE);
        dlg.setFilterPath(getSystemDesktop());
        System.out.println("==>> fileName: " + fileName);
        dlg.setFileName(CommonTools.removeFileSpecicalStr(fileName));
        dlg.setFilterExtensions(new String[] { "*.xlsx" }); // 设置打开文件扩展名
        dlg.setFilterNames(new String[] { "Excel Files (*.xlsx)" }); // 输入文件名时不用加.xlsx
        boolean done = false;
        String saveFileName = null;
        while (!done)
        {
            saveFileName = dlg.open();
            if (CommonTools.isEmpty(saveFileName))
            {
                org.eclipse.swt.widgets.MessageBox msg = new org.eclipse.swt.widgets.MessageBox(dlg.getParent(), SWT.ICON_WARNING | SWT.YES); // User
                                                                                                                                              // has
                                                                                                                                              // cancelled,
                                                                                                                                              // so
                                                                                                                                              // quit
                                                                                                                                              // and
                                                                                                                                              // return
                msg.setText("提示");
                msg.setMessage("你取消了保存文件");
                done = msg.open() == SWT.YES;
                done = true;
            }
            else
            {
                File file = new File(saveFileName);
                if (file.exists())
                { // 判斷文件是否已經存在
                    org.eclipse.swt.widgets.MessageBox msg = new org.eclipse.swt.widgets.MessageBox(dlg.getParent(), SWT.ICON_WARNING | SWT.YES | SWT.NO); // The
                                                                                                                                                           // file
                                                                                                                                                           // already
                                                                                                                                                           // exists;
                                                                                                                                                           // asks
                                                                                                                                                           // for
                                                                                                                                                           // confirmation
                    msg.setText("提示");
                    msg.setMessage(saveFileName + " 已经存在，是否要替換它？");
                    done = msg.open() == SWT.YES; // If they click Yes, drop out. If they click No, redisplay the File Dialog
                }
                else
                {
                    done = true; // 不存在文件名重複，可以保存
                }
            }
        }
        if (CommonTools.isNotEmpty(saveFileName))
        {
            destFile = new File(saveFileName);
        }
        return destFile;
    }

    /**
     * 获取动态LOV
     * 
     * @param session
     * @param itemRevType
     * @param prop
     * @return
     */
    public static ArrayList<String> getLovValues(TCSession session, TCComponentItemRevisionType itemRevType, String prop)
    {
    	ArrayList<String> lovList = new ArrayList<String>();
        try
        {
            TCPropertyDescriptor property = itemRevType.getPropertyDescriptor(prop);
            TCComponentListOfValues listOfValues = property.getLOV();
            String lovType = listOfValues.getProperty("lov_type");
            if ("Fnd0ListOfValuesDynamic".equals(lovType))
            {
                String valueProperty = listOfValues.getProperty("fnd0lov_value");
                LOVService lovService = LOVService.getService(session);
                InitialLovData input = new InitialLovData();
                LovFilterData filter = new LovFilterData();
                filter.order = 0;
                filter.numberToReturn = 10000;
                filter.maxResults = 10000;
                input.lov = listOfValues;
                input.filterData = filter;
                LOVSearchResults lovResult = lovService.getInitialLOVValues(input);
                if (lovResult.serviceData.sizeOfPartialErrors() < 1)
                {
                    for (LOVValueRow row : lovResult.lovValues)
                    {
                        Map<String, String[]> map = row.propDisplayValues;
                        for (Entry<String, String[]> entry : map.entrySet())
                        {
                            String key = entry.getKey();
                            if (key.equals(valueProperty))
                            {
                                String[] values = entry.getValue();
                                if (values != null && values.length > 0 && lovList.contains(values[0]) == false)
                                {
                                    lovList.add(values[0]);
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                ListOfValuesInfo lovi = listOfValues.getListOfValues();
                String[] displayValues = lovi.getLOVDisplayValues();
                for (String displayValue : displayValues)
                {
                    String realValue = lovi.getRealValue(displayValue).toString();
                    if (!lovList.contains(realValue))
                    {
                        lovList.add(realValue);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return lovList;
    }

    /**
     * 创建对象
     */
    public static TCComponent createCom(TCSession session, String itemTypeName, String itemID, String name, String revisionID, Map<String, String> revisionPropMap)
    {
        IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session, itemTypeName);
        CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);
        if (CommonTools.isNotEmpty(itemID))
        {
            createInstanceInput.add("item_id", itemID);
        }
        if (CommonTools.isNotEmpty(name))
        {
            createInstanceInput.add("object_name", name);
        }
        IBOCreateDefinition createDefinitionRev = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session, itemTypeName + "Revision");
        CreateInstanceInput createInstanceInputRev = new CreateInstanceInput(createDefinitionRev);
        if (CommonTools.isNotEmpty(revisionID))
        {
            createInstanceInputRev.add("item_revision_id", revisionID);
        }
        if (revisionPropMap != null)
        {
            for (Entry<String, String> entry : revisionPropMap.entrySet())
            {
                String p = entry.getKey();
                String v = entry.getValue();
                if (CommonTools.isNotEmpty(v))
                {
                    continue;
                }
                createInstanceInputRev.add(p, v);
            }
        }
        ArrayList<ICreateInstanceInput> iputList = new ArrayList<>();
        iputList.add(createInstanceInput);
        createInstanceInput.addSecondaryCreateInput(IBOCreateDefinition.REVISION, createInstanceInputRev);
        TCComponent obj = null;
        try
        {
            List<TCComponent> comps = SOAGenericCreateHelper.create(session, createDefinition, iputList);
            obj = comps.get(0);
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return obj;
    }
    
    /**
     * 创建对象
     * @throws TCException 
     */
    public static TCComponent createComWithOutCatch(TCSession session, String itemTypeName, String itemID, String name, String revisionID, Map<String, String> revisionPropMap) throws TCException
    {
        IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session, itemTypeName);
        CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);
        if (CommonTools.isNotEmpty(itemID))
        {
            createInstanceInput.add("item_id", itemID);
        }
        if (CommonTools.isNotEmpty(name))
        {
            createInstanceInput.add("object_name", name);
        }
        IBOCreateDefinition createDefinitionRev = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session, itemTypeName + "Revision");
        CreateInstanceInput createInstanceInputRev = new CreateInstanceInput(createDefinitionRev);
        if (CommonTools.isNotEmpty(revisionID))
        {
            createInstanceInputRev.add("item_revision_id", revisionID);
        }
        if (revisionPropMap != null)
        {
            for (Entry<String, String> entry : revisionPropMap.entrySet())
            {
                String p = entry.getKey();
                String v = entry.getValue();
                if (CommonTools.isNotEmpty(v))
                {
                    continue;
                }
                createInstanceInputRev.add(p, v);
            }
        }
        ArrayList<ICreateInstanceInput> iputList = new ArrayList<>();
        iputList.add(createInstanceInput);
        createInstanceInput.addSecondaryCreateInput(IBOCreateDefinition.REVISION, createInstanceInputRev);
        TCComponent obj = null;
        List<TCComponent> comps = SOAGenericCreateHelper.create(session, createDefinition, iputList);
        obj = comps.get(0);
        return obj;
    }
    
    public static TCComponent createCom(TCSession session, String itemTypeName, String itemID, Map<String, Object> revisionMap) throws TCException
    {
        IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session, itemTypeName);
        CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);
        if (!isNull(itemID))
            createInstanceInput.add("item_id", itemID);
        // createInstanceInput.add("object_name", name);
        IBOCreateDefinition createDefinitionRev = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session, itemTypeName + "Revision");
        CreateInstanceInput createInstanceInputRev = new CreateInstanceInput(createDefinitionRev);
        if (revisionMap == null)
        {
            revisionMap = new HashMap<String, Object>();
        }
        // createInstanceInputRev.add("item_revision_id", revisionID);
        for (Entry<String, Object> entry : revisionMap.entrySet())
        {
            String p = entry.getKey();
            Object v = entry.getValue();
            if (null == v)
            {
                continue;
            }
            createInstanceInputRev.add(p, v);
            createInstanceInput.add(p, v);
        }
        ArrayList iputList = new ArrayList();
        iputList.add(createInstanceInput);
        ArrayList list = new ArrayList(0);
        list.addAll(iputList);
        createInstanceInput.addSecondaryCreateInput(createDefinition.REVISION, createInstanceInputRev);
        TCComponent obj = null;
        List comps = null;
        // try {
        comps = SOAGenericCreateHelper.create(session, createDefinition, list);
        obj = (TCComponent) comps.get(0);
        // } catch (TCException e) {
        // e.printStackTrace();
        // }
        return obj;
    }
    
    
    /**
	 * 创建item对象
	 * @param session
	 * @param itemTypeName
	 * @param itemID
	 * @param itemMap
	 * @param revisionMap
	 * @return
	 * @throws TCException
	 */
    public static TCComponent createObject(TCSession session, String itemTypeName, String itemID, Map<String, Object> itemMap , Map<String, Object> revisionMap) throws TCException
    {
        IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session, itemTypeName);
        CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);
        if (itemID!=null && !"".equals(itemID.trim()))
            createInstanceInput.add("item_id", itemID);
         //createInstanceInput.add("object_name", name);
         
        IBOCreateDefinition createDefinitionRev = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session, itemTypeName + "Revision");
        CreateInstanceInput createInstanceInputRev = new CreateInstanceInput(createDefinitionRev);
        if (revisionMap == null)
        {
            revisionMap = new HashMap<String, Object>();
        }
        //createInstanceInputRev.add("item_revision_id", revisionID);
        
        for (Entry<String, Object> entry : itemMap.entrySet())
        {
            String p = entry.getKey();
            Object v = entry.getValue();
            if (null == v)
            {
                continue;
            }
            createInstanceInput.add(p, v);
        }
        
        for (Entry<String, Object> entry : revisionMap.entrySet())
        {
            String p = entry.getKey();
            Object v = entry.getValue();
            if (null == v)
            {
                continue;
            }
            createInstanceInputRev.add(p, v);
        }
        ArrayList<CreateInstanceInput> iputList = new ArrayList<CreateInstanceInput>();
        iputList.add(createInstanceInput);
        List<ICreateInstanceInput> list = new ArrayList<ICreateInstanceInput>(0);
        list.addAll(iputList);
        createInstanceInput.addSecondaryCreateInput("revision", createInstanceInputRev);
        TCComponent obj = null;
        List<TCComponent> comps = null;
        comps = SOAGenericCreateHelper.create(session, createDefinition, list);
        obj = (TCComponent) comps.get(0);
        
        return obj;
    }
    
    
    /**
	 * 格式化的日期
	 * @param formatStr
	 * @return
	 */
	public static String formatNowDate(String formatStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatStr);
		return formatter.format(LocalDateTime.now());
	}
	
	
	public static String downloadFile(TCComponentDataset dataset, String dir, String fileExtensions, String refName, String itemRevObjectName, boolean rename) {
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
			
			//exportFile = dataset.getFile(refName, tcfile.toString(), dir);
			exportFile = tcfile.getFmsFile();
			String newFileName = null;
			if (rename) {
				if (dir.endsWith("\\")) {
					newFileName = dir + itemRevObjectName + fileExtensions;
				} else {
					newFileName = dir + File.separator + itemRevObjectName + fileExtensions;
				}
				newFile = new File(newFileName);
				if (exportFile.exists() && exportFile.isFile()) {
					if (newFile.exists()) { 
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
     * 创建Item
     * 
     * @param session
     * @param itemId
     * @param itemRev
     * @param itemName
     * @param itemTypeName
     * @return
     */
    public static TCComponentItem createItem(TCSession session, String itemId, String itemRev, String itemName, String itemTypeName)
    {
        TCComponentItem item = null;
        try
        {
            TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent(itemTypeName);
            if (itemId==null || itemId.equals(""))
            {
                itemId = itemType.getNewID();
            }
            if (itemId==null || itemRev.equals(""))
            {
                itemRev = itemType.getNewRev(null);
            }
            item = itemType.create(itemId, itemRev, itemTypeName, itemName, "", null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return item;
    }

    
    public static void checkIn(TCSession session, TCComponent component) throws TCException {
    	String checkedOut = component.getProperty("checked_out").trim();
    	if ("".equals(checkedOut)) {
    		return;
		}
    	
    	ReservationService rs = ReservationService.getService(session);
    	ServiceData data = rs.checkin(new TCComponent[] {component});
    	if (data.sizeOfPartialErrors() > 0) {
			throw new TCException("ReservationService checkin returned a partial error.");
		}
    	
    } 
    
    public static void doCall(String path, String charsetName) throws IOException, InterruptedException {
    	System.out.println("==>> path: " + path);
    	List list = new ArrayList();
    	ProcessBuilder processBuilder = null;
		Process process = null;
		String line = null;
		BufferedReader stdout = null;
		list.add("cmd");
		list.add("/c");
		list.add(path);
		
		processBuilder = new ProcessBuilder(list);
		processBuilder.redirectErrorStream(true);
		process = processBuilder.start();
		stdout = new BufferedReader(new InputStreamReader(process.getInputStream(), charsetName));
		OutputStreamWriter os = new OutputStreamWriter(process.getOutputStream());
		while ((line = stdout.readLine()) != null) {
			System.out.println(line);
		}
		
		int ret = process.waitFor();
		stdout.close();
    }
    
    
    public static List<String> getTxtContent(String filePath, String charsetName) {
    	List<String> list = new ArrayList<String>();
    	InputStreamReader read = null;
    	BufferedReader bufferedReader = null;
    	try {
    		File file = new File(filePath);// 文件路径
    		if (file.isFile() && file.exists()) {
    			read = new InputStreamReader(new FileInputStream(file));
    			bufferedReader = new BufferedReader(read);
    			String line = null;
    			while ((line = bufferedReader.readLine()) != null) {
    				list.add(line);
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (read != null) {
				try {
					read.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
		}
    	
    	return list;
    }

    
    /**
     * 通用遍歷BOM（深度優先）
     * 如果要解包，先自己解包好之後再掉用
     * 默認拋異常中斷遍歷，可自己在onAction方法中補貨則不會中斷
     * @param topBomLine
     * @param action
     * @throws TCException
     */
    public static void ergodicBOM(TCComponentBOMLine bomLine,EergodicAction action) throws Exception {
    	if(action==null) {
    		return;
    	}
    	// 處理自己
    	action.onAction(bomLine);
    	// 處理自己的替代料
    	if (bomLine.hasSubstitutes()) {
			TCComponentBOMLine[] listSubstitutes = bomLine.listSubstitutes();
			for (TCComponentBOMLine tcComponentBOMLine : listSubstitutes) {				
				action.onAction(tcComponentBOMLine);
			}
		}
    	// 下一層
    	AIFComponentContext[] children = bomLine.getChildren();
    	for(AIFComponentContext child:children) {
    		ergodicBOM((TCComponentBOMLine) child.getComponent(),action);
    	}    	
    }
    
    public static interface EergodicAction{
    	void onAction(TCComponentBOMLine bomLine) throws Exception;    	
    }
    
    /**
     * 获取上一版本
     * 
     * @param itemRev
     * @return
     * @throws TCException
     */
    public static TCComponentItemRevision getPreviousRevision(TCComponentItemRevision itemRev) throws TCException
    {
        try
        {
            String myVer = itemRev.getProperty("item_revision_id");
            TCComponent[] revions = itemRev.getItem().getRelatedComponents("revision_list");
            if (revions != null && revions.length > 0)
            {
                for (int i = 0; i < revions.length; i++)
                {
                    TCComponentItemRevision itemRevision = (TCComponentItemRevision) revions[i];
                    String version = itemRevision.getProperty("item_revision_id");
                    if (myVer.equals(version))
                    {
                        if (i - 1 >= 0)
                        {
                            return (TCComponentItemRevision) revions[i - 1];
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
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
    
    public static void setWorker(TCSession session,TCComponentItemRevision itemRevision,String type,TCComponentGroupMember groupMember) {
    	try {
    		DataManagementService dataManagementService = DataManagementService.getService(session);
        	com.teamcenter.services.rac.core._2008_06.DataManagement.AddParticipantInfo addParticipantInfo = new com.teamcenter.services.rac.core._2008_06.DataManagement.AddParticipantInfo();
        	addParticipantInfo.itemRev = itemRevision;
        	com.teamcenter.services.rac.core._2008_06.DataManagement.ParticipantInfo info = new com.teamcenter.services.rac.core._2008_06.DataManagement.ParticipantInfo();
    		info.clientId = itemRevision.getUid();
    		info.participantType = type;
    		info.assignee = groupMember;
    		addParticipantInfo.participantInfo = new com.teamcenter.services.rac.core._2008_06.DataManagement.ParticipantInfo[] {info};
        	AddParticipantOutput addParticipants = dataManagementService.addParticipants(new com.teamcenter.services.rac.core._2008_06.DataManagement.AddParticipantInfo[] {addParticipantInfo});
        	ServiceData serviceData = addParticipants.serviceData;
        	if (serviceData.sizeOfPartialErrors() > 0) {
        		throw new TCException("添加設計者出錯");
        	}
    	}catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void removeWorker(TCSession session,TCComponentItemRevision itemRevision,TCComponentParticipant participant) {
    	try {
    		DataManagementService dataManagementService = DataManagementService.getService(session);
    		com.teamcenter.services.rac.core._2008_06.DataManagement.Participants participants = new com.teamcenter.services.rac.core._2008_06.DataManagement.Participants();
    		participants.itemRev = itemRevision;
    		participants.participant = new TCComponentParticipant[] {participant};

    		ServiceData serviceData = dataManagementService.removeParticipants(new Participants[] {participants});
        	if (serviceData.sizeOfPartialErrors() > 0) {
        		throw new TCException("移除設計者出錯");
        	}
    	}catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    
    public static com.teamcenter.soa.client.model.ModelObject getPublicMailForm(DataManagementService dmService, String IMFormName, String IMFormType, String rel, TCComponent parent, boolean saveDB) throws Exception {
    	AIFComponentContext[] related = parent.getRelated(rel);
    	if(related !=null && related.length > 0) {
    		for (int i = 0; i < related.length; i++) {
    			InterfaceAIFComponent component = related[i].getComponent();
    			if(component instanceof TCComponentForm) {
    				String object_name = component.getProperty("object_name");
    				if(object_name.equals(IMFormName)) {
    					return (com.teamcenter.soa.client.model.ModelObject)component;
    				}
    			}
    		}
    	}
    	
    	
    	Map<String, String[]> propMap = new HashMap<String, String[]>();

		FormInfo[] inputs = new FormInfo[1];
		inputs[0] = new FormInfo();
		inputs[0].clientId = "FormInfo";
		inputs[0].description = "";
		inputs[0].name = IMFormName;
		inputs[0].formType = IMFormType;
		inputs[0].saveDB = saveDB;
		inputs[0].parentObject = parent;
		inputs[0].relationName = rel;
		inputs[0].attributesMap = propMap;
		CreateOrUpdateFormsResponse response = dmService.createOrUpdateForms(inputs);
		if (response.serviceData.sizeOfPartialErrors() > 0) {
			System.out.println("create form error size:" + response.serviceData.sizeOfPartialErrors());
			System.out.println(response.serviceData.getPartialError(0).toString());

			for (int i = 0; i < response.serviceData.sizeOfPartialErrors(); i++) {
				ErrorStack temp = response.serviceData.getPartialError(i);
				for (int j = 0; j < temp.getErrorValues().length; j++) {
					System.out.println("===>partial error:" + temp.getCodes()[j] + "   " + temp.getErrorValues()[j] + "  " + temp.getErrorValues()[j].getMessage());

				}
			}

			throw new ServiceException("DataManagementService.createForms returned a partial error.");
		}
		return response.outputs[0].form;
	}
    
    public static TCComponent createTableRow(TCSession session, String type, HashMap<String, String> map) throws ServiceException {
		DataManagementService dmService = DataManagementService.getService(session);
		CreateIn[] createIn = new CreateIn[1];
		createIn[0] = new CreateIn();
		createIn[0].data.boName = type;

		Set<Entry<String,String>> entrySet = map.entrySet();
		for (Entry<String,String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			createIn[0].data.stringProps.put(key, value);
		}

		CreateResponse createObjects = dmService.createObjects(createIn);
		int sizeOfPartialErrors = createObjects.serviceData.sizeOfPartialErrors();
		for (int i = 0; i < sizeOfPartialErrors; i++) {
			ErrorStack partialError = createObjects.serviceData.getPartialError(i);
			String[] messages = partialError.getMessages();
			if(messages!=null && messages.length > 0) {
				StringBuffer sBuffe = new StringBuffer();
				for (String string : messages) {
					System.out.println(string);
					sBuffe.append(string);
				}
				throw new ServiceException(sBuffe.toString());
			}
			
		}
		if (createObjects.output.length > 0) {
			return createObjects.output[0].objects[0];
		}

		return null;
	}
    
    
    public static TCComponentForm getMailForm(TCComponent parent) throws Exception {
    	String IMFormName = "publicMail";
    	String rel = "IMAN_external_object_link";
    	AIFComponentContext[] related = parent.getRelated(rel);
    	if(related !=null && related.length > 0) {
    		for (int i = 0; i < related.length; i++) {
    			InterfaceAIFComponent component = related[i].getComponent();
    			if(component instanceof TCComponentForm) {
    				String object_name = component.getProperty("object_name");
    				if(object_name.equals(IMFormName)) {
    					return (TCComponentForm) component;
    				}
    			}
    		}
    	}
		return null;
    }
    
    public static String getProperty(TCComponent com,String key) {
    	try {
			return com.getProperty(key);
		} catch (TCException e) {
			return null;			
		}
    }
    
}
