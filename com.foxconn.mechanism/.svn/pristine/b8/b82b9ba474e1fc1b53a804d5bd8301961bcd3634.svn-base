package com.foxconn.mechanism.hhpnmaterialapply.dialog;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.foxconn.mechanism.hhpnmaterialapply.constants.ColorEnum;
import com.foxconn.mechanism.hhpnmaterialapply.domain.CheckDesignBean;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCSession;

public class CheckDialog extends Dialog{

	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private Shell shell = null;
	private TableViewer tv;
	private Table table;
	private TableLayout tableLayout = null;
	private Button confirmBtn = null; // 确定
	private Button cancelBtn = null; // 取消
	private boolean flag = false; // 用做是否继续下一步的标识
	private List<CheckDesignBean> list = null;
	private final String[] tableTitles = new String[] {"ID", "MODEL_NAME", "STATUS"};
	
	public CheckDialog(AbstractAIFUIApplication app, Shell parent, List<CheckDesignBean> list) {
		super(parent);
//		this.app = app;
//		this.session = (TCSession) this.app.getSession();
		this.shell = parent;
		this.list = list;
		initUI();
	}
	
	
	private void initUI() {
		shell = new Shell(shell, SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL | SWT.MIN);
		shell.setSize(750, 600);
		shell.setText("以下设计对象属性无法同步");
		
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);
		
		TCUtil.centerShell(shell);
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		
		Composite tableComposite = new Composite(shell, SWT.BORDER);
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
		
		createTreeColumn(); // 创建列
		
		tv.setContentProvider(new CheckTableViewerContentProvider()); // 设置内容器
		tv.setLabelProvider(new CheckTableViewerLabelProvider()); // 设置标签器
		tv.setInput(list); // 将数据输入到表格
		
//		resetPack(); // 重新布局表格
		
		Composite bottomBtnComposite = new Composite(shell, SWT.NONE);
		GridLayout bottomBtnLayout = new GridLayout(2, false);
		
		bottomBtnComposite.setLayout(bottomBtnLayout);
		bottomBtnComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		GridData bottomBtndata = new GridData();
		bottomBtndata.horizontalAlignment = SWT.END;
		bottomBtndata.grabExcessHorizontalSpace = true;
		bottomBtndata.widthHint = 60;
		bottomBtndata.heightHint = 40;
		
		confirmBtn = new Button(bottomBtnComposite, SWT.PUSH);
		confirmBtn.setText("确定");
		confirmBtn.setLayoutData(bottomBtndata);
		
		bottomBtndata = new GridData();
		bottomBtndata.horizontalAlignment = SWT.BEGINNING;
		bottomBtndata.grabExcessHorizontalSpace = true;
		bottomBtndata.widthHint = 60;
		bottomBtndata.heightHint = 40;

		cancelBtn = new Button(bottomBtnComposite, SWT.PUSH);
		cancelBtn.setText("取消");
		cancelBtn.setLayoutData(bottomBtndata);
		
		addListener(); // 按钮监听
		
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
		for (int index = 0; index < tableTitles.length; index ++) {
			TableViewerColumn tvc = new TableViewerColumn(tv, SWT.CENTER, index);
			TableColumn tableColumn = tvc.getColumn();
			tableColumn.setText(tableTitles[index]);
			int columnWidth = 0;
			if (index == 0) {
				columnWidth = 50;
			} else if (index == 1) {
				columnWidth = 400;
			} else {
				columnWidth = 100;
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
		}
	}

	
	private void resetPack() {
		for (int i = 0; i < tableTitles.length; i++) {
			table.getColumn(i).pack();
		}
	}
	
	
	private void addListener() {
		confirmBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				flag = true;
				System.out.println("单击了确定按钮");
				shell.dispose();
			}
		});
		
		cancelBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("单击了取消按钮");
				shell.dispose();
			}
		});
	}


	public boolean getFlag() {
		return flag;
	}
	
}
