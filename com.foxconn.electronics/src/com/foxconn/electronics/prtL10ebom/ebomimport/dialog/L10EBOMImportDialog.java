package com.foxconn.electronics.prtL10ebom.ebomimport.dialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


import org.apache.poi.hssf.record.PageBreakRecord.Break;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.foxconn.decompile.util.ExcelUtil;
import com.foxconn.electronics.managementebom.secondsource.constants.Search2ndSourceConstant;
import com.foxconn.electronics.prtL10ebom.ebomimport.domain.BOMBean;
import com.foxconn.electronics.prtL10ebom.ebomimport.domain.CellBean;
import com.foxconn.electronics.prtL10ebom.ebomimport.domain.MaterialBean;
import com.foxconn.electronics.prtL10ebom.ebomimport.domain.PartQtyBean;
import com.foxconn.electronics.prtL10ebom.ebomimport.rightbtn.MyActionGroup;
import com.foxconn.electronics.prtL10ebom.ebomimport.util.ExcelParseTools;
import com.foxconn.tcutils.constant.DatasetEnum;
import com.foxconn.tcutils.constant.TCSearchEnum;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.simple.traditionnal.util.S2TTransferUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentProjectType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.soa.client.model.StrongObjectFactory;
import com.teamcenter.soaictstubs.stringSeq_tHolder;
import com.teamdev.jxbrowser.deps.com.google.common.base.Objects;

import cn.hutool.json.JSONUtil;

public class L10EBOMImportDialog extends Dialog {	
	
	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private Shell shell = null;
	private Shell parentShell = null;
	private Text filePathText = null;
	private Button chooseBtn = null;
	private Button importButton = null;
	private Label projectLabel = null;
	private Label sheetLabel = null;
	private Combo sheetCombo = null;
	private Combo projectCombo = null;
	private StyledText styledText = null;
	private StyleRange infoStyle = null;
	private StyleRange warnStyle = null;
	private StyleRange errorStyle = null;
	private Composite statusBar = null;
	private Label status = null;
	private ProgressBar progressBar = null;
	private Composite progressComposite = null;
	private Registry reg = null;
	private List<String> sheetComboList = null;
	private List<String> projectComboList = null;
	private String selectSheetComboText = null;
	private String selectProjectComboText = null;
	private boolean isRunning = false;
	private Workbook workbook = null;
	private ExcelParseTools parseTools = null;
	private String filePath = null;
	private TCComponentFolder selectFolder = null;	
	private static final String D9_VirtualPart = "D9_VirtualPart";
	private static final String D9_FinishedPart = "D9_FinishedPart";
	private static final String D9_CommonPart = "D9_CommonPart";
	private static final String D9_PCA_Part = "D9_PCA_Part";
	private static final String EDAComPart = "EDAComPart";
	private static final String Bulk_Part = "Bulk_Part";
	private static final String FPU_Part = "FPU Part";
	private static final String Assembly = "Assembly";
	private static final String Packing = "Packing";
	private static final String PCA = "PCA";
	private static final String Others = "Others";
	private static final String Folder = "Folder";
	private static final String Contents = "contents";
	private static final String ObjectName = "object_name";
	private static final String ItemId = "item_id";	
	private static final String ItemRevisionId = "item_revision_id";
	private static final String BL_quantity = "bl_quantity";
	private static final String BL_line_object = "bl_line_object";
	private static final String Revision_list = "revision_list";
	private static final String Project_list = "project_list";
	private static final String CustomerPN = "d9_CustomerPN";
	private static final String D9_Prefix = "d9_";
	private static final String Uom_Prefix = "uom_";
	private static final String Uom_tag = "uom_tag";
	private static final String REPRESENTATION_FOR = "representation_for"; 
	private static final String TC_IS_REPRESENTED_BY = "TC_Is_Represented_By";
	private static final String D9_FIND_DESIGNREV = "__D9_Find_DesignRev";
	private static final String D9_BUPN = "d9_BUPN";
	private static final String D9_3DREV = "d9_3DRev";
	private static final String D9_2DREV = "d9_2DRev";
	private static final String DCN_FOLDER = "DCN";
	private static final String PRT_DCN = "D9_PRT_DCN";
	private static final String CMHasSolutionItem = "CMHasSolutionItem";
	private static final String CMHasProblemItem = "CMHasProblemItem";
	private boolean successFlag = true;
	private Map<String, TCComponentProject> projectMap = null;	
	
	private Map<String, String> ItemMap = new HashMap<String, String>() {{
		put("FNNNN-65NNN", D9_FinishedPart);
		put("FNNNN-61NNN", D9_PCA_Part);
	}};
	
	private Map<String, String> FolderMap = new HashMap<String, String>() {{
		put("FNNNN-65NNN", Bulk_Part);
		put("FNNNN-64NNN", FPU_Part);
		put("FNNNN-60NNN", Assembly);
		put("FNNNN-7NNNN", Packing);
		put("FNNNN-81NNN", Packing);
		put("FNNNN-82NNN", Packing);
		put("FNNNN-83NNN", Packing);
		put("FNNNN-61NNN", PCA);
	}};
	
	
	public L10EBOMImportDialog(AbstractAIFUIApplication app, Shell parentShell) {
		super(parentShell);
		this.app = app;
		this.session = (TCSession) this.app.getSession();
		this.parentShell = parentShell;
		reg = Registry.getRegistry("com.foxconn.electronics.prtL10ebom.prtL10ebom");
		
		parseTools = new ExcelParseTools();
		
		infoStyle = new StyleRange();
		infoStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN);
		
		warnStyle = new StyleRange();				
		warnStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_YELLOW);
		
		errorStyle = new StyleRange();				
		errorStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_RED);
		
		selectFolder = getSelectFolder(); // 获取当前选中的文件夹
		initUI();
	}
	
	
	private void initUI() {
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE);
		shell.setSize(700, 600);
		shell.setText(reg.getString("L10EBOMImport.TITLE"));
		shell.setLayout(new GridLayout(1, false));
		TCUtil.centerShell(shell);
		
		Image image = getDefaultImage();
		if (CommonTools.isNotEmpty(image)) {
			shell.setImage(image);
		}
		
		GridData topData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		topData.grabExcessHorizontalSpace = true;
		
		Composite topComposite = new Composite(shell, SWT.NONE);
		topComposite.setLayout(new GridLayout(7, false));
		topComposite.setLayoutData(topData);
		
		projectLabel = new Label(topComposite, SWT.NONE);
		projectLabel.setText(reg.getString("ProjectCombo.LABEL"));	
		
		GridData projectGridDataText = new GridData();
		projectGridDataText.widthHint = 280;
		
		projectCombo = new Combo(topComposite, SWT.NONE);
		projectCombo.setLayoutData(projectGridDataText);		
		
		projectCombo.setEnabled(false); // 设置专案下拉框不可以编辑
//		setProjectComboEnable(false); // 设置专案下拉框不可以编辑
		
		sheetLabel = new Label(topComposite, SWT.NONE);
		sheetLabel.setText(reg.getString("SheetCombo.LABEL"));		
		
		GridData sheetGridDataText = new GridData();
		sheetGridDataText.widthHint = 100;	
		
		sheetCombo = new Combo(topComposite, SWT.READ_ONLY); // 定义一个只读的下拉框
		sheetCombo.setLayoutData(sheetGridDataText);
		
		
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
		
//		statusBar = new Composite(shell, SWT.NONE);
//		statusBar.setLayout(new GridLayout(2, false));
//		statusBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		status = new Label(statusBar, SWT.NONE);
//		status.setText(reg.getString("ProgressBar1.TITLE"));
//		status.setVisible(false);
		
