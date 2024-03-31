package com.foxconn.electronics.dcnreport.dcncostimpact.domain;

import java.util.Date;

import com.foxconn.electronics.dcnreport.dcncostimpact.util.TCPropertes;
import com.foxconn.electronics.util.SerialCloneable;


public class DCNReportBean extends SerialCloneable {
	
	private String item;
	private String bu;
	
	@TCPropertes(tcProperty = "item_id", tcType = "DCNRevision")
	private String dcnNo = "";
	
	@TCPropertes(tcProperty = "item_id", tcType = "DesignRevision")
	private String modelNo = "";
	
	private String projectId = "";
	private String projectName = "";
	
	@TCPropertes(tcProperty = "d9_HHPN", tcType = "DesignRevision")
	private String hhpn = "";
	
	@TCPropertes(tcProperty = "d9_CustomerPN", tcType = "DesignRevision")
	private String customerPN = "";
	
	@TCPropertes(tcProperty = "object_name", tcType = "DesignRevision")
	private String partName = "";
	
	@TCPropertes(tcProperty = "object_desc", tcType = "DesignRevision")
	private String description = "";	
	
	private String reason;	
	private String costImpact;	
	private String status;
	private String customerType;
	private String productLine;
	
	private String modelNoPrefix;
	
	@TCPropertes(tcProperty = "creation_date", tcType = "DCNRevision")
	private String createDate;

	
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getBu() {
		return bu;
	}

	public void setBu(String bu) {
		this.bu = bu;
	}
	

	public String getDcnNo() {
		return dcnNo;
	}

	public void setDcnNo(String dcnNo) {
		this.dcnNo = dcnNo;
	}

	public String getModelNo() {
		return modelNo;
	}

	public void setModelNo(String modelNo) {
		this.modelNo = modelNo;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getHhpn() {
		return hhpn;
	}

	public void setHhpn(String hhpn) {
		this.hhpn = hhpn;
	}

	public String getCustomerPN() {
		return customerPN;
	}

	public void setCustomerPN(String customerPN) {
		this.customerPN = customerPN;
	}

	public String getPartName() {
		return partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	

	public String getCostImpact() {
		return costImpact;
	}

	public void setCostImpact(String costImpact) {
		this.costImpact = costImpact;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getProductLine() {
		return productLine;
	}

	public void setProductLine(String productLine) {
		this.productLine = productLine;
	}

	
	public String getModelNoPrefix() {
		return modelNoPrefix;
	}

	public void setModelNoPrefix(String modelNoPrefix) {
		this.modelNoPrefix = modelNoPrefix;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	
	
}
