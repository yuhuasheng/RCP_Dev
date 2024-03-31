// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   UploadFileDialog.java

package com.hh.tools.uploadFile;

import com.hh.tools.newitem.CheckUtil;
import com.hh.tools.newitem.CreateObject;
import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.newitem.Utils;
import com.hh.tools.util.ProgressBarThread;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.*;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

import java.io.File;
import java.util.HashMap;

import javax.swing.JFileChooser;

public class UploadFileDialog extends AbstractAIFDialog {

    private TCSession session;
    private AbstractAIFUIApplication app;
    private static String VERSION = "V20200725";
    private static String PERFERENAME = "DRAG_AND_DROP_default_dataset_type";
    private static JFileChooser chooser = null;
    private TCComponentItemRevision targetRev;
    private TCComponentFolder targetFolder;
    private Registry reg;
    private TCComponent targetCom;
    private TCComponentDatasetDefinitionType datasetDefinitiontype;
    GetPreferenceUtil getPreferenceUtil;
    ProgressBarThread barThread;

    public UploadFileDialog(AbstractAIFUIApplication app, TCSession session) {
        super(true);
        this.session = null;
        this.app = null;
        targetRev = null;
        targetFolder = null;
        reg = Registry.getRegistry("com.hh.tools.uploadFile.uploadfile");
        targetCom = null;
        datasetDefinitiontype = null;
        getPreferenceUtil = new GetPreferenceUtil();
        barThread = null;
        this.session = session;
        this.app = app;
        doOpenFile();
    }

