package com.foxconn.electronics.L10Ebom.dialog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.map.HashedMap;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import com.foxconn.electronics.L10Ebom.constant.ApplyFormConstant;
import com.foxconn.electronics.L10Ebom.domain.BOMApplyRowBean;
import com.foxconn.electronics.L10Ebom.domain.BOMApplyToTableBean;
import com.foxconn.electronics.L10Ebom.domain.EBOMApplyRowBean;
import com.foxconn.electronics.L10Ebom.domain.EEToTableBean;
import com.foxconn.electronics.L10Ebom.domain.EENewBOMRowBean;
import com.foxconn.electronics.L10Ebom.domain.FinishBOMRowBean;
import com.foxconn.electronics.L10Ebom.domain.FinishBOMToTableBean;
import com.foxconn.electronics.L10Ebom.domain.FinishMatRowBean;
import com.foxconn.electronics.L10Ebom.domain.HalfMatToTableBean;
import com.foxconn.electronics.L10Ebom.domain.METoTableBean;
import com.foxconn.electronics.L10Ebom.domain.HalfMatRowBean;
import com.foxconn.electronics.L10Ebom.domain.MENewBOMRowBean;
import com.foxconn.electronics.L10Ebom.domain.MatToTableBean;
import com.foxconn.electronics.L10Ebom.domain.PowerToTableBean;
import com.foxconn.electronics.L10Ebom.domain.PowerCordRowBean;
import com.foxconn.electronics.L10Ebom.domain.PowerNewBOMRowBean;
import com.foxconn.electronics.L10Ebom.domain.RDEBOMRowBean;
import com.foxconn.electronics.L10Ebom.domain.RDEBOMToTableBean;
import com.foxconn.electronics.L10Ebom.editor.MyEditingSupport;
import com.foxconn.electronics.L10Ebom.rightbtn.MyActionGroup;
import com.foxconn.electronics.L10Ebom.util.TableTools;
import com.foxconn.mechanism.jurisdiction.CustTreeNode;
import com.foxconn.mechanism.jurisdiction.JurisdictionChangeInfo;
import com.foxconn.tcutils.constant.ColorEnum;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

public class CustTableItem {
	
//	private Shell shell = null;
	private Shell parent = null;
	private TCSession session = null;
	private TCComponentItemRevision itemRev = null;
	private TabFolder tabFolder;
	private Composite allComposite = null;
	private TabItem tabItem = null;
	private TableViewer tv;
	private Table table;
	private TableLayout tableLayout = null;
	private Button saveBtn = null;
	private Button cancelBtn = null;
	private Button addBtn = null;
	private Button deleteBtn = null;
	private Button copyBtn = null;
	private Composite statusBar = null;
	private Label status = null;
	private ProgressBar progressBar = null;
	private MyActionGroup actionGroup = null;
	private String tablePropName = null;
	private String tableRowType = null;
	private List<String> matToList = null;
	private List<String> editorControllerList = null;
	private String groupName = null;
	private String title = null;	;
	private String[] tableTitles = null;
	private MyEditingSupport myEditingSupport = null;
	private List<MyEditingSupport> editList = null;
	private ButtonSelectionListener buttonSelectionListener = null;
	private Registry reg = null;
	private String key = null;	
	private L10EBOMApplyFormDialog formDialog = null;
	private List<String> finishCustomerList = null;
	private List<String> shipList = null;
	private List<String> powerLineTypeList = null;
	private List<String> PCBAInterfaceList = null;
	private List<String> colorList = null;
	private List<String> wireTypeList = null;
	private List<String> shipSizeList = null;
	private List<String> shipTypeList = null;
	private static final String D9_L10_FINISH_CUSTOMER_LOV = "D9_L10_Finish_Customer_Lov";
	private static final String D9_L10_FINISH_POWERLINETYPE_LOV = "D9_L10_Finish_PowerLineType_Lov";
	private static final String D9_L10_FINISH_PCBAINTERFACE_LOV = "D9_L10_Finish_PCBAInterface_Lov";
	private static final String D9_L10_FINISH_COLOR = "D9_L10_Finish_Color";
	private static final String D9_L10_FINISH_WIRETYPE_LOV = "D9_L10_Finish_WireType_Lov";
	private static final String D9_L10_FINISH_SHIPSIZE_LOV = "D9_L10_Finish_ShipSize_Lov";
	private static final String D9_L10_FINISH_SHIPTYPE_LOV = "D9_L10_Finish_ShipType_Lov";
	private String customerName = null;
	
