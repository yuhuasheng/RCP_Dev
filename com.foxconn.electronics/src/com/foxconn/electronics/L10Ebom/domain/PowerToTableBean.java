package com.foxconn.electronics.L10Ebom.domain;

import com.foxconn.tcutils.util.TCPropertes;

public class PowerToTableBean implements MatToTableBean {

	@TCPropertes(tcProperty = "item_id")
	private String assyPN;

	public String getAssyPN() {
		return assyPN;
	}

	public void setAssyPN(String assyPN) {
		this.assyPN = assyPN;
	}

	@Override
	public String getSemiType() {
		return null;
	}

	@Override
	public void setSemiType(String semiType) {
	}

	@Override
	public String getSemiPN() {
		return null;
	}

	@Override
	public void setSemiPN(String semiPN) {
	}

	@Override
	public String getDesc() {
		return null;
	}

	@Override
	public void setDesc(String desc) {
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
