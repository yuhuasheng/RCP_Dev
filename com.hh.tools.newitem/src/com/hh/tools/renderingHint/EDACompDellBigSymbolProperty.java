package com.hh.tools.renderingHint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import com.hh.tools.newitem.CreateObject;
import com.hh.tools.newitem.RelationName;
import com.hh.tools.renderingHint.util.EDACompDatasetDialog;
import com.hh.tools.renderingHint.util.IDatasetFolderOperation;
import com.hh.tools.util.CISFileStorageUtil;
import com.hh.tools.util.DatasetTypeUtil;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.stylesheet.InterfaceBufferedPropertyComponent;
import com.teamcenter.rac.stylesheet.InterfacePropertyComponent;
import com.teamcenter.rac.util.MessageBox;

public class EDACompDellBigSymbolProperty extends JPanel
        implements InterfacePropertyComponent, InterfaceBufferedPropertyComponent, IDatasetFolderOperation {

    private String property = "";
    private boolean mandatory = false;
    private boolean modifiable = true;
    private boolean savable;

    // 数据集所在的文件夹组件
    private static JTextField filePathText;
    private CISFileStorageUtil cisFileStorageUtil;
    private String defalutDatasetFolder = "BigSymbols/Dell";
    private static String otherFolderPath = null;

    // 关联的数据
    private static File uploadRelationFile = null;
    private static TCComponentDataset relationDataset = null;
    private TCComponentDataset defaultDataset;

    public EDACompDellBigSymbolProperty() {
        super();
        loadPropertyPanel();
    }

    public static void setFilePathEditable() {
        filePathText.setEditable(false);
    }

    private void loadPropertyPanel() {
        cisFileStorageUtil = CISFileStorageUtil.getInstance();

        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        filePathText = new JTextField();
        filePathText.setPreferredSize(new Dimension(350, 25));

        JButton button = new JButton("浏览");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                filePathText.setEditable(false);

                // 获取Category、PartType 属性值
                if (null == otherFolderPath || "".equals(otherFolderPath)) {
                	MessageBox.post("请选择Category和Part Type！", "Warn...", MessageBox.WARNING);
                    return;
                }

                if (!cisFileStorageUtil.getLoadDataFlag()) {
                    MessageBox.post("CISFileStorage Data Loading...", "Warn...", MessageBox.ERROR);
                    return;
                }

                new Thread(new Runnable() {
                    public void run() {
                        new EDACompDatasetDialog("Dell Big Symbols Dataset", EDACompDellBigSymbolProperty.this);
                    }
                }).start();
            }
        });

        this.add(filePathText, BorderLayout.WEST);
        this.add(button, BorderLayout.CENTER);
    }

    /**
	 * 加载当前数据集文件夹对象(PartType属性下拉值发生改变调用)
	 */
    public void loadCurrentDatasetFolder() {
        // 获取Category、PartType 属性值
        setDatasetPath();
        if (null != relationDataset) {
            relationDataset = null;
            filePathText.setText("");
        }
    }

    /**
	 * 设置数据集文件夹的路径
	 * 
	 * @return
	 */
    private void setDatasetPath() {
        otherFolderPath = "";
        // 获取Category、PartType 属性值
        String categoryVal = EDACompCategoryProperty.getValue();

        if (StringUtils.isNotEmpty(categoryVal)) {
            otherFolderPath = categoryVal;
            try {
                // 取数据集列表
                TCComponent[] tempComp = getDatasetFolder().getRelatedComponents("contents");
                if (null != tempComp && tempComp.length > 0) {
                    TCComponent itemComp = null;

                    for (int i = 0; i < tempComp.length; i++) {
                        itemComp = tempComp[i];
                        if (itemComp instanceof TCComponentDataset) {
                            defaultDataset = (TCComponentDataset) itemComp;
                            break;
                        }
                    }
                }
            } catch (TCException e) {
                e.printStackTrace();
            }
        }
    }

    /**
	 * 清除静态数据
	 */
    public static void clearStaticData() {
        otherFolderPath = null;
        uploadRelationFile = null;
        relationDataset = null;
    }

    @Override
    public String getFolderNodes() {
        return defalutDatasetFolder + "/" + otherFolderPath;
    }

    @Override
    public TCComponentFolder getDatasetFolder() {
        return cisFileStorageUtil.getDatasetFolderComp(defalutDatasetFolder + "/" + otherFolderPath);
    }

    @Override
    public void setRelationFile(File uploadFile) {
        savable = true;
        uploadRelationFile = uploadFile;
        relationDataset = null;
        filePathText.setText(uploadRelationFile.getAbsolutePath());
    }

    @Override
    public File getRelationFile() {
        return uploadRelationFile;
    }

    @Override
    public void setRelationDataset(TCComponentDataset dataset) {
        savable = true;
        relationDataset = dataset;
        uploadRelationFile = null;
        filePathText.setText(dataset.toDisplayString());
    }

    @Override
    public TCComponentDataset getRelationDataset() {
        return relationDataset;
    }

    @Override
    public void clearRelationData() {
        uploadRelationFile = null;
        relationDataset = null;
        filePathText.setText("");
    }

    public TCProperty getPropertyToSave(TCComponent paramTCComponent) throws Exception {
        if (this.property != null) {
            if (uploadRelationFile != null || relationDataset != null) {
                setDateSheet(paramTCComponent);
                return null;
            } else {
                if (defaultDataset != null) {
                    setSchematicPart(paramTCComponent, defaultDataset);
                }
            }
        }
        this.savable = false;
        return null;
    }

    public void setDateSheet(TCComponent paramTCComponent) {
        // 获取数据集类型工具
        DatasetTypeUtil datastTypeUtil = DatasetTypeUtil.getInstance();

        // DataSheet数据集操作
        TCComponentDataset dataSheetDatasetComp = null;
        try {
            if (null != uploadRelationFile) {
                // 创建数据集
                String relationPath = uploadRelationFile.getAbsolutePath();
                String relationFileName = uploadRelationFile.getName();
                String datasetTypeName = datastTypeUtil.getDatasetType(relationFileName);
                String dstDefintionType = datastTypeUtil.getDatasetDefinitionType(datasetTypeName);
                // 创建数据集
                dataSheetDatasetComp = CreateObject.createDataSet(paramTCComponent.getSession(), relationPath,
                        datasetTypeName, relationFileName, dstDefintionType);
            }
            if (null != relationDataset) {
                dataSheetDatasetComp = relationDataset;
            }

            if (dataSheetDatasetComp != null) {
                clearDateSheet(paramTCComponent);
                paramTCComponent.add(RelationName.DELLBIGSYMBOLREL, dataSheetDatasetComp);
                setSchematicPart(paramTCComponent, dataSheetDatasetComp);
            }
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void setSchematicPart(TCComponent paramTCComponent, TCComponentDataset dataSheetDatasetComp)
            throws TCException {
        String symbolName = paramTCComponent.getProperty(RelationName.DELLSYMBOL);
        String bigSymbolName = dataSheetDatasetComp.toDisplayString();
        if (StringUtils.isNotEmpty(symbolName) && StringUtils.isNotEmpty(bigSymbolName)) {
            paramTCComponent.setProperty("fx8_SchematicPart", symbolName.substring(0, symbolName.lastIndexOf(".")) + "/"
                    + bigSymbolName.substring(0, bigSymbolName.lastIndexOf(".")));
        }
    }

    public void clearDateSheet(TCComponent paramTCComponent) {
        try {
            TCComponent[] refComp = paramTCComponent.getRelatedComponents(RelationName.DELLBIGSYMBOLREL);
            if (null != refComp && refComp.length > 0) {
                for (int i = 0; i < refComp.length; i++) {
                    TCComponent itemRefComp = refComp[i];
                    paramTCComponent.remove(RelationName.DELLBIGSYMBOLREL, itemRefComp);
                }
            }
        } catch (TCException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TCProperty saveProperty(TCComponent paramTCComponent) throws Exception {
//		System.out.println("hp big symbol================saveProperty(TCComponent paramTCComponent)");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (this.savable) {
            return localTCProperty;
        }
        return null;
    }

    @Override
    public TCProperty saveProperty(TCProperty paramTCProperty) throws Exception {
//		System.out.println("hp big symbol================saveProperty(TCProperty paramTCProperty)");
//		TCProperty localTCProperty = getPropertyToSave(paramTCProperty);
//		if (this.savable) {
//			return localTCProperty;
//		}
        return null;
    }

    @Override
    public Object getEditableValue() {
//		System.out.println("datesheet================getEditableValue()");
        return filePathText.getText();
    }

    @Override
    public String getProperty() {
//		System.out.println("datesheet================getProperty()===" + property);
        return this.property;
    }

    @Override
    public boolean isMandatory() {
//		System.out.println("datesheet================isMandatory()");
        return this.mandatory;
    }

    @Override
    public boolean isPropertyModified(TCComponent paramTCComponent) throws Exception {
//		System.out.println("hp big symbol================isPropertyModified(TCComponent paramTCComponent)");
        if (this.property != null) {
            TCProperty localTCProperty = paramTCComponent.getTCProperty(this.property);
            return isPropertyModified(localTCProperty);
        }
        return false;
    }

    @Override
    public boolean isPropertyModified(TCProperty paramTCProperty) throws Exception {
//		System.out.println("hp big symbol================isPropertyModified(TCProperty paramTCProperty)");
        return false;
    }

    @Override
    public void load(TCComponent paramTCComponent) throws Exception {
//		System.out.println("hp big symbol================load(TCComponent paramTCComponent)");
        if (this.property != null) {
            TCComponent[] refComp = paramTCComponent.getRelatedComponents(RelationName.DELLBIGSYMBOLREL);
            if (null != refComp && refComp.length > 0) {
                for (int i = 0; i < refComp.length; i++) {
                    TCComponent itemRefComp = refComp[i];
                    filePathText.setText(itemRefComp.toDisplayString());
                }
            }
            setDatasetPath();
        }
    }

    @Override
    public void load(TCProperty paramTCProperty) throws Exception {
//		System.out.println("datesheet================load(TCProperty paramTCProperty)");
        setDatasetPath();
    }

    @Override
    public void load(TCComponentType paramTCComponentType) throws Exception {
//		System.out.println("datesheet================load(TCComponentType paramTCComponentType)");
        if (paramTCComponentType != null) {
            TCPropertyDescriptor localTCPropertyDescriptor = paramTCComponentType.getPropertyDescriptor(this.property);
            load(localTCPropertyDescriptor);
        }
    }

    @Override
    public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
//		System.out.println("datesheet================load(TCPropertyDescriptor paramTCPropertyDescriptor)");
        String defaultValue = paramTCPropertyDescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0 && null != filePathText) {
            filePathText.setText(defaultValue);
        }
    }

    @Override
    public void save(TCComponent paramTCComponent) throws Exception {
//		System.out.println("datesheet================save(TCComponent paramTCComponent)");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (this.savable) {
            paramTCComponent.setTCProperty(localTCProperty);
        }
    }

    @Override
    public void save(TCProperty paramTCProperty) throws Exception {
//		System.out.println("datesheet================save(TCProperty paramTCProperty)");
//		TCProperty localTCProperty = getPropertyToSave(paramTCProperty);
//		if ((this.savable) && (localTCProperty != null)) {
//			localTCProperty.getTCComponent().setTCProperty(paramTCProperty);
//		}
    }

    @Override
    public void setMandatory(boolean paramBoolean) {
//		System.out.println("datesheet================setMandatory(boolean paramBoolean)");
        this.mandatory = paramBoolean;
    }

    @Override
    public void setModifiable(boolean paramBoolean) {
//		System.out.println("datesheet================setModifiable(boolean paramBoolean)");
        this.modifiable = paramBoolean;
    }

    @Override
    public void setProperty(String paramString) {
//		System.out.println("datesheet================setProperty(String paramString)===" + property);
        this.property = paramString;
    }

    @Override
    public void setUIFValue(Object paramObject) {
//		System.out.println("datesheet================setUIFValue(Object paramObject)");
    }

    @Override
    public TCComponentDataset getCurrentDataset() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCanUpload() {
        // TODO Auto-generated method stub
        return false;
    }
}
