package com.hh.tools.customerPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Display;

import com.hh.tools.newitem.CreateObject;
import com.hh.tools.newitem.DownloadDataset;
import com.hh.tools.newitem.RelationName;
import com.hh.tools.newitem.Utils;
import com.hh.tools.util.CISFileStorageUtil;
import com.hh.tools.util.DatasetTypeUtil;
import com.hh.tools.util.SelectFileFilter;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.classification.common.G4MUserAppContext;
import com.teamcenter.rac.classification.common.tree.G4MTree;
import com.teamcenter.rac.classification.common.tree.G4MTreeNode;
import com.teamcenter.rac.classification.icm.ClassificationService;
import com.teamcenter.rac.common.TCTable;
import com.teamcenter.rac.kernel.TCClassificationService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentICO;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.ics.ICSAdminClass;
import com.teamcenter.rac.kernel.ics.ICSApplicationObject;
import com.teamcenter.rac.kernel.ics.ICSProperty;
import com.teamcenter.rac.kernel.ics.ICSPropertyDescription;
import com.teamcenter.rac.kernel.ics.ICSView;
import com.teamcenter.rac.stylesheet.AbstractRendering;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.iButton;

public class ICSPannelRendering extends AbstractRendering implements ActionListener {

    private TCSession session;

    private TCComponentItemRevision itemRev;

    private ArrayList<ClassPanel> classPanelList = new ArrayList<>();

    private JTextField hpSymbolTextField;

    private JTextField bigHpSymbolTextField;

    private JTextField dellSymbolTextField;

    private JTextField bigDellSymbolTextField;

    private JButton hpSymbolButton;

    private JButton bigHpSymbolButton;

    private JButton dellSymbolButton;

    private JButton bigDellSymbolButton;

    private TCComponentDataset hpSymbolDataset;
    private File hpSymbolFile;

    private TCComponentDataset hpBigSymbolDataset;
    private File hpBigSymbolFile;

    private TCComponentDataset dellSymbolDataset;
    private File dellSymbolFile;

    private TCComponentDataset dellBigSymbolDataset;
    private File dellBigSymbolFile;

    private String icsId;

    JTextField valueField;

    private boolean isCanUpload = true;

    public ICSPannelRendering(TCComponentItemRevision tcComponentItemRevision) throws Exception {
        super(tcComponentItemRevision);
        itemRev = tcComponentItemRevision;
        session = itemRev.getSession();
        loadRendering();
    }

