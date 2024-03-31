package com.foxconn.dp.plm.syncdoc.domain;

import java.util.Date;

public class DocumentRevEntity {
    private Long revSn;
    private Long docId;
    private Long fldId;
    private Long status;
    private String revName;
    private String revNum;
    private String refId;
    private String creator;
    private String creatorName;
    private Date created;

    public Long getRevSn() {
        return revSn;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public void setRevSn(Long revSn) {
        this.revSn = revSn;
    }

    public String getRevName() {
        return revName;
    }

    public void setRevName(String revName) {
        this.revName = revName;
    }

    public String getRevNum() {
        return revNum;
    }

    public void setRevNum(String revNum) {
        this.revNum = revNum;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getCreator() {
        return creator;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public Long getFldId() {
        return fldId;
    }

    public void setFldId(Long fldId) {
        this.fldId = fldId;
    }
}
