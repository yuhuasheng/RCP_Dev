package com.foxconn.sdebom.commands.mebom;

import java.util.ArrayList;

public class BOMInfo{
	private String bl_sequence;
	private String itemid;
	private String description;
	private String supplier;
	private String usage;
	private String un;
	private String location;
	private String revision;
	private ArrayList<BOMInfo> childrens;
	
	
	
	
	public String getBl_sequence() {
		return bl_sequence;
	}
	public void setBl_sequence(String bl_sequence) {
		this.bl_sequence = bl_sequence;
	}
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public String getUsage() {
		return usage;
	}
	public void setUsage(String usage) {
		this.usage = usage;
	}
	public String getUn() {
		return un;
	}
	public void setUn(String un) {
		this.un = un;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	
	public ArrayList<BOMInfo> getChildrens() {
		return childrens;
	}
	public void setChildrens(ArrayList<BOMInfo> childrens) {
		this.childrens = childrens;
	}
	
	public BOMInfo(String bl_sequence, String itemid, String description, String supplier, String usage, String un, String location, String revision) {
		this.bl_sequence = bl_sequence;
		this.itemid = itemid;
		this.description = description;
		this.supplier = supplier;
		this.usage = usage;
		this.un = un;
		this.location = location;
		this.revision = revision;
	}
	
	public BOMInfo(String[] properties) {
		this.bl_sequence = properties[0];
		this.itemid = properties[1];
		this.description = properties[2];
		this.supplier = properties[3];
		this.usage = properties[4];
		this.un = properties[5];
		this.revision = properties[6];
	}
	
	
	@Override
	public String toString() {
		return "BOMInfo [bl_sequence=" + bl_sequence + ", itemid=" + itemid + ", description=" + description + ", supplier="
				+ supplier + ", usage=" + usage + ", un=" + un + ", location=" + location + ", revision=" + revision
				+ "]";
	}
	
}
