package com.hh.tools.report.action;

import java.awt.Color;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hh.tools.newitem.DownloadDataset;
import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.newitem.RelationName;
import com.hh.tools.report.storage.ConnectorStorage;
import com.hh.tools.util.ExcelUtil;
import com.hh.tools.util.ProgressBarThread;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class ConnectorReportAction extends AbstractAIFAction {
    private AbstractAIFApplication app = null;
    private TCSession session = null;
    private AbstractPSEApplication pseApp = null;
    private ProgressBarThread barThread = null;
    private Registry reg = Registry.getRegistry("com.hh.tools.report.msg.message");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private String prjName;

    public ConnectorReportAction(AbstractAIFApplication arg0, Frame arg1, String arg2) {
        super(arg0, arg1, arg2);
        // TODO Auto-generated constructor stub
        this.app = arg0;
        this.session = (TCSession) this.app.getSession();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            if (app instanceof AbstractPSEApplication) {
                this.pseApp = (AbstractPSEApplication) this.app;
                TCComponentBOMLine topLine = this.pseApp.getTopBOMLine();
                System.out.println("解析到顶级BOMLINE == " + topLine);
                System.out.println("Type == " + topLine.getItem().getType());
                if (topLine.getItem().isTypeOf("EDACCABase")) {
                    AIFComponentContext[] aifComponentContexts = topLine.getChildren();
                    List<ConnectorStorage> emaList = analysisPCBA(aifComponentContexts);
                    if (emaList.size() == 0) {
                        throw new Exception(reg.getString("NotPCBA2.Msg"));
                    }

                    // 查找Excel文件
                    GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
                    String templateId = getPreferenceUtil.getStringPreference(session,
                            TCPreferenceService.TC_preference_site, "FX_Connector_Template");

                    TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent(ITypeName.Item);
                    TCComponentItem item = itemType.find(templateId);
                    if (item == null) {
                        throw new Exception(reg.getString("TemplateNotFound.Msg"));
                    }

                    TCComponent com = item.getLatestItemRevision().getRelatedComponent("IMAN_specification");
                    if (com == null || !(com instanceof TCComponentDataset)) {
                        throw new Exception(reg.getString("TemplateNotFound1.Msg"));
                    }

                    prjName = topLine.getItemRevision().getProperty("fx8_PrjName");

                    String excelFilePath = DownloadDataset.downloadFile((TCComponentDataset) com, true);

                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setApproveButtonText(reg.getString("Save.Msg"));
                    int result = chooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        barThread = new ProgressBarThread(reg.getString("Info.Msg"), reg.getString("Progress1.Msg"));
                        barThread.start();

                        String dateStr = dateFormat.format(new Date());
                        String dirName = chooser.getSelectedFile().getAbsolutePath() + File.separator + prjName
                                + "_CONNECTOR_" + dateStr;
                        File dirFile = new File(dirName);
                        dirFile.mkdir();

                        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excelFilePath));
                        XSSFSheet sheet = workbook.getSheetAt(0);

                        XSSFRow row = null;

                        XSSFCellStyle cellStyle = workbook.createCellStyle();
                        cellStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
                        cellStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
                        cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
                        cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
                        cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
                        cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
                        CreationHelper createHelper = workbook.getCreationHelper();
                        XSSFHyperlink link = null;

                        XSSFCellStyle linkStyle = workbook.createCellStyle();
                        linkStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
                        linkStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
                        linkStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
                        linkStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
                        linkStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
                        linkStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);
                        XSSFFont cellFont = workbook.createFont();
                        cellFont.setUnderline((byte) 1);
                        cellFont.setColor(new XSSFColor(Color.BLUE));
                        linkStyle.setFont(cellFont);

                        System.out.println("开始写EXCEL...");
                        int index = 1;
                        for (ConnectorStorage connectorStorage : emaList) {

                            row = ExcelUtil.getRow(sheet, index);

                            ExcelUtil.getCell(row, 0).setCellValue(index);
                            ExcelUtil.getCell(row, 0).setCellStyle(cellStyle);

                            ExcelUtil.getCell(row, 1).setCellValue(connectorStorage.getHhpn());
                            ExcelUtil.getCell(row, 1).setCellStyle(cellStyle);

                            ExcelUtil.getCell(row, 2).setCellValue(connectorStorage.getMfr());
                            ExcelUtil.getCell(row, 2).setCellStyle(cellStyle);

                            ExcelUtil.getCell(row, 3).setCellValue(connectorStorage.getMfrPN());
                            ExcelUtil.getCell(row, 3).setCellStyle(cellStyle);

                            ExcelUtil.getCell(row, 4).setCellValue(connectorStorage.getObjectDesc());
                            ExcelUtil.getCell(row, 4).setCellStyle(cellStyle);

                            TCComponentDataset dataset = connectorStorage.getDatasheet();
                            if (dataset != null) {
                                String fileName = DownloadDataset.downloadFile(dataset, true, dirName);
                                ExcelUtil.getCell(row, 5).setCellValue(dataset.toDisplayString());
                                ExcelUtil.getCell(row, 5).setCellStyle(linkStyle);

                                link = (XSSFHyperlink) createHelper.createHyperlink(Hyperlink.LINK_FILE);
                                link.setAddress(new File(fileName).getName());
                                ExcelUtil.getCell(row, 5).setHyperlink(link);
                            }

                            index++;
                        }
                        System.out.println("结束写EXCEL...");

						// 自动打开文件
                        if (workbook != null) {
                            try {
                                excelFilePath = dirName + File.separator + prjName + "_CONNECTOR_" + dateStr + ".xlsx";
                                FileOutputStream out = new FileOutputStream(excelFilePath);
                                workbook.write(out);
                                out.flush();
                                out.close();
                                // Desktop.getDesktop().open(excelFile);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        System.out.println("excelFilePath == " + excelFilePath);
//						Desktop.getDesktop().open(new File(excelFilePath));
                    }
                } else {
                    throw new Exception(reg.getString("NotPCBA.Msg"));
                }
            } else {
                throw new Exception(reg.getString("NotPCBA.Msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageBox.post(e);
        } finally {
            if (barThread != null) {
                barThread.stopBar();
            }
        }
    }

    private List<ConnectorStorage> analysisPCBA(AIFComponentContext[] aifComponentContexts) throws TCException {
    	System.out.println("开始解析子集...");
        List<ConnectorStorage> emaList = new ArrayList<ConnectorStorage>();
        for (AIFComponentContext aifComponentContext : aifComponentContexts) {
            TCComponentBOMLine childRenLine = (TCComponentBOMLine) aifComponentContext.getComponent();
            System.out.println("解析到子集 == " + childRenLine);

			TCComponentItemRevision childRev = childRenLine.getItemRevision();
			System.out.println("子集版本 == " + childRev);

            String category = childRev.getProperty("fx8_Category");
            System.out.println("子集类型 == " + category);

            if ("Connector".equalsIgnoreCase(category)) {
                ConnectorStorage connectorStorage = new ConnectorStorage();
                connectorStorage = new ConnectorStorage();
                connectorStorage.setHhpn(childRev.getProperty("fx8_StandardPN"));
                connectorStorage.setMfr(childRev.getProperty("fx8_Mfr"));
                connectorStorage.setMfrPN(childRev.getProperty("fx8_MfrPN"));
                connectorStorage.setObjectDesc(childRev.getProperty("object_desc"));
                TCComponent dataSheetCom = childRev.getRelatedComponent(RelationName.DATASHEET);
                if (dataSheetCom != null && dataSheetCom instanceof TCComponentDataset) {
                    TCComponentDataset dataSheetDataset = (TCComponentDataset) dataSheetCom;
                    if (dataSheetDataset.isTypeOf("PDF")) {
                        connectorStorage.setDatasheet(dataSheetDataset);
                    }
                }
                emaList.add(connectorStorage);
            }
        }
        System.out.println("结束解析子集...");
        return emaList;
    }

}
