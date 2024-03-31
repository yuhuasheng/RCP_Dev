package com.foxconn.decompile.util;

public class SPASUser {

	private String from;//来源
	private String deptName;
	private String sectionName; //组织
	private String workId; // 工号
	private String name; // 姓名
	private String notes; // 邮箱
	
	
	public String getDeptName() {
		return deptName==null?"":deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getFrom() {
		return from == null?"Team Roster":from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getSectionName() {
		return sectionName==null?"TC":sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getWorkId() {
		return workId==null?"":workId;
	}
	public void setWorkId(String workId) {
		this.workId = workId;
	}
	public String getName() {
		return name==null?"":name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNotes() {
		return notes==null?"":notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	@Override
	public String toString() {
		if(notes == null) {
			notes= "";
		}
		return "["+(sectionName==null?"TC":sectionName)+"] : "+name + "(" + workId + ")" + "/" + notes;
		//return "SPASUser [workId=" + workId + ", name=" + name + ", notes=" + notes + "]";
	}
	public SPASUser(String from, String sectionName,String workId, String name, String notes) {
		super();
		this.from = from;
		this.sectionName = sectionName;
		this.workId = workId;
		this.name = name;
		this.notes = notes;
	}
	
}
