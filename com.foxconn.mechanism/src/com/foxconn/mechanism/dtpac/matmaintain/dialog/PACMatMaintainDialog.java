package com.foxconn.mechanism.dtpac.matmaintain.dialog;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.foxconn.mechanism.dtpac.matmaintain.MatrixFactory.MatrixFatory;
import com.foxconn.mechanism.dtpac.matmaintain.domain.ExcelBean;
import com.foxconn.mechanism.dtpac.matmaintain.domain.LinkLovBean;
import com.foxconn.mechanism.dtpac.matmaintain.domain.MatMaintainBean;
import com.foxconn.mechanism.dtpac.matmaintain.domain.MatMaintainConstant;
import com.foxconn.mechanism.dtpac.matmaintain.editor.MyEditingSupport;
import com.foxconn.mechanism.dtpac.matmaintain.listener.ButtonSelectionListener;
import com.foxconn.mechanism.dtpac.matmaintain.rightbtn.MyActionGroup;
import com.foxconn.mechanism.dtpac.matmaintain.util.TableTools;
import com.foxconn.tcutils.constant.ColorEnum;
import com.foxconn.tcutils.constant.DatasetEnum;
import com.foxconn.tcutils.constant.PreferenceConstant;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.simple.traditionnal.util.S2TTransferUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

public class PACMatMaintainDialog extends Dialog {

	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private Shell shell = null;
	private Shell parentShell = null;
	private Registry reg = null;
	private Label typeLabel = null;
	private Label nameLabel = null;
	private Label totalMatCostLabel = null;
	private Text totalMatCostText = null;
	private Combo typeCombo = null;
	private Combo nameCombo = null;
	private TableViewer tv;
	private Table table;
	private TableLayout tableLayout = null;
	private Composite statusBar = null;
	private Label status = null;
	private ProgressBar progressBar = null;
	private MyEditingSupport myEditingSupport = null;
	private MyActionGroup actionGroup = null;
	private Button saveBtn = null;
	private Button cancelBtn = null;
	private Button addBtn = null;
	private String[] tableTitle = {MatMaintainConstant.INDEX, MatMaintainConstant.ITEMID, MatMaintainConstant.TYPE, MatMaintainConstant.MATERIAL, MatMaintainConstant.LENGTH, 
			MatMaintainConstant.WIDTH, MatMaintainConstant.HEIGHT, MatMaintainConstant.THICKNESS, MatMaintainConstant.DENSITY, MatMaintainConstant.WEIGHT, MatMaintainConstant.QTY, MatMaintainConstant.CALCULLATIONUNIT, 
			MatMaintainConstant.USAGECALCULATION, MatMaintainConstant.COSTFACTOR, MatMaintainConstant.COST};
	private TCComponentBOMLine selectBomLine = null;
	private Map<String, List<ExcelBean>> retMap = null;
	private List<ExcelBean> matBeanList = null;
	private List<ExcelBean> matCostBeanList = null;
	private List<LinkLovBean> linkLovList = null;
	private ButtonSelectionListener btnSelectionListener = null;
	private List<MatMaintainBean> dataList = null;
	private List<MatMaintainBean> cacheDataList = null;
	private String partType = null;
	private String curMatType = null;
	private String curMatName = null;
	private List<String> typeList = null;
	private List<String> matList = null;
	private MatMaintainBean selectItemRevBean = null;
	
