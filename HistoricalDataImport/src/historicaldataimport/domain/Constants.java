package historicaldataimport.domain;

import historicaldataimport.utils.PropertitesUtil;

public class Constants {

	public static final String TEMPLATE_PUID = "BGvJjw5WZ5OtvC";//模板PUID
	//查询
    public static final String FIND_PROJECT_FOLDER = "__D9_Find_Project_Folder";//查询文件夹
    public static final String OBJECT_NAME = "object_name";//查询条件1
    public static final String SPAS_ID = "d9_SPAS_ID";//查询条件2
    public static final String WEB_FIND_USER = "__WEB_find_user";//查询User
    public static final String USER_ID= "user_id";//查询条件
    public static final String EINT_GROUP_MEMBERS = "__EINT_group_members";//查询GroupMembers
    public static final String USER_USER_ID = "user.user_id";//查询条件1
    public static final String GROUP_GROUP_NAME = "Group:group.name";//查询条件2
    public static final String ITEM_NAME_OR_ID = "Item_Name_or_ID";//查询Item
    public static final String ITEM_ID = "item_id";//查询条件
    public static final String FIND_PROJECT = "__D9_Find_Project";//查询项目
    public static final String PROJECT_ID = "project_id";//查询条件
    public static final String FIND_CUSTOMER_FOLDER = "__D9_Find_Customer_Folder";
    public static final String FIND_SERIES_FOLDER = "__D9_Find_Series_Folder";
    public static final String OBJECT_DESC = "object_desc";

    
    //首选项
    public static final String PROJECT_KNOWLEDGE_FOLDER_UID = "D9_Project_Knowledge_Folder_UID";//专案知识库UID
    public static final String PROJECT_TML_TEMPLATE = "D9_Project_TML_Template";//专案指派人员模板
    public static final String WORKAREA_FOLDER_TEMPLATE = "D9_WorkArea_Folder_Template";//MNT、PRT、DT工作区文件夹模板
    public static final String DTSA_PROJECT_FOLDER_TEMPLATE1 = "D9_DTSA_Project_Folder_Template";//DT部门、阶段文件夹模板
    public static final String IPBD_PROJECT_FOLDER_TEMPLATE = "D9_IPBD_Project_Folder_Template";//MNT、PRT部门、阶段文件夹模板
    
    //文件夹类型
    public static final String CUSTOMER = "D9_CustomerFolder";//客户文件夹类型
    public static final String SERIES = "D9_ProjectSeriesFolder";//系列文件夹类型
    public static final String PLATFORMFOUND = "D9_PlatformFoundFolder";//专案文件夹类型
    public static final String WORKAREA = "D9_WorkAreaFolder";//工作区文件夹类型
    public static final String FUNCTION = "D9_FunctionFolder";//部门文件夹类型
    public static final String PHASE = "D9_PhaseFolder";//阶段文件夹类型
    public static final String ARCHIVE = "D9_ArchiveFolder";//资料文件夹类型
    
    //数据库信息
    public static final String JDBC_DRIVER = PropertitesUtil.props.getProperty("jdbc.driver");
    public static final String JDBC_URL_PRD = PropertitesUtil.props.getProperty("jdbc.url_prd");
    public static final String JDBC_URL_POC = PropertitesUtil.props.getProperty("jdbc.url_poc");
    public static final String JDBC_USER = PropertitesUtil.props.getProperty("jdbc.user");
    public static final String JDBC_PASSWORD = PropertitesUtil.props.getProperty("jdbc.password");
    
    public static final String SERIES_TYPE = "s";//系列
    public static final String PROJECT_TYPE = "p";//专案
}
