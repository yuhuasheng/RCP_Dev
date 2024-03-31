package com.foxconn.sdebom.batcheditorebom.custtree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.graphics.Image;

import com.foxconn.sdebom.batcheditorebom.domain.SDEBOMBean;
import com.foxconn.tcutils.util.CommonTools;

public class CustTreeNode {
	
	private SDEBOMBean data;
	public List<CustTreeNode> list = new ArrayList<CustTreeNode>();
	private CustTreeNode parentTreeNode;
	
	public CustTreeNode(SDEBOMBean data) {
		this.data = data;
	}
	
	public boolean hasChildren() {
		if (CommonTools.isEmpty(data.getChilds())) {
			return false;
		}
		return true;
	}	
	
	public List<CustTreeNode> getChildren() {
		List<SDEBOMBean> childs = data.getChilds();
		if (CommonTools.isEmpty(childs)) {
			return null;
		}
		
		for (SDEBOMBean childBean : childs) {
			if (treeNodeAnyMatch(childBean)) {
				continue;
			}
			CustTreeNode childTreeNode = new CustTreeNode(childBean); 
			childTreeNode.setParentTreeNode(this);
			list.add(childTreeNode);
		}
		return list;
	}	

	/**
	 * 判断节点是否已经存在
	 * @param bean
	 * @return
	 */
	public boolean treeNodeAnyMatch(SDEBOMBean bean) {
		return list.stream().anyMatch(e -> e.data.getItemRevUid().equals(bean.getItemRevUid()));
	}
	
	
	public SDEBOMBean getData() {
		return data;
	}

	public void setData(SDEBOMBean data) {
		this.data = data;
	}

	public List<CustTreeNode> getList() {
		List<CustTreeNode> tempList = new ArrayList<CustTreeNode>();
		tempList.add(new CustTreeNode(data));
		return tempList;
	}

	public void setList(List<CustTreeNode> list) {
		this.list = list;
	}

	public CustTreeNode getParentTreeNode() {
		return parentTreeNode;
	}

	public void setParentTreeNode(CustTreeNode parentTreeNode) {
		this.parentTreeNode = parentTreeNode;
	}
	
	
}
