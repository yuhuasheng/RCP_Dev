package com.foxconn.mechanism.jurisdiction;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

public class MyEditingSupport extends EditingSupport{
	
	private int index;
	private TreeViewer tv;
	private boolean isTopItem = false;
	TreeItem treeItem2 = null;
	
	public MyEditingSupport(ColumnViewer viewer, int index) {
		super(viewer);
		this.tv = (TreeViewer) viewer;
		this.index = index;
	}

	@Override
	protected CellEditor getCellEditor(Object obj) {
		if(index == 3 || index == 4) {
			return new TextCellEditor((Composite)getViewer().getControl(), SWT.NONE);
		}
		return null;
	}

	@Override
	protected boolean canEdit(Object obj) {
		if(index == 3 || index == 4) {
			return true;
		}
		return false;
	}

	@Override
	protected Object getValue(Object obj) {
		treeItem2 = tv.getTree().getSelection()[0];
		if(index == 3 || index == 4) {
			int topItem = tv.getTree().getTopItem().hashCode();
			int treeItem = tv.getTree().getSelection()[0].hashCode();
			if(topItem == treeItem) {
				isTopItem = true;
			}else {
				isTopItem = false;
			}
		}
		return tv.getTree().getSelection()[0].getText(index);
	}

	@Override
	protected void setValue(Object obj, Object obj1) {
		if(index == 3 || index == 4) {
			treeItem2.setText(index, obj1.toString());
			if(!isTopItem) {
				TreeItem topItem = tv.getTree().getTopItem();
				String topItemUser = topItem.getText(4);
				CustTreeNode data = (CustTreeNode) topItem.getData();
				data.setAssignUser(topItemUser);
				TreeItem[] items = topItem.getItems();
				for (int i = 0; i < items.length; i++) {
					String itemUser = items[i].getText(4);
					CustTreeNode data1 = (CustTreeNode) items[i].getData();
					data1.setAssignUser(itemUser);
				}
			}else {
				tv.getTree().getTopItem().setText(index, obj1.toString());
			}
		}
	}

}
