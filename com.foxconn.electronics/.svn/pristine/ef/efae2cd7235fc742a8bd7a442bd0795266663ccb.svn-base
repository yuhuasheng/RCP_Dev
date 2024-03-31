package com.foxconn.electronics.convertebom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;

import com.foxconn.electronics.convertebom.pojo.BOMPojo;
import com.foxconn.electronics.domain.Constants;
import com.foxconn.electronics.managementebom.secondsource.constants.AlternativeConstant;
import com.foxconn.electronics.util.PartBOMUtils;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.soa.client.model.Type;
import com.teamcenter.soaictstubs.booleanSeq_tHolder;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

public class MonitorBOM extends TCBOM
{
    TCSession                             session             = (TCSession) AIFUtility.getCurrentApplication().getSession();
    private List<TCComponentItemRevision> selfPartList;                                                                     // 自編料號
    private boolean                       isExistAISelfPart   = false;
    private boolean                       isExistAIASelfPart  = false;
    private boolean                       isExistSMTBSelfPart = false;
    private TCComponentBOMLine            currentTopBOMLine;                                                                // 当前原理图
    private List<BOMLinePojo>             currentBomPojo;                                                                   // 当前原理图BOM行
    private List<BOMLinePojo>             newBOMs;
    private List<BOMLinePojo>             bomPojos2;                                                                        // 當前原理圖過濾NI后的數據
    private TCComponentBOMLine            previousTopBOMLine;                                                               // 上一版本原理图
    private List<BOMLinePojo>             oldBOMs;
    private ComparisonResult              comparisonResult    = new ComparisonResult();                                     // 比对结果
    private List<BOMLinePojo>             changeDataParent;
    private List<BOMLinePojo>             ebomPrepareAdd      = new ArrayList<BOMLinePojo>();
    private List<BOMLinePojo>             currentBomPojoNew;
    // private List<BOMLinePojo> processedAdd;

    public MonitorBOM(TCComponentBOMLine currentTopBOMLine, List<TCComponentItemRevision> selfPartList, Map<String, TCComponentItemRevision> actionMsgMap, TCComponentFolder placeModelFolder) throws Exception
    {
        this.currentTopBOMLine = currentTopBOMLine;
        // Map<String, String> placeMapping = getPlaceTxtMapping(placeModelFolder); 屏蔽自動補充side 功能
        Map<String, String> placeMapping = new HashMap<String, String>();
        BOMLineConvertePojo bomLineConvertePojo = new BOMLineConvertePojo(currentTopBOMLine, actionMsgMap, placeMapping);
        currentBomPojo = bomLineConvertePojo.getBOMStructurePojo();
        currentBomPojoNew = BeanUtil.copyToList(currentBomPojo, BOMLinePojo.class);
        this.newBOMs = bomLineConvertePojo.getBOMStructurePojo();
        this.selfPartList = selfPartList;
    }

    public static boolean isPassType(String partType)
    {
        // System.out.println("partType :: " + partType);
        return Constants.EDA_COM_PART_REV.equals(partType) || Constants.COMMON_PART_PART_REV.equals(partType);
    }

    @Override
    public void validation() throws Exception
    {
        ConvertEBOMDialog2.writeLogText("當前原理圖共有 " + currentBomPojo.size() + " 條數據;\n");
        checkBoardFileStatus();
        List<BOMLinePojo> bomPojos0 = currentBomPojo.stream()
                                                    .filter(i -> isPassType(i.getItemType()) && "".equals(i.getBom()))
                                                    .collect(Collectors.toList());
        if (bomPojos0 != null && bomPojos0.size() > 0)
        {
            throw new Exception("檢查到 " + bomPojos0.size() + " 條【bl_occ_d9_BOM】屬性等於空值的數據，請修正后進行此操作！");
        }
        List<BOMLinePojo> bomPojos1 = currentBomPojo.stream()
                                                    .filter(i -> isPassType(i.getItemType()) && "NI".equals(i.getBom()))
                                                    .collect(Collectors.toList());// 獲取NI件
        bomPojos2 = currentBomPojo.stream().filter(i -> isPassType(i.getItemType()) && !"NI".equals(i.getBom())).collect(Collectors.toList());// 過濾NI件
        ConvertEBOMDialog2.writeLogText("過濾掉 " + bomPojos1.size() + " 條【bl_occ_d9_BOM】屬性等於【NI】的數據;\n");
        List<BOMLinePojo> bomPojos3 = bomPojos2.stream()
                                               .filter(i -> isPassType(i.getItemType()) && "N".equals(i.getBom()))
                                               .collect(Collectors.toList());// 獲取N件
        if (bomPojos3 != null && bomPojos3.size() > 0)
        {
            throw new Exception("檢查到 " + bomPojos3.size() + " 條【bl_occ_d9_BOM】屬性等於【N】的數據，請修正后進行此操作！");
        }
        //
        List<BOMLinePojo> bomPojos4 = bomPojos2.stream()
                                               .filter(i -> !Constants.DIP.equals(i.getPackageType()) && "".equals(i.getSide()))
                                               .collect(Collectors.toList());
        if (bomPojos4 != null && bomPojos4.size() > 0)
        {
            String errorLocation = bomPojos4.stream().map(BOMLinePojo::getLocation).collect(Collectors.joining());
            throw new Exception("檢查到 " + errorLocation + " 位置 的【PackageType】屬性不等於【DIP】，且【Side】等於空值的數據，并且没有从 placement文件 中获取到side , 請修正后進行此操作！");
        }
        List<BOMLinePojo> bomPojos5 = bomPojos2.stream()
                                               .filter(i -> Constants.DIP.equals(i.getPackageType()) && ("MI".equals(i.getInsertionType()) || "".equals(i.getInsertionType())))
                                               .collect(Collectors.toList());
        if (bomPojos5.size() > 0)
        {
            ConvertEBOMDialog2.writeLogText("檢查 【MI】虛擬件是否存在,\n");
            List<TCComponentItemRevision> selfPartMI = getSelfPartByDesc(Constants.MI);
            if (selfPartMI.size() == 0)
            {
                throw new Exception("自編料號中未查詢到【MI】虛擬件，請修正后進行此操作！");
            }
        }
        List<BOMLinePojo> bomPojos6 = bomPojos2.stream()
                                               .filter(i -> Constants.DIP.equals(i.getPackageType()) && Constants.AI1.equals(i.getInsertionType()))
                                               .collect(Collectors.toList());
        if (bomPojos6.size() > 0)
        {
            ConvertEBOMDialog2.writeLogText("檢查 【AI】虛擬件是否存在,\n");
            List<TCComponentItemRevision> selfPartAI = getSelfPartByDesc(Constants.AI);
            if (selfPartAI.size() == 0)
            {
                throw new Exception("自編料號中未查詢到【AI】虛擬件，請修正后進行此操作！");
            }
        }
        List<BOMLinePojo> bomPojos7 = bomPojos2.stream()
                                               .filter(i -> Constants.DIP.equals(i.getPackageType()) && Constants.AI_A1.equals(i.getInsertionType()))
                                               .collect(Collectors.toList());
        if (bomPojos7.size() > 0)
        {
            ConvertEBOMDialog2.writeLogText("檢查 【AI/A】虛擬件是否存在,\n");
            List<TCComponentItemRevision> selfPartAIA = getSelfPartByDesc(Constants.AI_A);
            if (selfPartAIA.size() == 0)
            {
                throw new Exception("自編料號中未查詢到【AI/A】虛擬件，請修正后進行此操作！");
            }
        }
        List<BOMLinePojo> bomPojos8 = bomPojos2.stream()
                                               .filter(i -> Constants.DIP.equals(i.getPackageType()) && Constants.AI_R1.equals(i.getInsertionType()))
                                               .collect(Collectors.toList());
        if (bomPojos8.size() > 0)
        {
            ConvertEBOMDialog2.writeLogText("檢查 【AI/R】虛擬件是否存在,\n");
            List<TCComponentItemRevision> selfPartAIR = getSelfPartByDesc(Constants.AI_R);
            if (selfPartAIR.size() == 0)
            {
                throw new Exception("自編料號中未查詢到【AI/R】虛擬件，請修正后進行此操作！");
            }
        }
        List<TCComponentItemRevision> selfPartBySMTT = getSelfPartByDesc(Constants.SMT_T1);
        if (selfPartBySMTT.size() == 0)
        {
            List<TCComponentItemRevision> selfPartBySMT = getSelfPartByDesc(Constants.SMT1);
            if (selfPartBySMT.size() == 0)
            {
                throw new Exception("【SMT/T】虛擬件不存在，且【SMT】虛擬件也不存在，請修正后進行此操作！");
            }
            // else
            // {
            // List<BOMLinePojo> top = bomPojos2.stream().filter(i -> Constants.TOP.equals(i.getSide())).collect(Collectors.toList());
            // List<BOMLinePojo> bottom = bomPojos2.stream().filter(i -> Constants.BOTTOM.equals(i.getSide())).collect(Collectors.toList());
            // if (top.size() > 0 && bottom.size() > 0)
            // {
            // throw new Exception("【SMT】虛擬件存在，【TOP】【BOTTOM】衹能存在一種，請修正后進行此操作！");
            // }
            // }
        }
        else
        {
            List<BOMLinePojo> bomPojos9 = bomPojos2.stream()
                                                   .filter(i -> Constants.SMD.equals(i.getPackageType()) && Constants.TOP.equals(i.getSide()))
                                                   .collect(Collectors.toList());
            if (bomPojos9.size() == 0)
            {
                throw new Exception("【SMD】【TOP】数据不存在，請修正后進行此操作！");
            }
        }
        List<BOMLinePojo> bomPojos10 = bomPojos2.stream()
                                                .filter(i -> Constants.SMD.equals(i.getPackageType()) && Constants.BOTTOM.equals(i.getSide()))
                                                .collect(Collectors.toList());
        if (bomPojos10.size() > 0)
        {
            ConvertEBOMDialog2.writeLogText("檢查 【SMT/B】虛擬件是否存在,\n");
            List<TCComponentItemRevision> selfPartSMTB = getSelfPartByDesc(Constants.SMT_B1);
            if (selfPartSMTB.size() == 0)
            {
                List<TCComponentItemRevision> selfPartBySMT = getSelfPartByDesc(Constants.SMT1);
                if (selfPartBySMT.size() == 0)
                {
                    throw new Exception("【SMT/B】虛擬件不存在，且【SMT】虛擬件也不存在，請修正后進行此操作！");
                }
            }
        }
        List<TCComponentItemRevision> selfPartMVA = getSelfPartByDesc(Constants.MVA);
        if (selfPartMVA.size() == 0)
        {
            throw new Exception("【自編料號工作區】中未檢查到【MVA】料號，請修正后進行此操作！");
        }
        List<BOMLinePojo> bomPojos11 = currentBomPojo.stream()
                                                     .filter(i -> "D9_PCB_PartRevision".equals(i.getItemType()))
                                                     .collect(Collectors.toList());
        if (bomPojos11.size() > 0)
        {
            ConvertEBOMDialog2.writeLogText("檢查到 " + bomPojos11.size() + " 條【PCB】數據,\n");
            for (int j = 0; j < bomPojos11.size(); j++)
            {
                BOMLinePojo bomLinePojo = bomPojos11.get(j);
                TCComponentItemRevision itemRev = bomLinePojo.getItemRev();
                String is2ndSource = itemRev.getProperty("d9_Is2ndSource");
                if ("是".equals(is2ndSource))
                {
                    throw new Exception("【PCB】料號不符合轉換要求，請在原理圖中使用PCB主料，不要使用替代料。請修正后進行此操作！");
                }
            }
        }
        else
        {
            throw new Exception("原理圖中未檢查到【PCB】類型料號，請修正后進行此操作！");
        }
        List<TCComponentItemRevision> selfPartAI = getSelfPartByDesc(Constants.AI);
        if (selfPartAI.size() > 0)
        {
            isExistAISelfPart = true;
        }
        List<TCComponentItemRevision> selfPartAIA = getSelfPartByDesc(Constants.AI_A);
        if (selfPartAIA.size() > 0)
        {
            isExistAIASelfPart = true;
        }
        List<TCComponentItemRevision> selfPartSMTB = getSelfPartByDesc(Constants.SMT_B1);
        if (selfPartSMTB.size() > 0)
        {
            isExistSMTBSelfPart = true;
        }
    }

