package com.foxconn.electronics.matrixbom.domain;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.lang.reflect.Field;
import com.foxconn.electronics.dcnreport.dcncostimpact.util.TCPropertes;
import com.foxconn.electronics.matrixbom.service.MatrixBOMService;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;

public class VariableBOMBean {

	private List<VariableBOMBean> child; // 子集合
	private List<VariableBOMBean> subList; // 替代料集合
	private Integer level=3; 
	private String lineId;
	
	@TCPropertes(tcProperty = "bl_occ_d9_Plant")
	private String plant;
	
	@TCPropertes(tcProperty = "bl_occ_d9_Category")
	private String category;
	
	@TCPropertes(tcProperty = "bl_occ_d9_Remark")
	private String remark;
	
	@TCPropertes(tcProperty = "bl_occ_d9_TorqueIn")
	private String torqueIn;
	
	@TCPropertes(tcProperty = "bl_occ_d9_TorqueOut")
	private String torqueOut;
	
	@TCPropertes(tcProperty = "bl_occ_d9_IsNew")
	private String isNew;

	@TCPropertes(tcProperty = "bl_occ_fnd0objectId")
	private String bomLineUid;
	
	@TCPropertes(tcProperty = "bl_sequence_no")
	private Integer findNum;
	
	@TCPropertes(tcProperty = "bl_item_item_id")
	private String itemId;
	
	@TCPropertes(tcProperty = "d9_EnglishDescription")
	private String englishDescription;
	
	@TCPropertes(tcProperty = "bl_rev_item_revision_id")
	private String itemRevision;
	
	private boolean isSub = false; // 判断是否为替代料	
	
	private boolean isAdd = false; // 判断此料是否为新增
	
	private String itemRevUid;
	
	@TCPropertes(tcProperty = "bl_quantity")
	private String qty = "1";	
	
	@TCPropertes(tcProperty = "object_name")
	private String itemName;
	
	private String bl_rev_d9_Customer;
	
	private String d9_ShippingArea;
	
	private String errorMsg;
	
	private boolean isReleased=false;
	
	private boolean isModify = false; // 是否发生修改
	
	@TCPropertes(tcProperty = "d9_Un")
	private String un;
	
	@TCPropertes(tcProperty = "d9_SupplierZF")
	private String supplierZF;
	
	@TCPropertes(tcProperty = "d9_AcknowledgementRev")
	private String acknowledgementRev;
	
	
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
	public String getAcknowledgementRev() {
		return acknowledgementRev;
	}
	public void setAcknowledgementRev(String acknowledgementRev) {
		this.acknowledgementRev = acknowledgementRev;
	}
	public VariableBOMBean() {		
		
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
							value = itemRev.getProperty(tcAttrName);
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
	
	public VariableBOMBean(TCComponentBOMLine bomLine) {		
		child = new CopyOnWriteArrayList<VariableBOMBean>();
		try {
			tcPropMapping(this, bomLine);
			TCComponentItemRevision itemRev = bomLine.getItemRevision();
			this.setItemRevUid(itemRev.getUid());
			this.setLineId(CommonTools.md5Encode(this.getItemId()+this.getCategory().trim()+this.getPlant()));
		   this.setReleased(TCUtil.isReleased(itemRev));
		} catch (IllegalArgumentException | IllegalAccessException | TCException e) {
			e.printStackTrace();
		}
	}	
	
	public Integer getFindNum() {
		return findNum;
	}

	public void setFindNum(Integer findNum) {
		this.findNum = findNum;
	}

	

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getEnglishDescription() {
		return englishDescription;
	}
	public void setEnglishDescription(String englishDescription) {
		this.englishDescription = englishDescription;
	}
	public String getBl_rev_d9_Customer() {
		return bl_rev_d9_Customer;
	}
	public void setBl_rev_d9_Customer(String bl_rev_d9_Customer) {
		this.bl_rev_d9_Customer = bl_rev_d9_Customer;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public boolean isSub() {
		return isSub;
	}

	public void setSub(boolean isSub) {
		this.isSub = isSub;
	}

	public void setIsSub(boolean isSub) {
		this.isSub = isSub;
	}
	public boolean getIsSub() {
		return isSub;
	}

	public String getItemRevUid() {
		return itemRevUid;
	}

	public void setItemRevUid(String itemRevUid) {
		this.itemRevUid = itemRevUid;
	}

	

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	
	
	public void addQty(String  num) {
		 if(num==null||"".equalsIgnoreCase(num.trim())) {
		    	return;  
		  }
		  Integer  mqtyInteger=Integer.parseInt(qty)+Integer.parseInt(num); 
		  qty = ""+mqtyInteger;
	}
	
	
	public void setChild(List<VariableBOMBean> child) {
		this.child = child;
	}
	
	public List<VariableBOMBean> getChild() {
		return child;
	}	
	
	public List<VariableBOMBean> getSubList() {
		return subList;
	}
	
	public void setSubList(List<VariableBOMBean> subList) {
		this.subList = subList;
	}	

	public boolean isAdd() {
		return isAdd;
	}

	public void setAdd(boolean isAdd) {
		this.isAdd = isAdd;
	}

	public boolean isModify() {
		return isModify;
	}

	public void setModify(boolean isModify) {
		this.isModify = isModify;
	}

	public String getBomLineUid() {
		return bomLineUid;
	}

	public void setBomLineUid(String bomLineUid) {
		this.bomLineUid = bomLineUid;
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

	public boolean isReleased() {
		return isReleased;
	}

	public void setReleased(boolean isReleased) {
		this.isReleased = isReleased;
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

	public String getPlant() {
		return plant==null?"":plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}
	public String getRemark() {
		return remark==null?"":remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public String getIsNew() {
		return isNew==null?"":isNew;
	}
	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}
	
	public String getD9_ShippingArea() {
		return d9_ShippingArea==null?"":d9_ShippingArea;
	}
	public void setD9_ShippingArea(String d9_ShippingArea) {
		this.d9_ShippingArea = d9_ShippingArea;
	}

	
}
