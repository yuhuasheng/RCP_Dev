package com.foxconn.mechanism.exportcreomodel;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

public class TableViewRenderer extends JTextArea implements TableCellRenderer {

	public TableViewRenderer() {
		// 将表格设置为自动换行
		setLineWrap(true); // 利用JTextArea的自动换行方法
		setWrapStyleWord(true);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, // obj指的是单元格内容
			boolean hasFocus, int row, int column) {
		// 计算当下行的最佳高度
		int maxPreferredHeight = 0;
		for (int i = 0; i < table.getColumnCount(); i++) {
			setText("" + table.getValueAt(row, i));
			setSize(table.getColumnModel().getColumn(column).getWidth(), 0);
			maxPreferredHeight = Math.max(maxPreferredHeight, getPreferredSize().height);
		}
		if (table.getRowHeight(row) != maxPreferredHeight) // 少了这行则处理器瞎忙
		{
			table.setRowHeight(row, maxPreferredHeight);
		}

		setText(obj == null ? "" : obj.toString()); // 利用JTextArea的setText设置文本方法
		return this;
	}

}
