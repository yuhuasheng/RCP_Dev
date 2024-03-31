package com.hh.tools.renderingHint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.hh.tools.newitem.Utils;
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

public class EDACompItemTypeProperty extends JPanel
        implements InterfacePropertyComponent, InterfaceBufferedPropertyComponent, InterfaceLegacyPropertyComponent {

    private static final long serialVersionUID = 1L;
    private static iComboBox itemTypeLov;
    private iTextField itemTypeTextField;
    private String property = "";
    private boolean mandatory = false;
    private boolean modifiable = true;
    private boolean savable;

    public EDACompItemTypeProperty() {
        super();
        loadPropertyPanel();
    }

    private void loadPropertyPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        itemTypeLov = new iComboBox() {
            @Override
            public void paint(Graphics arg0) {
                super.paint(arg0);
                Painter.paintIsRequired(this, arg0);
            }
        };

        this.add(itemTypeLov, BorderLayout.CENTER);

        itemTypeTextField = itemTypeLov.getTextField();
        itemTypeTextField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent arg0) {

            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                savable = true;
            }

            @Override
            public void removeUpdate(DocumentEvent arg0) {

            }
        });
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
        if (null != itemTypeLov) {
//			System.out.println("itemTypeLov => " + itemTypeLov.getSelectedItem());
            return itemTypeLov.getSelectedItem().toString();
        }
        return null;
    }

    public static void setValue(String value) {
        itemTypeLov.setSelectedItem(value);
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
        if (null != itemTypeLov) {
            return itemTypeLov.getSelectedObject();
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
        ArrayList<String> values = Utils.getLOVList(paramTCProperty.getLOV());
        if (values.size() > 0) {
            for (String value : values) {
                itemTypeLov.addItem(value);
            }
        }

        String propValue = paramTCProperty.getStringValue();
        itemTypeLov.setText(propValue);
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
        if (defaultValue != null && defaultValue.length() > 0 && null != itemTypeLov) {
            itemTypeLov.setSelectedItem(defaultValue);
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
            itemTypeTextField.setText(paramObject.toString());
        } else {
            itemTypeTextField.setText("");
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
