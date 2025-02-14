package com.foxconn.mechanism.custommaterial.customMaterialPostPlant;


import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;



public class InputInfoDialog extends Dialog{
	
	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private Registry reg = null;
	private Shell shell = null;
    private Button okbutton =null;
    private Text textContent;
    private String msg="";
    private TCComponentItemRevision itemRev;
    private String plant="";
    private String hasPlants="";
    //private String[] plants=new String[] {"CHMB"};
    private String[] plants=new String[] {"CHMB","CHMC","CHMD","CHMK","CHJM","AFLC","AFEC","JLMC","JLMB","WLLA","AHLX",
            "DCA1", "DFL1","DIL1","DTN1", "DTX1","DTX2","DTX3","HCA1","HFL1","HIN1", "HTX1","LNC1","CQSA","CHMS","CHMU","LF48",
             "ACDC", "AHMK" };
    
    //public static final String[] plant801 = {};

    //public static final String[] plant888 = {};

   // public static final String[] plant868 = { "ACDC", "AHMK" };
    
    
    private List<Button> buttons=new ArrayList<Button>();
	public InputInfoDialog (AbstractAIFUIApplication app, Shell parent, Registry reg,TCComponentItemRevision itemRev) {
		super(parent);
		this.app = app;
		this.session = (TCSession) this.app.getSession();
		this.shell = parent;
		this.reg = reg;	
		this.itemRev=itemRev;
		try {
		   initUI();
		}catch(Exception e) {}
	
	}
	
	//构建界面
	private void initUI() throws Exception {
		itemRev.refresh();
		hasPlants=itemRev.getProperty("d9_Plant_L6");
		shell = new Shell(shell,SWT.DIALOG_TRIM | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(990, 560);
		shell.setText(reg.getString("input.title"));
		shell.setLayout(new FillLayout());
		TCUtil.centerShell(shell);
		
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		//SashForm sashForm = new SashForm(shell, SWT.VERTICAL);
		Composite mainComposite = new Composite(shell, SWT.NONE);
		mainComposite.setLayout(new FormLayout());
       
		Composite topComposite = new Composite(mainComposite, SWT.NONE);
		FormData fd0=new FormData();
		fd0.left=new FormAttachment(0);
		fd0.top=new FormAttachment(0);
		fd0.right=new FormAttachment(100);
		fd0.height=150;
		topComposite.setLayoutData(fd0);
		FormLayout fl=new FormLayout();
		fl.marginWidth=10;
		fl.marginHeight=10;
		topComposite.setLayout(fl);
		FormToolkit formToolkit= new FormToolkit(shell.getDisplay());
		for(int i=0;i<plants.length;i++) {
			Button bt=formToolkit.createButton(topComposite,plants[i], SWT.CHECK);
			if(!needAdd(plants[i])) {
				bt.setSelection(true);
				bt.setEnabled(false);
			}
			FormData fd=new FormData();
			if(i%13==0) {
				fd.left=new FormAttachment(0);
			}else {
				fd.left=new FormAttachment(buttons.get(i-1),5); 
			}
			fd.top=new FormAttachment(0,(i/13)*30);
			bt.setLayoutData(fd);
			buttons.add(bt);
		}
		
	  
		
		Composite middleComposite = new Composite(mainComposite, SWT.NONE);
		FormData fd=new FormData();
		fd.left=new FormAttachment(0);
		fd.top=new FormAttachment(topComposite,10);
		fd.right=new FormAttachment(100);
		fd.bottom=new FormAttachment(100,-80);
		middleComposite.setLayoutData(fd);
        middleComposite.setLayout(new FormLayout());
 
        textContent = new Text(middleComposite, SWT.WRAP|SWT.V_SCROLL|SWT.BORDER|SWT.SCROLLBAR_OVERLAY);  
        FormData fd1=new FormData();
		fd1.left=new FormAttachment(0,10);
		fd1.top=new FormAttachment(0);
		fd1.right=new FormAttachment(100,-10);
		fd1.bottom=new FormAttachment(100);
		textContent.setLayoutData(fd1);
     

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
					     plant="";
					     for(Button  b:buttons) {
					         if(b.getSelection()) {
					        	if( needAdd(b.getText())) {
					        	    plant+=b.getText()+",";
					        	}
					         }
					     }
					     System.out.print(plant);
					     if(plant.endsWith(",")) {
					    	 plant=plant.substring(0, plant.length()-1); 
					     }
					     if(plant==null||"".equalsIgnoreCase(plant)) {
					    	    MessageBox.post(AIFUtility.getActiveDesktop(),reg.getString("select.plnat"), "Error", MessageBox.ERROR);
								   return; 
					     }
					    okbutton.setEnabled(false);
					    List<TCComponentItemRevision> ls=new ArrayList<TCComponentItemRevision>();
					    ls.add(itemRev);
				        new CustomPNUtil().PostCustomPn(session, textContent, ls, plant);
				    } catch (Exception e2) {
					    MessageBox.post(AIFUtility.getActiveDesktop(), e2.getMessage(), "Error", MessageBox.ERROR);
				    }
			}
		});
	   
	

   }

    

    private boolean needAdd(String txt) {
    	if(hasPlants==null||"".equalsIgnoreCase(hasPlants)) {
    		return true;
    	}
    	String[] m=hasPlants.split(",");
    	for(String s:m) {
    		if(s.equalsIgnoreCase(txt)) {
    			return false;
    		}
    	}
    	return true;
    }
	
}
