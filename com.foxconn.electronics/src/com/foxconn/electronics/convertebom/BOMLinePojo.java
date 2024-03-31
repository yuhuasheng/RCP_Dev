package com.foxconn.electronics.convertebom;

import com.foxconn.electronics.convertebom.pojo.MNTMaterialCategory;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class BOMLinePojo {

	private String itemId;
	private String itemRevId;
	private String itemType;
	private String quantity;
	private String packageType;
	private String insertionType;
	private String location = "";
	private String side;
	private String bom;
	private String desc;
	private TCComponentItemRevision itemRev;
	private String actionMsg = "";
	private Boolean showMsg = true;
	private MNTMaterialCategory materialCategory;
	private TCComponentItemRevision stepfather; //繼父
	private String stepfatherNum = ""; //繼父号
	private Boolean newNIFlag = false;
	
	public BOMLinePojo(){
		
	}
	
	public BOMLinePojo(TCComponentItemRevision itemRev){
		this.itemRev = itemRev;
		this.itemType = this.itemRev.getType();
	}
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemRevId() {
		return itemRevId;
	}
	public void setItemRevId(String itemRevId) {
		this.itemRevId = itemRevId;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getPackageType() {
		return packageType;
	}
	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}
	public String getInsertionType() {
		return insertionType;
	}
	public void setInsertionType(String insertionType) {
		this.insertionType = insertionType;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public String getBom() {
		return bom;
	}
	public void setBom(String bom) {
		this.bom = bom;
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
	
	public String getActionMsg() {
		return actionMsg;
	}

	public void setActionMsg(String actionMsg) {
		this.actionMsg = actionMsg;
	}

	public Boolean getShowMsg() {
		return showMsg;
	}

	public void setShowMsg(Boolean showMsg) {
		this.showMsg = showMsg;
	}

	public TCComponentItemRevision getStepfather() {
		return stepfather;
	}

	public MNTMaterialCategory getMaterialCategory() {
		return materialCategory;
	}

	public void setMaterialCategory(MNTMaterialCategory materialCategory) {
		this.materialCategory = materialCategory;
	}

	public void setStepfather(TCComponentItemRevision stepfather) {
		this.stepfather = stepfather;
	}

	public String getStepfatherNum() {
		return stepfatherNum;
	}

	public void setStepfatherNum(String stepfatherNum) {
		this.stepfatherNum = stepfatherNum;
	}

	public Boolean getNewNIFlag() {
		return newNIFlag;
	}

	public void setNewNIFlag(Boolean newNIFlag) {
		this.newNIFlag = newNIFlag;
	}

	@Override
	public String toString() {
		return "BOMLinePojo [itemId=" + itemId + ", itemRevId=" + itemRevId + ", itemType=" + itemType + ", quantity="
				+ quantity + ", packageType=" + packageType + ", insertionType=" + insertionType + ", location="
				+ location + ", side=" + side + ", bom=" + bom + ", desc=" + desc + ", itemRev=" + itemRev
				+ ", actionMsg=" + actionMsg + ", showMsg=" + showMsg + ", materialCategory=" + materialCategory
				+ ", stepfather=" + stepfather + ", stepfatherNum=" + stepfatherNum + ", newNIFlag=" + newNIFlag + "]";
	}
	
}
