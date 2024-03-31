package com.foxconn.sdebom.batcheditorebom.dialog;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;

import com.foxconn.sdebom.batcheditorebom.custtree.CustTreeNode;
import com.foxconn.sdebom.batcheditorebom.custtree.LazyCustTreeNode;
import com.foxconn.sdebom.batcheditorebom.util.LazyTreeTools;
import com.foxconn.tcutils.progress.BooleanFlag;
import com.foxconn.tcutils.progress.IProgressDialogRunnable;
import com.foxconn.tcutils.progress.LoopProgerssDialog;
import com.foxconn.tcutils.progress.ProgressBarThread;
import com.foxconn.tcutils.util.CommonTools;
import com.teamcenter.rac.util.Registry;

public class LazyMyActionGroup extends ActionGroup {

	private TreeViewer tv = null;
	private Registry reg = null;
	private Shell shell = null;
	private ExecutorService es = null; 
	
	public LazyMyActionGroup(TreeViewer tv, Registry reg, ExecutorService es) {
		super();
		this.tv = tv;
		this.reg = reg;
		this.es = es;
	}


	@Override
	public void fillContextMenu(IMenuManager mgr) {
		MenuManager menuManager = (MenuManager) mgr; // 类型转换
		final ExpandDownAction expandDownAction = new ExpandDownAction(); // 向下展开
		final CollapseBottomAction collapseBottomAction = new CollapseBottomAction(); // 向下折叠
		menuManager.setRemoveAllWhenShown(true); // 设置"在每次显示之前先删除全部老菜单"的属性为true
		menuManager.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager imenumanager) {
				List<LazyCustTreeNode> custTreeNodeList = getCustTreeNodes();
				if (CommonTools.isEmpty(custTreeNodeList)) { // 如果未选中结构树行对象，则取消显示
					return;
				}
				
				boolean expandFlag = true;
				for (LazyCustTreeNode custTreeNode : custTreeNodeList) {
					if (tv.getExpandedState(custTreeNode) || !custTreeNode.hasChildren()) { // 当前结点已经展开，或者不存在子节点
						expandFlag = false;
						break;
					}
				}
				
				if (expandFlag) {
					menuManager.add(expandDownAction);									
				}
				
				boolean collapseFlag = true;				
				for (LazyCustTreeNode custTreeNode : custTreeNodeList) {
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
			LoopProgerssDialog progerssDialog = new LoopProgerssDialog(new Shell(), null, "正在加载，请稍后...");
			progerssDialog.run(true, new IProgressDialogRunnable() {
				
				@Override
				public void run(BooleanFlag stopFlag) {
					Display.getDefault().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							List<LazyCustTreeNode> custTreeNodeList = getCustTreeNodes();			
							for (LazyCustTreeNode custTreeNode : custTreeNodeList) {
								LazyTreeTools.expandDownTreeNode(tv, custTreeNode, es);
							}
						}
					});
					
					stopFlag.setFlag(true); // 执行完毕把标识位设置为停止，好通知给进度框
				}
			});			
		}
		
	}
	
	
	private class CollapseBottomAction extends Action {
		public CollapseBottomAction() {
			setText(reg.getString("CollapseBottomBtn.LABEL"));
		}

		@Override
		public void run() {
			List<LazyCustTreeNode> custTreeNodeList = getCustTreeNodes();			
			for (LazyCustTreeNode custTreeNode : custTreeNodeList) {
				LazyTreeTools.collapseBottomTreeNode(tv, custTreeNode, true);
			}
					
		}
	}
	
	private List<LazyCustTreeNode> getCustTreeNodes() {
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
