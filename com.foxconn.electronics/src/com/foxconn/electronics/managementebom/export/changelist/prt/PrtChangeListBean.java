package com.foxconn.electronics.managementebom.export.changelist.prt;

import java.util.List;

import com.foxconn.electronics.managementebom.export.changelist.ChangeAction;
import com.foxconn.electronics.managementebom.export.changelist.MergedRegionInfo;
import com.foxconn.electronics.managementebom.updatebom.domain.EBOMLineBean;
import com.foxconn.electronics.util.TCPropName;

public class PrtChangeListBean
{
    @TCPropName(cell = 0)
    private String                 version;
    @TCPropName(cell = 1)
    private String                 stdPN;
    @TCPropName(cell = 2)
    private String                 mfgPn;
    @TCPropName(cell = 3)
    private String                 mfg;
    @TCPropName(cell = 4)
    private String                 changeDes;
    @TCPropName(cell = 5)
    private String                 action;
    @TCPropName(cell = 6)
    private String                 qtyBefore;
    @TCPropName(cell = 7)
    private String                 qtyAfter;
    @TCPropName(cell = 8)
    private String                 location;
    @TCPropName(cell = 9)
    private String                 changeDate;
    @TCPropName(cell = 10)
    private String                 impPhase;
    @TCPropName(cell = 11)
    private String                 substituteFlag;
    private List<MergedRegionInfo> mergedRegionInfos;

    public PrtChangeListBean(EBOMLineBean ebomBean, boolean isSubs, ChangeAction action)
    {
        this.action = action.name();
        this.location = ebomBean.getLocation();
        this.mfg = ebomBean.getMfg();
        this.mfgPn = ebomBean.getMfgPn();
        this.stdPN = ebomBean.getItem();
        if (isSubs)
        {
            this.substituteFlag = "ALT";
            this.qtyAfter = "N/A";
            this.location = "N/A";
            this.qtyBefore = "N/A";
            if (action.equals(ChangeAction.Add))
            {
                this.changeDes = "Add 2nd source:\n" + ebomBean.getMainSource();
            }
            else if (action.equals(ChangeAction.Delete))
            {
                this.changeDes = "Remove 2nd source:\n" + ebomBean.getMainSource();
            }
        }
        else
        {
            if (action.equals(ChangeAction.Add))
            {
                this.qtyAfter = "1";
                this.qtyBefore = "0";
            }
            else if (action.equals(ChangeAction.Delete))
            {
                this.qtyAfter = "0";
                this.qtyBefore = "1";
            }
        }
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getStdPN()
    {
        return stdPN;
    }

    public void setStdPN(String stdPN)
    {
        this.stdPN = stdPN;
    }

    public String getMfgPn()
    {
        return mfgPn;
    }

    public void setMfgPn(String mfgPn)
    {
        this.mfgPn = mfgPn;
    }

    public String getMfg()
    {
        return mfg;
    }

    public void setMfg(String mfg)
    {
        this.mfg = mfg;
    }

    public String getChangeDes()
    {
        return changeDes;
    }

    public void setChangeDes(String changeDes)
    {
        this.changeDes = changeDes;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getQtyBefore()
    {
        return qtyBefore;
    }

    public void setQtyBefore(String qtyBefore)
    {
        this.qtyBefore = qtyBefore;
    }

    public String getQtyAfter()
    {
        return qtyAfter;
    }

    public void setQtyAfter(String qtyAfter)
    {
        this.qtyAfter = qtyAfter;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getChangeDate()
    {
        return changeDate;
    }

    public void setChangeDate(String changeDate)
    {
        this.changeDate = changeDate;
    }

    public String getImpPhase()
    {
        return impPhase;
    }

    public void setImpPhase(String impPhase)
    {
        this.impPhase = impPhase;
    }

    public String getSubstituteFlag()
    {
        return substituteFlag;
    }

    public void setSubstituteFlag(String substituteFlag)
    {
        this.substituteFlag = substituteFlag;
    }

    public List<MergedRegionInfo> getMergedRegionInfos()
    {
        return mergedRegionInfos;
    }

    public void setMergedRegionInfos(List<MergedRegionInfo> mergedRegionInfos)
    {
        this.mergedRegionInfos = mergedRegionInfos;
    }
}
