package com.foxconn.electronics.L10Ebom.domain;

import com.foxconn.tcutils.util.TCPropertes;

public class BOMApplyToTableBean implements MatToTableBean {

	private String finishType = "Monitor";
	
	@TCPropertes(tcProperty = "item_id")
	private String finishPN;
	
	@TCPropertes(tcProperty = "d9_EnglishDescription")
	private String desc;

	public String getFinishType() {
		return finishType;
	}

	public void setFinishType(String finishType) {
		this.finishType = finishType;
	}

	public String getFinishPN() {
		return finishPN;
	}

	public void setFinishPN(String finishPN) {
		this.finishPN = finishPN;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String getSemiPN() {
		return null;
	}

	@Override
	public void setSemiType(String semiType) {
	}

	@Override
	public String getSemiType() {
		return null;
	}

	@Override
	public void setSemiPN(String semiPN) {
	}

	@Override
	public String getFinalPN() {
		return null;
	}

	@Override
	public void setFinalPN(String finalPN) {
	}
	
	@Override
	public String getPkgPN() {
		return null;
	}

	@Override
	public void setPkgPN(String pkgPN) {
	}

	@Override
	public String getAssyPN() {
		return null;
	}

	@Override
	public void setAssyPN(String assyPN) {
	}

	@Override
	public String getShippingArea() {
		return null;
	}

	@Override
	public void setShippingArea(String shippingArea) {
	}

	@Override
	public String getWireType() {
		return null;
	}

	@Override
	public void setWireType(String wireType) {
	}
	
}
