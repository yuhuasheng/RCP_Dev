package com.foxconn.mechanism.integrate.hhpnIntegrate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.foxconn.mechanism.util.ExcelUtils;

import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentFolderType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentProcessType;
import com.teamcenter.rac.kernel.TCComponentPseudoFolder;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCComponentTaskTemplateType;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.http.HttpUtil;
public class SyncDialog extends Dialog{
	
	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private Registry reg = null;
	private Shell shell = null;
    private Composite topContainer;
    private Label tipLabel;
    private Composite topMainComposite;
    private Composite topStatusComposite;
    private Composite buttonComposite;
    private Button okButton;
    private Button exportButton;
    private Button preButton;
    private TableViewer tableViewer;
    private ProgressBar progressBar;
    private List<HHPNPojo> datas;
    private String mCreateType;
    private Shell preShell;
    private int mUpFlag;
    private int mReleaseFlag;
    private String mDataFrom;
    private int mRohs;
    private int mSAP=-1;
    TCComponent tCComponentFolder;
	public SyncDialog (TCComponent tCComponentFolder,AbstractAIFUIApplication app, Shell parent,Shell preShell, Registry reg,List<HHPNPojo> datas,String createType ,int upFlag,int releaseFlag,String dataFrom,int rohos,int sap) {
		super(parent);
		this.app = app;
		this.session = (TCSession) this.app.getSession();
		this.shell = parent;
		this.reg = reg;
		this.datas=datas;
		mReleaseFlag=releaseFlag;
		this.preShell=preShell;
		mUpFlag=upFlag;
		mCreateType=createType;
		mDataFrom=dataFrom;
		mRohs=rohos;
		mSAP=sap;
		this.tCComponentFolder=tCComponentFolder;
		initUI();
	}
	
