package com.foxconn.sdebom.pojo;

public class BomLinePojo {

	private String itemId;
	private String itemName;
	private String itemRevId;
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemRevId() {
		return itemRevId;
	}
	public void setItemRevId(String itemRevId) {
		this.itemRevId = itemRevId;
	}
	
	@Override
	public String toString() {
		return "BomLinePojo [itemId=" + itemId + ", itemName=" + itemName + ", itemRevId=" + itemRevId + "]";
	}
	
}
