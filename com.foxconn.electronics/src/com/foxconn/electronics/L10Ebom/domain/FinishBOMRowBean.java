package com.foxconn.electronics.L10Ebom.domain;

import com.foxconn.tcutils.util.TCPropertes;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;

public class FinishBOMRowBean implements EBOMApplyRowBean {

	@TCPropertes(tcProperty = "d9_Sequence", cell = 0, required = true, columnName = "項次")
	private Integer sequence;
	
	@TCPropertes(tcProperty = "d9_ShippingArea", cell = 1, required = true, columnName = "出貨地區")
	private String shippingArea;
	
	@TCPropertes(cell = 1)
	private boolean isModifyShippingArea = true;
	
	@TCPropertes(tcProperty = "d9_PanelStyle", cell = 2, required = true, columnName = "Panel類型")
	private String panelStyle;
	
	@TCPropertes(tcProperty = "d9_FinishPN", cell = 3, required = true, columnName = "成品料號")
	private String finishPN;
	
	@TCPropertes(cell = 3)
	private boolean isModifyFinishPN = true;
	
	@TCPropertes(tcProperty = "d9_WireType", cell = 4, required = true, columnName = "外部線材類型")
	private String wireType;
	
	@TCPropertes(cell = 4)
	private boolean isModifyWireType = true;
	
	@TCPropertes(tcProperty = "d9_Other", cell = 5, required = false, columnName = "其他")
	private String other;
	
	@TCPropertes(tcProperty = "d9_FinalPN", cell = 6, required = true, columnName = "FINAL ASSY料號")
	private String finalPN;
	
	@TCPropertes(cell = 6)
	private boolean isModifyFinalPN = true;
	
	@TCPropertes(tcProperty = "d9_PkgPN", cell = 7, required = true, columnName = "Packing ASSY料號")
	private String pkgPN;
	
	@TCPropertes(cell = 7)
	private boolean isModifyPkgPN = true;
	
	@TCPropertes(tcProperty = "d9_BOMReleaseNo", cell = 8, required = true, columnName = "BOM Release No.")
	private String bomReleaseNo;
	
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

	public String getPanelStyle() {
		return panelStyle;
	}

	public void setPanelStyle(String panelStyle) {
		this.panelStyle = panelStyle;
	}

	public String getFinishPN() {
		return finishPN;
	}

	public void setFinishPN(String finishPN) {
		this.finishPN = finishPN;
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

	public String getFinalPN() {
		return finalPN;
	}

	public void setFinalPN(String finalPN) {
		this.finalPN = finalPN;
	}

	public String getPkgPN() {
		return pkgPN;
	}

	public void setPkgPN(String pkgPN) {
		this.pkgPN = pkgPN;
	}

	public String getBomReleaseNo() {
		return bomReleaseNo;
	}

	public void setBomReleaseNo(String bomReleaseNo) {
		this.bomReleaseNo = bomReleaseNo;
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
	
	public boolean isModifyShippingArea() {
		return isModifyShippingArea;
	}

	public void setModifyShippingArea(boolean isModifyShippingArea) {
		this.isModifyShippingArea = isModifyShippingArea;
	}

	public boolean isModifyFinishPN() {
		return isModifyFinishPN;
	}

	public void setModifyFinishPN(boolean isModifyFinishPN) {
		this.isModifyFinishPN = isModifyFinishPN;
	}

	public boolean isModifyWireType() {
		return isModifyWireType;
	}

	public void setModifyWireType(boolean isModifyWireType) {
		this.isModifyWireType = isModifyWireType;
	}

	public boolean isModifyFinalPN() {
		return isModifyFinalPN;
	}

	public void setModifyFinalPN(boolean isModifyFinalPN) {
		this.isModifyFinalPN = isModifyFinalPN;
	}

	public boolean isModifyPkgPN() {
		return isModifyPkgPN;
	}

	public void setModifyPkgPN(boolean isModifyPkgPN) {
		this.isModifyPkgPN = isModifyPkgPN;
	}

	@Override
	public String toString() {
		return "FinishBOMBean [sequence=" + sequence + ", shippingArea=" + shippingArea + ", panelStyle=" + panelStyle
				+ ", finishPN=" + finishPN + ", wireType=" + wireType + ", other=" + other + ", finalPN=" + finalPN
				+ ", pkgPN=" + pkgPN + ", bomReleaseNo=" + bomReleaseNo + "]";
	}

	@Override
	public String getValue() {
		return sequence + "|" + shippingArea + "|" + panelStyle + "|" + finishPN + "|" + wireType + "|" 
				+ other + "|" + finalPN + "|" + pkgPN + "|" + bomReleaseNo;
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
	public String getAssyPN() {
		return null;
	}

	@Override
	public void setAssyPN(String assyPN) {
	}
	

	@Override
	public String getFinishPNDesc() {
		return null;
	}

	@Override
	public void setFinishPNDesc(String finishPNDesc) {
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
	public boolean isModifyAssyPN() {
		return false;
	}

	@Override
	public void setModifyAssyPN(boolean isModifyAssyPN) {
	}
	
	
}
