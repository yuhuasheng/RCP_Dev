package com.foxconn.electronics.explodebom.domain;

import com.foxconn.electronics.util.TCPropName;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class BOMLineBean
{
    @TCPropName(value = "bl_item_item_id", order = 1)
    private String                  hhPN;
    @TCPropName(value = "bl_item_D9_STDPN", order = 2)
    private String                  stdPN;
    @TCPropName(value = "bl_item_object_name", order = 3)
    private String                  description;
    @TCPropName(value = "bl_item_D9_supplier", order = 4, isRequire = true)
    private String                  supplier;
    @TCPropName(value = "bl_item_D9_supplierPN", order = 5, isRequire = true)
    private String                  supplierPN;
    @TCPropName(value = "bl_quantity", isKey = false, order = 6)
    private String                  qty;
    @TCPropName(value = "bl_ref_designator", isKey = false, isProcessField = true, order = 7)
    private String                  location;
    @TCPropName(value = "D9_CCL", order = 8, isRequire = true)
    private String                  ccl;
    @TCPropName(isKey = false, order = 9)
    private String                  remark;
    @TCPropName(value = "bl_occ_d9_BOM", order = 10, isRequire = true)
    private String                  bom;
    @TCPropName(value = "bl_item_D9_pkgtype", order = 11, isRequire = true)
    private String                  packageType;
    @TCPropName(value = "bl_occ_d9_Side", order = 12, isRequire = true)
    private String                  side;
    @TCPropName(value = "bl_item_D9_subsystem", order = 13, isRequire = true)
    private String                  subSystem;
    @TCPropName(value = "bl_item_D9_function", order = 14)
    private String                  function;
    private TCComponentItemRevision itemRevisison;

    public String getHhPN()
    {
        return hhPN;
    }

    public void setHhPN(String hhPN)
    {
        this.hhPN = hhPN;
    }

    public String getStdPN()
    {
        return stdPN;
    }

    public void setStdPN(String stdPN)
    {
        this.stdPN = stdPN;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getSupplier()
    {
        return supplier;
    }

    public void setSupplier(String supplier)
    {
        this.supplier = supplier;
    }

    public String getSupplierPN()
    {
        return supplierPN;
    }

    public void setSupplierPN(String supplierPN)
    {
        this.supplierPN = supplierPN;
    }

    public String getQty()
    {
        return qty;
    }

    public void setQty(String qty)
    {
        this.qty = qty;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getCcl()
    {
        return ccl;
    }

    public void setCcl(String ccl)
    {
        this.ccl = ccl;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getBom()
    {
        return bom;
    }

    public void setBom(String bom)
    {
        this.bom = bom;
    }

    public String getPackageType()
    {
        return packageType;
    }

    public void setPackageType(String packageType)
    {
        this.packageType = packageType;
    }

    public String getSide()
    {
        return side;
    }

    public void setSide(String side)
    {
        this.side = side;
    }

    public String getSubSystem()
    {
        return subSystem;
    }

    public void setSubSystem(String subSystem)
    {
        this.subSystem = subSystem;
    }

    public String getFunction()
    {
        return function;
    }

    public void setFunction(String function)
    {
        this.function = function;
    }

    public TCComponentItemRevision getItemRevisison()
    {
        return itemRevisison;
    }

    public void setItemRevisison(TCComponentItemRevision itemRevisison)
    {
        this.itemRevisison = itemRevisison;
    }

    public String toString()
    {
        return this.stdPN + " @bom: " + this.bom + " @side: " + this.side + " @packageType: " + this.packageType + " @location:" + this.location;
    }
}
