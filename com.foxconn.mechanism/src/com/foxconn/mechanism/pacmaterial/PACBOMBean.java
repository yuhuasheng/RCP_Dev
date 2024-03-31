package com.foxconn.mechanism.pacmaterial;

import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class PACBOMBean {
	
	public boolean isTop;
	public String uid;
	public String level;
	public String pn;
	public String partType;
	public String description;
	public String qty;
	public String unit;
	public String remark;
	public String actualUser;
	public String initialProject;
	public String objectName;
	public boolean isChange;
	public boolean isReleased;
	public TCComponentItemRevision itemRevision;
	
	@Override
	public String toString() {
		return "PACBOMBean [level=" + level + ", pn=" + pn + ", partType=" + partType + ", description=" + description
				+ ", qty=" + qty + ", unit=" + unit + ", remark=" + remark + "]";
	}

}
