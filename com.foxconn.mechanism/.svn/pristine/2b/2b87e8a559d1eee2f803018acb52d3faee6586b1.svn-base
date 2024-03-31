package com.foxconn.mechanism.hhpnmaterialapply.export.services.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.DataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.ExcelConfig;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.MNTDataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.PRTDataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.SheetData;
import com.foxconn.mechanism.hhpnmaterialapply.export.services.IExportServices;
import com.foxconn.mechanism.hhpnmaterialapply.export.util.ExcelUtil;

/**
 * 
 * @author Robert
 *
 */
public class MNTExportServices extends SimpleExportServices
{
    private static final String EXCEL_TEMPLATE = "com/foxconn/mechanism/hhpnmaterialapply/export/template/MNTPartList.xlsx";
    private final static int    IMAGECOL       = 5;
    private final static int    STARTROW       = 6;
    private final static int    COLLENGTH      = 17;
    private final static int    ROWHEIGHT      = 160;

    public MNTExportServices()
    {
        ExcelConfig excelConfig = new ExcelConfig();
        excelConfig.setColLength(COLLENGTH);
        excelConfig.setExcel_template(EXCEL_TEMPLATE);
        excelConfig.setImageCol(IMAGECOL);
        excelConfig.setStartRow(STARTROW);
        excelConfig.setRowHeight(ROWHEIGHT);
        setExcelConfig(excelConfig);
    }

    @Override
    public DataModel newDataModel()
    {
        return new MNTDataModel();
    }

    @Override
    public DataModel modifyModelBean(DataModel bean)
    {
        if (bean instanceof MNTDataModel)
        {
            MNTDataModel mntBean = (MNTDataModel) bean;
            String itemId = mntBean.getItemId();
            if (itemId != null)
            {
                if (itemId.contains("@"))
                {
                    mntBean.setBupn(itemId);
                    mntBean.setHhpn("");
                }
                else
                {
                    mntBean.setBupn("");
                    mntBean.setHhpn(itemId);
                }
            }
            return mntBean;
        }
        return bean;
    }

    @Override
    public CellStyle getCellStyle(Workbook wb)
    {
        CellStyle style = ExcelUtil.getCellStyle(wb);
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }

    public void setExcelHeader(Sheet sheet)
    {
        Row r2 = sheet.getRow(2);
        Cell c20 = r2.getCell(0);
        Cell c26 = r2.getCell(6);
        Cell c29 = r2.getCell(9);
        c20.setCellValue("Project Name：" + getProjectName());
        c26.setCellValue(" Prepared by：");
        c29.setCellValue("Date：" + IExportServices.formatNowDate("yyyy/MM/dd"));
    }

    public List<DataModel> fillingModel(List<DataModel> models)
    {
        for (int k = 0; k < models.size(); k++)
        {
            MNTDataModel prtModel = (MNTDataModel) models.get(k);
            prtModel.setUsage(elseEmpty(prtModel.getUsage()));
        }
        return models;
    }

    @Override
    public List<SheetData> splitData(List<DataModel> finishDataList)
    {
        List<SheetData> list = new ArrayList<SheetData>();
        SheetData sheetData = new SheetData();
        sheetData.setDatas(fillingModel(finishDataList));
        sheetData.setIndex(0);
        sheetData.setName(getProjectName());
        list.add(sheetData);
        return list;
    }
}
