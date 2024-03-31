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


public class DCNDTSkuPropertyBean extends AbstractPropertyBean<Object> {

	Composite composite = null;
	FormToolkit toolkit = null;

	public DCNDTSkuPropertyBean() {
		super();
		System.out.println("Customer 1");
	}

	public DCNDTSkuPropertyBean(Control paramControl) {
		super(paramControl);
	}

	public DCNDTSkuPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
			Map<?, ?> paramMap) throws ExecutionException {
		AbstractAIFApplication app = AIFUtility.getCurrentApplication();
		this.savable = true;
		this.toolkit = paramFormToolkit;
		loadPanel(paramComposite);
	}
	
	static Text text;

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
		gridData.widthHint = 200;
		text.setLayoutData(gridData);

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
	
	public static String getSku() {
		return text.getText();
	}

	

}
