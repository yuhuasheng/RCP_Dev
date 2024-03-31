package com.foxconn.mechanism.SecondSourceImport;

import java.util.List;

public class GroupPojo {
private String groupId;	
private String bu;
private String objType;
private List<MaterialPojo> MaterialPojos;
public String getBu() {
	return bu;
}
public void setBu(String bu) {
	this.bu = bu;
}
public String getObjType() {
	return objType;
}
public void setObjType(String objType) {
	this.objType = objType;
}
public List<MaterialPojo> getMaterialPojos() {
	return MaterialPojos;
}
public void setMaterialPojos(List<MaterialPojo> materialPojos) {
	MaterialPojos = materialPojos;
}
public String getGroupId() {
	return groupId;
}
public void setGroupId(String groupId) {
	this.groupId = groupId;
}


}
