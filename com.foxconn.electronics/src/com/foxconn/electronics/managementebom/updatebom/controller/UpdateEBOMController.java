package com.foxconn.electronics.managementebom.updatebom.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.foxconn.electronics.convertebom.ShowDeriveModelDialog;
import com.foxconn.electronics.domain.Constants;
import com.foxconn.electronics.managementebom.createDCN.DCNService;
import com.foxconn.electronics.managementebom.export.ExcelUtil;
import com.foxconn.electronics.managementebom.export.bom.prt.PrtExportBOMHandle;
import com.foxconn.electronics.managementebom.export.bom.prt.PrtSheetBean;
import com.foxconn.electronics.managementebom.export.changelist.ChangeListHandle;
import com.foxconn.electronics.managementebom.export.changelist.mnt.MntChangeHandle;
import com.foxconn.electronics.managementebom.export.changelist.mnt.MntChangeSheet;
import com.foxconn.electronics.managementebom.export.changelist.prt.PrtChangeHandle;
import com.foxconn.electronics.managementebom.export.changelist.prt.PrtChangeSheet;
import com.foxconn.electronics.managementebom.updatebom.domain.EBOMChangeBean;
import com.foxconn.electronics.managementebom.updatebom.domain.EBOMLineBean;
import com.foxconn.electronics.managementebom.updatebom.service.UpdateEBOMService;
import com.foxconn.electronics.util.HttpUtil;
import com.foxconn.electronics.util.PartBOMUtils;
import com.foxconn.electronics.util.TCUtil;
import com.foxconn.tcutils.util.AjaxResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.plm.tc.httpService.jhttp.GetMapping;
import com.plm.tc.httpService.jhttp.HttpRequest;
import com.plm.tc.httpService.jhttp.HttpResponse;
import com.plm.tc.httpService.jhttp.PostMapping;
import com.plm.tc.httpService.jhttp.RequestMapping;
import com.plm.tc.httpService.jhttp.RequestParam;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentManager;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.pse.operations.RevertAllOperation;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.strong.Dataset;
import com.teamcenter.soa.exceptions.NotLoadedException;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;

/**
 * 
 * @author Robert
 *
 */
@RequestMapping("/electronics")
public class UpdateEBOMController
{
    private TCComponentBOMLine      rootLine;
    private UpdateEBOMService       updateEBOMService;
    private EBOMLineBean            rootBean;
    private TCComponentBOMWindow    bomWindow;
    private AbstractPSEApplication  app;
    private String                  bu;
    private String                  userBu;
    private TCComponentItemRevision dcn;
    private TCComponentTask         dcnTask;
    private EBOMLineBean            sourceBean;
    private TCComponentItemRevision sourceItemRev;

    public UpdateEBOMController(AbstractPSEApplication app) throws Exception
    {
        this.app = app;
        this.bomWindow = app.getBOMWindow();
        this.updateEBOMService = new UpdateEBOMService();
    }

