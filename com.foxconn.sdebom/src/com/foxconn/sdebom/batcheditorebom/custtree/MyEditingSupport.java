package com.foxconn.sdebom.batcheditorebom.custtree;

import java.util.ArrayList;
import java.util.List;

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
import com.foxconn.sdebom.batcheditorebom.util.TreeTools;
import com.teamcenter.rac.util.Registry;

public class MyEditingSupport extends EditingSupport {

	private TreeViewer tv;
	private int index;
	TreeItem currentTreeItem = null; // 当前结构树行
	private TreeTools treeTools;
	private ComboBoxViewerCellEditor comboBoxCellEditor = null;
	private Registry reg;
	
	public MyEditingSupport(ColumnViewer viewer, TreeTools treeTools, int index) {
		super(viewer);
		this.tv = (TreeViewer) viewer;
		this.treeTools = treeTools;
		this.index = index;	
		comboBoxCellEditor = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
		comboBoxCellEditor.setLabelProvider(new LabelProvider());
		comboBoxCellEditor.setContentProvider(new ArrayContentProvider());
		comboBoxCellEditor.setInput(SDEBOMConstant.EXPORTLIST);
	}

	@Override
	protected boolean canEdit(Object obj) {
		CustTreeNode treeNode = (CustTreeNode) obj;
		SDEBOMBean data = treeNode.getData();
		if (treeTools.anyMatch(data.getObjectType())) {
			return false;
		}
		
		if (index == 0 || index == 1 || index == 2 || index == 3) {
			return false;
		}
		
		treeTools.checkParentTreeNodeStatus(treeNode);
		
		if (!data.getCanModify()) {
			return false;
		}		
		
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object obj) {
//		CustTreeNode treeNode = (CustTreeNode) obj;
//		SDEBOMBean data = treeNode.getData();	
//		if (treeTools.anyMatch(data.getObjectType())) {
//			return null;
//		}
//		
//		treeTools.checkParentTreeNodeStatus(treeNode);
//		
//		if (!data.getCanModify()) {
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
		CustTreeNode currentTreeNode = (CustTreeNode) currentTreeItem.getData();
		SDEBOMBean currentData = currentTreeNode.getData();
		treeTools.updateTreeNodeData(currentData, index, value);	
		if (SDEBOMConstant.EXPORTLIST.get(SDEBOMConstant.EXPORTLIST.size() - 1).equals(currentData.getAssemblyStatus())) { // 假如当前结点设置为无法维护，则所有的子阶都不能修改
			treeTools.updateExportModifyFlag(currentData, false);
		} else {
			treeTools.updateExportModifyFlag(currentData, true);
		}
	}
	
	
}
