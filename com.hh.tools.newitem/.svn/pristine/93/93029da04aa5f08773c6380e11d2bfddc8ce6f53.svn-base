package com.hh.tools.newitem;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
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
import javax.swing.table.TableColumn;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.hh.tools.renderingHint.ArtworkTypeLovPropertyBean;
import com.hh.tools.renderingHint.ChassisLovPropertyBean;
import com.hh.tools.renderingHint.ColorPropertyBean;
import com.hh.tools.renderingHint.ComponentLovPropertyBean;
import com.hh.tools.renderingHint.EReportTypeLovPropertyBean;
import com.hh.tools.renderingHint.GasketShapeLovPropertyBean;
import com.hh.tools.renderingHint.HeadShapePropertyBean;
import com.hh.tools.renderingHint.OptionalPropertyBean;
import com.hh.tools.renderingHint.PACTypeLovPropertyBean;
import com.hh.tools.renderingHint.PCBLevelLovPropertyBean;
import com.hh.tools.renderingHint.PartPropertyBean;
import com.hh.tools.renderingHint.PlantformLovPropertyBean;
import com.hh.tools.renderingHint.PlatformLovPropertyBean;
import com.hh.tools.renderingHint.ProductTypeLovPropertyBean;
import com.hh.tools.renderingHint.ProgramPhaseLovPropertyBean;
import com.hh.tools.renderingHint.PurposePropertyBean;
import com.hh.tools.renderingHint.RubberShapeLovPropertyBean;
import com.hh.tools.renderingHint.SpecificationPropertyBean;
import com.hh.tools.renderingHint.TypePropertyBean;
import com.teamcenter.rac.aif.AIFClipboard;
import com.teamcenter.rac.aif.AIFPortal;
import com.teamcenter.rac.aif.AIFTransferable;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.common.create.BOCreateDefinitionFactory;
import com.teamcenter.rac.common.create.CreateInstanceInput;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.common.create.ICreateInstanceInput;
import com.teamcenter.rac.common.create.SOAGenericCreateHelper;
import com.teamcenter.rac.kernel.IRelationName;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.ListOfValuesInfo;
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
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
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
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCSignoffOriginType;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.services.IAspectUIService;
import com.teamcenter.rac.services.ISessionService;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.FileUtility;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.OSGIUtil;
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
 * @author Handk
 */
@SuppressWarnings("deprecation")
public class Utils {

    private static List<String> property_onlyone = null;
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
    public static String getPrefix1() {
        String[] arr = getTCSession().getPreferenceService().getStringArray(TCPreferenceService.TC_preference_site, "FX_Prefix");
        return arr[0];
    }

    //获取对象前缀小写
    public static String getPrefix2() {
        String[] arr = getTCSession().getPreferenceService().getStringArray(TCPreferenceService.TC_preference_site, "FX_Prefix");
        return arr[1];
    }

