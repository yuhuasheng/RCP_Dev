package com.foxconn.electronics.managementebom.secondsource.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.foxconn.electronics.managementebom.export.bom.mnt.domain.SourceConstant;
import com.foxconn.electronics.managementebom.secondsource.util.EBOMTreeTools;
import com.foxconn.electronics.util.CommonTools;
import com.foxconn.electronics.util.TCPropName;
import com.google.gson.annotations.Expose;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;

public class Sync2ndSourceInfo {

	@Expose(serialize = false, deserialize = false)
	private Sync2ndSourceInfo parent;
	private List<Sync2ndSourceInfo> childs; // 子对象集合
	private List<Sync2ndSourceInfo> substitutesList; // 替代料集合
	
	@TCPropName("bl_sequence_no")
	private Integer findNum;

	private String parentItem;
	
	@TCPropName("item_id")
	private String item;
	
	@TCPropName("item_revision_id")
	private String version;
	
	@TCPropName("d9_EnglishDescription")
	private String description;
	
	@TCPropName("d9_DescriptionSAP")
    private String sapDescription;

	@TCPropName("d9_ManufacturerID")
	private String mfg;

	@TCPropName("d9_ManufacturerPN")
	private String mfgPn;

	@TCPropName("bl_occ_d9_Location")
	private String location;

	@TCPropName("bl_occ_d9_AltGroup")
	private String alternativeGroup;
	
	private String alternativeCode;

	private Integer usageProb;
	
	@TCPropName("d9_MaterialGroup")
	private String materialGroup;
	
	@TCPropName("d9_MaterialType")
	private String materialType;	
	
	@TCPropName(value = "d9_ProcurementMethods")
    private String procurementType;	
	
	@TCPropName(value = "bl_quantity")
	private String qty;	
	
	@TCPropName(value = "d9_Un")
	private String unit;
	
	@TCPropName(value = "release_status_list")
	private String status;
	
	@TCPropName(value = "bl_occ_d9_ReferenceDimension")
    private String referenceDimension;
	
	@TCPropName(value = "d9_SAPRev")
	private String sapRev;

	@TCPropName(value = "bl_occ_d9_PackageType")
    private String  packageType;
    @TCPropName(value = "bl_occ_d9_Side")
    private String side;
    
	@TCPropName(value = "d9_SupplierZF")
    private String supplierZF;
	
	private Boolean isSub = false; // 判断是否为替代料
	
	private Boolean checkStates = false; // 判断是否为选中状态
	
	private String sourceSystem = SourceConstant.TC; // 物料是否已经存在TC中,默认设置为存在TC中
	
	private String itemRevUid;

	private String bomLineUid;	
	 
	private String verNote = ""; // 当前替代料父对象版次说明
	
	private Boolean subExistBom = true; // 替代料是否存在BOMLine中，默认替代料存在BOMLine中
	
	private String materialGroupItemId = ""; // 替代料群组ItemID 
	
	private Boolean syncCheckFlag = false; // 用作是否发生同步的标识
	
	private Boolean enable = true; // 默认设置为可以右键和取消勾选框	
	
	private Boolean isDelete = false; // 判断是否需要删除，默认为不需要删除
	
	private Boolean hasMerge = false; // 用量和是否发生合并, 默认为没有发生合并
	
//	private Boolean hasModify = false; // 判断是否发生改变  默认为没有发生改变
	
	public Sync2ndSourceInfo() {
		childs = new CopyOnWriteArrayList<Sync2ndSourceInfo>(); // 写时复制的技术，读写分离的思想来实现
	}

	public Sync2ndSourceInfo(TCComponentBOMLine bomLine) {
//		childs = new ArrayList<Sync2ndSourceInfo>();
//		childs = Collections.synchronizedList(new ArrayList<Sync2ndSourceInfo>());
		childs = new CopyOnWriteArrayList<Sync2ndSourceInfo>(); // 写时复制的技术，读写分离的思想来实现
		try {
			EBOMTreeTools.tcPropMapping(this, bomLine);
		} catch (IllegalArgumentException | IllegalAccessException | TCException e) {
			e.printStackTrace();
		}
	}

	public Sync2ndSourceInfo getParent() {
		return parent;
	}

	public void setParent(Sync2ndSourceInfo parent) {
		this.parent = parent;
	}

	public List<Sync2ndSourceInfo> getChilds() {
		return childs;
	}

	public void addChild(Sync2ndSourceInfo child) {
		this.childs.add(child);
	}

	public List<Sync2ndSourceInfo> getSubstitutesList() {
		return substitutesList;
	}

	public void setSubstitutesList(List<Sync2ndSourceInfo> substitutesList) {
		this.substitutesList = substitutesList;
	}	

	public Integer getFindNum() {
		return findNum;
	}

	public void setFindNum(Integer findNum) {
		this.findNum = findNum;
	}

	
	public String getParentItem() {
		return parentItem;
	}

