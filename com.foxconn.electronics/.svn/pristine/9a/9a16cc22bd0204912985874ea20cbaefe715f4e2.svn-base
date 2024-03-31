package com.foxconn.electronics.dcnreport.batcheditornewmoldfee.domain;

import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.constant.NewMoldFeeConstant;
import com.foxconn.tcutils.util.TCPropertes;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class NewMoldFeeBean {

	@TCPropertes(cell = 0)
	private Integer index;
	
	@TCPropertes(tcProperty = "item_id", tcType = "D9_MEDesignRevision", cell = 1)
	private String itemId;
	
	@TCPropertes(tcProperty = "item_revision_id", tcType = "D9_MEDesignRevision", cell = 2)
	private String version;
	
	@TCPropertes(tcProperty = "object_name", tcType = "D9_MEDesignRevision", cell = 3)
	private String objectName;
	
	@TCPropertes(tcProperty = "d9_ActualUserID", tcType = "D9_MEDesignRevision", cell = 4)
	private String owner;
	
	@TCPropertes(tcProperty = "d9_HHPN", tcType = "D9_MEDesignRevision", cell = 5)
	private String hhpn;
	
	@TCPropertes(tcProperty = "d9_NewMoldFee", tcType = "D9_MoldInfo", cell = 6, required = true, columnName = "新模费用")
	private String newMoldFee;
	
	@TCPropertes(tcProperty = "d9_Currency", tcType = "D9_MoldInfo", cell = 7, required = true, columnName = "币种")
	private String currency = NewMoldFeeConstant.CURRENCYLOV[0].toString();
	
	private TCComponentItemRevision itemRev;
	
	private TCComponentForm form;
	
	@TCPropertes(tcProperty = "object_name", tcType = "D9_MoldInfo")
	private String newMoldFeeName;
	
	private boolean hasModify = false; // 判断是否发生修改
	
	
	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
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

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getHhpn() {
		return hhpn;
	}

	public void setHhpn(String hhpn) {
		this.hhpn = hhpn;
	}

	public String getNewMoldFee() {
		return newMoldFee;
	}

	public void setNewMoldFee(String newMoldFee) {
		this.newMoldFee = newMoldFee;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public TCComponentItemRevision getItemRev() {
		return itemRev;
	}

	public void setItemRev(TCComponentItemRevision itemRev) {
		this.itemRev = itemRev;
	}

	public TCComponentForm getForm() {
		return form;
	}

	public void setForm(TCComponentForm form) {
		this.form = form;
	}

	public boolean isHasModify() {
		return hasModify;
	}

	public void setHasModify(boolean hasModify) {
		this.hasModify = hasModify;
	}

	public String getNewMoldFeeName() {
		return newMoldFeeName;
	}

	public void setNewMoldFeeName(String newMoldFeeName) {
		this.newMoldFeeName = newMoldFeeName;
	}

	@Override
	public String toString() {
		return "NewMoldFeeBean [index=" + index + ", itemId=" + itemId + ", version=" + version + ", objectName="
				+ objectName + ", owner=" + owner + ", hhpn=" + hhpn + ", newMoldFee=" + newMoldFee + ", currency="
				+ currency + "]";
	}	
	
	public String getValue() {
		return index + "|" + itemId + "|" + version + "|" + objectName + "|" + owner + "|" + hhpn + "|" + newMoldFee + "|" + currency;
	}
}
