package com.foxconn.mechanism.hhpnmaterialapply.DTSA;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import com.foxconn.mechanism.hhpnmaterialapply.constants.BUConstant;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ColorEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.IconsEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ItemEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ItemIdPrefix;
import com.foxconn.mechanism.hhpnmaterialapply.constants.ItemRevEnum;
import com.foxconn.mechanism.hhpnmaterialapply.constants.PreferenceConstant;
import com.foxconn.mechanism.hhpnmaterialapply.domain.BOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.domain.PropertiesInfo;
import com.foxconn.mechanism.hhpnmaterialapply.message.MessageShow;
import com.foxconn.mechanism.hhpnmaterialapply.progress.BooleanFlag;
import com.foxconn.mechanism.hhpnmaterialapply.progress.IProgressDialogRunnable;
import com.foxconn.mechanism.hhpnmaterialapply.progress.LoopProgerssDialog;
import com.foxconn.mechanism.hhpnmaterialapply.util.BOMTreeTools;
import com.foxconn.mechanism.hhpnmaterialapply.util.BOMTreeValidation;
import com.foxconn.mechanism.hhpnmaterialapply.util.TreeTools;
import com.foxconn.mechanism.hhpnmaterialapply.ButtonSelectionListener;
import com.foxconn.mechanism.hhpnmaterialapply.CustTreeViewerContentProvider;
import com.foxconn.mechanism.hhpnmaterialapply.MyActionGroup;
import com.foxconn.mechanism.hhpnmaterialapply.MyContainerCheckedTreeViewer;
import com.foxconn.mechanism.hhpnmaterialapply.TreeViewerAutoFitListener;
import com.foxconn.mechanism.util.CommonTools;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.Registry;

public class DTSAHHPNMaterialApplyDialog extends Dialog {

	private AbstractPSEApplication app = null;
	private TCSession session = null;
	public static BOMInfo topBomInfo;
	private Shell shell = null;
	private MyContainerCheckedTreeViewer checkedTreeViewer = null;
	private ButtonSelectionListener buttonSelectionListener = null;
	private DTSAEditingSupport dtsaEditingSupport = null;
	private Tree tree = null;
	private TableLayout tableLayout = null;
	private Button generateBtn = null; // 生成物料
	private Button generateMoreBtn = null; // 一图多料
	private Button designSyncBtn = null; // 设计同步物料属性
	private Button partSyncBtn = null; // 物料同步设计属性
	private Button saveBtn = null; // 保存
	private Button cancelBtn = null; // 取消
	private Button partListExportBtn = null; // partList导出按钮
	private Button checkAllBtn = null; // 全选按钮
	private Button reverseBtn = null; // 反选按钮
	private Button reorderBtn = null; // 重新排序按钮
	private Button expandAllBtn = null; // 展开所有结点按钮
	private Button collapseAllBtn = null; // 收缩所有结点按钮	
	private Button checkLeafBtn = null; // 勾选叶子节点
	private Label unableModifyLabel = null; // 无法修改
	private Label existLabel = null; // 已存在
	private Label addAndModifyLabel = null; // 新增/修改
	private Label deleteLabel = null; // 删除
	private Label highLightLabel = null; // 高亮
	private Label tipLabel = null; // 输入提示标签
	private Label label = null;
	private Boolean condition = null; // 加载标识
	private Registry reg = null;
	private BOMTreeValidation bomTreeValidation = null;
	private TreeTools treeTools = null;
	
	private static final String[] treeTitles = new String[] { "MODEL_NAME", "HHPN", "HHRevision", "HHPN state",
			"CustomerPN", "Chinese Description", "English Description", "Qty/Units", "Material", "UL Class",
			"Part weight(g)", "Color", "Painting", "Printing", "Remark", "Finish", "Technology", "ADHESIVE" };

