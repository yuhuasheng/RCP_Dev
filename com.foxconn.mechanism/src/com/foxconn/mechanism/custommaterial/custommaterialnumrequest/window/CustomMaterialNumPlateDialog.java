package com.foxconn.mechanism.custommaterial.custommaterialnumrequest.window;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
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
import com.foxconn.mechanism.custommaterial.custommaterialnumrequest.SupplierEntity;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.util.Registry;

import swing2swt.layout.BorderLayout;

public class CustomMaterialNumPlateDialog extends Dialog {

	private Shell shell = null;
	CustomMaterialNumService service;
	
	private Button chk_1;
	private Button chk_2;
	private Button chk_4;
	private Button chk_5;
	private Button chk_7;
	private Button chk_8;
	private Button chk_10;
	private Button chk_9;
	private Button chk_6;
	private Button chk_3;
	private String selected = "";
	Registry reg;
	

	public CustomMaterialNumPlateDialog(Shell parent,CustomMaterialNumService service,String selected,Registry reg) {
		super(parent);
		this.shell = parent;
		this.service = service;
		this.selected = selected;
		this.reg = reg;
		createContents();
	}
	
	
	
	public String getSelected() {
		return selected;
	}
	

	protected void createContents() {
		
		Shell parent = shell;
		shell = new Shell(shell, SWT.DIALOG_TRIM | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(394, 170);
		shell.setImage(Registry.getRegistry(AbstractAIFDialog.class).getImage("aifDesktop.ICON"));
		Rectangle parentBounds = parent.getBounds();
		Rectangle shellBounds = shell.getBounds();
		shell.setLocation(parentBounds.x + (parentBounds.width - shellBounds.width)/2, parentBounds.y + (parentBounds.height - shellBounds.height)/2);
		shell.setText(reg.getString("window.plate.title"));
		
		chk_1 = new Button(shell, SWT.CHECK);
		chk_1.setBounds(30, 26, 58, 17);
		chk_1.setText("CHMB");
		
		chk_2 = new Button(shell, SWT.CHECK);
		chk_2.setText("CHMC");
		chk_2.setBounds(94, 26, 58, 17);
		
		chk_4 = new Button(shell, SWT.CHECK);
		chk_4.setText("AHMB");
		chk_4.setBounds(218, 26, 58, 17);
		
		chk_5 = new Button(shell, SWT.CHECK);
		chk_5.setText("ACDC");
		chk_5.setBounds(282, 26, 58, 17);
		
		chk_7 = new Button(shell, SWT.CHECK);
		chk_7.setText("CHMD");
		chk_7.setBounds(94, 49, 58, 17);
		
		chk_8 = new Button(shell, SWT.CHECK);
		chk_8.setText("CHMA");
		chk_8.setBounds(154, 49, 58, 17);
		
		chk_10 = new Button(shell, SWT.CHECK);
		chk_10.setText("LF48");
		chk_10.setBounds(282, 49, 58, 17);
		
		chk_9 = new Button(shell, SWT.CHECK);
		chk_9.setText("CHMK");
		chk_9.setBounds(218, 49, 58, 17);
		
		chk_6 = new Button(shell, SWT.CHECK);
		chk_6.setText("AHMC");
		chk_6.setBounds(30, 49, 58, 17);
		
		chk_3 = new Button(shell, SWT.CHECK);
		chk_3.setText("CHKA");
		chk_3.setBounds(154, 26, 58, 17);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selected = "";
				List<Button> list = new ArrayList<>();
				if(chk_1.getSelection()) {
					list.add(chk_1);
				}
				if(chk_2.getSelection()) {
					list.add(chk_2);
				}
				if(chk_3.getSelection()) {
					list.add(chk_3);
				}
				if(chk_4.getSelection()) {
					list.add(chk_4);
				}
				if(chk_5.getSelection()) {
					list.add(chk_5);
				}
				if(chk_6.getSelection()) {
					list.add(chk_6);
				}
				if(chk_7.getSelection()) {
					list.add(chk_7);
				}
				if(chk_8.getSelection()) {
					list.add(chk_8);
				}
				if(chk_9.getSelection()) {
					list.add(chk_9);
				}
				if(chk_10.getSelection()) {
					list.add(chk_10);
				}
				for(Button bt : list) {
					if(!selected.isEmpty()) {
						selected += ";";
					}
					selected += bt.getText();
				}				
				shell.dispose();
			}
		});
		btnNewButton.setBounds(94, 85, 58, 27);
		btnNewButton.setText(reg.getString("window.plate.confirm"));
		
		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		btnNewButton_1.setBounds(218, 85, 58, 27);
		btnNewButton_1.setText(reg.getString("window.plate.close"));
		

		if(!selected.isEmpty()){
			String[] split = selected.split(";");
			for(String txt : split) {
				if(txt.equals(chk_1.getText())) {
					chk_1.setSelection(true);
				}
				if(txt.equals(chk_2.getText())) {
					chk_2.setSelection(true);
				}
				if(txt.equals(chk_3.getText())) {
					chk_3.setSelection(true);
				}
				if(txt.equals(chk_4.getText())) {
					chk_4.setSelection(true);
				}
				if(txt.equals(chk_5.getText())) {
					chk_5.setSelection(true);
				}
				if(txt.equals(chk_6.getText())) {
					chk_6.setSelection(true);
				}
				if(txt.equals(chk_7.getText())) {
					chk_7.setSelection(true);
				}
				if(txt.equals(chk_8.getText())) {
					chk_8.setSelection(true);
				}
				if(txt.equals(chk_9.getText())) {
					chk_9.setSelection(true);
				}
				if(txt.equals(chk_10.getText())) {
					chk_10.setSelection(true);
				}
			}
		}
		
		
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
