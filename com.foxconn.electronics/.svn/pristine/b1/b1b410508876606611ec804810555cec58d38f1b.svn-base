package com.foxconn.electronics.certificate;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentProjectType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.services.rac.workflow.WorkflowService;
import com.teamcenter.services.rac.workflow._2007_06.Workflow.ReleaseStatusInput;
import com.teamcenter.services.rac.workflow._2007_06.Workflow.ReleaseStatusOption;
import com.teamcenter.services.rac.workflow._2007_06.Workflow.SetReleaseStatusResponse;

import cn.hutool.core.util.ArrayUtil;

public class CerService
{
    private SimpleDateFormat       dateFormat         = new SimpleDateFormat("yyyy/MM/dd");
    private Map<String, String>    requestTypeRuleMap = new HashMap<String, String>();
    private TCComponentItemType    cerItemType;
    private TCComponentProjectType projectType;
    private TCComponentProject     project;
    private TCSession              tcSession;
    WorkflowService                wfService;
    private static final String    CERTYPE_NAME       = "D9_Certificate";
    private List<String>           spsaUserIdLov      = new ArrayList<String>();
    private boolean                isDT               = false;

    public CerService(TCComponentProject project) throws TCException
    {
        this.project = project;
        tcSession = RACUIUtil.getTCSession();
        TCComponentGroup currentGroup = tcSession.getCurrentGroup();
        String full_name = currentGroup.getProperty("full_name");
        isDT = full_name.contains("Desktop.D_Group");
        cerItemType = (TCComponentItemType) tcSession.getTypeComponent(CERTYPE_NAME);
        projectType = (TCComponentProjectType) tcSession.getTypeComponent(ITypeName.TC_Project);
        requestTypeRuleMap.put("新申請(A)", TCUtil.generateVersion(RACUIUtil.getTCSession(), "\"A\"NN", CERTYPE_NAME + "Revision"));
        requestTypeRuleMap.put("變更(B)", TCUtil.generateVersion(RACUIUtil.getTCSession(), "\"B\"NN", CERTYPE_NAME + "Revision"));
        requestTypeRuleMap.put("延展(C)", TCUtil.generateVersion(RACUIUtil.getTCSession(), "\"C\"NN", CERTYPE_NAME + "Revision"));
        spsaUserIdLov.addAll(TCUtil.getLovValues(tcSession, (TCComponentItemRevisionType) tcSession.getTypeComponent(CERTYPE_NAME + "Revision"), "d9_ActualUserID"));
        wfService = WorkflowService.getService(tcSession);
    }

    public TCComponentItemRevision createItem(CerPojo pojo) throws Exception
    {
        String currentRevId = requestTypeRuleMap.get(pojo.getRequestType());
        TCComponentItem currentItem = (TCComponentItem) TCUtil.createCom(tcSession, CERTYPE_NAME, null, null, currentRevId, null);
        projectType.assignToProject(project, currentItem);
        TCComponentItemRevision itemRev = currentItem.getLatestItemRevision();
        Map<String, String> tcAttrMap = getAttrMap(pojo, "d9_ValidDate", "d9_CertificationDate");
        itemRev.setProperties(tcAttrMap);
        itemRev.setDateProperty("d9_ValidDate", pojo.getValidDate());
        itemRev.setDateProperty("d9_CertificationDate", pojo.getCertificationDate());
        addRelatedFolder(currentItem, pojo);
        TCComponentDataset dateSetFile = createDataset(tcSession, pojo.getCerFile());
        ArrayList<TCComponent> componments = new ArrayList<TCComponent>();
        componments.add(itemRev);
        if (dateSetFile != null)
        {
            itemRev.add("IMAN_specification", dateSetFile);
            componments.add(dateSetFile);
        }
        setOrDelStatus(wfService, componments.toArray(new TCComponent[0]), "D9_FastRelease", "Append");
        return itemRev;
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
    public boolean setOrDelStatus(WorkflowService wfService, TCComponent[] tcComponents, String statusName, String operation) throws Exception
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
        String result = TCUtil.getErrorMsg(response.serviceData);
        if (CommonTools.isNotEmpty(result))
        {
            throw new Exception(result);
        }
        return true;
    }

