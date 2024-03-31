package com.foxconn.electronics.dcnreport.dcncostimpact;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.foxconn.electronics.dcnreport.dcncostimpact.constant.DCNCostImpactConstant;
import com.foxconn.electronics.dcnreport.dcncostimpact.domain.DCNReportBean;
import com.foxconn.electronics.dcnreport.dcncostimpact.util.TCPropertes;
import com.foxconn.electronics.progress.BooleanFlag;
import com.foxconn.electronics.progress.IProgressDialogRunnable;
import com.foxconn.electronics.progress.LoopProgerssDialog;
import com.foxconn.electronics.util.CommonTools;
import com.foxconn.electronics.util.MessageShow;
import com.foxconn.electronics.util.StatusEnum;
import com.foxconn.electronics.util.TCUtil;
import com.foxconn.tcutils.util.AjaxResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCFormProperty;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.Registry;
import cn.hutool.http.HttpUtil;

public class DCNCostImpactDialog extends Dialog {

	private Shell shell = null;
	private Shell parentShell = null;
	private AbstractAIFUIApplication app = null;
	private TCComponentItemRevision DCNItemRev = null;
	private TCSession session = null;
	private Label custRequestLabel = null;
	private Label MEImproveLabel = null;
	private Label otherImproveLabel = null;
	private Label processImproveLabel = null;
	private Label DFXImproveLabel = null;
	private Text custRequestText = null;
	private Text MEImproveText = null;
	private Text otherImproveText = null;
	private Text processImproveText = null;
	private Text DFXImproveText = null;
	private String custRequestTextValue = "";
	private String MEImproveTextValue = "";
	private String otherImproveTextValue = "";
	private String processImproveTextValue = "";
	private String DFXImproveTextValue = "";
	private Button saveBtn = null;
	private Button cancelBtn = null;
	private Map<String, String> paramsMap = null;
	private Map<String, String> paramsMappProp = new LinkedHashMap<String, String>();
	private Registry reg = null;
	private TCComponentForm form = null;
	private String custRequestLabelName = "custRequestLabel";
	private String MEImproveLabelName = "MEImproveLabel";
	private String otherImproveLabelName = "otherImproveLabel";
	private String processImproveLabelName = "processImproveLabel";
	private String DFXImproveLabelName = "DFXImproveLabel";
	private String custRequestProp = "d9_CustomerRequest";
	private String MEImproveProp = "d9_DesignImprovement_ME";
	private String otherImproveProp = "d9_DesignImprovement_Others";
	private String processImproveProp = "d9_ProcessImprovement";
	private String DFXImproveProp = "d9_DFXImprovement";
	private final String D9_PREFIX = "d9_";
	private final String POC_STR = "IR_";
	private String springUrl = null;
	private Boolean saveFlag = true; //
	private String errorMsg = null;
	private static final String sdf = "yyyy-MM-dd HH:mm";
	
	public DCNCostImpactDialog(Shell parentShell, AbstractAIFUIApplication app) throws TCException {
		super(parentShell);
		this.parentShell = parentShell;
		this.app = app;
		this.session = (TCSession) app.getSession();
		reg = Registry.getRegistry("com.foxconn.electronics.dcnreport.dcncostimpact.dcncostimpact");
		InterfaceAIFComponent targetComponent = app.getTargetComponent(); // ��ȡѡ�е�Ŀ�����
		DCNItemRev = (TCComponentItemRevision) targetComponent;	
		if (CommonTools.isNotEmpty(paramsMappProp)) {
			paramsMappProp.clear();
		}
		if (!checkDCNRelease()) {
			MessageShow.warningMsgBox(reg.getString("DCNRelease.MSG"), reg.getString("WARNING.MSG"));
			return;
		}
		springUrl = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, DCNCostImpactConstant.D9_SPRINGCLOUD_URL);
		if (CommonTools.isEmpty(springUrl)) {
			MessageShow.errorMsgBox(reg.getString("SpringCloudPreference.MSG").split("\\?")[0] + DCNCostImpactConstant.D9_SPRINGCLOUD_URL + reg.getString("SpringCloudPreference.MSG").split("\\?")[1], 
					reg.getString("ERROR.MSG"));
			return;
		}
		paramsMap = getDCNCostParams();
		if (CommonTools.isEmpty(paramsMap)) {
			MessageShow.errorMsgBox(reg.getString("DCNCostParamsError.MSG"), reg.getString("ERROR.MSG"));
			return;
		}
		initUI();
	}

	private void initUI() {
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL | SWT.MIN);
		shell.setSize(400, 280);
		shell.setText(reg.getString("shell.TITLE"));
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		TCUtil.centerShell(shell);
		Image image = getDefaultImage();
		if (CommonTools.isNotEmpty(image)) {
			shell.setImage(image);
		}

		GridData topGridData = new GridData(GridData.FILL_HORIZONTAL);
