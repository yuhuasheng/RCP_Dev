package com.foxconn.electronics.prtL10ebom.ebomimport.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.foxconn.electronics.util.SerialCloneable;
import com.foxconn.tcutils.util.TCPropertes;

public class BOMBean implements Comparable<BOMBean> {
	
	private Integer level;
	
	private String qty;
	
	private MaterialBean selfMaterialBean;
	
	private List<BOMBean> childList;
	
	public BOMBean() {
		super();
		childList = new CopyOnWriteArrayList<BOMBean>();
//		childList = new LinkedList<BOMBean>();
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	
	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public MaterialBean getSelfMaterialBean() {
		return selfMaterialBean;
	}

	public void setSelfMaterialBean(MaterialBean selfMaterialBean) {
		this.selfMaterialBean = selfMaterialBean;
	}

	public List<BOMBean> getChildList() {
		return childList;
	}

	public void setChildList(List<BOMBean> childList) {
		this.childList = childList;
	}

	@Override
	public String toString() {
		return "BOMBean [level=" + level + ", qty=" + qty + ", selfMaterialBean=" + selfMaterialBean + ", childList="
				+ childList + "]";
	}

	@Override
	public int compareTo(BOMBean o) {
		return this.getLevel().compareTo(o.getLevel());
	}
	
}
