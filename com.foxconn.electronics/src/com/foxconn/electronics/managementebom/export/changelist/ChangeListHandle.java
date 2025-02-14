package com.foxconn.electronics.managementebom.export.changelist;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

import com.foxconn.electronics.managementebom.updatebom.domain.EBOMLineBean;
import com.foxconn.electronics.managementebom.updatebom.domain.EBOMUpdateBean;
import com.foxconn.electronics.managementebom.updatebom.service.CompareBOM;
import com.foxconn.electronics.managementebom.updatebom.service.UpdateEBOMService;
import com.foxconn.electronics.util.PartBOMUtils;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;

public class ChangeListHandle
{
    protected EBOMLineBean       sourceBomBean;
    protected EBOMLineBean       targetBomBean;
    private List<EBOMLineBean>   adds    = new ArrayList<EBOMLineBean>();
    private List<EBOMLineBean>   dels    = new ArrayList<EBOMLineBean>();
    private List<EBOMUpdateBean> changes = new ArrayList<EBOMUpdateBean>();

    public ChangeListHandle(EBOMLineBean sourceBomBean, EBOMLineBean targetBomBean)
    {
        this.sourceBomBean = sourceBomBean;
        this.targetBomBean = targetBomBean;
        if (targetBomBean == null || sourceBomBean == null)
        {
            throw new RuntimeException("error data !!");
        }
    }

    public ChangeListHandle(TCComponentItemRevision sourceItemRev, TCComponentItemRevision targetItemRev) throws Exception
    {
        FutureTask<EBOMLineBean> nT = getTopBOMLineTask(sourceItemRev);
        FutureTask<EBOMLineBean> oT = getTopBOMLineTask(targetItemRev);
        sourceBomBean = nT.get();
        targetBomBean = oT.get();
        if (targetBomBean == null || sourceBomBean == null)
        {
            throw new RuntimeException("error data !!");
        }
        convertUid(sourceBomBean.getChilds());
        convertUid(targetBomBean.getChilds());
    }

    public List<EBOMLineBean> getAdd()
    {
        return adds;
    }

    public List<EBOMLineBean> getDel()
    {
        return dels;
    }

    public List<EBOMUpdateBean> getChange()
    {
        return changes;
    }

    public void compareBOM()
    {
        compareBOM(sourceBomBean, targetBomBean);
    }

    private void compareBOM(EBOMLineBean sourceBomBean, EBOMLineBean targetBomBean)
    {
        CompareBOM result = new CompareBOM(targetBomBean, sourceBomBean);
        adds.addAll(result.getAdd());
        dels.addAll(result.getDel());
        List<EBOMUpdateBean> beans = result.getChangeQty();
        List<EBOMUpdateBean> changeBeans = beans.stream().filter(e -> e.getChangeFiledNames().size() > 0).collect(Collectors.toList());
        changes.addAll(changeBeans);
        for (EBOMUpdateBean changeBean : beans)
        {
            compareBOM(changeBean.getOldEBomBean(), changeBean.getNewEBomBean());
        }
    }

    public static EBOMLineBean getSingeBOMStruct(TCComponentItemRevision itemRev) throws TCException
    {
        TCComponentBOMWindow bomWindow = PartBOMUtils.createBomWindow(itemRev);
        TCComponentBOMLine topBomLine = bomWindow.getTopBOMLine();
        try
        {
            UpdateEBOMService.unpackageBOMStructure(topBomLine);
            topBomLine.refresh();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        EBOMLineBean bomLineBean = new UpdateEBOMService().getSingleBOMStruct(topBomLine);
        bomWindow.close();
        return bomLineBean;
    }

    public static EBOMLineBean getTopBOMLine(TCComponentItemRevision itemRev) throws TCException
    {
        TCComponentBOMWindow bomWindow = null;
        if (TCUtil.isReleased(itemRev))
        {
            bomWindow = PartBOMUtils.createBomWindowBySnapshot(itemRev);
        }
        if (bomWindow == null)
        {
            bomWindow = PartBOMUtils.createBomWindow(itemRev);
        }
        TCComponentBOMLine topBomLine = bomWindow.getTopBOMLine();
        try
        {
            UpdateEBOMService.unpackageBOMStructure(topBomLine);
            topBomLine.refresh();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        EBOMLineBean bomLineBean = new UpdateEBOMService().getBOMStruct(topBomLine);
        bomWindow.close();
        return bomLineBean;
    }

    public static EBOMLineBean getMNTTopBOMLine(TCComponentItemRevision itemRev) throws TCException
    {
        TCComponentBOMWindow bomWindow = null;
        if (TCUtil.isReleased(itemRev))
        {
            bomWindow = PartBOMUtils.createBomWindowBySnapshot(itemRev);
        }
        if (bomWindow == null)
        {
            bomWindow = PartBOMUtils.createBomWindow(itemRev);
        }
        TCComponentBOMLine topBomLine = bomWindow.getTopBOMLine();
        try
        {
            UpdateEBOMService.unpackageBOMStructure(topBomLine);
            topBomLine.refresh();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        EBOMLineBean bomLineBean = new UpdateEBOMService().getBOMStructSingleSnapshot(topBomLine);
        bomWindow.close();
        return bomLineBean;
    }

    private FutureTask<EBOMLineBean> getTopBOMLineTask(TCComponentItemRevision itemRev) throws TCException
    {
        FutureTask<EBOMLineBean> fTask = new FutureTask<EBOMLineBean>(() -> getSingeBOMStruct(itemRev));
        new Thread(fTask).start();
        return fTask;
    }

    public String getExcelFileName() throws TCException
    {
        LocalDateTime localTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String fileName = sourceBomBean.getItem() + "_changeList_" + dtf.format(localTime) + "_.xlsx";
        return fileName;
    }

    protected void convertUid(List<EBOMLineBean> beans)
    {
        if (beans != null)
        {
            beans.forEach(bean -> {
                bean.setUid(bean.getBomId());
                if (!TCUtil.isNull(bean.getReferenceDimension()))
                {
                    bean.setUid(bean.getBomId() + bean.getLocation());
                }
                convertUid(bean.getSecondSource());
                convertUid(bean.getChilds());
            });
        }
    }
}
