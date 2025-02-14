package com.foxconn.savedcnchange.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.foxconn.plm.tcapi.utils.CommonTools;
import com.foxconn.savedcnchange.handler.SaveDCNChangeHandler;
import com.foxconn.savedcnchange.serial.SerialCloneable;
import com.foxconn.savedcnchange.util.TCPropertes;
import com.teamcenter.services.strong.core.DataManagementService;
import com.teamcenter.soa.client.model.strong.BOMLine;

public class BOMChangeBean extends SerialCloneable implements Comparable<BOMChangeBean> {	
	
	private static final long serialVersionUID = 1L;	
	
	private String dcnItemId;	
	
	private String parentUid;
	
	@TCPropertes(tcProperty = "item_id", type = "parentItemRev")
	private String parentId;
	
	@TCPropertes(tcProperty = "item_revision_id", type = "parentItemRev")
	private String parentVersion;
	
	@TCPropertes(tcProperty = "bl_sequence_no")
	private Integer findNum; 		
	
	private String childUid;	
	
	@TCPropertes(tcProperty = "item_id", type = "childItemRev")	
	private String childItemId;
	
	@TCPropertes(tcProperty = "item_revision_id", type = "childItemRev") 
	private String childVersion;	
	
	private String bomUsage;
	
	private String alternativeBOM;
	
	private String basicUnit;
	
	@TCPropertes(tcProperty = "bl_occ_d9_ReferenceDimension")
	private String referenceDimension;
	
	private String itemCategory;
	
	@TCPropertes(tcProperty = "bl_quantity")
	private String qty;
	
	@TCPropertes(tcProperty = "d9_Un")
	private String unit;	
	
	private String sortString;
	
	@TCPropertes(tcProperty = "bl_occ_d9_AltGroup")
	private String altItemGroup;
	
	private String priority;
	
	private String strategy;
	
	private String usageProb;
	
	@TCPropertes(tcProperty = "bl_occ_d9_Location")
	private String location;
	
	@TCPropertes(tcProperty = "d9_SAPRev")
	private String sapRev; // SAP�汾	
	
	private List<BOMChangeBean> childs; // �Ӷ��󼯺�
	
	private List<BOMChangeBean> substitutesList; // ����ϼ���
	
	
	public BOMChangeBean() {
		
	}
	
	public BOMChangeBean(DataManagementService dmService, BOMLine bomLine, boolean isTopLine) {
		try {
			if (isTopLine) {
				childs = Collections.synchronizedList(new ArrayList<BOMChangeBean>());
			}
			SaveDCNChangeHandler.tcPropMapping(dmService, this, bomLine, isTopLine);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}		

	public String getDcnItemId() {
		return dcnItemId;
	}

	public void setDcnItemId(String dcnItemId) {
		this.dcnItemId = dcnItemId;
	}

	public String getParentUid() {
		return parentUid;
	}

	public void setParentUid(String parentUid) {
		this.parentUid = parentUid;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getParentVersion() {
		return parentVersion;
	}

	public void setParentVersion(String parentVersion) {
		this.parentVersion = parentVersion;
	}
	

	public Integer getFindNum() {
		return findNum;
	}

	public void setFindNum(Integer findNum) {
		this.findNum = findNum;
	}

	public String getChildUid() {
		return childUid;
	}

	public void setChildUid(String childUid) {
		this.childUid = childUid;
	}

	public String getChildItemId() {
		return childItemId;
	}

	public void setChildItemId(String childItemId) {
		this.childItemId = childItemId;
	}

	public String getChildVersion() {
		return childVersion;
	}

	public void setChildVersion(String childVersion) {
		this.childVersion = childVersion;
	}

	public String getBomUsage() {
		return bomUsage;
	}

	public void setBomUsage(String bomUsage) {
		this.bomUsage = bomUsage;
	}

	public String getAlternativeBOM() {
		return alternativeBOM;
	}

	public void setAlternativeBOM(String alternativeBOM) {
		this.alternativeBOM = alternativeBOM;
	}

	public String getBasicUnit() {
		return basicUnit;
	}

	public void setBasicUnit(String basicUnit) {
		this.basicUnit = basicUnit;
	}

	public String getReferenceDimension() {
		return referenceDimension;
	}

	public void setReferenceDimension(String referenceDimension) {
		this.referenceDimension = referenceDimension;
	}

	public String getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getSortString() {
		return sortString;
	}

	public void setSortString(String sortString) {
		this.sortString = sortString;
	}

	public String getAltItemGroup() {
		return altItemGroup;
	}

	public void setAltItemGroup(String altItemGroup) {
		this.altItemGroup = altItemGroup;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	public String getUsageProb() {
		return usageProb;
	}

	public void setUsageProb(String usageProb) {
		this.usageProb = usageProb;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<BOMChangeBean> getChilds() {
		return childs;
	}

	public void addChilds(BOMChangeBean child) {
		this.childs.add(child);
	}

	public List<BOMChangeBean> getSubstitutesList() {
		return substitutesList;
	}

	public void setSubstitutesList(List<BOMChangeBean> substitutesList) {
		this.substitutesList = substitutesList;
	}
	
	
	public String getSapRev() {
		return sapRev;
	}

	public void setSapRev(String sapRev) {
		this.sapRev = sapRev;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BOMChangeBean) {
			BOMChangeBean other = (BOMChangeBean) obj;
			if (CommonTools.isNotEmpty(this.parentUid) && CommonTools.isNotEmpty(other.getParentUid())) {
				return this.parentUid.equals(other.getParentUid());
			}
		}
		return super.equals(obj);
	}

	@Override
	public int compareTo(BOMChangeBean o) {
		int i = this.dcnItemId.compareTo(o.getDcnItemId());
		if (i == 0) {
			int j = this.parentId.compareTo(o.getParentId());
			if (j == 0) {
				return this.childItemId.compareTo(o.getChildItemId());
			} else {
				return j;
			}
		}
		return i;
	}
}
