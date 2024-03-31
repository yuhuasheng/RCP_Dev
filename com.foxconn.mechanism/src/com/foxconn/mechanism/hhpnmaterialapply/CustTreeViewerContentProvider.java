package com.foxconn.mechanism.hhpnmaterialapply;

import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.foxconn.mechanism.hhpnmaterialapply.domain.BOMInfo;
import com.foxconn.mechanism.util.CommonTools;

public class CustTreeViewerContentProvider implements ITreeContentProvider {
	
	private static final Object BOMInfo = null;

	// 当界面中单击某个结点时, 由此方法决定被单击结点应显示哪些子结点
	// parentElement就是被单击的结点对象, 返回的数组就是应显示的子结点
	@Override
	public Object[] getChildren(Object parentElement) {
		CustTreeNode treeNode = (CustTreeNode) parentElement;
		List<CustTreeNode> childrens = treeNode.getChildren();	
		// 虽然通过界面单击方式, 有子结点才会执行到此方法, 但任然要做非空判断, 因为在调用TreeViewer的某些方法时其内部会附带调用此方法
		if (CommonTools.isEmpty(childrens)) {
			return new Object[0];			
		}
		for (CustTreeNode custTreeNode : childrens) {
			custTreeNode.setParentTreeNode(treeNode);
		}
		return childrens.toArray();
//		Object[] objs = new Object[childrens.size()];
//		for (int i = 0; i < childrens.size(); i++) {
//			objs[i] = childrens.get(i);
//		}
//		return objs;		
	}

	// 由此方法决定树的"第一级"结点显示哪些对象, inputElement是用tv.setInput方法输入的那个对象
	@Override
	public Object[] getElements(Object inputElement) {
		BOMInfo bomInfo = (BOMInfo) inputElement;
		CustTreeNode custTreeNode = new CustTreeNode(bomInfo);		
		bomInfo.setCustTreeNode(custTreeNode);		
		if (CommonTools.isNotEmpty(bomInfo.getParent())) {
			custTreeNode.setParentTreeNode(new CustTreeNode(bomInfo.getParent()));
		}
//		 custTreeNode.setParentTreeNode();
//		return new CustTreeNode((BOMInfo) inputElement).getList().toArray();
		 return custTreeNode.getList().toArray();
	}	
	
	@Override
	public Object getParent(Object element) { // 取得element的父结点	
		return null;
	}

	// 判断参数element结点是否含有子结点
	// 返回true表示element有子结点, 则其前面会显示有"+"号图标
	@Override
	public boolean hasChildren(Object element) {
		CustTreeNode treeNode = (CustTreeNode) element;
		return treeNode.hasChildren();
	}	

}
