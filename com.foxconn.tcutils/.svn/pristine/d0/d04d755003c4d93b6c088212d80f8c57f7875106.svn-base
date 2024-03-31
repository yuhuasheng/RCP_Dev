package com.foxconn.tcutils.constant;


/**
 * TC查询枚举类
 * @author MW00333
 *
 */
public enum TCSearchEnum {

	D9_Find_Schedule_Task("__D9_Find_Schedule_Task",new String[]{"startDate","endDate"}),
    WEB_FIND_USER("__WEB_find_user", new String[] {"User ID"}),
    D9_Find_ProductNode("__D9_Find_ProductNode", new String[] {"Project ID"}),
    D9_Find_Running_Project("__D9_Find_Project_Running", new String[] {"Project ID"}),
    D9_FIND_PROJECT_FOLDER("__D9_Find_Project_Folder", new String[] {"d9_SPAS_ID", "object_name"}),
    D9_FIND_PROJECT("__D9_Find_Project", new String[] {"project_id"}),
    ITEM_NAME_OR_ID("Item_Name_or_ID", new String[]{"item_id"}),
    D9_FIND_ACTUALUSER("__D9_Find_ActualUser", new String[]{"ID"});
	
	private final String queryName; // 查询名称

	private final String[] queryParams; // 查询参数名
    private TCSearchEnum(String queryName, String[] queryParams) {
    	this.queryName = queryName;
        this.queryParams = queryParams;
    }

    public String queryName() {
    	return queryName;
	}
    
    public String[] queryParams() {
        return queryParams;
    }
    
    
}