	public PACMatMaintainDialog(AbstractAIFUIApplication app, Shell parentShell) {
		super(parentShell);
		this.app = app;
		this.session = (TCSession) this.app.getSession();
		this.parentShell = parentShell;
		cacheDataList = new ArrayList<MatMaintainBean>();		
		reg = Registry.getRegistry("com.foxconn.mechanism.dtpac.dtpac");
		selectBomLine = getSelectBOMLine(); // 获取当前选中的BOMLine
		try {
			initUI();
		} catch (TCException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 界面初始化
	 * @throws TCException 
	 */
	private void initUI() throws TCException {
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE);
		shell.setSize(700, 600);
		shell.setText(reg.getString("PACMatMaintain.TITLE"));
		shell.setLayout(new GridLayout(1, false));
		TCUtil.centerShell(shell);
		
		Image image = getDefaultImage();
		if (CommonTools.isNotEmpty(image)) {
			shell.setImage(image);
		}
		
		GridData topData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		
		Composite topComposite = new Composite(shell, SWT.NONE);
		GridLayout topGridLayout = new GridLayout(5, false);
		topGridLayout.horizontalSpacing = 20;
		
		topComposite.setLayout(topGridLayout);
		topComposite.setLayoutData(topData);
		
		typeLabel = new Label(topComposite, SWT.NONE);
		typeLabel.setText(reg.getString("TypeCombo.LABEL"));
		
		GridData typeGridData = new GridData();
		typeGridData.widthHint = 120;		
		
		typeCombo = new Combo(topComposite, SWT.READ_ONLY);
		typeCombo.setLayoutData(typeGridData);
		
		nameLabel = new Label(topComposite, SWT.NONE);
		nameLabel.setText(reg.getString("NameCombo.LABEL"));
		
		GridData nameGridData = new GridData();
		nameGridData.widthHint = 100;	
		
		nameCombo = new Combo(topComposite, SWT.READ_ONLY);
		nameCombo.setLayoutData(nameGridData);
		
		GridData addBtnGridData = new GridData();
		addBtnGridData.widthHint = 60;
		addBtnGridData.heightHint = 30;
		
		addBtn = new Button(topComposite, SWT.PUSH);
		addBtn.setText(reg.getString("AddBtn.LABEL"));	
		addBtn.setLayoutData(addBtnGridData);		
		
		Composite tableComposite = new Composite(shell, SWT.NONE);
		tableComposite.setLayout(new GridLayout(1, false));
		tableComposite.setLayoutData(new GridData(GridData.FILL_BOTH));	
		
		tv = new TableViewer(tableComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
		table = tv.getTable();
		
		table.setHeaderVisible(true); // 显示表头
		table.setLinesVisible(true); // 显示
		table.setHeaderBackground(ColorEnum.GoldGray.color());
		
		tableLayout = new TableLayout(); // 专用于表格的布局
		table.setLayout(tableLayout);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));		
		
		createTreeColumn(); // 创建表格列			
		
		Composite totalMatCostComposite = new Composite(tableComposite, SWT.NONE);
		
		GridLayout totalMatGridLayout = new GridLayout(3, false);
		totalMatGridLayout.horizontalSpacing = 10;
		
		totalMatCostComposite.setLayoutData(createGridData(GridData.HORIZONTAL_ALIGN_FILL, 1, SWT.END));		
		totalMatCostComposite.setLayout(totalMatGridLayout);
		
		GridData totalMatCostData = new GridData(GridData.FILL_HORIZONTAL);
		totalMatCostData.horizontalAlignment = SWT.END;
		totalMatCostData.horizontalSpan = 1;
//		totalMatCostData.widthHint = 1000;
		
		totalMatCostLabel = new Label(totalMatCostComposite, SWT.NONE);
		totalMatCostLabel.setText(reg.getString("TotalMatCost.LABEL"));		
//		totalMatCostLabel.setLayoutData(totalMatCostData);
		totalMatCostLabel.setSize(100, 15);
		
		GridData totalMatCostTextData = new GridData(GridData.FILL_HORIZONTAL);
		totalMatCostTextData.horizontalAlignment = SWT.CENTER;
		totalMatCostTextData.horizontalSpan = 1;
		totalMatCostTextData.widthHint = 100;
		totalMatCostTextData.heightHint = 15;		
		
		totalMatCostText = new Text(totalMatCostComposite, SWT.SINGLE | SWT.BORDER);
		totalMatCostText.setEnabled(false); // 设置不可编辑
		totalMatCostText.setLayoutData(totalMatCostTextData);
		
		totalMatCostData.horizontalAlignment = SWT.CENTER;
		totalMatCostData.horizontalSpan = 1;
		totalMatCostData.widthHint = 35;
		
		Label label = new Label(totalMatCostComposite, SWT.NONE);
		label.setLayoutData(totalMatCostData);
		
		
		Composite bottomBtnComposite = new Composite(shell, SWT.NONE);		
		GridData bottomBtnData = new GridData(GridData.FILL_HORIZONTAL);
		bottomBtnData.horizontalAlignment = GridData.CENTER; 		
		bottomBtnComposite.setLayoutData(bottomBtnData);
		
		RowLayout bottomRowLayout = new RowLayout(); 
		bottomRowLayout.spacing = 15;
		
		bottomBtnComposite.setLayout(bottomRowLayout);
		
		RowData bottomBtnRowData = new RowData();
		bottomBtnRowData.width = 60;
		bottomBtnRowData.height = 30;		
		
		saveBtn = new Button(bottomBtnComposite, SWT.PUSH);
		saveBtn.setText(reg.getString("SaveBtn.LABEL"));
		saveBtn.setLayoutData(bottomBtnRowData);
		
		cancelBtn = new Button(bottomBtnComposite, SWT.PUSH);
		cancelBtn.setText(reg.getString("CancelBtn.LABEL"));
		cancelBtn.setLayoutData(bottomBtnRowData);
		
		btnSelectionListener = new ButtonSelectionListener(shell, session, reg, tv, typeLabel, typeCombo, nameLabel, nameCombo, tableComposite, this);
		addBtn.addSelectionListener(btnSelectionListener);
		saveBtn.addSelectionListener(btnSelectionListener);
		cancelBtn.addSelectionListener(btnSelectionListener);		
		
		if (selectBomLine.hasChildren() || !TCUtil.checkUserPrivilege(session, selectBomLine.getItemRevision())) { // 判断选择的是否为根节点或者当前BOMLine对应的对象版本是否由编辑权
			typeLabel.setVisible(false);
			nameLabel.setVisible(false);
			typeCombo.setVisible(false);
			nameCombo.setVisible(false);	
			addBtn.setVisible(false);
			saveBtn.setVisible(false);
			cancelBtn.setVisible(false);
		} else {
			typeCombo.setEnabled(false);
			nameCombo.setEnabled(false);
			addBtn.setEnabled(false);
			saveBtn.setEnabled(false);
			cancelBtn.setEnabled(false);
			
			actionGroup = new MyActionGroup(tv, reg, this);
			actionGroup.fillContextMenu(new MenuManager());	
		}
		
		totalMatCostLabel.setVisible(false);
		totalMatCostText.setVisible(false);
		table.setEnabled(false);
		
		setUpStatusBar(tableComposite, reg.getString("ProgressBar1.TITLE"), false);
		updateProgressBarByLoad();
		
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
	 * 创建表格的列并设置列宽
	 */
	private void createTreeColumn() {
		List<String> list = Stream.of(tableTitle).map(e -> S2TTransferUtil.toTraditionnalString(e)).collect(Collectors.toList());
		for (int index = 0; index < list.size(); index++) {
			TableViewerColumn tvc = new TableViewerColumn(tv, SWT.CENTER, index);
			TableColumn tableColumn = tvc.getColumn();
			tableColumn.setText(list.get(index));
			int columnWidth = 0;
			if (index == 0) {
				columnWidth = 35;
			} else if (index == 1) {
				columnWidth = 170;
			} else if (index == 2) {
				columnWidth = 120;
			} else if (index == 3 || index == 4 || index == 5 || index == 6 || index == 7 || index == 9  || index == 11) {
				columnWidth = 80;
			} else if (index == 8) {
				columnWidth = 100;
			} else if (index == 10) {
				columnWidth = 120;
			} 
			else if (index == 12) {
				columnWidth = 300;
			} else if (index == 13 || index == 14) {
				columnWidth = 120;
			} 
//			else {
//				columnWidth = 120;
//			}
			
			tableColumn.setWidth(columnWidth);	
			
			if (index == 0) {				
				tableColumn.addListener(SWT.Move, new Listener() {

					@Override
					public void handleEvent(Event event) {

						int[] order = table.getColumnOrder();
						for (int i = 0, col = 0; i < order.length; i++) {
							int tmp = order[i];
							order[i] = col;
							col = tmp;
							if (col == 0)
								if (i == 0)
									return;
								else
									break;
						}
						table.setColumnOrder(order);
					}
				});					
			
		        
			} else {
				tableColumn.setMoveable(true);
			}
			
			myEditingSupport = new MyEditingSupport(shell, tv, index, list.get(index), reg, this);
			tvc.setEditingSupport(myEditingSupport);
		}
	}

	
	/**
	 * 加载数据更新进度条信息
	 */

	private void updateProgressBarByLoad() {
		new Thread(new Runnable() {
			private int progress = 0;
			private static final int INCREMENT = 10;
			private boolean flag = false; // 数据接口获取成功与否接口
//			private List<MatMaintainBean> dataList = null;
			
			@Override
			public void run() {
				new Thread() {
					public void run() {	
						
						if (!checkPreCondition()) { // 前置条件校验
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									status.setVisible(false);
									progressBar.dispose(); // 销毁进度条组件
								}
							});								
							return;
						}
						
						matBeanList = retMap.get("matBeanList");
						matCostBeanList = retMap.get("matCostBeanList");
						linkLovList = groupByLov(matBeanList);
						
						dataList = getData(); // 获取数据的逻辑
						if (dataList == null) {
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									status.setVisible(false);
									progressBar.dispose(); // 销毁进度条组件
								}
							});	
							TCUtil.errorMsgBox(reg.getString("DataLoadingErr.MSG"), reg.getString("ERROR.MSG"));							
							return;
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
						    		
									tv.setContentProvider(new MatMaintainTableViewerContentProvider());
									tv.setLabelProvider(new MatMaintainTableViewerLabelProvider());
									
									try {
										resetCalculatParams(); // 重新设置用于计算公式的参数
										
										TableTools.updateTotalCost(dataList, selectBomLine, totalMatCostLabel, totalMatCostText); // 更新总物料成本文本框的值										
										TableTools.updateCacheDataList(cacheDataList, dataList);
						    			tv.setInput(dataList); // 设置数据并刷新TableViewer
							            tv.refresh();					            
							            
							            if (typeCombo.isVisible() && nameCombo.isVisible()) {
							            	if (setComboLov()) { // 设置组件下拉值
							            		addBtn.setEnabled(true);
												saveBtn.setEnabled(true);
												cancelBtn.setEnabled(true);
											}; 							            	
										}							    		
							    		
							    		table.setEnabled(true);
									} catch (Exception e) {
										e.printStackTrace();
										
									}					    		
						    		
						    		progressBar.dispose(); // 销毁进度条组件
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
	
	public void updateProgressBarBySave(List<MatMaintainBean> list) {
		new Thread(new Runnable() {
			private int progress = 0;
			private static final int INCREMENT = 10;
			private boolean flag = false; // 数据接口获取成功与否接口
			private boolean saveFlag = false; // 用作是否保存成功标识
			
			@Override
			public void run() {
				new Thread() {
					public void run() {	
						
						btnSelectionListener.resetPartTypeParams(list);
						if (!btnSelectionListener.saveData(list)) { // 保存数据
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									status.setVisible(false);
									progressBar.dispose(); // 销毁进度条组件
								}
							});
							
							TCUtil.errorMsgBox(reg.getString("DataSaveErr.MSG"), reg.getString("ERROR.MSG"));							
							return;							
						} else {
							dataList = getData(); // 获取数据的逻辑
							if (dataList == null) {
								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										status.setVisible(false);
										progressBar.dispose(); // 销毁进度条组件
									}
								});	
								TCUtil.errorMsgBox(reg.getString("DataReLoadingErr.MSG"), reg.getString("ERROR.MSG"));							
								return;
							}
							
							cacheDataList.clear(); // 数据加载完成，清空所有的缓存集合数据
							flag = true;
						}	
											 						
					}
				}.start();
				
