package com.foxconn.electronics.domain;

public class CoCaInfo {

	private Integer id;
	private String hhPN;
	private String designPN;
	private String description;
	private String supplier;
	private Integer groupId;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getHhPN() {
		return hhPN;
	}
	public void setHhPN(String hhPN) {
		this.hhPN = hhPN;
	}
	public String getDesignPN() {
		return designPN;
	}
	public void setDesignPN(String designPN) {
		this.designPN = designPN;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	
	@Override
	public String toString() {
		return "CoCaInfo [id=" + id + ", hhPN=" + hhPN + ", designPN=" + designPN + ", description=" + description
				+ ", supplier=" + supplier + ", groupId=" + groupId + "]";
	}
	
}
