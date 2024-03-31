package historicaldataimport.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.services.loose.query._2007_06.SavedQuery.SavedQueryResults;
import com.teamcenter.services.strong.core._2008_06.DataManagement.CreateResponse;
import com.teamcenter.soa.client.Connection;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;

import historicaldataimport.domain.Constants;
import historicaldataimport.domain.ExcelRowData;
import historicaldataimport.domain.FolderInfo;
import historicaldataimport.utils.PropertitesUtil;
import historicaldataimport.utils.Utils;

public class ProjectHDIDialog extends Dialog{
	
	private Shell shell = null;
	private Shell parentShell = null;
	private TCSession session = null;
	private Registry reg = null;
	private Button uploadButton = null;
	private Button downloadButton = null;
	private Button importButton = null;
	private Text logText = null;
//	private static Connection soaConnection = null;
	private String spasUser = "spas";
	private String adminUser = "admin";
	private File teamRosterTemplate = null;
	private static final Logger log = LoggerFactory.getLogger(ProjectHDIDialog.class);

	public ProjectHDIDialog(Shell parentShell,TCSession session) {
		super(parentShell);
		this.parentShell = parentShell;
		this.session = session;
//		this.soaConnection = session.getSoaConnection();
		reg = Registry.getRegistry("historicaldataimport.hdi");
		initUI();
	}
	
//	public static Connection getSOAConnection() {
//		return soaConnection;
//	}

