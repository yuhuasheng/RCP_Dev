package com.foxconn.constants;
/**
* @author 作者 Administrator
* @version 创建时间：2022年1月7日 上午10:08:50
* Description: 数据集枚举类
*/
public enum DatasetEnum {

	PDF("PDF", "PDF_Reference", ""), DXF("DXF", "DXF", ""), DRW("ProDrw", "DrwFile", ".drw"), PROASM("ProAsm", "AsmFile", ".asm"), 
	PROPRT("ProPrt", "PrtFile", ".prt"), PROFRM("ProFrm", "FrmFile", ".frm");
	
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
