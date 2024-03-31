package com.hh.tools.importBOM.dialog;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.gson.Gson;
import com.hh.tools.importBOM.entity.MatrixBom;
import com.hh.tools.newitem.Utils;
import com.hh.tools.util.ExcelUtil;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

/**
 * ����Matrix Bom
 * 
 * @author Leo
 */
public class ImportMatrixBOMDialog extends UploadFileCommonDialog {

    private TCSession session;

    private List<MatrixBom> bomList;

    private List<MatrixBom> addBomList;

    private List<MatrixBom> updateBomList;

    private JRadioButton jbCon = new JRadioButton("Consumer");

    private JRadioButton jbCom = new JRadioButton("Commercial");

    public ImportMatrixBOMDialog(AbstractAIFApplication app, TCSession session) throws Exception {
        super("Import Matrix BOM");
        this.session = session;
        super.initUI();
    }

    private static void getFileData(File uploadFile) {
        Workbook book;
        MatrixBom currentLevel1Bom = null;
        MatrixBom currentLevel2Bom = null;
        MatrixBom currentLevel3Bom = null;
        MatrixBom currentLevel4Bom = null;
        MatrixBom currentLevel5Bom = null;
        MatrixBom bom;
        try {
            book = Utils.getWorkbook(uploadFile);
            Sheet sheet = book.getSheet("Program Matrix");
            Iterator<Row> rows = sheet.rowIterator();
            Row row;
            boolean isPartCell = false;

            while (rows.hasNext()) {
                row = rows.next();
                if (ExcelUtil.isRowEmpty(row)) {
                    continue;
                }

                String cellValue = ExcelUtil.getCellValue(row.getCell(0));
                if (cellValue != null && cellValue.contains("Configuration Manager")) {
                    currentLevel1Bom = new MatrixBom();
                    currentLevel1Bom.setDescription(ExcelUtil.getCellValue(row.getCell(1)));
                    continue;
                }

                if (cellValue != null && cellValue.contains("KMAT Part Number")) {
                    currentLevel1Bom.setDescription(cellValue.split(":")[1].trim());
                    continue;
                }

                if (cellValue != null && cellValue.contains("Project Code")) {
                    continue;
                }

                if (cellValue != null && cellValue.contains("Config Code")) {
                    continue;
                }

                if (cellValue != null && cellValue.contains("Manufacturing Comments")) {
                    isPartCell = true;
                    continue;
                }

                if (isPartCell) {
                    String desc = ExcelUtil.getCellValue(row.getCell(2)).trim();
                    if (Utils.isNull(desc)) {
                        continue;
                    }
                    bom = new MatrixBom();
                    bom.setDescription(desc);
                    String qty = ExcelUtil.getCellValue(row.getCell(8)).trim();
                    if (!Utils.isNull(qty)) {
                        bom.setQty(Double.valueOf(qty).intValue());
                    }

                    bom.setSapRev(ExcelUtil.getCellValue(row.getCell(9)).trim());
                    String partNum = ExcelUtil.getCellValue(row.getCell(3)).trim();
                    if (!Utils.isNull(partNum)) {
                        bom.setPartNum(partNum);
                        setChildBom(currentLevel1Bom, bom);
                        currentLevel2Bom = bom;
                        continue;
                    }

                    partNum = ExcelUtil.getCellValue(row.getCell(4)).trim();
                    if (!Utils.isNull(partNum)) {
                        bom.setPartNum(partNum);
                        setChildBom(currentLevel2Bom, bom);
                        currentLevel3Bom = bom;
                        continue;
                    }

                    partNum = ExcelUtil.getCellValue(row.getCell(5)).trim();
                    if (!Utils.isNull(partNum)) {
                        bom.setPartNum(partNum);
                        setChildBom(currentLevel3Bom, bom);
                        currentLevel4Bom = bom;
                        continue;
                    }

                    partNum = ExcelUtil.getCellValue(row.getCell(6)).trim();
                    if (!Utils.isNull(partNum)) {
                        bom.setPartNum(partNum);
                        setChildBom(currentLevel4Bom, bom);
                        currentLevel5Bom = bom;
                        continue;
                    }

                    partNum = ExcelUtil.getCellValue(row.getCell(7)).trim();
                    if (!Utils.isNull(partNum)) {
                        bom.setPartNum(partNum);
                        setChildBom(currentLevel5Bom, bom);
                        continue;
                    }
                }

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(new Gson().toJson(currentLevel1Bom));
    }

    private static void setChildBom(MatrixBom parentBom, MatrixBom childBom) {
        List<MatrixBom> childBomList = parentBom.getChildBomList();
        if (childBomList == null) {
            childBomList = new ArrayList<>();
            childBomList.add(childBom);
            parentBom.setChildBomList(childBomList);
        } else {
            childBomList.add(childBom);
        }
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
        return isBomError;
    }

    @Override
    protected void addComponseToFormPanel() {
    	// ��Form����� ����������
        if (null != super.formPanel) {
            JPanel panel = new JPanel();
            ButtonGroup group = new ButtonGroup();
            panel.add(this.jbCon);
            panel.add(this.jbCom);
            group.add(this.jbCon);
            group.add(this.jbCom);
            JLabel typeLable = new JLabel("File Type:");
            formPanel.add("2.1.right.center.preferred.preferred", typeLable);
            formPanel.add("2.2.left.center.preferred.preferred", panel);
        }
    }

    public static void main(String[] args) throws Exception {
        String pathName = "C:/Users/Leo/Desktop/KMAT SuperBOM Sample.xlsx";
        File file = new File(pathName);
        getFileData(file);
    }


}
