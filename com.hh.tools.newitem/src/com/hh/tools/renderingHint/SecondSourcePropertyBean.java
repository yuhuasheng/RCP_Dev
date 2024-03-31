package com.hh.tools.renderingHint;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class SecondSourcePropertyBean extends AbstractPropertyBean<Object> {

    private TCComponent component;
    private TCProperty tcProperty;
    private TCPropertyDescriptor tcPropertyDescriptor;
    TCComponentType com = null;
    boolean isModify = false;
    String typename = "";
    TCProperty prop = null;
    private FormToolkit toolkit;
    private Composite composite;
    private boolean isModified = false;
    private static Button noneButton = null;
    private static Button smdButton = null;
    private static Button allButton = null;

    public SecondSourcePropertyBean(Control paramControl) {
        super(paramControl);
    }

    public SecondSourcePropertyBean(FormToolkit paramFormToolkit, Composite parentComposite, boolean paramBoolean,
                                    Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        initComposite(parentComposite);
    }


    private void initComposite(Composite parentComposite) {
        this.composite = new Composite(parentComposite, SWT.NONE);
        composite.setBackground(parentComposite.getBackground());

        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);

        noneButton = new Button(this.composite, SWT.RADIO);
        noneButton.setText("None");
        noneButton.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));

        smdButton = new Button(this.composite, SWT.RADIO);
        smdButton.setText("NC-SMD");
        smdButton.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));

        allButton = new Button(this.composite, SWT.RADIO);
        allButton.setText("All");
        allButton.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));

        setControl(this.composite);

    }

    @Override
    public String getProperty() {
        System.out.println("getProperty");
        if (component != null) {
            doSave();
        }
        return super.getProperty();
    }


    private void doSave() {
        String value = getValue();
        try {
            if (!Utils.isNull(value)) {
                component.setProperty(property, value);
            } else {
                component.setProperty(property, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isPropertyModified(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object getEditableValue() {
        String value = "";
        if (noneButton.getSelection()) {
            value = "None";
        } else if (smdButton.getSelection()) {
            value = "NC-SMD";
        } else if (allButton.getSelection()) {
            value = "All";
        }
        return value;
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public void load(TCProperty arg0) throws Exception {
        com = arg0.getTCComponent().getTypeComponent();
        property = arg0.getPropertyName();
        descriptor = arg0.getPropertyDescriptor();
        prop = arg0;
        component = arg0.getTCComponent();
        typename = descriptor.getTypeComponent().getTypeName();
        String value = arg0.getStringValue();
        if ("None".equals(value)) {
            noneButton.setSelection(true);
        } else if ("NC-SMD".equals(value)) {
            smdButton.setSelection(true);
        } else if ("All".equals(value)) {
            allButton.setSelection(true);
        }
        isModify = false;
    }

    public static String getValue() {
        String value = "";
        if (noneButton.getSelection()) {
            value = "None";
        } else if (smdButton.getSelection()) {
            value = "NC-SMD";
        } else if (allButton.getSelection()) {
            value = "All";
        }
        return value;
    }

    @Override
    public void load(TCPropertyDescriptor arg0) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void setModifiable(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUIFValue(Object arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        System.out.println("dispose");
        super.dispose();
    }

}
