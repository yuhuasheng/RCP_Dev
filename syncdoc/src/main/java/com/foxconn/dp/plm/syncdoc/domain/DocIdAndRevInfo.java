package com.foxconn.dp.plm.syncdoc.domain;

public class DocIdAndRevInfo {

	private String docNum;
	private String revNum;
	
	public String getDocNum() {
		return docNum;
	}
	public void setDocNum(String docNum) {
		this.docNum = docNum;
	}
	public String getRevNum() {
		return revNum;
	}
	public void setRevNum(String revNum) {
		this.revNum = revNum;
	}
	
	@Override
	public String toString() {
		return "DocIdAndRevInfo [docNum=" + docNum + ", revNum=" + revNum + "]";
	}
	
}