	//构建界面
	private void initUI() {
		String serverAddress = session.getSoaConnection().getServerAddress();
		if(serverAddress.contains("192")) {
			spasUser = "spas1";
			adminUser = "apadmin";
		}
		shell = new Shell(parentShell,SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(460, 500);
		shell.setText(reg.getString("dialogTitle"));
		shell.setLayout(new GridLayout(1, false));
		Utils.centerShell(shell);
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.grabExcessHorizontalSpace = true;
		
		Composite composite1 = new Composite(shell, SWT.NONE);
		composite1.setLayout(new GridLayout(2, false));
		composite1.setLayoutData(gridData);
		
		Text filePathText = new Text(composite1, SWT.SINGLE | SWT.BORDER);
		filePathText.setEnabled(false);
		filePathText.setLayoutData(gridData);
		
		uploadButton = new Button(composite1, SWT.NONE);
		uploadButton.setText(reg.getString("uploadFile"));
		
		GridData gridData0 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData0.grabExcessHorizontalSpace = true;
		gridData0.heightHint = 360;
		
		logText = new Text(shell, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		logText.setLayoutData(gridData0);
		logText.setEditable(false);
		
		Composite composite2 = new Composite(shell, SWT.NONE);
		composite2.setLayout(new GridLayout(2, true));
		composite2.setLayoutData(gridData);
		
		GridData gridData1 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.horizontalAlignment = GridData.END;
		
		downloadButton = new Button(composite2, SWT.NONE);
		downloadButton.setText(reg.getString("downloadTemplate"));
		downloadButton.setLayoutData(gridData1);
		
		importButton = new Button(composite2, SWT.NONE);
		importButton.setText(reg.getString("importFile"));
		
		downloadButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				try {
					TCComponentDataset dataset = (TCComponentDataset) session.stringToComponent(Constants.TEMPLATE_PUID);
					Utils.downloadFile(dataset, Utils.getSystemDesktop(), reg.getString("exceptionInfo"));
					MessageDialog.openInformation(shell, reg.getString("prompt"), reg.getString("promptInfo1"));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		uploadButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				String filePath = Utils.openFileChooser(shell);
				if(filePath == null) {
					return;
				}
				filePathText.setText(filePath);
			}
		});
		
		importButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				
				String filePath = filePathText.getText();
				if("".equals(filePath)) {
					MessageDialog.openInformation(shell, reg.getString("prompt"), reg.getString("promptInfo2"));
					return;
				}
				//重新开线程处理业务，解决UI界面卡死问题
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							setButtonState(false);
							Sheet sheet = checkExcelData(filePath);
							List<ExcelRowData> excelRowDatas = readSheetData(sheet);
							excelDataHandle(excelRowDatas);
							writeLogText("数据导入完成...");
							log.info("数据导入完成...");
							setButtonState(true);
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}).start();
			}
		});

		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	private void setButtonState(boolean isEnabled) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				uploadButton.setEnabled(isEnabled);
				downloadButton.setEnabled(isEnabled);
				importButton.setEnabled(isEnabled);
			}
		});
	}
	
	/**
	 * 检验excel有效行列单元格不可为空值
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private Sheet checkExcelData(String filePath) throws Exception{
		writeLogText("数据校验...\n");
		log.info("数据校验...\n");
		File excelFile = new File(filePath);
		Sheet sheet = Utils.getSheet(excelFile);
		int rowCount = sheet.getPhysicalNumberOfRows();
		writeLogText("读取到"+ (rowCount-1) + "条数据...\n");
		log.info("读取到"+ (rowCount-1) + "条数据...\n");
		if(rowCount < 2) {
			log.info("文件中没有可导入的数据！");
			throw new Exception("文件中没有可导入的数据！");
		}
		for (int i = 1; i < rowCount; i++) {
			Row row = sheet.getRow(i);
			for (int j = 1; j <= 10; j++) {
				Cell cell = row.getCell(j);
				if(cell == null) {
					log.info("第" + i + "行，第" + (j+1) + "列,不可为空值！");
					throw new Exception("第" + i + "行，第" + (j+1) + "列,不可为空值！");
				}
			}
		}
		return sheet;
	}
	
	/**
	 * 读取excel数据转换为对象
	 * @param sheet
	 */
	private List<ExcelRowData> readSheetData(Sheet sheet){
		List<ExcelRowData> excelRowDatas = new ArrayList<ExcelRowData>();
		int rowCount = sheet.getPhysicalNumberOfRows();
		for (int i = 1; i < rowCount; i++) {
			Row row = sheet.getRow(i);
			ExcelRowData excelRowData = new ExcelRowData();
			excelRowData.setRowNumber(i);
			for (int j = 1; j <= 10; j++) {
				Cell cell = row.getCell(j);
				cell.setCellType(CellType.STRING);
				String cellValue = cell.getStringCellValue();
				switch (j) {
				case 1:
					excelRowData.setCustomerId(cellValue);
					break;
				case 2:
					excelRowData.setCustomerName(cellValue);
					break;
				case 3:
					excelRowData.setSeriesId(cellValue);
					break;
				case 4:
					excelRowData.setSeriesName(cellValue);
					break;
				case 5:
					excelRowData.setProductLineId(cellValue);
					break;
				case 6:
					excelRowData.setProductLineName(cellValue);
					break;
				case 7:
					excelRowData.setProjectId(cellValue);
					break;
				case 8:
					excelRowData.setProjectName(cellValue);
					break;
				case 9:
					excelRowData.setProjectLevel(cellValue);
					break;
				case 10:
					excelRowData.setProjectPhase(cellValue);
					break;
				default:
					break;
				}
			}
			excelRowDatas.add(excelRowData);
		}
		return excelRowDatas;
	}
	
	
	private void excelDataHandle(List<ExcelRowData> excelRowDatas) throws Exception{
		writeLogText("导入数据...\n");
		log.info("导入数据...\n");
		for (int i = 0; i < excelRowDatas.size(); i++) {
			ExcelRowData excelRowData = excelRowDatas.get(i);
			writeLogText("处理第" + (i+1) + "行数据开始...\n");
			log.info("处理第" + (i+1) + "行数据开始...\n");
			ModelObject customerFolder = null;
			boolean cIsNewCreate = false;
			String customerId = "c" + excelRowData.getCustomerId();
			String customerName = excelRowData.getCustomerName();
			SavedQueryResults queryResults1 = Utils.executeSOAQuery(session, Constants.FIND_PROJECT_FOLDER, new String[] {Constants.SPAS_ID}, new String[] {customerId});
			if(queryResults1.objects.length == 0) {
				String[] projectKnowledgeBase = Utils.getSOATCPreferences(session, Constants.PROJECT_KNOWLEDGE_FOLDER_UID);
				ModelObject folder = Utils.findObjectByUid(session, projectKnowledgeBase[0]);
				customerFolder = createCSPFolder(folder,customerId,customerName,"",Constants.CUSTOMER);
				cIsNewCreate = true;
				writeLogText("客户【" + customerName + "】" + "导入成功...\n");
				log.info("客户【" + customerName + "】" + "导入成功...\n");
			}else {
				writeLogText("客户【" + customerName + "】" + "已存在，不做处理！\n");
				log.info("客户【" + customerName + "】" + "已存在，不做处理！\n");
			}
			
			ModelObject seriesFolder = null;
			boolean sIsNewCreate = false;
			String seriesId = "s" + excelRowData.getSeriesId();
			String seriesName = excelRowData.getSeriesName();
			String productLineName = excelRowData.getProductLineName();
			String buName = getBUName(customerName, productLineName);
			if(buName == null) {
				writeLogText("未找到BU！程序结束...\n");
				log.info("未找到BU！程序结束...\n");
				throw new Exception("未找到BU！程序结束...");
			}
			SavedQueryResults queryResults2 = Utils.executeSOAQuery(session, Constants.FIND_PROJECT_FOLDER, new String[] {Constants.SPAS_ID}, new String[] {seriesId});
			if(queryResults2.objects.length == 0) {
				if(customerFolder == null) {
					SavedQueryResults queryResults3 = Utils.executeSOAQuery(session, Constants.FIND_PROJECT_FOLDER, new String[] {Constants.SPAS_ID}, new String[] {customerId});
					if(queryResults3.objects.length > 0) {
						customerFolder = queryResults3.objects[0];
						sIsNewCreate = true;
					}else {
						writeLogText("系列【" + seriesName + "】" + "未找到客户，不做处理！\n");
						log.info("系列【" + seriesName + "】" + "未找到客户，不做处理！\n");
					}
				}
				seriesFolder = createCSPFolder(customerFolder,seriesId,seriesName,buName,Constants.SERIES);
				writeLogText("系列【" + seriesName + "】" + "导入成功...\n");
				log.info("系列【" + seriesName + "】" + "导入成功...\n");
			}else {
				writeLogText("系列【" + seriesName + "】" + "已存在，不做处理！\n");
				log.info("系列【" + seriesName + "】" + "已存在，不做处理！\n");
				if(cIsNewCreate && !sIsNewCreate) {
					writeLogText("创建客户前系列已经存在，数据错误！此行数据跳过...\n");
					log.info("创建客户前系列已经存在，数据错误！此行数据跳过...\n");
					continue;
				}
			}
			
			String projectId = "p" + excelRowData.getProjectId();
			String projectName = excelRowData.getProjectName();
			SavedQueryResults queryResults4 = Utils.executeSOAQuery(session, Constants.FIND_PROJECT_FOLDER, new String[] {Constants.SPAS_ID}, new String[] {projectId});
			if(queryResults4.objects.length == 0) {
				if(seriesFolder == null) {
					SavedQueryResults queryResults5 = Utils.executeSOAQuery(session, Constants.FIND_PROJECT_FOLDER, new String[] {Constants.SPAS_ID}, new String[] {seriesId});
					if(queryResults5.objects.length > 0) {
						seriesFolder = queryResults5.objects[0];
					}else {
						writeLogText("专案【" + projectName + "】" + "未找到系列，不做处理！\n");
						log.info("专案【" + projectName + "】" + "未找到系列，不做处理！\n");
					}
				}
				
				//===解决专案同名===
				SavedQueryResults queryResults6 = Utils.executeSOAQuery(session, Constants.FIND_PROJECT_FOLDER, new String[] {Constants.OBJECT_NAME}, new String[] {projectName});
				if (queryResults6.objects.length > 0) {
					projectName = seriesName + "_" + projectName;
				}
				//===解决专案同名===
				
				ModelObject projectFolder = createCSPFolder(seriesFolder,projectId,projectName,"",Constants.PLATFORMFOUND);
				projectName = projectName.replaceAll("\\*","_").replaceAll("\\.","_").replaceAll("%","_").replaceAll("@","_");
				if(teamRosterTemplate == null) {
					teamRosterTemplate = Utils.getTeamRosterTemplate(session);
				}
				
				ModelObject projectAdmin = Utils.getUser(session, adminUser);
				ModelObject adminGroupMember = Utils.getUserGroupMember(session, projectAdmin);
				ModelObject user = Utils.getUser(session, spasUser);
				ModelObject groupMember = Utils.getUserGroupMember(session, user);
				List<String> teamRoster = getTeamRoster(teamRosterTemplate,customerName, buName);
				ModelObject[] teamRosterUser = getTeamRosterUser(teamRoster);
				ModelObject[] teamRosterGroupMember = getTeamRosterGroupMember(teamRosterUser);
				ModelObject newProject = Utils.createTCProject(session, projectId.toUpperCase(), projectName, "",
						projectAdmin, adminGroupMember,user, groupMember, teamRosterUser, teamRosterGroupMember);
				writeLogText("创建TC_Project完成！\n");
				log.info("创建TC_Project完成！\n");
				Utils.assignedProject(session, projectFolder, newProject);
				writeLogText("专案【" + projectName + "】" + "导入成功...\n");
				log.info("专案【" + projectName + "】" + "导入成功...\n");
				
				ModelObject workspaceFolder = crateWorkspace(buName,newProject);
				Utils.addContents(session, projectFolder, workspaceFolder);
				if("DT".equals(buName)) {
					String projectPhase = excelRowData.getProjectPhase();
					crtateDTFolder(customerName, projectFolder, excelRowData.getProductLineName(),projectPhase);
				}else {
					crtatePMFolder(buName, projectFolder);
				}
			}else {
				writeLogText("专案【" + projectName + "】" + "已存在，不做处理！\n");
				log.info("专案【" + projectName + "】" + "已存在，不做处理！\n");
			}
			
			writeLogText("处理第" + (i+1) + "行数据结束...\n");
			log.info("处理第" + (i+1) + "行数据结束...\n");
		}
	}
	
	private ModelObject createCSPFolder(ModelObject parentFolder,String id, String name,String bu, String folderType) throws Exception{
		Map<String,String> propMap = new HashMap<>();
        propMap.put(Constants.SPAS_ID,id);
        propMap.put(Constants.OBJECT_NAME,name);
        if("D9_ProjectSeriesFolder".equals(folderType)) {
        	propMap.put("object_desc",bu);
        }
		CreateResponse response = Utils.createObjects(session, folderType, propMap);
		ServiceData serviceData = response.serviceData;
		if(serviceData.sizeOfPartialErrors() > 0) {
			throw new Exception(serviceData.getPartialError(0).toString());
		}
		ModelObject folder = response.output[0].objects[0];
		Utils.addContents(session, parentFolder, folder);
		return folder;
	}
	
	/**
	 *根据客户、产品线获取BU
	 * @param customerName
	 * @param productLineName
	 * @return
	 */
	private String getBUName(String customerName,String productLineName){
        customerName = customerName.replaceAll(" ","char(32)").replaceAll("_","char(95)");
        productLineName = productLineName.replaceAll(" ","char(32)").replaceAll("_","char(95)");
        String buKey = customerName + "_" + productLineName;
        return PropertitesUtil.props.getProperty(buKey);
    }
	
	private List<String> getTeamRoster(File teamRosterTemplate, String customerName, String bu) throws Exception{
		writeLogText("读取TeamRoster用户...\n");
        log.info("读取TeamRoster用户...");
		XSSFWorkbook wb = Utils.getWorkbook(teamRosterTemplate);
        XSSFSheet sheet = null;
        if("DT".equalsIgnoreCase(bu)){
            sheet = Utils.getSheet(wb, "DT");
        }else if("PRT".equalsIgnoreCase(bu)){
            sheet = Utils.getSheet(wb, "PRT");
        }else if("MNT".equalsIgnoreCase(bu)){
            sheet = Utils.getSheet(wb, "MNT");
        }else{
            throw new Exception("读取Excel模板失败");
        }

        HashMap<String, List<String>> tmls = new HashMap<>();
        String tmp = "";
        //解析Excel 按客户分组
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            String dept = Utils.getCellValue(row, 2);
            if (!dept.equalsIgnoreCase("")) {
                tmp = dept;
            } else {
                dept = tmp;
            }
            String userNumber = Utils.getCellValue(row, 4);
            List<String> users = tmls.get(dept);
            if (users == null) {
                users = new ArrayList<>();
                tmls.put(dept, users);
            }
            users.add(userNumber);
        }
        //专案成员
        List<String> projectPersonls = tmls.get(customerName);
        if(projectPersonls==null){
            projectPersonls=new  ArrayList<>();
        }
        List<String> projectPersonlsAll = tmls.get("All");
        if (projectPersonlsAll != null) {
            for (String p : projectPersonlsAll) {
                projectPersonls.add(p);
            }
        }
        return projectPersonls;
	}
	
	/**
     * 创建工作区
     * @param project
     * @return
	 * @throws Exception 
     */
    private ModelObject crateWorkspace(String BU,ModelObject TCProject) throws Exception{
        String[] workAreaTemps = Utils.getSOATCPreferences(session, Constants.WORKAREA_FOLDER_TEMPLATE);
        if(workAreaTemps == null){
        	writeLogText("创建工作区时未找到【D9_WorkAreaFolderTemplate】首选项,请联系管理员！程序结束...\n");
        	log.info("创建工作区时未找到【D9_WorkAreaFolderTemplate】首选项,请联系管理员！程序结束...\n");
        	throw new Exception("创建工作区时未找到【D9_WorkAreaFolderTemplate】首选项,请联系管理员！程序结束");
        }

        String tempId = "";
        for (int i = 0; i < workAreaTemps.length; i++) {
            String workAreaTemp = workAreaTemps[i];
            String[] temp = workAreaTemp.split(":");
            String buName = temp[0];
            if(buName.equals(BU)){
                tempId = temp[1];
                break;
            }
        }

        SavedQueryResults queryResults = Utils.executeSOAQuery(session, Constants.ITEM_NAME_OR_ID, new String[]{Constants.ITEM_ID}, new String[]{tempId});
        if(queryResults.objects.length == 0){
        	writeLogText("创建工作区时未找到【D9_WorkAreaFolderTemplate】首选项,请联系管理员！程序结束...\n");
        	log.info("创建工作区时未找到【D9_WorkAreaFolderTemplate】首选项,请联系管理员！程序结束...\n");
        	throw new Exception("创建工作区时未找到【D9_WorkAreaFolderTemplate】首选项,请联系管理员！程序结束");
        }
        TCComponentItem item = (TCComponentItem) queryResults.objects[0];
        TCComponentBOMLine topBOMLine = Utils.sendPSE(session, item.getLatestItemRevision());
        Map<String,String> propMap = new HashMap<>();
        propMap.put("object_name",topBOMLine.getProperty("bl_item_object_name"));
        propMap.put("object_desc",topBOMLine.getProperty("bl_item_object_desc"));
        CreateResponse response = Utils.createObjects(session, Constants.WORKAREA, propMap);
		ServiceData serviceData = response.serviceData;
		if(serviceData.sizeOfPartialErrors() > 0) {
			throw new Exception(serviceData.getPartialError(0).toString());
		}
		ModelObject topFolder = response.output[0].objects[0];
		Utils.assignedProject(session, topFolder,TCProject);
        
        List<FolderInfo> folderInfoList = Utils.getBOMChildren(topBOMLine);
        for (int i = 0; i < folderInfoList.size(); i++) {
        	FolderInfo wafi = folderInfoList.get(i);
        	Map<String,String> propMap1 = new HashMap<>();
        	propMap1.put("object_name",wafi.getName());
        	propMap1.put("object_desc",wafi.getDesc());
    		CreateResponse response1 = Utils.createObjects(session, Constants.WORKAREA, propMap1);
    		ServiceData serviceData1 = response1.serviceData;
    		if(serviceData1.sizeOfPartialErrors() > 0) {
    			throw new Exception(serviceData1.getPartialError(0).toString());
    		}
    		ModelObject folder = response1.output[0].objects[0];
    		Utils.assignedProject(session, folder,TCProject);
    		Utils.addContents(session, topFolder, folder);
		}
        return topFolder;
    }
    
    private ModelObject[] getTeamRosterUser(List<String> projectPersonls) throws Exception{
    	ModelObject[] users = null;
        String userStrs="";
        for(int i=0;i<projectPersonls.size();i++){
            String p=  projectPersonls.get(i);
            userStrs+= p+";";
        }
        if(userStrs.endsWith(";")){
            userStrs= userStrs.substring(0,userStrs.length()-1);
        }
        SavedQueryResults queryResults = Utils.executeSOAQuery(session, Constants.WEB_FIND_USER,new String[]{Constants.USER_ID},new String[]{userStrs});
        if(queryResults.objects.length == 0){
        	writeLogText("TeamRoster用户在TC中未找到...\n");
        	log.info("TeamRoster用户在TC中未找到...\n");
        	return users;
        }
        users = queryResults.objects;
        return users;
    }
    
    private ModelObject[] getTeamRosterGroupMember(ModelObject[] teamRosterUsers) throws Exception{
    	if(teamRosterUsers == null) {
    		return null;
    	}
    	ModelObject[] groupMembers = new ModelObject[teamRosterUsers.length];
    	for (int i = 0; i < teamRosterUsers.length; i++) {
    		ModelObject teamRosterUser = teamRosterUsers[i];
    		ModelObject userGroupMember = Utils.getUserGroupMember(session, teamRosterUser);
    		groupMembers[i] = userGroupMember;
		}
        return groupMembers;
    }
    
    /**
          * 创建DT部门文件夹
     * @param customerName
     * @param projectFolder
     * @param projectPhase
     * @throws Exception
     */
    public void crtateDTFolder(String customerName, ModelObject projectFolder,String ProductLineName, String projectPhase) throws Exception{
        List<String> projectPhaseList = Arrays.asList(projectPhase.split(","));
        Map<String, List<String>> dtTemplatesInfoMap = Utils.getDTTemplatesInfo(session, customerName, projectPhaseList);
        if(dtTemplatesInfoMap == null){
        	writeLogText("创建部门文件夹时未找到【" + customerName +"】模板，程序结束...\n");
        	log.info("创建部门文件夹时未找到【" + customerName +"】模板，程序结束...\n");
        	throw new Exception("创建部门文件夹时未找到【" + customerName +"】模板，程序结束...\n");
        }
        if (("Lenovo".equals(customerName)) && ("WKS".equals(ProductLineName) || "Card".equals(ProductLineName))){
            dtTemplatesInfoMap.remove("PSU");
        }
        if (("HP".equals(customerName)) && ("WKSBPC".equals(ProductLineName) || "Card".equals(ProductLineName))){
            dtTemplatesInfoMap.remove("PSU");
        }
        for (Map.Entry<String, List<String>> s : dtTemplatesInfoMap.entrySet()) {
           String department = s.getKey();
           ModelObject departFolder = Utils.createDepartFolder(session,projectFolder, department);
           List<String> phaseInfos = s.getValue();
           Utils.createPhaseArchiveFolder(session, phaseInfos,departFolder);
        }
    }
    
    /**
          * 获取DT模板信息
     * @param customerName
     * @param projectPhaseList
     * @return
     */
