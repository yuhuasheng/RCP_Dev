package com.hh.tools.report.action;

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hh.tools.newitem.DownloadDataset;
import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.newitem.ItemTypeName;
import com.hh.tools.newitem.RelationName;
import com.hh.tools.report.storage.ComplianceSotrage;
import com.hh.tools.util.ExcelUtil;
import com.hh.tools.util.ProgressBarThread;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentFolderType;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class ComplianceReportAction extends AbstractAIFAction {

    private AbstractAIFApplication app = null;
    private TCSession session = null;
    private Registry reg = Registry.getRegistry("com.hh.tools.report.msg.message");
    private ProgressBarThread barThread = null;
    String excelFilePath = "";
    private String itemId;

    public ComplianceReportAction(AbstractAIFApplication arg0, Frame arg1, String arg2) {
        super(arg0, arg1, arg2);
        // TODO Auto-generated constructor stub
        this.app = arg0;
        this.session = (TCSession) this.app.getSession();
    }

    @Override
    public void run() {
        try {
            InterfaceAIFComponent[] aifComponents = this.app.getTargetComponents();
            for (InterfaceAIFComponent aifComponent : aifComponents) {
                System.out.println(((TCComponent) aifComponent).getType());
                if (((TCComponent) aifComponent).isTypeOf("FX8_PCBEZBOMRevision")) {
                    continue;
                } else {
                    throw new Exception(reg.getString("NotPCBEZ1.Msg"));
                }
            }

            TCComponentRevisionRuleType imancomponentrevisionruletype = (TCComponentRevisionRuleType) session
                    .getTypeComponent("RevisionRule");
            TCComponentRevisionRule imancomponentrevisionrule = imancomponentrevisionruletype.getDefaultRule();
            TCComponentBOMWindowType imancomponentbomwindowtype = (TCComponentBOMWindowType) session
                    .getTypeComponent("BOMWindow");
            TCComponentBOMWindow imancomponentbomwindow = imancomponentbomwindowtype.create(imancomponentrevisionrule);
            HashMap<String, ArrayList<ComplianceSotrage>> map = new HashMap<String, ArrayList<ComplianceSotrage>>();
            for (InterfaceAIFComponent aifComponent : aifComponents) {
                TCComponentItemRevision itemRev = (TCComponentItemRevision) aifComponent;
                TCComponentBOMLine topLine = imancomponentbomwindow.setWindowTopLine(null, itemRev, null, null);
                itemId = itemRev.getProperty("item_id");
                System.out.println("解析到顶级BOMLINE == " + topLine);
                AIFComponentContext[] aifComponentContexts = topLine.getChildren();
                for (AIFComponentContext aIFComponentContext : aifComponentContexts) {
                    TCComponentBOMLine childRenLine = (TCComponentBOMLine) aIFComponentContext.getComponent();
                    analysis(map, childRenLine);
                }
            }

            if (map.size() == 0) {
                throw new Exception(reg.getString("NotPCBA2.Msg"));
            }

            HashMap<String, List<String>> contentMap = new HashMap<String, List<String>>();

            // 查找Excel文件
            GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
            String templateId = getPreferenceUtil.getStringPreference(session, TCPreferenceService.TC_preference_site,
                    "FX_ComplianceForm_Template");

            TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent(ITypeName.Item);
            TCComponentItem item = itemType.find(templateId);
            if (item == null) {
                throw new Exception(reg.getString("TemplateNotFound.Msg"));
            }

            TCComponent com = item.getLatestItemRevision().getRelatedComponent("IMAN_specification");
            if (com == null || !(com instanceof TCComponentDataset)) {
                throw new Exception(reg.getString("TemplateNotFound1.Msg"));
            }

            excelFilePath = DownloadDataset.downloadFile((TCComponentDataset) com, false);

            // 弹出文件选择器供用户选择
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
                String dirName = chooser.getSelectedFile().getAbsolutePath() + File.separator + "环保认证清单_" + dateStr;
                File dirFile = new File(dirName);
                dirFile.mkdir();

//				dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

                System.out.println("开始写EXCEL...");
                for (Map.Entry<String, ArrayList<ComplianceSotrage>> entry : map.entrySet()) {
                    String key = entry.getKey();
                    if (entry.getValue().size() > 0) {
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

                        String folderName = key + "_环保认证清单_" + dateStr;
                        folder = folderType.create(folderName, "", ITypeName.Folder);
                        List<String> itemIdList = contentMap.get(folderName);
                        if (itemIdList == null) {
                            itemIdList = new ArrayList<String>();
                            contentMap.put(folderName, itemIdList);
                        }

                        int index = 1;
                        for (ComplianceSotrage sotrage : entry.getValue()) {
                            row = ExcelUtil.getRow(sheet, index);
                            if (!itemIdList.contains(sotrage.getItemId())) {
                                ExcelUtil.getCell(row, 0).setCellValue(sotrage.getMfr());
                                ExcelUtil.getCell(row, 0).setCellStyle(cellStyle);

                                ExcelUtil.getCell(row, 1).setCellValue(sotrage.getMfrPn());
                                ExcelUtil.getCell(row, 1).setCellStyle(cellStyle);

                                ExcelUtil.getCell(row, 2).setCellValue(sotrage.getObjectName());
                                ExcelUtil.getCell(row, 2).setCellStyle(cellStyle);

                                ExcelUtil.getCell(row, 3).setCellValue(sotrage.getHhPn());
                                ExcelUtil.getCell(row, 3).setCellStyle(cellStyle);

                                ExcelUtil.getCell(row, 4).setCellStyle(cellStyle);

                                ExcelUtil.getCell(row, 5).setCellStyle(cellStyle);

//								ExcelUtil.getCell(row, 6).setCellValue(sotrage.getMcd());
                                ExcelUtil.getCell(row, 6).setCellStyle(cellStyle);

                                ExcelUtil.getCell(row, 7).setCellValue(sotrage.getMcdVersion());
                                ExcelUtil.getCell(row, 7).setCellStyle(cellStyle);

                                ExcelUtil.getCell(row, 8).setCellValue(sotrage.getMcdRohsStatus());
                                ExcelUtil.getCell(row, 8).setCellStyle(cellStyle);

//								ExcelUtil.getCell(row, 9).setCellValue(sotrage.getMdd());
                                ExcelUtil.getCell(row, 9).setCellStyle(cellStyle);

                                ExcelUtil.getCell(row, 10).setCellValue(sotrage.getMddVersion());
                                ExcelUtil.getCell(row, 10).setCellStyle(cellStyle);

                                ExcelUtil.getCell(row, 11).setCellValue(sotrage.getMddRohsStatus());
                                ExcelUtil.getCell(row, 11).setCellStyle(cellStyle);

                                ExcelUtil.getCell(row, 12).setCellValue(sotrage.getHfStatus());
                                ExcelUtil.getCell(row, 12).setCellStyle(cellStyle);

                                ExcelUtil.getCell(row, 13).setCellValue(sotrage.getExemption());
                                ExcelUtil.getCell(row, 13).setCellStyle(cellStyle);

//								ExcelUtil.getCell(row, 14).setCellValue(sotrage.getFmd());
                                ExcelUtil.getCell(row, 14).setCellStyle(cellStyle);

                                ExcelUtil.getCell(row, 15).setCellValue(sotrage.getFmdVersion());
                                ExcelUtil.getCell(row, 15).setCellStyle(cellStyle);

                                ExcelUtil.getCell(row, 16).setCellValue(sotrage.getReachStatus());
                                ExcelUtil.getCell(row, 16).setCellStyle(cellStyle);

                                itemIdList.add(sotrage.getItemId());
                                folder.add("contents", sotrage.getChildRev());
                                index++;
                            }
                        }
                        this.session.getUser().getHomeFolder().add("contents", folder);
                        excelFilePath = dirName + File.separator + key + "_环保认证清单_" + dateFormat.format(new Date())
                                + ".xlsx";
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
                    }
                }
            }
            System.out.println("结束写EXCEL...");
        } catch (Exception e) {
            e.printStackTrace();
            MessageBox.post(e);
        } finally {
            if (barThread != null) {
                barThread.stopBar();
            }
        }
    }

    private boolean compare(TCComponentForm pForm, HashMap<String, ArrayList<ComplianceSotrage>> map, TCComponentItemRevision childRev) throws TCException {
        System.out.println("form object_name======" + pForm.getProperty("object_name"));
        System.out.println("itemId======" + itemId);

        TCProperty prop = pForm.getTCProperty(RelationName.COMPLIANCES);
        TCComponent[] childForms = prop.getReferenceValueArray();

        System.out.println("childForms====" + childForms.length);

        for (TCComponent component : childForms) {
            TCComponentForm form = (TCComponentForm) component;
            if (itemId.equals(form.getReferenceProperty("fx8_PCBEZBOM").getProperty("item_id"))) {
                String epmcdROHSStatus = form.getProperty("fx8_PCBEZBOMMCDRoHSStatus");
                String epmddROHSStatus = form.getProperty("fx8_PCBEZBOMMDDRoHSStatus");
                String epfmdReachStatus = form.getProperty("fx8_PCBEZBOMFMDREACHStatus");
                String ephfStatus = form.getProperty("fx8_PCBEZBOMHFStatus");

                System.out.println("tabl epmcdROHSStatus======" + epmcdROHSStatus);
                System.out.println("tabl epmddROHSStatus======" + epmddROHSStatus);
                System.out.println("tabl epfmdReachStatus======" + epfmdReachStatus);
                System.out.println("tabl ephfStatus======" + ephfStatus);

                String mcdROHSStatus = form.getProperty("fx8_MCDROHSStatus");
                String mddROHSStatus = form.getProperty("fx8_MDDROHSStatus");
                String reachStatus = form.getProperty("fx8_REACHStatus");

                String hfStatus;
                if (form.getLogicalProperty("fx8_IsHFStatus")) {
                    hfStatus = "YES";
                } else {
                    hfStatus = "NO";
                }

                System.out.println("form fx8_MCDROHSStatus======" + mcdROHSStatus);
                System.out.println("form fx8_MDDROHSStatus======" + mddROHSStatus);
                System.out.println("form fx8_REACHStatus======" + reachStatus);
                System.out.println("form fx8_IsHFStatus======" + hfStatus);

                if (!compareMCDorMDD(mcdROHSStatus, epmcdROHSStatus) || !compareMCDorMDD(mddROHSStatus, epmddROHSStatus)
                        || !compareHFOrReach(hfStatus, ephfStatus)) {
                    ComplianceSotrage complianceSotrage = new ComplianceSotrage();
                    complianceSotrage.setMfr(childRev.getProperty("fx8_Mfr"));
                    complianceSotrage.setMfrPn(childRev.getProperty("fx8_MfrPN"));
                    complianceSotrage.setPrjName(childRev.getProperty("fx8_PrjName"));
                    complianceSotrage.setItemRev(childRev.getProperty("item_revision_id"));
                    complianceSotrage.setHhPn(childRev.getProperty("fx8_StandardPN"));
                    complianceSotrage.setObjectName(childRev.getProperty("object_name"));
                    complianceSotrage.setItemId(itemId);

                    complianceSotrage.setChildRev(childRev);
                    complianceSotrage.setForm(form);

                    complianceSotrage.setMcd(form.getProperty("FX8_MCDRel"));
                    complianceSotrage.setMcdVersion(form.getProperty("fx8_MCDVer"));
                    complianceSotrage.setMcdRohsStatus(mcdROHSStatus);
                    complianceSotrage.setMdd(form.getProperty("fx8_MDDRel"));
                    complianceSotrage.setMddVersion(form.getProperty("fx8_MDDVer"));
                    complianceSotrage.setMddRohsStatus(mddROHSStatus);
                    complianceSotrage.setFmd(form.getProperty("FX8_FMDRel"));
                    complianceSotrage.setFmdVersion(form.getProperty("fx8_FMDVer"));
                    complianceSotrage.setReachStatus(reachStatus);
                    complianceSotrage.setHfStatus(hfStatus);
                    complianceSotrage.setExemption(form.getProperty("fx8_Exemption"));

                    ArrayList<ComplianceSotrage> list = map
                            .get(complianceSotrage.getMfr() + "_" + complianceSotrage.getPrjName() + "_"
                                    + complianceSotrage.getMfrPn() + "_" + complianceSotrage.getItemRev());
                    if (list == null) {
                        list = new ArrayList<ComplianceSotrage>();
                    }
                    list.add(complianceSotrage);
                    map.put(complianceSotrage.getMfr() + "_" + complianceSotrage.getPrjName() + "_"
                            + complianceSotrage.getMfrPn() + "_" + complianceSotrage.getItemRev(), list);
                    return true;
                }
                break;
            }
        }
        return false;
    }

    // 比较HF和REACH
    private boolean compareHFOrReach(String nowStatus, String needStatus) {
        if ("NO".equals(needStatus)) {
            if ("NO".equals(nowStatus) || "YES".equals(nowStatus)) {
                return true;
            } else {
                return false;
            }
        } else if ("YES".equals(needStatus)) {
            if ("YES".equals(nowStatus)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // 比较MCD和MDD
    private boolean compareMCDorMDD(String formStatus, String tableStatus) {
        if ("ROHS2.0+12P".equals(tableStatus)) {
            if ("ROHS2.0+12P".equals(formStatus)) {
                return true;
            } else {
                return false;
            }
        } else if ("ROHS2.0+8P".equals(tableStatus)) {
            if ("ROHS2.0+12P".equals(formStatus) || "ROHS2.0+8P".equals(formStatus)) {
                return true;
            } else {
                return false;
            }
        } else if ("ROHS2.0+5P".equals(tableStatus)) {
            if ("ROHS2.0+12P".equals(formStatus) || "ROHS2.0+8P".equals(formStatus)
                    || "ROHS2.0+5P".equals(formStatus)) {
                return true;
            } else {
                return false;
            }
        } else if ("ROHS2.0+4P".equals(tableStatus)) {
            if ("ROHS2.0+12P".equals(formStatus) || "ROHS2.0+8P".equals(formStatus) || "ROHS2.0+5P".equals(formStatus)
                    || "ROHS2.0+4P".equals(formStatus)) {
                return true;
            } else {
                return false;
            }
        } else if ("ROHS1.0".equals(tableStatus)) {
            if ("ROHS2.0+12P".equals(formStatus) || "ROHS2.0+8P".equals(formStatus) || "ROHS2.0+5P".equals(formStatus)
                    || "ROHS2.0+4P".equals(formStatus) || "ROHS1.0".equals(formStatus)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void analysis(HashMap<String, ArrayList<ComplianceSotrage>> map, TCComponentBOMLine bomLine)
            throws TCException {
    	System.out.println("解析到==" + bomLine);
        TCComponentItemRevision childRev = bomLine.getItemRevision();
        TCComponent[] coms = childRev.getRelatedComponents("IMAN_specification");
        TCComponentForm form = null;
        for (TCComponent component : coms) {
            if (component.isTypeOf(ItemTypeName.PCOMPLIANCEFORM)) {
                form = (TCComponentForm) component;
                break;
            }
        }

        if (form != null) {
        	// 比较环保等级
            compare(form, map, childRev);
        }

        AIFComponentContext[] aifComponentContexts = bomLine.getChildren();
        for (AIFComponentContext aIFComponentContext : aifComponentContexts) {
            TCComponentBOMLine childRenLine = (TCComponentBOMLine) aIFComponentContext.getComponent();
            analysis(map, childRenLine);
        }
    }

}
