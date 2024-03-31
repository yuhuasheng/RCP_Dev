package com.foxconn.electronics.L10Ebom.domain;

import com.foxconn.tcutils.util.TCPropertes;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;

public class FinishMatRowBean implements EBOMApplyRowBean {

	@TCPropertes(tcProperty = "d9_Sequence", cell = 0, required = true, columnName = "項次")
	private Integer sequence;

	@TCPropertes(tcProperty = "d9_CustomerModelName", cell = 1, required = true, columnName = "客户Model Name")
	private String customerModelName;

	@TCPropertes(tcProperty = "d9_FoxconnModelName", cell = 2, required = true, columnName = "Foxconn Model Name")
	private String foxconnModelName;

	@TCPropertes(tcProperty = "d9_FinishPNDesc", cell = 3, required = true, columnName = "成品料號描述")
	private String finishPNDesc;

	@TCPropertes(tcProperty = "d9_ShippingArea", cell = 4, required = true, columnName = "出貨地區")
	private String shippingArea;

	@TCPropertes(tcProperty = "d9_PowerLineType", cell = 5, required = true, columnName = "電源線類型")
	private String powerLineType;

	@TCPropertes(tcProperty = "d9_PCBAInterface", cell = 6, required = true, columnName = "PCBA接口")
	private String pcbaInterface;

	@TCPropertes(tcProperty = "d9_IsSpeaker", cell = 7, required = true, columnName = "有無喇叭")
	private String isSpeaker;

	@TCPropertes(tcProperty = "d9_Color", cell = 8, required = true, columnName = "颜色")
	private String color;

	@TCPropertes(tcProperty = "d9_WireType", cell = 9, required = true, columnName = "外部線材類型")
	private String wireType;
	
	@TCPropertes(tcProperty = "d9_Other", cell = 10)
	private String other;
	
	@TCPropertes(tcProperty = "d9_ShipSize", cell = 11, required = true, columnName = "出貨優選方式尺寸")
	private String shipSize;
	
	@TCPropertes(tcProperty = "d9_ShipType", cell = 12, required = true, columnName = "出貨優選方式型式")
	private String shipType;
	
	@TCPropertes(tcProperty = "d9_RefMaterialPN", cell = 13)
	private String refMaterialPN;
	
	@TCPropertes(tcProperty = "d9_Remark", cell = 14)
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

	public String getCustomerModelName() {
		return customerModelName;
	}

	public void setCustomerModelName(String customerModelName) {
		this.customerModelName = customerModelName;
	}

	public String getFoxconnModelName() {
		return foxconnModelName;
	}

	public void setFoxconnModelName(String foxconnModelName) {
		this.foxconnModelName = foxconnModelName;
	}

	public String getFinishPNDesc() {
		return finishPNDesc;
	}

	public void setFinishPNDesc(String finishPNDesc) {
		this.finishPNDesc = finishPNDesc;
	}

	public String getShippingArea() {
		return shippingArea;
	}

	public void setShippingArea(String shippingArea) {
		this.shippingArea = shippingArea;
	}

	public String getPowerLineType() {
		return powerLineType;
	}

	public void setPowerLineType(String powerLineType) {
		this.powerLineType = powerLineType;
	}

	public String getPcbaInterface() {
		return pcbaInterface;
	}

	public void setPcbaInterface(String pcbaInterface) {
		this.pcbaInterface = pcbaInterface;
	}

	public String getIsSpeaker() {
		return isSpeaker;
	}

	public void setIsSpeaker(String isSpeaker) {
		this.isSpeaker = isSpeaker;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
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
	

	@Override
	public String toString() {
		return "FinishMatBean [sequence=" + sequence + ", customerModelName=" + customerModelName
				+ ", foxconnModelName=" + foxconnModelName + ", finishPNDesc=" + finishPNDesc + ", shippingArea="
				+ shippingArea + ", powerLineType=" + powerLineType + ", pcbaInterface=" + pcbaInterface
				+ ", isSpeaker=" + isSpeaker + ", color=" + color + "]";
	}

	public String getValue() {
		return sequence + "|" + customerModelName + "|" + foxconnModelName + "|" + finishPNDesc + "|" + shippingArea
				+ "|" + powerLineType + "|" + pcbaInterface + "|" + isSpeaker + "|" + color;
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
