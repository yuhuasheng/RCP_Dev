package com.foxconn.mechanism.SecondSourceImport;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import com.foxconn.mechanism.util.ExcelUtils;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.services.loose.core.DataManagementService;
import com.teamcenter.soa.client.model.ServiceData;



public class SelectFileDialog extends Dialog{
	
	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private TCComponentFolder folderItem;
	private Registry reg = null;
	private Shell shell = null;
    private Button okbutton =null;
    private Button selectFilebutton =null;
    private Button tmpFilebutton =null;
    private Text fileTextContent;
    private String msg="";
    private String bu;

	public SelectFileDialog (AbstractAIFUIApplication app, Shell parent, Registry reg,TCComponentFolder  folderItem,String bu) {
		super(parent);
		this.app = app;
		this.folderItem=folderItem;
		this.session = (TCSession) this.app.getSession();
		this.shell = parent;
		this.reg = reg;	
		this.bu=bu;
		initUI();
	}
	
	//构建界面
	private void initUI() {
		shell = new Shell(shell,SWT.DIALOG_TRIM | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(990, 560);
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
		fd.bottom=new FormAttachment(100,-80);
		middleComposite.setLayoutData(fd);
        middleComposite.setLayout(new FormLayout());
 
  
        Composite filePanelComposite = new Composite(middleComposite,SWT.NONE);
        FormData fd4=new FormData();
        fd4.left=new FormAttachment(50,-230);
        fd4.top=new FormAttachment(50,-10);
        filePanelComposite.setLayoutData(fd4);        
        
        filePanelComposite.setLayout(new RowLayout());
        fileTextContent = new Text(filePanelComposite, SWT.SINGLE|SWT.BORDER|SWT.READ_ONLY);
        RowData rd1=new RowData();
        rd1.width=280;
        rd1.height=17;      
        fileTextContent.setLayoutData(rd1);
        
        selectFilebutton=new Button(filePanelComposite,SWT.BUTTON5|SWT.CENTER);
        RowData rd2=new RowData(); 
        rd2.height=24;
        rd2.width=80;
        selectFilebutton.setText("Browser...");
        selectFilebutton.setLayoutData(rd2);
        
        tmpFilebutton=new Button(filePanelComposite,SWT.BUTTON5|SWT.CENTER);
        RowData rd3=new RowData(); 
        rd3.height=24;
        rd3.width=80;
        tmpFilebutton.setText(reg.getString("dowmloadTmp"));
        tmpFilebutton.setLayoutData(rd3);  
               
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
	
	
	  //下载模板文件
	   tmpFilebutton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				  new Thread() {

					@Override
					public void run() {
						    //选择文件
						try {
						   JFileChooser fileChooser=new JFileChooser();
					       FileSystemView desk = FileSystemView.getFileSystemView();
					       File comDisk=desk.getHomeDirectory();   
					       fileChooser.setCurrentDirectory(comDisk);
					       fileChooser.setCurrentDirectory(comDisk);
					       fileChooser.setSelectedFile(new File("2ndSource_import_template.xlsx"));					      
					       int option=fileChooser.showSaveDialog(null);
					       if(option==JFileChooser.APPROVE_OPTION) {
					          File newFile= fileChooser.getSelectedFile();				   			       
					          //模板dataset uid
					          String puid=TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, "D9_2nd_Source_Template");					       
					          DataManagementService dataManagementService=DataManagementService.getService(session.getSoaConnection());
					          ServiceData data=dataManagementService.loadObjects(new String[] { puid });
					          TCComponentDataset dataset = (TCComponentDataset)data.getPlainObject(0);
					          TCComponentTcFile[] tcfiles = dataset.getTcFiles();
							  if (tcfiles == null || tcfiles.length == 0) {
									return ;
							  }
							  TCComponentTcFile tcfile = null;						
							  tcfile = tcfiles[0];
							  if (null == tcfile) {
									return ;
							   }
							  //下载文件
							  File exportFile= tcfile.getFmsFile();															
							  exportFile.renameTo(newFile);				          
					      }
						}catch(Exception e) {
							System.out.print(e);
						} 
					}
				  }.start();
			}
			   
		   });
	   
	
	   selectFilebutton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				  new Thread() {
					@Override
					public void run() {
						    //选择文件
						   JFileChooser fileChooser=new JFileChooser();
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
						List<GroupPojo> groupPojos= new ArrayList<GroupPojo>();
						//读取Excel
						FileInputStream fis=null;
						XSSFWorkbook workbook=null;
						int ri=0;
						Map<String,String> gmap=new HashMap<String,String>();
						try {
						  fis = new FileInputStream(strText);
						  workbook = new XSSFWorkbook(fis);
						  XSSFSheet sheet=workbook.getSheetAt(0);										 
						  String lastGroupId=null;
						  GroupPojo lastGroupPojo=null;
						  for(int i=1;i<Integer.MAX_VALUE;i++) {
							  XSSFRow xrow=null;
							  try {  
								    xrow=sheet.getRow(i);
								    XSSFCell cell1=xrow.getCell(1);
								    String itemId=ExcelUtils.getCellValueToString(cell1);
								    if(itemId==null||"".equalsIgnoreCase(itemId.trim())) {
								    	break;
								    }
							  }catch(Exception e0){
								  break;
							  }
							   ri=i;
							   XSSFCell cell0=xrow.getCell(0);
							   String groupIdTmp= ExcelUtils.getCellValueToString(cell0);
							   if(groupIdTmp!=null) {
								   groupIdTmp=groupIdTmp.trim();								 
							   }
							   if(lastGroupId==null||(groupIdTmp!=null&&(!"".equalsIgnoreCase(groupIdTmp))&&!(groupIdTmp.equalsIgnoreCase(lastGroupId)))) {
								   lastGroupId=groupIdTmp;								
								   GroupPojo groupPojo=new GroupPojo();
								   groupPojo.setObjType("D9_MaterialGroup");
								   groupPojo.setBu(bu);
								   groupPojo.setGroupId(""+lastGroupId);
								   groupPojos.add(groupPojo);
								   lastGroupPojo=groupPojo;
							   }
							   XSSFCell cell1=xrow.getCell(1);
							   String itemId=ExcelUtils.getCellValueToString(cell1);
							   if(itemId==null||"".equalsIgnoreCase(itemId)) {
								   throw new Exception("料号为空");
							   }
							   
							   XSSFCell cell2=xrow.getCell(2);
							   String descr=ExcelUtils.getCellValueToString(cell2);
							   if(descr==null||"".equalsIgnoreCase(descr)) {
								   throw new Exception("描述为空");
							   }
							   							   
							   XSSFCell cell3=xrow.getCell(3);
							   String manuId=ExcelUtils.getCellValueToString(cell3);
							   if(manuId==null||"".equalsIgnoreCase(manuId)) {
								   throw new Exception("厂商为空");
							   }
							   
							   XSSFCell cell4=xrow.getCell(4);
							   String manuPn=ExcelUtils.getCellValueToString(cell4);
							   if(manuPn==null||"".equalsIgnoreCase(manuPn)) {
								   throw new Exception("厂商料号为空");
							   }
							   
							   MaterialPojo materialPojo =new MaterialPojo();
							   materialPojo.setDescriptionEn(descr);
							   materialPojo.setItemId(itemId);
							   materialPojo.setManufactureId(manuId);
							   materialPojo.setManufaturePn(manuPn);
							   materialPojo.setObjType("EDAComPart");
							   List<MaterialPojo>  mms=lastGroupPojo.getMaterialPojos();
							   if(mms==null) {
								   mms=new ArrayList<MaterialPojo>();
								   lastGroupPojo.setMaterialPojos(mms);
							   }
							   mms.add(materialPojo);
						  }
						}catch(Exception e0) {
							throw new Exception("解析Excel失敗  第"+(ri+1)+"行"+e0.getLocalizedMessage());
						}
						shell.dispose();
						String mg="";
						String mg1="";
					 for(GroupPojo g:groupPojos) {
						   mg+=g.getGroupId()+","+g.getMaterialPojos().size()+";";
						   if(g.getMaterialPojos().size()<=1) {
							   mg1+=g.getGroupId()+","+g.getMaterialPojos().size()+";";  
						   }   
					 }
					  System.out.println("群组数量"+groupPojos.size());
					   System.out.println(mg);
					   System.out.println(mg1);
						new ImportDialog(app,new Shell(),reg,folderItem,groupPojos);
				   } catch (Exception e2) {
					    okbutton.setEnabled(true);
					    MessageBox.post(AIFUtility.getActiveDesktop(), e2.getMessage(), "Error", MessageBox.ERROR);
				    }
			}
		});
	   
	

   }

    

    
	
}
