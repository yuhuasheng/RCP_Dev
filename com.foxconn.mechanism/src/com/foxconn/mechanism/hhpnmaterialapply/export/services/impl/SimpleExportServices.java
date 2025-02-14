package com.foxconn.mechanism.hhpnmaterialapply.export.services.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.foxconn.mechanism.hhpnmaterialapply.export.domain.DataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.ExcelConfig;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.SheetData;
import com.foxconn.mechanism.hhpnmaterialapply.export.services.IExportServices;
import com.foxconn.mechanism.hhpnmaterialapply.export.util.ExcelUtil;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.soa.exceptions.NotLoadedException;

/**
 * 
 * @author Robert
 *
 */
public abstract class SimpleExportServices implements IExportServices
{
    private ExcelConfig              excelConfig;
    private String                   projectName;
    private TCComponentBOMLine       topBOMLine;
    private List<TCComponentBOMLine> boms;
    private List<DataModel>          finishDataList = new ArrayList<DataModel>();

    public void setExcelConfig(ExcelConfig excelConfig)
    {
        this.excelConfig = excelConfig;
    }

    public abstract DataModel newDataModel();

    @Override
    public void setBOMLines(TCComponentBOMLine topBOMLine, List<TCComponentBOMLine> boms) throws NotLoadedException, TCException
    {
        this.topBOMLine = topBOMLine;
        this.boms = boms;
        this.projectName = IExportServices.getProjectName(topBOMLine);
        if (this.projectName == null || this.projectName.isEmpty())
        {
            this.projectName = "NULL";
        }
    }

    public TCComponentBOMLine getTopBOMLine()
    {
        return topBOMLine;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public List<DataModel> getTOPDataModel() throws Exception
    {
        DataModel model = newDataModel();
        IExportServices.tcPropMapping(model, topBOMLine, "BOMLine");
        model.setItemRevision(topBOMLine.getItemRevision());
        return getRepresentations(model);
    }

    abstract public void setExcelHeader(Sheet sheet);

    abstract public List<SheetData> splitData(List<DataModel> finishDataList) throws TCException;

    abstract public CellStyle getCellStyle(Workbook wb);

    @Override
    public void generatePartsExcel(File targeFile) throws Exception
    {
        ExcelUtil excelUtil = new ExcelUtil();
        Workbook wb = excelUtil.getWorkbook(excelConfig.getExcel_template());
        CellStyle cellStyle = getCellStyle(wb);
        if (finishDataList != null)
        {
            List<SheetData> sheetDatas = splitData(finishDataList);
            for (SheetData sheetData : sheetDatas)
            {
                List<DataModel> dataModelList = sheetData.getDatas();
                Sheet sheet = wb.getSheetAt(sheetData.getIndex());
                if (sheetData.getName() != null)
                {
                    wb.setSheetName(sheetData.getIndex(), sheetData.getName());
                }
                setExcelHeader(sheet);
                for (int i = 0; i < dataModelList.size(); i++)
                {
                    int rowIndex = i + excelConfig.getStartRow();
                    Row newRow = sheet.createRow(rowIndex);
                    for (int c = 0; c < excelConfig.getColLength(); c++)
                    {
                        Cell cell = newRow.createCell(c);
                        cell.setCellStyle(cellStyle);
                    }
                    DataModel bean = dataModelList.get(i);
                    bean = modifyModelBean(bean);
                    excelUtil.setCellValue(bean, newRow);
                    File imageFile = IExportServices.getModelJPEG(bean.getItemRevision());
                    if (imageFile != null)
                    {
                        newRow.setHeightInPoints(excelConfig.getRowHeight());
                        excelUtil.insertImg(wb, sheet, imageFile, excelConfig.getImageCol(), rowIndex);
                    }
                }
            }
        }
        OutputStream out = new FileOutputStream(targeFile);
        wb.write(out);
        out.flush();
        out.close();
    }

    public DataModel modifyModelBean(DataModel bean)
    {
        return bean;
    }

    public String elseEmpty(String str)
    {
        if (str == null || str.length() == 0)
        {
            return "1";
        }
        return str;
    }

    /**
     * 
     * Robert 2022年4月13日
     * 
     * @return
     * @throws Exception
     */
    public List<DataModel> mergeBOM() throws Exception
    {
        if (boms != null)
        {
            List<DataModel> list = new ArrayList<DataModel>();
            for (TCComponentBOMLine bomLine : boms)
            {
                DataModel bean = newDataModel();
                IExportServices.tcPropMapping(bean, bomLine, "BOMLine");
                bean.setItemRevision(bomLine.getItemRevision());
                list.add(bean);
            }
            List<DataModel> merge = new ArrayList<DataModel>();
            merge.addAll(list.stream().collect(Collectors.toMap(k -> k.getItemRevision(), v -> v, (n, o) -> {
                int usage = Integer.parseInt(elseEmpty(n.getUsage())) + Integer.parseInt(elseEmpty(o.getUsage()));
                n.setUsage("" + usage);
                return n;
            })).values());
            return merge;
        }
        return null;
    }

    /**
     * 
     * Robert 2022年4月13日
     * 
     * @param model
     * @return
     * @throws Exception
     */
    public List<DataModel> getRepresentations(DataModel model) throws Exception
    {
        TCComponentItemRevision itemRev = model.getItemRevision();
        List<DataModel> reps = new ArrayList<DataModel>();
        if (itemRev != null)
        {
            List<TCComponentItemRevision> repList = IExportServices.getItemRevByRepresentation(itemRev);
            if (repList != null)
            {
                for (TCComponentItemRevision repRev : repList)
                {
                    if (repRev != null)
                    {
                        reps.add(IExportServices.tcPropMapping((DataModel) model.clone(), repRev, ""));
                    }
                }
            }
        }
        return reps;
    }

    @Override
    public void dataHandle() throws Exception
    {
        List<DataModel> dataList = mergeBOM();
        if (dataList != null)
        {
            this.finishDataList.addAll(getTOPDataModel());
            this.finishDataList.addAll(dataList.stream().map(e -> {
                try
                {
                    return getRepresentations(e);
                }
                catch (Exception e1)
                {
                    e1.printStackTrace();
                }
                return null;
            }).filter(e -> e.size() > 0).flatMap(e -> e.stream()).collect(Collectors.toList()));
        }
    }
}
