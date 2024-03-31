package com.foxconn.dp.plm.syncdoc.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PDMDocSyncInfo {
    public static int DIFF_TYPE_NEW = 1;
    public static int DIFF_TYPE_UPDATE = 2;
    public static int DIFF_TYPE_DELETE = 3;
    private Long fsSn;
    private String puid;
    private String itemId;
    private PDMDocSyncInfo parent;
    private String uid;
    private String name;
    private String type;
    private String version;
    private String spasId;
    private String creator;
    private Date created;
    private Long status;
    private String creatorName;
    // 1 新增 2 修改 3 删除
    private int diffType;
    List<PDMDocSyncInfo> children = new ArrayList<>();

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PDMDocSyncInfo that = (PDMDocSyncInfo) o;

        return Objects.equals(uid, that.uid);
    }

    @Override
    public int hashCode() {
        return uid != null ? uid.hashCode() : 0;
    }

    public int getDiffType() {
        return diffType;
    }

    public void setDiffType(int diffType) {
        this.diffType = diffType;
    }

    public PDMDocSyncInfo() {

    }

    public PDMDocSyncInfo(String uid) {
        this.uid = uid;
    }

    public Long getFsSn() {
        return fsSn;
    }

    public void setFsSn(Long fsSn) {
        this.fsSn = fsSn;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public PDMDocSyncInfo getParent() {
        return parent;
    }

    public void setParent(PDMDocSyncInfo parent) {
        this.parent = parent;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type ;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSpasId() {
        return spasId;
    }

    public void setSpasId(String spasId) {
        this.spasId = spasId;
    }

    public List<PDMDocSyncInfo> getChildren() {
        return children;
    }

    public void addChildren(List<PDMDocSyncInfo> children) {
        this.children.addAll(children);
    }

    public void addChild(PDMDocSyncInfo child) {
        this.children.add(child);
    }

    public void removeChild(PDMDocSyncInfo child){
        this.children.remove(child);
    }


}
