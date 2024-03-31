package com.foxconn.tcutils.progress;

public class BooleanFlag {
	private boolean flag = false;

	
	public BooleanFlag() {}

	public BooleanFlag(boolean flag) {		
		this.flag = flag;
	}
	
	public synchronized boolean getFlag() {
		return flag; // 该类被多线程使用，方法需同步
	}
	
	public synchronized void setFlag(boolean flag) {
		this.flag = flag;
	}
}
