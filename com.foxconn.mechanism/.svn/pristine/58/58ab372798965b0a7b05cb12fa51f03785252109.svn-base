package com.foxconn.mechanism.renderingHint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.controls.LOVComboBox;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;


public class DesignTemplatePropertyBean extends AbstractPropertyBean<Object> {
	
	Composite composite = null;
	FormToolkit toolkit = null;
	Composite parent = null;
	private static LOVComboBox propLov = null;
	Text textField = null;
	private TCSession session = null;
	private String Design3DTemplateName = "Fx8_3D_Template"; // 首选项名称
	private List<String> Design3DTemplateList;	
	private boolean isModify = false;		
	
	public DesignTemplatePropertyBean() {
		super();
		System.out.println("Customer 1");
	}

	public DesignTemplatePropertyBean(Control paramControl) {
		super(paramControl);
		parent = (Composite) paramControl;
		loadPanel();
	}
	
	
	public DesignTemplatePropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
			Map<?, ?> paramMap) {
		System.out.println("==>> DesignTemplatePropertyBean");
		AbstractAIFApplication app = AIFUtility.getCurrentApplication();
		InterfaceAIFComponent aif = app.getTargetComponent();		
//		System.out.println(aif.getProperty("object_name"));
		this.session = (TCSession) app.getSession();
		Design3DTemplateList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, Design3DTemplateName);
		this.savable = true;
		this.toolkit = paramFormToolkit;
		parent = paramComposite;		
		loadPanel(); // UI加载		
	}
	
	/**
	 * 加载UI, 属性面板
	 * @param paramComposite
	 */
	@SuppressWarnings("deprecation")
	private void loadPanel() {
		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1,false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 10;
		this.composite.setLayout(gridLayout);
		
		propLov = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);				
		propLov.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		propLov.setSize(220, 25);
		textField = propLov.getTextField();
		
		// 加载下拉框
		loadDropDownList(propLov, Design3DTemplateList);
		//添加监听
		addListener();
		
		setControl(composite);
	}
	
	/**
	 * 添加监听
	 */
	private void addListener() {
		textField.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				if (propLov != null) {
					String option = textField.getText();
					System.out.println("==>> 下拉框值为: " + option);	
					isModify = true;				
					
				}				
			}
		});
	}
	
	/**
	 * 清空下拉框
	 * @param propLov
	 */
	private void clearPropLov(LOVComboBox propLov) {
		if (propLov != null && propLov.isDisposed()) {
			propLov.removeAllItems();
			propLov.setSelectedItem("");
			propLov.update();
		}
	}
	
	/**
	 * 加载下拉框，数据集不设置默认值
	 * @param propLov
	 * @param list
	 */
	private void loadDropDownList(LOVComboBox propLov, List<String> list) {
		if (propLov != null && !propLov.isDisposed()) {
			propLov.removeAllItems();
			propLov.update();
			for (String str : Design3DTemplateList) {
				System.out.println("==>> 下拉框选项值为: " + str);
				propLov.addItem(str.split("=")[1]);
			}
//		 	propLov.setSelectedIndex(0);
		}
		propLov.update();
		propLov.setEditable(false);
	}
	
	@Override
	public boolean isPropertyModified(TCProperty tcProperty) throws Exception {
		System.out.println("==>> DesignTemplatePropertyBean isPropertyModified");		
		return isModify;
	}
	
	@Override
	public void setModifiable(boolean flag) {
		System.out.println("==>> DesignTemplatePropertyBean setModifiable");
		modifiable = flag;		
	}
	
	@Override
	public Object getEditableValue() {
		System.out.println("==>> DesignTemplatePropertyBean getEditableValue");
		if (null != propLov) {
			return propLov.getSelectedObject();
		}
		return null;
	}

	@Override
	public TCProperty getPropertyToSave(TCProperty tcproperty) throws Exception {
		System.out.println("==>> DesignTemplatePropertyBean getPropertyToSave");
		return tcproperty;
	}

	@Override
	public void load(TCProperty tcProperty) throws Exception {
		System.out.println("==>> DesignTemplatePropertyBean load 1");	
		isModify = false;
	}

	
	@Override
	public void load(TCComponent arg0) throws Exception {
		System.out.println("==>> DesignTemplatePropertyBea n" );
		super.load(arg0);
	}

	@Override
	public void load(TCPropertyDescriptor tcPropertyDescriptor) throws Exception {		
		System.out.println("==>> DesignTemplatePropertyBean TCPropertyDescriptor");		
	}	

	@Override
	public void setUIFValue(Object arg0) {
		System.out.println("==>> DesignTemplatePropertyBean setUIFValue");		
	}
	
	@Override
	protected Viewer getViewer() {
		// TODO Auto-generated method stub
		return super.getViewer();
	}

	@Override
	public void dispose() {
		System.out.println("==>> DesignTemplatePropertyBean dispose");
		super.dispose();
	}
	
	
	
}
