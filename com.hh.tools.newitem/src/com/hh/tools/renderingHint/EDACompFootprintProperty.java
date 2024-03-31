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

import com.hh.tools.customerPanel.FootprintZIPFileFilter;
import com.hh.tools.newitem.CreateObject;
import com.hh.tools.newitem.RelationName;
import com.hh.tools.newitem.Utils;
import com.hh.tools.renderingHint.util.EDACompDatasetDialog;
import com.hh.tools.renderingHint.util.IDatasetFolderOperation;
import com.hh.tools.util.CISFileStorageUtil;
import com.hh.tools.util.DatasetTypeUtil;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.stylesheet.InterfaceBufferedPropertyComponent;
import com.teamcenter.rac.stylesheet.InterfaceLegacyPropertyComponent;
import com.teamcenter.rac.stylesheet.InterfacePropertyComponent;
import com.teamcenter.rac.util.MessageBox;

public class EDACompFootprintProperty extends JPanel implements InterfacePropertyComponent,
        InterfaceBufferedPropertyComponent, InterfaceLegacyPropertyComponent, IDatasetFolderOperation {

    private String property = "";
    private boolean mandatory = false;
    private boolean modifiable = true;
    private boolean savable;

    // 数据集所在的文件夹组件
    private static JTextField filePathText;
    private CISFileStorageUtil cisFileStorageUtil;
    private String defalutDatasetFolder = "FootPrint";
    private static String otherFolderPath = null;
    private static JButton button;

    // 关联的数据
    private static File uploadRelationFile = null;
    private static TCComponentDataset relationDataset = null;

    private TCComponentDataset currentDataset = null;

    private static boolean isCanUpload = true;

    public EDACompFootprintProperty() {
        super();
        loadPropertyPanel();
    }

    public static void setFilePathEditable() {
        filePathText.setEditable(false);
    }

    public static void setCanUpload(boolean isCan) {
        button.setEnabled(true);
        isCanUpload = isCan;
    }

    private void loadPropertyPanel() {
        cisFileStorageUtil = CISFileStorageUtil.getInstance();

        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        filePathText = new JTextField();
        filePathText.setPreferredSize(new Dimension(350, 25));

        button = new JButton("浏览");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                filePathText.setEditable(false);

//				System.out.println("footprint otherFolderPath=====" + otherFolderPath);
                // 获取Category、PartType 属性值
                if (null == otherFolderPath || "".equals(otherFolderPath)) {
                	MessageBox.post("请选择Footprint Category！", "Warn...", MessageBox.WARNING);
                    return;
                }

                if (!cisFileStorageUtil.getLoadDataFlag()) {
                    MessageBox.post("CISFileStorage Data Loading...", "Warn...", MessageBox.ERROR);
                    return;
                }

                new Thread(new Runnable() {
                    public void run() {
                        FootprintZIPFileFilter zipFilter = new FootprintZIPFileFilter();
                        new EDACompDatasetDialog("Footprint Dataset", EDACompFootprintProperty.this)
                                .setFileFilter(zipFilter);
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
    public static void loadCurrentDatasetFolder() {
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
    private static void setDatasetPath() {
        otherFolderPath = "";
        // 获取Category、PartType 属性值
        String categoryVal = EDACompCategoryProperty.getValue();
        String footprintCategoryVal = EDACompFootprintCategoryProperty.getValue();
        if (StringUtils.isNotEmpty(categoryVal) && StringUtils.isNotEmpty(footprintCategoryVal)) {
            if ("Special".equalsIgnoreCase(categoryVal)) {
                otherFolderPath = "Special/" + footprintCategoryVal;
            } else {
                otherFolderPath = "Standard/" + footprintCategoryVal;
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
            }
        }
        this.savable = false;
        return null;
    }

    public void setDateSheet(TCComponent paramTCComponent) throws TCException {
    	// 获取数据集类型工具
        DatasetTypeUtil datastTypeUtil = DatasetTypeUtil.getInstance();

        // DataSheet数据集操作
        TCComponentDataset dataSheetDatasetComp = null;
        if (null != uploadRelationFile) {
            // 创建数据集
            String relationPath = uploadRelationFile.getAbsolutePath();
            String relationFileName = uploadRelationFile.getName();
            String datasetTypeName = datastTypeUtil.getDatasetType(relationFileName);
            String dstDefintionType = datastTypeUtil.getDatasetDefinitionType(datasetTypeName);
            // 创建数据集
            dataSheetDatasetComp = CreateObject.createDataSet(paramTCComponent.getSession(), relationPath,
                    "FX8_FootPrint", relationFileName, "FX8_FootPrint");
//			System.out.println("dataSheetDatasetComp===========" + dataSheetDatasetComp);
        }

        if (null != relationDataset) {
            TCComponentTcFile[] tcfiles = relationDataset.getTcFiles();
            for (int i = 0; i < tcfiles.length; i++) {
                TCComponentTcFile onetcfile = tcfiles[i];
                String filename = onetcfile.getProperty("original_file_name");
                File tempfile = onetcfile.getFmsFile();
                dataSheetDatasetComp = CreateObject.createDataSet(paramTCComponent.getSession(),
                        tempfile.getAbsolutePath(), "FX8_FootPrint", filename, "FX8_FootPrint");
            }
        }

        if (null != dataSheetDatasetComp) {
            try {
                clearDateSheet(paramTCComponent);
                paramTCComponent.add(RelationName.FOOTPRINT, dataSheetDatasetComp);
            } catch (TCException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void clearDateSheet(TCComponent paramTCComponent) {
        try {
            TCComponent[] refComp = paramTCComponent.getRelatedComponents(RelationName.FOOTPRINT);
//			System.out.println("===========TCComponent[]=========length=" + refComp.length);
            if (null != refComp && refComp.length > 0) {
                for (int i = 0; i < refComp.length; i++) {
                    TCComponent itemRefComp = refComp[i];
                    paramTCComponent.remove(RelationName.FOOTPRINT, itemRefComp);
                }
            }
        } catch (TCException e) {
            e.printStackTrace();
        }
    }

    @Override
    public TCProperty saveProperty(TCComponent paramTCComponent) throws Exception {
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (this.savable) {
//			System.out.println("footprint================saveProperty(TCComponent paramTCComponent)");
            return localTCProperty;
        }
        return null;
    }

    @Override
    public TCProperty saveProperty(TCProperty paramTCProperty) throws Exception {
//		System.out.println("datesheet================saveProperty(TCProperty paramTCProperty)");
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
//		System.out.println("datesheet================isPropertyModified(TCComponent paramTCComponent)");
        if (this.property != null) {
            TCProperty localTCProperty = paramTCComponent.getTCProperty(this.property);
            return isPropertyModified(localTCProperty);
        }
        return false;
    }

    @Override
    public boolean isPropertyModified(TCProperty paramTCProperty) throws Exception {
//		System.out.println("datesheet================isPropertyModified(TCProperty paramTCProperty)");
        return true;
    }

    @Override
    public void load(TCComponent paramTCComponent) throws Exception {
//		System.out.println("footprint================load(TCComponent paramTCComponent)");
        if (this.property != null) {
            TCProperty localTCProperty = paramTCComponent.getTCProperty(this.property);
            TCComponent datasetComponent = paramTCComponent.getRelatedComponent(RelationName.FOOTPRINT);
            if (datasetComponent != null) {
                currentDataset = (TCComponentDataset) datasetComponent;
            }
            load(localTCProperty);
        }
    }

    @Override
    public void load(TCProperty paramTCProperty) throws Exception {
//		System.out.println("datesheet================load(TCProperty paramTCProperty)");
        String propValue = paramTCProperty.getStringValue();
//		setDatasetPath();
//		System.out.println("footprint================propValue=" + propValue);
        filePathText.setText(propValue);
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
        if (paramObject != null) {
            filePathText.setText(paramObject.toString());
        } else {
            filePathText.setText("");
        }
    }

    @Override
    public boolean isPropertyModified(TCProperty arg0, boolean arg1) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPropertyModified(TCComponent arg0, boolean arg1) throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TCComponentDataset getCurrentDataset() {
        // TODO Auto-generated method stub
        return currentDataset;
    }

    @Override
    public boolean isCanUpload() {
        // TODO Auto-generated method stub
        return isCanUpload;
    }
}