    private void checkBoardFileStatus() throws Exception
    {
        // TODO Auto-generated method stub
        boolean flag = false;
        TCComponentItemRevision itemRevision = currentTopBOMLine.getItemRevision();
        AIFComponentContext[] whereReferenced = itemRevision.whereReferenced();
        for (int i = 0; i < whereReferenced.length; i++)
        {
            InterfaceAIFComponent component = whereReferenced[i].getComponent();
            if (!(component instanceof TCComponentItemRevision))
            {
                continue;
            }
            String boardfileId = component.getProperty(Constants.ITEM_ID);
            if (boardfileId.equals(itemRevision.getProperty(Constants.ITEM_ID)))
            {
                continue;
            }
            TCComponentItemRevision revision = (TCComponentItemRevision) component;
            TCComponent[] component2 = revision.getRelatedComponents("IMAN_specification");
            Boolean boardFileExsit = false;
            for (TCComponent tcComponent : component2)
            {
                if (!(tcComponent instanceof TCComponentDataset))
                {
                    continue;
                }
                String relatedName = tcComponent.getProperty("object_string");
                if (!relatedName.endsWith("boardfile"))
                {
                    continue;
                }
                boardFileExsit = true;
            }
            if (!boardFileExsit)
            {
                // throw new Exception("未检测到Board file, 请先联系Layout创建。");
            }
            if (!TCUtil.isReleased(revision))
            {
                throw new Exception(String.format("%s未Release，请先Release。", boardfileId));
            }
            flag = true;
            break;
        }
        if (!flag)
        {
            // throw new Exception("未检测到Board file, 请先联系Layout创建。");
        }
    }

    @Override
    public void comparison(Map<String, TCComponentItemRevision> actionMsgMap) throws Exception
    {
        TCComponentBOMWindow newBOMWindow = TCUtil.createBOMWindow(session);
        TCComponentItemRevision pcaPartRev = getPCAPart();
        TCComponentBOMLine topBomline = TCUtil.getTopBomline(newBOMWindow, pcaPartRev);
        AIFComponentContext[] children = topBomline.getChildren();
        if (children.length == 0)
        {
            ConvertEBOMDialog2.writeLogText("當前原理圖只有一個版本，無需比對！\n");
        }
        else
        {
            TCComponentItemRevision itemRev = currentTopBOMLine.getItemRevision();
            // String itemRevId = itemRev.getProperty(Constants.ITEM_REV_ID);
            // TCComponentItemRevision previousRev = TCUtil.getPreviousRevision(itemRev);
            Map<TCComponentItemRevision, TCComponentItemRevision> map = getPreviousRevision(itemRev);
            if (map == null)
            {
                ConvertEBOMDialog2.writeLogText("比對無效，最近歷史Design BOM版本無轉換過EBOM！\n");
                throw new Exception("最近歷史Design BOM版本未轉換過EBOM，終止導入！");
            }
            Set<TCComponentItemRevision> set = map.keySet();
            TCComponentItemRevision previousRev = null;
            TCComponentItemRevision pcaPreRev = null;
            for (TCComponentItemRevision tcComponentItemRevision : set)
            {
                previousRev = tcComponentItemRevision;
                pcaPreRev = map.get(tcComponentItemRevision);
                break;
            }
            if (previousRev == null)
            {
                ConvertEBOMDialog2.writeLogText("比對無效，最近歷史Design BOM版本無轉換過EBOM！\n");
                throw new Exception("最近歷史Design BOM版本未轉換過EBOM，終止導入！");
            }
            TCComponentBOMWindow pcaBOMWindow = TCUtil.createBOMWindow(session);
            TCComponentBOMLine pcaPreBomLine = TCUtil.getTopBomline(pcaBOMWindow, pcaPreRev);
            BOMPojo pcaPreBomPojo = BOMBuildService.getPCAPreBOM(pcaPreBomLine);
            if (pcaBOMWindow != null && !pcaBOMWindow.isWindowClosed())
            {
                pcaBOMWindow.close();
            }
            TCComponentBOMWindow bomWindow = PartBOMUtils.createBomWindowBySnapshot(previousRev);
            if (bomWindow == null)
            {
                bomWindow = TCUtil.createBOMWindow(session);
            }
            previousTopBOMLine = TCUtil.getTopBomline(bomWindow, previousRev);
            BOMLineConvertePojo bomLineConvertePojo = new BOMLineConvertePojo(previousTopBOMLine, actionMsgMap, null);
            oldBOMs = bomLineConvertePojo.getBOMStructurePojo();
            bomWindow.save();
            if (newBOMWindow != null && !newBOMWindow.isWindowClosed())
            {
                newBOMWindow.close();
            }
            comparisonData(pcaPreBomPojo);
        }
        if (newBOMWindow != null && !newBOMWindow.isWindowClosed())
        {
            newBOMWindow.close();
        }
    }

    // key: 原理图
    // Value: PCA PN
    private Map<TCComponentItemRevision, TCComponentItemRevision> getPreviousRevision(TCComponentItemRevision itemRev) throws TCException
    {
        String latestVersion = itemRev.getProperty("item_revision_id");
        TCComponent[] revions = itemRev.getItem().getRelatedComponents("revision_list");
        if (revions == null || revions.length == 0)
        {
            return null;
        }
        int index = 0;
        for (int i = revions.length - 1; i >= 0; i--)
        {
            TCComponentItemRevision itemRevision = (TCComponentItemRevision) revions[i];
            String version = itemRevision.getProperty("item_revision_id");
            if (latestVersion.equals(version))
            {
                index = i;
                break;
            }
        }
        for (int i = index - 1; i >= 0; i--)
        {
            TCComponentItemRevision itemRevision = (TCComponentItemRevision) revions[i];
            String version = itemRevision.getProperty("item_revision_id");
            if (latestVersion.equals(version))
            {
                continue;
            }
            TCComponent[] components = itemRevision.getRelatedComponents("IMAN_specification");
            for (TCComponent component : components)
            {
                if (!(component instanceof TCComponentItemRevision))
                {
                    continue;
                }
                String objType = component.getProperty("object_type");
                if ("PCA Part Revision".equals(objType))
                {
                    Map<TCComponentItemRevision, TCComponentItemRevision> map = new HashMap<TCComponentItemRevision, TCComponentItemRevision>();
                    map.put(itemRevision, (TCComponentItemRevision) component);
                    ConvertEBOMDialog2.writeLogText(String.format("比對原理圖【%s】版本為【%s】\n", itemRevision.getProperty("item_id"), version));
                    return map;
                }
            }
        }
        return null;
    }