    public TCComponentDataset createDataset(TCSession tcSession, File file) throws TCException
    {
        if (file == null)
        {
            return null;
        }
        String fileName = file.getName();
        int index = fileName.lastIndexOf(".");
        String fileFormat = "";
        if (index > 0)
        {
            fileFormat = fileName.substring(index + 1);
        }
        TCComponentDataset dateSetFile = null;
        switch (fileFormat.toUpperCase())
        {
            case "PDF":
                dateSetFile = TCUtil.createDataSet(tcSession, file.getAbsolutePath(), "PDF", fileName, "PDF_Reference");
                break;
            case "XLSX":
                dateSetFile = TCUtil.createDataSet(tcSession, file.getAbsolutePath(), "MSExcelX", fileName, "excel");
                break;
            case "XLS":
                dateSetFile = TCUtil.createDataSet(tcSession, file.getAbsolutePath(), "MSExcel", fileName, "excel");
                break;
            default:
                dateSetFile = TCUtil.createDataSet(tcSession, file.getAbsolutePath(), "Text", fileName, "Text");
                break;
        }
        return dateSetFile;
    }

    public void addRelatedFolder(TCComponent itemRev, CerPojo pojo) throws Exception
    {
        String group = tcSession.getCurrentGroup().getLocalizedFullName().toLowerCase();
        AIFComponentContext[] aif = project.getRelated("project_data");
        if (aif != null && aif.length > 0)
        {
            TCComponent projectSmartFolder = (TCComponent) aif[0].getComponent();
            AIFComponentContext[] folderChilds = projectSmartFolder.getChildren();
            TCComponent projectFolder = (TCComponent) folderChilds[0].getComponent();
            if (group.toLowerCase().contains("monitor") || group.toLowerCase().contains("dba"))
            {
                // mnt
                // folderPath = new String[] { "Safety", "P6(PVT)", "Safety certificate" };
                TCComponentFolder SafetyFolder = getFolder(projectFolder, "Safety");
                if (SafetyFolder == null)
                {
                    throw new RuntimeException(project.getProjectName() + " 专案对应的文件夹不存在 ：Safety 部门文件夹");
                }
                List<TCComponentFolder> folders = getSafetyCerFolders(SafetyFolder, null, 2);
                if (CommonTools.isEmpty(folders))
                {
                    throw new RuntimeException(project.getProjectName() + " 专案对应的文件夹不存在 ：Safety 部门文件夹下不存在阶段文件夹");
                }
                for (TCComponentFolder folder : folders)
                {
                    folder.add("contents", itemRev);
                }
            }
            else if (group.toLowerCase().contains("desktop"))
            {
                String[] folderPath = new String[0];
                if ("Environment".equalsIgnoreCase(pojo.getCertificateType()))
                {
                    folderPath = new String[] { "Env", "P7", "TCO Certification" };
                }
                else
                {
                    folderPath = new String[] { "Agency Certification", "P7", "*" };
                }
                TCComponentFolder folder = getFolder(projectFolder, folderPath);
                if (folder == null)
                {
                    throw new RuntimeException(project.getProjectName() + " 专案对应的文件夹不存在 ： " + String.join("/", folderPath));
                }
                folder.add("contents", itemRev);
            }
        }
        else
        {
            throw new RuntimeException(project.getProjectName() + " 专案文件夹不存在！");
        }
    }

    public List<TCComponentFolder> getSafetyCerFolders(TCComponentFolder safetyFolder, List<TCComponentFolder> safetyCerFolders, int level) throws TCException
    {
        level--;
        if (safetyCerFolders == null)
        {
            safetyCerFolders = new ArrayList<TCComponentFolder>();
        }
        AIFComponentContext[] childContexts = safetyFolder.getChildren();
        if (childContexts != null)
        {
            for (AIFComponentContext childContext : childContexts)
            {
                TCComponent childFolder = (TCComponent) childContext.getComponent();
                if (childFolder instanceof TCComponentFolder)
                {
                    TCComponentFolder cFolder = (TCComponentFolder) childFolder;
                    if (level == 0)
                    {
                        safetyCerFolders.add(cFolder);
                    }
                    else
                    {
                        getSafetyCerFolders(cFolder, safetyCerFolders, level);
                    }
                }
            }
        }
        return safetyCerFolders;
    }

