package com.foxconn.mechanism.renderingHint;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.controls.LOVComboBox;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class LikelihoodLovPropertyBean extends AbstractPropertyBean<Object> {

	private TCComponent com = null;
	private TCPropertyDescriptor tcPropertyDescriptor;
	private TCProperty tcProperty = null;
	private TCSession session = null;
	private FormToolkit toolkit = null;
	private Composite composite = null;
	public static Combo combo = null;
	
	public LikelihoodLovPropertyBean() {
		super();
		System.out.println("ProductImpactLovPropertyBean");
	}

	public LikelihoodLovPropertyBean(Control paramControl) {
		super(paramControl);
		
	}

	public LikelihoodLovPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
			Map<?, ?> paramMap) {
		System.out.println("==>> LikelihoodLovPropertyBean 构造方法");
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		this.session = (TCSession) app.getSession();
		this.toolkit = paramFormToolkit;
		loadPanel(paramComposite);
	}	
	
	
	
	private void loadPanel(Composite parentComposite) {
		composite = new Composite(parentComposite, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 10;
		this.composite.setLayout(gridLayout);
		
		combo = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		GridData gridData = new GridData();		
		gridData.widthHint = 165;
		gridData.heightHint = 25;
		combo.setLayoutData(gridData);
		
		addListener(); // 添加监听
		setControl(composite);
	}
	
	
	private void addListener() {
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo source = (Combo) e.getSource();				
				String text = source.getText();
				System.out.println("==>> Likelihood Combo Select value is : " + text);
				
				String productImpactValue = ProductImpactLovPropertyBean.getCombo().getText();
				String custImpactDellValue = CustomerImpactDellLovPropertyBean.getCombo().getText();
				
				if (CommonTools.isNotEmpty(productImpactValue) && CommonTools.isNotEmpty(custImpactDellValue) && CommonTools.isNotEmpty(text)) {
					if (ProductImpactLovPropertyBean.checkSpecChar(productImpactValue) && ProductImpactLovPropertyBean.checkSpecChar(custImpactDellValue) 
							&& ProductImpactLovPropertyBean.checkSpecChar(text)) {
						text = text.replace(")", "");
						productImpactValue = productImpactValue.replace(")", "");
						custImpactDellValue = custImpactDellValue.replace(")", "");
						
						String[] split1 = productImpactValue.split("\\(");
						String[] split2 = custImpactDellValue.split("\\(");
						String[] split3 = text.split("\\(");
						
						String productImpactNew = split1[1].replace(" ", "");
						String custImpactDellNew = split2[1].replace(" ", "");
						String likelihoodNew = split3[1].replace(" ", "");
						
						long j = Integer.parseInt(productImpactNew) * Integer.parseInt(custImpactDellNew) * Integer.parseInt(likelihoodNew);
						String RPNValue = String.valueOf(j);
						
						String sevValue = ProductImpactLovPropertyBean.getSevValue(RPNValue);
						
						Display.getDefault().syncExec(new Runnable() {
							
							@Override
							public void run() {
								
								RPNTextPropertyBean.getText().setText(RPNValue);
								
								SeverityTextPropertyBean.getText().setText(sevValue);								
							}
						});
					}
				}
			}
			
		});
	}
	
	
	@Override
	public boolean isPropertyModified(TCProperty tcproperty) throws Exception {
		return false;
	}

	@Override
	public Object getEditableValue() {
		if (combo != null && !combo.isDisposed()) {
			System.out.println("==>> text: " + combo.getText());
			return combo.getText();
		}
		return null;
	}

	@Override
	public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
		return null;
	}

	@Override
	public void load(TCProperty paramTCProperty) throws Exception {
		System.out.println("LikelihoodLovPropertyBean load TCProperty");
		tcProperty = paramTCProperty;
		tcPropertyDescriptor = paramTCProperty.getDescriptor();
		com = tcProperty.getTCComponent();
		 
		String typeName = com.getTypeObject().getName();
		System.out.println("==>> typeName: " + typeName);
			
		String propName = tcPropertyDescriptor.getName();
		System.out.println("==>> propName: " + propName);
		
		List<String> lovList = getLov(typeName, propName);
		
		String value = tcProperty.getStringValue();
		 
		int index = CommonTools.getIndex(lovList, value);
		
		loadLovList(lovList, index); // 加载下拉值
	}

	@Override
	public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
		System.out.println("==>> LikelihoodLovPropertyBean TCPropertyDescriptor");	
		tcPropertyDescriptor = paramTCPropertyDescriptor;
		TCComponentType typeComponentype = tcPropertyDescriptor.getTypeComponent();
		String typeName = typeComponentype.getClassName();
		System.out.println("==>> typeName: " + typeName);
		
		String propName = tcPropertyDescriptor.getName();
		System.out.println("==>> propName: " + propName);
		
		List<String> lovList = getLov(typeName.replace("CreI", ""), propName);		
		
		loadLovList(lovList, 0); // 加载下拉值
	}

	
	private List<String> getLov(String type, String prop) {
		try {
			return TCUtil.getLovValues(session, (TCComponentItemRevisionType) session.getTypeComponent(type), prop);			
		} catch (TCException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void loadLovList(List<String> lovList, int index) {
		if (combo != null && !combo.isDisposed()) {
			combo.removeAll();
			
			for (String str : lovList) {
				combo.add(str);
			}			
			combo.select(index);			
		}		
	}
	
	
	@Override
	public void setModifiable(boolean arg0) {
	}

	@Override
	public void setUIFValue(Object arg0) {
	}

	@Override
	public boolean isMandatory() {
		String value = combo.getText();
		try {
			if (CommonTools.isNotEmpty(tcProperty)) {
				com.setProperty(tcProperty.getPropertyName(), value);
			}
			
		} catch (TCException e) {
			e.printStackTrace();
			
		}
		return super.isMandatory();
	}	
	
	public static Combo getCombo() {
		return combo;
	}

	public static void setCombo(Combo combo) {
		LikelihoodLovPropertyBean.combo = combo;
	}
	
	
}