//		topGridData.grabExcessHorizontalSpace = true;

		Composite topComposite = new Composite(shell, SWT.NONE);
		GridLayout topLayout = new GridLayout(2, false);
		topLayout.horizontalSpacing = 50;
		topLayout.verticalSpacing = 15;

		topComposite.setLayout(topLayout);
		topComposite.setLayoutData(topGridData);

		GridData labelData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		labelData.widthHint = 50;
		labelData.heightHint = 15;

		custRequestLabel = new Label(topComposite, SWT.NONE);
		custRequestLabel.setText(paramsMap.get(custRequestLabelName) == null ? "" : paramsMap.get(custRequestLabelName) + reg.getString("DCNReport.UNIT"));
		custRequestLabel.setLayoutData(labelData);
		
		custRequestText = new Text(topComposite, SWT.SINGLE | SWT.BORDER);
		custRequestText.setText(paramsMap.get(custRequestProp) == null ? "" : paramsMap.get(custRequestProp));
		custRequestText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		custRequestText.setToolTipText(reg.getString("ToolTipText.MSG"));
		custRequestText.setSelection(custRequestText.getText().length());

		labelData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		labelData.widthHint = 100;
		labelData.heightHint = 15;

		MEImproveLabel = new Label(topComposite, SWT.NONE);
		MEImproveLabel.setText(paramsMap.get(MEImproveLabelName) == null ? "" : paramsMap.get(MEImproveLabelName) + reg.getString("DCNReport.UNIT"));
		MEImproveLabel.setLayoutData(labelData);

		MEImproveText = new Text(topComposite, SWT.SINGLE | SWT.BORDER);
		MEImproveText.setText(paramsMap.get(MEImproveProp) == null ? "" : paramsMap.get(MEImproveProp));
		MEImproveText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		MEImproveText.setToolTipText(reg.getString("ToolTipText.MSG"));
		MEImproveText.setSelection(MEImproveText.getText().length());

		labelData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		labelData.widthHint = 100;
		labelData.heightHint = 15;

		otherImproveLabel = new Label(topComposite, SWT.NONE);
		otherImproveLabel.setText(paramsMap.get(otherImproveLabelName) == null ? "" : paramsMap.get(otherImproveLabelName) + reg.getString("DCNReport.UNIT"));
		otherImproveLabel.setLayoutData(labelData);

		otherImproveText = new Text(topComposite, SWT.SINGLE | SWT.BORDER);
		otherImproveText.setText(paramsMap.get(otherImproveProp) == null ? "" : paramsMap.get(otherImproveProp));
		otherImproveText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		otherImproveText.setToolTipText(reg.getString("ToolTipText.MSG"));
		otherImproveText.setSelection(otherImproveText.getText().length());

		labelData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		labelData.widthHint = 50;
		labelData.heightHint = 15;

		processImproveLabel = new Label(topComposite, SWT.NONE);
		processImproveLabel.setText(paramsMap.get(processImproveLabelName) == null ? "" : paramsMap.get(processImproveLabelName) + reg.getString("DCNReport.UNIT"));
		processImproveLabel.setLayoutData(labelData);

		processImproveText = new Text(topComposite, SWT.SINGLE | SWT.BORDER);
		processImproveText.setText(paramsMap.get(processImproveProp) == null ? "" : paramsMap.get(processImproveProp));
		processImproveText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		processImproveText.setToolTipText(reg.getString("ToolTipText.MSG"));
		processImproveText.setSelection(processImproveText.getText().length());

		labelData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		labelData.widthHint = 50;
		labelData.heightHint = 15;

		DFXImproveLabel = new Label(topComposite, SWT.NONE);
		DFXImproveLabel.setText(paramsMap.get(DFXImproveLabelName) == null ? "" : paramsMap.get(DFXImproveLabelName) + reg.getString("DCNReport.UNIT"));
		DFXImproveLabel.setLayoutData(labelData);

		DFXImproveText = new Text(topComposite, SWT.SINGLE | SWT.BORDER);
		DFXImproveText.setText(paramsMap.get(DFXImproveProp) == null ? "" : paramsMap.get(DFXImproveProp));
		DFXImproveText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		DFXImproveText.setToolTipText(reg.getString("ToolTipText.MSG"));
		DFXImproveText.setSelection(DFXImproveText.getText().length());

		Composite bottomBtnComposite = new Composite(shell, SWT.NONE);
		GridData bottomBtndata = new GridData(GridData.FILL_HORIZONTAL);
		bottomBtndata.horizontalAlignment = GridData.CENTER;
		bottomBtnComposite.setLayoutData(bottomBtndata);

		RowLayout rowLayout = new RowLayout(); // ���õײ���ť��尴ťΪ����ʽ���֣���ť���15����
		rowLayout.spacing = 15;
		bottomBtnComposite.setLayout(rowLayout);

		RowData bottomBtnRowData = new RowData();
		bottomBtnRowData.width = 60;
		bottomBtnRowData.height = 30;

		saveBtn = new Button(bottomBtnComposite, SWT.PUSH);
		saveBtn.setText(reg.getString("saveBtn.LABEL"));
		saveBtn.setLayoutData(bottomBtnRowData);

		cancelBtn = new Button(bottomBtnComposite, SWT.PUSH);
		cancelBtn.setText(reg.getString("cancelBtn.LABEL"));
		cancelBtn.setLayoutData(bottomBtnRowData);

		addListener();
		shell.open();
		shell.layout();

		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void addListener() {
		saveBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {				
				getTextValue();
				shell.dispose();
				
				LoopProgerssDialog loopProgerssDialog = new LoopProgerssDialog(shell, null, reg.getString("progressDialog.TITLE"));
				loopProgerssDialog.run(true, new IProgressDialogRunnable() {
					
					@Override
					public void run(BooleanFlag stopFlag) {
						try {
							if (!checkTextContent()) {
								stopFlag.setFlag(true); // ִ����Ϻ�ѱ�־λ����Ϊֹͣ����֪ͨ�����ȿ�
								saveFlag = false;
								return;
							}
							 
							TCUtil.setBypass(session);
							saveForm();
							TCUtil.closeBypass(session);
							DCNReportBean bean = new DCNReportBean();
							String objectType = DCNItemRev.getTypeObject().getName();
							if (DCNCostImpactConstant.DT_DCN_TYPE.equals(objectType)) {
								bean.setBu(DCNCostImpactConstant.DT);
							} else if (DCNCostImpactConstant.MNT_DCN_TYPE.equals(objectType)) {
								bean.setBu(DCNCostImpactConstant.MNT);
							}
							bean = tcPropMapping(bean, DCNItemRev, "DCNRevision");
							getSolutionItem(bean); // ������ƶ�������������ƶ���
							String projectInfo = getProjectInfo(); // ��ȡר����Ϣ
							if (CommonTools.isNotEmpty(projectInfo)) {
								bean.setProjectId(projectInfo.split("\\|")[0]);
								bean.setProjectName(projectInfo.split("\\|")[1]);						
							}					
							if (checkDCNRelease()) {
								bean.setStatus(StatusEnum.Released.name());						
							}
							
							List<DCNReportBean> list = new ArrayList<DCNReportBean>();					
							if (CommonTools.isNotEmpty(paramsMappProp)) {
								for (Map.Entry<String, String> entry: paramsMappProp.entrySet()) {
									DCNReportBean newBean = (DCNReportBean) bean.clone();
									newBean.setReason(entry.getKey());									
									if (CommonTools.isNotEmpty(entry.getValue())) {
										newBean.setCostImpact(entry.getValue());
									}								
									list.add(newBean);
								}
							}	
							GsonBuilder builder = new GsonBuilder();
							Gson gson = builder.create();
							System.out.println(gson.toJson(list));
							String resultStr = HttpUtil.post(springUrl + "/tc-service/DCNReport/saveDCNData", gson.toJson(list));
							System.out.println(springUrl + "/tc-service/DCNReport/saveDCNData");							
//							String resultStr = HttpUtil.post("http://127.0.0.1:8888" + "/DCNReport/saveDCNData", gson.toJson(list));
							if (stopFlag.getFlag()) { // ����Ƿ���ֹͣ��̨����
								saveFlag = false;
//								throw new Exception();
								return;
							}
							AjaxResult result = gson.fromJson(resultStr, AjaxResult.class);	
							stopFlag.setFlag(true);
							if (((String)result.get(AjaxResult.CODE_TAG)).equalsIgnoreCase(AjaxResult.STATUS_SUCCESS)) {
								saveFlag = true;																
							} else {
								throw new Exception(reg.getString("saveFailure.MSG"));
							}  
						} catch (Exception e) {
							e.printStackTrace();
							stopFlag.setFlag(true);	
							saveFlag = false;
							errorMsg = e.getLocalizedMessage();	
						}
					}
				});	
				
				if (saveFlag) {
					MessageShow.infoMsgBox(reg.getString("saveSuccess.MSG"), reg.getString("INFORMATION.MSG"));
				} else {
					if (CommonTools.isNotEmpty(errorMsg)) {
						MessageShow.errorMsgBox(errorMsg, reg.getString("ERROR.MSG"));
					} else {
						MessageShow.errorMsgBox(reg.getString("saveFailure.MSG"), reg.getString("ERROR.MSG"));
					}
				} 
			}
		});

		cancelBtn.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				MessageBox msgBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				msgBox.setText(reg.getString("messageBox.TITLE"));
				msgBox.setMessage(reg.getString("messageBox.MSG"));
				int rc = msgBox.open();
				if (rc == SWT.YES) {
					System.out.println("�������� ���ǡ� ��ť");
					shell.dispose();
				} else if (rc == SWT.NO) {
					System.out.println("�������� ���� ��ť");
				}
			}
		});

		shell.addShellListener(new ShellAdapter() {
			// �����رմ����¼�
			public void shellClosed(ShellEvent e) {
				MessageBox msgBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				msgBox.setText(reg.getString("messageBox.TITLE"));
				msgBox.setMessage(reg.getString("messageBox.MSG"));			
				int rc = msgBox.open();
				e.doit = rc == SWT.YES;
				if (e.doit) {
					System.out.println("�������� ���ǡ� ��ť");
					shell.dispose();
				} else {
					System.out.println("�������� ���� ��ť");
				}

			}
		});

		custRequestText.addVerifyListener(new VerifyListener() { // �����������ÿһ�ΰ������ᴥ��

			@Override
			public void verifyText(VerifyEvent e) {
				String inStr = e.text; // ��ǰ�����ַ���ע��: ��ֵ��text.getText()����ͬһ��ֵ
				System.out.println(inStr);
				if (inStr.length() > 0) { // ���˸��ʱinStr="",����Ϊ0
					// doit�������Ϊtrue�����ַ�������ʾ���ı��򣬷�֮������
					e.doit = NumberUtils.isNumber(inStr) || ".".equals(inStr); // �ж�inStr�Ƿ�Ϊ����/.
				}
			}
		});

		MEImproveText.addVerifyListener(new VerifyListener() { // �����������ÿһ�ΰ������ᴥ��

			@Override
			public void verifyText(VerifyEvent e) {
				String inStr = e.text; // ��ǰ�����ַ���ע��: ��ֵ��text.getText()����ͬһ��ֵ
				System.out.println(inStr);
				if (inStr.length() > 0) { // ���˸��ʱinStr="",����Ϊ0
					// doit�������Ϊtrue�����ַ�������ʾ���ı��򣬷�֮������
					e.doit = NumberUtils.isNumber(inStr) || ".".equals(inStr); // �ж�inStr�Ƿ�Ϊ����/.
				}
			}
		});

		otherImproveText.addVerifyListener(new VerifyListener() { // �����������ÿһ�ΰ������ᴥ��

			@Override
			public void verifyText(VerifyEvent e) {
				String inStr = e.text; // ��ǰ�����ַ���ע��: ��ֵ��text.getText()����ͬһ��ֵ
				System.out.println(inStr);
				if (inStr.length() > 0) { // ���˸��ʱinStr="",����Ϊ0
					// doit�������Ϊtrue�����ַ�������ʾ���ı��򣬷�֮������
					e.doit = NumberUtils.isNumber(inStr) || ".".equals(inStr); // �ж�inStr�Ƿ�Ϊ����/.
				}
			}
		});

		processImproveText.addVerifyListener(new VerifyListener() { // �����������ÿһ�ΰ������ᴥ��

			@Override
			public void verifyText(VerifyEvent e) {
				String inStr = e.text; // ��ǰ�����ַ���ע��: ��ֵ��text.getText()����ͬһ��ֵ
				System.out.println(inStr);
				if (inStr.length() > 0) { // ���˸��ʱinStr="",����Ϊ0
					// doit�������Ϊtrue�����ַ�������ʾ���ı��򣬷�֮������
					e.doit = NumberUtils.isNumber(inStr) || ".".equals(inStr); // �ж�inStr�Ƿ�Ϊ����/.
				}
			}
		});

		DFXImproveText.addVerifyListener(new VerifyListener() { // �����������ÿһ�ΰ������ᴥ��

			@Override
			public void verifyText(VerifyEvent e) {
				String inStr = e.text; // ��ǰ�����ַ���ע��: ��ֵ��text.getText()����ͬһ��ֵ
				System.out.println(inStr);
				if (inStr.length() > 0) { // ���˸��ʱinStr="",����Ϊ0
					// doit�������Ϊtrue�����ַ�������ʾ���ı��򣬷�֮������
					e.doit = NumberUtils.isNumber(inStr) || ".".equals(inStr); // �ж�inStr�Ƿ�Ϊ����/.
				}
			}
		});
	}

	/**
	 * У���ı���д�Ƿ����Ҫ��
	 * 
	 * @return
	 */
	private boolean checkTextContent() {
		if (CommonTools.countStr(custRequestTextValue, ".") > 1) {
			MessageShow.warningMsgBox(reg.getString("CustRequest.MSG"), reg.getString("WARNING.MSG"));
			return false;
		}
		if (CommonTools.countStr(MEImproveTextValue, ".") > 1) {
			MessageShow.warningMsgBox(reg.getString("MEImprove.MSG"), reg.getString("WARNING.MSG"));
			return false;
		}

		if (CommonTools.countStr(otherImproveTextValue, ".") > 1) {
			MessageShow.warningMsgBox(reg.getString("OtherImprove.LABEL"), reg.getString("WARNING.MSG"));
			return false;
		}

		if (CommonTools.countStr(processImproveTextValue, ".") > 1) {
			MessageShow.warningMsgBox(reg.getString("ProcessImprove.LABEL"), reg.getString("WARNING.MSG"));
			return false;
		}

		if (CommonTools.countStr(DFXImproveTextValue, ".") > 1) {
			MessageShow.warningMsgBox(reg.getString("DFXImprove.LABEL"), reg.getString("WARNING.MSG"));
			return false;
		}
		return true;
	}

	/**
	 * ����form������
	 * 
	 * @throws TCException
	 */
	private void saveForm() throws TCException {
		Map<String, String> propMap = new HashMap<String, String>();
		if (CommonTools.isNotEmpty(custRequestTextValue)) {
			propMap.put(custRequestProp, CommonTools.formatDecimal(String.valueOf(NumberUtils.toDouble(custRequestTextValue)), 2));
		} else {
			propMap.put(custRequestProp, "");
		}
		if (CommonTools.isNotEmpty(MEImproveTextValue)) {
			propMap.put(MEImproveProp, CommonTools.formatDecimal(String.valueOf(NumberUtils.toDouble(MEImproveTextValue)), 2));
		} else {
			propMap.put(MEImproveProp, "");
		}
		if (CommonTools.isNotEmpty(otherImproveTextValue)) {
			propMap.put(otherImproveProp, CommonTools.formatDecimal(String.valueOf(NumberUtils.toDouble(otherImproveTextValue)), 2));
		} else {
			propMap.put(otherImproveProp, "");
		}
		if (CommonTools.isNotEmpty(processImproveTextValue)) {
			propMap.put(processImproveProp, CommonTools.formatDecimal(String.valueOf(NumberUtils.toDouble(processImproveTextValue)), 2));
		} else {
			propMap.put(processImproveProp, "");
		}
		if (CommonTools.isNotEmpty(DFXImproveTextValue)) {
			propMap.put(DFXImproveProp, CommonTools.formatDecimal(String.valueOf(NumberUtils.toDouble(DFXImproveTextValue)), 2));
		} else {
			propMap.put(DFXImproveProp, "");
		}
		form.setProperties(propMap);		
		
		if (CommonTools.isNotEmpty(paramsMap.get(custRequestLabelName))) {
			paramsMappProp.put(custRequestProp, propMap.get(custRequestProp));
		}
		
		if (CommonTools.isNotEmpty(paramsMap.get(MEImproveLabelName))) {
			paramsMappProp.put(MEImproveProp, propMap.get(MEImproveProp));
		}
		
		if (CommonTools.isNotEmpty(paramsMap.get(otherImproveLabelName))) {
			paramsMappProp.put(otherImproveProp, propMap.get(otherImproveProp));
		}
		
		if (CommonTools.isNotEmpty(paramsMap.get(processImproveLabelName))) {
			paramsMappProp.put(processImproveProp, propMap.get(processImproveProp));
		}
		
		if (CommonTools.isNotEmpty(paramsMap.get(DFXImproveLabelName))) {
			paramsMappProp.put(DFXImproveProp, propMap.get(DFXImproveProp));
		}
		
	}
	
	
	/**
	 * ��ȡDCN���ñ༭����
	 * 
	 * @return
	 * @throws TCException
	 */
 	private Map<String, String> getDCNCostParams() throws TCException {
		Map<String, String> paramsMap = new LinkedHashMap<String, String>();
		TCComponent[] relatedComponents = DCNItemRev.getRelatedComponents("IMAN_specification");
		if (CommonTools.isNotEmpty(relatedComponents)) {
			for (TCComponent tcComponent : relatedComponents) {
				if (!(tcComponent instanceof TCComponentForm)) {
					continue;
				}
				form = (TCComponentForm) tcComponent;
				String objectType = form.getTypeObject().getName();
				if (!DCNCostImpactConstant.D9_DCNCOSTIMPACT.equals(objectType)) {
					continue;
				}
				break;
			}
		}
		if (CommonTools.isNotEmpty(form)) {
			form.refresh();
			TCFormProperty[] displayableFormProperties = form.getDisplayableFormProperties();
			for (TCFormProperty tcFormProperty : displayableFormProperties) {
				String propertyDisplayName = tcFormProperty.getPropertyDisplayName();
				System.out.println("==>> ��ʾ����Ϊ: " + propertyDisplayName);

				String propertyName = tcFormProperty.getPropertyName();
				System.out.println("==>> ��ʵ������Ϊ: " + propertyName);

				String propertyValue = tcFormProperty.getPropertyValue() == null ? "" : tcFormProperty.getPropertyValue().toString().trim();
				System.out.println("==>> ����ֵΪ: " + propertyName);
				if (custRequestProp.equals(propertyName)) {
					paramsMap.put(custRequestLabelName, propertyDisplayName);
					paramsMap.put(custRequestProp, propertyValue);
				} else if (MEImproveProp.equals(propertyName)) {
					paramsMap.put(MEImproveLabelName, propertyDisplayName);
					paramsMap.put(MEImproveProp, propertyValue);
				} else if (otherImproveProp.equals(propertyName)) {
					paramsMap.put(otherImproveLabelName, propertyDisplayName);
					paramsMap.put(otherImproveProp, propertyValue);
				} else if (processImproveProp.equals(propertyName)) {
					paramsMap.put(processImproveLabelName, propertyDisplayName);
					paramsMap.put(processImproveProp, propertyValue);
				} else if (DFXImproveProp.equals(propertyName)) {
					paramsMap.put(DFXImproveLabelName, propertyDisplayName);
					paramsMap.put(DFXImproveProp, propertyValue);
				}
			}
		}
		return paramsMap;
	}
	
	/**
	 * У��DCN����汾�Ƿ��ڷ���װ��
	 * @return
	 */
	private boolean checkDCNRelease() {
		if (!TCUtil.isReleased(DCNItemRev)) {
//			MessageShow.warningMsgBox(reg.getString("DCNRelease.MSG"), reg.getString("WARNING.MSG"));
			return false;
		}
		return true;
	}
	 
	private void getSolutionItem(DCNReportBean bean) throws TCException, IllegalArgumentException, IllegalAccessException {
		TCComponentItemRevision itemRev = null;
		DCNItemRev.refresh();
		TCComponent[] relatedComponents = DCNItemRev.getRelatedComponents("CMHasSolutionItem");
		if (CommonTools.isNotEmpty(relatedComponents)) {
			for (TCComponent tcComponent : relatedComponents) {
				if (tcComponent instanceof TCComponentItemRevision) {
					itemRev = (TCComponentItemRevision) tcComponent;
					String objectType = itemRev.getTypeObject().getName();
					if (objectType.contains(DCNCostImpactConstant.DesignRev)) {
						break;
					}
				}
			}
		}
		
		if (CommonTools.isNotEmpty(itemRev)) {
			bean = tcPropMapping(bean, itemRev, "DesignRevision");
			String itemId = itemRev.getProperty("item_id");
			if (itemId.startsWith(DCNCostImpactConstant.SHEET_METAL)) {
				bean.setModelNoPrefix(DCNCostImpactConstant.SHEET_METAL);
			} else if (itemId.startsWith(DCNCostImpactConstant.PLASTIC)) {
				bean.setModelNoPrefix(DCNCostImpactConstant.PLASTIC);				
			}
		}
	}
	
	/**
	 * ��ȡר����Ϣ
	 * @return
	 * @throws TCException
	 */
	private String getProjectInfo() throws TCException {
		TCComponentItem item = DCNItemRev.getItem();
		item.refresh();
		TCComponent[] pjs = item.getRelatedComponents("project_list");
//		item.getRelatedComponent(s)
//		TCProperty props = item.getTCProperty("project_list");
		if (CommonTools.isNotEmpty(pjs)) {			
//			TCComponent[] pjs = props.getReferenceValueArray();
			return Stream.of(pjs).map(e -> ((TCComponentProject) e).getProjectID() + "|" + ((TCComponentProject) e).getProjectName()).collect(Collectors.toList()).get(0);
		}
		return "";
	}
	
	
	private void getTextValue() {
		custRequestTextValue = CommonTools.replaceBlank(custRequestText.getText());
		MEImproveTextValue = CommonTools.replaceBlank(MEImproveText.getText());
		otherImproveTextValue = CommonTools.replaceBlank(otherImproveText.getText());
		processImproveTextValue = CommonTools.replaceBlank(processImproveText.getText());
		DFXImproveTextValue = CommonTools.replaceBlank(DFXImproveText.getText());
	}
	
	
 	public <T> T tcPropMapping(T bean, TCComponent tcObject, String typeStr)
			throws IllegalArgumentException, IllegalAccessException, TCException {
		if (bean != null && tcObject != null) {
			Field[] fields = bean.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				TCPropertes tcPropName = fields[i].getAnnotation(TCPropertes.class);
				if (tcPropName != null) {
					String val = "";
					String propertyName = tcPropName.tcProperty();					
					String tctype = tcPropName.tcType();
					if (propertyName.length() == 0 || !tctype.equalsIgnoreCase(typeStr)) {
						continue;
					}
					
					int index = propertyName.indexOf(D9_PREFIX);
					if (index != -1) {
						String pocPropertyName = propertyName.substring(0, index + D9_PREFIX.length()) + POC_STR
								+ propertyName.substring(index + D9_PREFIX.length());
						if (tcObject.isValidPropertyName(pocPropertyName)) {
							propertyName = pocPropertyName;
						}
					}
					tcObject.refresh();
					
					if (tcObject.isValidPropertyName(propertyName)) {
						if ("creation_date".equals(propertyName)) {
							TCProperty tcProperty = tcObject.getTCProperty("creation_date");
							Date dateValue = tcProperty.getDateValue();
							val = new SimpleDateFormat(sdf).format(dateValue);
						} else {
							val = tcObject.getProperty(propertyName);
						}
						
					} else {
						System.out.println("propertyName is not exist " + propertyName);
					}					
					
					if (val != null && val.length() > 0) {
						fields[i].set(bean, val);
					} else {
						fields[i].set(bean, "");
					}
				}
			}
		}
		return bean;
	}
		
}
