package com.hh.tools.renderingHint;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class LXPartMaterialFeatureTypePropertyBean extends AbstractPropertyBean<Object> {

    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected Composite parentComposite;

    public static Map<String, String> lovListFolderMappingMap = null;

    @SuppressWarnings("deprecation")
    static LOVComboBox materialFeatureTypePropLov = null;
    Text materialFeatureTypeTextField = null;
    boolean isModify = false;

    // 是否第一次加载
    boolean firstLoadDataFlag = false;

    public LXPartMaterialFeatureTypePropertyBean(Control paramControl) {
        super(paramControl);
        this.parentComposite = (Composite) paramControl;
        System.out.println("加载属性组件 LXPartMaterialFeatureTypePropertyBean 1");
        loadPropertyPanel();
    }

    public LXPartMaterialFeatureTypePropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                                 Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        this.parentComposite = paramComposite;
        System.out.println("加载属性组件 LXPartMaterialFeatureTypePropertyBean 2");
        loadPropertyPanel();
    }

    /**
	 * 加载属性面板
	 */
    @SuppressWarnings("deprecation")
    private void loadPropertyPanel() {
        System.out.println("LXPartMaterialFeatureTypePropertyBean loadPropertyPanel");
        this.composite = new Composite(this.parentComposite, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);

        materialFeatureTypePropLov = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);
        materialFeatureTypePropLov.setSize(168, 25);
        materialFeatureTypeTextField = materialFeatureTypePropLov.getTextField();
        // 加载下拉数据
        loadMaterialFeatureType();
        setControl(composite);

        materialFeatureTypeTextField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent paramModifyEvent) {
                String selectedText = materialFeatureTypeTextField.getText();
                System.out.println("materialFeatureTypePropLov => " + selectedText);
                isModify = true;
                mandatory = true;
                setDirty(true);

                // 不是第一次加载 则清除级联数据
                if (!firstLoadDataFlag) {
                    LXPartMaterialFeaturePropertyBean.clearPropLov();

                    if (null != selectedText) {
                        if (lovListFolderMappingMap.containsKey(selectedText)) {
                            String folderName = lovListFolderMappingMap.get(selectedText);
                            LXPartMaterialFeaturePropertyBean.loadMaterialFeatureList(folderName);
                        }
                    }
                }

                // 当加载完成后的操作 
                firstLoadDataFlag = false;
            }
        });
    }

    /**
	 * 加载下拉列表
	 * @return 
	 */
    private void loadMaterialFeatureType() {
        System.out.println("LXPartMaterialFeatureTypePropertyBean loadMaterialFeatureType");
        lovListFolderMappingMap = new HashMap<String, String>();
        ArrayList<String> lovList = getMaterialFeatureTypeLovList();
        if (null != lovList) {
            String lovListValue = null;
            String folderName = null;

            for (int i = 0; i < lovList.size(); i++) {
                lovListValue = lovList.get(i);
                materialFeatureTypePropLov.addItem(lovListValue);
                folderName = getFolderNameByValue(lovListValue);
                lovListFolderMappingMap.put(lovListValue, folderName);
            }
        }
    }

    private static String getFolderNameByValue(String MaterialFeatureTypeVal) {
        String folderName = null;
        if (null != MaterialFeatureTypeVal && !"".equals(MaterialFeatureTypeVal)) {
            folderName = "#" + MaterialFeatureTypeVal.toUpperCase() + "FEATURE#";

            if ("Memory".equalsIgnoreCase(MaterialFeatureTypeVal)) {
                folderName = "#" + "MEM" + "FEATURE#";
            }
        }

        return folderName;
    }

    /**
	 * 获取 MaterialFeatureTypeVal 指定的文件夹名称
	 * @param MaterialFeatureTypeVal
	 * @return
	 */
    public static String getMappingFolderName(String MaterialFeatureTypeVal) {
        String folderName = null;
        if (null != MaterialFeatureTypeVal && !"".equals(MaterialFeatureTypeVal)) {
            if (lovListFolderMappingMap.containsKey(MaterialFeatureTypeVal)) {
                folderName = lovListFolderMappingMap.get(MaterialFeatureTypeVal);
            } else {
                folderName = getFolderNameByValue(MaterialFeatureTypeVal);
            }
        }
        return folderName;
    }

    /**
	 * 获取LOV列表
	 * @return
	 */
    private static ArrayList<String> getMaterialFeatureTypeLovList() {
        System.out.println("LXPartMaterialFeatureTypePropertyBean getMaterialFeatureTypeLovList");
        TCSession session = Utils.getTCSession();
        TCComponentListOfValuesType lovType = null;
        TCComponentListOfValues lov = null;
        try {
            lovType = (TCComponentListOfValuesType) session.getTypeComponent(ITypeName.ListOfValues);
            lov = lovType.findLOVByName("FX8_MaterialFeatureTypeLOV");
        } catch (TCException e) {
            e.printStackTrace();
        }

        if (null != lov) {
            return Utils.getLOVList(lov);
        }

        return null;
    }

    @Override
    public boolean isPropertyModified(TCProperty tcproperty) throws Exception {
        System.out.println("LXPartMaterialFeatureTypePropertyBean isPropertyModified");
        return isModify;
    }

    @Override
    public void setModifiable(boolean flag) {
        System.out.println("LXPartMaterialFeatureTypePropertyBean setModifiable");
        modifiable = flag;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object getEditableValue() {
        System.out.println("LXPartMaterialFeatureTypePropertyBean getEditableValue");
        if (null != materialFeatureTypePropLov) {
            return materialFeatureTypePropLov.getSelectedObject();
        }
        return null;
    }

    @Override
    public void load(TCProperty tcproperty) throws Exception {
        System.out.println("LXPartMaterialFeatureTypePropertyBean load 1");
        if (null != materialFeatureTypePropLov) {
            firstLoadDataFlag = true;
            String propValue = tcproperty.getStringValue();
            materialFeatureTypePropLov.setText(propValue);
        }
        isModify = false;
        setDirty(false);
    }

    @Override
    public void load(TCPropertyDescriptor tcpropertydescriptor) throws Exception {
        System.out.println("LXPartMaterialFeatureTypePropertyBean load 2");
        setDirty(false);
        String defaultValue = tcpropertydescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0 && null != materialFeatureTypePropLov) {
            firstLoadDataFlag = true;
            materialFeatureTypePropLov.setSelectedItem(defaultValue);
        }
        setDirty(false);
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty tcproperty) throws Exception {
        System.out.println("LXPartMaterialFeatureTypePropertyBean getPropertyToSave => " + tcproperty.getStringValue());
        Object valueObj = getEditableValue();
        if (null != valueObj) {
            tcproperty.setStringValue(valueObj.toString());
        }
        return tcproperty;
    }

    @Override
    public void setUIFValue(Object obj) {
        System.out.println("LXPartMaterialFeatureTypePropertyBean setUIFValue");
    }

    @Override
    public void dispose() {
        System.out.println("LXPartMaterialFeatureTypePropertyBean dispose");
        super.dispose();
    }
}