				while (progressBar != null && !progressBar.isDisposed()) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							if (flag) {
								if (!shell.isDisposed()) {
									status.setVisible(false);
						    		
									tv.setContentProvider(new MatMaintainTableViewerContentProvider());
									tv.setLabelProvider(new MatMaintainTableViewerLabelProvider());
									
									try {
										resetCalculatParams(); // 重新设置用于计算公式的参数
										
										TableTools.updateTotalCost(dataList, selectBomLine, totalMatCostLabel, totalMatCostText); // 更新总物料成本文本框的值										
										TableTools.updateCacheDataList(cacheDataList, dataList);
						    			tv.setInput(dataList); // 设置数据并刷新TableViewer
							            tv.refresh();
							            
							    		saveBtn.setVisible(true);
							    		cancelBtn.setVisible(true);
							    		table.setEnabled(true);
							    		
							    		progressBar.dispose(); // 销毁进度条组件
							    		
							    		saveFlag = true;
									} catch (Exception e) {
										e.printStackTrace();
										progressBar.dispose(); // 销毁进度条组件										
									}
									
									if (saveFlag) {
										TCUtil.infoMsgBox(reg.getString("DataSaveSucc.MSG"), reg.getString("INFORMATION.MSG"));		
									}	    		
						    		
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
	
	
	
	private boolean checkPreCondition() {
		try {
			String filePath = downloadTemplate(session, reg);
			if (CommonTools.isEmpty(filePath)) {
				TCUtil.errorMsgBox(reg.getString("DownLoadingErr.MSG"), reg.getString("ERROR.MSG"));
 				return false;
			}
			
			retMap = parseExcelTemplate(filePath);
			if (CommonTools.isEmpty(retMap)) {
				TCUtil.errorMsgBox(reg.getString("TemplateParseErr.MSG"), reg.getString("ERROR.MSG"));
 				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
		
	/**
	 * 重新设置用于计算公式的参数
	 */
 	private void resetCalculatParams() {
		for (MatMaintainBean bean : dataList) {
			Optional<ExcelBean> findAny = matBeanList.stream().filter(e -> e.getPartType().equals(S2TTransferUtil.toTraditionnalString(bean.getPartType())) 
					&& e.getMatType().equals(S2TTransferUtil.toTraditionnalString(bean.getMatType()))&& e.getMatName().equals(S2TTransferUtil.toTraditionnalString(bean.getMatName())) 
					&& e.getType().equals(S2TTransferUtil.toTraditionnalString(bean.getType())) && e.getMaterial().contains(S2TTransferUtil.toTraditionnalString(bean.getMaterial()))).findAny();
			if (findAny.isPresent()) {
				ExcelBean excelBean = findAny.get();
				bean.setUsageCalculation(excelBean.getUsageCalculation());
				bean.setCalcullationUnit(excelBean.getCalcullationUnit());
			}
			
			Optional<ExcelBean> findAny2 = matCostBeanList.stream().filter(e -> S2TTransferUtil.toTraditionnalString(e.getMaterial()).equals(bean.getMaterial())).findAny();
			if (findAny2.isPresent()) { // 根据材料类型匹配材料成本因数
				ExcelBean excelBean2 = findAny2.get();
				bean.setCostFactor(excelBean2.getCostFactor());
			}
			
			ExcelBean newExcelBean = new ExcelBean();
			TableTools.copyBeanProp(bean, newExcelBean); // 属性复制
			
			TableTools.resetSizeValue(newExcelBean); // 重新设置尺寸一些参数
			
			MatrixFatory.calculateCost(newExcelBean); // 计算物料成本
			
			bean.setCost(newExcelBean.getCost()); 
		}
	}
	 	
 	
	/**
	 * 设置组件下拉值
	 * @return
	 */
	private boolean setComboLov() {
		MatMaintainBean bean = null;
		if (CommonTools.isEmpty(dataList)) {
			bean = selectItemRevBean;
		} else {
			bean = dataList.get(0);
		}
		
    	String partType = S2TTransferUtil.toTraditionnalString(bean.getPartType());
    	setPartType(partType);
    	String matType = S2TTransferUtil.toTraditionnalString(bean.getMatType());						            	
    	String matName = S2TTransferUtil.toTraditionnalString(bean.getMatName());
    	
    	List<String> matTypeLovList = getLovValue(partType, null, null, null);
    	if (CommonTools.isEmpty(matTypeLovList)) {
    		progressBar.dispose(); // 销毁进度条组件
    		TCUtil.errorMsgBox(S2TTransferUtil.toTraditionnalString("当前零组件: " + bean.getItemId() + ", 版本号: " + bean.getVersion()) + ", "+ reg.getString("PartTypeErr.MSG"), reg.getString("ERROR.MSG"));						            		
    		return false;
		}						            	
    	
    	typeCombo.setEnabled(true);
    	int matTypeIndex = getIndex(matTypeLovList, matType);						            	
    	setComboValues(typeCombo, matTypeLovList, matTypeIndex);						            	
    	setCurMatType(typeCombo.getItem(matTypeIndex));
    	
    	if (CommonTools.isEmpty(matType)) {    		
			matType = getCurMatType();
			bean.setMatType(matType);
		}
    	
    	List<String> matNameLovList = getLovValue(partType, matType, null, null);
		if (CommonTools.isEmpty(matNameLovList)) {
			progressBar.dispose(); // 销毁进度条组件
			typeCombo.setEnabled(false);
			TCUtil.errorMsgBox(S2TTransferUtil.toTraditionnalString("当前零组件: " + bean.getItemId() + ", 版本号: " + bean.getVersion()) + ", "+ reg.getString("PartType2Err.MSG"), reg.getString("ERROR.MSG"));							            		
			return false;
		}
		
		nameCombo.setEnabled(true);
		int matNameIndex = getIndex(matNameLovList, matName);										
		setComboValues(nameCombo, matNameLovList, matNameIndex);										
		setCurMatName(nameCombo.getItem(matNameIndex));									
    	
		if (CommonTools.isEmpty(matName)) {
			matName = getCurMatName();
			bean.setMatName(matName);
		}
		
		typeList = getLovValue(partType, matType, matName, null);
		setTypeList(typeList);
		if (CommonTools.isEmpty(typeList)) {
			progressBar.dispose(); // 销毁进度条组件
			typeCombo.setEnabled(false);
			nameCombo.setEnabled(false);
			TCUtil.errorMsgBox(S2TTransferUtil.toTraditionnalString("当前零组件: " + bean.getItemId() + ", 版本号: " + bean.getVersion()) + ", "+ reg.getString("PartType3Err.MSG"), reg.getString("ERROR.MSG"));							            		
			return false;
		}	
		
		return true;	
		
		
	}
	
	
	private List<LinkLovBean> groupByLov(List<ExcelBean> list) {
		List<LinkLovBean> resultList = new ArrayList<>();
		Map<String, List<ExcelBean>> partTypeGroup = list.stream().collect(Collectors.groupingBy(bean -> bean.getPartType()));
		partTypeGroup.forEach((k1, v1) -> {
			LinkLovBean rootBean = new LinkLovBean();			
			rootBean.setValue(k1);
			rootBean.setIndex(v1.get(0).getIndex());
			
			Map<String, List<ExcelBean>> matTypeGroup = v1.stream().collect(Collectors.groupingBy(bean -> bean.getMatType()));
			matTypeGroup.forEach((k2, v2) -> {
				LinkLovBean matTypeBean = new LinkLovBean();
				matTypeBean.setValue(k2);
				matTypeBean.setIndex(v2.get(0).getIndex());
				rootBean.addChild(matTypeBean);
				
				Map<String, List<ExcelBean>> matNameGroup = v2.stream().collect(Collectors.groupingBy(bean -> bean.getMatName()));
				matNameGroup.forEach((k3, v3) -> {
					LinkLovBean matNameBean = new LinkLovBean();
					matNameBean.setValue(k3);
					matNameBean.setIndex(v3.get(0).getIndex());
					matTypeBean.addChild(matNameBean);
					
					Map<String, List<ExcelBean>> typeGroup = v3.stream().collect(Collectors.groupingBy(bean -> bean.getType()));
					typeGroup.forEach((k4, v4) -> {
						LinkLovBean typeBean = new LinkLovBean();
						typeBean.setValue(k4);
						typeBean.setIndex(v4.get(0).getIndex());
						matNameBean.addChild(typeBean);
						
						Map<String, List<ExcelBean>> materialGroup = v4.stream().collect(Collectors.groupingBy(bean -> bean.getMaterial()));
						materialGroup.forEach((k5, v5) -> {
							LinkLovBean materialBean = new LinkLovBean();
							materialBean.setValue(k5);
							materialBean.setIndex(v5.get(0).getIndex());							
							typeBean.addChild(materialBean);
						});
					});	
					
				});
			});
			
			resultList.add(rootBean);
		});
		return resultList;
	}
	
	
	/**
	 * 获取下拉值
	 * @param partType
	 * @param matType
	 * @param matName
	 * @param type
	 * @return
	 */
	public List<String> getLovValue(String partType, String matType, String matName, String type) {
		List<String> lovList = new ArrayList<String>();
		if (CommonTools.isEmpty(partType) && CommonTools.isEmpty(matType) && CommonTools.isEmpty(matName) && CommonTools.isEmpty(type)) {
			return null;
		}
		
		if (CommonTools.isNotEmpty(partType) && CommonTools.isEmpty(matType) && CommonTools.isEmpty(matName) && CommonTools.isEmpty(type)) {
			Optional<LinkLovBean> findAny = linkLovList.stream().filter(bean -> bean.getValue().equals(partType)).findAny();
			if (findAny.isPresent()) {
				LinkLovBean partTypeBean = findAny.get();
				List<LinkLovBean> matTypeList = partTypeBean.getChilds();
				matTypeList.sort(Comparator.comparing(LinkLovBean::getIndex));
				return matTypeList.stream().map(e -> e.getValue()).collect(Collectors.toList());
			}
		} else if (CommonTools.isNotEmpty(partType) && CommonTools.isNotEmpty(matType) && CommonTools.isEmpty(matName) && CommonTools.isEmpty(type)) {
			Optional<LinkLovBean> findAny = linkLovList.stream().filter(bean -> bean.getValue().equals(partType)).findAny();
			if (findAny.isPresent()) {
				LinkLovBean partTypeBean = findAny.get();
				Optional<LinkLovBean> findAny2 = partTypeBean.getChilds().stream().filter(bean -> bean.getValue().equals(matType)).findAny();
				if (findAny2.isPresent()) {
					LinkLovBean matTypeBean = findAny2.get();
					List<LinkLovBean> matNameList = matTypeBean.getChilds();
					matNameList.sort(Comparator.comparing(LinkLovBean::getIndex));
					return matNameList.stream().map(e -> e.getValue()).collect(Collectors.toList());
				}
			}
		} else if (CommonTools.isNotEmpty(partType) && CommonTools.isNotEmpty(matType) && CommonTools.isNotEmpty(matName) && CommonTools.isEmpty(type)) {
			Optional<LinkLovBean> findAny = linkLovList.stream().filter(bean -> bean.getValue().equals(partType)).findAny();
			if (findAny.isPresent()) {
				LinkLovBean partTypeBean = findAny.get();
				Optional<LinkLovBean> findAny2 = partTypeBean.getChilds().stream().filter(bean -> bean.getValue().equals(matType)).findAny();
				if (findAny2.isPresent()) {
					LinkLovBean matTypeBean = findAny2.get();
					Optional<LinkLovBean> findAny3 = matTypeBean.getChilds().stream().filter(bean -> bean.getValue().equals(matName)).findAny();
					if (findAny3.isPresent()) {
						LinkLovBean matNameBean = findAny3.get();
						List<LinkLovBean> typeList = matNameBean.getChilds();
						typeList.sort(Comparator.comparing(LinkLovBean::getIndex));
						return typeList.stream().map(e -> e.getValue()).collect(Collectors.toList());
					}
				}
			}
		} else if (CommonTools.isNotEmpty(partType) && CommonTools.isNotEmpty(matType) && CommonTools.isNotEmpty(matName) && CommonTools.isNotEmpty(type)) {
			Optional<LinkLovBean> findAny = linkLovList.stream().filter(bean -> bean.getValue().equals(partType)).findAny();
			if (findAny.isPresent()) {
				LinkLovBean partTypeBean = findAny.get();
				Optional<LinkLovBean> findAny2 = partTypeBean.getChilds().stream().filter(bean -> bean.getValue().equals(matType)).findAny();
				if (findAny2.isPresent()) {
					LinkLovBean matTypeBean = findAny2.get();
					Optional<LinkLovBean> findAny3 = matTypeBean.getChilds().stream().filter(bean -> bean.getValue().equals(matName)).findAny();
					if (findAny3.isPresent()) {
						LinkLovBean matNameBean = findAny3.get();
						Optional<LinkLovBean> findAny4 = matNameBean.getChilds().stream().filter(bean -> bean.getValue().equals(type)).findAny();
						if (findAny4.isPresent()) {
							LinkLovBean typeBean = findAny4.get();
							List<LinkLovBean> materialList = typeBean.getChilds();
							materialList.sort(Comparator.comparing(LinkLovBean::getIndex));	
							materialList.stream().map(e -> new ArrayList<String>(Arrays.asList(e.getValue().split("、")))).forEach(list -> {
								list.forEach(str -> {									
									lovList.add(str);
								});
							});
							
							return lovList.stream().filter(CommonTools.distinctByKey(str -> str)).collect(Collectors.toList());
						}
					}
				}
			}
		}		
		return null;
	}
	
	/**
	 * 下载数据集模板文件
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String downloadTemplate(TCSession session, Registry reg) throws Exception {		
		String filePath = null;
		String preference = TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, PreferenceConstant.D9_DT_PAC_MATERIAL_MAINTAIN);
		if (CommonTools.isEmpty(preference)) {
			TCUtil.warningMsgBox(reg.getString("PreferenceName.MSG") +  PreferenceConstant.D9_DT_PAC_MATERIAL_MAINTAIN + reg.getString("PreferenceErr.MSG") , reg.getString("WARNING.MSG"));
				return null;
		}
		
		TCComponentItem findItem = TCUtil.findItem(preference);
		if (CommonTools.isEmpty(findItem)) {
			System.err.println("首选项为: " +  PreferenceConstant.D9_DT_PAC_MATERIAL_MAINTAIN + ", 值为: " + preference + ", 系统查找对象失败");
			return null;
		}
		
		TCComponentItemRevision itemRev = findItem.getLatestItemRevision();
		TCComponent[] relatedComponents = itemRev.getRelatedComponents("IMAN_specification");
		
		if (CommonTools.isNotEmpty(relatedComponents)) {
			Optional<TCComponent> findAny = Stream.of(relatedComponents).filter(tcComponent -> {
				if (tcComponent instanceof TCComponentDataset) {
					String objectType = tcComponent.getTypeObject().getName();
					return DatasetEnum.MSExcel.type().equals(objectType) || DatasetEnum.MSExcelX.type().equals(objectType);
				}
				return false;
			}).findAny();
			
			if (findAny.isPresent()) {
				TCComponentDataset dataset = (TCComponentDataset) findAny.get();
				
				String dir = CommonTools.getFilePath("DT PAC Material Maintain");
				System.out.println("【INFO】 dir: " + dir);
				CommonTools.deletefile(dir); // 删除文件夹下面的所有文件
				
				String type = dataset.getType();
				String fileExtensions = null;
				String refName = null;
				if (DatasetEnum.MSExcel.type().equals(type)) {
					fileExtensions = DatasetEnum.MSExcel.fileExtensions();
					refName = DatasetEnum.MSExcel.refName();
				} else if (DatasetEnum.MSExcelX.type().equals(type)) {
					fileExtensions = DatasetEnum.MSExcelX.fileExtensions();
					refName = DatasetEnum.MSExcel.refName();
				}
				if (CommonTools.isNotEmpty(fileExtensions) && CommonTools.isNotEmpty(refName)) {
					filePath = TCUtil.downloadFile(dataset, dir, fileExtensions, refName, "", false);
				}
				
				if (CommonTools.isEmpty(filePath)) {
					System.err.println("首选项为: " +  PreferenceConstant.D9_DT_PAC_MATERIAL_MAINTAIN + ", 值为: " + preference + ", 下载DT_PAC_Material_Maintain_Template.xlsx 文件失败");
					return null;
				}
			}
		}
		return filePath;
	}
	
	
	/**
	 * 解析Excel模板文件
	 * @param filePath
	 * @return
	 */
	public static Map<String, List<ExcelBean>> parseExcelTemplate(String filePath) {
		List<List<Object>> tempReadList = null;
		List<List<Object>> matList = new ArrayList<List<Object>>();
		List<List<Object>> matCostList = new ArrayList<List<Object>>();
		Map<String, List<ExcelBean>> retMap = new LinkedHashMap<String, List<ExcelBean>>();
		List<ExcelBean> matBeanList = new ArrayList<ExcelBean>();
		List<ExcelBean> matCostBeanList = new ArrayList<ExcelBean>();
		try {
			ExcelReader reader1 = ExcelUtil.getReader(filePath, "Material");
			tempReadList = reader1.read();
			tempReadList.remove(0);
			
			IoUtil.close(reader1);
			
			matList.addAll(tempReadList);
			
			
			ExcelReader reader2 = ExcelUtil.getReader(filePath, "Material_Cost_Factor");
			tempReadList = reader2.read();
			ListIterator<List<Object>> listIterator = tempReadList.listIterator();
			while (listIterator.hasNext()) {
				List<Object> next = listIterator.next();
				String str = next.toString().replace("[", "").replace("]", "");
				if (!CommonTools.isStartWithNumber(str)) {
					listIterator.remove();
				}
			}
			
			IoUtil.close(reader2);
			
			matCostList.addAll(tempReadList);
			
			int index = 1;
			for (List<Object> read : matList) {
			 String partType = read.get(CommonTools.getColumIntByString("A")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("A")).toString());
			 String matType = read.get(CommonTools.getColumIntByString("B")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("B")).toString());
			 String matName = read.get(CommonTools.getColumIntByString("C")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("C")).toString());
			 String type = read.get(CommonTools.getColumIntByString("D")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("D")).toString());
			 String material = read.get(CommonTools.getColumIntByString("E")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("E")).toString());
			 String length = read.get(CommonTools.getColumIntByString("F")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("F")).toString());
			 String width = read.get(CommonTools.getColumIntByString("G")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("G")).toString());
			 String height = read.get(CommonTools.getColumIntByString("H")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("H")).toString());
			 String thickness = read.get(CommonTools.getColumIntByString("I")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("I")).toString());
			 String density = read.get(CommonTools.getColumIntByString("J")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("J")).toString());
			 String weight = read.get(CommonTools.getColumIntByString("K")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("K")).toString());
			 String usageCalculation = read.get(CommonTools.getColumIntByString("M")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("M")).toString());
			 String calcullationUnit = read.get(CommonTools.getColumIntByString("N")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("N")).toString());
			 
			 ExcelBean bean = new ExcelBean(index, partType, matType, matName, type, material, length, width, height, thickness, density, weight, usageCalculation, calcullationUnit);
			 matBeanList.add(bean);
			 index++;
			}
			
			for (List<Object> read : matCostList) {
				String material = read.get(CommonTools.getColumIntByString("C")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("C")).toString());
				String costFactor = read.get(CommonTools.getColumIntByString("D")) == null ? "" : CommonTools.replaceBlank(read.get(CommonTools.getColumIntByString("D")).toString());
				ExcelBean bean = new ExcelBean(material, costFactor);
				matCostBeanList.add(bean);
			}
			
			retMap.put("matBeanList", matBeanList);
			retMap.put("matCostBeanList", matCostBeanList);
			
		} catch (Exception e) {
			e.printStackTrace();			
		} 
		
		return retMap;
	}
	
	
	private List<MatMaintainBean> getData() {
		try {			
			List<MatMaintainBean> list = recurseBOMLine(selectBomLine, null);
			
			int k =1;
			for (MatMaintainBean bean : list) {
				bean.setIndex(k);
				k++;
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 递归遍历BOMLine
	 * @param topLine
	 * @param list
	 * @throws TCException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private List<MatMaintainBean> recurseBOMLine(TCComponentBOMLine topLine, List<MatMaintainBean> list) throws TCException, IllegalArgumentException, IllegalAccessException {
		if (list == null) {
			list = new ArrayList<MatMaintainBean>();
		}		
		topLine.refresh();
		AIFComponentContext[] children = topLine.getChildren();
		if (CommonTools.isEmpty(children)) {
			TCComponentItemRevision itemRev = topLine.getItemRevision();
//			MatMaintainBean bean = tcPropMapping(new MatMaintainBean(), topLine, "BOMLine");
			MatMaintainBean bean = tcPropMapping(new MatMaintainBean(), itemRev, "ItemRevision");
			bean.setItemId(bean.getItemId() + "/" + bean.getVersion());
			
			selectItemRevBean = new MatMaintainBean();
			TableTools.copyBeanProp(bean, selectItemRevBean);
			
//			MatMaintainBean newBean = (MatMaintainBean) bean.clone();
			itemRev.refresh();
			TCComponent[] relatedComponents = itemRev.getRelatedComponents(MatMaintainConstant.D9_PACMATERIALTABLE);
			for (int i = 0; i < relatedComponents.length; i++) {
				relatedComponents[i].refresh();
				MatMaintainBean newBean =  new MatMaintainBean();
				TableTools.copyBeanProp(bean, newBean);
				newBean = tcPropMapping(newBean, relatedComponents[i], "TCComponentFnd0TableRow");
				newBean.setRow((TCComponentFnd0TableRow)relatedComponents[i]);
//				newBean.setItemRev(itemRev);
				newBean.setUUID(CommonTools.getUUID()); // 生成一个随机的UUID
				if (selectBomLine.hasChildren()) { // 判断当前选中的是否为叶子节点
					StringBuffer buffer = new StringBuffer("");
					getTotalQtyByLeaf(topLine, buffer);
					System.out.println(buffer.toString());
					String totalQtyByLeaf = newBean.getQty() + buffer.toString();
					newBean.setQty(totalQtyByLeaf);
					
				} 
//				else {
//					newBean.setQty(newBean.getTableQty()); // 重新设置用量
//				}
				
				TableTools.setDisableEditorColumnValue(newBean, matBeanList); // 设置不能编列的属性值
				list.add(newBean);
			}
			
		} else {
			for (AIFComponentContext e : children) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) e.getComponent();
				recurseBOMLine(bomLine, list);
			}
		}
		
		return list;
	}
	
	
	
	/**
	 * 设置table表用量和BOMLine行用量的总和
	 * @param bean
	 * @return
	 */
//	private String getTotalQty(MatMaintainBean bean) {
//		String bomQty = bean.getBomQty();
//		String tableQty = bean.getTableQty();		
//		
//		if (CommonTools.isEmpty(bomQty) && CommonTools.isEmpty(tableQty)) {
//			return null;
//		} else if (CommonTools.isNotEmpty(bomQty) && CommonTools.isEmpty(tableQty)) {
//			return bomQty;
//		} else if (CommonTools.isEmpty(bomQty) && CommonTools.isNotEmpty(tableQty)) {
//			return tableQty;
//		} else {
//			String value = NumberUtil.mul(bomQty, tableQty).stripTrailingZeros().toPlainString();			
//			int decimalPlaces = CommonTools.getDecimalPlaces(value);
//			if (decimalPlaces < 6) {
//				return value; 
//			} else {
//				return CommonTools.formatDecimal(value, 6); // 小数超过六位,则只保留六位小数
//			}
//		}		
//	}
	
	
	/**
	 * 通过从叶子节点从下往上设置获取所有的用量，用X进行分割
	 * @param bomLine
	 * @param totalQty
	 * @return
	 * @throws TCException
	 */
	private void getTotalQtyByLeaf(TCComponentBOMLine bomLine, StringBuffer buffer) throws TCException {
		if (bomLine.getUid().equals(selectBomLine.getUid())) { // 如果和选中的BOMLine相同,不能算当前选中BOMLine的用量
			return;
		}
		
		if (CommonTools.isNotEmpty(bomLine.parent())) {
			String qty = bomLine.getProperty("bl_quantity");
			if (CommonTools.isNotEmpty(qty)) {
				BigDecimal bigDecimal = new BigDecimal(qty);
				buffer.append("*").append(String.valueOf(bigDecimal.stripTrailingZeros().toPlainString()));				
//				totalQty = totalQty + "*" + String.valueOf(bigDecimal.stripTrailingZeros().toPlainString());
				getTotalQtyByLeaf(bomLine.parent(), buffer);
			}			
		}		
	}
	
	
	/**
	 * 返回BOMLine
	 * @return
	 * @throws TCException 
	 */

	private TCComponentBOMLine getSelectBOMLine() {
		TCComponentBOMLine bomLine = null;
		try {
			bomLine = (TCComponentBOMLine) app.getTargetComponents()[0];
			bomLine.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return bomLine;
	}
	
	
	/**
	 * 设置进度条
	 * @param tableComposite
	 * @param statusName
	 * @param saveFlag
	 */

	public void setUpStatusBar(Composite tableComposite, String statusName, boolean saveFlag) {
		if (!saveFlag) {
			statusBar = new Composite(tableComposite, SWT.NONE);
			statusBar.setLayout(new GridLayout(2, false));
			statusBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));			
			status = new Label(statusBar, SWT.NONE);			
		} else {
			status.setVisible(true);
		}
		
		status.setText(statusName);
		
		progressBar = new ProgressBar(statusBar, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressBar.setMaximum(100);
		
		if (saveFlag) {
			statusBar.layout();
		}
	}
	
	
	/**
	 * 设置下拉值
	 * @param combo
	 * @param list
	 * @param index
	 */
	public void setComboValues(Combo combo, List<String> list, int index) {
		combo.removeAll(); // 清除所有的下拉值
		for (int i = 0; i < list.size(); i++) {
			combo.add(list.get(i));
		}
		
		combo.select(index); // 设置当前项
	}
	
	
	/**
	 * 创建GridData布局
	 * @param style
	 * @param horizontalSpan
	 * @return
	 */
	private GridData createGridData(int style, int horizontalSpan, int horizontalAlignment) {
		GridData gridData = new GridData(style);
		gridData.horizontalSpan = horizontalSpan;
		gridData.horizontalAlignment = horizontalAlignment;
		return gridData;
	}

	/**
	 * 返回下拉框的索引
	 * @param list
	 * @param selectValue
	 * @return
	 */
	public int getIndex(List<String> list, String value) {
		return IntStream.range(0, list.size()).filter(i -> value.equals(list.get(i))).findFirst().orElse(0);
	}
	
	
	public static <T> T tcPropMapping(T bean, TCComponent tcComponent, String objectType)
			throws IllegalArgumentException, IllegalAccessException, TCException {
		if (bean != null && tcComponent != null) {			
			Field[] fields = bean.getClass().getDeclaredFields();
			List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
			fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
			fieldList.removeIf(field -> field.getType() == boolean.class);
			for (Field field : fieldList) {
				field.setAccessible(true);
				TCPropertes tcPropName = field.getAnnotation(TCPropertes.class);
				if (tcPropName != null) {
					String propertyName = tcPropName.tcProperty();
					String tcType = tcPropName.tcType();
					Object val = null;
					if (CommonTools.isNotEmpty(propertyName)) {	
						if (tcType.equals(objectType)) {
							if (tcComponent.isValidPropertyName(propertyName)) {
								val = tcComponent.getProperty(propertyName);
								if (propertyName.startsWith("bl_quantity")) {
									if (CommonTools.isNotEmpty(val)) {
										BigDecimal bigDecimal = new BigDecimal(val.toString());
										val = String.valueOf(bigDecimal.stripTrailingZeros().toPlainString());
									}
								}
								field.set(bean, val);
							} else {
								System.out.println(propertyName + " propertyName is not exist ");
							}	
						}											
					}					
				}
			}
		}		
		return bean;
	}


	
	
	public List<MatMaintainBean> getDataList() {
		return dataList;
	}


	public void setDataList(List<MatMaintainBean> dataList) {
		this.dataList = dataList;
	}

	public String getPartType() {
		return partType;
	}


	public void setPartType(String partType) {
		this.partType = partType;
	}

	public String getCurMatType() {
		return curMatType;
	}


	public void setCurMatType(String curMatType) {
		this.curMatType = curMatType;
	}


	public String getCurMatName() {
		return curMatName;
	}


	public void setCurMatName(String curMatName) {
		this.curMatName = curMatName;
	}


	public List<String> getTypeList() {
		return typeList;
	}


	public void setTypeList(List<String> typeList) {
		this.typeList = typeList;
	}


	public List<String> getMatList() {
		return matList;
	}


	public void setMatList(List<String> matList) {
		this.matList = matList;
	}


	public List<ExcelBean> getMatBeanList() {
		return matBeanList;
	}


	public void setMatBeanList(List<ExcelBean> matBeanList) {
		this.matBeanList = matBeanList;
	}

	
	public List<ExcelBean> getMatCostBeanList() {
		return matCostBeanList;
	}


	public void setMatCostBeanList(List<ExcelBean> matCostBeanList) {
		this.matCostBeanList = matCostBeanList;
	}


	public String[] getTableTitle() {
		return tableTitle;
	}


	public void setTableTitle(String[] tableTitle) {
		this.tableTitle = tableTitle;
	}


	public List<MatMaintainBean> getCacheDataList() {
		return cacheDataList;
	}


	public void setCacheDataList(List<MatMaintainBean> cacheDataList) {
		this.cacheDataList = cacheDataList;
	}

	
	public Label getTotalMatCostLabel() {
		return totalMatCostLabel;
	}


	public void setTotalMatCostLabel(Label totalMatCostLabel) {
		this.totalMatCostLabel = totalMatCostLabel;
	}


	public Text getTotalMatCostText() {
		return totalMatCostText;
	}


	public void setTotalMatCostText(Text totalMatCostText) {
		this.totalMatCostText = totalMatCostText;
	}


	public TCComponentBOMLine getSelectBomLine() {
		return selectBomLine;
	}


	public void setSelectBomLine(TCComponentBOMLine selectBomLine) {
		this.selectBomLine = selectBomLine;
	}


	public MatMaintainBean getSelectItemRevBean() {
		return selectItemRevBean;
	}


	public void setSelectItemRevBean(MatMaintainBean selectItemRevBean) {
		this.selectItemRevBean = selectItemRevBean;
	}
	
	
	
}
