package com.foxconn.electronics.managementebom.export.changelist;

public enum ChangeAction
{
 Add("A"), Delete("D"), Change("C");

    private String value;

    private ChangeAction(String value)
    {
        this.value = value;
    }

    public String value()
    {
        return value;
    }
}
