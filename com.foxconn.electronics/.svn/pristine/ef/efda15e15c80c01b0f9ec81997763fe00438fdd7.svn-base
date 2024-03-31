package com.foxconn.electronics.dcnreport.batcheditornewmoldfee.dialog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.formula.functions.T;
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

import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.constant.NewMoldFeeConstant;
import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.domain.NewMoldFeeBean;
import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.editor.MyEditingSupport;
import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.listener.ButtonSelectionListener;
import com.foxconn.tcutils.constant.ColorEnum;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

public class BatchEditorNewMoldFeeDialog extends Dialog {

	
	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private Shell shell = null;
	private Shell parentShell = null;
	private TableViewer tv;
	private Table table;
	private TableLayout tableLayout = null;
	private Button saveBtn = null;
	private Button cancelBtn = null;
	private Composite statusBar = null;
	private Label status = null;
	private ProgressBar progressBar = null;
	private Text filterText = null;
	private MyEditingSupport myEditingSupport = null;
	private ButtonSelectionListener buttonSelectionListener = null;
	private Registry reg = null;
	private final static String[] tableTitles = new String[] { "序号", "图号", "版本号", "名称", "所有者", "HHPN", "新模费用*", "币种*"};
	private boolean isRunning = false;
	private TCComponentTask rootTask = null;
	
