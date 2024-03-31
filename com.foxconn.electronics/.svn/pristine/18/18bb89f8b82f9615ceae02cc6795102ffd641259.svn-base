package com.foxconn.electronics.managementebom.export.bom.prt;

import com.foxconn.electronics.managementebom.updatebom.domain.EBOMLineBean;
import com.foxconn.electronics.util.TCPropName;

/**
 * 
 * @author Robert
 *
 */
public class PrtBomBean
{
    @TCPropName(cell = 0)
    private String item;
    @TCPropName(cell = 1)
    private String stdPN;
    @TCPropName(cell = 2)
    private String partDes;
    @TCPropName(cell = 3)
    private String mfg;
    @TCPropName(cell = 4)
    private String mfgPN;
    @TCPropName(cell = 5)
    private String location;
    @TCPropName(cell = 6)
    private String qty;
    @TCPropName(cell = 7)
    private String packageType;
    @TCPropName(cell = 8)
    private String substituteFlag;
    @TCPropName(cell = 9)
    private String remark;

    public PrtBomBean(EBOMLineBean ebomBean, boolean isSubs)
    {
        this.item = ebomBean.getFindNum() + "";
        this.stdPN = ebomBean.getItem();
        this.location = ebomBean.getLocation();
        this.partDes = ebomBean.getDescription();
        this.mfg = ebomBean.getMfg();
        this.mfgPN = ebomBean.getMfgPn();
        this.packageType = ebomBean.getPackageType();
        this.qty = ebomBean.getQty();
        this.substituteFlag = "PRI";
        if (isSubs)
        {
            substituteFlag = "ALT";
            item = "";
            location = "";
            qty = "";
        }
    }

    public String getItem()
    {
        return item;
    }

    public void setItem(String item)
    {
        this.item = item;
    }

    public String getStdPN()
    {
        return stdPN;
    }

    public void setStdPN(String stdPN)
    {
        this.stdPN = stdPN;
    }

    public String getPartDes()
    {
        return partDes;
    }

    public void setPartDes(String partDes)
    {
        this.partDes = partDes;
    }

    public String getMfg()
    {
        return mfg;
    }

    public void setMfg(String mfg)
    {
        this.mfg = mfg;
    }

    public String getMfgPN()
    {
        return mfgPN;
    }

    public void setMfgPN(String mfgPN)
    {
        this.mfgPN = mfgPN;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getQty()
    {
        return qty;
    }

    public void setQty(String qty)
    {
        this.qty = qty;
    }

    public String getPackageType()
    {
        return packageType;
    }

    public void setPackageType(String packageType)
    {
        this.packageType = packageType;
    }

    public String getSubstituteFlag()
    {
        return substituteFlag;
    }

    public void setSubstituteFlag(String substituteFlag)
    {
        this.substituteFlag = substituteFlag;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}
