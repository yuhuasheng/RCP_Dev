package com.foxconn.mechanism.SecondSourceImport;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.foxconn.mechanism.integrate.hhpnIntegrate.HHPNPojo;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;



public class SelectBuDialog extends Dialog{
	
	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private TCComponentFolder folderItem;
	private Registry reg = null;
	private Shell shell = null;
    private Button okbutton =null;
    private Button bt1 =null;
    private Button bt2 =null;
    private Button bt3 =null;
    private Text fileTextContent;
    private String msg="";


	public SelectBuDialog (AbstractAIFUIApplication app, Shell parent, Registry reg,TCComponentFolder  folderItem) {
		super(parent);
		this.app = app;
		this.folderItem=folderItem;
		this.session = (TCSession) this.app.getSession();
		this.shell = parent;
		this.reg = reg;	
		initUI();
	}
	
	//构建界面
	private void initUI() {
		shell = new Shell(shell,SWT.DIALOG_TRIM | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(990, 360);
		shell.setText("选择BU");
		shell.setLayout(new FillLayout());
		TCUtil.centerShell(shell);
		
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		Composite mainComposite = new Composite(shell, SWT.NONE);
		mainComposite.setLayout(new FormLayout());
       

		Composite middleComposite = new Composite(mainComposite, SWT.BORDER);
		FormData fd=new FormData();
		fd.left=new FormAttachment(0,10);
		fd.top=new FormAttachment(10);
		fd.right=new FormAttachment(100,-10);
		fd.bottom=new FormAttachment(100,-80);
		middleComposite.setLayoutData(fd);
        middleComposite.setLayout(new FormLayout());
 
  
        Composite filePanelComposite = new Composite(middleComposite,SWT.NONE);
        FormData fd4=new FormData();
        fd4.left=new FormAttachment(50,-230);
        fd4.top=new FormAttachment(50,-10);
        filePanelComposite.setLayoutData(fd4);        
        
        filePanelComposite.setLayout(new RowLayout());
        
    	FormToolkit formToolkit= new FormToolkit(shell.getDisplay());
    	
    	bt1=formToolkit.createButton(filePanelComposite,"DT", SWT.RADIO);
        RowData rd1=new RowData();
        rd1.width=60;
        rd1.height=17;      
        bt1.setLayoutData(rd1);
        
        bt2=formToolkit.createButton(filePanelComposite,"MNT", SWT.RADIO);
        RowData rd2=new RowData();
        rd2.width=60;
        rd2.height=17;      
        bt2.setLayoutData(rd2);
        
        bt3=formToolkit.createButton(filePanelComposite,"PRT", SWT.RADIO);
        RowData rd3=new RowData();
        rd3.width=60;
        rd3.height=17;      
        bt3.setLayoutData(rd3);
               
        Composite buttonComposite = new Composite(mainComposite, SWT.NONE);
		FormData fd2=new FormData();
		fd2.left=new FormAttachment(0);
		fd2.top=new FormAttachment(middleComposite,0);
		fd2.right=new FormAttachment(100);
		fd2.bottom=new FormAttachment(100);
		buttonComposite.setLayoutData(fd2);
		buttonComposite.setLayout(new FormLayout());
		
	    okbutton=new Button(buttonComposite,SWT.BUTTON5|SWT.CENTER);
	    okbutton.setText(reg.getString("confirmButton"));
	    FormData fd5=new FormData();
	    fd5.left=new FormAttachment(50,-35);
	    fd5.top=new FormAttachment(50,-12);
	    fd5.height=25;
	    fd5.width=70;
	    okbutton.setLayoutData(fd5); 
	
        
		addListener();
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
 * 添加事件
 */
private void addListener() {
	
	
	 
	
	
	  	  
        //确定按钮事件
	   okbutton.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				   try {
					    okbutton.setEnabled(false);
						String bu="";
						if(bt1.getSelection()) {
							bu="DT";
						}
						if(bt2.getSelection()) {
							bu="MNT";
						}
						if(bt3.getSelection()) {
							bu="PRT";
						}
						if("".equalsIgnoreCase(bu)) {
							throw new Exception("请选择BU");
						}
						shell.dispose();
						new SelectFileDialog(app,new Shell(),reg,folderItem,bu);
				   } catch (Exception e2) {
					    okbutton.setEnabled(true);
					    MessageBox.post(AIFUtility.getActiveDesktop(), e2.getMessage(), "Error", MessageBox.ERROR);
				    }
			}
		});
	   
	

   }

    

    
	
}
