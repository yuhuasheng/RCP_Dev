package com.foxconn.mechanism.SecondSourceImport;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.mechanism.util.ExcelUtils;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

import cn.hutool.http.HttpUtil;



public class ImportDialog extends Dialog{
	
	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private TCComponentFolder folderItem;
	private Registry reg = null;
	private Shell shell = null;
    private Button okbutton =null;
    private Text textContent;
    private String msg="";
    private List<GroupPojo> groupPojos;
    private List<GroupPojo> errorGroups=new ArrayList<>();
	public ImportDialog (AbstractAIFUIApplication app, Shell parent, Registry reg,TCComponentFolder  folderItem,List<GroupPojo> groupPojos) {
		super(parent);
		this.app = app;
		this.folderItem=folderItem;
		this.session = (TCSession) this.app.getSession();
		this.shell = parent;
		this.reg = reg;	
		this.groupPojos=groupPojos;
		initUI();
	}
	
	//构建界面
	private void initUI() {
		shell = new Shell(shell,SWT.DIALOG_TRIM | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(990, 560);
		shell.setText("替代料导入");
		shell.setLayout(new FillLayout());
		TCUtil.centerShell(shell);
		
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		Composite mainComposite = new Composite(shell, SWT.NONE);
		mainComposite.setLayout(new FormLayout());
 
		Composite middleComposite = new Composite(mainComposite, SWT.NONE);
		FormData fd=new FormData();
		fd.left=new FormAttachment(0);
		fd.top=new FormAttachment(10);
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
	    okbutton.setText(reg.getString("logButton"));
	    FormData fd5=new FormData();
	    fd5.left=new FormAttachment(50,-35);
	    fd5.top=new FormAttachment(50,-12);
	    fd5.height=25;
	    fd5.width=90;
	    okbutton.setLayoutData(fd5); 
	    okbutton.setEnabled(false);
	  
		addListener();
		shell.open();
		shell.layout();			
		 new Thread() {
				@Override
				public void run() {
					try {
					String url=TCUtil.getPreference(TCUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					ImportCommand importCommand=new ImportCommand();
					 TCComponentFolder avlFolder=TCUtil.createFolder(session, "Folder", "AVL 2nd Source");
					 folderItem.add("contents", avlFolder);
					 folderItem.refresh();
					 JSONArray logs=new JSONArray();
					 String creator=session.getUser().getUserId();
					 String creatorName=session.getUser().getUserName();
					for( GroupPojo groupPojo:groupPojos) {
						JSONObject log=new JSONObject();
					  try {
						   log.put("functionName", "在TC系統中批量建立替代群組時間");
						   log.put("creator", creator);
						   log.put("creatorName", creatorName);
						   log.put("rev", "A");
						   Date dat1= new Date();
						   HashMap<String,String>  rp=importCommand.createGroup(session, groupPojo, folderItem,textContent,avlFolder);
					       if(rp==null) {
					    	   continue;
					       } 
					       Date dat2= new Date();
					       log.put("itemId", rp.get("itemid"));
					       log.put("revUid", rp.get("uid"));
					       log.put("startTime", sdf.format(dat1));
					       log.put("endTime", sdf.format(dat2));
					  }catch(Exception e) {
						  errorGroups.add(groupPojo);
						  System.out.print(e);
						  Display.getDefault().syncExec(new Runnable() {
				        	    public void run() {
				        	    	List<MaterialPojo> mms=groupPojo.getMaterialPojos();
				        	    	for(MaterialPojo m:mms) {
				        	    	   textContent.append("处理报错:"+m.getItemId()+" "+e.getMessage()+"\n" );
				        	    	}
				        	    }
				         }); 
					  }
					  logs.add(log);
					}
					HttpUtil.post(url + "/tc-integrate/actionlog/addlog", logs.toJSONString());						
					Display.getDefault().syncExec(new Runnable() {
			        	    public void run() {
			        	    	textContent.append("导入完成...\n" );
			        	    	okbutton.setEnabled(true);
			        	    }
			         });	
					
					}catch(Exception e) {
						Display.getDefault().syncExec(new Runnable() {
			        	    public void run() {
			        	    	textContent.append(e.getMessage()+"\n" );
			        	    	okbutton.setEnabled(true);
			        	    }
			         });
					}
				}
		    }.start();
		
		
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
					   if(errorGroups.size()<=0) {
						   throw new Exception("沒有錯誤信息");
					   }
					   SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
					   JFileChooser fileChooser=new JFileChooser();
				       FileSystemView desk = FileSystemView.getFileSystemView();
				       File comDisk=desk.getHomeDirectory();   
				       fileChooser.setCurrentDirectory(comDisk);
				       fileChooser.setSelectedFile(new File("2ndSource_import_failed_parts"+sdf.format(new Date())+".xlsx"));
				       int option=fileChooser.showSaveDialog(null);
				       if(option==JFileChooser.APPROVE_OPTION) {
				          File file= fileChooser.getSelectedFile();
				          XSSFWorkbook wb=null;
				          try {
				        	  wb= (XSSFWorkbook)ExcelUtils.createWorkbook("XSSF");
				        	  XSSFSheet sheet= wb.createSheet("failed group");
				        	  sheet.setColumnWidth(2, 5000);
				        	  sheet.setColumnWidth(7, 5000);
				        	  XSSFCellStyle borderStyle=wb.createCellStyle();
				        	  borderStyle.setBorderBottom(BorderStyle.THIN);
				        	  borderStyle.setBorderLeft(BorderStyle.THIN);
				        	  borderStyle.setBorderTop(BorderStyle.THIN);
				        	  borderStyle.setBorderRight(BorderStyle.THIN);
				        	  XSSFRow r=sheet.createRow(1);
				        	  XSSFCell c1=r.createCell(1);
				        	  c1.setCellStyle(borderStyle);
				        	  c1.setCellValue("Index");
				        	  
				        	  XSSFCell c2=r.createCell(2);
				        	  c2.setCellStyle(borderStyle);
				        	  c2.setCellValue("Item ID");
				        	  		
				        	  XSSFCell c3=r.createCell(3);
				        	  c3.setCellStyle(borderStyle);
				        	  c3.setCellValue("Description");
				        	  
				        	  XSSFCell c4=r.createCell(4);
				        	  c4.setCellStyle(borderStyle);
				        	  c4.setCellValue("MFG");
				        	  
				        	  XSSFCell c5=r.createCell(5);
				        	  c5.setCellStyle(borderStyle);
				        	  c5.setCellValue("MFG PN");
				        
				        	  int k=2;
				        	  for(GroupPojo groupPojo:errorGroups) {
				        		   List<MaterialPojo> mms= groupPojo.getMaterialPojos();
				        		   if(mms==null||mms.size()<=1) {
				        			  continue;
				        		   }
				        		   for(MaterialPojo m:mms) {
				        			   r=sheet.createRow(k);
					        		   c1=r.createCell(1);
					        		   c1.setCellStyle(borderStyle);
						        	   c1.setCellValue(groupPojo.getGroupId());
						        	  
						        	   c2=r.createCell(2);
						        	   c2.setCellStyle(borderStyle);
						        	   c2.setCellValue(m.getItemId());
						        	  						        	  
						        	   c3=r.createCell(3);
						        	   c3.setCellStyle(borderStyle);
						        	   c3.setCellValue(m.getDescriptionEn());
						        	  
						        	   c4=r.createCell(4);
						        	   c4.setCellStyle(borderStyle);
						        	   c4.setCellValue(m.getManufactureId());
						        	  
						        	   c5=r.createCell(5);
						        	   c5.setCellStyle(borderStyle);						        	
						        	   c5.setCellValue(m.getManufaturePn());
				        			   k++;
				        		   }
				        	  }						        	  
				        	  ExcelUtils.writeExcel(wb, file);
				          }catch(Exception e1){						        	  
				          }finally {
				        	  try {
				        	     wb.close();
				        	  }catch(Exception e1) {}
				          }
				      }
				
					   
				   } catch (Exception e2) {
					    MessageBox.post(AIFUtility.getActiveDesktop(), e2.getMessage(), "Error", MessageBox.ERROR);
				    }
			}
		});
	   
	

   }

    

    
	
}
