package com.hh.tools.customerPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;

import org.eclipse.swt.widgets.Display;

import com.hh.tools.newitem.CreateObject;
import com.hh.tools.newitem.DownloadDataset;
import com.hh.tools.newitem.FileStreamUtil;
import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.newitem.RelationName;
import com.hh.tools.newitem.Utils;
import com.hh.tools.util.HHDateButton;
import com.hh.tools.util.HHTextField;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.classification.common.G4MUserAppContext;
import com.teamcenter.rac.classification.common.tree.G4MTree;
import com.teamcenter.rac.classification.common.tree.G4MTreeNode;
import com.teamcenter.rac.classification.icm.ClassificationService;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.common.lov.LOVComboBox;
import com.teamcenter.rac.common.viewedit.ViewEditHelper;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinition;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinitionType;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentICO;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.stylesheet.AbstractRendering;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.DateButton;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.iTextField;

public class EDACompCustomerPanel extends AbstractRendering {
    private FileStreamUtil fileStreamUtil = new FileStreamUtil();
    private PrintStream printStream = null;
    private SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
    private JPanel contentPanel = new JPanel();
    private TCComponentItemRevision itemRev = null;
    public int height = 25;
    private TCSession session = null;
    private static Map<String, String> projectMap = new HashMap<>();
    private static String PERFERENAME = "DRAG_AND_DROP_default_dataset_type";
    //HW Property
    public LOVComboBox categoryLov;
    public LOVComboBox partTypeLov;

    public HHTextField MfgPNField;
    public LOVComboBox projectLov;
    public HHDateButton updateTime;
    public HHTextField MfgField;
    public JButton MgfButton;
    public HHTextField dataSheetField;
    public JButton dataSheetButton;
    public HHTextField HHPNField;
    public JButton editButton;
    public HHTextField dellField;
    public JButton dellChooseButton;
    public JButton dellSelectButton;
    public HHTextField symbolField;
    private TCComponentProject oldProject = null;
    private TCComponentDataset dataSheetDataset = null;
    private TCComponentDataset symbolDataset = null;
    private TCComponentDataset dellSymbolDataset = null;
    private TCComponentDataset footprintDataset = null;
    private TCComponentDataset PADDataset = null;
    public JButton symbolChooseButton;
    public JButton symbolSelectButton;

    public LOVComboBox footprintLov;
    public HHTextField PADField;
    public JButton PADChooseButton;

    public HHTextField footprintField;
    public JButton footprintChooseButton;
    public JButton footprintSelectButton;


    public EDACompCustomerPanel(TCComponent paramTCComponent) throws Exception {
        super(paramTCComponent);
        String logFile = fileStreamUtil.getTempPath("EDACompRevisionRendering");
        printStream = fileStreamUtil.openStream(logFile);
        itemRev = (TCComponentItemRevision) paramTCComponent;
        session = itemRev.getSession();
        if (dataSheetDataset != null) dataSheetDataset = null;
        if (symbolDataset != null) symbolDataset = null;
        if (dellSymbolDataset != null) dellSymbolDataset = null;
        if (footprintDataset != null) footprintDataset = null;
        if (PADDataset != null) PADDataset = null;
        loadRendering();
    }

    @Override
    public void loadRendering() throws TCException {
        setBounds(100, 100, 900, 400);
        setPreferredSize(new Dimension(900, 400));
        this.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        contentPanel.add(getEDACompPanel());
    }

    @Override
    public boolean isRenderingModified() {
        System.out.println("isRenderingModified==" + super.isRenderingModified());
        System.out.println("是否签出=="+itemRev.isCheckedOut());
        if (itemRev.isCheckedOut()) {
            return true;
        }
        return false;

    }

