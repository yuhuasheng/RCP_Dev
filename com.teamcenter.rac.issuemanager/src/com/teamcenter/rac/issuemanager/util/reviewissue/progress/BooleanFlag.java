package com.teamcenter.rac.issuemanager.util.reviewissue.progress;

public class BooleanFlag {
	private boolean flag = false;

	
	public BooleanFlag() {}

	public BooleanFlag(boolean flag) {		
		this.flag = flag;
	}
	
	public synchronized boolean getFlag() {
		return flag; 
	}
	
	public synchronized void setFlag(boolean flag) {
		this.flag = flag;
	}
}
