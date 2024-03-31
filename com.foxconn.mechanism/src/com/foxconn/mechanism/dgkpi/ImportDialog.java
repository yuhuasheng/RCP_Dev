package com.foxconn.mechanism.dgkpi;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.schemas.soa._2006_03.exceptions.ServiceException;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateIn;
import com.teamcenter.services.rac.core._2008_06.DataManagement.CreateResponse;
import com.teamcenter.soa.client.model.ErrorStack;
public class ImportDialog extends Dialog{
	
	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private Registry reg = null;
	private Shell shell = null;
	private Shell parent = null;
	private Shell  preShell=null;
	private String path;
    private Composite topContainer;
    private Composite topMainComposite;
    private Composite topStatusComposite;
    private Composite buttonComposite;
    private Button preButton;
    private Button closeButton;
    private ProgressBar progressBar;
    private TCComponentItemRevision itemRevision;
    private Text textComp;
    private String msgString="";
    
	public ImportDialog (AbstractAIFUIApplication app, Shell parent,Shell preShell, Registry reg,String path,TCComponentItemRevision itemRevision) {
		super(parent);
		this.app = app;
		this.session = (TCSession) this.app.getSession();
		this.parent = parent;
		this.preShell=preShell;
		this.itemRevision=itemRevision;
		this.reg = reg;
		this.path=path;
		initUI();
	}
	
     //初始化UI
	private void initUI() {
		shell = new Shell(parent,SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.PRIMARY_MODAL);
		shell.setSize(480, 500);
		shell.setText(reg.getString("import.title"));
		shell.setLayout(new FillLayout());
		TCUtil.centerShell(shell);
	
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		SashForm sashForm = new SashForm(shell, SWT.VERTICAL|SWT.NONE);
		topContainer = new Composite(sashForm, SWT.NONE);
		topContainer.setLayout(new FormLayout());
       

		topMainComposite = new Composite(topContainer, SWT.BORDER);
		FormData fd=new FormData();
		fd.left=new FormAttachment(0,10);
		fd.top=new FormAttachment(0,10);
		fd.right=new FormAttachment(100,-10);
		fd.bottom=new FormAttachment(100,-50);
		topMainComposite.setLayoutData(fd);

		textComp=new Text(topMainComposite,SWT.NONE|SWT.MULTI|SWT.READ_ONLY);
		FormData fd0=new FormData();
		fd0.left=new FormAttachment(0);
		fd0.top=new FormAttachment(0);
		fd0.right=new FormAttachment(100);
		fd0.bottom=new FormAttachment(100);
		textComp.setLayoutData(fd0);
		
		
		FormLayout fl1=new FormLayout();
		fl1.marginHeight=10;
		fl1.marginWidth=10;
		topMainComposite.setLayout(fl1);
		
		
		topStatusComposite = new Composite(topContainer, SWT.NONE);
		FormData fd1=new FormData();
		fd1.left=new FormAttachment(0);
		fd1.top=new FormAttachment(topMainComposite,10);
		fd1.right=new FormAttachment(100);
		fd1.bottom=new FormAttachment(100);
		topStatusComposite.setLayoutData(fd1);
		
		topStatusComposite.setLayout(new FormLayout());

		progressBar = new ProgressBar(topStatusComposite, SWT.HORIZONTAL);
		FormData fd2=new FormData();
		fd2.left=new FormAttachment(0,12);
		fd2.top=new FormAttachment(50,-20);
		fd2.right=new FormAttachment(100,-10);
		fd2.height=25;
		progressBar.setLayoutData(fd2);
		
		buttonComposite = new Composite(sashForm, SWT.NONE);
		buttonComposite.setLayout(new FormLayout());
	    preButton=new Button(buttonComposite,SWT.BUTTON5|SWT.CENTER);
	    preButton.setText(reg.getString("preButton"));
	    FormData fd5=new FormData();
	    fd5.left=new FormAttachment(50,-80);
	    fd5.top=new FormAttachment(50,-12);
	    fd5.height=25;
	    fd5.width=70;
	    preButton.setLayoutData(fd5);
	    
	    closeButton=new Button(buttonComposite,SWT.BUTTON5|SWT.CENTER|SWT.V_SCROLL);
	    closeButton.setText(reg.getString("closeButton"));
	    FormData fd6=new FormData();
	    fd6.left=new FormAttachment(50,10);
	    fd6.top=new FormAttachment(50,-12);
	    fd6.height=25;
	    fd6.width=70;
	    closeButton.setLayoutData(fd6);
	    
	 
	    
		sashForm.setWeights(new int[] {7,1});
		addListener();
		shell.open();
		shell.layout();
		
		closeButton.setEnabled(false);
		preButton.setEnabled(false);
	
    	progressBar.setMaximum(100);
    	importData();
		Display display = shell.getDisplay();	
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	
	}
	
