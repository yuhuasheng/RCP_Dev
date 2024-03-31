package com.foxconn.electronics.ftereport.dialog;
import java.io.File;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
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

import com.alibaba.fastjson.JSONObject;
import com.foxconn.electronics.ftereport.util.FteUtil;
import com.foxconn.electronics.progress.BooleanFlag;
import com.foxconn.electronics.progress.IProgressDialogRunnable;
import com.foxconn.electronics.progress.LoopProgerssDialog;
import com.foxconn.mechanism.util.CommonTools;
import com.foxconn.mechanism.util.TCUtil;
import com.foxconn.tcutils.util.AjaxResult;
import com.google.gson.Gson;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

public class NonFTEDataImportDialog extends Dialog {

	private Shell shell = null;
	private Shell parentShell = null;
	private TCSession session = null;
	private Button chooseButton = null;
	private Button importButton = null;
	private Button stopButton = null;
	private Text logText = null;
	private Text filePathText = null;
	private Label label = null;
	private Combo combo = null;
	private Registry reg = null;
	private String[] comboValue = {"--- 请选择 ---", "DT_L5", "DT_L6", "DT_L10", "MNT_L5", "MNT_L6", "MNT_L10"};
	private String filePath = null;
	private String comboTextValue = null;
	
	public NonFTEDataImportDialog(Shell parentShell, TCSession session) {
		super(parentShell);
		this.parentShell = parentShell;
		this.session = session;
		reg = Registry.getRegistry("com.foxconn.electronics.ftereport.ftereport");
		initUI();
	}
	
	
	private void initUI() {
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(550, 600);
		shell.setText(reg.getString("FTEDataImportShell.TITLE"));
		shell.setLayout(new GridLayout(1, false));
		TCUtil.centerShell(shell);
		
		Image image = getDefaultImage();
		if (CommonTools.isNotEmpty(image)) {
			shell.setImage(image);
		}
		
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.grabExcessHorizontalSpace = true;		
		
		Composite composite1 = new Composite(shell, SWT.NONE);
		composite1.setLayout(new GridLayout(4, false));
		composite1.setLayoutData(gridData);		
		
		label = new Label(composite1, SWT.NONE);
		label.setText(reg.getString("productLine.LABEL"));
//		label.setLayoutData(gridDataLabel);		
		
		GridData gridDataText = new GridData();
		gridDataText.widthHint = 100;
		
		combo = new Combo(composite1, SWT.READ_ONLY); // 定义一个只读的下拉框
		combo.setLayoutData(gridDataText);				
		
		GridData gridDataLabel = new GridData(GridData.FILL_HORIZONTAL);
		gridDataLabel.horizontalIndent = 15;
		
		filePathText = new Text(composite1, SWT.SINGLE | SWT.BORDER);
		filePathText.setEnabled(false); // 设置不可编辑
		filePathText.setLayoutData(gridDataLabel);	
		
		chooseButton = new Button(composite1, SWT.NONE);
		chooseButton.setText(reg.getString("chooseBtn.LABEL"));			
		
		GridData gridData1 = new GridData(GridData.FILL_BOTH);
		
		logText = new Text(shell, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		logText.setLayoutData(gridData1);		 
		logText.setEditable(false);
		logText.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));; // 設置字體顏色
		
		MyActionGroup actionGroup = new MyActionGroup(logText, reg);
		actionGroup.fillContextMenu(new MenuManager());
		
		Composite composite2 = new Composite(shell, SWT.NONE);
		composite2.setLayout(new GridLayout(1, true));
		composite2.setLayoutData(gridData);
		
		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalAlignment = GridData.CENTER;
		
//		stopButton = new Button(composite2, SWT.NONE);
//		stopButton.setText(reg.getString("stopBtn.LABEL"));
//		stopButton.setLayoutData(gridData2);
//		stopButton.setEnabled(false);

		importButton = new Button(composite2, SWT.NONE);
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
		chooseButton.addSelectionListener(new SelectionAdapter() {

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
		
		importButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				String filePath = filePathText.getText();
				LoopProgerssDialog loopProgerssDialog = new LoopProgerssDialog(shell, null, reg.getString("progressDialog.TITLE"));
				loopProgerssDialog.run(true, new IProgressDialogRunnable() {
					
					@Override
					public void run(BooleanFlag stopFlag) {
						updateImportBtnStates(false); // 更新导入按钮状态为不可点击
						writeInfoLogText("******** 当前导入的Excel文件路径为: " + filePath);
						writeInfoLogText("******** 开始执行导入 ********");						
						try {							
							JSONObject result = FteUtil.uploadFile(comboTextValue, new File(filePath)); // 上传附件到微服务进行处理	
							if (stopFlag.getFlag()) { // 监控是否让停止后台任务
								writeErrorLogText("******** 当前导入操作被终止 ********");
								throw new Exception("导入終止");
							}
							
							if((result.getString(AjaxResult.CODE_TAG).equalsIgnoreCase(AjaxResult.STATUS_SUCCESS))) {
								writeInfoLogText("******** 本次导入已完成 ********");	
							} else {
								if (result.get(AjaxResult.DATA_TAG) instanceof List) {
									List<String> list = (List<String>) result.get(AjaxResult.DATA_TAG);
									list.forEach(str -> {
										writeErrorLogText(str);	
									});
								}								
								else {
									writeErrorLogText(result.get(AjaxResult.MSG_TAG).toString());
								}								
								writeErrorLogText("******** 本次导入失败 ********");								
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
	private void writeInfoLogText(final String message) {

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
//				logText.append("【INFO】 " + message + "\n");
				if (!logText.isDisposed()) {
					logText.insert("【INFO】 " + message + "\n");
				}
			}
		});
	}
	
	/**
	 * 记录错误日志信息
	 * @param message
	 */
	private void writeErrorLogText(final String message) {

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
//				logText.append("【ERROR】 " + message + "\n");
				if (!logText.isDisposed()) {
					logText.insert("【ERROR】 " + message + "\n");					
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
}
