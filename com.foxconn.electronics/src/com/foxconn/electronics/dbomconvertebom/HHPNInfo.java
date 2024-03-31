package com.foxconn.electronics.dbomconvertebom;

public class HHPNInfo {

	private String hhpn;
	private String englishDescription;
    private String chineseDescription;
	private String customer;
	private String customerPN;
	private String manufacturerID;
	private String manufacturerPN;
	private String objectName;
	private String isWrite;
	
	public HHPNInfo() {

	}
	

	public String getIsWrite() {
		return isWrite;
	}

	public void setIsWrite(String isWrite) {
		this.isWrite = isWrite;
	}

	public String getHhpn() {
		return hhpn;
	}

	public void setHhpn(String hhpn) {
		this.hhpn = hhpn;
	}

	public String getEnglishDescription() {
		return englishDescription;
	}

	public void setEnglishDescription(String englishDescription) {
		this.englishDescription = englishDescription;
	}

	public String getChineseDescription() {
		return chineseDescription;
	}

	public void setChineseDescription(String chineseDescription) {
		this.chineseDescription = chineseDescription;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCustomerPN() {
		return customerPN;
	}

	public void setCustomerPN(String customerPN) {
		this.customerPN = customerPN;
	}

	public String getManufacturerID() {
		return manufacturerID;
	}

	public void setManufacturerID(String manufacturerID) {
		this.manufacturerID = manufacturerID;
	}

	public String getManufacturerPN() {
		return manufacturerPN;
	}

	public void setManufacturerPN(String manufacturerPN) {
		this.manufacturerPN = manufacturerPN;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

}
