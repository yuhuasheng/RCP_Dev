package com.foxconn.mechanism.custommaterial.custommaterialnumrequest.window;

import java.awt.EventQueue;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.foxconn.mechanism.pacmaterial.PACBOMBean;
import com.foxconn.mechanism.pacmaterial.PACImportBOMService;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.foxconn.mechanism.util.TCUtil;

public class FinishGoodDialog extends Dialog{
	
	private Registry reg = null;
	public Shell shell = null;
	private FinishGoodSerivce service;
	public AbstractAIFUIApplication app;
	private int fonSize = 9;
	public CCombo cmb_actuality_user;
	public Table table;
	public TableEditor editor;
	public TCComponentItemRevision itemRevision;
	
	public FinishGoodDialog(Registry reg, AbstractAIFUIApplication app,Shell parent,TCComponentItemRevision itemRevision) {
		super(parent);
		this.shell = parent;
		this.app = app;
		this.service = new FinishGoodSerivce(this);
		this.reg = reg;
		this.itemRevision = itemRevision;
		createContents();
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new FinishGoodDialog(null, null, new Shell(),null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void createContents() {
		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		if (bounds.width > 2000) {
			fonSize = 8;
		}

		Shell parent = shell;

		shell = new Shell(shell, SWT.DIALOG_TRIM | SWT.CLOSE | SWT.PRIMARY_MODAL | SWT.RESIZE | SWT.MAX);

		shell.setSize(1300, 500);
		shell.setImage(Registry.getRegistry(AbstractAIFDialog.class).getImage("aifDesktop.ICON"));
		Rectangle parentBounds = parent.getBounds();
		Rectangle shellBounds = shell.getBounds();
		shell.setLocation(parentBounds.x + (parentBounds.width - shellBounds.width) / 2,
				parentBounds.y + (parentBounds.height - shellBounds.height) / 2);
		String title = "MNT L10 EBOM-製作單集成成品料號申請v02";
		// 收尾
		shell.setText(title);
		shell.setLayout(new FormLayout());
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		FormData fd_table = new FormData();
		fd_table.top = new FormAttachment(0, 0);
		fd_table.left = new FormAttachment(0, 10);
		fd_table.right = new FormAttachment(99, 5);
		fd_table.bottom = new FormAttachment(90, 2);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		FormData fd_lab_actuality_user = new FormData(50, 15);
		fd_lab_actuality_user.top = new FormAttachment(table, 10,SWT.BOTTOM);
		fd_lab_actuality_user.left = new FormAttachment(0, 10);
		Label lab_actuality_user = new Label(shell, SWT.NONE);
		lab_actuality_user.setText("實際用戶");
		lab_actuality_user.setLayoutData(fd_lab_actuality_user);
		
		FormData fd_cmb_actuality_user_star = new FormData(161, 15);
		fd_cmb_actuality_user_star.left = new FormAttachment(lab_actuality_user, 7);
		fd_cmb_actuality_user_star.top = new FormAttachment(lab_actuality_user,0,SWT.TOP);
		cmb_actuality_user = new CCombo(shell, SWT.BORDER);
		cmb_actuality_user.setLayoutData(fd_cmb_actuality_user_star);
		cmb_actuality_user.addKeyListener(new KeyListener() {
			
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
		
		FormData fd_lab_actuality_user_star = new FormData(10,9);
		fd_lab_actuality_user_star.left = new FormAttachment(cmb_actuality_user, 5);
		fd_lab_actuality_user_star.top = new FormAttachment(cmb_actuality_user,0,SWT.TOP);
		Label lab_actuality_user_star = new Label(shell, SWT.NONE);
		lab_actuality_user_star.setText("*");
		lab_actuality_user_star.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		lab_actuality_user_star.setLayoutData(fd_lab_actuality_user_star);
		
		FormData fd_bt_copy = new FormData(50,25);
		fd_bt_copy.left = new FormAttachment(lab_actuality_user_star, 10);
		fd_bt_copy.top = new FormAttachment(lab_actuality_user_star,0,SWT.TOP);
		Button bt_copy = new Button(shell, SWT.NONE);
		bt_copy.setText("複製");
		bt_copy.setLayoutData(fd_bt_copy);
		bt_copy.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
				TableItem[] selection = table.getSelection();
				if(selection == null || selection.length == 0) {
					MessageBox.post("你想複製哪一行？", "提示", MessageBox.INFORMATION);
					return;
				}				
				service.onCopy(selection[0]);
			}
		});
		
		Button bt_save = new Button(shell, SWT.NONE);
//		bt_save.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				String openFileChooser = TCUtil.openFileChooser(shell);
//				if(openFileChooser!=null) {
//					service.onImport(openFileChooser);
//				}
//			}
//		});
		FormData fd_bt_broswer = new FormData(50,25);
		fd_bt_broswer.right = new FormAttachment(100, -8);
		fd_bt_broswer.top = new FormAttachment(cmb_actuality_user,0,SWT.TOP);
		bt_save.setLayoutData(fd_bt_broswer);
		bt_save.setText("保存");
		bt_save.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {			
					try {
						service.onSave(itemRevision);
					} catch (Exception e1) {						
						e1.printStackTrace();
						MessageBox.post(e1.toString(),"错误",MessageBox.ERROR);
					}
				}
			});

		
		// 设置列编辑器
		editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		TableColumn tableColumn_1 = new TableColumn(table,SWT.BORDER_SOLID);
		tableColumn_1.setWidth(120);
		tableColumn_1.setText("PRODUCT TYPE");
		
