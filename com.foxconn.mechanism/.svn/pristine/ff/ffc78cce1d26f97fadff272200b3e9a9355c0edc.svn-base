package com.foxconn.mechanism.mntL5MgfEcnManagement;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class CreateEcnDialog extends Dialog {

	public Shell shell;
	public Text txt_ecn_num;
	public CreateEcnService service;
	public TCSession session;
	public TCComponentItemRevision itemRev;
	public Button bt_assign_ecn;
	public CCombo cmb_ecn_role;
	public Text txt_name;
	public StyledText txt_change_desc;
	public CCombo cmb_real_user;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CreateEcnDialog(Shell parent,TCComponentItemRevision itemRev,TCSession session) {
		super(parent,SWT.DIALOG_TRIM | SWT.CLOSE| SWT.MIN | SWT.PRIMARY_MODAL | SWT.RESIZE);
		service = new CreateEcnService(this);
		this.session = session;
		this.itemRev = itemRev;
		open();
	}

	

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setLayout(new FormLayout());
		
		Label lab_title = new Label(shell, SWT.NONE);
		lab_title.setForeground(SWTResourceManager.getColor(0, 139, 139));
		lab_title.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 15, SWT.NORMAL));
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 20);
		formData.top = new FormAttachment(0, 10);
		lab_title.setLayoutData(formData);
		lab_title.setText("Monitor L5 MFG ECN");
		
		Label lab_info = new Label(shell, SWT.NONE);
		lab_info.setFont(SWTResourceManager.getFont("Microsoft YaHei UI", 11, SWT.NORMAL));
		lab_info.setAlignment(SWT.RIGHT);
		formData = new FormData();
		formData.top = new FormAttachment(lab_title, 0, SWT.BOTTOM);
		formData.right = new FormAttachment(100, -20);
		lab_info.setLayoutData(formData);
		lab_info.setText("常規信息");
		
		Label lab_divider = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		formData = new FormData();
		formData.top = new FormAttachment(lab_info, 0,SWT.BOTTOM);
		formData.left = new FormAttachment(0,10);
		formData.right = new FormAttachment(lab_info, 0,SWT.RIGHT);
		lab_divider.setLayoutData(formData);
		
		formData = new FormData();
		formData.top = new FormAttachment(lab_divider,40,SWT.BOTTOM);
		formData.left = new FormAttachment(lab_title,0,SWT.LEFT);
		Composite comp_label = new Composite(shell, SWT.NONE);
		comp_label.setLayoutData(formData);
		comp_label.setLayout(new RowLayout());

		Label label = new Label(comp_label, SWT.NONE);
		RowData rowData = new RowData();
		rowData.width = 180;
		label.setLayoutData(rowData);
		label.setText("ECN 編號：");
		
		txt_ecn_num = new Text(shell, SWT.BORDER);
		formData = new FormData();
		formData.top = new FormAttachment(comp_label, 0,SWT.TOP);
		formData.left = new FormAttachment(comp_label,0,SWT.RIGHT);
		formData.width = 150;
		txt_ecn_num.setLayoutData(formData);
		txt_ecn_num.setEditable(false);
		
		cmb_ecn_role = new CCombo(shell, SWT.BORDER);		
		formData = new FormData();
		formData.left = new FormAttachment(txt_ecn_num,10,SWT.RIGHT);
		formData.top = new FormAttachment(txt_ecn_num,0,SWT.TOP);
		formData.bottom = new FormAttachment(txt_ecn_num,0,SWT.BOTTOM);
		cmb_ecn_role.add("\"CSAP\"NNNNNN");
		cmb_ecn_role.select(0);
		cmb_ecn_role.setLayoutData(formData);
		cmb_ecn_role.setEditable(false);
			
		bt_assign_ecn = new Button(shell, SWT.NONE);
		bt_assign_ecn.setText("指派");
		formData = new FormData();
		formData.left = new FormAttachment(cmb_ecn_role,10,SWT.RIGHT);
		formData.top = new FormAttachment(cmb_ecn_role,0,SWT.TOP);
		formData.bottom = new FormAttachment(cmb_ecn_role,0,SWT.BOTTOM);
		bt_assign_ecn.setLayoutData(formData);
		bt_assign_ecn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				service.onAssignECN();
			}
		});
		
		formData = new FormData();
		formData.top = new FormAttachment(comp_label,10,SWT.BOTTOM);
		formData.left = new FormAttachment(comp_label,0,SWT.LEFT);
		Composite comp_lblNewLabel_1 = new Composite(shell, SWT.NONE);
		comp_lblNewLabel_1.setLayoutData(formData);
		comp_lblNewLabel_1.setLayout(new RowLayout());
		
		Label lblNewLabel_1 = new Label(comp_lblNewLabel_1, SWT.NONE);
		rowData = new RowData();
		lblNewLabel_1.setLayoutData(rowData);
		lblNewLabel_1.setText("概要：");
		
		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(comp_lblNewLabel_1, 0,SWT.TOP);
		fd_lblNewLabel.left = new FormAttachment(comp_lblNewLabel_1, 0,SWT.RIGHT);
		fd_lblNewLabel.right = new FormAttachment(comp_label, 0,SWT.RIGHT);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("*");
		
		txt_name = new Text(shell, SWT.BORDER);
		FormData fd_txt_name = new FormData();
		fd_txt_name.top = new FormAttachment(comp_lblNewLabel_1, 0,SWT.TOP);
		fd_txt_name.left = new FormAttachment(lblNewLabel, 0,SWT.RIGHT);
		fd_txt_name.right = new FormAttachment(bt_assign_ecn, 0,SWT.RIGHT);
		txt_name.setLayoutData(fd_txt_name);
		
		formData = new FormData();
		formData.top = new FormAttachment(comp_lblNewLabel_1,10,SWT.BOTTOM);
		formData.left = new FormAttachment(comp_lblNewLabel_1,0,SWT.LEFT);
		Composite comp_lblNewLabel_2 = new Composite(shell, SWT.NONE);
		comp_lblNewLabel_2.setLayoutData(formData);
		comp_lblNewLabel_2.setLayout(new RowLayout());
		
		Label lblNewLabel_2 = new Label(comp_lblNewLabel_2, SWT.NONE);
		rowData = new RowData();
		rowData.width = 180;
		lblNewLabel_2.setLayoutData(rowData);
		lblNewLabel_2.setText("描述：");
		
		txt_change_desc = new StyledText(shell, SWT.BORDER|SWT.MULTI|SWT.WRAP|SWT.V_SCROLL);
		formData = new FormData();
		formData.top = new FormAttachment(comp_lblNewLabel_2, 0,SWT.TOP);
		formData.height=128;
		formData.left = new FormAttachment(comp_lblNewLabel_2, 0,SWT.RIGHT);
		formData.right = new FormAttachment(bt_assign_ecn,0,SWT.RIGHT);
		txt_change_desc.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(comp_lblNewLabel_2, 0,SWT.LEFT);
		formData.top = new FormAttachment(txt_change_desc, 10,SWT.BOTTOM);
		formData.right = new FormAttachment(comp_lblNewLabel_2, 0,SWT.RIGHT);
		Composite comp_label6 = new Composite(shell, SWT.NONE);
		comp_label6.setLayoutData(formData);
		comp_label6.setLayout(new RowLayout());

		Label label6 = new Label(comp_label6, SWT.NONE);
		rowData = new RowData();
		rowData.width = 100;
		label6.setLayoutData(rowData);
		label6.setText("實際用戶：");

		cmb_real_user = new CCombo(shell, SWT.BORDER);		
		formData = new FormData();
		formData.right = new FormAttachment(bt_assign_ecn,0,SWT.RIGHT);
		formData.left = new FormAttachment(comp_label6);
		formData.top = new FormAttachment(comp_label6,0,SWT.TOP);
		cmb_real_user.setLayoutData(formData);
		cmb_real_user.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				int keyCode = arg0.keyCode;
				if(keyCode!=13 && keyCode != 16777296) {
					return;
				}
				CCombo source = (CCombo) arg0.getSource();
				String text = source.getText();
				List<String> userList = (List<String>) source.getData();
				source.removeAll();
				for(String user : userList) {
					if(user.toLowerCase().contains(text.toLowerCase())) {
						source.add(user);
					}
				}
				source.setText(text);
				source.setSelection(new Point(999,999));
				if(source.getItemCount()==1) {
					source.setText(source.getItem(0));
				}else {
					source.setListVisible(true);
				}				
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		Button bt_close = new Button(shell, SWT.NONE);
		bt_close.setText("關閉");
		formData = new FormData();
		formData.right = new FormAttachment(bt_assign_ecn, 0,SWT.RIGHT);
		formData.top = new FormAttachment(cmb_real_user,30,SWT.BOTTOM);
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
		service.onGUIInited();
	}
	
	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		Point computeSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		computeSize.x+=40;
		computeSize.y+=20;
		shell.setSize(computeSize);
		Rectangle parentBounds = getParent().getBounds();
		Rectangle shellBounds = shell.getBounds();
		shell.setLocation(parentBounds.x + (parentBounds.width - shellBounds.width) / 2,
				parentBounds.y + (parentBounds.height - shellBounds.height) / 2);
		shell.setText("創建ECN");		
		shell.setImage(Registry.getRegistry(AbstractAIFDialog.class).getImage("aifDesktop.ICON"));
		
		
		
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return null;
	}
}
