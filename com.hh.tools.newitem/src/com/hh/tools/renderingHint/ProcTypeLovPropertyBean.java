package com.hh.tools.renderingHint;

import java.awt.Frame;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.binding.AIFPropertyDataBean;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.controls.LOVComboBox;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;
import com.teamcenter.rac.viewer.stylesheet.viewer.IFormProvider;

public class ProcTypeLovPropertyBean extends AbstractPropertyBean<Object> {

    Composite composite = null;
    Frame frame = null;
    Composite parent = null;
    static LOVComboBox propLov = null;
    Text textField = null;
    FormToolkit toolkit = null;
    TCComponentType com = null;
    boolean isModify = false;
    String typename = "";
    TCProperty prop = null;
    TCComponent component = null;
    TCPropertyDescriptor tcPropertyDescriptor;

    public ProcTypeLovPropertyBean() {
        super();
        // TODO Auto-generated constructor stub
        System.out.println("ProcTypeLov 1");
    }

    public ProcTypeLovPropertyBean(Control paramControl) {
        super(paramControl);
        // TODO Auto-generated constructor stub
        System.out.println("ProcTypeLov 2");
        parent = (Composite) paramControl;
        loadPanel();
    }

    public ProcTypeLovPropertyBean(FormToolkit paramFormToolkit,
                                   Composite paramComposite, boolean paramBoolean, Map<?, ?> paramMap) {
        System.out.println("ProcTypeLov 3");
        this.savable = true;
        this.toolkit = paramFormToolkit;
        parent = paramComposite;
        loadPanel();

    }

