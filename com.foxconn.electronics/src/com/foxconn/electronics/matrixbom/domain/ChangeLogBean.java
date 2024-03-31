package com.foxconn.electronics.matrixbom.domain;

public class ChangeLogBean {
	public String changeDate;
	public String changeVer;
	public String changeLog;
	public String changeECR;
	public String changeReqDateUser;
	
	public String getChangeDate() {
		return changeDate==null?"":changeDate;
	}
	public void setChangeDate(String changeDate) {
		this.changeDate = changeDate;
	}
	public String getChangeVer() {
		return changeVer==null?"":changeVer;
	}
	public void setChangeVer(String changeVer) {
		this.changeVer = changeVer;
	}
	public String getChangeLog() {
		return changeLog==null?"":changeLog;
	}
	public void setChangeLog(String changeLog) {
		this.changeLog = changeLog;
	}
	public String getChangeECR() {
		return changeECR==null?"":changeECR;
	}
	public void setChangeECR(String changeECR) {
		this.changeECR = changeECR;
	}
	public String getChangeReqDateUser() {
		return changeReqDateUser==null?"":changeReqDateUser;
	}
	public void setChangeReqDateUser(String changeReqDateUser) {
		this.changeReqDateUser = changeReqDateUser;
	}
	
}
