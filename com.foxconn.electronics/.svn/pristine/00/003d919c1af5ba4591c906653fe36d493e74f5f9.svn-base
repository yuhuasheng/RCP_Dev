package com.foxconn.electronics.tcuserimport.dialog;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.tcuserimport.domain.SpasToTCBean;
import com.foxconn.electronics.tcuserimport.excel.ExcelAnalyseTools;
import com.foxconn.tcutils.constant.PreferenceConstant;
import com.foxconn.tcutils.progress.BooleanFlag;
import com.foxconn.tcutils.progress.IProgressDialogRunnable;
import com.foxconn.tcutils.progress.LoopProgerssDialog;
import com.foxconn.tcutils.util.AjaxResult;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.HttpUtil;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpResponse;


public class TCUserImportDialog extends Dialog {

	private Shell shell = null;
	private Shell parentShell = null;
	private TCSession session = null;
	private Registry reg = null;
	private Text filePathText = null;
	private StyledText styledText = null;
	private StyleRange infoStyle = null;
	private StyleRange warnStyle = null;
	private StyleRange errorStyle = null;
//	private Text logText = null;
	private Button chooseBtn = null;
	private Button downloadBtn = null;
	private Button importButton = null;	
	private Label label = null;
	private Combo combo = null;	
	private ExcelAnalyseTools analyseTools = null;
	private String springCloudUrl = null;
	private static final String format = "yyyy-MM-dd";
	private String[] comboValue = {"--- 请选择 ---", "更新或新增", "删除"};
	private String comboTextValue = null;
	private String filePath = null;
	private String operation = null;
	private static final String add = "add";
	private static final String	delete = "delete";
	
	public TCUserImportDialog(Shell parentShell, TCSession session) {
		super(parentShell);
		this.parentShell = parentShell;
		this.session = session;
		reg = Registry.getRegistry("com.foxconn.electronics.tcuserimport.tcuserimport");
		springCloudUrl = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site,
				PreferenceConstant.D9_SPRINGCLOUD_URL);
		if (CommonTools.isEmpty(springCloudUrl)) {
			TCUtil.warningMsgBox(reg.getString("preferenceName.MSG") + PreferenceConstant.D9_SPRINGCLOUD_URL + reg.getString("preferenceErr.MSG"), reg.getString("WARNING.MSG"));
			return;
		}
		analyseTools = new ExcelAnalyseTools(this);
		
		infoStyle = new StyleRange();
		infoStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN);
		
		warnStyle = new StyleRange();				
		warnStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_YELLOW);
		
		errorStyle = new StyleRange();				
