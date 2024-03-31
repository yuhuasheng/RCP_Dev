package com.foxconn.electronics.L10Ebom.domain;

import com.foxconn.tcutils.util.TCPropertes;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;

public class HalfMatRowBean implements EBOMApplyRowBean {

	@TCPropertes(tcProperty = "d9_Sequence", cell = 0, required = true, columnName = "項次")
	private Integer sequence;
	
	@TCPropertes(tcProperty = "d9_SemiType", cell = 1, required = true, columnName = "半成品類別")
	private String semiType;
	
	@TCPropertes(cell = 1)
	private boolean isModifySemiType = true;
	
	@TCPropertes(tcProperty = "d9_SemiPN", cell = 2, required = true, columnName = "半成品料號")
	private String semiPN;
	
	@TCPropertes(cell = 2)
	private boolean isModifySemiPN = true;
	
	@TCPropertes(tcProperty = "d9_Desc", cell = 3, required = true, columnName = "描述")
	private String desc;
	
	@TCPropertes(cell = 3)
	private boolean isModifyDesc = true;
	
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

	public String getSemiType() {
		return semiType;
	}

	public void setSemiType(String semiType) {
		this.semiType = semiType;
	}

	public String getSemiPN() {
		return semiPN;
	}

	public void setSemiPN(String semiPN) {
		this.semiPN = semiPN;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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

	
	public boolean isModifySemiType() {
		return isModifySemiType;
	}

	public void setModifySemiType(boolean isModifySemiType) {
		this.isModifySemiType = isModifySemiType;
	}	

	public boolean isModifySemiPN() {
		return isModifySemiPN;
	}

	public void setModifySemiPN(boolean isModifySemiPN) {
		this.isModifySemiPN = isModifySemiPN;
	}

	public boolean isModifyDesc() {
		return isModifyDesc;
	}

	public void setModifyDesc(boolean isModifyDesc) {
		this.isModifyDesc = isModifyDesc;
	}

	@Override
	public String toString() {
		return "HalfMatBean [sequence=" + sequence + ", semiType=" + semiType + ", semiPN=" + semiPN + ", desc=" + desc
				+ ", remark=" + remark + "]";
	}

	@Override
	public String getValue() {
		return sequence + "|" + semiType + "|" + semiPN + "|" + desc
				+ "," + remark;
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
	public String getWireType() {
		return null;
	}

	@Override
	public void setWireType(String wireType) {
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
