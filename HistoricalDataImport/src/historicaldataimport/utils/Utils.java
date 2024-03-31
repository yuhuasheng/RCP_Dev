package historicaldataimport.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.filechooser.FileSystemView;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentGroup;
import com.teamcenter.rac.kernel.TCComponentGroupMember;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCUserService;
import com.teamcenter.services.loose.core.ProjectLevelSecurityService;
import com.teamcenter.services.loose.core._2007_09.ProjectLevelSecurity.AssignedOrRemovedObjects;
import com.teamcenter.services.loose.core._2012_09.ProjectLevelSecurity;
import com.teamcenter.services.loose.core._2012_09.ProjectLevelSecurity.ProjectOpsResponse;
import com.teamcenter.services.loose.core._2012_09.ProjectLevelSecurity.TeamMemberInfo;
import com.teamcenter.services.loose.core._2017_05.ProjectLevelSecurity.ModifyProjectsInfo2;
import com.teamcenter.services.loose.core._2017_05.ProjectLevelSecurity.ProjectInformation2;
import com.teamcenter.services.loose.query.SavedQueryService;
import com.teamcenter.services.loose.query._2006_03.SavedQuery;
import com.teamcenter.services.loose.query._2006_03.SavedQuery.GetSavedQueriesResponse;
import com.teamcenter.services.loose.query._2007_06.SavedQuery.ExecuteSavedQueriesResponse;
import com.teamcenter.services.loose.query._2007_06.SavedQuery.SavedQueryInput;
import com.teamcenter.services.loose.query._2007_06.SavedQuery.SavedQueryResults;
import com.teamcenter.services.strong.administration.PreferenceManagementService;
import com.teamcenter.services.strong.administration._2012_09.PreferenceManagement;
import com.teamcenter.services.strong.administration._2012_09.PreferenceManagement.GetPreferencesResponse;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.services.strong.core._2006_03.DataManagement.Relationship;
import com.teamcenter.services.strong.core._2007_01.DataManagement.WhereReferencedResponse;
import com.teamcenter.services.strong.core._2008_06.DataManagement;
import com.teamcenter.services.strong.core._2008_06.DataManagement.CreateIn;
import com.teamcenter.services.strong.core._2008_06.DataManagement.CreateInput;
import com.teamcenter.services.strong.core._2008_06.DataManagement.CreateResponse;
import com.teamcenter.soa.client.model.ModelObject;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.soa.client.model.strong.Folder;
import com.teamcenter.soa.client.model.strong.TC_Project;
import com.teamcenter.soa.client.model.strong.WorkspaceObject;

import historicaldataimport.dialog.ProjectHDIDialog;
import historicaldataimport.domain.Constants;
import historicaldataimport.domain.FolderInfo;

public class Utils {
	
	public static void centerShell(Shell shell) {
		int width = shell.getMonitor().getClientArea().width;
		int height = shell.getMonitor().getClientArea().height;
		int x = shell.getSize().x;
		int y = shell.getSize().y;
		if (x > width) {
			shell.getSize().x = width;
		}
		if (y > height) {
			shell.getSize().y = height;
		}
		shell.setLocation((width - x) / 2, (height - y) / 2);
	}
	
	/**
	 * 打开文件选择器
	 * @return	文件路径
	 */
	public static String openFileChooser(Shell shell){
		FileDialog fileDialog = new FileDialog(shell);
		fileDialog.setFilterPath(getSystemDesktop());
		fileDialog.setFilterNames(new String[] {"Microsoft Excel(*.xlsx)","Microsoft Excel(*.xls)"});
		fileDialog.setFilterExtensions(new String[] {"*.xlsx","*.xls"});
		return fileDialog.open();
	}
	
	/**
	 * 获取系统桌面路径
	 * @return
	 */
	public static String getSystemDesktop() {
		File desktopDir = FileSystemView.getFileSystemView() .getHomeDirectory();
		return desktopDir.getAbsolutePath();
	}
	
	/**
	 * 下载数据集
	 * @param dataset 数据集
	 * @param dir 文件存放路径
	 * @throws Exception 
	 */
	public static void downloadFile(TCComponentDataset dataset, String dir, String exceptionInfo) throws Exception {
		TCComponentTcFile[] files = dataset.getTcFiles();
		if (files == null || files.length == 0) {
			throw new Exception(exceptionInfo);
		}
		TCComponentTcFile tcfile = files[0];
		String fileName = tcfile.getProperty("original_file_name");
		File tempfile = tcfile.getFmsFile();
		File newfile = new File(dir + File.separator + fileName);
		copyFile(tempfile, newfile);
	}
	
