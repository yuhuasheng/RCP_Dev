/**
 * 
 */
package com.foxconn.electronics.convertebom.pojo;

import java.util.List;

import com.foxconn.electronics.util.TCPropName;

/**
 * @author Leo
 *
 */
public class BOMPojo {
	@TCPropName(value = "bl_item_item_id")
	private String materialNum;
	@TCPropName(value = "bl_sequence_no")
	private String findNum;
	@TCPropName(value = "bl_quantity")
	private String qty;
	@TCPropName(value = "bl_occ_d9_Location")
	private String location;
	@TCPropName(value = "bl_occ_d9_CCL")
	private String ccl;

	private MaterialPojo selfMaterial;
	private List<BOMPojo> child;
	private List<BOMPojo> substitute;
	private BOMPojo substituteGroup;

	public String getMaterialNum() {
		return materialNum;
	}

	public void setMaterialNum(String materialNum) {
		this.materialNum = materialNum;
	}

	public String getFindNum() {
		return findNum;
	}

	public void setFindNum(String findNum) {
		this.findNum = findNum;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCcl() {
		return ccl;
	}

	public void setCcl(String ccl) {
		this.ccl = ccl;
	}

	public MaterialPojo getSelfMaterial() {
		return selfMaterial;
	}

	public void setSelfMaterial(MaterialPojo selfMaterial) {
		this.selfMaterial = selfMaterial;
	}

	public List<BOMPojo> getChild() {
		return child;
	}

	public void setChild(List<BOMPojo> child) {
		this.child = child;
	}

	public List<BOMPojo> getSubstitute() {
		return substitute;
	}

	public void setSubstitute(List<BOMPojo> substitute) {
		this.substitute = substitute;
	}

	public BOMPojo getSubstituteGroup() {
		return substituteGroup;
	}

	public void setSubstituteGroup(BOMPojo substituteGroup) {
		this.substituteGroup = substituteGroup;
	}

	@Override
	public String toString() {
		return "BOMPojo [materialNum=" + materialNum + ", findNum=" + findNum + ", qty=" + qty + ", location="
				+ location + ", ccl=" + ccl + ", selfMaterial=" + selfMaterial + ", child=" + child + ", substitute="
				+ substitute + ", substituteGroup=" + substituteGroup + "]";
	}

}
