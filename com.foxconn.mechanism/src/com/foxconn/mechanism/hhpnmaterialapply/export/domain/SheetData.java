package com.foxconn.mechanism.hhpnmaterialapply.export.domain;

import java.util.List;

public class SheetData
{
    private int             index;
    private String          name;
    private List<DataModel> datas;

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<DataModel> getDatas()
    {
        return datas;
    }

    public void setDatas(List<DataModel> datas)
    {
        this.datas = datas;
    }
}
