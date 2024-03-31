package com.foxconn.mechanism.dcndtsngenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
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
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2013_05.DataManagement.GenerateNextValuesIn;
import com.teamcenter.services.rac.core._2013_05.DataManagement.GenerateNextValuesResponse;
import com.teamcenter.services.rac.core._2013_05.DataManagement.GeneratedValue;
import com.teamcenter.services.rac.core._2013_05.DataManagement.GeneratedValuesOutput;

public class DCNDTAssignPropertyBean extends AbstractPropertyBean<Object> {

	Composite composite = null;
	FormToolkit toolkit = null;
	private static Text text = null;
	private static Button assignButton = null;
	private TCSession session = null;

	public DCNDTAssignPropertyBean() {
		super();
		System.out.println("Customer 1");
		
	}

	public DCNDTAssignPropertyBean(Control paramControl) {
		super(paramControl);
	}

	public DCNDTAssignPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
			Map<?, ?> paramMap) throws ExecutionException {
		System.out.println("==>> DesignAssignPropertyBean ");
		AbstractAIFApplication app = AIFUtility.getCurrentApplication();
		this.session = (TCSession) app.getSession();
		this.toolkit = paramFormToolkit;
		loadPanel(paramComposite);
	}

	private void loadPanel(Composite parentComposite) {
		composite = new Composite(parentComposite, SWT.NONE);

		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 10;
		this.composite.setLayout(gridLayout);

		text = new Text(composite, SWT.LEFT | SWT.BORDER);
		GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gridData.heightHint = 20;
		gridData.widthHint = 200;
		text.setLayoutData(gridData);

		assignButton = new Button(this.composite, SWT.NONE);
		assignButton.setText("指派");
		assignButton.computeSize(80, 25);
		assignButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		addListeners(parentComposite);

	}

	private void addListeners(final Composite parentComposite) {
		Listener assignListener = new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {	
						String materialKindValue = DCNDTMaterialKindPropertyBean.getMaterialKindValue();
						if(TCUtil.isNull(materialKindValue)) {
							TCUtil.warningMsgBox("請選擇件別");
							return;
						}
						String sku = DCNDTSkuPropertyBean.getSku();
						if(TCUtil.isNull(sku)) {
							TCUtil.warningMsgBox("請輸入幾種");
							return;
						}
						
						Map<String, String> map = TCUtil.generateId(session,"Item");						
						if (TCUtil.isNull(map)) {
							TCUtil.warningMsgBox("生成ID失败");
						} else {
							String id = "DCN-"+sku+"-"+(materialKindValue+map.get("id") == null ? "" : map.get("id").toString().trim());
							text.setText(id);
						}
					}
				});

			}
		};
		assignButton.addListener(SWT.Selection, assignListener);
	}

	@Override
	public boolean isPropertyModified(TCProperty arg0) throws Exception {
		System.out.println("DesignAssignPropertyBean isPropertyModified");	
		return false;
	}

	@Override
	public void setModifiable(boolean Flag) {
		System.out.println("==>> DesignAssignPropertyBean setModifiable");
		modifiable = Flag;	
	}
	
	
	@Override
	public Object getEditableValue() {
		System.out.println("==>> DesignAssignPropertyBean getEditableValue");
		if (null != text) {
			return text.getText();
		}
		return null;
	}

	@Override
	public TCProperty getPropertyToSave(TCProperty tcproperty) throws Exception {
		System.out.println("==>> DesignAssignPropertyBean getPropertyToSave");
		return tcproperty;
	}

	@Override
	public void load(TCProperty tcProperty) throws Exception {
		System.out.println("==>> DesignAssignPropertyBean load 1");		
	}

	@Override
	public void load(TCPropertyDescriptor tcPropertyDescriptor) throws Exception {
		System.out.println("==>> DesignAssignPropertyBean TCPropertyDescriptor");
	}

	

	@Override
	public void setUIFValue(Object arg0) {
		System.out.println("==>> DesignAssignPropertyBean setUIFValue");
	}

	@Override
	public void dispose() {
		System.out.println("==>> DesignAssignPropertyBean dispose");
		super.dispose();
	}
	
	public static Text getText() {
		return text;
	}	

	public static Button getAssignButton() {
		return assignButton;
	}

}