	public CustTableItem(Shell parent, TCSession session, TCComponentItemRevision itemRev, Registry reg, TabFolder tabFolder, TabItem tabItem, String tablePropName, 
			String tableRowType, String title, List<String> matToList, List<String> editorControllerList, String groupName, L10EBOMApplyFormDialog formDialog) {
		super();
		this.parent = parent;
		this.session = session;
		this.itemRev = itemRev;
		this.reg = reg;
		this.tabFolder = tabFolder;
		this.tabItem = tabItem;
		this.tablePropName = tablePropName;
		this.tableRowType = tableRowType;
		this.title = title;		
		this.matToList = matToList;
		this.editorControllerList = editorControllerList;
		this.groupName = groupName;
		this.formDialog = formDialog;
		editList = new ArrayList<MyEditingSupport>();
//		tableTitles = list.get(0).getTableTitles();
		initUI();
	}
	
	private void initUI() {
		allComposite = new Composite(tabFolder, SWT.NONE); 
		allComposite.setLayout(new GridLayout(1, false));
		tabItem.setControl(allComposite);	
		
		
		Composite topBtnComposite = new Composite(allComposite, SWT.NONE);
		GridData topBtndata = new GridData(GridData.FILL_HORIZONTAL);
		topBtndata.horizontalAlignment = GridData.BEGINNING; 		
		topBtnComposite.setLayoutData(topBtndata);
		
		RowLayout topRowLayout = new RowLayout(); 
		topRowLayout.spacing = 15;
		
		topBtnComposite.setLayout(topRowLayout);
		
		RowData topBtnRowData = new RowData();
		topBtnRowData.width = 60;
		topBtnRowData.height = 30;
		
		addBtn = new Button(topBtnComposite, SWT.PUSH);
		addBtn.setText(reg.getString("AddBtn.LABEL"));
		addBtn.setLayoutData(topBtnRowData);

		copyBtn = new Button(topBtnComposite, SWT.PUSH);
		copyBtn.setText(reg.getString("CopyBtn.LABEL"));
		copyBtn.setLayoutData(topBtnRowData);
		
		deleteBtn = new Button(topBtnComposite, SWT.PUSH);
		deleteBtn.setText(reg.getString("DeleteBtn.LABEL"));
		deleteBtn.setLayoutData(topBtnRowData);
		
		
		Composite tableComposite = new Composite(allComposite, SWT.NONE);
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
		
		
		key = tablePropName;
		
		if (!ApplyFormConstant.TABLEMAP.containsKey(key)) {
			TCUtil.infoMsgBox(title + reg.getString("MissTableHeadErr.MSG"), reg.getString("INFORMATION.MSG"));
			return;
		}
		
		tableTitles = ApplyFormConstant.TABLEMAP.get(key);
		createTreeColumn(); // 创建列	
		
		if (tablePropName.equals(ApplyFormConstant.D9_BOMREQTABLE_1)) { // 成品料号申请信息(申请人信息)
			actionGroup = new MyActionGroup(tv, reg, tablePropName, this);
			actionGroup.fillContextMenu(new MenuManager()); // 将按钮注入到菜单对象中
		}
		
		
		Composite bottomBtnComposite = new Composite(allComposite, SWT.NONE);		
		GridData bottomBtndata = new GridData(GridData.FILL_HORIZONTAL);
		bottomBtndata.horizontalAlignment = GridData.CENTER; 		
		bottomBtnComposite.setLayoutData(bottomBtndata);
		
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
		
		buttonSelectionListener = new ButtonSelectionListener(allComposite, tableComposite, tv, parent, session, reg, itemRev, tablePropName, tableRowType, this);
		addBtn.addSelectionListener(buttonSelectionListener);
		deleteBtn.addSelectionListener(buttonSelectionListener);
		
		saveBtn.addSelectionListener(buttonSelectionListener);
		copyBtn.addSelectionListener(buttonSelectionListener);
		cancelBtn.addSelectionListener(buttonSelectionListener);
		
		
		table.setEnabled(false);
		addBtn.setEnabled(false);
		copyBtn.setEnabled(false);
		deleteBtn.setEnabled(false);
		saveBtn.setEnabled(false);
		cancelBtn.setEnabled(false);
		setUpStatusBar(tableComposite, reg.getString("ProgressBar1.TITLE"), false);
		updateProgressBarByLoad(allComposite);	
			
	}
	
	
	/**
	 * 创建表格的列并设置列宽
	 */
	private void createTreeColumn() {
		
		for (int index = 0; index < tableTitles.length; index ++) {
			TableViewerColumn tvc = new TableViewerColumn(tv, SWT.CENTER, index);
			TableColumn tableColumn = tvc.getColumn();
			tableColumn.setText(tableTitles[index]);
			int columnWidth = 0;
			if (index == 0) {
				columnWidth = 50;
			} else if (index == 1 || index == 2 || index == 4) {
				columnWidth = 150;
			} else if (index == 7 || index == 8) {
				columnWidth = 100;
			} else if (index == 3) {
				columnWidth = 400;	
			} else if (index == 9 || index == 10) {
				columnWidth = 200;
			} else if (index == 14) {
				columnWidth = 120;
			} else {
				columnWidth = 320;
			}
			
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
			
			myEditingSupport = new MyEditingSupport(tv, allComposite, tablePropName, title, index, tableTitles[index], editorControllerList, groupName, shipList, powerLineTypeList, PCBAInterfaceList, colorList);
			editList.add(myEditingSupport);
			tvc.setEditingSupport(myEditingSupport);
		}		
	}
	
	
 	private List<EBOMApplyRowBean> getData() {
		List<EBOMApplyRowBean> list = new ArrayList<EBOMApplyRowBean>();		
		try {
			List<MatToTableBean> matInfoList = null;
//			List<MatToTableBean> matInfoList = getMatInfoList();
			
			TCComponent[] relatedComponents = itemRev.getRelatedComponents(tablePropName);
			
			switch (tablePropName) {
				case ApplyFormConstant.D9_BOMREQTABLE_1:
					for (TCComponent tcComponent : relatedComponents) {
						TCComponentFnd0TableRow row = (TCComponentFnd0TableRow)tcComponent;
						
						EBOMApplyRowBean bean = new FinishMatRowBean();
						bean = tcPropMapping(bean, row);						
						if (CommonTools.isNotEmpty(bean)) {
			 				bean.setRow(row);	 			
				 			list.add(bean);
						}
					}
					
					break;
				case ApplyFormConstant.D9_BOMREQTABLE_2:					
					TableTools.halfMatToTable(list, matInfoList, relatedComponents);
					break;					
				case ApplyFormConstant.D9_BOMREQTABLE_3:
					TableTools.BOMApplyMatToTable(list, matInfoList, relatedComponents);
					break;
				case ApplyFormConstant.D9_BOMREQTABLE_4:
					TableTools.RDEBOMMatToTable(list, matInfoList, relatedComponents);
					break;
				case ApplyFormConstant.D9_BOMREQTABLE_5:
					TableTools.MEMatToTable(list, matInfoList, relatedComponents);
					break;
				case ApplyFormConstant.D9_BOMREQTABLE_6:
					TableTools.EEMatToTable(list, matInfoList, relatedComponents);
					break;
				case ApplyFormConstant.D9_BOMREQTABLE_7:
					TableTools.PowerMatToTable(list, matInfoList, relatedComponents);					
					break;
					
				case ApplyFormConstant.D9_BOMREQTABLE_8:
					for (TCComponent tcComponent : relatedComponents) {
						TCComponentFnd0TableRow row = (TCComponentFnd0TableRow)tcComponent;
						EBOMApplyRowBean bean = new PowerCordRowBean();;
						bean = tcPropMapping(bean, row);
						if (CommonTools.isNotEmpty(bean)) {
			 				bean.setRow(row);	 			
				 			list.add(bean);
						}
					}
					break;
				case ApplyFormConstant.D9_BOMREQTABLE_9:					
					while (true) {
						if (formDialog.getTotalTableItemList().size() == formDialog.getPropMappTitle().size()) { // 判断tableItem页是否已经加载出来
							matInfoList = TableTools.syncFinishMatAndHalfMatInfo(session, itemRev, formDialog.getTotalTableItemList(), formDialog.getMatToList());
							break;
						}
					}
					
					TableTools.finishBOMToTable(list, matInfoList, relatedComponents);					
					break;				
				}			
			
			int k = 0;
			if (CommonTools.isNotEmpty(list)) {
				for (EBOMApplyRowBean bean : list) {
					k++;
					if (bean.getSequence().intValue() != k) {
						bean.setSequence(k);
						bean.setHasModify(true);
					}
					
				}
				
				list.sort(Comparator.comparing(EBOMApplyRowBean::getSequence)); // 按照项次进行升序
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
		return list;
 	}
	
 	
 	private boolean checkPreCondition() {
 		MyEditingSupport editor = null;
 		try {
 			finishCustomerList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, D9_L10_FINISH_CUSTOMER_LOV); // 获取首选项
 			if (CommonTools.isEmpty(finishCustomerList)) {
 				TCUtil.warningMsgBox(reg.getString("PreferenceName.MSG") +  D9_L10_FINISH_CUSTOMER_LOV + reg.getString("PreferenceErr.MSG") , reg.getString("WARNING.MSG"));
 				return false;
 			}
 			
 			powerLineTypeList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, D9_L10_FINISH_POWERLINETYPE_LOV); // 获取首选项
 			if (CommonTools.isEmpty(powerLineTypeList)) {
 				TCUtil.warningMsgBox(reg.getString("PreferenceName.MSG") +  D9_L10_FINISH_POWERLINETYPE_LOV + reg.getString("PreferenceErr.MSG") , reg.getString("WARNING.MSG"));
 				return false;
 			}
 			
 			editor = getEditingSupport(ApplyFormConstant.POWERLINETYPE);
 			editor.setPowerLineTypeList(powerLineTypeList);
 			
 			PCBAInterfaceList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, D9_L10_FINISH_PCBAINTERFACE_LOV); // 获取首选项
 			if (CommonTools.isEmpty(PCBAInterfaceList)) {
 				TCUtil.warningMsgBox(reg.getString("PreferenceName.MSG") +  D9_L10_FINISH_PCBAINTERFACE_LOV + reg.getString("PreferenceErr.MSG") , reg.getString("WARNING.MSG"));
 				return false;
 			}
 			
 			editor = getEditingSupport(ApplyFormConstant.PCBAINTERFACE);
 			editor.setPCBAInterfaceList(PCBAInterfaceList);
 			
 			colorList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, D9_L10_FINISH_COLOR); // 获取首选项
 			if (CommonTools.isEmpty(colorList)) {
 				TCUtil.warningMsgBox(reg.getString("PreferenceName.MSG") +  D9_L10_FINISH_COLOR + reg.getString("PreferenceErr.MSG") , reg.getString("WARNING.MSG"));
 				return false;
 			}
 			
