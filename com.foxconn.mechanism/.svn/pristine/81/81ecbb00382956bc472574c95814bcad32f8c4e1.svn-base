package com.foxconn.mechanism.dgkpi;


import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
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

import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;





public class SelectFileDialog extends Dialog{
	
	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private TCComponentItemRevision itemRevision;
	private Registry reg = null;
	private Shell shell = null;
	private Shell parent = null;
    private Button okbutton =null;
    private Button selectFilebutton =null;
    private Text fileTextContent;

	public SelectFileDialog (AbstractAIFUIApplication app, Shell parent, Registry reg,TCComponentItemRevision  itemRevision ) {
		super(parent);
		this.app = app;
		this.itemRevision=itemRevision;
		this.session = (TCSession) this.app.getSession();	
		this.reg = reg;	
		this.parent=parent;
		initUI();
	}
	
	//构建界面
	private void initUI() {
		shell = new Shell(parent,SWT.DIALOG_TRIM | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(480, 260);
		shell.setText("选择文件");
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
		fd.bottom=new FormAttachment(100,-60);
		middleComposite.setLayoutData(fd);
        middleComposite.setLayout(new FormLayout());
 
  
        Composite filePanelComposite = new Composite(middleComposite,SWT.NONE);
        FormData fd4=new FormData();
        fd4.left=new FormAttachment(50,-200);
        fd4.top=new FormAttachment(50,-10);
        filePanelComposite.setLayoutData(fd4);        
        
        filePanelComposite.setLayout(new RowLayout());
        fileTextContent = new Text(filePanelComposite, SWT.SINGLE|SWT.BORDER|SWT.READ_ONLY);
        RowData rd1=new RowData();
        rd1.width=300;
        rd1.height=17;      
        fileTextContent.setLayoutData(rd1);
        
        selectFilebutton=new Button(filePanelComposite,SWT.BUTTON5|SWT.CENTER);
        RowData rd2=new RowData(); 
        rd2.height=24;
        rd2.width=80;
        selectFilebutton.setText("Browser...");
        selectFilebutton.setLayoutData(rd2);
                         
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
	
	   selectFilebutton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				  new Thread() {
					@Override
					public void run() {
						    //选择文件
						    JFileChooser fileChooser=new JFileChooser() ;
						    fileChooser.setFileFilter(new FileFilter() {
							
							@Override
							public String getDescription() {
								return "*.pdf";
							}
							
							@Override
							public boolean accept(File f) {
							   String fnameString=	f.getName();
							   if(fnameString.indexOf(".")>-1) {
								    String extString=fnameString.toLowerCase().substring(fnameString.toLowerCase().lastIndexOf("."));
							        if(extString.equalsIgnoreCase(".pdf")) {
							         	return true;
							        }else {
							        	return false;
							        }
							    }else {
								    return true; 
							    }
							 }
						   });
						   
					       FileSystemView desk = FileSystemView.getFileSystemView();
					       File comDisk=desk.getHomeDirectory();   
					       fileChooser.setCurrentDirectory(comDisk);
					       int option=fileChooser.showSaveDialog(null);
					       if(option==JFileChooser.APPROVE_OPTION) {
					          File file= fileChooser.getSelectedFile();				   			       
					          Display.getDefault().syncExec(new Runnable() {
					        	    public void run() {
					        	    	  fileTextContent.setText(file.getAbsolutePath());
					        	    }
					         }); 
					      }     
					}
				  }.start();
			}
		   });
	
	  	  
        //确定按钮事件
	   okbutton.addSelectionListener( new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
			  try {
				   okbutton.setEnabled(false);
				   String strText=fileTextContent.getText();
				   System.out.print(strText);
				   if(strText==null||"".equalsIgnoreCase(strText)) {
					throw new Exception(reg.getString("nofile"));
					}	
				    if(!(strText.toLowerCase().endsWith(".pdf"))) {
					   throw new Exception("请选择pdf文件");
				    }
					shell.setVisible(false);
					okbutton.setEnabled(true);			
					new ImportDialog(app,parent,shell,reg,strText,itemRevision);		
				} catch (Exception e2) {
					    okbutton.setEnabled(true);
					    MessageBox.post(AIFUtility.getActiveDesktop(), e2.getMessage(), "Error", MessageBox.ERROR);
				 }
			}
		});
	   
	

   }

    

}