     //初始化UI
	private void initUI() {
		shell = new Shell(shell,SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(1000, 560);
		shell.setText(reg.getString("input.title"));
		shell.setLayout(new FillLayout());
		TCUtil.centerShell(shell);
	
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		SashForm sashForm = new SashForm(shell, SWT.VERTICAL|SWT.NONE);
		topContainer = new Composite(sashForm, SWT.NONE);
		topContainer.setLayout(new FormLayout());
       
		Composite topTipComposite = new Composite(topContainer, SWT.NONE);
		FormData fd0=new FormData();
		fd0.left=new FormAttachment(0);
		fd0.top=new FormAttachment(0);
		fd0.right=new FormAttachment(100);
		fd0.height=50;
		topTipComposite.setLayoutData(fd0);
		FormLayout fl=new FormLayout();
		fl.marginWidth=10;
		fl.marginHeight=10;
		topTipComposite.setLayout(fl);
		tipLabel=new Label(topTipComposite, SWT.NONE|SWT.BOTTOM);	
		FormData fd6=new FormData();
		fd6.left=new FormAttachment(0);
		fd6.right=new FormAttachment(100);
		fd6.bottom=new FormAttachment(100);
		fd6.height=16;
		tipLabel.setLayoutData(fd6);	
		
		topMainComposite = new Composite(topContainer, SWT.NONE);
		FormData fd=new FormData();
		fd.left=new FormAttachment(0);
		fd.top=new FormAttachment(topTipComposite,0);
		fd.right=new FormAttachment(100);
		fd.bottom=new FormAttachment(100,-50);
		topMainComposite.setLayoutData(fd);

		FormLayout fl1=new FormLayout();
		fl1.marginHeight=10;
		fl1.marginWidth=10;
		topMainComposite.setLayout(fl1);
		
		tableViewer=new TableViewer(topMainComposite,SWT.BORDER|SWT.V_SCROLL|SWT.RESIZE|SWT.FULL_SELECTION);
		Table table=tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setHeaderBackground(new Color(null, 211, 211, 211));
		TableLayout tl=new TableLayout();
		table.setLayout(tl);
		table.setLinesVisible(true);
		FormData fd7=new FormData();
		fd7.left=new FormAttachment(0);
		fd7.top=new FormAttachment(0);
		fd7.right=new FormAttachment(100);
		fd7.bottom=new FormAttachment(100);
		table.setLayoutData(fd7);
		
		tl.addColumnData(new ColumnWeightData(5));
		new TableColumn(table,SWT.BORDER|SWT.CENTER).setText(reg.getString("table.head.index"));
		
		tl.addColumnData(new ColumnWeightData(10));
		new TableColumn(table,SWT.BORDER).setText("ID");
		
		tl.addColumnData(new ColumnWeightData(10));
		new TableColumn(table,SWT.BORDER).setText(reg.getString("table.head.datafrom"));
		
		tl.addColumnData(new ColumnWeightData(10));
		new TableColumn(table,SWT.BORDER).setText(reg.getString("table.head.mfg"));
		
		tl.addColumnData(new ColumnWeightData(10));
		new TableColumn(table,SWT.BORDER).setText(reg.getString("table.head.mfgpn"));
		
		tl.addColumnData(new ColumnWeightData(5));
		new TableColumn(table,SWT.BORDER).setText(reg.getString("table.head.un"));
				
		tl.addColumnData(new ColumnWeightData(10));
		new TableColumn(table,SWT.BORDER).setText(reg.getString("table.head.mt"));
		
		tl.addColumnData(new ColumnWeightData(10));
		new TableColumn(table,SWT.BORDER).setText(reg.getString("table.head.mg"));
		
		tl.addColumnData(new ColumnWeightData(10));
		new TableColumn(table,SWT.BORDER).setText(reg.getString("table.head.pt"));
		
		tl.addColumnData(new ColumnWeightData(10));
		new TableColumn(table,SWT.BORDER).setText(reg.getString("table.head.rev"));
		
		tl.addColumnData(new ColumnWeightData(10));
		new TableColumn(table,SWT.BORDER).setText(reg.getString("table.head.descr"));
		
		tableViewer.setContentProvider(new TableViewContentProvider());
		tableViewer.setLabelProvider(new TableLableProvider());
		tableViewer.setUseHashlookup(true);
		
		topStatusComposite = new Composite(topContainer, SWT.NONE);
		FormData fd1=new FormData();
		fd1.left=new FormAttachment(0);
		fd1.top=new FormAttachment(topMainComposite,0);
		fd1.right=new FormAttachment(100);
		fd1.bottom=new FormAttachment(100);
		topStatusComposite.setLayoutData(fd1);
		
		topStatusComposite.setLayout(new FormLayout());

		progressBar = new ProgressBar(topStatusComposite, SWT.HORIZONTAL);
		FormData fd2=new FormData();
		fd2.left=new FormAttachment(0,10);
		fd2.top=new FormAttachment(50,-20);
		fd2.right=new FormAttachment(100,-10);
		fd2.height=25;
		progressBar.setLayoutData(fd2);
		
		buttonComposite = new Composite(sashForm, SWT.NONE);
		buttonComposite.setLayout(new FormLayout());
	    okButton=new Button(buttonComposite,SWT.BUTTON5|SWT.CENTER);
	    okButton.setText(reg.getString("importButton"));
	    FormData fd5=new FormData();
	    fd5.left=new FormAttachment(50,-120);
	    fd5.top=new FormAttachment(50,-12);
	    fd5.height=25;
	    fd5.width=70;
	    okButton.setLayoutData(fd5);
	    
	    exportButton=new Button(buttonComposite,SWT.BUTTON5|SWT.CENTER);
	    exportButton.setText(reg.getString("exportButton"));
	    FormData fd8=new FormData();
	    fd8.left=new FormAttachment(okButton,20);
	    fd8.top=new FormAttachment(50,-12);
	    fd8.height=25;
	    fd8.width=70;
	    exportButton.setLayoutData(fd8);
	    
	    
	    preButton=new Button(buttonComposite,SWT.BUTTON5|SWT.CENTER);
	    preButton.setText(reg.getString("preButton"));
	    FormData fd9=new FormData();
	    fd9.left=new FormAttachment(exportButton,20);
	    fd9.top=new FormAttachment(50,-12);
	    fd9.height=25;
	    fd9.width=70;
	    preButton.setLayoutData(fd9);
	    
		sashForm.setWeights(new int[] {5,1});
		addListener();
		shell.open();
		shell.layout();
		
		exportButton.setEnabled(false);
		okButton.setEnabled(false);
		tableViewer.setInput(datas);
    	progressBar.setMaximum(100);
    	tipLabel.setText(reg.getString("sync.tip.import"));
		getHHPNInfo();
		Display display = shell.getDisplay();	
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	
	}
	
	
//給按鈕添加監聽
private void addListener(){
	
	    //導入按鈕
	   okButton.addSelectionListener( new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				    TCUtil.setBypass(session);
				    tipLabel.setText(reg.getString("sync.tip.process"));
				    okButton.setEnabled(false);
					progressBar.setMaximum(datas.size());
				
					List<TCComponentItemRevision> needReleaseRevs=new ArrayList<>();
					TCComponentFolder folder= findChildFolder();
					Map<String,TCComponentItemRevision> revMap=new HashMap<String,TCComponentItemRevision>();
					for(int i=0;i<datas.size();i++) {
						HHPNPojo hhpnPojo=datas.get(i);
						try {
						progressBar.setSelection(i);
						int  isInTc=hhpnPojo.getIsExistInTC();
						String descr=hhpnPojo.getDescr();
						if(isInTc==1) {
							hhpnPojo.setResult("Success");
							continue;
						}
						if(descr==null||"".equalsIgnoreCase(descr)) {
							hhpnPojo.setResult("Failed");
							continue;
						}						
				
						String objType=getObjType();																			
					    TCComponentItemType tCComponentType=(TCComponentItemType)session.getTypeComponent("Item");
					    String revId=tCComponentType.getNewRev(null);
					    if("D9_CommonPart".equalsIgnoreCase(objType)) {
					    	revId="A";
					    }else if("D9_PCB_Part".equalsIgnoreCase(objType)) {
					    	revId="A";
					    }else if("D9_PCA_Part".equalsIgnoreCase(objType)) {
					    	revId="A";
					    }else if("EDAComPart".equalsIgnoreCase(objType)) {
					    	revId="A";
					    }else if("D9_FinishedPart".equalsIgnoreCase(objType)) {
					    	revId="A";
					    }else if("D9_VirtualPart".equalsIgnoreCase(objType)) {
					    	revId="A";
					    }else if("D9_VirtualPart".equalsIgnoreCase(objType)) {
					    	revId="A";
					    }else if("D9_L5_Part".equalsIgnoreCase(objType)) {
					    	revId="A";
					    }else {
					    	 throw new Exception("不支持的对象了类型");
					    }
					   
					    TCComponentItem tCComponentItem= null;
					    TCComponentItemRevision itemRev=null;
					    String itemId= hhpnPojo.getItemId();
					    if(mDataFrom.equalsIgnoreCase("pnms")) {	
						    if(itemId.endsWith("-G")||itemId.endsWith("-H")) {
							  itemId=itemId.substring(0, itemId.length()-2);
						    }
						     if(mRohs==0) {
						    	 itemId=itemId+"-G";
						      }else if(mRohs==1) {
						    	 itemId=itemId+"-H";
						     }
						}
					    
					    TCComponent[] cs=TCUtil.executeQuery(session, "__Item_Revision_name_or_ID", new String[] {"items_tag.item_id"}, new String[] {itemId});
					    
					    if(cs!=null&&cs.length>0&&getTCComponent(cs)!=null) {
					    	TCComponentItemRevision	iv= (TCComponentItemRevision)getTCComponent(cs);	
					    	tCComponentItem=iv.getItem();
					    } else {  					   
					        tCComponentItem= tCComponentType.create(itemId,revId,objType,itemId,"", null);
					    }
					    tCComponentItem.refresh();
					
						TCComponent[] revions = tCComponentItem.getRelatedComponents("revision_list");
						if (revions != null && revions.length > 0) {
							for (int j = 0; j < revions.length; j++) {
								 itemRev = (TCComponentItemRevision) revions[j];
							}
						}
					    if(itemRev!=null) {
					    	 revMap.put(itemId,itemRev);
					    	 if(itemRev.isValidPropertyName("d9_ManufacturerID")) {
					    		 if(hhpnPojo.getMfg()!=null&&!(hhpnPojo.getMfg().equalsIgnoreCase(""))) {
					    			 itemRev.setProperty("d9_ManufacturerID", hhpnPojo.getMfg());	
						    	 }
				    		 }
					    	 if(itemRev.isValidPropertyName("d9_ManufacturerPN")) {
					    		 if(hhpnPojo.getMfgPN()!=null&&!(hhpnPojo.getMfgPN().equalsIgnoreCase(""))) {
					    			 itemRev.setProperty("d9_ManufacturerPN", hhpnPojo.getMfgPN());	
						    	 }
				    		 }
					    	 
					    	 if(itemRev.isValidPropertyName("d9_MaterialGroup")) {
					    		 if(hhpnPojo.getMaterialGroup()!=null&&!(hhpnPojo.getMaterialGroup().equalsIgnoreCase(""))) {
					    			 itemRev.setProperty("d9_MaterialGroup", hhpnPojo.getMaterialGroup());	
						    	 }
				    		 }
					    	 
					    	 if(itemRev.isValidPropertyName("d9_MaterialType")) {
					    		 if(hhpnPojo.getMaterialType()!=null&&!(hhpnPojo.getMaterialType().equalsIgnoreCase(""))) {
					    			 itemRev.setProperty("d9_MaterialType", hhpnPojo.getMaterialType());	
						    	 }
				    		 }
					    	 
					    	 if(itemRev.isValidPropertyName("d9_Un")) {
					    		 if(hhpnPojo.getUnit()!=null&&!(hhpnPojo.getUnit().equalsIgnoreCase(""))) {
					    			 itemRev.setProperty("d9_Un", hhpnPojo.getUnit());	
						    	 }
				    		 }
					    	 
					    	 if(itemRev.isValidPropertyName("d9_EnglishDescription")) {
					    		 if(hhpnPojo.getDescr()!=null&&!(hhpnPojo.getDescr().equalsIgnoreCase(""))) {
					    			 itemRev.setProperty("d9_EnglishDescription", hhpnPojo.getDescr());	
						    	 }
				    		 }
					    	 
					    	 if(itemRev.isValidPropertyName("d9_Description")) {
					    		 if(hhpnPojo.getDescr()!=null&&!(hhpnPojo.getDescr().equalsIgnoreCase(""))) {
					    			 itemRev.setProperty("d9_Description", hhpnPojo.getDescr());	
						    	 }
				    		 }
					    	 
					    	 if(itemRev.isValidPropertyName("d9_ChineseDescription")) {
					    		 if(hhpnPojo.getCnDescr()!=null&&!(hhpnPojo.getCnDescr().equalsIgnoreCase(""))) {
					    			 itemRev.setProperty("d9_ChineseDescription", hhpnPojo.getCnDescr());	
					    		 }
				    		 }

					    	 if(itemRev.isValidPropertyName("d9_DescriptionSAP")) {
					    		 if(hhpnPojo.getEnDescr()!=null&&!(hhpnPojo.getEnDescr().equalsIgnoreCase(""))) {
					    			 if(hhpnPojo.getDataFrom().equalsIgnoreCase("pnms")) {
					    			    itemRev.setProperty("d9_DescriptionSAP", hhpnPojo.getEnDescr());
					    			 }
					    		 }else if(hhpnPojo.getDescr()!=null&&!(hhpnPojo.getDescr().equalsIgnoreCase(""))) {
					    			 if(hhpnPojo.getDataFrom().equalsIgnoreCase("sap")) {
					    				 itemRev.setProperty("d9_DescriptionSAP", hhpnPojo.getDescr());	
					    			 }
						    	 }
				    		 }
					    	 
					    	 if(itemRev.isValidPropertyName("d9_SAPRev")) {
					    		 if(hhpnPojo.getRev()!=null&&!(hhpnPojo.getRev().equalsIgnoreCase(""))) {
					    				 itemRev.setProperty("d9_SAPRev", hhpnPojo.getRev());	
						    	 }
				    		 }
					    	 					    	  
					    	  if(itemRev.isValidPropertyName("d9_StandardPN")) {
					    	    itemRev.setProperty("d9_StandardPN", itemId);
					    	  }
					    	  
					    	  itemRev.setProperty("object_desc","from "+hhpnPojo.getDataFrom());
					    	  tCComponentItem.setProperty("object_desc","from "+hhpnPojo.getDataFrom());
					    	  if(mReleaseFlag==1) {
					    			needReleaseRevs.add(itemRev);
					    	  }else if(mReleaseFlag==0){
					    		  
					    	  }else if(mReleaseFlag==-1&&!"D9_PCA_Part".equalsIgnoreCase(objType)&&!"D9_CommonPart".equalsIgnoreCase(objType)&&!"D9_L5_Part".equalsIgnoreCase(objType)) {
					    		needReleaseRevs.add(itemRev);
					    	  }
					    	  folder.add("contents", tCComponentItem);					    	
					    }
						hhpnPojo.setResult("Success");
						}catch(Exception e0) {
							hhpnPojo.setResult("Failed");
						}					
					}
				
					try {
						if(tCComponentFolder !=null) {
							for(int i=0;i<datas.size();i++) {
								HHPNPojo hhpnPojo=datas.get(i);
							    String itemId= hhpnPojo.getItemId();
							    if(mDataFrom.equalsIgnoreCase("pnms")) {	
								    if(itemId.endsWith("-G")||itemId.endsWith("-H")) {
									  itemId=itemId.substring(0, itemId.length()-2);
								    }
								     if(mRohs==0) {
								    	 itemId=itemId+"-G";
								      }else if(mRohs==1) {
								    	 itemId=itemId+"-H";
								     }
								}
							    TCComponent[] cs=TCUtil.executeQuery(session, "__Item_Revision_name_or_ID", new String[] {"items_tag.item_id"}, new String[] {itemId});
							    if(cs!=null&&cs.length>0&&getTCComponent(cs)!=null) {
							    	TCComponentItemRevision	iv= (TCComponentItemRevision)getTCComponent(cs);	
							    	if(tCComponentFolder instanceof TCComponentPseudoFolder) {
							    		TCComponentPseudoFolder  tCComponentPseudoFolder=(TCComponentPseudoFolder)tCComponentFolder;
							    		AIFComponentContext[] whereReferenced = tCComponentPseudoFolder.whereReferenced();						    									    	
							  		       for(AIFComponentContext m:whereReferenced) {
							  			     if(m.getComponent() instanceof TCComponentItemRevision ) {
							  			    	  try {
							  			    	     TCComponentItemRevision f=(TCComponentItemRevision) m.getComponent();  
							  				         f.add(tCComponentPseudoFolder.getDefaultPasteRelation(), iv);
							  			    	  }catch(Exception e0) {System.out.println(e0);}
							  			       }
							  		      }
							    	}else if(tCComponentFolder instanceof TCComponentFolder){
							    		      try {
							         	           tCComponentFolder.add(tCComponentFolder.getDefaultPasteRelation(), iv.getItem());
							    		       }catch(Exception e0) {System.out.println(e0);}
							    	}
							    }				    
							}
						}						
					}catch(Exception e0) {
						System.out.println(e0);
					}
					try {
						    TCComponentItemRevision[] items=new TCComponentItemRevision[needReleaseRevs.size()];
						    for(int i=0;i<needReleaseRevs.size();i++) {
							   items[i]=needReleaseRevs.get(i);
						    }  
						   createWorkflow(items,"FXN30_Parts BOM Fast Release Process");
					}catch(Exception e0) {}
					progressBar.setSelection(datas.size());
					tipLabel.setText(reg.getString("sync.tip.done"));
					TCUtil.closeBypass(session);
			}
		});
	   
