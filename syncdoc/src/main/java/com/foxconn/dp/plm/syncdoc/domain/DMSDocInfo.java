package com.foxconn.dp.plm.syncdoc.domain;


public class DMSDocInfo {

	private String docName;
	private String docType;
	
	public String getDocName() {
		return docName;
	}
	public void setDocName(String docName) {
		this.docName = docName;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	@Override
	public String toString() {
		return "DMSDocInfo [docName=" + docName + ", docType=" + docType + "]";
	}

}
