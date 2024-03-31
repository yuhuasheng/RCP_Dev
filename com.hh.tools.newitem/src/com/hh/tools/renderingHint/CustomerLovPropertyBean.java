package com.hh.tools.renderingHint;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.binding.AIFPropertyDataBean;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.controls.LOVComboBox;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;
import com.teamcenter.rac.viewer.stylesheet.viewer.IFormProvider;

public class CustomerLovPropertyBean extends AbstractPropertyBean<Object> {

    Composite composite = null;
    Frame frame = null;
    Composite parent = null;
    static LOVComboBox propLov = null;
    Text textField = null;
    FormToolkit toolkit = null;
    TCComponentType com = null;
    boolean isModify = false;
    String typename = "";

    TCComponent component = null;
    TCPropertyDescriptor tcPropertyDescriptor;
    TCProperty tcProperty;

    public CustomerLovPropertyBean() {
        super();
        // TODO Auto-generated constructor stub
        System.out.println("Plant 1");
    }

    public CustomerLovPropertyBean(Control paramControl) {
        super(paramControl);
        // TODO Auto-generated constructor stub
        System.out.println("Plant 2");
        parent = (Composite) paramControl;
        loadPanel();
    }

    public CustomerLovPropertyBean(FormToolkit paramFormToolkit,
                                   Composite paramComposite, boolean paramBoolean, Map<?, ?> paramMap) {
        System.out.println("Plant 3");
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
                // TODO Auto-generated method stub
                System.out.println("PlantLov modifyText");
                isModify = true;
                mandatory = true;
                setDirty(true);

                if ("".equals(textField.getText())) {
                	// Çå¿Õ½×¶Î
                    CustomerPhaseLovPropertyBean.Clear();
                } else {
                	// ¼ÓÔØ½×¶Î
                    CustomerPhaseLovPropertyBean.loadPop();
                }
            }
        });

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
//		setDirty(false);
        modifiable = false;
        mandatory = false;
        savable = false;
        System.out.println("Plant getEditableValue == "
                + propLov.getSelectedObject());
        return propLov.getSelectedObject();
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("Plant getPropertyToSave");
        return arg0;
    }

    @Override
    public boolean isPropertyModified(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("Plant isPropertyModified 11111");
        return isModify;
    }

    @Override
    public void load(TCProperty paramTCProperty) throws Exception {
        System.out.println("load paramTCProperty");
        tcProperty = paramTCProperty;
        tcPropertyDescriptor = paramTCProperty.getDescriptor();
        component = tcProperty.getTCComponent();
        List<String> values = getCustomerList();
        if (values.size() > 0) {
            for (String value : values) {
                propLov.addItem(value);
            }
        }
        String selectedValue = tcProperty.getStringValue();
        if (selectedValue != null && selectedValue.length() > 0) {
            propLov.setSelectedItem(selectedValue);
        }
        setDirty(false);
    }

    @Override
    public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
        System.out.println("load paramTCPropertyDescriptor");
        tcPropertyDescriptor = paramTCPropertyDescriptor;
        List<String> values = getCustomerList();
        if (values.size() > 0) {
            for (String value : values) {
                propLov.addItem(value);
            }
        }
        String defaultValue = paramTCPropertyDescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0) {
            propLov.setSelectedItem(defaultValue);
        }
        setDirty(false);
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
        if (tcProperty != null) {
            try {
                component.setProperty(property, textField.getText());
            } catch (TCException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
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
        // System.out.println("Plant getDataBeanViewModel");
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
//		if(isModify){
//			doSave();
//		}
        return super.getProperty();
    }

//	private void doSave() {
//		try {
//			String customer = getText();
//			if(!Utils.isNull(customer)){
//				component.setProperty("fx8_Customer", customer);	
//			}else{
//				component.setProperty("fx8_Customer", null);	
//			}				
//		} catch (Exception e) {
//			e.printStackTrace();
//		}		
//	}

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
//		// TODO Auto-generated method stub
//		System.out.println("setLabelComposite 12");
//		Composite labelComposite = arg0;
//		// GridLayout localGridLayout = new GridLayout(3, false);
//		GridLayout layout = (GridLayout) labelComposite.getLayout();
//		layout.numColumns = 2;
//		System.out.println("layout == " + layout);
//		Label label41 = toolkit.createLabel(labelComposite, "*");
//		// label41.setLayoutData(layout);
//		label41.setForeground(labelComposite.getDisplay().getSystemColor(3));
//		super.setLabelComposite(arg0);		
        System.out.println("Plant setLabelComposite 12");
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

    private static List<String> getCustomerList() {
        List<String> list = new ArrayList<>();
        TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
        TCComponentGroup group = session.getGroup();
        try {
            String groupFullName = group.getFullName();
            String preferenceName = "";
            if (groupFullName.contains("Monitor.D_Group")) {
                preferenceName = "FX_Get_MNT_Customer_Phase_Values";
            } else if (groupFullName.contains("Printer.D_Group")) {
                preferenceName = "FX_Get_PRT_Customer_Phase_Values";
            } else {
                preferenceName = "FX_Get_Customer_Phase_Values";
            }
            String[] customerPhaseArr = new GetPreferenceUtil().getArrayPreference(session, TCPreferenceService.TC_preference_site, preferenceName);
            if (customerPhaseArr.length > 0) {
                for (String customerPhase : customerPhaseArr) {
                    String customer = customerPhase.split("::")[0];
                    list.add(customer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return list;

    }

}
