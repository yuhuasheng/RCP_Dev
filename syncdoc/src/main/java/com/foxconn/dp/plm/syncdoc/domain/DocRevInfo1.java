package com.foxconn.dp.plm.syncdoc.domain;

public class DocRevInfo1 {
	
	private String docId;
	private String docRev;
	private Integer issueState;
	
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public String getDocRev() {
		return docRev;
	}
	public void setDocRev(String docRev) {
		this.docRev = docRev;
	}
	public Integer getIssueState() {
		return issueState;
	}
	public void setIssueState(Integer issueState) {
		this.issueState = issueState;
	}
	@Override
	public String toString() {
		return "DocRevInfo1 [docId=" + docId + ", docRev=" + docRev + ", issueState=" + issueState + "]";
	}
	
}
