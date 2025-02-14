package com.foxconn.sdebom.batcheditorebom.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import com.foxconn.sdebom.batcheditorebom.constants.IconsEnum;
import com.foxconn.sdebom.batcheditorebom.util.TreeTools;
import com.foxconn.tcutils.serial.SerialCloneable;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;

public class SDEBOMBean extends SerialCloneable {	
	
	private static final long serialVersionUID = 1L;

//	@TCPropertes(tcProperty = "bl_sequence_no", tcType = "BOMLine")
//	private Integer findNum;
	
	@TCPropertes(tcProperty = "item_id")
	private String itemId;
	
	@TCPropertes(tcProperty = "item_revision_id")
	private String version;
	
	@TCPropertes(tcProperty = "bl_indented_title", tcType = "BOMLine")
	private String title;	
	
	@TCPropertes(tcProperty = "d9_CustomerPN")
	private String customerPN;
	
	@TCPropertes(tcProperty = "d9_CustomerPNDescription")
	private String customerPNDesc;
	
	@TCPropertes(tcProperty = "d9_EnglishDescription")
	private String descriptionEn;
	
	@TCPropertes(tcProperty = "fnd0bl_is_mono_override", tcType = "BOMLine")
	private String assemblyStatus;
	
//	@TCPropertes(tcProperty = "bl_quantity",tcType = "BOMLine")
	private Integer qty; // 数量默认值设置为1
	
	private List<SDEBOMBean> childs; // 子对象集合

	private String itemRevUid;
	
	private TCComponentItemRevision itemRev;
	
	private TCComponentBOMLine bomLine;
	
	private String objectType;
	
	private boolean sub = false; // 作为判断是否为替代料的标识，默认设置为不是替代料	
	
	private boolean canModify = true; // 判断当前节点输出列是否可以修改，默认为可以修改
	
	private Color color;
	
	private boolean hasModify = false; // 判断当前节点是否发生修改，默认设置为没有发生修改
	
	public SDEBOMBean() {
		
	}
	
	public SDEBOMBean(TreeTools treeTools, TCComponentBOMLine bomLine) {
//		childs = Collections.synchronizedList(new ArrayList<SDEBOMBean>());
		childs = new CopyOnWriteArrayList<SDEBOMBean>();
		try {
//			TCComponentItemRevision itemRev = bomLine.getItemRevision();
//			treeTools.tcPropMapping(this, bomLine, isSub);
			treeTools.getTCProp(this, bomLine);
//			itemRev.refresh();
//			itemRev.clearCache();
//			TCUtil.tcPropMapping(this, bomLine, "BOMLine");
//			TCUtil.tcPropMapping(this, itemRev, "");
//			this.itemRevUid = itemRev.getUid();
//			this.objectType = itemRev.getTypeObject().getName();
//			if (CommonTools.isEmpty(this.getQty())) {				
//				this.qty = 1;
//			}	
//			
//			if (isSub) {
//				this.image = new Image(null, SDEBOMBean.class.getResourceAsStream(IconsEnum.ReplacePartRevIcons.relativePath()));
//			} else {
//				this.image = new Image(null, SDEBOMBean.class.getResourceAsStream(IconsEnum.PartRevIcons.relativePath()));
//			}			
			
		} catch (TCException e) {
			e.printStackTrace();
		}
	}
	
//	public Integer getFindNum() {
//		return findNum;
//	}
//
//	public void setFindNum(Integer findNum) {
//		this.findNum = findNum;
//	}
	

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCustomerPN() {
		return customerPN;
	}

	public void setCustomerPN(String customerPN) {
		this.customerPN = customerPN;
	}

	public String getCustomerPNDesc() {
		return customerPNDesc;
	}

	public void setCustomerPNDesc(String customerPNDesc) {
		this.customerPNDesc = customerPNDesc;
	}

	public String getDescriptionEn() {
		return descriptionEn;
	}

	public void setDescriptionEn(String descriptionEn) {
		this.descriptionEn = descriptionEn;
	}	

	public String getAssemblyStatus() {
		return assemblyStatus;
	}

	public void setAssemblyStatus(String assemblyStatus) {
		this.assemblyStatus = assemblyStatus;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	/**
	 * 添加数量
	 * @param num
	 */
	public void addQty(Integer num) {
		qty = qty + num;
	}
	
	
	public List<SDEBOMBean> getChilds() {
		return childs;
	}

	
	public void setChilds(List<SDEBOMBean> childs) {
		this.childs = childs;
	}

	public void addChild(SDEBOMBean child) {
		this.childs.add(child);
	}	

	
	public String getItemRevUid() {
		return itemRevUid;
	}

	public void setItemRevUid(String itemRevUid) {
		this.itemRevUid = itemRevUid;
	}

	
	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}	
	

	public boolean getSub() {
		return sub;
	}

	public void setSub(boolean sub) {
		this.sub = sub;
	}

	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	
	public boolean getCanModify() {
		return canModify;
	}

	public void setCanModify(boolean canModify) {
		this.canModify = canModify;
	}
	

	public boolean getHasModify() {
		return hasModify;
	}

	public void setHasModify(boolean hasModify) {
		this.hasModify = hasModify;
	}

	public TCComponentItemRevision getItemRev() {
		return itemRev;
	}

	public void setItemRev(TCComponentItemRevision itemRev) {
		this.itemRev = itemRev;
	}

	public TCComponentBOMLine getBomLine() {
		return bomLine;
	}

	public void setBomLine(TCComponentBOMLine bomLine) {
		this.bomLine = bomLine;
	}

	@Override
	public boolean equals(Object var1) {		
		if (var1 instanceof SDEBOMBean) {
			SDEBOMBean other = (SDEBOMBean) var1;
			if (CommonTools.isNotEmpty(this.itemRevUid) && CommonTools.isNotEmpty(other.getItemRevUid())) {
				return this.itemRevUid.equals(other.getItemRevUid());
			}
		}
		return super.equals(var1);
	}

	@Override
	public String toString() {
		return "SDEBOMBean [itemId=" + itemId + ", version=" + version + ", title=" + title
				+ ", customerPN=" + customerPN + ", customerPNDesc=" + customerPNDesc + ", descriptionEn="
				+ descriptionEn + ", assemblyStatus=" + assemblyStatus + ", qty=" + qty + ", childs=" + childs
				+ ", itemRevUid=" + itemRevUid + ", itemRev=" + itemRev + ", bomLine=" + bomLine + ", objectType="
				+ objectType + ", sub=" + sub + ", canModify=" + canModify + ", color=" + color + ", hasModify="
				+ hasModify + "]";
	}

		
}
