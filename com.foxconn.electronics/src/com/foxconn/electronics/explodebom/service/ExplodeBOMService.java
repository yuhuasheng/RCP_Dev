package com.foxconn.electronics.explodebom.service;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.SwingUtilities;

import com.foxconn.electronics.explodebom.domain.BOMLineBean;
import com.foxconn.electronics.explodebom.domain.InputInfoBean;
import com.foxconn.electronics.util.PartBOMUtils;
import com.foxconn.electronics.util.FileStreamUtil;
import com.foxconn.electronics.util.Phase;
import com.foxconn.electronics.util.TCPropName;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMView;
import com.teamcenter.rac.kernel.TCComponentBOMViewRevision;
import com.teamcenter.rac.kernel.TCComponentBOMViewRevisionType;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentViewType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.bom.BOMLineViewerHelper;
import com.teamcenter.rac.util.MessageBox;

/**
 * 炸BOM 业务逻辑处理
 * 
 * @author Robert
 *
 */
public class ExplodeBOMService implements Runnable
{
    private static final int           LIMIT_SIZE                        = 4;
    private static final int           PRE_SIZE                          = 6;
    private static final int           POST_SIZE                         = 0;
    private static final String        PCBA_Muti_TYPE                    = "Item";                // "D9_EE_PCBA_MutiRevision";
    private static final String        PCBA_Muti_LINK                    = "EDAHasVariant";
    private static final String        PREFERENCE_VARIANT_BOM_SEPARATION = ":";
    private static final String        BOM_ATTR_SEPARATION               = ",";
    private static final String        PREFERENCE_VARIANT_BOM_STATE      = "D9_Variant_BOM_State";
    private static Map<String, String> variantBOMStateMap;
    private List<BOMLineBean>          bomLineBeans;
    static
    {
        if (variantBOMStateMap == null)
        {
            variantBOMStateMap = TCUtil.getArrayPreference(TCPreferenceService.TC_preference_site, PREFERENCE_VARIANT_BOM_STATE)
                                       .stream()
                                       .map(e -> e.toUpperCase().split(PREFERENCE_VARIANT_BOM_SEPARATION))
                                       .collect(Collectors.toMap(e -> e[1], e -> e[0]));
        }
    }
    //
    private TCSession                       session;
    private TCComponentBOMLine              rootBomLine;
    private TCComponentItemRevision         pcbPartRev;
    private List<InputInfoBean>             inputs;
    private Map<TCComponentBOMLine, String> exceptionDataMap = new HashMap<TCComponentBOMLine, String>();

    public ExplodeBOMService(TCSession session, TCComponentBOMLine rootBomLine, List<InputInfoBean> inputs)
    {
        this.session = session;
        this.rootBomLine = rootBomLine;
        this.inputs = inputs;
    }

    public void executeCreateBom()
    {
        try
        {
            System.out.println("******************* start  explpde bom **********************");
            this.bomLineBeans = genBomLineBeans();
            if (exceptionDataMap.size() == 0)
            {
                System.out.println("*******************explpde bom: 数据没有问题，进行处理 **********************");
                run();
            }
        }
        catch (Exception e)
        {
            exceptionDataMap.put(rootBomLine, e.getCause().getMessage());
            e.printStackTrace();
        }
    }

    public void execute()
    {
        System.out.println("******************* start explpde bom **********************");
        List<BOMLineBean> bomLineBeans = genBomLineBeans();
        System.out.println("******************* check data complete ! **********************");
        if (exceptionDataMap.size() == 0)
        {
            // 数据没有问题，进行处理
            Map<String, List<BOMLineBean>> dataMap = inputs.stream()
                                                           .collect(Collectors.toMap(this::getBOMFileName, e -> packedBOMLineBeans(extractBOMLine(bomLineBeans, e.getBomNo(), Phase.valueOf(e.getPhase())))));
            // 导出bom file
            dataMap.forEach((key, value) -> {
                try
                {
                    ExportBOMFile((TCComponentItemRevision) rootBomLine.getItemRevision(), key, value);
                }
                catch (TCException exp)
                {
                    exp.printStackTrace();
                }
            });
        }
    }