		TableColumn tableColumn_2 = new TableColumn(table,SWT.BORDER_SOLID);
		tableColumn_2.setWidth(190);
		tableColumn_2.setText("PRODUCT CLASSIFICATION");
		
		TableColumn tableColumn_3 = new TableColumn(table, SWT.NONE);
		tableColumn_3.setWidth(260);
		tableColumn_3.setText("MODEL REGISTERED,WITH SEQUENTIAL");
		
		TableColumn tableColumn_4 = new TableColumn(table, SWT.NONE);
		tableColumn_4.setMoveable(true);
		tableColumn_4.setWidth(120);
		tableColumn_4.setText("PANEL SOURCE");
		
		TableColumn tableColumn_5 = new TableColumn(table, SWT.NONE);
		tableColumn_5.setWidth(100);
		tableColumn_5.setText("SCALER IC");
		
		
		TableColumn tableColumn_6 = new TableColumn(table, SWT.NONE);
		tableColumn_6.setWidth(200);
		tableColumn_6.setText("CUSTOMER CODE");
		
		TableColumn tableColumn_7 = new TableColumn(table, SWT.NONE);
		tableColumn_7.setWidth(100);
		tableColumn_7.setText("AUDIO INPUT");
		
		TableColumn tableColumn_8 = new TableColumn(table, SWT.NONE);
		tableColumn_8.setWidth(150);
		tableColumn_8.setText("POWER CORD TYPE");
		
		TableColumn tableColumn_9 = new TableColumn(table, SWT.NONE);
		tableColumn_9.setWidth(200);
		tableColumn_9.setText("ENVIRONMENTAL");
		
		TableColumn tableColumn_10 = new TableColumn(table, SWT.NONE);
		tableColumn_10.setWidth(150);
		tableColumn_10.setText("物料群組");
		
		TableColumn tableColumn_11 = new TableColumn(table, SWT.NONE);
		tableColumn_11.setWidth(150);
		tableColumn_11.setText("PCBA料号");
		
		TableColumn tableColumn_12 = new TableColumn(table, SWT.NONE);
		tableColumn_12.setWidth(100);
		tableColumn_12.setText("進料方式");
		
		TableColumn tableColumn_13 = new TableColumn(table, SWT.NONE);
		tableColumn_13.setWidth(100);
		tableColumn_13.setText("自製方式");
		
		TableColumn tableColumn_14 = new TableColumn(table, SWT.NONE);
		tableColumn_14.setWidth(150);
		tableColumn_14.setText("成品Model Name");
		
		TableColumn tableColumn_15 = new TableColumn(table, SWT.NONE);
		tableColumn_15.setWidth(150);		
		tableColumn_15.setText("英文描述");
		
