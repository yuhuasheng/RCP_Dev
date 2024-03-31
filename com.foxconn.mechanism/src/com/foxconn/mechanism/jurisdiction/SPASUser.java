package com.foxconn.mechanism.jurisdiction;

public class SPASUser {

	private String workId;
	private String name;
	private String notes;
	
	public String getWorkId() {
		return workId;
	}
	public void setWorkId(String workId) {
		this.workId = workId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	@Override
	public String toString() {
		if(notes == null) {
			notes= "";
		}
		return name + "(" + workId + ")" + "/" + notes;
		//return "SPASUser [workId=" + workId + ", name=" + name + ", notes=" + notes + "]";
	}
	
}
