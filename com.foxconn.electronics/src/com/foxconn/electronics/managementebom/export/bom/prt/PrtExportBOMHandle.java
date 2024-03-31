package com.foxconn.electronics.managementebom.export.bom.prt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.foxconn.electronics.managementebom.export.ExcelUtil;
import com.foxconn.electronics.managementebom.updatebom.domain.EBOMLineBean;
import com.foxconn.electronics.managementebom.updatebom.service.UpdateEBOMService;
import com.foxconn.electronics.util.PartBOMUtils;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;

/**
 * 
 * @author Robert
 *
 */
public class PrtExportBOMHandle
{
    private TCComponentItemRevision itemVev;
    private EBOMLineBean            bomLineBean;

    public PrtExportBOMHandle(TCComponentBOMLine topBomLine) throws TCException
    {
        this.itemVev = topBomLine.getItemRevision();
        bomLineBean = new UpdateEBOMService().getBOMStruct(topBomLine);
    }

    public PrtExportBOMHandle(TCComponentItemRevision itemVev) throws TCException
    {
        this.itemVev = itemVev;
        TCComponentBOMWindow bomWindow = PartBOMUtils.createBomWindow(itemVev);
        bomLineBean = new UpdateEBOMService().getBOMStruct(bomWindow.getTopBOMLine());
        bomWindow.close();
    }

    public String getExcelFileName() throws TCException
    {
        LocalDateTime localTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String fileName = bomLineBean.getItem() + "_" + itemVev.getProperty("current_revision_id") + "_" + dtf.format(localTime) + "_.xlsx";
        return fileName;
    }

    public PrtSheetBean getSheetBean() throws TCException
    {
        PrtSheetBean sheetBean = new PrtSheetBean();
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        sheetBean.setDate(dtf.format(localDate));
        sheetBean.setProjectName(getProjectName());
        sheetBean.setVersion(itemVev.getProperty("current_revision_id"));
        sheetBean.setPcba(bomLineBean.getDescription());
        sheetBean.setPartNumber(bomLineBean.getItem());
        return sheetBean;
    }

    private String getProjectName() throws TCException
    {
        TCProperty props = itemVev.getTCProperty("project_list");
        if (props != null)
        {
            TCComponent[] pjs = props.getReferenceValueArray();
            return Stream.of(pjs).map(e -> ((TCComponentProject) e).getProjectName()).collect(Collectors.joining(","));
        }
        return "";
    }

    public List<PrtBomBean> getBOMBeanList()
    {
        List<PrtBomBean> bomList = new ArrayList<PrtBomBean>();
        List<EBOMLineBean> boms = bomLineBean.getChilds();
        for (EBOMLineBean ebomBean : boms)
        {
            PrtBomBean prtBomBean = new PrtBomBean(ebomBean, false);
            bomList.add(prtBomBean);
            List<EBOMLineBean> subsList = ebomBean.getSecondSource();
            if (subsList != null)
            {
                for (EBOMLineBean subsBean : subsList)
                {
                    PrtBomBean subsPrtBomBean = new PrtBomBean(subsBean, true);
                    // subsPrtBomBean.setLocation(prtBomBean.getLocation());
                    // subsPrtBomBean.setQty(prtBomBean.getQty());
                    // subsPrtBomBean.setPackageType(prtBomBean.getPackageType());
                    bomList.add(subsPrtBomBean);
                }
            }
        }
        return bomList;
    }

    public void writeFile(File file)
    {
        ExcelUtil excelUtil = new ExcelUtil();
        try (Workbook wb = excelUtil.getWorkbook(PrtSheetBean.TEMPLATE); FileOutputStream outStream = new FileOutputStream(file);)
        {
            PrtSheetBean sheetBean = this.getSheetBean();
            Sheet sheet = wb.getSheetAt(0);
            excelUtil.setRowCellVaule(sheetBean, sheet, null);
            CellStyle cellStyle = ExcelUtil.getCellStyle(wb);
            excelUtil.setCellValue(this.getBOMBeanList(), sheetBean.getStart(), sheet, cellStyle);
            wb.write(outStream);
            outStream.flush();
        }
        catch (Exception e)
        {
        }
    }
}
