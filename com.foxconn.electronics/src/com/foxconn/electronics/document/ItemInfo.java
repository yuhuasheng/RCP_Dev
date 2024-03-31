package com.foxconn.electronics.document;

public class ItemInfo {

	private String puid;
	private String id;
	private String type;
	private String name;
	
	public String getPuid() {
		return puid;
	}
	public void setPuid(String puid) {
		this.puid = puid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "ItemInfo [puid=" + puid + ", id=" + id + ", type=" + type + ", name=" + name + "]";
	}
	
}
