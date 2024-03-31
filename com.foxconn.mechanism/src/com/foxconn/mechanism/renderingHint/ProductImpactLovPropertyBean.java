package com.foxconn.mechanism.renderingHint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.binding.AIFPropertyDataBean;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.controls.LOVComboBox;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;
import com.teamcenter.rac.viewer.stylesheet.viewer.IFormProvider;

public class ProductImpactLovPropertyBean extends AbstractPropertyBean<Object> {
	
	private TCComponent com = null;
	private TCPropertyDescriptor tcPropertyDescriptor = null;
	private TCProperty tcProperty = null;
	private TCSession session = null;
	private FormToolkit toolkit = null;
	private Composite composite = null;
	public static Combo combo = null;
	public static List<String> issueRPNSevList = null;
	private static final String D9_DELL_ISSUE_RPN_SEV = "D9_DELL_ISSUE_RPN_Sev";
	private static Button saveButton = null;
	
	public ProductImpactLovPropertyBean() {
		super();
		System.out.println("ProductImpactLovPropertyBean");
	}

	public ProductImpactLovPropertyBean(Control paramControl) {
		super(paramControl);
		
	}

	public ProductImpactLovPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
			Map<?, ?> paramMap) {
		System.out.println("==>> ProductImpactLovPropertyBean 构造方法");
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		this.savable = true;
		this.session = (TCSession) app.getSession();
		this.toolkit = paramFormToolkit;
		issueRPNSevList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, D9_DELL_ISSUE_RPN_SEV);
		if (CommonTools.isEmpty(issueRPNSevList)) {
			TCUtil.warningMsgBox("首选项 " + D9_DELL_ISSUE_RPN_SEV + "不存在", "警告");
			return;
		}
		
		loadPanel(paramComposite);		
	}	
	
	
	private void getSaveBtn(Control[] children) {
		if (CommonTools.isNotEmpty(children)) {
			for (Control control : children) {
				if (control instanceof Button) {
					if ("完成(&F)".equals(((Button) control).getText())) {
						saveButton = (Button) control;
						break;
					}
				} else if (control instanceof Composite) {
					Composite composite = (Composite) control;
					getSaveBtn(composite.getChildren());
				}
			}
		}
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
				System.out.println("==>> ProductImpact Combo Select value is : " + text);				
				String custImpactDellValue = CustomerImpactDellLovPropertyBean.getCombo().getText();
				String likelihoodValue = LikelihoodLovPropertyBean.getCombo().getText();
				if (CommonTools.isNotEmpty(text) && CommonTools.isNotEmpty(custImpactDellValue) && CommonTools.isNotEmpty(likelihoodValue)) {
					if (checkSpecChar(text) && checkSpecChar(custImpactDellValue) && checkSpecChar(likelihoodValue)) {
						text = text.replace(")", "");
						custImpactDellValue = custImpactDellValue.replace(")", "");
						likelihoodValue = likelihoodValue.replace(")", "");
						
						String[] split1 = text.split("\\(");
						String[] split2 = custImpactDellValue.split("\\(");
						String[] split3 = likelihoodValue.split("\\(");
						
						
						String productImpactNew = split1[1].replace(" ", "");
						String custImpactDellNew = split2[1].replace(" ", "");
						String likelihoodNew = split3[1].replace(" ", "");
						
						long j = Integer.parseInt(productImpactNew) * Integer.parseInt(custImpactDellNew) * Integer.parseInt(likelihoodNew);
						String RPNValue = String.valueOf(j);
						
						String sevValue = getSevValue(RPNValue);
						
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
	
	
	public static boolean checkSpecChar(String str) {
		if (str.indexOf("(") != -1 && str.indexOf(")") != -1) {
			return true;
		}
		
		return false;
	}
	
	
	public static String getSevValue(String RPNValue) {
		for (String str : issueRPNSevList) {
			String[] split = str.split("=");
			String RPNRange = split[0];
			String sevValue = split[1];
			
			if (checkRPNRange(RPNRange, RPNValue)) {
				return sevValue;
			}
		}
		
		return "";
	}
	
	
	public static boolean checkRPNRange(String RPNRange, String RPNValue) {
		String[] split = RPNRange.split(",");
		String str1 = split[0];
		String str2 = split[1];
		
		if (str1.equals("[" + RPNValue) || str2.equals(RPNValue + "]")) {
			return true;
		}
		
		RPNRange = RPNRange.replace("[", "");
		RPNRange = RPNRange.replace("]", "");
		RPNRange = RPNRange.replace("(", "");
		RPNRange = RPNRange.replace(")", "");
		
		split = RPNRange.split(",");
		str1 = split[0];
		str2 = split[1];
		
		int value = Integer.parseInt(RPNValue);
		
		int min = 0;
		int max = 0;
		
		if (CommonTools.isNumeric(str1)) {
			min = Integer.parseInt(str1);
		}
		
		if (CommonTools.isNumeric(str2)) {
			max = Integer.parseInt(str2);
		}
		
		if (max > 0) {
			if (value > min && value < max) {
				return true;
			}
		} else {
			if (value > min) {
				return true;
			}
		}
		
		return false;
	}
	
	
	
	
	@Override
	public boolean isPropertyModified(TCProperty arg0) throws Exception {
		System.out.println("==>> isPropertyModified");
		return false;
	}

	@Override
	public Object getEditableValue() {
		System.out.println("==>> ProductImpactLovPropertyBean getEditableValue");
		if (combo != null && !combo.isDisposed()) {
			System.out.println("==>> text: " + combo.getText());
			return combo.getText();
		}
		return null;
	}

	@Override
	public TCProperty getPropertyToSave(TCProperty paramTCProperty) throws Exception {
		System.out.println("==>> getPropertyToSave");
		return paramTCProperty;
	}

	@Override
	public void load(TCProperty paramTCProperty) throws Exception {
		 System.out.println("ProductImpactLovPropertyBean load TCProperty");
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
		System.out.println("==>> ProductImpactLovPropertyBean TCPropertyDescriptor");	
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
		System.out.println("==>> setModifiable");
	}

	@Override
	public void setUIFValue(Object arg0) {
		System.out.println("==>> setUIFValue");
	}

	
	public static Combo getCombo() {
		return combo;
	}

	public static void setCombo(Combo combo) {
		ProductImpactLovPropertyBean.combo = combo;
	}

	public static List<String> getIssueRPNSevList() {
		return issueRPNSevList;
	}

	public static void setIssueRPNSevList(List<String> issueRPNSevList) {
		ProductImpactLovPropertyBean.issueRPNSevList = issueRPNSevList;
	}

	@Override
	public void save(TCComponent paramTCComponent) throws Exception {
		System.out.println("save TCComponent!");
		TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
		if (savable) {
			paramTCComponent.setTCProperty(localTCProperty);
		}
	}

	@Override
	public void save(TCProperty arg0) throws Exception {
		System.out.println("save ");
		super.save(arg0);
	}

	@Override
	public TCProperty saveProperty(TCComponent paramTCComponent) throws Exception {
		System.out.println("saveProperty!");
        TCProperty localTCProperty = getPropertyToSave(paramTCComponent);
        if (savable) {
			return localTCProperty;
		}
        
        return null;
	}
	

	@Override
	public String getProperty() {
		System.out.println("getProperty");
		return super.getProperty();
	}
	

	@Override
	public void setProperty(String arg0) {
		System.out.println("setProperty");
		super.setProperty(arg0);
	}

	@Override
	public boolean isMandatory() {
		System.out.println("==>> isMandatory");
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
	
	@Override
	public boolean isDirty() {
		return super.isDirty();
	}

	@Override
	public void setDirty(boolean flag) {
		super.setDirty(flag);
	}
	
}
