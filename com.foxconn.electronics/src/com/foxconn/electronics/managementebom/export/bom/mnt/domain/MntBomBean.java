package com.foxconn.electronics.managementebom.export.bom.mnt.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.foxconn.electronics.managementebom.export.bom.mnt.MntBomExportAction;
import com.foxconn.electronics.util.TCPropName;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;

public class MntBomBean {
		
	@TCPropName(cell = 0)
	private String plant;
	
	@TCPropName(value = "bl_item_item_id", cell = 1)
	private String materialNumber;
	
	@TCPropName(cell = 3)
	private Integer bomUsage = 2;
	
	@TCPropName(cell = 4)
	private Integer alternativeBOM = 1;
	
	@TCPropName(cell = 5)
	private Integer basicUnit = 100;
	
	@TCPropName(value = "bl_sequence_no", cell = 6)
	private Integer findNum;	
	
	@TCPropName(value = "d9_ReferenceDimension", cell = 7)
	private String referenceDimension;
	
	@TCPropName(cell = 8)
	private String itemCategory;
	
	@TCPropName(value = "bl_item_item_id", cell = 9)
	private String components;
	
	private List<MntBomBean> childs; // 子对象集合
	
	private List<MntBomBean> substitutesList; // 替代料集合
	
	@TCPropName(value = "bl_quantity", cell = 10)
	private String qty;
	
	@TCPropName(value = "d9_Un", cell = 11)
	private String unit;
	
	@TCPropName(value = "bl_occ_d9_AltGroup", cell = 15)
	private String altItemGroup;
	
	@TCPropName(cell = 16)
	private Integer priority;
	
	@TCPropName(cell = 17)
	private Integer strategy;
	
	@TCPropName(cell = 18)
	private Integer usageProb;
	
	@TCPropName(value = "bl_occ_d9_Location", cell = 22) 
	private String location;

	private String uid;
	
	public MntBomBean() {
		
	}
	
	public MntBomBean(TCComponentBOMLine bomLine, boolean isTopLine) {			
		try {
			if (isTopLine) {
				childs = Collections.synchronizedList(new ArrayList<MntBomBean>());
			}	
			MntBomExportAction.tcPropMapping(this, bomLine, isTopLine);
		} catch (IllegalArgumentException | IllegalAccessException | TCException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getPlant() {
		return plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}

	public String getMaterialNumber() {
		return materialNumber;
	}

	public void setMaterialNumber(String materialNumber) {
		this.materialNumber = materialNumber;
	}

	public Integer getBomUsage() {
		return bomUsage;
	}

	public void setBomUsage(Integer bomUsage) {
		this.bomUsage = bomUsage;
	}

	public Integer getAlternativeBOM() {
		return alternativeBOM;
	}

	public void setAlternativeBOM(Integer alternativeBOM) {
		this.alternativeBOM = alternativeBOM;
	}

	public Integer getBasicUnit() {
		return basicUnit;
	}

	public void setBasicUnit(Integer basicUnit) {
		this.basicUnit = basicUnit;
	}
	
	public Integer getFindNum() {
		return findNum;
	}

	public void setFindNum(Integer findNum) {
		this.findNum = findNum;
	}	
	
	
	public String getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}

	public String getComponents() {
		return components;
	}

	public void setComponents(String components) {
		this.components = components;
	}

	public List<MntBomBean> getChilds() {
		return childs;
	}

	public void addChilds(MntBomBean child) {
		this.childs.add(child);
	}

	public List<MntBomBean> getSubstitutesList() {
		return substitutesList;
	}

	public void setSubstitutesList(List<MntBomBean> substitutesList) {
		this.substitutesList = substitutesList;
	}	

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getAltItemGroup() {
		return altItemGroup;
	}

	public void setAltItemGroup(String altItemGroup) {
		this.altItemGroup = altItemGroup;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getStrategy() {
		return strategy;
	}

	public void setStrategy(Integer strategy) {
		this.strategy = strategy;
	}

	
	public Integer getUsageProb() {
		return usageProb;
	}

	public void setUsageProb(Integer usageProb) {
		this.usageProb = usageProb;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}	
	
	public String getReferenceDimension() {
		return referenceDimension;
	}

	public void setReferenceDimension(String referenceDimension) {
		this.referenceDimension = referenceDimension;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MntBomBean) {
			MntBomBean other = (MntBomBean) obj;
			if (!TCUtil.isNull(this.uid) && !TCUtil.isNull(other.getUid())) {
				return this.uid.equals(other.getUid());
			}
		}
		return super.equals(obj);
	}
	
	
}
