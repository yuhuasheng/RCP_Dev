package com.hh.fx.rewrite.jtree;

import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * ���ڵ�Ĳ���
 * @author wangsf
 *
 */
public class TreeNodeOperation {

	private JTree tree = null;
	// root�ڵ��� �������нڵ�����
	private DefaultMutableTreeNode rootTreeNode = null;
	
	public TreeNodeOperation(JTree tree, DefaultMutableTreeNode rootTreeNode) {
		super();
		this.tree = tree;
		this.rootTreeNode = rootTreeNode;
	}
	
	/**
	 *  �����ڵ����
	 * @param nodeKey �ؼ���
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
	 *  ѡ�нڵ�
	 * @param nodeKey
	 */
	public void selectedNode(String nodeKey) {
		DefaultMutableTreeNode searchTreeNode = searchNodeKey(nodeKey);
		TreeNode[] treeNodes = searchTreeNode.getPath();
		TreePath path = new TreePath(treeNodes);
		System.out.println("ѡ�еĽڵ�·��:" + path.toString());
		tree.setSelectionPath(path);
	}
	
}
