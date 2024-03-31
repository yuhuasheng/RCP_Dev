package com.foxconn.mechanism.hhpnmaterialapply.domain;

import com.foxconn.mechanism.hhpnmaterialapply.serial.SerialCloneable;

public class PropertiesInfo extends SerialCloneable {
	private static final long serialVersionUID = 1L;
	
	private String Item_ID;	
	private String HHPN;	
	private String customerPN;	
	private String customerID;	
	private String HHRevision;	
	private String HHPNState; // 是否为鸿海料号	
	private String chineseDescription;	
	private String EnglishDescription;	
	private Integer QtyUnits = 1; // 数量默认值设置为1	
	private String material;	
	private String ULClass;	
	private String partWeight;	
	private String color;
	private String painting;	
	private String printing;	
	private String remark;	
	private String surfaceFinished;	
	private String technology;	
	private String ADHESIVE;
	private String customerDrawingNumber;	
	private String Customer3DRev;	
	private String Customer2DRev;	
	private String smPartMaterialDimension;
	private String projectName;	
	private String module;	
	private String referenceDimension;	
	private String runnerWeight;
	private String totalweight;	
	private String manufacturerID;	
	private String manufacturerPN;	
	private String BUPN;
	
	public String getItem_ID() {
		return Item_ID;
	}
	public void setItem_ID(String item_ID) {
		Item_ID = item_ID;
	}
	public String getHHPN() {
		return HHPN;
	}
	public void setHHPN(String hHPN) {
		HHPN = hHPN;
	}
	public String getCustomerPN() {
		return customerPN;
	}
	public void setCustomerPN(String customerPN) {
		this.customerPN = customerPN;
	}
	
	
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	
	public String getHHRevision() {
		return HHRevision;
	}
	public void setHHRevision(String hHRevision) {
		HHRevision = hHRevision;
	}
	public String getHHPNState() {
		return HHPNState;
	}
	public void setHHPNState(String hHPNState) {
		HHPNState = hHPNState;
	}
	public String getChineseDescription() {
		return chineseDescription;
	}
	public void setChineseDescription(String chineseDescription) {
		this.chineseDescription = chineseDescription;
	}
	public String getEnglishDescription() {
		return EnglishDescription;
	}
	public void setEnglishDescription(String englishDescription) {
		EnglishDescription = englishDescription;
	}
	public Integer getQtyUnits() {
		return QtyUnits;
	}
	public void setQtyUnits(Integer qtyUnits) {
		QtyUnits = qtyUnits;
	}
	
	/**
	 * 添加数量
	 * @param num
	 */
	public void addQtyUnits(int num) {
		QtyUnits = QtyUnits + num;
	}
	
	
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	
	public String getULClass() {
		return ULClass;
	}
	public void setULClass(String uLClass) {
		ULClass = uLClass;
	}
	public String getPartWeight() {
		return partWeight;
	}
	public void setPartWeight(String partWeight) {
		this.partWeight = partWeight;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	public String getPainting() {
		return painting;
	}
	public void setPainting(String painting) {
		this.painting = painting;
	}
	public String getPrinting() {
		return printing;
	}
	public void setPrinting(String printing) {
		this.printing = printing;
	}	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}	
	public String getTechnology() {
		return technology;
	}
	public void setTechnology(String technology) {
		this.technology = technology;
	}	
	public String getADHESIVE() {
		return ADHESIVE;
	}
	public void setADHESIVE(String aDHESIVE) {
		ADHESIVE = aDHESIVE;
	}	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getBUPN() {
		return BUPN;
	}	
	public void setBUPN(String bUPN) {
		BUPN = bUPN;
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
	public String getSurfaceFinished() {
		return surfaceFinished;
	}
	public void setSurfaceFinished(String surfaceFinished) {
		this.surfaceFinished = surfaceFinished;
	}
	public String getCustomerDrawingNumber() {
		return customerDrawingNumber;
	}
	public void setCustomerDrawingNumber(String customerDrawingNumber) {
		this.customerDrawingNumber = customerDrawingNumber;
	}
	public String getCustomer3DRev() {
		return Customer3DRev;
	}
	public void setCustomer3DRev(String customer3dRev) {
		Customer3DRev = customer3dRev;
	}
	public String getCustomer2DRev() {
		return Customer2DRev;
	}
	public void setCustomer2DRev(String customer2dRev) {
		Customer2DRev = customer2dRev;
	}
	public String getSmPartMaterialDimension() {
		return smPartMaterialDimension;
	}
	public void setSmPartMaterialDimension(String smPartMaterialDimension) {
		this.smPartMaterialDimension = smPartMaterialDimension;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getReferenceDimension() {
		return referenceDimension;
	}
	public void setReferenceDimension(String referenceDimension) {
		this.referenceDimension = referenceDimension;
	}
	public String getRunnerWeight() {
		return runnerWeight;
	}
	public void setRunnerWeight(String runnerWeight) {
		this.runnerWeight = runnerWeight;
	}
	public String getTotalweight() {
		return totalweight;
	}
	public void setTotalweight(String totalweight) {
		this.totalweight = totalweight;
	}
	
}
