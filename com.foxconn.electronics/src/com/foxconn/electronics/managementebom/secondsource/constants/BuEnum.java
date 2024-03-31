package com.foxconn.electronics.managementebom.secondsource.constants;

public enum BuEnum {
	MNT("monitor", "mnt"), PRT("printer", "prt");
	
	private String groupName;
	
	private String buName;

	private BuEnum(String groupName, String buName) {
		this.groupName = groupName;
		this.buName = buName;
	}
	
	public String groupName() {
		return groupName;
	}
	
	public String buName() {
		return buName;
	}
}
