package com.hh.fx.rewrite.jtree;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * 树节点的操作
 * @author wangsf
 *
 */
public class TreeNodeOperation {

	private JTree tree = null;
	// root节点下 包含所有节点数据
	private DefaultMutableTreeNode rootTreeNode = null;
	
	public TreeNodeOperation(JTree tree, DefaultMutableTreeNode rootTreeNode) {
		super();
		this.tree = tree;
		this.rootTreeNode = rootTreeNode;
	}
	
	/**
	 *  搜索节点对象
	 * @param nodeKey 关键字
	 * @return
	 */
	public DefaultMutableTreeNode searchNodeKey(String nodeKey) {
		DefaultMutableTreeNode treeNodeItem = null;
		Object userObject = null;
		TreeNodeData tempTreeNodeData = null;
		
		Enumeration e = this.rootTreeNode.breadthFirstEnumeration(); 
		while (e.hasMoreElements()) {
			treeNodeItem = (DefaultMutableTreeNode) e.nextElement();
			userObject = treeNodeItem.getUserObject();
			
			if (userObject instanceof TreeNodeData) {
				tempTreeNodeData = (TreeNodeData) userObject;
				if (tempTreeNodeData.getClassId().contains(nodeKey)) {
					return treeNodeItem;
				}
				
			} else if (userObject instanceof String) {
				if ((userObject.toString()).contains(nodeKey)) {
					return treeNodeItem;
				}
			}
		}
		
		return null;
	}
	
	/**
	 *  选中节点
	 * @param nodeKey
	 */
	public void selectedNode(String nodeKey) {
		DefaultMutableTreeNode searchTreeNode = searchNodeKey(nodeKey);
		TreeNode[] treeNodes = searchTreeNode.getPath();
		TreePath path = new TreePath(treeNodes);
		System.out.println("选中的节点路径:" + path.toString());
		tree.setSelectionPath(path);
	}
	
}