    /**
     * 依据文件名数据获取多层级的文件夹对象，文件夹名必须按父到子的递归顺序存放到 folderNames 参数中
     * 
     * @param parentFolder
     * @param folderNames 如果是 * 星号 则是匹配第一个子文件夹
     * @return
     * @throws TCException
     * @author MW00054
     */
    public TCComponentFolder getFolder(TCComponent parentFolder, String... folderNames) throws TCException
    {
        AIFComponentContext[] childContexts = parentFolder.getChildren();
        if (childContexts != null)
        {
            for (AIFComponentContext childContext : childContexts)
            {
                TCComponent childFolder = (TCComponent) childContext.getComponent();
                if (childFolder instanceof TCComponentFolder)
                {
                    String folderName = childFolder.getProperty("object_name");
                    if (folderNames[0] != null && (folderNames[0].equalsIgnoreCase(folderName) || folderNames[0].equalsIgnoreCase("*")))
                    {
                        if (folderNames.length == 1)
                        {
                            {
                                return (TCComponentFolder) childFolder;
                            }
                        }
                        else if (folderNames.length > 1)
                        {
                            String[] newFolderNames = ArrayUtil.sub(folderNames, 1, folderNames.length);
                            return getFolder(childFolder, newFolderNames);
                        }
                    }
                }
            }
        }
        return null;
    }

    public < T > List<T> readExcel(File file, Class<T> cls) throws EncryptedDocumentException, IOException
    {
        List<T> list = new ArrayList<T>();
        Workbook wb = WorkbookFactory.create(file);
        Sheet sheet = wb.getSheetAt(0);
        int lastRow = sheet.getLastRowNum();
        for (int r = 1; r <= lastRow; r++)
        {
            Row row = sheet.getRow(r);
            T pojo = getPojo(cls, row);
            list.add(pojo);
        }
        wb.close();
        return list;
    }

    public String getCellValue(Cell srcCell)
    {
        String value = "";
        CellType srcCellType = srcCell.getCellType();
        if (srcCellType.equals(CellType.NUMERIC))
        {
            if (DateUtil.isCellDateFormatted(srcCell))
            {
                value = dateFormat.format(srcCell.getDateCellValue());
            }
            else
            {
                BigDecimal bd = new BigDecimal(String.valueOf(srcCell.getNumericCellValue()));
                value = bd.stripTrailingZeros().toPlainString();
            }
        }
        else if (srcCellType.equals(CellType.STRING))
        {
            value = (srcCell.getStringCellValue());
        }
        else if (srcCellType.equals(CellType.BLANK))
        {
        }
        return value;
    }

