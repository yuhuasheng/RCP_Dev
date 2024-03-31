package com.foxconn.electronics.managementebom.updatebom.domain;

import java.util.List;

public class EBOMChangeBean
{
    private List<EBOMLineBean> adds;
    private List<EBOMLineBean> dels;
    private List<EBOMLineBean> changes;

    public List<EBOMLineBean> getAdds()
    {
        return adds;
    }

    public void setAdds(List<EBOMLineBean> adds)
    {
        this.adds = adds;
    }

    public List<EBOMLineBean> getDels()
    {
        return dels;
    }

    public void setDels(List<EBOMLineBean> dels)
    {
        this.dels = dels;
    }

    public List<EBOMLineBean> getChanges()
    {
        return changes;
    }

    public void setChanges(List<EBOMLineBean> changes)
    {
        this.changes = changes;
    }
}
