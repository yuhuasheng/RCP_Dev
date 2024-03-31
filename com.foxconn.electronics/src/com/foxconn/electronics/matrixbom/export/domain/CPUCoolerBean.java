package com.foxconn.electronics.matrixbom.export.domain;

import com.foxconn.electronics.util.TCPropName;

public class CPUCoolerBean implements DTThermalBean {
	
	@TCPropName("bl_sequence_no")
	private Integer findNum;
	
	@TCPropName(cell = 0)
	private Integer item;
	
	@TCPropName(value = "bl_occ_d9_ProgramName", cell = 1)
	private String programName;
	
	@TCPropName(value = "d9_CodeName", cell = 2)
	private String codeName;
	
	@TCPropName(value = "d9_CoolerVendor", cell = 3)
	private String coolerVendor;
	
	@TCPropName(value = "d9_CustomerPN", cell = 4)
	private String customerPN;
	
	@TCPropName(value = "d9_FRUPN", cell = 5)
	private String frupn;
	
	@TCPropName(value = "item_id", cell = 6)
	private String hhpn;
	
	@TCPropName(value = "d9_CoolerVendorPN", cell = 7)
	private String coolerVendorPN;
	
	@TCPropName(value = "d9_CoolerFanVendor", cell = 8)
	private String coolerFanVendor;
	
	@TCPropName(value = "d9_CoolerFanModelNo", cell = 9)
	private String coolerFanModelNo;	
	
	private String type;
	
	private boolean sub = false;
	
	
	public Integer getFindNum() {
		return findNum;
	}

	public void setFindNum(Integer findNum) {
		this.findNum = findNum;
	}

	public Integer getItem() {
		return item;
	}

	public void setItem(Integer item) {
		this.item = item;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getCoolerVendor() {
		return coolerVendor;
	}

	public void setCoolerVendor(String coolerVendor) {
		this.coolerVendor = coolerVendor;
	}

	public String getCustomerPN() {
		return customerPN;
	}

	public void setCustomerPN(String customerPN) {
		this.customerPN = customerPN;
	}

	public String getFrupn() {
		return frupn;
	}

	public void setFrupn(String frupn) {
		this.frupn = frupn;
	}

	public String getHhpn() {
		return hhpn;
	}

	public void setHhpn(String hhpn) {
		this.hhpn = hhpn;
	}

	public String getCoolerVendorPN() {
		return coolerVendorPN;
	}

	public void setCoolerVendorPN(String coolerVendorPN) {
		this.coolerVendorPN = coolerVendorPN;
	}

	public String getCoolerFanVendor() {
		return coolerFanVendor;
	}

	public void setCoolerFanVendor(String coolerFanVendor) {
		this.coolerFanVendor = coolerFanVendor;
	}

	public String getCoolerFanModelNo() {
		return coolerFanModelNo;
	}

	public void setCoolerFanModelNo(String coolerFanModelNo) {
		this.coolerFanModelNo = coolerFanModelNo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isSub() {
		return sub;
	}

	public void setSub(boolean sub) {
		this.sub = sub;
	}
	
	
}