		TableColumn tableColumn_16 = new TableColumn(table, SWT.NONE);
		tableColumn_16.setWidth(150);
		tableColumn_16.setText("中文描述");
		
		TableColumn tableColumn_17 = new TableColumn(table, SWT.NONE);
		tableColumn_17.setWidth(140);
		tableColumn_17.setText("用量單位");
		
		TableColumn tableColumn_18 = new TableColumn(table, SWT.NONE);
		tableColumn_18.setWidth(140);
		tableColumn_18.setText("客戶 ModelName");
		tableColumn_18.setData("tcColumn","d9_CustomerModelName");
		
		TableColumn tableColumn_19 = new TableColumn(table, SWT.NONE);
		tableColumn_19.setWidth(140);
		tableColumn_19.setText("Foxconn ModelName");
		tableColumn_19.setData("tcColumn","d9_FoxconnModelName");
		
		TableColumn tableColumn_20 = new TableColumn(table, SWT.NONE);
		tableColumn_20.setWidth(140);
		tableColumn_20.setText("成品料號描述");
		tableColumn_20.setData("tcColumn","d9_FinishPNDesc");
		
		TableColumn tableColumn_21 = new TableColumn(table, SWT.NONE);
		tableColumn_21.setWidth(140);
		tableColumn_21.setText("出貨地區");
		tableColumn_21.setData("tcColumn","d9_ShippingArea");
		
		TableColumn tableColumn_22 = new TableColumn(table, SWT.NONE);
		tableColumn_22.setWidth(140);
		tableColumn_22.setText("電源線類型");
		tableColumn_22.setData("tcColumn","d9_PowerLineType");
		
		TableColumn tableColumn_23 = new TableColumn(table, SWT.NONE);
		tableColumn_23.setWidth(140);
		tableColumn_23.setText("PCBA接口");
		tableColumn_23.setData("tcColumn","d9_PCBAInterface");
		
		TableColumn tableColumn_24 = new TableColumn(table, SWT.NONE);
		tableColumn_24.setWidth(140);
		tableColumn_24.setText("有無喇叭");
		tableColumn_24.setData("tcColumn","d9_IsSpeaker");
		
		TableColumn tableColumn_25 = new TableColumn(table, SWT.NONE);
		tableColumn_25.setWidth(140);
		tableColumn_25.setText("顏色");
		tableColumn_25.setData("tcColumn","d9_Color");
		
		
		TableColumn tableColumn_26 = new TableColumn(table, SWT.NONE);
		tableColumn_26.setWidth(140);
		tableColumn_26.setText("外部線材");
		tableColumn_26.setData("tcColumn","d9_WireType");
		
		
		TableColumn tableColumn_27 = new TableColumn(table, SWT.NONE);
		tableColumn_27.setWidth(140);
		tableColumn_27.setText("其他");
		tableColumn_27.setData("tcColumn","d9_Other");
		
		
		TableColumn tableColumn_28 = new TableColumn(table, SWT.NONE);
		tableColumn_28.setWidth(140);
		tableColumn_28.setText("出貨優選方式尺寸");
		tableColumn_28.setData("tcColumn","d9_ShipSize");
		
		
		TableColumn tableColumn_29 = new TableColumn(table, SWT.NONE);
		tableColumn_29.setWidth(140);
		tableColumn_29.setText("出貨優選方式形式");
		tableColumn_29.setData("tcColumn","d9_ShipType");
		
		
		TableColumn tableColumn_30 = new TableColumn(table, SWT.NONE);
		tableColumn_30.setWidth(140);
		tableColumn_30.setText("參照BOM料號");
		tableColumn_30.setData("tcColumn","d9_RefMaterialPN");
		
		TableColumn tableColumn_31 = new TableColumn(table, SWT.NONE);
		tableColumn_31.setWidth(140);
		tableColumn_31.setText("備註");
		tableColumn_31.setData("tcColumn","d9_Remarks");
		
		table.addListener(SWT.MouseDown, new Listener() {
		    public void handleEvent(Event event) {
		        service.onTableClick(event);
		    }
		});
		
		service.onGUIInited(itemRevision);
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