	   //導出按鈕
	   exportButton.addSelectionListener( new SelectionAdapter() {
            @Override
			public void widgetSelected(SelectionEvent e) {
			         new Thread() {
						@Override
						public void run() {
							   SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
							   JFileChooser fileChooser=new JFileChooser();
						       FileSystemView desk = FileSystemView.getFileSystemView();
						       File comDisk=desk.getHomeDirectory();   
						       fileChooser.setCurrentDirectory(comDisk);
						       fileChooser.setSelectedFile(new File("eda_miss_parts"+sdf.format(new Date())+".xlsx"));
						       int option=fileChooser.showSaveDialog(null);
						       if(option==JFileChooser.APPROVE_OPTION) {
						          File file= fileChooser.getSelectedFile();
						          XSSFWorkbook wb=null;
						          try {
						        	  wb= (XSSFWorkbook)ExcelUtils.createWorkbook("XSSF");
						        	  XSSFSheet sheet= wb.createSheet("eda_miss_parts");
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
						        	  c3.setCellValue("MFG");
						        	  
						        	  XSSFCell c4=r.createCell(4);
						        	  c4.setCellStyle(borderStyle);
						        	  c4.setCellValue("MFG PN");
						        	  
						        	  XSSFCell c5=r.createCell(5);
						        	  c5.setCellStyle(borderStyle);
						        	  c5.setCellValue("Origion");
						        	  
						        	  XSSFCell c6=r.createCell(6);
						        	  c6.setCellStyle(borderStyle);
						        	  c6.setCellValue("Unit");
						        	  
						        	  XSSFCell c7=r.createCell(7);
						        	  c7.setCellStyle(borderStyle);
						        	  c7.setCellValue("Description");
						        	  
						        	  XSSFCell c8=r.createCell(8);
						        	  c8.setCellStyle(borderStyle);
						        	  c8.setCellValue("Result");
						        	  
						        	  for(int i=0;i<datas.size();i++) {
						        		  HHPNPojo hhpnPojo=datas.get(i);
						        		   r=sheet.createRow(i+2);
						        		   c1=r.createCell(1);
						        		   c1.setCellStyle(borderStyle);
							        	   c1.setCellValue(""+hhpnPojo.getIndex());
							        	  
							        	   c2=r.createCell(2);
							        	   c2.setCellStyle(borderStyle);
							        	   c2.setCellValue(hhpnPojo.getItemId());
							        	  						        	  
							        	   c3=r.createCell(3);
							        	   c3.setCellStyle(borderStyle);
							        	   c3.setCellValue(hhpnPojo.getMfg());
							        	  
							        	   c4=r.createCell(4);
							        	   c4.setCellStyle(borderStyle);
							        	   c4.setCellValue(hhpnPojo.getMfgPN());
							        	  
							        	   c5=r.createCell(5);
							        	   c5.setCellStyle(borderStyle);
							        	   String origion=hhpnPojo.getDataFrom();
							        	   if(origion==null) {
							        		   origion="";  
							        	   }
							        	   c5.setCellValue(origion);
							        	  
							        	   c6=r.createCell(6);
							        	   c6.setCellStyle(borderStyle);
							        	   c6.setCellValue(hhpnPojo.getUnit());
							        	  
							        	   c7=r.createCell(7);
							        	   c7.setCellStyle(borderStyle);
							        	   c7.setCellValue(hhpnPojo.getDescr());
						        		  
							        	   c8=r.createCell(8);
							        	   c8.setCellStyle(borderStyle);
							        	   c8.setCellValue(hhpnPojo.getResult());
						        	  }						        	  
						        	  ExcelUtils.writeExcel(wb, file);
						          }catch(Exception e){						        	  
						          }finally {
						        	  try {
						        	     wb.close();
						        	  }catch(Exception e) {}
						          }
						      }
						}			        	 
			         }.start();
		   }
	   });
	   
	   
	   //導出按鈕
	   preButton.addSelectionListener( new SelectionAdapter() {
            @Override
			public void widgetSelected(SelectionEvent e) {
            	shell.dispose();
            	preShell.setVisible(true);
            	
            }
            
	   });
  
	   
   }
	

