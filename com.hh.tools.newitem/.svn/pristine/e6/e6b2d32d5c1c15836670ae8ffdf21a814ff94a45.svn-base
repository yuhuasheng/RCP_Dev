package com.hh.tools.renderingHint;

import java.io.File;
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
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class LXPartTypePropertyBean extends AbstractPropertyBean<Object> {

    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected Composite parentComposite;
    private TCComponent propComponent = null;

    // 特殊类型配置表映射数据
    public static Map<String, String[]> partTypeMappingDataMap = null;
    public static boolean partTypeMappingDataLoadFlag = false;

    // Part type与MG对应表映射数据
    public static Map<String, String> partTypeMGMappingDataMap = null;
    public static boolean partTypeMGMappingDataLoadFlag = false;

    @SuppressWarnings("deprecation")
    static LOVComboBox partTypePropLov = null;
    Text partTypeTextField = null;
    boolean isModify = false;

    // 是否第一次加载
    boolean firstLoadDataFlag = false;

    public LXPartTypePropertyBean(Control paramControl) {
        super(paramControl);
        this.parentComposite = (Composite) paramControl;
        System.out.println("加载属性组件 LXPartTypePropertyBean 1");
        loadPropertyPanel();
    }

    public LXPartTypePropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                  Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        this.parentComposite = paramComposite;
        System.out.println("加载属性组件 LXPartTypePropertyBean 2");
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

        partTypePropLov = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);
        partTypePropLov.setSize(168, 25);
        partTypeTextField = partTypePropLov.getTextField();

        // 设置下拉数据
        setPartTypeList("#LXPARTTYPE#");
        setControl(composite);

        partTypeTextField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent paramModifyEvent) {
                String selectedText = partTypeTextField.getText();
                System.out.println("partTypePropLov => " + selectedText);
                isModify = true;
                mandatory = true;
                setDirty(true);

                if (!firstLoadDataFlag) {
                    // 修改ProcurementType的值
                    LXPartProcurementTypePropertyBean.setProcurementTypeValueByPartType(selectedText);
                }

                firstLoadDataFlag = false;
            }
        });

        // 线程加载partType的映射数据
        threadLoadPartTypeMappingData();
    }

    /**
	 * 等待映射数据加载完成
	 */
    public static void waitMappingDataLaodOver() {
    	// 验证数据是否加载完成
        while (!partTypeMappingDataLoadFlag) {
        	System.out.println("线程等待.特殊类型配置表.映射数据加载完成.");
        }
    }

    /**
	 * 等待MG映射数据加载完成
	 */
    public static void waitMGMappingDataLaodOver() {
        // 验证数据是否加载完成
        while (!partTypeMGMappingDataLoadFlag) {
        	System.out.println("线程等待.Part type与MG对应表.映射数据加载完成.");
        }
    }

    /**
	 * 线程加载partType的映射数据
	 * @return
	 */
    private static void threadLoadPartTypeMappingData() {
        partTypeMappingDataMap = new HashMap<String, String[]>();
        partTypeMappingDataLoadFlag = false;

        partTypeMGMappingDataMap = new HashMap<String, String>();
        partTypeMGMappingDataLoadFlag = false;

        new Thread(new Runnable() {
            public void run() {
            	System.out.println("加载[特殊类型配置表][Part type与MG对应表]数据 ");
				
				// 从数据集 读取映射数据
                TCComponentDataset dataset = Utils.getDatasetBypreferenceName("FX8_L10Prop_Data_File_Template");
                if (null != dataset) {
                    Workbook book = null;
                    try {
                        TCComponentTcFile[] tcfiles = dataset.getTcFiles();
                        if (null != tcfiles && tcfiles.length > 0) {
                            TCComponentTcFile componentTcFile = tcfiles[0];
                            File datasetFile = componentTcFile.getFmsFile();

                            // 从Excel文件中 组装映射数据
							book = ExcelUtil.getWorkbook(datasetFile);
							Sheet sheetMapping = book.getSheet("特殊类型配置表");
                            loadPartTypeMappingData(sheetMapping);

                            Sheet sheetMG = book.getSheet("Part type与MG对应表");
                            loadPartTypeMGMappingData(sheetMG);

                        }

                        System.out.println("[特殊类型配置表] partTypeMappingDataMap => " + partTypeMappingDataMap.size());
						System.out.println("[Part type与MG对应表] partTypeMGMappingDataMap => " + partTypeMGMappingDataMap.size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                partTypeMappingDataLoadFlag = true;
                partTypeMGMappingDataLoadFlag = true;
                System.out.println("加载[特殊类型配置表][Part type与MG对应表]完成 => ");
            }
        }).start();
    }

    private static void loadPartTypeMappingData(Sheet sheet) {
        Iterator<Row> rows = sheet.rowIterator();
        Row itemRow = null;

        String[] dataArr = null;
        String partType = null;
        String materialType = null;
        String procurementType = null;

        while (rows.hasNext()) {
            itemRow = rows.next();

            // 获取 Part Type, Material Type,Procurement Type
            partType = ExcelUtil.getCellValue(itemRow.getCell(0));
            materialType = ExcelUtil.getCellValue(itemRow.getCell(1));
            procurementType = ExcelUtil.getCellValue(itemRow.getCell(2));

            if (null != partType && !"".equals(partType)) {
                dataArr = new String[]{procurementType, materialType};
                partTypeMappingDataMap.put(partType, dataArr);
            }
        }
    }

    private static void loadPartTypeMGMappingData(Sheet sheet) {
        Iterator<Row> rows = sheet.rowIterator();
        Row itemRow = null;
        String partType = null;
        String mgType = null;

        while (rows.hasNext()) {
            itemRow = rows.next();

            // 获取 Part Type, MG
            partType = ExcelUtil.getCellValue(itemRow.getCell(0));
            mgType = ExcelUtil.getCellValue(itemRow.getCell(1));

            if (null != partType && !"".equals(partType)) {
                partTypeMGMappingDataMap.put(partType, mgType);
            }
        }
    }

    /**
	 * 加载下拉列表
	 * @return 
	 */
    public void setPartTypeList(String folderName) {
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
                    partTypePropLov.addItem(tcComponent.getProperty("object_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        if (null != partTypePropLov) {
            return partTypePropLov.getSelectedObject();
        }
        return "";
    }

    @Override
    public void load(TCProperty tcproperty) throws Exception {
        propComponent = tcproperty.getTCComponent();
        if (null != partTypePropLov) {
            firstLoadDataFlag = true;
            partTypePropLov.setText(tcproperty.getStringValue());
        }
        isModify = false;
        setDirty(false);
    }

    @Override
    public void load(TCPropertyDescriptor tcpropertydescriptor) throws Exception {
        setDirty(false);
        String defaultValue = tcpropertydescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0 && null != partTypePropLov) {
            partTypePropLov.setSelectedItem(defaultValue);
        }
        setDirty(false);
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty tcproperty) throws Exception {
        String partTypeValue = getEditableValue().toString();
        tcproperty.setStringValue(partTypeValue);

        String materialGroupValue = "";
        if (null != partTypeValue && !"".equals(partTypeValue)) {
            if (!partTypeMGMappingDataLoadFlag) {
                waitMGMappingDataLaodOver();
            }
            materialGroupValue = partTypeMGMappingDataMap.get(partTypeValue);
        }
        propComponent.setProperty("fx8_MaterialGroup", materialGroupValue);

        return tcproperty;
    }

    @Override
    public void setUIFValue(Object obj) {

    }

}
