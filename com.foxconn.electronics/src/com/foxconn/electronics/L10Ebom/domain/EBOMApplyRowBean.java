package com.foxconn.electronics.L10Ebom.domain;

import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;

public interface EBOMApplyRowBean {
	
//	public String[] getTableTitles();
	
//	public void setTitle(String title);
	
//	public String getTitle();
	
//	public void setTablePropName(String tablePropName);
	
//	public String getTablePropName();
	
	public void setSequence(Integer sequence);
	
	public Integer getSequence();
	
	public void setDeleteFlag(boolean deleteFlag);
	
	public boolean isDeleteFlag();
	
	public void setSelect(boolean isSelect);
	
	public boolean isSelect();

	public void setAdd(boolean isAdd);
	
	public boolean isAdd();
	
	public boolean isHasModify();
	
	public void setHasModify(boolean hasModify);
	
	public TCComponentFnd0TableRow getRow();

	public void setRow(TCComponentFnd0TableRow row);
	
	public String getValue();
	
	public String getCreateMode();

	public void setCreateMode(String createMode);
	
	public String getSemiType();

	public void setSemiType(String semiType);

	public String getIsNewPCBA();

	public void setIsNewPCBA(String isNewPCBA);

	public String getName();

	public void setName(String name);

	public String getSemiPN();

	public void setSemiPN(String semiPN);

	public String getDesc();

	public void setDesc(String desc);
	
	public String getFinishType();

	public void setFinishType(String finishType);

	public String getFinishPN();

	public void setFinishPN(String finishPN);
	
	public String getFinalPN();

	public void setFinalPN(String finalPN);
	
	public String getPkgPN();

	public void setPkgPN(String pkgPN);
	
	public String getAssyPN();

	public void setAssyPN(String assyPN);
	
	public String getWireType();

	public void setWireType(String wireType);
	
	public String getShippingArea();

	public void setShippingArea(String shippingArea);	
	
	public String getFinishPNDesc();

	public void setFinishPNDesc(String finishPNDesc);	
	
	public boolean isModifySemiType();

	public void setModifySemiType(boolean isModifySemiType);

	public boolean isModifySemiPN();

	public void setModifySemiPN(boolean isModifySemiPN);

	public boolean isModifyDesc();

	public void setModifyDesc(boolean isModifyDesc);
	
	public boolean isModifyFinishType();

	public void setModifyFinishType(boolean isModifyFinishType);

	public boolean isModifyFinishPN();

	public void setModifyFinishPN(boolean isModifyFinishPN);
	
	public boolean isModifyPkgPN();

	public void setModifyPkgPN(boolean isModifyPkgPN);
	
	public boolean isModifyAssyPN();

	public void setModifyAssyPN(boolean isModifyAssyPN);
	
	public boolean isModifyShippingArea();

	public void setModifyShippingArea(boolean isModifyShippingArea);

	public boolean isModifyWireType();

	public void setModifyWireType(boolean isModifyWireType);

	public boolean isModifyFinalPN();

	public void setModifyFinalPN(boolean isModifyFinalPN);

}
