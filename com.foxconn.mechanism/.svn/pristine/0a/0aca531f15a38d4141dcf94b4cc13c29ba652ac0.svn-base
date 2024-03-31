package com.foxconn.mechanism.hhpnmaterialapply.domain;


/**
 * TreeItem结构树属性修改类
 * @author HuashengYu
 *
 */
public class TreeItemUpdateInfo {	
	
	private String itemId;
	
	private int index;
	
	private String value;

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "TreeItemUpdateInfo [itemId=" + itemId + ", index=" + index + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		TreeItemUpdateInfo other = (TreeItemUpdateInfo) obj;
		if (index != other.index)
			return false;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	
	
}