    @GetMapping("/getSingleEBOMStruct")
    public AjaxResult getSingleEBOMStruct(@RequestParam("bomuid") String bomuid)
    {
        try
        {
            this.rootLine = bomWindow.getTopBOMLine();
            EBOMLineBean bom = null;
            if (TCUtil.isNull(bomuid))
            {
                bom = updateEBOMService.getSingleBOMStruct(rootLine);
                try
                {
                    this.bu = TCUtil.findBUNameByProject(rootLine.getItem());
                    this.userBu = updateEBOMService.getBuByGroup();
                    bom.setUserBu(userBu);
                    if (this.userBu.equalsIgnoreCase(Constants.MNT))
                    {
                        TCComponentItemRevision itemRev = rootLine.getItemRevision();
                        TCComponentTask task = updateEBOMService.getItemProcessTask(itemRev);
                        if (task != null)
                        {
                            bom.setIsBOMViewWFTask("BOM Team Review".equalsIgnoreCase(task.getParent()
                                                                                          .toString()) && "FXN31_MNT BOM CoWork Process".equalsIgnoreCase(task.getParent().getParent().toString()));
                        }
                        String partType = itemRev.getTypeObject().getName();
                        if ("D9_EE_PCBARevision".equalsIgnoreCase(partType))
                        {
                            bom.setIsNewVersion(false);
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                TCComponentBOMLine bLine = (TCComponentBOMLine) RACUIUtil.getTCSession().getComponentManager().getTCComponent(bomuid);
                bom = updateEBOMService.getSingleBOMStruct(bLine);
            }
            bom.setIsNewVersion(updateEBOMService.isNewRevsion(rootLine.getItemRevision()));
            this.updateEBOMService.setMaxFindNumMap(bom);
            return AjaxResult.success(bom);
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "系統繁忙，請稍後再試");
    }

    public boolean isSignOffNode(TCComponentTask task, String wfName, String[] nodeName) throws TCException
    {
        String signOffWfName = task.getParent().toString();
        String signOffNodeName = task.toString();
        boolean isContaisNode = ArrayUtil.contains(nodeName, signOffNodeName);
        if (isContaisNode && wfName.equalsIgnoreCase(signOffWfName))
        {
            return true;
        }
        return false;
    }

    @GetMapping("/getEBOMStruct")
    public AjaxResult getEBOMStructBySOA()
    {
        try
        {
            long startTime = System.currentTimeMillis();
            this.rootLine = bomWindow.getTopBOMLine();
            rootLine.refresh();
            this.rootBean = updateEBOMService.getBOMStructBySOA(bomWindow);
            System.out.println("all cast time ::  " + (System.currentTimeMillis() - startTime) + " (ms)");
            this.updateEBOMService.setMaxFindNumMap(rootBean);
            return AjaxResult.success(rootBean);
        }
        catch (Exception e)
        {
            return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
        }
    }

    // @GetMapping("/getEBOMStruct")
    public AjaxResult getEBOMStruct()
    {
        try
        {
            long startTime = System.currentTimeMillis();
            this.rootLine = bomWindow.getTopBOMLine();
            System.out.println("refresh bom cast time ::  " + (System.currentTimeMillis() - startTime));
            rootLine.refresh();
            UpdateEBOMService.unpackageBOMStructure(rootLine);
            rootLine.refresh();
            updateEBOMService.loadAllProperties(rootLine, updateEBOMService.ALL_PART_ATTRS, updateEBOMService.ALL_BOM_ATTRS);
            this.rootBean = updateEBOMService.getBOMStruct(rootLine);
            rootBean.setIsNewVersion(updateEBOMService.isNewRevsion(rootLine.getItemRevision()));
            try
            {
                this.bu = TCUtil.findBUNameByProject(rootLine.getItem());
                this.userBu = updateEBOMService.getBuByGroup();
                rootBean.setUserBu(userBu);
                // if (this.userBu.equalsIgnoreCase(Constants.MNT))
                {
                    // c0103270
                    TCComponentItemRevision itemRev = rootLine.getItemRevision();
                    List<TCComponentItemRevision> dcns = updateEBOMService.getDCNItemRev(itemRev);
                    boolean isDCNEdit = false;
                    if (dcns.size() > 0)
                    {
                        dcn = dcns.get(dcns.size() - 1);
                        if (dcn != null)
                        {
                            for (TCComponentItemRevision d : dcns)
                            {
                                if (!TCUtil.isReleased(d))
                                {
                                    rootBean.setIsCanDcn(Boolean.FALSE);
                                    break;
                                }
                            }
                            TCComponentTask task = updateEBOMService.getItemProcessTask(itemRev);
                            if (task != null)
                            {
                                dcnTask = task.getParent().getParent();
                                isDCNEdit = updateEBOMService.judgeUpdateDcn(task);
                            }
                        }
                    }
                    boolean isBOMCoWorkEdit = false;
                    TCComponentTask task = updateEBOMService.getItemProcessTask(itemRev);
                    if (task != null)
                    {
                        if (task.getParent().getParent() != null)
                        {
                            task = task.getParent();
                        }
                        boolean isFXN65EEReview = isSignOffNode(task, "FXN65_MNT L5 BOM ECN Process", new String[] { "Check BOM" });
                        boolean isFXN31EEReview = isSignOffNode(task, "FXN31_MNT BOM CoWork Process", new String[] { "2-EE Review" });
                        boolean isF61L10Edit = isSignOffNode(task, "FXN61_MNT L10 EBOM DCN Initial Release Process", new String[] { "會簽（CE/PAC/ME/Panel CE/EE/PI）", "BOM Team Review" });
                        boolean isF67Edit = isSignOffNode(task, "FXN67_MNT PA EBOM DCN Release Process", new String[] { "BOM Team Review" });
                        boolean isF31Edit = "BOM Team Review".equalsIgnoreCase(task.toString()) && "FXN31_MNT BOM CoWork Process".equalsIgnoreCase(task.getParent()
                                                                                                                                                       .toString());
                        boolean isF46Edit = "FXN46_MNT L6-EBOM(衍生機種) Release Process".equalsIgnoreCase(task.getParent()
                                                                                                           .toString()) && ("1-CE Review".equalsIgnoreCase(task.toString()) || "4-BOMTeam Review".equalsIgnoreCase(task.getParent()
                                                                                                                                                                                                                       .toString()));
                        isBOMCoWorkEdit = isF31Edit || isF46Edit || isF61L10Edit || isF67Edit || isFXN65EEReview;
                        rootBean.setShowDifference(isFXN31EEReview);
                    }
                    else
                    {
                        // 没有发起流程之前 PSU.R&D.Monitor.D_Group EE.R&D.Monitor.D_Group 要可以编辑
                        ModelObject[] wfModelObject = updateEBOMService.getWfRelation(itemRev);
                        if (wfModelObject.length == 0 && updateEBOMService.isEditDept())
                        {
                            rootBean.setCanEditPIEE(true);
                        }
                    }
                    rootBean.setIsBOMViewWFTask(isDCNEdit || isBOMCoWorkEdit);
                    // rootBean.setIsBOMViewWFTask(true); // 测试 测试
                    String partType = itemRev.getTypeObject().getName();
                    if ("D9_EE_PCBARevision".equalsIgnoreCase(partType))
                    {
                        rootBean.setIsNewVersion(false);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            this.updateEBOMService.setMaxFindNumMap(rootBean);
            System.out.println("all cast time ::  " + (System.currentTimeMillis() - startTime));
            return AjaxResult.success(rootBean);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "系統繁忙，請稍後再試");
    }

    @GetMapping("/getSourceItem")
    public AjaxResult getSourceItem()
    {
        try
        {
            if (this.sourceItemRev == null)
            {
                this.sourceItemRev = updateEBOMService.getSourceItemRev(this.rootLine.getItemRevision());
            }
            if (sourceItemRev != null)
            {
                Map<String, String> pcaMap = new HashMap<>();
                pcaMap.put("modelName", sourceItemRev.getProperty("d9_ModelName"));
                pcaMap.put("chineseDescription", sourceItemRev.getProperty("d9_ChineseDescription"));
                pcaMap.put("englishDescription", sourceItemRev.getProperty("d9_EnglishDescription"));
                pcaMap.put("pcaPn", sourceItemRev.getProperty("item_id"));
                pcaMap.put("itemUid", sourceItemRev.getUid());
                return AjaxResult.success(pcaMap);
            }
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        return AjaxResult.error(AjaxResult.STATUS_NO_RESULT, "沒有源機種!");
    }

    @GetMapping("/getSourceBOMStruct")
    public AjaxResult getSourceBOMStruct(@RequestParam("itemUid") String itemUid)
    {
        TCComponentBOMWindow bomWindow = null;
        try
        {
            System.out.println("getSourceBOMStruct :: " + itemUid);
            TCComponentItemRevision itemRev = null;
            if (TCUtil.isNull(itemUid))
            {
                itemRev = updateEBOMService.getSourceItemRev(this.rootLine.getItemRevision());
            }
            else
            {
                itemRev = (TCComponentItemRevision) RACUIUtil.getTCSession().getComponentManager().getTCComponent(itemUid);
            }
            if (itemRev != null)
            {
                bomWindow = PartBOMUtils.createBomWindow(itemRev);
                TCComponentBOMLine topBomLine = bomWindow.getTopBOMLine();
                topBomLine = bomWindow.getTopBOMLine();
                topBomLine.refresh();
                UpdateEBOMService.unpackageBOMStructure(topBomLine);
                topBomLine.refresh();
                updateEBOMService.loadAllProperties(topBomLine, updateEBOMService.ALL_PART_ATTRS, updateEBOMService.ALL_BOM_ATTRS);
                sourceBean = updateEBOMService.getBOMStruct(topBomLine);
                sourceBean.setIsNewVersion(true);
                sourceBean.setUserBu(this.userBu);
                sourceBean.setCanEditPIEE(false);
                sourceBean.setIsNewVersion(true);
                sourceBean.setIsCanDcn(Boolean.FALSE);
                this.updateEBOMService.setMaxFindNumMap(sourceBean);
                return AjaxResult.success(sourceBean);
            }
            else
            {
                AjaxResult.error(AjaxResult.STATUS_NO_RESULT, "沒有源機種!");
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
        return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "系統繁忙，請稍後再試");
    }

    /**
     * 
     * Robert 2022年5月23日
     * 
     * @param hhpn
     * @param mfg
     * @param mfgPn
     * @return
     */
    @GetMapping("/findMainParts")
    public AjaxResult findParts(@RequestParam("hhpn") String hhpn, @RequestParam("mfg") String mfg, @RequestParam("mfgPn") String mfgPn, @RequestParam("sourceSystem") String sourceSystem)
    {
        List<EBOMLineBean> list = null;
        if (TCUtil.isNull(hhpn) && TCUtil.isNull(mfg) && TCUtil.isNull(mfgPn))
        {
            return AjaxResult.error(AjaxResult.STATUS_PARAM_MISS, "Search condition cannot all be empty!");
        }
        try
        {
            if ("pnms".equalsIgnoreCase(sourceSystem))
            {
                hhpn = hhpn.trim();
                if (!TCUtil.isNull(hhpn))
                {
                    if (hhpn.length() == 15)
                    {
                        list = updateEBOMService.queryMainSource(hhpn, mfg, mfgPn);
                        if (list.size() == 0)
                        {
                            if (UpdateEBOMService.springCloudUrl == null)
                            {
                                UpdateEBOMService.springCloudUrl = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
                            }
                            String result = HttpUtil.httpGet(UpdateEBOMService.springCloudUrl + "/tc-integrate/pnms/getHHPNInfo", "hhpn=" + hhpn, 20000);
                            if (result.length() > 0)
                            {
                                // if (Constants.MNT.equalsIgnoreCase(bu))
                                // {
                                // char[] hhcs = hhpn.toCharArray();
                                // hhcs[14] = 'H';
                                // hhpn = new String(hhcs);
                                // }
                                Gson gson = new Gson();
                                Map<String, String> resultMap = gson.fromJson(result, Map.class);
                                EBOMLineBean bean = new EBOMLineBean();
                                bean.setItem(hhpn);
                                bean.setMfg(resultMap.get("mfg"));
                                bean.setMfgPn(resultMap.get("mfgpn"));
                                bean.setDescription(resultMap.get("des"));
                                bean.setSourceSystem("pnms");
                                list.add(bean);
                            }
                        }
                    }
                    else
                    {
                        return AjaxResult.error(AjaxResult.STATUS_PARAM_ERROR, "Query from pnms can only be an exact query. The Foxconn part number length must be 15 yards. !");
                    }
                }
                else if (!TCUtil.isNull(mfg) || !TCUtil.isNull(mfgPn))
                {
                    list = updateEBOMService.queryMainSource(hhpn, mfg, mfgPn);
                    if (list.size() == 0 && !TCUtil.isNull(mfgPn))
                    {
                        if (UpdateEBOMService.springCloudUrl == null)
                        {
                            UpdateEBOMService.springCloudUrl = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
                        }
                        String result = HttpUtil.httpGet(UpdateEBOMService.springCloudUrl + "/tc-integrate/pnms/getHHPNInfoByMfg", "mfgPn=" + mfgPn + "&mfg=" + mfg, 20000);
                        if (result.length() > 0)
                        {
                            Gson gson = new Gson();
                            List<Map<String, String>> resultList = gson.fromJson(result, List.class);
                            for (Map<String, String> resultMap : resultList)
                            {
                                hhpn = resultMap.get("hhpn");
                                if (Constants.MNT.equalsIgnoreCase(bu))
                                {
                                    char[] hhcs = hhpn.toCharArray();
                                    hhcs[14] = 'H';
                                    hhpn = new String(hhcs);
                                }
                                EBOMLineBean bean = new EBOMLineBean();
                                bean.setItem(hhpn);
                                bean.setMfg(resultMap.get("mfg"));
                                bean.setMfgPn(resultMap.get("mfgpn"));
                                bean.setDescription(resultMap.get("des"));
                                bean.setSourceSystem("pnms");
                                list.add(bean);
                            }
                        }
                    }
                }
            }
            else
            {
                list = updateEBOMService.queryMainSource(hhpn, mfg, mfgPn);
            }
            return AjaxResult.success(list);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "find parts fail!");
    }

    @PostMapping("/submitBOM")
    public AjaxResult submitBOM(HttpRequest req) throws TCException
    {
        String json = req.getBody();
        Gson gson = new Gson();
        EBOMLineBean newRootBean = gson.fromJson(json, EBOMLineBean.class);
        com.foxconn.mechanism.util.TCUtil.setBypass(RACUIUtil.getTCSession());
        try
        {
            TCComponentItemRevision revItemOld = rootLine.getItemRevision();
            TCComponentBOMLine newLine = updateEBOMService.updateBOM(rootLine, newRootBean, rootBean, true);
            updateEBOMService.updateEERelation(revItemOld, newLine.getItemRevision());
            bomWindow.save();
            this.rootBean = newRootBean;
        }
        catch (Exception e)
        {
            // RevertAllCommand r = new RevertAllCommand(app);
            RevertAllOperation var2 = new RevertAllOperation(app);
            var2.executeOperation();
            long oldTime = System.currentTimeMillis();
            while (bomWindow.isModified())
            {
                var2.executeOperation();
                if (System.currentTimeMillis() - oldTime > 500)
                {
                    break;
                }
            }
            e.printStackTrace();
            com.foxconn.mechanism.util.TCUtil.closeBypass(RACUIUtil.getTCSession());
            return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "BOM update failed ," + e.getLocalizedMessage());
        }
        try
        {
            updateEBOMService.updateItems(newRootBean);
            List<TCComponentItemRevision> dcnDatas = new ArrayList<>(updateEBOMService.getUpdatedBOMs());
            dcnDatas.addAll(updateEBOMService.getUpdatedParts());
            if (dcn != null && !TCUtil.isReleased(dcn))
            {
                DCNService.updateDCN(dcn, dcnTask, dcnDatas);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "Item Property update failed," + e.getLocalizedMessage());
        }
        finally
        {
            bomWindow.refresh();
            com.foxconn.mechanism.util.TCUtil.closeBypass(RACUIUtil.getTCSession());
        }
        return AjaxResult.success();
    }

    @PostMapping("/saveBOM")
    public AjaxResult saveBOM(HttpRequest req) throws TCException
    {
        String json = req.getBody();
        Gson gson = new Gson();
        EBOMChangeBean newRootBean = gson.fromJson(json, EBOMChangeBean.class);
        com.foxconn.mechanism.util.TCUtil.setBypass(RACUIUtil.getTCSession());
        try
        {
            // TCComponentItemRevision revItemOld = rootLine.getItemRevision();
            // updateEBOMService.updateEERelation(revItemOld, newLine.getItemRevision());
            bomWindow.save();
        }
        catch (Exception e)
        {
            // RevertAllCommand r = new RevertAllCommand(app);
            RevertAllOperation var2 = new RevertAllOperation(app);
            var2.executeOperation();
            long oldTime = System.currentTimeMillis();
            while (bomWindow.isModified())
            {
                var2.executeOperation();
                if (System.currentTimeMillis() - oldTime > 500)
                {
                    break;
                }
            }
            e.printStackTrace();
            com.foxconn.mechanism.util.TCUtil.closeBypass(RACUIUtil.getTCSession());
            return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "BOM update failed ," + e.getLocalizedMessage());
        }
        try
        {
            // updateEBOMService.updateItems(newRootBean);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "Item Property update failed," + e.getLocalizedMessage());
        }
        finally
        {
            bomWindow.refresh();
            com.foxconn.mechanism.util.TCUtil.closeBypass(RACUIUtil.getTCSession());
        }
        return AjaxResult.success();
    }

    @GetMapping("/getDeriveInfo")
    public AjaxResult getDeriveInfo()
    {
        try
        {
            Object[] derives = UpdateEBOMService.getDeriveItemsByPca(rootLine.getItemRevision());
            return AjaxResult.success(derives);
        }
        catch (TCException e)
        {
            e.printStackTrace();
            return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
        }
    }

    @GetMapping("/findVersions")
    public AjaxResult findVersion()
    {
        try
        {
            TCComponentItem item = rootLine.getItem();
            TCComponent[] itemRevs = item.getRelatedComponents("revision_list");
            List list = new ArrayList();
            for (TCComponent itemRev : itemRevs)
            {
                Map<String, String> map = new HashMap<String, String>();
                String version = itemRev.getProperty("current_revision_id");
                String uid = itemRev.getUid();
                map.put("uid", uid);
                map.put("version", version);
                list.add(map);
            }
            return AjaxResult.success(list);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
        }
    }

    @GetMapping("/getOtherEBOM")
    public AjaxResult getOtherBOM(@RequestParam("uid") String uid)
    {
        try
        {
            TCComponentItemRevision itemVev = (TCComponentItemRevision) RACUIUtil.getTCSession().getComponentManager().getTCComponent(uid);
            EBOMLineBean otherBOMBean = ChangeListHandle.getMNTTopBOMLine(itemVev);
            // otherBOMBean.setVersion(itemVev.getProperty("current_revision_id"));
            return AjaxResult.success(otherBOMBean);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, e.getLocalizedMessage());
        }
    }

    @GetMapping("/downloadPrtChange")
    public void downLoadPrtChangeList(@RequestParam("sourceRevUid") String sourceRevUid, @RequestParam("targetRevUid") String targetRevUid, HttpResponse resp)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String startTime = sdf.format(new Date());
            TCComponentManager tcMgr = RACUIUtil.getTCSession().getComponentManager();
            TCComponentItemRevision sourceItemVev = (TCComponentItemRevision) tcMgr.getTCComponent(sourceRevUid);
            TCComponentItemRevision targetItemVev = (TCComponentItemRevision) tcMgr.getTCComponent(targetRevUid);
            PrtChangeHandle prtHandle = new PrtChangeHandle(sourceItemVev, targetItemVev);
            prtHandle.compareBOM();
            Workbook wb = prtHandle.writeExcel(new PrtChangeSheet());
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            wb.write(outStream);
            wb.close();
            resp.addHeader("Content-Disposition", "attachment;filename=" + prtHandle.getExcelFileName());
            resp.out(outStream.toByteArray());
            outStream.close();
            String endTime = sdf.format(new Date());
            updateEBOMService.writeLog(sourceItemVev, startTime, endTime, "EBOM比對效率");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @GetMapping("/downloadMntChange")
    public void downLoadMntChangeList(@RequestParam("sourceRevUid") String sourceRevUid, @RequestParam("targetRevUid") String targetRevUid, HttpResponse resp)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String startTime = sdf.format(new Date());
            TCComponentManager tcMgr = RACUIUtil.getTCSession().getComponentManager();
            TCComponentItemRevision sourceItemVev = (TCComponentItemRevision) tcMgr.getTCComponent(sourceRevUid);
            TCComponentItemRevision targetItemVev = (TCComponentItemRevision) tcMgr.getTCComponent(targetRevUid);
            MntChangeHandle mntHandle = new MntChangeHandle(sourceItemVev, targetItemVev);
            // mntHandle.writeDCNExcel(new FileOutputStream("d://testDCNChange.xlsx"),
            // null); // 测试
            // MntDCNChange.generateMNTDCNChange((TCComponentItemRevision)
            // tcMgr.getTCComponent("RLuN2Zp2Z5OtvC")); // 测试
            mntHandle.compareBOM();
            MntChangeSheet mntSheet = new MntChangeSheet();
            mntSheet.setModel(targetItemVev.getProperty("d9_ModelName"));
            Workbook wb = mntHandle.writeExcel(mntSheet);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            wb.write(outStream);
            wb.close();
            resp.addHeader("Content-Disposition", "attachment;filename=" + mntHandle.getExcelFileName());
            resp.out(outStream.toByteArray());
            outStream.close();
            String endTime = sdf.format(new Date());
            updateEBOMService.writeLog(sourceItemVev, startTime, endTime, "EBOM比對效率");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @GetMapping("/writeActionLog")
    public AjaxResult writeActionLog(@RequestParam("revUid") String revUid, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime, @RequestParam("func") String func)
    {
        try
        {
            TCComponentManager tcMgr = RACUIUtil.getTCSession().getComponentManager();
            TCComponentItemRevision itemRev = (TCComponentItemRevision) tcMgr.getTCComponent(revUid);
            updateEBOMService.writeLog(itemRev, startTime, endTime, func);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return AjaxResult.success();
    }

    @GetMapping("/downloadPrtEBOM")
    public void downLoadPrtEBOM(HttpRequest req, HttpResponse resp)
    {
        try
        {
            String uid = req.get("uid");
            TCComponentItemRevision itemVev = (TCComponentItemRevision) RACUIUtil.getTCSession().getComponentManager().getTCComponent(uid);
            PrtExportBOMHandle prtExportBOMHandle = new PrtExportBOMHandle(itemVev);
            PrtSheetBean sheetBean = prtExportBOMHandle.getSheetBean();
            ExcelUtil excelUtil = new ExcelUtil();
            Workbook wb = excelUtil.getWorkbook(PrtSheetBean.TEMPLATE);
            Sheet sheet = wb.getSheetAt(0);
            excelUtil.setRowCellVaule(sheetBean, sheet, null);
            CellStyle cellStyle = ExcelUtil.getCellStyle(wb);
            excelUtil.setCellValue(prtExportBOMHandle.getBOMBeanList(), sheetBean.getStart(), sheet, cellStyle);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            wb.write(outStream);
            wb.close();
            resp.addHeader("Content-Disposition", "attachment;filename=" + prtExportBOMHandle.getExcelFileName());
            resp.out(outStream.toByteArray());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @GetMapping("/getMaxFindNum")
    public AjaxResult getMaxNum(@RequestParam("itemRevUid") String itemRevUid)
    {
        try
        {
            int[] max = updateEBOMService.getMaxFindNum(itemRevUid);
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put("findNum", max[0]);
            map.put("altGroup", max[1]);
            return AjaxResult.success(map);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, "系統繁忙，請稍後再試");
    }

    @GetMapping("/mountChangeList")
    public AjaxResult mountChangeListToWf(HttpResponse resp)
    {
        String msg = null;
        TCComponentTask task = null;
        try
        {
            task = updateEBOMService.getItemProcessTask(rootLine.getItemRevision());
        }
        catch (Exception e1)
        {
            msg = e1.getLocalizedMessage();
            e1.printStackTrace();
        }
        if (task != null)
        {
            boolean isFXN31EEReview = false;
            try
            {
                isFXN31EEReview = isSignOffNode(task.getParent(), "FXN31_MNT BOM CoWork Process", new String[] { "2-EE Review" });
            }
            catch (TCException e1)
            {
                msg = e1.getLocalizedMessage();
                e1.printStackTrace();
            }
            if (isFXN31EEReview)
            {
                if (sourceBean == null)
                {
                    sourceBean = (EBOMLineBean) getSourceBOMStruct(null).get(AjaxResult.DATA_TAG);
                }
                if (sourceBean != null)
                {
                    File file = null;
                    InputStream fileIn = null;
                    ByteArrayOutputStream outStream = null;
                    try
                    {
                        file = updateEBOMService.addTargetAttachments(task, sourceBean, rootBean);
                        // fileIn = new FileInputStream(file);
                        // outStream = new ByteArrayOutputStream();
                        // IoUtil.copy(fileIn, outStream);
                        // resp.addHeader("Content-Disposition", "attachment;filename=" + "Difference.xlsx");
                        // resp.out(outStream.toByteArray());
                        return AjaxResult.success();
                    }
                    catch (Exception e)
                    {
                        msg = e.getLocalizedMessage();
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (fileIn != null)
                        {
                            try
                            {
                                fileIn.close();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        if (outStream != null)
                        {
                            try
                            {
                                outStream.close();
                            }
                            catch (IOException e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        if (file != null && file.exists())
                        {
                            file.delete();
                        }
                    }
                }
                else
                {
                    msg = "源機種數據為空！";
                }
            }
            else
            {
                msg = " 必須在 2-EE Review 任務節點才可以操作!";
            }
        }
        else
        {
            msg = "必須在 FXN31_MNT BOM CoWork Process 流程的 2-EE Review 任務節點才可以操作!";
        }
        return AjaxResult.error(AjaxResult.STATUS_SERVER_ERROR, msg);
    }

    @GetMapping("/judgeEditBOM")
    public AjaxResult judgeEditBySourceBom(@RequestParam("pItems") String pItems) throws TCException
    {
        System.out.println("judgeEditBySourceBom pItems :: " + pItems);
        if (this.sourceItemRev == null)
        {
            this.sourceItemRev = updateEBOMService.getSourceItemRev(this.rootLine.getItemRevision());
        }
        if (sourceItemRev != null)
        {
            try
            {
                String[] parentPns = pItems.split(",");
                if (parentPns.length == 1)
                {
                    return AjaxResult.success("Y");
                }
                int index = 0;
                TCComponent[] childs = sourceItemRev.getReferenceListProperty("ps_children");
                while (index < parentPns.length)
                {
                    index++;
                    boolean flag = true;
                    for (TCComponent com : childs)
                    {
                        String itemId = com.getProperty("item_id");
                        if (itemId.equalsIgnoreCase(parentPns[index]))
                        {
                            if (index + 1 == parentPns.length)
                            {
                                return AjaxResult.success("N");
                            }
                            else
                            {
                                childs = com.getReferenceListProperty("ps_children");
                            }
                            flag = false;
                            break;
                        }
                    }
                    if (flag)
                    {
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return AjaxResult.success("N");
            }
        }
        else
        {
            System.out.println("no soruce bom !");
        }
        return AjaxResult.success("Y");
    }
}
