package com.foxconn.electronics.managementebom.Import.bom.mnt.dialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.foxconn.electronics.managementebom.Import.bom.mnt.constants.ConstantsEnum;
import com.foxconn.electronics.managementebom.Import.bom.mnt.constants.MntMissPartSheetConstant;
import com.foxconn.electronics.managementebom.Import.bom.mnt.constants.PreferenceConstant;
import com.foxconn.electronics.managementebom.Import.bom.mnt.constants.SearchConstant;
import com.foxconn.electronics.managementebom.Import.bom.mnt.domain.MntEBOMInfo;
import com.foxconn.electronics.managementebom.Import.bom.mnt.domain.MntSearchBean;
import com.foxconn.electronics.managementebom.Import.bom.mnt.excel.ExcelEBOMAnalyseTools;
import com.foxconn.electronics.managementebom.export.ExcelUtil;
import com.foxconn.electronics.util.CommonTools;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

public class MntEBOMImportDialog extends Dialog {

	private Shell shell = null;
	private Shell parentShell = null;
	private TCSession session = null;
	private Button chooseButton = null;
	private Button importButton = null;
	private Button stopButton = null;
	private Text filePathText = null;
	private StyledText styledText = null;
	public StyleRange infoStyle = null;
	public StyleRange warnStyle = null;
	public StyleRange errorStyle = null;
	private List<MntEBOMInfo> sheetDataList = null;
	private List<String> importItemTypeList = null;
	private List<MntSearchBean> notExistList = null;
	private Registry reg = null;
	private Integer itemNo = 1;
	private TCComponentFolder homeFolder = null;
	private boolean stopFlag = false; // 导入停止标识 ， 默认设置为false，不停止
	private boolean closeFlag = true; // Dialog是否可以关闭的标识，默认为可以关闭
	private MntEBOMImportDialog dialog = null;
	
