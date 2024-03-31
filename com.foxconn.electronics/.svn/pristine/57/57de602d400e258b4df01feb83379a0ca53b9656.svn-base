package com.foxconn.electronics.pamatrixbom.domain;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import com.foxconn.electronics.dcnreport.dcncostimpact.util.TCPropertes;
import com.foxconn.electronics.pamatrixbom.service.PAMatrixBOMService;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;

public class PAVariableBOMBean {

	private List<PAVariableBOMBean> child; // 子集合
	private List<PAVariableBOMBean> subList; // 替代料集合
	private Integer level=3; 
	private String lineId;
	private boolean isSub = false; // 判断是否为替代料
	
	@TCPropertes(tcProperty = "bl_sequence_no")
	private String findNum;
	@TCPropertes(tcProperty = "bl_quantity")
	private String qty = "1";
	@TCPropertes(tcProperty = "bl_item_item_id")
	private String itemId;
	@TCPropertes(tcProperty = "bl_rev_item_revision_id")
	private String itemRevision;
	@TCPropertes(tcProperty = "object_type")
	private String object_type;
	@TCPropertes(tcProperty = "bl_rev_d9_ChineseDescription")
	private String chineseDescription;
	@TCPropertes(tcProperty = "bl_rev_d9_EnglishDescription")
	private String englishDescription;
	@TCPropertes(tcProperty = "bl_Part Revision_d9_ShippingArea")
	private String shippingArea;
	
	@TCPropertes(tcProperty = "bl_occ_d9_IsNew")
	private String isNew;
	@TCPropertes(tcProperty = "bl_occ_d9_Remark")
	private String remark;
	@TCPropertes(tcProperty = "bl_occ_d9_BOMTemp")
	private String d9_BOMTemp;
	
	
	
	private String itemRevUid;
	private String errorMsg;
	private boolean isReleased=false;
	private boolean isModify = false;
	
	public PAVariableBOMBean() {		
		
	}
	
	public static PAVariableBOMBean getBean(PAVariableBOMBean bean, TCComponentItemRevision itemRev) throws TCException, InterruptedException {
		String[] itemNames = new String[] {"item_id","item_revision_id","object_type","d9_EnglishDescription","d9_ChineseDescription",
				"d9_ShippingArea"};
		
		if (bean != null && itemRev != null) {
			PAMatrixBOMService.loadAllProperties(itemRev, itemNames);
			
			String[] itemPro = itemRev.getProperties(itemNames);
			bean.setItemId(itemPro[0]);
			bean.setItemRevision(itemPro[1]);
			bean.setObject_type(itemPro[2]);
			bean.setEnglishDescription(itemPro[3]);
			bean.setChineseDescription(itemPro[4]);
			bean.setShippingArea(itemPro[5]);
		}
		return bean;
	}
	
	public static PAVariableBOMBean getBean(PAVariableBOMBean bean, TCComponentBOMLine tcbomLine) throws TCException, InterruptedException {
		String[] bomName = new String[] {"bl_item_item_id","bl_rev_item_revision_id",
				"bl_rev_d9_ChineseDescription","bl_rev_d9_EnglishDescription","bl_quantity",
				"bl_sequence_no","bl_Part Revision_d9_ShippingArea","bl_occ_d9_BOMTemp"};
		
		if (bean != null && tcbomLine != null) {
			PAMatrixBOMService.loadAllProperties(tcbomLine,null, null, bomName);
			
			String[] bomPro = tcbomLine.getProperties(bomName);
			bean.setItemId(bomPro[0]);
			bean.setItemRevision(bomPro[1]);
			bean.setChineseDescription(bomPro[2]);
			bean.setEnglishDescription(bomPro[3]);
			bean.setQty(bomPro[4]);
			bean.setFindNum(bomPro[5]);
			bean.setShippingArea(bomPro[6]);
			bean.setD9_BOMTemp(bomPro[7]);
		}
		return bean;
	}
	
	public PAVariableBOMBean(TCComponentBOMLine bomLine) {
		child = new CopyOnWriteArrayList<PAVariableBOMBean>();
		try {
			getBean(this, bomLine);
			TCComponentItemRevision itemRev = bomLine.getItemRevision();
			
			this.setObject_type(itemRev.getProperty("object_type"));
			this.setItemRevUid(itemRev.getUid());
			this.setLineId(CommonTools.md5Encode(this.getItemId()+this.getD9_BOMTemp()));
			this.setReleased(TCUtil.isReleased(itemRev));
		} catch (IllegalArgumentException | TCException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	

	public String getD9_BOMTemp() {
		return d9_BOMTemp==null?"":d9_BOMTemp;
	}

	public void setD9_BOMTemp(String d9_BOMTemp) {
		this.d9_BOMTemp = d9_BOMTemp;
	}

	public String getFindNum() {
		return findNum;
	}

	public void setFindNum(String findNum) {
		this.findNum = findNum;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String object_type) {
		this.object_type = object_type;
	}

	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
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
	
	public String getEnglishDescription() {
		return englishDescription;
	}
	public void setEnglishDescription(String englishDescription) {
		this.englishDescription = englishDescription;
	}
	
	public void addQty(String  num) {
		 if(num==null||"".equalsIgnoreCase(num.trim())) {
		    	return;  
		  }
		  Integer  mqtyInteger=Integer.parseInt(qty)+Integer.parseInt(num); 
		  qty = ""+mqtyInteger;
	}
	
	public void setChild(List<PAVariableBOMBean> child) {
		this.child = child;
	}
	
	public List<PAVariableBOMBean> getChild() {
		return child;
	}	
	
	public List<PAVariableBOMBean> getSubList() {
		return subList;
	}
	
	public void setSubList(List<PAVariableBOMBean> subList) {
		this.subList = subList;
	}	


	public boolean isModify() {
		return isModify;
	}

	public void setModify(boolean isModify) {
		this.isModify = isModify;
	}

	public String getItemRevision() {
		return itemRevision;
	}

	public void setItemRevision(String itemRevision) {
		this.itemRevision = itemRevision;
	}


	public String getChineseDescription() {
		return chineseDescription;
	}

	public void setChineseDescription(String chineseDescription) {
		this.chineseDescription = chineseDescription;
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

	public void setIsSub(boolean isSub) {
		this.isSub = isSub;
	}
	
	public boolean getIsSub() {
		return isSub;
	}

	public String getShippingArea() {
		return shippingArea;
	}

	public void setShippingArea(String shippingArea) {
		this.shippingArea = shippingArea;
	}

	public String getIsNew() {
		return isNew == null?"":isNew;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	
}