    private List<String> getCategoryList() {
        List<String> categoryList = new ArrayList<String>();
        try {
            ClassificationService clafService = new ClassificationService();
            String partClassifyRootId = "ICM";
            G4MUserAppContext g4mUserAppContext = new G4MUserAppContext(clafService, partClassifyRootId);
            G4MTree g4mtree = new G4MTree(g4mUserAppContext);
            g4mtree.setShowPopupMenu(false);
            G4MTreeNode rootNode = g4mtree.findNode("ICM");
            g4mtree.setRootNode(rootNode, true);
            int count = rootNode.getChildCount();

            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    G4MTreeNode treeNode = (G4MTreeNode) rootNode.getChildAt(i);
                    if ("ICM01".equals(treeNode.getNodeName())) {
                        g4mtree.setRootNode(treeNode, true);
                        G4MTreeNode hpTreeNode = (G4MTreeNode) treeNode.getFirstChild();
                        g4mtree.setRootNode(hpTreeNode, true);
                        int categroyCount = hpTreeNode.getChildCount();
                        if (categroyCount > 0) {
                            for (int j = 0; j < categroyCount; j++) {
                                G4MTreeNode childNode = (G4MTreeNode) hpTreeNode.getChildAt(j);
                                String categroyStr = childNode.getICSDescriptor().getName();
                                categoryList.add(categroyStr);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categoryList;
    }

    @Override
    public void saveRendering() {
        try {
            Utils.byPass(true);
            saveValue(itemRev);
            Utils.byPass(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateRendering() {

        ViewEditHelper localViewEditHelper = new ViewEditHelper(component.getSession());
        ViewEditHelper.CKO localCKO = localViewEditHelper.getObjectState(component);
        switch (localCKO) {
            case CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE:
            case CHECKED_OUT_SAME_USER_NON_CANCEL_CHECKOUTABLE:
            case IMPLICITLY_CHECKOUTABLE:
                Utilities.invokeLater(new Runnable() {
                    public void run() {
                        setRenderingReadWrite();
                        checkApplyButton();
                        setMfgLabelReadOnly();
                        System.err.println("IMPLICITLY_CHECKOUTABLE");
                    }
                });
                break;
            case CHECKED_IN:
                Utilities.invokeLater(new Runnable() {
                    public void run() {
                        setRenderingReadOnly();
                        checkApplyButton();
                        System.err.println("CHECKED_IN");
                    }
                });
                break;
            case NOT_CHECKOUTABLE:
                Utilities.invokeLater(new Runnable() {
                    public void run() {
                        setRenderingReadOnly();
                        checkApplyButton();
                        System.err.println("NOT_CHECKOUTABLE");
                    }
                });
                break;
            default:
                break;
        }
    }

    private void setMfgLabelReadOnly() {
        try {
        	//当电子料件已入库，电子料件版本上的Mfg PN与Mfg属性栏位不能更改
            TCComponentICO[] icos = itemRev.getClassificationObjects();
            System.out.println("icos==" + icos.length);
            if (icos != null && icos.length > 0) {
                MfgField.setEditable(false);
                MgfButton.setEnabled(false);
                MfgPNField.setEditable(false);
            }
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void checkApplyButton() {
        MfgField.setEditable(false);
        HHPNField.setEditable(false);
        dataSheetField.setEditable(false);
        dellField.setEditable(false);
        symbolField.setEditable(false);
        PADField.setEditable(false);
        footprintField.setEditable(false);
        updateTime.setEnabled(false);
    }


    public void saveValue(TCComponentItemRevision itemRev) {
    	System.out.println("保存属性");
        Object category = categoryLov.getSelectedItem();
        Object partType = partTypeLov.getSelectedItem();
        Object projectName = projectLov.getSelectedItem();
        Object footprintCategory = footprintLov.getSelectedItem();
        String updateTimeStr = updateTime.getDateString();
        String dataSheetStr = dataSheetField.getText();
        String symbolStr = symbolField.getText();
        String dellSymbolStr = dellField.getText();
        String footprintStr = footprintField.getText();
        String PADStr = PADField.getText();
        System.out.println("saveRendering");
        try {
            Utils.setByPass(true, session);
            itemRev.setProperty("fx8_MfrPN", MfgPNField.getText());
            itemRev.setProperty("fx8_Mfr", MfgField.getText());
            itemRev.setProperty("fx8_StandardPN", HHPNField.getText());

            itemRev.setProperty("fx8_Category", category.toString());
            itemRev.setProperty("fx8_PartType", partType.toString());
            if (updateTimeStr != null && !updateTimeStr.contains("未设置")) {
                itemRev.setProperty("fx8_UpdatedTime", updateTimeStr);
            }
            TCComponent com = itemRev.getRelatedComponent(RelationName.DATASHEET);
            if (!Utils.isNull(dataSheetStr) && new File(dataSheetStr).exists()) {
                String mfg = MfgField.getText().trim();
                String mfgPN = MfgPNField.getText().trim();
                //处理特殊字符
                mfgPN = Utils.transferSpecChar(mfgPN, "-");
                String dataSheet = mfg + "_" + mfgPN;
                String suffix = dataSheetStr.substring(dataSheetStr.lastIndexOf(".") + 1);
                String fileName = dataSheet + "." + suffix;
                if (!Utils.isNull(fileName)) {
                    String dataSheetPath = System.getenv("TEMP") + File.separator + fileName;
                    TCComponentDatasetDefinitionType datasetDefinitiontype = (TCComponentDatasetDefinitionType) session.getTypeComponent("DatasetType");
                    HashMap dragMap = new GetPreferenceUtil().getHashMapPreference(session, 4, PERFERENAME, ":");
                    String defaultDstName = new GetPreferenceUtil().getStringPreference(session, 4, "FX_Dataset_DefaultTypeName");
                    String dsType = "";
                    if (dragMap.containsKey(suffix.toLowerCase()))
                        dsType = (String) dragMap.get(suffix.toLowerCase());
                    else
                        dsType = defaultDstName;
                    System.out.println("dsType==" + dsType);
                    TCComponentDatasetDefinition datasetDefinition = datasetDefinitiontype.find(dsType);
                    //上传dataSheet
                    Utils.copyFile(new File(dataSheetStr), new File(dataSheetPath));
                    TCComponentDataset dataSheetDs = CreateObject.createDataSet(Utils.getTCSession(), dataSheetPath, dsType, dataSheet, datasetDefinition.getNamedReferences()[0]);
                    new File(dataSheetPath).delete();
                    if (com != null) {
                        itemRev.remove(RelationName.DATASHEET, com);
                    }
                    itemRev.add(RelationName.DATASHEET, dataSheetDs);
                    itemRev.setProperty("fx8_DataSheet", dataSheet);
                }
            }
            TCComponent PADCom = itemRev.getRelatedComponent(RelationName.PAD);
            if (!Utils.isNull(PADStr) && new File(PADStr).exists()) {
                String PADName = new File(PADStr).getName();
                String PADPath = System.getenv("TEMP") + File.separator + PADName;
                String fileName = PADName.substring(0, PADName.lastIndexOf("."));
                //上传PAD
                Utils.copyFile(new File(PADStr), new File(PADPath));
                TCComponentDataset PADDs = null;
                if (PADStr.toLowerCase().endsWith("zip")) {
                    PADDs = CreateObject.createDataSet(Utils.getTCSession(), PADPath, "Zip", fileName, "ZIPFILE");
                } else if (PADStr.toLowerCase().endsWith("7z")) {
                    PADDs = CreateObject.createDataSet(Utils.getTCSession(), PADPath, "FX8_7Z", fileName, "FX8_7ZDst");
                } else if (PADStr.toLowerCase().endsWith("rar")) {
                    PADDs = CreateObject.createDataSet(Utils.getTCSession(), PADPath, "FX8_RAR", fileName, "FX8_RARDst");
                }
                new File(PADPath).delete();
                if (PADCom != null) {
                    itemRev.remove(RelationName.PAD, PADCom);
                }
                itemRev.add(RelationName.PAD, PADDs);
                itemRev.setProperty("fx8_PAD", fileName);
            }
            {
                TCComponent symbolCom = itemRev.getRelatedComponent(RelationName.SYMBOLREL);
                if (!Utils.isNull(symbolStr) && new File(symbolStr).exists()) {
                    String symbolFileName = new File(symbolStr).getName();
                    String symbol = symbolFileName.substring(0, symbolFileName.lastIndexOf("."));
//					if(symbolDataset==null){										
                    //暂定类型，后期修改
                    TCComponentDataset symbolDs = CreateObject.createDataSet(Utils.getTCSession(), symbolStr, "FX8_OLB", symbol, "FX8_OLBDst");
                    if (symbolCom != null) {
                        itemRev.remove(RelationName.SYMBOLREL, symbolCom);
                    }
                    itemRev.add(RelationName.SYMBOLREL, symbolDs);
                    itemRev.setProperty("fx8_Symbol", symbol);
                    String[] keys = new String[]{Utils.getTextValue("Type"), Utils.getTextValue("OwningUser"), Utils.getTextValue("Name")};
                    String[] values = new String[]{"Folder", "infodba (infodba)", "SYMBOL"};
                    List<InterfaceAIFComponent> list = Utils.search("General...", keys, values);
                    TCComponentFolder categoryFolder = null;
                    if (list != null && list.size() > 0) {
                        TCComponentFolder symbolFolder = (TCComponentFolder) list.get(0);
                        TCComponent[] coms = symbolFolder.getRelatedComponents("contents");
                        if (coms != null && coms.length > 0) {
                            for (TCComponent tcComponent : coms) {
                                if (tcComponent instanceof TCComponentFolder && category.toString().toUpperCase()
                                        .equals(tcComponent.getProperty("object_name"))) {
                                    categoryFolder = (TCComponentFolder) tcComponent;
                                    break;
                                }
                            }
                        } else {
                            TCComponentFolder folder = CreateObject.createFolder(Utils.getTCSession(),
                                    category.toString().toUpperCase(), "Folder");
                            symbolFolder.add("contents", folder);
                            folder.add("contents", symbolDs);
                            return;
                        }
                        if (categoryFolder != null) {
                            categoryFolder.add("contents", symbolDs);
                        } else {
                            TCComponentFolder folder = CreateObject.createFolder(Utils.getTCSession(),
                                    category.toString().toUpperCase(), "Folder");
                            symbolFolder.add("contents", folder);
                            folder.add("contents", symbolDs);
                        }
                    }
//					}
                } else if (symbolDataset != null) {
                	// 选择
                    if (symbolCom != null) {
                        itemRev.remove(RelationName.SYMBOLREL, symbolCom);
                    }
                    itemRev.add(RelationName.SYMBOLREL, symbolDataset);
                    itemRev.setProperty("fx8_Symbol", symbolDataset.getProperty("object_name"));
                }
            }
            {
                TCComponent dellSymbolCom = itemRev.getRelatedComponent(RelationName.DELLSYMBOLREL);
                if (!Utils.isNull(dellSymbolStr) && new File(dellSymbolStr).exists()) {
                    String dellSymbolFileName = new File(dellSymbolStr).getName();
                    String dellSymbol = dellSymbolFileName.substring(0, dellSymbolFileName.lastIndexOf("."));
//					if(dellSymbolDataset==null){										
                    // 暂定类型，后期修改
                    TCComponentDataset dellSymbolDs = CreateObject.createDataSet(Utils.getTCSession(), dellSymbolStr,
                            "FX8_OLB", dellSymbol, "FX8_OLBDst");
                    if (dellSymbolCom != null) {
                        itemRev.remove(RelationName.DELLSYMBOLREL, dellSymbolCom);
                    }
                    itemRev.add(RelationName.DELLSYMBOLREL, dellSymbolDs);
                    itemRev.setProperty("fx8_DellSymbol", dellSymbol);
                    String[] keys = new String[]{Utils.getTextValue("Type"), Utils.getTextValue("OwningUser"),
                            Utils.getTextValue("Name")};
                    String[] values = new String[]{"Folder", "infodba (infodba)", "DELLSYMBOL"};
                    List<InterfaceAIFComponent> list = Utils.search("General...", keys, values);
                    TCComponentFolder categoryFolder = null;
                    if (list != null && list.size() > 0) {
                        TCComponentFolder dellSymbolFolder = (TCComponentFolder) list.get(0);
                        TCComponent[] coms = dellSymbolFolder.getRelatedComponents("contents");
                        if (coms != null && coms.length > 0) {
                            for (TCComponent tcComponent : coms) {
                                if (tcComponent instanceof TCComponentFolder && category.toString().toUpperCase()
                                        .equals(tcComponent.getProperty("object_name"))) {
                                    categoryFolder = (TCComponentFolder) tcComponent;
                                    break;
                                }
                            }
                        } else {
                            TCComponentFolder folder = CreateObject.createFolder(Utils.getTCSession(),
                                    category.toString().toUpperCase(), "Folder");
                            dellSymbolFolder.add("contents", folder);
                            folder.add("contents", dellSymbolDs);
                            return;
                        }
                        if (categoryFolder != null) {
                            categoryFolder.add("contents", dellSymbolDs);
                        } else {
                            TCComponentFolder folder = CreateObject.createFolder(Utils.getTCSession(),
                                    category.toString().toUpperCase(), "Folder");
                            dellSymbolFolder.add("contents", folder);
                            folder.add("contents", dellSymbolDs);
                        }
                    }
//					}
                } else if (dellSymbolDataset != null) {
                	// 选择
                    if (dellSymbolCom != null) {
                        itemRev.remove(RelationName.DELLSYMBOLREL, dellSymbolCom);
                    }
                    itemRev.add(RelationName.DELLSYMBOLREL, dellSymbolDataset);
                    itemRev.setProperty("fx8_DellSymbol", dellSymbolDataset.getProperty("object_name"));
                }
            }
            {
                TCComponent footprintCom = itemRev.getRelatedComponent(RelationName.FOOTPRINT);
                if (!Utils.isNull(footprintStr) && new File(footprintStr).exists()) {
                    String footprintFileName = new File(footprintStr).getName();
                    String footprint = footprintFileName.substring(0, footprintFileName.lastIndexOf("."));
//					if(footprintDataset==null){										
                    // 暂定类型，后期修改
                    TCComponentDataset footprintDs = CreateObject.createDataSet(Utils.getTCSession(), footprintStr,
                            "FX8_FootPrint", footprint, "FX8_FootPrint");
                    if (footprintCom != null) {
                        itemRev.remove(RelationName.FOOTPRINT, footprintCom);
                    }
                    itemRev.add(RelationName.FOOTPRINT, footprintDs);
                    itemRev.setProperty("fx8_Footprint", footprint);
                    String[] keys = new String[]{Utils.getTextValue("Type"), Utils.getTextValue("OwningUser"),
                            Utils.getTextValue("Name")};
                    String[] values = new String[]{"Folder", "infodba (infodba)", "FOOTPRINT"};
                    List<InterfaceAIFComponent> list = Utils.search("General...", keys, values);
                    TCComponentFolder footprintCategoryFolder = null;
                    if (list != null && list.size() > 0) {
                        TCComponentFolder footprintFolder = (TCComponentFolder) list.get(0);
                        TCComponent[] coms = footprintFolder.getRelatedComponents("contents");
                        if (coms != null && coms.length > 0) {
                            for (TCComponent tcComponent : coms) {
                                if (tcComponent instanceof TCComponentFolder && footprintCategory.toString()
                                        .toUpperCase().equals(tcComponent.getProperty("object_name"))) {
                                    footprintCategoryFolder = (TCComponentFolder) tcComponent;
                                    break;
                                }
                            }
                        } else {
                            TCComponentFolder folder = CreateObject.createFolder(Utils.getTCSession(),
                                    footprintCategory.toString().toUpperCase(), "Folder");
                            footprintFolder.add("contents", folder);
                            folder.add("contents", footprintDs);
                            return;
                        }
                        if (footprintCategoryFolder != null) {
                            footprintCategoryFolder.add("contents", footprintDs);
                        } else {
                            TCComponentFolder folder = CreateObject.createFolder(Utils.getTCSession(),
                                    footprintCategory.toString().toUpperCase(), "Folder");
                            footprintFolder.add("contents", folder);
                            folder.add("contents", footprintDs);
                        }
                    }
//					}
                } else if (footprintDataset != null) {
                	// 选择
                    if (footprintCom != null) {
                        itemRev.remove(RelationName.FOOTPRINT, footprintCom);
                    }
                    itemRev.add(RelationName.FOOTPRINT, footprintDataset);
                    itemRev.setProperty("fx8_Footprint", footprintDataset.getProperty("object_name"));
                }
            }
            if (footprintCategory != null)
                itemRev.setProperty("fx8_FootprintCategory", footprintCategory.toString());

//			TCProperty prjListP = itemRev.getTCProperty("project_list");
//			if(prjListP!=null){
//				TCComponent[] components = prjListP.getReferenceValueArray();
//				if(components!=null&&components.length>0){
//					TCComponentProject oldProject = (TCComponentProject)components[0];
//					oldProject.removeFromProject(new TCComponent[]{itemRev.getItem(),itemRev});
//				}
//			}		
//			String oldProjectName = itemRev.getProperty("fx8_PrjName");
//			if(!Utils.isNull(oldProjectName)){
//				String[] keys = new String[] { Utils.getTextValue("ProjectName")};
//				String[] values = new String[] { oldProjectName.toString()};
//				List<InterfaceAIFComponent> projectList = Utils.search("FX_FindProject", keys, values);
//				if(projectList!=null&&projectList.size()>0){
//					TCComponentProject oldProject = (TCComponentProject)projectList.get(0);
//					oldProject.removeFromProject(new TCComponent[]{itemRev.getItem(),itemRev});
//				}				
//			}
            if (projectName != null) {
                itemRev.setProperty("fx8_PrjName", projectName.toString());
//				String[] keys = new String[] { Utils.getTextValue("ProjectName")};
//				String[] values = new String[] { projectName.toString()};
//				List<InterfaceAIFComponent> projectList = Utils.search("__FX_FindProject", keys, values);
//				if(projectList!=null&&projectList.size()>0){
//					TCComponentProject project = (TCComponentProject)projectList.get(0);
//					project.assignToProject(new TCComponent[]{itemRev.getItem()});						
//				}
            }
            Utils.setByPass(false, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isObjectSavable() {
        String category = categoryLov.getTextField().getText();
        if (Utils.isNull(category)) {
        	Utils.infoMessage("Category属性栏位为空，不允许保存!");
            return false;
        }
        System.out.println("isObjectSavable==" + super.isObjectSavable());
        return super.isObjectSavable();
    }

//	private void custSetRenderingReadWrite() {
//		setRenderingReadWrite();
//	}
//	
//	private void custSetRenderingRead() {
//		setRenderingReadOnly();
//	}

    private static List<String> getPartTypeList(String categroyLovText) {
        List<String> list = new ArrayList<String>();
        TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
        String[] partTypeArr = new GetPreferenceUtil().getArrayPreference(session,
                TCPreferenceService.TC_preference_site, "FX_Get_PartType_Values");
        if (partTypeArr != null && partTypeArr.length > 0) {
            for (String partTypeValue : partTypeArr) {
                String[] partTypeArray = partTypeValue.split("::");
                String categoryStr = partTypeArray[0];
                String partTypeValueArr = partTypeArray[1];
                String[] partTypeStr = partTypeValueArr.split(",");
                if (categroyLovText.equals(categoryStr)) {
                    for (String partType : partTypeStr) {
                        list.add(partType);
                    }
                }
            }
        }
        return list;
    }

//	private static List<String> getPartTypeList(String categroyLovText) {
//		List<String> list = new ArrayList<String>();
//		try {			
//			ClassificationService clafService = new ClassificationService();
//			String partClassifyRootId = "ICM";
//			G4MUserAppContext g4mUserAppContext = new G4MUserAppContext(clafService,partClassifyRootId);
//			G4MTree g4mtree = new G4MTree(g4mUserAppContext);			
//			g4mtree.setShowPopupMenu(false);
//			G4MTreeNode rootNode = g4mtree.findNode("ICM");
//			g4mtree.setRootNode(rootNode, true);			
//			int count = rootNode.getChildCount();		
//			if(count>0){
//				outer:for (int i = 0; i < count; i++) {										
//					G4MTreeNode treeNode = (G4MTreeNode)rootNode.getChildAt(i);				
//					if("ICM01".equals(treeNode.getNodeName())){
//						g4mtree.setRootNode(treeNode, true);
//						G4MTreeNode hpTreeNode = (G4MTreeNode)treeNode.getFirstChild();
//						g4mtree.setRootNode(hpTreeNode, true);
//						int categroyCount = hpTreeNode.getChildCount();				
//						if(categroyCount>0){
//							for (int j = 0; j < categroyCount; j++) {								
//								G4MTreeNode childNode = (G4MTreeNode)hpTreeNode.getChildAt(j);							
//								String categroyStr = childNode.getICSDescriptor().getName();
//								if(categroyLovText.equals(categroyStr)){
//									g4mtree.setRootNode(childNode, true);
//									int partTypeCount = childNode.getChildCount();	
//									if(partTypeCount>0){
//										for (int k = 0; k < partTypeCount; k++) {	
//											G4MTreeNode partTypeNode = (G4MTreeNode)childNode.getChildAt(k);
//											String partTypeStr = partTypeNode.getICSDescriptor().getName();
//											list.add(partTypeStr);
//											
//										}
//									}
//									break outer;
//								}
//								
//							}														
//						}						
//					}
//				}
//			}						
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return list;
//	}

    class AssignMfgDialog extends AbstractAIFDialog {
        private final JPanel contentPanel = new JPanel();
        private iTextField mfgNameField;
        private JButton searchButton;
        private TCTable table;
        private String[] titles = new String[] { "序号", "ID", "名称" };

        public AssignMfgDialog() {
            super(true);
            initUI();
        }

        private void initUI() {
            setTitle("选择Mfg");
            setBounds(100, 100, 750, 400);
            getContentPane().setLayout(new BorderLayout());
            contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            getContentPane().add(contentPanel, BorderLayout.CENTER);
            contentPanel.setLayout(new BorderLayout(0, 0));
            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
            {
                JPanel panel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
                panel.setBorder(new TitledBorder(null, "搜索", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                contentPanel.add(panel, BorderLayout.NORTH);
                panel.setBackground(Color.white);

                JLabel nameLabel = new JLabel("名称:", JLabel.RIGHT);
                nameLabel.setPreferredSize(new Dimension(100, height));
                mfgNameField = new iTextField();
                mfgNameField.setPreferredSize(new Dimension(150, height));

                JButton searchButton = new JButton("查询");
                searchButton.setPreferredSize(new Dimension(70, height));
                searchButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                searchAction();
                            }
                        }).start();
                    }
                });

                panel.add("1.1.right.center.preferred.preferred", nameLabel);
                panel.add("1.2.left.center.preferred.preferred", mfgNameField);
                panel.add("1.3.right.center.preferred.preferred", searchButton);

            }
            {
                JPanel panel = new JPanel();
                panel.setBorder(new TitledBorder(null, "Mfr列表", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                panel.setBackground(Color.white);
                contentPanel.add(panel, BorderLayout.CENTER);
                panel.setLayout(new BorderLayout(0, 0));

                table = new TCTable(session, titles) {
                    public boolean isCellEditable(int arg0, int arg1) {

                        return false;

                    }
                };

                JScrollPane scrollTablePanel = new JScrollPane();
                scrollTablePanel.setBackground(Color.WHITE);
                scrollTablePanel.setPreferredSize(new Dimension(730, 300));
                scrollTablePanel.setViewportView(table);
                table.getTableHeader().setBackground(Color.WHITE);
                table.setRowHeight(30);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                table.getTableHeader().setResizingAllowed(true);
                table.getTableHeader().setReorderingAllowed(false);
                panel.add(scrollTablePanel, BorderLayout.CENTER);

                table.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (event.getClickCount() > 1) {
                            dispose();
                            Display.getDefault().syncExec(new Runnable() {
                                @Override
                                public void run() {
                                    int row = table.getSelectedRow();
                                    MfgField.setText(table.getValueAt(row, 2).toString());
                                }
                            });
                        }
                    }
                });
            }
            centerToScreen();
            setVisible(true);
        }

        private void searchAction() {
            table.removeAllRows();
            String name = mfgNameField.getText();

            List key = new ArrayList();
            List value = new ArrayList();
            key.add(Utils.getTextValue("fx8_MfrStatus"));
            value.add("Approved");
            if (!"".equals(name)) {
                key.add(Utils.getTextValue("object_name"));
                value.add("*" + name + "*");
            }

            String[] keyArray = new String[key.size()];
            key.toArray(keyArray);
            String[] valueArray = new String[value.size()];
            value.toArray(valueArray);
            List<InterfaceAIFComponent> supplierList = Utils.search("__FX_FindMfr", keyArray, valueArray);
            System.out.println("supplierList==" + supplierList);
            try {
                if (supplierList != null && supplierList.size() > 0) {
                    for (int i = 0; i < supplierList.size(); i++) {
                        InterfaceAIFComponent com = supplierList.get(i);
                        TCComponentItemRevision itemRev = ((TCComponentItem) com).getLatestItemRevision();
                        table.addRow(new Object[]{i + 1, itemRev.getProperty("item_id"),
                                itemRev.getProperty("object_name")});
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    class SymbolDialog extends AbstractAIFDialog {
        private final JPanel contentPanel = new JPanel();
        //		private int height = 25;
        private iTextField nameField;
        private JButton searchButton;
        private String category;
        private TCTable table;
        private String[] titles = new String[] { "序号", "名称", "对象" };

        public SymbolDialog(String category) {
            super(true);
            this.category = category;
            initUI();
        }

        private void initUI() {
        	setTitle("选择Symbol");
            setBounds(100, 100, 450, 400);
            getContentPane().setLayout(new BorderLayout());
            contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            getContentPane().add(contentPanel, BorderLayout.CENTER);
            contentPanel.setLayout(new BorderLayout(0, 0));
            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
            {
                JPanel panel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
                panel.setBorder(new TitledBorder(null, "搜索", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                contentPanel.add(panel, BorderLayout.NORTH);
                panel.setBackground(Color.white);

                JLabel nameLabel = new JLabel("名称:", JLabel.RIGHT);
                nameLabel.setPreferredSize(new Dimension(100, height));
                nameField = new iTextField();
                nameField.setPreferredSize(new Dimension(150, height));

                JButton searchButton = new JButton("查询");
                searchButton.setPreferredSize(new Dimension(70, height));
                searchButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                searchAction();
                            }
                        }).start();
                    }
                });

                panel.add("1.1.right.center.preferred.preferred", nameLabel);
                panel.add("1.2.left.center.preferred.preferred", nameField);
                panel.add("1.3.right.center.preferred.preferred", searchButton);
            }
            {
                JPanel panel = new JPanel();
                panel.setBorder(new TitledBorder(null, "Symbol列表", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                panel.setBackground(Color.white);
                contentPanel.add(panel, BorderLayout.CENTER);
                panel.setLayout(new BorderLayout(0, 0));

                table = new TCTable(session, titles) {
                    public boolean isCellEditable(int arg0, int arg1) {
                        return false;
                    }
                };

                JScrollPane scrollTablePanel = new JScrollPane();
                scrollTablePanel.setBackground(Color.WHITE);
                scrollTablePanel.setPreferredSize(new Dimension(430, 300));
                scrollTablePanel.setViewportView(table);
                table.getTableHeader().setBackground(Color.WHITE);
                table.setRowHeight(30);
                hideTableColumn(table, 2);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                table.getTableHeader().setResizingAllowed(true);
                table.getTableHeader().setReorderingAllowed(false);
                panel.add(scrollTablePanel, BorderLayout.CENTER);

            }
            {
                JPanel buttonPanel = new JPanel(new ButtonLayout(ButtonLayout.HORIZONTAL, ButtonLayout.CENTER, 20));
                JButton okButton = new JButton("确定");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                confirmAction();
                            }

                        });
                        int row = table.getSelectedRow();
                        if (row >= 0) {
                            dispose();
                        }
                    }
                });
                okButton.setActionCommand("OK");
                buttonPanel.add(okButton);
                getRootPane().setDefaultButton(okButton);

                JButton downLoadButton = new JButton("下载");
                downLoadButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                downLoadAction();
                            }
                        }).start();

                    }
                });
                downLoadButton.setActionCommand("DownLoad");
                buttonPanel.add(downLoadButton);

                JButton cancelButton = new JButton("取消");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPanel.add(cancelButton);
                contentPanel.add(buttonPanel, BorderLayout.PAGE_END);
            }
            centerToScreen();
            setVisible(true);
        }

        private void searchAction() {
            table.removeAllRows();
            if (!Utils.isNull(category)) {
                List<TCComponentDataset> datasetList = new ArrayList<>();
                String[] keys = new String[]{Utils.getTextValue("Type"), Utils.getTextValue("OwningUser"),
                        Utils.getTextValue("Name")};
                String[] values = new String[]{"Folder", "infodba (infodba)", "SYMBOL"};
                List<InterfaceAIFComponent> list = Utils.search("General...", keys, values);
                TCComponentFolder categoryFolder = null;
                try {
                    if (list != null && list.size() > 0) {
                        TCComponentFolder symbolfolder = (TCComponentFolder) list.get(0);
                        TCComponent[] coms = symbolfolder.getRelatedComponents("contents");
                        if (coms != null && coms.length > 0) {
                            for (TCComponent tcComponent : coms) {
                                if (tcComponent instanceof TCComponentFolder
                                        && category.toUpperCase().equals(tcComponent.getProperty("object_name"))) {
                                    categoryFolder = (TCComponentFolder) tcComponent;
                                    break;
                                }
                            }
                        }
                    }
                    if (categoryFolder != null) {
                        TCComponent[] coms = categoryFolder.getRelatedComponents("contents");
                        if (coms != null && coms.length > 0) {
                            for (TCComponent tcComponent : coms) {
                                if (tcComponent instanceof TCComponentDataset) {
                                    datasetList.add((TCComponentDataset) tcComponent);
                                }
                            }
                        }
                    }
                    String name = nameField.getText().trim();
                    List<TCComponentDataset> symbolList = new ArrayList<>();
                    if (datasetList.size() > 0) {
                        for (TCComponentDataset dataset : datasetList) {
                            String datasetName = dataset.getProperty("object_name");
                            if (datasetName.contains(name)) {
                                symbolList.add(dataset);
                            }
                        }
                    }
                    if (symbolList.size() > 0) {
                        for (int i = 0; i < symbolList.size(); i++) {
                            TCComponentDataset dataset = symbolList.get(i);
                            table.addRow(new Object[]{i + 1, dataset.getProperty("object_name"), dataset});
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        private void confirmAction() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                TCComponentDataset dataset = (TCComponentDataset) table.getValueAt(row, 2);
                String filePath = DownloadDataset.downloadFile(dataset, true);
                symbolField.setText(filePath);
                symbolDataset = dataset;
            }
        }

        private void downLoadAction() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("选择下载路径");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    TCComponentDataset dataset = (TCComponentDataset) table.getValueAt(row, 2);
                    DownloadDataset.downloadFile(dataset, true, file.getPath());
                    dispose();
                }
            }
        }
    }

    class DellSymbolDialog extends AbstractAIFDialog {
        private final JPanel contentPanel = new JPanel();
        //		private int height = 25;
        private iTextField nameField;
        private JButton searchButton;
        private String category;
        private TCTable table;
        private String[] titles = new String[] { "序号", "名称", "对象" };

        public DellSymbolDialog(String category) {
            super(true);
            this.category = category;
            initUI();
        }

        private void initUI() {
            setTitle("选择DellSymbol");
            setBounds(100, 100, 450, 400);
            getContentPane().setLayout(new BorderLayout());
            contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            getContentPane().add(contentPanel, BorderLayout.CENTER);
            contentPanel.setLayout(new BorderLayout(0, 0));
            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
            {
                JPanel panel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
                panel.setBorder(new TitledBorder(null, "搜索", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                contentPanel.add(panel, BorderLayout.NORTH);
                panel.setBackground(Color.white);

                JLabel nameLabel = new JLabel("名称:", JLabel.RIGHT);
                nameLabel.setPreferredSize(new Dimension(100, height));
                nameField = new iTextField();
                nameField.setPreferredSize(new Dimension(150, height));

                JButton searchButton = new JButton("查询");
                searchButton.setPreferredSize(new Dimension(70, height));
                searchButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                searchAction();
                            }
                        }).start();
                    }
                });

                panel.add("1.1.right.center.preferred.preferred", nameLabel);
                panel.add("1.2.left.center.preferred.preferred", nameField);
                panel.add("1.3.right.center.preferred.preferred", searchButton);
            }
            {
                JPanel panel = new JPanel();
                panel.setBorder(
                		new TitledBorder(null, "DellSymbol列表", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                panel.setBackground(Color.white);
                contentPanel.add(panel, BorderLayout.CENTER);
                panel.setLayout(new BorderLayout(0, 0));

                table = new TCTable(session, titles) {
                    public boolean isCellEditable(int arg0, int arg1) {
                        return false;
                    }
                };

                JScrollPane scrollTablePanel = new JScrollPane();
                scrollTablePanel.setBackground(Color.WHITE);
                scrollTablePanel.setPreferredSize(new Dimension(430, 300));
                scrollTablePanel.setViewportView(table);
                table.getTableHeader().setBackground(Color.WHITE);
                table.setRowHeight(30);
                hideTableColumn(table, 2);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                table.getTableHeader().setResizingAllowed(true);
                table.getTableHeader().setReorderingAllowed(false);
                panel.add(scrollTablePanel, BorderLayout.CENTER);

            }
            {
                JPanel buttonPanel = new JPanel(new ButtonLayout(ButtonLayout.HORIZONTAL, ButtonLayout.CENTER, 20));
                JButton okButton = new JButton("确定");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                confirmAction();
                            }

                        });
                        int row = table.getSelectedRow();
                        if (row >= 0) {
                            dispose();
                        }
                    }
                });
                okButton.setActionCommand("OK");
                buttonPanel.add(okButton);
                getRootPane().setDefaultButton(okButton);

                JButton downLoadButton = new JButton("下载");
                downLoadButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                downLoadAction();
                            }
                        }).start();

                    }
                });
                downLoadButton.setActionCommand("DownLoad");
                buttonPanel.add(downLoadButton);

                JButton cancelButton = new JButton("取消");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPanel.add(cancelButton);
                contentPanel.add(buttonPanel, BorderLayout.PAGE_END);
            }
            centerToScreen();
            setVisible(true);
        }

        private void searchAction() {
            table.removeAllRows();
            if (!Utils.isNull(category)) {
                List<TCComponentDataset> datasetList = new ArrayList<>();
                String[] keys = new String[]{Utils.getTextValue("Type"), Utils.getTextValue("OwningUser"),
                        Utils.getTextValue("Name")};
                String[] values = new String[]{"Folder", "infodba (infodba)", "DELLSYMBOL"};
                List<InterfaceAIFComponent> list = Utils.search("General...", keys, values);
                TCComponentFolder categoryFolder = null;
                try {
                    if (list != null && list.size() > 0) {
                        TCComponentFolder dellSymbolfolder = (TCComponentFolder) list.get(0);
                        TCComponent[] coms = dellSymbolfolder.getRelatedComponents("contents");
                        if (coms != null && coms.length > 0) {
                            for (TCComponent tcComponent : coms) {
                                if (tcComponent instanceof TCComponentFolder
                                        && category.toUpperCase().equals(tcComponent.getProperty("object_name"))) {
                                    categoryFolder = (TCComponentFolder) tcComponent;
                                    break;
                                }
                            }
                        }
                    }
                    if (categoryFolder != null) {
                        TCComponent[] coms = categoryFolder.getRelatedComponents("contents");
                        if (coms != null && coms.length > 0) {
                            for (TCComponent tcComponent : coms) {
                                if (tcComponent instanceof TCComponentDataset) {
                                    datasetList.add((TCComponentDataset) tcComponent);
                                }
                            }
                        }
                    }
                    String name = nameField.getText().trim();
                    List<TCComponentDataset> symbolList = new ArrayList<>();
                    if (datasetList.size() > 0) {
                        for (TCComponentDataset dataset : datasetList) {
                            String datasetName = dataset.getProperty("object_name");
                            if (datasetName.contains(name)) {
                                symbolList.add(dataset);
                            }
                        }
                    }
                    if (symbolList.size() > 0) {
                        for (int i = 0; i < symbolList.size(); i++) {
                            TCComponentDataset dataset = symbolList.get(i);
                            table.addRow(new Object[]{i + 1, dataset.getProperty("object_name"), dataset});
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void confirmAction() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                TCComponentDataset dataset = (TCComponentDataset) table.getValueAt(row, 2);
                String filePath = DownloadDataset.downloadFile(dataset, true);
                dellField.setText(filePath);
                dellSymbolDataset = dataset;
            }
        }

        private void downLoadAction() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("选择下载路径");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    TCComponentDataset dataset = (TCComponentDataset) table.getValueAt(row, 2);
                    DownloadDataset.downloadFile(dataset, true, file.getPath());
                    dispose();
                }
            }
        }

    }

    class FootprintDialog extends AbstractAIFDialog {
        private final JPanel contentPanel = new JPanel();
        //		private int height = 25;
        private iTextField nameField;
        private JButton searchButton;
        private String footprintCategory;
        private TCTable table;
        private String[] titles = new String[] { "序号", "名称", "对象" };

        public FootprintDialog(String footprintCategory) {
            super(true);
            this.footprintCategory = footprintCategory;
            initUI();
        }

        private void initUI() {
        	setTitle("选择Footprint");
            setBounds(100, 100, 450, 400);
            getContentPane().setLayout(new BorderLayout());
            contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            getContentPane().add(contentPanel, BorderLayout.CENTER);
            contentPanel.setLayout(new BorderLayout(0, 0));
            TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
            {
                JPanel panel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
                panel.setBorder(new TitledBorder(null, "搜索", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                contentPanel.add(panel, BorderLayout.NORTH);
                panel.setBackground(Color.white);

                JLabel nameLabel = new JLabel("名称:", JLabel.RIGHT);
                nameLabel.setPreferredSize(new Dimension(100, height));
                nameField = new iTextField();
                nameField.setPreferredSize(new Dimension(150, height));

                JButton searchButton = new JButton("查询");
                searchButton.setPreferredSize(new Dimension(70, height));
                searchButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                searchAction();
                            }
                        }).start();
                    }
                });

                panel.add("1.1.right.center.preferred.preferred", nameLabel);
                panel.add("1.2.left.center.preferred.preferred", nameField);
                panel.add("1.3.right.center.preferred.preferred", searchButton);
            }
            {
                JPanel panel = new JPanel();
                panel.setBorder(
                		new TitledBorder(null, "Footprint列表", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                panel.setBackground(Color.white);
                contentPanel.add(panel, BorderLayout.CENTER);
                panel.setLayout(new BorderLayout(0, 0));

                table = new TCTable(session, titles) {
                    public boolean isCellEditable(int arg0, int arg1) {
                        return false;
                    }
                };

                JScrollPane scrollTablePanel = new JScrollPane();
                scrollTablePanel.setBackground(Color.WHITE);
                scrollTablePanel.setPreferredSize(new Dimension(430, 300));
                scrollTablePanel.setViewportView(table);
                table.getTableHeader().setBackground(Color.WHITE);
                table.setRowHeight(30);
                hideTableColumn(table, 2);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                table.getTableHeader().setResizingAllowed(true);
                table.getTableHeader().setReorderingAllowed(false);
                panel.add(scrollTablePanel, BorderLayout.CENTER);

            }
            {
                JPanel buttonPanel = new JPanel(new ButtonLayout(ButtonLayout.HORIZONTAL, ButtonLayout.CENTER, 20));
                JButton okButton = new JButton("确定");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                confirmAction();
                            }

                        });
                        int row = table.getSelectedRow();
                        if (row >= 0) {
                            dispose();
                        }
                    }
                });
                okButton.setActionCommand("OK");
                buttonPanel.add(okButton);
                getRootPane().setDefaultButton(okButton);

                JButton downLoadButton = new JButton("下载");
                downLoadButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                downLoadAction();
                            }
                        }).start();

                    }
                });
                downLoadButton.setActionCommand("DownLoad");
                buttonPanel.add(downLoadButton);

                JButton cancelButton = new JButton("取消");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        dispose();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPanel.add(cancelButton);
                contentPanel.add(buttonPanel, BorderLayout.PAGE_END);
            }
            centerToScreen();
            setVisible(true);
        }

        private void searchAction() {
            table.removeAllRows();
            if (!Utils.isNull(footprintCategory)) {
                List<TCComponentDataset> datasetList = new ArrayList<>();
                String[] keys = new String[]{Utils.getTextValue("Type"), Utils.getTextValue("OwningUser"),
                        Utils.getTextValue("Name")};
                String[] values = new String[]{"Folder", "infodba (infodba)", "FOOTPRINT"};
                List<InterfaceAIFComponent> list = Utils.search("General...", keys, values);
                TCComponentFolder categoryFolder = null;
                try {
                    if (list != null && list.size() > 0) {
                        TCComponentFolder footprintfolder = (TCComponentFolder) list.get(0);
                        TCComponent[] coms = footprintfolder.getRelatedComponents("contents");
                        if (coms != null && coms.length > 0) {
                            for (TCComponent tcComponent : coms) {
                                if (tcComponent instanceof TCComponentFolder && footprintCategory.toUpperCase()
                                        .equals(tcComponent.getProperty("object_name"))) {
                                    categoryFolder = (TCComponentFolder) tcComponent;
                                    break;
                                }
                            }
                        }
                    }
                    if (categoryFolder != null) {
                        TCComponent[] coms = categoryFolder.getRelatedComponents("contents");
                        if (coms != null && coms.length > 0) {
                            for (TCComponent tcComponent : coms) {
                                if (tcComponent instanceof TCComponentDataset) {
                                    datasetList.add((TCComponentDataset) tcComponent);
                                }
                            }
                        }
                    }
                    String name = nameField.getText().trim();
                    List<TCComponentDataset> symbolList = new ArrayList<>();
                    if (datasetList.size() > 0) {
                        for (TCComponentDataset dataset : datasetList) {
                            String datasetName = dataset.getProperty("object_name");
                            if (datasetName.contains(name)) {
                                symbolList.add(dataset);
                            }
                        }
                    }
                    if (symbolList.size() > 0) {
                        for (int i = 0; i < symbolList.size(); i++) {
                            TCComponentDataset dataset = symbolList.get(i);
                            table.addRow(new Object[]{i + 1, dataset.getProperty("object_name"), dataset});
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void confirmAction() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                TCComponentDataset dataset = (TCComponentDataset) table.getValueAt(row, 2);
                String filePath = DownloadDataset.downloadFile(dataset, true);
                footprintField.setText(filePath);
                footprintDataset = dataset;
            }
        }

        private void downLoadAction() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("选择下载路径");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    TCComponentDataset dataset = (TCComponentDataset) table.getValueAt(row, 2);
                    DownloadDataset.downloadFile(dataset, true, file.getPath());
                    dispose();
                }
            }
        }

    }

    /**
	 * 隐藏表格中的列 Create by 郑良 2019.05.10
	 * 
	 * @param table 表格
	 * @param table 需隐藏的列
	 */
    public void hideTableColumn(TCTable table, int column) {
        TableColumn tc = table.getTableHeader().getColumnModel().getColumn(column);
        tc.setMaxWidth(0);
        tc.setPreferredWidth(0);
        tc.setWidth(0);
        tc.setMinWidth(0);
        table.getTableHeader().getColumnModel().getColumn(column).setMaxWidth(0);
        table.getTableHeader().getColumnModel().getColumn(column).setMinWidth(0);
    }

    public JPanel getEDACompPanel() {
        JPanel EDACompPanel = new JPanel();
        try {
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            EDACompPanel.add(mainPanel, BorderLayout.WEST);
            mainPanel.setBackground(Color.white);
            {
                JPanel panel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
                panel.setBorder(
                        new TitledBorder(null, "HW Property", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                mainPanel.add(panel);
                panel.setBackground(Color.white);

                JLabel categoryLabel = new JLabel("Category:", JLabel.RIGHT);
                categoryLabel.setPreferredSize(new Dimension(150, height));
                categoryLov = new LOVComboBox();
                categoryLov.setPreferredSize(new Dimension(200, height));
                categoryLov.setSelectedItem(itemRev.getProperty("fx8_Category"));
                List<String> categoryList = getCategoryList();
                for (String category : categoryList) {
                    categoryLov.addItem(category);
                }

                JLabel partTypeLabel = new JLabel("PartType:", JLabel.RIGHT);
                partTypeLabel.setPreferredSize(new Dimension(150, height));
                partTypeLov = new LOVComboBox();
                partTypeLov.setPreferredSize(new Dimension(200, height));
                partTypeLov.setSelectedItem(itemRev.getProperty("fx8_PartType"));

                categoryLov.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String category = categoryLov.getSelectedItem().toString();
                        if (!Utils.isNull(category)) {
                            partTypeLov.removeAllItems();
                            partTypeLov.setSelectedItem("");
                            List<String> partTypeList = getPartTypeList(category);
                            if (partTypeList != null && partTypeList.size() > 0) {
                                for (String partType : partTypeList) {
                                    partTypeLov.addItem(partType);
                                }
                            }
                        }

                    }
                });

                JLabel MfgPNLabel = new JLabel("Mfr PN:", JLabel.RIGHT);
                MfgPNLabel.setPreferredSize(new Dimension(150, height));
                MfgPNField = new HHTextField(fileStreamUtil, printStream);
                MfgPNField.setPreferredSize(new Dimension(200, height));
                MfgPNField.setText(itemRev.getProperty("fx8_MfrPN"));

                JLabel projectLabel = new JLabel("Project:", JLabel.RIGHT);
                projectLabel.setPreferredSize(new Dimension(150, height));
                projectLov = new LOVComboBox();
                projectLov.setPreferredSize(new Dimension(200, height));
                String userid = session.getUser().getUserId();
                Utils.setByPass(true, session);
                String[] keys2 = new String[]{Utils.getTextValue("Active"), Utils.getTextValue("user_id")};
                String[] values2 = new String[]{"True", userid};
                List<InterfaceAIFComponent> projectList = Utils.search("__FX_FindProject", keys2, values2);

                if (projectList != null && projectList.size() > 0) {

                    for (InterfaceAIFComponent interfaceAIFComponent : projectList) {
                        try {
                            String projectName = ((TCComponentProject) interfaceAIFComponent)
                                    .getProperty("project_name");
                            AIFComponentContext[] parentContext = ((TCComponentProject) interfaceAIFComponent)
                                    .whereReferenced();
                            if (parentContext != null && parentContext.length > 0) {
                                for (int i = 0; i < parentContext.length; i++) {
                                    InterfaceAIFComponent component = parentContext[i].getComponent();
                                    if ("Prg0ProgramPlan".equals(component.getType())) {
                                        String programPlanName = component.getProperty("object_name");
                                        projectMap.put(projectName, programPlanName);
                                        projectLov.addItem(programPlanName);
                                    }
                                }
                            }
                        } catch (TCException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Utils.setByPass(false, session);
                String projectName = itemRev.getProperty("fx8_PrjName");
//				if(!Utils.isNull(projectName)){
//					String[] proectKeys = new String[] { Utils.getTextValue("ProjectName")};
//					String[] proectValues = new String[] { projectName};
//					List<InterfaceAIFComponent> oldProjectList = Utils.search("__FX_FindProject", proectKeys, proectValues);
//					if(oldProjectList!=null&&oldProjectList.size()>0){
//						oldProject = (TCComponentProject)oldProjectList.get(0);
//					}
//				}
                projectLov.setSelectedItem(projectName);

                JLabel updateTimeLabel = new JLabel("Update Time:", JLabel.RIGHT);
                updateTimeLabel.setPreferredSize(new Dimension(150, height));
                updateTime = new HHDateButton(simpleFormat);
                updateTime.setPreferredSize(new Dimension(200, height));

                String updateTimeStr = itemRev.getProperty("fx8_UpdatedTime");
                System.out.println("updateTimeStr==" + updateTimeStr);
                if (!Utils.isNull(updateTimeStr)) {
                    try {
                        updateTime.setDate(simpleFormat.parse(updateTimeStr));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Date updateTimeDate = null;
                    updateTime.setDate(updateTimeDate);
                }

                JLabel MfgLabel = new JLabel("Mfr:", JLabel.RIGHT);
                MfgLabel.setPreferredSize(new Dimension(150, height));
                MfgField = new HHTextField(fileStreamUtil, printStream);
                MfgField.setPreferredSize(new Dimension(200, height));
                MfgField.setText(itemRev.getProperty("fx8_Mfr"));

                MgfButton = new JButton("Select");
                MgfButton.setPreferredSize(new Dimension(100, height));
                MgfButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                AssignMfgDialog dialog = new AssignMfgDialog();
                            }
                        }).start();

                    }
                });

                JLabel dataSheetLabel = new JLabel("Data Sheet:", JLabel.RIGHT);
                dataSheetLabel.setPreferredSize(new Dimension(150, height));
                dataSheetField = new HHTextField(fileStreamUtil, printStream);
                dataSheetField.setPreferredSize(new Dimension(200, height));
                dataSheetField.setText(itemRev.getProperty("fx8_DataSheet"));

                dataSheetButton = new JButton("Choose");
                dataSheetButton.setPreferredSize(new Dimension(100, height));
                TCComponent dataSheetCom = itemRev.getRelatedComponent(RelationName.DATASHEET);
                if (dataSheetCom != null && dataSheetCom instanceof TCComponentDataset) {
                    dataSheetDataset = (TCComponentDataset) dataSheetCom;
                }
                dataSheetButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String mfg = MfgField.getText().trim();
                        String mfgPN = MfgPNField.getText().trim();
                        if (Utils.isNull(mfgPN)) {
                        	Utils.infoMessage("Manufactuer PN属性栏位为空，请填写!");
                            return;
                        }
                        if (Utils.isNull(mfg)) {
                        	Utils.infoMessage("Manufactuer属性栏位为空，请填写!");
                            return;
                        }
                        new Thread(new Runnable() {
                            public void run() {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setDialogTitle("选择DataSheet");
                                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                PDFFileFilter pdfFilter = new PDFFileFilter();
                                fileChooser.addChoosableFileFilter(pdfFilter);
                                fileChooser.setFileFilter(pdfFilter);
                                int result = fileChooser.showSaveDialog(AIFUtility.getActiveDesktop().getFrame());
                                if (result == JFileChooser.APPROVE_OPTION) {
                                    File file = fileChooser.getSelectedFile();
                                    dataSheetField.setText(file.getPath());
                                    dataSheetDataset = null;
                                }
                            }
                        }).start();

                    }
                });

                JLabel dellLabel = new JLabel("Dell Symbol:", JLabel.RIGHT);
                dellLabel.setPreferredSize(new Dimension(150, height));
                dellField = new HHTextField(fileStreamUtil, printStream);
                dellField.setPreferredSize(new Dimension(200, height));
                dellField.setText(itemRev.getProperty("fx8_DellSymbol"));

                dellChooseButton = new JButton("Choose");
                dellChooseButton.setPreferredSize(new Dimension(100, height));
                TCComponent dellSymbolCom = itemRev.getRelatedComponent(RelationName.DELLSYMBOLREL);
                if (dellSymbolCom != null && dellSymbolCom instanceof TCComponentDataset) {
                    dellSymbolDataset = (TCComponentDataset) dellSymbolCom;
                }
                dellSelectButton = new JButton("Select");
                dellSelectButton.setPreferredSize(new Dimension(100, height));
                dellChooseButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String category = categoryLov.getSelectedItem().toString();

                        if (Utils.isNull(category)) {
                        	Utils.infoMessage("Category属性栏位为空，请填写!");
                            return;
                        }
                        new Thread(new Runnable() {
                            public void run() {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setDialogTitle("选择Dell Symbol");
                                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                OLBFileFilter olbFilter = new OLBFileFilter();
                                fileChooser.addChoosableFileFilter(olbFilter);
                                fileChooser.setFileFilter(olbFilter);
                                int result = fileChooser.showSaveDialog(AIFUtility.getActiveDesktop().getFrame());
                                if (result == JFileChooser.APPROVE_OPTION) {
                                    File file = fileChooser.getSelectedFile();
                                    dellField.setText(file.getPath());
                                    dellSymbolDataset = null;
                                }
                            }
                        }).start();

                    }
                });
                dellSelectButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final String category = categoryLov.getSelectedItem().toString();

                        if (Utils.isNull(category)) {
                        	Utils.infoMessage("Category属性栏位为空，请填写!");
                            return;
                        }
                        new Thread(new Runnable() {
                            public void run() {
                                DellSymbolDialog dialog = new DellSymbolDialog(category);
                            }
                        }).start();

                    }
                });

                JLabel symbolLabel = new JLabel("Symbol:", JLabel.RIGHT);
                symbolLabel.setPreferredSize(new Dimension(150, height));
                symbolField = new HHTextField(fileStreamUtil, printStream);
                symbolField.setPreferredSize(new Dimension(200, height));
                symbolField.setText(itemRev.getProperty("fx8_Symbol"));

                symbolChooseButton = new JButton("Choose");
                symbolChooseButton.setPreferredSize(new Dimension(100, height));
                TCComponent symbolCom = itemRev.getRelatedComponent(RelationName.SYMBOLREL);
                if (symbolCom != null && symbolCom instanceof TCComponentDataset) {
                    symbolDataset = (TCComponentDataset) symbolCom;
                }

                symbolSelectButton = new JButton("Select");
                symbolSelectButton.setPreferredSize(new Dimension(100, height));
                symbolChooseButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String category = categoryLov.getSelectedItem().toString();

                        if (Utils.isNull(category)) {
                        	Utils.infoMessage("Category属性栏位为空，请填写!");
                            return;
                        }
                        new Thread(new Runnable() {
                            public void run() {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setDialogTitle("选择Symbol");
                                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                OLBFileFilter olbFilter = new OLBFileFilter();
                                fileChooser.addChoosableFileFilter(olbFilter);
                                fileChooser.setFileFilter(olbFilter);
                                int result = fileChooser.showSaveDialog(AIFUtility.getActiveDesktop().getFrame());
                                if (result == JFileChooser.APPROVE_OPTION) {
                                    File file = fileChooser.getSelectedFile();
                                    symbolField.setText(file.getPath());
                                    symbolDataset = null;
                                }
                            }
                        }).start();

                    }
                });
                symbolSelectButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final String category = categoryLov.getSelectedItem().toString();

                        if (Utils.isNull(category)) {
                        	Utils.infoMessage("Category属性栏位为空，请填写!");
                            return;
                        }
                        new Thread(new Runnable() {
                            public void run() {
                                SymbolDialog dialog = new SymbolDialog(category);
                            }
                        }).start();

                    }
                });

                panel.add("1.1.right.center.preferred.preferred", categoryLabel);
                panel.add("1.2.left.center.preferred.preferred", categoryLov);

                JLabel spaceLable = new JLabel("", JLabel.RIGHT);
                spaceLable.setPreferredSize(new Dimension(150, height));
                panel.add("1.3.right.center.preferred.preferred", spaceLable);

                panel.add("1.4.right.center.preferred.preferred", partTypeLabel);
                panel.add("1.5.left.center.preferred.preferred", partTypeLov);

                panel.add("2.1.right.center.preferred.preferred", MfgLabel);
                panel.add("2.2.left.center.preferred.preferred", MfgField);
                panel.add("2.3.left.center.preferred.preferred", MgfButton);
                JLabel spaceLable2 = new JLabel("", JLabel.RIGHT);
                spaceLable2.setPreferredSize(new Dimension(50, height));
                panel.add("2.4.right.center.preferred.preferred", spaceLable2);
                panel.add("2.5.right.center.preferred.preferred", MfgPNLabel);
                panel.add("2.6.left.center.preferred.preferred", MfgPNField);

                panel.add("3.1.right.center.preferred.preferred", projectLabel);
                panel.add("3.2.left.center.preferred.preferred", projectLov);

                JLabel spaceLable3 = new JLabel("", JLabel.RIGHT);
                spaceLable3.setPreferredSize(new Dimension(150, height));
                panel.add("3.3.right.center.preferred.preferred", spaceLable3);
                panel.add("3.4.right.center.preferred.preferred", updateTimeLabel);
                panel.add("3.5.left.center.preferred.preferred", updateTime);

                panel.add("4.1.right.center.preferred.preferred", dataSheetLabel);
                panel.add("4.2.left.center.preferred.preferred", dataSheetField);
                panel.add("4.3.left.center.preferred.preferred", dataSheetButton);

                panel.add("5.1.right.center.preferred.preferred", dellLabel);
                panel.add("5.2.left.center.preferred.preferred", dellField);
                panel.add("5.3.left.center.preferred.preferred", dellChooseButton);
                panel.add("5.4.left.center.preferred.preferred", dellSelectButton);

                panel.add("6.1.right.center.preferred.preferred", symbolLabel);
                panel.add("6.2.left.center.preferred.preferred", symbolField);
                panel.add("6.3.left.center.preferred.preferred", symbolChooseButton);
                panel.add("6.4.left.center.preferred.preferred", symbolSelectButton);
            }
            {

                JPanel panel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
                panel.setBorder(
                        new TitledBorder(null, "CE Property", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                mainPanel.add(panel);
                panel.setBackground(Color.white);

                JLabel HHPNLabel = new JLabel("StandardPN:", JLabel.RIGHT);
                HHPNLabel.setPreferredSize(new Dimension(150, height));
                HHPNField = new HHTextField(fileStreamUtil, printStream);
                HHPNField.setPreferredSize(new Dimension(200, height));
                HHPNField.setText(itemRev.getProperty("fx8_StandardPN"));

                panel.add("1.1.right.center.preferred.preferred", HHPNLabel);
                panel.add("1.2.left.center.preferred.preferred", HHPNField);

                editButton = new JButton("Edit");
                editButton.setPreferredSize(new Dimension(100, height));
                editButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        HHPNField.setEditable(true);
                    }
                });
                panel.add("1.3.left.center.preferred.preferred", editButton);
                JLabel spaceLable = new JLabel("", JLabel.RIGHT);
                spaceLable.setPreferredSize(new Dimension(400, height));
                panel.add("1.4.right.center.preferred.preferred", spaceLable);

            }

            {
                JPanel panel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
                panel.setBorder(
                        new TitledBorder(null, "Layout Property", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                mainPanel.add(panel);
                panel.setBackground(Color.white);

                JLabel footprintCLabel = new JLabel("Footprint Category:", JLabel.RIGHT);
                footprintCLabel.setPreferredSize(new Dimension(150, height));
                footprintLov = new LOVComboBox();
                footprintLov.setPreferredSize(new Dimension(200, height));
                footprintLov.setSelectedItem(itemRev.getProperty("fx8_FootprintCategory"));
                List<String> footprintCategoryList = getCategoryList();
                for (String footprintCategory : footprintCategoryList) {
                    footprintLov.addItem(footprintCategory);
                }

                JLabel PADLabel = new JLabel("PAD:", JLabel.RIGHT);
                PADLabel.setPreferredSize(new Dimension(150, height));
                PADField = new HHTextField(fileStreamUtil, printStream);
                PADField.setPreferredSize(new Dimension(200, height));
                PADField.setText(itemRev.getProperty("fx8_PAD"));

                PADChooseButton = new JButton("Choose");
                PADChooseButton.setPreferredSize(new Dimension(100, height));
                TCComponent PADCom = itemRev.getRelatedComponent(RelationName.PAD);
                if (PADCom != null && PADCom instanceof TCComponentDataset) {
                    PADDataset = (TCComponentDataset) PADCom;
                }

                PADChooseButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String category = footprintLov.getSelectedItem().toString();

                        if (Utils.isNull(category)) {
                        	Utils.infoMessage("Footprint Category属性栏位为空，请填写!");
                            return;
                        }
                        new Thread(new Runnable() {
                            public void run() {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setDialogTitle("选择PAD");
                                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                ZIPFileFilter zipFilter = new ZIPFileFilter();
                                fileChooser.addChoosableFileFilter(zipFilter);
                                fileChooser.setFileFilter(zipFilter);
                                int result = fileChooser.showSaveDialog(AIFUtility.getActiveDesktop().getFrame());
                                if (result == JFileChooser.APPROVE_OPTION) {
                                    File file = fileChooser.getSelectedFile();
                                    PADField.setText(file.getPath());
                                    PADDataset = null;
                                }
                            }
                        }).start();

                    }
                });

                JLabel footprintLabel = new JLabel("Footprint:", JLabel.RIGHT);
                footprintLabel.setPreferredSize(new Dimension(150, height));
                footprintField = new HHTextField(fileStreamUtil, printStream);
                footprintField.setPreferredSize(new Dimension(200, height));
                footprintField.setText(itemRev.getProperty("fx8_Footprint"));

                footprintChooseButton = new JButton("Choose");
                footprintChooseButton.setPreferredSize(new Dimension(100, height));
                TCComponent footprintCom = itemRev.getRelatedComponent(RelationName.FOOTPRINT);
                if (footprintCom != null && footprintCom instanceof TCComponentDataset) {
                    footprintDataset = (TCComponentDataset) footprintCom;
                }

                footprintChooseButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String category = categoryLov.getSelectedItem().toString();

                        if (Utils.isNull(category)) {
                        	Utils.infoMessage("Category属性栏位为空，请填写!");
                            return;
                        }
                        new Thread(new Runnable() {
                            public void run() {
                                JFileChooser fileChooser = new JFileChooser();
                                fileChooser.setDialogTitle("选择Footprint");
                                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                FootprintZIPFileFilter zipFilter = new FootprintZIPFileFilter();
                                fileChooser.addChoosableFileFilter(zipFilter);
                                fileChooser.setFileFilter(zipFilter);
                                int result = fileChooser.showSaveDialog(AIFUtility.getActiveDesktop().getFrame());
                                if (result == JFileChooser.APPROVE_OPTION) {
                                    File file = fileChooser.getSelectedFile();
                                    footprintField.setText(file.getPath());
                                    footprintDataset = null;
                                }
                            }
                        }).start();

                    }
                });
                footprintSelectButton = new JButton("Select");
                footprintSelectButton.setPreferredSize(new Dimension(100, height));
                footprintSelectButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final String footprintCategory = footprintLov.getSelectedItem().toString();

                        if (Utils.isNull(footprintCategory)) {
                            Utils.infoMessage("Footprint Category属性栏位为空，请填写!");
                            return;
                        }
                        new Thread(new Runnable() {
                            public void run() {
                                FootprintDialog dialog = new FootprintDialog(footprintCategory);
                            }
                        }).start();

                    }
                });

                panel.add("1.1.right.center.preferred.preferred", footprintCLabel);
                panel.add("1.2.left.center.preferred.preferred", footprintLov);
                JLabel spaceLable = new JLabel("", JLabel.RIGHT);
                spaceLable.setPreferredSize(new Dimension(50, height));
                panel.add("1.3.right.center.preferred.preferred", spaceLable);
                panel.add("1.4.right.center.preferred.preferred", PADLabel);
                panel.add("1.5.left.center.preferred.preferred", PADField);
                panel.add("1.6.left.center.preferred.preferred", PADChooseButton);

                panel.add("2.1.right.center.preferred.preferred", footprintLabel);
                panel.add("2.2.left.center.preferred.preferred", footprintField);
                panel.add("2.3.left.center.preferred.preferred", footprintChooseButton);
                panel.add("2.4.left.center.preferred.preferred", footprintSelectButton);
                JLabel spaceLable2 = new JLabel("", JLabel.RIGHT);
                spaceLable2.setPreferredSize(new Dimension(150, height));
                panel.add("2.5.right.center.preferred.preferred", spaceLable2);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EDACompPanel;
    }
}
