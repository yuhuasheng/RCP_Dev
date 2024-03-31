package com.foxconn.dp.plm.syncdoc.domain;

public class ItemRevInfo1 {

	private Integer docId;
	private String docNum;
	private String versionNum;
	
	public Integer getDocId() {
		return docId;
	}
	public void setDocId(Integer docId) {
		this.docId = docId;
	}
	public String getDocNum() {
		return docNum;
	}
	public void setDocNum(String docNum) {
		this.docNum = docNum;
	}
	public String getVersionNum() {
		return versionNum;
	}
	public void setVersionNum(String versionNum) {
		this.versionNum = versionNum;
	}
	
	@Override
	public String toString() {
		return "ItemRevInfo1 [docId=" + docId + ", docNum=" + docNum + ", versionNum=" + versionNum + "]";
	}
	
}
