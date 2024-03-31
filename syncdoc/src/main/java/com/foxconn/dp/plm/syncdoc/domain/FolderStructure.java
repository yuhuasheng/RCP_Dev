package com.foxconn.dp.plm.syncdoc.domain;

public class FolderStructure {

	private String level;
	private String parentName;
	private String childName;
	private String folderPath;
	
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getChildName() {
		return childName;
	}
	public void setChildName(String childName) {
		this.childName = childName;
	}
	public String getFolderPath() {
		return folderPath;
	}
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	
	@Override
	public String toString() {
		return "ProjectStructure [level=" + level + ", parentName=" + parentName + ", childName=" + childName
				+ ", folderPath=" + folderPath + "]";
	}
	
}
