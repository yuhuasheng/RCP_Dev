package com.foxconn.mechanism.hhpnmaterialapply.export.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.DataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.ExcelConfig;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.PRTDataModel;
import com.foxconn.mechanism.hhpnmaterialapply.export.domain.SheetData;
import com.foxconn.mechanism.hhpnmaterialapply.export.services.IExportServices;
import com.foxconn.mechanism.hhpnmaterialapply.export.util.ExcelUtil;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;

/**
 * 
 * @author Robert
 *
 */
public class PRTExportServices extends SimpleExportServices
{
    private static final String   EXCEL_TEMPLATE = "com/foxconn/mechanism/hhpnmaterialapply/export/template/PRTPartList.xlsx";
    private final static int      IMAGECOL       = 11;
    private final static int      STARTROW       = 1;
    private final static int      COLLENGTH      = 21;
    private final static int      ROWHEIGHT      = 160;
    private static final String   GROUP_1        = "^ME-PL01-.*";
    private static final String   GROUP_2        = "^ME-SM01-.*";
    private static final String   GROUP_OTHER    = "other";
    private static final String   GROUP_4        = "^ME-SCRW-.*";
    private static final String   GROUP_5        = "^SE-CB\\S{2}-.*";
    private static final String[] GROUPS         = { GROUP_1, GROUP_2, GROUP_OTHER, GROUP_4, GROUP_5 };

    public PRTExportServices()
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
        return new PRTDataModel();
    }

    @Override
    public void setExcelHeader(Sheet sheet)
    {
        // TODO Auto-generated method stub
    }

    public List<DataModel> fillingModel(List<DataModel> models)
    {
        for (int k = 0; k < models.size(); k++)
        {
            PRTDataModel prtModel = (PRTDataModel) models.get(k);
            prtModel.setItem((k + 1) + "");
            prtModel.setUsage(elseEmpty(prtModel.getUsage()));
            prtModel.setProjectName(getProjectName());
            try
            {
            	if (prtModel.getItemRevision().getProperty("item_id").equals("ME-PL01-02972")) {
					System.out.println(123);
				}
                IExportServices.tcPropMapping(prtModel, prtModel.getItemRevision(), "MEDesign");
            }
            catch (IllegalArgumentException | IllegalAccessException | TCException e)
            {
                e.printStackTrace();
            }
        }
        return models;
    }

    /**
     * 
     * Robert 2022年4月13日
     * 
     * @param finishDataList
     * @return
     * @throws TCException
     */
    @Override
    public List<SheetData> splitData(List<DataModel> finishDataList) throws TCException
    {
        List<SheetData> sheetDataList = new ArrayList<SheetData>();
        Map<Object, List<DataModel>> groupMap = groupingByItemID(finishDataList);
        System.out.println("groupMap:: " + groupMap);
        for (int i = 0; i < GROUPS.length; i++)
        {
            List<DataModel> models = groupMap.get(GROUPS[i]);
            if (models != null)
            {
                SheetData sheetData = new SheetData();
                sheetData.setIndex(i);
                sheetData.setDatas(fillingModel(models));
                sheetDataList.add(sheetData);
            }
        }
        return sheetDataList;
    }

    /**
     * 
     * Robert 2022年4月13日
     * 
     * @param finishDataList
     * @return
     * @throws TCException
     */
    public Map<Object, List<DataModel>> groupingByItemID(List<DataModel> finishDataList) throws TCException
    {
        Map<Object, List<DataModel>> map = new HashMap<Object, List<DataModel>>();
        for (DataModel model : finishDataList)
        {
            TCComponentItemRevision itemRev = model.getItemRevision();
            String itemID = itemRev.getProperty("item_id");
            boolean flag = false;
            for (int i = 0; i < GROUPS.length; i++)
            {
                if (itemID.matches(GROUPS[i]))
                {
                    if (map.containsKey(GROUPS[i]))
                    {
                        map.get(GROUPS[i]).add(model);
                    }
                    else
                    {
                        List<DataModel> dataList = new ArrayList<DataModel>();
                        dataList.add(model);
                        map.put(GROUPS[i], dataList);
                    }
                    flag = true;
                    continue;
                }
            }
            if (!flag)
            {
                if (map.containsKey(GROUPS[2]))
                {
                    map.get(GROUPS[2]).add(model);
                }
                else
                {
                    List<DataModel> dataList = new ArrayList<DataModel>();
                    dataList.add(model);
                    map.put(GROUPS[2], dataList);
                }
            }
        }
        return map;
    }

    @Override
    public CellStyle getCellStyle(Workbook wb)
    {
        return ExcelUtil.getCellStyle(wb);
    }
}
