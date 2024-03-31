package com.foxconn.electronics.managementebom.export.changelist.mnt;

import com.foxconn.electronics.util.TCPropName;

public class MntChangeSheet
{
    public final static int    START_ROW = 6;
    public final static String TEMPLATE  = "com/foxconn/electronics/managementebom/template/MNT_BOM_Change_List.xlsx";
    @TCPropName(row = 2, cell = 1)
    private String             model;

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }
}
