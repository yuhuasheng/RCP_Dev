package com.foxconn.electronics.convertebom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.collections4.map.HashedMap;

import com.foxconn.electronics.domain.Constants;
import com.foxconn.electronics.managementebom.updatebom.service.UpdateEBOMService;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpUtil;

public class SelfPart
{
    private String                               drawingNo;
    private String                               projectName;
    private String                               productLine;
    private TCComponentFolder                    selfPartFolder;
    private List<TCComponentFolder>              partFolders           = new ArrayList<TCComponentFolder>();
    private Map<String, TCComponentItemRevision> actionMsgMap          = new HashedMap<String, TCComponentItemRevision>();
    private List<String>                         folderNames           = new ArrayList<String>();
    static final String                          FOLDER_NAME_SEPARATOR = " / ";
    static final String[]                        PSU_CLS_ARRAY         = { "Power&PI&INV&LED CONV BD", "LED driver BD", "LED Lighting BD", "LED Lighting  Driver BD", "DC JACK", "Safety POWER BD" };
    private TCComponentFolder                    projectFolder;

    public Map<String, TCComponentItemRevision> getActionMsgMap()
    {
        return actionMsgMap;
    }

    public SelfPart(TCComponentItem item) throws Exception
    {
        getProjectFolder(item);
    }

