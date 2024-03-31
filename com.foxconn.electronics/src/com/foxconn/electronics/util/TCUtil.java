package com.foxconn.electronics.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.swing.filechooser.FileSystemView;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.domain.Constants;
import com.foxconn.electronics.explodebom.domain.BOMLineBean;
import com.foxconn.tcutils.util.CommonTools;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.create.BOCreateDefinitionFactory;
import com.teamcenter.rac.common.create.CreateInstanceInput;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.common.create.SOAGenericCreateHelper;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.ListOfValuesInfo;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentProcessType;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCComponentSite;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentTaskTemplateType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCSiteInfo;
import com.teamcenter.rac.kernel.TCTextService;
import com.teamcenter.rac.kernel.TCTypeService;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.services.loose.core.ProjectLevelSecurityService;
import com.teamcenter.services.loose.core._2007_09.ProjectLevelSecurity.AssignedOrRemovedObjects;
import com.teamcenter.services.rac.core.LOVService;
import com.teamcenter.services.rac.core._2013_05.LOV.InitialLovData;
import com.teamcenter.services.rac.core._2013_05.LOV.LOVSearchResults;
import com.teamcenter.services.rac.core._2013_05.LOV.LOVValueRow;
import com.teamcenter.services.rac.core._2013_05.LOV.LovFilterData;
import com.teamcenter.services.rac.workflow.WorkflowService;
import com.teamcenter.services.rac.workflow._2007_06.Workflow.ReleaseStatusInput;
import com.teamcenter.services.rac.workflow._2007_06.Workflow.ReleaseStatusOption;
import com.teamcenter.services.rac.workflow._2007_06.Workflow.SetReleaseStatusResponse;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.ItemRevision;
@Deprecated
public class TCUtil
{
    static TCTextService tcTextService = null;

    /**
     * 获取程序桌面
     * 
     * @return
     */
    public static AIFDesktop getDesktop()
    {
        return AIFDesktop.getActiveDesktop();
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

    /**
     * 获取系统桌面路径
     * 
     * @return
     */
    private static String getSystemDesktop()
    {
        File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
        return desktopDir.getAbsolutePath();
    }

    /**
     * 提示框
     * 
     * @param info
     */
    public static void infoMsgBox(String info)
    {
        MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, "提示", MessageBox.INFORMATION);
    }

    /**
     * 
     * @param <T>
     * @param bean
     * @param tcObject
     * @return
     * @throws TCException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static < T > T tcPropMapping(T bean, TCComponent tcObject) throws IllegalArgumentException, IllegalAccessException, TCException
    {
        if (bean != null && tcObject != null)
        {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++)
            {
                fields[i].setAccessible(true);
                TCPropName tcPropName = fields[i].getAnnotation(TCPropName.class);
                if (tcPropName != null)
                {
                    fields[i].set(bean, tcObject.getProperty(tcPropName.value()));
                }
            }
        }
        return bean;
    }

    public static TCSession getTCSession()
    {
        return RACUIUtil.getTCSession();
    }

    /**
     * 根据LOV名称获取值列表
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
     * 
     * @param bean
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @author Robert
     */
    public static Map<String, String> getBeanTCPropMap(Object bean) throws IllegalArgumentException, IllegalAccessException
    {
        if (bean != null)
        {
            Map<String, String> propMap = new HashMap<String, String>();
            Field[] fields = bean.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++)
            {
                fields[i].setAccessible(true);
                TCPropName tcPropName = fields[i].getAnnotation(TCPropName.class);
                if (tcPropName != null)
                {
                    propMap.put(tcPropName.value(), (String) fields[i].get(tcPropName));
                }
            }
            return propMap;
        }
        return null;
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
    public static List<String> getArrayPreference(int scope, String preferenceName) 
    {
       try {
    	TCPreferenceService tCPreferenceService=RACUIUtil.getTCSession().getPreferenceService();
    	tCPreferenceService.refresh();
        String[] array = tCPreferenceService.getStringArray(scope, preferenceName);
        return Arrays.asList(array);
        }catch(Exception e) {
        	System.out.println(e);
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
    	try {
    	TCPreferenceService tCPreferenceService=RACUIUtil.getTCSession().getPreferenceService();
    	tCPreferenceService.refresh();
    	return tCPreferenceService.getString(scope, preferenceName);
    	}catch(Exception e) {
    		System.out.println(e);
    	}
    	return null;
       
    }

    /**
     * 
     * @param rootLine
     * @param lines
     * @param unpacked 是否解包 true 解包 false 不解包
     * @return
     * @throws TCException
     */
    public static List<TCComponentBOMLine> getTCComponmentBOMLines(TCComponentBOMLine rootLine, List<TCComponentBOMLine> lines, boolean unpacked) throws TCException
    {
        if (lines == null)
        {
            lines = new ArrayList<TCComponentBOMLine>();
        }
        AIFComponentContext[] componmentContext = rootLine.getChildren();
        if (componmentContext != null)
        {
            for (int i = 0; i < componmentContext.length; i++)
            {
                TCComponentBOMLine bomLine = (TCComponentBOMLine) componmentContext[i].getComponent();
                if (unpacked)
                {
                    if (bomLine.isPacked())
                    {
                        TCComponentBOMLine[] packedLines = bomLine.getPackedLines();
                        bomLine.unpack();
                        if (packedLines != null && packedLines.length > 0)
                        {
                            lines.add(bomLine);
                            lines.addAll(Arrays.asList(packedLines));
                        }
                        else
                        {
                            lines.add(bomLine);
                        }
                    }
                    else
                    {
                        lines.add(bomLine);
                    }
                }
                else
                {
                    lines.add(bomLine);
                }
                getTCComponmentBOMLines(bomLine, lines, unpacked);
            }
        }
        return lines;
    }

    /**
     * 
     * @param <T>
     * @param bean
     * @return List<Field>
     * @throws TCException
     */
    public static < T > List<Field> getOrderedField(T bean) throws TCException
    {
        if (bean != null)
        {
            List<Field> fieldLst = new ArrayList<Field>();
            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields)
            {
                if (field.getAnnotation(TCPropName.class) != null)
                {
                    fieldLst.add(field);
                }
            }
            fieldLst.sort(Comparator.comparingInt(e -> e.getAnnotation(TCPropName.class).order()));
            return fieldLst;
        }
        return new ArrayList<Field>();
    }

