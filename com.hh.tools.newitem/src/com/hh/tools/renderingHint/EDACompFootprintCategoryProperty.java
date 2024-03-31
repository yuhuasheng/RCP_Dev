package com.hh.tools.renderingHint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.classification.common.G4MUserAppContext;
import com.teamcenter.rac.classification.common.tree.G4MTree;
import com.teamcenter.rac.classification.common.tree.G4MTreeNode;
import com.teamcenter.rac.classification.icm.ClassificationService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.stylesheet.InterfaceBufferedPropertyComponent;
import com.teamcenter.rac.stylesheet.InterfaceLegacyPropertyComponent;
import com.teamcenter.rac.stylesheet.InterfacePropertyComponent;
import com.teamcenter.rac.util.Painter;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.util.combobox.iComboBox;

public class EDACompFootprintCategoryProperty extends JPanel
        implements InterfacePropertyComponent, InterfaceBufferedPropertyComponent, InterfaceLegacyPropertyComponent {

    private static final long serialVersionUID = 1L;
    private static iComboBox categoryPropLov;
    private iTextField categoryTextField;
    private String property = "";
    private boolean mandatory = false;
    private boolean modifiable = true;
    private boolean savable;
    private TCProperty tcProperty;

    public EDACompFootprintCategoryProperty() {
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

        this.add(categoryPropLov, BorderLayout.CENTER);

        categoryTextField = categoryPropLov.getTextField();
        categoryTextField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent arg0) {

            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                String selectedText = categoryTextField.getText();
//				System.out.println("footprint category insertUpdate=====" + selectedText);
                savable = true;
                if (null != selectedText && !"".equals(selectedText)) {
                    // 加载数据
                    EDACompFootprintProperty.loadCurrentDatasetFolder();
                    EDACompPadProperty.loadCurrentDatasetFolder();
                }
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
        TCComponentListOfValues categoryLOV = tcProperty.getPropertyDescriptor().getLOV();
        ArrayList<String> categoryLOVList = Utils.getLOVList(categoryLOV);
        categoryPropLov.addItem("");
        for (String value : categoryLOVList) {
            categoryPropLov.addItem(value);
        }
    }

    public TCProperty getPropertyToSave(TCProperty tcproperty) throws Exception {
        Object valueObj = getEditableValue();
        if (null != valueObj) {
//			System.out.println("===========footprint category set tcproperty================"+property);
//			System.out.println("===========footprint category set tcproperty.getName()================"+tcproperty.getName());
//			System.out.println("===========footprint category tcproperty new value================"+valueObj.toString());
//			System.out.println("===========footprint category tcproperty old value================"+tcproperty.getStringValue());
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
//		System.out.println("footprint category================saveProperty(TCComponent paramTCComponent)");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (this.savable) {
            return localTCProperty;
        }
        return null;
    }

    @Override
    public TCProperty saveProperty(TCProperty paramTCProperty) throws Exception {
//		System.out.println("footprint category================saveProperty(TCProperty paramTCProperty)");
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
            tcProperty = localTCProperty;
            // 加载下拉数据
            loadCategoryList();
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
