package com.hh.tools.renderingHint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.controls.LOVComboBox;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class LXPartMaterialFeaturePropertyBean extends AbstractPropertyBean<Object> {

    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected Composite parentComposite;
    private TCComponent propComponent = null;

    private static Map<String, String[]> folderContentMap = null;

    @SuppressWarnings("deprecation")
    static LOVComboBox materialFeaturePropLov = null;
    Text materialFeatureTextField = null;
    boolean isModify = false;

    public LXPartMaterialFeaturePropertyBean(Control paramControl) {
        super(paramControl);
        this.parentComposite = (Composite) paramControl;
        loadPropertyPanel();
    }

    public LXPartMaterialFeaturePropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                             Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        this.parentComposite = paramComposite;
        loadPropertyPanel();
    }

    /**
	 * 加载属性面板
	 */
    @SuppressWarnings("deprecation")
    private void loadPropertyPanel() {
        System.out.println("LXPartMaterialFeaturePropertyBean loadPropertyPanel");
        // 加载LOV数据
        threadLoadLovData();
        this.composite = new Composite(this.parentComposite, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);

        materialFeaturePropLov = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);
        materialFeaturePropLov.setSize(168, 25);
        materialFeatureTextField = materialFeaturePropLov.getTextField();
        setControl(composite);
    }

    /**
	 * 线程加载LOV数据
	 */
    private void threadLoadLovData() {
        folderContentMap = new HashMap<String, String[]>();
        new Thread(new Runnable() {
            public void run() {
            	System.out.println("加载MaterialFeatureLOV数据 ");
                if (null != LXPartMaterialFeatureTypePropertyBean.lovListFolderMappingMap) {
                    System.out.println("LXPartMaterialFeatureTypePropertyBean.lovListFolderMappingMap => " + LXPartMaterialFeatureTypePropertyBean.lovListFolderMappingMap.size());
                    String[] lovDataArr = null;

                    for (String folderName : LXPartMaterialFeatureTypePropertyBean.lovListFolderMappingMap.values()) {
                        lovDataArr = searchLovFolder(folderName);
                        if (!folderContentMap.containsKey(folderName)) {
                            folderContentMap.put(folderName, lovDataArr);
                        }
                    }
                }
                System.out.println("加载MaterialFeatureLOV完成 => " + folderContentMap.size());
            }
        }).start();
    }

    /**
	 * 加载下拉列表
	 * @return 
	 */
    public static void loadMaterialFeatureList(String folderName) {
        System.out.println("LXPartMaterialFeaturePropertyBean loadMaterialFeatureList");
        if (null != materialFeaturePropLov) {
            String[] lovDataArr = null;
            if (null != folderContentMap && folderContentMap.containsKey(folderName)) {
                lovDataArr = folderContentMap.get(folderName);
            } else {
                lovDataArr = searchLovFolder(folderName);
            }

            if (null != lovDataArr) {
                materialFeaturePropLov.addItems(lovDataArr, lovDataArr);
            }
        }
    }

    /**
	 * 搜索LOV文件夹
	 * @param folderName
	 * @return
	 */
    private static String[] searchLovFolder(String folderName) {
        String[] lovDataArr = null;
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
            if (null != folder) {
                TCComponent[] coms = folder.getRelatedComponents("contents");
                lovDataArr = new String[coms.length];
                for (int i = 0; i < coms.length; i++) {
                    lovDataArr[i] = coms[i].getProperty("object_name");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lovDataArr;
    }

    /**
	 * 清空列表数据
	 */
    public static void clearPropLov() {
        if (materialFeaturePropLov != null) {
            materialFeaturePropLov.removeAllItems();
            materialFeaturePropLov.setSelectedItem("");
            materialFeaturePropLov.update();
        }
    }

    @Override
    public boolean isPropertyModified(TCProperty tcproperty) throws Exception {
        System.out.println("LXPartMaterialFeaturePropertyBean isPropertyModified");
        return isModify;
    }

    @Override
    public void setModifiable(boolean flag) {
        System.out.println("LXPartMaterialFeaturePropertyBean setModifiable");
        modifiable = flag;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object getEditableValue() {
        System.out.println("LXPartMaterialFeaturePropertyBean getEditableValue");
        if (null != materialFeaturePropLov) {
            return materialFeaturePropLov.getSelectedObject();
        }
        return null;
    }

    /**
	 * 汇总页面 调用此加载方法
	 */
    @Override
    public void load(TCProperty tcproperty) throws Exception {
        System.out.println("LXPartMaterialFeaturePropertyBean load 1");
        propComponent = tcproperty.getTCComponent();
        if (null != materialFeaturePropLov) {

        	// 根据 MaterialFeatureType 值 加载下拉列表
            String propMaterialFeatureType = propComponent.getProperty("fx8_MaterialFeatureType");
            String folderName = LXPartMaterialFeatureTypePropertyBean.getMappingFolderName(propMaterialFeatureType);
            System.out.println("LXPartMaterialFeaturePropertyBean load 1 folderName => " + folderName);
            if (null != folderName && !"".equals(folderName)) {
                loadMaterialFeatureList(folderName);
            }

            materialFeaturePropLov.setText(tcproperty.getStringValue());
        }

        isModify = false;
        setDirty(false);
    }

    /**
	 * 创建页面 调用此加载方法
	 */
    @Override
    public void load(TCPropertyDescriptor tcpropertydescriptor) throws Exception {
        System.out.println("LXPartMaterialFeaturePropertyBean load 2");
        setDirty(false);
        String defaultValue = tcpropertydescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0 && null != materialFeaturePropLov) {
            materialFeaturePropLov.setSelectedItem(defaultValue);
        }
        setDirty(false);
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty tcproperty) throws Exception {
        return tcproperty;
    }

    @Override
    public void setUIFValue(Object obj) {
        System.out.println("LXPartMaterialFeaturePropertyBean setUIFValue");
    }

    @Override
    public void dispose() {
        System.out.println("LXPartMaterialFeaturePropertyBean dispose");
        if (null != propComponent) {
            Object valueObj = getEditableValue();
            if (null != valueObj) {
                try {
                    propComponent.setProperty("fx8_MaterialFeature", valueObj.toString());
                } catch (TCException e) {
                    e.printStackTrace();
                }
            }
        }
        super.dispose();
    }
}
