package com.foxconn.electronics.pamatrixbom.domain;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.foxconn.electronics.dcnreport.dcncostimpact.util.TCPropertes;
import com.foxconn.electronics.matrixbom.domain.IMatixBOMBean;
import com.foxconn.electronics.pamatrixbom.service.PAMatrixBOMService;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class PAProductLineBOMBean  implements IMatixBOMBean{
	
	private List<PAProductLineBOMBean> child; // 子集合
	private List<PAProductLineBOMBean> subList; // 替代料集合
	private boolean sub = false;
	private String lineId;
	private String itemRevUid;
	private String productLineItemUID;
	private boolean released=false;
	private boolean bomEnabled = true;
	private boolean itemEnabled;
	private Boolean isModifyItem;
	private String modifyKey;
	private boolean hasChildren;
	private String errorMsg;
	
	public static String[] bomgetPro = new String[] {"bl_quantity","bl_sequence_no","bl_occ_fnd0objectId","bl_uom","bl_occ_d9_IsNew",
			"bl_substitute_list","bl_occ_d9_Remark","bl_occ_d9_BOMTemp"};
	public static String[] itemgetPro = new String[] {"item_id","item_revision_id","d9_EnglishDescription","d9_DescriptionSAP","d9_Un",
			"d9_SupplierZF","d9_ManufacturerID","d9_ManufacturerPN","d9_AcknowledgementRev","d9_SAPRev",
			"d9_ChineseDescription","d9_MaterialGroup","d9_MaterialType","d9_ProcurementMethods","owning_user",
			"object_type","d9_ActualUserID"};
	
	public static String[] bomsetPro = new String[] { "bl_occ_d9_Remark", "bl_sequence_no", "bl_quantity", "bl_occ_d9_IsNew", "bl_occ_d9_BOMTemp" };
	
	
	@TCPropertes(tcProperty = "item_id")
	private String itemId;
	@TCPropertes(tcProperty = "item_revision_id")
	private String itemRevision;
	@TCPropertes(tcProperty = "d9_EnglishDescription")
	private String englishDescription;
	@TCPropertes(tcProperty = "d9_DescriptionSAP")
	private String descriptionSAP;
	@TCPropertes(tcProperty = "d9_Un")
	private String un;
	@TCPropertes(tcProperty = "d9_SupplierZF")
	private String supplierZF;
	@TCPropertes(tcProperty = "d9_ManufacturerID")
	private String manufacturerID;
	@TCPropertes(tcProperty = "d9_ManufacturerPN")
	private String manufacturerPN;
	@TCPropertes(tcProperty = "d9_AcknowledgementRev")
	private String acknowledgementRev;
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
	@TCPropertes(tcProperty = "object_type")
	private String itemType;
	@TCPropertes(tcProperty = "d9_ActualUserID")
	private String actualUserID;
	
	
	@TCPropertes(tcProperty = "bl_quantity")
	private String qty;
	@TCPropertes(tcProperty = "bl_sequence_no")
	private Integer sequence_no;
	@TCPropertes(tcProperty = "bl_occ_fnd0objectId")
	private String bomLineUid;
	@TCPropertes(tcProperty = "bl_uom")
	private String bl_uom;
	@TCPropertes(tcProperty = "bl_occ_d9_IsNew")
	private String isNew;
	@TCPropertes(tcProperty = "bl_substitute_list")
	private String substitute_list;
	@TCPropertes(tcProperty = "bl_occ_d9_Remark")
	private String remark;
	@TCPropertes(tcProperty = "bl_occ_d9_BOMTemp")
	private String d9_BOMTemp;
	
	
	private String sap_rev;
	
	public String getSap_rev() {
		return sap_rev;
	}

	public void setSap_rev(String sap_rev) {
		this.sap_rev = sap_rev;
	}

	public String getActualUserID() {
		return actualUserID;
	}

	public void setActualUserID(String actualUserID) {
		this.actualUserID = actualUserID;
	}

	public String getD9_BOMTemp() {
		return d9_BOMTemp==null?"":d9_BOMTemp;
	}

	public void setD9_BOMTemp(String d9_BOMTemp) {
		this.d9_BOMTemp = d9_BOMTemp;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Boolean getIsModifyItem() {
		return isModifyItem == null ? false:isModifyItem;
	}

	public void setIsModifyItem(Boolean isModifyItem) {
		this.isModifyItem = isModifyItem;
	}

	public String getModifyKey() {
		return modifyKey==null?"":modifyKey;
	}

	public void setModifyKey(String modifyKey) {
		this.modifyKey = modifyKey;
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
	
	public Integer getSequence_no() {
		return sequence_no;
	}

	public void setSequence_no(Integer sequence_no) {
		this.sequence_no = sequence_no;
	}
	
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

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
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

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
	
	public void setChild(List<PAProductLineBOMBean> child) {
		this.child = child;
	}
	
	public List<PAProductLineBOMBean> getChild() {
		return child;
	}
	
	public void addChilds(PAProductLineBOMBean child) {
		this.child.add(child);
	}
	
	public List<PAProductLineBOMBean> getSubList() {
		return subList;
	}

	public void setSubList(List<PAProductLineBOMBean> subList) {
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

	public PAProductLineBOMBean() {
		super();		
	}
	
	public static void modifyPAItem(TCSession session, TCComponentItemRevision itemrev, PAProductLineBOMBean rootBean) throws TCException {
		boolean iswrite = TCUtil.checkOwninguserisWrite(session, itemrev);
		if (iswrite && itemrev != null) {
			// 修改零件属性
			String[] itemsetPro = new String[] { "d9_ChineseDescription", "d9_EnglishDescription", "d9_DescriptionSAP",
					"d9_SupplierZF", "d9_SAPRev", "d9_AcknowledgementRev", "d9_ManufacturerID", "d9_ManufacturerPN",
					"d9_MaterialGroup", "d9_MaterialType", "d9_ProcurementMethods", "d9_Un" ,"d9_ActualUserID" };
			String[] provalue = new String[] {
					rootBean.getChineseDescription(),rootBean.getEnglishDescription(),rootBean.getDescriptionSAP(), 
					rootBean.getSupplierZF(),rootBean.getsAPRev(),rootBean.getAcknowledgementRev(), rootBean.getManufacturerID(), rootBean.getManufacturerPN(),
					rootBean.getMaterialGroup(),rootBean.getMaterialType(), rootBean.getProcurementMethods(), rootBean.getUn(), rootBean.getActualUserID()
					};
			itemrev.setProperties(itemsetPro, provalue);
		}
	}
	
	public static void modifyPABom(TCSession session, TCComponentBOMLine children, PAProductLineBOMBean rootBean) throws TCException {
		// 修改结构属性
		String[] bomvalue = new String[] { rootBean.getRemark(), "" + rootBean.getSequence_no(), rootBean.getQty(),
				rootBean.getIsNew(), rootBean.getD9_BOMTemp()};
		children.setProperties(bomsetPro, bomvalue);
	}
	
	
	
	public static PAProductLineBOMBean getBean(PAProductLineBOMBean bean, TCComponentItemRevision itemRev) throws TCException, InterruptedException {
		if (bean != null && itemRev != null) {
			PAMatrixBOMService.loadAllProperties(itemRev, itemgetPro);
			String[] itemPro = itemRev.getProperties(itemgetPro);
			bean.setItemId(itemPro[0]);
			bean.setItemRevision(itemPro[1]);
			bean.setEnglishDescription(itemPro[2]);
			bean.setDescriptionSAP(itemPro[3]);
			bean.setUn(itemPro[4]);
			
			bean.setSupplierZF(itemPro[5]);
			bean.setManufacturerID(itemPro[6]);
			bean.setManufacturerPN(itemPro[7]);
			bean.setAcknowledgementRev(itemPro[8]);
			bean.setsAPRev(itemPro[9]);
			
			bean.setChineseDescription(itemPro[10]);
			bean.setMaterialGroup(itemPro[11]);
			bean.setMaterialType(itemPro[12]);
			bean.setProcurementMethods(itemPro[13]);
			bean.setOwning_user(itemPro[14]);
			bean.setItemType(itemPro[15]);
			bean.setActualUserID(itemPro[16]);
		}
		
		return bean;
	}
	

	public static PAProductLineBOMBean getBean(PAProductLineBOMBean bean, TCComponentBOMLine tcbomLine) throws TCException, InterruptedException {
		
		if (bean != null && tcbomLine != null) {
			
			TCComponentItemRevision itemRev = tcbomLine.getItemRevision();
			
			PAMatrixBOMService.loadAllProperties(tcbomLine, itemRev,itemgetPro, bomgetPro);
			
			String[] bomPro = tcbomLine.getProperties(bomgetPro);
			bean.setQty(bomPro[0]);
			if (bomPro[1].equals("") || bomPro[1] == null) {
				bean.setSequence_no(null);
			}else {
				String match = "[0-9]{2,6}";
				Integer bl_sequence_no = null;
				if (!((String) bomPro[1]).matches(match)) {
					bl_sequence_no = null;
				}else {
					bl_sequence_no = Integer.parseInt((String) bomPro[1]);
				}
				bean.setSequence_no(bl_sequence_no);
			}
			bean.setBomLineUid(bomPro[2]);
			bean.setBl_uom(bomPro[3]);
			bean.setIsNew(bomPro[4]);
			bean.setSubstitute_list(bomPro[5]);
			bean.setRemark(bomPro[6]);
			bean.setD9_BOMTemp(bomPro[7]);
			
			
			if (itemRev != null) {
				String[] itemPro = itemRev.getProperties(itemgetPro);
				bean.setItemId(itemPro[0]);
				bean.setItemRevision(itemPro[1]);
				bean.setEnglishDescription(itemPro[2]);
				bean.setDescriptionSAP(itemPro[3]);
				bean.setUn(itemPro[4]);
				
				bean.setSupplierZF(itemPro[5]);
				bean.setManufacturerID(itemPro[6]);
				bean.setManufacturerPN(itemPro[7]);
				bean.setAcknowledgementRev(itemPro[8]);
				bean.setsAPRev(itemPro[9]);
				
				bean.setChineseDescription(itemPro[10]);
				bean.setMaterialGroup(itemPro[11]);
				bean.setMaterialType(itemPro[12]);
				bean.setProcurementMethods(itemPro[13]);
				bean.setOwning_user(itemPro[14]);
				bean.setItemType(itemPro[15]);
				bean.setActualUserID(itemPro[16]);
				
			}
		}
		
		return bean;
	}
	
	
	public PAProductLineBOMBean(TCComponentBOMLine bomLine,TCSession session) {
		try {
			child = new CopyOnWriteArrayList<PAProductLineBOMBean>();
			getBean(this, bomLine);
			
			TCComponentItemRevision itemRev = bomLine.getItemRevision();
			this.setItemRevUid(itemRev.getUid());
			this.setLineId(CommonTools.md5Encode(this.getItemId()+this.getD9_BOMTemp()));
			this.setReleased(TCUtil.isReleased(itemRev));
			boolean itemWrite = TCUtil.checkOwninguserisWrite(session, itemRev);
			this.setItemEnabled(itemWrite);
			
			AIFComponentContext[] children = bomLine.getChildren();
			if(children!=null && children.length > 0 ) {
				this.setHasChildren(true);
			}else {
				this.setHasChildren(false);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		String uuid = UUID.randomUUID().toString();
		System.out.println("uuid = "+uuid);
		
		String md5Encode = CommonTools.md5Encode(uuid);
		System.out.println(md5Encode);
		
		
		Instant timeInstant= Instant.now();
		System.out.println(timeInstant);
		
		 SimpleDateFormat sdf=new SimpleDateFormat("yyMMddHHmmssSSS");
		 Date dat1=new Date();
		 String format = sdf.format(dat1);
		 System.out.println(format);
		 
	}
	
}