//		errorStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_RED);
		errorStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_RED);
		
		initUI();
	}
	
	
	private void initUI() {
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(550, 600);
		shell.setText(reg.getString("TCUserImportShell.TITLE"));
		shell.setLayout(new GridLayout(1, false));
		TCUtil.centerShell(shell);
		
		Image image = getDefaultImage();
		if (CommonTools.isNotEmpty(image)) {
			shell.setImage(image);
		}
		
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.grabExcessHorizontalSpace = true;
		
		
		Composite topComposite = new Composite(shell, SWT.NONE);
		topComposite.setLayout(new GridLayout(5, false));
		topComposite.setLayoutData(gridData);	
		
		
		label = new Label(topComposite, SWT.NONE);
		label.setText(reg.getString("operation.LABEL"));
		
		GridData gridDataText = new GridData();
		gridDataText.widthHint = 100;
		
		combo = new Combo(topComposite, SWT.READ_ONLY); // 定义一个只读的下拉框
		combo.setLayoutData(gridDataText);
		
		GridData filePathData = new GridData(GridData.FILL_HORIZONTAL);
		filePathData.horizontalIndent = 15;		
		
		filePathText = new Text(topComposite, SWT.SINGLE | SWT.BORDER);
		filePathText.setEnabled(false); // 设置不可编辑
		filePathText.setLayoutData(filePathData);
		
		chooseBtn = new Button(topComposite, SWT.NONE);
		chooseBtn.setText(reg.getString("chooseBtn.LABEL"));
		
		downloadBtn = new Button(topComposite, SWT.NONE);
		downloadBtn.setText(reg.getString("downloadBtn.LABEL"));
		
//		logText = new Text(shell, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
//		logText.setLayoutData(new GridData(GridData.FILL_BOTH));		 
//		logText.setEditable(false);
//		logText.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));; // 设置字体颜色
		
		styledText = new StyledText(shell, SWT.READ_ONLY | SWT.MULTI |  SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		styledText.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		MyActionGroup actionGroup = new MyActionGroup(styledText, reg);
		actionGroup.fillContextMenu(new MenuManager());
		
		Composite bottomComposite = new Composite(shell, SWT.NONE);
		bottomComposite.setLayout(new GridLayout(1, true));
		bottomComposite.setLayoutData(gridData);
		
		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalAlignment = GridData.CENTER;		

		importButton = new Button(bottomComposite, SWT.NONE);
		importButton.setText(reg.getString("importBtn.LABEL"));
		importButton.setLayoutData(gridData2);
		importButton.setEnabled(false); // 设置为不可以点击		
		
		setComboValues(); // 设置下拉框的值
		addListener(); // 添加事件监听
		
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
		chooseBtn.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent e) {
				filePath = TCUtil.openFileChooser(shell);
				if (CommonTools.isEmpty(filePath)) {
					return;
				}
				filePathText.setText(filePath);
				if (!comboTextValue.equals(comboValue[0])) { // 判断当前下拉框值是否为请选择
					importButton.setEnabled(true);
				}				
			}
		});
		
		downloadBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String currentTime = new SimpleDateFormat(format).format(new Date());
				String fileName = currentTime + "_" + "SPAS_To_TCUser_Template.xlsx";
				File exportFile = TCUtil.openSaveFileDialog(shell, fileName);
				if (CommonTools.isEmpty(exportFile)) {
					return;
				}
				
				LoopProgerssDialog loopProgerssDialog = new LoopProgerssDialog(shell, null, reg.getString("downloadProgressDialog"));
				loopProgerssDialog.run(true, new IProgressDialogRunnable() {
					
					@Override
					public void run(BooleanFlag stopFlag) {
						updateDownloadBtnStates(false); // 更新下载按钮状态为不可点击
						writeInfoLogText("******** 当前选择保存文件路径为: " + exportFile.getAbsolutePath());
						writeInfoLogText("******** 开始下载导入模板 ********");
						try {
							String result = HttpUtil.httpGet(springCloudUrl + "/tc-integrate/meet/downloadTemplate", null, null, 20000, null, null);
							if (stopFlag.getFlag()) { // 监控是否让停止后台任务
								writeErrorLogText("******** 当前下载操作被终止 ********");
								throw new Exception("下载模板終止");
							}
							
							if(CommonTools.isNotEmpty(result)) {
								FileUtil.writeBytes(Base64.decode(result), exportFile.getAbsolutePath()); // 将输入流输出到本地文件								
								writeInfoLogText("******** 模板文件下载完成，文件存放路径为: " + exportFile.getAbsolutePath());
								writeInfoLogText("******** 本次下载完成 ********");
							} else {
								writeErrorLogText("******** 本次下载失败  ********");
							}
							
						} catch (Exception e) {
							e.printStackTrace();
							writeErrorLogText(TCUtil.getExceptionMsg(e));
							writeErrorLogText("******** 本次下载失败  ********");
						} finally {
							stopFlag.setFlag(true); // 不管成功与否，将进度条关闭
							updateDownloadBtnStates(true); // 恢复下载模板按钮
							
						}
					}
				});
			}
		});
		

		importButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String filePath = filePathText.getText();
				if (!checkOperation(e)) { // 校验选择的下拉框
					return;
				};
				
				LoopProgerssDialog loopProgerssDialog = new LoopProgerssDialog(shell, null, reg.getString("progressDialog.TITLE"));
				loopProgerssDialog.run(true, new IProgressDialogRunnable() {
					
					@Override
					public void run(BooleanFlag stopFlag) {
						updateImportBtnStates(false); // 更新导入按钮状态为不可点击
						writeInfoLogText("******** 当前导入的Excel文件路径为: " + filePath + " ********");
						writeInfoLogText("******** 本次操作开始执行 ********");
						
						try {							
							List<SpasToTCBean> resultList = analyseTools.analyseExcel(filePath);
							if (CommonTools.isEmpty(resultList)) {
								writeErrorLogText("解析Excel文件发生异常，请检查文件后，然后进行操作");
								writeErrorLogText("******** 当前导入操作被终止 ********");
								throw new Exception("解析Excel文件发生异常");
							}
							
							if (stopFlag.getFlag()) { // 监控是否让停止后台任务
								writeErrorLogText("******** 当前操作被终止 ********");
								throw new Exception("操作終止");
							}
							
							if (comboTextValue.equals(comboValue[1])) {
								operation = add;
							} else if (comboTextValue.equals(comboValue[2])) {
								operation = delete;
							}
							
							String rs = HttpUtil.httpPost(springCloudUrl + "/tc-integrate/meet/updateSpasToTC", "operation=" + operation, JSONObject.toJSONString(resultList), null, 1000 * 60, null, null);
//							String rs = HttpUtil.httpPost("http://127.0.0.1:8220/meet/updateSpasToTC", "operation=" + operation, JSONObject.toJSONString(resultList), null, 12000, null, null);
//							String rs1 = HttpUtil.httpGet(springCloudUrl + "/tc-integrate/meet/sendTCInfoToTCFR", null, null, 1000 * 30, null, null);
//							System.out.println(rs1);
							
							JSONObject rsObj=JSON.parseObject(rs);
							if((rsObj.getString(AjaxResult.CODE_TAG).equalsIgnoreCase(AjaxResult.STATUS_SUCCESS))) {
								writeInfoLogText("******** 本次执行已完成 ********");	
							} else {
								writeErrorLogText(rsObj.get(AjaxResult.MSG_TAG).toString());
								writeErrorLogText("******** 本次执行失败 ********");
							}
							
							stopFlag.setFlag(true); // 执行完毕后把标志位设置为停止，好通知给进度框
						} catch (Exception e) {
							e.printStackTrace();
							writeErrorLogText(TCUtil.getExceptionMsg(e));
							stopFlag.setFlag(true); // 执行发生异常，也需要将进度条关闭
						} finally {
							updateImportBtnStates(true); // 恢复按钮可以点击状态
						}
					}
				});
			}

			
			
		});
		
		
		shell.addShellListener(new ShellAdapter() {

			public void shellClosed(ShellEvent e) {
				MessageBox msgBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				msgBox.setText(reg.getString("messageBox.TITLE"));
				msgBox.setMessage(reg.getString("messageBox.MSG"));
				int rc = msgBox.open();
				e.doit = rc == SWT.YES;
				if (e.doit) {
					System.out.println("您单击了 “是” 按钮");
					shell.dispose();
				} else {
					System.out.println("您单击了 “否” 按钮");
				}
			}
		});
		
		
		combo.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent e) {
				Combo source = (Combo) e.getSource();
				comboTextValue = source.getText();
				System.out.println("==>> 当前下拉框选中值为: " + source.getText());
				if (!comboTextValue.equals(comboValue[0]) && CommonTools.isNotEmpty(filePath)) { // 判断当前下拉框值是否为请选择并且文件路径不为空					
					importButton.setEnabled(true);
				} else if (comboTextValue.equals(comboValue[0]) || CommonTools.isEmpty(filePath)) {
					importButton.setEnabled(false);
				}
			}
		});
	}
	
	
	
	/**
	 * 设置下拉框值
	 */
	private void setComboValues() {
		for (int i = 0; i < comboValue.length; i++) {
			combo.add(comboValue[i]);
		}
		comboTextValue = comboValue[0];
		combo.select(0); // 设置第一项为当前项
	}
	
	
	/**
	 * 记录提示信息日志
	 * 
	 * @param message
	 */
	public void writeInfoLogText(final String message) {

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
//				logText.append("【INFO】 " + message + "\n");
				if (!styledText.isDisposed()) {
					String msg = "【INFO】 " + message;
					styledText.append(msg + "\n");
					
					infoStyle.start = styledText.getCharCount() - msg.length() - 1;
					infoStyle.length = msg.length();
					styledText.setStyleRange(infoStyle);
				}
			}
		});
	}
	
	
	/**
	 * 记录错误日志信息
	 * @param message
	 */
	public void writeErrorLogText(final String message) {

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
//				logText.append("【ERROR】 " + message + "\n");
				if (!styledText.isDisposed()) {
					String msg = "【ERROR】 " + message;
					styledText.append(msg + "\n");	
					
					errorStyle.start = styledText.getCharCount() - msg.length() - 1;
					errorStyle.length = msg.length();
					styledText.setStyleRange(errorStyle);				
				}
			}
		});
	}
	
	/**
	 * 更新导入按钮状态
	 * @param importFlag
	 */
	private void updateImportBtnStates(final boolean importFlag) {
		
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				importButton.setEnabled(importFlag); // 设置导入按钮状态
			}
		});
	}
	
	
	/**
	 * 更新下载按钮状态
	 * @param downloadFlag
	 */
	private void updateDownloadBtnStates(final boolean downloadFlag) {
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				downloadBtn.setEnabled(downloadFlag);
			}
		});
	}	
	
	private boolean checkOperation(SelectionEvent e) {
		boolean follow = true;
		if (comboTextValue.equals(comboValue[2])) {
			MessageBox msgBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			msgBox.setText(reg.getString("deleteBox.TITLE"));
			msgBox.setMessage(reg.getString("deleteBox.MSG"));
			int rc = msgBox.open();
			e.doit = rc == SWT.YES;
			if (e.doit) {
				System.out.println("您单击了 “是” 按钮");						
			} else {
				System.out.println("您单击了 “否” 按钮");
				follow = false;
			}
			
//			shell.dispose();					
		}
		
		return follow;
	}
	
	
	public static void main(String[] args) throws IOException {
		HttpUtil.httpPost("http://127.0.0.1:8023/meet/downloadTemplate", null, null, null, 12000, null, null);
//		HttpUtil.httpPost("http://10.203.163.243/tc-integrate/meet/downloadTemplate", null, null, null, 12000, null, null);
//		FileUtils.copyInputStreamToFile(inputStream, new File("D:\\1.xlsx"));
	}
}
