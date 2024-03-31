package com.foxconn.electronics.managementebom.export.bom.prt;

import com.foxconn.electronics.util.TCPropName;

/**
 * 
 * @author Robert
 *
 */
public class PrtSheetBean
{
    public final static String TEMPLATE = "com/foxconn/electronics/managementebom/template/Printer_BOM.xlsx";
    private String             sheetName;
    private int                start    = 7;
    @TCPropName(row = 0, cell = 1)
    private String             ProjectName;
    @TCPropName(row = 1, cell = 1)
    private String             pcba;
    @TCPropName(row = 2, cell = 1)
    private String             version;
    @TCPropName(row = 3, cell = 1)
    private String             date;
    @TCPropName(row = 4, cell = 1)
    private String             partNumber;

    public String getSheetName()
    {
        return sheetName;
    }

    public void setSheetName(String sheetName)
    {
        this.sheetName = sheetName;
    }

    public int getStart()
    {
        return start;
    }

    public void setStart(int start)
    {
        this.start = start;
    }

    public String getProjectName()
    {
        return ProjectName;
    }

    public void setProjectName(String projectName)
    {
        ProjectName = projectName;
    }

    public String getPcba()
    {
        return pcba;
    }

    public void setPcba(String pcba)
    {
        this.pcba = pcba;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getPartNumber()
    {
        return partNumber;
    }

    public void setPartNumber(String partNumber)
    {
        this.partNumber = partNumber;
    }
}
