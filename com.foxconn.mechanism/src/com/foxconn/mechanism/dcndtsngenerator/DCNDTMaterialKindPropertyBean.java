package com.foxconn.mechanism.dcndtsngenerator;

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
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;


public class DCNDTMaterialKindPropertyBean extends AbstractPropertyBean<Object> {

	Composite composite = null;
	FormToolkit toolkit = null;
	private static LOVComboBox propLov1 = null;
	Text textField1 = null;
	private TCSession session = null;

	public DCNDTMaterialKindPropertyBean() {
		super();
		System.out.println("Customer 1");
	}

	public DCNDTMaterialKindPropertyBean(Control paramControl) {
		super(paramControl);
	}

	public DCNDTMaterialKindPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
			Map<?, ?> paramMap) throws ExecutionException {
		System.out.println("加载==>> DesignCategoryLovPropertyBean");
		AbstractAIFApplication app = AIFUtility.getCurrentApplication();
		this.session = (TCSession) app.getSession();
		this.savable = true;
		this.toolkit = paramFormToolkit;
		loadPanel(paramComposite);
		// 加载下拉框
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("鐵件");
		arrayList.add("塑件");
		arrayList.add("外購件");
		loadDropDownList(propLov1, arrayList);	
		
	}

	private void loadPanel(Composite parentComposite) {
		composite = new Composite(parentComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout(7, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 10;
		this.composite.setLayout(gridLayout);

		propLov1 = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);
	
//		propLov1.setSize(100, 25);
//		propLov1.setEnabled(false);
		textField1 = propLov1.getTextField();


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
				String str1 = value.split("=")[0];
				propLov.addItem(str1);
			}
			propLov.setSelectedIndex(0); //设置第一个被默认选中
		}
		propLov.update();
		propLov.setEditable(false);
	}
	
	public static String getMaterialKindValue() {
		String selectedString = (String) propLov1.getSelectedItem();
		if("鐵件".equals(selectedString)) {
			return "S";
		}
		if("塑件".equals(selectedString)) {
			return "P";
		}
		if("外購件".equals(selectedString)) {
			return "M";
		}
		return null;
	}
	@Override
	public boolean isPropertyModified(TCProperty arg0) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load(TCProperty arg0) throws Exception {
		System.out.println("==>> TCProperty: " + arg0);

	}

	@Override
	public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
		System.out.println("加载==>> DesignCategoryLovPropertyBean paramTCPropertyDescriptor");		

	}

	@Override
	public void setModifiable(boolean arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUIFValue(Object arg0) {
		// TODO Auto-generated method stub

	}

	

}