//調用接口獲取物料信息
private void  getHHPNInfo() {
	       new Thread() {
			@Override
			public void run() {
				try {
				     Display.getDefault().syncExec(new Runnable() {
		        	       public void run() {
		  				     progressBar.setSelection(10);
		        	       }
		        	    }); 
				     String items="";
				     for(HHPNPojo hhpnPojo:datas) {
				     	 String itemId=hhpnPojo.getItemId();
				    	 if(mDataFrom.equalsIgnoreCase("pnms")) {	
							    if(itemId.endsWith("-G")||itemId.endsWith("-H")) {
								  itemId=itemId.substring(0, itemId.length()-2);
							    }
							     if(mRohs==0) {
							    	 itemId=itemId+"-G";
							      }else if(mRohs==1) {
							    	 itemId=itemId+"-H";
							     }
							}
					     items=items+itemId+";";
				      }
				     if(items.endsWith(";")) {
				    	 items=items.substring(0, items.length()-1);
				     }
			      if(mUpFlag==-1) {
				       TCComponent[] cs=TCUtil.executeQuery(session, "__Item_Revision_name_or_ID", new String[] {"items_tag.item_id"}, new String[] {items});
					    if(cs!=null&&cs.length>0) {
						  for(TCComponent c:cs) {
							String id= c.getProperty("item_id");
							String objectType=c.getTypeObject().getName();
							for(HHPNPojo hhpnPojo:datas){
								 String itemId=hhpnPojo.getItemId();
						    	 if(mDataFrom.equalsIgnoreCase("pnms")) {	
									    if(itemId.endsWith("-G")||itemId.endsWith("-H")) {
										  itemId=itemId.substring(0, itemId.length()-2);
									    }
									     if(mRohs==0) {
									    	 itemId=itemId+"-G";
									      }else if(mRohs==1) {
									    	 itemId=itemId+"-H";
									     }
									}
								 if(id.equalsIgnoreCase(itemId)) {
									 if(mCreateType.equalsIgnoreCase("L5 Part")) {
										 if(objectType.indexOf("D9_L5_Part")>-1) {
											    hhpnPojo.setIsExistInTC(1);
												hhpnPojo.setDataFrom("TC");
												break;
										 }
									 }else {
										 if(objectType.indexOf("D9_L5_Part") <0) {
									         hhpnPojo.setIsExistInTC(1);
									         hhpnPojo.setDataFrom("TC");
									          break;
										  }
									 }								
								 }
							}
						  }
					    }
			         }
			
				    Display.getDefault().syncExec(new Runnable() {
		        	    public void run() {
		  				   progressBar.setSelection(50);
		        	    }
		        	}); 
						  
				     String url=TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
				     if(url==null||"".equalsIgnoreCase(url)) {
				    	 throw new Exception(reg.getString("urltip"));
				     }
				     if(mSAP==1) {
				    	  url="http://10.203.163.43"; 
				     }else if(mSAP==2) {
				    	 url="http://10.203.163.184"; 
				     }
				
				    String jsons = JSONArray.toJSONString(datas);				   
				    String rs=HttpUtil.post(url+"/tc-integrate/agile/partManage/getPartInfo", jsons);
				    if(rs==null||"".equalsIgnoreCase(rs)) {
				    	 throw new Exception(reg.getString("errdata"));	
				    }
					System.out.print(rs);
				
				    List<HHPNPojo> rsTmps=null;
				    try {
					     rsTmps =JSON.parseArray(rs, HHPNPojo.class);
				    }catch(Exception e) {
				    	 throw new Exception(reg.getString("errdata"));	
				    }
					if(rsTmps!=null&&rsTmps.size()>0) {
						datas.clear();
						datas.addAll(rsTmps);
					}
			
				    Display.getDefault().syncExec(new Runnable() {
				        	    public void run() {
				        	    	updateTable();
				        	    }
				        });
				}catch(Exception e) {
					System.out.print(e);
					  Display.getDefault().syncExec(new Runnable() {
		        	    public void run() {
		        	    	MessageBox.post(AIFUtility.getActiveDesktop(), e.getMessage(), "Error", MessageBox.ERROR);
		        	    }
		        	    });
				}
			}   
	       }.start();
	   
   }

  //更新table數據
