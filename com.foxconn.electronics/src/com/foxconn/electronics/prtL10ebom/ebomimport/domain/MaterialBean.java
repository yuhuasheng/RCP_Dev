package com.foxconn.electronics.prtL10ebom.ebomimport.domain;

import com.foxconn.electronics.util.SerialCloneable;
import com.foxconn.tcutils.util.TCPropertes;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class MaterialBean extends SerialCloneable {
	
	private int index;
	
	private String sku;
	
	@TCPropertes(tcProperty = "d9_FamilyPartNumber", tcType = "ItemRevision")
	private String familyPartNumber;	
	
	private String parentNumber;
	
	@TCPropertes(tcProperty = "item_id")
	private String partNumber;
	
	@TCPropertes(tcProperty = "object_name")
	private String objectName;
	
	@TCPropertes(tcProperty = "d9_EnglishDescription")
	private String EnDesc;
	
	@TCPropertes(tcProperty = "d9_ChineseDescription")
	private String chDesc;
	
	@TCPropertes(tcProperty = "d9_Un")
	private String unit = "EA"; // 默认值设置为EA
	
	@TCPropertes(tcProperty = "d9_Module")
	private String subSystem;
	
	@TCPropertes(tcProperty = "d9_CommodityType")
	private String commodityType;
	
	@TCPropertes(tcProperty = "d9_Material")
	private String material;
	
	@TCPropertes(tcProperty = "d9_2DRev")
	private String draw2DRev;
	
	@TCPropertes(tcProperty = "d9_3DRev")
	private String draw3DRev;
	
	@TCPropertes(tcProperty = "d9_SourcingType")
	private String sourcingType;
	
	@TCPropertes(tcProperty = "d9_Remarks")
	private String remark;
	
	@TCPropertes(tcProperty = "d9_ProcurementMethods")
	private String objectType;
	
	@TCPropertes(tcProperty = "uom_tag")
	private String uom_tag = "Other";
	
	private TCComponentItem parentItem = null;
	
	private TCComponentItem partItem = null;
	
	private TCComponentItemRevision addSolutionItemRev = null;
	
	private TCComponentItemRevision addProblemItemRev = null;
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getFamilyPartNumber() {
		return familyPartNumber;
	}

	public void setFamilyPartNumber(String familyPartNumber) {
		this.familyPartNumber = familyPartNumber;
	}

	public String getParentNumber() {
		return parentNumber;
	}

	public void setParentNumber(String parentNumber) {
		this.parentNumber = parentNumber;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	
	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getEnDesc() {
		return EnDesc;
	}

	public void setEnDesc(String enDesc) {
		EnDesc = enDesc;
	}

	public String getChDesc() {
		return chDesc;
	}

	public void setChDesc(String chDesc) {
		this.chDesc = chDesc;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getSubSystem() {
		return subSystem;
	}

	public void setSubSystem(String subSystem) {
		this.subSystem = subSystem;
	}

	public String getCommodityType() {
		return commodityType;
	}

	public void setCommodityType(String commodityType) {
		this.commodityType = commodityType;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getDraw2DRev() {
		return draw2DRev;
	}

	public void setDraw2DRev(String draw2dRev) {
		draw2DRev = draw2dRev;
	}

	public String getDraw3DRev() {
		return draw3DRev;
	}

	public void setDraw3DRev(String draw3dRev) {
		draw3DRev = draw3dRev;
	}

	public String getSourcingType() {
		return sourcingType;
	}

	public void setSourcingType(String sourcingType) {
		this.sourcingType = sourcingType;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}		


	public TCComponentItem getParentItem() {
		return parentItem;
	}

	public void setParentItem(TCComponentItem parentItem) {
		this.parentItem = parentItem;
	}

	public String getUom_tag() {
		return uom_tag;
	}

	public void setUom_tag(String uom_tag) {
		this.uom_tag = uom_tag;
	}

	public TCComponentItem getPartItem() {
		return partItem;
	}

	public void setPartItem(TCComponentItem partItem) {
		this.partItem = partItem;
	}

	public TCComponentItemRevision getAddSolutionItemRev() {
		return addSolutionItemRev;
	}

	public void setAddSolutionItemRev(TCComponentItemRevision addSolutionItemRev) {
		this.addSolutionItemRev = addSolutionItemRev;
	}

	public TCComponentItemRevision getAddProblemItemRev() {
		return addProblemItemRev;
	}

	public void setAddProblemItemRev(TCComponentItemRevision addProblemItemRev) {
		this.addProblemItemRev = addProblemItemRev;
	}

	@Override
	public String toString() {
		return "MaterialBean [index=" + index + ", sku=" + sku + ", familyPartNumber=" + familyPartNumber
				+ ", parentNumber=" + parentNumber + ", partNumber=" + partNumber + ", objectName=" + objectName
				+ ", EnDesc=" + EnDesc + ", chDesc=" + chDesc + ", unit=" + unit + ", subSystem=" + subSystem
				+ ", commodityType=" + commodityType + ", material=" + material + ", draw2DRev=" + draw2DRev
				+ ", draw3DRev=" + draw3DRev + ", sourcingType=" + sourcingType + ", remark=" + remark + ", objectType="
				+ objectType + ", uom_tag=" + uom_tag + "]";
	}
	
	
	
}
