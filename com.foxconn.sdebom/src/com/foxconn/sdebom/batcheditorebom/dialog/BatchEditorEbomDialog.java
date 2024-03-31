package com.foxconn.sdebom.batcheditorebom.dialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.foxconn.sdebom.batcheditorebom.constants.ColorEnum;
import com.foxconn.sdebom.batcheditorebom.custtree.CustTableLabelProvider;
import com.foxconn.sdebom.batcheditorebom.custtree.CustTreeContentProvider;
import com.foxconn.sdebom.batcheditorebom.custtree.LazyCustTableLabelProvider;
import com.foxconn.sdebom.batcheditorebom.custtree.LazyCustTreeContentProvider;
import com.foxconn.sdebom.batcheditorebom.custtree.LazyMyEditingSupport;
import com.foxconn.sdebom.batcheditorebom.custtree.MyEditingSupport;
import com.foxconn.sdebom.batcheditorebom.custtree.TreeViewerAutoFitListener;
import com.foxconn.sdebom.batcheditorebom.domain.SDEBOMBean;
import com.foxconn.sdebom.batcheditorebom.util.TreeTools;
import com.foxconn.tcutils.progress.BooleanFlag;
import com.foxconn.tcutils.progress.IProgressDialogRunnable;
import com.foxconn.tcutils.progress.LoopProgerssDialog;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.Registry;

public class BatchEditorEbomDialog extends Dialog {

	private AbstractPSEApplication app = null;
	private TCSession session = null;
	private SDEBOMBean rootData = null;
	private Shell shell = null;
	private TreeViewer treeViewer = null;
	private ButtonSelectionListener buttonSelectionListener = null;
	private LazyButtonSelectionListener lazyButtonSelectionListener = null;
	private TCComponentBOMLine topBomLine;
	private Tree tree = null;
	private TableLayout tableLayout = null;
	private Button saveBtn = null; // 保存
	private Button cancelBtn = null; // 取消
	private Button expandAllBtn = null; // 展开所有结点按钮
	private Button collapseAllBtn = null; // 收缩所有结点按钮	
	private Registry reg = null;
	private Boolean loadFlag = null; // 加载标识
	private TreeTools treeTools = null;
//	private MyEditingSupport editingSupport = null;
	private LazyMyEditingSupport lazyEditingSupport = null;
	private static final String[] treeTitles = new String[] { "BOM行", "客戶料號", "客戶料號描述", "英文描述", "頂層零組件裝配狀態" };
	private Label status = null;
	private ProgressBar progressBar = null;
	private ExecutorService es = Executors.newFixedThreadPool(15);
	
	public BatchEditorEbomDialog(AbstractPSEApplication app, Shell parent) {
		super(parent);
		this.app = app;
		this.session = (TCSession) this.app.getSession();
		reg = Registry.getRegistry("com.foxconn.sdebom.batcheditorebom.batcheditorebom");
		this.shell = parent;
		treeTools = new TreeTools(reg);		
		
//		LoopProgerssDialog progerssDialog = new LoopProgerssDialog(shell, null, reg.getString("LoadProgressDialog.TITLE"));
//		progerssDialog.run(true, new IProgressDialogRunnable() {
//
//			@Override
//			public void run(BooleanFlag stopFlag) {
//				try {
//					if (stopFlag.getFlag()) { // 监控是否要让停止后台任务
//						System.out.println("被中断了");
//						loadFlag = false;
//						return;
//					}
//
//					topBomLine = checkSelect();
//					System.out.println("******** 开始遍历SDEBOM结构树 ********");
////					rootData = treeTools.getSDEBOMStruct(topBomLine, false);
////					treeTools.resetExportModifyFlag(rootData, true);
//					System.out.println("******** 遍历SDEBOM结构树结束 ********");
//					// 结构树遍历完成后，重新获取一下标识位
//					if (stopFlag.getFlag()) {
//						loadFlag = false;
//					} else {
//						loadFlag = true;
//					}					
//				} catch (Exception e) {
//					e.printStackTrace();
//					TCUtil.errorMsgBox(e.getLocalizedMessage(), reg.getString("ERROR.MSG"));
//					loadFlag = false;
//				}
//				stopFlag.setFlag(true); // 无论执行成功或者失败，需要把标志位设置为停止，好通知给进度框
//			}
//		});
//
//		if (loadFlag != null && loadFlag == true) {
//			initUI();
//		}	
		
		try {
			topBomLine = checkSelect();	
			initUI();
		} catch (Exception e) {			
			e.printStackTrace();
			TCUtil.errorMsgBox(e.getLocalizedMessage(), reg.getString("ERROR.MSG"));
		}

	}