    public List<String> cerValidate(List<CerPojo> list)
    {
        List<String> errorList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++)
        {
            CerPojo pojo = list.get(i);
            if (pojo == null)
            {
                errorList.add("第 " + (i + 1) + " 行沒有數據");
            }
            else
            {
                Field[] fields = CerPojo.class.getDeclaredFields();
                Set<String> nullStr = new HashSet<String>();
                for (Field field : fields)
                {
                    field.setAccessible(true);
                    TCPropertes tcPropAnno = field.getAnnotation(TCPropertes.class);
                    if (tcPropAnno != null)
                    {
                        int cellNum = tcPropAnno.cell();
                        if (10 == cellNum && isDT)
                        {
                            continue;
                        }
                        String colStr = excelColIndexToStr(cellNum + 1);
                        if (cellNum > 0)
                        {
                            try
                            {
                                Object o = field.get(pojo);
                                if (o == null || o.toString().length() == 0)
                                {
                                    nullStr.add(colStr);
                                }
                            }
                            catch (IllegalArgumentException | IllegalAccessException e)
                            {
                                nullStr.add(colStr);
                                e.printStackTrace();
                            }
                        }
                    }
                }
                String errorMsg = "";
                if (nullStr.size() > 0)
                {
                    errorMsg += String.join(",", nullStr) + " 列没有数据;";
                }
                if (CommonTools.isNotEmpty(pojo.getCerFilePath()))
                {
                    File file = new File(pojo.getCerFilePath());
                    if (!file.exists())
                    {
                        errorMsg += pojo.getCerFilePath() + " 文件不存在";
                    }
                    else
                    {
                        if (file.isDirectory())
                        {
                            File[] files = file.listFiles();
                            if (files.length > 0)
                            {
                                pojo.setCerFile(files[0]);
                            }
                            else
                            {
                                errorMsg += pojo.getCerFilePath() + " 文件不存在";
                            }
                        }
                        else
                        {
                            pojo.setCerFile(file);
                        }
                    }
                }
                if (CommonTools.isNotEmpty(pojo.getRequestType()))
                {
                    if (!requestTypeRuleMap.containsKey(pojo.getRequestType()))
                    {
                        errorMsg += " 申請類型只能是 " + String.join(",", requestTypeRuleMap.keySet());
                    }
                }
                if (CommonTools.isNotEmpty(pojo.getValidDateStr()))
                {
                    if ("NA".equalsIgnoreCase(pojo.getValidDateStr()))
                    {
                        pojo.setValidDateStr("9999/12/31");
                    }
                    try
                    {
                        Date date = dateFormat.parse(pojo.getValidDateStr());
                        pojo.setValidDate(date);
                    }
                    catch (ParseException e)
                    {
                        errorMsg += pojo.getValidDate() + " 时间格式(yyyy/MM/dd) 不对,";
                        e.printStackTrace();
                    }
                }
                if (CommonTools.isNotEmpty(pojo.getCertificationDateStr()))
                {
                    try
                    {
                        Date date = dateFormat.parse(pojo.getCertificationDateStr());
                        pojo.setCertificationDate(date);
                    }
                    catch (ParseException e)
                    {
                        errorMsg += pojo.getCertificationDate() + " 时间格式(yyyy/MM/dd) 不对,";
                        e.printStackTrace();
                    }
                }
                String spasUserid = "(" + pojo.getRealAuthor() + ")";
                String spasUserIdLov = spsaUserIdLov.stream().filter(e -> e.contains(spasUserid)).findFirst().orElse(null);
                if (CommonTools.isEmpty(spasUserIdLov))
                {
                    errorMsg += pojo.getRealAuthor() + " 用戶工號不存在";
                }
                else
                {
                    pojo.setRealAuthor(spasUserIdLov);
                }
                if (errorMsg.length() > 0)
                {
                    errorList.add("第 " + (i + 1) + " 行 " + errorMsg);
                }
            }
        }
        return errorList;
    }

    public < T > T getPojo(Class<T> t, Row row)
    {
        Field[] fields = t.getDeclaredFields();
        T pojo = null;
        try
        {
            pojo = t.getConstructor().newInstance();
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (pojo != null)
        {
            for (Field field : fields)
            {
                field.setAccessible(true);
                TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
                if (tcProp != null)
                {
                    int cellNum = tcProp.cell();
                    if (cellNum >= 0)
                    {
                        Cell cell = row.getCell(cellNum);
                        if (cell != null)
                        {
                            try
                            {
                                String value = getCellValue(cell);
                                if (value != null)
                                {
                                    value = value.trim();
                                }
                                field.set(pojo, value);
                            }
                            catch (IllegalArgumentException | IllegalAccessException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        return pojo;
    }

    public Map<String, String> getAttrMap(Object pojo, String... exclusions) throws IllegalArgumentException, IllegalAccessException
    {
        Field[] fields = pojo.getClass().getDeclaredFields();
        Map<String, String> attrMap = new HashMap<String, String>();
        for (Field field : fields)
        {
            field.setAccessible(true);
            TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
            if (tcProp != null)
            {
                String tcAtrrName = tcProp.tcProperty();
                if (CommonTools.isNotEmpty(tcAtrrName) && (exclusions != null && !ArrayUtil.contains(exclusions, tcAtrrName)))
                {
                    Object vaule = field.get(pojo);
                    if (CommonTools.isNotEmpty(vaule))
                    {
                        attrMap.put(tcAtrrName, vaule + "");
                    }
                }
            }
        }
        return attrMap;
    }

    public static String excelColIndexToStr(int columnIndex)
    {
        if (columnIndex <= 0)
        {
            return null;
        }
        String columnStr = "";
        columnIndex--;
        do
        {
            if (columnStr.length() > 0)
            {
                columnIndex--;
            }
            columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
            columnIndex = (int) ((columnIndex - columnIndex % 26) / 26);
        } while (columnIndex > 0);
        return columnStr;
    }
}
