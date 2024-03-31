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
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class LXPartMaterialTypePropertyBean extends AbstractPropertyBean<Object> {

    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected Composite parentComposite;
    private TCComponent propComponent = null;

    @SuppressWarnings("deprecation")
    static LOVComboBox materialTypePropLov = null;
    Text materialTypeTextField = null;
    boolean isModify = false;

    public LXPartMaterialTypePropertyBean(Control paramControl) {
        super(paramControl);
        this.parentComposite = (Composite) paramControl;
        System.out.println("加载属性组件 LXPartMaterialTypePropertyBean 1");
        loadPropertyPanel();
    }

    public LXPartMaterialTypePropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                          Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        this.parentComposite = paramComposite;
        System.out.println("加载属性组件 LXPartMaterialTypePropertyBean 2");
        loadPropertyPanel();
    }

    /**
	 * 加载属性面板
	 */
    @SuppressWarnings("deprecation")
    private void loadPropertyPanel() {
        this.composite = new Composite(this.parentComposite, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);

        materialTypePropLov = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);
        materialTypePropLov.setSize(168, 25);
        materialTypeTextField = materialTypePropLov.getTextField();

        // 设置下拉数据
        setMaterialTypeList();
        setMaterialTypeValue(null);
        setControl(composite);
    }

    /**
	 * 加载下拉列表
	 * @return 
	 */
    public void setMaterialTypeList() {
        TCSession session = Utils.getTCSession();
        TCComponentListOfValuesType lovType = null;
        TCComponentListOfValues lov = null;
        try {
            lovType = (TCComponentListOfValuesType) session.getTypeComponent(ITypeName.ListOfValues);
            lov = lovType.findLOVByName("FX8_MaterialTypeLOV");
        } catch (TCException e) {
            e.printStackTrace();
        }

        if (null != lov) {
            ArrayList<String> lovList = Utils.getLOVList(lov);
            for (int i = 0; i < lovList.size(); i++) {
                materialTypePropLov.addItem(lovList.get(i));
            }
        }
    }

    /**
	 * 设置下拉列表的值
	 * @param materialTypeValue
	 */
    public static void setMaterialTypeValue(String materialTypeValue) {
        if (null != materialTypePropLov) {
            if (null == materialTypeValue || "".equals(materialTypeValue)) {
                materialTypeValue = "ZROH";
            }
            materialTypePropLov.setSelectedItem(materialTypeValue);
            materialTypePropLov.update();
        }
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
        if (null != materialTypePropLov) {
            return materialTypePropLov.getSelectedObject();
        }
        return null;
    }

    @Override
    public void load(TCProperty tcproperty) throws Exception {
        propComponent = tcproperty.getTCComponent();
        if (null != materialTypePropLov) {
            materialTypePropLov.setText(tcproperty.getStringValue());
        }
        isModify = false;
        setDirty(false);
    }

    @Override
    public void load(TCPropertyDescriptor tcpropertydescriptor) throws Exception {
        setDirty(false);
        String defaultValue = tcpropertydescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0 && null != materialTypePropLov) {
            materialTypePropLov.setSelectedItem(defaultValue);
        }
        setDirty(false);
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty tcproperty) throws Exception {
        return tcproperty;
    }

    @Override
    public void setUIFValue(Object obj) {

    }

    @Override
    public void dispose() {
        if (null != propComponent) {
            Object valueObj = getEditableValue();
            if (null != valueObj) {
                try {
                    propComponent.setProperty("fx8_MaterialType", valueObj.toString());
                } catch (TCException e) {
                    e.printStackTrace();
                }
            }
        }
        super.dispose();
    }

}
