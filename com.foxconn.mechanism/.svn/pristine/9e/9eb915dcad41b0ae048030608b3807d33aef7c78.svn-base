package com.foxconn.mechanism.jurisdiction;

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

public class MyEditingSupport2 extends EditingSupport {

	private TreeViewer tv;
	private int index;
	private ComboBoxViewerCellEditor cellEditor = null; 
	private static List<String> names = null;
	private TreeItem treeItem2 = null;
	
	public MyEditingSupport2(ColumnViewer viewer,int index) {
		super(viewer);
		this.tv = (TreeViewer) viewer;
		this.index = index;
		names = new ArrayList<String>();
		names.add("zhangc");
		names.add("chenl");
		names.add("lip");
		cellEditor = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
		cellEditor.setLabelProvider(new LabelProvider());
	    cellEditor.setContentProvider(new ArrayContentProvider());
		cellEditor.setInput(names);
	}

	@Override
	protected CellEditor getCellEditor(Object obj) {
		if(index == 3 || index == 4) {
			return cellEditor;
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
		return tv.getTree().getSelection()[0].getText(index);
	}

	@Override
	protected void setValue(Object obj, Object obj1) {
		treeItem2.setText(index, obj1.toString());
	}

}
