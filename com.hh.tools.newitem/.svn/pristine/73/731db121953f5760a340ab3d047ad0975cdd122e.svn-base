package com.hh.tools.importBOM.dialog;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.gson.Gson;
import com.hh.tools.importBOM.entity.RsscSkuBom;
import com.hh.tools.importBOM.entity.RsscSkuBomPart;
import com.hh.tools.importBOM.util.RsscSkuBomCheckUtil;
import com.hh.tools.importBOM.util.TcSearchUtil;
import com.hh.tools.importBOM.util.TcSystemUtil;
import com.hh.tools.newitem.Utils;
import com.hh.tools.util.ExcelUtil;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

/**
 * 导入RSSC SKU BOM
 * 
 * @author Leo
 */
public class ImportRsscSkuBOMDialog extends UploadFileCommonDialog {

    private TCSession session;

    private List<RsscSkuBom> bomList;

    private List<RsscSkuBom> addBomList;

    private List<RsscSkuBom> updateBomList;

    // 搜索L10SKUPartRevision对象名称
    private final String SEARCH_L10_SKU_PART_NAME = "__FX8_Search_L10SKUPart";
    private final String[] SEARCH_KEY = new String[]{"鸿海料号"};

    public ImportRsscSkuBOMDialog(AbstractAIFApplication app, TCSession session) throws Exception {
        super("Import RSSC SKU BOM");
        this.session = session;
        super.initUI();
    }

