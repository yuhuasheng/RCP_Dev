package com.foxconn.electronics.managementebom.export.changelist.prt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.foxconn.electronics.managementebom.export.ExcelUtil;
import com.foxconn.electronics.managementebom.export.changelist.ChangeAction;
import com.foxconn.electronics.managementebom.export.changelist.ChangeListHandle;
import com.foxconn.electronics.managementebom.export.changelist.MergedRegionInfo;
import com.foxconn.electronics.managementebom.updatebom.domain.EBOMLineBean;
import com.foxconn.electronics.managementebom.updatebom.domain.EBOMUpdateBean;
import com.foxconn.electronics.managementebom.updatebom.service.CompareBOM;
import com.foxconn.electronics.managementebom.updatebom.service.UpdateEBOMService;
import com.foxconn.electronics.util.PartBOMUtils;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;

/**
 * 
 * @author Robert
 *
 */
public class PrtChangeHandle
{
    private EBOMLineBean            sourceBomBean;
    private EBOMLineBean            targetBomBean;
    private List<PrtChangeListBean> prtChangeList;

    public PrtChangeHandle(TCComponentItemRevision sourceItemRev, TCComponentItemRevision targetItemRev) throws TCException, InterruptedException, ExecutionException
    {
        FutureTask<EBOMLineBean> nT = getTopBOMLineTask(sourceItemRev);
        FutureTask<EBOMLineBean> oT = getTopBOMLineTask(targetItemRev);
        sourceBomBean = nT.get();
        targetBomBean = oT.get();
        if (targetBomBean == null || sourceBomBean == null)
        {
            throw new RuntimeException("error data !!");
        }
        prtChangeList = new ArrayList<>();
    }

    public String getExcelFileName() throws TCException
    {
        LocalDateTime localTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String fileName = sourceBomBean.getItem() + "_changeList_" + dtf.format(localTime) + "_.xlsx";
        return fileName;
    }

    /**
     * 
     * Robert 2022年6月6日
     * 
     * @param changeSheet
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public Workbook writeExcel(PrtChangeSheet changeSheet) throws Exception
    {
        ExcelUtil util = new ExcelUtil();
        Workbook wb = util.getWorkbook(changeSheet.TEMPLATE);
        if (prtChangeList.size() > 0)
        {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 0; i < prtChangeList.size(); i++)
            {
                int rowLine = changeSheet.START_ROW + i;
                PrtChangeListBean pl = prtChangeList.get(i);
                List<MergedRegionInfo> mergedList = pl.getMergedRegionInfos();
                if (mergedList != null)
                {
                    for (MergedRegionInfo meInfo : mergedList)
                    {
                        util.setMerged(meInfo, PrtChangeListBean.class, sheet, rowLine);
                    }
                }
            }
            util.setCellValue(prtChangeList, changeSheet.START_ROW, sheet, util.getCellStyle(wb));
        }
        return wb;
    }

    public List<PrtChangeListBean> getChangeList()
    {
        return this.prtChangeList;
    }

    public void compareBOM2nd()
    {
        sourceBomBean.getChilds().forEach(this::convertUid);
        targetBomBean.getChilds().forEach(this::convertUid);
        CompareBOM result = new CompareBOM(targetBomBean, sourceBomBean);
        prtChangeList.addAll(convert2ndBean(result.getAdd(), ChangeAction.Add));
        prtChangeList.addAll(convert2ndBean(result.getDel(), ChangeAction.Delete));
        List<EBOMUpdateBean> changeList = result.getChange2nd();
        for (EBOMUpdateBean eBean : changeList)
        {
            prtChangeList.addAll(eBean.getAdd2nd().stream().map(e -> new PrtChangeListBean(e, true, ChangeAction.Add)).collect(Collectors.toList()));
            prtChangeList.addAll(eBean.getDel2nd()
                                      .stream()
                                      .map(e -> new PrtChangeListBean(e, true, ChangeAction.Delete))
                                      .collect(Collectors.toList()));
        }
    }

    public void compareBOM()
    {
        List<EBOMLineBean> sourceBody = convert(sourceBomBean);
        List<EBOMLineBean> targetBody = convert(targetBomBean);
        EBOMLineBean sBean = new EBOMLineBean();
        sBean.setChilds(sourceBody);
        EBOMLineBean tBean = new EBOMLineBean();
        tBean.setChilds(targetBody);
        CompareBOM result = new CompareBOM(tBean, sBean);
        addChangeMainSource(result.getAdd(), ChangeAction.Add);
        addChangeMainSource(result.getDel(), ChangeAction.Delete);
        List<EBOMUpdateBean> changeBeans = result.getLocationChange();
        for (EBOMUpdateBean eBean : changeBeans)
        {
            if (eBean.getChangeFiledNames().size() > 0)
            {
                PrtChangeListBean addCBean = new PrtChangeListBean(eBean.getNewEBomBean(), false, ChangeAction.Add);
                addCBean.setMergedRegionInfos(Collections.singletonList(new MergedRegionInfo("changeDes", 1)));
                PrtChangeListBean delCBean = new PrtChangeListBean(eBean.getOldEBomBean(), false, ChangeAction.Delete);
                prtChangeList.add(addCBean);
                prtChangeList.add(delCBean);
            }
        }
        compareBOM2nd();
    }

    private void addChangeMainSource(List<EBOMLineBean> list, ChangeAction action)
    {
        for (EBOMLineBean ebomBean : list)
        {
            prtChangeList.add(new PrtChangeListBean(ebomBean, false, action));
        }
    }

    private List<PrtChangeListBean> convert2ndBean(List<EBOMLineBean> list, ChangeAction action)
    {
        return list.stream()
                   .map(EBOMLineBean::getSecondSource)
                   .filter(Objects::nonNull)
                   .flatMap(Collection::stream)
                   .map(e -> new PrtChangeListBean(e, true, action))
                   .collect(Collectors.toList());
    }

    private FutureTask<EBOMLineBean> getTopBOMLineTask(TCComponentItemRevision itemRev) throws TCException
    {
        FutureTask<EBOMLineBean> fTask = new FutureTask<EBOMLineBean>(() -> ChangeListHandle.getTopBOMLine(itemRev));
        new Thread(fTask).start();
        return fTask;
    }

    private List<EBOMLineBean> convert(EBOMLineBean bomBean)
    {
        return bomBean.getChilds().parallelStream().map(this::convertByLocation).flatMap(Stream::of).collect(Collectors.toList());
    }

    private void convertUid(EBOMLineBean bean)
    {
        bean.setUid(bean.getBomId());
        if (bean.getSecondSource() != null)
        {
            bean.getSecondSource().forEach(this::convertUid);
        }
    }

    private EBOMLineBean[] convertByLocation(EBOMLineBean bomBean)
    {
        try
        {
            String location = bomBean.getLocation();
            if (!TCUtil.isNull(location))
            {
                String locations[] = location.split(",");
                EBOMLineBean[] beans = new EBOMLineBean[locations.length];
                for (int i = 0; i < locations.length; i++)
                {
                    String newLocation = locations[i];
                    beans[i] = bomBean.clone();
                    beans[i].setLocation(newLocation);
                    beans[i].setUid(newLocation);
                }
                return beans;
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return new EBOMLineBean[0];
    }

    public static void main(String[] args) throws InvalidFormatException, IOException
    {
    }
}
