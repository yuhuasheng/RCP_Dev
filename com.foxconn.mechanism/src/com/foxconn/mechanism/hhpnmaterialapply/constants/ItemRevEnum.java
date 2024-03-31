package com.foxconn.mechanism.hhpnmaterialapply.constants;

public enum ItemRevEnum {
	
	DesignRev("DesignRevision"), CommonPartRev("");	
	
	private String type;
	
	private ItemRevEnum(String type) {
    	this.type = type;       
    }

    public String type() {
    	return type;
	}    
   
    public void update(String type) {
    	this.type = type;
    }
}
