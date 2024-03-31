package com.hh.tools.renderingHint;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hh.tools.util.CISFileStorageUtil;
import com.hh.tools.renderingHint.util.EDACompDatasetDialog;
import com.hh.tools.renderingHint.util.IDatasetFolderOperation;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class EDACompSymbolPropertyBean extends AbstractPropertyBean<Object> implements IDatasetFolderOperation {

    private Composite composite;
    private Composite parentComposite;
    private static Text symbolText;
    private Button selectBtn;
    boolean isModify = false;

    // 数据集所在的文件夹组件
    private CISFileStorageUtil cisFileStorageUtil = null;
    private String defalutDatasetFolder = "Symbols/HP";
    private static String otherFolderPath = null;

    // 关联的数据
    public static File uploadRelationFile = null;
    public static TCComponentDataset relationDataset = null;

    public EDACompSymbolPropertyBean(Control paramControl) {
        super(paramControl);
        this.parentComposite = (Composite) paramControl;
        loadPropertyPanel();
    }

    public EDACompSymbolPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                     Map<?, ?> paramMap) {
        this.savable = true;
        this.parentComposite = paramComposite;
        loadPropertyPanel();
    }

    private void loadPropertyPanel() {
        System.out.println("EDACompSymbolPropertyBean loadPropertyPanel");
        cisFileStorageUtil = CISFileStorageUtil.getInstance();

        this.composite = new Composite(parentComposite, SWT.NONE);
        composite.setBackground(parentComposite.getBackground());
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.horizontalSpacing = 5;
        this.composite.setLayout(gridLayout);

        symbolText = new Text(this.composite, SWT.BORDER | SWT.LEFT);
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gridData.heightHint = 20;
        gridData.widthHint = 320;
        symbolText.setLayoutData(gridData);
        symbolText.setEditable(false);

        this.selectBtn = new Button(this.composite, SWT.NONE);
        this.selectBtn.setText("...");
        this.selectBtn.computeSize(80, 25);
        this.selectBtn.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        // 添加选择点击事件
        this.selectBtn.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                // 获取Category、PartType 属性值
                if (StringUtils.isEmpty(otherFolderPath)) {
                	MessageBox.post("请选择Category和Part Type！", "Warn...", MessageBox.WARNING);
                    return;
                }

                if (!cisFileStorageUtil.getLoadDataFlag()) {
                    MessageBox.post("CISFileStorage Data Loading...", "Warn...", MessageBox.WARNING);
                    return;
                }

                System.out.println("开始加载 HP Symbols Dataset Dialog");
                new Thread(new Runnable() {
                    public void run() {
                        new EDACompDatasetDialog("HP Symbols Dataset", EDACompSymbolPropertyBean.this);
                    }
                }).start();
            }
        });
        setControl(this.composite);
    }

    /**
	 * 加载当前数据集文件夹对象(PartType属性下拉值发生改变调用)
	 */
    public static void loadCurrentDatasetFolder() {
        // 获取Category、PartType 属性值
        setDatasetPath();
        if (null != relationDataset) {
            relationDataset = null;
            symbolText.setText("");
        }
    }

    @Override
    public String getFolderNodes() {
        return defalutDatasetFolder + "/" + otherFolderPath;
    }

    @Override
    public TCComponentFolder getDatasetFolder() {
    	System.out.println("EDACompSymbolPropertyBean 获取数据集文件夹");
        return cisFileStorageUtil.getDatasetFolderComp(defalutDatasetFolder + "/" + otherFolderPath);
    }

    @Override
    public void setRelationFile(File uploadFile) {
        uploadRelationFile = uploadFile;
        relationDataset = null;
        symbolText.setText(uploadRelationFile.getAbsolutePath());
    }

    @Override
    public File getRelationFile() {
        return uploadRelationFile;
    }

    @Override
    public void setRelationDataset(TCComponentDataset dataset) {
        relationDataset = dataset;
        uploadRelationFile = null;
        symbolText.setText(dataset.toDisplayString());
    }

    @Override
    public TCComponentDataset getRelationDataset() {
        return relationDataset;
    }

    @Override
    public void clearRelationData() {
        uploadRelationFile = null;
        relationDataset = null;
        symbolText.setText("");
    }

    @Override
    public boolean isPropertyModified(TCProperty arg0) throws Exception {
        return false;
    }

    @Override
    public Object getEditableValue() {
        return null;
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
        return null;
    }

    @Override
    public void load(TCProperty tcproperty) throws Exception {
        if (null != symbolText) {
            String propValue = tcproperty.getStringValue();
            if (propValue != null && propValue.length() > 0) {
                symbolText.setText(propValue);
            }
        }
        isModify = false;
        setDirty(false);
    }

    @Override
    public void load(TCPropertyDescriptor tcpropertydescriptor) throws Exception {
        setDirty(false);
        String defaultValue = tcpropertydescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0 && null != symbolText) {
            symbolText.setText(defaultValue);
        }
        setDirty(false);
    }

    @Override
    public void setModifiable(boolean flag) {
        modifiable = flag;
    }

    @Override
    public void setUIFValue(Object arg0) {

    }

    @Override
    public void dispose() {
        clearStaticData();
        super.dispose();
    }

    /**
	 * 设置数据集文件夹的路径
	 * 
	 * @return
	 */
    private static void setDatasetPath() {
        otherFolderPath = "";
        // 获取Category、PartType 属性值
        String categoryVal = EDACompCategoryPropertyBean.getValue();
        String partTypeVal = EDACompPartTypePropertyBean.getValue();

        if (StringUtils.isNotEmpty(categoryVal) && StringUtils.isNotEmpty(partTypeVal)) {
            otherFolderPath = categoryVal + "/" + partTypeVal;
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
    public TCComponentDataset getCurrentDataset() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCanUpload() {
        // TODO Auto-generated method stub
        return true;
    }

}
