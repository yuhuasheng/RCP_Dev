package com.foxconn.electronics.L10Ebom.domain;

import com.foxconn.tcutils.util.TCPropertes;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;

public class RDEBOMRowBean implements EBOMApplyRowBean {

	@TCPropertes(tcProperty = "d9_Sequence", cell = 0, required = true, columnName = "項次")
	private Integer sequence;
	
	@TCPropertes(tcProperty = "d9_ShippingArea", cell = 1, required = true, columnName = "出貨地區")
	private String shippingArea;
	
	@TCPropertes(tcProperty = "d9_PkgPN", cell = 2, required = true, columnName = "Packing ASSY料號")
	private String pkgPN;
	
	@TCPropertes(cell = 2)
	private boolean isModifyPkgPN = true;
	
	@TCPropertes(tcProperty = "d9_IsNewPkgBOM", cell = 3, required = true, columnName = "是否新建Packing ASSY BOM")
	private String isNewPkgBOM;
	
	@TCPropertes(tcProperty = "d9_Remark", cell = 4, required = false, columnName = "備註")
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

	public String getShippingArea() {
		return shippingArea;
	}

	public void setShippingArea(String shippingArea) {
		this.shippingArea = shippingArea;
	}

	public String getPkgPN() {
		return pkgPN;
	}

	public void setPkgPN(String pkgPN) {
		this.pkgPN = pkgPN;
	}

	public String getIsNewPkgBOM() {
		return isNewPkgBOM;
	}

	public void setIsNewPkgBOM(String isNewPkgBOM) {
		this.isNewPkgBOM = isNewPkgBOM;
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
	
	public boolean isModifyPkgPN() {
		return isModifyPkgPN;
	}

	public void setModifyPkgPN(boolean isModifyPkgPN) {
		this.isModifyPkgPN = isModifyPkgPN;
	}

	@Override
	public String toString() {
		return "RDEBOMBean [sequence=" + sequence + ", shippingArea=" + shippingArea + ", pkgPN=" + pkgPN
				+ ", isNewPkgBOM=" + isNewPkgBOM + ", remark=" + remark + "]";
	}

	@Override
	public String getValue() {
		return sequence + "|" + shippingArea + "|" + pkgPN
				+ "|" + isNewPkgBOM + "|" + remark;
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
	public String getDesc() {
		return null;
	}

	@Override
	public void setDesc(String desc) {
	}

	@Override
	public String getFinishType() {
		return null;
	}

	@Override
	public void setFinishType(String finishType) {
	}

	@Override
	public String getFinishPN() {
		return null;
	}

	@Override
	public void setFinishPN(String finishPN) {
	}

	@Override
	public String getAssyPN() {
		return null;
	}

	@Override
	public void setAssyPN(String assyPN) {
	}

	@Override
	public String getWireType() {
		return null;
	}

	@Override
	public void setWireType(String wireType) {
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
	public boolean isModifyDesc() {
		return false;
	}

	@Override
	public void setModifyDesc(boolean isModifyDesc) {
	}

	@Override
	public boolean isModifyFinishType() {
		return false;
	}

	@Override
	public void setModifyFinishType(boolean isModifyFinishType) {
	}

	@Override
	public boolean isModifyFinishPN() {
		return false;
	}

	@Override
	public void setModifyFinishPN(boolean isModifyFinishPN) {
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
