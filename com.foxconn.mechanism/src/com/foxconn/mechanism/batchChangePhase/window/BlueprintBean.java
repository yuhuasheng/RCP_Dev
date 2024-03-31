package com.foxconn.mechanism.batchChangePhase.window;

public class BlueprintBean {

	// 图纸名称
    String blueprintName;

    // 所有者
    String owner;

    // 所属父项ID
    String parentId;

    // 所属父项版本
    String parentVer;


    public BlueprintBean(String blueprintName, String owner, String parentId, String parentVer) {
        this.blueprintName = blueprintName;
        this.owner = owner;
        this.parentId = parentId;
        this.parentVer = parentVer;
    }

    public String getBlueprintName() {
        return blueprintName;
    }

    public void setBlueprintName(String blueprintName) {
        this.blueprintName = blueprintName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentVer() {
        return parentVer;
    }

    public void setParentVer(String parentVer) {
        this.parentVer = parentVer;
    }

}