//		progressComposite = new Composite(shell, SWT.NONE);
		
		statusBar = new Composite(shell, SWT.NONE);
		statusBar.setLayout(new GridLayout(2, false));
		statusBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		status = new Label(statusBar, SWT.NONE);		
		
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
		status.setVisible(false); // 界面加载设置不显示	
		
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
	
	
	private void setUpStatusBar(String statusName) {		
		if (!statusBar.isDisposed()) {
			status.setText(statusName);
			status.setVisible(true);
		}
		
		progressBar = new ProgressBar(statusBar, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressBar.setMaximum(100);
		
		statusBar.layout();

	}

	
	private void updateProgressBarByLoadSheet() {

		new Thread(new Runnable() {
			private int progress = 0;
			private static final int INCREMENT = 10;
			private boolean flag = false; // 数据接口获取成功与否接口
			
			@Override
			public void run() {
				new Thread() {
					public void run() {	
						FileInputStream fis = null;
						try {
							fis = new FileInputStream(filePath);
							workbook = WorkbookFactory.create(fis);
							
							List<String> sheetList = parseTools.getSheetNameList(workbook);
									
							if (CommonTools.isEmpty(sheetList)) {
								TCUtil.errorMsgBox(reg.getString("SheetLoadingErr.MSG"), reg.getString("ERROR.MSG"));
								return;
							}
							
							sheetComboList = new ArrayList<String>(sheetList.size() + 1); 
							
							sheetComboList.addAll(sheetList);
							sheetComboList.add(0, "--- 请选择 ---");	
							
							setComboValues(sheetLabel, sheetCombo, sheetComboList, 0); // 设置sheet页名称下拉值
							
//							Map<String, TCComponentProject> projectMap = getProjectList();
//							if (CommonTools.isEmpty(projectMap)) {
//								TCUtil.errorMsgBox(reg.getString("ProjectLoadingErr.MSG"), reg.getString("ERROR.MSG"));
//							} else {
//								projectComboList = projectMap.keySet().stream().collect(Collectors.toList());
//								projectComboList.add(0, "--- 请选择 ---");
//							}							
//							
//							setComboValues(projectLabel, projectCombo, projectComboList, 0);							
						} catch (Exception e) {
							e.printStackTrace();
						} finally {			
							if (CommonTools.isNotEmpty(fis)) {
								try {
									fis.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}			
							if (CommonTools.isNotEmpty(workbook)) {
								try {
									workbook.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}		
							
						}
						
						flag = true;						 						
					}
				}.start();
				
				while (progressBar != null && !progressBar.isDisposed()) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							if (flag) {
								if (!shell.isDisposed()) {
									status.setVisible(false);					    		
									filePathText.setText(filePath);
						    		progressBar.dispose(); // 销毁进度条组件
						    		resetFlag(true, false); // 设置选择按钮可以编辑,专案不可下拉框,Sheet下拉框可以编辑
						    						
								}								
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
	
	private void updateProgressBarByLoadProject() {
		new Thread(new Runnable() {
			private int progress = 0;
			private static final int INCREMENT = 10;
			private boolean flag = false; // 数据接口获取成功与否接口
			
			@Override
			public void run() {
				new Thread() {
					public void run() {	
						FileInputStream fis = null;
						int index = 0;
						String projectName = "";
						TCComponentProject assignProject = null;
						TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 当前选中的Excel文件路径为: ") + filePath + " ************", styledText, infoStyle);
						try {
							fis = new FileInputStream(filePath);
							workbook = WorkbookFactory.create(fis);						
							
							TCUtil.writeWarnLog(S2TTransferUtil.toTraditionnalString("************ 当前选中的sheet名称为:") + selectSheetComboText + " ************", styledText, infoStyle);
							Sheet sheet = workbook.getSheet(selectSheetComboText);
							
							TCUtil.writeInfoLog("====================================", styledText, infoStyle);	
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始校验sheet页 ************"), styledText, infoStyle);
							if (!parseTools.checkSheetTemplate(sheet)) {
								TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("************ 当前选中的sheet,模板不符合要求************"), styledText, errorStyle);
								throw new Exception(S2TTransferUtil.toTraditionnalString("************ 执行終止 ************"));
							}
							
							TCUtil.writeInfoLog("====================================", styledText, infoStyle);								
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始获取顶层料号 ************"), styledText, infoStyle);
							List<CellBean> colIndexList = parseTools.getColIndex(sheet, null);
							
							String rootPartNum = parseTools.getRootPartNum(workbook, sheet, colIndexList);
							
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 获取顶层料号完成 ************"), styledText, infoStyle);
							
							TCComponentItem item = TCUtil.findItem(rootPartNum); // 查询获取根物料对象
							
							TCUtil.writeInfoLog("====================================", styledText, infoStyle);								
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始获取物料" + rootPartNum + "指派的专案 ************"), styledText, infoStyle);
							
							TCComponent[] tcComponents = getAssignProject(item);
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 获取物料" + rootPartNum + "指派的专案完成 ************"), styledText, infoStyle);
							
							
							if (CommonTools.isNotEmpty(tcComponents)) {
								assignProject = (TCComponentProject) tcComponents[0];
								projectName = assignProject.getProjectID() + "-" + assignProject.getProjectName();
							}
							
							TCUtil.writeInfoLog("====================================", styledText, infoStyle);								
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始获取当前用户参与的专案 ************"), styledText, infoStyle);
							projectMap = getProjectList();
							if (CommonTools.isEmpty(projectMap)) {
								TCUtil.errorMsgBox(reg.getString("ProjectLoadingErr.MSG"), reg.getString("ERROR.MSG"));
								projectMap = new LinkedHashMap<String, TCComponentProject>();
							} 
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 获取当前用户参与的专案完成 ************"), styledText, infoStyle);
							
							
							if (CommonTools.isNotEmpty(assignProject) && !projectMap.containsValue(assignProject)) {
								projectMap.put(projectName, assignProject);
							}
							
							projectComboList = projectMap.keySet().stream().collect(Collectors.toList());
							
							
							projectComboList.add(0, "--- 请选择 ---");
							
							index = getIndex(projectComboList, projectName) == -1 ? 0 : getIndex(projectComboList, projectName);													
							
							setComboValues(projectLabel, projectCombo, projectComboList, index);	
							
							if (index != 0) {
								setSuccessFlag(true);								
							} else {
								setSuccessFlag(false);
							}
						} catch (Exception e) {
							e.printStackTrace();
							setSuccessFlag(false); // 设置标识为导入失败
						} finally {			
							if (CommonTools.isNotEmpty(fis)) {
								try {
									fis.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}			
							if (CommonTools.isNotEmpty(workbook)) {
								try {
									workbook.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}		
							
						}
						
						flag = true;						 						
					}
				}.start();
				
				while (progressBar != null && !progressBar.isDisposed()) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							if (flag) {
								if (!shell.isDisposed()) {
									status.setVisible(false);					    		
						    		progressBar.dispose(); // 销毁进度条组件					    		
						    		
						    		setImportFlag(isSuccessFlag());
									resetFlag(true, true); // 设置选择按钮,专案下拉框,Sheet下拉框可以编辑															    						
								}								
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
	
	
	
 	private void updateProgressBarByImport(String filePath) {
		new Thread(new Runnable() {
			private int progress = 0;
			private static final int INCREMENT = 10;
			private boolean finish = false; // 导入完成标识
			
			@Override
			public void run() {
				
				new Thread() {
					public void run() {
						TCUtil.setBypass(session);
						FileInputStream fis = null;
						setImportFlag(false);	
						resetFlag(false, true);
						isRunning = true;
						TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 当前导入的Excel文件路径为: ") + filePath + " ************", styledText, infoStyle);
						TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始执行本次导入************"), styledText, infoStyle);
						try {
							fis = new FileInputStream(filePath);
							workbook = WorkbookFactory.create(fis);
							
//							TCUtil.writeWarnLog(S2TTransferUtil.toTraditionnalString("************ 当前选中的sheet名称为:") + selectSheetComboText + " ************", styledText, infoStyle);
							Sheet sheet = workbook.getSheet(selectSheetComboText);
							
//							TCUtil.writeInfoLog("====================================", styledText, infoStyle);	
//							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始校验sheet页 ************"), styledText, infoStyle);
//							if (!parseTools.checkSheetTemplate(sheet)) {
//								TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("************ 当前选中的sheet,模板不符合要求************"), styledText, errorStyle);
//								throw new Exception(S2TTransferUtil.toTraditionnalString("************ 导入終止 ************"));
//							}
//							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 校验sheet页完成************"), styledText, infoStyle);
							
							
							TCUtil.writeInfoLog("====================================", styledText, infoStyle);								
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始解析sheet页内容 ************"), styledText, infoStyle);
							List<CellBean> colIndexList = parseTools.getColIndex(sheet, null);
							
							List<String> skuList = parseTools.getSKUList(sheet, colIndexList);
							if (CommonTools.isEmpty(skuList)) {
								TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("************ 当前sheet页SKU单元格全部为空,无法执行下一步动作  ************"), styledText, errorStyle);
								throw new Exception(S2TTransferUtil.toTraditionnalString("************ 导入終止 ************"));
							}
							
							List<CellBean> qtyIndexList = parseTools.getColIndex(sheet, skuList);
							if (CommonTools.isEmpty(qtyIndexList)) {
								TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("************ 当前sheet页SKU没有匹配到Qty单元格,无法执行下一步  ************"), styledText, errorStyle);
								throw new Exception(S2TTransferUtil.toTraditionnalString("************ 导入終止 ************"));
							}
							
							Map<String, List<PartQtyBean>> retMap = new LinkedHashMap<String, List<PartQtyBean>>();							
							List<String> errorList = new ArrayList<String>();
							List<BOMBean> totalList = new ArrayList<BOMBean>();
							
							parseTools.getPartBeanList(workbook, sheet, colIndexList, qtyIndexList, retMap, errorList);		
							showErrLog(errorList);
							
							parseTools.parseExcel(workbook, sheet, colIndexList, qtyIndexList, retMap, totalList,errorList);							
							showErrLog(errorList);
							
							if (CommonTools.isEmpty(totalList)) {
								TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("************ 当前sheet页内容为空,无法执行下一步  ************"), styledText, errorStyle);
								throw new Exception(S2TTransferUtil.toTraditionnalString("************ 导入終止 ************"));
							}
							
							totalList = mergeQty(totalList); // 合并父子图号相同的用量
							List<BOMBean> filterList = totalList.stream().filter(CommonTools.distinctByKey(bean -> bean.getSelfMaterialBean().getPartNumber())).collect(Collectors.toList());
							
//							Collections.sort(filterList);
							
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 完成解析sheet页内容 ************"), styledText, infoStyle);							
							
							TCUtil.writeInfoLog("====================================", styledText, infoStyle);							
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始导入物料 ************"), styledText, infoStyle);
							createItem(filterList, totalList);
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 物料导入完成 ************"), styledText, infoStyle);
							
							TCUtil.writeInfoLog("====================================", styledText, infoStyle);	
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始搭建BOM ************"), styledText, infoStyle);
//							List<BOMBean> rootList = totalList.stream().filter(bean -> CommonTools.isEmpty(bean.getSelfMaterialBean().getParentNumber())).collect(Collectors.toList());
							List<BOMBean> createList = totalList.stream().filter(bean -> CommonTools.isNotEmpty(bean.getSelfMaterialBean().getParentNumber())).collect(Collectors.toList());
							
							Map<String, List<BOMBean>> groupByParentNum = groupByParentNum(totalList);
							
							Map<String, List<TCComponentItemRevision>> map = createBOM(createList, groupByParentNum);
							
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ BOM搭建完成 ************"), styledText, infoStyle);
							
//							TCUtil.writeInfoLog("====================================", styledText, infoStyle);	
//							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始设置BOM用量 ************"), styledText, infoStyle);
//							setQty(rootList, totalList);
//							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ BOM用量设置完成 ************"), styledText, infoStyle);
							
							TCUtil.writeInfoLog("====================================", styledText, infoStyle);	
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始设置料号版本与3D图档版本关联 ************"), styledText, infoStyle);
							setRelation(filterList);
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 设置料号版本与3D图档版本关联完成 ************"), styledText, infoStyle);
							
							
							TCUtil.writeInfoLog("====================================", styledText, infoStyle);	
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始创建DCN ************"), styledText, infoStyle);
							TCComponentItem DCNItem = createDCN();							
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 创建DCN完成 ************"), styledText, infoStyle);
							
							TCUtil.writeInfoLog("====================================", styledText, infoStyle);	
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始为DCN添加解决方案项和问题项物料 ************"), styledText, infoStyle);
							if (addSolutionOrProblemItem(filterList, DCNItem, map)) {
								TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ DCN添加解决方案项和问题项物料完成 ************"), styledText, infoStyle);
							} else {
								throw new Exception(S2TTransferUtil.toTraditionnalString("************ DCN添加解决方案项和问题项物料发生错误 ************"));
							}
							
							TCUtil.writeInfoLog("====================================", styledText, infoStyle);	
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始为DCN添加数据集文件 ************"), styledText, infoStyle);
							
							addDataset(DCNItem);
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ DCN添加数据集完成 ************"), styledText, infoStyle);
							
							
							TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 本次导入完成************"), styledText, infoStyle);
							
						} catch (Exception e) {
							e.printStackTrace();
							setSuccessFlag(false); // 设置标识为导入失败
							TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("************ 本次导入发生错误 ************"), styledText, errorStyle);							
							TCUtil.writeErrorLog(TCUtil.getExceptionMsg(e), styledText, errorStyle);	
						} finally {
							if (CommonTools.isNotEmpty(fis)) {
								try {
									fis.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}			
							if (CommonTools.isNotEmpty(workbook)) {
								try {
									workbook.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
							
							setImportFlag(!isSuccessFlag());
							
							resetFlag(true, true);							
							
							finish = true;
							isRunning = false;
							TCUtil.closeBypass(session); // 关闭旁路
						}
						
					}
				}.start();
				
				while (progressBar != null && !progressBar.isDisposed()) {
					Display.getDefault().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							if (finish) {
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
	
 	/**
 	 * 获取料号指派的专案
 	 * @param rootPartNum
 	 * @return
 	 * @throws TCException
 	 */
 	private TCComponent[] getAssignProject(TCComponent item) throws TCException {
 		if (CommonTools.isEmpty(item)) {
			return null;
		}
		TCComponent[] projects = item.getRelatedComponents(Project_list);
		if (CommonTools.isNotEmpty(projects)) {
			return projects;
		}
		
		return null;
 	}
 	
 	
	private void showErrLog(List<String> errorList ) throws Exception {
		if (CommonTools.isNotEmpty(errorList)) {
			for (String str : errorList.stream().filter(CommonTools.distinctByKey(str -> str)).collect(Collectors.toList())) {
				TCUtil.writeErrorLog(str, styledText, errorStyle);
			}			
			throw new Exception(S2TTransferUtil.toTraditionnalString("************ 导入終止 ************"));
		}
	}
		
 	private void addListener() {
		chooseBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				filePath = TCUtil.openFileChooser(shell);
				if (CommonTools.isEmpty(filePath)) {
					return;
				}
				
				setUpStatusBar(reg.getString("ProgressBar1.TITLE"));
				updateProgressBarByLoadSheet();
				
				resetFlag(false, true); // 设置选择，专案下拉框，Sheet下拉框不可以编辑
				setImportFlag(false); // 设置导入按钮不可点击
			}
		});
		
		importButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String selectFilePath = filePathText.getText();
				setUpStatusBar(reg.getString("ProgressBar3.TITLE"));
				updateProgressBarByImport(selectFilePath);
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
	
		sheetCombo.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent e) {
				Combo source = (Combo) e.getSource();
				selectSheetComboText = source.getText();
				System.out.println("==>> 当前Sheet页下拉框选中值为: " + source.getText());
				if (!selectSheetComboText.equals(sheetComboList.get(0)) && CommonTools.isNotEmpty(filePath)) { // 判断当前下拉框值是否为请选择并且文件路径不为空					
//					importButton.setEnabled(true);
					setUpStatusBar(reg.getString("ProgressBar2.TITLE"));
					updateProgressBarByLoadProject(); // 加载获取专案信息
					resetFlag(false, true);
				} 
				
//				else if (selectSheetComboText.equals(sheetComboList.get(0)) || CommonTools.isEmpty(filePath)) {
//					importButton.setEnabled(false);
//				}
			}
		});		
		
		
		projectCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo source = (Combo) e.getSource();
				selectProjectComboText = source.getText();
				System.out.println("==>> 当前专案下拉框选中值为: " + source.getText());
				if (!selectSheetComboText.equals(sheetComboList.get(0)) && !selectProjectComboText.equals(projectComboList.get(0)) 
						&& CommonTools.isNotEmpty(filePath)) {					
					importButton.setEnabled(true);
				} else {
					importButton.setEnabled(false);
				}
			}
			
		});
		
		styledText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				styledText.setTopIndex(styledText.getLineCount() - 1);
			}
		});
		
		projectCombo.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				int keyCode = e.keyCode;
				if(keyCode!=13 && keyCode != 16777296) {
					return;
				}
				Combo source = (Combo) e.getSource();
				String filter = source.getText().toLowerCase();;
				System.out.println("==>> filter: " + filter);
				int selectionIndex = source.getSelectionIndex();
				
				String[] dataList = (String[]) source.getItems();
				source.removeAll();
				for (String item : dataList) {
					if(item.toLowerCase().contains(filter)) {
						source.add(item);
					}
				}
				
				if (source.getItemCount() > 0) {
                    if (selectionIndex != -1) {
                    	source.select(selectionIndex);                    	
                    } else {
                    	source.select(0);
                    }
                    
                    selectProjectComboText = source.getText();
                	System.out.println("==>> selectProjectComboText: " + selectProjectComboText);
                	
                	setImportFlag(true);
                }
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
			}
		});
 	}
	
 	/**
 	 * 创建零件
 	 * @param list
 	 * @throws Exception
 	 */
 	private void createItem(List<BOMBean> list, List<BOMBean> totalList) throws Exception {
 		for (BOMBean bean : list) {	
 			try {
 				TCComponentItemRevision itemRev = null;
 				MaterialBean matBean = bean.getSelfMaterialBean();
 				String partNum = matBean.getPartNumber();
 				Map<String, Object> ootbMap = new HashMap<String, Object>();
 				Map<String, String> itemRevCustMap = new HashMap<String, String>();
 				Map<String, String> itemCustMap = new HashMap<String, String>();
 				String itemType = getItemType(partNum, ItemMap, true);
 				getPropMap(matBean, ootbMap, itemRevCustMap, itemCustMap);
 				if (D9_FinishedPart.equals(itemType)) { // 判断料号类型是否为成品料号
 					itemRevCustMap.put(CustomerPN, matBean.getSku());
				}
 				String foldName = getItemType(partNum, FolderMap, false);
 				TCComponent matchFolder = getMatchTCComponent(selectFolder, foldName, Contents);
 				if (CommonTools.isEmpty(matchFolder)) {
 					matchFolder = TCUtil.createFolder(session, Folder, foldName);
 					selectFolder.add(Contents, matchFolder);				
 				}
 				
 				TCComponentItem item = TCUtil.findItem(partNum);
 				if (CommonTools.isNotEmpty(item)) {
 					getMatchItemRevision(item, matBean, itemRevCustMap); 
// 					Map<String, Object> map = getMatchItemRevision(item, matBean, itemRevCustMap); 					
// 					itemRev = (TCComponentItemRevision) map.get("itemRev");
// 					MaterialBean afMatBean = (MaterialBean) map.get("matBean");
// 					bean.setSelfMaterialBean(afMatBean);
// 					TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,料号: " + partNum + ", 物料已经存在TC中,无需创建"), styledText, infoStyle);
 				} else {
 					List<Map<String, Object>> createList = new ArrayList<Map<String,Object>>();
 					createList.add(ootbMap);
 					TCComponent[] createObjects = TCUtil.createObjects(session, itemType, createList);
 					if (CommonTools.isEmpty(createObjects)) {
 						TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,料号: " + partNum + ",物料创建失败"), styledText, errorStyle);
 						continue;
 					} 

 					TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,料号: " + partNum + ",物料创建成功"), styledText, infoStyle);
 					
 					item = (TCComponentItem) createObjects[0]; 
 					itemRev = item.getLatestItemRevision();
 					itemRev.setProperties(itemRevCustMap);
 					
 					matBean.setAddSolutionItemRev(itemRev); 					
 				}
 				
 				if (!item.getProperty(Uom_tag).equals(matBean.getUom_tag())) {
 					item.setProperties(itemCustMap);
				}
				
 				TCComponent[] tcComponents = getAssignProject(item); // 获取对象指派的专案集合
 				TCComponentProject selectProject = projectMap.get(selectProjectComboText);
 				if (CommonTools.isEmpty(selectProject)) {
 					TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("当前选中的专案名: " + selectProjectComboText + ", 查找不到专案"), styledText, infoStyle);
				} else {
					if (!checkAssignProject(tcComponents, selectProject)) {
	 					try {
	 						selectProject.assignToProject(new TCComponent[] {item});
	 						TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,料号: " + partNum + ", 指派" + selectProjectComboText+ "专案成功"), styledText, infoStyle);
						} catch (Exception e) {
							e.printStackTrace();
							setSuccessFlag(false); // 设置标识为导入失败
							TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,料号: " + partNum + "指派" + selectProjectComboText + "专案失败," + TCUtil.getExceptionMsg(e)), styledText, errorStyle);
						}
						
					} else {
						TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,料号: " + partNum + ", 已经指派" + selectProjectComboText+ "专案，无需重复指派"), styledText, infoStyle);
					}
				}
 				
 				
 				for (BOMBean e : totalList) {
 					MaterialBean selfMaterialBean = e.getSelfMaterialBean();
					if (selfMaterialBean.getParentNumber().equals(partNum)) {
						selfMaterialBean.setParentItem(item);
//						selfMaterialBean.setParentItemRev(itemRev);
					}
					
					if (selfMaterialBean.getPartNumber().equals(partNum)) {
						selfMaterialBean.setPartItem(item);						
//						selfMaterialBean.setPartItemRev(itemRev);
					}
				}
 				
 				
 				int length = CommonTools.isEmpty(matchFolder.getRelatedComponents(Contents)) ? 0 : matchFolder.getRelatedComponents(Contents).length;
// 				TCComponent[] relatedComponents = matchFolder.getRelatedComponents(Contents);
// 				if (CommonTools.isNotEmpty(relatedComponents)) {
// 					length = relatedComponents.length;
// 				}
 				
 				if (CommonTools.isEmpty(getMatchTCComponent(matchFolder, partNum, Contents))) {
 					matchFolder.add(Contents, item, length); // 将对象追加到文件夹末尾				
 				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception(S2TTransferUtil.toTraditionnalString("第 " + bean.getSelfMaterialBean().getIndex() + " 行发生错误,错误信息如下:"+ e));
			}
 			
		}
 	}

 	/**
 	 * 返回匹配成功的对象版本
 	 * @param relatedComponents
 	 * @param matBean
 	 * @return
 	 * @throws Exception 
 	 */
 	private void getMatchItemRevision(TCComponentItem item, MaterialBean matBean, Map<String, String> itemRevCustMap) throws Exception {
// 		Map<String, Object> retMap = new HashMap<String, Object>(); 		
 		TCComponentItemRevision itemRev = null;
 		item.refresh();
 		if (matBean.getPartNumber().equals("FC231-87006")) {
			System.out.println(123);
		}
 		List<TCComponent> relatedList = new ArrayList<TCComponent>(Arrays.asList(item.getRelatedComponents(Revision_list)));
 		Collections.reverse(relatedList); // 反转list集合元素
 		Optional<TCComponent> findAny = relatedList.stream().filter(component -> {
 			try {
				MaterialBean oldBean = tcPropMapping(new MaterialBean(), component);
				if (!checkPropChange(oldBean, matBean)) { // 判断属性是否发生更改
					return true;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
 			return false;
 		}).findAny();
 		
 		if (findAny.isPresent()) {
 			itemRev = (TCComponentItemRevision) findAny.get(); // 代表属性没有发生改变
 			if (!TCUtil.isReleased(itemRev)) { //  属性没有发生变化,进一步判断是否已经发行
				matBean.setAddSolutionItemRev(itemRev);
				if (relatedList.size() > 1) {
					for (int i = 1; i < relatedList.size(); i++) {
						TCComponentItemRevision released = (TCComponentItemRevision) relatedList.get(i);
						if (TCUtil.isReleased(released)) {
							matBean.setAddProblemItemRev(released);
							break;
						}
					}					
				}				
			}
 			
// 			retMap.put("itemRev", itemRev);
 			TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,料号: " + matBean.getPartNumber() + ", 物料已经存在TC中,无需创建"), styledText, infoStyle);
		} else { // 代表属性发生更改
			itemRev = item.getLatestItemRevision();
			if (TCUtil.isReleased(itemRev)) { // 判断最大版本是否已经发布
				TCComponentItemRevision newItemRev = TCUtil.doRevise(itemRev); // 进行升版
				newItemRev.setProperties(itemRevCustMap);
				matBean.setAddProblemItemRev(itemRev);				
				matBean.setAddSolutionItemRev(newItemRev);
//				retMap.put("itemRev", newItemRev);
				
				TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,料号: " + matBean.getPartNumber() + ", 升版成功"), styledText, infoStyle);
//				return newItemRev;
			} else {
				itemRev.setProperties(itemRevCustMap);
				matBean.setAddSolutionItemRev(itemRev);	
//				retMap.put("itemRev", itemRev);
				TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,料号: " + matBean.getPartNumber() + ", 物料已经存在TC中,无需创建"), styledText, infoStyle);
			}
		} 
// 		retMap.put("matBean", matBean);
// 		return retMap;
 	}
 	
 	/**
 	 * 判断专案是否已经指派
 	 * @param tcComponents
 	 * @param selectProject
 	 * @return
 	 */
 	private boolean checkAssignProject(TCComponent[] tcComponents, TCComponentProject selectProject) {
 		if (CommonTools.isEmpty(tcComponents)) {
			return false;
		}
 		
 		return Stream.of(tcComponents).anyMatch(component -> {
 			return component.getUid().equals(selectProject.getUid());
 		});
 	}
 	
 	/**
 	 * 合并父子图号相同的用量
 	 * @param list
 	 * @return
 	 */
 	private List<BOMBean> mergeQty(List<BOMBean> list) {
 		Map<String, List<BOMBean>> groupByMap = groupByParentAndPart(list);
 		for (Map.Entry<String, List<BOMBean>> entry: groupByMap.entrySet()) { 			
			List<BOMBean> value = entry.getValue();
			String totalQty = getTotalQty(value);
			for (BOMBean bean : value) {
				bean.setQty(totalQty);
			}
		}
 		
 		return list.stream().filter(CommonTools.distinctByKey(bean -> bean.getSelfMaterialBean().getParentNumber() + bean.getSelfMaterialBean().getPartNumber())).collect(Collectors.toList()); // 去重父子图号相同的记录
 	}
 	
 	
 	private Map<String, List<BOMBean>> groupByParentAndPart(List<BOMBean> list) {
 		Map<String, List<BOMBean>> resultMap = new LinkedHashMap<String, List<BOMBean>>();
 		list.forEach(bean -> {
			List<BOMBean> dtbomInfoList = resultMap.get(bean.getSelfMaterialBean().getParentNumber() 
					+ "-" + bean.getSelfMaterialBean().getPartNumber());
			if (dtbomInfoList == null) {
				dtbomInfoList = new ArrayList<BOMBean>();
				dtbomInfoList.add(bean);
				resultMap.put(bean.getSelfMaterialBean().getParentNumber() 
						+ "-" + bean.getSelfMaterialBean().getPartNumber(), dtbomInfoList);
			} else {
				dtbomInfoList.add(bean);
			}
		});
 		
 		return resultMap;
 	}
 	
 	private Map<String, List<BOMBean>> groupByParentNum(List<BOMBean> list) {
 		Map<String, List<BOMBean>> resultMap = new LinkedHashMap<String, List<BOMBean>>();
 		list.forEach(bean -> {
			List<BOMBean> dtbomInfoList = resultMap.get(bean.getSelfMaterialBean().getParentNumber());
			if (dtbomInfoList == null) {
				dtbomInfoList = new ArrayList<BOMBean>();
				dtbomInfoList.add(bean);
				resultMap.put(bean.getSelfMaterialBean().getParentNumber(), dtbomInfoList);
			} else {
				dtbomInfoList.add(bean);
			}
		});
 		
 		return resultMap;
 	}
 	
 	
 	/**
 	 * 获取总Qty
 	 * @param list
 	 * @return
 	 */
 	private String getTotalQty(List<BOMBean> list) {
 		float totalQty = 0;
 		for (BOMBean bean : list) {
 			String qtyStr = bean.getQty();
 			if (CommonTools.isNotEmpty(qtyStr)) {
 				float qty = Float.parseFloat(qtyStr);
 				totalQty = totalQty + qty;
 			}
		}
 		
 		return new BigDecimal(String.valueOf(totalQty)).stripTrailingZeros().toPlainString();
 	}
 	
 	
 	/**
 	 * 创建BOM
 	 * @param list
 	 * @throws Exception
 	 */
  	private Map<String, List<TCComponentItemRevision>> createBOM(List<BOMBean> list, Map<String, List<BOMBean>> map) throws Exception {
  		Map<String, List<TCComponentItemRevision>> retMap = new HashMap<String, List<TCComponentItemRevision>>();
  		List<TCComponentItemRevision> problemList = new ArrayList<TCComponentItemRevision>();
  		List<TCComponentItemRevision> solutionList = new ArrayList<TCComponentItemRevision>();
  		
 		MaterialBean matBean = null;
 		String parentNum = null;
 		String partNum = null;
 		for (BOMBean bean : list) {
 			TCComponentBOMWindow window = null;
 			TCComponentBOMLine topLine = null;
			try {
				TCComponentItemRevision parentItemRev = null;
				TCComponentItemRevision partItemRev = null;
				matBean = bean.getSelfMaterialBean();
				parentNum = matBean.getParentNumber();
 				partNum = matBean.getPartNumber(); 				
 				String qty = bean.getQty();
 	 			
 				if (partNum.equals("FC233-46005")) {
					System.out.println(123);
				}
				parentItemRev = matBean.getParentItem().getLatestItemRevision();
 				if (CommonTools.isEmpty(parentItemRev)) {
 					TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,上阶料号: " + parentNum + ",在TC中不存在"), styledText, errorStyle);
 					continue;
				} 				
 				
				partItemRev = matBean.getPartItem().getLatestItemRevision();
 				
				

 				window = TCUtil.createBOMWindow(session); // 创建BOMWindow
 				
				topLine = TCUtil.getTopBomline(window, parentItemRev); // 发送到结构管理器
				
				if (TCUtil.isReleased(parentItemRev)) { // 判断父阶料号是否已经发布
					List<BOMBean> list2 = map.get(parentNum);
					if (checkBOMChange(topLine, list2)) { // 判断BOM是否发生改变
						TCComponentItemRevision newParentItemRev = TCUtil.doRevise(parentItemRev); // 对父阶料号进行升版
						topLine = TCUtil.getTopBomline(window, newParentItemRev); // 将升版后的父对象发送到结构管理器
						problemList.add(parentItemRev);
						solutionList.add(newParentItemRev);
						
						cutBOMLine(topLine, list2); // 执行是否需要删除BOMLine
					}					
				}

				TCComponentBOMLine bomLine = checkBOMLineExist(topLine, partItemRev); // 判断BOMLine是否已经存在
				if (CommonTools.isEmpty(bomLine)) {
					bomLine = topLine.add(partItemRev.getItem(), partItemRev, null, false);
					if (!TCUtil.isReleased(partItemRev)) { // 判断是否已经发行
						TCComponentItem item = partItemRev.getItem();
						List<TCComponent> relatedList = new ArrayList<TCComponent>(Arrays.asList(item.getRelatedComponents(Revision_list))); // 反转对象版本
						solutionList.add(partItemRev);
						if (relatedList.size() > 1) {
							for (int i = 1; i < relatedList.size(); i++) {
								TCComponentItemRevision released = (TCComponentItemRevision) relatedList.get(i);
								if (TCUtil.isReleased(released)) {
									problemList.add(released);
									break;
								}
							}					
						}
					}
					
					TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,上阶料号: " + parentNum + ", 添加料号: " + partNum + ", 成功"), styledText, infoStyle);
					
				} else {					
					TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,上阶料号: " + parentNum + ", 已经存在料号: " + partNum + ", 无需重复添加"), styledText, infoStyle);
				}
				
				if (CommonTools.isNotEmpty(qty)) {
					try {
						bomLine.setProperty(BL_quantity, qty);
					} catch (Exception e) {
						e.printStackTrace();
						setSuccessFlag(false); // 设置标识为导入失败
						TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,料号: " + parentNum + "设置用量失败," + TCUtil.getExceptionMsg(e)), styledText, errorStyle);
					}					
				}
			} catch (Exception e) {
				e.printStackTrace();
				setSuccessFlag(false); // 设置标识为导入失败
				throw new Exception(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,上阶料号: " + parentNum + ", 料号: " + partNum + ",发生错误,错误信息如下:" + e));
			} finally {
				if (CommonTools.isNotEmpty(window)) {
					window.save();
					window.close();
				}
			}
		} 	
 		
 		retMap.put(CMHasProblemItem, problemList);
 		retMap.put(CMHasSolutionItem, solutionList);
 		return retMap;
 	}
 	
 	
  	private boolean checkBOMChange(TCComponentBOMLine topLine, List<BOMBean> list) throws TCException {
  		topLine.refresh();
  		AIFComponentContext[] children = topLine.getChildren();
  		if (CommonTools.isEmpty(children)) {
			return false;
		}
  		
  		if (CommonTools.isEmpty(list)) {
  			return false;
		}
  		
  		if (children.length != list.size()) { // 如果父子条目不一致，代表发生改变,也需要变更
			return true;
		}
  		
  		MaterialBean matBean = null;
  		for (BOMBean bean : list) {
  			matBean = bean.getSelfMaterialBean();
  			String qty = bean.getQty();
  			TCComponentItemRevision itemRev = matBean.getPartItem().getLatestItemRevision();
  			
  			boolean anyMatch = Stream.of(children).anyMatch(e -> {
  				try {
					TCComponentBOMLine bomLine = (TCComponentBOMLine) e.getComponent();
					if (bomLine.getItemRevision().getUid().equals(itemRev.getUid())) { // 判断物料是否已经存在
						String bomQtyStr = bomLine.getProperty(BL_quantity);
						if (CommonTools.isNotEmpty(bomQtyStr)) {
							Integer bomQty = Integer.valueOf(new BigDecimal(bomQtyStr).stripTrailingZeros().toPlainString());
							bomQtyStr = String.valueOf(bomQty);
						}
												
						if (qty.equals(bomQtyStr)) { // 判断用量是否一致
							return true;
						}
					}
					
					return false;
				} catch (Exception e1) {
					e1.printStackTrace();
					throw new RuntimeException(e1);					
				}  				
  			});
  			
  			if (!anyMatch) { // true代表匹配成功，没有发生变化，false代表用量发生变更或者物料属于新增
				return true;
			}
  			
		}
  		
  		return false;
  	}
  	
  	
  	/**
  	 * 剪切BOMLine
  	 * @param topLine
  	 * @param list
  	 * @throws TCException
  	 */
  	private void cutBOMLine(TCComponentBOMLine topLine, List<BOMBean> list) throws TCException {
  		topLine.refresh();
  		AIFComponentContext[] children = topLine.getChildren();
  		
  		if (CommonTools.isEmpty(children)) {
			return;
		}
  		
  		if (CommonTools.isEmpty(list)) {
  			return;
		}
  		
  		for (AIFComponentContext context : children) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
			
			boolean anyMatch = list.stream().anyMatch(bean -> {
  				try {
  					MaterialBean matBean = bean.getSelfMaterialBean();
  					TCComponentItemRevision itemRev = matBean.getPartItem().getLatestItemRevision();
					if (itemRev.getUid().equals(bomLine.getItemRevision().getUid())) { // 判断物料是否已经存在												
						return true;
					}					
					return false;
				} catch (Exception e1) {
					e1.printStackTrace();
					throw new RuntimeException(e1);					
				}  				
  			});
			
			if (!anyMatch) { // 如果没有匹配成功，则需要将BOMLine此行给移除
				bomLine.cut();
			}
		}
  	}
  	
  	
 	/**
 	 * 设置用量
 	 * @param rootList
 	 * @param totalList
 	 * @throws TCException
 	 */
 	private void setQty(List<BOMBean> rootList, List<BOMBean> totalList) throws TCException {
 		MaterialBean matBean = null;
 		for (BOMBean bean : rootList) {
 			TCComponentBOMWindow window = null;
 			TCComponentBOMLine topLine = null;
 			TCComponentItemRevision itemRev = null;
 			try {
 				matBean = bean.getSelfMaterialBean();
 				itemRev = matBean.getPartItem().getLatestItemRevision();
 				
 				window = TCUtil.createBOMWindow(session);
 				
 				topLine = TCUtil.getTopBomline(window, itemRev);
 				
 				TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 开始设置料号:" + matBean.getPartNumber() + " BOM子阶用量 ************"), styledText, infoStyle);
 				recurseBOMLine(topLine, totalList);
 				TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("************ 料号:" + matBean.getPartNumber() + "设置BOM子阶用量完成 ************"), styledText, infoStyle);
			} catch (Exception e) {
				e.printStackTrace();
				TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("************ 料号:" + matBean.getPartNumber() + "设置BOM子阶用量出现错误,错误信息如下: ************" + TCUtil.getExceptionMsg(e)), styledText, errorStyle);
			} finally {
				if (CommonTools.isNotEmpty(window)) {
					window.save();
					window.close();
				}
			}
 		}
 	}
 	
 	private void recurseBOMLine(TCComponentBOMLine topLine, List<BOMBean> totalList) throws TCException {
 		TCComponentBOMLine parent = topLine.parent();
 		if (parent != null) {
 			TCComponentItemRevision parentItemRev = (TCComponentItemRevision) parent.getRelatedComponent(BL_line_object);
 			TCComponentItemRevision itemRev = (TCComponentItemRevision) topLine.getRelatedComponent(BL_line_object);
 			Optional<BOMBean> findAny = totalList.stream().filter(e -> {
 				MaterialBean selfMaterialBean = e.getSelfMaterialBean(); 				
 				try {
					return selfMaterialBean.getParentNumber().equals(parentItemRev.getProperty(ItemId)) && selfMaterialBean.getPartNumber().equals(itemRev.getProperty(ItemId));
				} catch (TCException e1) {
					throw new RuntimeException(e1);
				}
 				
// 				TCComponentItemRevision partItemRev = selfMaterialBean.getPartItemRev();
// 				if (CommonTools.isNotEmpty(partItemRev)) {
//					try {
//						return partItemRev.getProperty(ItemId).equals(itemRev.getProperty(ItemId));
//					} catch (TCException e1) {
//						throw new RuntimeException(e1);
//					}
//				}
// 				return false;
 			}).findAny();
 			
 			if (findAny.isPresent()) {
				BOMBean bomBean = findAny.get();
				MaterialBean matBean = bomBean.getSelfMaterialBean();
				String qty = bomBean.getQty();
				if (CommonTools.isNotEmpty(qty)) {
					try {
						topLine.setProperty(BL_quantity, qty);
					} catch (Exception e) {
						e.printStackTrace();
						
						TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,料号: " + matBean.getPartNumber() + "设置用量失败," + TCUtil.getExceptionMsg(e)), styledText, errorStyle);
					}					
				}				
			}
		}
 		
 		AIFComponentContext[] children = topLine.getChildren();
 		if (CommonTools.isNotEmpty(children)) {
 			for (AIFComponentContext e : children) {
 				TCComponentBOMLine bomLine = (TCComponentBOMLine) e.getComponent();
 				recurseBOMLine(bomLine, totalList);
			} 			
		}
 	}
 	
 	/**
 	 * 设置关联关系
 	 * @param list
 	 * @throws Exception
 	 */
 	private void setRelation(List<BOMBean> list) throws Exception {
 		MaterialBean matBean = null;
 		for (BOMBean bean : list) {
 			List<String> queryNames = new ArrayList<>();
 			List<String> queryValues = new ArrayList<>();
 			matBean = bean.getSelfMaterialBean(); 			
 			
 			String partNum = matBean.getPartNumber();
 			TCComponentItemRevision partItemRev = matBean.getPartItem().getLatestItemRevision();
 			
 			if (CommonTools.isEmpty(partNum)) {
 				TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行, 料号为空"), styledText, errorStyle);
 				continue;
			}
 			
 			if (CommonTools.isEmpty(partItemRev)) {
				TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行,料号: " + partNum + ",在TC中不存在"), styledText, errorStyle);
				continue;
			}
 			
 			String draw3dRev = matBean.getDraw3DRev();
 			String draw2dRev = matBean.getDraw2DRev();
 			
 			queryNames.add(D9_BUPN);
 			queryValues.add(partNum);
 			
 			if (CommonTools.isNotEmpty(draw3dRev)) {
 				queryNames.add(D9_3DREV);
 				queryValues.add(draw3dRev);
 			}
 			if (CommonTools.isNotEmpty(draw2dRev)) {
 				queryNames.add(D9_2DREV);
 				queryValues.add(draw2dRev);
 			} 			
 			
 			TCComponent[] results = TCUtil.executeQuery(session, D9_FIND_DESIGNREV, queryNames.toArray(new String[0]), queryValues.toArray(new String[0]));
 			if (CommonTools.isEmpty(results)) {
 				TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行, 料号: " + partNum +" 没有查询到3D图档版本"), styledText, errorStyle);
 				continue;
			}
 			
 			for (TCComponent tcComponent : results) {
 				if (!checkPartItemRev((TCComponentItemRevision)tcComponent, partItemRev, REPRESENTATION_FOR)) {
 					try { 						
 						partItemRev.add(TC_IS_REPRESENTED_BY, tcComponent);					
 						TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行, 料号: " + partNum +" 关联3D图档版本成功"), styledText, infoStyle);
					} catch (Exception e) {
						e.printStackTrace();
						setSuccessFlag(false); // 设置标识为导入失败
						TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行, 料号: " + partNum +" 关联3D图档版本失败,发生错误,错误信息如下:" + TCUtil.getExceptionMsg(e)), 
								styledText, errorStyle);
					}					
				} else {
					TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("第 " + matBean.getIndex() + " 行, 料号: " + partNum +" 已经关联3D图档版本,无需重复关联"), styledText, infoStyle);
				}
			}
 		}
 	}
 	
 	
 	/**
 	 * 创建DCN
 	 * @throws Exception
 	 */
 	private TCComponentItem createDCN() throws Exception {
 		TCComponentItem DCNItem = null;
 		String dcnItemId = null;
 		String dcnVersion = null;
 		TCComponent matchFolder = getMatchTCComponent(selectFolder, DCN_FOLDER, Contents);
 		if (CommonTools.isEmpty(matchFolder)) {
			matchFolder = TCUtil.createFolder(session, Folder, DCN_FOLDER);
			selectFolder.add(Contents, matchFolder);
		}
 		
 		String[] split = selectProjectComboText.split("-");
 		StringBuilder sb = new StringBuilder();
 		if (split.length > 1) {
			for (int i = 1; i < split.length; i++) {
				sb.append(split[i]);
				if (i == split.length - 1) {
					break;
				}
				sb.append("-");
			}
		} 		
 		
 		int length = CommonTools.isEmpty(matchFolder.getRelatedComponents(Contents)) ? 0 : matchFolder.getRelatedComponents(Contents).length;
 		
 		String itemName = sb.toString() + " EBOM Change";
 		TCComponent matchTCComponent = getMatchTCComponent(matchFolder, itemName, Contents); 		
		if (CommonTools.isEmpty(matchTCComponent) || TCUtil.isReleased(((TCComponentItem)matchTCComponent).getLatestItemRevision())) {	// DCN 不存在或者DCN已经发行		
 			DCNItem = TCUtil.createItem(TCUtil.generateId(session, "DCN-", PRT_DCN), PRT_DCN, itemName, "NN", PRT_DCN + "Revision");
 			matchFolder.add(Contents, DCNItem, length);
		} else {
			DCNItem = (TCComponentItem) matchTCComponent;
		}
 		
		
		dcnItemId = DCNItem.getProperty(ItemId);
		dcnVersion = DCNItem.getLatestItemRevision().getProperty(ItemRevisionId);		
		
		TCComponentProject selectProject = projectMap.get(selectProjectComboText);
		TCComponent[] tcComponents = getAssignProject(DCNItem); // 获取DCN对象指派的专案集合
		if (!checkAssignProject(tcComponents, selectProject)) {
			try {
				selectProject.assignToProject(new TCComponent[] {DCNItem}); // DCN对象指派专案
				TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("DCN编号为: " + dcnItemId + " 版本号为: " + dcnVersion + ", 指派" + selectProjectComboText+ "专案成功"), styledText, infoStyle);
			} catch (Exception e) {
				e.printStackTrace();
				setSuccessFlag(false); // 设置标识为导入失败
				throw new Exception(S2TTransferUtil.toTraditionnalString("DCN编号为: " + dcnItemId + " 版本号为: " + dcnVersion + "指派" + selectProjectComboText + "专案失败, 错误信息如下:" + e));
			}
		}  else {
			TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("DCN编号为: " + dcnItemId + " 版本号为: " + dcnVersion + ", 已经指派" + selectProjectComboText+ "专案，无需重复指派"), styledText, infoStyle);
		}
		
		return DCNItem;
 	}
 	
 	
 	/**
 	 * 添加对象到解决方案项或问题项
 	 * @param list
 	 * @param DCNItem
 	 * @throws TCException
 	 */
 	private boolean addSolutionOrProblemItem(List<BOMBean> list, TCComponentItem DCNItem, Map<String, List<TCComponentItemRevision>> map) throws TCException {
 		String dcnItemId = null;
 		String dcnVersion = null;
 		String addProblemItemId = null;
 		String addProblemVersion = null;
 		String addSolutionItemId = null;
 		String addSolutionVersion = null;
 		
 		TCComponentItemRevision DCNItemRev = DCNItem.getLatestItemRevision(); 		
 		dcnItemId = DCNItemRev.getProperty(ItemId);
		dcnVersion = DCNItemRev.getProperty(ItemRevisionId);
		boolean addFlag = true;
 		for (BOMBean bean : list) {
		
			MaterialBean matBean = bean.getSelfMaterialBean();
			TCComponentItemRevision addProblemItemRev = matBean.getAddProblemItemRev();
			TCComponentItemRevision addSolutionItemRev = matBean.getAddSolutionItemRev();
			
			if (CommonTools.isNotEmpty(addProblemItemRev)) {
				addProblemItemId = addProblemItemRev.getProperty(ItemId);
				addProblemVersion = addProblemItemRev.getProperty(ItemRevisionId);
				
				addFlag = addItemByRelationName(DCNItemRev, addProblemItemRev, dcnItemId, dcnVersion, addProblemItemId, addProblemVersion, CMHasProblemItem);
			}
			
			if (CommonTools.isNotEmpty(addSolutionItemRev)) {
				addSolutionItemId = addSolutionItemRev.getProperty(ItemId);
				addSolutionVersion = addSolutionItemRev.getProperty(ItemRevisionId);
				
				addFlag = addItemByRelationName(DCNItemRev, addSolutionItemRev, dcnItemId, dcnVersion, addSolutionItemId, addSolutionVersion, CMHasSolutionItem);				
			}
			
			if (!addFlag) { // 代表添加失败
				deleteDCN(DCNItemRev);
				return false;
			}
		}
 		
 		List<TCComponentItemRevision> problemList = map.get(CMHasProblemItem);
 		
 		if (CommonTools.isNotEmpty(problemList)) {
 			for (TCComponentItemRevision e : problemList) {
 				addProblemItemId = e.getProperty(ItemId);
 				addProblemVersion = e.getProperty(ItemRevisionId);
				addFlag = addItemByRelationName(DCNItemRev, e, dcnItemId, dcnVersion, addProblemItemId, addProblemVersion, CMHasProblemItem);
				if (!addFlag) { // 代表添加失败
					deleteDCN(DCNItemRev);
					return false;
				}
			}			
		}
 		
 		List<TCComponentItemRevision> solutionList = map.get(CMHasSolutionItem);
 		if (CommonTools.isNotEmpty(solutionList)) {
			for (TCComponentItemRevision e : solutionList) {
				addSolutionItemId = e.getProperty(ItemId);
				addSolutionVersion = e.getProperty(ItemRevisionId);
				
				addFlag = addItemByRelationName(DCNItemRev, e, dcnItemId, dcnVersion, addSolutionItemId, addSolutionVersion, CMHasSolutionItem);
				if (!addFlag) { // 代表添加失败
					deleteDCN(DCNItemRev);
					return false;
				}
			}
		}
 		return true;
 	}
 	
 	
 	/**
 	 * 添加数据集
 	 * @param DCNItemRev
 	 * @throws TCException
 	 */
 	private void addDataset(TCComponentItem DCNItem) throws TCException {
 		TCComponentItemRevision DCNItemRev = DCNItem.getLatestItemRevision();
 		String dcnItemId = DCNItemRev.getProperty(ItemId);
		String dcnVersion = DCNItemRev.getProperty(ItemRevisionId);
 		String dsName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
 		String type = "";
 		String refName = "";
 		if (dsName.endsWith(DatasetEnum.MSExcelX.fileExtensions())) {
			type = DatasetEnum.MSExcelX.type();
			refName = DatasetEnum.MSExcelX.refName();
		} else if (dsName.endsWith(DatasetEnum.MSExcel.fileExtensions())) {
			type = DatasetEnum.MSExcel.type();
			refName = DatasetEnum.MSExcel.refName();
		}
 		
 		if (!checkDataset(DCNItemRev, dsName)) {
 			TCComponentDataset createDataSet = TCUtil.createDataSet(session, filePath, type, dsName, refName);
 			DCNItemRev.add("IMAN_specification", createDataSet);
 			TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("DCN编号为: " + dcnItemId + " 版本号为: " + dcnVersion + ", 添加数据集 " + dsName+ ", 成功"), styledText, infoStyle);
 		} else {
			TCUtil.updateDataset(session, DCNItemRev, "IMAN_specification", filePath);
			TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("DCN编号为: " + dcnItemId + " 版本号为: " + dcnVersion + ", 更新数据集 " + dsName+ ", 成功"), styledText, infoStyle);
		}
 		
 	}
 	
	/**
	 * 校验数据集是否存在
	 * @param dsName
	 * @return
	 * @throws TCException
	 */
	private boolean checkDataset(TCComponentItemRevision itemRev, String dsName) throws TCException {
		itemRev.refresh();
		TCComponent[] relatedComponents = itemRev.getRelatedComponents("IMAN_specification");
		boolean anyMatch = Stream.of(relatedComponents).anyMatch(e -> {
			try {
				System.out.println(e.getProperty("object_name"));
				return e.getProperty("object_name").equals(dsName);
			} catch (TCException e1) {
				e1.printStackTrace();
			}
			return false;
		});
		return anyMatch;
	}
	
	
 	/**
 	 * 通过管理添加对象
 	 * @param DCNItemRev
 	 * @param itemRev
 	 * @param dcnItemId
 	 * @param dcnVersion
 	 * @param itemId
 	 * @param version
 	 * @param propName
 	 * @throws TCException
 	 */
 	private boolean addItemByRelationName(TCComponentItemRevision DCNItemRev, TCComponentItemRevision itemRev, String dcnItemId, String dcnVersion, String itemId, String version, String propName) throws TCException {
 		int length = CommonTools.isEmpty(DCNItemRev.getRelatedComponents(propName)) ? 0 : DCNItemRev.getRelatedComponents(propName).length;
 		
// 		TCComponent[] relatedComponents = DCNItemRev.getRelatedComponents(propName);
// 		if (CommonTools.isNotEmpty(relatedComponents)) {
//			length = relatedComponents.length;
//		}
 		
 		
 		if (!checkPartItemRev(DCNItemRev, itemRev, propName)) { 			
 			try {
				DCNItemRev.add(propName, itemRev, length);
				if (propName.equals(CMHasProblemItem)) {
					TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("DCN编号为:  " + dcnItemId + " 版本号为: " + dcnVersion + ", 添加物料对象ID为:" + itemId + ", 版本号为:" + version + "到DCN问题项成功"), styledText, infoStyle);
				} else if (propName.equals(CMHasSolutionItem)) {
					TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("DCN编号为:  " + dcnItemId + " 版本号为: " + dcnVersion + ", 添加物料对象ID为:" + itemId + ", 版本号为:" + version + "到DCN解决方案项成功"), styledText, infoStyle);
				}
			} catch (Exception e) {
				e.printStackTrace();
				setSuccessFlag(false); // 设置标识为导入失败
				if (propName.equals(CMHasProblemItem)) {
					TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("DCN编号为:  " + dcnItemId + " 版本号为: " + dcnVersion + ", 添加物料对象ID为:" + itemId + ", 版本号为:" + version + "到DCN问题项失败,错误信息如下:" + e), styledText, errorStyle);
				} else if (propName.equals(CMHasSolutionItem)) {
					TCUtil.writeErrorLog(S2TTransferUtil.toTraditionnalString("DCN编号为:  " + dcnItemId + " 版本号为: " + dcnVersion + ", 添加物料对象ID为:" + itemId + ", 版本号为:" + version + "到DCN解决方案项成功,错误信息如下:" + e), styledText, errorStyle);
				}
				return false;
			}
 		} else {
 			if (propName.equals(CMHasProblemItem)) {
				TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("DCN编号为:  " + dcnItemId + " 版本号为: " + dcnVersion + ", 添加物料对象ID为:" + itemId + ", 版本号为:" + version + "已经添加到DCN问题项,无需重复添加"), styledText, infoStyle);
			} else if (propName.equals(CMHasSolutionItem)) {
				TCUtil.writeInfoLog(S2TTransferUtil.toTraditionnalString("DCN编号为:  " + dcnItemId + " 版本号为: " + dcnVersion + ", 添加物料对象ID为:" + itemId + ", 版本号为:" + version + "已经添加到DCN解决方案项,无需重复添加"), styledText, infoStyle);
			}
		}
 		
 		return true;
 	}
 	
 	private void deleteDCN(TCComponentItemRevision DCNItemRev) throws TCException {
 		TCComponentItem DCNItem = DCNItemRev.getItem();
 		TCComponent[] problemComponents = DCNItemRev.getRelatedComponents(CMHasProblemItem);
 		if (CommonTools.isNotEmpty(problemComponents)) {
			DCNItemRev.remove(CMHasProblemItem, problemComponents); // 移除问题项对象
		}
 		
 		TCComponent[] solutionComponents = DCNItemRev.getRelatedComponents(CMHasSolutionItem);
 		if (CommonTools.isNotEmpty(solutionComponents)) {
			DCNItemRev.remove(CMHasSolutionItem, solutionComponents); // 移除解决方案项对象
		}
 		
 		AIFComponentContext[] whereReferenced = DCNItem.whereReferencedByTypeRelation(new String[] {"Folder"}, null); // 查找所有引用此DCN的文件夹
 		if (CommonTools.isNotEmpty(whereReferenced)) { 			
			for (AIFComponentContext relate : whereReferenced) {
				TCComponent tcComponent = (TCComponent) relate.getComponent();
				String objectName = tcComponent.getProperty(ObjectName);
				if (objectName.equals(DCN_FOLDER)) {
					tcComponent.remove(Contents, DCNItem);
				}
			}
		}
 		
 		DCNItem.delete(); // 删除DCN
 	}
 	
 	
 	
 	/**
 	 * 校验dest对象是否和dest通过propName关系关联过
 	 * @param designItemRev
 	 * @param partItemRev
 	 * @return
 	 * @throws TCException
 	 */
 	private boolean checkPartItemRev(TCComponentItemRevision sourceItemRev, TCComponentItemRevision destItemRev, String propName) throws TCException {
 		boolean anyMatch = false;
 		sourceItemRev.refresh();
 		TCComponent[] relatedComponents = sourceItemRev.getRelatedComponents(propName);
 		if (CommonTools.isNotEmpty(relatedComponents)) {
			anyMatch = Stream.of(relatedComponents).anyMatch(e -> {
				return e.getUid().equals(destItemRev.getUid());
			});			
		} 		
 		return anyMatch;
 	}
 	
 	
 	/**
 	 * 判断BOM是否已经存在
 	 * @param componentContext
 	 * @param itemRev
 	 * @return
 	 * @throws TCException 
 	 */
 	private TCComponentBOMLine checkBOMLineExist(TCComponentBOMLine topLine, TCComponentItemRevision itemRev) throws TCException {
 		topLine.refresh();
 		AIFComponentContext[] children = topLine.getChildren();
 		if (CommonTools.isEmpty(children)) {
			return null;
		}
 		
 		Optional<AIFComponentContext> findAny = Stream.of(children).filter(e -> {
 			try {
 				TCComponentBOMLine bomLine = (TCComponentBOMLine) e.getComponent();
 				TCComponentItemRevision relatedComponent = (TCComponentItemRevision) bomLine.getRelatedComponent(BL_line_object);
 				return relatedComponent.getProperty(ItemId).equals(itemRev.getProperty(ItemId));
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
 		}).findAny();
 		
 		if (findAny.isPresent()) {
			return (TCComponentBOMLine) findAny.get().getComponent();
		}
 		
 		return null;
 	}
 	
 	
 	private TCComponent getMatchTCComponent(TCComponent tcComponent, String name, String propName) throws TCException {
		tcComponent.refresh();
		TCComponent[] relatedComponents = tcComponent.getRelatedComponents(propName);
		Optional<TCComponent> findAny = Stream.of(relatedComponents).filter(component -> {
			try {				
				if (component instanceof TCComponentFolder) {					
					TCComponentFolder folder = (TCComponentFolder)component;
					return name.equalsIgnoreCase(folder.getProperty(ObjectName));
				} else if (PRT_DCN.equals(component.getTypeObject().getName()) ) {
					return name.equalsIgnoreCase(component.getProperty(ObjectName));
				} else if (component instanceof TCComponentItem) {
					TCComponentItem item = (TCComponentItem) component;
					return name.equalsIgnoreCase(item.getProperty(ItemId));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
			return false;
		}).findAny();
		
		if (findAny.isPresent()) {
			return findAny.get();
		}
		
		return null;
	}
 	
 	
 	/**
 	 * 创建零组件
 	 * @param bean
 	 * @param ootbMap
 	 * @param custMap
 	 * @throws IllegalArgumentException
 	 * @throws IllegalAccessException
 	 */
 	private void getPropMap(MaterialBean bean, Map<String, Object> ootbMap, Map<String, String> itemRevCustMap, Map<String, String> itemCustMap) throws IllegalArgumentException, IllegalAccessException {
 		System.out.println("start -->>  getTCPropMap");
 		Field[] fields = bean.getClass().getDeclaredFields();
		List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
		fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
		for (Field field : fieldList) {
			field.setAccessible(true);
			TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
			String tcPropName = tcProp.tcProperty();
			Object o = field.get(bean);
			if (tcPropName.startsWith(D9_Prefix)) {				
				itemRevCustMap.put(tcPropName, (String) o);
			} else {
				if (tcPropName.startsWith(Uom_Prefix)) {
					itemCustMap.put(tcPropName, (String) o);
				} else {
					ootbMap.put(tcPropName, o);
				}				
				
			}
		}		
 	}
 	
 	/**
 	 * 获取对象类型
 	 * @param itemId
 	 * @param index
 	 * @return
 	 */
 	private String getItemType(String itemId, Map<String, String> map, boolean createFlag) {
 		String value = null;
 		String codeRule = "";
 		String[] split = itemId.split("-");
 		String str1 = split[0];
 		String str2 = split[1];
 		
 		str1 = str1.replace(str1.subSequence(1, 5), "NNNN");
 		str2 = str2.replace(str2.subSequence(2, 5), "NNN");
 		codeRule = str1 + "-" + str2;
 		value = map.get(codeRule);
 		if (CommonTools.isEmpty(value)) {
 			str2 = str2.replace(str2.subSequence(1, 5), "NNNN");
 			codeRule = str1 + "-" + str2;
 			value = map.get(codeRule);
 			if (CommonTools.isEmpty(value)) {
 				if (createFlag) {
 					value = D9_CommonPart;
				} else {
					value = Others;
				} 				
			}
		} 
 		
 		return value; 		
 	} 	
 	
 	
 	/**
 	 * 获取当前选中的文件夹
 	 * @return
 	 */
 	private TCComponentFolder getSelectFolder() {
 		InterfaceAIFComponent[] targetComponent = app.getTargetComponents();
 		return (TCComponentFolder) targetComponent[0];
 	}

	private void setImportFlag(boolean flag) {
		Display.getDefault().syncExec(() -> {
//			isRunning = flag;
			if (!importButton.isDisposed()) {
				importButton.setEnabled(flag);
			}
		});
	}
	
	private void resetFlag(boolean flag, boolean setProjectFlag) {
		Display.getDefault().syncExec(() -> {
			if (!chooseBtn.isDisposed()) {
				chooseBtn.setEnabled(flag);
			}
			
			if (!projectCombo.isDisposed() && setProjectFlag) {
				projectCombo.setEnabled(flag);
			}
			
			if (!sheetCombo.isDisposed()) {
				sheetCombo.setEnabled(flag);
			}
		});		
	}	
	
	
	
	private void setComboValues(Label label, Combo combo, List<String> list, int index) {
		Display.getDefault().syncExec(() -> {
			combo.removeAll(); // 清除所有的下拉值
			for (int i = 0; i < list.size(); i++) {
				combo.add(list.get(i));
			}
			
			if (label.getText().equals(reg.getString("SheetCombo.LABEL"))) {
				selectSheetComboText = list.get(index);
			} else if (label.getText().equals(reg.getString("ProjectCombo.LABEL"))) {
				selectProjectComboText = list.get(index);
			}
			
			combo.select(index); // 设置当前项
		});		
	}	
	
	/**
	 * 判断属性是否发生改变
	 * @param oldBean
	 * @param newBean
	 * @return true代表发生更改，false代表没有发生改变
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static boolean checkPropChange(MaterialBean oldBean, MaterialBean newBean) throws IllegalArgumentException, IllegalAccessException {
		if (oldBean != null && newBean != null) {
			Field[] oldFields = oldBean.getClass().getDeclaredFields();
			Field[] newFields = newBean.getClass().getDeclaredFields();
			
			List<Field> oldFieldList = new ArrayList<Field>(Arrays.asList(oldFields));
			List<Field> newFieldList = new ArrayList<Field>(Arrays.asList(newFields));
			
			oldFieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
			oldFieldList.removeIf(field -> !field.getAnnotation(TCPropertes.class).tcProperty().startsWith(D9_Prefix));
			
			newFieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
			newFieldList.removeIf(field -> !field.getAnnotation(TCPropertes.class).tcProperty().startsWith(D9_Prefix));
			
			for (int i = 0; i < oldFieldList.size(); i++) {
				Field oldField = oldFieldList.get(i);
				Field newField = newFieldList.get(i);
				
				oldField.setAccessible(true);
				newField.setAccessible(true);
				
				String oldvalue = oldField.get(oldBean).toString();
				String newValue = newField.get(newBean).toString();
				
				if (!oldvalue.equals(newValue)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	public static <T> T tcPropMapping(T bean, TCComponent tcComponent) throws IllegalArgumentException, IllegalAccessException, TCException {
		if (bean != null && tcComponent != null) {
			Field[] fields = bean.getClass().getDeclaredFields();
			List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
			fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
			fieldList.removeIf(field -> !field.getAnnotation(TCPropertes.class).tcProperty().startsWith(D9_Prefix));
			for (Field field : fieldList) {
				field.setAccessible(true);
				TCPropertes tcPropName = field.getAnnotation(TCPropertes.class);
				if (tcPropName != null) {
					String propertyName = tcPropName.tcProperty();
					if (CommonTools.isNotEmpty(propertyName)) {
						if (tcComponent.isValidPropertyName(propertyName)) { // 判断属性是否存在							
							field.set(bean, tcComponent.getProperty(propertyName));
						} else {
							System.out.println(propertyName + " propertyName is not exist ");
							bean = null;
							continue;
						}
					}	
				}
			}
		}
		
		return bean;
	}
	
	/**
	 * 获取当前用户所参与的专案
	 * @return
	 * @throws TCException
	 */
	private Map<String,TCComponentProject> getProjectList() throws TCException {
		TCComponentUser user = session.getUser();
		TCComponentProjectType projectType = (TCComponentProjectType) session.getTypeComponent(ITypeName.TC_Project);
		TCComponentProject[] projects = projectType.extent(user, true);
		if (CommonTools.isNotEmpty(projects)) {
			return Stream.of(projects).collect(Collectors.toMap(e -> e.getProjectID() + "-" + e.getProjectName(), e -> e));
		}
		
		return null;
	}
	
	/**
	 * 返回下拉框的索引
	 * @param list
	 * @param selectValue
	 * @return
	 */
	private int getIndex(List<String> list, String value) {
		return IntStream.range(0, list.size()).filter(i -> Objects.equal(list.get(i), value)).findFirst().orElse(-1);
	}
	
	
	public boolean isSuccessFlag() {
		return successFlag;
	}


	public void setSuccessFlag(boolean successFlag) {
		this.successFlag = successFlag;
	}
	
	
}
