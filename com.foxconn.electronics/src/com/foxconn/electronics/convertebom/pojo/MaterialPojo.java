/**
 * 
 */
package com.foxconn.electronics.convertebom.pojo;

import com.foxconn.electronics.util.TCPropName;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

/**
 * @author Leo
 *
 */
public class MaterialPojo {
	@TCPropName(value = "bl_item_item_id")
	private String materialNum;
	@TCPropName(value = "bl_rev_item_revision_id")
	private String rev;
	@TCPropName(value = "bl_rev_d9_EnglishDescription")
	private String description_en;
	@TCPropName(value = "bl_rev_d9_ChineseDescription")
	private String description_zh;
	
	@TCPropName(value = "bl_Part Revision_d9_ManufacturerID")
	private String mfg;
	@TCPropName(value = "bl_Part Revision_d9_ManufacturerPN")
	private String mfgPn;
	
	@TCPropName(value = "bl_Part Revision_d9_MaterialGroup")
	private String materialGroup;
	@TCPropName(value = "bl_Part Revision_d9_MaterialType")
	private String materialType;
	@TCPropName(value = "bl_Part Revision_d9_Un")
	private String baseUnit;
	private String issueStorage;
	@TCPropName(value = "bl_Part Revision_d9_ProcurementMethords")
	private String procurementType;
	
	@TCPropName(value = "bl_rev_release_status_list")
	private String status;
	
	private TCComponentItemRevision itemRevision;

	public String getMaterialNum() {
		return materialNum;
	}

	public void setMaterialNum(String materialNum) {
		this.materialNum = materialNum;
	}

	public String getRev() {
		return rev;
	}

	public void setRev(String rev) {
		this.rev = rev;
	}

	public String getDescription_en() {
		return description_en;
	}

	public void setDescription_en(String description_en) {
		this.description_en = description_en;
	}

	public String getDescription_zh() {
		return description_zh;
	}

	public void setDescription_zh(String description_zh) {
		this.description_zh = description_zh;
	}

	public String getMfg() {
		return mfg;
	}

	public void setMfg(String mfg) {
		this.mfg = mfg;
	}

	public String getMfgPn() {
		return mfgPn;
	}

	public void setMfgPn(String mfgPn) {
		this.mfgPn = mfgPn;
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

	public String getBaseUnit() {
		return baseUnit;
	}

	public void setBaseUnit(String baseUnit) {
		this.baseUnit = baseUnit;
	}

	public String getIssueStorage() {
		return issueStorage;
	}

	public void setIssueStorage(String issueStorage) {
		this.issueStorage = issueStorage;
	}

	public String getProcurementType() {
		return procurementType;
	}

	public void setProcurementType(String procurementType) {
		this.procurementType = procurementType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public TCComponentItemRevision getItemRevision() {
		return itemRevision;
	}

	public void setItemRevision(TCComponentItemRevision itemRevision) {
		this.itemRevision = itemRevision;
	}

	@Override
	public String toString() {
		return "MaterialPojo [materialNum=" + materialNum + ", rev=" + rev + ", description_en=" + description_en
				+ ", description_zh=" + description_zh + ", mfg=" + mfg + ", mfgPn=" + mfgPn + ", materialGroup="
				+ materialGroup + ", materialType=" + materialType + ", baseUnit=" + baseUnit + ", issueStorage="
				+ issueStorage + ", procurementType=" + procurementType + ", status=" + status + ", itemRevision="
				+ itemRevision + "]";
	}
	
}
