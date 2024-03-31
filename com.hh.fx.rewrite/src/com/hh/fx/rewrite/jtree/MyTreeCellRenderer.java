package com.hh.fx.rewrite.jtree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * ���ڵ���Ⱦ
 * 
 * @author wangsf
 *
 */
public class MyTreeCellRenderer extends DefaultTreeCellRenderer {

	/**
	 * ��д����DefaultTreeCellRenderer�ķ���
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean isLeaf, int row, boolean hasFocus) {
		
		// ѡ��
		if (selected) {
			setForeground(getTextSelectionColor());
		} else {
			setForeground(getTextNonSelectionColor());
		}
		
		// TreeNode
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
		Object obj = treeNode.getUserObject();
		if (obj instanceof TreeNodeData) {
			TreeNodeData node = (TreeNodeData) obj;
			DefaultTreeCellRenderer tempCellRenderer = new DefaultTreeCellRenderer();
			tempCellRenderer.setLeafIcon(null);
			return tempCellRenderer.getTreeCellRendererComponent(tree, node.getNodeName(), selected, expanded, true, row,
					hasFocus);
		} else if (obj instanceof String) {
			String text = (String) obj;
			DefaultTreeCellRenderer tempCellRenderer = new DefaultTreeCellRenderer();
			return tempCellRenderer.getTreeCellRendererComponent(tree, text, selected, expanded, false, row, hasFocus);
		}
		return super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, hasFocus);
	}

}
