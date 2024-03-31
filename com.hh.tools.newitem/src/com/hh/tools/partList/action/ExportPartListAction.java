package com.hh.tools.partList.action;

import java.awt.Desktop;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hh.tools.newitem.CreateObject;
import com.hh.tools.newitem.DownloadDataset;
import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.newitem.ItemTypeName;
import com.hh.tools.newitem.Utils;
import com.hh.tools.util.ExcelUtil;
import com.hh.tools.util.StateDialog;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.IRelationName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class ExportPartListAction extends AbstractAIFAction {
    private AbstractAIFUIApplication app = null;
    private TCSession session = null;
    private Registry reg = null;
    private StateDialog statDlg;
    Workbook book = null;
    private List<TCComponentItemRevision> plasticList = new ArrayList<>();
    private List<TCComponentItemRevision> sheetList = new ArrayList<>();
    private List<TCComponentItemRevision> miscList = new ArrayList<>();
    private XSSFSheet sheetMetalSheet = null;
    private XSSFSheet plasticSheet = null;
    private XSSFSheet miscSheet = null;

    public ExportPartListAction(AbstractAIFUIApplication arg0, Frame arg1, String arg2) {
        super(arg0, arg1, arg2);
        this.app = arg0;
        this.session = (TCSession) this.app.getSession();
        this.reg = Registry.getRegistry("com.hh.tools.partList.action.exportPartList");
    }

    @Override
    public void run() {
        InterfaceAIFComponent aifComponent = this.app.getTargetComponent();
        TCComponentItemRevision itemRev = null;
        try {
            if (aifComponent != null && aifComponent instanceof TCComponentBOMLine) {
                TCComponentBOMLine bomLine = (TCComponentBOMLine) aifComponent;
                itemRev = bomLine.getItemRevision();
                if (!bomLine.isRoot()) {
                    MessageBox.post(reg.getString("SelectTopBOM.MSG"),
                            reg.getString("Warn.MSG"), MessageBox.WARNING);
                    return;
                }
                recursionBOMLine(bomLine);
                //写值
                String itemId = new GetPreferenceUtil().getStringPreference(session, TCPreferenceService.TC_preference_site, "FX_Get_PartList_Template");
                TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent("Item");
                TCComponentItem item = itemType.find(itemId);
                TCComponent component = item.getLatestItemRevision().getRelatedComponent("IMAN_specification");
                if (component != null && component instanceof TCComponentDataset) {
                    TCComponentDataset dataset = (TCComponentDataset) component;
                    String filePath = DownloadDataset.downloadFile(dataset, true);
                    statDlg = new StateDialog(reg.getString("MSG"));
                    statDlg.setMessage(reg.getString("StatDlg.MSG"));
                    book = Utils.getWorkbook(new File(filePath));
                    sheetMetalSheet = (XSSFSheet) book.getSheetAt(0);
                    plasticSheet = (XSSFSheet) book.getSheetAt(1);
                    miscSheet = (XSSFSheet) book.getSheetAt(2);
                    int startRow = 3;
                    //写入sheet页
                    if (sheetList.size() > 0) {
                        for (int i = 1; i < sheetList.size(); i++) {
                            sheetMetalSheet.shiftRows(startRow + i, sheetMetalSheet.getLastRowNum() + 1, 1, true, false);
                            int newRow = startRow + i;
                            sheetMetalSheet.createRow(startRow + i);
                            ExcelUtil.copyRow(sheetMetalSheet, newRow - 1, newRow, 1);
                        }
                        for (int i = 0; i < sheetList.size(); i++) {
                            TCComponentItemRevision itemRevision = sheetList.get(i);
                            String imagePath = getImagePath(itemRevision);
                            TCComponentBOMLine currentBOMLine = transferToBOMLine(session, itemRevision);
                            sheetMetalSheet.getRow(startRow + i).getCell(0).setCellValue(i + 1);
                            sheetMetalSheet.getRow(startRow + i).getCell(1).setCellValue(itemRevision.getProperty("fx8_CustomerPN"));
                            sheetMetalSheet.getRow(startRow + i).getCell(2).setCellValue(itemRevision.getProperty("fx8_HHPN"));
                            sheetMetalSheet.getRow(startRow + i).getCell(3).setCellValue(itemRevision.getProperty("item_revision_id"));
                            sheetMetalSheet.getRow(startRow + i).getCell(4).setCellValue(itemRevision.getProperty("fx8_ObjectDesc"));
                            if (!Utils.isNull(imagePath))
                                addPic(sheetMetalSheet, imagePath, startRow + i, 5, startRow + i + 1, 6);
                            sheetMetalSheet.getRow(startRow + i).getCell(6).setCellValue(currentBOMLine.getProperty("bl_quantity"));
                            sheetMetalSheet.getRow(startRow + i).getCell(7).setCellValue(itemRevision.getProperty("fx8_MaterialType"));
                            sheetMetalSheet.getRow(startRow + i).getCell(8).setCellValue(itemRevision.getProperty("fx8_PartWeight"));
                            TCProperty property = itemRevision.getTCProperty("fx8_ProdProc");
                            TCComponent[] components = property.getReferenceValueArray();
                            StringBuffer processBuffer = new StringBuffer();
                            if (components != null && components.length > 0) {
                                for (TCComponent tcComponent : components) {
                                    String process = tcComponent.getProperty("fx8_Proc");
                                    processBuffer.append(process + "\n");
                                }
                                sheetMetalSheet.getRow(startRow + i).getCell(9).setCellValue(processBuffer.toString());
                            }
                            TCProperty postProperty = itemRevision.getTCProperty("fx8_PostProc");
                            TCComponent[] postComponents = postProperty.getReferenceValueArray();
                            StringBuffer postProcessBuffer = new StringBuffer();
                            if (postComponents != null && postComponents.length > 0) {
                                for (TCComponent tcComponent : postComponents) {
                                    String postProcess = tcComponent.getProperty("fx8_PostProc");
                                    postProcessBuffer.append(postProcess + "\n");
                                }
                                sheetMetalSheet.getRow(startRow + i).getCell(10).setCellValue(postProcessBuffer.toString());
                            }
                            sheetMetalSheet.getRow(startRow + i).getCell(11).setCellValue(itemRevision.getProperty("fx8_Remark"));
                        }
                    }
                    //写入plastic sheet页
                    if (plasticList.size() > 0) {
                        for (int i = 1; i < plasticList.size(); i++) {
                            plasticSheet.shiftRows(startRow + i, plasticSheet.getLastRowNum() + 1, 1, true, false);
                            int newRow = startRow + i;
                            plasticSheet.createRow(startRow + i);
                            ExcelUtil.copyRow(plasticSheet, newRow - 1, newRow, 1);
                        }
                        for (int i = 0; i < plasticList.size(); i++) {
                            TCComponentItemRevision itemRevision = plasticList.get(i);
                            String imagePath = getImagePath(itemRevision);
                            TCComponentBOMLine currentBOMLine = transferToBOMLine(session, itemRevision);
                            plasticSheet.getRow(startRow + i).getCell(0).setCellValue(i + 1);
                            plasticSheet.getRow(startRow + i).getCell(1).setCellValue(itemRevision.getProperty("fx8_CustomerPN"));
                            plasticSheet.getRow(startRow + i).getCell(2).setCellValue(itemRevision.getProperty("fx8_HHPN"));
                            plasticSheet.getRow(startRow + i).getCell(3).setCellValue(itemRevision.getProperty("item_revision_id"));
                            plasticSheet.getRow(startRow + i).getCell(4).setCellValue(itemRevision.getProperty("fx8_ObjectDesc"));
                            if (!Utils.isNull(imagePath))
                                addPic(plasticSheet, imagePath, startRow + i, 5, startRow + i + 1, 6);
                            plasticSheet.getRow(startRow + i).getCell(6).setCellValue(currentBOMLine.getProperty("bl_quantity"));
                            plasticSheet.getRow(startRow + i).getCell(7).setCellValue(itemRevision.getProperty("fx8_MaterialType"));
                            plasticSheet.getRow(startRow + i).getCell(8).setCellValue(itemRevision.getProperty("fx8_PartWeight"));
                            TCProperty property = itemRevision.getTCProperty("fx8_ProdProc");
                            TCComponent[] components = property.getReferenceValueArray();
                            StringBuffer processBuffer = new StringBuffer();
                            if (components != null && components.length > 0) {
                                for (TCComponent tcComponent : components) {
                                    String process = tcComponent.getProperty("fx8_Proc");
                                    processBuffer.append(process + "\n");
                                }
                                plasticSheet.getRow(startRow + i).getCell(9).setCellValue(processBuffer.toString());
                            }
                            TCProperty postProperty = itemRevision.getTCProperty("fx8_PostProc");
                            TCComponent[] postComponents = postProperty.getReferenceValueArray();
                            StringBuffer postProcessBuffer = new StringBuffer();
                            if (postComponents != null && postComponents.length > 0) {
                                for (TCComponent tcComponent : postComponents) {
                                    String postProcess = tcComponent.getProperty("fx8_PostProc");
                                    postProcessBuffer.append(postProcess + "\n");
                                }
                                plasticSheet.getRow(startRow + i).getCell(10).setCellValue(postProcessBuffer.toString());
                            }
                            plasticSheet.getRow(startRow + i).getCell(11).setCellValue(itemRevision.getProperty("fx8_Remark"));
                        }

                    }
                    //写入misc sheet页
                    if (miscList.size() > 0) {
                        for (int i = 1; i < miscList.size(); i++) {
                            miscSheet.shiftRows(startRow + i, miscSheet.getLastRowNum() + 1, 1, true, false);
                            int newRow = startRow + i;
                            miscSheet.createRow(startRow + i);
                            ExcelUtil.copyRow(miscSheet, newRow - 1, newRow, 1);
                        }
                        for (int i = 0; i < miscList.size(); i++) {
                            TCComponentItemRevision itemRevision = miscList.get(i);
                            String imagePath = getImagePath(itemRevision);
                            TCComponentBOMLine currentBOMLine = transferToBOMLine(session, itemRevision);
                            miscSheet.getRow(startRow + i).getCell(0).setCellValue(i + 1);
                            miscSheet.getRow(startRow + i).getCell(1).setCellValue(itemRevision.getProperty("fx8_CustomerPN"));
                            miscSheet.getRow(startRow + i).getCell(2).setCellValue(itemRevision.getProperty("fx8_HHPN"));
                            miscSheet.getRow(startRow + i).getCell(3).setCellValue(itemRevision.getProperty("item_revision_id"));
                            miscSheet.getRow(startRow + i).getCell(4).setCellValue(itemRevision.getProperty("fx8_ObjectDesc"));
                            if (!Utils.isNull(imagePath))
                                addPic(miscSheet, imagePath, startRow + i, 5, startRow + i + 1, 6);
                            miscSheet.getRow(startRow + i).getCell(6).setCellValue(currentBOMLine.getProperty("bl_quantity"));
                            miscSheet.getRow(startRow + i).getCell(7).setCellValue(itemRevision.getProperty("fx8_MaterialType"));
                            TCProperty property = itemRevision.getTCProperty("fx8_ProdProc");
                            TCComponent[] components = property.getReferenceValueArray();
                            StringBuffer processBuffer = new StringBuffer();
                            if (components != null && components.length > 0) {
                                for (TCComponent tcComponent : components) {
                                    String process = tcComponent.getProperty("fx8_Proc");
                                    processBuffer.append(process + "\n");
                                }
                                miscSheet.getRow(startRow + i).getCell(8).setCellValue(processBuffer.toString());
                            }
                            miscSheet.getRow(startRow + i).getCell(9).setCellValue(itemRevision.getProperty("fx8_SurfaceFinished"));
                            miscSheet.getRow(startRow + i).getCell(10).setCellValue(itemRevision.getProperty("fx8_Remark"));
                        }
                    }
                    FileOutputStream out = new FileOutputStream(filePath);
                    book.write(out);
                    out.flush();
                    out.close();
                    statDlg.dispose();
                    statDlg.stopth();
                    //回传tc
                    uploadFile(itemRev, filePath);
                    Desktop.getDesktop().open(new File(filePath));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void addPic(XSSFSheet sheet, String imgPath, int startRow, int startCol, int endRow,
                       int endCol) {
        try {
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            File imgFile = new File(imgPath);
            BufferedImage bufferImg = ImageIO.read(imgFile);
            String suffix = imgPath.substring(imgPath.lastIndexOf(".") + 1);
            ImageIO.write(bufferImg, suffix, byteArrayOut);
            XSSFDrawing patriarch = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0,
                    (short) startCol, startRow, (short) endCol, endRow);
            int imgType = getImgType(suffix);

            patriarch.createPicture(anchor, book.addPicture(byteArrayOut.toByteArray(), imgType));
            byteArrayOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getImgType(String suffix) {
        if ("jpeg".equalsIgnoreCase(suffix) || "jpg".equalsIgnoreCase(suffix)) {
            return HSSFWorkbook.PICTURE_TYPE_JPEG;
        } else if ("png".equalsIgnoreCase(suffix)) {
            return HSSFWorkbook.PICTURE_TYPE_PNG;
        } else if ("gif".equalsIgnoreCase(suffix)) {
            return HSSFWorkbook.PICTURE_TYPE_JPEG;
        }
        return HSSFWorkbook.PICTURE_TYPE_JPEG;
    }

    private void uploadFile(TCComponentItemRevision itemRev, String filePath) {
        try {
            TCComponent[] coms = itemRev.getReferenceListProperty(IRelationName.IMAN_specification);
            if (coms != null && coms.length > 0) {
                itemRev.remove(IRelationName.IMAN_specification, coms);
            }
            String datasetName = itemRev.getProperty("object_name") + " Part List";
            TCComponentDataset dataset = CreateObject.createDataSet(session, filePath, "MSExcelX", datasetName, "excel");
            System.out.println("dataset==" + dataset);
            itemRev.add(IRelationName.IMAN_specification, dataset);
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //遍历BOM
    private void recursionBOMLine(TCComponentBOMLine bomLine) {
        try {
            TCComponentItemRevision itemRev = bomLine.getItemRevision();
            addToList(itemRev);
            AIFComponentContext[] aifComponentContexts = bomLine.getChildren();
            if (aifComponentContexts != null && aifComponentContexts.length > 0) {
                for (int i = 0; i < aifComponentContexts.length; i++) {
                    TCComponentBOMLine childrenLine = (TCComponentBOMLine) aifComponentContexts[i].getComponent();
                    TCComponentItemRevision childrenRev = childrenLine.getItemRevision();
                    addToList(childrenRev);
                    if (childrenLine.hasChildren()) {
                        recursionBOMLine(childrenLine);
                    }
                }
            }
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void addToList(TCComponentItemRevision itemRev) {
        if (ItemTypeName.PLASTICREVISION.equals(itemRev.getType()) && !plasticList.contains(itemRev)) {
            plasticList.add(itemRev);
        }
        if (ItemTypeName.SHEETMETALREVISION.equals(itemRev.getType()) && !sheetList.contains(itemRev)) {
            sheetList.add(itemRev);
        }
        if ((ItemTypeName.SCREWREVISION.equals(itemRev.getType()) || ItemTypeName.STANDOFFREVISION.equals(itemRev.getType())
                || ItemTypeName.MYLARREVISION.equals(itemRev.getType()) || ItemTypeName.LABELREVISION.equals(itemRev.getType())
                || ItemTypeName.RUBBERREVISION.equals(itemRev.getType()) || ItemTypeName.GASKETREVISION.equals(itemRev.getType())) && !miscList.contains(itemRev)) {
            miscList.add(itemRev);
        }
    }


    //创建BOM
    private static TCComponentBOMLine transferToBOMLine(TCSession session, TCComponentItemRevision itemRev) throws Exception {
        TCComponentRevisionRuleType revisionType = (TCComponentRevisionRuleType) session.getTypeComponent("RevisionRule");
        TCComponentRevisionRule revisionRule = revisionType.getDefaultRule();
        TCComponentBOMWindowType bomWindowType = (TCComponentBOMWindowType) session.getTypeComponent("BOMWindow");
        TCComponentBOMWindow bomWindow = bomWindowType.create(revisionRule);
        TCComponentBOMLine topBOMLine = bomWindow.setWindowTopLine(itemRev.getItem(), itemRev, null, null);
        return topBOMLine;
    }

    private String getImagePath(TCComponentItemRevision itemRev) {
        //获取图片		
        String imgPath = "";
        try {
            TCComponent[] tcComponents = itemRev.getRelatedComponents("IMAN_specification");
            if (tcComponents != null && tcComponents.length > 0) {
                for (TCComponent tcComponent : tcComponents) {
                	System.out.println("数据集类型=="+tcComponent.getType());
                    if (tcComponent instanceof TCComponentDataset && "ProPrt".equals(tcComponent.getType())) {
                        TCComponentDataset tcDataset = (TCComponentDataset) tcComponent;
                        tcDataset.refresh();
                        TCComponent[] coms = tcDataset.getNamedReferences();
                        if (coms != null && coms.length > 0) {
                            for (TCComponent tcComponent2 : coms) {
                                String fileName = tcComponent2.toDisplayString().toUpperCase();
                                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                                if ("JPG".equals(suffix.toUpperCase())) {
                                    TCComponentTcFile tcFile = (TCComponentTcFile) tcComponent2;
                                    File tempfile = tcFile.getFmsFile();
                                    imgPath = tempfile.getPath();
                                    System.out.println("imgPath==" + imgPath);
                                }

                            }

                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgPath;
    }

}
