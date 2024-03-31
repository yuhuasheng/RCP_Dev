package com.foxconn.electronics.managementebom.Import.bom.mnt.domain;

import com.foxconn.electronics.util.TCPropName;

public class MntSearchBean {
	
	@TCPropName(cell = 0)
	private Integer itemNo;
	
	@TCPropName(cell = 1)
	private String missPN;
	
	@TCPropName(cell = 2)
	private String partType;
	
	public String getMissPN() {
		return missPN;
	}
	public void setMissPN(String missPN) {
		this.missPN = missPN;
	}
	public String getPartType() {
		return partType;
	}
	public void setPartType(String partType) {
		this.partType = partType;
	}
	public Integer getItemNo() {
		return itemNo;
	}
	public void setItemNo(Integer itemNo) {
		this.itemNo = itemNo;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemNo == null) ? 0 : itemNo.hashCode());
		result = prime * result + ((missPN == null) ? 0 : missPN.hashCode());
		result = prime * result + ((partType == null) ? 0 : partType.hashCode());
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
		MntSearchBean other = (MntSearchBean) obj;
		if (itemNo == null) {
			if (other.itemNo != null)
				return false;
		} else if (!itemNo.equals(other.itemNo))
			return false;
		if (missPN == null) {
			if (other.missPN != null)
				return false;
		} else if (!missPN.equals(other.missPN))
			return false;
		if (partType == null) {
			if (other.partType != null)
				return false;
		} else if (!partType.equals(other.partType))
			return false;
		return true;
	}
	
	
}
