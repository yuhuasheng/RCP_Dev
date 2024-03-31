package com.foxconn.electronics.issuemanagement.domain;

public class UpdatesRes {
	
	private String state;
	private String response;
	private String assignedTo;
	private String createBy;
	private String createDate;
	private Long createTime;
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public String getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}
	
	
}