    @Override
    public TCComponentItemRevision buildEBOM() throws Exception
    {
        TCUtil.setBypass(session);
        ConvertEBOMDialog2.writeLogText("正在構建EBOM，請稍等...\n");
        TCComponentItemRevision pcaPartRev = getPCAPart();
        TCComponentBOMWindow newBOMWindow = TCUtil.createBOMWindow(session);
        TCComponentBOMLine topBomline = TCUtil.getTopBomline(newBOMWindow, pcaPartRev);
        AIFComponentContext[] children2 = topBomline.getChildren();
        TCComponentItemRevision newPcaRev = null;
        try
        {
            if (children2.length == 0)
            {
                boolean isReleased = TCUtil.isReleased(pcaPartRev);
                if (!isReleased)
                {
                    throw new Exception("請先Release PCA Part");
                }
                String versionRule = getVersionRule(pcaPartRev); // 返回版本规则
                String newRevId = com.foxconn.mechanism.util.TCUtil.reviseVersion(session, versionRule, pcaPartRev.getTypeObject()
                                                                                                                  .getName(), pcaPartRev.getUid()); // 返回指定版本规则的版本号
                newPcaRev = pcaPartRev.saveAs(newRevId);
                topBomline = TCUtil.getTopBomline(newBOMWindow, newPcaRev);
                EBOMStructurePojo ebomStructurePojo = firstBuildEBOMPojo(newPcaRev);// 首次構建
                BOMLinePojo parent = ebomStructurePojo.getParent();
                List<EBOMStructurePojo> children = ebomStructurePojo.getChildren();
                if (children != null && children.size() > 0)
                {
                    parent.setItemRev(newPcaRev);
                }
                parentIsrevise(ebomStructurePojo);
                startBuild(topBomline, ebomStructurePojo);
            }
            else
            {
                List<BOMLinePojo> deleteBOMLine = comparisonResult.getDeleteBOMLine();
                List<BOMLinePojo> addBOMLine = comparisonResult.getAddBOMLine();
                if ((deleteBOMLine == null || deleteBOMLine.size() == 0) && (addBOMLine == null || addBOMLine.size() == 0) && ebomPrepareAdd.size() == 0)
                {
                    throw new Exception("当前版本原理图对比上一版本原理图无数据变化！請修正后進行此操作！");
                }
                List<BOMLinePojo> changeData = new ArrayList<BOMLinePojo>();
                changeDataParent = new ArrayList<BOMLinePojo>();
                changeData.addAll(deleteBOMLine);
                changeData.addAll(addBOMLine);
                changeData.addAll(ebomPrepareAdd);
                for (int i = 0; i < changeData.size(); i++)
                {
                    BOMLinePojo bomLinePojo = changeData.get(i);
                    // String itemId = bomLinePojo.getItemId();
                    // String stepfatherId = bomLinePojo.getStepfatherNum();
                    // getChangeDataParent(topBomline,stepfatherId);//获取变更数据的父阶
                    BOMLinePojo stepParent = new BOMLinePojo();
                    stepParent.setItemId(bomLinePojo.getStepfatherNum());
                    stepParent.setItemRev(bomLinePojo.getStepfather());
                    changeDataParent.add(stepParent);
                }
                changeDataParent = changeDataParent.stream()
                                                   .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(BOMLinePojo::getItemId))), ArrayList::new));
                for (int i = 0; i < changeDataParent.size(); i++)
                {
                    BOMLinePojo bomLinePojo = changeDataParent.get(i);
                    TCComponentItemRevision itemRev2 = bomLinePojo.getItemRev();
                    boolean partIsReleased = TCUtil.isReleased(itemRev2);
                    if (!partIsReleased)
                    {
                        throw new Exception("料号【" + itemRev2.toDisplayString() + "】未发布！請修正后進行此操作！");
                    }
                }
                for (int i = 0; i < changeDataParent.size(); i++)
                {
                    BOMLinePojo bomLinePojo = changeDataParent.get(i);
                    TCComponentItemRevision itemRev2 = bomLinePojo.getItemRev();
                    String versionRule = getVersionRule(itemRev2); // 返回版本规则
                    String newRevId = com.foxconn.mechanism.util.TCUtil.reviseVersion(session, versionRule, itemRev2.getTypeObject()
                                                                                                                    .getName(), itemRev2.getUid()); // 返回指定版本规则的版本号
                    itemRev2.saveAs(newRevId);
                }
                TCComponentItemRevision latestItemRevision = pcaPartRev.getItem().getLatestItemRevision();
                TCComponentBOMWindow newBOMWindow2 = TCUtil.createBOMWindow(session);
                topBomline = TCUtil.getTopBomline(newBOMWindow2, latestItemRevision);
                try
                {
                    if (topBomline == null)
                    {
                        throw new Exception(String.format("Can't not get latest Item Revision【%s】.\n", pcaPartRev.getItem().toString()));
                    }
                    for (int i = 0; i < deleteBOMLine.size(); i++)
                    {
                        BOMLinePojo bomLinePojo = deleteBOMLine.get(i);
                        startDelete(topBomline, bomLinePojo);
                    }
                    startAddEBOM(topBomline, ebomPrepareAdd);
                    List<BOMLinePojo> alreadyAdded = new ArrayList<BOMLinePojo>();// 已处理
                    for (int i = 0; i < addBOMLine.size(); i++)
                    {
                        BOMLinePojo bomLinePojo = addBOMLine.get(i);
                        startAdd(topBomline, bomLinePojo, alreadyAdded);
                    }
                    newBOMWindow2.save();
                }
                finally
                {
                    if (newBOMWindow2 != null && !newBOMWindow2.isWindowClosed())
                    {
                        newBOMWindow2.close();
                    }
                }
            }
            newBOMWindow.save();
            boolean hasBypass = session.hasBypass();
            System.out.println("hasBypass:" + hasBypass);
            TCComponentItemRevision itemRevision = currentTopBOMLine.getItemRevision();
            if (newPcaRev == null)
            {
                newPcaRev = pcaPartRev.getItem().getLatestItemRevision();
            }
            try
            {
                itemRevision.add("IMAN_specification", newPcaRev);
            }
            catch (TCException tce)
            {
                tce.printStackTrace();
            }
            ConvertEBOMDialog2.writeLogText("EBOM構建完成！\n");
        }
        catch (Exception e)
        {
            ConvertEBOMDialog2.writeLogText(e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            if (newBOMWindow != null && !newBOMWindow.isWindowClosed())
            {
                newBOMWindow.close();
            }
            TCUtil.closeBypass(session);
        }
        return newPcaRev;
    }

    private void getChangeDataParent(TCComponentBOMLine topBomLine, String itemId) throws Exception
    {
        AIFComponentContext[] children = topBomLine.getChildren();
        for (int i = 0; i < children.length; i++)
        {
            TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
            String blItemId = bomLine.getProperty(Constants.BL_ITEM_ID);
            if (blItemId.equals(itemId))
            {
                TCComponentItemRevision itemRev = bomLine.getItemRevision();
                // String itemId2 = topBomLine.getProperty(Constants.BL_ITEM_ID);
                BOMLinePojo bomLinePojo = new BOMLinePojo();
                bomLinePojo.setItemId(blItemId);
                bomLinePojo.setItemRev(itemRev);
                changeDataParent.add(bomLinePojo);
                break;
            }
            getChangeDataParent(bomLine, itemId);
        }
    }

    private void startBuild(TCComponentBOMLine topBomline, EBOMStructurePojo ebomStructurePojo) throws Exception
    {
        List<EBOMStructurePojo> children = ebomStructurePojo.getChildren();
        if (children != null && children.size() > 0)
        {
            for (int i = 0; i < children.size(); i++)
            {
                EBOMStructurePojo pojo = children.get(i);
                BOMLinePojo parent = pojo.getParent();
                List<TCComponentItemRevision> substitute = pojo.getSubstitute();
                TCComponentItemRevision itemRev = parent.getItemRev();
                TCComponentBOMLine newBomLine = topBomline.add(itemRev.getItem(), itemRev, null, false);
                if (substitute != null && substitute.size() > 0)
                {
                    for (int j = 0; j < substitute.size(); j++)
                    {
                        TCComponentItemRevision substituteItemRev = substitute.get(j);
                        newBomLine.add(substituteItemRev.getItem(), substituteItemRev, null, true);
                    }
                }
                // 此处添加获取place 中 side 信息
                String qty = changeQty(newBomLine, parent.getQuantity());
                newBomLine.setProperty(Constants.BL_QUANTITY, qty);
                newBomLine.setProperty(Constants.BL_PACKAGE_TYPE, parent.getPackageType());
                newBomLine.setProperty(Constants.INSERTION_TYPE, parent.getInsertionType());
                newBomLine.setProperty(Constants.BL_SIDE, parent.getSide());
                newBomLine.setProperty(Constants.BL_BOM, parent.getBom());
                newBomLine.setProperty(Constants.BL_BOM, parent.getBom());
                newBomLine.setProperty(Constants.BL_LOCATION, parent.getLocation());
                newBomLine.setProperty(Constants.BL_DESCRIPTION, parent.getDesc());
                startBuild(newBomLine, pojo);
            }
        }
    }

    String changeQty(TCComponentBOMLine newBomLine, String qty) throws TCException
    {
        if (qty == null || qty.length() == 0)
        {
            qty = "1";
        }
        TCComponentItemRevision itemRev = newBomLine.getItemRevision();
        String unit = itemRev.getProperty("d9_Un");
        if ("KEA".equalsIgnoreCase(unit))
        {
            newBomLine.setProperty("bl_uom", "Other");
            DecimalFormat dt = new DecimalFormat("0.000");
            double d1 = Double.parseDouble(qty);
            qty = dt.format(d1 / 1000d);
        }
        return qty;
    }

    private void parentIsrevise(EBOMStructurePojo ebomStructurePojo) throws Exception
    {
        List<EBOMStructurePojo> children = ebomStructurePojo.getChildren();
        if (children == null || children.size() == 0)
        {
            return;
        }
        for (int i = 0; i < children.size(); i++)
        {
            EBOMStructurePojo pojo = children.get(i);
            BOMLinePojo parent = pojo.getParent();
            List<EBOMStructurePojo> children2 = pojo.getChildren();
            if (children2 == null || children2.size() == 0)
            {
                continue;
            }
            parentIsrevise(pojo);
            TCComponentItemRevision itemRev = parent.getItemRev();
            boolean partIsReleased = TCUtil.isReleased(itemRev);
            if (!partIsReleased)
            {
                continue;
            }
            if (TCUtil.hasChildren(session, itemRev))
            {
                String versionRule = getVersionRule(itemRev); // 返回版本规则
                String newRevId = com.foxconn.mechanism.util.TCUtil.reviseVersion(session, versionRule, itemRev.getTypeObject()
                                                                                                               .getName(), itemRev.getUid()); // 返回指定版本规则的版本号
                TCComponentItemRevision newItemRev = itemRev.saveAs(newRevId);
                parent.setItemRev(newItemRev);
            }
        }
    }

    private void startDelete(TCComponentBOMLine topBomline, BOMLinePojo bomLinePojo) throws Exception
    {
        topBomline.refresh();
        AIFComponentContext[] children = topBomline.getChildren();
        String parentId = topBomline.getProperty(Constants.BL_ITEM_ID);
        if (children.length == 0)
        {
            return;
        }
        if (parentId.equals(bomLinePojo.getStepfatherNum()))
        {
            Map<String, TCComponentBOMLine> locationMap = new HashMap<String, TCComponentBOMLine>();
            for (int i = 0; i < children.length; i++)
            {
                TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
                if (bomLine.isSubstitute())
                {
                    continue;
                }
                String locations = bomLine.getProperty(Constants.BL_LOCATION);
                if (StrUtil.isNotEmpty(locations))
                {
                    String[] locationArray = locations.split(",");
                    for (String locationStr : locationArray)
                    {
                        locationMap.put(locationStr, bomLine);
                    }
                }
            }
            String location = bomLinePojo.getLocation();
            Set<String> locationSet = new HashSet<String>(Arrays.asList(location.split(",")));
            Set<String> locationKeySet = locationMap.keySet();
            locationSet.retainAll(locationKeySet);
            for (String keyLocation : locationSet)
            {
                TCComponentBOMLine bomLine = locationMap.get(keyLocation);
                String locations = bomLine.getProperty(Constants.BL_LOCATION);
                Set<String> locationsList = new HashSet<String>(Arrays.asList(locations.split(",")));
                locationsList.remove(keyLocation);
                String unit = bomLine.getItemRevision().getProperty("d9_Un");
                if (locationsList.size() == 0)
                {
                    bomLine.cut();
                    locationMap.remove(keyLocation);
                    continue;
                }
                String qtyStr = locationsList.size() + "";
                if ("KEA".equalsIgnoreCase(unit))
                {
                    qtyStr = new BigDecimal(qtyStr).divide(new BigDecimal("1000")).toPlainString();
                }
                bomLine.setProperty(Constants.BL_LOCATION, String.join(",", locationsList));
                bomLine.setProperty(Constants.BL_QUANTITY, qtyStr);
            }
        }
        else
        {
            for (int i = 0; i < children.length; i++)
            {
                TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
                if (!bomLine.isSubstitute())
                {
                    startDelete(bomLine, bomLinePojo);
                }
            }
        }
    }

    void startAddEBOM(TCComponentBOMLine topBomline, List<BOMLinePojo> alreadyAdded) throws Exception
    {
        AIFComponentContext[] children = topBomline.getChildren();
        String parentId = topBomline.getProperty(Constants.BL_ITEM_ID);
        List<BOMLinePojo> singeBOMs = alreadyAdded.stream().filter(e -> parentId.equalsIgnoreCase(e.getStepfatherNum())).collect(Collectors.toList());
        alreadyAdded.removeAll(singeBOMs);
        Map<String, BOMLinePojo> bomMap = singeBOMs.stream().collect(Collectors.toMap(BOMLinePojo::getItemId, e -> e, (v1, v2) -> v1));
        if (children.length > 0)
        {
            for (int i = 0; i < children.length; i++)
            {
                TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
                String bomLineItemid = bomLine.getProperty(Constants.BL_ITEM_ID);
                BOMLinePojo bomPojo = bomMap.get(bomLineItemid);
                if (bomPojo != null)
                {
                    bomMap.remove(bomLineItemid);
                    BigDecimal quantity2 = new BigDecimal(bomLine.getProperty(Constants.BL_QUANTITY));
                    String locations = bomLine.getProperty(Constants.BL_LOCATION);
                    String[] addLocations = bomPojo.getLocation().split(",");
                    if (addLocations.length > 0)
                    {
                        int len = addLocations.length;
                        String subLen = "" + len;
                        locations += "," + String.join(",", addLocations);
                        TCComponentItemRevision itemRev = bomLine.getItemRevision();
                        String unit = itemRev.getProperty("d9_Un");
                        if ("KEA".equalsIgnoreCase(unit))
                        {
                            DecimalFormat dt = new DecimalFormat("0.000");
                            subLen = dt.format(len / 1000d);
                        }
                        quantity2 = quantity2.add(new BigDecimal(subLen));
                        bomLine.setProperty(Constants.BL_LOCATION, locations);
                        bomLine.setProperty(Constants.BL_QUANTITY, quantity2.toPlainString());
                    }
                }
                if (bomLine.hasChildren() && alreadyAdded.size() > 0)
                {
                    startAddEBOM(bomLine, alreadyAdded);
                }
            }
            if (bomMap.size() > 0)
            {
                bomMap.values().forEach(bomPojo -> {
                    try
                    {
                        String[] addLocations = bomPojo.getLocation().split(",");
                        int len = addLocations.length;
                        bomPojo.setQuantity(len + "");
                        addBOMRowSingle(topBomline, bomPojo);
                    }
                    catch (TCException e1)
                    {
                        e1.printStackTrace();
                    }
                });
            }
        }
    }

    private void startAdd(TCComponentBOMLine topBomline, BOMLinePojo bomLinePojo, List<BOMLinePojo> alreadyAdded) throws Exception
    {
        AIFComponentContext[] children = topBomline.getChildren();
        String parentId = topBomline.getProperty(Constants.BL_ITEM_ID);
        if (children.length > 0)
        {
            if (parentId.equals(bomLinePojo.getStepfatherNum()))
            {
                Map<String, TCComponentBOMLine> locationMap = new HashMap<String, TCComponentBOMLine>();
                TCComponentBOMLine sameBomLine = null;
                for (int i = 0; i < children.length; i++)
                {
                    TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
                    if (bomLine.isSubstitute())
                    {
                        continue;
                    }
                    String locations = bomLine.getProperty(Constants.BL_LOCATION);
                    if (bomLine.getProperty(Constants.BL_ITEM_ID).equalsIgnoreCase(bomLinePojo.getItemId()))
                    {
                        sameBomLine = bomLine;
                    }
                    if (StrUtil.isNotEmpty(locations))
                    {
                        String[] locationArray = locations.split(",");
                        for (String locationStr : locationArray)
                        {
                            locationMap.put(locationStr, bomLine);
                        }
                    }
                }
                String location = bomLinePojo.getLocation();
                Set<String> locationSet = new HashSet<String>(Arrays.asList(location.split(",")));
                Set<String> addLocations = new HashSet<String>(locationSet);
                Set<String> sameLocations = new HashSet<String>(locationSet);
                Set<String> locationKeySet = locationMap.keySet();
                addLocations.removeAll(locationKeySet);
                sameLocations.retainAll(locationKeySet);
                if (addLocations.size() > 0)
                {
                    if (sameBomLine != null)
                    {
                        BigDecimal quantity2 = new BigDecimal(sameBomLine.getProperty(Constants.BL_QUANTITY));
                        String locations = sameBomLine.getProperty(Constants.BL_LOCATION);
                        int len = addLocations.size();
                        String subLen = "" + len;
                        locations += "," + String.join(",", addLocations);
                        TCComponentItemRevision itemRev = sameBomLine.getItemRevision();
                        String unit = itemRev.getProperty("d9_Un");
                        if ("KEA".equalsIgnoreCase(unit))
                        {
                            DecimalFormat dt = new DecimalFormat("0.000");
                            subLen = dt.format(len / 1000d);
                        }
                        quantity2 = quantity2.add(new BigDecimal(subLen));
                        sameBomLine.setProperty(Constants.BL_LOCATION, locations);
                        sameBomLine.setProperty(Constants.BL_QUANTITY, quantity2.toPlainString());
                    }
                    else
                    {
                        bomLinePojo.setQuantity(addLocations.size() + "");
                        bomLinePojo.setLocation(String.join(",", addLocations));
                        addBOMRowSingle(topBomline, bomLinePojo);
                    }
                }
                if (sameLocations.size() > 0)
                {
                    for (String sameLocation : sameLocations)
                    {
                        TCComponentBOMLine bomLine = locationMap.get(sameLocation);
                        String itemId = bomLine.getProperty(Constants.BL_ITEM_ID);
                        if (bomLinePojo.getItemId().equals(itemId))
                        {
                            continue;
                        }
                        if (bomLine.hasSubstitutes())
                        {
                            TCComponentBOMLine[] subLines = bomLine.listSubstitutes();
                            int falg = 0;
                            for (TCComponentBOMLine subLine : subLines)
                            {
                                if (bomLinePojo.getItemId().equalsIgnoreCase(subLine.getProperty(Constants.BL_ITEM_ID)))
                                {
                                    falg++;
                                }
                            }
                            if (falg == 0)
                            {
                                TCComponentBOMLine newSubBOMLine = bomLine.add(bomLinePojo.getItemRev()
                                                                                          .getItem(), bomLinePojo.getItemRev(), null, true);
                            }
                        }
                        else
                        {
                            String locations = bomLine.getProperty(Constants.BL_LOCATION);
                            Set<String> locationsList = new HashSet<String>(Arrays.asList(locations.split(",")));
                            locationsList.remove(sameLocation);
                            if (locationsList.size() == 0)
                            {
                                bomLine.cut();
                            }
                            else
                            {
                                String unit = bomLine.getItemRevision().getProperty("d9_Un");
                                String qtyStr = locationsList.size() + "";
                                if ("KEA".equalsIgnoreCase(unit))
                                {
                                    qtyStr = new BigDecimal(qtyStr).divide(new BigDecimal("1000")).toPlainString();
                                }
                                bomLine.setProperty(Constants.BL_LOCATION, String.join(",", locationsList));
                                bomLine.setProperty(Constants.BL_QUANTITY, qtyStr);
                            }
                            if (sameBomLine != null)
                            {
                                String changeLocations = sameBomLine.getProperty(Constants.BL_LOCATION);
                                String unit = sameBomLine.getItemRevision().getProperty("d9_Un");
                                BigDecimal oldQty = new BigDecimal(sameBomLine.getProperty(Constants.BL_QUANTITY));
                                BigDecimal one = new BigDecimal("1");
                                if ("KEA".equalsIgnoreCase(unit))
                                {
                                    one = one.divide(new BigDecimal("1000"));
                                }
                                oldQty = oldQty.add(one);
                                sameBomLine.setProperty(Constants.BL_LOCATION, changeLocations + "," + sameLocation);
                                sameBomLine.setProperty(Constants.BL_QUANTITY, oldQty.toPlainString());
                            }
                            else
                            {
                                addBOMRowSingle(topBomline, bomLinePojo);
                            }
                        }
                    }
                }
            }
            else
            {
                for (int i = 0; i < children.length; i++)
                {
                    TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
                    if (!bomLine.isSubstitute())
                    {
                        startAdd(bomLine, bomLinePojo, alreadyAdded);
                    }
                }
            }
        }
    }

    private void addBOMRow(TCComponentBOMLine topBomline, BOMLinePojo bomLinePojo) throws Exception
    {
        AIFComponentContext[] children = topBomline.getChildren();
        if (children.length == 0)
        {
            return;
        }
        for (int i = 0; i < children.length; i++)
        {
            TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
            String itemId = bomLine.getProperty(Constants.BL_ITEM_ID);
            if (itemId.equals(bomLinePojo.getStepfatherNum()))
            {
                addBOMRowSingle(bomLine, bomLinePojo);
                break;
            }
            addBOMRow(bomLine, bomLinePojo);
        }
    }

    private void addBOMRowSingle(TCComponentBOMLine bomLine, BOMLinePojo bomLinePojo) throws TCException
    {
        TCComponentItemRevision itemRev2 = bomLinePojo.getItemRev();
        String bom = bomLinePojo.getBom();
        String location = bomLinePojo.getLocation();
        String insertionType = bomLinePojo.getInsertionType();
        String packageType = bomLinePojo.getPackageType();
        String quantity = bomLinePojo.getQuantity();
        String side = bomLinePojo.getSide();
        TCComponentBOMLine newBOMLine = bomLine.add(itemRev2.getItem(), itemRev2, null, false);
        quantity = changeQty(newBOMLine, quantity);
        newBOMLine.setProperty(Constants.BL_BOM, bom);
        newBOMLine.setProperty(Constants.BL_LOCATION, location);
        newBOMLine.setProperty(Constants.INSERTION_TYPE, insertionType);
        newBOMLine.setProperty(Constants.BL_PACKAGE_TYPE, packageType);
        newBOMLine.setProperty(Constants.BL_QUANTITY, quantity);
        newBOMLine.setProperty(Constants.BL_SIDE, side);
    }

    private void addBOMRow(TCComponentBOMLine topBomline, BOMLinePojo bomLinePojo, String parentDesc) throws Exception
    {
        AIFComponentContext[] children = topBomline.getChildren();
        if (children.length > 0)
        {
            for (int i = 0; i < children.length; i++)
            {
                TCComponentBOMLine bomLine = (TCComponentBOMLine) children[i].getComponent();
                TCComponentItemRevision itemRev = bomLine.getItemRevision();
                String desc = itemRev.getProperty(Constants.ENGLISH_DESC);
                if (desc.contains(parentDesc))
                {
                    TCComponentItemRevision itemRev2 = bomLinePojo.getItemRev();
                    String bom = bomLinePojo.getBom();
                    String location = bomLinePojo.getLocation();
                    String insertionType = bomLinePojo.getInsertionType();
                    String packageType = bomLinePojo.getPackageType();
                    String quantity = bomLinePojo.getQuantity();
                    String side = bomLinePojo.getSide();
                    TCComponentBOMLine newBOMLine = bomLine.add(itemRev2.getItem(), itemRev2, null, false);
                    quantity = changeQty(newBOMLine, quantity);
                    newBOMLine.setProperty(Constants.BL_BOM, bom);
                    newBOMLine.setProperty(Constants.BL_LOCATION, location);
                    newBOMLine.setProperty(Constants.INSERTION_TYPE, insertionType);
                    newBOMLine.setProperty(Constants.BL_PACKAGE_TYPE, packageType);
                    newBOMLine.setProperty(Constants.BL_QUANTITY, quantity);
                    newBOMLine.setProperty(Constants.BL_SIDE, side);
                    break;
                }
                addBOMRow(bomLine, bomLinePojo, parentDesc);
            }
        }
    }

    private List<TCComponentItemRevision> getSelfPartByDesc(String partDesc) throws Exception
    {
        List<TCComponentItemRevision> selfParts = new ArrayList<TCComponentItemRevision>();
        for (int i = 0; i < selfPartList.size(); i++)
        {
            TCComponentItemRevision itemRev = selfPartList.get(i);
            String desc = itemRev.getProperty(Constants.ENGLISH_DESC).toUpperCase();
            if (desc.contains(partDesc))
            {
                selfParts.add(itemRev);
            }
        }
        return selfParts;
    }

    private List<TCComponentItemRevision> getSelfPartByType(String typeName) throws Exception
    {
        List<TCComponentItemRevision> selfParts = new ArrayList<TCComponentItemRevision>();
        for (int i = 0; i < selfPartList.size(); i++)
        {
            TCComponentItemRevision itemRev = selfPartList.get(i);
            String type = itemRev.getType();
            if (type.equals(typeName))
            {
                String is2ndSource = itemRev.getProperty("d9_Is2ndSource");
                if ("是".equals(is2ndSource))
                {
                    selfParts.add(itemRev);
                }
            }
        }
        return selfParts;
    }

    private void comparisonData(BOMPojo pcaPreBomPojo) throws TCException
    {
        ConvertEBOMDialog2.writeLogText("開始比對原理圖數據...\n");
        String msg = "";
        for (int i = 0; i < newBOMs.size(); i++)
        {
            BOMLinePojo newBOMLine = newBOMs.get(i);
            for (int j = 0; j < oldBOMs.size(); j++)
            {
                BOMLinePojo oldBOMLine = oldBOMs.get(j);
                if (!newBOMLine.getItemId().equals(oldBOMLine.getItemId()))
                {
                    continue;
                }
                if (newBOMLine.getLocation().length() == 0)
                {
                    newBOMs.remove(newBOMLine);
                    oldBOMs.remove(oldBOMLine);
                    i--;
                    j--;
                    break;
                }
                if (!newBOMLine.getLocation().equals(oldBOMLine.getLocation()))
                {
                    continue;
                }
                if (!newBOMLine.getBom().equals(oldBOMLine.getBom()))
                {
                    if (Constants.NI.equals(newBOMLine.getBom()))
                    {
                        msg = "【%s】【%s】位置【%s】：BOM 由【%s】改為【%s】，從父節點【%s】下階移除位置【%s】 \n";
                        msg = String.format(msg, oldBOMLine.getSide(), newBOMLine.getItemId(), newBOMLine.getLocation(), oldBOMLine.getBom(), newBOMLine.getBom(), oldBOMLine.getStepfatherNum(), newBOMLine.getLocation());
                        newBOMLine.setNewNIFlag(true);
                        newBOMs.remove(newBOMLine);
                        i--;
                    }
                    else
                    {
                        msg = "【%s】【%s】位置【%s】：BOM 由【%s】改為【%s】，在父節點【%s】下階新增位置【%s】 \n";
                        msg = String.format(msg, newBOMLine.getSide(), newBOMLine.getItemId(), newBOMLine.getLocation(), oldBOMLine.getBom(), newBOMLine.getBom(), newBOMLine.getStepfatherNum(), newBOMLine.getLocation());
                    }
                    newBOMLine.setActionMsg(msg);
                    oldBOMLine.setShowMsg(false);
                    printerMsgInDialog(msg);
                    continue;
                }
                if (Constants.NI.equals(newBOMLine.getBom()) && Constants.NI.equals(oldBOMLine.getBom()))
                {
                    newBOMs.remove(newBOMLine);
                    oldBOMs.remove(oldBOMLine);
                    i--;
                    j--;
                    break;
                }
                // DIP处理
                if (!newBOMLine.getPackageType().equals(oldBOMLine.getPackageType()))
                {
                    msg = "【%s】【%s】【%s】【%s】改为【%s】,父節點【%s】改為【%s】 \n";
                    msg = String.format(msg, newBOMLine.getSide(), newBOMLine.getItemId(), newBOMLine.getLocation(), oldBOMLine.getPackageType(), newBOMLine.getPackageType(), oldBOMLine.getStepfatherNum(), newBOMLine.getStepfatherNum());
                    newBOMLine.setActionMsg(msg);
                    printerMsgInDialog(msg);
                    newBOMLine.setShowMsg(false);
                    oldBOMLine.setShowMsg(false);
                    continue;
                }
                if (Constants.DIP.equals(newBOMLine.getPackageType()))
                {
                    if (newBOMLine.getInsertionType().equals(oldBOMLine.getInsertionType()))
                    {
                        newBOMs.remove(newBOMLine);
                        oldBOMs.remove(oldBOMLine);
                        i--;
                        j--;
                        break;
                    }
                    msg = "【%s】【%s】位置【%s】：Insertion Type 由【%s】改為【%s】，父節點由【%s】改為【%s】 \n";
                    msg = String.format(msg, newBOMLine.getSide(), newBOMLine.getItemId(), newBOMLine.getLocation(), oldBOMLine.getInsertionType(), newBOMLine.getInsertionType(), oldBOMLine.getStepfatherNum(), newBOMLine.getStepfatherNum());
                }
                else
                { // SMD处理
                    if (newBOMLine.getSide().equals(oldBOMLine.getSide()))
                    {
                        newBOMs.remove(newBOMLine);
                        oldBOMs.remove(oldBOMLine);
                        i--;
                        j--;
                        break;
                    }
                    msg = "【%s】改為【%s】【%s】位置【%s】：父節點由【%s】改為【%s】 \n";
                    msg = String.format(msg, oldBOMLine.getSide(), newBOMLine.getSide(), newBOMLine.getItemId(), newBOMLine.getLocation(), oldBOMLine.getStepfatherNum(), newBOMLine.getStepfatherNum());
                }
                newBOMLine.setActionMsg(msg);
                oldBOMLine.setShowMsg(false);
                printerMsgInDialog(msg);
            }
        }
        updateOldBOMStepfather(pcaPreBomPojo);
        for (BOMLinePojo bomLine : oldBOMs)
        {
            if (bomLine.getActionMsg().length() > 0 || !bomLine.getShowMsg())
            {
                continue;
            }
            msg = "【%s】父節點【%s】下子階物料【%s】移除位置【%s】 \n";
            msg = String.format(msg, bomLine.getSide(), bomLine.getStepfatherNum(), bomLine.getItemId(), bomLine.getLocation());
            if (bomLine.getSide() == null || bomLine.getSide().length() == 0)
            {
                if (bomLine.getLocation() == null || bomLine.getLocation().length() == 0)
                {
                    msg = "父節點【%s】移除子階物料【%s】 \n";
                    msg = String.format(msg, bomLine.getStepfatherNum(), bomLine.getItemId());
                }
                else
                {
                    msg = "父節點【%s】移除子階物料【%s】位置【%s】 \n";
                    msg = String.format(msg, bomLine.getStepfatherNum(), bomLine.getItemId(), bomLine.getLocation());
                }
            }
            else
            {
                if (bomLine.getLocation() == null || bomLine.getLocation().length() == 0)
                {
                    msg = "【%s】父節點【%s】移除子階物料【%s】 \n";
                    msg = String.format(msg, bomLine.getSide(), bomLine.getStepfatherNum(), bomLine.getItemId());
                }
            }
            bomLine.setActionMsg(msg);
            printerMsgInDialog(msg);
        }
        for (int i = 0; i < newBOMs.size(); i++)
        {
            BOMLinePojo bomLine = newBOMs.get(i);
            if (!"NI".equals(bomLine.getBom()))
            {
                for (int k = 0; k < oldBOMs.size(); k++)
                {
                    BOMLinePojo oldBomLine = oldBOMs.get(k);
                    if ("NI".equals(oldBomLine.getBom()) && bomLine.getItemId().equals(oldBomLine.getItemId()))
                    {
                        oldBOMs.remove(oldBomLine);
                        k--;
                        continue;
                    }
                }
                if (bomLine.getActionMsg().length() > 0)
                {
                    continue;
                }
                msg = "【%s】父節點【%s】下子階物料【%s】新增位置【%s】 \n";
                msg = String.format(msg, bomLine.getSide(), bomLine.getStepfatherNum(), bomLine.getItemId(), bomLine.getLocation());
                if (bomLine.getSide() == null || bomLine.getSide().length() == 0)
                {
                    if (bomLine.getLocation() == null || bomLine.getLocation().length() == 0)
                    {
                        msg = "父節點【%s】新增子階物料【%s】 \n";
                        msg = String.format(msg, bomLine.getStepfatherNum(), bomLine.getItemId());
                    }
                    else
                    {
                        msg = "父節點【%s】新增子階物料【%s】位置【%s】 \n";
                        msg = String.format(msg, bomLine.getStepfatherNum(), bomLine.getItemId(), bomLine.getLocation());
                    }
                }
                else
                {
                    if (bomLine.getLocation() == null || bomLine.getLocation().length() == 0)
                    {
                        msg = "【%s】父節點【%s】新增子階物料【%s】 \n";
                        msg = String.format(msg, bomLine.getSide(), bomLine.getStepfatherNum(), bomLine.getItemId());
                    }
                }
                bomLine.setActionMsg(msg);
                printerMsgInDialog(msg);
                continue;
            }
            if (!bomLine.getNewNIFlag())
            {
                msg = "【%s】【%s】【%s】為【%s】 \n";
                msg = String.format(msg, bomLine.getSide(), bomLine.getItemId(), bomLine.getLocation(), bomLine.getBom());
                bomLine.setActionMsg(msg);
                printerMsgInDialog(msg);
                newBOMs.remove(bomLine);
                i--;
                continue;
            }
            newBOMs.remove(bomLine);
            oldBOMs.add(bomLine);
            i--;
        }
        List<BOMLinePojo> newBOMLine = mergeBOMLine(newBOMs, Constants.ADDACTION);
        List<BOMLinePojo> oldBOMLine = mergeBOMLine(oldBOMs, Constants.DELACTION);
        comparisonResult.setAddBOMLine(newBOMLine);
        comparisonResult.setDeleteBOMLine(oldBOMLine);
    }

    void printerMsgInDialog()
    {
        List<BOMLinePojo> newBOMLine = comparisonResult.getAddBOMLine();
        List<BOMLinePojo> oldBOMLine = comparisonResult.getDeleteBOMLine();
        if ((newBOMLine == null || newBOMLine.size() == 0) && (oldBOMLine == null || oldBOMLine.size() == 0) && (ebomPrepareAdd.size() == 0))
        {
            ConvertEBOMDialog2.writeLogText("比對結束,數據沒有差異 ... \n");
        }
        else
        {
            ConvertEBOMDialog2.writeLogText("比對結束...\n-----------------------------系統即將執行以下操作----------------------------- \n");
            printerMsgInDialog(newBOMLine);
            printerMsgInDialog(oldBOMLine);
            printerMsgInDialog(ebomPrepareAdd);
        }
    }

    private void updateOldBOMStepfather(BOMPojo pcaPreBomPojo)
    {
        // TODO Auto-generated method stub
        List<BOMPojo> childs = pcaPreBomPojo.getChild();
        if (childs == null || childs.size() == 0)
        {
            return;
        }
        for (BOMPojo bomPojo : childs)
        {
            for (BOMLinePojo bomLinePojo : oldBOMs)
            {
                if (bomLinePojo.getLocation() == null || bomLinePojo.getLocation().length() == 0)
                {
                    continue;
                }
                if (!bomPojo.getMaterialNum().equals(bomLinePojo.getItemId()))
                {
                    continue;
                }
                if (!bomPojo.getLocation().contains(bomLinePojo.getLocation()))
                {
                    continue;
                }
                if (pcaPreBomPojo.getMaterialNum().equals(bomLinePojo.getStepfatherNum()))
                {
                    continue;
                }
                String msg = "★★★重置【%s】的上階物料，上階物料由【%s】改為【%s】★★★ \n";
                msg = String.format(msg, bomPojo.getMaterialNum(), bomLinePojo.getStepfatherNum(), pcaPreBomPojo.getMaterialNum());
                bomLinePojo.setActionMsg(msg);
                printerMsgInDialog(msg);
                bomLinePojo.setStepfatherNum(pcaPreBomPojo.getMaterialNum());
                bomLinePojo.setStepfather(pcaPreBomPojo.getSelfMaterial().getItemRevision());
                break;
            }
            updateOldBOMStepfather(bomPojo);
        }
    }

    private List<BOMLinePojo> mergeBOMLine(List<BOMLinePojo> bomlines, String actions) throws TCException
    {
        List<BOMLinePojo> result = new ArrayList<BOMLinePojo>();
        Map<String, BOMLinePojo> map = new HashMap<String, BOMLinePojo>();
        for (int i = 0; i < bomlines.size(); i++)
        {
            BOMLinePojo bomLine1 = bomlines.get(i);
            String key = bomLine1.getItemId() + "_" + bomLine1.getSide();
            boolean isAdded = false;
            for (int j = i + 1; j < bomlines.size(); j++)
            {
                BOMLinePojo bomLine2 = bomlines.get(j);
                if (!bomLine1.getItemId().equals(bomLine2.getItemId()))
                {
                    continue;
                }
                if (!bomLine1.getStepfatherNum().equals(bomLine2.getStepfatherNum()))
                {
                    continue;
                }
                setBOMLine(key, map, bomLine1, bomLine2, actions);
                isAdded = true;
                j--;
                bomlines.remove(bomLine2);
            }
            if (isAdded)
            {
                isAdded = false;
                continue;
            }
            String msg = String.format("【%s】【%s】%s 【%s】【%s】 \n", bomLine1.getSide(), bomLine1.getStepfatherNum(), actions, bomLine1.getItemId(), bomLine1.getLocation());
            if (bomLine1.getSide() == null || bomLine1.getSide().length() == 0)
            {
                if (bomLine1.getLocation() == null || bomLine1.getLocation().length() == 0)
                {
                    msg = String.format("【%s】%s 【%s】 \n", bomLine1.getStepfatherNum(), actions, bomLine1.getItemId());
                }
                else
                {
                    msg = String.format("【%s】%s 【%s】【%s】 \n", bomLine1.getStepfatherNum(), actions, bomLine1.getItemId(), bomLine1.getLocation());
                }
            }
            else
            {
                if (bomLine1.getLocation() == null || bomLine1.getLocation().length() == 0)
                {
                    msg = String.format("【%s】【%s】%s  \n", bomLine1.getSide(), bomLine1.getStepfatherNum(), actions, bomLine1.getItemId());
                }
            }
            bomLine1.setActionMsg(msg);
            map.put(key, bomLine1);
        }
        for (Map.Entry<String, BOMLinePojo> entry : map.entrySet())
        {
            result.add(entry.getValue());
        }
        return result;
    }

    private void setBOMLine(String key, Map<String, BOMLinePojo> map, BOMLinePojo bomLine1, BOMLinePojo bomLine2, String actions) throws TCException
    {
        BOMLinePojo bomline = map.get(key);
        String msg = "";
        if (bomline == null)
        {
            String localtion = bomLine1.getLocation() + "," + bomLine2.getLocation();
            bomLine1.setLocation(localtion);
            BigDecimal quantity2 = new BigDecimal(bomLine1.getQuantity()).add(new BigDecimal(bomLine2.getQuantity()));
            String qty = quantity2.toPlainString();
            bomLine1.setQuantity(qty);
            msg = String.format("【%s】【%s】%s 【%s】【%s】 \n", bomLine1.getSide(), bomLine1.getStepfatherNum(), actions, bomLine1.getItemId(), bomLine1.getLocation());
            bomLine1.setActionMsg(msg);
            map.put(key, bomLine1);
        }
        else
        {
            String localtion = bomline.getLocation() + "," + bomLine2.getLocation();
            bomline.setLocation(localtion);
            BigDecimal quantity2 = new BigDecimal(bomline.getQuantity()).add(new BigDecimal(bomLine2.getQuantity()));
            String qty = quantity2.toPlainString();
            bomline.setQuantity(qty);
            msg = String.format("【%s】【%s】%s 【%s】【%s】 \n", bomline.getSide(), bomline.getStepfatherNum(), actions, bomline.getItemId(), localtion);
            bomline.setActionMsg(msg);
            map.put(key, bomline);
        }
    }

    private void printerMsgInDialog(List<BOMLinePojo> newBOMs)
    {
        List<BOMLinePojo> bomlineList = newBOMs.stream()
                                               .sorted(Comparator.comparing(BOMLinePojo::getStepfatherNum).reversed())
                                               .collect(Collectors.toList());
        for (BOMLinePojo bomLine : bomlineList)
        {
            ConvertEBOMDialog2.writeLogText(bomLine.getActionMsg());
        }
    }

    private void printerMsgInDialog(String msg)
    {
        ConvertEBOMDialog2.writeLogText(msg);
    }

    private TCComponentItemRevision getPCAPart() throws Exception
    {
        for (int i = 0; i < selfPartList.size(); i++)
        {
            TCComponentItemRevision itemRev = selfPartList.get(i);
            // String desc = itemRev.getProperty(Constants.ENGLISH_DESC).toUpperCase();
            String materialGroup = itemRev.getProperty(Constants.MATERIAL_GROUP).toUpperCase();
            if (materialGroup.equalsIgnoreCase("B8X80"))
            {
                return itemRev;
            }
        }
        return null;
    }

    private EBOMStructurePojo firstBuildEBOMPojo(TCComponentItemRevision pcaPartRev) throws Exception
    {
        EBOMStructurePojo ebomStructurePCA = new EBOMStructurePojo();
        ebomStructurePCA.setParent(new BOMLinePojo(pcaPartRev));
        List<EBOMStructurePojo> pcaStructurePojos = new ArrayList<EBOMStructurePojo>();
        EBOMStructurePojo selfPartMI = getSelfPartMI();
        if (selfPartMI != null)
        {
            pcaStructurePojos.add(selfPartMI);
        }
        EBOMStructurePojo selfPartSMT = getSelfPartSMT();
        if (selfPartSMT != null)
        {
            pcaStructurePojos.add(selfPartSMT);
        }
        EBOMStructurePojo selfPartSMTT = getSelfPartSMTT();
        if (selfPartSMTT != null)
        {
            pcaStructurePojos.add(selfPartSMTT);
        }
        List<EBOMStructurePojo> selfPartMVA = getSelfPartMVA();
        if (selfPartMVA != null)
        {
            pcaStructurePojos.addAll(selfPartMVA);
        }
        List<EBOMStructurePojo> selfPartASSY = getSelfPartASSY();
        if (selfPartASSY != null)
        {
            pcaStructurePojos.addAll(selfPartASSY);
        }
        ebomStructurePCA.setChildren(pcaStructurePojos);
        return ebomStructurePCA;
    }

    private List<BOMLinePojo> mergeItemIdAndDesignator(List<BOMLinePojo> BOMLinePojoList)
    {
        Map<String, List<BOMLinePojo>> BOMLinePojoListMap = BOMLinePojoList.stream().collect(Collectors.groupingBy(BOMLinePojo::getItemId));
        List<BOMLinePojo> mergeResult = new ArrayList<BOMLinePojo>();
        for (Entry<String, List<BOMLinePojo>> i : BOMLinePojoListMap.entrySet())
        {
            List<BOMLinePojo> value = i.getValue();
            BOMLinePojo value2 = value.get(0);
            int quantity = 1;
            String location = value2.getLocation();
            for (int j = 1; j < value.size(); j++)
            {
                BOMLinePojo bomLinePojo = value.get(j);
                quantity++;
                location = location + "," + bomLinePojo.getLocation();
            }
            value2.setQuantity(String.valueOf(quantity));
            value2.setLocation(location);
            mergeResult.add(value2);
        }
        return mergeResult;
    }

    private EBOMStructurePojo getSelfPartMI() throws Exception
    {
        EBOMStructurePojo ebomStructureMI = new EBOMStructurePojo();
        List<TCComponentItemRevision> selfPartMI = getSelfPartByDesc(Constants.MI);// 從自編料號工作區獲取MI件
        if (selfPartMI.size() == 0)
        {
            ConvertEBOMDialog2.writeLogText("自編料號工作區中未找到【MI】虛擬件！\n");
            return null;
        }
        ebomStructureMI.setParent(new BOMLinePojo(selfPartMI.get(0)));
        List<EBOMStructurePojo> dipStructurePojos = new ArrayList<EBOMStructurePojo>();
        List<BOMLinePojo> designBOMLineDIPPart = bomPojos2.stream()
                                                          .filter(i -> Constants.DIP.equals(i.getPackageType()) && ("".equals(i.getInsertionType()) || "MI".equals(i.getInsertionType())))
                                                          .collect(Collectors.toList());// 從原理圖獲取DIP件
        List<BOMLinePojo> mergeItemIdAndDesignator = mergeItemIdAndDesignator(designBOMLineDIPPart);
        for (int j = 0; j < mergeItemIdAndDesignator.size(); j++)
        {
            BOMLinePojo bomLinePojo = mergeItemIdAndDesignator.get(j);
            EBOMStructurePojo dipStructurePojo = new EBOMStructurePojo();
            dipStructurePojo.setParent(bomLinePojo);
            dipStructurePojos.add(dipStructurePojo);
        }
        ebomStructureMI.setChildren(dipStructurePojos);
        return ebomStructureMI;
    }

    private EBOMStructurePojo getSelfPartSMT() throws Exception
    {
        List<TCComponentItemRevision> selfPartSMT = getSelfPartByDesc(Constants.SMT1);// 從自編料號工作區獲取SMT件
        if (selfPartSMT.size() == 0)
        {
            ConvertEBOMDialog2.writeLogText("自編料號工作區中未找到【SMT】虛擬件！\n");
            return null;
        }
        EBOMStructurePojo ebomStructureSMT = new EBOMStructurePojo();
        ebomStructureSMT.setParent(new BOMLinePojo(selfPartSMT.get(0)));
        List<BOMLinePojo> designBOMLineTOPPart = bomPojos2.stream()
                                                          .filter(i -> Constants.TOP.equals(i.getSide()) && Constants.SMD.equals(i.getPackageType()))
                                                          .collect(Collectors.toList());// 從原理圖獲取TOP件
        List<BOMLinePojo> designBOMLineBOTTOMPart = bomPojos2.stream().filter(i -> Constants.BOTTOM.equals(i.getSide())).collect(Collectors.toList());// 從原理圖獲取BOTTOM件
        List<BOMLinePojo> mergeItemIdAndDesignator1 = mergeItemIdAndDesignator(designBOMLineTOPPart);
        List<BOMLinePojo> mergeItemIdAndDesignator2 = mergeItemIdAndDesignator(designBOMLineBOTTOMPart);
        List<EBOMStructurePojo> SMTStructureChildrenPojos = new ArrayList<EBOMStructurePojo>();
        List<EBOMStructurePojo> tsStructurePojos = new ArrayList<EBOMStructurePojo>();
        for (int j = 0; j < mergeItemIdAndDesignator1.size(); j++)
        {
            BOMLinePojo bomLinePojo = mergeItemIdAndDesignator1.get(j);
            EBOMStructurePojo topStructurePojo = new EBOMStructurePojo();
            topStructurePojo.setParent(bomLinePojo);
            tsStructurePojos.add(topStructurePojo);
        }
        for (int j = 0; j < mergeItemIdAndDesignator2.size(); j++)
        {
            BOMLinePojo bomLinePojo = mergeItemIdAndDesignator2.get(j);
            EBOMStructurePojo bottomStructurePojo = new EBOMStructurePojo();
            bottomStructurePojo.setParent(bomLinePojo);
            tsStructurePojos.add(bottomStructurePojo);
        }
        SMTStructureChildrenPojos.addAll(tsStructurePojos);
        EBOMStructurePojo ebomStructureAI = getSelfPartAI();
        if (ebomStructureAI != null)
        {
            SMTStructureChildrenPojos.add(ebomStructureAI);
        }
        if (!isExistAIASelfPart && !isExistAISelfPart && !isExistSMTBSelfPart)
        {
            List<EBOMStructurePojo> selfPartPCB = getSelfPartPCB();
            SMTStructureChildrenPojos.addAll(selfPartPCB);
        }
        ebomStructureSMT.setChildren(SMTStructureChildrenPojos);
        return ebomStructureSMT;
    }

    private EBOMStructurePojo getSelfPartSMTT() throws Exception
    {
        List<TCComponentItemRevision> selfPartSMTT = getSelfPartByDesc(Constants.SMT_T1);// 從自編料號工作區獲取SMT/T件
        if (selfPartSMTT.size() == 0)
        {
            ConvertEBOMDialog2.writeLogText("自編料號工作區中未找到【SMT/T】虛擬件！\n");
            return null;
        }
        EBOMStructurePojo ebomStructureSMTT = new EBOMStructurePojo();
        ebomStructureSMTT.setParent(new BOMLinePojo(selfPartSMTT.get(0)));
        List<BOMLinePojo> designBOMLineTOPPart = bomPojos2.stream()
                                                          .filter(i -> Constants.SMD.equals(i.getPackageType()) && Constants.TOP.equals(i.getSide()))
                                                          .collect(Collectors.toList());// 從原理圖獲取TOP件
        List<BOMLinePojo> mergeItemIdAndDesignator = mergeItemIdAndDesignator(designBOMLineTOPPart);
        List<EBOMStructurePojo> SMTTStructureChildrenPojos = new ArrayList<EBOMStructurePojo>();
        List<EBOMStructurePojo> topStructurePojos = new ArrayList<EBOMStructurePojo>();
        for (int j = 0; j < mergeItemIdAndDesignator.size(); j++)
        {
            BOMLinePojo bomLinePojo = mergeItemIdAndDesignator.get(j);
            EBOMStructurePojo topStructurePojo = new EBOMStructurePojo();
            topStructurePojo.setParent(bomLinePojo);
            topStructurePojos.add(topStructurePojo);
        }
        SMTTStructureChildrenPojos.addAll(topStructurePojos);
        EBOMStructurePojo selfPartSMTB = getSelfPartSMTB();// 從自編料號工作區獲取SMT/B件
        if (selfPartSMTB == null)
        {
            throw new Exception("自編料號工作區中未找到【SMT/B】虛擬件，請修正后進行此操作！");
        }
        SMTTStructureChildrenPojos.add(selfPartSMTB);
        ebomStructureSMTT.setChildren(SMTTStructureChildrenPojos);
        return ebomStructureSMTT;
    }

    private EBOMStructurePojo getSelfPartSMTB() throws Exception
    {
        List<TCComponentItemRevision> selfPartSMTB = getSelfPartByDesc(Constants.SMT_B1);// 從自編料號工作區獲取SMT/T件
        if (selfPartSMTB.size() == 0)
        {
            ConvertEBOMDialog2.writeLogText("自編料號工作區中未找到【SMT/B】虛擬件！\n");
            return null;
        }
        EBOMStructurePojo ebomStructureSMTB = new EBOMStructurePojo();
        ebomStructureSMTB.setParent(new BOMLinePojo(selfPartSMTB.get(0)));
        List<BOMLinePojo> designBOMLineBottomPart = bomPojos2.stream()
                                                             .filter(i -> Constants.SMD.equals(i.getPackageType()) && Constants.BOTTOM.equals(i.getSide()))
                                                             .collect(Collectors.toList());// 從原理圖獲取BOTTOM件
        List<BOMLinePojo> mergeItemIdAndDesignator = mergeItemIdAndDesignator(designBOMLineBottomPart);
        List<EBOMStructurePojo> SMTBStructureChildrenPojos = new ArrayList<EBOMStructurePojo>();
        List<EBOMStructurePojo> bottomStructurePojos = new ArrayList<EBOMStructurePojo>();
        for (int j = 0; j < mergeItemIdAndDesignator.size(); j++)
        {
            BOMLinePojo bomLinePojo = mergeItemIdAndDesignator.get(j);
            EBOMStructurePojo bottomStructurePojo = new EBOMStructurePojo();
            bottomStructurePojo.setParent(bomLinePojo);
            bottomStructurePojos.add(bottomStructurePojo);
        }
        SMTBStructureChildrenPojos.addAll(bottomStructurePojos);
        EBOMStructurePojo selfPartAI = getSelfPartAI();
        if (selfPartAI != null)
        {
            SMTBStructureChildrenPojos.add(selfPartAI);
        }
        if (!isExistAIASelfPart && !isExistAISelfPart)
        {
            List<EBOMStructurePojo> selfPartPCB = getSelfPartPCB();
            SMTBStructureChildrenPojos.addAll(selfPartPCB);
        }
        ebomStructureSMTB.setChildren(SMTBStructureChildrenPojos);
        return ebomStructureSMTB;
    }

    private List<EBOMStructurePojo> getSelfPartMVA() throws Exception
    {
        List<TCComponentItemRevision> selfPartaMVA = getSelfPartByDesc(Constants.MVA);// 從自編料號工作區獲取MVA件
        if (selfPartaMVA.size() == 0)
        {
            ConvertEBOMDialog2.writeLogText("自編料號工作區中未找到【MAV】虛擬件！\n");
            return null;
        }
        List<EBOMStructurePojo> ebomStructureMVAs = new ArrayList<EBOMStructurePojo>();
        for (int i = 0; i < selfPartaMVA.size(); i++)
        {
            EBOMStructurePojo ebomStructureMVA = new EBOMStructurePojo();
            ebomStructureMVA.setParent(new BOMLinePojo(selfPartaMVA.get(i)));
            ebomStructureMVAs.add(ebomStructureMVA);
        }
        return ebomStructureMVAs;
    }

    private List<EBOMStructurePojo> getSelfPartASSY() throws Exception
    {
        List<TCComponentItemRevision> selfPartaASSY = getSelfPartByDesc(Constants.ASSY);// 從自編料號工作區獲取ASSY件
        if (selfPartaASSY.size() == 0)
        {
            ConvertEBOMDialog2.writeLogText("自編料號工作區中未找到【ASSY】虛擬件！\n");
            return null;
        }
        List<EBOMStructurePojo> ebomStructureASSYs = new ArrayList<EBOMStructurePojo>();
        for (int i = 0; i < selfPartaASSY.size(); i++)
        {
            EBOMStructurePojo ebomStructureASSY = new EBOMStructurePojo();
            ebomStructureASSY.setParent(new BOMLinePojo(selfPartaASSY.get(i)));
            ebomStructureASSYs.add(ebomStructureASSY);
        }
        return ebomStructureASSYs;
    }

    private EBOMStructurePojo getSelfPartAI() throws Exception
    {
        List<TCComponentItemRevision> selfPartaAI = getSelfPartByDesc(Constants.AI);// 從自編料號工作區獲取AI件
        if (selfPartaAI.size() == 0)
        {
            ConvertEBOMDialog2.writeLogText("自編料號工作區中未找到【AI】虛擬件！\n");
            return null;
        }
        EBOMStructurePojo ebomStructureAI = new EBOMStructurePojo();
        ebomStructureAI.setParent(new BOMLinePojo(selfPartaAI.get(0)));
        List<EBOMStructurePojo> ebomStructureAIChildren = new ArrayList<EBOMStructurePojo>();
        List<BOMLinePojo> designBOMLineAIPart = bomPojos2.stream()
                                                         .filter(i -> Constants.DIP.equals(i.getPackageType()) && Constants.AI1.equals(i.getInsertionType()))
                                                         .collect(Collectors.toList());// 從原理圖獲取AI件
        List<BOMLinePojo> mergeItemIdAndDesignator = mergeItemIdAndDesignator(designBOMLineAIPart);
        for (int j = 0; j < mergeItemIdAndDesignator.size(); j++)
        {
            BOMLinePojo bomLinePojo = mergeItemIdAndDesignator.get(j);
            EBOMStructurePojo aiStructurePojo = new EBOMStructurePojo();
            aiStructurePojo.setParent(bomLinePojo);
            ebomStructureAIChildren.add(aiStructurePojo);
        }
        EBOMStructurePojo selfPartAIA = getSelfPartAIA();
        if (selfPartAIA != null)
        {
            ebomStructureAIChildren.add(selfPartAIA);
        }
        else
        {
            List<EBOMStructurePojo> selfPartPCB = getSelfPartPCB();
            ebomStructureAIChildren.addAll(selfPartPCB);
        }
        EBOMStructurePojo selfPartAIR = getSelfPartAIR();
        if (selfPartAIR != null)
        {
            ebomStructureAIChildren.add(selfPartAIR);
        }
        ebomStructureAI.setChildren(ebomStructureAIChildren);
        return ebomStructureAI;
    }

    private EBOMStructurePojo getSelfPartAIA() throws Exception
    {
        List<TCComponentItemRevision> selfPartaAIA = getSelfPartByDesc(Constants.AI_A1);// 從自編料號工作區獲取AI/A件
        if (selfPartaAIA.size() == 0)
        {
            ConvertEBOMDialog2.writeLogText("自編料號工作區中未找到【AI/A】虛擬件！\n");
            return null;
        }
        EBOMStructurePojo ebomStructureAIA = new EBOMStructurePojo();
        ebomStructureAIA.setParent(new BOMLinePojo(selfPartaAIA.get(0)));
        List<EBOMStructurePojo> ebomStructureAIAChildren = new ArrayList<EBOMStructurePojo>();
        List<BOMLinePojo> designBOMLineAIAPart = bomPojos2.stream()
                                                          .filter(i -> Constants.DIP.equals(i.getPackageType()) && Constants.AI_A1.equals(i.getInsertionType()))
                                                          .collect(Collectors.toList());// 從原理圖獲取AI/A件
        List<BOMLinePojo> mergeItemIdAndDesignator = mergeItemIdAndDesignator(designBOMLineAIAPart);
        for (int j = 0; j < mergeItemIdAndDesignator.size(); j++)
        {
            BOMLinePojo bomLinePojo = mergeItemIdAndDesignator.get(j);
            EBOMStructurePojo aiaStructurePojo = new EBOMStructurePojo();
            aiaStructurePojo.setParent(bomLinePojo);
            ebomStructureAIAChildren.add(aiaStructurePojo);
        }
        List<EBOMStructurePojo> selfPartPCB = getSelfPartPCB();
        ebomStructureAIAChildren.addAll(selfPartPCB);
        ebomStructureAIA.setChildren(ebomStructureAIAChildren);
        return ebomStructureAIA;
    }

    private List<EBOMStructurePojo> getSelfPartPCB() throws Exception
    {
        List<EBOMStructurePojo> ebomStructurePCB = new ArrayList<EBOMStructurePojo>();
        List<BOMLinePojo> pcbMainPart = currentBomPojo.stream()
                                                      .filter(i -> "D9_PCB_PartRevision".equals(i.getItemType()))
                                                      .collect(Collectors.toList());// 主料
        List<TCComponentItemRevision> pcbSubstitutePart = getSelfPartByType("D9_PCB_PartRevision");// 替代料
        for (int j = 0; j < pcbMainPart.size(); j++)
        {
            BOMLinePojo bomLinePojo = pcbMainPart.get(j);
            EBOMStructurePojo pcbPart = new EBOMStructurePojo();
            pcbPart.setParent(bomLinePojo);
            pcbPart.setSubstitute(pcbSubstitutePart);
            ebomStructurePCB.add(pcbPart);
        }
        return ebomStructurePCB;
    }

    private EBOMStructurePojo getSelfPartAIR() throws Exception
    {
        List<TCComponentItemRevision> selfPartaAIR = getSelfPartByDesc(Constants.AI_R1);// 從自編料號工作區獲取AI/R件
        if (selfPartaAIR.size() == 0)
        {
            ConvertEBOMDialog2.writeLogText("自編料號工作區中未找到【AI/R】虛擬件！\n");
            return null;
        }
        EBOMStructurePojo ebomStructureAIR = new EBOMStructurePojo();
        ebomStructureAIR.setParent(new BOMLinePojo(selfPartaAIR.get(0)));
        List<EBOMStructurePojo> ebomStructureAIRChildren = new ArrayList<EBOMStructurePojo>();
        List<BOMLinePojo> designBOMLineAIRPart = bomPojos2.stream()
                                                          .filter(i -> Constants.DIP.equals(i.getPackageType()) && Constants.AI_R1.equals(i.getInsertionType()))
                                                          .collect(Collectors.toList());// 從原理圖獲取AI/R件
        List<BOMLinePojo> mergeItemIdAndDesignator = mergeItemIdAndDesignator(designBOMLineAIRPart);
        for (int j = 0; j < mergeItemIdAndDesignator.size(); j++)
        {
            BOMLinePojo bomLinePojo = mergeItemIdAndDesignator.get(j);
            EBOMStructurePojo airStructurePojo = new EBOMStructurePojo();
            airStructurePojo.setParent(bomLinePojo);
            ebomStructureAIRChildren.add(airStructurePojo);
        }
        ebomStructureAIR.setChildren(ebomStructureAIRChildren);
        return ebomStructureAIR;
    }

    public Map<String, String> getPlaceTxtMapping(TCComponentFolder modelFolder)
    {
        if (modelFolder != null)
        {
            try
            {
                AIFComponentContext[] childrenFolder = modelFolder.getRelated(Constants.CONTENTS);
                if (childrenFolder.length > 0)
                {
                    for (int i = 0; i < childrenFolder.length; i++)
                    {
                        TCComponent component = (TCComponent) childrenFolder[i].getComponent();
                        if (component instanceof TCComponentItemRevision)
                        {
                            AIFComponentContext[] datesets = component.getRelated("EDAHasDerivedDataset");
                            if (datesets.length > 0)
                            {
                                for (AIFComponentContext cx : datesets)
                                {
                                    TCComponent dataset = (TCComponent) cx.getComponent();
                                    if (dataset instanceof TCComponentDataset)
                                    {
                                        String objectType = dataset.getProperty("object_type");
                                        if ("Placement".equalsIgnoreCase(objectType))
                                        {
                                            TCComponentTcFile[] tcfiles = ((TCComponentDataset) dataset).getTcFiles();
                                            if (tcfiles.length > 0)
                                            {
                                                File placeFile = tcfiles[0].getFmsFile();
                                                return Files.readAllLines(placeFile.toPath())
                                                            .stream()
                                                            .skip(5)
                                                            .map(e -> e.split("!"))
                                                            .filter(e -> e.length == 7)
                                                            .collect(Collectors.toMap(e -> e[0].trim(), e -> {
                                                                if (e[4] != null && "m".equalsIgnoreCase(e[4].trim()))
                                                                {
                                                                    return Constants.BOTTOM;
                                                                }
                                                                return Constants.TOP;
                                                            }));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 返回升版规则
     * 
     * @param itemRev
     * @return
     * @throws TCException
     */
    private String getVersionRule(TCComponentItemRevision itemRev) throws TCException
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

    public void comparisonEBOM()
    {
        TCComponentBOMWindow bomWindow = null;
        try
        {
            ConvertEBOMDialog2.writeLogText("開始比對 PCA EBOM... \n");
            if (currentBomPojoNew != null)
            {
                // Map<String, List<BOMLinePojo>> dBomMap = currentBomPojoNew.stream()
                // .filter(e -> !"NI".equalsIgnoreCase(e.getBom()))
                // .collect(Collectors.groupingBy(BOMLinePojo::getStepfatherNum));
                TCComponentItemRevision pcaPartRev = getPCAPart();
                bomWindow = PartBOMUtils.createBomWindow(pcaPartRev);
                TCComponentBOMLine ebomTopBOMLine = bomWindow.getTopBOMLine();
                com.foxconn.tcutils.util.TCUtil.unpackageBOMStructure(ebomTopBOMLine);
                List<BOMLinePojo> pcaBOMList = new ArrayList<BOMLinePojo>();
                getPCAEBOMStuct(ebomTopBOMLine, pcaBOMList);
                // Map<String, List<BOMLinePojo>> eBomMap = pcaBOMList.stream().collect(Collectors.groupingBy(BOMLinePojo::getStepfatherNum));
                // Set<String> dKeys = dBomMap.keySet();
                // for (String keyStr : dKeys) {}
                currentBomPojoNew.removeIf(e -> "NI".equalsIgnoreCase(e.getBom()));
                List<BOMLinePojo> addLocationPojoList = getAddLocation(currentBomPojoNew, pcaBOMList);
                //
                List<BOMLinePojo> newBOMLine = comparisonResult.getAddBOMLine();
                Set<String> addSets = newBOMLine.stream().map(BOMLinePojo::getLocation).collect(Collectors.toSet());
                if (addSets.size() > 0)
                {
                    addLocationPojoList.removeIf(e -> addSets.contains(e.getLocation()));
                }
                //
                ebomPrepareAdd.addAll(addLocationPojoList);
                if (ebomPrepareAdd.size() > 0)
                {
                    ebomPrepareAdd.sort(Comparator.comparing(BOMLinePojo::getStepfatherNum));
                    // ConvertEBOMDialog2.writeLogText("PCA EBOM 比對結果：\n");
                    for (BOMLinePojo addPojo : ebomPrepareAdd)
                    {
                        addPojo.setActionMsg(String.format("【%s】【%s】新增位號 【%s】【%s】 \n", addPojo.getSide(), addPojo.getStepfatherNum(), addPojo.getItemId(), addPojo.getLocation()));
                        ConvertEBOMDialog2.writeLogText(String.format("線路圖【%s】 【%s】 位號在 PCA EBOM【%s】 中不存在;\n", addPojo.getLocation(), addPojo.getItemId(), addPojo.getStepfatherNum()));
                    }
                }
            }
        }
        catch (Exception e)
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
        printerMsgInDialog();
    }

    Set<String> getAddedLocation(String parentId)
    {
        List<BOMLinePojo> addedList = comparisonResult.getAddBOMLine();
        if (addedList != null && addedList.size() > 0)
        {
            return comparisonResult.getAddBOMLine()
                                   .stream()
                                   .filter(e -> parentId.equalsIgnoreCase(e.getStepfatherNum()))
                                   .map(e -> e.getLocation().split(","))
                                   .flatMap(Stream::of)
                                   .collect(Collectors.toSet());
        }
        return new HashSet<String>();
    }

    public List<BOMLinePojo> getAddLocation(List<BOMLinePojo> dBomList, List<BOMLinePojo> eBomList)
    {
        List<BOMLinePojo> list = new ArrayList<BOMLinePojo>();
        if (dBomList != null && dBomList.size() > 0 && eBomList != null && eBomList.size() > 0)
        {
            Map<String, BOMLinePojo> dMap = convertLocationMap(dBomList, true);
            Map<String, BOMLinePojo> eMap = convertLocationMap(eBomList, false);
            Set<String> dLocations = new HashSet<String>(dMap.keySet());
            dLocations.removeAll(eMap.keySet());
            Set<String> addedSet = getAddedLocation(eBomList.get(0).getStepfatherNum());
            dLocations.removeAll(addedSet);
            if (dLocations.size() > 0)
            {
                Map<String, List<BOMLinePojo>> addPojoMap = dMap.keySet()
                                                                .stream()
                                                                .filter(e -> dLocations.contains(e))
                                                                .map(e -> dMap.get(e))
                                                                .collect(Collectors.groupingBy(BOMLinePojo::getItemId));
                for (List<BOMLinePojo> pojoList : addPojoMap.values())
                {
                    String locations = pojoList.stream().map(BOMLinePojo::getLocation).collect(Collectors.joining(","));
                    BOMLinePojo pojo = pojoList.get(0);
                    pojo.setLocation(locations);
                    list.add(pojo);
                }
            }
        }
        return list;
    }

    Map<String, BOMLinePojo> convertLocationMap(List<BOMLinePojo> bomList, boolean isClone)
    {
        Map<String, BOMLinePojo> map = new HashMap<String, BOMLinePojo>();
        for (BOMLinePojo bomPojo : bomList)
        {
            String[] locations = bomPojo.getLocation().split(",");
            for (String location : locations)
            {
                if (StrUtil.isNotEmpty(location))
                {
                    BOMLinePojo bomPojoN = bomPojo;
                    if (isClone)
                    {
                        bomPojoN = BeanUtil.copyProperties(bomPojo, BOMLinePojo.class);
                        bomPojoN.setLocation(location);
                    }
                    map.put(location, bomPojoN);
                }
            }
        }
        return map;
    }

    public void getPCAEBOMStuct(TCComponentBOMLine ebomTopBOMLine, List<BOMLinePojo> bomList) throws TCException
    {
        AIFComponentContext[] aifChilds = ebomTopBOMLine.getChildren();
        for (AIFComponentContext context : aifChilds)
        {
            TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
            String parentItemId = ebomTopBOMLine.getProperty(Constants.BL_ITEM_ID);
            TCComponentItemRevision parentItemRev = ebomTopBOMLine.getItemRevision();
            if (!bomLine.isSubstitute())
            {
                BOMLinePojo bomPojo = new BOMLinePojo();
                String itemId = bomLine.getProperty(Constants.BL_ITEM_ID);
                String quantity = bomLine.getProperty(Constants.BL_QUANTITY);
                String location = bomLine.getProperty(Constants.BL_LOCATION);
                String desc = bomLine.getProperty(Constants.BL_DESCRIPTION);
                String packageType = bomLine.getProperty(Constants.BL_PACKAGE_TYPE);
                String insertionType = bomLine.getProperty(Constants.INSERTION_TYPE);
                String side = bomLine.getProperty(Constants.BL_SIDE);
                String bom = bomLine.getProperty(Constants.BL_BOM);
                bomPojo.setItemRev(bomLine.getItemRevision());
                bomPojo.setItemId(itemId);
                bomPojo.setQuantity(quantity);
                bomPojo.setLocation(location);
                bomPojo.setPackageType(packageType);
                bomPojo.setInsertionType(insertionType);
                bomPojo.setSide(side);
                bomPojo.setBom(bom);
                bomPojo.setDesc(desc);
                bomPojo.setStepfatherNum(parentItemId);
                bomPojo.setStepfather(parentItemRev);
                bomList.add(bomPojo);
                if (bomLine.hasChildren())
                {
                    getPCAEBOMStuct(bomLine, bomList);
                }
            }
        }
    }

    public static void main(String[] args)
    {
    }
}