	/**
	 * 复制文件
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}
	
	public static Sheet getSheet(File file){
		Workbook wb = null;
		Sheet sheet = null;
		try {
			if(file.getName().endsWith("xls")){
				wb = new HSSFWorkbook(new FileInputStream(file));
			}else {
				wb = new XSSFWorkbook(new FileInputStream(file));
			}
			sheet = wb.getSheetAt(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sheet;
	}
	
	public static List<Sheet> getSheet(File file, String[] sheetNames){
		Workbook wb = null;
		List<Sheet> sheetList = new ArrayList<>();
		try {
			if(file.getName().endsWith("xls")){
				wb = new HSSFWorkbook(new FileInputStream(file));
			}else {
				wb = new XSSFWorkbook(new FileInputStream(file));
			}
			for (int i = 0; i < sheetNames.length; i++) {
				String sheetName = sheetNames[i];
				Sheet sheet = wb.getSheet(sheetName);
				if(sheet != null) {
					sheetList.add(sheet);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sheetList;
	}
	
	public static XSSFWorkbook getWorkbook(File f) throws Exception{
        FileInputStream in= new FileInputStream(f);
        XSSFWorkbook  wb=  new XSSFWorkbook(in);
        return wb;
    }
	
	public static XSSFSheet getSheet(XSSFWorkbook wb, String name) throws Exception{
        return wb.getSheet(name);
    }
	
	public static String getCellValue(XSSFRow row, int i) throws Exception {
        XSSFCell cell=row.getCell(i);
        int type=cell.getCellType().getCode();
        switch ( type){
            case 3:
                return "";
            case 4:
                return  String.valueOf(cell.getBooleanCellValue()).trim();
            case 5:
                return "";
            case 2:
                 return cell.getCellFormula().trim();
            case 0:
                 cell.setCellType(CellType.STRING);
                 return cell.getRichStringCellValue().getString().trim();
            case 1:
                 return cell.getRichStringCellValue().getString().trim();
        }
         return "";
    }
	
	/**
	 * 获取单值首选项
	 * @param session
	 * @param preferenceName
	 * @return
	 */
	public static String getRCPTCPreference(TCSession session, String preferenceName){
		TCPreferenceService preferenceService = session.getPreferenceService();
		return preferenceService.getString(preferenceService.TC_preference_site, preferenceName);
	}
	
	/**
	 * 调用查询构建器查询
	 * @param session
	 * @param queryName
	 * @param keys
	 * @param values
	 * @return
	 * @throws Exception
	 */
	public static TCComponent[] executeRCPQuery(TCSession session, String queryName, String[] keys, String[] values) throws Exception{
		
		TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
  		TCComponentQuery query = (TCComponentQuery) imancomponentquerytype.find(queryName);
  		if(keys.length != values.length) {
  			throw new Exception("queryAttributies length is not equal queryValues length");
  		}
  		String[] queryAttributeDisplayNames = new String[keys.length];
  		TCQueryClause[] elements = query.describe();
  		for(int i = 0; i <keys.length ; i++){
  			for(TCQueryClause element : elements){
  				if(element.getAttributeName().equals(keys[i])){
  					queryAttributeDisplayNames[i] = element.getUserEntryNameDisplay();
  				}
  			}
  			if(queryAttributeDisplayNames[i] == null ||  queryAttributeDisplayNames[i].equals("")){
  				throw new Exception("queryAttribute\""+keys[i]+"\"未找到对应的显示名称");
  			}
	  }
//	  System.out.println("queryAttributeDisplayNames:"+Arrays.toString(queryAttributeDisplayNames));
//	  System.out.println("queryValues:"+Arrays.toString(values));
	  return query.execute(queryAttributeDisplayNames, values);
	}
	