    public void createBom(TCComponentItemRevision pcbaMultiImteRev, List<BOMLineBean> bomLineBeans) throws TCException
    {
        TCComponentBOMWindow bomWindow = PartBOMUtils.createBomWindow(pcbaMultiImteRev);
        TCComponentBOMLine topBomLine = bomWindow.getTopBOMLine();
        if (pcbPartRev != null)
        {
            topBomLine.add(pcbPartRev.getItem(), pcbPartRev, null, false);// 添加PCB
        }
        bomLineBeans.forEach(bomBean -> {
            try
            {
                TCComponentItemRevision itemRevision = bomBean.getItemRevisison();
                TCComponentBOMLine childBomLine = topBomLine.add(itemRevision.getItem(), itemRevision, null, false);
                Map<String, String> propMap = TCUtil.getBeanTCPropMap(bomBean);
                childBomLine.setProperties(propMap);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new RuntimeException(e.getCause());
            }
        });
        bomWindow.save();
        bomWindow.close();
    }

    private String getPCBAMultiItemName(InputInfoBean inputInfoBean)
    {
        String bomName = "";
        try
        {
            // 总图名称
            bomName = rootBomLine.getProperty("bl_item_object_name");
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return "POS_" + inputInfoBean.getBomNo() + "_" + bomName;
    }

    /**
     * POS_” BOM栏位序号 ”_”总图名称”_”客户PROJECT NAME”_YYYYMMDD
     * 
     * @param inputInfoBean
     * @return
     */
    private String getBOMFileName(InputInfoBean inputInfoBean)
    {
        String bomName = "";
        try
        {
            // 总图名称
            bomName = rootBomLine.getProperty("bl_item_object_name");
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return "POS_" + inputInfoBean.getBomNo() + "_" + bomName + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    /**
     * 依据bom 属性进行上件处理
     * 
     * @param bomLineBeans
     * @param bomNo
     * @return
     */
    private List<BOMLineBean> extractBOMLine(List<BOMLineBean> bomLineBeans, int bomNo, Phase phase)
    {
        return bomLineBeans.stream().map(e -> {
            String[] bomAttrs = e.getBom().split(BOM_ATTR_SEPARATION);
            if (bomAttrs.length > 1)
            {
                e.setBom(bomAttrs[bomNo - 1]);
            }
            return e;
        }).filter(e -> {
            String bomSate = variantBOMStateMap.get(e.getBom().toUpperCase());
            return extractRules(bomSate, phase);
        }).collect(Collectors.toList());
    }

    /**
     * 提取规则
     * 
     * @param bomSate
     * @param phase
     * @return
     */
    private boolean extractRules(String bomSate, Phase phase)
    {
        switch (bomSate)
        {
            case "0":
                return false;
            case "1":
                return true;
            case "MP":
                return Phase.MP.equals(phase);
            case "NPI":
                return Phase.NPI.equals(phase);
            default:
                System.out.println(bomSate + "  Rule not configured !");
                return false;
        }
    }

    /**
     * 合并loaction 打包
     * 
     * @param bomLineBeans
     * @return
     */
    private List<BOMLineBean> packedBOMLineBeans(List<BOMLineBean> bomLineBeans)
    {
        return bomLineBeans.stream().collect(Collectors.toMap(this::getKeyForPacked, e -> e, (n, o) -> {
            n.setLocation(o.getLocation() + "," + n.getLocation());
            return n;
        })).values().stream().collect(Collectors.toList());
    }

    private String getKeyForPacked(BOMLineBean bomLineBean)
    {
        Field[] fields = bomLineBean.getClass().getDeclaredFields();
        StringBuffer keys = new StringBuffer();
        for (int i = 0; i < fields.length; i++)
        {
            fields[i].setAccessible(true);
            TCPropName tcPropName = fields[i].getAnnotation(TCPropName.class);
            if (tcPropName != null && tcPropName.isKey())
            {
                try
                {
                    keys.append(Optional.ofNullable(fields[i].get(bomLineBean)).orElse("null"));
                }
                catch (IllegalArgumentException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return keys.toString();
    }

    /**
     * @param bomLineBean
     * @return 返回True 表示数据合法
     */
    private String checkData(BOMLineBean bomLineBean)
    {
        // bom 属性检查
        StringBuffer result = new StringBuffer();
        String bomAttr = bomLineBean.getBom();
        if (bomAttr == null || bomAttr.length() == 0)
        {
            result.append("bom attribute is null!");
            return result.toString();
        }
        String[] bomAttrs = bomAttr.split(BOM_ATTR_SEPARATION);
        for (InputInfoBean inputBean : inputs)
        {
            String temp = "";
            // 判断长度是否合规
            if (bomAttrs.length != 1 && bomAttrs.length < inputBean.getBomNo())
            {
                temp += bomAttr + " bom attribute length less than bom column number ";
            }
            // 判断首选项是否包含该bom 属性
            String containsBom = Stream.of(bomAttrs).filter(e -> !variantBOMStateMap.containsKey(e.toUpperCase())).collect(Collectors.joining(","));
            if (containsBom.length() != 0)
            {
                temp += "teamcenter preference variant bom state not contain bom attribute :" + containsBom;
            }
            // if (!variantBOMStateMap.containsKey(bomAttrs[inputBean.getBomNo() - 1]) || !variantBOMStateMap.containsKey(bomAttrs[0]))
            // {
            // temp += "teamcenter preference variant bom state not contain bom attribute :" + bomAttrs[0] + " or " + bomAttrs[inputBean.getBomNo() -
            // 1];
            // }
            if (temp.length() > 0)
            {
                result.append(inputBean.getPcbNumber()).append(" error info;").append(temp).append("  !");
            }
        }
        return result.toString();
    }

    /**
     * object_type : Part 不处理 EDAComp 处理
     * 
     * @param bomLine
     * @return 返回true 表示是需要处理的数据
     */
    private boolean filterData(TCComponentBOMLine bomLine)
    {
        try
        {
            boolean isPcb = "Part".equalsIgnoreCase(bomLine.getStringProperty("bl_item_object_type"));
            if (isPcb)
            {
                this.pcbPartRev = bomLine.getItemRevision();
            }
            return !isPcb;
        }
        catch (TCException e)
        {
            exceptionDataMap.put(bomLine, e.getCause().getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private List<BOMLineBean> genBomLineBeans()
    {
        try
        {
            rootBomLine.refresh();
            return TCUtil.getTCComponmentBOMLines(rootBomLine, null, true).stream().filter(e -> filterData(e)).map(e -> {
                try
                {
                    BOMLineBean bomLineBean = TCUtil.tcPropMapping(new BOMLineBean(), e);
                    bomLineBean.setItemRevisison(e.getItemRevision());
                    String checkResult = checkData(bomLineBean);
                    if (checkResult.length() > 0)
                    {
                        exceptionDataMap.put(e, checkResult);
                    }
                    return bomLineBean;
                }
                catch (Exception e1)
                {
                    exceptionDataMap.put(e, e1.getCause().getMessage());
                    e1.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
        }
        catch (Exception e2)
        {
            exceptionDataMap.put(rootBomLine, e2.getCause().getMessage());
            e2.printStackTrace();
        }
        return null;
    }

    public Map<TCComponentBOMLine, String> getExceptionDataMap()
    {
        return exceptionDataMap;
    }

    public static Map<TCComponentBOMLine, String> executeGenBom(TCSession session, TCComponentBOMLine rootBomLine, List<InputInfoBean> inputs) throws Exception
    {
        ExplodeBOMService explodeBom = new ExplodeBOMService(session, rootBomLine, inputs);
        explodeBom.executeCreateBom();
        return explodeBom.getExceptionDataMap();
    }

    /**
     * 创建BOM File
     * 
     * @return
     */
    public void ExportBOMFile(TCComponentItemRevision itemRevComp, String fileName, List<BOMLineBean> bomLst)
    {
        try
        {
            FileStreamUtil fileStreamUtil = new FileStreamUtil();
            String fileFullPath = fileStreamUtil.getBOMFileTempPath(fileName + ".BOM");
            PrintStream printStream = fileStreamUtil.openStream(fileFullPath);
            WriteBOMFileHeader(fileStreamUtil, printStream);
            int iItemIndex = 0;
            List<Field> fieldLst = TCUtil.getOrderedField(new BOMLineBean());
            for (BOMLineBean bomlineBean : bomLst)
            {
                try
                {
                    iItemIndex++;
                    String sTextLine = "";
                    String sProcessInfo = TCUtil.getProcessInfo(bomlineBean);
                    List<String> lineLst = TCUtil.splitText(sProcessInfo, BOM_ATTR_SEPARATION, LIMIT_SIZE);
                    for (int i = 0; i < lineLst.size(); i++)
                    {
                        if (0 == i)
                        {
                            sTextLine = iItemIndex + "\t" + TCUtil.getBOMLineInfo(bomlineBean, fieldLst, lineLst.get(i));
                        }
                        else
                        {
                            List<String> bomLineLst = new ArrayList<String>();
                            for (int j = 0; j < PRE_SIZE; j++)
                            {
                                bomLineLst.add("");
                            }
                            bomLineLst.add(lineLst.get(i));
                            for (int j = 0; j < POST_SIZE; j++)
                            {
                                bomLineLst.add("");
                            }
                            sTextLine = bomLineLst.stream().collect(Collectors.joining("\t"));
                        }
                        fileStreamUtil.writeData(printStream, sTextLine);
                    }
                }
                catch (IllegalAccessException | TCException exp)
                {
                    exp.printStackTrace();
                }
            }
            fileStreamUtil.close(printStream);
            linkDataSet(itemRevComp, fileFullPath, fileName);
            fileStreamUtil.deleteFile(fileFullPath);
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 写入BOM File信息
     * 
     * @return
     */
    public void WriteBOMFileHeader(FileStreamUtil fileStreamUtil, PrintStream printStream) throws TCException
    {
        fileStreamUtil.writeData(printStream, "Cover Page  Revised: " + new SimpleDateFormat("EEEE, MMMM, dd, yyyy", Locale.ENGLISH).format(new Date()));
        fileStreamUtil.writeData(printStream, "D10 DIAVEL&Carnage CML/RKL          Revision: X0A");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "Bill Of Materials       " + new SimpleDateFormat("MMMM, dd,yyyy      hh:mm:ss", Locale.ENGLISH).format(new Date()) + "	Page1");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "Item	HH PN	STD PN	Description	Supplier	Supplier PN	Qty	Location	CCL	Remark	BOM	Package Type	Side	Sub System	Function");
        fileStreamUtil.writeData(printStream, "______________________________________________");
        fileStreamUtil.writeData(printStream, "");
    }

    /**
     * 创建数据集
     * 
     * @return
     */
    public void linkDataSet(TCComponent itemRevComp, String fileFullPath, String fileName) throws TCException
    {
        try
        {
            session.enableBypass(true);
            TCComponentItemRevision itemRev = (TCComponentItemRevision) itemRevComp;
            TCComponentDataset findDataSet = TCUtil.findDataSet((TCComponentItemRevision) itemRevComp, fileFullPath.split("\\\\")[fileFullPath.split("\\\\").length - 1]);
            if (findDataSet != null)
            {
                itemRev.remove("IMAN_specification", findDataSet);
            }
            TCComponentDataset dataSet = TCUtil.createDataSet(session, fileFullPath, "Text", fileName, "Text");
            if (itemRevComp instanceof TCComponentItemRevision)
            {
                itemRev.add("IMAN_specification", dataSet);
            }
        }
        finally
        {
            session.enableBypass(false);
        }
    }

    @Override
    public void run()
    {
        // 数据没有问题，进行处理
        System.out.println("炸 Bom 搭建BOM 开始处理 !! ");
        inputs.parallelStream().forEach(inputBean -> {
            try
            {
                List<BOMLineBean> rebomLineBeans = extractBOMLine(bomLineBeans, inputBean.getBomNo(), Phase.valueOf(inputBean.getPhase()));
                TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent(PCBA_Muti_TYPE);
                TCComponentItem pcebMutiItem = itemType.find(inputBean.getPcbNumber());
                if (pcebMutiItem == null)
                {
                    pcebMutiItem = itemType.create(inputBean.getPcbNumber(), "", PCBA_Muti_TYPE, getPCBAMultiItemName(inputBean), "", null);
                    rootBomLine.getItemRevision().add(PCBA_Muti_LINK, pcebMutiItem.getLatestItemRevision());
                }
                else
                {
                    TCComponentItemRevision pcbaMutilItemRev = pcebMutiItem.getLatestItemRevision();
                    TCComponent[] bomviews = pcbaMutilItemRev.getRelatedComponents("structure_revisions");
                    for (int k = 0; k < bomviews.length; k++)
                    {
                        TCComponent bomView = bomviews[k].getReferenceProperty("bom_view");
                        bomView.clearCache();
                        bomView.refresh();
                        bomView.delete();
                    }
                }
                createBom(pcebMutiItem.getLatestItemRevision(), rebomLineBeans);
            }
            catch (Exception e)
            {
                exceptionDataMap.put(rootBomLine, e.getCause().getMessage());
                e.printStackTrace();
            }
        });
        System.out.println("炸 Bom  执行完 !! ");
        System.out.println("exceptionDataMap -->>>>>  " + exceptionDataMap);
        if (exceptionDataMap.size() > 0)
        {
            MessageBox.post("Error", "explode bom error！", MessageBox.ERROR);
        }
        else
        {
            MessageBox.post("Info", "explode bom complete！", MessageBox.INFORMATION);
        }
    }
    // for (InputInfoBean inputBean : inputs)
    // {
    // List<BOMLineBean> rebomLineBeans = extractBOMLine(bomLineBeans, inputBean.getBomNo(), Phase.valueOf(inputBean.getPhase()));
    // TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent(PCBA_Muti_TYPE);
    // TCComponentItem pcebMutiItem = itemType.find(inputBean.getPcbNumber());
    // if (pcebMutiItem == null)
    // {
    // pcebMutiItem = itemType.create(inputBean.getPcbNumber(), "", PCBA_Muti_TYPE, getPCBAMultiItemName(inputBean), "", null);
    // rootPCBA.add(PCBA_Muti_LINK, pcebMutiItem.getLatestItemRevision());
    // }
    // else
    // {
    // TCComponentItemRevision pcbaMutilItemRev = pcebMutiItem.getLatestItemRevision();
    // TCComponent[] bomviews = pcbaMutilItemRev.getRelatedComponents("structure_revisions");
    // for (int k = 0; k < bomviews.length; k++)
    // {
    // TCComponent bomView = bomviews[k].getReferenceProperty("bom_view");
    // bomView.clearCache();
    // bomView.refresh();
    // bomView.delete();
    // }
    // // pcba muti item 存在 先删除视图
    // }
    // createBom(pcebMutiItem.getLatestItemRevision(), rebomLineBeans);
    // }
}
