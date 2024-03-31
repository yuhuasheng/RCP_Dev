package com.foxconn.electronics.dgkpi.cadimport.dialog;


import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.sound.midi.Soundbank;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

public class CADImportTCDialog extends Dialog {

	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private Shell shell = null;
	private Shell parentShell = null;
	private Text filePathText = null;	
	private Button chooseBtn = null;
	private Button importButton = null;	
	private StyledText styledText = null;
	private StyleRange infoStyle = null;
	private StyleRange warnStyle = null;
	private StyleRange errorStyle = null;
	private Composite statusBar = null;
	private Label status = null;
	private ProgressBar progressBar = null;
	private Registry reg = null;
	private boolean isRunning = false;
	private boolean importFlag = false; //用作是否导入属性到TC中的
	
	public CADImportTCDialog(AbstractAIFUIApplication app, Shell parentShell) {
		super(parentShell);
		this.app = app;
		this.session = (TCSession) this.app.getSession();
		this.parentShell = parentShell;
		reg = Registry.getRegistry("com.foxconn.electronics.dgkpi.dgkpi");
		
		infoStyle = new StyleRange();
		infoStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN);
		
		warnStyle = new StyleRange();				
		warnStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_YELLOW);
		
		errorStyle = new StyleRange();				
		errorStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_RED);
		
		initUI();
	}
	
	private void initUI() {
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(620, 600);
		shell.setText(reg.getString("CADImportShell.TITLE"));		
		shell.setLayout(new GridLayout(1, false));
		TCUtil.centerShell(shell);
		
		Image image = getDefaultImage();
		if (CommonTools.isNotEmpty(image)) {
			shell.setImage(image);
		}		
		
		GridData topData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		topData.grabExcessHorizontalSpace = true;
		
		Composite topComposite = new Composite(shell, SWT.NONE);
		topComposite.setLayout(new GridLayout(2, false));
		topComposite.setLayoutData(topData);		
		
		GridData filePathData = new GridData(GridData.FILL_HORIZONTAL);
		
		filePathText = new Text(topComposite, SWT.SINGLE | SWT.BORDER);
		filePathText.setEnabled(false); // 设置不可编辑
		filePathText.setLayoutData(filePathData);
		
		chooseBtn = new Button(topComposite, SWT.NONE);
		chooseBtn.setText(reg.getString("ChooseBtn.LABEL"));
		
		styledText = new StyledText(shell, SWT.READ_ONLY | SWT.MULTI |  SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		styledText.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		MyActionGroup actionGroup = new MyActionGroup(styledText, reg);
		actionGroup.fillContextMenu(new MenuManager());			
		
		statusBar = new Composite(shell, SWT.NONE);
		statusBar.setLayout(new GridLayout(2, false));
		statusBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		status = new Label(statusBar, SWT.NONE);
		status.setText(reg.getString("ProgressBar1.TITLE"));
		status.setVisible(false);		
		
		
		Composite bottomBtnComposite = new Composite(shell, SWT.NONE);
		GridData bottomBtndata = new GridData(GridData.FILL_HORIZONTAL);
		bottomBtndata.horizontalAlignment = GridData.CENTER; 		
		bottomBtnComposite.setLayoutData(bottomBtndata);
		
		RowLayout bottomRowLayout = new RowLayout(); 
		bottomRowLayout.spacing = 15;
		
		bottomBtnComposite.setLayout(bottomRowLayout);
		
		RowData bottomBtnRowData = new RowData();
		bottomBtnRowData.width = 60;
		bottomBtnRowData.height = 30;
		
		importButton = new Button(bottomBtnComposite, SWT.NONE);
		importButton.setText(reg.getString("ImportBtn.LABEL"));
		importButton.setLayoutData(bottomBtnRowData);
		importButton.setEnabled(false); // 设置不可点击		
		
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
	
	
	/**
	 * 初始化进度天气界面
	 * @param statusName
	 * @param importFlag
	 */
	public void setProgressBar() {	
		status.setVisible(true);
		
		progressBar = new ProgressBar(statusBar, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressBar.setMaximum(100);
		
		
		statusBar.layout();		
	}
	
	
	private void updateProgressBarByImport(String filePath) {
		new Thread(new Runnable() {
			private int progress = 0;
			private static final int INCREMENT = 10;
			private boolean flag = false; // 数据接口获取成功与否接口
			
			@Override
			public void run() {
				
				new Thread() {
					public void run() {
						setRunningFlag(true);						
						TCUtil.writeInfoLog("############ 当前导入的CAD文件路径为: " + filePath + " ############", styledText, infoStyle);
						TCUtil.writeInfoLog("############ 开始执行本次导入 ############", styledText, infoStyle);
						try {							
							if (filePath.contains(" ")) {
								TCUtil.writeErrorLog("############ 文件名: " + filePath.substring(filePath.lastIndexOf(File.separator)) + " 含有空格，无法执行下一步 ############", styledText, errorStyle);
								throw new Exception("操作終止");
							}
							
							if (filePath.contains("&")) {
								TCUtil.writeErrorLog("############ 文件名: " + filePath.substring(filePath.lastIndexOf(File.separator)) + " 含有特殊字符&，无法执行下一步 ############", styledText, errorStyle);
								throw new Exception("操作終止");
							}
							String resultPath = filePath.substring(0, filePath.lastIndexOf(File.separator)) + "\\result\\result.txt";
							String logPath = filePath.substring(0, filePath.lastIndexOf(File.separator)) + "\\Log\\log.txt";
							String command = "C:\\Siemens\\Teamcenter14\\portal\\plugins\\CAD\\AutoCAD2022_ReadTable.exe" + " " + filePath + " " + resultPath;
									
							TCUtil.doCall(command, "GBK");										
							
							List<String> txtContents = TCUtil.getTxtContent(logPath, null);
							List<String> resultContents = TCUtil.getTxtContent(resultPath, null);
							
							for (String str : txtContents) {
								System.out.println(str);
								if (str.toUpperCase().contains("INFO")) {
									TCUtil.writeInfoLog(str, styledText, infoStyle);
								} else if (str.toUpperCase().contains("WARN")) {
									TCUtil.writeWarnLog(str, styledText, warnStyle);
								} else if (str.toUpperCase().contains("ERROR")) {
									TCUtil.writeErrorLog(str, styledText, errorStyle);
								}
							}							
						
							
							if (CommonTools.isNotEmpty(resultContents)) {
								String result = resultContents.stream().collect(Collectors.joining("\n"));
								Display.getDefault().syncExec(() -> {
									boolean openConfirm = MessageDialog.openQuestion(getShell(), reg.getString("AnalyseTip.MSG"), result);
									System.out.println("==>> openConfirm: " + openConfirm);		
									importFlag = openConfirm;									
								});
														
							}
							
							if (importFlag) {
								TCUtil.writeWarnLog("====================================", styledText, warnStyle);	
								TCUtil.writeInfoLog("############ 开始执行保存属性到TC ############", styledText, infoStyle);								
								Thread.sleep(2000);		
								
								TCUtil.writeInfoLog("############ 执行保存属性到TC完成 ############", styledText, infoStyle);
								TCUtil.writeWarnLog("====================================", styledText, warnStyle);	
							}
							TCUtil.writeInfoLog("############ 本次导入执行完成 ############", styledText, infoStyle);
						} catch (Exception e1) {
							e1.printStackTrace();
							TCUtil.writeErrorLog("############ 本次导入发生错误 ############", styledText, errorStyle);							
							TCUtil.writeErrorLog(TCUtil.getExceptionMsg(e1), styledText, errorStyle);							
						} finally {
							flag = true;
							setRunningFlag(false);
						}
					}
				}.start();
				
				while (progressBar != null && !progressBar.isDisposed()) {
					Display.getDefault().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							if (flag) {
//								importButton.setEnabled(true); // 设置导入按钮状态
								status.setVisible(false);
								progressBar.dispose(); // 销毁进度条组件
							}
							
							if (!progressBar.isDisposed()) {
								progressBar.setSelection((progress += INCREMENT) % (progressBar.getMaximum() + INCREMENT));
							}
						}
					});
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	
	private void addListener() {
		
		chooseBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String filePath = TCUtil.openCADFileChooser(shell);
				if (CommonTools.isEmpty(filePath)) {
					return;
				}
				
				filePathText.setText(filePath);				
				importButton.setEnabled(true);
								
			}
		});
		
		importButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String filePath = filePathText.getText();
				
				setProgressBar();
				updateProgressBarByImport(filePath);			
				
			}
		});
		
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				if (isRunning) {
					MessageDialog.openWarning(getShell(), reg.getString("WARNING.MSG"), reg.getString("ImportWarn.MSG"));
					e.doit = false;
				} else {
					boolean flag = MessageDialog.openQuestion(getShell(), reg.getString("INFORMATION.MSG"), reg.getString("CloseTip.MSG"));
					System.out.println("==>> flag: " + flag);
					e.doit = flag == true;
					if (e.doit) {
						shell.dispose();
					}
					
				}
			}
		});
	}
	
	private void setRunningFlag(boolean flag) {
		Display.getDefault().syncExec(() -> {
			isRunning = flag;
			if (!importButton.isDisposed()) {
				importButton.setEnabled(!flag);
			}			
		});
	}	
}
