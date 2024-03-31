package com.foxconn.mechanism.renderingHint;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.controls.LOVComboBox;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;


public class DesignCategoryLovPropertyBean extends AbstractPropertyBean<Object> {

	Composite composite = null;
	FormToolkit toolkit = null;
	private static Text text = null;
	private static LOVComboBox propLov2 = null;
	private static LOVComboBox propLov3 = null;
	private static LOVComboBox propLov4 = null;
	Text textField1 = null;
	Text textField2 = null;
	Text textField3 = null;
	Text textField4 = null;
	Label label_1 = null;
	Label label_2 = null;
	Label label_3 = null;
	private TCSession session = null;
	private static List<String> bigClassList;
	private List<String> categoryClassList;
	private String bigClassPreferenceName = "Fx8_Big_Class"; // 首选项名称
	private String categoryClassPreferenceName = "Fx8_Category_Class"; // 首选项名称
	private static String tcComponentTypeName = ""; //对象类型
	private List<String> downList = new ArrayList<String>();
	private List<String> thirdDownValue = new ArrayList<String>();
	private List<String> fourDownValue = new ArrayList<String>();
	private static String firstFlag = "";
	private static String secondFlag = "";
	private static String thridFlag = "";
	private static String fourFlag = "";
	Toolkit toolkits = Toolkit.getDefaultToolkit();
	private int DEFAULE_WIDTH = 650;
	private int DEFAULE_HEIGH = 500;
	int Location_x = (int) (toolkits.getScreenSize().getWidth() - DEFAULE_WIDTH) / 2;
	int Location_y = (int) (toolkits.getScreenSize().getHeight() - DEFAULE_HEIGH) / 2;
	
	public DesignCategoryLovPropertyBean() {
		super();
		System.out.println("Customer 1");
	}

	public DesignCategoryLovPropertyBean(Control paramControl) {
		super(paramControl);
	}

	public DesignCategoryLovPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
			Map<?, ?> paramMap) throws ExecutionException {
		System.out.println("==>> DesignCategoryLovPropertyBean 构造方法");
		AbstractAIFApplication app = AIFUtility.getCurrentApplication();
		this.session = (TCSession) app.getSession();
		bigClassList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site,
				bigClassPreferenceName);
		categoryClassList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site,
				categoryClassPreferenceName);
		this.savable = true;
		this.toolkit = paramFormToolkit;
		loadPanel(paramComposite);
		paramComposite.setLocation(Location_x, Location_y);
		paramComposite.setSize(DEFAULE_WIDTH, DEFAULE_HEIGH);
		// 加载下拉框
//		loadDropDownList(propLov1, bigClassList);	
		
	}

	private void loadPanel(Composite parentComposite) {
		composite = new Composite(parentComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout(7, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 10;
		this.composite.setLayout(gridLayout);

		text = new Text(composite, SWT.LEFT | SWT.BORDER);
		GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gridData.heightHint = 20;
		gridData.widthHint = 60;
		text.setLayoutData(gridData);
		
//		text.setSize(100, 25);
//		propLov1.setEnabled(false);
//		textField1 = propLov1.getTextField();

		label_1 = new Label(composite, SWT.DROP_DOWN);
		label_1.setText("-");
		label_1.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

		propLov2 = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);
		propLov2.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		propLov2.setSize(135, 25);
		textField2 = propLov2.getTextField();

		label_2 = new Label(composite, SWT.DROP_DOWN);
		label_2.setText("-");
		label_2.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

		propLov3 = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);
		propLov3.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		propLov3.setSize(150, 25);
		textField3 = propLov3.getTextField();

		label_3 = new Label(composite, SWT.DROP_DOWN);
		label_3.setText("-");
		label_3.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

		propLov4 = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);
		propLov4.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		propLov4.setSize(160, 25);
		textField4 = propLov4.getTextField();

		// 添加监听
		addListener();		
		
	}

	/**
	 * 添加监听
	 */
	private void addListener() {
		
		text.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {				
				String option = text.getText();
				System.out.println("==>> 文本框的输入值为：  " + option);
				firstFlag = option;
				downList.clear();
				// 清空下拉框的值
				clearDropDownList(propLov2);
				clearDropDownList(propLov3);
				clearDropDownList(propLov4);
				// 获取下拉框值
				getDownList(option);
				if (downList != null && downList.size() > 0) {
					loadDropDownList(propLov2, downList);
				}
//				if (!DesignAssignPropertyBean.getFlag()) { //判断假如渲染属性item_id的界面还没加载完，无需进行判断
//					return;
//				}
				// 核对下拉框是否有值
				if (checkDropValue()) {
					// 代表四个下拉框都有值
					editableValue(DesignAssignPropertyBean.getAssignButton(), true);
				} else {					
					// 代表四个下拉框不全有值
					editableValue(DesignAssignPropertyBean.getAssignButton(), false);											
				}
			}
		});

