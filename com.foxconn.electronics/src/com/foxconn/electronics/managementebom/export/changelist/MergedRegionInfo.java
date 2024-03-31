package com.foxconn.electronics.managementebom.export.changelist;

public class MergedRegionInfo
{
    private String fieldName;
    private int    lenth;

    public MergedRegionInfo(String fieldName, int lenth)
    {
        this.lenth = lenth;
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public int getLenth()
    {
        return lenth;
    }

    public void setLenth(int lenth)
    {
        this.lenth = lenth;
    }
}
