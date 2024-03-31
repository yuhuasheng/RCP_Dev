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
 * ����RSSC SKU BOM
 * 
 * @author Leo
 */
public class ImportRsscSkuBOMDialog extends UploadFileCommonDialog {

    private TCSession session;

    private List<RsscSkuBom> bomList;

    private List<RsscSkuBom> addBomList;

    private List<RsscSkuBom> updateBomList;

    // ����L10SKUPartRevision��������
    private final String SEARCH_L10_SKU_PART_NAME = "__FX8_Search_L10SKUPart";
    private final String[] SEARCH_KEY = new String[]{"�躣�Ϻ�"};

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
            // �������һ��bom�ṹ
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
    	// ѡ���ļ�����¼� �򿪵���
        if (event.getSource() == super.selectFileBtn) {
            if (super.selectExcelFile("*.xls;")) {
                super.importFileBtn.setEnabled(true);
            }
        }
        // ִ���ϴ��ļ�
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
	 * �����ļ�
	 */
    private void parseFile() {
        System.out.println("----------------- UploadFileDialog parseFile start -----------------");

        // �����ϴ��ļ�
        String excelFilePath = this.filePathTxt.getText();
        File uploadFile = null;
        uploadFile = new File(excelFilePath);

        // �ļ�Ϊnull �� ������
        if ((uploadFile == null) || (!uploadFile.exists())) {
            MessageBox.post(this, "File Not Found!", "Warn", MessageBox.WARNING);
            return;
        }

        // �ļ��Ƿ�����ʹ��
        if (!uploadFile.renameTo(uploadFile)) {
            MessageBox.post(this, "File In Used!", "Warn", MessageBox.WARNING);
            return;
        }

        // ������Ϣ������
        this.msgWarnPane.setText("");
        this.msgWarnPane.updateUI();

        // ��������
        getFileData(uploadFile);
    }

