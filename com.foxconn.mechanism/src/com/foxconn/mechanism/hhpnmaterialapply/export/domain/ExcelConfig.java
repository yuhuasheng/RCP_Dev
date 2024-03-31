package com.foxconn.mechanism.hhpnmaterialapply.export.domain;

public class ExcelConfig
{
    private String excel_template;
    private int    imageCol;
    private int    startRow;
    private int    colLength;
    private int    rowHeight;

    public String getExcel_template()
    {
        return excel_template;
    }

    public void setExcel_template(String excel_template)
    {
        this.excel_template = excel_template;
    }

    public int getImageCol()
    {
        return imageCol;
    }

    public void setImageCol(int imageCol)
    {
        this.imageCol = imageCol;
    }

    public int getStartRow()
    {
        return startRow;
    }

    public void setStartRow(int startRow)
    {
        this.startRow = startRow;
    }

    public int getColLength()
    {
        return colLength;
    }

    public void setColLength(int colLength)
    {
        this.colLength = colLength;
    }

    public int getRowHeight()
    {
        return rowHeight;
    }

    public void setRowHeight(int rowHeight)
    {
        this.rowHeight = rowHeight;
    }
}
