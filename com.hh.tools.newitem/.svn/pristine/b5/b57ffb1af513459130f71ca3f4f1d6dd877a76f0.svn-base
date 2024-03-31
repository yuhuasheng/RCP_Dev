package com.hh.tools.renderingHint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.classification.common.G4MUserAppContext;
import com.teamcenter.rac.classification.common.tree.G4MTree;
import com.teamcenter.rac.classification.common.tree.G4MTreeNode;
import com.teamcenter.rac.classification.icm.ClassificationService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.stylesheet.InterfaceBufferedPropertyComponent;
import com.teamcenter.rac.stylesheet.InterfaceLegacyPropertyComponent;
import com.teamcenter.rac.stylesheet.InterfacePropertyComponent;
import com.teamcenter.rac.util.Painter;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.util.combobox.iComboBox;

public class EDACompCategoryProperty extends JPanel
        implements InterfacePropertyComponent, InterfaceBufferedPropertyComponent, InterfaceLegacyPropertyComponent {

    private static final long serialVersionUID = 1L;
    private static iComboBox categoryPropLov;
    private iTextField categoryTextField;
    private String property = "";
    private boolean mandatory = false;
    private boolean modifiable = true;
    private boolean savable;
    private boolean isFirstLoadDataFlag = true;
    private TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();

    public EDACompCategoryProperty() {
        super();
        loadPropertyPanel();
    }

    private void loadPropertyPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        categoryPropLov = new iComboBox() {
            @Override
            public void paint(Graphics arg0) {
                super.paint(arg0);
                Painter.paintIsRequired(this, arg0);
            }
        };

        // 加载下拉数据
        loadCategoryList();

        this.add(categoryPropLov, BorderLayout.CENTER);

        categoryTextField = categoryPropLov.getTextField();
        categoryTextField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent arg0) {

            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                String selectedText = categoryTextField.getText();
//				System.out.println("category insertUpdate=====" + selectedText);
                savable = true;

                // 不是第一次加载 则清除级联数据
                if (!isFirstLoadDataFlag) {
                    // 清除数据
                    EDACompPartTypeProperty.clearPropLov();

                    // 加载数据
                    EDACompPartTypeProperty.loadPartTypeList(selectedText.toUpperCase());
//					EDACompDellBigSymbolProperty.loadCurrentDatasetFolder();
//					EDACompHpBigSymbolProperty.loadCurrentDatasetFolder();
                    if ("Special".toUpperCase().equals((selectedText.toUpperCase()))) {
                        EDACompItemTypeProperty.setValue("Special");
                    } else {
                        EDACompItemTypeProperty.setValue("Standard");
                    }
                }

                // 当加载完成后的操作
                isFirstLoadDataFlag = false;
            }

            @Override
            public void removeUpdate(DocumentEvent arg0) {

            }
        });
    }

    /**
	 * 加载下拉列表
	 * 
	 * @return
	 */
    private void loadCategoryList() {
        // 查询category
        try {
            String group = this.session.getGroup().toString();
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
            g4mtree.setShowPopupMenu(false);
            G4MTreeNode rootNode = g4mtree.findNode("ICM");
            g4mtree.setRootNode(rootNode, true);

            int count = rootNode.getChildCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    G4MTreeNode treeNode = (G4MTreeNode) rootNode.getChildAt(i);
                    if ("ICM01".equals(treeNode.getNodeName())) {
                        g4mtree.setRootNode(treeNode, true);
                        G4MTreeNode hpTreeNode = (G4MTreeNode) treeNode.getChildAt(index);
                        g4mtree.setRootNode(hpTreeNode, true);
                        int categroyCount = hpTreeNode.getChildCount();
                        if (categroyCount > 0) {
                            for (int j = 0; j < categroyCount; j++) {
                                G4MTreeNode childNode = (G4MTreeNode) hpTreeNode.getChildAt(j);
                                String categroyStr = childNode.getICSDescriptor().getName();
                                categoryPropLov.addItem(categroyStr);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TCProperty getPropertyToSave(TCProperty tcproperty) throws Exception {
        Object valueObj = getEditableValue();
        if (null != valueObj) {
//			System.out.println("===========category set tcproperty================"+property);
//			System.out.println("===========category set tcproperty.getName()================"+tcproperty.getName());
//			System.out.println("===========category tcproperty new value================"+valueObj.toString());
//			System.out.println("===========category tcproperty old value================"+tcproperty.getStringValue());
            tcproperty.setStringValue(valueObj.toString());
        }
        return tcproperty;
    }

    public TCProperty getPropertyToSave(TCComponent paramTCComponent) throws Exception {
        if (this.property != null) {
            TCProperty localTCProperty = paramTCComponent.getTCProperty(this.property);
            return getPropertyToSave(localTCProperty);
        }
        this.savable = false;
        return null;
    }

    public static String getValue() {
        if (null != categoryPropLov) {
//			System.out.println("categoryPropLov => " + categoryPropLov.getSelectedItem());
            return categoryPropLov.getSelectedItem().toString();
        }
        return null;
    }

    @Override
    public TCProperty saveProperty(TCComponent paramTCComponent) throws Exception {
//		System.out.println("category================saveProperty(TCComponent paramTCComponent)");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (this.savable) {
            return localTCProperty;
        }
        return null;
    }

    @Override
    public TCProperty saveProperty(TCProperty paramTCProperty) throws Exception {
//		System.out.println("category================saveProperty(TCProperty paramTCProperty)");
        TCProperty localTCProperty = getPropertyToSave(paramTCProperty);
        if (this.savable) {
            return localTCProperty;
        }
        return null;
    }

    @Override
    public Object getEditableValue() {
//		System.out.println("category================getEditableValue()");
        if (null != categoryPropLov) {
            return categoryPropLov.getSelectedObject();
        }
        return "";
    }

    @Override
    public String getProperty() {
//		System.out.println("category================getProperty()==="+property);
        return this.property;
    }

    @Override
    public boolean isMandatory() {
//		System.out.println("category================isMandatory()");
        return this.mandatory;
    }

    @Override
    public boolean isPropertyModified(TCComponent paramTCComponent) throws Exception {
//		System.out.println("category================isPropertyModified(TCComponent paramTCComponent)");
        if (this.property != null) {
            TCProperty localTCProperty = paramTCComponent.getTCProperty(this.property);
            return isPropertyModified(localTCProperty);
        }
        return false;
    }

    @Override
    public boolean isPropertyModified(TCProperty paramTCProperty) throws Exception {
//		System.out.println("category================isPropertyModified(TCProperty paramTCProperty)");
        if (paramTCProperty.getStringValue().equals(getEditableValue())) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void load(TCComponent paramTCComponent) throws Exception {
//		System.out.println("category================load(TCComponent paramTCComponent)");
        if (this.property != null) {
            TCProperty localTCProperty = paramTCComponent.getTCProperty(this.property);
            load(localTCProperty);
        }
    }

    @Override
    public void load(TCProperty paramTCProperty) throws Exception {
//		System.out.println("category================load(TCProperty paramTCProperty)");
        String propValue = paramTCProperty.getStringValue();
        categoryPropLov.setText(propValue);
    }

    @Override
    public void load(TCComponentType paramTCComponentType) throws Exception {
//		System.out.println("category================load(TCComponentType paramTCComponentType)");
        if (paramTCComponentType != null) {
            TCPropertyDescriptor localTCPropertyDescriptor = paramTCComponentType.getPropertyDescriptor(this.property);
            load(localTCPropertyDescriptor);
        }
    }

    @Override
    public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
//		System.out.println("category================load(TCPropertyDescriptor paramTCPropertyDescriptor)");
        String defaultValue = paramTCPropertyDescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0 && null != categoryPropLov) {
            categoryPropLov.setSelectedItem(defaultValue);
        }
    }

    @Override
    public void save(TCComponent paramTCComponent) throws Exception {
//		System.out.println("category================save(TCComponent paramTCComponent)");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (this.savable) {
            paramTCComponent.setTCProperty(localTCProperty);
        }
    }

    @Override
    public void save(TCProperty paramTCProperty) throws Exception {
//		System.out.println("category================save(TCProperty paramTCProperty)");
        TCProperty localTCProperty = getPropertyToSave(paramTCProperty);
        if ((this.savable) && (localTCProperty != null)) {
            localTCProperty.getTCComponent().setTCProperty(paramTCProperty);
        }
    }

    @Override
    public void setMandatory(boolean paramBoolean) {
//		System.out.println("category================setMandatory(boolean paramBoolean)");
        this.mandatory = paramBoolean;
    }

    @Override
    public void setModifiable(boolean paramBoolean) {
//		System.out.println("category================setModifiable(boolean paramBoolean)");
        this.modifiable = paramBoolean;
    }

    @Override
    public void setProperty(String paramString) {
//		System.out.println("category================setProperty(String paramString)==="+property);
        this.property = paramString;
    }

    @Override
    public void setUIFValue(Object paramObject) {
//		System.out.println("category================setUIFValue(Object paramObject)");
        if (paramObject != null) {
            categoryTextField.setText(paramObject.toString());
        } else {
            categoryTextField.setText("");
        }
    }

    @Override
    public boolean isPropertyModified(TCProperty arg0, boolean arg1) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPropertyModified(TCComponent arg0, boolean arg1) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

}