    private boolean checkBom() {
        boolean isBomError = false;
        RsscSkuBomCheckUtil checkUtil = new RsscSkuBomCheckUtil(session);
        addBomList = new ArrayList<>();
        updateBomList = new ArrayList<>();
        for (RsscSkuBom bom : bomList) {
            boolean isError = false;
            // ��ѯbom�Ƿ��Ѵ���
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
	 *  ����BOM
	 */
    private void importBom() {
        postAddBom();
        postUpdateBom();
        MessageBox.post(this, "Import BOM Over!", "Warn", MessageBox.WARNING);
    }

    /**
	 * ������ӵ�BOM
	 */
    private void postAddBom() {
    	// �ṹ������
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

                // �������SKU BOM��ҵ��
                for (int i = 0; i < addBomList.size(); i++) {
                    itemRsscSkuBom = addBomList.get(i);
                    setPropValues = new String[]{itemRsscSkuBom.getPartNum(), itemRsscSkuBom.getPartNum(),
                            itemRsscSkuBom.getUnCode(), isEmpty(itemRsscSkuBom.getRevision()) ? "Pilot" : "Production"};
                    tempParentBomItemRev = createSKUItemRev(itemRsscSkuBom.getModelDesc(), setParentPropKeys, setPropValues);

                    tempChildPartMap = itemRsscSkuBom.getPartMap();
                    if (null != tempChildPartMap && !tempChildPartMap.isEmpty()) {
                    	// ���ͽṹ������ ��װBOM�ṹ
                        bomWindom = TcSystemUtil.getComponentBOMWindow(this.session);
                        topBOMLine = bomWindom.setWindowTopLine(tempParentBomItemRev.getItem(), tempParentBomItemRev, null,
                                null);

                        // �ڸ��ṹ�������� �����BOM
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
            // �رսṹ������
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
	 * ������µ�BOM
	 */
    private void postUpdateBom() {
    	// �ṹ������
        TCComponentBOMWindow bomWindom = null;
        TCComponentBOMLine topBOMLine = null;
        Map<String, TCComponentBOMLine> childBomLineMap = null;

        TCComponentItemRevision tempParentBomItemRev = null;
        String releaseStatusList = null;
        RsscSkuBom itemRsscSkuBom = null;

        try {
            if (null != updateBomList && !updateBomList.isEmpty()) {
            	// �������SKU BOM��ҵ��
                for (int i = 0; i < updateBomList.size(); i++) {
                    itemRsscSkuBom = updateBomList.get(i);
                    tempParentBomItemRev = TcSearchUtil.searchMaxItemRev(SEARCH_L10_SKU_PART_NAME, SEARCH_KEY,
                            new String[]{itemRsscSkuBom.getPartNum()});
                    System.out.println("���ϵ��� => " + itemRsscSkuBom.getPartNum() + ",�������� => " + tempParentBomItemRev);
					
					// �жϸ�Bom�Ƿ�Ϊ ������
                    releaseStatusList = tempParentBomItemRev.getProperty("release_status_list");
                    System.out.println("�Ƿ����� => " + releaseStatusList);
                    if (null != releaseStatusList && !"".equals(releaseStatusList)) {
                    	// �������� ����
                        String newItemRevId = TcSystemUtil.generateNewRevID(tempParentBomItemRev.getItem());
                        tempParentBomItemRev = tempParentBomItemRev.saveAs(newItemRevId);
                    }

                    // ���ͽṹ������ ��װBOM�ṹ
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
            // �رսṹ������
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
	 * ���������ļ�BOM��ϵͳBom֮��Ĳ���
	 * 
	 * @param topBOMLine      ����Bom
	 * @param childPartMap    �����ļ�BOM
	 * @param childBomLineMap ϵͳBOM
	 * @throws Exception
	 */
    private void operBomCompare(TCComponentBOMLine topBOMLine, Map<String, RsscSkuBomPart> childPartMap,
                                Map<String, TCComponentBOMLine> childBomLineMap) throws Exception {
        if (null == childPartMap) {
            childPartMap = new HashMap<String, RsscSkuBomPart>();
        }
        System.out.println("�Ƚϲ���: ����BomLine =>" + childBomLineMap.size() + ", ����BomLine => " + childPartMap.size());

        // �Ƚ� ϵͳBom �� �����ļ�BOM�� ���������Ƴ�
        TCComponentBOMLine itemBomLine = null;
        for (String tempPartNumKey : childBomLineMap.keySet()) {
            if (!childPartMap.containsKey(tempPartNumKey)) {
                itemBomLine = childBomLineMap.get(tempPartNumKey);
                System.out.println("��Ҫɾ����BomLine => " + itemBomLine);
                itemBomLine.cut();
            }
        }

        // �Ƚ� �����ļ�BOM �� ϵͳBom�� ������ ϵͳBom�����
        TCComponentItemRevision tempChildBomItemRev = null;
        for (String tempPartNumKey : childPartMap.keySet()) {
            if (!childBomLineMap.containsKey(tempPartNumKey)) {
            	System.out.println("��Ҫ��ӵ����ϵ��� => " + tempPartNumKey);
                tempChildBomItemRev = TcSearchUtil.searchMaxItemRev(SEARCH_L10_SKU_PART_NAME, SEARCH_KEY,
                        new String[]{tempPartNumKey});

                System.out.println("�����ı���itemRev => " + tempChildBomItemRev);
                topBOMLine.add(tempChildBomItemRev.getItem(), tempChildBomItemRev, null, false);
            }
        }
    }


    /**
	 * ���ݸ�BOM ��ȡ��Bom���б�
	 * @param topBOMLine
	 * @return
	 * @throws Exception
	 */
    private Map<String, TCComponentBOMLine> getChildBomLineData(TCComponentBOMLine topBOMLine) throws Exception {
    	// �����Map key: �躣�Ϻ�,value: BomLine
        Map<String, TCComponentBOMLine> childBomLineMap = new HashMap<String, TCComponentBOMLine>();

        if (null != topBOMLine) {
        	// �Ӷ����BOM�� ��ȡ�����
            AIFComponentContext[] childContext = topBOMLine.getChildren();
            TCComponentItemRevision tempItemRev = null;
            if (null != childContext && childContext.length > 0) {

                TCComponentBOMLine childBOMLine = null;
                // ������ĺ躣�Ϻ�
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
	 *  ����LX_SKU_PartD�������
	 * @param itemName �������
	 * @param propKeys ����Keys
	 * @param propValues ����ֵ
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
