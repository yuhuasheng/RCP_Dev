package com.foxconn.electronics.L10Ebom.domain;

import com.foxconn.tcutils.util.TCPropertes;

public class HalfMatToTableBean implements MatToTableBean {
	
	private String semiType;
	
	@TCPropertes(tcProperty = "item_id")
	private String semiPN;
	
	@TCPropertes(tcProperty = "d9_EnglishDescription")
	private String desc;

	public String getSemiType() {
		return semiType;
	}

	public void setSemiType(String semiType) {
		this.semiType = semiType;
	}

	public String getSemiPN() {
		return semiPN;
	}

	public void setSemiPN(String semiPN) {
		this.semiPN = semiPN;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String getFinishType() {
		return null;
	}

	@Override
	public void setFinishType(String finishType) {
	}

	@Override
	public String getFinishPN() {
		return null;
	}

	@Override
	public void setFinishPN(String finishPN) {
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

	@Override
	public String getFinalPN() {
		return null;
	}

	@Override
	public void setFinalPN(String finalPN) {
	}

	
	
	
}
