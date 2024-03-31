package com.foxconn.mechanism.renderingHint;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.foxconn.tcutils.util.CommonTools;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class RPNTextPropertyBean extends AbstractPropertyBean<Object> {
	
	private TCComponent com = null;
	private TCPropertyDescriptor tcPropertyDescriptor = null;
	private TCProperty tcProperty = null;
	private TCSession session = null;
	private FormToolkit toolkit = null;
	private Composite composite = null;
	public static Text text = null;
	
	
	public RPNTextPropertyBean() {
		super();
		System.out.println("ProductImpactLovPropertyBean");
	}

	public RPNTextPropertyBean(Control paramControl) {
		super(paramControl);
		
	}

	public RPNTextPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
			Map<?, ?> paramMap) {
		System.out.println("==>> ProductImpactLovPropertyBean 构造方法");
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
		
		text = new Text(composite, SWT.LEFT | SWT.BORDER);
		GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		gridData.heightHint = 20;
		gridData.widthHint = 180;
		text.setLayoutData(gridData);
		text.setEditable(false);
		
		loadPropValue();
		
		setControl(composite);
	}
	
	
	
	@Override
	public boolean isPropertyModified(TCProperty tcproperty) throws Exception {
		return false;
	}

	@Override
	public Object getEditableValue() {
		System.out.println("==>> SeverityTextPropertyBean getEditableValue");
		if (text != null && !text.isDisposed()) {
			System.out.println("==>> text: " + text.getText());
			return text.getText();
		}
		return null;
	}

	@Override
	public TCProperty getPropertyToSave(TCProperty arg0) throws Exception {
		return null;
	}

	@Override
	public void load(TCProperty paramTCProperty) throws Exception {
		System.out.println("ProductImpactLovPropertyBean load TCProperty");
		tcProperty = paramTCProperty;
		tcPropertyDescriptor = paramTCProperty.getDescriptor();
		com = tcProperty.getTCComponent();
		 
		String value = tcProperty.getStringValue();
		
		text.setText(value);
	}

	@Override
	public void load(TCPropertyDescriptor paramTCPropertyDescriptor) throws Exception {
		System.out.println("==>> RPNTextPropertyBean TCPropertyDescriptor");	
		tcPropertyDescriptor = paramTCPropertyDescriptor;
		
		loadPropValue(); // 加载属性值
	}

	@Override
	public void setModifiable(boolean arg0) {
		
	}

	@Override
	public void setUIFValue(Object arg0) {
		
	}

	
	/**
	 * 加载属性值
	 */
	private void loadPropValue() {
		String productImpactValue = ProductImpactLovPropertyBean.getCombo().getText();
		String custImpactDellValue = CustomerImpactDellLovPropertyBean.getCombo().getText();
		String likelihoodValue = LikelihoodLovPropertyBean.getCombo().getText();
		if (CommonTools.isNotEmpty(productImpactValue) && CommonTools.isNotEmpty(custImpactDellValue) && CommonTools.isNotEmpty(likelihoodValue)) {
			if (ProductImpactLovPropertyBean.checkSpecChar(productImpactValue) && ProductImpactLovPropertyBean.checkSpecChar(custImpactDellValue) 
					&& ProductImpactLovPropertyBean.checkSpecChar(likelihoodValue)) {
				productImpactValue = productImpactValue.replace(")", "");
				custImpactDellValue = custImpactDellValue.replace(")", "");
				likelihoodValue = likelihoodValue.replace(")", "");
				
				String[] split1 = productImpactValue.split("\\(");
				String[] split2 = custImpactDellValue.split("\\(");
				String[] split3 = likelihoodValue.split("\\(");
				
				String productImpactNew = split1[1].replace(" ", "");
				String custImpactDellNew = split2[1].replace(" ", "");
				String likelihoodNew = split3[1].replace(" ", "");
				
				long j = Integer.parseInt(productImpactNew) * Integer.parseInt(custImpactDellNew) * Integer.parseInt(likelihoodNew);
				String RPNValue = String.valueOf(j);
				
				text.setText(RPNValue);
			}
		}
	}
	
	
	@Override
	public boolean isMandatory() {
		String value = text.getText();
		try {
			if (CommonTools.isNotEmpty(tcProperty)) {
				com.setProperty(tcProperty.getPropertyName(), value);
			}
			
		} catch (TCException e) {
			e.printStackTrace();
			
		}
		return super.isMandatory();
	}
	
	
	public static Text getText() {
		return text;
	}

	public static void setText(Text text) {
		RPNTextPropertyBean.text = text;
	}
	
	
}
