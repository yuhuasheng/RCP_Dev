package com.foxconn.electronics.dbomconvertebom;

import java.util.List;

import com.foxconn.electronics.util.TCPropName;
import com.google.gson.annotations.Expose;

public class DBOMInfo {

	@Expose(serialize = false, deserialize = false)
	
	//private DBOMInfo parent;
	//private TCComponentBOMLine bomLine;
	
	private String parentitemRevUid;
	
	@TCPropName("bl_item_item_id")
	private String itemid;
	
	@TCPropName("bl_indented_title")
	private String indented_title;
	
	@TCPropName("bl_quantity")
	private String quantity;

	private List<HHPNInfo> hhpnInfo;
	
	private Boolean checkStates = true; // 判断是否为选中状态
	
	//private String sourceSystem = SourceConstant.TC; // 物料是否已经存在TC中,默认设置为存在TC中
	
	private String itemRevUid;

	private String bomLineUid;
	
	private List<DBOMInfo> childs; // 子对象集合
	
	public DBOMInfo() {

	}

	public String getParentitemRevUid() {
		return parentitemRevUid;
	}

	public void setParentitemRevUid(String parentitemRevUid) {
		this.parentitemRevUid = parentitemRevUid;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getIndented_title() {
		return indented_title;
	}

	public void setIndented_title(String indented_title) {
		this.indented_title = indented_title;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public List<HHPNInfo> getHhpnInfo() {
		return hhpnInfo;
	}

	public void setHhpnInfo(List<HHPNInfo> hhpnInfo) {
		this.hhpnInfo = hhpnInfo;
	}

	public Boolean getCheckStates() {
		return checkStates;
	}

	public void setCheckStates(Boolean checkStates) {
		this.checkStates = checkStates;
	}

	public String getItemRevUid() {
		return itemRevUid;
	}

	public void setItemRevUid(String itemRevUid) {
		this.itemRevUid = itemRevUid;
	}

	public String getBomLineUid() {
		return bomLineUid;
	}

	public void setBomLineUid(String bomLineUid) {
		this.bomLineUid = bomLineUid;
	}

	public List<DBOMInfo> getChilds() {
		return childs;
	}

	public void setChilds(List<DBOMInfo> childs) {
		this.childs = childs;
	}

	@Override
	public String toString() {
		return "DBOMInfo [parentitemRevUid=" + parentitemRevUid + ", itemid=" + itemid + ", indented_title="
				+ indented_title + ", quantity=" + quantity + ", hhpnInfo=" + hhpnInfo + ", checkStates=" + checkStates
				+ ", itemRevUid=" + itemRevUid + ", bomLineUid=" + bomLineUid + ", childs=" + childs + "]";
	}
	
}
