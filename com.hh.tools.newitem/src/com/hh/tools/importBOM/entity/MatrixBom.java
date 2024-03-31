package com.hh.tools.importBOM.entity;

import java.util.List;

public class MatrixBom {
    private String partNum;

    private String description;

    private List<MatrixBom> childBomList;

    private int qty;

    private String sapRev;

    public String getPartNum() {
        return partNum;
    }

    public void setPartNum(String partNum) {
        this.partNum = partNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MatrixBom> getChildBomList() {
        return childBomList;
    }

    public void setChildBomList(List<MatrixBom> childBomList) {
        this.childBomList = childBomList;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getSapRev() {
        return sapRev;
    }

    public void setSapRev(String sapRev) {
        this.sapRev = sapRev;
    }

}