	public void setParentItem(String parentItem) {
		this.parentItem = parentItem;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMfg() {
		return mfg;
	}

	public void setMfg(String mfg) {
		this.mfg = mfg;
	}	

	public String getMfgPn() {
		return mfgPn;
	}

	public void setMfgPn(String mfgPn) {
		this.mfgPn = mfgPn;
	}

	
	public String getProcurementType() {
		return procurementType;
	}

	public void setProcurementType(String procurementType) {
		this.procurementType = procurementType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAlternativeGroup() {
		return alternativeGroup;
	}

	public void setAlternativeGroup(Integer altGroup) {			
		if (CommonTools.isNotEmpty(altGroup)) {
			if (altGroup == 0) {
				this.alternativeGroup = "";
//				System.out.println("==>> bl_item_item_id： " + item + ", 无需设置Alternative Group值");
			} else {
				if (String.valueOf(altGroup).length() == 1) {
					this.alternativeGroup = "0" + altGroup;
				} else {
					this.alternativeGroup = String.valueOf(altGroup);
				}
			}
			
		}
	}	
	
	
	public void setAlternativeGroupNew(String altGroup) {
		this.alternativeGroup = altGroup;
	}
	
	
	
	public String getAlternativeCode() {
		return alternativeCode;
	}

	public void setAlternativeCode(String alternativeCode) {
		this.alternativeCode = alternativeCode;
	}

	
	public Integer getUsageProb() {
		return usageProb;
	}

	public void setUsageProb(Integer usageProb) {
		this.usageProb = usageProb;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}	
	
	public String getMaterialGroup() {
		return materialGroup;
	}

	public void setMaterialGroup(String materialGroup) {
		this.materialGroup = materialGroup;
	}

	public String getMaterialType() {
		return materialType;
	}

	public void setMaterialType(String materialType) {
		this.materialType = materialType;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Boolean getIsSub() {
		return isSub;
	}

	public void setIsSub(Boolean isSub) {
		this.isSub = isSub;
	}

	public Boolean getCheckStates() {
		return checkStates;
	}

	public void setCheckStates(Boolean checkStates) {
		this.checkStates = checkStates;
	}	

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getItemRevUid() {
		return itemRevUid;
	}

	public void setItemRevUid(String itemRevUid) {
		this.itemRevUid = itemRevUid;
	}

	public String getBomLineUid() {
		return bomLineUid;
	}

	public void setBomLineUid(String bomLineUid) {
		this.bomLineUid = bomLineUid;
	}	

	public String getVerNote() {
		return verNote;
	}

	public void setVerNote(String verNote) {
		this.verNote = verNote;
	}

	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	public Boolean getSubExistBom() {
		return subExistBom;
	}

	public void setSubExistBom(Boolean subExistBom) {
		this.subExistBom = subExistBom;
	}	
	

	public String getMaterialGroupItemId() {
		return materialGroupItemId;
	}

	public void setMaterialGroupItemId(String materialGroupItemId) {
		this.materialGroupItemId = materialGroupItemId;
	}

	
	public String getReferenceDimension() {
		return referenceDimension;
	}

	public void setReferenceDimension(String referenceDimension) {
		this.referenceDimension = referenceDimension;
	}
	
	public String getSapDescription() {
		return sapDescription;
	}

	public void setSapDescription(String sapDescription) {
		this.sapDescription = sapDescription;
	}

	
	public String getSapRev() {
		return sapRev;
	}

	public void setSapRev(String sapRev) {
		this.sapRev = sapRev;
	}

	
	public String getSupplierZF() {
		return supplierZF;
	}

	public void setSupplierZF(String supplierZF) {
		this.supplierZF = supplierZF;
	}

	
	
	public Boolean getSyncCheckFlag() {
		return syncCheckFlag;
	}

	public void setSyncCheckFlag(Boolean syncCheckFlag) {
		this.syncCheckFlag = syncCheckFlag;
	}
	
	
	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public String getSide() {
		return side;
	}

	public void setSide(String side) {
		this.side = side;
	}
	
	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
	
	public Boolean getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Boolean isDelete) {
		this.isDelete = isDelete;
	}

	
//	public Boolean getHasModify() {
//		return hasModify;
//	}
//
//	public void setHasModify(Boolean hasModify) {
//		this.hasModify = hasModify;
//	}

	public Boolean getHasMerge() {
		return hasMerge;
	}

	public void setHasMerge(Boolean hasMerge) {
		this.hasMerge = hasMerge;
	}

	@Override
	public boolean equals(Object var1) {
		if (var1 instanceof Sync2ndSourceInfo) {
			Sync2ndSourceInfo other = (Sync2ndSourceInfo) var1;
			if (CommonTools.isNotEmpty(this.bomLineUid) && CommonTools.isNotEmpty(other.getBomLineUid())) {
				return this.bomLineUid.equals(other.getBomLineUid());
			}
		}
		return super.equals(var1);
	}

	public String toString() {
		return this.bomLineUid + " " + this.getFindNum() + "   " + this.getItem() + "  " + this.getLocation();
	}
	
	
}
