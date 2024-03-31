package com.hh.tools.renderingHint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.hh.tools.newitem.GetPreferenceUtil;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.stylesheet.InterfaceBufferedPropertyComponent;
import com.teamcenter.rac.stylesheet.InterfaceLegacyPropertyComponent;
import com.teamcenter.rac.stylesheet.InterfacePropertyComponent;
import com.teamcenter.rac.util.Painter;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.util.combobox.iComboBox;

public class EDACompPartTypeProperty extends JPanel
        implements InterfacePropertyComponent, InterfaceBufferedPropertyComponent, InterfaceLegacyPropertyComponent {

    private static final long serialVersionUID = 1L;

    // 首选项名称
    private final static String CATEGORY_PART_TYPE_NAME = "FX_Get_PartType_Values";

    public static iComboBox partTypePropLov;
    private iTextField partTypeTextField;
    private String property = "";
    private boolean mandatory = false;
    private boolean modifiable = true;
    private boolean savable;

    // 列表集合数据
    private static Map<String, String[]> categoryContentMap = null;
    public static boolean categoryContentLoadFlag = false;

    public EDACompPartTypeProperty() {
        super();
        loadPropertyPanel();
    }

    private void loadPropertyPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        partTypePropLov = new iComboBox() {
            @Override
            public void paint(Graphics arg0) {
                super.paint(arg0);
                Painter.paintIsRequired(this, arg0);
            }
        };

        // 加载LOV数据
        threadLoadListData();

        this.add(partTypePropLov, BorderLayout.CENTER);

        partTypeTextField = partTypePropLov.getTextField();
        partTypeTextField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent arg0) {

            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
//				String selectedText = partTypeTextField.getText();
//				System.out.println("partType insertUpdate=====" + selectedText);
                savable = true;

                // 加载SmallSymbol、DellSymbol、DataSheet数据集文件夹对象
                EDACompSymbolProperty.loadCurrentDatasetFolder();
                EDACompDellSymbolProperty.loadCurrentDatasetFolder();
                EDACompDataSheetProperty.loadCurrentDatasetFolder();
//				EDACompDellBigSymbolProperty.loadCurrentDatasetFolder();
//				EDACompHpBigSymbolProperty.loadCurrentDatasetFolder();
            }

            @Override
            public void removeUpdate(DocumentEvent arg0) {

            }
        });
    }

    /**
	 * 线程加载LOV数据
	 */
    private void threadLoadListData() {
        categoryContentMap = new HashMap<String, String[]>();
        categoryContentLoadFlag = false;
        new Thread(new Runnable() {
            public void run() {
            	System.out.println("加载 EDACompPartType 列表数据 ");

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
                System.out.println("加载 EDACompPartType 列表数据完成 => " + categoryContentMap.size());
            }
        }).start();
    }

    /**
	 * 加载下拉列表
	 * 
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
	 * 等待列表数据加载完成
	 */
    public static void waitPartTypeLisDataLaodOver() {
    	// 验证数据是否加载完成
        while (!categoryContentLoadFlag) {
        	System.out.println("线程等待.EDAComp PartType列表数据加载完成...");
        }
    }

    /**
	 * 清空列表数据
	 */
    public static void clearPropLov() {
        if (partTypePropLov != null) {
            partTypePropLov.removeAllItems();
            partTypePropLov.setSelectedItem("");
            partTypePropLov.updateUI();
        }
    }

    public static String getValue() {
        if (null != partTypePropLov) {
//			System.out.println("partTypePropLov => " + partTypePropLov.getSelectedItem());
            return partTypePropLov.getSelectedItem().toString();
        }
        return null;
    }

    public TCProperty getPropertyToSave(TCComponent paramTCComponent) throws Exception {
        if (this.property != null) {
            TCProperty localTCProperty = paramTCComponent.getTCProperty(this.property);
            return getPropertyToSave(localTCProperty);
        }
        this.savable = false;
        return null;
    }

    public TCProperty getPropertyToSave(TCProperty tcproperty) throws Exception {
        Object valueObj = getEditableValue();
        if (null != valueObj) {
//			System.out.println("===========partType set tcproperty================"+property);
//			System.out.println("===========partType set tcproperty.getName()================"+tcproperty.getName());
//			System.out.println("===========partType tcproperty new value================"+valueObj.toString());
//			System.out.println("===========partType tcproperty old value================"+tcproperty.getStringValue());
            tcproperty.setStringValue(valueObj.toString());
        }
        return tcproperty;
    }

    @Override
    public TCProperty saveProperty(TCComponent paramTCComponent) throws Exception {
//		System.out.println("partType================saveProperty(TCComponent paramTCComponent)");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (this.savable) {
            return localTCProperty;
        }
        return null;
    }

    @Override
    public TCProperty saveProperty(TCProperty paramTCProperty) throws Exception {
//		System.out.println("partType================saveProperty(TCProperty paramTCProperty)");
        TCProperty localTCProperty = getPropertyToSave(paramTCProperty);
        if (this.savable) {
            return localTCProperty;
        }
        return null;
    }

    @Override
    public Object getEditableValue() {
//		System.out.println("partType================getEditableValue()");
        if (null != partTypePropLov) {
            return partTypePropLov.getSelectedObject();
        }
        return "";
    }

    @Override
    public String getProperty() {
//		System.out.println("partType================getProperty()==="+property);
        return this.property;
    }

    @Override
    public boolean isMandatory() {
//		System.out.println("partType================isMandatory()");
        return this.mandatory;
    }

    @Override
    public boolean isPropertyModified(TCComponent paramTCComponent) throws Exception {
//		System.out.println("partType================isPropertyModified(TCComponent paramTCComponent)");
        if (this.property != null) {
            TCProperty localTCProperty = paramTCComponent.getTCProperty(this.property);
            return isPropertyModified(localTCProperty);
        }
        return false;
    }

    @Override
    public boolean isPropertyModified(TCProperty paramTCProperty) throws Exception {
//		System.out.println("partType================isPropertyModified(TCProperty paramTCProperty)");
        if (paramTCProperty.getStringValue().equals(getEditableValue())) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void load(TCComponent paramTCComponent) throws Exception {
//		System.out.println("partType================load(TCComponent paramTCComponent)");
        if (this.property != null) {
            TCProperty localTCProperty = paramTCComponent.getTCProperty(this.property);
            load(localTCProperty);
        }
    }

    @Override
    public void load(TCProperty paramTCProperty) throws Exception {
//		System.out.println("partType================load(TCProperty paramTCProperty)");
        String propValue = paramTCProperty.getStringValue();
        partTypePropLov.setText(propValue);
    }

    @Override
    public void load(TCComponentType paramTCComponentType) throws Exception {
//		System.out.println("partType================load(TCComponentType paramTCComponentType)");
        if (paramTCComponentType != null) {
            TCPropertyDescriptor localTCPropertyDescriptor = paramTCComponentType.getPropertyDescriptor(this.property);
            load(localTCPropertyDescriptor);
        }
    }

    @Override
    public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
//		System.out.println("partType================load(TCPropertyDescriptor paramTCPropertyDescriptor)");
        String defaultValue = paramTCPropertyDescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0 && null != partTypePropLov) {
            partTypePropLov.setSelectedItem(defaultValue);
        }
    }

    @Override
    public void save(TCComponent paramTCComponent) throws Exception {
//		System.out.println("partType================save(TCComponent paramTCComponent)");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (this.savable) {
            paramTCComponent.setTCProperty(localTCProperty);
        }
    }

    @Override
    public void save(TCProperty paramTCProperty) throws Exception {
//		System.out.println("partType================save(TCProperty paramTCProperty)");
        TCProperty localTCProperty = getPropertyToSave(paramTCProperty);
        if ((this.savable) && (localTCProperty != null)) {
            localTCProperty.getTCComponent().setTCProperty(paramTCProperty);
        }
    }

    @Override
    public void setMandatory(boolean paramBoolean) {
//		System.out.println("partType================setMandatory(boolean paramBoolean)");
        this.mandatory = paramBoolean;
    }

    @Override
    public void setModifiable(boolean paramBoolean) {
//		System.out.println("partType================setModifiable(boolean paramBoolean)");
        this.modifiable = paramBoolean;
    }

    @Override
    public void setProperty(String paramString) {
//		System.out.println("partType================setProperty(String paramString)==="+property);
        this.property = paramString;
    }

    @Override
    public void setUIFValue(Object paramObject) {
//		System.out.println("partType================setUIFValue(Object paramObject)");
        if (paramObject != null) {
            partTypeTextField.setText(paramObject.toString());
        } else {
            partTypeTextField.setText("");
        }
    }

    @Override
    public boolean isPropertyModified(TCProperty arg0, boolean arg1) throws Exception {
//		System.out.println("partType================isPropertyModified(TCProperty arg0, boolean arg1)");
        return false;
    }

    @Override
    public boolean isPropertyModified(TCComponent arg0, boolean arg1) throws Exception {
//		System.out.println("partType================isPropertyModified(TCProperty arg0, boolean arg1)");
        return false;
    }
}
