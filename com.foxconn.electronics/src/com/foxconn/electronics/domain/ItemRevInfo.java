package com.foxconn.electronics.domain;

import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class ItemRevInfo {

	private String id;
	private String name;
	private String desc;
	private TCComponentItemRevision itemRev;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public TCComponentItemRevision getItemRev() {
		return itemRev;
	}
	public void setItemRev(TCComponentItemRevision itemRev) {
		this.itemRev = itemRev;
	}
	
	@Override
	public String toString() {
		return "ItemRevInfo [id=" + id + ", name=" + name + ", desc=" + desc + ", itemRev=" + itemRev + "]";
	}
	
}
