package com.foxconn.mechanism.hhpnmaterialapply.export.domain.dt;

import com.foxconn.mechanism.hhpnmaterialapply.export.util.TCPropertes;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class DTSMDataModel implements DTDataModel {

	@TCPropertes(cell = 0)
	private String item;

	@TCPropertes(tcProperty = "object_type")
	private String objectType;
	
	@TCPropertes(tcProperty = "bl_item_item_id", tcType = "BOMLine", cell = 1)
	private String ItemID;
	
	@TCPropertes(tcProperty = "d9_HHPN", cell = 2)
	private String hhpn;

	@TCPropertes(tcProperty = "")
	private String hhpnRev;

	@TCPropertes(tcProperty = "d9_CustomerPN", cell = 3)
	private String customPN;

	@TCPropertes(tcProperty = "bl_item_object_name", tcType = "BOMLine", cell = 4)
	private String modelName;

	@TCPropertes(tcProperty = "d9_TempPN", cell = 5)
	private String bupn;

	@TCPropertes(tcProperty = "d9_ChineseDescription", cell = 6)
	private String descriptionZh;

	@TCPropertes(tcProperty = "d9_EnglishDescription", cell = 7)
	private String descriptionEn;

	@TCPropertes(tcProperty = "d9_Material", cell = 9)
	private String material;	

	@TCPropertes(tcProperty = "bl_quantity", tcType = "BOMLine", cell = 10)
	private String usage = "1";

	@TCPropertes(tcProperty = "d9_PartWeight", cell = 11)
	private String partWeight;

	@TCPropertes(tcProperty = "d9_Finish", cell = 12)
	private String finish;

	@TCPropertes(tcProperty = "d9_Technology", cell = 13)
	private String technology;

	@TCPropertes(tcProperty = "d9_Remarks", cell = 14)
	private String remarks;	
	
	private TCComponentItemRevision itemRevision;	
	
	
	private String partType;
	
	private String optionalType;
	
	private TCComponentBOMLine bomLine;
	
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}
	
	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getHhpn() {
		return hhpn;
	}

	public void setHhpn(String hhpn) {
		this.hhpn = hhpn;
	}

	public String getHhpnRev() {
		return hhpnRev;
	}

	public void setHhpnRev(String hhpnRev) {
		this.hhpnRev = hhpnRev;
	}

	public String getCustomPN() {
		return customPN;
	}

	public void setCustomPN(String customPN) {
		this.customPN = customPN;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getBupn() {
		return bupn;
	}

	public void setBupn(String bupn) {
		this.bupn = bupn;
	}

	public String getDescriptionZh() {
		return descriptionZh;
	}

	public void setDescriptionZh(String descriptionZh) {
		this.descriptionZh = descriptionZh;
	}

	public String getDescriptionEn() {
		return descriptionEn;
	}

	public void setDescriptionEn(String descriptionEn) {
		this.descriptionEn = descriptionEn;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}	

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getPartWeight() {
		return partWeight;
	}

	public void setPartWeight(String partWeight) {
		this.partWeight = partWeight;
	}	

	public String getFinish() {
		return finish;
	}

	public void setFinish(String finish) {
		this.finish = finish;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}	

	public TCComponentItemRevision getItemRevision() {
		return itemRevision;
	}

	public void setItemRevision(TCComponentItemRevision itemRevision) {
		this.itemRevision = itemRevision;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {		
		return super.clone();
	}

	public String getPartType() {
		return partType;
	}

	public void setPartType(String partType) {
		this.partType = partType;
	}

	public String getOptionalType() {
		return optionalType;
	}

	public void setOptionalType(String optionalType) {
		this.optionalType = optionalType;
	}

	public TCComponentBOMLine getBomLine() {
		return bomLine;
	}

	public void setBomLine(TCComponentBOMLine bomLine) {
		this.bomLine = bomLine;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getItemID() {
		return ItemID;
	}

	public void setItemID(String itemID) {
		ItemID = itemID;
	}
	
}
