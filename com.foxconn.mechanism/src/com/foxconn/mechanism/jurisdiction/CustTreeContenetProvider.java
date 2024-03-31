package com.foxconn.mechanism.jurisdiction;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.teamcenter.rac.kernel.TCComponentBOMLine;

public class CustTreeContenetProvider implements ITreeContentProvider{

	//private int count = 0;
	
	@Override
	public Object[] getChildren(Object arg0) {
		CustTreeNode treeNode = (CustTreeNode) arg0;
		List<CustTreeNode> childrens = treeNode.getChildren();
		Object[] objs = new Object[childrens.size()];
		for (int i = 0; i < childrens.size(); i++) {
			objs[i] = childrens.get(i);
		}
		return objs;
	}

	@Override
	public Object[] getElements(Object arg0) {
		return new CustTreeNode((TCComponentBOMLine) arg0).getList().toArray();
	}

	@Override
	public Object getParent(Object arg0) {
		return null;
	}

	@Override
	public boolean hasChildren(Object arg0) {
//		count++;
//		if(count >= 2) {
//			return false;
//		}
		CustTreeNode treeNode = (CustTreeNode) arg0;
		return treeNode.hasChildren();
	}

}
