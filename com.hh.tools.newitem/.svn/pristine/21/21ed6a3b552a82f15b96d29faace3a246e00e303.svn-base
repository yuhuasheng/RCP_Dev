package com.hh.tools.renderingHint;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.newitem.Utils;
import com.hh.tools.util.ExcelUtil;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.controls.LOVComboBox;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class LXPartProcurementTypePropertyBean extends AbstractPropertyBean<Object> {

    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected Composite parentComposite;

    private static Map<String, String> pcmmMappingDataMap = null;

    @SuppressWarnings("deprecation")
    static LOVComboBox procurementTypePropLov = null;
    Text procurementTypeTypeTextField = null;
    boolean isModify = false;

    public LXPartProcurementTypePropertyBean(Control paramControl) {
        super(paramControl);
        this.parentComposite = (Composite) paramControl;
        System.out.println("加载属性组件 LXPartProcurementTypePropertyBean 1");
        loadPropertyPanel();
    }

    public LXPartProcurementTypePropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                             Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        this.parentComposite = paramComposite;
        System.out.println("加载属性组件 LXPartProcurementTypePropertyBean 2");
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

        procurementTypePropLov = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);
        procurementTypePropLov.setSize(168, 25);
        procurementTypeTypeTextField = procurementTypePropLov.getTextField();

        // 设置下拉数据
        setProcurementTypeList("#PCMTTYPE#");
        setControl(composite);
        procurementTypePropLov.setSelectedItem("F");
        procurementTypePropLov.update();

        // 加载Procurement Type对Material Type映射
        pcmmMappingDataMap = new HashMap<String, String>();
        pcmmMappingDataMap.put("F", "ZROH");
        pcmmMappingDataMap.put("E", "ZMOD");
        pcmmMappingDataMap.put("X", "ZFRT");

        procurementTypeTypeTextField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent paramModifyEvent) {
                String selectedText = procurementTypeTypeTextField.getText();
                System.out.println("procurementTypePropLov => " + selectedText);
                isModify = true;
                mandatory = true;
                setDirty(true);

                // 设置 MaterialType的值
                String materialType = pcmmMappingDataMap.get(selectedText);
                if (null != materialType && !"".equals(materialType)) {
                    LXPartMaterialTypePropertyBean.setMaterialTypeValue(materialType);
                }
            }
        });


    }

    /**
	 * 加载下拉列表
	 * @return 
	 */
    public void setProcurementTypeList(String folderName) {
        // 查询工作区对象
        String[] keys = new String[]{Utils.getTextValue("Type"), Utils.getTextValue("OwningUser"),
                Utils.getTextValue("Name")};
        String[] values = new String[]{"Folder", "infodba (infodba)", folderName};
        List<InterfaceAIFComponent> phaseList = Utils.search("General...", keys, values);
        TCComponentFolder folder = null;
        if (phaseList != null && phaseList.size() > 0) {
            folder = (TCComponentFolder) phaseList.get(0);
        }

        try {
            TCComponent[] coms = folder.getRelatedComponents("contents");
            for (TCComponent tcComponent : coms) {
                if (tcComponent instanceof TCComponentForm)
                    procurementTypePropLov.addItem(tcComponent.getProperty("object_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
	 * 根据 Part Type 设置下拉列表的值
	 * @param partTypeValue
	 */
    public static void setProcurementTypeValueByPartType(String partTypeValue) {
        if (null != procurementTypePropLov) {
            String procurementTypeValue = "F";
            String materialTypeValue = "ZROH";
            if (null != partTypeValue && !"".equals(partTypeValue)) {

                if (!LXPartTypePropertyBean.partTypeMappingDataLoadFlag) {
                    LXPartTypePropertyBean.waitMappingDataLaodOver();
                }

                if (null != LXPartTypePropertyBean.partTypeMappingDataMap && LXPartTypePropertyBean.partTypeMappingDataMap.containsKey(partTypeValue)) {
                    String[] dataArr = LXPartTypePropertyBean.partTypeMappingDataMap.get(partTypeValue);
                    // 从映射Map中 获取对应的数据
                    procurementTypeValue = dataArr[0];
                    materialTypeValue = dataArr[1];
                }
            }
            procurementTypePropLov.setSelectedItem(procurementTypeValue);
            procurementTypePropLov.update();

            // 设置 MaterialType的值
            LXPartMaterialTypePropertyBean.setMaterialTypeValue(materialTypeValue);
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
        if (null != procurementTypePropLov) {
            return procurementTypePropLov.getSelectedObject();
        }
        return null;
    }

    @Override
    public void load(TCProperty tcproperty) throws Exception {
        if (null != procurementTypePropLov) {
            procurementTypePropLov.setText(tcproperty.getStringValue());
        }
        isModify = false;
        setDirty(false);
    }

    @Override
    public void load(TCPropertyDescriptor tcpropertydescriptor) throws Exception {
        setDirty(false);
        String defaultValue = tcpropertydescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0 && null != procurementTypePropLov) {
            procurementTypePropLov.setSelectedItem(defaultValue);
        }
        setDirty(false);
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty tcproperty) throws Exception {
        Object valueObj = getEditableValue();
        if (null != valueObj) {
            tcproperty.setStringValue(valueObj.toString());
        }
        System.out.println("getPropertyToSave => " + tcproperty.getStringValue());
        return tcproperty;
    }

    @Override
    public void setUIFValue(Object obj) {

    }

}