    /**
     * 
     * @param <T>
     * @param bean
     * @return String
     * @throws IllegalAccessException
     * @throws TCException
     */
    public static < T > String getProcessInfo(T bean) throws IllegalAccessException, TCException
    {
        if (bean != null)
        {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++)
            {
                fields[i].setAccessible(true);
                TCPropName tcPropName = fields[i].getAnnotation(TCPropName.class);
                if (tcPropName != null && tcPropName.isProcessField())
                {
                    return (String) fields[i].get(bean);
                }
            }
        }
        return "";
    }

    /**
     * 
     * @param <T>
     * @param bean
     * @return String
     * @throws IllegalAccessException
     * @throws TCException
     */
    public static < T > String getRequiredInfo(T bean) throws IllegalAccessException, TCException
    {
        List<String> retLst = new ArrayList<String>();
        ;
        if (bean != null)
        {
            Field[] fields = bean.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++)
            {
                fields[i].setAccessible(true);
                TCPropName tcPropName = fields[i].getAnnotation(TCPropName.class);
                if (tcPropName != null && tcPropName.isRequire())
                {
                    String sValue = (String) fields[i].get(bean);
                    if (true == isNull(sValue))
                    {
                        retLst.add(fields[i].getName() + " 字段为空");
                    }
                }
            }
        }
        if (retLst.size() > 0)
        {
            return ("【" + ((BOMLineBean) bean).getDescription()) + "】 " + retLst.stream().collect(Collectors.joining("\t"));
        }
        return "";
    }

    /**
     * 
     * @param <T>
     * @param bean
     * @return String
     * @throws IllegalAccessException
     * @throws TCException
     */
    public static < T > String getBOMLineInfo(T bean, List<Field> fieldLst, String processInfo) throws IllegalAccessException, TCException
    {
        List<String> retLst = new ArrayList<String>();
        if (bean != null)
        {
            for (Field field : fieldLst)
            {
                field.setAccessible(true);
                TCPropName tcPropName = field.getAnnotation(TCPropName.class);
                if (tcPropName != null && tcPropName.isProcessField())
                {
                    retLst.add(processInfo);
                }
                else
                {
                    retLst.add((String) field.get(bean));
                }
            }
        }
        return retLst.stream().collect(Collectors.joining("\t"));
    }

    /**
     * 
     * @param location
     * @param separator
     * @param limit
     * @return String
     * @throws Exception
     */
    public static List<String> splitText(String location, String separator, int limit) throws TCException
    {
        List<String> outLstList = new ArrayList<>();
        String[] textArray = location.split(separator);
        int textArrLen = textArray.length;
        if (textArrLen > 0)
        {
            int line = textArrLen / limit + (textArrLen % limit > 0 ? 1 : 0);
            int iArrayIndex = 0;
            for (int i = 0; i < line; i++)
            {
                String subText = "";
                for (int j = 1; (iArrayIndex < textArrLen) && (j <= limit); iArrayIndex++, j++)
                {
                    subText += textArray[iArrayIndex];
                    if (iArrayIndex + 1 < textArrLen)
                    {
                        subText += separator;
                    }
                }
                outLstList.add(subText);
            }
        }
        return outLstList;
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
        System.out.println("selectFilepath == " + selectFilepath);
        n[0] = ref_type;
        System.out.println("ref_type == " + ref_type);
        dataset.setFiles(p, n);
        return dataset;
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
                if (isNull(name))
                {
                    return false;
                }
                for (String statusName : statusNameArray)
                {
                    if (!isNull(statusName) && name.equalsIgnoreCase(statusName))
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
                if (!isNull(name) && name.equalsIgnoreCase(statusName))
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

    public static boolean isNull(String info)
    {
        return info == null || info.trim().length() < 1;
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

    public static List<InterfaceAIFComponent> search(String searchName, String[] keys, String[] values)
    {
        List<InterfaceAIFComponent> res;
        InterfaceAIFComponent[] temp;
        try
        {
            temp = getTCSession().search(searchName, keys, values);
            if (temp == null || temp.length < 1)
            {
                res = new ArrayList<InterfaceAIFComponent>();
            }
            else
            {
                res = Arrays.asList(temp);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            res = new ArrayList<InterfaceAIFComponent>();
        }
        return res;
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
                // System.out.println("queryName: " + element.getAttributeName());
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
        	System.out.print("close_bypass");
            TCUserService userService = session.getUserService();
            userService.call("close_bypass", new String[] { "" });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static < T, R > Function<T, R> wrap(CheckedFunction<T, R> checkedFunction)
    {
        return t -> {
            try
            {
                return checkedFunction.apply(t);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        };
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
            if (isNull(value))
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

    /**
     * 弹出消息框
     * 
     * @return
     */
    public static void infoMsgBox(String info, String title)
    {
        if (CommonTools.isEmpty(title))
        {
            MessageBox.displayModalMessageBox(getDesktop().getFrame(), info, "提示", MessageBox.INFORMATION);
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

    public static String findBUNameByProject(TCComponentItem item) throws Exception
    {
        if (item != null)
        {
            String projectIds = item.getProperty("project_ids");
            if (!isNull(projectIds))
            {
                TCComponent[] coms = executeQuery(getTCSession(), "__D9_Find_Series_Folder", new String[] { "D9_PlatformFoundFolder:contents.d9_SPAS_ID" }, new String[] { projectIds });
                if (coms != null && coms.length > 0)
                {
                    String buName = coms[0].getProperty("object_desc");
                    return buName;
                }
            }
        }
        return "";
    }

    public static TCComponentItemRevision revise(TCComponentItemRevision itemRevision)
    {
        // 升版
        try
        {
            return itemRevision.saveAs("");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isBom(TCComponentItemRevision revision)
    {
        TCComponentBOMWindow window = null;
        try
        {
            window = TCUtil.createBOMWindow(TCUtil.getTCSession());
            TCComponentBOMLine bomLine = window.setWindowTopLine(revision.getItem(), revision, null, null);
            return bomLine.hasChildren(); // 判断BOMLine是否含有子
        }
        catch (TCException e)
        {
            return false;
        }
        finally
        {
            if (CommonTools.isNotEmpty(window))
            {
                try
                {
                    window.save();
                    window.close();
                }
                catch (TCException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    public static boolean isBOM(TCComponentItemRevision itemRev) throws TCException {
    	itemRev.refresh();
    	TCComponent[] relatedComponents = itemRev.getRelatedComponents("ps_children");
    	return (relatedComponents != null || relatedComponents.length > 0) ? true : false;
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

    /**
     * 指派项目
     * 
     * @param folder
     * @param project
     * @throws Exception
     */
    public static void assignedProject(TCSession session, ModelObject folder, ModelObject project) throws Exception
    {
        AssignedOrRemovedObjects assignedOrRemovedObjects = new AssignedOrRemovedObjects();
        assignedOrRemovedObjects.objectToAssign = new ModelObject[] { folder };
        // assignedOrRemovedObjects.objectToRemove = null;
        assignedOrRemovedObjects.projects = new ModelObject[] { project };
        AssignedOrRemovedObjects[] aassignedorremovedobjects = new AssignedOrRemovedObjects[1];
        aassignedorremovedobjects[0] = assignedOrRemovedObjects;
        ProjectLevelSecurityService projectLevelSecurityService = ProjectLevelSecurityService.getService(session.getSoaConnection());
        ServiceData serviceData = projectLevelSecurityService.assignOrRemoveObjects(aassignedorremovedobjects);
        if (serviceData.sizeOfPartialErrors() > 0)
        {
            throw new Exception(serviceData.getPartialError(0).toString());
        }
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
     * 获取电脑桌面路径
     */
    public static String getDesktopPath()
    {
        File homeDirectory = FileSystemView.getFileSystemView().getHomeDirectory();
        return homeDirectory.getAbsolutePath();
    }

    /**
     * 获取Dataset文件
     * 
     * @param dataset
     * @return
     */
    public static File getDataSetFile(TCComponentDataset dataset)
    {
        File file = null;
        try
        {
            TCComponentTcFile[] tcFiles = dataset.getTcFiles();
            if (tcFiles.length > 0)
            {
                TCComponentTcFile tcFile = tcFiles[0];
                file = tcFile.getFmsFile();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 下载数据集
     * 
     * @param dataset 数据集
     * @param dir 文件存放路径
     * @throws Exception
     */
    public static void downloadFile(TCComponentDataset dataset, String dir) throws Exception
    {
        String datasetName = dataset.getProperty("object_name");
        TCComponentTcFile[] files = dataset.getTcFiles();
        if (files == null || files.length == 0)
        {
            throw new Exception("dataset未找到文件!");
        }
        boolean isSuccess = false;
        if (files.length > 1)
        {
            isSuccess = createFolder(datasetName, dir);
        }
        if (isSuccess)
        {
            dir = dir + File.separator + datasetName;
        }
        for (int i = 0; i < files.length; i++)
        {
            TCComponentTcFile tcfile = files[i];
            String fileName = tcfile.getProperty("original_file_name");
            File tempfile = tcfile.getFmsFile();
            File newfile = new File(dir + File.separator + fileName);
            copyFile(tempfile, newfile);
        }
    }

    /**
     * 复制文件
     * 
     * @param sourceFile
     * @param targetFile
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File targetFile) throws IOException
    {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try
        {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1)
            {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        }
        finally
        {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    public static boolean createFolder(String folderName, String dir)
    {
        boolean isSuccess = false;
        File file = new File(dir + File.separator + folderName);
        if (!file.exists())
        {
            isSuccess = file.mkdir();
        }
        return isSuccess;
    }

    /**
     * 生成ItemId
     * 
     * @param session
     * @param itemTypeName
     * @return
     */
    public static String generateItemId(TCSession session, String itemTypeName)
    {
        String itemId = "";
        try
        {
            TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent(itemTypeName);
            itemId = itemType.getNewID();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return itemId;
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

    /**
     * 获取动态LOV
     * 
     * @param session
     * @param itemRevType
     * @param prop
     * @return
     */
    public static ArrayList<String> dynamicLovValue(TCSession session, TCComponentItemRevisionType itemRevType, String prop)
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
                        @SuppressWarnings("unchecked")
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

    public static boolean hasChildren(TCSession session, TCComponentItemRevision itemRevision)
    {
        TCComponentBOMWindow bomWindow = null;
        AIFComponentContext[] children;
        try
        {
            bomWindow = TCUtil.createBOMWindow(session);
            TCComponentBOMLine topBomline = TCUtil.getTopBomline(bomWindow, itemRevision);
            children = topBomline.getChildren();
            return children.length == 0;
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (bomWindow != null)
            {
                try
                {
                    bomWindow.close();
                }
                catch (TCException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static String getSiteName()
    {
        try
        {
            AbstractAIFApplication app = AIFUtility.getCurrentApplication();
            TCSession tcSession = (TCSession) app.getSession();
            TCComponentSite site = tcSession.getCurrentSite();
            TCSiteInfo info = site.getSiteInfo();
            System.out.println("SiteID=" + info.getSiteID());
            System.out.println("SiteName=" + info.getSiteName());
            return info.getSiteName();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isPRD()
    {
        return "TcPRD".equals(getSiteName());
    }

    public static boolean isPOC()
    {
        return !isPRD();
    }

    public static TCComponentFolder getProjectFolderByFolderNameAndCategory(TCComponent projectFolder, String folderCategory, String folderName, int level) throws Exception
    {
        AIFComponentContext[] childrenFolder = projectFolder.getRelated(Constants.CONTENTS);
        if (level == 0)
        {
            return null;
        }
        for (int i = 0; i < childrenFolder.length; i++)
        {
            TCComponent component = (TCComponent) childrenFolder[i].getComponent();
            String folderType = component.getType();
            if (!folderType.equals(folderCategory))
            {
                continue;
            }
            String name = component.getProperty(Constants.OBJECT_NAME);
            if (name.equals(folderName))
            {
                return (TCComponentFolder) component;
            }
            TCComponentFolder folder = getProjectFolderByFolderNameAndCategory(component, folderCategory, folderName, level--);
            if (folder == null)
            {
                continue;
            }
            return folder;
        }
        return null;
    }
}
