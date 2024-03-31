package com.foxconn.mechanism.hhpnmaterialapply;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import com.foxconn.mechanism.hhpnmaterialapply.domain.BOMInfo;
import com.foxconn.mechanism.util.CommonTools;

public class CustTreeNode {
	
	private String objectName;	
	
	private BOMInfo bomInfo;	
	
	public List<CustTreeNode> list = new ArrayList<CustTreeNode>();
	
	private boolean checkedStates = false; // 结点是否被选中，默认为没有选中
	
	private CustTreeNode parentTreeNode;
	
	public CustTreeNode(BOMInfo bomInfo) {
		this.bomInfo = bomInfo;		
	}	
	
	public boolean hasChildren() {
		if (bomInfo.getChild() == null) {
			return false;
		} else {
			return bomInfo.getChild().size() == 0 ? false : true;
		}		
	}
	
	public Image getImage() {
		return bomInfo.getImage();
	}
	
	public List<CustTreeNode> getChildren() {
		List<BOMInfo> child = bomInfo.getChild();
		if (child == null || child.size() <= 0) {
			return null;
		}	
		
		for (BOMInfo bomInfo : child) {				
			boolean flag = false;			
			for (CustTreeNode custTreeNode : list) {
				BOMInfo bomInfo2 = custTreeNode.getBomInfo();				
				if (bomInfo2.getPropertiesInfo().getItem_ID().equals(bomInfo.getPropertiesInfo().getItem_ID()) && bomInfo2.getObjectType().equals(bomInfo.getObjectType())) {
					flag = true;
					break;
				}
			}
			if (flag) {
				continue;
			} else {
				CustTreeNode childTreeNode = new CustTreeNode(bomInfo);
				childTreeNode.setParentTreeNode(this);
				bomInfo.setCustTreeNode(childTreeNode);					
//				list.add(new CustTreeNode(bomInfo));
				list.add(childTreeNode);
			}			
		}
		return list;
	}	
	
	
	
	public String getObjectName() {		
		return bomInfo.getObjectName();
	}

	public String getItemId() {
		return bomInfo.getItemId();
	}
	
	
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}		

	public List<CustTreeNode> getList() {
		List<CustTreeNode> tempList = new ArrayList<CustTreeNode>();
		tempList.add(new CustTreeNode(bomInfo));
		return tempList;
	}	
	
	public void setList(List<CustTreeNode> list) {
		this.list = list;
	}
	
	public BOMInfo getBomInfo() {
		return bomInfo;
	}

	public void setBomInfo(BOMInfo bomInfo) {
		this.bomInfo = bomInfo;
	}
	
	
	public List<CustTreeNode> getList2() {
		return list;
	}

	public boolean getCheckedStates() {
		return checkedStates;
	}

	public void setCheckedStates(boolean checkedStates) {
		this.checkedStates = checkedStates;
	}


	public CustTreeNode getParentTreeNode() {
		return parentTreeNode;
	}


	public void setParentTreeNode(CustTreeNode parentTreeNode) {
		this.parentTreeNode = parentTreeNode;
	}
	
	
}