private void updateTable() {
		try {
			   tipLabel.setText(reg.getString("sync.tip.comp"));
			   progressBar.setSelection(75);			
		
			   tableViewer.refresh();
		       Table table=tableViewer.getTable();
		       int cnt= table.getItemCount();
		       int n=0;
		       for(int i=0;i<cnt;i++) {
		    	   TableItem tableItem=table.getItem(i);
		    	   String descr=((HHPNPojo)tableItem.getData()).getDescr();
		    	   int isInTC=((HHPNPojo)tableItem.getData()).getIsExistInTC();
		    	   if((descr==null||"".equalsIgnoreCase(descr))&&isInTC==0) {
		    		   Color color=Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
				       tableItem.setBackground(1, color);
				       table.redraw();
				       n++;
		    	   }			     
		       }		       
		     exportButton.setEnabled(true);
		     if(n<cnt||tCComponentFolder!=null) {
		    	 okButton.setEnabled(true); 
		     }
		   	 progressBar.setSelection(100);		   
		}catch(Exception e) {
		 	MessageBox.post(AIFUtility.getActiveDesktop(), e.getMessage(), "Error", MessageBox.ERROR);  
		}
}


//創建流程
	private void createWorkflow(TCComponentItemRevision[] itemRevisons,String taskTemplate) throws Exception{
		TCComponentTaskTemplateType taskTemplateType = (TCComponentTaskTemplateType)session
				.getTypeComponent("EPMTaskTemplate");
		TCComponentTaskTemplate[] taskTemplates = taskTemplateType.getProcessTemplates(false, false, (TCComponent[]) null, (String[]) null,
				(String) null);
		TCComponentTaskTemplate tCComponentTaskTemplate=null;
		for(TCComponentTaskTemplate t:taskTemplates) {
			if(taskTemplate.equalsIgnoreCase(t.getName())) {
				tCComponentTaskTemplate=t;
				break;
			}
		}
		TCComponentProcessType localTCComponentProcessType = (TCComponentProcessType)session.getTypeComponent("Job");
		int[] var7 = new int[itemRevisons.length];
		Arrays.fill(var7, 1);
		localTCComponentProcessType.create(""+new Date().getTime(), taskTemplate, tCComponentTaskTemplate,itemRevisons ,var7);
	}
	
	private TCComponentFolder findChildFolder() {
		   TCComponentFolder childFolder=null;
    	try {
    		   TCComponentFolder homeFolder= session.getUser().getHomeFolder();
    		   homeFolder.refresh();
    		   TCComponentFolder eDAFolder=null;
    		   SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmm");
    		   TCComponentFolderType folderType =(TCComponentFolderType)session.getTypeComponent("Folder");		    	
    		   String childFolderName=sdf.format(new Date());
  		       AIFComponentContext[]  ms=homeFolder.getChildren();
  		       for(AIFComponentContext m:ms) {
  			     if(m.getComponent() instanceof TCComponentFolder ) {
  				       TCComponentFolder f=(TCComponentFolder) m.getComponent();  			
  				       String folderName=f.getProperty("object_name"); 
  				       if("miss_parts".equalsIgnoreCase(folderName)) {
  				    	 eDAFolder=f;
  				    	 break;
  				       }
  			       }
  		      }
  		     if(eDAFolder==null) {
  		    	eDAFolder=folderType.create("miss_parts", "miss_parts", "Folder");
  		    	homeFolder.add("contents", eDAFolder);
  		    	childFolder=folderType.create(childFolderName, childFolderName, "Folder");
  		    	eDAFolder.add("contents", childFolder);
  		    	return childFolder;
  		     }
  		   ms=eDAFolder.getChildren();
  		   for(AIFComponentContext m:ms) {
			     if(m.getComponent() instanceof TCComponentFolder ) {
				       TCComponentFolder f=(TCComponentFolder) m.getComponent();  			
				       String folderName=f.getProperty("object_name"); 
				       if("childFolderName".equalsIgnoreCase(folderName)) {
				    	   childFolder=f;
				    	 break;
				       }
			       }
		      } 
  		   if(childFolder==null) { 
		    	childFolder=folderType.create(childFolderName, childFolderName, "Folder");
		    	eDAFolder.add("contents", childFolder);
		    	return childFolder;
  		   }
  		   
  		}catch(Exception e) {}
    	
    	return childFolder;
    }
	

