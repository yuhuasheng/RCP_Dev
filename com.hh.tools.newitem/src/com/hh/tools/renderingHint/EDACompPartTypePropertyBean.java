package com.hh.tools.renderingHint;

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

import com.hh.tools.newitem.GetPreferenceUtil;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.controls.LOVComboBox;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class EDACompPartTypePropertyBean extends AbstractPropertyBean<Object> {

    // ��ѡ������
    private final static String CATEGORY_PART_TYPE_NAME = "FX_Get_PartType_Values";

    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected Composite parentComposite;
    private TCComponent propComponent = null;

    @SuppressWarnings("deprecation")
    static LOVComboBox partTypePropLov = null;
    Text partTypeTextField = null;
    boolean isModify = false;

    // �б�������
    private static Map<String, String[]> categoryContentMap = null;
    public static boolean categoryContentLoadFlag = false;

    public EDACompPartTypePropertyBean(Control paramControl) {
        super(paramControl);
        this.parentComposite = (Composite) paramControl;
        loadPropertyPanel();
    }

    public EDACompPartTypePropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                       Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        this.parentComposite = paramComposite;
        loadPropertyPanel();
    }

    /**
	 * �����������
	 */
    @SuppressWarnings("deprecation")
    private void loadPropertyPanel() {
        // ����LOV����
        threadLoadListData();
        this.composite = new Composite(this.parentComposite, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);

        partTypePropLov = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);
        partTypePropLov.setSize(168, 25);
        partTypeTextField = partTypePropLov.getTextField();
        setControl(composite);

        partTypeTextField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent paramModifyEvent) {
                isModify = true;
                mandatory = true;
                setDirty(true);

                // ����SmallSymbol��DellSymbol��DataSheet���ݼ��ļ��ж���
                EDACompSymbolPropertyBean.loadCurrentDatasetFolder();
                EDACompDellSymbolPropertyBean.loadCurrentDatasetFolder();
                EDACompDataSheetPropertyBean.loadCurrentDatasetFolder();
            }
        });
    }

    /**
	 * �̼߳���LOV����
	 */
    private void threadLoadListData() {
        categoryContentMap = new HashMap<String, String[]>();
        categoryContentLoadFlag = false;
        new Thread(new Runnable() {
            public void run() {
            	System.out.println("���� EDACompPartType �б����� ");

                String[] listDataArr = null;
                TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
                String[] partTypeArr = new GetPreferenceUtil().getArrayPreference(session,
                        TCPreferenceService.TC_preference_site, CATEGORY_PART_TYPE_NAME);

                if (partTypeArr != null && partTypeArr.length > 0) {
                    for (String partTypeValue : partTypeArr) {
                        String[] partTypeArray = partTypeValue.split("::");
                        String categoryStr = partTypeArray[0];
                        String partTypeValueArr = partTypeArray[1];
                        listDataArr = partTypeValueArr.split(",");
                        categoryContentMap.put(categoryStr.toUpperCase(), listDataArr);
                    }
                }

                categoryContentLoadFlag = true;
                System.out.println("���� EDACompPartType �б�������� => " + categoryContentMap.size());
            }
        }).start();
    }

    /**
	 * ���������б�
	 * @return 
	 */
    public static void loadPartTypeList(String categoryVal) {
        if (null != partTypePropLov) {
            waitPartTypeLisDataLaodOver();
            String[] lovDataArr = null;
            if (null != categoryContentMap && categoryContentMap.containsKey(categoryVal)) {
                lovDataArr = categoryContentMap.get(categoryVal);
            }

            if (null != lovDataArr) {
                partTypePropLov.addItems(lovDataArr, lovDataArr);
                partTypePropLov.setSelectedIndex(0);
            }
        }
    }

    /**
	 * �ȴ��б����ݼ������
	 */
    public static void waitPartTypeLisDataLaodOver() {
        // ��֤�����Ƿ�������
        while (!categoryContentLoadFlag) {
        	System.out.println("�̵߳ȴ�.EDAComp PartType�б����ݼ������...");
        }
    }

    public static String getValue() {
        if (null != partTypePropLov) {
//			System.out.println("partTypePropLov => " + partTypePropLov.getSelectedItem());
            return partTypePropLov.getSelectedItem().toString();
        }
        return null;
    }

    /**
	 * ����б�����
	 */
    public static void clearPropLov() {
        if (partTypePropLov != null) {
            partTypePropLov.removeAllItems();
            partTypePropLov.setSelectedItem("");
            partTypePropLov.update();
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
        return null;
    }

    /**
	 * ����ҳ�� ���ô˼��ط���
	 */
    @Override
    public void load(TCProperty tcproperty) throws Exception {
        propComponent = tcproperty.getTCComponent();
        if (null != partTypePropLov) {

            // ���� MaterialFeatureType ֵ ���������б�
            String propCategory = propComponent.getProperty("fx8_Category");
            if (null != propCategory && !"".equals(propCategory)) {
                loadPartTypeList(propCategory);
            }

            partTypePropLov.setText(tcproperty.getStringValue());
        }

        isModify = false;
        setDirty(false);
    }

    /**
	 * ����ҳ�� ���ô˼��ط���
	 */
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
                    propComponent.setProperty("fx8_PartType", valueObj.toString());
                } catch (TCException e) {
                    e.printStackTrace();
                }
            }
        }
        super.dispose();
    }

}
