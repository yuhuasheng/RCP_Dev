package com.foxconn.tcutils.constant;

public enum DatasetEnum {


	TXT("Text", "IMAN_specification", "Text", ""), D9_EDAPlacement ("D9_Placement", "", "D9_Placement", ".txt"), MSExcel("MSExcel", "", "excel", ".xls"), MSExcelX("MSExcelX", "", "excel", ".xlsx");
	
	private final String type; //类型
	private final String relationType; //引用关系
	private final String refName; //命名的引用 
	private final String fileExtensions; //文件后缀
	
    private DatasetEnum(String type, String relationType, String refName, String fileExtensions) {
    	this.type = type;
        this.relationType = relationType;
        this.refName = refName;
        this.fileExtensions = fileExtensions;
    }

    public String type() {
    	return type;
	}
    
    public String relationType() {
        return relationType;
    }
    
    public String refName() {
    	return refName;
    }
    
    public String fileExtensions() {
    	return fileExtensions;
    }
    
}