private  void replaceMissPartsFromPSE (Map<String,TCComponentItemRevision> revMap)  {
			 		
		   InterfaceAIFComponent interfaceAIFComponent=null;
		   if(app.getApplicationId().contains("PSEApplication")) {
				interfaceAIFComponent = this.app.getTargetComponent();
				if(interfaceAIFComponent==null) {
				  return;
				}
			}else {
				return ;
			}
		   
		   TCComponentBOMLine bomLine = (TCComponentBOMLine) interfaceAIFComponent;
		   try {
		      String pro =bomLine.getProperty("bl_line_object");
		      if(pro.contains("missing_part_number")) {
		    	    String itemId =bomLine.getProperty("bl_occ_d9_FoxconnPartNumber");						
					if(revMap.get(itemId)!=null) {
						TCComponentItemRevision rev=revMap.get(itemId);
						bomLine.replace(null, rev, null);
						bomLine.save();
						bomLine.refresh();
					}
					TCComponentBOMLine[] packlines=bomLine.getPackedLines();
					if(packlines!=null&&packlines.length>0) {
						for(TCComponentBOMLine packline :packlines ) {
							itemId =packline.getProperty("bl_occ_d9_FoxconnPartNumber");	
							if(revMap.get(itemId)!=null) {
								TCComponentItemRevision rev=revMap.get(itemId);
								packline.replace(null, rev, null);
								packline.save();
								packline.refresh();
							}
							
						}
					}
		      }
		   }catch(Exception e) {}
		   try {
				   AIFComponentContext[] children = bomLine.getChildren();
				  for(AIFComponentContext aifComponentContext : children) {
					InterfaceAIFComponent component = aifComponentContext.getComponent();
					if (!(component instanceof TCComponentBOMLine)) {
						continue;
					}
					TCComponentBOMLine childBomLine = (TCComponentBOMLine) component;
					String pro =childBomLine.getProperty("bl_line_object");
					if(pro==null ||(!pro.contains("missing_part_number"))) {
						continue;
					}
					String itemId =childBomLine.getProperty("bl_occ_d9_FoxconnPartNumber");	
					if(revMap.get(itemId)!=null) {
						TCComponentItemRevision rev=revMap.get(itemId);
						childBomLine.replace(null, rev, null);
						childBomLine.save();
						childBomLine.refresh();
					}
					
					TCComponentBOMLine[] packlines=childBomLine.getPackedLines();
					if(packlines!=null&&packlines.length>0) {
						for(TCComponentBOMLine packline :packlines ) {
							itemId =packline.getProperty("bl_occ_d9_FoxconnPartNumber");	
							if(revMap.get(itemId)!=null) {
								TCComponentItemRevision rev=revMap.get(itemId);
								packline.replace(null, rev, null);
								packline.save();
								packline.refresh();
							}
						}
					}
				
				}
				bomLine.save();
				bomLine.refresh();
		   }catch(Exception e) {
			   System.out.print(e);
		   }
	   }
	   
//獲取對象類型
private String getObjType() {
	 
	String objType="";
	 String[] createTypePre=TCUtil.getArrayByPreference(session, TCPreferenceService.TC_preference_site, "D9_PartInfo_CreateType");
	  
	    for(int i=0;i<createTypePre.length;i++) {
	    	String s=createTypePre[i].split("=")[0].trim();
	    	if(s.equalsIgnoreCase(mCreateType)) {
	    		return createTypePre[i].split("=")[1].trim();
	    	}
	    }

	return objType;
}



   private TCComponent getTCComponent(TCComponent[] cs) {
	            for(TCComponent c:cs) {
	            	if(mCreateType.equalsIgnoreCase("L5 Part")) {
	            		if(c.getTypeObject().getName().indexOf("D9_L5_Part")>-1) {
	            			 return c; 
	            		}
	            	}else {
	            		if(c.getTypeObject().getName().indexOf("D9_L5_Part")<0) {
	            			 return c; 
	            		}
	            	}	            	 
	            }
	            
	            return null;
	  
      }


}
