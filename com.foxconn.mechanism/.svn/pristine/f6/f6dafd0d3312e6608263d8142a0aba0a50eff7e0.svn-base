package com.foxconn.mechanism.exportcreomodel.constant;

public enum DatasetEnum {

	PDF("pdf", "PDF_Reference", ".pdf"), DXF("dxf", "DXF", ".dxf"), DRW("prodrw", "DrwFile", ".drw"), PROASM("proasm", "", ".asm"), 
	PROPRT("proprt", "", ".prt"), PROFRM("profrm", "FrmFile", ".frm");
	
	private final String type;	//类型
	private final String refName; //命名的引用
	private final String fileExtensions; //文件后缀
	
    private DatasetEnum(String type, String refName, String fileExtensions) {
    	this.type = type;       
        this.refName = refName;
        this.fileExtensions = fileExtensions;
    }

    public String type() {
    	return type;
	}
    
    public String refName() {
    	return refName;
    }
    
    public String fileExtensions() {
    	return fileExtensions;
    }
}