    @Override
    public void loadRendering() throws TCException {

        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setPreferredSize(new Dimension(950, 500));
        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        jScrollPane.setViewportView(mainPanel);
        this.add(jScrollPane);

        JPanel iscPanel = new JPanel();
        iscPanel.setBorder(new TitledBorder(null, "Part Datas", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        mainPanel.add(iscPanel);
        try {
            String category = itemRev.getProperty("fx8_Category");

            TCComponentICO[] icos = itemRev.getClassificationObjects();
            TCClassificationService icsServer = session.getClassificationService();
            ICSApplicationObject icsAppObj = icsServer.newICSApplicationObject("ICM");

            if (icos != null && icos.length != 0) {
                String classId = icos[0].getProperty("object_type_id");
//				System.out.println("====================classId==" + classId);
                icsId = classId;
                icsAppObj.setView(classId);
                ICSView icsView = icsAppObj.getView();
                String viewName = icsView.getName();
//				System.out.println("====================viewName==" + viewName);
                if (viewName.equals(category)) {
                    ICSPropertyDescription icsPropertyDescription = null;
                    ICSProperty[] icsProperties = icos[0].getICSProperties(true);
                    for (int i = 0; i < icsProperties.length; i++) {
                        int valueId = icsProperties[i].getId();
                        String value = icsProperties[i].getValue();
                        icsPropertyDescription = icsView.getPropertyDescription(valueId);
                        getICSPropDescInfo(icsPropertyDescription, value);
                    }
                } else {
                    addClasspropetyPanel(icsAppObj, category);
                }

            } else {
                addClasspropetyPanel(icsAppObj, category);
            }

            iscPanel.setLayout(new GridLayout((int) Math.ceil(classPanelList.size() / 2), 2));

//			//新增category和value属性
//			JPanel categoryPanel = new JPanel();
//			categoryPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//			JLabel categorylabel = new JLabel("Category:", JLabel.RIGHT);
//			categorylabel.setPreferredSize(new Dimension(200, 25));
//			categoryPanel.add(categorylabel);
//			
//			JTextField categoryField = new JTextField();
//			categoryField.setPreferredSize(new Dimension(200, 25));
//			categoryField.setText(itemRev.getProperty("fx8_Category"));
//			categoryField.setEditable(false);
//			categoryPanel.add(categoryField);
//			iscPanel.add(categoryPanel);
//			
//			JPanel valuePanel = new JPanel();
//			valuePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//			JLabel valuelabel = new JLabel("Value:", JLabel.RIGHT);
//			valuelabel.setPreferredSize(new Dimension(200, 25));
//			valuePanel.add(valuelabel);
//		
//			valueField = new JTextField();
//			valueField.setPreferredSize(new Dimension(200, 25));
//			valuePanel.add(valueField);
//			iscPanel.add(valuePanel);

            for (int i = 0; i < classPanelList.size(); i++) {
                iscPanel.add(classPanelList.get(i));
            }

            JPanel symbolPanel = new JPanel(new GridLayout(1, 2));
            mainPanel.add(symbolPanel);

            JPanel hpSymbolPanel = new JPanel(new PropertyLayout(5, 10, 10, 10, 10, 10));
            hpSymbolPanel.setPreferredSize(new Dimension(350, 50));
            hpSymbolPanel.setBorder(
                    new TitledBorder(null, "CE Check Symbol", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            symbolPanel.add(hpSymbolPanel);

            JLabel hpSymbolLabel = new JLabel("Symbol：", JLabel.RIGHT);
            hpSymbolLabel.setPreferredSize(new Dimension(100, 25));

            hpSymbolTextField = new JTextField();
            hpSymbolTextField.setEditable(false);
            hpSymbolTextField.setPreferredSize(new Dimension(250, 25));
            hpSymbolTextField.setText(itemRev.getProperty(RelationName.SYMBOL));

            hpSymbolButton = new JButton("浏览");
            hpSymbolButton.addActionListener(this);

            hpSymbolPanel.add("1.1.left.center.preferred.preferred", hpSymbolLabel);
            hpSymbolPanel.add("1.2.left.center.preferred.preferred", hpSymbolTextField);
            hpSymbolPanel.add("1.3.left.center.preferred.preferred", hpSymbolButton);

            JLabel bigHpSymbolLabel = new JLabel("Big-Symbol：", JLabel.RIGHT);
            bigHpSymbolLabel.setPreferredSize(new Dimension(100, 25));

            bigHpSymbolTextField = new JTextField();
            bigHpSymbolTextField.setEditable(false);
            bigHpSymbolTextField.setPreferredSize(new Dimension(250, 25));
            TCComponent[] refComp = itemRev.getRelatedComponents(RelationName.BIGSYMBOLREL);
            if (null != refComp && refComp.length > 0) {
                for (int i = 0; i < refComp.length; i++) {
                    TCComponent itemRefComp = refComp[i];
                    bigHpSymbolTextField.setText(itemRefComp.toDisplayString());
                }
            }

            bigHpSymbolButton = new JButton("浏览");
            bigHpSymbolButton.addActionListener(this);

            hpSymbolPanel.add("2.1.left.center.preferred.preferred", bigHpSymbolLabel);
            hpSymbolPanel.add("2.2.left.center.preferred.preferred", bigHpSymbolTextField);
            hpSymbolPanel.add("2.3.left.center.preferred.preferred", bigHpSymbolButton);

            JPanel dellSymbolPanel = new JPanel(new PropertyLayout());
            dellSymbolPanel.setBorder(
                    new TitledBorder(null, "CE Check Dell-Symbol", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            symbolPanel.add(dellSymbolPanel);

            JLabel dellSymbolLabel = new JLabel("DellSymbol：", JLabel.RIGHT);
            dellSymbolLabel.setPreferredSize(new Dimension(100, 25));

            dellSymbolTextField = new JTextField();
            dellSymbolTextField.setEditable(false);
            dellSymbolTextField.setPreferredSize(new Dimension(250, 25));
            dellSymbolTextField.setText(itemRev.getProperty(RelationName.DELLSYMBOL));

            dellSymbolButton = new JButton("浏览");
            dellSymbolButton.addActionListener(this);

            dellSymbolPanel.add("1.1.left.center.preferred.preferred", dellSymbolLabel);
            dellSymbolPanel.add("1.2.left.center.preferred.preferred", dellSymbolTextField);
            dellSymbolPanel.add("1.3.left.center.preferred.preferred", dellSymbolButton);

            JLabel bigDellSymbolLabel = new JLabel("Big-DellSymbol：", JLabel.RIGHT);
            bigDellSymbolLabel.setPreferredSize(new Dimension(100, 25));

            bigDellSymbolTextField = new JTextField();
            bigDellSymbolTextField.setEditable(false);
            bigDellSymbolTextField.setPreferredSize(new Dimension(250, 25));
            refComp = itemRev.getRelatedComponents(RelationName.DELLBIGSYMBOLREL);
            if (null != refComp && refComp.length > 0) {
                for (int i = 0; i < refComp.length; i++) {
                    TCComponent itemRefComp = refComp[i];
                    bigDellSymbolTextField.setText(itemRefComp.toDisplayString());
                }
            }

            bigDellSymbolButton = new JButton("浏览");
            bigDellSymbolButton.addActionListener(this);

            dellSymbolPanel.add("2.1.left.center.preferred.preferred", bigDellSymbolLabel);
            dellSymbolPanel.add("2.2.left.center.preferred.preferred", bigDellSymbolTextField);
            dellSymbolPanel.add("2.3.left.center.preferred.preferred", bigDellSymbolButton);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveRendering() {
        ArrayList<ICSProperty> icss = new ArrayList<ICSProperty>();
        for (ClassPanel classPanel : classPanelList) {
            String value = null;
            if (classPanel.textField != null) {
                value = classPanel.textField.getText();
            }
            if (classPanel.comboBox != null) {
                value = classPanel.comboBox.getSelectedItem().toString();
            }
            if (StringUtils.isNotBlank(value)) {
                try {
                    if (StringUtils.isNotEmpty(classPanel.refOptions)) {
                        itemRev.setProperty(classPanel.refOptions, value);
                    }
                    // 保存分类属性
                    if (classPanel.className.equals("PCB_FOOTPRINT")) {
                        value = value.substring(0, value.lastIndexOf("."));
                    }
                    ICSProperty icsProperty = new ICSProperty(classPanel.classId, value);
                    icss.add(icsProperty);
                    System.out.println("save==================" + classPanel.className + "=============" + classPanel.refOptions + "===============" + value);
                } catch (TCException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        try {
        	// 先删除之前分类信息
            TCComponentICO[] arrayICO = itemRev.getClassificationObjects();
            for (int i = 0; i < arrayICO.length; i++) {
                arrayICO[i].delete();
            }

            G4MUserAppContext G4 = new G4MUserAppContext(AIFUtility.getCurrentApplication(), icsId);
            ICSApplicationObject icsAppObj = G4.getICSApplicationObject();
            TCClassificationService tccs = G4.getClassificationService();
            ICSAdminClass icsAdminClass = tccs.newICSAdminClass();
            icsAdminClass.load(icsId);
            icsAppObj.setView(icsId);
            icsAppObj.create(itemRev.getTCProperty("item_id").toString(), itemRev.getUid());
            icsAppObj.setProperties(icss.toArray(new ICSProperty[icss.size()]));
            icsAppObj.save();

            if (hpSymbolDataset != null || hpSymbolFile != null) {
                TCComponentDataset dataset = setDateSheet(hpSymbolFile, hpSymbolDataset, RelationName.SYMBOLREL);
                if (dataset != null) {
                    String symbolName = dataset.toDisplayString();
                    itemRev.setProperty("fx8_SchematicPart", symbolName.substring(0, symbolName.lastIndexOf(".")));
                }
            }

            if (hpBigSymbolDataset != null || hpBigSymbolFile != null) {
                setDateSheet(hpBigSymbolFile, hpBigSymbolDataset, RelationName.BIGSYMBOLREL);
            }

            if (dellSymbolDataset != null || dellSymbolFile != null) {
                TCComponentDataset dataset = setDateSheet(dellSymbolFile, dellSymbolDataset,
                        RelationName.DELLSYMBOLREL);
                if (dataset != null) {
                    String symbolName = dataset.toDisplayString();
                    itemRev.setProperty("fx8_DellSchematicPart", symbolName.substring(0, symbolName.lastIndexOf(".")));
                }
            }

            if (dellBigSymbolDataset != null || dellBigSymbolFile != null) {
                setDateSheet(dellBigSymbolFile, dellBigSymbolDataset, RelationName.DELLBIGSYMBOLREL);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private TCComponentDataset setDateSheet(File uploadRelationFile, TCComponentDataset relationDataset,
                                            String propType) {
    	// 获取数据集类型工具
        DatasetTypeUtil datastTypeUtil = DatasetTypeUtil.getInstance();

        // DataSheet数据集操作
        TCComponentDataset dataSheetDatasetComp = null;
        try {
            if (null != uploadRelationFile) {
            	// 创建数据集
                String relationPath = uploadRelationFile.getAbsolutePath();
                String relationFileName = uploadRelationFile.getName();
                String datasetTypeName = datastTypeUtil.getDatasetType(relationFileName);
                String dstDefintionType = datastTypeUtil.getDatasetDefinitionType(datasetTypeName);
                // 创建数据集
                dataSheetDatasetComp = CreateObject.createDataSet(session, relationPath, datasetTypeName,
                        relationFileName, dstDefintionType);
            }
            if (null != relationDataset) {
                dataSheetDatasetComp = relationDataset;
            }
            if (dataSheetDatasetComp != null) {
                clearDateSheet(propType);
                itemRev.add(propType, dataSheetDatasetComp);
            }
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dataSheetDatasetComp;
    }

    private void clearDateSheet(String propType) throws TCException {
        TCComponent[] refComp = itemRev.getRelatedComponents(propType);
        if (null != refComp && refComp.length > 0) {
            itemRev.remove(propType, refComp);
        }
    }

    public void initRenderingReadWrite() {
        for (ClassPanel classPanel : classPanelList) {
            if (classPanel.textField != null) {
                classPanel.textField.setEditable(classPanel.isEditable);
            }
            if (classPanel.comboBox != null) {
                classPanel.comboBox.setEditable(false);
                classPanel.comboBox.setEnabled(classPanel.isEditable);
            }
        }

        hpSymbolButton.setEnabled(true);
        bigHpSymbolButton.setEnabled(true);
        dellSymbolButton.setEnabled(true);
        bigDellSymbolButton.setEnabled(true);
    }

    private JPanel getICSPropDescInfo(ICSPropertyDescription propDesc, String value) {
        try {
            String className = propDesc.getName();
            String refOptions = propDesc.getRefOptions();
            int classId = propDesc.getId();

            // 控件相关
            ClassPanel classPanel = new ClassPanel();
            classPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            classPanel.refOptions = refOptions;
            classPanel.classId = classId;
            classPanel.className = className;
            String propVal = "";
            if (StringUtils.isNotEmpty(refOptions)) {
                propVal = itemRev.getProperty(refOptions);
            }
            System.out.println("show=====================" + className + "================" + refOptions
                    + "================" + propVal + "===================" + value);
            JLabel label = new JLabel(className + ":", JLabel.RIGHT);
            label.setPreferredSize(new Dimension(200, 25));
            classPanel.add(label);

            if (StringUtils.isEmpty(value)) {
                if (StringUtils.isEmpty(propVal)) {
                    classPanel.isEditable = true;
                    if (StringUtils.isNotEmpty(refOptions)) {
                        TCComponentListOfValues lovValues = itemRev.getTCProperty(refOptions).getLOV();
                        if (lovValues == null) {
                            JTextField field = new JTextField();
                            field.setPreferredSize(new Dimension(200, 30));
                            classPanel.add(field);
                            classPanel.textField = field;
                        } else {
                            JComboBox comboBox = new JComboBox();
                            comboBox.setPreferredSize(new Dimension(200, 25));
                            ArrayList<String> list = Utils.getLOVList(lovValues);
                            comboBox.addItem("");
                            for (String lovValue : list) {
                                comboBox.addItem(lovValue);
                            }
                            classPanel.add(comboBox);
                            classPanel.comboBox = comboBox;
                        }
                    } else {
                        JTextField field = new JTextField();
                        field.setPreferredSize(new Dimension(200, 30));
                        if (className.equals("MFG")) {
                            field.setText(itemRev.getProperty("fx8_Mfr"));
                            classPanel.isEditable = false;
                        }

                        if (className.equals("DATASHEET")) {
                            field.setText(itemRev.getProperty("fx8_DataSheet"));
                            classPanel.isEditable = false;
                        }

                        if (className.equals("PCB_FOOTPRINT")) {
                            field.setText(itemRev.getProperty("fx8_Footprint"));
                            classPanel.isEditable = false;
                        }
                        classPanel.add(field);
                        classPanel.textField = field;
                    }
                } else {
                    JTextField field = new JTextField();
                    field.setPreferredSize(new Dimension(200, 30));
                    field.setText(propVal);
                    classPanel.add(field);
                    classPanel.textField = field;
                }
            } else {
                JTextField field = new JTextField();
                field.setPreferredSize(new Dimension(200, 30));
                field.setText(value);
                classPanel.add(field);
                classPanel.textField = field;
            }

            classPanelList.add(classPanel);
            return classPanel;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // 根据parttype信息处理
    public void addClasspropetyPanel(ICSApplicationObject icsAppObj, String category) {
        try {
            String group = itemRev.getProperty("owning_group");
            int index = 0;
            if (group.contains("Monitor")) {
                index = 1;
            } else if (group.contains("Printer")) {
                index = 2;
            } else if (group.contains("Dell")) {
                index = 3;
            } else if (group.contains("HP")) {
                index = 0;
            }

            ClassificationService clafService = new ClassificationService();
            String partClassifyRootId = "ICM";
            G4MUserAppContext g4mUserAppContext = new G4MUserAppContext(clafService, partClassifyRootId);
            G4MTree g4mtree = new G4MTree(g4mUserAppContext);

            G4MTreeNode rootNode = g4mtree.findNode("ICM");
            g4mtree.setRootNode(rootNode, true);

            int count = rootNode.getChildCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    G4MTreeNode treeNode = (G4MTreeNode) rootNode.getChildAt(i);
                    if ("ICM01".equals(treeNode.getNodeName())) {
                        g4mtree.setRootNode(treeNode, true);
                        treeNode = (G4MTreeNode) treeNode.getChildAt(index);
                        g4mtree.setRootNode(treeNode, true);
                        String classId = doSearchClassView(g4mtree, treeNode, category);
                        if (StringUtils.isNoneEmpty(classId)) {
                            icsId = classId;
                            icsAppObj.setView(classId);
                            ICSView icsView = icsAppObj.getView();
                            ICSPropertyDescription[] icsPropertyDescriptions = icsView.getPropertyDescriptions();
                            for (ICSPropertyDescription description : icsPropertyDescriptions) {
                                getICSPropDescInfo(description, "");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 查找分类节点
    private String doSearchClassView(G4MTree g4mtree, G4MTreeNode rootNode, String parentNodeName) {
        try {
            G4MTreeNode parentNode = getCategoryNode(g4mtree, rootNode, parentNodeName);
            // 获取 parentNode id
            if (parentNode != null) {
                return parentNode.getICSDescriptor().getId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private G4MTreeNode getCategoryNode(G4MTree g4mtree, G4MTreeNode rootNode, String parentNodeName) {
        int count = rootNode.getChildCount();
        G4MTreeNode parentNode = null;
        for (int i = 0; i < count; i++) {
            G4MTreeNode treeNode = (G4MTreeNode) rootNode.getChildAt(i);
            g4mtree.setRootNode(treeNode, true);
            String className = treeNode.getICSDescriptor().getName();

            if (parentNodeName.equals(className)) {
                parentNode = treeNode;
                return parentNode;
            }

            parentNode = getCategoryNode(g4mtree, treeNode, parentNodeName);
            if (parentNode != null) {
                return parentNode;
            }
        }
        return parentNode;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        TCComponentDataset oldDataset = null;
        if (event.getSource() == hpSymbolButton) {
            try {
                TCComponent tcComponent = itemRev.getRelatedComponent(RelationName.SYMBOLREL);
                String datasetFolderPath = "Symbols/HP/" + itemRev.getProperty("fx8_Category") + "/"
                        + itemRev.getProperty("fx8_PartType");
                if (tcComponent != null) {
                    oldDataset = (TCComponentDataset) tcComponent;
                }
                openDatasetDialog("HP Symbols Dataset", oldDataset, datasetFolderPath, RelationName.SYMBOLREL);
            } catch (TCException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (event.getSource() == bigHpSymbolButton) {
            try {
                TCComponent tcComponent = itemRev.getRelatedComponent(RelationName.BIGSYMBOLREL);
                String datasetFolderPath = "BigSymbols/HP/" + itemRev.getProperty("fx8_Category");
                if (tcComponent != null) {
                    oldDataset = (TCComponentDataset) tcComponent;
                }
                openDatasetDialog("HP Big Symbols Dataset", oldDataset, datasetFolderPath, RelationName.BIGSYMBOLREL);
            } catch (TCException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (event.getSource() == dellSymbolButton) {
            try {
                TCComponent tcComponent = itemRev.getRelatedComponent(RelationName.DELLSYMBOLREL);
                String datasetFolderPath = "Symbols/Dell/" + itemRev.getProperty("fx8_Category") + "/"
                        + itemRev.getProperty("fx8_PartType");
                if (tcComponent != null) {
                    oldDataset = (TCComponentDataset) tcComponent;
                }
                openDatasetDialog("Dell Symbols Dataset", oldDataset, datasetFolderPath, RelationName.DELLSYMBOLREL);
            } catch (TCException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (event.getSource() == bigDellSymbolButton) {
            try {
                TCComponent tcComponent = itemRev.getRelatedComponent(RelationName.DELLBIGSYMBOLREL);
                String datasetFolderPath = "BigSymbols/Dell/" + itemRev.getProperty("fx8_Category");
                if (tcComponent != null) {
                    oldDataset = (TCComponentDataset) tcComponent;
                }
                openDatasetDialog("Dell Big Symbols Dataset", oldDataset, datasetFolderPath,
                        RelationName.DELLBIGSYMBOLREL);
            } catch (TCException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void openDatasetDialog(final String title, final TCComponentDataset oldDataset,
                                   final String datasetFolderPath, final String datasetType) {
        new Thread(new Runnable() {
            public void run() {
                new EDACompDatasetDialog(title, oldDataset, datasetFolderPath, datasetType);
            }
        }).start();
    }

    private class ClassPanel extends JPanel {
        private boolean isEditable = false;
        private String refOptions;
        private JTextField textField;
        private JComboBox comboBox;
        private Integer classId;
        private String className;
    }

    private class EDACompDatasetDialog extends AbstractAIFDialog implements ActionListener {
        private String datasetType;

        private String datasetFolderPath;

        private TCComponentFolder datasetFolderComp;

        // 数据集Map key: 数据集名称 value: 数据集对象
        private Map<String, TCComponentDataset> datasetMap;

        // 弹框的宽、高
        private int dialogWidth = 780;
        private int dialogHeight = 550;

        private iButton uploadBtn;
        private iButton queryBtn;
        private iButton downloadBtn;

        private FileFilter fileFilter;
        private TCComponentDataset oldDataset;

        // 显示的表格组件
        private TCTable tableObj;

        public EDACompDatasetDialog(String dialogTitle, TCComponentDataset oldDataset, String datasetFolderPath,
                                    String datasetType) {
            super(true);
            this.oldDataset = oldDataset;
            this.datasetType = datasetType;
            this.datasetFolderPath = datasetFolderPath;
            CISFileStorageUtil cisFileStorageUtil = CISFileStorageUtil.getInstance();
            this.datasetFolderComp = cisFileStorageUtil.getDatasetFolderComp(datasetFolderPath);
            if (checkDatasetFolder()) {
                initUI(dialogTitle);
            }
        }

        public void initUI(String dialogTitle) {
            setTitle(dialogTitle);
            setSize(dialogWidth, dialogHeight);

            // 渲染表格组件
            tableObj = new TCTable(
                    new String[]{"No", "Position", "Dataset-Name", "File-Name", "Owning_user", "Last_Modi_Time"}) {
                public boolean isCellEditable(int arg0, int arg1) {
                    return false;
                }
            };
            tableObj.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            tableObj.getTableHeader().setResizingAllowed(true);
            tableObj.setRowHeight(25);

            try {
                if (oldDataset != null) {
                    tableObj.addRow(getTableData(oldDataset, 1, ""));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            tableObj.addRow(new Object[]{});

            // 底部panel
            JPanel southPanel = new JPanel();

            // 添加双击监听事件
            if (isCanUpload) {
            	// 下载按钮
                downloadBtn = new iButton("Download");
                downloadBtn.addActionListener(this);
                southPanel.add(downloadBtn);

                tableObj.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        if (event.getClickCount() > 1) {
                            dispose();

                            Display.getDefault().syncExec(new Runnable() {
                                @Override
                                public void run() {
                                	// 从表格数据中 获取数据 根据Object-Name、File-Name
                                    Object positionCell = tableObj.getValueAt(tableObj.getSelectedRow(), 1);
                                    Object objectNameCell = tableObj.getValueAt(tableObj.getSelectedRow(), 2);
                                    if (StringUtils.isBlank(positionCell.toString())) {
                                        TCComponentDataset downDataset = null;
                                        String objectName = objectNameCell.toString();
                                        downDataset = datasetMap.get(objectName);
                                        if (null != downDataset) {
                                            if (RelationName.SYMBOLREL.equals(datasetType)) {
                                                hpSymbolDataset = downDataset;
                                                hpSymbolFile = null;
                                            } else if (RelationName.BIGSYMBOLREL.equals(datasetType)) {
                                                hpBigSymbolDataset = downDataset;
                                                hpBigSymbolFile = null;
                                            } else if (RelationName.DELLSYMBOLREL.equals(datasetType)) {
                                                dellSymbolDataset = downDataset;
                                                dellSymbolFile = null;
                                            } else if (RelationName.DELLBIGSYMBOLREL.equals(datasetType)) {
                                                dellBigSymbolDataset = downDataset;
                                                dellBigSymbolFile = null;
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }

            // 列表panel
            JPanel listPanel = new JPanel(new BorderLayout());
            // 滚动条panel
            JScrollPane jScrollPane = new JScrollPane(tableObj);
            jScrollPane.setPreferredSize(new Dimension(dialogWidth, dialogHeight));
            listPanel.add(jScrollPane, "Center");

            // 上传按钮
            uploadBtn = new iButton("Upload");
            uploadBtn.addActionListener(this);
            southPanel.add(uploadBtn);

            // 查询按钮
            queryBtn = new iButton("Query");
            queryBtn.addActionListener(this);
            southPanel.add(queryBtn);

            // 把panel添加到弹框中
            add(listPanel, BorderLayout.CENTER);
            add(southPanel, BorderLayout.SOUTH);

            centerToScreen();
            setVisible(true);
        }

        private boolean checkDatasetFolder() {
//			System.out.println("EDACompDatasetDialog checkDatasetFolder Nodes => " + datasetFolderPath);
            if (null == datasetFolderComp) {
                MessageBox.post(this, "Dataset folder Not Exist!", "Error", MessageBox.ERROR);
                return false;
            }

            // 从文件夹中 加载数据集列表
            if (null == datasetMap) {
                datasetMap = new HashMap<String, TCComponentDataset>();

                try {
                	// 取数据集列表
                    TCComponent[] tempComp = datasetFolderComp.getRelatedComponents("contents");
                    if (null != tempComp && tempComp.length > 0) {
                        TCComponent itemComp = null;
                        TCComponentDataset itemDataset = null;

                        for (int i = 0; i < tempComp.length; i++) {
                            itemComp = tempComp[i];
                            if (itemComp instanceof TCComponentDataset) {
                                itemDataset = (TCComponentDataset) itemComp;
                                datasetMap.put(itemDataset.getProperty("object_name"), itemDataset);
                            }
                        }
                    }
                } catch (TCException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        /**
		 * 获取表格数据
		 * 
		 * @param datasetComp 数据集组件
		 * @param numNo       列表序号
		 * @return
		 * @throws TCException
		 */
        private String[] getTableData(TCComponentDataset datasetComp, int numNo, String folderPath) throws TCException {
            String fileName = "";
            TCComponentTcFile tempFile = getFileByDataset(datasetComp);
            if (null != tempFile) {
                fileName = tempFile.getProperty("original_file_name");
            }

            return new String[]{String.valueOf(numNo), folderPath, datasetComp.getProperty("object_name"), fileName,
                    datasetComp.getProperty("owning_user"), datasetComp.getProperty("last_mod_date")};
        }

        /**
		 * 根据数据集 获取文件
		 * 
		 * @param dataset 数据集
		 * @return
		 * @throws TCException
		 */
        private TCComponentTcFile getFileByDataset(TCComponentDataset dataset) throws TCException {
            TCComponentTcFile tempFile = null;
            TCComponentTcFile[] tcFiles = dataset.getTcFiles();

            if (ArrayUtils.isNotEmpty(tcFiles)) {
                tempFile = tcFiles[0];
            }

            return tempFile;
        }

        /**
		 * 上传数据集
		 */
        private void uploadDataset() {
            final File selectFile = getSelectFileByChooser(this, null);
            if (null != selectFile) {
//				System.out.println("EmpCompDatasetDialog 上传的文件 => " + selectFile.getAbsolutePath());

            	// 文件名称
				String fileName = selectFile.getName();
				// 判断上传的文件 在数据集中是否存在
                if (this.datasetMap.containsKey(fileName)) {
                	MessageBox.post(this, fileName + "已存在！", "WARN...", MessageBox.WARNING);
                } else {
                    dispose();
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            if (RelationName.SYMBOLREL.equals(datasetType)) {
                                hpSymbolFile = selectFile;
                                hpSymbolTextField.setText(selectFile.getAbsolutePath());
                                hpSymbolDataset = null;
                            } else if (RelationName.BIGSYMBOLREL.equals(datasetType)) {
                                hpBigSymbolFile = selectFile;
                                bigHpSymbolTextField.setText(selectFile.getAbsolutePath());
                                hpBigSymbolDataset = null;
                            } else if (RelationName.DELLSYMBOLREL.equals(datasetType)) {
                                dellSymbolFile = selectFile;
                                dellSymbolTextField.setText(selectFile.getAbsolutePath());
                                dellSymbolDataset = null;
                            } else if (RelationName.DELLBIGSYMBOLREL.equals(datasetType)) {
                                dellBigSymbolFile = selectFile;
                                bigDellSymbolTextField.setText(selectFile.getAbsolutePath());
                                dellBigSymbolDataset = null;
                            }
                        }
                    });
                }
            }
        }

        /**
		 * 获取上传的文件
		 * 
		 * @param abstractAIFDialog
		 * @param fileTypes
		 * @return
		 */
        private File getSelectFileByChooser(AbstractAIFDialog abstractAIFDialog, String fileTypes) {
            JFileChooser selectFileChooser = getSelectFileChooser(fileTypes);
            int result = selectFileChooser.showOpenDialog(abstractAIFDialog);
            if (result == 0) {
                File selectFile = selectFileChooser.getSelectedFile();
                if (null != selectFile) {
                	// 文件是否存在的操作
                    if (selectFile.exists()) {
                        return selectFile;
                    }
                }
            }
            return null;
        }

        /**
		 * 获取选择文件弹框对象
		 * 
		 * @param fileTypes 文件类型
		 * @return
		 */
        private JFileChooser getSelectFileChooser(String fileTypes) {
            SelectFileFilter selectFileFilter = new SelectFileFilter();
            if (null != fileTypes) {
                selectFileFilter.setFileFormat(fileTypes);
            }

            JFileChooser selectFileChooser = new JFileChooser();
            selectFileChooser.setFileSelectionMode(0);
            selectFileChooser.setFileFilter(selectFileFilter);
            selectFileChooser.addChoosableFileFilter(selectFileFilter);
            return selectFileChooser;
        }

        /**
		 * 导出文件弹出框
		 * 
		 * @param abstractAIFDialog 当前弹框类
		 * @return
		 */
        private File fileDialogExport(AbstractAIFDialog abstractAIFDialog) {
            try {
                JFileChooser jf = new JFileChooser();
                jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (fileFilter != null) {
                    jf.addChoosableFileFilter(fileFilter);
                    jf.setFileFilter(fileFilter);
                }
                int result = jf.showOpenDialog(abstractAIFDialog);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = jf.getSelectedFile();
                    return file;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        public void setFileFilter(FileFilter fileFilter) {
            fileFilter = fileFilter;
        }

        /**
		 * 查询数据集
		 */
        private void queryDataset() {
            int numNo = 1;
            try {
            	// 组装数据集数据
                if (null != datasetMap && datasetMap.size() > 0) {
                    tableObj.removeAllRows();
                    for (TCComponentDataset tempDataset : this.datasetMap.values()) {
                        tableObj.addRow(getTableData(tempDataset, numNo, datasetFolderPath));
                        numNo++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            tableObj.addRow(new Object[]{});
            tableObj.updateUI();
        }

        /**
		 * 下载数据集
		 */
        private void downloadDataset() {
            boolean downFlag = false;

            // 获取表格选中的行 如果没有选中 则取第一行
            int selectedRowIndex = tableObj.getSelectedRow();
//			System.out.println("当前选中的行 => " + selectedRowIndex);
            if (selectedRowIndex == -1) {
                selectedRowIndex = 0;
            }

            // 从表格数据中 获取数据
            Object positionCell = tableObj.getValueAt(tableObj.getSelectedRow(), 1);
            Object objectNameCell = tableObj.getValueAt(selectedRowIndex, 2);
            TCComponentDataset downDataset = null;
            if (StringUtils.isBlank(positionCell.toString())) {
                downDataset = oldDataset;
            } else {
                String objectName = objectNameCell.toString();
                downDataset = datasetMap.get(objectName);
            }

            String downFilePath = null;
            if (null != downDataset) {
            	// 下载文件弹框
                File file = fileDialogExport(this);
                if (null != file) {
                    downFilePath = DownloadDataset.downloadFile(downDataset, true, file.getAbsolutePath());
                    if (null != downFilePath && !"".equals(downFilePath)) {
                        downFlag = true;
                    }
                }
            }

            if (downFlag) {
                MessageBox.post(this, "DownLoad Dataset Success! File Path:" + downFilePath, "INFORMATION",
                        MessageBox.INFORMATION);
            }
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == uploadBtn) {
                uploadDataset();
            } else if (event.getSource() == downloadBtn) {
                downloadDataset();
            } else if (event.getSource() == queryBtn) {
                if (datasetMap.size() > 0) {
                    queryDataset();
                } else {
                	MessageBox.post(this, "没有查询到数据！", "Warning", MessageBox.WARNING);
                }
            }
        }
    }

    public void setCanUpload(boolean isCanUpload) {
        this.isCanUpload = isCanUpload;
        hpSymbolButton.setEnabled(true);
        bigHpSymbolButton.setEnabled(true);
        dellSymbolButton.setEnabled(true);
        bigDellSymbolButton.setEnabled(true);
    }
}
