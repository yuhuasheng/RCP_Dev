package com.foxconn.electronics.tclicensereport.domain;

import java.util.Date;

public class DateRecordInfo {
	
	private String recordDate;
	
	private String workingDayMainland;
	
	private String workingDayTaiwan;	
	
	public String getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(String recordDate) {
		this.recordDate = recordDate;
	}

	public String getWorkingDayMainland() {
		return workingDayMainland;
	}

	public void setWorkingDayMainland(String workingDayMainland) {
		this.workingDayMainland = workingDayMainland;
	}

	public String getWorkingDayTaiwan() {
		return workingDayTaiwan;
	}

	public void setWorkingDayTaiwan(String workingDayTaiwan) {
		this.workingDayTaiwan = workingDayTaiwan;
	}
	
	
}
