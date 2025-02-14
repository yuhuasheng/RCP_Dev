package com.foxconn.mechanism.mntL5MgfEcnManagement;

import java.awt.EventQueue;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

public class RelateExsitingECNDialog extends Dialog {

	public Shell shell;
	public TCComponentItemRevision itemRev;
	public Table table;
	public RelateExsitingService service;
	public TCSession session;
	
	public RelateExsitingECNDialog(Shell parent,TCComponentItemRevision itemRev,TCSession session) {
		super(parent);
		this.shell = parent;
		this.itemRev = itemRev;
		this.session = session;
		this.service = new RelateExsitingService(this);
		createContents();
	}
	
	protected void createContents() {

		Shell parent = shell;

		shell = new Shell(shell, SWT.DIALOG_TRIM | SWT.CLOSE | SWT.PRIMARY_MODAL | SWT.RESIZE | SWT.MAX);

		shell.setSize(1000, 400);
		shell.setImage(Registry.getRegistry(AbstractAIFDialog.class).getImage("aifDesktop.ICON"));
		Rectangle parentBounds = parent.getBounds();
		Rectangle shellBounds = shell.getBounds();
		shell.setLocation(parentBounds.x + (parentBounds.width - shellBounds.width) / 2,
				parentBounds.y + (parentBounds.height - shellBounds.height) / 2);
		String title = "添加現有ECN";
		shell.setText(title);
		shell.setLayout(new FormLayout());
		
		Button bt_close = new Button(shell, SWT.NONE);
		bt_close.setText("關閉");
		FormData formData = new FormData();
		formData.top = new FormAttachment(100,-35);
		formData.right = new FormAttachment(100,-10);
		formData.width = 60;
		bt_close.setLayoutData(formData);
		bt_close.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		
		Button bt_complete = new Button(shell, SWT.NONE);
		bt_complete.setText("完成");
		formData = new FormData();
		formData.right = new FormAttachment(bt_close,-10,SWT.LEFT);
		formData.top = new FormAttachment(bt_close,0,SWT.TOP);
		formData.width = 60;
		bt_complete.setLayoutData(formData);
		bt_complete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				service.onComplete();
			}
		});
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		FormData fd_table = new FormData();
		fd_table.top = new FormAttachment(0, 10);
		fd_table.left = new FormAttachment(0, 10);
		fd_table.right = new FormAttachment(bt_close, 0,SWT.RIGHT);
		fd_table.bottom = new FormAttachment(bt_close, -10,SWT.TOP);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		
		TableColumn tableColumn = new TableColumn(table,SWT.BORDER_SOLID);
		tableColumn.setWidth(50);
		tableColumn.setText("選擇");
		
		tableColumn = new TableColumn(table,SWT.BORDER_SOLID);
		tableColumn.setWidth(150);
		tableColumn.setText("ECN編號");
		
		tableColumn = new TableColumn(table,SWT.BORDER_SOLID);
		tableColumn.setWidth(50);
		tableColumn.setText("版本");
		
		tableColumn = new TableColumn(table,SWT.BORDER_SOLID);
		tableColumn.setWidth(200);
		tableColumn.setText("概要");
		
		tableColumn = new TableColumn(table,SWT.BORDER_SOLID);
		tableColumn.setWidth(150);
		tableColumn.setText("實際用戶");
		
		tableColumn = new TableColumn(table,SWT.BORDER_SOLID);
		tableColumn.setWidth(200);
		tableColumn.setText("描述");
		
		tableColumn = new TableColumn(table,SWT.BORDER_SOLID);
		tableColumn.setWidth(150);
		tableColumn.setText("設變廠別");
		
		tableColumn = new TableColumn(table,SWT.BORDER_SOLID);
		tableColumn.setWidth(150);
		tableColumn.setText("變更適用範圍");
		
		tableColumn = new TableColumn(table,SWT.BORDER_SOLID);
		tableColumn.setWidth(150);
		tableColumn.setText("客戶");
		
		tableColumn = new TableColumn(table,SWT.BORDER_SOLID);
		tableColumn.setWidth(150);
		tableColumn.setText("機種名稱");
		
		tableColumn = new TableColumn(table,SWT.BORDER_SOLID);
		tableColumn.setWidth(150);
		tableColumn.setText("範圍");
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(150);
		tblclmnNewColumn.setText("變更類型");
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setMoveable(true);
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("等級");
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(150);
		tblclmnNewColumn_2.setText("BOM Change Or Not");
		
		TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_3.setWidth(200);
		tblclmnNewColumn_3.setText("變更後說明");
		
		TableColumn tblclmnNewColumn_4 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_4.setWidth(200);
		tblclmnNewColumn_4.setText("變更前說明");
		
		TableColumn tblclmnNewColumn_5 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_5.setWidth(150);
		tblclmnNewColumn_5.setText("申請人課級主管");
		
		TableColumn tblclmnNewColumn_6 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_6.setWidth(150);
		tblclmnNewColumn_6.setText("申請人部級主管");
		
		service.onGUIInited();
		
		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	
}
