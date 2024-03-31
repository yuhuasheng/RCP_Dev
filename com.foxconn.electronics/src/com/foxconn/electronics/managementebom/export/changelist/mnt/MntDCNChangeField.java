package com.foxconn.electronics.managementebom.export.changelist.mnt;

public enum MntDCNChangeField
{
 QTY("qty"), Rev("version"), Des("description"), UNIT("unit"), SUPPLIER("mfg"), LOCATION("");

    private String value;

    private MntDCNChangeField(String value)
    {
        this.value = value;
    }

    public String value()
    {
        return this.value;
    }
}