    private void loadPanel() {
        // TODO Auto-generated method stub
        // FormToolkit toolkit = new FormToolkit( parent.getDisplay() );
        composite = new Composite(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);

        propLov = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);
        System.out.println("composite.getBounds().width == "
                + composite.getBounds().width);
        propLov.setSize(168, 25);
        textField = propLov.getTextField();
        textField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent paramModifyEvent) {
                if (ProgramCustomerLovPropertyBean.propLov != null) {
                    String customer = ProgramCustomerLovPropertyBean.getText();
                    if ("HP".equalsIgnoreCase(customer) && "L10".equals(getText())) {
                        LXDiagObjectNamePropertyBean.objectNameText.setEnabled(true);
                    } else {
                        LXDiagObjectNamePropertyBean.objectNameText.setEnabled(false);
                    }
                }

                isModify = true;
            }
        });

        try {
            String[] keys = new String[]{Utils.getTextValue("Type"), Utils.getTextValue("OwningUser"), Utils.getTextValue("Name")};
            String[] values = new String[]{"Folder", "infodba (infodba)", "#PROCTYPE#"};
            List<InterfaceAIFComponent> phaseList = Utils.search("General...", keys, values);
            TCComponentFolder folder = null;
            if (phaseList != null && phaseList.size() > 0) {
                folder = (TCComponentFolder) phaseList.get(0);
            }
            TCComponent[] coms = folder.getRelatedComponents("contents");
            for (TCComponent tcComponent : coms) {
                if (tcComponent instanceof TCComponentForm)
                    propLov.addItem(tcComponent.getProperty("object_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        setControl(composite);
    }

    public static String getText() {
        System.out.println("getText");
        // String text = (String) pop.getEditor().getItem();
        String text1 = "";
        if (propLov != null) {
            text1 = propLov.getTextField().getText();
            System.out.println("text1 == " + text1);
        }
        return text1;
    }

    @Override
    public Object getEditableValue() {
        // TODO Auto-generated method stub
        isModify = false;
        // setDirty(false);
        modifiable = false;
        mandatory = false;
        savable = false;
        System.out.println("ProcTypeLov getEditableValue == "
                + propLov.getSelectedObject());
        return propLov.getSelectedObject();
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("ProcTypeLov getPropertyToSave");
        return arg0;
    }

    @Override
    public boolean isPropertyModified(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("ProcTypeLov isPropertyModified 11111");
        return isModify;
    }

    @Override
    public void load(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("ProcTypeLov load 1");
        // super.load(arg0);
        com = arg0.getTCComponent().getTypeComponent();
        property = arg0.getPropertyName();
        descriptor = arg0.getPropertyDescriptor();
        prop = arg0;
        component = arg0.getTCComponent();
        typename = descriptor.getTypeComponent().getTypeName();
        String[] arr = arg0.getStringArrayValue();
        try {
            if (arr != null) propLov.setText(arr[0]);
            isModify = false;
        } catch (Exception e) {

        }

    }

    @Override
    public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
        System.out.println("load paramTCPropertyDescriptor");
        tcPropertyDescriptor = paramTCPropertyDescriptor;
        String defaultValue = paramTCPropertyDescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0) {
            propLov.setSelectedItem(defaultValue);
        }


    }

    @Override
    public void processPropertyInfo(Map arg0) {
        // TODO Auto-generated method stub
        System.out.println("processPropertyInfo");
        super.processPropertyInfo(arg0);
    }

    @Override
    public void propertyChange(PropertyChangeEvent arg0) {
        // TODO Auto-generated method stub
        System.out.println("propertyChange");
        super.propertyChange(arg0);
    }

    @Override
    public void setModifiable(boolean arg0) {// ---
        // TODO Auto-generated method stub
        System.out.println("setModifiable == " + arg0);
        modifiable = arg0;
        // super.setModifiable(true);
    }

    @Override
    public void setUIFValue(Object arg0) {
        // TODO Auto-generated method stub
        System.out.println("setUIFValue");
        // super.setUIFValue(arg0);
    }

    @Override
    public void addFocusListener() {
        // TODO Auto-generated method stub
        System.out.println("addFocusListener");
        super.addFocusListener();
    }

    @Override
    public void addListener(Listener arg0) {
        // TODO Auto-generated method stub
        System.out.println("addListener");
        super.addListener(arg0);
    }

    @Override
    public void addPropertyChangeListener(IPropertyChangeListener arg0) {
        // TODO Auto-generated method stub
        System.out.println("addPropertyChangeListener");

        super.addPropertyChangeListener(arg0);
    }

    @Override
    protected void bindValues(TCProperty arg0) {
        // TODO Auto-generated method stub
        System.out.println("bindValues");
        super.bindValues(arg0);
    }

    @Override
    protected void bindVisibility() {
        // TODO Auto-generated method stub
        System.out.println("bindVisibility");
        super.bindVisibility();
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        System.out.println("dispose");
        super.dispose();
    }

    @Override
    protected void firePropertyChange(Object arg0, String arg1, Object arg2,
                                      Object arg3) {
        // TODO Auto-generated method stub
        System.out.println("firePropertyChange");
        super.firePropertyChange(arg0, arg1, arg2, arg3);
    }

    @Override
    public Map getBeanParamTable() {
        // TODO Auto-generated method stub
        System.out.println("getBeanParamTable");
        return super.getBeanParamTable();
    }

    @Override
    public Control getControl() {
        // TODO Auto-generated method stub
        System.out.println("getControl");
        return super.getControl();
    }

    @Override
    public IBOCreateDefinition getCreateDefintion() {
        // TODO Auto-generated method stub
        System.out.println("getCreateDefintion");
        return super.getCreateDefintion();
    }

    @Override
    public AIFPropertyDataBean getDataBeanViewModel() {
        // TODO Auto-generated method stub
        // System.out.println("ProcTypeLov getDataBeanViewModel");
        return super.getDataBeanViewModel();
    }

    @Override
    public String getDefaultValue() {
        // TODO Auto-generated method stub
        System.out.println("getDefaultValue");
        return super.getDefaultValue();
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        System.out.println("getDescription");
        return super.getDescription();
    }

    @Override
    public Composite getLabelComposite() {
        // TODO Auto-generated method stub
        System.out.println("getLabelComposite");
        // Composite labelComposite = super.getLabelComposite();
        // Label label41 = toolkit.createLabel(labelComposite, "*");
        // label41.setForeground(labelComposite.getDisplay().getSystemColor(3));
        return super.getLabelComposite();
    }

    @Override
    public boolean getMandatory() {
        // TODO Auto-generated method stub
        System.out.println("getMandatory");
        return super.getMandatory();
    }

    @Override
    public boolean getModifiable() {
        // TODO Auto-generated method stub
        System.out.println("getModifiable");
        return super.getModifiable();
    }

    @Override
    public String getProperty() {
        // TODO Auto-generated method stub
        System.out.println("getProperty");
        return super.getProperty();
    }


    @Override
    public TCPropertyDescriptor getPropertyDescriptor() {
        // TODO Auto-generated method stub
        System.out.println("getPropertyDescriptor");
        return super.getPropertyDescriptor();
    }

    @Override
    public TCProperty getPropertyToSave(TCComponent arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("getPropertyToSave");
        return super.getPropertyToSave(arg0);
    }

    @Override
    protected Viewer getViewer() {
        // TODO Auto-generated method stub
        System.out.println("getViewer");
        return super.getViewer();
    }

    @Override
    public boolean isDirty() {// ---
        // TODO Auto-generated method stub
        System.out.println("isDirty == " + super.isDirty());
        return super.isDirty();
    }

    @Override
    public boolean isForNumericPropertyType() {
        // TODO Auto-generated method stub
        System.out.println("isForNumericPropertyType == "
                + super.isForNumericPropertyType());
        return super.isForNumericPropertyType();
    }

    @Override
    public boolean isMandatory() { // --
        // TODO Auto-generated method stub
        System.out.println("isMandatory == "
                + super.isMandatory());
        return super.isMandatory();

    }

    @Override
    public boolean isPropertyModified(TCComponent arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("isPropertyModified == " + isModify);
        return false;
    }

    @Override
    public void load(Object arg0, boolean arg1) {

        // TODO Auto-generated method stub
        System.out.println("load 11");
        super.load(arg0, arg1);
    }

    @Override
    public void load(TCComponent arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("load 12");
        super.load(arg0);
    }

    @Override
    public void load(TCComponentType arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("load 13");
        super.load(arg0);
    }

    @Override
    public void removePropertyChangeListener(IPropertyChangeListener arg0) {
        // TODO Auto-generated method stub
        System.out.println("removePropertyChangeListener");
        super.removePropertyChangeListener(arg0);
    }

    @Override
    public void save(TCComponent arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("save 11");
        super.save(arg0);
    }

    @Override
    public void save(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("save 12");
        super.save(arg0);
    }

    @Override
    public TCProperty saveProperty(TCComponent arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("saveProperty 11");
        return super.saveProperty(arg0);
    }

    @Override
    public TCProperty saveProperty(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("saveProperty 12");
        return super.saveProperty(arg0);
    }

    @Override
    protected void setAIFPropertyDataBean(TCProperty arg0) {
        // TODO Auto-generated method stub
        System.out.println("setAIFPropertyDataBean 12");
        super.setAIFPropertyDataBean(arg0);
    }

    @Override
    public void setBeanLabel(Label arg0) {
        // TODO Auto-generated method stub
        System.out.println("setBeanLabel 12 == "
                + arg0.getText());

        super.setBeanLabel(arg0);
    }

    @Override
    public void setBeanParamTable(Map arg0) {
        // TODO Auto-generated method stub
        System.out.println("setBeanParamTable 12");
        super.setBeanParamTable(arg0);
    }

    @Override
    public void setContextData(Map arg0) {
        // TODO Auto-generated method stub
        System.out.println("setContextData 12");
        super.setContextData(arg0);
    }

    @Override
    public void setControl(Control arg0) {
        // TODO Auto-generated method stub
        System.out.println("setControl 12");
        super.setControl(arg0);
    }

    @Override
    public void setCreateDefintion(IBOCreateDefinition arg0) {
        // TODO Auto-generated method stub
        System.out.println("setCreateDefintion 12");
        super.setCreateDefintion(arg0);
    }

    @Override
    public boolean setDefaultAsUIFvalue() {
        // TODO Auto-generated method stub
        System.out.println("setDefaultAsUIFvalue 12");
        return super.setDefaultAsUIFvalue();
    }

    @Override
    public void setDirty(boolean arg0) { // --
        // TODO Auto-generated method stub
        System.out.println("setDirty 12 == " + arg0);
        super.setDirty(arg0);

    }

    @Override
    public void setFormProvider(IFormProvider arg0) {
        System.out.println("setFormProvider 12");
        // TODO Auto-generated method stub
        super.setFormProvider(arg0);
    }

    @Override
    public void setLabelComposite(Composite arg0) {
        // TODO Auto-generated method stub
        System.out.println("setLabelComposite 12");

//		Composite labelComposite = arg0;
//		// GridLayout localGridLayout = new GridLayout(3, false);
//		GridLayout layout = (GridLayout) labelComposite.getLayout();
//		layout.numColumns = 2;
//		System.out.println("layout == " + layout);
//		Label label41 = toolkit.createLabel(labelComposite, "*");
//		// label41.setLayoutData(layout);
//		label41.setForeground(labelComposite.getDisplay().getSystemColor(3));

        super.setLabelComposite(arg0);
    }

    @Override
    public void setMandatory(boolean arg0) {
        // TODO Auto-generated method stub
        System.out.println("setMandatory 12");
        super.setMandatory(arg0);
    }

    @Override
    public void setOperationName(String arg0) {
        // TODO Auto-generated method stub
        System.out.println("setOperationName 12");
        super.setOperationName(arg0);
    }

    @Override
    public void setProperty(String arg0) {
        // TODO Auto-generated method stub
        System.out.println("setProperty 111");
        super.setProperty(arg0);
    }

    @Override
    protected void setSeedValue(Object arg0, Object arg1) {
        // TODO Auto-generated method stub
        System.out.println("setSeedValue 111");
        super.setSeedValue(arg0, arg1);
    }

    @Override
    public void setViewer(Viewer arg0) {
        // TODO Auto-generated method stub
        System.out.println("setViewer 111");
        super.setViewer(arg0);
    }

    @Override
    public void setVisible(boolean arg0) {
        // TODO Auto-generated method stub
        System.out.println("setVisible 111");
        super.setVisible(arg0);
    }

    @Override
    public void setupDataBinding(TCProperty arg0) {
        // TODO Auto-generated method stub

        System.out.println("setupDataBinding 111");
        super.setupDataBinding(arg0);
    }

    @Override
    public void validate() {
        // TODO Auto-generated method stub
        System.out.println("validate 111");
        super.validate();
    }

}