//    public Map<String,List<String>> getDTTemplatesInfo(String customerName,List<String> projectPhaseList){
//        Map<String,List<String>> departPhaseMap = null;
//        String[] projectTemplates = Utils.getSOATCPreferences(session, Constants.DTSA_PROJECT_FOLDER_TEMPLATE1);
//        List<String> projectTemplateList = Arrays.asList(projectTemplates);
//        projectTemplateList = projectTemplateList.stream().filter(
//                p -> p.split("_")[0].equals(customerName)).collect(Collectors.toList());
//        for (int i = 0; i < projectTemplateList.size(); i++) {
//            String projectTemplate = projectTemplateList.get(i);
//            String[] customerDepartPhase = projectTemplate.split("\\|");
//            String depart = customerDepartPhase[0].split("_")[1];
//            String templatePhase = customerDepartPhase[1];
//            List<String> templatePhaseInfos = containsPhase(templatePhase, projectPhaseList);
//            if(departPhaseMap == null){
//                departPhaseMap = new HashMap<>();
//            }
//            departPhaseMap.put(depart,templatePhaseInfos);
//        }
//        return departPhaseMap;
//    }
    
    /**
          * 筛选包含的阶段模板
     * @param templatePhase
     * @param projectPhaseList
     * @return
     */
    private List<String> containsPhase(String templatePhase,List<String> projectPhaseList){
        List<String> platformFoundPhaseInfos = null;
        String[] phaseAndTemplates = templatePhase.split(",");
        for (int i = 0; i < phaseAndTemplates.length; i++) {
            String phaseAndTemplate = phaseAndTemplates[i];
            String[] split = phaseAndTemplate.split(":");
            if(projectPhaseList.contains(split[0]) || "P10".equals(split[0])){
                if(platformFoundPhaseInfos == null){
                    platformFoundPhaseInfos = new ArrayList();
                }
                platformFoundPhaseInfos.add(split[1]);
            }
        }
        return platformFoundPhaseInfos;
    }
    
    /**
          * 创建部门文件夹
     * @param projectFolder
     * @param department
     * @return
     * @throws Exception
     */
