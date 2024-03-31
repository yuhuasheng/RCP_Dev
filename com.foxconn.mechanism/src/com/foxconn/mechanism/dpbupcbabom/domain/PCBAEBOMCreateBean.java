package com.foxconn.mechanism.dpbupcbabom.domain;

import com.foxconn.mechanism.hhpnmaterialapply.export.util.TCPropertes;

public class PCBAEBOMCreateBean implements BOMMakeApplyModel {
	
	@TCPropertes(cell = 0)
	private Integer sequence;
	
	@TCPropertes(tcProperty = "d9_PCBAssyClassification_L6", cell = 1)
	private String productionTypeL6;
	
	@TCPropertes(tcProperty = "item_id", cell = 2)
	private String itemId;
	
//	@TCPropertes(tcProperty = "d9_SupplementInfo", cell = 3)
	private String supplementInfo;
	
	@TCPropertes(tcProperty = "cm0AuthoringChangeRevision", cell = 3)
	private String cm0AuthoringChangeRevision;

	public String getProductionTypeL6() {
		return productionTypeL6;
	}

	public void setProductionTypeL6(String productionTypeL6) {
		this.productionTypeL6 = productionTypeL6;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getSupplementInfo() {
		return supplementInfo;
	}

	public void setSupplementInfo(String supplementInfo) {
		this.supplementInfo = supplementInfo;
	}

	public String getCm0AuthoringChangeRevision() {
		return cm0AuthoringChangeRevision;
	}

	public void setCm0AuthoringChangeRevision(String cm0AuthoringChangeRevision) {
		this.cm0AuthoringChangeRevision = cm0AuthoringChangeRevision;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	
}
