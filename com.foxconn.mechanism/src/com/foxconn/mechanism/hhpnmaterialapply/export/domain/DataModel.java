package com.foxconn.mechanism.hhpnmaterialapply.export.domain;

import com.teamcenter.rac.kernel.TCComponentItemRevision;

/**
 * 
 * @author Robert
 *
 */
public interface DataModel extends Cloneable
{
    public TCComponentItemRevision getItemRevision();

    public void setItemRevision(TCComponentItemRevision itemResion);

    public String getUsage();

    public void setUsage(String usage);

    public Object clone() throws CloneNotSupportedException;
}
