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
	static DefaultMutableTreeNode first = new DefaultMutableTreeNode("�й�");
	// static DefaultTreeModel model = new DefaultTreeModel(first);

	public static void main(String[] args) {
		initTree();
		JFrame j = new JFrame();
		j.setLayout(new FlowLayout());
		final JTree tree = new JTree(first);
		j.add(tree);
		JTreeTest.init(j, 500, 400, "JTreeTest");
		JButton button = new JButton("ѡ��");
		j.add(button);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode hunan = searchNode("��ɳ"); // ��д��
				
				TreeNode[] nodes = hunan.getPath();
				TreePath path = new TreePath(nodes);
				System.out.println(path.toString()); // ·��
				tree.setSelectionPath(new TreePath(nodes));
			}
		});
	}

	/**
	 * ��ʼ�����ڵ�
	 */
	public static void initTree() {

		DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("�Ϻ�");
		DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("����");
		DefaultMutableTreeNode node3 = new DefaultMutableTreeNode("��ɳ");
		DefaultMutableTreeNode node4 = new DefaultMutableTreeNode("����");
		DefaultMutableTreeNode node5 = new DefaultMutableTreeNode("����");

		node2.add(node3);
		node2.add(node4);
		node2.add(node5);
		first.add(node1);
		first.add(node2);
	}

	/**
	 * �����ַ������ض�Ӧ�ڵ�
	 * 
	 * @param nodeStr
	 * @return
	 */
	private static DefaultMutableTreeNode searchNode(String nodeStr) {
		DefaultMutableTreeNode node = null;
		Enumeration e = first.breadthFirstEnumeration(); // ��ȡroot�����нڵ�
		while (e.hasMoreElements()) {
			node = (DefaultMutableTreeNode) e.nextElement();
			if ((node.getUserObject().toString()).contains(nodeStr)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * ��ʼ������
	 * 
	 * @param f          �������̳���JFrame
	 * @param width      ����Ŀ��
	 * @param height     ����ĸ߶�
	 * @param windowName ��������
	 */
	public static void init(final JFrame f, final int width, final int height, final String windowName) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// �õ���ʾ����Ļ�Ŀ��
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