	private void importData() {
		new Thread() {

			@Override
			public void run() {
				
				 try {
					 Display.getDefault().syncExec(new Runnable() {
		        	       public void run() {
					          msgString+="开始读取ID特徵庫模板文件...\r\n";
					          progressBar.setSelection(10);
					          textComp.setText(msgString);
		        	       }
					   }); 
						//读取Excel
					    FileInputStream fis=null;				
												
						fis = new FileInputStream(path);
						PDDocument document = Loader.loadPDF(fis); 
						PDFTextStripper stripper = new PDFTextStripper();
						stripper.setSortByPosition(true);
						stripper.setWordSeparator("|");
						String text = stripper.getText(document);
						        
						String templateUIdString= TCUtil.getPreference(session, TCPreferenceService.TC_preference_site,"D9_ID_Feature_Template");
						if(templateUIdString==null||"".equalsIgnoreCase(templateUIdString)) {
							 Display.getDefault().syncExec(new Runnable() {
				        	       public void run() {
							          msgString+="ID特徵庫模板文件不存在\r\n";
							          progressBar.setSelection(100);
							          textComp.setText(msgString);
				        	          }
				        	       }); 
							throw new Exception("ID特徵庫模板文件不存在");
						}
						TCComponentDataset  dataset=(TCComponentDataset)TCUtil.loadObjectByUid(templateUIdString);
						if(dataset==null) {
							Display.getDefault().syncExec(new Runnable() {
				        	       public void run() {
							          msgString+="ID特徵庫模板文件不存在\r\n";
							          progressBar.setSelection(100);
							          textComp.setText(msgString);
				        	       }
			        	       });
							 throw new Exception("ID特徵庫模板文件不存在");
						}
						        
						TCComponentTcFile[] files = dataset.getTcFiles();      
						if(files==null||files.length<=0) {
							Display.getDefault().syncExec(new Runnable() {
				        	       public void run() {
							          msgString+="ID特徵庫模板文件不存在\r\n";
							          progressBar.setSelection(100);
							          textComp.setText(msgString);
				        	       }
			        	       });
						    throw new Exception("ID特徵庫模板文件不存在");
					    } 
						
						XSSFWorkbook wb=new XSSFWorkbook(new FileInputStream(files[0].getFmsFile()));
						XSSFSheet sheet=wb.getSheetAt(0);
						int cnt=sheet.getLastRowNum();
						Map<String, String> idMap=new HashedMap<String, String>();
						for(int i=1;i<cnt;i++) {
						  XSSFRow row=sheet.getRow(i);
						  String idString=row.getCell(0).getStringCellValue();
						  String nameString=row.getCell(1).getStringCellValue();
						  System.out.println(idString+"  "+ nameString);	
						  if(idString.equalsIgnoreCase("id")) {
							 continue; 
						  }
						  if(idString==null||"".equalsIgnoreCase(idString)) {
						    break;	
						  }
						  idMap.put(idString.trim(), idString.trim());
						}
						
						List<IdFeaturePojo>  idFeaturePojos= new ArrayList<IdFeaturePojo>();
						String[] mStrings=text.split("\r\n");
						Set<String> keys=idMap.keySet();
						for(String t:mStrings) {
							if(t==null||"".equalsIgnoreCase(t.trim())) {
								continue;
							}
							int g=0;
							if(t.toLowerCase().contains("id-")) {
								msgString+="读取到数据:"+t.replaceAll("\\|", " ");		
							}else {
								g=1;
							}
							int f=0;
						    for(String key:keys) {
						      if(t.toUpperCase().contains(key.toUpperCase())) {
						    	 t= t.substring(t.toUpperCase().indexOf(key.toUpperCase()));
						    	 String[]  tmpStrings=t.split("\\|");
						    	 if(tmpStrings[0].trim().equalsIgnoreCase(key)) {
						    		 IdFeaturePojo idFeaturePojo= new IdFeaturePojo();
						    		 idFeaturePojo.setId(tmpStrings[0].trim());
						    		 idFeaturePojo.setName(tmpStrings[1].trim());
						    		 idFeaturePojo.setQty(tmpStrings[2].trim());
						    		 idFeaturePojos.add(idFeaturePojo);
						             f=1;
						             if(g==1) {
				        	    		   msgString+="读取到数据:"+t.replaceAll("\\|", " ");	
				        	    	   }
						    		 Display.getDefault().syncExec(new Runnable() {
						        	       public void run() {
						 			           msgString+=" (特征库特征)\r\n";
						 			           textComp.setText(msgString);
						        	       }
					        	       });
						    	 }
						      }	
						      
						    }
						    if(f==0&&t.toLowerCase().contains("id-")) {
						    	 Display.getDefault().syncExec(new Runnable() {
					        	       public void run() {
					 			           msgString+=" (非特征库特征)\r\n";
					 			           textComp.setText(msgString);
					        	       }
				        	       });
						    }
						    
						    
						}  
						wb.close();
						Display.getDefault().syncExec(new Runnable() {
			        	       public void run() {						
						          progressBar.setSelection(30);
						          textComp.setText(msgString);
			        	       }
		        	       });
						Display.getDefault().syncExec(new Runnable() {
			        	       public void run() {
						          msgString+="开始写入匹配的特征值到对象上...\r\n";
						          textComp.setText(msgString);
			        	       }
						   });
						TCComponent[]  coms=itemRevision.getRelatedComponents("d9_IDFeaturesTable");
						for(TCComponent  m:coms) {
							itemRevision.remove("d9_IDFeaturesTable", m);						
						}
						Display.getDefault().syncExec(new Runnable() {
			        	       public void run() {
						          progressBar.setSelection(70);
			        	       }
		        	       });
						for(IdFeaturePojo idFeaturePojo:idFeaturePojos) {
							TCComponent component=createObjects(session, "D9_IDFeaturesTableRow", idFeaturePojo.getId(), idFeaturePojo.getName(), idFeaturePojo.getQty());
							if(component!=null) {
							   itemRevision.add("d9_IDFeaturesTable", component);
							}
						}
						Display.getDefault().syncExec(new Runnable() {
			        	       public void run() {
						           msgString+="开始上传PDF文件...\r\n";
						           textComp.setText(msgString);
			        	       }
						   });
						File file = new File(path);
						TCComponentDataset pdfdataset = TCUtil.createDataSet(TCUtil.getTCSession(), file.getAbsolutePath(), "PDF",file.getName(), "PDF_Reference");
						itemRevision.add("IMAN_specification", pdfdataset);
						Display.getDefault().syncExec(new Runnable() {
			        	       public void run() {						  
						          msgString+="数据导入成功\r\n";
						          progressBar.setSelection(100);
						          textComp.setText(msgString);
						          closeButton.setEnabled(true);
								  preButton.setEnabled(true);
			        	       }
						   });
						
						
					} catch (Exception e2) {
						 Display.getDefault().syncExec(new Runnable() {
			        	       public void run() {
						         closeButton.setEnabled(true);
						         preButton.setEnabled(true);
			        	       }
						   });
						    MessageBox.post(AIFUtility.getActiveDesktop(), e2.getMessage(), "Error", MessageBox.ERROR);
					 }
				
			}
			
		}.start();
		 
	}
	
	
	
	
	private  TCComponent createObjects(TCSession session,String type, String id,String name,String qty) {
		try {
			if(id==null||"".equalsIgnoreCase(id)) {
				return null;
			}
			if(name==null||"".equalsIgnoreCase(name)) {
				return null;
			}
			if(qty==null||"".equalsIgnoreCase(qty)) {
				return null;
			}
			DataManagementService dmService = DataManagementService.getService(session);
			CreateIn[] createIn = new CreateIn[1];
			createIn[0] = new CreateIn();
//			D9_IDFeaturesTableRow
			createIn[0].data.boName = type;
//			d9_FeaturesID d9_FeaturesName d9_FeaturesQTY
			createIn[0].data.stringProps.put("d9_FeaturesID", id);
			createIn[0].data.stringProps.put("d9_FeaturesName", name);
			createIn[0].data.stringProps.put("d9_FeaturesQTY", qty);
			
			CreateResponse createObjects = dmService.createObjects(createIn);
			int sizeOfPartialErrors = createObjects.serviceData.sizeOfPartialErrors();
			for (int i = 0; i < sizeOfPartialErrors; i++) {
				ErrorStack partialError = createObjects.serviceData.getPartialError(i);
				String[] messages = partialError.getMessages();
				for (String string : messages) {
					System.out.println(string);
				}
			}
			if(createObjects.output.length > 0){
				return createObjects.output[0].objects[0];
			}

		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return null;
	}
	
//給按鈕添加監聽
private void addListener(){
	
	 
	   preButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
				preShell.setVisible(true);
			}
		});
	   
	   closeButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				 shell.dispose();
				 preShell.dispose();
			}
		});
   }
	


}
