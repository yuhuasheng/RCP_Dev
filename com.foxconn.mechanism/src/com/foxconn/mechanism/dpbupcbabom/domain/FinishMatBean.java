package com.foxconn.mechanism.dpbupcbabom.domain;

import com.foxconn.mechanism.hhpnmaterialapply.export.util.TCPropertes;

public class FinishMatBean implements BOMMakeApplyModel {
	
	@TCPropertes(tcProperty = "d9_Sequence", cell = 0)
	private Integer sequence;
	
	@TCPropertes(tcProperty = "d9_PCBAType", cell = 1)
	private String pcbaType;
	
	@TCPropertes(tcProperty = "d9_CustomerModelName", cell = 2)
	private String customerModelName;
	
	@TCPropertes(tcProperty = "d9_FoxconnModelName", cell = 3)
	private String foxconnModelName;
	
	@TCPropertes(tcProperty = "d9_PanelType", cell = 4)
	private String panelType;
	
	@TCPropertes(tcProperty = "d9_InterfaceType", cell = 5)
	private String interfaceType;
	
	@TCPropertes(tcProperty = "d9_IsSpeaker", cell = 6)
	private String isSpeaker;
	
	@TCPropertes(tcProperty = "d9_IsFW", cell = 7)
	private String isFW;
	
	@TCPropertes(tcProperty = "d9_FWName", cell = 8)
	private String fwName;
	
	@TCPropertes(tcProperty = "d9_FWType", cell = 9)
	private String fwType;
	
	@TCPropertes(tcProperty = "d9_DriverType", cell = 10)
	private String driverType;
	
	@TCPropertes(tcProperty = "d9_RefMaterialPN", cell = 11)
	private String refMaterialPN;
	
	@TCPropertes(tcProperty = "d9_Remark", cell = 12)
	private String remark;	

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getPcbaType() {
		return pcbaType;
	}

	public void setPcbaType(String pcbaType) {
		this.pcbaType = pcbaType;
	}

	public String getCustomerModelName() {
		return customerModelName;
	}

	public void setCustomerModelName(String customerModelName) {
		this.customerModelName = customerModelName;
	}

	public String getFoxconnModelName() {
		return foxconnModelName;
	}

	public void setFoxconnModelName(String foxconnModelName) {
		this.foxconnModelName = foxconnModelName;
	}

	public String getPanelType() {
		return panelType;
	}

	public void setPanelType(String panelType) {
		this.panelType = panelType;
	}

	public String getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
	}

	public String getIsSpeaker() {
		return isSpeaker;
	}

	public void setIsSpeaker(String isSpeaker) {
		this.isSpeaker = isSpeaker;
	}

	public String getIsFW() {
		return isFW;
	}

	public void setIsFW(String isFW) {
		this.isFW = isFW;
	}

	public String getFwName() {
		return fwName;
	}

	public void setFwName(String fwName) {
		this.fwName = fwName;
	}

	public String getFwType() {
		return fwType;
	}

	public void setFwType(String fwType) {
		this.fwType = fwType;
	}

	public String getDriverType() {
		return driverType;
	}

	public void setDriverType(String driverType) {
		this.driverType = driverType;
	}

	public String getRefMaterialPN() {
		return refMaterialPN;
	}

	public void setRefMaterialPN(String refMaterialPN) {
		this.refMaterialPN = refMaterialPN;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}	
	
}