	public MntEBOMImportDialog(Shell parentShell, TCSession session) {
		super(parentShell);
		this.parentShell = parentShell;
		this.session = session;
		reg = Registry.getRegistry("com.foxconn.electronics.managementebom.managementebom");
		importItemTypeList = getImportItemTypeList();
		if (CommonTools.isEmpty(importItemTypeList)) {
			TCUtil.warningMsgBox(reg.getString("preferenceName.MSG") + PreferenceConstant.D9_MNT_EBOM_IMPORT_ITEM_TYPE
					+ reg.getString("preferenceErr.MSG"), reg.getString("WARNING.MSG"));
			return;
		}
		
		
		infoStyle = new StyleRange();				
		infoStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN);
		
		
		warnStyle = new StyleRange();				
		warnStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_YELLOW);
		
		
		errorStyle = new StyleRange();				
		errorStyle.foreground = SWTResourceManager.getColor(SWT.COLOR_DARK_RED);
		
		initUI(); // 界面初始化
		
	}

	/**
	 * 獲取MNT EBOM導入TC對象類型的首選項值
	 */
	private List<String> getImportItemTypeList() {
		return TCUtil.getArrayPreference(TCPreferenceService.TC_preference_site, PreferenceConstant.D9_MNT_EBOM_IMPORT_ITEM_TYPE);
	}

	private TCComponentFolder getHomeFolder() throws TCException {
		return session.getUser().getHomeFolder();
	}

	private void initUI() {
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(550, 600);
		shell.setText(reg.getString("MNTEBOMImportShell.TITLE"));
		shell.setLayout(new GridLayout(1, false));
		TCUtil.centerShell(shell);

		Image image = getDefaultImage();
		if (CommonTools.isNotEmpty(image)) {
			shell.setImage(image);
		}

		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.grabExcessHorizontalSpace = true;

		Composite composite1 = new Composite(shell, SWT.NONE);
		composite1.setLayout(new GridLayout(2, false));
		composite1.setLayoutData(gridData);

		filePathText = new Text(composite1, SWT.SINGLE | SWT.BORDER);
		filePathText.setEnabled(false); // 设置不可编辑
		filePathText.setLayoutData(gridData);

		chooseButton = new Button(composite1, SWT.NONE);
		chooseButton.setText(reg.getString("chooseBtn.LABEL"));

		GridData gridData1 = new GridData(GridData.FILL_BOTH);
		
//		logText = new Text(shell, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
//		logText.setLayoutData(gridData1);		 
//		logText.setEditable(false);
//		logText.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));; // 設置字體顏色		
		
		styledText = new StyledText(shell, SWT.READ_ONLY | SWT.MULTI |  SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
//		styledText.setEditable(false);
		styledText.setLayoutData(gridData1);
		
		
		MyActionGroup actionGroup = new MyActionGroup(styledText, reg);
		actionGroup.fillContextMenu(new MenuManager());

		Composite composite2 = new Composite(shell, SWT.NONE);
		composite2.setLayout(new GridLayout(2, true));
		composite2.setLayoutData(gridData);

		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalAlignment = GridData.END;

		stopButton = new Button(composite2, SWT.NONE);
		stopButton.setText(reg.getString("stopBtn.LABEL"));
		stopButton.setLayoutData(gridData2);
		stopButton.setEnabled(false);

		importButton = new Button(composite2, SWT.NONE);
		importButton.setText(reg.getString("importBtn.LABEL"));
		importButton.setEnabled(false); // 设置为不可以点击		
		
		addListener(); // 添加事件监听
		
		dialog = this;
		
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
	 * 添加事件监听
	 */
	private void addListener() {

		chooseButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				String filePath = TCUtil.openFileChooser(shell);
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
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							com.foxconn.mechanism.util.TCUtil.setBypass(session); // 開啟旁路
							stopFlag = false; // 将停止标识设置为false
							closeFlag = false; // 将可以关闭窗体的标识设置为false
							updateImportBtnStates(false); // 更新导入按钮状态为不可点击
							updateStopBtnStates(true); // 更新停止按钮为可以点击
							writeInfoLogText("當前解析EBOM Excel文件的路徑為: " + filePath);
							ExcelEBOMAnalyseTools excelEBOMAnalyseTools = new ExcelEBOMAnalyseTools(styledText, dialog);
							sheetDataList = excelEBOMAnalyseTools.analyseEBOMExcel(filePath);
							if (CommonTools.isEmpty(sheetDataList)) {
								writeErrorLogText("解析EBOM Excel文件發生異常，請檢查EBOM文件，然後進行操作");
								throw new Exception("解析EBOM Excel文件發生異常");
							}
							if (stopFlag) {
								writeErrorLogText("************ 此次EBOM導入已終止 ************");								
								throw new Exception("導入終止");
							}
							
							notExistList = new ArrayList<MntSearchBean>();							
							writeInfoLogText("********** 開始在Teamcenter中查找以下P/N 編號對象是否存在 **********");
							itemNo = 1 ; // 每次執行查找對象前，先將編號設置為1
							batchCheckItem(sheetDataList); // 批量查找对象是否在TC中存在
							writeInfoLogText("********** 結束在Teamcenter中查找以下P/N 編號對象是否存在 **********");
							if (stopFlag) {
								writeErrorLogText(" ************ 此次EBOM導入已終止 ************");
								throw new Exception("導入終止");
							}
							
							if (CommonTools.isNotEmpty(notExistList)) { // 代表存在有P/N 號在TC中不存在對象
								String missPartFilePath = generateMissPartList(filePath);
								writeErrorLogText("當前在Teamcenter中不存在的P/N 編號清單路徑為: " + missPartFilePath);
								throw new Exception("存在P/N 編號對象在Teamcenter中不存在");
							}
							
							writeInfoLogText(" ************ 開始導入EBOM ************");
							if (generateEBOM()) { // MNT物料構建EBOM
								writeInfoLogText(" ************ 此次EBOM導入已完成 ************");
							} else {
								writeErrorLogText(" ************ 此次EBOM導入已終止 ************");
							}														
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							updateImportBtnStates(true); // 恢复按钮可以点击状态
							updateStopBtnStates(false); // 恢复停止按钮为不可点击状态
							closeFlag = true; // 将可以关闭窗体的标识设置为true
							com.foxconn.mechanism.util.TCUtil.closeBypass(session); // 关闭旁路
						}						
					}

				}).start();
			}
		});

		stopButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				stopFlag = true; // 将停止标识设置为true	
				closeFlag = true; // 将可以关闭窗体的标识设置为true
			}
		});

		shell.addShellListener(new ShellAdapter() {

			public void shellClosed(ShellEvent e) {
				if (closeFlag) {
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
				} else {
//					TCUtil.warningMsgBox(reg.getString("warnBox.MSG"), reg.getString("warnBox.TITLE"));
					MessageBox warnBox = new MessageBox(shell, SWT.ICON_WARNING);
					warnBox.setText(reg.getString("warnBox.TITLE"));
					warnBox.setMessage(reg.getString("warnBox.MSG"));
					warnBox.open();
					e.doit = false;
				}				
			}
		});

	}

	/**
	 * MNT物料構建EBOM
	 * 
	 * @param MNTEBOMFolder
	 * @param foldName
	 */
	private boolean generateEBOM() {
		boolean flag = true;
		for (int i = 0; i < sheetDataList.size(); i++) {
			if (stopFlag) {				
				updateImportBtnStates(true); // 设置导入按钮为可以点击
				updateStopBtnStates(false); // 恢复停止按钮为不可点击状态
				flag = false;
				break;
			}
			MntEBOMInfo rootBean = sheetDataList.get(i);
			writeInfoLogText("************ 開始導入第 " + (i + 1) + " 個EBOM結構 " + " ************");

			writeInfoLogText("********** 開始查找Item **********");
			batchFindItem(rootBean); // 批量查找item
			writeInfoLogText("********** 查找Item結束 **********");

			writeInfoLogText("********** 開始創建EBOM **********");
			createOrAddEBOMStruct(rootBean); // 开始创建EBOM
			writeInfoLogText("********** 創建EBOM結束 **********");

			writeInfoLogText("************ 第 " + (i + 1) + " 個EBOM結構導入完成 " + " ************");
		}
		return flag;
	}

	/**
	 * 判斷文件件是否存在
	 * 
	 * @param foldName
	 * @return
	 * @throws Exception
	 */
	private TCComponentFolder checkFoldExist(String foldName) {
		TCComponentFolder folder = null;
		try {
			List<String> queryNames = new ArrayList<>();
			List<String> queryValues = new ArrayList<>();
			queryNames.add(SearchConstant.OBJECT_NAME);
			queryValues.add(foldName);
			System.out.println("find folder param: " + foldName);
			TCComponent[] results = TCUtil.executeQuery(session, SearchConstant.FIND_FOLDER,
					queryNames.toArray(new String[0]), queryValues.toArray(new String[0]));
			if (CommonTools.isEmpty(results)) {
				return null;
			}
			for (TCComponent tcComponent : results) {
				if (!(tcComponent instanceof TCComponentFolder)) {
					continue;
				}
				folder = (TCComponentFolder) tcComponent;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			writeErrorLogText(TCUtil.getExceptionMsg(e));
		}
		return folder;
	}

	/**
	 * 批量查找对象是否在TC中存在
	 * 
	 * @param list
	 */
	private void batchCheckItem(List<MntEBOMInfo> list) {
		list.forEach(bean -> {
			try {
				TCComponentItem findItem = TCUtil.findItem(bean.getItemId());
				if (CommonTools.isEmpty(findItem)) {
					System.out.println("P/N為: " + bean.getItemId() + ", 在Teamcenter中不存在");
					writeErrorLogText("P/N為: " + bean.getItemId() + ", 在Teamcenter中不存在");
					MntSearchBean searchBean = new MntSearchBean();
					searchBean.setMissPN(bean.getItemId());
					searchBean.setPartType(checkItemType(bean.getItemId()));
					searchBean.setItemNo(itemNo++);
					notExistList.add(searchBean);
				} else {
					bean.setItemRev(findItem.getLatestItemRevision());
				}
				if (CommonTools.isNotEmpty(bean.getChilds())) {
					batchCheckItem(bean.getChilds());
				}
				if (CommonTools.isNotEmpty(bean.getSubstitutesList())) { // 判断是否含有替代料
					batchCheckItem(bean.getSubstitutesList());
				}
			} catch (TCException e) {
				e.printStackTrace();
				writeErrorLogText(TCUtil.getExceptionMsg(e));				
				throw new RuntimeException(e);
			}
		});
	}

	/**
	 * 生成MissPart文件
	 * 
	 * @return
	 * @throws Exception
	 */
	private String generateMissPartList(String excelFilePath) {
		try {
			ExcelUtil excelUtil = new ExcelUtil();
			Workbook wb = excelUtil.getWorkbook(MntMissPartSheetConstant.TEMPLATE);
			Sheet sheet = wb.getSheetAt(0);
			CellStyle cellStyle = excelUtil.getCellStyle2(wb);
			String filePath = CommonTools.getFilePath(ConstantsEnum.MISSPARTFOLDER.names()); // 查找保存Miss Part文件夾
			CommonTools.deletefile(filePath); // 刪除文件夾下面的所有文件
//			String excelFilePath = filePathText.getText();
			String fileName = excelFilePath.substring(excelFilePath.lastIndexOf(File.separator) + 1,
					excelFilePath.lastIndexOf(".")) + "_" + ConstantsEnum.MISSPARTFOLDER.names()
					+ excelFilePath.substring(excelFilePath.lastIndexOf("."));
			File targetFile = null;
			if (filePath.endsWith(File.separator)) {
				targetFile = new File(filePath + fileName);
			} else {
				targetFile = new File(filePath + File.separator + fileName);
			}
			excelUtil.setCellValue2(notExistList, MntMissPartSheetConstant.START, MntMissPartSheetConstant.COLLENGTH,
					sheet, cellStyle);
			OutputStream out = new FileOutputStream(targetFile);
			wb.write(out);
			out.flush();
			out.close();
			return targetFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
			writeErrorLogText(TCUtil.getExceptionMsg(e));
			throw new RuntimeException(e);
		}
	}

	/**
	 * 批量查找Item
	 * 
	 * @param rootBean
	 * @throws TCException
	 */
	private void batchFindItem(MntEBOMInfo rootBean) {
		if (CommonTools.isNotEmpty(rootBean.getItemRev())) {
			writeInfoLogText("P/N為: " + rootBean.getItemId() + ", 在Teamcenter中查詢成功");
		}
		List<MntEBOMInfo> childs = rootBean.getChilds();
		if (CommonTools.isNotEmpty(childs)) {
			childs.forEach(childBean -> {
				if (CommonTools.isNotEmpty(childBean.getItemRev())) {
					writeInfoLogText("P/N為: " + childBean.getItemId() + ", 在Teamcenter中查詢成功");
				}
				List<MntEBOMInfo> subList = childBean.getSubstitutesList();
				if (CommonTools.isNotEmpty(subList)) {
					subList.forEach(subBean -> {
						if (CommonTools.isNotEmpty(subBean.getItemRev())) {
							writeInfoLogText("P/N為: " + subBean.getItemId() + ", 在Teamcenter中查詢成功");
						}
					});
				}
			});
		}
	}

	/**
	 * 创建EBOM或更新EBOM
	 * @param rootBean
	 */
	private void createOrAddEBOMStruct(MntEBOMInfo rootBean) {
		TCComponentBOMWindow window = null;
		try {
			TCComponentItemRevision rootItemRev = rootBean.getItemRev();
			if (CommonTools.isEmpty(rootItemRev)) {
				return;
			}
			window = TCUtil.createBOMWindow(session);
			TCComponentBOMLine rootBomLine = window.setWindowTopLine(rootItemRev.getItem(), rootItemRev, null, null);
//			rootItemRev.setProperties(tcPropMapping(rootItemRev, rootBean)); // 保存對象版本屬性
//			rootBomLine.setProperties(tcPropMapping(rootBomLine, rootBean)); // 保存BOmLine屬性
			rootBomLine.unpack();
			rootBomLine.refresh();
			List<AIFComponentContext> list = new ArrayList<AIFComponentContext>();
			AIFComponentContext[] children = rootBomLine.getChildren();
			if (CommonTools.isNotEmpty(children)) {
				list = Stream.of(children).collect(Collectors.toList());
			}
			List<MntEBOMInfo> childs = rootBean.getChilds();
			if (CommonTools.isNotEmpty(childs)) {
//				checkBomLineExist(list, childs); // 判断子BOMLine是否已经存在
				checkBomLineExistNew(list, childs); // 判断子BOMLine是否已经存在
				for (MntEBOMInfo childBean : childs) {
					if (!childBean.getChildExist()) { // 代表不存在
						addChild(rootBomLine, childBean, rootBean.getItemId()); // 添加子料
					} else { // 代表已存在
						TCComponentBOMLine childBomLine = (TCComponentBOMLine) session.getComponentManager()
								.getTCComponent(childBean.getBomLineUid());
						writeInfoLogText("P/N為: " + rootBean.getItemId() + ", 子料 P/N為: " + childBean.getItemId()+ ", 已經存在，無需重複添加！");
						childBomLine.unpack();
						childBomLine.setProperties(tcPropMapping(childBomLine, childBean));
						List<MntEBOMInfo> subBeanList = childBean.getSubstitutesList();
						if (CommonTools.isNotEmpty(subBeanList)) {
							List<TCComponentBOMLine> substituteslist = new ArrayList<TCComponentBOMLine>();
							TCComponentBOMLine[] listSubstitutes = childBomLine.listSubstitutes();
							if (CommonTools.isNotEmpty(listSubstitutes)) {
								substituteslist = Stream.of(listSubstitutes).collect(Collectors.toList());
							}
//							if (CommonTools.isNotEmpty(subBeanList)) {
//								checkSubBomLineExist(substituteslist, subBeanList); // 判断替代料是否已经存在
								checkBomLineExistNew(substituteslist, subBeanList); // 判断替代料是否已经存在
								addSub(subBeanList, childBomLine, childBean.getItemId()); // 添加替代料
//							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			writeErrorLogText(TCUtil.getExceptionMsg(e));
			throw new RuntimeException(e);
		} finally {
			if (CommonTools.isNotEmpty(window)) {
				try {
					window.save();
					window.close();
				} catch (TCException e) {
					e.printStackTrace();
					writeErrorLogText(TCUtil.getExceptionMsg(e));
				}
			}
		}
	}

	/**
	 * 添加子BOMLine
	 * 
	 * @param rootBomLine
	 * @param childBean
	 */
	private void addChild(TCComponentBOMLine rootBomLine, MntEBOMInfo childBean, String rootItemRevId) {
		TCComponentBOMLine childBomLine = null;
		if (!childBean.getChildExist()) { // 如果子BOMLine不存在
			try {
				childBomLine = rootBomLine.add(childBean.getItemRev().getItem(), childBean.getItemRev(), null, false);
				writeInfoLogText("P/N為: " + rootItemRevId + ", 子料 P/N為: " + childBean.getItemId() + ", 添加成功！");
			} catch (TCException e1) {
				e1.printStackTrace();
				writeErrorLogText("P/N為: " + rootItemRevId + ", 子料 P/N為: " + childBean.getItemId() + ", 添加失敗！");
				writeErrorLogText(TCUtil.getExceptionMsg(e1));
				childBomLine = null;
			}

			try {
//				childBean.getItemRev().setProperties(tcPropMapping(childBean.getItemRev(), childBean)); // 保存對象版本屬性
				childBomLine.unpack();
				childBomLine.setProperties(tcPropMapping(childBean, childBean)); // 保存BOMLine屬性
			} catch (TCException e2) {
				e2.printStackTrace();
				writeErrorLogText("P/N為: " + rootItemRevId + ", 子料 P/N為: " + childBean.getItemId() + ", 屬性修改失敗！");
				writeErrorLogText(TCUtil.getExceptionMsg(e2));
				childBomLine = null;
			}
		}
		if (CommonTools.isNotEmpty(childBomLine)) {
			if (CommonTools.isNotEmpty(childBean.getSubstitutesList())) {
				addSub(childBean.getSubstitutesList(), childBomLine, childBean.getItemId()); // 添加替代料
			}
		}
	}

	/**
	 * 子添加替代料
	 * 
	 * @param childBean
	 * @param childBomLine
	 */
	private void addSub(List<MntEBOMInfo> subList, TCComponentBOMLine childBomLine, String childItemRevId) {
		if (CommonTools.isNotEmpty(subList)) {
			subList.forEach(subBean -> {
				if (!subBean.getSubExist()) { // 如果替代料不存在
					try {
						childBomLine.add(subBean.getItemRev().getItem(), subBean.getItemRev(), null, true); // 为当前BOMLine添加替代料
						writeInfoLogText("P/N為: " + childItemRevId + ", 替代料 P/N為: " + subBean.getItemId() + ", 添加成功！");
					} catch (TCException e1) {
						e1.printStackTrace();
						writeErrorLogText(TCUtil.getExceptionMsg(e1));
						writeInfoLogText("P/N為: " + childItemRevId + ", 替代料 P/N為: " + subBean.getItemId() + ", 添加失敗！");
					}
//					try {
//						subBean.getItemRev().setProperties(tcPropMapping(subBean.getItemRev(), subBean)); // 保存對象版本屬性
//					} catch (TCException e2) {
//						e2.printStackTrace();
//						writeErrorLogText(TCUtil.getExceptionMsg(e2));
//						writeInfoLogText("P/N為: " + childItemRevId + ", 替代料 P/N為: " + subBean.getItemId() + ", 屬性修改失敗！");
//					}
				} else {
					writeInfoLogText("P/N為: " + childItemRevId + ", 替代料 P/N為: " + subBean.getItemId() + ", 已經存在，無需重複添加！");
				}
			});
		}
	}

	/**
	 * 判断子BOMLine是否已经存在
	 * 
	 * @param list
	 * @param childs
	 * @return
	 */
	private void checkBomLineExist(List<AIFComponentContext> list, List<MntEBOMInfo> childs) {
		
		childs.parallelStream().forEach(childBean -> {
			Optional<AIFComponentContext> findAny = list.stream().filter(aifComponentContext -> {
				try {
					return ((TCComponentBOMLine) aifComponentContext.getComponent()).getItemRevision().getUid()
							.equals(childBean.getItemRev().getUid());
				} catch (TCException e) {
					e.printStackTrace();
				}
				return false;
			}).findAny();
			if (findAny.isPresent()) {
				AIFComponentContext aifComponentContext = findAny.get();
				TCComponentBOMLine childBomLine = (TCComponentBOMLine) aifComponentContext.getComponent();
				childBean.setBomLineUid(childBomLine.getUid()); // 设置BOMLine uid
				childBean.setChildExist(true); // 设置已经存在
			}
		});
	}

	/**
	 * 判断子BOMLine是否已经存在
	 * 
	 * @param list
	 * @param childs
	 * @return
	 */
	private void checkBomLineExistNew(List<? extends Object> bomLineList, List<MntEBOMInfo> list) {
		
		list.parallelStream().forEach(bean -> {
			Optional<? extends Object> findAny = bomLineList.stream().filter(object -> {
				try {
					if (object instanceof AIFComponentContext) {
						AIFComponentContext aifComponentContext = (AIFComponentContext) object;
						return ((TCComponentBOMLine) aifComponentContext.getComponent()).getItemRevision().getUid()
								.equals(bean.getItemRev().getUid());
					} else if (object instanceof TCComponentBOMLine) {
						TCComponentBOMLine bomLine = (TCComponentBOMLine) object;
						return bomLine.getItemRevision().getUid().equals(bean.getItemRev().getUid());
					}
					
				} catch (TCException e) {
					e.printStackTrace();
				}
				return false;
			}).findAny();
			if (findAny.isPresent()) {				
				Object obj = findAny.get();
				TCComponentBOMLine bomLine = null;
				if (obj instanceof AIFComponentContext) {
					AIFComponentContext aifComponentContext = (AIFComponentContext) obj;
					bomLine = (TCComponentBOMLine) (aifComponentContext.getComponent());
					bean.setChildExist(true); // 设置子BOMLine已经存在
				} else if (obj instanceof TCComponentBOMLine) {
					bomLine = (TCComponentBOMLine)obj;	
					bean.setSubExist(true); // 设置替代料已存在
				}
				bean.setBomLineUid(bomLine.getUid()); // 设置BOMLine uid
			}
		});
	}
	
	/**
	 * 判断替代料是否已经存在
	 * 
	 * @param listSubstitutes
	 * @param subBeanList
	 * @return
	 */
	private void checkSubBomLineExist(List<TCComponentBOMLine> listSubstitutes, List<MntEBOMInfo> subBeanList) {
		
		subBeanList.parallelStream().forEach(subBean -> {
			Optional<TCComponentBOMLine> findAny = listSubstitutes.stream().filter(bomLine -> {
				try {
					return bomLine.getItemRevision().getUid().equals(subBean.getItemRev().getUid());
				} catch (TCException e) {
					e.printStackTrace();
				}
				return false;
			}).findAny();

			if (findAny.isPresent()) {
				TCComponentBOMLine subBomLine = findAny.get();
				subBean.setBomLineUid(subBomLine.getUid()); // 设置BOMLine uid
				subBean.setSubExist(true); // 设置已存在
			}
		});
	}

	/**
	 * 判斷對象類型
	 * 
	 * @param itemId
	 * @return
	 */
	private String checkItemType(String itemId) {
		String objectType = "";
		for (String str : importItemTypeList) {
			String[] split = str.split("=");
			if (itemId.startsWith(split[0])) { // 判斷itemId的開頭
				objectType = split[1];
				break;
			}
		}
		if (CommonTools.isEmpty(objectType)) {
			return "EDAComPart";
		}
		return objectType;
	}

	/**
	 * 創建零組件/TC中查找零組件
	 * 
	 * @param itemId
	 * @param objectType
	 * @return
	 * @throws TCException
	 */
	private TCComponentItemRevision createItem(MntEBOMInfo bean, String objectType) throws TCException {
		TCComponentItemRevision itemRevision = null;
		String itemId = bean.getItemId();
		TCComponentItem findItem = TCUtil.findItem(itemId); // 從TC中查找對象
		if (CommonTools.isNotEmpty(findItem)) {
			itemRevision = findItem.getLatestItemRevision();
			writeInfoLogText("P/N為: " + itemId + ", 已經存在Teamcenter中，無需重複創建");
		} else {
			try {
				TCComponentItem createItem = null;
				if (CommonTools.isEmpty(objectType)) {
					createItem = com.foxconn.mechanism.util.TCUtil.createItem(itemId, "EDAComPart", itemId, "@",
							"EDAComPart Revision");
					itemRevision = createItem.getLatestItemRevision();
				} else {
					createItem = com.foxconn.mechanism.util.TCUtil.createItem(itemId, objectType, itemId, "@",
							objectType + " Revision");
				}
				writeInfoLogText("P/N為: " + itemId + ", 創建零組件成功！");
			} catch (Exception e) {
				e.printStackTrace();
				writeErrorLogText("P/N為: " + itemId + ", 創建零組件失敗！");
				writeErrorLogText(TCUtil.getExceptionMsg(e));
			}
		}
		itemRevision.setProperties(tcPropMapping(itemRevision, bean)); // 設置對象版本屬性
		return itemRevision;
	}

	/**
	 * 返回tc屬性映射
	 * 
	 * @param obj
	 * @param bean
	 * @return
	 */
	private Map<String, String> tcPropMapping(Object obj, MntEBOMInfo bean) {
		Map<String, String> properties = new HashMap<String, String>();
		if (obj instanceof TCComponentItemRevision) {
			properties.put("object_name", bean.getItemId());
			properties.put("d9_EnglishDescription", bean.getDescription());
			properties.put("d9_ManufacturerID", bean.getSupplier());
			properties.put("d9_ManufacturerPN", bean.getSupplierPN());
			properties.put("d9_SupplierZF", bean.getSupplierZF());
			properties.put("d9_Un", bean.getUnit());
		} else if (obj instanceof TCComponentBOMLine) {
			properties.put("bl_sequence_no", String.valueOf(bean.getFindNum()));
			properties.put("bl_quantity", bean.getUsage());
			properties.put("bl_occ_d9_GroupQuantity", bean.getGrpQty());
			properties.put("bl_occ_d9_Location", bean.getLocation());
		}
		return properties;
	}

	/**
	 * 記錄日誌信息
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

	public void writeWarnLogText(final String messsage) {
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				if (!styledText.isDisposed()) {
					String msg = "【WARN】" + messsage;
					styledText.append(msg + "\n");
					
					warnStyle.start = styledText.getCharCount() - msg.length() - 1;
					warnStyle.length = msg.length();
					styledText.setStyleRange(warnStyle);
				}
			}
		});
	}
	
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
				if (!importButton.isDisposed()) {
					importButton.setEnabled(importFlag); // 设置导入按钮状态
				}				
			}
		});
	}
	
	/**
	 * 更新停止按钮状态
	 * @param stopFlag
	 */
	private void updateStopBtnStates(final boolean stopFlag) {
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				if (!stopButton.isDisposed()) {
					stopButton.setEnabled(stopFlag); // 设置停止按钮状态
				}				
			}
		});
	}
	
}
