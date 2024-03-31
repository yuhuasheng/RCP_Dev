package com.foxconn.sdebom.batcheditorebom.dialog;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.actions.ActionGroup;

import com.foxconn.sdebom.batcheditorebom.custtree.CustTreeNode;
import com.foxconn.sdebom.batcheditorebom.util.TreeTools;
import com.foxconn.tcutils.progress.BooleanFlag;
import com.foxconn.tcutils.progress.IProgressDialogRunnable;
import com.foxconn.tcutils.progress.LoopProgerssDialog;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.util.Registry;

public class MyActionGroup extends ActionGroup {

	private TreeViewer tv = null;
	private Registry reg = null;
	private TreeTools treeTools = null;
	private Shell shell;
	
	public MyActionGroup(TreeViewer tv, Registry reg, TreeTools treeTools, Shell shell) {
		super();
		this.tv = tv;
		this.reg = reg;
		this.treeTools = treeTools;
		this.shell = shell;
	}


	/**
	 * 加入Action对象到菜单管理器
	 */
	public void fillContextMenu(IMenuManager mgr) {
		MenuManager menuManager = (MenuManager) mgr; // 类型转换
		final ExpandDownAction expandDownAction = new ExpandDownAction(); // 向下展开
		final CollapseBottomAction collapseBottomAction = new CollapseBottomAction(); // 自下折叠
		menuManager.setRemoveAllWhenShown(true); // 设置"在每次显示之前先删除全部老菜单"的属性为true
		menuManager.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager arg0) {
				List<CustTreeNode> custTreeNodeList = getCustTreeNodes();
				if (CommonTools.isEmpty(custTreeNodeList)) { // 如果未选中结构树行对象，则取消显示
					return;
				}
				
				boolean expandFlag = true;				
				for (CustTreeNode custTreeNode : custTreeNodeList) {
					if (tv.getExpandedState(custTreeNode) || !custTreeNode.hasChildren()) { // 当前结点已经展开，或者不存在子节点
						expandFlag = false;
						break;
					}
					
				}
				
				if (expandFlag) {
					menuManager.add(expandDownAction);									
				}
				
				boolean collapseFlag = true;				
				for (CustTreeNode custTreeNode : custTreeNodeList) {
					if (!tv.getExpandedState(custTreeNode) || !custTreeNode.hasChildren()) {
						collapseFlag = false;
						break;
					}					
				}
				
				if (collapseFlag) {
					menuManager.add(collapseBottomAction);
				}
			}
		});
		
		Tree tree = tv.getTree();
		Menu menu = menuManager.createContextMenu(tree);
		tree.setMenu(menu);
	}

	
	private class ExpandDownAction extends Action {		
		
		public ExpandDownAction() {
			setText(reg.getString("ExpandDownBtn.LABEL"));			
		}


		@Override
		public void run() {
			List<CustTreeNode> custTreeNodesList = getCustTreeNodes();
			for (CustTreeNode custTreeNode : custTreeNodesList) {
				treeTools.expandDownTreeNode(tv, custTreeNode);
			}				
		}		
	}
	
	
	private class CollapseBottomAction extends Action {

		
		public CollapseBottomAction() {
			setText(reg.getString("CollapseBottomBtn.LABEL"));
		}

		@Override
		public void run() {
			List<CustTreeNode> custTreeNodesList = getCustTreeNodes();
			for (CustTreeNode custTreeNode : custTreeNodesList) {
				treeTools.collapseBottomTreeNode(tv, custTreeNode, true);
			}
		}
		
	}
	
	
	private List<CustTreeNode> getCustTreeNodes() {
		IStructuredSelection selection = (IStructuredSelection) tv.getSelection();
		if (CommonTools.isEmpty(selection)) {
			return null;
		}
		
		if (selection.size() > 0) {
			return selection.toList();
		}
		
		return null;
	}
}