    private void doOpenFile() {
        try {
        	System.out.println("上传文件");
            InterfaceAIFComponent aifComponent = app.getTargetComponent();
            TCComponentDatasetDefinition datasetDefinition = null;
            if (aifComponent == null)
                throw new Exception(reg.getString("SelectObj.Err"));
            System.out.println((new StringBuilder("选择的对象 == ")).append(aifComponent).toString());
            if (aifComponent instanceof TCComponentItemRevision) {
                targetRev = (TCComponentItemRevision) aifComponent;
                targetCom = targetRev;
            } else if (aifComponent instanceof TCComponentItem) {
//				targetRev = ((TCComponentItem)aifComponent).getLatestItemRevision();
                targetCom = (TCComponent) aifComponent;
            } else if (aifComponent instanceof TCComponentFolder) {
                targetFolder = (TCComponentFolder) aifComponent;
                targetCom = targetFolder;
            } else if (aifComponent instanceof TCComponentBOMLine) {
                targetRev = ((TCComponentBOMLine) aifComponent).getItemRevision();
                targetCom = targetRev;
            } else {
                throw new Exception(reg.getString("SelectFaultObj.Err"));
            }
            if (Utils.isReleased(targetRev)) {
                throw new Exception(reg.getString("IsReleased.MSG"));
            }
//			boolean flag = false;
//			if(targetRev!=null&&isInWorkflow(targetRev)){
//				flag = true;
//			}
            boolean flag = true;
            System.out.println("flag==" + flag);
            if (!flag && !CheckUtil.checkUserPrivilege(session, targetCom, "WRITE"))
                throw new Exception(reg.getStringWithSubstitution(reg.getString("NoAclObj.Err"), new String[]{
                        targetCom.toDisplayString()
                }));
            datasetDefinitiontype = (TCComponentDatasetDefinitionType) session.getTypeComponent("DatasetType");
            HashMap dragMap = getPreferenceUtil.getHashMapPreference(session, 4, PERFERENAME, ":");
            String defaultDstName = getPreferenceUtil.getStringPreference(session, 4, "FX_Dataset_DefaultTypeName");
            if ("".equals(defaultDstName))
                throw new Exception(reg.getStringWithSubstitution("PreferenceNoValue.Err", new String[]{
                        "FX_Dataset_DefaultTypeName"
                }));
            datasetDefinition = datasetDefinitiontype.find(defaultDstName);
            if (datasetDefinition == null)
                throw new Exception(reg.getStringWithSubstitution("DefaultDstNoFound.Err", new String[]{
                        defaultDstName
                }));
            if (chooser == null) {
                chooser = new JFileChooser();
                chooser.setApproveButtonText(reg.getString("UploadBtn.Name"));
                chooser.setFileSelectionMode(0);
                chooser.setMultiSelectionEnabled(true);
            }
            chooser.setDialogTitle((new StringBuilder(String.valueOf(reg.getString("FileUpload.Title")))).append("-").append(VERSION).toString());
            int result = chooser.showOpenDialog(this);
            String fileName = "";
            String suffix = "";
            String dsType = "";
            TCComponentDataset dataset = null;
            String errorMsg = "";
            barThread = new ProgressBarThread(reg.getString("Info.Msg"), reg.getString("UploadFile.Msg"));
            barThread.start();
            if (result == 0) {
                File files[] = chooser.getSelectedFiles();
                System.out.println((new StringBuilder("选择的文件数量 == ")).append(files.length).toString());
                for (int i = 0; i < files.length; i++) {
                	System.out.println((new StringBuilder("========正在处理第")).append(i).append("个数据=========").append(files[i]).toString());
                    if (files[i].isDirectory()) {
                    	System.out.println("当前文件是文件夹，不做处理");
                    } else {
                        fileName = files[i].getName();
                        System.out.println((new StringBuilder("文件名 == ")).append(fileName).toString());
                        suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                        if (dragMap.containsKey(suffix.toLowerCase()))
                            dsType = (String) dragMap.get(suffix.toLowerCase());
                        else
                            dsType = defaultDstName;
                        System.out.println((new StringBuilder("系统中的文件类型 == ")).append(dsType).toString());
                        datasetDefinition = datasetDefinitiontype.find(dsType);
                        if (datasetDefinition != null) {
                            dataset = CreateObject.createDataSet(session, files[i].getAbsolutePath(), dsType, fileName, datasetDefinition.getNamedReferences()[0]);
                            System.out.println((new StringBuilder("创建的数据集 == ")).append(dataset).toString());						
                            if (flag) {
                                Utils.setByPass(true, session);
                                if (targetCom instanceof TCComponentItem) {
                                    targetCom.add("IMAN_requirement", dataset);
                                } else if (targetCom instanceof TCComponentItemRevision) {
                                    targetRev.add("IMAN_specification", dataset);
                                }
                                Utils.setByPass(false, session);
                            } else {
                                if (targetCom instanceof TCComponentItem) {
                                    targetCom.add("IMAN_requirement", dataset);
                                } else if (targetCom instanceof TCComponentItemRevision) {
                                    targetRev.add("IMAN_specification", dataset);
                                }
                            }
                            if (targetFolder != null)
                                targetFolder.add("contents", dataset);
                        } else {
                            errorMsg = (new StringBuilder(String.valueOf(errorMsg))).append("\n").append(reg.getStringWithSubstitution("NoDatasetDefine.Err", new String[]{
                                    files[i].getAbsolutePath(), dsType, PERFERENAME
                            })).toString();
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            MessageBox.post(e);
        }
        if (barThread != null)
            barThread.stopBar();


    }

    private boolean isInWorkflow(TCComponentItemRevision itemRev) {
        boolean flag = false;
        try {
            TCComponent[] tcObjects = itemRev.getRelatedComponents("fnd0MyWorkflowTasks");
            if (tcObjects != null && tcObjects.length > 0) {
                for (TCComponent tcComponent : tcObjects) {
                    TCComponentTask task = (TCComponentTask) tcComponent;
                    String status = task.getProperty("fnd0TaskExecutionStatus");
                    TCComponentRule[] rules = task.getRules(TCComponentTask.COMPLETE_ACTION);
                    if (rules.length > 0) {
                        for (int i = 0; i < rules.length; i++) {
                            TCComponentRuleHandler[] rulehandlers = rules[i].getRuleHandlers();
                            for (TCComponentRuleHandler tcComponentRuleHandler : rulehandlers) {
                                String ruleHandlerName = tcComponentRuleHandler.getProperty("object_name");
                                System.out.println("ruleHandlerName==" + ruleHandlerName);
                                if ("FX_IsUploadFile".equals(ruleHandlerName) && ("0".equals(status) || "2".equals(status))) {
                                    flag = true;
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;


    }


}