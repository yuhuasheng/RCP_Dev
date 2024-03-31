package com.foxconn.mechanism.hhpnmaterialapply.export.domain;

import com.foxconn.mechanism.hhpnmaterialapply.export.util.TCPropertes;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class PRTDataModel implements DataModel
{
    @TCPropertes(cell = 0)
    private String                  item;
    @TCPropertes(cell = 1)
    private String                  projectName;
    //
    @TCPropertes(tcProperty = "d9_Module", cell = 2)
    private String                  module;
    //
    @TCPropertes(tcProperty = "d9_CustomerDrawingNumber", cell = 3)
    private String                  customEmpPN;
    //
    @TCPropertes(tcProperty = "item_master_tag", cell = 4, tcType = "MEDesign")
    private String                  tcID;
    //
    @TCPropertes(tcProperty = "bl_item_object_name", cell = 5, tcType = "BOMLine")
    private String                  modelName;
    //
    @TCPropertes(tcProperty = "d9_TempPN", cell = 6)
    private String                  bupn;
    //
    @TCPropertes(tcProperty = "d9_HHPN", cell = 7)
    private String                  hhpn;
    @TCPropertes(tcProperty = "d9_CustomerPN", cell = 8)
    private String                  customPN;
    //
    @TCPropertes(tcProperty = "d9_EnglishDescription", cell = 9)
    private String                  descriptionEn;
    //
    @TCPropertes(tcProperty = "d9_ChineseDescription", cell = 10)
    private String                  descriptionZh;
    //
    @TCPropertes(tcProperty = "d9_Finish", cell = 12)
    private String                  finish;
    //
    @TCPropertes(tcProperty = "bl_quantity", tcType = "BOMLine", cell = 13)
    private String                  usage;
    //
    @TCPropertes(tcProperty = "d9_Customer3DRev", cell = 14)
    private String                  customer3DRev;
    //
    @TCPropertes(tcProperty = "d9_Customer2DRev", cell = 15)
    private String                  customer2DRev;
    //
    @TCPropertes(tcProperty = "d9_Customer", cell = 16)
    private String                  customer;
    //
    @TCPropertes(tcProperty = "d9_MaterialColor", cell = 17)
    private String                  materialColor;
    //
    @TCPropertes(tcProperty = "d9_Material", cell = 18)
    private String                  material;
    //
    @TCPropertes(tcProperty = "d9_SMPartMatDimension", cell = 19)
    private String                  smPartMaterialDimension;
    
    @TCPropertes(tcProperty = "d9_ActualUserID", cell = 20, tcType = "MEDesign")
    private String actualUser;
    
    // part
    private TCComponentItemRevision itemRevision;

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getCustomPN()
    {
        return customPN;
    }

    public void setCustomPN(String customPN)
    {
        this.customPN = customPN;
    }

    public String getCustomEmpPN()
    {
        return customEmpPN;
    }

    public void setCustomEmpPN(String customEmpPN)
    {
        this.customEmpPN = customEmpPN;
    }

    public String getCustomer3DRev()
    {
        return customer3DRev;
    }

    public void setCustomer3DRev(String customer3dRev)
    {
        customer3DRev = customer3dRev;
    }

    public String getModule()
    {
        return module;
    }

    public void setModule(String module)
    {
        this.module = module;
    }

    public String getSmPartMaterialDimension()
    {
        return smPartMaterialDimension;
    }

    public void setSmPartMaterialDimension(String smPartMaterialDimension)
    {
        this.smPartMaterialDimension = smPartMaterialDimension;
    }
    
    public String getActualUser() {
		return actualUser;
	}

	public void setActualUser(String actualUser) {
		this.actualUser = actualUser;
	}

	public String getCustomer()
    {
        return customer;
    }

    public void setCustomer(String customer)
    {
        this.customer = customer;
    }

    public String getCustomer2DRev()
    {
        return customer2DRev;
    }

    public void setCustomer2DRev(String customer2dRev)
    {
        customer2DRev = customer2dRev;
    }

    public String getItem()
    {
        return item;
    }

    public void setItem(String item)
    {
        this.item = item;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        // TODO Auto-generated method stub
        return super.clone();
    }

    public String getHhpn()
    {
        return hhpn;
    }

    public void setHhpn(String hhpn)
    {
        this.hhpn = hhpn;
    }

    public String getBupn()
    {
        return bupn;
    }

    public void setBupn(String bupn)
    {
        this.bupn = bupn;
    }

    public String getUsage()
    {
        return usage;
    }

    public void setUsage(String usage)
    {
        this.usage = usage;
    }

    public TCComponentItemRevision getItemRevision()
    {
        return itemRevision;
    }

    public void setItemRevision(TCComponentItemRevision itemRevision)
    {
        this.itemRevision = itemRevision;
    }

    public String getDescriptionZh()
    {
        return descriptionZh;
    }

    public void setDescriptionZh(String descriptionZh)
    {
        this.descriptionZh = descriptionZh;
    }

    public String getDescriptionEn()
    {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn)
    {
        this.descriptionEn = descriptionEn;
    }

    public String getModelName()
    {
        return modelName;
    }

    public void setModelName(String modelName)
    {
        this.modelName = modelName;
    }

    public String getTcID()
    {
        return tcID;
    }

    public void setTcID(String tcID)
    {
        this.tcID = tcID;
    }
}
