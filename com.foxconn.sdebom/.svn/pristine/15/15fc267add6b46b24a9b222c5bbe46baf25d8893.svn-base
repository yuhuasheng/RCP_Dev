package com.foxconn.sdebom.batcheditorebom.custtree;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

import com.foxconn.sdebom.batcheditorebom.constants.SDEBOMConstant;
import com.foxconn.sdebom.batcheditorebom.domain.SDEBOMBean;
import com.foxconn.sdebom.batcheditorebom.util.LazyTreeTools;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.util.Registry;

public class LazyMyEditingSupport extends EditingSupport {
	
	private TreeViewer tv;
	private int index;
	TreeItem currentTreeItem = null; // 当前结构树行
	private ComboBoxViewerCellEditor comboBoxCellEditor = null;	
	private Registry reg;
	private Boolean treeNodeLock = false;
	
	public LazyMyEditingSupport(ColumnViewer viewer, int index, Registry reg) {
		super(viewer);
		this.tv = (TreeViewer) viewer;
		this.index = index;
		this.reg = reg;
		comboBoxCellEditor = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
		comboBoxCellEditor.setLabelProvider(new LabelProvider());
		comboBoxCellEditor.setContentProvider(new ArrayContentProvider());
		comboBoxCellEditor.setInput(SDEBOMConstant.EXPORTLIST);
	}

	@Override
	protected boolean canEdit(Object obj) {
		LazyCustTreeNode treeNode = (LazyCustTreeNode) obj;
		SDEBOMBean data = treeNode.getData();
		if (LazyTreeTools.anyMatch(treeNode.getData().getObjectType())) {
			return false;
		}
		
		if (index == 0 || index == 1 || index == 2 || index == 3) {
			return false;
		}
		
		checkParentTreeNodeLock(treeNode, reg);
		if (treeNodeLock) {
			setTreeNodeLock(false);
			return false;
		}		
		 
		if (!data.getCanModify()) {
			return false;
		}
		return true;
	}

	
	
	@Override
	protected CellEditor getCellEditor(Object obj) {
//		LazyCustTreeNode treeNode = (LazyCustTreeNode) obj;
//		if (treeTools.anyMatch(treeNode.getData().getObjectType())) {
//			return null;
//		}
//		
//		if (index == 0 || index == 1 || index == 2 || index == 3) {
//			return null;
//		}
		return comboBoxCellEditor;
	}

	@Override
	protected Object getValue(Object obj) {
		currentTreeItem = tv.getTree().getSelection()[0];		
		return tv.getTree().getSelection()[0].getText(index);
	}

	@Override
	protected void setValue(Object obj, Object obj1) {
		String value = obj1.toString().trim();
 		currentTreeItem.setText(index, value);
 		LazyCustTreeNode currentTreeNode = (LazyCustTreeNode) currentTreeItem.getData();
 		SDEBOMBean currentData = currentTreeNode.getData();
 		LazyTreeTools.updateTreeNodeData(currentData, index, value);
// 		if (SDEBOMConstant.EXPORTLIST.get(SDEBOMConstant.EXPORTLIST.size() - 1).equals(currentData.getExport())) { //  假如当前结点设置为无法维护，则所有的子阶都不能修改
// 			LazyTreeTools.updateExportModifyFlag(currentData, false);
// 		} else {
//			LazyTreeTools.updateExportModifyFlag(currentData, true);
//		}
	}
	
	
	/**
	 * 校验父结点状态是否输出列
	 * @param currentTreeNode
	 * @param reg
	 */
	public void checkParentTreeNodeLock(LazyCustTreeNode currentTreeNode,Registry reg) {
		LazyCustTreeNode parentTreeNode = currentTreeNode.getParentTreeNode();
		if (CommonTools.isEmpty(parentTreeNode)) {
			return;
		}
		
		SDEBOMBean parentData = parentTreeNode.getData();
		if (SDEBOMConstant.EXPORTLIST.get(SDEBOMConstant.EXPORTLIST.size() - 1).equals(parentData.getAssemblyStatus()) 
				&& !LazyTreeTools.anyMatch(parentData.getObjectType())) {
			String[] split = reg.getString("SetAssemblyStatusWarn.MSG").split("&&");
			TCUtil.warningMsgBox(split[0] + parentData.getTitle() + split[1] + parentData.getAssemblyStatus() + split[2], reg.getString("WARNING.MSG"));
			treeNodeLock = true;
			return;
		}
		checkParentTreeNodeLock(parentTreeNode, reg);
	}

	public Boolean getTreeNodeLock() {
		return treeNodeLock;
	}

	public void setTreeNodeLock(Boolean treeNodeLock) {
		this.treeNodeLock = treeNodeLock;
	}
	
	
}
