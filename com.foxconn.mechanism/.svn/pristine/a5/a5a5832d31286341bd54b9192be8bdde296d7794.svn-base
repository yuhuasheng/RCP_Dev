package com.foxconn.mechanism.exportcreomodel.domain;

public class ModelInfo {
	
	
	/**
	 * 零件ID/版本
	 */
	private String itemRevId;
	
	/**
	 * 零件名称
	 */
	private String objectName;
	
	/**
	 * 新模型名称
	 */
	private String newModelName;

	
	public String getItemRevId() {
		return itemRevId;
	}


	public void setItemRevId(String itemRevId) {
		this.itemRevId = itemRevId;
	}


	public String getObjectName() {
		return objectName;
	}


	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}


	public String getNewModelName() {
		return newModelName;
	}


	public void setNewModelName(String newModelName) {
		this.newModelName = newModelName;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemRevId == null) ? 0 : itemRevId.hashCode());
		result = prime * result + ((newModelName == null) ? 0 : newModelName.hashCode());
		result = prime * result + ((objectName == null) ? 0 : objectName.hashCode());
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
		ModelInfo other = (ModelInfo) obj;
		if (itemRevId == null) {
			if (other.itemRevId != null)
				return false;
		} else if (!itemRevId.equals(other.itemRevId))
			return false;
		if (newModelName == null) {
			if (other.newModelName != null)
				return false;
		} else if (!newModelName.equals(other.newModelName))
			return false;
		if (objectName == null) {
			if (other.objectName != null)
				return false;
		} else if (!objectName.equals(other.objectName))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "ModelInfo [itemRevId=" + itemRevId + ", objectName=" + objectName + ", newModelName=" + newModelName
				+ "]";
	}

	
	
	
}
