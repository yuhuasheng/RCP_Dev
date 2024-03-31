package com.foxconn.savedcnchange.domain;

import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.savedcnchange.util.TCPropertes;

public class MaterialBean implements Comparable<MaterialBean> {
	
	private String dcnItemId;
	
	@TCPropertes(tcProperty = "item_id")
	private String itemId;
	
	@TCPropertes(tcProperty = "item_revision_id")
	private String version;
		
	private String uid;
	
	@TCPropertes(tcProperty = "d9_DescriptionSAP")
	private String sapDescription;
	
	@TCPropertes(tcProperty = "d9_EnglishDescription")
	private String enDescription;
	
	@TCPropertes(tcProperty = "d9_ChineseDescription")
	private String chDescription;
	
	@TCPropertes(tcProperty = "d9_Un")
	private String unit;
	
	@TCPropertes(tcProperty = "d9_MaterialGroup")
	private String materialGroup;
	
	@TCPropertes(tcProperty = "d9_MaterialType")
	private String materialType;
	
	@TCPropertes(tcProperty = "d9_ManufacturerPN")
	private String manufacturerPN;
	
	@TCPropertes(tcProperty = "d9_ManufacturerID")
	private String manufacturerID;
	
	@TCPropertes(tcProperty = "d9_ProcurementMethods")
	private String procurementMethods;
	
	private String grossWeight;
	
	private String netWeight;
	
	private String weightUnit;

	@TCPropertes(tcProperty = "d9_SAPRev")
	private String sapRev; // SAP�汾
	
	@TCPropertes(tcProperty = "d9_SupplierZF")
	private String supplierZF;
	
	public String getDcnItemId() {
		return dcnItemId;
	}

	public void setDcnItemId(String dcnItemId) {
		this.dcnItemId = dcnItemId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public String getSapDescription() {
		return sapDescription;
	}

	public void setSapDescription(String sapDescription) {
		this.sapDescription = sapDescription;
	}
	

	public String getEnDescription() {
		return enDescription;
	}

	public void setEnDescription(String enDescription) {
		this.enDescription = enDescription;
	}

	public String getChDescription() {
		return chDescription;
	}

	public void setChDescription(String chDescription) {
		this.chDescription = chDescription;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getMaterialGroup() {
		return materialGroup;
	}

	public void setMaterialGroup(String materialGroup) {
		this.materialGroup = materialGroup;
	}

	public String getMaterialType() {
		return materialType;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}

	public String getManufacturerPN() {
		return manufacturerPN;
	}

	public void setManufacturerPN(String manufacturerPN) {
		this.manufacturerPN = manufacturerPN;
	}

	public String getManufacturerID() {
		return manufacturerID;
	}

	public void setManufacturerID(String manufacturerID) {
		this.manufacturerID = manufacturerID;
	}

	public String getProcurementMethods() {
		return procurementMethods;
	}

	public void setProcurementMethods(String procurementMethods) {
		this.procurementMethods = procurementMethods;
	}

	public String getGrossWeight() {
		return grossWeight;
	}

	public void setGrossWeight(String grossWeight) {
		this.grossWeight = grossWeight;
	}

	public String getNetWeight() {
		return netWeight;
	}

	public void setNetWeight(String netWeight) {
		this.netWeight = netWeight;
	}

	public String getWeightUnit() {
		return weightUnit;
	}

	public void setWeightUnit(String weightUnit) {
		this.weightUnit = weightUnit;
	}
	
	
	public String getSapRev() {
		return sapRev;
	}

	public void setSapRev(String sapRev) {
		this.sapRev = sapRev;
	}

	
	public String getSupplierZF() {
		return supplierZF;
	}

	public void setSupplierZF(String supplierZF) {
		this.supplierZF = supplierZF;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MaterialBean) {
			MaterialBean other = (MaterialBean) obj;
			if (CommonTools.isNotEmpty(this.uid) && !CommonTools.isNotEmpty(other.getUid())) {
				return this.uid.equals(other.getUid());
			}
		}
		return super.equals(obj);
	}

	@Override
	public int compareTo(MaterialBean o) {		
		int i = this.dcnItemId.compareTo(o.getDcnItemId());
		if (i == 0) {
			return this.itemId.compareTo(o.getItemId());
		}
		return i;
	}
}
