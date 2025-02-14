package com.foxconn.electronics.matrixbom.domain;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.foxconn.electronics.dcnreport.dcncostimpact.util.TCPropertes;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class ProductLineBOMBean  implements IMatixBOMBean{
	
	private List<ProductLineBOMBean> child; // 子集合
	private List<ProductLineBOMBean> subList; // 替代料集合
	private boolean sub = false;
	private String No;
	private String lineId;
	private String itemRevUid;
	private String productLineItemUID;
	private String errorMsg;
    private String itemImgPath;
	private boolean released=false;
	
	private String base64Str;
	private String imgName;
	private String uom;
	private boolean bomEnabled = true;
	private boolean itemEnabled;
	private Boolean isModifyItem;
	
	private String modifyKey;
	
	
	public Boolean getIsModifyItem() {
		return isModifyItem == null ? false:isModifyItem;
	}

	public void setIsModifyItem(Boolean isModifyItem) {
		this.isModifyItem = isModifyItem;
	}

	public String getNo() {
		return No;
	}

	public void setNo(String no) {
		No = no;
	}

	public String getModifyKey() {
		return modifyKey==null?"":modifyKey;
	}

	public void setModifyKey(String modifyKey) {
		this.modifyKey = modifyKey;
	}

	
	public String getUom() {
		return uom==null?"":uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public boolean isBomEnabled() {
		return bomEnabled;
	}

	public void setBomEnabled(boolean bomEnabled) {
		this.bomEnabled = bomEnabled;
	}

	public boolean isItemEnabled() {
		return itemEnabled;
	}

	public void setItemEnabled(boolean itemEnabled) {
		this.itemEnabled = itemEnabled;
	}

	public String getQty() {
		return qty==null?"":qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public boolean isReleased() {
		return released;
	}

	public void setReleased(boolean released) {
		this.released = released;
	}

	public boolean isSub() {
		return sub;
	}

	public void setSub(boolean sub) {
		this.sub = sub;
	}
	
	public String getBase64Str() {
		return base64Str;
	}

	public void setBase64Str(String base64Str) {
		this.base64Str = base64Str;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	@TCPropertes(tcProperty = "bl_sequence_no")
	private Integer sequence_no;
	public Integer getSequence_no() {
		return sequence_no;
	}

	public void setSequence_no(Integer sequence_no) {
		this.sequence_no = sequence_no;
	}

	@TCPropertes(tcProperty = "bl_occ_fnd0objectId")
	private String bomLineUid;
	
	@TCPropertes(tcProperty = "bl_occ_d9_Remark")
	private String remark;
	
	@TCPropertes(tcProperty = "bl_occ_d9_Plant")
	private String plant;
	
	@TCPropertes(tcProperty = "bl_occ_d9_Category")
	private String category;
	
	@TCPropertes(tcProperty = "bl_occ_d9_TorqueIn")
	private String torqueIn;
	
	@TCPropertes(tcProperty = "bl_occ_d9_TorqueOut")
	private String torqueOut;
	
	@TCPropertes(tcProperty = "bl_occ_d9_ProgramName")
	private String programName;
	
	@TCPropertes(tcProperty = "bl_quantity")
	private String qty;
	
	@TCPropertes(tcProperty = "item_id")
	private String itemId;
	
	@TCPropertes(tcProperty = "item_revision_id")
	private String itemRevision;
	
	@TCPropertes(tcProperty = "object_name")
	private String itemName;
	
	@TCPropertes(tcProperty = "object_type")
	private String itemType;
	
	@TCPropertes(tcProperty = "object_desc")
	private String itemDescription;
	
	
	@TCPropertes(tcProperty = "d9_CustomerPN")
	private String customerPN;
	
	@TCPropertes(tcProperty = "d9_ManufacturerID")
	private String manufacturerID;
	
	@TCPropertes(tcProperty = "d9_ManufacturerPN")
	private String manufacturerPN;
	
	@TCPropertes(tcProperty = "d9_EnglishDescription")
	private String englishDescription;
	
	@TCPropertes(tcProperty = "d9_Un")
	private String un;
	
	@TCPropertes(tcProperty = "d9_SupplierZF")
	private String supplierZF;
	
	@TCPropertes(tcProperty = "d9_FRUPN")
	private String frupn;
	
	@TCPropertes(tcProperty = "d9_CoolerFanVendor")
	private String coolerFanVendor;
	
	@TCPropertes(tcProperty = "d9_CoolerFanModelNo")
	private String coolerFanModelNo;
	
	@TCPropertes(tcProperty = "d9_Chassis")
	private String chassis;
	
	@TCPropertes(tcProperty = "d9_Rating")
	private String rating;
	
	@TCPropertes(tcProperty = "d9_Type")
	private String type;
	
	@TCPropertes(tcProperty = "d9_MeetTCO90")
	private String meetTCO90;
	
	@TCPropertes(tcProperty = "d9_ThermalType")
	private String thermalType;
	
	@TCPropertes(tcProperty = "d9_AcknowledgementRev")
	private String acknowledgementRev;
	
	@TCPropertes(tcProperty = "d9_DescriptionSAP")
	private String descriptionSAP;
	
	@TCPropertes(tcProperty = "bl_uom")
	private String bl_uom;
	
	@TCPropertes(tcProperty = "bl_occ_d9_IsNew")
	private String isNew;
	
	@TCPropertes(tcProperty = "d9_SAPRev")
	private String sAPRev;
	
	@TCPropertes(tcProperty = "d9_ChineseDescription")
	private String chineseDescription;
	
	@TCPropertes(tcProperty = "d9_MaterialGroup")
	private String materialGroup;
	
	@TCPropertes(tcProperty = "d9_MaterialType")
	private String materialType;
	
	@TCPropertes(tcProperty = "d9_ProcurementMethods")
	private String procurementMethods;
	
	@TCPropertes(tcProperty = "owning_user")
	private String owning_user;
	
	@TCPropertes(tcProperty = "bl_substitute_list")
	private String substitute_list;
	
	private boolean hasChildren;
	
	
	public boolean getHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	public String getSubstitute_list() {
		return substitute_list;
	}

	public void setSubstitute_list(String substitute_list) {
		this.substitute_list = substitute_list;
	}

	public String getOwning_user() {
		return owning_user;
	}

	public void setOwning_user(String owning_user) {
		this.owning_user = owning_user;
	}

	public String getDescriptionSAP() {
		return descriptionSAP;
	}

	public void setDescriptionSAP(String descriptionSAP) {
		this.descriptionSAP = descriptionSAP;
	}

	public String getBl_uom() {
		return bl_uom;
	}

	public void setBl_uom(String bl_uom) {
		this.bl_uom = bl_uom;
	}

	public String getIsNew() {
		return isNew==null?"":isNew;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}

	public String getsAPRev() {
		return sAPRev;
	}

	public void setsAPRev(String sAPRev) {
		this.sAPRev = sAPRev;
	}

	public String getChineseDescription() {
		return chineseDescription;
	}

	public void setChineseDescription(String chineseDescription) {
		this.chineseDescription = chineseDescription;
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

	public String getProcurementMethods() {
		return procurementMethods;
	}

	public void setProcurementMethods(String procurementMethods) {
		this.procurementMethods = procurementMethods;
	}

	public String getAcknowledgementRev() {
		return acknowledgementRev;
	}

	public void setAcknowledgementRev(String acknowledgementRev) {
		this.acknowledgementRev = acknowledgementRev;
	}

	public String getThermalType() {
		return thermalType;
	}

	public void setThermalType(String thermalType) {
		this.thermalType = thermalType;
	}

	public String getItemRevUid() {
		return itemRevUid;
	}

	public void setItemRevUid(String itemRevUid) {
		this.itemRevUid = itemRevUid;
	}

	public String getProductLineItemUID() {
		return productLineItemUID;
	}

	public void setProductLineItemUID(String productLineItemUID) {
		this.productLineItemUID = productLineItemUID;
	}

	public String getRemark() {
		return remark==null?"":remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPlant() {
		return plant==null?"":plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}

	public String getTorqueIn() {
		return torqueIn==null?"":torqueIn;
	}

	public void setTorqueIn(String torqueIn) {
		this.torqueIn = torqueIn;
	}

	public String getTorqueOut() {
		return torqueOut==null?"":torqueOut;
	}

	public void setTorqueOut(String torqueOut) {
		this.torqueOut = torqueOut;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getCustomerPN() {
		return customerPN;
	}

	public void setCustomerPN(String customerPN) {
		this.customerPN = customerPN;
	}

	public String getManufacturerID() {
		return manufacturerID;
	}

	public void setManufacturerID(String manufacturerID) {
		this.manufacturerID = manufacturerID;
	}

	public String getManufacturerPN() {
		return manufacturerPN;
	}

	public void setManufacturerPN(String manufacturerPN) {
		this.manufacturerPN = manufacturerPN;
	}

	public String getEnglishDescription() {
		return englishDescription;
	}

	public void setEnglishDescription(String englishDescription) {
		this.englishDescription = englishDescription;
	}

	public String getUn() {
		return un;
	}

	public void setUn(String un) {
		this.un = un;
	}

	public String getSupplierZF() {
		return supplierZF;
	}

	public void setSupplierZF(String supplierZF) {
		this.supplierZF = supplierZF;
	}

	public String getFrupn() {
		return frupn;
	}

	public void setFrupn(String frupn) {
		this.frupn = frupn;
	}

	public String getProgramName() {
		return programName==null?"":programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}


	public String getCoolerFanVendor() {
		return coolerFanVendor;
	}

	public void setCoolerFanVendor(String coolerFanVendor) {
		this.coolerFanVendor = coolerFanVendor;
	}

	public String getCoolerFanModelNo() {
		return coolerFanModelNo;
	}

	public void setCoolerFanModelNo(String coolerFanModelNo) {
		this.coolerFanModelNo = coolerFanModelNo;
	}

	public String getChassis() {
		return chassis;
	}

	public void setChassis(String chassis) {
		this.chassis = chassis;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMeetTCO90() {
		return meetTCO90;
	}

	public void setMeetTCO90(String meetTCO90) {
		this.meetTCO90 = meetTCO90;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getCategory() {
		return category==null?"":category;
	}

	public void setCategory(String category) {
		this.category = category;
	}



	public ProductLineBOMBean() {
		super();		
	}

	public static <T> T tcPropItemMapping(T bean, TCComponentItemRevision itemRev)
			throws TCException, IllegalArgumentException, IllegalAccessException {	
		if (bean != null && itemRev != null) {			
			Field[] fields = bean.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				TCPropertes tcpropertes = fields[i].getAnnotation(TCPropertes.class);
				if (tcpropertes != null) {
					String tcAttrName = tcpropertes.tcProperty();
					if (!tcAttrName.isEmpty()) {
						Object value = "";
						if (tcAttrName.startsWith("bl")) {
							//value = tcbomLine.getProperty(tcAttrName);
						} else {
							if(tcAttrName.equalsIgnoreCase("d9_MeetTCO90")) {
								value = itemRev.getProperty(tcAttrName);
								if("是".equals(value)) {
									value = "Y";
								}else if("否".equals(value)) {
									value = "N";
								}
							}else {
								value = itemRev.getProperty(tcAttrName);
							}
							
						}
						if (fields[i].getType() == Integer.class) {
							if (value.equals("") || value == null) {
								value = null;
							} else {
								String match = "[0-9]{2,6}";
								if (!((String) value).matches(match)) {
									value = null;
								}else {
									value = Integer.parseInt((String) value);
								}
							}
						}
						fields[i].set(bean, value);
					}
				}
			}
		}	
		return bean;
	}
	
	public static <T> T tcPropMapping(T bean, TCComponentBOMLine tcbomLine)
			throws TCException, IllegalArgumentException, IllegalAccessException {	
		if (bean != null && tcbomLine != null) {			
			TCComponentItemRevision itemRev = tcbomLine.getItemRevision();
			Field[] fields = bean.getClass().getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				TCPropertes tcpropertes = fields[i].getAnnotation(TCPropertes.class);
				if (tcpropertes != null) {
					String tcAttrName = tcpropertes.tcProperty();
					if (!tcAttrName.isEmpty()) {
						Object value = "";
						if (tcAttrName.startsWith("bl")) {
							value = tcbomLine.getProperty(tcAttrName);
						} else {
							if(tcAttrName.equalsIgnoreCase("d9_MeetTCO90")) {
								value = itemRev.getProperty(tcAttrName);
								if("是".equals(value)) {
									value = "Y";
								}else if("否".equals(value)) {
									value = "N";
								}
							}else {
								value = itemRev.getProperty(tcAttrName);
							}
						}
						if (fields[i].getType() == Integer.class) {
							if (value.equals("") || value == null) {
								value = null;
							} else {
								String match = "[0-9]{2,6}";
								if (!((String) value).matches(match)) {
									value = null;
								}else {
									value = Integer.parseInt((String) value);
								}
							}
						}
						fields[i].set(bean, value);
					}
				}
			}
		}	
		return bean;
	}	
	
	public ProductLineBOMBean(TCComponentBOMLine bomLine,TCSession session) {
		try {
			child = new CopyOnWriteArrayList<ProductLineBOMBean>();
			tcPropMapping(this, bomLine);
			TCComponentItemRevision itemRev = bomLine.getItemRevision();
			this.setItemRevUid(itemRev.getUid());
			this.setLineId(CommonTools.md5Encode(this.getItemId()+this.getCategory().trim()+this.getPlant()));
			this.setReleased(TCUtil.isReleased(itemRev));
			boolean itemWrite = TCUtil.checkOwninguserisWrite(session, itemRev);
			this.setItemEnabled(itemWrite);
			String uom = itemRev.getItem().getProperty("uom_tag");
			this.setUom(uom);
			
			AIFComponentContext[] children = bomLine.getChildren();
			if(children!=null && children.length > 0 ) {
				this.setHasChildren(true);
			}else {
				this.setHasChildren(false);
			}
			
			
			
//			权限判断
//			if(!itemWrite) {
//				String item_id = itemRev.getProperty("item_id");
//				System.out.println("没有权限 item_id = "+item_id);
//			}
//			TCComponentBOMLine praentBom = bomLine.getCachedParent();
//			if(praentBom != null) {
//				boolean bomWrite = TCUtil.checkOwninguserisWrite(session, praentBom.getItemRevision());
//				if(!bomWrite) {
//					String item_id = praentBom.getProperty("bl_item_item_id");
//					String item_id1 = itemRev.getProperty("item_id");
//					System.out.println("没有权限的 父item_id = "+item_id+",item_id1 = "+item_id1);
//				}
//				this.setBomEnabled(itemWrite);
//			}
			

			
//			TCComponent[] components = itemRev.getRelatedComponents("IMAN_specification");
//			for (TCComponent tcComponent : components) {
//				if (!(tcComponent instanceof TCComponentDataset)) {
//					continue;
//				}
//				String uid=tcComponent.getUid();
//				String url=TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
//				itemImgPath=url+"/tc-hdfs/downloadFile?site=WH&refId="+uid;
//			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	public void setChild(List<ProductLineBOMBean> child) {
		this.child = child;
	}
	
	
	public List<ProductLineBOMBean> getChild() {
		return child;
	}

	
	public void addChilds(ProductLineBOMBean child) {
		this.child.add(child);
	}
	
	public List<ProductLineBOMBean> getSubList() {
		return subList;
	}

	public void setSubList(List<ProductLineBOMBean> subList) {
		this.subList = subList;
	}



	public String getBomLineUid() {
		return bomLineUid;
	}

	public void setBomLineUid(String bomLineUid) {
		this.bomLineUid = bomLineUid;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemRevision() {
		return itemRevision;
	}

	public void setItemRevision(String itemRevision) {
		this.itemRevision = itemRevision;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}


	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getItemImgPath() {
		return itemImgPath;
	}

	public void setItemImgPath(String itemImgPath) {
		this.itemImgPath = itemImgPath;
	}


}
