package com.foxconn.electronics.issuemanagement.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import com.foxconn.electronics.issuemanagement.service.ImportIssueService;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

public class ImportIssueDialog extends Dialog{
	public Shell shell = null;	
	public TCSession session;
	private ImportIssueService service;
	private Text txt_path;
	public StyledText styledText;
	public TCComponentFolder folder;
	public ImportIssueDialog(Registry reg, AbstractAIFUIApplication app,Shell parent,TCComponentFolder folder) {
		super(parent);
		
		this.shell = parent;
		session = (TCSession) app.getSession();
		this.folder = folder;
		this.service = new ImportIssueService(this);
		createContents();

	}
	
	protected void createContents() {

		Shell parent = shell;
		shell = new Shell(shell, SWT.DIALOG_TRIM | SWT.CLOSE | SWT.PRIMARY_MODAL | SWT.RESIZE | SWT.MAX);
		shell.setSize(700, 600);
		shell.setImage(Registry.getRegistry(AbstractAIFDialog.class).getImage("aifDesktop.ICON"));
		Rectangle parentBounds = parent.getBounds();
		Rectangle shellBounds = shell.getBounds();
		shell.setLocation(parentBounds.x + (parentBounds.width - shellBounds.width) / 2,
				parentBounds.y + (parentBounds.height - shellBounds.height) / 2);
		String title = "導入Issue v1.0";
		// 收尾
		shell.setText(title);
		shell.setLayout(new FormLayout());
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String openFileChooser = TCUtil.openFileChooser(shell);
				if(openFileChooser!=null) {
					txt_path.setText(openFileChooser);
				}
			}
		});
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 10);
		formData.right = new FormAttachment(100, -10);
		btnNewButton.setLayoutData(formData);
		btnNewButton.setText("選擇文件");
		
		txt_path = new Text(shell, SWT.BORDER);
		formData = new FormData();
		formData.top = new FormAttachment(0, 12);
		formData.left = new FormAttachment(0, 10);
		formData.right = new FormAttachment(btnNewButton,-10, SWT.LEFT);
		txt_path.setLayoutData(formData);
		txt_path.setEditable(false);
		
		Button bt_start = new Button(shell, SWT.NONE);
		bt_start.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				service.onImport(txt_path.getText());
			}
		});
		formData = new FormData();
		formData.left = new FormAttachment(50,-17);
		formData.bottom = new FormAttachment(100, -10);
		bt_start.setLayoutData(formData);
		bt_start.setText("導入");
		
			
		styledText = new StyledText(shell, SWT.BORDER);
		formData = new FormData();
		formData.top = new FormAttachment(txt_path,10,SWT.BOTTOM);
		formData.right = new FormAttachment(btnNewButton,0, SWT.RIGHT);
		formData.bottom = new FormAttachment(bt_start,-10, SWT.TOP);
		formData.left = new FormAttachment(txt_path,0, SWT.LEFT);
		styledText.setLayoutData(formData);
		
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