    public List<Object[]> getPhase(String selectFolderName)
    {
        String[] folderNames = selectFolderName.split(FOLDER_NAME_SEPARATOR);
        TCComponentFolder layoutFolder = getChildFolderByName(projectFolder, "Layout");
        try
        {
            layoutFolder.refresh();
        }
        catch (TCException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        List<Object[]> phaseFolderInfo = getPhaseFoldersByModeName(layoutFolder, folderNames);
        return phaseFolderInfo;
    }

    public List<Object[]> getPhaseFoldersByModeName(TCComponentFolder layoutFolder, String[] folderNames)
    {
        List<Object[]> list = new ArrayList<>();
        try
        {
            AIFComponentContext[] childrenFolder = layoutFolder.getRelated(Constants.CONTENTS);
            if (childrenFolder.length > 0)
            {
                for (int i = 0; i < childrenFolder.length; i++)
                {
                    TCComponent component = (TCComponent) childrenFolder[i].getComponent();
                    if (component instanceof TCComponentFolder)
                    {
                        String name = component.getProperty(Constants.OBJECT_NAME);
                        String type = component.getProperty("object_type");
                        if ("D9_PhaseFolder".equalsIgnoreCase(type) || "Phase Folder".equalsIgnoreCase(type))
                        {
                            TCComponentFolder modelFolder = getFolder((TCComponentFolder) component, folderNames);
                            if (modelFolder != null)
                            {
                                Object[] folderInfo = new Object[2];
                                folderInfo[0] = name;
                                folderInfo[1] = modelFolder;
                                list.add(folderInfo);
                            }
                        }
                    }
                }
            }
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return list;
    }

    public TCComponentFolder getFolder(TCComponentFolder parentFolder, String... folderNames) throws TCException
    {
        AIFComponentContext[] childContexts = parentFolder.getChildren();
        if (childContexts.length > 0)
        {
            for (AIFComponentContext childContext : childContexts)
            {
                TCComponent childFolder = (TCComponent) childContext.getComponent();
                if (childFolder instanceof TCComponentFolder)
                {
                    String folderName = childFolder.getProperty("object_name");
                    if (folderNames[0] != null && folderNames[0].trim().equalsIgnoreCase(folderName))
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
                            return getFolder((TCComponentFolder) childFolder, newFolderNames);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static TCComponentFolder getChildFolderByName(TCComponentFolder projectFolder, String folderName)
    {
        try
        {
            AIFComponentContext[] childrenFolder = projectFolder.getRelated(Constants.CONTENTS);
            if (childrenFolder.length > 0)
            {
                for (int i = 0; i < childrenFolder.length; i++)
                {
                    TCComponent component = (TCComponent) childrenFolder[i].getComponent();
                    if (component instanceof TCComponentFolder)
                    {
                        String name = component.getProperty(Constants.OBJECT_NAME);
                        if (name.equals(folderName))
                        {
                            return (TCComponentFolder) component;
                        }
                    }
                }
            }
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    // 獲取專案文件夾
    private void getProjectFolder(TCComponentItem item) throws Exception
    {
        drawingNo = item.getProperty(Constants.ITEM_ID);
        String projectIds = item.getProperty(Constants.PROJECT_IDS);
        if (" ".equals(projectIds))
        {
            throw new Exception("當前原理圖未指派專案，請指派專案后進行此操作！");
        }
        String projectId = Arrays.asList(projectIds.split(",")).get(0);
        TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
        TCComponent[] queryReslut = TCUtil.executeQuery(session, Constants.FIND_PROJECT_FOLDER, new String[] { Constants.SPAS_ID }, new String[] { projectId });
        if (queryReslut.length == 0)
        {
            throw new Exception("【" + projectId + "】項目文件夾不存在！");
        }
        projectFolder = (TCComponentFolder) queryReslut[0];
        projectName = projectFolder.getProperty(Constants.OBJECT_NAME);
        projectFolder.refresh();
        AIFComponentContext[] acx = projectFolder.whereReferenced();
        if (acx.length == 0)
        {
            // 如果TC RAC 没有取到关系 ，再用http 调用 获取bu
            try
            {
                String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL") + "/tc-integrate/spas/getProjectBu?projId=" + projectId;
                System.out.println("get bu url :: " + url);
                productLine = HttpUtil.get(url, 10000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            TCComponentFolder seriesFolder = (TCComponentFolder) acx[0].getComponent();
            productLine = seriesFolder.getProperty(Constants.OBJECT_DESC);
        }
        if (!Constants.MNT.equals(productLine) && !Constants.PRT.equals(productLine))
        {
            throw new Exception("BOM指派的專案為【DT】，請選擇【MNT】【PRT】專案進行此操作！");
        }
        getSelfPartFolder(projectFolder);
        if (selfPartFolder == null)
        {
            throw new Exception("【自編物料協同工作區】不存在！");
        }
        getPartFolder(selfPartFolder, true);
        if (partFolders.size() == 0)
        {
            throw new Exception("【物料文件夾】不存在！");
        }
    }

    // 獲取自編料號文件夾
    private void getSelfPartFolder(TCComponent projectFolder) throws Exception
    {
        AIFComponentContext[] childrenFolder = projectFolder.getRelated(Constants.CONTENTS);
        if (childrenFolder.length > 0)
        {
            for (int i = 0; i < childrenFolder.length; i++)
            {
                TCComponent component = (TCComponent) childrenFolder[i].getComponent();
                String name = component.getProperty(Constants.OBJECT_NAME);
                if (name.equals("自編物料協同工作區"))
                {
                    selfPartFolder = (TCComponentFolder) component;
                }
                getSelfPartFolder(component);
            }
        }
    }

    // 獲取料號文件夾
    private void getPartFolder(TCComponentFolder selfPartFolder, boolean flag) throws Exception
    {
        AIFComponentContext[] related = selfPartFolder.getRelated(Constants.CONTENTS);
        for (int i = 0; i < related.length; i++)
        {
            InterfaceAIFComponent component = related[i].getComponent();
            if (component instanceof TCComponentFolder)
            {
                TCComponentFolder partFolder = (TCComponentFolder) component;
                if (flag)
                {
                    getPartFolder(partFolder, false);
                }
                else
                {
                    partFolders.add(partFolder);
                    String name = selfPartFolder.getProperty(Constants.OBJECT_NAME) + FOLDER_NAME_SEPARATOR + partFolder.getProperty(Constants.OBJECT_NAME);
                    folderNames.add(name);
                }
            }
        }
    }

    public String getDrawingNo()
    {
        return drawingNo;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public String getProductLine()
    {
        return productLine;
    }

    public String[] getPartFolderName() throws TCException
    {
        return folderNames.toArray(new String[0]);
        // String[] names = new String[partFolders.size()];
        // for (int i = 0; i < partFolders.size(); i++) {
        // TCComponentFolder tcComponentFolder = partFolders.get(i);
        // String name = tcComponentFolder.getProperty(Constants.OBJECT_NAME);
        // names[i] = name;
        // }
        // return names;
    }

    public List<TCComponentItemRevision> getSelfPart(String partFolderName) throws Exception
    {
        // String subFolderName = partFolderName.split(FOLDER_NAME_SEPARATOR)[1];
        // TCComponentFolder partFolder = null;
        // for (int i = 0; i < partFolders.size(); i++)
        // {
        // TCComponentFolder tcComponentFolder = partFolders.get(i);
        // String folderName = tcComponentFolder.getProperty(Constants.OBJECT_NAME);
        // if (folderName.equals(subFolderName))
        // {
        // partFolder = tcComponentFolder;
        // break;
        // }
        // }
        List<TCComponentItemRevision> selfParts = new ArrayList<TCComponentItemRevision>();
        TCComponentFolder partFolder = getFolder(selfPartFolder, partFolderName.split(FOLDER_NAME_SEPARATOR));
        AIFComponentContext[] related = partFolder.getRelated(Constants.CONTENTS);
        if (related.length == 0)
        {
            throw new Exception("物料文件夾下未查找到任何物料！\n");
        }
        for (int i = 0; i < related.length; i++)
        {
            TCComponentItem item = (TCComponentItem) related[i].getComponent();
            TCComponentItemRevision itemRev = item.getLatestItemRevision();
            selfParts.add(itemRev);
        }
        return selfParts;
    }

    public TCComponentItemRevision getPCAPart(List<TCComponentItemRevision> selfParts) throws Exception
    {
        TCComponentItemRevision pcaPart = null;
        for (int i = 0; i < selfParts.size(); i++)
        {
            TCComponentItemRevision itemRev = selfParts.get(i);
            String desc = itemRev.getProperty(Constants.ENGLISH_DESC).toUpperCase();
            String materialGroup = itemRev.getProperty(Constants.MATERIAL_GROUP).toUpperCase();
            if (desc.contains(Constants.MI))
            {
                actionMsgMap.put(Constants.MI1, itemRev);
            }
            else if (desc.contains(Constants.SMT1))
            {
                actionMsgMap.put(Constants.SMT, itemRev);
            }
            else if (desc.contains(Constants.SMT_T1))
            {
                actionMsgMap.put(Constants.SMT_T, itemRev);
            }
            else if (desc.contains(Constants.SMT_B1))
            {
                actionMsgMap.put(Constants.SMT_B, itemRev);
            }
            else if (desc.contains(Constants.AI))
            {
                actionMsgMap.put(Constants.AI1, itemRev);
            }
            else if (desc.contains(Constants.AI_A))
            {
                actionMsgMap.put(Constants.AI_A1, itemRev);
            }
            else if (desc.contains(Constants.AI_R))
            {
                actionMsgMap.put(Constants.AI_R1, itemRev);
            }
            else if (desc.contains(Constants.MVA))
            {
                actionMsgMap.put(Constants.MVA1, itemRev);
            }
            else if (materialGroup.equals("B8X80"))
            { // PCBA
                pcaPart = itemRev;
                actionMsgMap.put(Constants.PCA, itemRev);
                // break;
            }
        }
        if (pcaPart == null)
        {
            throw new Exception("自編料號工作區中未找到PCA料號!");
        }
        return pcaPart;
    }

    public void archivePCA(TCComponentItemRevision newPcaRev, String partFolder, String phaseFolder) throws TCException
    {
        String[] partFolders = partFolder.split(FOLDER_NAME_SEPARATOR);
        String modelFolderName = partFolders[0];
        String partTypeName = partFolders[1];
        boolean isPSU = ArrayUtil.contains(PSU_CLS_ARRAY, partTypeName);
        TCComponentFolder buFolder = null;
        if (isPSU)
        {
            buFolder = getFolder(projectFolder, "PSU", phaseFolder, "PCBA EBOM(PI)");
        }
        else
        {
            buFolder = getFolder(projectFolder, "EE", phaseFolder, "PCBA EBOM(IF+Keypad)");
        }
        if (buFolder != null)
        {
            boolean falg = false;
            TCComponentFolder folder = getFolder(buFolder, modelFolderName);
            if (folder == null)
            {
                folder = TCUtil.createFolder(RACUIUtil.getTCSession(), "D9_ArchiveFolder", modelFolderName);
                buFolder.add("contents", folder);
            }
            else
            {
                AIFComponentContext[] childs = folder.getChildren();
                if (childs != null)
                {
                    for (AIFComponentContext child : childs)
                    {
                        if (child.getComponent().equals(newPcaRev))
                        {
                            falg = true;
                            break;
                        }
                    }
                }
            }
            buFolder.refresh();
            if (!falg)
            {
                folder.add("contents", newPcaRev);
                folder.refresh();
            }
        }
    }

    public static void main(String[] args)
    {
    }
}
