package com.foxconn.mechanism.renderingHint;

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

public class DesignAssignPropertyBean extends AbstractPropertyBean<Object> {

	Composite composite = null;
	FormToolkit toolkit = null;
	private static Text text = null;
	private static Button assignButton = null;
	private TCSession session = null;
	private Boolean isModified = false;
	private static Boolean flag = false; //检测渲染界面是否加载完成
	private List<String> ruleMappingList;
	private String ruleMappingPreferenceName = "Fx8_Rule_Mapping"; // 首选项名称
//	private String itemType = "Design";
	
	public DesignAssignPropertyBean() {
		super();
		System.out.println("Customer 1");
		
	}

	public DesignAssignPropertyBean(Control paramControl) {
		super(paramControl);
	}

	public DesignAssignPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
			Map<?, ?> paramMap) throws ExecutionException {
		System.out.println("==>> DesignAssignPropertyBean ");
		AbstractAIFApplication app = AIFUtility.getCurrentApplication();
		this.session = (TCSession) app.getSession();
		ruleMappingList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site,
				ruleMappingPreferenceName);
		this.savable = true;
		this.toolkit = paramFormToolkit;
		loadPanel(paramComposite);
		//加载下拉框
		List<String> bigClassList = DesignCategoryLovPropertyBean.getBigClassList();
		Text text2 = DesignCategoryLovPropertyBean.getText();
		DesignCategoryLovPropertyBean.setTextValue(text2, bigClassList, DesignCategoryLovPropertyBean.getTcComponentTypeName());
//		LOVComboBox propLov1 = DesignCategoryLovPropertyBean.getPropLov1();
		// 加载下拉框
//		DesignCategoryLovPropertyBean.loadDropDownList(propLov1, bigClassList);	
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

		//判断是否可以编辑
		editableValue();

		//添加监听
		addListeners(parentComposite);
		
		setControl(composite);
		
		flag = true;
	}

	/**
	 * 判断是否可以编辑
	 */
	private void editableValue() {
		if (!isModified) {
			text.setEditable(false);
			assignButton.setEnabled(false);
		} else {
			text.setEditable(true);
		}
	}

	private void addListeners(final Composite parentComposite) {
		Listener assignListener = new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				Display.getDefault().syncExec(new Runnable() {

					@Override
					public void run() {
						if (TCUtil.isNull(DesignCategoryLovPropertyBean.getFirstFlag())
								|| TCUtil.isNull(DesignCategoryLovPropertyBean.getSecondFlag())
								|| TCUtil.isNull(DesignCategoryLovPropertyBean.getThridFlag())
								|| TCUtil.isNull(DesignCategoryLovPropertyBean.getFourFlag())) { // 判断四个下拉框是否存在一个为空
							return;
						}
						String str = DesignCategoryLovPropertyBean.getFirstFlag() + "*"
								+ DesignCategoryLovPropertyBean.getSecondFlag() + "*"
								+ DesignCategoryLovPropertyBean.getThridFlag() + "*"
								+ DesignCategoryLovPropertyBean.getFourFlag();
						//获取编码规则
						String ruleMapping = getRuleMapping(str); 
						System.out.println("==>> 编码规则为:：" + ruleMapping);
						if (TCUtil.isNull(ruleMapping)) {
							TCUtil.warningMsgBox("获取首选项的编码匹配规则失败");
							return;
						}
						//设置编码匹配规则
						if (!TCUtil.setRule(session, ruleMapping, DesignCategoryLovPropertyBean.getTcComponentTypeName())) {
							TCUtil.warningMsgBox("设置编码配置模式失败");
							return;
						}						
						Map<String, String> map = TCUtil.generateId(session, DesignCategoryLovPropertyBean.getTcComponentTypeName());						
						if (TCUtil.isNull(map)) {
							TCUtil.warningMsgBox("生成ID失败");
						} else {
							isModified = true;
							// 设置可以编辑
							editableValue();
							// 设置文本框值
							text.setText(map.get("id") == null ? "" : map.get("id").toString().trim());
							isModified = false;
							//设置文本不可编辑，按钮不能点击
							editableValue();
						}
					}
				});

			}
		};
		assignButton.addListener(SWT.Selection, assignListener);
	}

	/**
	 * 返回编码规则
	 * 
	 * @return
	 */
	private String getRuleMapping(String str) {
		str = str.replace(" ", "");
		String result = "";
		for (String value : ruleMappingList) {
			String[] split = value.split("=");
			split[0] = split[0].replace(" ", "");
			if (str.equals(split[0])) {
				result = split[1];
				break;
			}
		}
		return result;
	}

	
	@Override
	public boolean isPropertyModified(TCProperty arg0) throws Exception {
		System.out.println("==>> DesignAssignPropertyBean isPropertyModified");	
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

	public static Boolean getFlag() {
		return flag;
	}
	
	

}
