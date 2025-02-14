package com.foxconn.mechanism.dtpac.matmaintain.domain;

import com.foxconn.tcutils.util.TCPropertes;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;
 
public class MatMaintainBean {
	
	private long num = 0;
	
	private String UUID;
	
	@TCPropertes(tcProperty = "d9_PartType", tcType = "ItemRevision")
	private String partType;
	
	@TCPropertes(tcProperty = "d9_PartType2", tcType = "ItemRevision")
	private String matType;
	
	@TCPropertes(tcProperty = "d9_PartType3", tcType = "ItemRevision")
	private String matName;
	
	@TCPropertes(cell = 0)
	private int index;
	
	@TCPropertes(tcProperty = "item_id", cell = 1, tcType = "ItemRevision")
	private String itemId;
	
	@TCPropertes(tcProperty = "item_revision_id", tcType = "ItemRevision")
	private String version;	
	
	@TCPropertes(tcProperty = "d9_Type", cell = 2, tcType = "TCComponentFnd0TableRow")
	private String type = "";
	
	@TCPropertes(tcProperty = "d9_Material", cell = 3, tcType = "TCComponentFnd0TableRow")
	private String material = "";
	
	@TCPropertes(tcProperty = "d9_Length", cell = 4, tcType = "TCComponentFnd0TableRow", columnName = MatMaintainConstant.LENGTH)
	private String length = "";
	
	@TCPropertes(tcProperty = "d9_Width", cell = 5, tcType = "TCComponentFnd0TableRow", columnName = MatMaintainConstant.WIDTH)
	private String width = "";
	
	@TCPropertes(tcProperty = "d9_Height", cell = 6, tcType = "TCComponentFnd0TableRow", columnName = MatMaintainConstant.HEIGHT)
	private String height = "";
	
	@TCPropertes(tcProperty = "d9_Thickness", cell = 7, tcType = "TCComponentFnd0TableRow", columnName = MatMaintainConstant.THICKNESS)
	private String thickness = "";
	
	@TCPropertes(tcProperty = "d9_Density", cell = 8, tcType = "TCComponentFnd0TableRow", columnName = MatMaintainConstant.DENSITY)
	private String density = "";
	
	@TCPropertes(tcProperty = "d9_Weight", cell = 9, tcType = "TCComponentFnd0TableRow", columnName = MatMaintainConstant.WEIGHT)
	private String weight = ""; // 重量
	
//	@TCPropertes(tcProperty = "bl_quantity", tcType = "BOMLine")
//	private String bomQty = "";	
//	
//	@TCPropertes(tcProperty = "d9_Quantity", tcType = "TCComponentFnd0TableRow")
//	private String tableQty = "";
	
	@TCPropertes(cell = 10, tcProperty = "d9_Quantity", tcType = "TCComponentFnd0TableRow", columnName = MatMaintainConstant.QTY)
	private String qty = "";		
	
	@TCPropertes(tcProperty = "d9_CalculationUnit", cell = 11, tcType = "TCComponentFnd0TableRow")
	private String calcullationUnit = ""; // 计算单位	
	
	@TCPropertes(tcProperty = "d9_UsageCalculation", cell = 12, tcType = "TCComponentFnd0TableRow")
	private String usageCalculation = ""; // 用量计算	
	
	@TCPropertes(tcProperty = "d9_CostFactor", cell = 13, tcType = "TCComponentFnd0TableRow")
	private String costFactor = "";
	
	@TCPropertes(tcProperty = "d9_Cost", cell = 14, tcType = "TCComponentFnd0TableRow", columnName = MatMaintainConstant.COST, required = true)
	private String cost = "";

//	@TCPropertes(tcProperty = "d9_PartCost", tcType = "ItemRevision")
//	private String totalCost;

	private boolean hasModify = false;
	
	private boolean isDelete = false; // 记录是否被删除	
	
	private boolean isSelect = false;
	
	private TCComponentFnd0TableRow row;
	
//	private TCComponentItemRevision itemRev;	
	
	public long getNum() {
		return num;
	}

	public void setNum(long num) {
		this.num = num;
	}

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}

	public String getPartType() {
		return partType;
	}

	public void setPartType(String partType) {
		this.partType = partType;
	}
	

	public String getMatType() {
		return matType;
	}

	public void setMatType(String matType) {
		this.matType = matType;
	}

	public String getMatName() {
		return matName;
	}

	public void setMatName(String matName) {
		this.matName = matName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getThickness() {
		return thickness;
	}

	public void setThickness(String thickness) {
		this.thickness = thickness;
	}

	public String getDensity() {
		return density;
	}

	public void setDensity(String density) {
		this.density = density;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}
			
	
//	public String getBomQty() {
//		return bomQty;
//	}
//
//	public void setBomQty(String bomQty) {
//		this.bomQty = bomQty;
//	}	
//
//	public String getTableQty() {
//		return tableQty;
//	}
//
//	public void setTableQty(String tableQty) {
//		this.tableQty = tableQty;
//	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getUsageCalculation() {
		return usageCalculation;
	}

	public void setUsageCalculation(String usageCalculation) {
		this.usageCalculation = usageCalculation;
	}

	public String getCalcullationUnit() {
		return calcullationUnit;
	}

	public void setCalcullationUnit(String calcullationUnit) {
		this.calcullationUnit = calcullationUnit;
	}

	public String getCostFactor() {
		return costFactor;
	}

	public void setCostFactor(String costFactor) {
		this.costFactor = costFactor;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

//	public String getTotalCost() {
//		return totalCost;
//	}
//
//	public void setTotalCost(String totalCost) {
//		this.totalCost = totalCost;
//	}

	public boolean isHasModify() {
		return hasModify;
	}

	public void setHasModify(boolean hasModify) {
		this.hasModify = hasModify;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public TCComponentFnd0TableRow getRow() {
		return row;
	}

	public void setRow(TCComponentFnd0TableRow row) {
		this.row = row;
	}

//	public TCComponentItemRevision getItemRev() {
//		return itemRev;
//	}
//
//	public void setItemRev(TCComponentItemRevision itemRev) {
//		this.itemRev = itemRev;
//	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((UUID == null) ? 0 : UUID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatMaintainBean other = (MatMaintainBean) obj;
		if (UUID == null) {
			if (other.UUID != null)
				return false;
		} else if (!UUID.equals(other.UUID))
			return false;
		return true;
	}		
	
	
}
