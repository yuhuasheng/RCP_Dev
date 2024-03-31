package com.foxconn.electronics.L10Ebom.domain;

import com.foxconn.tcutils.util.TCPropertes;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;

public class BOMApplyRowBean implements EBOMApplyRowBean {
	
	@TCPropertes(tcProperty = "d9_Sequence", cell = 0, required = true, columnName = "項次")
	private Integer sequence;
	
	@TCPropertes(tcProperty = "d9_FinishType", cell = 1, required = true, columnName = "成品類別")
	private String finishType;
	
	@TCPropertes(cell = 1)
	private boolean isModifyFinishType = true;
	
	@TCPropertes(tcProperty = "d9_FinishPN", cell = 2, required = true, columnName = "成品料號")
	private String finishPN;
	
	@TCPropertes(cell = 2)
	private boolean isModifyFinishPN = true;
	
	@TCPropertes(tcProperty = "d9_Desc", cell = 3, required = true, columnName = "描述")
	private String desc;
	
	@TCPropertes(cell = 3)
	private boolean isModifyDesc = true;	
	
	@TCPropertes(tcProperty = "d9_PanelPN", cell = 4, required = true, columnName = "Panel料號")
	private String panelPN;
	
	@TCPropertes(tcProperty = "d9_WireType", cell = 5, required = true, columnName = "外部線材類型")
	private String wireType;
	
	@TCPropertes(tcProperty = "d9_Other", cell = 6)
	private String other;
	
	@TCPropertes(tcProperty = "d9_ShipSize", cell = 7, required = true, columnName = "出貨優選方式尺寸")
	private String shipSize;
	
	@TCPropertes(tcProperty = "d9_ShipType", cell = 8, required = true, columnName = "出貨優選方式形式")
	private String shipType;
	
	@TCPropertes(tcProperty = "d9_RefMaterialPN", cell = 9, required = true, columnName = "参照BOM料號")
	private String refMaterialPN;
	
	@TCPropertes(tcProperty = "d9_Remark", cell = 10, required = false, columnName = "備註")
	private String remark;
    
	private boolean deleteFlag = false; // 判断当前行是否需要删除，默认为不删除
	
	private boolean isSelect = false; // 判断当前行是否被选中，默认为不选中
	
	private boolean isAdd = false; // 判断当前行是否为新增，默认为不是新增
	
	private boolean hasModify = false; // 作为判断是否发生修改的标识
	
	private TCComponentFnd0TableRow row = null; // 当前table行对象	

	private String createMode = "";	
	
	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public String getFinishType() {
		return finishType;
	}

	public void setFinishType(String finishType) {
		this.finishType = finishType;
	}

	public String getFinishPN() {
		return finishPN;
	}

	public void setFinishPN(String finishPN) {
		this.finishPN = finishPN;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getPanelPN() {
		return panelPN;
	}

	public void setPanelPN(String panelPN) {
		this.panelPN = panelPN;
	}

	public String getWireType() {
		return wireType;
	}

	public void setWireType(String wireType) {
		this.wireType = wireType;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getShipSize() {
		return shipSize;
	}

	public void setShipSize(String shipSize) {
		this.shipSize = shipSize;
	}

	public String getShipType() {
		return shipType;
	}

	public void setShipType(String shipType) {
		this.shipType = shipType;
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

	public boolean isDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public boolean isAdd() {
		return isAdd;
	}

	public void setAdd(boolean isAdd) {
		this.isAdd = isAdd;
	}

	public boolean isHasModify() {
		return hasModify;
	}

	public void setHasModify(boolean hasModify) {
		this.hasModify = hasModify;
	}

	public TCComponentFnd0TableRow getRow() {
		return row;
	}

	public void setRow(TCComponentFnd0TableRow row) {
		this.row = row;
	}

	
	public String getCreateMode() {
		return createMode;
	}

	public void setCreateMode(String createMode) {
		this.createMode = createMode;
	}

	
	public boolean isModifyFinishType() {
		return isModifyFinishType;
	}

	public void setModifyFinishType(boolean isModifyFinishType) {
		this.isModifyFinishType = isModifyFinishType;
	}

	public boolean isModifyFinishPN() {
		return isModifyFinishPN;
	}

	public void setModifyFinishPN(boolean isModifyFinishPN) {
		this.isModifyFinishPN = isModifyFinishPN;
	}

	public boolean isModifyDesc() {
		return isModifyDesc;
	}

	public void setModifyDesc(boolean isModifyDesc) {
		this.isModifyDesc = isModifyDesc;
	}

	@Override
	public String toString() {
		return "BOMApplyBean [sequence=" + sequence + ", finishType=" + finishType + ", finishPN=" + finishPN
				+ ", desc=" + desc + ", panelPN=" + panelPN + ", wireType=" + wireType + ", other=" + other
				+ ", shipSize=" + shipSize + ", shipType=" + shipType + ", refMaterialPN=" + refMaterialPN + ", remark="
				+ remark + "]";
	}

	@Override
	public String getValue() {
		return sequence + "|" + finishType + "|" + finishPN	+ "|" + desc + "|" + panelPN + "|" + wireType + "|" + other
				+ "|" + shipSize + "|" + shipType + "|" + refMaterialPN + "|" + remark;
	}

	@Override
	public String getSemiType() {
		return null;
	}

	@Override
	public void setSemiType(String semiType) {
	}

	@Override
	public String getSemiPN() {
		return null;
	}

	@Override
	public void setSemiPN(String semiPN) {
	}

	@Override
	public String getPkgPN() {
		return null;
	}

	@Override
	public void setPkgPN(String pkgPN) {
	}

	@Override
	public String getAssyPN() {
		return null;
	}

	@Override
	public void setAssyPN(String assyPN) {
	}

	@Override
	public String getShippingArea() {
		return null;
	}

	@Override
	public void setShippingArea(String shippingArea) {
	}
	

	@Override
	public String getFinishPNDesc() {
		return null;
	}

	@Override
	public void setFinishPNDesc(String finishPNDesc) {
	}

	@Override
	public String getFinalPN() {
		return null;
	}

	@Override
	public void setFinalPN(String finalPN) {
	}

	@Override
	public String getIsNewPCBA() {
		return null;
	}

	@Override
	public void setIsNewPCBA(String isNewPCBA) {
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setName(String name) {
	}

	@Override
	public boolean isModifySemiType() {
		return false;
	}

	@Override
	public void setModifySemiType(boolean isModifySemiType) {
	}

	@Override
	public boolean isModifySemiPN() {
		return false;
	}

	@Override
	public void setModifySemiPN(boolean isModifySemiPN) {
	}

	@Override
	public boolean isModifyPkgPN() {
		return false;
	}

	@Override
	public void setModifyPkgPN(boolean isModifyPkgPN) {
	}

	@Override
	public boolean isModifyAssyPN() {
		return false;
	}

	@Override
	public void setModifyAssyPN(boolean isModifyAssyPN) {
	}

	@Override
	public boolean isModifyShippingArea() {
		return false;
	}

	@Override
	public void setModifyShippingArea(boolean isModifyShippingArea) {
	}

	@Override
	public boolean isModifyWireType() {
		return false;
	}

	@Override
	public void setModifyWireType(boolean isModifyWireType) {
	}

	@Override
	public boolean isModifyFinalPN() {
		return false;
	}

	@Override
	public void setModifyFinalPN(boolean isModifyFinalPN) {
	}
	
	
}
