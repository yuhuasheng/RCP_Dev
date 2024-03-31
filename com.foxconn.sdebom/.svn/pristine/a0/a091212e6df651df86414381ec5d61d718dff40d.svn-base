package com.foxconn.sdebom.batcheditorebom.custtree;

import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;

public class MyComboBoxViewerCellEditor extends ComboBoxViewerCellEditor {

	public MyComboBoxViewerCellEditor(Composite parent) {
		this(parent, SWT.NONE); 
	}

	public MyComboBoxViewerCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void doSetValue(Object value) {
		if (value instanceof String) {
			getComboBox().setText((String) value);
		}
	}

	@Override
	protected Object doGetValue() {
		return getComboBox().getText();
	}

	private CCombo getComboBox() {
		return (CCombo) this.getControl();
	}

}
