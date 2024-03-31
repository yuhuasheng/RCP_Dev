package com.foxconn.sdebom.dtl5ebom.pojo;

import java.util.ArrayList;
import java.util.List;

import com.foxconn.sdebom.pojo.ItemRevPojo;

public class DtL5ItemRevPojo extends ItemRevPojo{

	private String englishDescription;
	private String chineseDescription;
	private String customerPN;
	private String manufacturerID;
	private String manufacturerPN;
	private String assemblyCode;
	private List<DtL5ItemRevPojo> child = new ArrayList<>();
	
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
	public String getAssemblyCode() {
		return assemblyCode;
	}
	public void setAssemblyCode(String assemblyCode) {
		this.assemblyCode = assemblyCode;
	}
	public List<DtL5ItemRevPojo> getChild() {
		return child;
	}
	public void setChild(List<DtL5ItemRevPojo> child) {
		this.child = child;
	}
	@Override
	public String toString() {
		return "DtL5ItemRevPojo [englishDescription=" + englishDescription + ", chineseDescription="
				+ chineseDescription + ", customerPN=" + customerPN + ", manufacturerID=" + manufacturerID
				+ ", manufacturerPN=" + manufacturerPN + ", assemblyCode=" + assemblyCode + ", child=" + child + "]";
	}
	
}