	/**
	 * 校验当前选中的对象
	 * 
	 * @return
	 * @throws Exception
	 */
	private TCComponentBOMLine checkSelect() throws Exception {
		// 获取选中的目标对象
		InterfaceAIFComponent targetComponent = app.getTargetComponent();
		if (CommonTools.isEmpty(targetComponent) || !(targetComponent instanceof TCComponentBOMLine)) {
			throw new Exception("请选中BOMLine进行操作");
		}
		return (TCComponentBOMLine) targetComponent;
	}

	/**
	 * 界面初始化
	 */
	private void initUI() {
		shell = new Shell(shell, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL | SWT.MIN);
		shell.setSize(1150, 600);
		shell.setText(reg.getString("Shell.TITLE"));
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);

		TCUtil.centerShell(shell);
		Image image = getDefaultImage();
		if (CommonTools.isNotEmpty(image)) {
			shell.setImage(image);
		}		
		
		Composite topComposite = new Composite(shell, SWT.BORDER);
		GridData topData = new GridData(GridData.FILL_HORIZONTAL);
		GridLayout topLayout = new GridLayout(1, false);
//		topLayout.horizontalSpacing = 50; // 设置此面板的组件的水平间距
		topComposite.setLayoutData(topData);
		topComposite.setLayout(topLayout);
		
		Composite fastBtnComposite = new Composite(topComposite, SWT.NONE);			
		
		fastBtnComposite.setLayoutData(createGridData(GridData.HORIZONTAL_ALIGN_FILL, 1));
		fastBtnComposite.setLayout(new GridLayout(2, false));
		
		GridData fastBtndata = new GridData(GridData.FILL_HORIZONTAL);
		fastBtndata.horizontalAlignment = SWT.CENTER;
		fastBtndata.widthHint = 54;
		fastBtndata.heightHint = 20;

		expandAllBtn = new Button(fastBtnComposite, SWT.PUSH);
		expandAllBtn.setText(reg.getString("ExpandAllBtn.LABEL"));
		expandAllBtn.setLayoutData(fastBtndata);

		fastBtndata = new GridData();
		fastBtndata.horizontalAlignment = SWT.CENTER;
		fastBtndata.widthHint = 55;
		fastBtndata.heightHint = 20;

		collapseAllBtn = new Button(fastBtnComposite, SWT.PUSH);
		collapseAllBtn.setText(reg.getString("CollapseAllBtn.LABEL"));
		collapseAllBtn.setLayoutData(fastBtndata);
		
		Composite treeComposite = new Composite(shell, SWT.BORDER);
		treeComposite.setLayout(new GridLayout(1, false));
		treeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		treeViewer = new TreeViewer(treeComposite,
				SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
		tree = treeViewer.getTree();
		tableLayout = new TableLayout();

		tree.setHeaderVisible(true);
		tree.setHeaderBackground(ColorEnum.GoldGray.color());
		tree.setLinesVisible(true);
		tree.setLayout(tableLayout);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTreeColumn(treeViewer); // 创建列

//		treeViewer.setContentProvider(new CustTreeContentProvider());
//		treeViewer.setLabelProvider(new CustTableLabelProvider());
//		treeViewer.setInput(rootData);
		
		treeViewer.setContentProvider(new LazyCustTreeContentProvider());
		treeViewer.setLabelProvider(new LazyCustTableLabelProvider());
		treeViewer.setInput(topBomLine);
		
//		treeViewer.addTreeListener(new TreeViewerAutoFitListener());
		
//		MyActionGroup actionGroup = new MyActionGroup(treeViewer, reg, treeTools, shell); // 生成一个ActionGroup对象
//		actionGroup.fillContextMenu(new MenuManager()); // 将按钮注入到菜单对象中
		
		LazyMyActionGroup actionGroup = new LazyMyActionGroup(treeViewer, reg, es); // 生成一个ActionGroup对象
		actionGroup.fillContextMenu(new MenuManager()); // 将按钮注入到菜单对象中

		treeViewer.setExpandedState(treeViewer.getTree().getItems()[0].getData(), true); // 将根节点设置为默认打开
		treeViewer.getTree().setSelection(treeViewer.getTree().getItems()[0]); // 设置结构树的根结点被选中

//		Composite bottomComposite = new Composite(shell, SWT.NONE);
//		bottomComposite.setLayout(new GridLayout(3, true));
//		bottomComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
//		
//		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.horizontalAlignment = GridData.END;
//		gridData.widthHint = 60;
//		gridData.heightHint = 40;
//		
//		saveBtn = new Button(bottomComposite, SWT.PUSH);
//		saveBtn.setText(reg.getString("SaveBtn.LABEL"));
//		saveBtn.setLayoutData(gridData);
//		
//		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.horizontalAlignment = GridData.BEGINNING;
//		gridData.widthHint = 60;
//		gridData.heightHint = 40;
//		
//		
//		cancelBtn = new Button(bottomComposite, SWT.PUSH);
//		cancelBtn.setText(reg.getString("CancelBtn.LABEL"));
//		cancelBtn.setLayoutData(gridData);
//		
//		
//		Composite statusBar = new Composite(bottomComposite, SWT.BORDER);
//		statusBar.setLayout(new GridLayout(2, false));
//		statusBar.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL, SWT.END, true, false));
//		  
//		ProgressBar progressBar = new ProgressBar(statusBar, SWT.SMOOTH);
//		progressBar.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
//		progressBar.setMaximum(100); 
//		 
//		Label status = new Label(statusBar, SWT.NONE);
//		status.setText("Some status message");
//		status.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
		
		Composite bottomComposite = new Composite(shell, SWT.NONE);
		bottomComposite.setLayout(new GridLayout(2, true));
		bottomComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));		
		
		Composite bottomBtnComposite = new Composite(bottomComposite, SWT.NONE);		
		GridData bottomBtndata = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		bottomBtndata.horizontalSpan = 2;
		bottomBtndata.grabExcessHorizontalSpace = true;
		
		bottomBtndata.horizontalAlignment = GridData.CENTER;
		bottomBtnComposite.setLayoutData(bottomBtndata);

		RowLayout rowLayout = new RowLayout(); // 设置底部按钮面板按钮为行列式布局，按钮间隔15像素
		rowLayout.spacing = 20;
		bottomBtnComposite.setLayout(rowLayout);

		RowData bottomBtnRowData = new RowData();
		bottomBtnRowData.width = 60;
		bottomBtnRowData.height = 40;

		saveBtn = new Button(bottomBtnComposite, SWT.PUSH);
		saveBtn.setText(reg.getString("SaveBtn.LABEL"));
		saveBtn.setLayoutData(bottomBtnRowData);

		cancelBtn = new Button(bottomBtnComposite, SWT.PUSH);
		cancelBtn.setText(reg.getString("CancelBtn.LABEL"));
		cancelBtn.setLayoutData(bottomBtnRowData);
		