//    public ModelObject createDepartFolder(ModelObject projectFolder, String department) throws Exception{
//        Map<String,String> propMap = new HashMap<>();
//        propMap.put("object_name",department);
//        CreateResponse response = Utils.createObjects(session, Constants.FUNCTION, propMap);
//        ServiceData serviceData = response.serviceData;
//		if(serviceData.sizeOfPartialErrors() > 0) {
//			throw new Exception(serviceData.getPartialError(0).toString());
//		}
//		ModelObject departmentFolder = response.output[0].objects[0];
//        Utils.addContents(session, projectFolder,departmentFolder);
//        return departmentFolder;
//    }
    
    /**
           *创建阶段、资料文件夹
     * @param projectPhaseInfos
     * @param departFolder
     * @throws Exception
     */
//    public void createPhaseArchiveFolder(List<String> projectPhaseInfos, ModelObject departFolder) throws Exception{
//		String folderType = Constants.PHASE;
//        for (int i = 0; i < projectPhaseInfos.size(); i++) {
//            String projectPhaseInfo = projectPhaseInfos.get(i);
//            SavedQueryResults queryResults = Utils.executeSOAQuery(session, Constants.ITEM_NAME_OR_ID, new String[]{Constants.ITEM_ID}, new String[]{projectPhaseInfo});
//            if(queryResults.objects.length == 0){
//            	writeLogText("创建阶段文件夹时未找到【" + projectPhaseInfo +"】模板，程序结束...\n");
//            	log.info("创建阶段文件夹时未找到【" + projectPhaseInfo +"】模板，程序结束...\n");
//            	throw new Exception("创建阶段文件夹时未找到【" + projectPhaseInfo +"】模板，程序结束...\n");
//            }
//            TCComponentItem item = (TCComponentItem) queryResults.objects[0];
//            TCComponentBOMLine topBOMLine = Utils.sendPSE(session, item.getLatestItemRevision());
//            Map<String,String> propMap = new HashMap<>();
//            String name = topBOMLine.getProperty("bl_rev_object_name");
//            String desc = topBOMLine.getProperty("bl_rev_object_desc");
//            propMap.put("object_name",name);
//            if(!"".equals(desc)){
//                folderType = Constants.ARCHIVE;
//            }
//            CreateResponse response = Utils.createObjects(session, folderType, propMap);
//            ServiceData serviceData = response.serviceData;
//    		if(serviceData.sizeOfPartialErrors() > 0) {
//    			throw new Exception(serviceData.getPartialError(0).toString());
//    		}
//    		ModelObject phaseFolder = response.output[0].objects[0];
//    		Utils.addContents(session, departFolder,phaseFolder);
//            
//            List<FolderInfo> folderInfoList = Utils.getBOMChildren(topBOMLine);
//            if(folderInfoList.size() > 0) {
//            	for (int j = 0; j < folderInfoList.size(); j++) {
//            		FolderInfo folderInfo = folderInfoList.get(j);
//                	Map<String,String> propMap1 = new HashMap<>();
//                	propMap1.put("object_name",folderInfo.getName());
//                	CreateResponse response1 = Utils.createObjects(session, Constants.ARCHIVE, propMap1);
//                    ServiceData serviceData1 = response1.serviceData;
//            		if(serviceData1.sizeOfPartialErrors() > 0) {
//            			throw new Exception(serviceData1.getPartialError(0).toString());
//            		}
//            		ModelObject archiveFolder = response1.output[0].objects[0];
//            		Utils.addContents(session, phaseFolder,archiveFolder);
//				}
//            }
//        }
//    }

    /**
     * 创建PRT、MNT部门文件夹
     * @param BU
     * @param projectFolder
     * @throws Exception 
     */
    public void crtatePMFolder(String BU,ModelObject projectFolder) throws Exception{
        Map<String, List<String>> templatesInfoMap = Utils.getPMTemplatesInfo(session, BU);
        writeLogText("正在创建部门、阶段、资料文件夹，请稍等...\n");
        log.info("正在创建部门、阶段、资料文件夹，请稍等...\n");
        for (Map.Entry<String, List<String>> templatesInfo : templatesInfoMap.entrySet()) {
            String depart = templatesInfo.getKey();
            ModelObject departFolder = Utils.createDepartFolder(session, projectFolder, depart);
            Utils.createPhaseArchiveFolder(session, templatesInfo.getValue(),departFolder);
        }
    }
    
    /**
     *获取PRT、MNT模板信息
     * @param BU
     * @return
     */
//    public Map<String,List<String>> getPMTemplatesInfo(String BU){
//        String[] projectTemplates = Utils.getSOATCPreferences(session, Constants.IPBD_PROJECT_FOLDER_TEMPLATE);
//        Map<String,List<String>> templatesInfoMap = null;
//        for (int i = 0; i < projectTemplates.length; i++) {
//            String projectTemplate = projectTemplates[i];
//            String[] templateInfo = projectTemplate.split("\\|");
//            String template0 = templateInfo[0];
//            String[] buOrDepart = template0.split("_");
//            if(buOrDepart[0].equals(BU)){
//                String s = buOrDepart[1];
//                String template1 = templateInfo[1];
//                String[] split = template1.split(",");
//                List<String> templateIds = new ArrayList<>();
//                for (int j = 0; j < split.length; j++) {
//                    String s1 = split[j];
//                    String templateId = s1.split(":")[1];
//                    templateIds.add(templateId);
//                }
//                if(templatesInfoMap == null){
//                    templatesInfoMap = new HashMap<>();
//                }
//                templatesInfoMap.put(s,templateIds);
//            }
//        }
//        return templatesInfoMap;
//    }
    
    private void writeLogText(String message){
    	
    	Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				logText.append(message);
			}
		});
    }
}
