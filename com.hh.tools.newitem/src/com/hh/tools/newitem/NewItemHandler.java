package com.hh.tools.newitem;


import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.aifrcp.SelectionHelper;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentSchedule;
import com.teamcenter.rac.kernel.TCComponentScheduleTask;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.commands.Messages;
import com.teamcenter.rac.ui.commands.RACUICommandsActivator;
import com.teamcenter.rac.ui.commands.create.bo.NewBOModel;
import com.teamcenter.rac.ui.commands.create.bo.NewBOWizard;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.SWTUIUtilities;
import com.teamcenter.rac.util.UIUtilities;
import com.teamcenter.rac.util.wizard.extension.BaseExternalWizardDialog;
import com.teamcenter.rac.util.wizard.extension.WizardExtensionHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Handk
 */
public class NewItemHandler extends AbstractHandler implements IExecutableExtension {
    protected InterfaceAIFComponent[] selectedCmps;
    protected ISelection mCurrentSelection;
    protected String wizardId;
    protected WizardDialog dialog;
    protected TCSession session;
    protected NewBOModel mBoModel;

    protected CusNewItemWizard newbowizard = null;
    private InterfaceAIFComponent targetObject = null;
    private InterfaceAIFComponent[] targetObjects = null;
    private TCComponentItemRevision targetRev = null;
    private TCComponentItem targetItem = null;
    private TCComponentBOMLine targetBOMLine = null;
    private TCComponentProject targetProject = null;
    private String itemType2Create;
    private String defaultValue2Create;
    private String defaultValueString;
    private AbstractAIFUIApplication app = null;
    private static NewItemConfig config = null;
    private Registry reg = Registry.getRegistry("com.hh.tools.newitem.newItem");
//	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Object execute(ExecutionEvent executionevent) throws ExecutionException {
        if (executionevent == null) {
            throw new IllegalArgumentException("Event can't be null");
        }
//		System.out.println("创建开始时间=="+dateFormat.format(new Date()));
        app = AIFUtility.getCurrentApplication();
        System.out.println("Application's class:" + app.getClass().toString());
        String type = executionevent.getParameter("Type");
        if (type.indexOf(":") == -1) {
            itemType2Create = type;
        } else {
            itemType2Create = type.split(":")[0];
            defaultValue2Create = type.split(":")[1];
        }
        session = (TCSession) AIFUtility.getDefaultSession();
        targetObject = app.getTargetComponent();
        targetObjects = app.getTargetComponents();
        if (targetObject != null) {
            System.out.println("Target's Type:" + targetObject.getType());
        }
        if (targetObject instanceof TCComponentItemRevision) {
            System.out.println("targetObject is rev 94");
            targetRev = (TCComponentItemRevision) targetObject;
        } else {
            targetRev = null;
        }
        if (targetObject instanceof TCComponentItem) {
            System.out.println("targetObject is Item 100");
            targetItem = (TCComponentItem) targetObject;
        } else if (targetObject instanceof TCComponentBOMLine) {
            targetBOMLine = (TCComponentBOMLine) targetObject;
            try {
                targetItem = targetBOMLine.getItem();
                targetRev = targetBOMLine.getItemRevision();
            } catch (TCException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (targetObject instanceof TCComponentProject) {
            targetProject = (TCComponentProject) targetObject;
        } else if (targetObject instanceof TCComponentScheduleTask) {
            TCComponentScheduleTask scheduleTask = (TCComponentScheduleTask) targetObject;
            try {
                TCProperty prop = scheduleTask.getTCProperty("schedule_tag");
                TCComponentSchedule schedule = (TCComponentSchedule) prop.getReferenceValue();
                TCProperty prop1 = schedule.getTCProperty("project_list");
                TCComponent[] coms = prop1.getReferenceValueArray();
                if (coms != null && coms.length != 0) {
                    targetProject = (TCComponentProject) coms[0];
                }
            } catch (TCException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            targetItem = null;
        }

        System.out.println("itemType2Create == " + itemType2Create);

        boolean checkResult = checkAll();
        if (!checkResult) {
            return null;
        }

        int i = 0;
        if ((executionevent.getParameters() != null) && (executionevent.getParameters().containsKey("selection"))) {
            Object localObject = executionevent.getParameters().get("selection");
            if ((localObject instanceof InterfaceAIFComponent[])) {
                selectedCmps = ((InterfaceAIFComponent[]) localObject);
                mCurrentSelection = new StructuredSelection(selectedCmps);
                i = 1;
            }
        }
        if (i == 0) {
            mCurrentSelection = HandlerUtil.getCurrentSelection(executionevent);
            selectedCmps = SelectionHelper.getTargetComponents(mCurrentSelection);
        }
        mBoModel = getBOModel();
        try {
            session = ((TCSession) RACUICommandsActivator.getDefault().getSession());
        } catch (Exception localException) {
            session = ((TCSession) AIFUtility.getDefaultSession());
        }
        launchWizard(executionevent);
        app.refresh();
        return null;
    }


    /**
	 * checkAll,创建对象前对一些必要条件进行检查
	 * */
    private boolean checkAll() {
        if (itemType2Create == null) {
            return false;
        }
        if (itemType2Create.equals(ItemTypeName.PPAPCONTEXT)) {
            if (targetObject == null) {
                MessageBox.post(reg.getString("SelectTopBOM.MSG"),
                        reg.getString("Warn.MSG"), MessageBox.WARNING);
                return false;
            } else if (targetObject instanceof TCComponentBOMLine) {
                try {
                    TCComponentBOMLine targetBOMLine = (TCComponentBOMLine) targetObject;
                    TCComponentItemRevision itemRev = targetBOMLine.getItemRevision();
                    if (ItemTypeName.L5PARTREVISION.equals(itemRev.getType())) {
                        AIFComponentContext[] context = targetBOMLine.getChildren();
                        if (context == null || context.length == 0) {
                            MessageBox.post(reg.getString("NoChileNode.MSG"),
                                    reg.getString("Warn.MSG"), MessageBox.WARNING);
                            return false;
                        }
                        System.out.println("bomLine==" + targetBOMLine);
                        TCComponentBOMWindow bomWindow = targetBOMLine.window();
                        TCComponentBOMLine topBomLine = bomWindow.getTopBOMLine();
                        if (!targetBOMLine.equals(topBomLine)) {
                            MessageBox.post(reg.getString("SelectTopBOM.MSG"),
                                    reg.getString("Warn.MSG"), MessageBox.WARNING);
                            return false;
                        }
                        TCComponentUser user = (TCComponentUser) itemRev.getTCProperty("owning_user").getReferenceValue();
                        TCComponentUser currentUser = RACUIUtil.getTCSession().getUser();
                        if (!currentUser.equals(user)) {
                            MessageBox.post(reg.getString("NoPBOMPartOwner.MSG"),
                                    reg.getString("Warn.MSG"), MessageBox.WARNING);
                            return false;
                        }
                    } else {
                        MessageBox.post(reg.getString("SelectTopBOMToCreate.MSG"),
                                reg.getString("Warn.MSG"), MessageBox.WARNING);
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                MessageBox.post(reg.getString("SelectTopBOMToCreate.MSG"),
                        reg.getString("Warn.MSG"), MessageBox.WARNING);
                return false;
            }
        } else if (itemType2Create.equals(ItemTypeName.DTPCASELFPNFORM)) {
            boolean flag = false;
            if (targetObjects != null && targetObjects.length == 1 && targetObjects[0] instanceof TCComponentItemRevision) {
                flag = true;
            } else {
                flag = false;
            }
            if (!flag) {
                MessageBox.post(reg.getString("SelectSingleTarget.MSG"),
                        reg.getString("Warn.MSG"), MessageBox.WARNING);
                return false;
            }

            try {
                TCComponentItemRevision itemRev = (TCComponentItemRevision) targetObject;
                String hhpn = "";
                if (ItemTypeName.EDACOMPREVISION.equals(itemRev.getType())) {
                    hhpn = itemRev.getProperty("fx8_StandardPN");
                } else {
                    hhpn = itemRev.getProperty("fx8_HHPN");
                }
                //查询是否维护过自编料号
                String[] keys = new String[]{"OrgHHPN"};
                String[] values = new String[]{hhpn};
                List<InterfaceAIFComponent> selfEditFormList = Utils.search("__FX_L6QuerySelfEditForm", keys, values);
                if (selfEditFormList != null && selfEditFormList.size() > 0) {
                    MessageBox.post(reg.getString("CreateSelfEditForm1.MSG") + hhpn + reg.getString("CreateSelfEditForm2.MSG"),
                            reg.getString("Warn.MSG"), MessageBox.WARNING);
                    return false;
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (itemType2Create.equals(ItemTypeName.L5ECR)) {
            if (targetObjects == null || (targetObjects != null && targetObjects.length > 1)) {
                MessageBox.post(reg.getString("SelectSingleTarget2.MSG"),
                        reg.getString("Warn.MSG"), MessageBox.WARNING);
                return false;
            }
        } else if (itemType2Create.equals(ItemTypeName.L5ECN)) {
            if (targetObjects == null || (targetObjects != null && targetObjects.length > 1)) {
                MessageBox.post(reg.getString("SelectSingleTarget2.MSG"),
                        reg.getString("Warn.MSG"), MessageBox.WARNING);
                return false;
            }
        } else if (itemType2Create.equals(ItemTypeName.PROCVER)
                || itemType2Create.equals(ItemTypeName.PROCPAGE)) {
            if (targetObjects == null || (targetObjects != null && targetObjects.length > 1)) {
                MessageBox.post(reg.getString("SelectSingleProcess.MSG"),
                        reg.getString("Warn.MSG"), MessageBox.WARNING);
                return false;
            }
        }
        // 填写SCR表单信息
        else if (ItemTypeName.HPSCRFORM.equals(itemType2Create)) {
            if (null == targetObjects || targetObjects.length > 1) {
                MessageBox.post(reg.getString("SelectSingleTarget2.MSG"), reg.getString("Warn.MSG"),
                        MessageBox.WARNING);
                return false;
            }
        }

        config = NewItemConfig.findConfig(itemType2Create);
        if (config != null) {
            String[] targets = config.getTarget();
            System.out.println("targets==" + Arrays.toString(targets));
            if (targets != null && targets.length != 0) {
                ArrayList<TCComponentType> list = new ArrayList<TCComponentType>();
                for (int i = 0; i < targets.length; i++) {
                    System.out.println("targets[i] == " + targets[i]);
                    TCComponentType objectType = null;
                    try {
                        objectType = session.getTypeComponent(targets[i]);
                    } catch (TCException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.out.println("系统查询不到" + targets[i] + "类型的对象");
                    }

                    if (objectType != null) {
                        list.add(objectType);
                        System.out.println("往List中添加 == " + objectType.getDisplayTypeName());

                    }
                }

                if (list.size() == 0) {
                    return true;
                }
                boolean flag = false;
                for (TCComponentType componentType : list) {
                    String type = componentType.getType();
                    if (targetObject != null && type.equals(targetObject.getType())) {
                        flag = true;
                        break;
                    }
                }
                if (flag) return true;
                String[] typeArr = new String[list.size()];

                String errorMsg = "";
                for (int i = 0; i < list.size(); i++) {
                    TCComponentType objectType = list.get(i);
                    String type = objectType.getTypeComponent().getTypeName();
                    System.out.println("type == " + type);
                    typeArr[i] = type;
                    errorMsg = errorMsg + " " + objectType.getDisplayTypeName() + " ";
                }
                System.out.println("errorMsg==" + errorMsg);
                if (targetRev == null) {
                    if (itemType2Create.equals(ItemTypeName.L5ECR)) {
                        MessageBox.post(reg.getString("SelectPBOMPart.MSG"),
                                reg.getString("Warn.MSG"), MessageBox.WARNING);
                        return false;
                    } else if (itemType2Create.equals(ItemTypeName.L5ZZOP)
                            || itemType2Create.equals(ItemTypeName.L5TZOP)
                            || itemType2Create.equals(ItemTypeName.L5CXOP)
                            || itemType2Create.equals(ItemTypeName.L5CYOP)
                            || itemType2Create.equals(ItemTypeName.L6EMDOPD)) {
                        MessageBox.post(reg.getString("CreateOP.MSG"),
                                reg.getString("Warn.MSG"), MessageBox.WARNING);
                        return false;
                    } else if (itemType2Create.equals(ItemTypeName.PROCVER)
                            || itemType2Create.equals(ItemTypeName.PROCPAGE)) {
                        MessageBox.post(reg.getString("SelectProcess.MSG"),
                                reg.getString("Warn.MSG"), MessageBox.WARNING);
                        return false;
                    } else {
                    	MessageBox.post("只能选择"+errorMsg+"执行此项操作",
                                reg.getString("Warn.MSG"), MessageBox.WARNING);
                        return false;
                    }
                }

                try {
                    if (targetRev != null && !targetRev.isTypeOf(typeArr)) {
                        if (itemType2Create.equals(ItemTypeName.L5ECR)) {
                            MessageBox.post(reg.getString("SelectPBOMPart.MSG"),
                                    reg.getString("Warn.MSG"), MessageBox.WARNING);
                            return false;
                        } else if (itemType2Create.equals(ItemTypeName.L5ZZOP)
                                || itemType2Create.equals(ItemTypeName.L5TZOP)
                                || itemType2Create.equals(ItemTypeName.L5CXOP)
                                || itemType2Create.equals(ItemTypeName.L5CYOP)
                                || itemType2Create.equals(ItemTypeName.L6EMDOPD)) {
                            MessageBox.post(reg.getString("CreateOP.MSG"),
                                    reg.getString("Warn.MSG"), MessageBox.WARNING);
                            return false;
                        } else if (itemType2Create.equals(ItemTypeName.PROCVER)
                                || itemType2Create.equals(ItemTypeName.PROCPAGE)) {
                            MessageBox.post(reg.getString("SelectProcess.MSG"),
                                    reg.getString("Warn.MSG"), MessageBox.WARNING);
                            return false;
                        } else {
                        	MessageBox.post("只能选择"+errorMsg+"执行此项操作",
                                    reg.getString("Warn.MSG"), MessageBox.WARNING);
                            return false;
                        }
                    } else if (targetRev != null && targetRev.isTypeOf(typeArr)) {
                        if ((itemType2Create.equals(ItemTypeName.L5ZZOP)
                                || itemType2Create.equals(ItemTypeName.L5TZOP)
                                || itemType2Create.equals(ItemTypeName.L5CXOP)
                                || itemType2Create.equals(ItemTypeName.L5CYOP)
                                || itemType2Create.equals(ItemTypeName.L6EMDOPD)) && targetBOMLine == null) {
                            MessageBox.post(reg.getString("CreateOP2.MSG"),
                                    reg.getString("Warn.MSG"), MessageBox.WARNING);
                            return false;
                        } else if (itemType2Create.equals(ItemTypeName.PROCPAGE)) {
                            if (targetRev.getProperty("item_revision_id").toUpperCase().startsWith("X")) {
                                MessageBox.post(reg.getString("CreateL5HYECN.MSG"),
                                        reg.getString("Warn.MSG"), MessageBox.WARNING);
                                return false;
                            }
                        }
                    }
                } catch (TCException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            // 判断选中对象是否需要发布
            try {
                if (config.isReleased()) {
                    if (!Utils.isReleased(targetRev)) {
                        MessageBox.post(reg.getString("Release.MSG"),
                                reg.getString("Warn.MSG"), MessageBox.WARNING);
                        return false;
                    }
                }
                if (itemType2Create.equals(ItemTypeName.APPROVESHEET)) {
                    //承认书是否已存在，存在的话不允许创建承认
                    try {
                        TCComponent com = targetRev.getRelatedComponent(RelationName.APPROVEREL);

//						if(com!=null&&ItemTypeName.APPROVESHEETREVISION.equals(com.getType())){
//							MessageBox.post(reg.getString("ApproveSheetExist.MSG"),
//									reg.getString("Warn.MSG"), MessageBox.WARNING);
//							return false;
//						}
                        TCComponent[] coms = targetRev.getItem().getReleasedItemRevisions();
                        if (coms != null && coms.length > 1) {
                            boolean flag = false;
                            for (int i = 0; i < coms.length; i++) {
                                TCComponentItemRevision itemRev = (TCComponentItemRevision) coms[i];
                                TCComponent approveCom = itemRev.getRelatedComponent(RelationName.APPROVEREL);
                                if (approveCom != null) {
                                    flag = true;
                                    break;
                                }

                            }
                            if (flag) itemType2Create = "FX8_RDDCN";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (itemType2Create.equals(ItemTypeName.TESTRPT)) {
                    try {
                        TCComponent com = targetRev.getRelatedComponent(RelationName.TESTREPORTREL);

//						if(com!=null&&ItemTypeName.TESTRPTREVISION.equals(com.getType())){
//							MessageBox.post(reg.getString("TestRPTExist.MSG"),
//									reg.getString("Warn.MSG"), MessageBox.WARNING);
//							return false;
//						}
                        TCComponent[] coms = targetRev.getItem().getReleasedItemRevisions();
                        if (coms != null && coms.length > 1) {
                            boolean flag = false;
                            for (int i = 0; i < coms.length; i++) {
                                TCComponentItemRevision itemRev = (TCComponentItemRevision) coms[i];
                                TCComponent approveCom = itemRev.getRelatedComponent(RelationName.TESTREPORTREL);
                                if (approveCom != null) {
                                    flag = true;
                                    break;
                                }

                            }
                            if (flag) itemType2Create = "FX8_RDDCN";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//				else if(itemType2Create.equals(ItemTypeName.SSIMULATION)){
//					try {			
//						TCComponent com = targetRev.getRelatedComponent(RelationName.SIMULATIONREPORTREL);		
//						
//						if(com!=null&&ItemTypeName.SSIMULATIONREVISION.equals(com.getType())){
//							MessageBox.post(reg.getString("SSExist.MSG"),
//									reg.getString("Warn.MSG"), MessageBox.WARNING);
//							return false;
//						}
//						TCComponent[] coms = targetRev.getItem().getReleasedItemRevisions();
//						if(coms!=null&&coms.length>1){
//							boolean flag = false;
//							for (int i = 0; i < coms.length; i++) {
//								TCComponentItemRevision itemRev = (TCComponentItemRevision)coms[i];
//								TCComponent approveCom = itemRev.getRelatedComponent(RelationName.SIMULATIONREPORTREL);
//								if(approveCom!=null){
//									flag = true;
//									break;
//								}
//								
//							}
//							if(flag)itemType2Create = "FX8_RDDCN";														
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			else if(itemType2Create.equals(ItemTypeName.L6PROCVER)){
//					itemType2Create = "FX8_ProcVer";
//				}
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public void setInitializationData(IConfigurationElement paramIConfigurationElement, String paramString,
                                      Object paramObject) throws CoreException {
    }

    protected NewBOModel getBOModel() {
        if (mBoModel == null) {
            mBoModel = new NewBOModel(this);
        }
        return mBoModel;
    }

    public Wizard getWizard() {
        if ((wizardId == null) || (wizardId.length() == 0)) {
            wizardId = "com.steel.newitem.CusNewItemWizard";
        }
        return WizardExtensionHelper.getWizard(wizardId);

    }

    public String getWizardTitle() {
        return Messages.getString("wizard.TITLE");
    }

    public void launchWizard() {
        launchWizard(null);
    }

    public void launchWizard(ExecutionEvent paramExecutionEvent) {
        boolean bool = false;
        String str1 = "";
        if ((paramExecutionEvent != null) && (paramExecutionEvent.getParameters() != null)) {
            if (paramExecutionEvent.getParameters().containsKey("revisionFlag")) {
                bool = ((Boolean) paramExecutionEvent.getParameters().get("revisionFlag")).booleanValue();
            }
            str1 = (String) paramExecutionEvent.getParameters().get("objectType");
            if (paramExecutionEvent.getParameters().containsKey("pasteRelation")) {
                String s = (String) paramExecutionEvent.getParameters().get("pasteRelation");
                if (s != null) {
                    String[] s2 = null;
                    s2 = s.split(",");
                    mBoModel.setRelType(s2[0]);
                }
            }
            if (paramExecutionEvent.getParameters().containsKey("parentComponents")) {
                InterfaceAIFComponent[] aifcs = (InterfaceAIFComponent[]) paramExecutionEvent.getParameters()
                        .get("parentComponents");
                mBoModel.setTargetArray(aifcs);
            }
            if (paramExecutionEvent.getParameters().containsKey("relOtherTypeInfo")) {
                String s = (String) paramExecutionEvent.getParameters().get("relOtherTypeInfo");
                if (s != null) {
                    String[] s2 = s.split(",");
                    List<String> localArrayList1 = new ArrayList<String>();
                    List<String> localArrayList2 = new ArrayList<String>();
                    String[] s3;
                    int j = (s3 = s2).length;
                    for (int i = 0; i < j; i++) {
                        String str2 = s3[i];
                        str2 = str2.trim();
                        int k = str2.indexOf(".");
                        localArrayList1.add(str2.substring(0, k));
                        localArrayList2.add(str2.substring(k + 1, str2.length()));
                    }
                    mBoModel.setPreAssignedRelTypesForAllSubTypes(localArrayList1, localArrayList2);
                }
            } else {
                mBoModel.setAllowUserToSelectRelation(useDefaultRelations());
            }
        }

        mBoModel.setAllowUserToSelectRelation(false);
        mBoModel.setBOType(itemType2Create);
        AIFDesktop aifdesktop = AIFUtility.getActiveDesktop();
        Shell shell = aifdesktop.getShell();
        str1 = itemType2Create;
        if (shell != null) {
            SWTUIUtilities.asyncExec(new CreateNewBOSWTDialog(shell, bool, str1));
        }

    }

    protected boolean useDefaultRelations() {
        return true;
    }

    protected void readDisplayParameters(NewBOWizard paramNewBOWizard, WizardDialog paramWizardDialog) {
        paramNewBOWizard.retrievePersistedDialogSettings(paramWizardDialog);
    }

    private class CreateNewBOSWTDialog implements Runnable {
        private final Shell mShell;
        private final boolean mRevisionFlag;
        private final String mType;

        private CreateNewBOSWTDialog(Shell paramShell, boolean paramBoolean, String paramString) {
            mShell = paramShell;
            mRevisionFlag = paramBoolean;
            mType = paramString;
        }

        @Override
        public void run() {
            newbowizard = (CusNewItemWizard) getWizard();
            if (newbowizard == null) {
                newbowizard = new CusNewItemWizard(wizardId);
            }

            mBoModel.setSession(session);
            mBoModel.reInitializeTransientData();
            mBoModel.setRevisionFlag(mRevisionFlag);

            newbowizard.setBOModel(mBoModel);
            newbowizard.setShell(mShell);
            newbowizard.setParentFrame(AIFUtility.getActiveDesktop());
            newbowizard.setTargetArray(selectedCmps);
            newbowizard.setCurrentSelection(mCurrentSelection);
            newbowizard.setWindowTitle(getWizardTitle());
            newbowizard.setRevisionFlag(mRevisionFlag);
            newbowizard.setDefaultType(mType);

            System.out.println("config == " + config);
            // 使用异步的方式，取获取系统中关于NewItemConfig的设置
            if (config != null) {
                config.setTargetBOMLine(targetBOMLine);
                config.setTargetItem(targetItem);
                config.setTargetItemRev(targetRev);
                config.setTargetObjects(targetObjects);
                newbowizard.setConfig(config);
                newbowizard.setApplication(app);
                Utils.print2Console("NewItemConfig 已设置!");
            }
            newbowizard.setDefaultValueForDoc(defaultValue2Create);
            System.out.println("targetRev == " + targetRev);
            newbowizard.setTargetRev(NewItemHandler.this.targetRev);
            System.out.println("targetItem == " + targetItem);
            newbowizard.setTargetItem(NewItemHandler.this.targetItem);
            System.out.println("targetObject == " + targetObject);
            newbowizard.setTargetObject(NewItemHandler.this.targetObject);
            System.out.println("targetBOMLine == " + targetBOMLine);
            newbowizard.setTargetBOMLine(NewItemHandler.this.targetBOMLine);
            System.out.println("targetProject == " + targetProject);
            newbowizard.setTargetProject(NewItemHandler.this.targetProject);

            // 设置文档类型的默认值
            if (!Utils.isNull(defaultValueString)) {
                String[] values = defaultValueString.split("&&");
                for (String value : values) {
                    if (!value.contains("=")) {
                        continue;
                    }
                    String[] temp = value.split("=");
                    if (temp == null || temp.length != 2) {
                        continue;
                    }
                    String pn = temp[0];
                    String pv = temp[1];
                    if (Utils.isNull(pn)) {
                        continue;
                    }
                    newbowizard.setRevisionDefaultValue(pn, pv);
                }
            }

            Shell localShell = UIUtilities.getCurrentModalShell();
            dialog = new BaseExternalWizardDialog(mShell, newbowizard);
            dialog.create();

            newbowizard.retrievePersistedDialogSettings(dialog);
            newbowizard.setWizardDialog(dialog);
            UIUtilities.setCurrentModalShell(dialog.getShell());

            newbowizard.addPropertyChangeListener(new IPropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent event) {
                    System.out.println("hello newitem addPropertyChangeListener");
                }
            });
            IWizardPage currentPage = dialog.getCurrentPage();
            if (currentPage.getName().endsWith("BOTypePage")) {
                dialog.showPage(dialog.getCurrentPage().getNextPage());
            }
            dialog.setBlockOnOpen(true);

            try {
                dialog.open();
            } catch (Exception e) {

            }

            dialog = null;
            mBoModel = null;
            UIUtilities.setCurrentModalShell(localShell);

        }
    }
}