//		Composite progressComposite = new Composite(bottomComposite, SWT.NONE);		
//		GridData progressdata = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		progressdata.horizontalSpan = 1;
//		progressdata.grabExcessHorizontalSpace = true;
//		
//		progressdata.horizontalAlignment = GridData.CENTER;
//		progressComposite.setLayoutData(progressdata);	    
	    
//	    status = new Label(bottomBtnComposite, SWT.NONE);
//	    status.setText("Some status message");
	    
//	    progressBar = new ProgressBar(bottomBtnComposite, SWT.SMOOTH);
//	    progressBar.setMaximum(100);
	    
//		buttonSelectionListener = new ButtonSelectionListener(app, treeViewer, shell, rootData, reg, treeTools);
//		saveBtn.addSelectionListener(buttonSelectionListener);
//		cancelBtn.addSelectionListener(buttonSelectionListener);
//		expandAllBtn.addSelectionListener(buttonSelectionListener);
//		collapseAllBtn.addSelectionListener(buttonSelectionListener);
		
		lazyButtonSelectionListener = new LazyButtonSelectionListener(app, treeViewer, shell, reg, es);
		saveBtn.addSelectionListener(lazyButtonSelectionListener);
		cancelBtn.addSelectionListener(lazyButtonSelectionListener);
		expandAllBtn.addSelectionListener(lazyButtonSelectionListener);
		collapseAllBtn.addSelectionListener(lazyButtonSelectionListener);
		
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
	 * 创建GridData布局
	 * @param style
	 * @param horizontalSpan
	 * @return
	 */
	private GridData createGridData(int style, int horizontalSpan) {
		GridData gridData = new GridData(style);
		gridData.horizontalSpan = horizontalSpan;
		return gridData;
	}
	
	
	private void createTreeColumn(TreeViewer tv) {
		for (int index = 0; index < treeTitles.length; index++) {
			TreeViewerColumn tvc = new TreeViewerColumn(tv, SWT.CENTER, index);
			TreeColumn treeColumn = tvc.getColumn();
			treeColumn.setText(treeTitles[index]);
			int columnWidth = 0;
			if (index == 0) {
				columnWidth = 550;
			} else if (index == 1 || index == 2 || index == 3) {
				columnWidth = 350;
			} else {
				columnWidth = 300;
			}

			treeColumn.setWidth(columnWidth);
			if (index == 0) {
				treeColumn.addListener(SWT.Move, new Listener() {

					@Override
					public void handleEvent(Event event) {

						int[] order = tree.getColumnOrder();
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
						tree.setColumnOrder(order);
					}
				});
			} else {
				treeColumn.setMoveable(true);
			}
//			editingSupport = new MyEditingSupport(tv, treeTools, index);
//			tvc.setEditingSupport(editingSupport);
			lazyEditingSupport = new LazyMyEditingSupport(tv, index, reg);
			tvc.setEditingSupport(lazyEditingSupport);
		}
	}
}