	public DTSAHHPNMaterialApplyDialog(AbstractPSEApplication app, Shell parent) {
		super(parent);
		this.app = app;
		this.session = (TCSession) this.app.getSession();
		reg = Registry.getRegistry("com.foxconn.mechanism.hhpnmaterialapply.hhpnmaterialapply");
		
		List<String> list = getPartType();
		if (CommonTools.isEmpty(list)) {
			MessageShow.warningMsgBox(reg.getString("preferenceName.MSG") + PreferenceConstant.D9_HHPN_PART_TYPE + reg.getString("preferenceErr.MSG"), reg.getString("WARNING.MSG"));
			return;
		}
		ItemRevEnum.CommonPartRev.update(list.get(0));
		System.out.println("==>> partRevType: " + ItemRevEnum.CommonPartRev.type());
		
		ItemEnum.CommonPart.update(list.get(1));
		System.out.println("==>> partType: " + ItemEnum.CommonPart.type());
		
		bomTreeValidation = new BOMTreeValidation();	
		treeTools = new TreeTools();
		
		this.shell = parent;		
		try {
			LoopProgerssDialog loopProgerssDialog = new LoopProgerssDialog(shell, null,
					reg.getString("progressDialog1.TITLE"));
			loopProgerssDialog.run(true, new IProgressDialogRunnable() {

				@Override
				public void run(BooleanFlag stopFlag) {
					try {
						if (stopFlag.getFlag()) { // 监控是否要让停止后台任务
							System.out.println("被中断了");
							condition = false;
							return;
						}
						System.out.println("开始遍历结构树");
						topBomInfo = BOMTreeTools.traversalBOMTree(app, session, BUConstant.DTSABUNAME, stopFlag); // 遍历BOM结构树
						System.out.println("结构树遍历完成");
						// 结构树遍历完成后，重新获取一下标识位
						if (stopFlag.getFlag()) {
							condition = false;
						} else {
							condition = true;
						}
						
						bomTreeValidation.checkPartRev(topBomInfo); // 校验BOM结构树中是否含有物料对象						
						if (!bomTreeValidation.getHasPartFlag()) { // 如果结构树不含有料对象
							addPart(topBomInfo); // 添加料对象
						}
						treeTools.setDesignLeafColor(topBomInfo); // 设置设计对象叶子节点没有物料对象时高亮
						stopFlag.setFlag(true); // 执行完毕后把标志位设置为停止，好通知给进度框
					} catch (Exception e) {
						e.printStackTrace();
						MessageShow.warningMsgBox(reg.getString("traversalErr.MSG"), reg.getString("ERROR.MSG"));
						stopFlag.setFlag(true);
					}
				}
			});
			if (condition != null && condition == true) {
				initUI(); // 界面初始化
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取物料类型首选项
	 * @return
	 */
	private List<String> getPartType() {
		return TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, PreferenceConstant.D9_HHPN_PART_TYPE);
	}
	
	
	/**
	 * 界面初始化
	 */
	private void initUI() {
//		Display display = new Display();
//		Shell shell = new Shell(display);		 
		shell = new Shell(shell, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL | SWT.MIN);
//		Display display = shell.getDisplay();
		shell.setSize(1500, 600);
		shell.setText(reg.getString("shell.TITLE"));
		GridLayout layout = new GridLayout(1, false);
//		shell.setLayout(new FillLayout()); // 设置充满式布局		
		
		shell.setLayout(layout);
		TCUtil.centerShell(shell);
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}		
		
		Composite topComposite = new Composite(shell, SWT.BORDER);
		GridData topData = new GridData(GridData.FILL_HORIZONTAL);
//		topData.horizontalSpan = 5;
		GridLayout topLayout = new GridLayout(3, false);
		topLayout.horizontalSpacing = 50; // 设置此面板的组件的水平间距
		topComposite.setLayoutData(topData);
		topComposite.setLayout(topLayout);
		

		Composite fastBtnComposite = new Composite(topComposite, SWT.NONE);			
		
		fastBtnComposite.setLayoutData(createGridData(GridData.HORIZONTAL_ALIGN_FILL, 1));
		fastBtnComposite.setLayout(new GridLayout(6, false));		
		
		GridData fastBtndata = new GridData(GridData.FILL_HORIZONTAL);
		fastBtndata.horizontalAlignment = SWT.BEGINNING;
		fastBtndata.widthHint = 40;
		fastBtndata.heightHint = 20;

		checkAllBtn = new Button(fastBtnComposite, SWT.PUSH);
		checkAllBtn.setText(reg.getString("checkAllBtn.LABEL"));
		checkAllBtn.setLayoutData(fastBtndata);

		fastBtndata = new GridData();
		fastBtndata.horizontalAlignment = SWT.BEGINNING;
		fastBtndata.widthHint = 40;
		fastBtndata.heightHint = 20;

		reverseBtn = new Button(fastBtnComposite, SWT.PUSH);
		reverseBtn.setText(reg.getString("reverseBtn.LABEL"));
		reverseBtn.setLayoutData(fastBtndata);

		fastBtndata = new GridData();
		fastBtndata.horizontalAlignment = SWT.BEGINNING;
		fastBtndata.widthHint = 54;
		fastBtndata.heightHint = 20;
		
		reorderBtn = new Button(fastBtnComposite, SWT.PUSH);
		reorderBtn.setText(reg.getString("reorderBtn.LABEL"));		
		reorderBtn.setLayoutData(fastBtndata);
		
		fastBtndata = new GridData();
		fastBtndata.horizontalAlignment = SWT.BEGINNING;
		fastBtndata.widthHint = 54;
		fastBtndata.heightHint = 20;

		expandAllBtn = new Button(fastBtnComposite, SWT.PUSH);
		expandAllBtn.setText(reg.getString("expandAllBtn.LABEL"));
		expandAllBtn.setLayoutData(fastBtndata);

		fastBtndata = new GridData();
		fastBtndata.horizontalAlignment = SWT.BEGINNING;
		fastBtndata.widthHint = 55;
		fastBtndata.heightHint = 20;

		collapseAllBtn = new Button(fastBtnComposite, SWT.PUSH);
		collapseAllBtn.setText(reg.getString("collapseAllBtn.LABEL"));
		collapseAllBtn.setLayoutData(fastBtndata);
		
		fastBtndata = new GridData();
		fastBtndata.horizontalAlignment = SWT.BEGINNING;
		fastBtndata.widthHint = 80;
		fastBtndata.heightHint = 20;		
		
		checkLeafBtn = new Button(fastBtnComposite, SWT.PUSH);
		checkLeafBtn.setText(reg.getString("checkLeafBtn.LABEL"));
		checkLeafBtn.setLayoutData(fastBtndata);
		
		Composite functionBtnComposite = new Composite(topComposite, SWT.NONE);		
		functionBtnComposite.setLayoutData(createGridData(GridData.HORIZONTAL_ALIGN_FILL, 1));
		functionBtnComposite.setLayout(new GridLayout(4, false));		
		
		GridData functionBtnData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		functionBtnData.horizontalAlignment = SWT.END;
		functionBtnData.widthHint = 100;
		functionBtnData.heightHint = 40;

		generateBtn = new Button(functionBtnComposite, SWT.PUSH);
		generateBtn.setText(reg.getString("generateBtn.LABEL"));
		generateBtn.setLayoutData(functionBtnData);
		
		
		functionBtnData = new GridData();
		functionBtnData.horizontalAlignment = SWT.BEGINNING;
		functionBtnData.widthHint = 100;
		functionBtnData.heightHint = 40;

		generateMoreBtn = new Button(functionBtnComposite, SWT.PUSH | SWT.NONE);
		generateMoreBtn.setText(reg.getString("generateMoreBtn.LABEL"));
		generateMoreBtn.setLayoutData(functionBtnData);
		
		
		functionBtnData = new GridData();
//		functionBtnData.horizontalAlignment = SWT.BEGINNING;
		functionBtnData.widthHint = 100;
		functionBtnData.heightHint = 40;
		
		
		designSyncBtn = new Button(functionBtnComposite, SWT.PUSH);
		designSyncBtn.setText(reg.getString("designSyncBtn.LABEL"));
		designSyncBtn.setLayoutData(functionBtnData);		
		
		
		functionBtnData = new GridData();
//		functionBtnData.horizontalAlignment = SWT.BEGINNING;
		functionBtnData.widthHint = 100;
		functionBtnData.heightHint = 40;		
		
		partSyncBtn = new Button(functionBtnComposite, SWT.PUSH);
		partSyncBtn.setText(reg.getString("partSyncBtn.LABEL"));
		partSyncBtn.setLayoutData(functionBtnData);
		
		Composite tipComposite = new Composite(topComposite, SWT.NONE);
		GridLayout tipGridLayout = new GridLayout(10, false);
		tipGridLayout.verticalSpacing = 10; // 设置此面板的组件的垂直间距
		tipComposite.setLayoutData(createGridData(GridData.HORIZONTAL_ALIGN_FILL, 1));
		tipComposite.setLayout(tipGridLayout);	
		

		GridData tipData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		tipData.horizontalAlignment = SWT.BEGINNING;
		tipData.widthHint = 50;
		tipData.heightHint = 15;

		unableModifyLabel = new Label(tipComposite, SWT.NONE);
		unableModifyLabel.setText(reg.getString("unableModify.LABEL"));
		unableModifyLabel.setLayoutData(tipData);		
		
		tipData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		tipData.horizontalAlignment = SWT.BEGINNING;
		tipData.widthHint = 35;
		tipData.heightHint = 12;		

		label = new Label(tipComposite, SWT.NONE);
		label.setBackgroundImage(new Image(null,DTSAHHPNMaterialApplyDialog.class.getResourceAsStream(IconsEnum.GrayIcons.relativePath())));
		label.setLayoutData(tipData);
		
		tipData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		tipData.horizontalAlignment = SWT.CENTER;
		tipData.widthHint = 35;
		tipData.heightHint = 15;

		existLabel = new Label(tipComposite, SWT.NONE);
		existLabel.setText(reg.getString("exist.LABEL"));
		existLabel.setLayoutData(tipData);
		
		tipData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		tipData.horizontalAlignment = SWT.CENTER;
		tipData.widthHint = 35;
		tipData.heightHint = 12;
		
		label = new Label(tipComposite, SWT.NONE);
		label.setBackgroundImage(new Image(null,DTSAHHPNMaterialApplyDialog.class.getResourceAsStream(IconsEnum.BlueIcons.relativePath())));
		label.setLayoutData(tipData);
		
		tipData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		tipData.horizontalAlignment = SWT.CENTER;
		tipData.widthHint = 55;
		tipData.heightHint = 15;
		
		addAndModifyLabel = new Label(tipComposite, SWT.NONE);
		addAndModifyLabel.setText(reg.getString("addAndModify.LABEL"));
		addAndModifyLabel.setLayoutData(tipData);
		
		tipData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		tipData.horizontalAlignment = SWT.CENTER;
		tipData.widthHint = 35;
		tipData.heightHint = 12;		

		label = new Label(tipComposite, SWT.NONE);
		label.setBackgroundImage(new Image(null,DTSAHHPNMaterialApplyDialog.class.getResourceAsStream(IconsEnum.GreenIcons.relativePath())));
		label.setBackground(ColorEnum.Green.color());
		label.setLayoutData(tipData);
		
		
		tipData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		tipData.horizontalAlignment = SWT.END;
		tipData.widthHint = 25;
		tipData.heightHint = 15;

		deleteLabel = new Label(tipComposite, SWT.NONE);
		deleteLabel.setText(reg.getString("delete.LABEL"));
		deleteLabel.setLayoutData(tipData);
		
		tipData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		tipData.horizontalAlignment = SWT.BEGINNING;
		tipData.widthHint = 35;
		tipData.heightHint = 12;

		label = new Label(tipComposite, SWT.NONE);
		label.setBackgroundImage(new Image(null,DTSAHHPNMaterialApplyDialog.class.getResourceAsStream(IconsEnum.RedIcons.relativePath())));
		label.setLayoutData(tipData);
		
		tipData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		tipData.horizontalAlignment = SWT.END;
		tipData.widthHint = 80;
		tipData.heightHint = 15;

		highLightLabel = new Label(tipComposite, SWT.NONE);
		highLightLabel.setText(reg.getString("leafHighLight.LABEL"));
		highLightLabel.setLayoutData(tipData);
		
		tipData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		tipData.horizontalAlignment = SWT.BEGINNING;
		tipData.widthHint = 35;
		tipData.heightHint = 12;

		label = new Label(tipComposite, SWT.NONE);
		label.setBackgroundImage(new Image(null,DTSAHHPNMaterialApplyDialog.class.getResourceAsStream(IconsEnum.HighLightIcons.relativePath())));
		label.setLayoutData(tipData);
		
		
		tipData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		tipData.horizontalAlignment = SWT.BEGINNING;
		tipData.horizontalSpan = 10; // 水平抢占两列空间
		tipData.widthHint = 500;
		tipData.heightHint = 15;

		tipLabel = new Label(tipComposite, SWT.NONE);
		tipLabel.setText("");
		Font font = new Font(null, "宋体", 10, SWT.NORMAL);
		tipLabel.setFont(font);
		tipLabel.setForeground(ColorEnum.TipColor.color());
		tipLabel.setLayoutData(tipData);		
		
		Composite treeComposite = new Composite(shell, SWT.BORDER);
		treeComposite.setLayout(new GridLayout(1, false));
		treeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		checkedTreeViewer = new MyContainerCheckedTreeViewer(treeComposite,
				SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
		tree = checkedTreeViewer.getTree();
		tableLayout = new TableLayout();
		tree.setHeaderVisible(true);

		tree.setHeaderBackground(ColorEnum.GoldGray.color());
		tree.setLinesVisible(true);
		tree.setLayout(tableLayout);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTreeColumn(checkedTreeViewer); // 创建列

		checkedTreeViewer.setContentProvider(new CustTreeViewerContentProvider());
		checkedTreeViewer.setLabelProvider(new DTSACustTreeViewerTableLabelProvider());
		checkedTreeViewer.setInput(topBomInfo);
		checkedTreeViewer.addTreeListener(new TreeViewerAutoFitListener());

		MyActionGroup actionGroup = new MyActionGroup(checkedTreeViewer, reg, tipLabel, treeTools); // 生成一个ActionGroup对象
		actionGroup.fillContextMenu(new MenuManager()); // 将按钮注入到菜单对象中

		checkedTreeViewer.setExpandedState(checkedTreeViewer.getTree().getItems()[0].getData(), true); // 将根节点设置为默认打开
		checkedTreeViewer.getTree().setSelection(checkedTreeViewer.getTree().getItem(0)); // 设置结构树的根结点被选中
		
		Composite bottomBtnComposite = new Composite(shell, SWT.NONE);
		GridData bottomBtndata = new GridData(GridData.FILL_HORIZONTAL);
		bottomBtndata.horizontalAlignment = GridData.CENTER; 		
		bottomBtnComposite.setLayoutData(bottomBtndata);		
		
		RowLayout rowLayout = new RowLayout(); // 设置底部按钮面板按钮为行列式布局，按钮间隔15像素
		rowLayout.spacing = 15;
		bottomBtnComposite.setLayout(rowLayout);		
		
		RowData bottomBtnRowData = new RowData();
		bottomBtnRowData.width = 60;
		bottomBtnRowData.height = 40;
		
		saveBtn = new Button(bottomBtnComposite, SWT.PUSH);
		saveBtn.setText(reg.getString("saveBtn.LABEL"));
		saveBtn.setLayoutData(bottomBtnRowData);

		cancelBtn = new Button(bottomBtnComposite, SWT.PUSH);
		cancelBtn.setText(reg.getString("cancelBtn.LABEL"));
		cancelBtn.setLayoutData(bottomBtnRowData);
		
		bottomBtnRowData = new RowData();
		bottomBtnRowData.width = 100;
		bottomBtnRowData.height = 40;
		
		partListExportBtn = new Button(bottomBtnComposite, SWT.PUSH);
		partListExportBtn.setText(reg.getString("partListExportBtn.LABEL"));
		partListExportBtn.setLayoutData(bottomBtnRowData);		
		
		buttonSelectionListener = new ButtonSelectionListener(app, checkedTreeViewer, shell, session, topBomInfo, tipLabel, BUConstant.DTSABUNAME, reg, bomTreeValidation, treeTools);
//		buttonSelectionListener.addListener(); // 添加监听
//		buttonSelectionListener.resetTreeColumn(); // 重新设置各列宽，让其文本可以完全显示

		generateBtn.addSelectionListener(buttonSelectionListener);
		generateMoreBtn.addSelectionListener(buttonSelectionListener);
		designSyncBtn.addSelectionListener(buttonSelectionListener);
		partSyncBtn.addSelectionListener(buttonSelectionListener);
		saveBtn.addSelectionListener(buttonSelectionListener);
		cancelBtn.addSelectionListener(buttonSelectionListener);
		partListExportBtn.addSelectionListener(buttonSelectionListener);
		checkAllBtn.addSelectionListener(buttonSelectionListener);
		reverseBtn.addSelectionListener(buttonSelectionListener);
		reorderBtn.addSelectionListener(buttonSelectionListener);
		expandAllBtn.addSelectionListener(buttonSelectionListener);
		collapseAllBtn.addSelectionListener(buttonSelectionListener);
		checkLeafBtn.addSelectionListener(buttonSelectionListener);

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
	
	/**
	 * 创建结构树的列并且设置列宽
	 * 
	 * @param tv
	 */
 	private void createTreeColumn(TreeViewer tv) {
		for (int index = 0; index < treeTitles.length; index++) {
			TreeViewerColumn tvc = new TreeViewerColumn(tv, SWT.CENTER, index);			
			TreeColumn treeColumn = tvc.getColumn();
			treeColumn.setText(treeTitles[index]);
			int columnWidth = 0;
			if (index == 0) {
				columnWidth = 400;
//				treeColumn.setToolTipText("温馨提示");
//				tv.reveal(tvc.getColumn());
			} else if (index == 1 || index == 3 || index == 4 || index == 5 || index == 6 || index == 7 || index == 9
					|| index == 16) {
				columnWidth = 200;
			} else {
				columnWidth = 130;
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
			dtsaEditingSupport = new DTSAEditingSupport(tv, index, treeTools);			
			tvc.setEditingSupport(dtsaEditingSupport);
		}
	}

	/**
	 * 添加物料对象
	 * @param topBomInfo
	 * @throws TCException
	 */
	private void addPart(BOMInfo topBomInfo) throws TCException {
		String objectType = topBomInfo.getObjectType();
		List<BOMInfo> childBomInfos = topBomInfo.getChild();
		if (CommonTools.isEmpty(childBomInfos)) { // 代表为叶子结点
			TCComponentItemRevision itemRevision = topBomInfo.getItemRevision();
			if (CommonTools.isEmpty(itemRevision)) {
				return;
			}
			
			if (objectType.contains(ItemRevEnum.DesignRev.type())
					&& itemRevision.getProperty("item_id").startsWith(ItemIdPrefix.ME.name())) { // 判断对象类型为设计对象，并且零组件ID为ME开头
				BOMInfo childInfo = new BOMInfo();
				PropertiesInfo propertiesInfo = topBomInfo.getPropertiesInfo();
				PropertiesInfo childPropertiesInfo = (PropertiesInfo) propertiesInfo.clone(); // 继承父对象属性

				childPropertiesInfo.setHHPN("");
				childPropertiesInfo.setHHRevision("");
				childPropertiesInfo.setHHPNState("");
				String childItemId = propertiesInfo.getItem_ID() + "@" + 1;
				childPropertiesInfo.setBUPN(childItemId);
				childInfo.setObjectName(childItemId);
				childInfo.setImage(new Image(null, BOMTreeTools.class.getResourceAsStream(IconsEnum.PartRevIcons.relativePath())));
				int QtyUnits = childPropertiesInfo.getQtyUnits();
				if (QtyUnits > 1) { // 如果数量
					childInfo.setItemId(childItemId + " x " + QtyUnits);
				} else {
					childInfo.setItemId(childItemId);
				}
				childPropertiesInfo.setItem_ID(childItemId);
				childInfo.setColor(ColorEnum.Green.color()); // 新增用绿色表示
				childInfo.setObjectType(ItemRevEnum.CommonPartRev.type());
				childInfo.setPropertiesInfo(childPropertiesInfo);
				childInfo.setModify(true); // 设计对象设置可编辑
				childInfo.setAddFlag(true); // 设置可以添加到设计对象下
				childInfo.setDeleteFlag(false); // 设置为不可删除
				childInfo.setIsExist(false); // 设置此物料对象是新增，不是存在TC中
				
				List<BOMInfo> child = topBomInfo.getChild();
				if (CommonTools.isEmpty(child)) {
					child = new ArrayList<BOMInfo>();
					topBomInfo.setChild(child);
				}
				child.add(childInfo);
			}
		} else {
			for (BOMInfo childBomInfo : childBomInfos) {
				addPart(childBomInfo);
			}
		}
	}
}
