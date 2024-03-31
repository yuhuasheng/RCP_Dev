package com.foxconn.mechanism.custommaterial.custommaterialnumrequest.window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.mechanism.custommaterial.custommaterialnumrequest.SupplierEntity;
import com.foxconn.mechanism.util.HttpUtil;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class CustomMaterialNumSupplierDialog extends Dialog {

	private Shell shell = null;
	CustomMaterialNumService service;
	private Table table;
	
	private SupplierEntity selectedEntity = null;
	private Text txt_query;
	private CCombo combo;
	Registry reg;

	public CustomMaterialNumSupplierDialog(Shell parent,CustomMaterialNumService service,Registry reg) {
		super(parent);
		this.shell = parent;
		this.service = service;
		this.reg = reg;
		createContents();
		
	}
	
	public SupplierEntity getSelected() {
		return selectedEntity;
	}
	

	protected void createContents() {
		Shell parent = shell;
		shell = new Shell(shell, SWT.DIALOG_TRIM | SWT.CLOSE | SWT.PRIMARY_MODAL | SWT.MIN | SWT.MAX);
		shell.setSize(1100, 589);
		shell.setText(reg.getString("window.supplier.title"));
		shell.setImage(Registry.getRegistry(AbstractAIFDialog.class).getImage("aifDesktop.ICON"));
		Rectangle parentBounds = parent.getBounds();
		Rectangle shellBounds = shell.getBounds();
		shell.setLocation(parentBounds.x + (parentBounds.width - shellBounds.width)/2, parentBounds.y + (parentBounds.height - shellBounds.height)/2);
		shell.setLayout(new FormLayout());
		
		Composite composite = new Composite(shell, SWT.NONE);
		FormData fd_composite = new FormData(700,-1);
		fd_composite.right = new FormAttachment(100, -10);
		fd_composite.left = new FormAttachment(0);
		fd_composite.top = new FormAttachment(0);
		fd_composite.bottom = new FormAttachment(0, 44);
		composite.setLayoutData(fd_composite);
		composite.setLayout(null);
		
		Button bt_query = new Button(composite, SWT.NONE);
		bt_query.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doQuery();
			}
		});
		bt_query.setBounds(312, 8, 80, 27);
		bt_query.setText(reg.getString("window.supplier.query"));
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = table.getSelectionIndex();
				if(selectionIndex==-1) {
					return;
				}
				TableItem item = table.getItem(selectionIndex);
				selectedEntity = (SupplierEntity) item.getData();				
				shell.dispose();
			}
		});
		btnNewButton.setBounds(493, 8, 60, 27);
		btnNewButton.setText(reg.getString("window.supplier.confirm"));
		
		Button btnNewButton_1 = new Button(composite, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		btnNewButton_1.setBounds(559, 8, 60, 27);
		btnNewButton_1.setText(reg.getString("window.supplier.close"));
		
		combo = new CCombo(composite, SWT.BORDER);
		combo.setItems(new String[] {reg.getString("window.supplier.query.key1"), reg.getString("window.supplier.query.key2")});
		combo.setData("lov",new String[] {"makerName","makerContact"});
		combo.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		combo.setEditable(false);
		combo.setListVisible(true);
		combo.select(0);
		combo.setBounds(10, 11, 71, 20);
		
		txt_query = new Text(composite, SWT.BORDER);
		txt_query.setBounds(98, 10, 200, 23);
		
	
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		FormData fd_table = new FormData(200,-1);
		fd_table.bottom = new FormAttachment(100, -10);
		fd_table.top = new FormAttachment(composite, 7);
		fd_table.right = new FormAttachment(100, -10);
		fd_table.left = new FormAttachment(0);
		table.setLayoutData(fd_table);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
//		table.setLayoutData(new RowData(10, 700));
		
		TableColumn tableColumn = new TableColumn(table,SWT.BORDER_SOLID);
		tableColumn.setWidth(50);
		tableColumn.setText(reg.getString("window.table.column1"));
		
		TableColumn tblclmnNewColumn = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText(reg.getString("window.table.column2"));
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_1.setMoveable(true);
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText(reg.getString("window.table.column3"));
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText(reg.getString("window.table.column4"));
		
		TableColumn tblclmnNewColumn_3 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_3.setWidth(100);
		tblclmnNewColumn_3.setText(reg.getString("window.table.column5"));
		
		TableColumn tblclmnNewColumn_4 = new TableColumn(table, SWT.NONE);
		tblclmnNewColumn_4.setWidth(1000);
		tblclmnNewColumn_4.setText(reg.getString("window.table.column6"));
		
		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}
	
	private void doQuery() {
		table.removeAll();
		String[] keys = (String[]) combo.getData("lov");
		int selectionIndex = combo.getSelectionIndex();		
		List<SupplierEntity> supplier = service.getSupplier(keys[selectionIndex], txt_query.getText());
		if(supplier.isEmpty()) {
			MessageBox.post("查無此人", "查詢", MessageBox.INFORMATION);
			return;
		}
		for(int i=0;i<supplier.size();i++) {
			SupplierEntity entity = supplier.get(i);
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setData(entity);
			tableItem.setText(new String[] {i+1+"",entity.name,entity.contact,entity.tel,entity.fax,entity.address});
		}
	}

	
}