    /**
     * 根据模板创建数据集
     * @param templateDataset 模板数据集
     * @param rev 挂载对象版本
     * @param newDatasetName 新创建的数据集
     */
    public static void createNewDataset(TCComponentDataset templateDataset, TCComponentItemRevision rev,
                                        String newDatasetName) {
        if (templateDataset == null || rev == null || isNull(newDatasetName)) {
            return;
        }
        String referenceName = "";
        try {
            String dataType = templateDataset.getType();
            System.out.println("dataType ==" + dataType);
            if (dataType.toLowerCase().contains("excel")) {
                referenceName = "excel";
            } else if (dataType.toLowerCase().contains("word")) {
                referenceName = "word";
            } else if (dataType.toLowerCase().contains("proasm")) {
                referenceName = "AsmFile";
            } else if (dataType.toLowerCase().contains("proprt")) {
                referenceName = "PrtFile";
            }
            TCComponentDatasetType datasetType = (TCComponentDatasetType) templateDataset.getTypeComponent();
            TCComponentDataset dataset = datasetType.create(newDatasetName, newDatasetName, dataType);
            File file = exportFileToPath(templateDataset, referenceName);
            File newFile = copyFileWithNewName(file, newDatasetName);
            dataset.setFiles(new String[]{newFile.getAbsolutePath()}, new String[]{referenceName});

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
        dataset.setFiles(new String[]{newFile.getAbsolutePath()}, new String[]{referenceName});
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
            if (newDataset == null) {
                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                if (suffix.equalsIgnoreCase("xls")) {
                    newDataset = CreateObject.createDataSet(session, filePath, "MSExcel", fileName, "excel");
                } else if (suffix.equalsIgnoreCase("doc")) {
                    newDataset = CreateObject.createDataSet(session, filePath, "MSWord", fileName, "word");
                } else if (suffix.equalsIgnoreCase("xlsx")) {
                    newDataset = CreateObject.createDataSet(session, filePath, "MSExcelX", fileName, "excel");
                } else if (suffix.equalsIgnoreCase("docx")) {
                    newDataset = CreateObject.createDataSet(session, filePath, "MSWordX", fileName, "word");
                } else if (suffix.equalsIgnoreCase("pdf")) {
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
        MessageBox.post(message, "提示", MessageBox.INFORMATION);
    }

    public static void showMessage(String message) {
        if (isNull(message)) {
            return;
        }
        JOptionPane.showMessageDialog(null, message);
    }

    public static TCComponent createCom(TCSession session, String itemTypeName, String itemID, String name, String revisionID, Map<String, String> revisionMap) {
        IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session, itemTypeName);
        CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);


        createInstanceInput.add("item_id", itemID);
        createInstanceInput.add("object_name", name);

        IBOCreateDefinition createDefinitionRev = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session, itemTypeName + "Revision");
        CreateInstanceInput createInstanceInputRev = new CreateInstanceInput(createDefinitionRev);

        if (revisionMap == null) {
            revisionMap = new HashMap<String, String>();
        }
        for (Entry<String, String> entry : revisionMap.entrySet()) {
            String p = entry.getKey();
            String v = entry.getValue();
            createInstanceInputRev.add(p, v);
            System.out.println("p== " + p);
            System.out.println("v== " + v);
        }
        createInstanceInputRev.add("item_revision_id", revisionID);
        if (itemTypeName.equals("HH8_PFMEA")) {
            createInstanceInputRev.add("hh8_tube", false);
        }
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

    public static TCComponentItem createItem(TCSession session, String itemTypeName, String itemID, String name, String revisionID, Map<String, String> itemMap, Map<String, String> revisionMap) {
        try {
//		TCComponentItemType itemType=(TCComponentItemType)Utils.getTCSession().getTypeComponent(itemTypeName);
//		TCComponentItemRevisionType itemRevisionType=itemType.getItemRevisionType();
//		String itemRevisionTypeName=itemRevisionType.getTypeName();
            BOCreateDefinitionFactory createDefinitionFactory = BOCreateDefinitionFactory.getInstance();

            IBOCreateDefinition itemCreateDefinition = createDefinitionFactory.getCreateDefinition(Utils.getTCSession(), itemTypeName);
            CreateInstanceInput createInstanceInput = new CreateInstanceInput(itemCreateDefinition);

            if (itemMap == null) {
                itemMap = new HashMap<String, String>();
            }
            itemMap.put("item_id", itemID);
            itemMap.put("object_name", name);
            itemMap.put("object_desc", "");
            for (Entry<String, String> entry : itemMap.entrySet()) {
                String p = entry.getKey();
                String v = entry.getValue();
                createInstanceInput.add(p, v);
            }


            List<IBOCreateDefinition> secondaryCreateDefinitionList = itemCreateDefinition.getSecondaryCreateDefinition("revision");
            if (secondaryCreateDefinitionList != null && secondaryCreateDefinitionList.size() > 0) {
                IBOCreateDefinition revisionCreateDefinition = secondaryCreateDefinitionList.get(0);
                CreateInstanceInput revisionInstanceInput = new CreateInstanceInput(revisionCreateDefinition);
                if (revisionMap == null) {
                    revisionMap = new HashMap<String, String>();
                }
                itemMap.put("object_name", name);
                itemMap.put("object_desc", "");
                itemMap.put("item_revision_id", revisionID);
                for (Entry<String, String> entry : revisionMap.entrySet()) {
                    String p = entry.getKey();
                    String v = entry.getValue();
                    revisionInstanceInput.add(p, v);
                }
                createInstanceInput.add("revision", revisionInstanceInput);
            }
            List<ICreateInstanceInput> list = new ArrayList<ICreateInstanceInput>();
            list.add(createInstanceInput);
            List<TCComponent> componentList = SOAGenericCreateHelper.create(session, itemCreateDefinition, list);

            if (componentList != null && componentList.size() > 0) {
                TCComponent component = componentList.get(0);
                if (component instanceof TCComponentItem) {
                    return (TCComponentItem) component;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
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
            ReleaseStatusOption[] operation = {releaseStatusOption};

            ReleaseStatusInput releaseStatusInput = new ReleaseStatusInput();
            releaseStatusInput.objects = new TCComponent[]{tcc};
            releaseStatusInput.operations = operation;
            ReleaseStatusInput[] input = {releaseStatusInput};
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

    // java调C 开关bypass
    public static void setByPass(boolean val, TCSession session)
            throws TCException {
        TCUserService userservice = null;
        if (userservice == null) {
            userservice = session.getUserService();
        }
        Object[] obj = new Object[1];
        obj[0] = "origin";
        if (val) {
            String setByPass = (String) userservice.call("set_bypass",
                    obj);
            System.out.println("ORIGIN_set_bypass===" + setByPass);
        } else {
            String setByPass = (String) userservice.call("close_bypass",
                    obj);
            System.out.println("ORIGIN_close_bypass===" + setByPass);
        }
    }

    public static void byPass(boolean on) {
        try {
            TCUserService userservice = null;
            if (userservice == null) {
                userservice = getTCSession().getUserService();
            }
            Object[] obj = new Object[1];
            obj[0] = "origin";
            if (on) {
                String setByPass = (String) userservice.call("set_bypass",
                        obj);
                System.out.println("ORIGIN_set_bypass===" + setByPass);
            } else {
                String setByPass = (String) userservice.call("close_bypass",
                        obj);
                System.out.println("ORIGIN_close_bypass===" + setByPass);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void addClipboard(TCComponent obj) {
        if (obj == null) {
            return;
        }
        List list = new ArrayList();
        list.add(obj);
        addClipboard(list);
    }

    @SuppressWarnings("rawtypes")
    public static void addClipboard(List list) {
        if (list == null || list.size() < 1) {
            return;
        }
        AIFClipboard aifclipboard = AIFPortal.getClipboard();
        AIFTransferable aiftransferable = new AIFTransferable(list);
        aifclipboard.setContents(aiftransferable, null);
    }

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
            TCComponentItem currentItem = itemType.create(itemId, currentRevId, itemTypeName, itemName, "", null);
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
                               TCComponentItemRevision currentRev, TCComponentProject targetProject) {
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
                                } else if ("project".equals(tValue1)) {
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
                                if (currentProp.isNotArray()) {
                                    System.out.println("not array");
                                    currentProp.setPropertyData(targetProp
                                            .getPropertyData());
                                    currentCom.setTCProperty(currentProp);
                                } else {
                                    System.out.println("is array");
                                    if (targetProp.getPropertyType() == TCProperty.PROP_string) {
                                        System.out.println("PROP_string");
                                        currentProp.setPropertyArrayData(targetProp.getStringArrayValue());
                                        currentCom.setTCProperty(currentProp);
                                    } else if (targetProp.getPropertyType() == TCProperty.PROP_untyped_reference) {
                                        System.out.println("PROP_untyped_reference");
                                        currentProp.setPropertyArrayData(targetProp.getReferenceValueArray());
                                        currentCom.setTCProperty(currentProp);
                                    } else if (targetProp.getPropertyType() == TCProperty.PROP_typed_reference) {
                                        System.out.println("PROP_typed_reference");
                                        TCComponent[] coms = targetProp.getReferenceValueArray();

                                        ArrayList list = new ArrayList();
                                        for (int m = 0; m < coms.length; m++) {

                                            String typeName = coms[m].getTypeComponent().getTypeName();

                                            TCComponent newCom = createCom(typeName);

                                            TCProperty[] props = coms[m].getAllTCProperties();
                                            for (int x = 0; x < props.length; x++) {
                                                String propName = props[x].getPropertyName();

                                                if (propName.startsWith(Utils.getPrefix2())) {
                                                    TCProperty prop = newCom.getTCProperty(propName);
                                                    if (props[x].isNotArray()) {

                                                        prop.setPropertyData(props[x].getPropertyData());
                                                    } else {
                                                        prop.setPropertyArrayData((Object[]) props[x].getPropertyData());
                                                    }
                                                    newCom.setTCProperty(prop);
                                                }
                                            }
                                            list.add(newCom);

                                        }
                                        currentProp.setPropertyArrayData(list.toArray());
                                        currentCom.setTCProperty(currentProp);
                                    } else if (targetProp.getPropertyType() == TCProperty.PROP_untyped_reference) {
                                        System.out.println("PROP_untyped_reference");
                                        TCComponent[] coms = targetProp.getReferenceValueArray();
                                        currentProp.setPropertyArrayData(coms);
                                        currentCom.setTCProperty(currentProp);
                                    } else {
                                        System.out.println("PropertyType == " + targetProp.getPropertyType());
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
        TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
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


    private void saveTable(TCComponentItemRevision itemRev, TCProperty prop, ArrayList list, ArrayList propList, TCSession session, String tableTypeName) {
        try {
            FileStreamUtil fileStreamUtil = new FileStreamUtil();
            String logFile = fileStreamUtil.getTempPath("TablePanel");
            PrintStream printStream = fileStreamUtil.openStream(logFile);
            fileStreamUtil.writeData(printStream, "tableTypeName == " + tableTypeName);
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
                        for (int j = 0; j < datalist.size(); j++) {
                            createInstanceInput.add(propList.get(j).toString(), datalist.get(j));
                        }

                        iputList.add(createInstanceInput);
                        TCComponent obj = null;
                        List comps = null;
                        try {
                            comps = SOAGenericCreateHelper.create(session,
                                    createDefinition, iputList);
                            obj = (TCComponent) comps.get(0);
                            fileStreamUtil.writeData(printStream, "obj == " + obj);
                            comList.add(obj);
                            fileStreamUtil.writeData(printStream, "comList add == " + obj);
                        } catch (TCException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    prop.setPropertyArrayData(comList.toArray());
                    itemRev.setTCProperty(prop);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static boolean hasWritePrivilege(TCSession session, InterfaceAIFComponent comp) {
        try {
            TCAccessControlService accessControlService = session
                    .getTCAccessControlService();
            TCComponent component = null;

            if (comp instanceof TCComponentBOMLine) {
            	// 检查bomview
                TCComponentBOMLine bomline = (TCComponentBOMLine) comp;
                component = bomline.getItemRevision();
                TCComponent[] bvs = component
                        .getRelatedComponents("structure_revisions");
                if (bvs != null && bvs.length > 0) {
                    if (bvs[0] != null) {
                        component = bvs[0];
                    }
                }
            } else if (comp instanceof TCComponentPseudoFolder) {
                TCComponentPseudoFolder pseudoFolder = (TCComponentPseudoFolder) comp;
                TCComponent owningTarget = pseudoFolder.getOwningComponent();
                component = owningTarget;
            } else {
                component = (TCComponent) comp;
            }
            TCComponent[] accessors = {session.getUser(),
                    session.getCurrentGroup(), session.getCurrentRole()};

            boolean accessRight = accessControlService.checkAccessorsPrivilege(
                    accessors, component, "WRITE");
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

    /** 
     * 获取lov的值
     * Create by 郑良 2020.06.22
     * @param listOfValues	lov列表
     */ 
    public static ArrayList<String> getLOVList(TCComponentListOfValues listOfValues) {
        ArrayList<String> lovValues = new ArrayList<String>();
        if (listOfValues == null) {
            return lovValues;
        }
        try {
            String lovType = listOfValues.getProperty("lov_type");

            if (Fnd0ListOfValuesDynamic.equals(lovType)) {

                String valueProperty = listOfValues.getProperty("fnd0lov_value");
                String descProperty = listOfValues.getProperty("fnd0lov_desc");
                String typeProperty = listOfValues.getProperty("fnd0query_type");
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
                            String key = entry.getKey();
                            if (key.equals(valueProperty)) {
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

    //移除命名引用并更新数据集
    public static void updateDataset(TCSession session, TCComponentDataset dataset, String filePath) {
        try {
            TCComponentDatasetDefinitionType dsdefType = (TCComponentDatasetDefinitionType) session
                    .getTypeComponent("DatasetType");
            TCComponentDatasetDefinition definition = dsdefType
                    .find(dataset.getType());
            NamedReferenceContext[] contexts = definition
                    .getNamedReferenceContexts();
            String namedReference = contexts[0].getNamedReference();
            System.out.println("namedReference==" + namedReference);

            dataset.removeNamedReference(namedReference);

            dataset.setFiles(
                    new String[]{filePath},
                    new String[]{namedReference});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //创建BOM
    public static TCComponentBOMLine createBOMLine(TCSession session, TCComponentItemRevision itemRev) throws Exception {
        TCComponentRevisionRuleType revisionType = (TCComponentRevisionRuleType) session.getTypeComponent("RevisionRule");
        TCComponentRevisionRule revisionRule = revisionType.getDefaultRule();
        TCComponentBOMWindowType bomWindowType = (TCComponentBOMWindowType) session.getTypeComponent("BOMWindow");
        TCComponentBOMWindow bomWindow = bomWindowType.create(revisionRule);
        TCComponentBOMLine topBOMLine = bomWindow.setWindowTopLine(itemRev.getItem(), itemRev, null, null);
        return topBOMLine;
    }

//	public static List<TCComponentItemRevision> recursionBOMLine(TCComponentBOMLine bomLine) {	
//		List<TCComponentItemRevision> list = new ArrayList<TCComponentItemRevision>();
//		try {			
//			AIFComponentContext[] aifComponentContexts = bomLine.getChildren();
//			if( aifComponentContexts!= null&&aifComponentContexts.length>0){
//				for (int i = 0; i < aifComponentContexts.length; i++) {
//					TCComponentBOMLine childrenLine = (TCComponentBOMLine) aifComponentContexts[i].getComponent();
//					TCComponentItemRevision childrenRev = childrenLine.getItemRevision();
//					if("EDAComp Revision".equals(childrenRev.getType())&&!list.contains(childrenRev)){
//						list.add(childrenRev);
//					}
//					if(childrenLine.hasChildren()){					
//						recursionBOMLine(childrenLine);
//					}									
//				}
//			}
//		} catch (TCException e) {
//			e.printStackTrace();
//		}		
//		return list;			
//	}	

    public static String getRealValue(String type, String displayVAlue) {
        String realValue = "";
        String[] keys = new String[]{Utils.getTextValue("Type"), Utils.getTextValue("OwningUser"), Utils.getTextValue("Name")};
        String[] values = new String[]{"Folder", "infodba (infodba)", type};
        List<InterfaceAIFComponent> list = Utils.search("General...", keys, values);
        TCComponentFolder folder = null;
        if (list != null && list.size() > 0) {
            folder = (TCComponentFolder) list.get(0);
        }
        try {
            TCComponent[] coms = folder.getRelatedComponents("contents");
            for (TCComponent tcComponent : coms) {
                if (displayVAlue.equals(tcComponent.getProperty("object_name"))) {
                    realValue = tcComponent.getProperty("object_desc");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realValue;
    }

    /**
	 * 使用给定查询，查找对象
	 * 
	 * @return
	 * @param TCSession
	 *            当前会话
	 * @param String
	 *            查询名称
	 * @param String
	 *            [] 查询关键字数组
	 * @param String
	 *            [] 查询输入值数组
	 */
    public static TCComponent[] search(TCSession session, String s,
                                       String[] search1, String[] input1) {
        TCComponent atccomponent[] = null;
        TCComponentQueryType tccomponentquerytype = null;
        TCComponentQuery query = null;
        TCQueryClause tcqc[];
        ArrayList<String> keys;
        ArrayList<String> values;
        String key;
        String value;

        int i;
        try {
            tccomponentquerytype = (TCComponentQueryType) session
                    .getTypeComponent("ImanQuery");
            if (tccomponentquerytype != null) {
                query = (TCComponentQuery) tccomponentquerytype.find(s);
                tcqc = query.describe();
                if (search1 != null && input1 != null) {
                    atccomponent = query.execute(search1, input1);
                    return atccomponent;

                } else {
                    keys = new ArrayList<String>();
                    values = new ArrayList<String>();
                    for (i = 0; i < tcqc.length; i++) {
                        key = tcqc[i].getUserEntryNameDisplay();
                        value = tcqc[i].getDefaultValue();
                        if (key != null && key.length() > 0 && value != null
                                && value.length() > 0) {
                            keys.add(key);
                            values.add(value);
                        }
                    }
                    if (keys.size() > 0) {
                        atccomponent = query.execute(
                                keys.toArray(new String[keys.size()]),
                                values.toArray(new String[values.size()]));
                        return atccomponent;
                    }
                }
            }
        } catch (TCException e) {
            e.printStackTrace();
        }
        return atccomponent;
    }

    /**
	 * 获取模板根文件夹
	 * 
	 * @return TCComponent TCComponent模板根文件夹对象
	 * @param TCSession
	 *            当前会话
	 */
    private static TCComponent getTemplateRootFolder(TCSession session) {

        TCComponent[] comps = null;
        String[] keyArr = new String[]{Utils.getTextValue("object_name")};
        String[] valueArr = new String[]{"系统配置"};
        comps = search(session, "__TemplateFolder", keyArr, valueArr);
        if (comps != null && comps.length > 0 && comps[0] != null) {
            return comps[0];
        }
        return null;

    }

    /**
	 * 根据输入的名称，返回模板根文件夹下保存的同名对象。
	 * 
	 * @return TCComponent TCComponent对象
	 * @param TCSession
	 *            当前会话
	 * @param String
	 *            用于搜索的文件夹名称
	 */
    public static TCComponent getTemplateFolder(TCSession session, String s) {
        TCComponent comp = null;
        TCComponent[] comps = null;
        int i;
        comp = getTemplateRootFolder(session);
        if (comp != null) {
            if (s == null || s.equalsIgnoreCase("")) {
                return comp;
            }
            try {
                comp.refresh();
                comps = comp.getRelatedComponents("contents");
                if (comps != null) {
                    comp = null;
                    for (i = 0; i < comps.length; i++) {
                        if (comps[i].getProperty("object_name").equals(s)) {
                            comp = comps[i];
                            return comp;
                        }
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                comp = null;
                e.printStackTrace();
            }
        }
        return comp;

    }

    /**
	 * 根据输入的文件夹名称和数据集名称、类型，返回模板文件夹下保存的同名对象
	 * 
	 * @return TCComponent TCComponent对象
	 * @param TCSession
	 *            当前会话
	 * @param folder
	 *            用于搜索的文件夹名称
	 * @param da
	 *            用于搜索的对象名称
	 * @param daType
	 *            用于搜索的对象类型
	 */
    public static TCComponent getTemplateComponent(TCSession session,
                                                   String folder, String ds, String dsType) {
        TCComponent comp = null;
        TCComponent[] comps = null;
        int i;
        comp = getTemplateFolder(session, folder);
        if (comp != null) {
            try {
                comp.refresh();
                comps = comp.getRelatedComponents("contents");
                if (comps != null) {
                    comp = null;
                    for (i = 0; i < comps.length; i++) {
                        if (comps[i].getProperty("object_name").equals(ds)) {
                            if (dsType != null) {
                                if (comps[i].getType().equals(dsType)) {
                                    comp = comps[i];
                                    return comp;
                                }

                            } else {
                                comp = comps[i];
                                return comp;
                            }

                        }
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                comp = null;
                e.printStackTrace();
            }
        }
        return comp;

    }

    /**
	 * 根据命名引用移除版本下相应数据集
	 *
	 * @param itemRev
	 *            对象版本
	 * @param session
	 *            当前会话
	 * @param dsType
	 *            命名的引用
	 */
    public static void removePDFFromRev(TCComponentItemRevision itemRev, TCSession session, String dsType) {
        try {
            TCComponent[] coms = itemRev.getRelatedComponents("IMAN_specification");
            List<TCComponentDataset> list = new ArrayList<>();
            if (coms != null && coms.length > 0) {
                for (TCComponent tcComponent : coms) {
                    if (tcComponent instanceof TCComponentDataset) {
                        TCComponentDataset dataset = (TCComponentDataset) tcComponent;

                        TCComponentDatasetDefinitionType dsdefType = (TCComponentDatasetDefinitionType) session
                                .getTypeComponent("DatasetType");
                        TCComponentDatasetDefinition definition = dsdefType
                                .find(dataset.getType());
                        NamedReferenceContext[] referenceContexts = definition
                                .getNamedReferenceContexts();
                        for (NamedReferenceContext namedReferenceContext : referenceContexts) {
                            String namedReference = namedReferenceContext.getNamedReference();
                            System.out.println("namedReference==" + namedReference);
                            if (dsType.equals(namedReference)) {
                                list.add(dataset);
                                break;
                            }
                        }

                    }
                }
            }
            if (list.size() > 0) {
                itemRev.remove("IMAN_specification", list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /** 
     * 打开默认浏览器
     * Create by 郑良 2020.06.17
     * @param webSite	  网址
     */ 
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

    public static String getSheetmetalName() {
        String sheetmetalName = "";
        List<String> list = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        String typeStr = "S";
        String componentStr = ComponentLovPropertyBean.getText();
        String chassisStr = ChassisLovPropertyBean.getText();
        String purposeStr = PurposePropertyBean.getText();
        String projectStr = Utils.getPlatformName();
        list.add(typeStr);
        if (!Utils.isNull(componentStr)) {
            list.add(componentStr);
        }
        if (!Utils.isNull(chassisStr)) {
            list.add(chassisStr);
        }
        if (!Utils.isNull(purposeStr)) {
            list.add(purposeStr);
        }
        if (!Utils.isNull(projectStr)) {
            list.add(projectStr);
        }
        if (list.size() > 0) {
            for (String Str : list) {
                buffer.append(Str + "_");
            }
            sheetmetalName = buffer.toString().substring(0, buffer.toString().length() - 1);
        }
        return sheetmetalName;
    }

    public static String getScrewName() {
        String screwName = "";
        List<String> list = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        String fixedValueStr = "B-Screw";
        String headShapeStr = HeadShapePropertyBean.getText();
        String specificationStr = SpecificationPropertyBean.getText();
        String typeStr = TypePropertyBean.getText();
        String colorStr = ColorPropertyBean.getText();

        list.add(fixedValueStr);
        if (!Utils.isNull(headShapeStr)) {
            list.add(headShapeStr);
        }
        if (!Utils.isNull(specificationStr)) {
            list.add(specificationStr);
        }
        if (!Utils.isNull(typeStr)) {
            list.add(typeStr);
        }
        if (!Utils.isNull(colorStr)) {
            list.add(colorStr);
        }
        if (list.size() > 0) {
            for (String Str : list) {
                buffer.append(Str + "-");
            }
            screwName = buffer.toString().substring(0, buffer.toString().length() - 1);
        }
        return screwName;
    }

    public static String getStandoffName() {
        String standoffName = "";
        List<String> list = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        String fixedValueStr = "B-Standoff";
        String headShapeStr = HeadShapePropertyBean.getText();
        String specificationStr = SpecificationPropertyBean.getText();
        String colorStr = ColorPropertyBean.getText();
        list.add(fixedValueStr);
        if (!Utils.isNull(headShapeStr)) {
            list.add(headShapeStr);
        }
        if (!Utils.isNull(specificationStr)) {
            list.add(specificationStr);
        }

        if (!Utils.isNull(colorStr)) {
            list.add(colorStr);
        }
        if (list.size() > 0) {
            for (String Str : list) {
                buffer.append(Str + "-");
            }
            standoffName = buffer.toString().substring(0, buffer.toString().length() - 1);
        }
        return standoffName;
    }

    public static String getRubberName() {
        String rubberName = "";
        List<String> list = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        String fixedValueStr = "B-Rubber";
        String rubberShapeStr = RubberShapeLovPropertyBean.getText();
        String specificationStr = SpecificationPropertyBean.getText();
        list.add(fixedValueStr);
        if (!Utils.isNull(rubberShapeStr)) {
            list.add(rubberShapeStr);
        }
        if (!Utils.isNull(specificationStr)) {
            list.add(specificationStr);
        }
        if (list.size() > 0) {
            for (String Str : list) {
                buffer.append(Str + "-");
            }
            rubberName = buffer.toString().substring(0, buffer.toString().length() - 1);
        }
        return rubberName;
    }

    public static String getGasketName() {
        String gasketName = "";
        List<String> list = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        String fixedValueStr = "B-Gasket";
        String gasketShapeStr = GasketShapeLovPropertyBean.getText();
        String specificationStr = SpecificationPropertyBean.getText();
        list.add(fixedValueStr);
        if (!Utils.isNull(gasketShapeStr)) {
            list.add(gasketShapeStr);
        }
        if (!Utils.isNull(specificationStr)) {
            list.add(specificationStr);
        }
        if (list.size() > 0) {
            for (String Str : list) {
                buffer.append(Str + "-");
            }
            gasketName = buffer.toString().substring(0, buffer.toString().length() - 1);
        }
        return gasketName;
    }

    public static String getMylarName() {
        String mylarName = "";
        List<String> list = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        String fixedValueStr = "B-Mylar";
        String partStr = PartPropertyBean.getText();
        list.add(fixedValueStr);
        if (!Utils.isNull(partStr)) {
            list.add(partStr);
        }
        if (list.size() > 0) {
            for (String Str : list) {
                buffer.append(Str + "-");
            }
            mylarName = buffer.toString().substring(0, buffer.toString().length() - 1);
        }
        return mylarName;
    }

    public static String getLabelName() {
        String mylarName = "";
        List<String> list = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        String fixedValueStr = "B-Label";
        String partStr = PartPropertyBean.getText();
        list.add(fixedValueStr);
        if (!Utils.isNull(partStr)) {
            list.add(partStr);
        }
        if (list.size() > 0) {
            for (String Str : list) {
                buffer.append(Str + "-");
            }
            mylarName = buffer.toString().substring(0, buffer.toString().length() - 1);
        }
        return mylarName;
    }

    public static String getPlasticName() {
        String plasticName = "";
        StringBuffer buffer = new StringBuffer();
        List<String> list = new ArrayList<>();
        String typeStr = "P";
        String componentStr = ComponentLovPropertyBean.getText();
        String chassisStr = ChassisLovPropertyBean.getText();
        String purposeStr = PurposePropertyBean.getText();
        String projectStr = Utils.getPlatformName();

        list.add(typeStr);
        if (!Utils.isNull(componentStr)) {
            list.add(componentStr);
        }
        if (!Utils.isNull(chassisStr)) {
            list.add(chassisStr);
        }
        if (!Utils.isNull(purposeStr)) {
            list.add(purposeStr);
        }
        if (!Utils.isNull(projectStr)) {
            list.add(projectStr);
        }
        if (list.size() > 0) {
            for (String Str : list) {
                buffer.append(Str + "-");
            }
            plasticName = buffer.toString().substring(0, buffer.toString().length() - 1);

        }
        return plasticName;
    }

    public static String getMechanismName() {
        String mechanismName = "";
        StringBuffer buffer = new StringBuffer();
        List<String> list = new ArrayList<>();
        String typeStr = "Assy";
        String componentStr = ComponentLovPropertyBean.getText();
        String chassisStr = ChassisLovPropertyBean.getText();
        String purposeStr = PurposePropertyBean.getText();
        String projectStr = Utils.getPlatformName();

        list.add(typeStr);
        if (!Utils.isNull(componentStr)) {
            list.add(componentStr);
        }
        if (!Utils.isNull(chassisStr)) {
            list.add(chassisStr);
        }
        if (!Utils.isNull(purposeStr)) {
            list.add(purposeStr);
        }
        if (!Utils.isNull(projectStr)) {
            list.add(projectStr);
        }
        if (list.size() > 0) {
            for (String Str : list) {
                buffer.append(Str + "-");
            }
            mechanismName = buffer.toString().substring(0, buffer.toString().length() - 1);
        }
        return mechanismName;
    }

    public static String getPACName() {
        String PACName = "";
        List<String> list = new ArrayList<>();
        String platformName = Utils.getPlatformName();
        String pacTypeStr = PACTypeLovPropertyBean.getText();
        String phaseStr = ProgramPhaseLovPropertyBean.getText();

        if (!Utils.isNull(platformName)) {
            list.add(platformName);
        }
        if (!Utils.isNull(pacTypeStr)) {
            list.add(pacTypeStr);
        }
        if (!Utils.isNull(phaseStr)) {
            list.add(phaseStr);
        }
        StringBuffer buffer = new StringBuffer();
        if (list.size() > 0) {
            for (String Str : list) {
                buffer.append(Str + "_");
            }
            PACName = buffer.toString().substring(0, buffer.toString().length() - 1);
        }
        return PACName;
    }

    public static String getARTWORKName() {
        String artworkName = "";
        List<String> list = new ArrayList<>();
        String projectStr = Utils.getPlatformName();
        String artwrkTypeStr = ArtworkTypeLovPropertyBean.getText();
        String phaseStr = ProgramPhaseLovPropertyBean.getText();

        if (!Utils.isNull(projectStr)) {
            list.add(projectStr);
        }
        if (!Utils.isNull(artwrkTypeStr)) {
            list.add(artwrkTypeStr);
        }
        if (!Utils.isNull(phaseStr)) {
            list.add(phaseStr);
        }
        StringBuffer buffer = new StringBuffer();
        if (list.size() > 0) {
            for (String Str : list) {
                buffer.append(Str + "_");
            }
            artworkName = buffer.toString().substring(0, buffer.toString().length() - 1);
        }
        return artworkName;
    }


    public static TCComponentBOMLine getLineByRev(TCComponentItemRevision one) throws TCException {
        TCComponentBOMWindowType bomwindowtype = (TCComponentBOMWindowType) getTCSession().getTypeComponent("BOMWindow");
        TCComponentBOMWindow bomwindow = (TCComponentBOMWindow) bomwindowtype.create(null);
        TCComponentBOMLine root = (TCComponentBOMLine) bomwindow.setWindowTopLine(null, one, null, null);
        return root;
    }

    public static Workbook getWorkbook(File file) throws IOException {
        Workbook wb = null;
        FileInputStream in = new FileInputStream(file);
        if (file.getName().endsWith(".xls")) {
            wb = new HSSFWorkbook(in);
        } else if (file.getName().endsWith(".xlsx")) {
            wb = new XSSFWorkbook(in);
        }
        return wb;
    }

    /**
	 * 隐藏表格列
	 * Create by 郑良 2019.05.10	
	 * @param table  表格
	 * @param column 表格列索引
	 */
    public static void hideTableColumn(TCTable table, int column) {
        TableColumn tc = table.getTableHeader().getColumnModel().getColumn(
                column);
        tc.setMaxWidth(0);
        tc.setPreferredWidth(0);
        tc.setWidth(0);
        tc.setMinWidth(0);
        table.getTableHeader().getColumnModel().getColumn(column)
                .setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(column)
                .setMinWidth(0);
    }

    /**
	 * 获取数据集
	 * 
	 * @param preferenceName    首选项名称
	 */
    public static TCComponentDataset getDatasetBypreferenceName(String preferenceName) {
        TCComponentDataset dataset = null;
        TCSession session = Utils.getTCSession();
        try {
        	// 获取首选项的值 获取文件  
            String value = getPreferenceValue(preferenceName, TCPreferenceService.TC_preference_site);
            System.out.println("preferenceValue ==" + preferenceName);
            TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent("Item");
            TCComponentItem item = itemType.find(value);
            System.out.println("item ==" + item);

            TCComponentItemRevision rev = item.getLatestItemRevision();
            TCComponent com = rev.getRelatedComponent("IMAN_specification");
            System.out.println("com ==" + com);

            // 下载数据
            dataset = (TCComponentDataset) com;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataset;
    }

    /**
	 * 获取电子料Sub System 在数据库中是否存在
	 * @param PCBEZBOMItemRev 顶层BomLine版本对象
	 * @param session
	 */
    public static Map<String, String> getSubSystemList(TCComponentItemRevision PCBEZBOMItemRev, TCSession session) {
        // 获取电子料中的 SubSystem 验证数据
        Map<String, String> subSystemDataMap = getEdaSubSystemData(PCBEZBOMItemRev, session);
        Map<String, String> checkErrorDataMap = new HashMap<String, String>(subSystemDataMap.size());
        if (subSystemDataMap.size() > 0) {

            // 从首选项中 获取连接地址
            HashMap dbInfo = new GetPreferenceUtil().getHashMapPreference(session, TCPreferenceService.TC_preference_site,
                    "FX_DB_Info", "=");
            String ip = (String) dbInfo.get("IP");
            String username = (String) dbInfo.get("UserName");
            String password = (String) dbInfo.get("Password");
            String sid = (String) dbInfo.get("SID");
            String port = (String) dbInfo.get("Port");
            System.out.println("ip => " + ip + " => username => " + username + " => " + password + " => sid => " + sid + " => port => " + port);
            DBUtil dbUtil = new DBUtil();
            Connection conn = dbUtil.getConnection(ip, username, password, sid, port);

            if (null != conn) {
                Statement stmt = null;
                String sql = null;
                String subSystemVal = null;
                try {
                    conn.setAutoCommit(false);
                    stmt = conn.createStatement();
                    ResultSet rs = null;

                    for (String tempKey : subSystemDataMap.keySet()) {
                        boolean verifyResult = false;
                        subSystemVal = subSystemDataMap.get(tempKey);
                        if (null != subSystemVal && !"".equals(subSystemVal)) {
                            sql = "select count(1) from dell_modules" + " where lower(SUB_FUNCTION) = lower('" + subSystemVal + "') ";
                            System.out.println("执行的SQL语句 => " + sql);
                            rs = stmt.executeQuery(sql);
                            if (rs.next()) {
                            	// 在表中存在
                                if (rs.getInt(1) > 0) {
                                    verifyResult = true;
                                }
                            }
                        }

                        if (!verifyResult) {
                            checkErrorDataMap.put(tempKey, subSystemVal);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != stmt) {
                        try {
                            stmt.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    dbUtil.closeDB(conn);
                }
            }

        }

        return checkErrorDataMap;
    }

    /**
	 * 获取电子料 Sub System 验证数据
	 * 
	 * @return
	 */
    private static Map<String, String> getEdaSubSystemData(TCComponentItemRevision PCBEZBOMItemRev, TCSession session) {
        System.out.println("getEdaSubSystemData");

        Map<String, String> returnDataMap = new HashMap<String, String>();
        TCComponentBOMWindow bomWindom = null;
        AIFComponentContext[] childContext = null;

        try {
        	// 结构管理器
            TCComponentRevisionRuleType imancomponentrevisionruletype = (TCComponentRevisionRuleType) session
                    .getTypeComponent("RevisionRule");
            TCComponentRevisionRule imancomponentrevisionrule = imancomponentrevisionruletype.getDefaultRule();
            TCComponentBOMWindowType imancomponentbomwindowtype = (TCComponentBOMWindowType) session
                    .getTypeComponent("BOMWindow");
            bomWindom = imancomponentbomwindowtype.create(imancomponentrevisionrule);

            // 从结构管理器 获取最顶层的BOM
            TCComponentBOMLine topBOMLine = bomWindom.setWindowTopLine(PCBEZBOMItemRev.getItem(),
                    PCBEZBOMItemRev, null, null);

            // 从顶层的BOM中 获取子组件
            childContext = topBOMLine.getChildren();
            if (null != childContext && childContext.length > 0) {

                TCComponentBOMLine childBOMLine = null;
                TCComponentItemRevision childItemRev = null;
                String dispalyName = null;
                String subSystemVal = null;
                for (int j = 0; j < childContext.length; j++) {
                    childBOMLine = (TCComponentBOMLine) childContext[j].getComponent();
                    childItemRev = childBOMLine.getItemRevision();

                    // 判断子BOM是否为 电子料类型
                    if (ItemTypeName.EDACOMPREVISION.equals(childItemRev.getType())) {
                        dispalyName = childItemRev.getProperty("object_name");
                        subSystemVal = childBOMLine.getProperty("FX8_SubSystem");
                        returnDataMap.put(dispalyName, subSystemVal);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	// 关闭结构管理器
            if (null != bomWindom) {
                try {
                    bomWindom.close();
                } catch (TCException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("BOM Line SubSystem 验证数据  => " + returnDataMap);
        return returnDataMap;
    }

    public static String getPlatformName() {
        String plateformName = "";
        String[] plateformNames = PlatformLovPropertyBean.getSelectedPlatform();
        StringBuffer platformBuffer = new StringBuffer();
        if (plateformNames != null && plateformNames.length > 0) {
            for (String plateformNameStr : plateformNames) {
                platformBuffer.append(plateformNameStr + "_");
            }
        }
        if (platformBuffer.toString().endsWith("_")) {
            plateformName = platformBuffer.toString().substring(0, platformBuffer.toString().length() - 1);
        }
        return plateformName;
    }

    public static String transferSpecChar(String mfgPN, String replaceStr) {
    	String regEx="[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]";	
        Matcher matcher = Pattern.compile(regEx).matcher(mfgPN);
        return matcher.replaceAll(replaceStr).trim();
    }


}
