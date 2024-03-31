package com.hh.tools.importCLForm;

import java.awt.Frame;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.hh.tools.newitem.ItemTypeName;
import com.hh.tools.newitem.RelationName;
import com.hh.tools.newitem.Utils;
import com.hh.tools.util.ExcelUtil;
import com.hh.tools.util.ProgressBarThread;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class ImportFormAction extends AbstractAIFAction {

    private AbstractAIFUIApplication app = null;
    private TCSession session = null;
    private Registry reg = Registry.getRegistry("com.hh.tools.report.msg.message");
    private AbstractPSEApplication pseApp = null;
    private TCComponentUser user = null;
    private JFileChooser dirChoose = new JFileChooser();
    private ProgressBarThread barThread = null;

    public ImportFormAction(AbstractAIFUIApplication arg0, Frame arg1, String arg2) {
        super(arg0, arg1, arg2);
        this.app = arg0;
        session = (TCSession) app.getSession();
        user = session.getUser();
        barThread = new ProgressBarThread("提示", "正在写入...");
    }

    @Override
    public void run() {
        try {
            if (app instanceof AbstractPSEApplication) {
                this.pseApp = (AbstractPSEApplication) this.app;
                TCComponentBOMLine topLine = this.pseApp.getTopBOMLine();
                System.out.println("解析到顶级BOMLINE == " + topLine);
                if (topLine.getItem().isTypeOf("FX8_PCBEZBOM")) {
                    TCComponentItemRevision pcbezbomItemRev = topLine.getItemRevision();
                    HashMap<String, TCComponentItemRevision> map = new HashMap<String, TCComponentItemRevision>();
                    AIFComponentContext[] aifComponentContexts = topLine.getChildren();
                    for (AIFComponentContext aIFComponentContext : aifComponentContexts) {
                        TCComponentBOMLine childRenLine = (TCComponentBOMLine) aIFComponentContext.getComponent();
                        TCComponentItemRevision childRev = childRenLine.getItemRevision();
                        map.put(childRev.getProperty("fx8_StandardPN"), childRev);
                    }

                    dirChoose.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    dirChoose.setApproveButtonText("确定");
                    dirChoose.setVisible(true);
                    dirChoose.setFileFilter(new FileNameExtensionFilter("Excel文件(*.xls,*.xlsx)", "xls", "xlsx"));
                    int returnVal = dirChoose.showOpenDialog(AIFDesktop.getActiveDesktop());
                    if (returnVal != JFileChooser.APPROVE_OPTION) {
                        return;
                    }
                    File excelFile = dirChoose.getSelectedFile();
                    if (excelFile == null || excelFile.exists() == false) {
                    	MessageBox.post("请选择Excel文件!", "警告", MessageBox.WARNING);
                    } else {
                        barThread.start();
                        Workbook workbook = ExcelUtil.getWorkbook(excelFile);
                        Sheet sheet = workbook.getSheetAt(0);

                        List<String> mcdVersionLOV = getLOV("FX8_MCDVersionLOV");
                        List<String> mddVersionLOV = getLOV("FX8_MDDVersionLOV");
                        List<String> fmdVersionLOV = getLOV("FX8_FMDVersionLOV");
                        List<String> mcdROHSStatusLOV = getLOV("FX8_MCDROHSStatusLOV");

                        for (int i = 1; true; i++) {
                            Row row = sheet.getRow(i);
                            if (row == null) {
                                break;
                            }
                            Cell cell = getCell(row, 3);
                            String hhpn = cell.getStringCellValue();
                            System.out.println("hhpn == " + hhpn);
                            if ("".equals(hhpn)) {
                                continue;
                            }

                            TCComponentItemRevision itemRev = map.get(hhpn);

                            if (itemRev != null) {
                                TCComponent[] coms = itemRev.getRelatedComponents("IMAN_specification");
                                if (coms == null || coms.length == 0) {
                                    continue;
                                }
                                TCComponentForm form = null;
                                for (TCComponent com : coms) {
                                    String formType = com.getType();
                                    System.out.println("formType == " + formType);
                                    if (ItemTypeName.PCOMPLIANCEFORM.equals(formType)) {
                                        form = (TCComponentForm) com;
                                        break;
                                    }
                                }
                                if (form == null) {
                                    continue;
                                }
//								TCComponentUser owner = (TCComponentUser) form.getTCProperty("owning_user")
//										.getReferenceValue();
//								System.out.println("owner == " + owner.getUserId());
//								if (user.getUserId().equals(owner.getUserId()) == false) {
//									barThread.stopBar();
//									MessageBox.post("当前用户对电子料件：" + itemId + " 无编辑权限", "警告", MessageBox.WARNING);
//									return;
//								}

                                TCProperty prop = form.getTCProperty(RelationName.COMPLIANCES);
                                TCComponent[] childEPIForms = prop.getReferenceValueArray();
                                if (childEPIForms != null) {
                                    for (TCComponent component : childEPIForms) {
                                        TCComponentForm compForm = (TCComponentForm) component;
                                        TCComponent pcbzbom = compForm.getReferenceProperty("fx8_PCBEZBOM");
                                        if (pcbzbom.getProperty("item_id")
                                                .equals(pcbezbomItemRev.getProperty("item_id"))) {
                                            String MCDVersion = getCell(row, 7).getStringCellValue();
                                            String MCDROHSStatus = getCell(row, 8).getStringCellValue();
                                            String MDDVersion = getCell(row, 10).getStringCellValue();
                                            String MDDROHSStatus = getCell(row, 11).getStringCellValue();
                                            String HFStatus = getCell(row, 12).getStringCellValue();
                                            String exemption = getCell(row, 13).getStringCellValue();
                                            String MFDVersion = getCell(row, 15).getStringCellValue();
                                            String REACHStatus = getCell(row, 16).getStringCellValue();
                                            System.out.println("MCDVersion == " + MCDVersion);
                                            System.out.println("MCDROHSStatus == " + MCDROHSStatus);
                                            System.out.println("MDDVersion == " + MDDVersion);
                                            System.out.println("MDDROHSStatus == " + MDDROHSStatus);
                                            System.out.println("HFStatus == " + HFStatus);
                                            System.out.println("exemption == " + exemption);
                                            System.out.println("MFDVersion == " + MFDVersion);
                                            System.out.println("REACHStatus == " + REACHStatus);

                                            if (mcdVersionLOV.contains(MCDVersion)) {
                                                compForm.getTCProperty("fx8_MCDVer").setStringValue(MCDVersion);
                                            } else {
                                            	throw new Exception("MCD Version 数据格式不正确");
                                            }

                                            if (mcdROHSStatusLOV.contains(MCDROHSStatus)) {
                                                compForm.getTCProperty("fx8_MCDROHSStatus")
                                                        .setStringValue(MCDROHSStatus);
                                            } else {
                                            	throw new Exception("MCD ROHS Status 数据格式不正确");
                                            }

                                            if (mddVersionLOV.contains(MDDVersion)) {
                                                compForm.getTCProperty("fx8_MDDVer").setStringValue(MDDVersion);
                                            } else {
                                            	throw new Exception("MDD Version 数据格式不正确");
                                            }

                                            if (mcdROHSStatusLOV.contains(MDDROHSStatus)) {
                                                compForm.getTCProperty("fx8_MDDROHSStatus")
                                                        .setStringValue(MDDROHSStatus);
                                            } else {
                                            	throw new Exception("MDD ROHS Status 数据格式不正确");
                                            }

                                            if ("YES".equals(HFStatus) || "NO".equals(HFStatus)) {
                                                if ("YES".equals(HFStatus)) {
                                                    compForm.getTCProperty("fx8_IsHFStatus").setLogicalValue(true);
                                                } else {
                                                    compForm.getTCProperty("fx8_IsHFStatus").setLogicalValue(false);
                                                }
                                            } else {
                                            	throw new Exception("HF Status 数据格式不正确");
                                            }

                                            compForm.getTCProperty("fx8_Exemption").setStringValue(exemption);

                                            if (fmdVersionLOV.contains(MFDVersion)) {
                                                compForm.getTCProperty("fx8_FMDVer").setStringValue(MFDVersion);
                                            } else {
                                            	throw new Exception("FMD Version 数据格式不正确");
                                            }

                                            if (mcdROHSStatusLOV.contains(REACHStatus)) {
                                                compForm.getTCProperty("fx8_REACHStatus").setStringValue(REACHStatus);
                                            } else {
                                            	throw new Exception("REACH Status 数据格式不正确");
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                    MessageBox.post("导入完成", "提示", MessageBox.INFORMATION);
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

    private List<String> getLOV(String type) {
        try {
            TCComponentListOfValuesType lovType = (TCComponentListOfValuesType) session
                    .getTypeComponent(ITypeName.ListOfValues);
            TCComponentListOfValues listOfValues = lovType.findLOVByName(type);
            return Utils.getLOVList(listOfValues);
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private Cell getCell(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) {
            cell = row.createCell(col);
        }
        return cell;
    }

}
