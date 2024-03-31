package com.foxconn.mechanism.hhpnmaterialapply.PRT;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import com.foxconn.mechanism.hhpnmaterialapply.constants.BUConstant;
import com.foxconn.mechanism.hhpnmaterialapply.domain.BOMInfo;
import com.foxconn.mechanism.hhpnmaterialapply.domain.PropertiesInfo;
import com.foxconn.mechanism.hhpnmaterialapply.util.TreeTools;
import com.foxconn.mechanism.hhpnmaterialapply.CustTreeNode;
import com.foxconn.mechanism.util.CommonTools;


public class PRTEditingSupport extends EditingSupport {

	private TreeViewer tv;
	private int index;
	TreeItem currentTreeItem = null; // 当前结构树行	
	private PropertiesInfo currentPropertiesInfo = null;
	private TreeTools treeTools = null;
	
	public PRTEditingSupport(ColumnViewer viewer, int index, TreeTools treeTools) {
		super(viewer);
		this.tv = (TreeViewer) viewer;
		this.index = index;	
		this.treeTools = treeTools;
	}

	@Override
	protected boolean canEdit(Object obj) {
		CustTreeNode treeNode = (CustTreeNode) obj;
		BOMInfo bomInfo = treeNode.getBomInfo();
		if (bomInfo.getModify() == false) {
			return false;
		}
		if (index == 0 || index == 1 || index == 2 || index == 6 || index == 12) {
			return false;
		}
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object obj) {
		if (index == 0 || index == 1 || index == 2 || index == 6 || index == 12) {
			return null;
		}
		return new TextCellEditor((Composite) getViewer().getControl(), SWT.NONE);
	}

	@Override
	protected Object getValue(Object obj) {
		currentTreeItem = tv.getTree().getSelection()[0];
		return tv.getTree().getSelection()[0].getText(index);
	}

	@Override
	protected void setValue(Object obj, Object obj1) {
		if (index == 0 || index == 1 || index == 2 || index == 6 || index == 12) {
			return;
		}
		String value = obj1.toString().trim();
		CustTreeNode custTreeNode = (CustTreeNode) currentTreeItem.getData();	
		if (CommonTools.isEmpty(custTreeNode)) {
			return;
		}
		BOMInfo currentInfo = custTreeNode.getBomInfo();			
		currentTreeItem.setText(index, value);
		currentPropertiesInfo = currentInfo.getPropertiesInfo();		
//		TreeItem topItem = tv.getTree().getTopItem();
		TreeItem topItem = tv.getTree().getItems()[0]; // 获取树的顶层		
		CustTreeNode topCustTreeNode = (CustTreeNode) topItem.getData();
		if (CommonTools.isEmpty(topCustTreeNode)) {
			return;
		}
		BOMInfo topBomInfo = topCustTreeNode.getBomInfo();		

		// 更新属性值到Tree绑定的实体类中
//		PRTTreePropertiesTools.updateProperties(currentPropertiesInfo, index, value);
		// 同步结构树中的模型数据
		treeTools.syncTreeModelData(topBomInfo, currentInfo, index, value, BUConstant.PRTBUNAME);
		// 刷新结构树中类型为料号对象的属性
		treeTools.refreshTreeItemProperties(tv, topItem);
	}

}
