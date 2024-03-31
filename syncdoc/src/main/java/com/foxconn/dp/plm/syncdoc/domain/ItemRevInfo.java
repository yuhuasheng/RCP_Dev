package com.foxconn.dp.plm.syncdoc.domain;

public class ItemRevInfo {
	
	private String puid;
	private String rev;
	

	public String getPuid() {
		return puid;
	}
	public void setPuid(String puid) {
		this.puid = puid;
	}
	public String getRev() {
		return rev;
	}
	public void setRev(String rev) {
		this.rev = rev;
	}
	
	@Override
	public String toString() {
		return "ItemRevInfo [puid=" + puid + ", rev=" + rev + "]";
	}
	
}