	/**
	 * 获得指定名称首选项:站点类型
	 * @param prefername
	 * @return
	 */
    public static String[] getSOATCPreferences(TCSession session, String prefername){
    	String[] temps = null;
        PreferenceManagementService preferenmanagementservice = PreferenceManagementService.getService(session.getSoaConnection());
        try{
            preferenmanagementservice.refreshPreferences();
            GetPreferencesResponse getpreferencesRes = preferenmanagementservice.getPreferences(new String[] { prefername }, false);
            PreferenceManagement.CompletePreference[] completePref = getpreferencesRes.response;
            if (completePref.length > 0) {
                PreferenceManagement.CompletePreference onecompletePref = completePref[0];
                PreferenceManagement.PreferenceValue prefvalue = onecompletePref.values;
                temps = prefvalue.values;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return temps;
    }
	
	
	public static DataManagement.CreateResponse createObjects(TCSession session, String folderType, Map<String,String> propMap) {
		DataManagementService dmService = DataManagementService.getService(session.getSoaConnection());
        DataManagement.CreateResponse response = null;
        try{
            CreateIn[] createIns = new CreateIn[1];
            createIns[0] = new CreateIn();
            CreateInput createInput = new CreateInput();
            createInput.boName = folderType;
            createInput.stringProps = propMap;
            createIns[0].data = createInput;
            response = dmService.createObjects(createIns);
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }
	
	/**
     * 添加关系
     * @param primaryObject
     * @param secondaryObject
     */
    public static void addContents(TCSession session, ModelObject primaryObject, ModelObject secondaryObject) {
    	DataManagementService dmService = DataManagementService.getService(session.getSoaConnection());
        String primaryUid = primaryObject.getUid();
        String secondaryUid = secondaryObject.getUid();
        ModelObject obj1 = findObjectByUid(session,primaryUid);
        ModelObject obj2 = findObjectByUid(session,secondaryUid);
        Relationship[] relationships = new Relationship[1];
        relationships[0] = new Relationship();
        relationships[0].primaryObject = obj1;
        relationships[0].secondaryObject = obj2;
        relationships[0].relationType = "contents";
        dmService.createRelations(relationships);
    }
    
    /**
     * 根据UID查数据
     * @param dmService
     * @param uid
     * @return
     */
    public static ModelObject findObjectByUid(TCSession session, String uid) {
    	DataManagementService dmService = DataManagementService.getService(session.getSoaConnection());
        ServiceData sd = dmService.loadObjects( new String[]{uid} );
        return sd.getPlainObject(0);
    }
    

    /**
     * 调用SOA查询
     * @param session
     * @param searchName
     * @param keys
     * @param values
     * @return
     * @throws Exception
     */
    public static SavedQueryResults executeSOAQuery(TCSession session, String searchName, String[] keys, String[] values) throws Exception {
        SavedQueryService queryService = SavedQueryService.getService(session.getSoaConnection());
        GetSavedQueriesResponse savedQueries = queryService.getSavedQueries();
        ModelObject query = null;
		for (int i = 0; i < savedQueries.queries.length; i++) {
            if (savedQueries.queries[i].name.equals(searchName)) {
                query  = savedQueries.queries[i].query;
                break;
            }
        }

        if (query == null) {
        	throw new Exception("系统中未找到" + searchName + " 查询..");
        }

        Map<String,String> entriesMap = new HashMap<>();
        SavedQuery.DescribeSavedQueriesResponse describeSavedQueriesResponse = queryService.describeSavedQueries(new ModelObject[]{query});
        for (SavedQuery.SavedQueryFieldObject field : describeSavedQueriesResponse.fieldLists[0].fields) {
            String attributeName = field.attributeName;
            String entryName = field.entryName;
            entriesMap.put(attributeName,entryName);
        }

        String[] entries = new String[keys.length];
        for (int i = 0; i < keys.length; i++) {
            entries[i] = entriesMap.get(keys[i]);
        }

        SavedQueryInput[] savedQueryInput = new SavedQueryInput[1];
        savedQueryInput[0] = new SavedQueryInput();
        savedQueryInput[0].query = query;
        savedQueryInput[0].entries = entries;
        savedQueryInput[0].values = values;
        ExecuteSavedQueriesResponse savedQueryResult = queryService.executeSavedQueries(savedQueryInput);
        SavedQueryResults found = savedQueryResult.arrayOfResults[0];
        return found;
    }
    
    /**
     * 创建项目
     * @param projectId
     * @param projectName
     * @param projectDescription
     * @param teamAadmin
     * @param teamUser
     * @throws Exception 
     */
    public static ModelObject createTCProject(TCSession session, String projectId, String projectName, String projectDescription,
    		ModelObject projectAdmin, ModelObject adminGroupMember, 
    		ModelObject user, ModelObject userGroupMember, 
    		ModelObject[] teamRosterUser, ModelObject[] teamRosterGroupMember) throws Exception {

        ProjectLevelSecurityService plss = ProjectLevelSecurityService.getService(session.getSoaConnection());
        ProjectInformation2[] projectInfos = new ProjectInformation2[1];
        projectInfos[0] = new ProjectInformation2();
        projectInfos[0].projectId = projectId;
        projectInfos[0].projectName = projectName;
        projectInfos[0].projectDescription = projectDescription;
        projectInfos[0].useProgramContext=false;
        projectInfos[0].visible=true;
        projectInfos[0].active=true;
        
        List<TeamMemberInfo> teamMemberInfoList = new ArrayList<TeamMemberInfo>();
        
        TeamMemberInfo teamMemberInfo1 = new TeamMemberInfo();
        teamMemberInfo1.teamMember = projectAdmin;
        teamMemberInfo1.teamMemberType = 2;
        teamMemberInfoList.add(teamMemberInfo1);
        
        TeamMemberInfo teamMemberInfo2 = new TeamMemberInfo();
        teamMemberInfo2.teamMember = adminGroupMember;
        teamMemberInfo2.teamMemberType = 0;
        teamMemberInfoList.add(teamMemberInfo2);
        
        TeamMemberInfo teamMemberInfo3 = new TeamMemberInfo();
        teamMemberInfo3.teamMember = user;
        teamMemberInfo3.teamMemberType = 2;
        teamMemberInfoList.add(teamMemberInfo1);
        
        TeamMemberInfo teamMemberInfo4 = new TeamMemberInfo();
        teamMemberInfo4.teamMember = userGroupMember;
        teamMemberInfo4.teamMemberType = 0;
        teamMemberInfoList.add(teamMemberInfo2);
        
        if(teamRosterUser != null) {
        	for (int i = 0; i < teamRosterUser.length; i++) {
        		TeamMemberInfo teamMemberInfo = new TeamMemberInfo();
        		teamMemberInfo.teamMember = teamRosterUser[i];
        		teamMemberInfo.teamMemberType = 1;
        		teamMemberInfoList.add(teamMemberInfo);
			}
        	for (int i = 0; i < teamRosterGroupMember.length; i++) {
        		TeamMemberInfo teamMemberInfo = new TeamMemberInfo();
        		teamMemberInfo.teamMember = teamRosterGroupMember[i];
        		teamMemberInfo.teamMemberType = 0;
        		teamMemberInfoList.add(teamMemberInfo);
        	}
        }
        
        TeamMemberInfo[] TeamMemberInfos = new TeamMemberInfo[teamMemberInfoList.size()];
        TeamMemberInfos = teamMemberInfoList.toArray(TeamMemberInfos);
        projectInfos[0].teamMembers = TeamMemberInfos;
        ProjectOpsResponse response = plss.createProjects2(projectInfos);
        ServiceData serviceData = response.serviceData;
        if(serviceData.sizeOfPartialErrors() > 0){
        	throw new Exception(response.serviceData.getPartialError(0).getErrorValues()[0].getMessage());
        }
        return  serviceData.getCreatedObject(0);
    }
    
    public static void modifyProjects(TCSession session, ModelObject sourceProject,
    		ModelObject teamAdmin, ModelObject adminGroupMember, 
    		ModelObject[] teamPrivileged, ModelObject[] privilegedGroupMember, 
    		ModelObject[] teamMember, ModelObject[] memberGroupMember, boolean isActive) throws Exception {
    	
    	TCComponentProject project = (TCComponentProject) sourceProject;
    	String projectId = project.getProperty("project_id");
    	String projectName = project.getProperty("project_name");
    	
        ProjectLevelSecurityService plss = ProjectLevelSecurityService.getService(session.getSoaConnection());
        
        ModifyProjectsInfo2[] modifyProjectsInfos = new ModifyProjectsInfo2[1];
        modifyProjectsInfos[0] = new ModifyProjectsInfo2();
        modifyProjectsInfos[0].sourceProject = sourceProject;
       
        List<TeamMemberInfo> teamMemberInfoList = new ArrayList<TeamMemberInfo>();
        
        TeamMemberInfo teamMemberInfo1 = new TeamMemberInfo();
        teamMemberInfo1.teamMember = teamAdmin;
        teamMemberInfo1.teamMemberType = 2;
        teamMemberInfoList.add(teamMemberInfo1);
        
        TeamMemberInfo teamMemberInfo2 = new TeamMemberInfo();
        teamMemberInfo2.teamMember = adminGroupMember;
        teamMemberInfo2.teamMemberType = 0;
        teamMemberInfoList.add(teamMemberInfo2);
        
        for (int i = 0; i < teamPrivileged.length; i++) {
        	TeamMemberInfo teamMemberInfo3 = new TeamMemberInfo();
        	teamMemberInfo3.teamMember = teamPrivileged[i];
        	teamMemberInfo3.teamMemberType = 2;
            teamMemberInfoList.add(teamMemberInfo3);
		}
        for (int i = 0; i < privilegedGroupMember.length; i++) {
        	TeamMemberInfo teamMemberInfo4 = new TeamMemberInfo();
        	teamMemberInfo4.teamMember = privilegedGroupMember[i];
        	teamMemberInfo4.teamMemberType = 0;
            teamMemberInfoList.add(teamMemberInfo4);
		}
        for (int i = 0; i < teamMember.length; i++) {
        	TeamMemberInfo teamMemberInfo5 = new TeamMemberInfo();
        	teamMemberInfo5.teamMember = teamMember[i];
        	teamMemberInfo5.teamMemberType = 1;
            teamMemberInfoList.add(teamMemberInfo5);
		}
        for (int i = 0; i < memberGroupMember.length; i++) {
        	TeamMemberInfo teamMemberInfo6 = new TeamMemberInfo();
        	teamMemberInfo6.teamMember = memberGroupMember[i];
        	teamMemberInfo6.teamMemberType = 0;
            teamMemberInfoList.add(teamMemberInfo6);
		}
       
        TeamMemberInfo[] TeamMemberInfos = new TeamMemberInfo[teamMemberInfoList.size()];
        TeamMemberInfos = teamMemberInfoList.toArray(TeamMemberInfos);
        
        ProjectInformation2 projectInfo = new ProjectInformation2();
        projectInfo.active = isActive;
        projectInfo.projectId = projectId;
        projectInfo.projectName = projectName;
        projectInfo.teamMembers = TeamMemberInfos;
        
        modifyProjectsInfos[0].projectInfo = projectInfo;
        
        setBypass(session);
        ProjectOpsResponse response = plss.modifyProjects2(modifyProjectsInfos);
        closeBypass(session);
        ServiceData serviceData = response.serviceData;
        if(serviceData.sizeOfPartialErrors() > 0){
        	throw new Exception(response.serviceData.getPartialError(0).getErrorValues()[0].getMessage());
        }
    }
    
    /**
     * 获取TC用户
     * @param session
     * @param userName
     * @return
     * @throws Exception
     */
    public static ModelObject getUser(TCSession session,String userName) throws Exception {
    	SavedQueryResults executeSOAQuery = executeSOAQuery(session,Constants.WEB_FIND_USER,new String[]{Constants.USER_ID}, new String[]{userName});
    	if(executeSOAQuery.objects.length == 0) {
    		throw new Exception("未找到" + userName + "管理员！");
		}
    	return executeSOAQuery.objects[0];
    }
    
    /**
     *获取用户GroupMember
     * @param session
     * @param user
     * @param userName
     * @return
     * @throws Exception
     */
    public static ModelObject getUserGroupMember(TCSession session, ModelObject user) throws Exception {
    	TCComponentUser tcComponentUser = (TCComponentUser) user;
    	String userName = tcComponentUser.getUserName();
    	String userId = tcComponentUser.getUserId();
    	TCComponentGroup[] groups = tcComponentUser.getGroups();
    	
    	String strGroups = groups[0].toDisplayString();
    	if("apadmin".equals(userName) || "admin".equals(userName)) {
    		for (int i = 0; i < groups.length; i++) {
        		String displayString = groups[i].toDisplayString();
        		if(displayString.contains("dba")) {
        			strGroups = displayString;
        		}
    		}
    	}
    	
        SavedQueryResults executeSOAQuery = executeSOAQuery(session, Constants.EINT_GROUP_MEMBERS,
        		new String[]{Constants.USER_USER_ID,Constants.GROUP_GROUP_NAME}, new String[]{userId,strGroups});
        if(executeSOAQuery.objects.length == 0) {
    		throw new Exception("未找到" + tcComponentUser.getUserName() + "管理员的UserGroupMember信息！");
		}
        return executeSOAQuery.objects[0];
    }
    
    /**
     * ָ指派项目
     * @param folder
     * @param project
     * @throws Exception 
     */
    public static void assignedProject(TCSession session, ModelObject folder, ModelObject project) throws Exception{
        AssignedOrRemovedObjects assignedOrRemovedObjects = new AssignedOrRemovedObjects();
        assignedOrRemovedObjects.objectToAssign = new ModelObject[] {folder};
        //assignedOrRemovedObjects.objectToRemove = null;
        assignedOrRemovedObjects.projects = new ModelObject[] {project};

        AssignedOrRemovedObjects[] aassignedorremovedobjects = new AssignedOrRemovedObjects[1];
        aassignedorremovedobjects[0] = assignedOrRemovedObjects;

        ProjectLevelSecurityService projectLevelSecurityService = ProjectLevelSecurityService.getService(session.getSoaConnection());
        ServiceData serviceData = projectLevelSecurityService.assignOrRemoveObjects(aassignedorremovedobjects);

        if(serviceData.sizeOfPartialErrors() > 0){
        	throw new Exception(serviceData.getPartialError(0).toString());
        }
    }
    
    /**
     * 获取单个属性
     * @param object
     * @param propName
     */
    public static void getProperty(TCSession session, ModelObject object, String propName){
    	DataManagementService dmService = DataManagementService.getService(session.getSoaConnection());
        ModelObject[] objects = { object };
        String[] atts = { propName };
        dmService.getProperties(objects, atts);
    }
    
    /**
	 * 将ItemRev发送到结构管理器
	 * @param session
	 * @param itemRev
	 * @return
	 */
	public static TCComponentBOMLine sendPSE(TCSession session, TCComponentItemRevision itemRev){
		TCComponentBOMLine topLine = null;
		try {
			TCComponentRevisionRuleType ruleType = (TCComponentRevisionRuleType) session.getTypeComponent(ITypeName.RevisionRule);
			TCComponentRevisionRule defaultRule = ruleType.getDefaultRule();
			TCComponentBOMWindowType windowType = (TCComponentBOMWindowType) session.getTypeComponent(ITypeName.BOMWindow);
			TCComponentBOMWindow bomWindow = windowType.create(defaultRule);
			topLine = bomWindow.setWindowTopLine(itemRev.getItem(), itemRev, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return topLine;
	}
	
	public static List<FolderInfo> getBOMChildren(TCComponentBOMLine topBOMLine) throws TCException {
		List<FolderInfo> folderInfos = new ArrayList<FolderInfo>();
		AIFComponentContext[] BOMChildren = topBOMLine.getChildren();
		for (int i = 0; i < BOMChildren.length; i++) {
			TCComponentBOMLine BOMline = (TCComponentBOMLine) BOMChildren[i].getComponent();
			FolderInfo folderInfo = new FolderInfo();
			folderInfo.setName(BOMline.getProperty("bl_item_object_name"));
			folderInfo.setDesc(BOMline.getProperty("bl_item_object_desc"));
			folderInfos.add(folderInfo);
		}
		return folderInfos;
	}
	
	/**
     * 创建部门文件夹
     * @param projectFolder
     * @param department
     * @return
     * @throws Exception
     */
    public static ModelObject createDepartFolder(TCSession session, ModelObject projectFolder, String department) throws Exception{
        Map<String,String> propMap = new HashMap<>();
        propMap.put("object_name",department);
        CreateResponse response = Utils.createObjects(session, Constants.FUNCTION, propMap);
        ServiceData serviceData = response.serviceData;
		if(serviceData.sizeOfPartialErrors() > 0) {
			throw new Exception(serviceData.getPartialError(0).toString());
		}
		ModelObject departmentFolder = response.output[0].objects[0];
        Utils.addContents(session, projectFolder,departmentFolder);
        return departmentFolder;
    }
    
    /**
     * 获取DT模板信息
     * @param customerName
     * @param projectPhaseList
     * @return
     */
    public static Map<String,List<String>> getDTTemplatesInfo(TCSession session, String customerName,List<String> projectPhaseList){
        Map<String,List<String>> departPhaseMap = null;
        String[] projectTemplates = Utils.getSOATCPreferences(session, Constants.DTSA_PROJECT_FOLDER_TEMPLATE1);
        List<String> projectTemplateList = Arrays.asList(projectTemplates);
        projectTemplateList = projectTemplateList.stream().filter(
                p -> p.split("_")[0].equals(customerName)).collect(Collectors.toList());
        for (int i = 0; i < projectTemplateList.size(); i++) {
            String projectTemplate = projectTemplateList.get(i);
            String[] customerDepartPhase = projectTemplate.split("\\|");
            String depart = customerDepartPhase[0].split("_")[1];
            String templatePhase = customerDepartPhase[1];
            List<String> templatePhaseInfos = containsPhase(templatePhase, projectPhaseList);
            if(departPhaseMap == null){
                departPhaseMap = new HashMap<>();
            }
            departPhaseMap.put(depart,templatePhaseInfos);
        }
        return departPhaseMap;
    }
    
    /**
     * 筛选包含的阶段模板
     * @param templatePhase
     * @param projectPhaseList
     * @return
     */
    private static List<String> containsPhase(String templatePhase,List<String> projectPhaseList){
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
     * 创建阶段、资料文件夹
     * @param projectPhaseInfos
     * @param departFolder
     * @throws Exception
     */
    public static void createPhaseArchiveFolder(TCSession session, List<String> projectPhaseInfos, ModelObject departFolder) throws Exception{
		String folderType = Constants.PHASE;
        for (int i = 0; i < projectPhaseInfos.size(); i++) {
            String projectPhaseInfo = projectPhaseInfos.get(i);
            SavedQueryResults queryResults = Utils.executeSOAQuery(session, Constants.ITEM_NAME_OR_ID, new String[]{Constants.ITEM_ID}, new String[]{projectPhaseInfo});
            if(queryResults.objects.length == 0){
            	throw new Exception("创建阶段文件夹时未找到【" + projectPhaseInfo +"】模板，程序结束...\n");
            }
            TCComponentItem item = (TCComponentItem) queryResults.objects[0];
            TCComponentBOMLine topBOMLine = Utils.sendPSE(session, item.getLatestItemRevision());
            Map<String,String> propMap = new HashMap<>();
            String name = topBOMLine.getProperty("bl_rev_object_name");
            String desc = topBOMLine.getProperty("bl_rev_object_desc");
            propMap.put("object_name",name);
            if(!"".equals(desc)){
                folderType = Constants.ARCHIVE;
            }
            CreateResponse response = Utils.createObjects(session, folderType, propMap);
            ServiceData serviceData = response.serviceData;
    		if(serviceData.sizeOfPartialErrors() > 0) {
    			throw new Exception(serviceData.getPartialError(0).toString());
    		}
    		ModelObject phaseFolder = response.output[0].objects[0];
    		Utils.addContents(session, departFolder,phaseFolder);
            
            List<FolderInfo> folderInfoList = Utils.getBOMChildren(topBOMLine);
            if(folderInfoList.size() > 0) {
            	for (int j = 0; j < folderInfoList.size(); j++) {
            		FolderInfo folderInfo = folderInfoList.get(j);
                	Map<String,String> propMap1 = new HashMap<>();
                	propMap1.put("object_name",folderInfo.getName());
                	CreateResponse response1 = Utils.createObjects(session, Constants.ARCHIVE, propMap1);
                    ServiceData serviceData1 = response1.serviceData;
            		if(serviceData1.sizeOfPartialErrors() > 0) {
            			throw new Exception(serviceData1.getPartialError(0).toString());
            		}
            		ModelObject archiveFolder = response1.output[0].objects[0];
            		Utils.addContents(session, phaseFolder,archiveFolder);
				}
            }
        }
    }
    
    /**
     * 获取PRT、MNT模板信息
     * @param BU
     * @return
     */
    public static Map<String,List<String>> getPMTemplatesInfo(TCSession session, String BU){
        String[] projectTemplates = Utils.getSOATCPreferences(session, Constants.IPBD_PROJECT_FOLDER_TEMPLATE);
        Map<String,List<String>> templatesInfoMap = null;
        for (int i = 0; i < projectTemplates.length; i++) {
            String projectTemplate = projectTemplates[i];
            String[] templateInfo = projectTemplate.split("\\|");
            String template0 = templateInfo[0];
            String[] buOrDepart = template0.split("_");
            if(buOrDepart[0].equals(BU)){
                String s = buOrDepart[1];
                String template1 = templateInfo[1];
                String[] split = template1.split(",");
                List<String> templateIds = new ArrayList<>();
                for (int j = 0; j < split.length; j++) {
                    String s1 = split[j];
                    String templateId = s1.split(":")[1];
                    templateIds.add(templateId);
                }
                if(templatesInfoMap == null){
                    templatesInfoMap = new HashMap<>();
                }
                templatesInfoMap.put(s,templateIds);
            }
        }
        return templatesInfoMap;
    }
    
    /**
     * 开旁路
     * 
     * @param session
     * @throws Exception
     */
    public static void setBypass(TCSession session)
    {
        try
        {
            TCUserService userService = session.getUserService();
            userService.call("set_bypass", new String[] { "" });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     *关闭旁路
     * 
     * @param session
     * @throws Exception
     */
    public static void closeBypass(TCSession session)
    {
        try
        {
            TCUserService userService = session.getUserService();
            userService.call("close_bypass", new String[] { "" });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static File getTeamRosterTemplate(TCSession session) throws Exception{
		String TMLTemplats = getRCPTCPreference(session, Constants.PROJECT_TML_TEMPLATE);//ͨ通过首选项获取模板
		TCComponentDataset dataset = (TCComponentDataset) session.stringToComponent(TMLTemplats);
		TCComponentTcFile[] files = dataset.getTcFiles();
		if (files == null || files.length == 0) {
			throw new Exception("数据集中未找到文件，请联系管理员！");
		}
		TCComponentTcFile tcfile = files[0];
		return tcfile.getFile(null);
	}
    
    public static String getCustomerName(TCSession session, String projectSpasId) throws Exception{
    	TCComponent[] queryResults = Utils.executeRCPQuery(session, Constants.FIND_CUSTOMER_FOLDER, 
    			new String[] {"D9_ProjectSeriesFolder:contents.D9_PlatformFoundFolder:contents.d9_SPAS_ID"}, new String[] {projectSpasId});
    	if(queryResults.length == 0) {
    		return "";
    	}
    	String name = queryResults[0].getProperty(Constants.OBJECT_NAME);
    	return name;
	}
    
    public static String getBUName(TCSession session, String projectSpasId) throws Exception{
    	TCComponent[] queryResults = Utils.executeRCPQuery(session, Constants.FIND_SERIES_FOLDER, 
    			new String[] {"D9_PlatformFoundFolder:contents.d9_SPAS_ID"}, new String[] {projectSpasId});
    	if(queryResults.length == 0) {
    		return "";
    	}
    	String bu = queryResults[0].getProperty(Constants.OBJECT_DESC);
    	return bu;
	}
    
    public static List<String> getTeamRoster(File teamRosterTemplate, String customerName, String bu) throws Exception{
		XSSFWorkbook wb = Utils.getWorkbook(teamRosterTemplate);
        XSSFSheet sheet = null;
        if("DT".equalsIgnoreCase(bu)){
            sheet = getSheet(wb, "DT");
        }else if("PRT".equalsIgnoreCase(bu)){
            sheet = getSheet(wb, "PRT");
        }else if("MNT".equalsIgnoreCase(bu)){
            sheet = getSheet(wb, "MNT");
        }else{
            throw new Exception("读取Excel模板失败");
        }

        HashMap<String, List<String>> tmls = new HashMap<>();
        String tmp = "";
        //解析Excel 按客户分组
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            String dept = getCellValue(row, 2);
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
    
    public static ModelObject queryProject(TCSession session, String spasId) {
    	ModelObject project = null;
    	try {
			SavedQueryResults queryResults = executeSOAQuery(session, Constants.FIND_PROJECT, new String[] {Constants.PROJECT_ID}, new String[] {spasId});
			if(queryResults.numOfObjects == 0) {
				return null;
			}
			project = queryResults.objects[0];
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return project;
    }
    
    public static ModelObject[] getTeamRosterUser(TCSession session, List<String> teamRoster) throws Exception{
    	ModelObject[] users = null;
        String userStrs="";
        for(int i=0;i<teamRoster.size();i++){
            String p=  teamRoster.get(i);
            userStrs+= p+";";
        }
        if(userStrs.endsWith(";")){
            userStrs= userStrs.substring(0,userStrs.length()-1);
        }
        SavedQueryResults queryResults = Utils.executeSOAQuery(session, Constants.WEB_FIND_USER,new String[]{Constants.USER_ID},new String[]{userStrs});
        if(queryResults.objects.length == 0){
        	return users;
        }
        users = queryResults.objects;
        return users;
    }
    
    public static ModelObject[] getTeamRosterGroupMember(TCSession session, ModelObject[] teamRosterUsers) throws Exception{
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
	
}