//		textField1.addModifyListener(new ModifyListener() {
//
//			@Override
//			public void modifyText(ModifyEvent arg0) {
//				if (propLov1 != null) {
//					String option = textField1.getText();
//					System.out.println("==>> 一级下拉框值： " + option);
//					firstFlag = option;
//					downList.clear();
//					/*
//					 * if (propLov3 != null && !propLov3.isDisposed()) { propLov3.removeAllItems();
//					 * propLov3.setSelectedItem(""); propLov3.update(); }
//					 */
//					// 清空下拉框的值
//					clearDropDownList(propLov2);
//					clearDropDownList(propLov3);
//					clearDropDownList(propLov4);
//					// 获取下拉框值
//					getDownList(option);
//					if (downList != null && downList.size() > 0) {
//						loadDropDownList(propLov2, downList);
//					}
//					if (!DesignAssignPropertyBean.getFlag()) { //判断假如渲染属性item_id的界面还没加载完，无需进行判断
//						return;
//					}
//					// 核对下拉框是否有值
//					if (checkDropValue()) {
//						// 代表四个下拉框都有值
//						editableValue(DesignAssignPropertyBean.getAssignButton(), true);
//					} else {					
//						// 代表四个下拉框不全有值
//						editableValue(DesignAssignPropertyBean.getAssignButton(), false);											
//					}
//				}
//			}
//		});

		textField2.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				if (propLov2 != null) {
					String option = textField2.getText();
					System.out.println("==>> 二级下拉框值：" + option);
					secondFlag = option;
					downList.clear();
					clearDropDownList(propLov3);
					clearDropDownList(propLov4);
					// 获取下拉框值
					getDownList(option, firstFlag, secondFlag);
					if (downList != null && downList.size() > 0) {
						loadDropDownList(propLov3, downList);
					}
//					if (!DesignAssignPropertyBean.getFlag()) { //判断假如渲染属性item_id的界面还没加载完，无需进行判断
//						return;
//					}
					// 核对下拉框是否有值
					if (checkDropValue()) {
						// 代表四个下拉框都有值
						editableValue(DesignAssignPropertyBean.getAssignButton(), true);
					} else {						
						// 代表四个下拉框不全有值
						editableValue(DesignAssignPropertyBean.getAssignButton(), false);
					}
				}
			}
		});

		textField3.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				if (propLov3 != null) {
					String option = textField3.getText();
					System.out.println("==>> 三级下拉框值： " + option);
					thridFlag = option;
					downList.clear();
					clearDropDownList(propLov4);
					// 获取下拉框值
					getDownList(option, firstFlag, secondFlag, thridFlag);
					if (downList != null && downList.size() > 0) {
						loadDropDownList(propLov4, downList);
					}
//					if (!DesignAssignPropertyBean.getFlag()) { //判断假如渲染属性item_id的界面还没加载完，无需进行判断
//						return;
//					}
					// 核对下拉框是否有值
					if (checkDropValue()) {
						// 代表四个下拉框都有值
						editableValue(DesignAssignPropertyBean.getAssignButton(), true);
					} else {						
						// 代表四个下拉框不全有值
						editableValue(DesignAssignPropertyBean.getAssignButton(), false);
					}
				}
			}
		});

		textField4.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				if (propLov4 != null) {
					String option = textField4.getText();
					System.out.println("==>> 四级下拉框值： " + option);
					fourFlag = option;
//					if (!DesignAssignPropertyBean.getFlag()) { //判断假如渲染属性item_id的界面还没加载完，无需进行判断
//						return;
//					}
					// 核对下拉框是否有值
					if (checkDropValue()) {
						// 代表四个下拉框都有值
						editableValue(DesignAssignPropertyBean.getAssignButton(), true);
					} else {						
						// 代表四个下拉框不全有值
						editableValue(DesignAssignPropertyBean.getAssignButton(), false);
					}
				}				
			}
			
		});
	}

	/**
	 * 核对四个下拉框是否全部有值
	 * 
	 * @return
	 */
	private Boolean checkDropValue() {
		if (TCUtil.isNull(firstFlag) || TCUtil.isNull(secondFlag) || 
			TCUtil.isNull(thridFlag) || TCUtil.isNull(fourFlag)) { // 判断四个下拉框是否存在一个为空
			return false;
		}
		return true;
	}

	/**
	 * 获取下拉框值
	 * 
	 * @param option
	 * @param splitFlag
	 */
	private void getDownList(String option) {
		for (String str : categoryClassList) {
			String key = str.split("=")[0];
			String value = str.split("=")[1];
			if (option.equals(key)) {
				String[] split = value.split("&&");
				if (!downList.contains(split[0].trim())) {
					downList.add(split[0].trim());
				}
			}
		}
	}

	/**
	 * 获取下拉框
	 * 
	 * @param option
	 * @param firstFlag
	 * @param secondFlag
	 */
	private void getDownList(String option, String firstFlag, String secondFlag) {
		for (String str : categoryClassList) {
			String key = str.split("=")[0];
			String value = str.split("=")[1];
			if (firstFlag.equals(key)) {
				String[] split = value.split("&&");
				if (secondFlag.equals(split[0])) {
					value = split[1];
					String[] split2 = value.split("##");
					if (!downList.contains(split2[0].trim())) {
						downList.add(split2[0].trim());
					}
				}
			}
		}
	}

	/**
	 * 获取下拉框值
	 * 
	 * @param option
	 * @param firstFlag
	 * @param secondFlag
	 * @param thridFlag
	 */
	@SuppressWarnings("unused")
	private void getDownList(String option, String firstFlag, String secondFlag, String thridFlag) {
		for (String str : categoryClassList) {
			String key = str.split("=")[0];
			String value = str.split("=")[1];
			if (firstFlag.equals(key)) {
				String[] split = value.split("&&");
				if (secondFlag.equals(split[0])) {
					value = split[1];
					String[] split2 = value.split("##");
					if (thridFlag.equals(split2[0].trim())) {
						downList.add(split2[1].trim());
					}
				}
			}
		}
	}

	/**
	 * 加载下拉框
	 */
	public static void loadDropDownList(LOVComboBox propLov, List<String> list) {
		if (propLov != null && !propLov.isDisposed()) {
			propLov.removeAllItems();
			// 设置默认值
//			propLov.addItem("");
			for (int i = 0; i < list.size(); i++) {
				String value = list.get(i);
//				String str1 = value.split("=")[0];				
//				propLov.addItem(str1);
				propLov.addItem(value);
			}
			propLov.setSelectedIndex(0); //设置第一个被默认选中
		}
		propLov.update();
		propLov.setEditable(false);
	}

	/**
	 * 设置文本框数值
	 * @param text
	 * @param list
	 * @param typeName
	 */
	public static void setTextValue(Text text, List<String> list, String typeName) {
		if (text != null && !text.isDisposed()) {
			for (String str : list) {
				String[] split = str.split("=");				
				if (split[1].equals(typeName)) {
					text.setText(split[0]);
					text.setEditable(false);
				}
			}
		}
	}
	
	
	/**
	 * 清空下拉框的值
	 * 
	 * @param propLov
	 */
	private void clearDropDownList(LOVComboBox propLov) {
		if (propLov != null && !propLov.isDisposed()) {
			System.out.println(propLov.getSelectedString());
			propLov.removeAllItems();
			propLov.setSelectedItem("");
			propLov.update();
		}
	}

	/**
	 * 设置指派按钮是否可以点击
	 * @param text
	 * @param assignButton
	 * @param flag
	 */
	private void editableValue(Button assignButton, Boolean flag) {
		if (flag) {
			assignButton.setEnabled(true);
		} else {
			assignButton.setEnabled(false);
		}
	}
	
	
	@Override
	public boolean isPropertyModified(TCProperty arg0) throws Exception {
		System.out.println("==>> DesignCategoryLovPropertyBean isPropertyModified");
		return false;
	}

	@Override
	public void setModifiable(boolean Flag) {
		System.out.println("==>> DesignCategoryLovPropertyBean setModifiable");
		modifiable = Flag;
	}	
	
	@SuppressWarnings({ "deprecation", "null" })
	@Override
	public Object getEditableValue() {
		System.out.println("==>> DesignCategoryLovPropertyBean getEditableValue");
		Object obj = null;
//		if (null != propLov1) {
//			obj += propLov1.getSelectedObject() + "-";
//		}
		if (null != text) {
			obj += text.getText() + "-";
		}
		if (null != propLov2) {
			obj += propLov2.getSelectedObject() + "-";
		}
		if (null != propLov3) {
			obj += propLov3.getSelectedObject() + "-";
		}
		if (null != propLov4) {
			obj += propLov4.getSelectedObject() + "-";
		}
		if (null != obj) {
			String str = obj.toString().replace("null", "").trim();
			return str.substring(0, str.length() - 2);
		}
		return null;
	}

	@Override
	public TCProperty getPropertyToSave(TCProperty tcProperty) throws Exception {
		System.out.println("==>> DesignCategoryLovPropertyBean getPropertyToSave");
		return tcProperty;
	}

	@Override
	public void load(TCProperty tcProperty) throws Exception {
		System.out.println("==>> DesignCategoryLovPropertyBean load 1");	

	}

	@Override
	public void load(TCPropertyDescriptor tcPropertyDescriptor) throws Exception {
		System.out.println("==>> DesignCategoryLovPropertyBean TCPropertyDescriptor");	
		TCComponentType typeComponentype = tcPropertyDescriptor.getTypeComponent();
		String typeComponentTypeName = typeComponentype.getClassName();
		System.out.println("==>> typeComponentTypeName: " + typeComponentTypeName);
		tcComponentTypeName = typeComponentTypeName.replace("CreI", "");
		System.out.println("==>> tcComponentTypeName: " + tcComponentTypeName);
	}
	

	@Override
	public void setUIFValue(Object arg0) {
		System.out.println("==>> DesignCategoryLovPropertyBean setUIFValue");
	}
	
	@Override
	public void dispose() {
		System.out.println("==>> DesignCategoryLovPropertyBean dispose");
		super.dispose();
	}
	
	public static String getFirstFlag() {
		return firstFlag;
	}	
	
	
	public static String getSecondFlag() {
		return secondFlag;
	}
	

	public static String getThridFlag() {
		return thridFlag;
	}
	

	public static String getFourFlag() {
		return fourFlag;
	}

//	public static LOVComboBox getPropLov1() {
//		return propLov1;
//	}
	
	
	public static Text getText() {
		return text;
	}

	public static List<String> getBigClassList() {
		return bigClassList;
	}

	public static String getTcComponentTypeName() {
		return tcComponentTypeName;
	}	

}