    private void getFileData(File uploadFile) {
        bomList = new ArrayList<>();
        Workbook book;
        try {
            book = Utils.getWorkbook(uploadFile);
            Sheet sheet = book.getSheetAt(0);
            Iterator<Row> rows = sheet.rowIterator();
            Row row;
            boolean isPartCell = false;
            RsscSkuBom bom = null;
            while (rows.hasNext()) {
                row = rows.next();

                String cellValue = ExcelUtil.getCellValue(row.getCell(0));
                if ("SKU Product BOM".equals(cellValue)) {
                    isPartCell = false;
                    if (bom != null && bom.getMfgSite().contains("01DJ")) {
                        bomList.add(bom);
                    }
                    bom = new RsscSkuBom();
                    continue;
                }
                if ("Part Number:".equals(cellValue)) {
                    cellValue = ExcelUtil.getCellValue(row.getCell(1));
                    bom.setPartNum(cellValue);
                    continue;
                }
                if ("Revision:".equals(cellValue)) {
                    cellValue = ExcelUtil.getCellValue(row.getCell(1));
                    bom.setRevision(cellValue);
                    continue;
                }
                if ("Country:".equals(cellValue)) {
                    cellValue = ExcelUtil.getCellValue(row.getCell(1));
                    bom.setCountry(cellValue);
                    continue;
                }
                if ("Mfg Site(s):".equals(cellValue)) {
                    cellValue = ExcelUtil.getCellValue(row.getCell(1));
                    bom.setMfgSite(cellValue);
                    continue;
                }
                if ("Model Description:".equals(cellValue)) {
                    cellValue = ExcelUtil.getCellValue(row.getCell(1));
                    bom.setModelDesc(cellValue);
                    continue;
                }
                if ("UC Code:".equals(cellValue)) {
                    cellValue = ExcelUtil.getCellValue(row.getCell(1));
                    cellValue = cellValue.replace(String.valueOf((char) 160), " ");
                    cellValue = cellValue.trim();
                    bom.setUnCode(cellValue);
                    continue;
                }
                if ("Part Number".equals(cellValue)) {
                    isPartCell = true;
                    continue;
                }

                if (isPartCell) {
                    RsscSkuBomPart bomPart = new RsscSkuBomPart();
                    bomPart.setPartNum(cellValue);
                    bomPart.setPartDesc(ExcelUtil.getCellValue(row.getCell(2)));
                    bomPart.setRev(ExcelUtil.getCellValue(row.getCell(3)));
                    bomPart.setQty(Integer.parseInt(ExcelUtil.getCellValue(row.getCell(4))));
                    bomPart.setUoM(ExcelUtil.getCellValue(row.getCell(5)));
                    bomPart.setType(ExcelUtil.getCellValue(row.getCell(6)));
                    Map<String, RsscSkuBomPart> partMap = bom.getPartMap();
                    if (partMap == null) {
                        partMap = new LinkedHashMap<>();
                        partMap.put(bomPart.getPartNum(), bomPart);
                        bom.setPartMap(partMap);
                    } else {
                        RsscSkuBomPart part = partMap.get(bomPart.getPartNum());
                        if (part == null) {
                            partMap.put(bomPart.getPartNum(), bomPart);
                        } else {
                            part.setQty(part.getQty() + bomPart.getQty());
                        }
                    }
                    continue;
                }
            }
            // 处理最后一个bom结构
            if (bom != null && bom.getMfgSite().contains("01DJ")) {
                bomList.add(bom);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(new Gson().toJson(bomList));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
    	// 选择文件点击事件 打开弹框
        if (event.getSource() == super.selectFileBtn) {
            if (super.selectExcelFile("*.xls;")) {
                super.importFileBtn.setEnabled(true);
            }
        }
        // 执行上传文件
        else if (event.getSource() == super.importFileBtn) {
            new Thread(new Runnable() {
                public void run() {
                    parseFile();
                    if (!checkBom()) {
                        importBom();
                    }
                }
            }).start();
        } else if (event.getSource() == super.cancelImportBtn) {
            dispose();
        }
    }

    /**
	 * 解析文件
	 */
    private void parseFile() {
        System.out.println("----------------- UploadFileDialog parseFile start -----------------");

        // 创建上传文件
        String excelFilePath = this.filePathTxt.getText();
        File uploadFile = null;
        uploadFile = new File(excelFilePath);

        // 文件为null 或 不存在
        if ((uploadFile == null) || (!uploadFile.exists())) {
            MessageBox.post(this, "File Not Found!", "Warn", MessageBox.WARNING);
            return;
        }

        // 文件是否正在使用
        if (!uploadFile.renameTo(uploadFile)) {
            MessageBox.post(this, "File In Used!", "Warn", MessageBox.WARNING);
            return;
        }

        // 更新消息提醒域
        this.msgWarnPane.setText("");
        this.msgWarnPane.updateUI();

        // 解析数据
        getFileData(uploadFile);
    }

    private boolean checkBom() {
        boolean isBomError = false;
        RsscSkuBomCheckUtil checkUtil = new RsscSkuBomCheckUtil(session);
        addBomList = new ArrayList<>();
        updateBomList = new ArrayList<>();
        for (RsscSkuBom bom : bomList) {
            boolean isError = false;
            // 查询bom是否已存在
            String puid = checkUtil.searchRsscSkuBomPuid(bom.getPartNum());
            if (puid == null) {
                for (String partNum : bom.getPartMap().keySet()) {
                    if (checkUtil.searchRsscSkuBomPuid(bom.getPartNum()) == null) {
                        addMsgWarnContent("Material:" + partNum + "don't exist!", Color.RED, false, null);
                        isError = true;
                        isBomError = true;
                    }
                }
                if (!isError) {
                    addBomList.add(bom);
                }
            } else {
                String status = checkUtil.searchRsscSkuBomStatus(puid);
                if (status == null) {
                    addMsgWarnContent("Material:" + bom.getPartNum() + "has already existed,but it's not published ", Color.RED, false, null);
                    isError = true;
                    isBomError = true;
                } else {
                    for (String partNum : bom.getPartMap().keySet()) {
                        if (checkUtil.searchRsscSkuBomPuid(bom.getPartNum()) == null) {
                            addMsgWarnContent("Material:" + partNum + "don't exist!", Color.RED, false, null);
                            isError = true;
                            isBomError = true;
                        }
                    }
                    if (!isError) {
                        updateBomList.add(bom);
                    }
                }
            }
        }

        return isBomError;
    }

    /**
	 *  导入BOM
	 */
    private void importBom() {
        postAddBom();
        postUpdateBom();
        MessageBox.post(this, "Import BOM Over!", "Warn", MessageBox.WARNING);
    }

    /**
	 * 处理添加的BOM
	 */
    private void postAddBom() {
    	// 结构管理器
        TCComponentBOMWindow bomWindom = null;
        TCComponentBOMLine topBOMLine = null;

        TCComponentItemRevision tempParentBomItemRev = null;
        TCComponentItemRevision tempChildBomItemRev = null;
        RsscSkuBom itemRsscSkuBom = null;
        RsscSkuBomPart itemRsscSkuBomPart = null;
        Map<String, RsscSkuBomPart> tempChildPartMap = null;

        try {
            if (null != addBomList && !addBomList.isEmpty()) {
                String[] setParentPropKeys = new String[]{"fx8_PartNumber", "fx8_HHPN", "fx8_UPCCode", "fx8_LifecyclePhase"};
                String[] setChildPropKeys = new String[]{"fx8_PartNumber", "fx8_HHPN"};
                String[] setPropValues = null;

                // 处理添加SKU BOM的业务
                for (int i = 0; i < addBomList.size(); i++) {
                    itemRsscSkuBom = addBomList.get(i);
                    setPropValues = new String[]{itemRsscSkuBom.getPartNum(), itemRsscSkuBom.getPartNum(),
                            itemRsscSkuBom.getUnCode(), isEmpty(itemRsscSkuBom.getRevision()) ? "Pilot" : "Production"};
                    tempParentBomItemRev = createSKUItemRev(itemRsscSkuBom.getModelDesc(), setParentPropKeys, setPropValues);

                    tempChildPartMap = itemRsscSkuBom.getPartMap();
                    if (null != tempChildPartMap && !tempChildPartMap.isEmpty()) {
                    	// 发送结构管理器 组装BOM结构
                        bomWindom = TcSystemUtil.getComponentBOMWindow(this.session);
                        topBOMLine = bomWindom.setWindowTopLine(tempParentBomItemRev.getItem(), tempParentBomItemRev, null,
                                null);

                        // 在父结构管理器中 添加子BOM
                        for (String tempChildPartNumKey : tempChildPartMap.keySet()) {
                            itemRsscSkuBomPart = tempChildPartMap.get(tempChildPartNumKey);
                            setPropValues = new String[]{itemRsscSkuBomPart.getPartNum(), itemRsscSkuBomPart.getPartNum()};
                            tempChildBomItemRev = createSKUItemRev(itemRsscSkuBomPart.getPartDesc(), setChildPropKeys, setPropValues);
                            topBOMLine.add(tempChildBomItemRev.getItem(), tempChildBomItemRev, null, false);
                        }

                        bomWindom.save();
                        bomWindom.close();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 关闭结构管理器
            if (null != bomWindom) {
                try {
                    bomWindom.close();
                } catch (TCException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
	 * 处理更新的BOM
	 */
    private void postUpdateBom() {
    	// 结构管理器
        TCComponentBOMWindow bomWindom = null;
        TCComponentBOMLine topBOMLine = null;
        Map<String, TCComponentBOMLine> childBomLineMap = null;

        TCComponentItemRevision tempParentBomItemRev = null;
        String releaseStatusList = null;
        RsscSkuBom itemRsscSkuBom = null;

        try {
            if (null != updateBomList && !updateBomList.isEmpty()) {
            	// 处理更新SKU BOM的业务
                for (int i = 0; i < updateBomList.size(); i++) {
                    itemRsscSkuBom = updateBomList.get(i);
                    tempParentBomItemRev = TcSearchUtil.searchMaxItemRev(SEARCH_L10_SKU_PART_NAME, SEARCH_KEY,
                            new String[]{itemRsscSkuBom.getPartNum()});
                    System.out.println("物料单号 => " + itemRsscSkuBom.getPartNum() + ",搜索对象 => " + tempParentBomItemRev);
					
					// 判断父Bom是否为 发布版
                    releaseStatusList = tempParentBomItemRev.getProperty("release_status_list");
                    System.out.println("是否升版 => " + releaseStatusList);
                    if (null != releaseStatusList && !"".equals(releaseStatusList)) {
                    	// 在升版中 操作
                        String newItemRevId = TcSystemUtil.generateNewRevID(tempParentBomItemRev.getItem());
                        tempParentBomItemRev = tempParentBomItemRev.saveAs(newItemRevId);
                    }

                    // 发送结构管理器 组装BOM结构
                    bomWindom = TcSystemUtil.getComponentBOMWindow(this.session);
                    topBOMLine = bomWindom.setWindowTopLine(tempParentBomItemRev.getItem(), tempParentBomItemRev, null,
                            null);
                    childBomLineMap = getChildBomLineData(topBOMLine);
                    operBomCompare(topBOMLine, itemRsscSkuBom.getPartMap(), childBomLineMap);
                    bomWindom.save();
                    bomWindom.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 关闭结构管理器
            if (null != bomWindom) {
                try {
                    bomWindom.close();
                } catch (TCException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
	 * 操作导入文件BOM和系统Bom之间的差异
	 * 
	 * @param topBOMLine      顶层Bom
	 * @param childPartMap    导入文件BOM
	 * @param childBomLineMap 系统BOM
	 * @throws Exception
	 */
    private void operBomCompare(TCComponentBOMLine topBOMLine, Map<String, RsscSkuBomPart> childPartMap,
                                Map<String, TCComponentBOMLine> childBomLineMap) throws Exception {
        if (null == childPartMap) {
            childPartMap = new HashMap<String, RsscSkuBomPart>();
        }
        System.out.println("比较差异: 本地BomLine =>" + childBomLineMap.size() + ", 导入BomLine => " + childPartMap.size());

        // 比较 系统Bom 在 导入文件BOM中 不存在则移除
        TCComponentBOMLine itemBomLine = null;
        for (String tempPartNumKey : childBomLineMap.keySet()) {
            if (!childPartMap.containsKey(tempPartNumKey)) {
                itemBomLine = childBomLineMap.get(tempPartNumKey);
                System.out.println("需要删除的BomLine => " + itemBomLine);
                itemBomLine.cut();
            }
        }

        // 比较 导入文件BOM 在 系统Bom中 不存在 系统Bom中添加
        TCComponentItemRevision tempChildBomItemRev = null;
        for (String tempPartNumKey : childPartMap.keySet()) {
            if (!childBomLineMap.containsKey(tempPartNumKey)) {
            	System.out.println("需要添加的物料单号 => " + tempPartNumKey);
                tempChildBomItemRev = TcSearchUtil.searchMaxItemRev(SEARCH_L10_SKU_PART_NAME, SEARCH_KEY,
                        new String[]{tempPartNumKey});

                System.out.println("所属的本地itemRev => " + tempChildBomItemRev);
                topBOMLine.add(tempChildBomItemRev.getItem(), tempChildBomItemRev, null, false);
            }
        }
    }


    /**
	 * 根据父BOM 获取子Bom的列表
	 * @param topBOMLine
	 * @return
	 * @throws Exception
	 */
    private Map<String, TCComponentBOMLine> getChildBomLineData(TCComponentBOMLine topBOMLine) throws Exception {
    	// 子组件Map key: 鸿海料号,value: BomLine
        Map<String, TCComponentBOMLine> childBomLineMap = new HashMap<String, TCComponentBOMLine>();

        if (null != topBOMLine) {
        	// 从顶层的BOM中 获取子组件
            AIFComponentContext[] childContext = topBOMLine.getChildren();
            TCComponentItemRevision tempItemRev = null;
            if (null != childContext && childContext.length > 0) {

                TCComponentBOMLine childBOMLine = null;
                // 子组件的鸿海料号
                String childBomHHPN = null;
                for (int j = 0; j < childContext.length; j++) {
                    childBOMLine = (TCComponentBOMLine) childContext[j].getComponent();
                    tempItemRev = childBOMLine.getItemRevision();
                    childBomHHPN = tempItemRev.getProperty("fx8_PartNumber");
                    childBomLineMap.put(childBomHHPN, childBOMLine);
                }
            }
        }

        return childBomLineMap;
    }


    /**
	 *  创建LX_SKU_PartD组件对象
	 * @param itemName 组件名称
	 * @param propKeys 属性Keys
	 * @param propValues 属性值
	 * @return
	 * @throws Exception
	 */
    private TCComponentItemRevision createSKUItemRev(String itemName, String[] propKeys, String[] propValues) throws Exception {
        TCComponentItemRevision tempParentBomItemRev = TcSystemUtil.addCreateItemToNewStuffFolder("FX8_LXSKUPartD", itemName);
        if (null != propKeys && null != propValues) {
            String propKey = null;
            String propValue = null;
            for (int i = 0; i < propKeys.length; i++) {
                propKey = propKeys[i];
                propValue = propValues[i];
                tempParentBomItemRev.setProperty(propKey, propValue);
            }
        }
        return tempParentBomItemRev;
    }

    private boolean isEmpty(String str) {
        return null == str || "".equals(str);
    }
}
