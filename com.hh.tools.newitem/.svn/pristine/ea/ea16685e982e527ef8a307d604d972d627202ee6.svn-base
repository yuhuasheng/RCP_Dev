package com.hh.tools.report.action;

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hh.tools.newitem.DownloadDataset;
import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.report.storage.SingleEnvlistStorage;
import com.hh.tools.util.ExcelUtil;
import com.hh.tools.util.ProgressBarThread;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentFolderType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class SingleenvlistReportAction extends AbstractAIFAction {

    private AbstractAIFApplication app = null;
    private TCSession session = null;
    private Registry reg = Registry.getRegistry("com.hh.tools.report.msg.message");
    private AbstractPSEApplication pseApp = null;
    private ProgressBarThread barThread = null;
    private String prjName;

    public SingleenvlistReportAction(AbstractAIFApplication arg0, Frame arg1, String arg2) {
        super(arg0, arg1, arg2);
        // TODO Auto-generated constructor stub
        this.app = arg0;
        this.session = (TCSession) this.app.getSession();
    }

    @Override
    public void run() {
        try {
            if (app instanceof AbstractPSEApplication) {
                this.pseApp = (AbstractPSEApplication) this.app;
                TCComponentBOMLine topLine = this.pseApp.getTopBOMLine();
                System.out.println("解析到顶级BOMLINE == " + topLine);
                if (topLine.getItem().isTypeOf("FX8_PCBEZBOM")) {
                    HashMap<String, HashMap<String, SingleEnvlistStorage>> map = new HashMap<String, HashMap<String, SingleEnvlistStorage>>();

                    AIFComponentContext[] aifComponentContexts = topLine.getChildren();
                    for (AIFComponentContext aIFComponentContext : aifComponentContexts) {
                        TCComponentBOMLine childRenLine = (TCComponentBOMLine) aIFComponentContext.getComponent();
                        analysis(map, childRenLine);
                    }

                    if (map.size() == 0) {
                        throw new Exception(reg.getString("NotPCBA2.Msg"));
                    }

                    // 查找Excel文件
                    GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
                    String templateId = getPreferenceUtil.getStringPreference(session,
                            TCPreferenceService.TC_preference_site, "FX_SingleEnvList_Template");

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

                    String excelFilePath = DownloadDataset.downloadFile((TCComponentDataset) com, false);

                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setApproveButtonText(reg.getString("Save.Msg"));
                    int result = chooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        barThread = new ProgressBarThread(reg.getString("Info.Msg"), reg.getString("Progress1.Msg"));
                        barThread.start();

                        TCComponentFolderType folderType = (TCComponentFolderType) this.session
                                .getTypeComponent(ITypeName.Folder);
                        TCComponentFolder folder = null;
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
                        String dateStr = dateFormat.format(new Date());

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

                        System.out.println("开始写EXCEL...");
                        int index = 1;
                        for (Map.Entry<String, HashMap<String, SingleEnvlistStorage>> entry : map.entrySet()) {
                            String key = entry.getKey();
                            HashMap<String, SingleEnvlistStorage> emaMap = entry.getValue();

                            if (emaMap.size() != 0) {
                            	// 在当前HOME下新建文件夹
								folder = folderType.create(key + "_单体认证清单_" + dateStr, "", ITypeName.Folder);
								System.out.println("创建到的文件夹 == " + folder);

                                for (Map.Entry<String, SingleEnvlistStorage> ema : emaMap.entrySet()) {
                                    SingleEnvlistStorage envlistStorage = ema.getValue();
                                    row = ExcelUtil.getRow(sheet, index);

                                    ExcelUtil.getCell(row, 0).setCellValue(index);
                                    ExcelUtil.getCell(row, 0).setCellStyle(cellStyle);

                                    ExcelUtil.getCell(row, 1).setCellValue(envlistStorage.getHfpn());
                                    ExcelUtil.getCell(row, 1).setCellStyle(cellStyle);

                                    ExcelUtil.getCell(row, 2).setCellValue(envlistStorage.getObjectDesc());
                                    ExcelUtil.getCell(row, 2).setCellStyle(cellStyle);

                                    ExcelUtil.getCell(row, 3).setCellValue(envlistStorage.getMfr());
                                    ExcelUtil.getCell(row, 3).setCellStyle(cellStyle);

                                    ExcelUtil.getCell(row, 4).setCellValue(envlistStorage.getMfrPN());
                                    ExcelUtil.getCell(row, 4).setCellStyle(cellStyle);

                                    ExcelUtil.getCell(row, 5).setCellValue(envlistStorage.getHhpn());
                                    ExcelUtil.getCell(row, 5).setCellStyle(cellStyle);

                                    ExcelUtil.getCell(row, 6).setCellValue(envlistStorage.getPrjName());
                                    ExcelUtil.getCell(row, 6).setCellStyle(cellStyle);

                                    folder.add("contents", envlistStorage.getChildRev());

                                    index++;
                                }

                                this.session.getUser().getHomeFolder().add("contents", folder);
                            }
                        }
                        dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        excelFilePath = chooser.getSelectedFile().getAbsolutePath() + File.separator + prjName
                        		+ "_单体认证清单_" + dateFormat.format(new Date()) + ".xlsx";

                        if (workbook != null) {
                            try {
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
						System.out.println("结束写EXCEL...");

						// 自动打开文件
//						Desktop.getDesktop().open(new File(excelFilePath));
                    }
                } else {
                    throw new Exception(reg.getString("NotPCBEZ.Msg"));
                }
            } else {
                throw new Exception(reg.getString("NotPCBEZ.Msg"));
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

    private void analysis(HashMap<String, HashMap<String, SingleEnvlistStorage>> map, TCComponentBOMLine bomLine)
            throws TCException {
    	System.out.println("解析到子集 == " + bomLine);

        TCComponentItemRevision childRev = bomLine.getItemRevision();
        String approvalStatus = null;
        String approvalHFPN = null;

        // 获取承认表单
        for (TCComponent component : childRev.getRelatedComponents("IMAN_specification")) {
            if (component.isTypeOf("FX8_ApprovesheetForm")) {
                approvalStatus = component.getProperty("fx8_ApprovalStatus");
                approvalHFPN = component.getProperty("fx8_HFPN");
                break;
            }
        }

        System.out.println("解析到HFPN == " + approvalHFPN);
		System.out.println("解析到ApprovalStatus== " + approvalStatus);

        if ("N".equalsIgnoreCase(approvalStatus) || "".equals(approvalStatus)) {
            SingleEnvlistStorage envlistStorage = new SingleEnvlistStorage();
            envlistStorage.setMfr(childRev.getProperty("fx8_Mfr"));
            envlistStorage.setMfrPN(childRev.getProperty("fx8_MfrPN"));
            envlistStorage.setHhpn(childRev.getProperty("fx8_StandardPN"));
            envlistStorage.setItemRev(childRev.getProperty("item_revision_id"));
            envlistStorage.setObjectDesc(childRev.getProperty("object_desc"));
            envlistStorage.setPrjName(childRev.getProperty("fx8_PrjName"));
            envlistStorage.setHfpn(approvalHFPN);
            envlistStorage.setChildRev(childRev);
            HashMap<String, SingleEnvlistStorage> emaMap = map
                    .get(envlistStorage.getMfr() + "_" + envlistStorage.getPrjName() + "_" + envlistStorage.getMfrPN()
                            + "_" + envlistStorage.getItemRev());
            if (emaMap == null) {
                emaMap = new HashMap<String, SingleEnvlistStorage>();
            }
            emaMap.put(childRev.getProperty("item_id"), envlistStorage);
            map.put(envlistStorage.getMfr() + "_" + envlistStorage.getPrjName() + "_" + envlistStorage.getMfrPN() + "_"
                    + envlistStorage.getItemRev(), emaMap);
        }

        AIFComponentContext[] aifComponentContexts = bomLine.getChildren();
        for (AIFComponentContext aIFComponentContext : aifComponentContexts) {
            TCComponentBOMLine childRenLine = (TCComponentBOMLine) aIFComponentContext.getComponent();
            analysis(map, childRenLine);
        }
    }

}
