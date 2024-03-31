package com.foxconn.electronics.managementebom.Import.bom.mnt.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class MntEBOMInfo {
	
	private List<MntEBOMInfo> childs; // 子对象集合
	
	private List<MntEBOMInfo> substitutesList; // 替代料集合
	
	private Integer findNum;
	
	private String itemId;
	
	private String description;
	
	private String supplier;
	
	private String supplierPN;
	
	private String supplierZF;
	
	private String usage;
	
	private String GrpQty;
	
	private String unit = "";
	
	private String location;
	
	private TCComponentItemRevision itemRev;
		
	private String bomLineUid;
	
	private Boolean subExist = false; // 替代料是否已经存在
	
	private Boolean childExist = false; // 判断子BOMLine是否存在
	
	public MntEBOMInfo() {
		childs = Collections.synchronizedList(new ArrayList<MntEBOMInfo>());
	}

	public Integer getFindNum() {
		return findNum;
	}

	public void setFindNum(Integer findNum) {
		this.findNum = findNum;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getSupplierPN() {
		return supplierPN;
	}

	public void setSupplierPN(String supplierPN) {
		this.supplierPN = supplierPN;
	}

	public String getSupplierZF() {
		return supplierZF;
	}

	public void setSupplierZF(String supplierZF) {
		this.supplierZF = supplierZF;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getGrpQty() {
		return GrpQty;
	}

	public void setGrpQty(String grpQty) {
		GrpQty = grpQty;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<MntEBOMInfo> getSubstitutesList() {
		return substitutesList;
	}

	public void setSubstitutesList(List<MntEBOMInfo> substitutesList) {
		this.substitutesList = substitutesList;
	}

	public List<MntEBOMInfo> getChilds() {
		return childs;
	}

	public void addChild(MntEBOMInfo child) {
		this.childs.add(child);
	}

	public void setChilds(List<MntEBOMInfo> childs) {
		this.childs = childs;
	}

	public TCComponentItemRevision getItemRev() {
		return itemRev;
	}

	public void setItemRev(TCComponentItemRevision itemRev) {
		this.itemRev = itemRev;
	}

	public String getBomLineUid() {
		return bomLineUid;
	}

	public void setBomLineUid(String bomLineUid) {
		this.bomLineUid = bomLineUid;
	}

	public Boolean getSubExist() {
		return subExist;
	}

	public void setSubExist(Boolean subExist) {
		this.subExist = subExist;
	}

	public Boolean getChildExist() {
		return childExist;
	}

	public void setChildExist(Boolean childExist) {
		this.childExist = childExist;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((GrpQty == null) ? 0 : GrpQty.hashCode());
		result = prime * result + ((bomLineUid == null) ? 0 : bomLineUid.hashCode());
		result = prime * result + ((childExist == null) ? 0 : childExist.hashCode());
		result = prime * result + ((childs == null) ? 0 : childs.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((findNum == null) ? 0 : findNum.hashCode());
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result + ((itemRev == null) ? 0 : itemRev.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((subExist == null) ? 0 : subExist.hashCode());
		result = prime * result + ((substitutesList == null) ? 0 : substitutesList.hashCode());
		result = prime * result + ((supplier == null) ? 0 : supplier.hashCode());
		result = prime * result + ((supplierPN == null) ? 0 : supplierPN.hashCode());
		result = prime * result + ((supplierZF == null) ? 0 : supplierZF.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		result = prime * result + ((usage == null) ? 0 : usage.hashCode());
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
		MntEBOMInfo other = (MntEBOMInfo) obj;
		if (GrpQty == null) {
			if (other.GrpQty != null)
				return false;
		} else if (!GrpQty.equals(other.GrpQty))
			return false;
		if (bomLineUid == null) {
			if (other.bomLineUid != null)
				return false;
		} else if (!bomLineUid.equals(other.bomLineUid))
			return false;
		if (childExist == null) {
			if (other.childExist != null)
				return false;
		} else if (!childExist.equals(other.childExist))
			return false;
		if (childs == null) {
			if (other.childs != null)
				return false;
		} else if (!childs.equals(other.childs))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (findNum == null) {
			if (other.findNum != null)
				return false;
		} else if (!findNum.equals(other.findNum))
			return false;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		if (itemRev == null) {
			if (other.itemRev != null)
				return false;
		} else if (!itemRev.equals(other.itemRev))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (subExist == null) {
			if (other.subExist != null)
				return false;
		} else if (!subExist.equals(other.subExist))
			return false;
		if (substitutesList == null) {
			if (other.substitutesList != null)
				return false;
		} else if (!substitutesList.equals(other.substitutesList))
			return false;
		if (supplier == null) {
			if (other.supplier != null)
				return false;
		} else if (!supplier.equals(other.supplier))
			return false;
		if (supplierPN == null) {
			if (other.supplierPN != null)
				return false;
		} else if (!supplierPN.equals(other.supplierPN))
			return false;
		if (supplierZF == null) {
			if (other.supplierZF != null)
				return false;
		} else if (!supplierZF.equals(other.supplierZF))
			return false;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		if (usage == null) {
			if (other.usage != null)
				return false;
		} else if (!usage.equals(other.usage))
			return false;
		return true;
	}	
	
	
}
