package com.foxconn.mechanism.batchDownloadDataset;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.teamcenter.rac.kernel.TCComponentBOMLine;

public class BomLineContenetProvider implements ITreeContentProvider{

	//private int count = 0;
	
	List<BomLineTreeNode> list = new ArrayList<BomLineTreeNode>();
	
	public List<BomLineTreeNode> getAllElements(){
		return list;
	}
	
	@Override
	public Object[] getChildren(Object arg0) {
		BomLineTreeNode treeNode = (BomLineTreeNode) arg0;
		List<BomLineTreeNode> childrens = treeNode.getChildren();
		list.addAll(childrens);
		Object[] objs = new Object[childrens.size()];
		for (int i = 0; i < childrens.size(); i++) {
			objs[i] = childrens.get(i);
		}
		return objs;
	}

	@Override
	public Object[] getElements(Object arg0) {
		
		List<BomLineTreeNode> list2 = new BomLineTreeNode((TCComponentBOMLine) arg0).getList();
		list.addAll(list2);
		return list2.toArray();
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
		BomLineTreeNode treeNode = (BomLineTreeNode) arg0;
		return treeNode.hasChildren();
	}

}
