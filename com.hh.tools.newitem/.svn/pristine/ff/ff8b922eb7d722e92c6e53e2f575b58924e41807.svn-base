package com.hh.tools.renderingHint;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.customerPanel.MaterialPanel;
import com.hh.tools.newitem.ItemTypeName;
import com.teamcenter.rac.aif.kernel.AbstractAIFSession;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class HHPNPropertyBean extends AbstractPropertyBean<Object> {

    protected TCComponent com;
    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected boolean isModified = false;
    protected static Text HHPNText;

    public HHPNPropertyBean(Control paramControl) {
        super(paramControl);
    }

    public HHPNPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                            Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        initComposite(paramComposite);

    }

    private void initComposite(Composite parentComposite) {
        this.composite = new Composite(parentComposite, SWT.NONE);
        composite.setBackground(parentComposite.getBackground());
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.horizontalSpacing = 5;
        this.composite.setLayout(gridLayout);


        HHPNText = new Text(this.composite, SWT.BORDER | SWT.LEFT);
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gridData.heightHint = 20;
        gridData.widthHint = 320;
        HHPNText.setLayoutData(gridData);
        HHPNText.setEnabled(false);
        setControl(this.composite);


    }

    public static String getText() {
        System.out.println("getText");
        // String text = (String) pop.getEditor().getItem();
        String text1 = "";
        if (HHPNText != null) {
            text1 = HHPNText.getText();
            System.out.println("text1 == " + text1);
        }

        return text1;
    }


    @Override
    public boolean isPropertyModified(TCComponent paramTCComponent) throws Exception {
        return false;
    }

    @Override
    public boolean isPropertyModified(TCProperty paramTCProperty) throws Exception {
        return this.isModified;
    }

    @Override
    public String getProperty() {
        System.out.println("HHPN getProperty");
        System.out.println("HHPN isModified == " + isModified);
        System.out.println("HHPN tcProperty == " + tcProperty);
        if (com != null && (ItemTypeName.SHEETMETALREVISION.equals(com.getType())
                || ItemTypeName.PLASTICREVISION.equals(com.getType())
                || ItemTypeName.SCREWREVISION.equals(com.getType())
                || ItemTypeName.STANDOFFREVISION.equals(com.getType())
                || ItemTypeName.MYLARREVISION.equals(com.getType())
                || ItemTypeName.LABELREVISION.equals(com.getType())
                || ItemTypeName.RUBBERREVISION.equals(com.getType())
                || ItemTypeName.GASKETREVISION.equals(com.getType())
                || ItemTypeName.PCBPANELREVISION.equals(com.getType())
                || ItemTypeName.OTHERSREVISION.equals(com.getType()))) {
        	//±£´æ
            MaterialPanel.addButton.setEnabled(false);
            MaterialPanel.removeButton.setEnabled(false);
            int size = MaterialPanel.materialList.size();
            try {
                if (size > 0) {
                    TableItem tableItem = MaterialPanel.table.getItem(0);
                    String materialType = tableItem.getText(0);
                    String density = tableItem.getText(1);
                    String remark = tableItem.getText(2);
                    System.out.println("materialType==" + materialType + "==density==" + density + "==remark==" + remark);
                    com.setProperty("fx8_MaterialType", materialType);
                    com.setProperty("fx8_Density", density);
                    com.setProperty("fx8_MaterialRemark", remark);
                } else {
                    com.setProperty("fx8_MaterialType", null);
                    com.setProperty("fx8_Density", null);
                    com.setProperty("fx8_MaterialRemark", null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return super.getProperty();
    }

    @Override
    public Object getEditableValue() {
        return HHPNText.getText();
    }


    @Override
    public void save(TCComponent paramTCComponent) throws Exception {
        System.out.println("save TCComponent!");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (savable) {
            paramTCComponent.setTCProperty(localTCProperty);
        }
    }

    @Override
    public TCProperty saveProperty(TCComponent paramTCComponent) throws Exception {
        System.out.println("save TCComponent!");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (savable) {
            return localTCProperty;
        }
        return null;
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty paramTCProperty) throws Exception {

        return paramTCProperty;
    }

    @Override
    public void load(TCProperty paramTCProperty) throws Exception {
        System.out.println("load HHPN TCProperty");
        tcProperty = paramTCProperty;
        tcPropertyDescriptor = paramTCProperty.getDescriptor();
        com = tcProperty.getTCComponent();
        String value = tcProperty.getStringValue();
        HHPNText.setText(value);
//		if(com!=null&&(ItemTypeName.SHEETMETALREVISION.equals(com.getType())
//				||ItemTypeName.PLASTICREVISION.equals(com.getType())
//				||ItemTypeName.SCREWREVISION.equals(com.getType())
//				||ItemTypeName.STANDOFFREVISION.equals(com.getType())
//				||ItemTypeName.MYLARREVISION.equals(com.getType())
//				||ItemTypeName.LABELREVISION.equals(com.getType())
//				||ItemTypeName.RUBBERREVISION.equals(com.getType())
//				||ItemTypeName.GASKETREVISION.equals(com.getType())
//				||ItemTypeName.PCBPANELREVISION.equals(com.getType()))){
//			//±£´æ
//			MaterialPanel.addButton.setEnabled(true);
//			MaterialPanel.removeButton.setEnabled(true);
//		}
    }

    @Override
    public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
        tcPropertyDescriptor = paramTCPropertyDescriptor;
        String defaultValue = paramTCPropertyDescriptor.getDefaultValue();
        HHPNText.setText(defaultValue);
    }

    @Override
    public void setModifiable(boolean arg0) {
        modifiable = arg0;
    }

    @Override
    public void setUIFValue(Object arg0) {
        // TODO Auto-generated method stub

    }

    protected String getDefaultExportDirectory() {
        AbstractAIFSession session = AIFUtility.getCurrentApplication().getSession();
        String s = Utilities.getCookie("filechooser", "DatasetExport.DIR", true);
        if (s == null || s.length() == 0) {
            TCPreferenceService tcpreferenceservice = ((TCSession) session).getPreferenceService();
            s = tcpreferenceservice.getString(0, "defaultExportDirectory");
        }
        if (s != null) {
            s = s.trim();
            if (s.length() == 0) {
                s = null;
            }
        }
        return s;
    }


}
