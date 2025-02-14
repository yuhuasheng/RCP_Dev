package com.foxconn.mechanism.dtpac.matmaintain.domain;

import com.foxconn.tcutils.util.TCPropertes;
import com.simple.traditionnal.util.S2TTransferUtil;

import cn.hutool.core.util.StrUtil;

public class ExcelBean {
	@TCPropertes(cell = 0)
	private int index;
	
	@TCPropertes(canEditor = true)
	private String partType = ""; 
	
	@TCPropertes(canEditor = true)
	private String matType = ""; // 物料类型
	
	@TCPropertes(canEditor = true)
	private String matName = ""; // 物料名称
	
	@TCPropertes(canEditor = true, cell = 2, columnName = MatMaintainConstant.TYPE)
	private String type = ""; // 类型
	
	@TCPropertes(canEditor = true, cell = 3, columnName = MatMaintainConstant.MATERIAL)
	private String material = ""; // 材质
	
	@TCPropertes(cell = 4, columnName = MatMaintainConstant.LENGTH, canEditor = true)
	private String length = ""; // 长
	
	@TCPropertes(cell = 5, columnName = MatMaintainConstant.WIDTH, canEditor = true)
	private String width = ""; // 宽
	
	@TCPropertes(cell = 6, columnName = MatMaintainConstant.HEIGHT, canEditor = true)
	private String height = ""; // 高
	
	@TCPropertes(cell = 7, columnName = MatMaintainConstant.THICKNESS, canEditor = true)
	private String thickness = ""; // 厚	
	
	@TCPropertes(cell = 8, columnName = MatMaintainConstant.DENSITY, canEditor = true)
	private String density = ""; // 密度
	
	@TCPropertes(cell = 9, columnName = MatMaintainConstant.WEIGHT, canEditor = true)
	private String weight = ""; // 重量
	
	@TCPropertes(cell = 10, columnName = MatMaintainConstant.QTY, canEditor = true)
	private String qty = "";
	
	@TCPropertes(cell = 11, columnName = MatMaintainConstant.CALCULLATIONUNIT)
	private String calcullationUnit = ""; // 计算单位
	
	@TCPropertes(cell = 12, columnName = MatMaintainConstant.USAGECALCULATION)
	private String usageCalculation = ""; //  用量计算	
	
	@TCPropertes(cell = 13, columnName = MatMaintainConstant.COSTFACTOR)
	private String costFactor = ""; // 成本因子
	
	@TCPropertes(cell = 14, columnName = MatMaintainConstant.COST)
	private String cost = ""; // 物料成本
	
	
	public ExcelBean() {
		super();
	}


	public ExcelBean(int index, String partType, String matType, String matName, String type, String material, String length,
			String width, String height, String thickness, String density, String weight, String usageCalculation,
			String calcullationUnit) {
		super();
		this.index = index;
		this.partType = partType;
		this.matType = matType;
		this.matName = matName;
		this.type = type;
		this.material = material;
		this.length = length;
		this.width = width;
		this.height = height;
		this.thickness = thickness;
		this.density = density;
		this.weight = weight;
		this.usageCalculation = usageCalculation;
		this.calcullationUnit = calcullationUnit;
	}
	
	
	public ExcelBean(String material, String costFactor) {
		super();
		this.material = material;
		this.costFactor = costFactor;
	}
	
	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}

	public String getPartType() {
		return partType;
	}
	public void setPartType(String partType) {
		this.partType = S2TTransferUtil.toSimpleString(partType);
	}
	public String getMatType() {
		return matType;
	}
	public void setMatType(String matType) {
		this.matType = S2TTransferUtil.toSimpleString(matType);
	}
	public String getMatName() {
		return matName;
	}
	public void setMatName(String matName) {
		this.matName = S2TTransferUtil.toSimpleString(matName);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = S2TTransferUtil.toSimpleString(type);
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = S2TTransferUtil.toSimpleString(material);
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
		this.usageCalculation = S2TTransferUtil.toSimpleString(usageCalculation);
	}
	public String getCalcullationUnit() {
		return calcullationUnit;
	}
	public void setCalcullationUnit(String calcullationUnit) {
		this.calcullationUnit = S2TTransferUtil.toSimpleString(calcullationUnit);
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
		if(StrUtil.isNotBlank(cost) && cost.contains(".")) {
			int index = cost.length() -1;
	        while (cost.charAt(index) == '0'){
	            index --;
	        }
	        if(cost.charAt(index) == '.') {
	        	this.cost = cost.substring(0,index);
	        }else {
	        	this.cost = cost.substring(0,index +1);
	        }
		}else {
			this.cost = cost;
		}
		
	}
	
}