	public BatchEditorNewMoldFeeDialog(AbstractAIFUIApplication app, Shell parentShell) {
		super(parentShell);
		this.app = app;
		this.session = (TCSession) this.app.getSession();
		this.parentShell = parentShell;
		reg = Registry.getRegistry("com.foxconn.electronics.dcnreport.dcnreport");
		if (rootTask == null) {
			rootTask = getTask(); // 获取选中的任务
 			if (CommonTools.isEmpty(rootTask)) {
 				return;
 			}
		}
		
		try {
			String taskTemplateName = rootTask.getName();
			if (!NewMoldFeeConstant.NEW_MOLD_FEE_RELEASED_WORKLFLOW_TEMPLATE.equalsIgnoreCase(taskTemplateName)) {			
				TCUtil.warningMsgBox(reg.getString("WorkFlowTemplateErr.MSG"), reg.getString("WARNING.MSG"));							
				return;
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		
		
		initUI();
	}
	
	private void initUI() {
		shell = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.MIN); // 用了SWT.PRIMARY_MODAL代表使用了模态
		shell.setSize(1150, 600);
		shell.setText(reg.getString("BatchEditorNewMoldFee.TITLE"));
		
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		
		TCUtil.centerShell(shell);
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		
//		allComposite = new Composite(shell, SWT.NONE); 
//		allComposite.setLayout(new GridLayout(1, false));
//		allComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite tableComposite = new Composite(shell, SWT.NONE);
		tableComposite.setLayout(new GridLayout(1, false));
		tableComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		filterText = new Text(tableComposite, SWT.SEARCH | SWT.CANCEL);
		filterText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		filterText.setMessage(reg.getString("PlaceHolder.MSG"));
		
		tv = new TableViewer(tableComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
		table = tv.getTable();
		
		table.setHeaderVisible(true); // 显示表头
		table.setLinesVisible(true); // 显示
		table.setHeaderBackground(ColorEnum.GoldGray.color());
		
		tableLayout = new TableLayout(); // 专用于表格的布局
		table.setLayout(tableLayout);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createTreeColumn(); // 创建列	
		
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
		
		saveBtn = new Button(bottomBtnComposite, SWT.PUSH);
		saveBtn.setText(reg.getString("SaveBtn.LABEL"));
		saveBtn.setLayoutData(bottomBtnRowData);
		
		cancelBtn = new Button(bottomBtnComposite, SWT.PUSH);
		cancelBtn.setText(reg.getString("CancelBtn.LABEL"));
		cancelBtn.setLayoutData(bottomBtnRowData);
		
		buttonSelectionListener = new ButtonSelectionListener(shell, session, tv, reg, filterText, this);
		saveBtn.addSelectionListener(buttonSelectionListener);
		cancelBtn.addSelectionListener(buttonSelectionListener);
		
		table.setEnabled(false);
		saveBtn.setEnabled(false);
		cancelBtn.setEnabled(false);
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
	
	
	public void setUpStatusBar(Composite tableComposite, String statusName, boolean saveFlag) {
		if (!saveFlag) {
			statusBar = new Composite(tableComposite, SWT.NONE);
			statusBar.setLayout(new GridLayout(2, false));
			statusBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			status = new Label(statusBar, SWT.NONE);
			status.setText(statusName);
		} else {
			status.setVisible(true);
		}
		
		progressBar = new ProgressBar(statusBar, SWT.SMOOTH);
		progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressBar.setMaximum(100);
		
		if (saveFlag) {
			statusBar.layout();
		}
	}
	
	
	private void updateProgressBarByLoad() {
		new Thread(new Runnable() {
			private int progress = 0;
			private static final int INCREMENT = 10;
			private boolean flag = false; // 数据接口获取成功与否接口
			private List<NewMoldFeeBean> dataList = null;
			
			@Override
			public void run() {
				new Thread() {
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
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
									
						    		tv.setContentProvider(new NewMoldFeeTableViewerContentProvider());
						    		tv.setLabelProvider(new NewMoldFeeTableViewerLabelProvider());
					    			tv.setInput(dataList); // 设置数据并刷新TableViewer
						            tv.refresh();			            
						    		
					    			tv.getTable().setEnabled(true); // 恢复用户输入
						    		saveBtn.setEnabled(true);
						    		cancelBtn.setEnabled(true);
						    		
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
	
	public void updateProgressBarBySave(List<NewMoldFeeBean> list) {
		new Thread(new Runnable() {
			private int progress = 0;
			private static final int INCREMENT = 10;
			private boolean flag = false; // 数据接口获取成功与否接口
			private boolean saveFlag = false; // 用作是否保存成功标识
			private List<NewMoldFeeBean> dataList = null;
			
			@Override
			public void run() {
				new Thread( ) {
					public void run() {
						setRunningFlag(true);
						
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						if (!buttonSelectionListener.saveData(list)) {
							Display.getDefault().syncExec(new Runnable() {
								
								@Override
								public void run() {
									status.setVisible(false);
									progressBar.dispose(); // 销毁进度条组件
								}
							});	
							
							setRunningFlag(false);
							TCUtil.errorMsgBox(reg.getString("DataSaveErr.MSG"), reg.getString("ERROR.MSG"));
							return;
						} else {
							dataList = getData();
							if (dataList == null) {
								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										status.setVisible(false);
										progressBar.dispose(); // 销毁进度条组件
									}
								});	
								
								setRunningFlag(false);
								TCUtil.errorMsgBox(reg.getString("DataReLoadingErr.MSG"), reg.getString("ERROR.MSG"));							
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
								if (!shell.isDisposed()) {
									status.setVisible(false);
									tv.setContentProvider(new NewMoldFeeTableViewerContentProvider());
						    		tv.setLabelProvider(new NewMoldFeeTableViewerLabelProvider());
						    		
						    		tv.setInput(dataList); // 设置数据并刷新TableViewer
						    		tv.refresh();
						    		
						    		tv.getTable().setEnabled(true); // 恢复用户输入
						    		setRunningFlag(false);
						    		
						    		progressBar.dispose(); // 销毁进度条组件
						    		
						    		saveFlag = true;
								}
								
								if (saveFlag) {
									TCUtil.infoMsgBox(reg.getString("DataSaveSucc.MSG"), reg.getString("INFORMATION.MSG"));		
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
	
	
	
 	private List<NewMoldFeeBean> getData() {	
 		List<NewMoldFeeBean> list = new ArrayList<NewMoldFeeBean>();
 		try {
 			
 			rootTask.refresh();
 			TCComponent[] targetRelatedComponents = rootTask.getRelatedComponents(TCComponentTask.PROP_GLOBAL_TARGET_ATTACHMENTS);
 			if (CommonTools.isEmpty(targetRelatedComponents)) {
 				return null;
 			} 			
 			
 			List<TCComponent> collect = Stream.of(targetRelatedComponents).filter(e -> {
 				return NewMoldFeeConstant.D9_MEDesignRevision.equals(e.getTypeObject().getName());
 			}).collect(Collectors.toList());
 			
 			int index = 1;
 			for (TCComponent tcComponent : collect) {
 				TCComponentItemRevision itemRev = (TCComponentItemRevision) tcComponent;
 				NewMoldFeeBean bean = new NewMoldFeeBean();
 				
 				bean = tcPropMapping(bean, itemRev, NewMoldFeeConstant.D9_MEDesignRevision);
 				
 				itemRev.refresh();
 				TCComponent[] referenceComponents = itemRev.getRelatedComponents(NewMoldFeeConstant.IMAN_REFERENCE);
 				Optional<TCComponent> findAny = Stream.of(referenceComponents).filter(e -> {
 					if (e instanceof TCComponentForm) {
 						TCComponentForm form = (TCComponentForm) e;
 						try {
							return (NewMoldFeeConstant.D9_MoldInfo.equals(form.getTypeObject().getName()) && form.getProperty("object_name").startsWith(itemRev.getProperty("item_id")));
						} catch (TCException e1) {
							e1.printStackTrace();
						} 
 					}
 					return false;
 				}).findAny();
 				
 				if (findAny.isPresent()) {
 					TCComponentForm form = (TCComponentForm) findAny.get();
 					bean = tcPropMapping(bean, form, NewMoldFeeConstant.D9_MoldInfo);
 					bean.setForm(form); 					
 				} else {
					bean.setNewMoldFeeName(bean.getItemId() + NewMoldFeeConstant.D9_MoldInfo.substring(NewMoldFeeConstant.D9_MoldInfo.lastIndexOf("_")));
				}
 				
 				bean.setIndex(index);
 				bean.setItemRev(itemRev); 				
 				
 				list.add(bean); 				
 				index ++;
 			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}		
		return list;
	}
	
	/**
	 * 设置运行标识
	 * @param flag
	 */
 	private void setRunningFlag(boolean flag) {
 		Display.getDefault().syncExec(() -> {
 			isRunning = flag;
 			if (!saveBtn.isDisposed()) {
				saveBtn.setEnabled(!flag);
			}
 			
 			if (!cancelBtn.isDisposed()) {
				cancelBtn.setEnabled(!flag);
			}
 		});
 	}
 	
 	
	/**
	 * 获取选中的任务
	 * @return
	 */
	private TCComponentTask getTask() {
		try {
			InterfaceAIFComponent[] targetComponent = app.getTargetComponents(); 
			Optional<InterfaceAIFComponent> findAny = Stream.of(targetComponent).filter(e -> {	
				TCComponentTask currentTask = (TCComponentTask) e;
				try {
					return currentTask.getRoot().getTypeObject().getName().equalsIgnoreCase("EPMTask");
				} catch (TCException e1) {
					e1.printStackTrace();
				}
				return false;
			}).findAny();
			
			if (findAny.isPresent()) {
				rootTask = ((TCComponentTask) findAny.get()).getRoot(); 
			}
		} catch (Exception e) {
			e.printStackTrace();
			TCUtil.errorMsgBox(e.getLocalizedMessage(), reg.getString("ERROR.MSG"));
		}
		
		return rootTask;
	}
	
	
	public static <T> T tcPropMapping(T bean, TCComponent tcObject, String type) throws TCException, IllegalArgumentException, IllegalAccessException {
		if (bean != null && tcObject != null) {
			Field[] fields = bean.getClass().getDeclaredFields();
			List<Field> fieldList = new ArrayList<Field>( Arrays.asList(fields));
			for (Field field : fieldList) {
				field.setAccessible(true);
				TCPropertes tcPropName = field.getAnnotation(TCPropertes.class);
				if (tcPropName != null) {
					String propertyName = tcPropName.tcProperty();
					String tcType = tcPropName.tcType();
					Object val = null;
					if (CommonTools.isEmpty(propertyName) || !tcType.equalsIgnoreCase(type)) {
						continue;
					}
					
					if (tcObject.isValidPropertyName(propertyName) && type.equalsIgnoreCase(tcType)) {
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
		return bean;
	}

 	private void createTreeColumn() {
		for (int index = 0; index < tableTitles.length; index ++) {
			TableViewerColumn tvc = new TableViewerColumn(tv, SWT.CENTER, index);
			TableColumn tableColumn = tvc.getColumn();
			tableColumn.setText(tableTitles[index]);
			int columnWidth = 0;
			if (index == 0) {
				columnWidth = 50;
			} else if (index == 2) {
				columnWidth = 75;
			} else if (index == 3) {
				columnWidth = 250;
			} else if (index == 1 || index == 5) {
				columnWidth = 150;
			} else if (index == 7) {
				columnWidth = 100;	
			} else {
				columnWidth = 180;
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
			
			myEditingSupport = new MyEditingSupport(tv, index, tableTitles[index]);
			tvc.setEditingSupport(myEditingSupport);
		}
	}

	
 	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

 	
}
