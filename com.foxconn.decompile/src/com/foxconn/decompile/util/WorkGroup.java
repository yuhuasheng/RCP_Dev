package com.foxconn.decompile.util;

public class WorkGroup {
	private WorkGroup approveRealityUser;//实际工程师
	private WorkGroup approveTcUser;//TC工程师
	
	private WorkGroup reviewRealityUser;//实际工程师
	private WorkGroup reviewTcUser;//TC工程师
	
	private String dell;
	private String hp;
	private String lenovo;
	private String g10;
	
	private String doAW; //行政组织
	private String codeGroup; //代码逻辑组
	private String tcGroup; //tc组织
	
	private String workId; // 工号
	private String name; // 姓名
	private String notes; // 邮箱
	
	private String from;
	
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public WorkGroup getApproveRealityUser() {
		return approveRealityUser;
	}
	public void setApproveRealityUser(WorkGroup approveRealityUser) {
		this.approveRealityUser = approveRealityUser;
	}
	public WorkGroup getApproveTcUser() {
		return approveTcUser;
	}
	public void setApproveTcUser(WorkGroup approveTcUser) {
		this.approveTcUser = approveTcUser;
	}
	public WorkGroup getReviewRealityUser() {
		return reviewRealityUser;
	}
	public void setReviewRealityUser(WorkGroup reviewRealityUser) {
		this.reviewRealityUser = reviewRealityUser;
	}
	public WorkGroup getReviewTcUser() {
		return reviewTcUser;
	}
	public void setReviewTcUser(WorkGroup reviewTcUser) {
		this.reviewTcUser = reviewTcUser;
	}
	
	public String getDell() {
		return dell;
	}
	public void setDell(String dell) {
		this.dell = dell;
	}
	public String getHp() {
		return hp;
	}
	public void setHp(String hp) {
		this.hp = hp;
	}
	public String getLenovo() {
		return lenovo;
	}
	public void setLenovo(String lenovo) {
		this.lenovo = lenovo;
	}
	public String getG10() {
		return g10;
	}
	public void setG10(String g10) {
		this.g10 = g10;
	}
	public String getDoAW() {
		return doAW;
	}
	public void setDoAW(String doAW) {
		this.doAW = doAW;
	}
	public String getCodeGroup() {
		return codeGroup==null?"":codeGroup;
	}
	public void setCodeGroup(String codeGroup) {
		this.codeGroup = codeGroup;
	}
	public String getTcGroup() {
		return tcGroup==null?"":tcGroup;
	}
	public void setTcGroup(String tcGroup) {
		this.tcGroup = tcGroup;
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
	
	
	public WorkGroup() {}
	public WorkGroup(String dell, String hp, String lenovo,String g10,
			String workId,String name,
			String doAW,String codeGroup,String tcGroup,
			String notes,String from) {
		this.dell = dell;
		this.hp = hp;
		this.lenovo = lenovo;
		this.g10 = g10;
		
		this.workId = workId;
		this.name = name;
		
		this.doAW = doAW;
		this.codeGroup = codeGroup;
		this.tcGroup = tcGroup;
		
		this.notes = notes;
		this.from = from;
	}
	@Override
	public String toString() {
		return "WorkGroup [dell=" + dell + ", hp=" + hp + ", lenovo=" + lenovo + ", g10=" + g10 + ", doAW=" + doAW
				+ ", codeGroup=" + codeGroup + ", tcGroup=" + tcGroup + ", workId=" + workId + ", name=" + name
				+ ", notes=" + notes + "]";
	}
	
}