 			editor = getEditingSupport(ApplyFormConstant.COLOR);
 			editor.setColorList(colorList);			
 			
 			try {
 				customerName = getCustomerName(); // 获取客户名称
 			} catch (TCException e) {
 				e.printStackTrace();
 			}
 			
 			
 			if (CommonTools.isEmpty(customerName)) {
 				TCUtil.warningMsgBox(reg.getString("LoadingWarn1.MSG"), reg.getString("WARNING.MSG"));
 				return false;
 			}
 			
 			shipList = getShipList(); // 获取出货地
			if (CommonTools.isEmpty(shipList)) {				
				TCUtil.errorMsgBox(title + reg.getString("LoadingWarn2.MSG"), reg.getString("ERROR.MSG"));
				return false;
			}
			
			editor = getEditingSupport(ApplyFormConstant.SHIP);
			editor.setShipList(shipList);
			
			wireTypeList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, D9_L10_FINISH_WIRETYPE_LOV);
 			if (CommonTools.isEmpty(wireTypeList)) {
 				TCUtil.warningMsgBox(reg.getString("PreferenceName.MSG") +  D9_L10_FINISH_WIRETYPE_LOV + reg.getString("PreferenceErr.MSG") , reg.getString("WARNING.MSG"));
 				return false;
 			}
 			
 			editor = getEditingSupport(ApplyFormConstant.WIRETYPE);
 			editor.setWireTypeList(wireTypeList);
 			
 			
 			shipSizeList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, D9_L10_FINISH_SHIPSIZE_LOV);
 			if (CommonTools.isEmpty(shipSizeList)) {
 				TCUtil.warningMsgBox(reg.getString("PreferenceName.MSG") +  D9_L10_FINISH_SHIPSIZE_LOV + reg.getString("PreferenceErr.MSG") , reg.getString("WARNING.MSG"));
 				return false;
 			}
 			
 			editor = getEditingSupport(ApplyFormConstant.SHIPSIZE);
 			editor.setShipSizeList(shipSizeList); 			
 			
 			shipTypeList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, D9_L10_FINISH_SHIPTYPE_LOV);
 			if (CommonTools.isEmpty(shipTypeList)) {
 				TCUtil.warningMsgBox(reg.getString("PreferenceName.MSG") +  D9_L10_FINISH_SHIPTYPE_LOV + reg.getString("PreferenceErr.MSG") , reg.getString("WARNING.MSG"));
 				return false;
 			}
 			
 			editor = getEditingSupport(ApplyFormConstant.SHIPTYPE);
 			editor.setShipTypeList(shipTypeList); 			
 			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
 		
 		return true;
 	}
 	
 	
 	private MyEditingSupport getEditingSupport(String columnName) {
 		Optional<MyEditingSupport> findAny = editList.stream().filter(e -> columnName.equals(e.getColumnName().replace("*", "").replace(" ", ""))).findAny();
 		if (findAny.isPresent()) {
			return findAny.get();
		}
 		return null;
 	}
 	
 	/**
 	 * 获取客户名称
 	 * @return
 	 * @throws TCException
 	 */
	private String getCustomerName() throws TCException {
		return itemRev.getProperty("d9_Customer");
	}
	
	private List<String> getShipList() {
 		finishCustomerList.removeIf(str -> !customerName.equalsIgnoreCase(str.split("=")[0]));
 		if (CommonTools.isEmpty(finishCustomerList)) {
			return null;
		}
 		return finishCustomerList.stream().map(e -> e.toUpperCase().split("=")[1]).collect(Collectors.toList());
 	}
	
	
	private List<MatToTableBean> getMatInfoList() throws TCException, IllegalArgumentException, IllegalAccessException {
		String result = TableTools.getFind(matToList, tablePropName);
		if (CommonTools.isEmpty(result)) {
			return null;
		}
		
		String folderPropName = result.split("=")[1];
		TCComponent[] relatedComponents = itemRev.getRelatedComponents(folderPropName);
		List<MatToTableBean> resultList = new ArrayList<MatToTableBean>();
		List<TCComponent> filteredList = null;
		switch (tablePropName) {
			case ApplyFormConstant.D9_BOMREQTABLE_2:
				filteredList = TableTools.filterComponentList(relatedComponents, ApplyFormConstant.D9_VIRTUALPART_REV, ApplyFormConstant.HALFMATARRAY);
				if (CommonTools.isNotEmpty(filteredList)) {
					MatToTableBean bean = null;
					for (TCComponent component : filteredList) {
						bean = new HalfMatToTableBean();
						bean = tcPropMapping(bean, component);
						if (bean.getSemiPN().startsWith(ApplyFormConstant.HALFMATARRAY[0])) {
							bean.setSemiType(ApplyFormConstant.FIANL_ASSY);
						} else if (bean.getSemiPN().startsWith(ApplyFormConstant.HALFMATARRAY[1])) {
							bean.setSemiType(ApplyFormConstant.PANEL_ASSY);
						}
						
						resultList.add(bean);
					}					
				}
				break;
			case ApplyFormConstant.D9_BOMREQTABLE_3:
				filteredList = TableTools.filterComponentList(relatedComponents, ApplyFormConstant.D9_FINISHEDPART_REV, ApplyFormConstant.FINISHBOMARRAY);	
				if (CommonTools.isNotEmpty(filteredList)) {
					MatToTableBean bean = null;
					for (TCComponent component : filteredList) {
						bean = new BOMApplyToTableBean();
						bean = tcPropMapping(bean, component);
						
						resultList.add(bean);
					}
				}
				break;
			case ApplyFormConstant.D9_BOMREQTABLE_4:
				filteredList = TableTools.filterComponentList(relatedComponents, ApplyFormConstant.D9_VIRTUALPART_REV, ApplyFormConstant.PACKASSYMATARRAY);
				if (CommonTools.isNotEmpty(filteredList)) {
					MatToTableBean bean = null;
					for (TCComponent component : filteredList) {
						bean = new RDEBOMToTableBean();
						bean = tcPropMapping(bean, component);
						
						resultList.add(bean);
					}
				}
				break;
			case ApplyFormConstant.D9_BOMREQTABLE_5:
				filteredList = TableTools.filterComponentList(relatedComponents, ApplyFormConstant.D9_COMMONPART_REV, ApplyFormConstant.MEMATARRAY);
				if (CommonTools.isNotEmpty(filteredList)) {
					MatToTableBean bean = null;
					for (TCComponent component : filteredList) {
						bean = new METoTableBean();
						bean = tcPropMapping(bean, component);
						
						resultList.add(bean);
					}
				}
				break;
			case ApplyFormConstant.D9_BOMREQTABLE_6:
				filteredList = TableTools.filterComponentList(relatedComponents, ApplyFormConstant.D9_PCA_PART_REV, ApplyFormConstant.EEMATARRAY);
				if (CommonTools.isNotEmpty(filteredList)) {
					MatToTableBean bean = null;
					for (TCComponent component : filteredList) {
						bean = new EEToTableBean();
						bean = tcPropMapping(bean, component);
						
						resultList.add(bean);
					}
				}
				break;
			case ApplyFormConstant.D9_BOMREQTABLE_7:
				filteredList = TableTools.filterComponentList(relatedComponents, ApplyFormConstant.D9_PCA_PART_REV, ApplyFormConstant.POWERMATARRAY);
				if (CommonTools.isNotEmpty(filteredList)) {
					MatToTableBean bean = null;
					for (TCComponent component : filteredList) {
						bean = new PowerToTableBean();
						bean = tcPropMapping(bean, component);
						
						resultList.add(bean);
					}
				}
				break;
			default:
				break;
			
		}
		
		return resultList;
	}
	   
	
	
	/**
	 * 设置进度条
	 * @param tableComposite
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
	 * 更新进度条信息
	 */

	private void updateProgressBarByLoad(Composite allComposite) {
		new Thread(new Runnable() {
			private int progress = 0;
			private static final int INCREMENT = 10;
			private boolean flag = false; // 数据接口获取成功与否接口
			private List<EBOMApplyRowBean> dataList = null;
			
			@Override
			public void run() {
				new Thread() {
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						if (!checkPreCondition()) { // 前置条件校验
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									status.setVisible(false);
									progressBar.dispose(); // 销毁进度条组件
								}
							});	
							
							return;
						}						
						
						dataList = getData(); // 获取数据的逻辑
						if (dataList == null) {
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									status.setVisible(false);
									progressBar.dispose(); // 销毁进度条组件
								}
							});	
							TCUtil.errorMsgBox(title + reg.getString("DataLoadingErr.MSG"), reg.getString("ERROR.MSG"));							
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
								if (!parent.isDisposed()) {
									status.setVisible(false);
									tv.setContentProvider(new MaterialTableViewerContentProvider()); // 设置TableViewer的内容提供者和标签提供者
						    		tv.setLabelProvider(new MaterialTableViewerLabelProvider());
						    		
					    			tv.setInput(dataList); // 设置数据并刷新TableViewer
						            tv.refresh();							            					    		
															            
						    		
						    		boolean anyMatch = TableTools.anyMatchGroup(editorControllerList, groupName, tablePropName); // 判断当前用户是否对此表格有编辑权
						    		if (anyMatch) {
						    			tv.getTable().setEnabled(true); // 恢复用户输入
							            addBtn.setEnabled(true);
							            copyBtn.setEnabled(true);
							    		deleteBtn.setEnabled(true);
							    		saveBtn.setEnabled(true);
							    		cancelBtn.setEnabled(true);
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
	
	
	public void updateProgressBarBySave(Composite allComposite, List<EBOMApplyRowBean> list) {
		new Thread(new Runnable() {
			private int progress = 0;
			private static final int INCREMENT = 10;
			private boolean flag = false; // 数据接口获取成功与否接口
			private boolean saveFlag = false; // 用作是否保存成功标识
			private List<EBOMApplyRowBean> dataList = null;
			@Override
			public void run() {
				new Thread() {
					public void run() {	
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (!buttonSelectionListener.saveData(list)) { // 保存数据
							Display.getDefault().syncExec(new Runnable() {
								public void run() {
									status.setVisible(false);
									progressBar.dispose(); // 销毁进度条组件
								}
							});	
							TCUtil.errorMsgBox(title + reg.getString("DataSaveErr.MSG"), reg.getString("ERROR.MSG"));							
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
								TCUtil.errorMsgBox(title + reg.getString("DataReLoadingErr.MSG"), reg.getString("ERROR.MSG"));							
								return;
							}
						}
						
						flag = true;						
					}
				}.start();
				
				while (!progressBar.isDisposed()) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							if (flag) {
								if (!parent.isDisposed()) {
									status.setVisible(false);
									tv.setContentProvider(new MaterialTableViewerContentProvider()); // 设置TableViewer的内容提供者和标签提供者
						    		tv.setLabelProvider(new MaterialTableViewerLabelProvider());						    		
						    		
						    		tv.setInput(dataList); // 设置数据并刷新TableViewer
						            tv.refresh();
						    		
						    		tv.getTable().setEnabled(true); // 恢复用户输入
						    		saveBtn.setEnabled(true);
						    		cancelBtn.setEnabled(true);		
						    		
						    		progressBar.dispose(); // 销毁进度条组件
						    		
						    		saveFlag = true;
								}
								
								if (saveFlag) {
									TCUtil.infoMsgBox(title + reg.getString("DataSaveSucc.MSG"), reg.getString("INFORMATION.MSG"));		
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
	
	public static <T> T tcPropMapping(T bean, TCComponent tcObject)
			throws IllegalArgumentException, IllegalAccessException, TCException {
		if (bean != null && tcObject != null) {
			Field[] fields = bean.getClass().getDeclaredFields();
			List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
			fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
			fieldList.removeIf(field -> field.getType() == boolean.class);
			for (Field field : fieldList) {
				field.setAccessible(true);
				TCPropertes tcPropName = field.getAnnotation(TCPropertes.class);
				if (tcPropName != null) {
					String propertyName = tcPropName.tcProperty();
					Object val = null;
//					tcObject.refresh();
					if (CommonTools.isNotEmpty(propertyName)) {
						if (tcObject.isValidPropertyName(propertyName)) { // 判断属性是否存在
							val = tcObject.getProperty(propertyName);							
							if (field.getType() == Integer.class) {
								if ("".equals(val) || val == null) {
									val = "";
								} else {
									val = Integer.parseInt((String) val);
								}
							}
							field.set(bean, val);
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
	
	
	
	public TableViewer getTv() {
		return tv;
	}

	public void setTv(TableViewer tv) {
		this.tv = tv;
	}

	public String getTablePropName() {
		return tablePropName;
	}

	public void setTablePropName(String tablePropName) {
		this.tablePropName = tablePropName;
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(ProgressBar progressBar) {
		this.progressBar = progressBar;
	}
	

	public Composite getAllComposite() {
		return allComposite;
	}

	public void setAllComposite(Composite allComposite) {
		this.allComposite = allComposite;
	}

	public Button getSaveBtn() {
		return saveBtn;
	}

	public void setSaveBtn(Button saveBtn) {
		this.saveBtn = saveBtn;
	}

	public Button getCancelBtn() {
		return cancelBtn;
	}

	public void setCancelBtn(Button cancelBtn) {
		this.cancelBtn = cancelBtn;
	}
	
}
