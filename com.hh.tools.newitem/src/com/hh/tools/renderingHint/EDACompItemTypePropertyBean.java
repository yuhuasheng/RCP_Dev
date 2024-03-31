package com.hh.tools.renderingHint;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.common.controls.LOVComboBox;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class EDACompItemTypePropertyBean extends AbstractPropertyBean<Object> {

    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected Composite parentComposite;

    @SuppressWarnings("deprecation")
    static LOVComboBox itemTypeLov = null;
    Text itemTypeField = null;
    boolean isModify = false;

    public EDACompItemTypePropertyBean(Control paramControl) {
        super(paramControl);
        this.parentComposite = (Composite) paramControl;
        loadPropertyPanel();
    }

    public EDACompItemTypePropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                       Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        this.parentComposite = paramComposite;
        loadPropertyPanel();
    }

    /**
	 * º”‘ÿ Ù–‘√Ê∞Â
	 */
    @SuppressWarnings("deprecation")
    private void loadPropertyPanel() {
        this.composite = new Composite(this.parentComposite, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);

        itemTypeLov = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);
        itemTypeLov.setSize(168, 25);
        itemTypeField = itemTypeLov.getTextField();
        setControl(composite);

        itemTypeField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent paramModifyEvent) {
                isModify = true;
            }
        });
    }

    public static void setValue(String value) {
        itemTypeLov.setSelectedItem(value);
    }

    @Override
    public boolean isPropertyModified(TCProperty tcproperty) throws Exception {
        return isModify;
    }

    @Override
    public void setModifiable(boolean flag) {
        modifiable = flag;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object getEditableValue() {
        if (null != itemTypeLov) {
            return itemTypeLov.getSelectedObject();
        }
        return "";
    }

    @Override
    public void load(TCProperty tcproperty) throws Exception {
        if (null != itemTypeLov) {
            String propValue = tcproperty.getStringValue();
            itemTypeLov.setText(propValue);
        }
        isModify = false;
    }

    @Override
    public void load(TCPropertyDescriptor tcpropertydescriptor) throws Exception {
        ArrayList<String> values = Utils.getLOVList(tcpropertydescriptor.getLOV());
        if (values.size() > 0) {
            for (String value : values) {
                itemTypeLov.addItem(value);
            }
        }

        String defaultValue = tcpropertydescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0) {
            itemTypeLov.setSelectedItem(defaultValue);
        }
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty tcproperty) throws Exception {
        Object valueObj = getEditableValue();
        if (null != valueObj) {
            tcproperty.setStringValue(valueObj.toString());
        }
        return tcproperty;
    }

    @Override
    public void setUIFValue(Object obj) {

    }

}
