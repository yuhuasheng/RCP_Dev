package com.foxconn.electronics.dcnreport.batcheditornewmoldfee.editor;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

import com.foxconn.electronics.L10Ebom.combox.MyComboBoxViewerCellEditor;
import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.constant.NewMoldFeeConstant;
import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.domain.NewMoldFeeBean;
import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.util.Tools;

public class MyEditingSupport extends EditingSupport {

	private TableViewer tv;
	private TableItem currentTableItem = null; // 当前table行
	private ComboBoxViewerCellEditor currencyCombox = null;
	private String columnName;
	private int index;
	
	public MyEditingSupport(ColumnViewer viewer, int index, String columnName) {
		super(viewer);
		this.tv = (TableViewer) viewer;
		this.index = index;
		columnName = columnName.replace("*", "").replace(" ", "");
		this.columnName = columnName;
		if (columnName.equals(NewMoldFeeConstant.CURRENCY)) {
			currencyCombox = new MyComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
			currencyCombox.setLabelProvider(new LabelProvider());
			currencyCombox.setContentProvider(new ArrayContentProvider());
			currencyCombox.setInput(NewMoldFeeConstant.CURRENCYLOV);			
		}
	}

	
	

	@Override
	protected boolean canEdit(Object obj) {
		if (index == 0) {
			return false;
		}
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object obj) {
		if (columnName.equals(NewMoldFeeConstant.CURRENCY)) {
			return currencyCombox;
		}
		TextCellEditor textCellEditor = new TextCellEditor((Composite) getViewer().getControl(), SWT.NONE);
//		textCellEditor.performCopy();
		return textCellEditor;
	}

	@Override
	protected Object getValue(Object obj) {
		currentTableItem = tv.getTable().getSelection()[0];
		return tv.getTable().getSelection()[0].getText(index);
	}

	@Override
	protected void setValue(Object obj, Object obj1) {
		if (index != 6) { // 只保存新模费用单元格
			return;
		}
		String value = obj1.toString().trim();
		currentTableItem.setText(index, value);
		NewMoldFeeBean currentBean = (NewMoldFeeBean) currentTableItem.getData();
		Tools.updateData(currentBean, index, value);
	}
}
