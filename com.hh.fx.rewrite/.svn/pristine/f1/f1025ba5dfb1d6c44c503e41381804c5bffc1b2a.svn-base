package com.hh.fx.rewrite.jtree;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class JTreeTest {
	static DefaultMutableTreeNode first = new DefaultMutableTreeNode("中国");
	// static DefaultTreeModel model = new DefaultTreeModel(first);

	public static void main(String[] args) {
		initTree();
		JFrame j = new JFrame();
		j.setLayout(new FlowLayout());
		final JTree tree = new JTree(first);
		j.add(tree);
		JTreeTest.init(j, 500, 400, "JTreeTest");
		JButton button = new JButton("选中");
		j.add(button);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode hunan = searchNode("长沙"); // 这写死
				
				TreeNode[] nodes = hunan.getPath();
				TreePath path = new TreePath(nodes);
				System.out.println(path.toString()); // 路径
				tree.setSelectionPath(new TreePath(nodes));
			}
		});
	}

	/**
	 * 初始化树节点
	 */
	public static void initTree() {

		DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("上海");
		DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("湖南");
		DefaultMutableTreeNode node3 = new DefaultMutableTreeNode("长沙");
		DefaultMutableTreeNode node4 = new DefaultMutableTreeNode("郴州");
		DefaultMutableTreeNode node5 = new DefaultMutableTreeNode("衡阳");

		node2.add(node3);
		node2.add(node4);
		node2.add(node5);
		first.add(node1);
		first.add(node2);
	}

	/**
	 * 根据字符串返回对应节点
	 * 
	 * @param nodeStr
	 * @return
	 */
	private static DefaultMutableTreeNode searchNode(String nodeStr) {
		DefaultMutableTreeNode node = null;
		Enumeration e = first.breadthFirstEnumeration(); // 获取root下所有节点
		while (e.hasMoreElements()) {
			node = (DefaultMutableTreeNode) e.nextElement();
			if ((node.getUserObject().toString()).contains(nodeStr)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * 初始化窗口
	 * 
	 * @param f          窗体对象继承了JFrame
	 * @param width      窗体的宽度
	 * @param height     窗体的高度
	 * @param windowName 窗体名称
	 */
	public static void init(final JFrame f, final int width, final int height, final String windowName) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// 得到显示器屏幕的宽高
				int widthAll = Toolkit.getDefaultToolkit().getScreenSize().width;
				int heightAll = Toolkit.getDefaultToolkit().getScreenSize().height;
				f.setTitle(windowName);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(width, height);
				f.setBounds((widthAll - width) / 2, 20, width, height);
				f.setVisible(true);

			}
		});
	}
}
