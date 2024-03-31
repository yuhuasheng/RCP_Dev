package com.teamcenter.rac.workflow.commands.adhoc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

public class PublicMailPanel extends JPanel {
	private Registry appReg;
	
	protected TCSession session;
	
	protected JTable table;
	
	protected TableModel tableModel;
		
	private String publicUserInfo = "";
	
	private static final Object[] columnNames = { "流程節點", "TC共用帳號", "實際工作者", "實際工作者郵箱" };
	
	protected static Map<String, List<String>> m_rowMap = new LinkedHashMap<String, List<String>>();
	
	// 表格所有行数据
	private static Object[][] rowData = {{"","","",""}};

	public PublicMailPanel(TCSession paramTCSession, List<List<String>> pubMailInfoLst) {
//		System.out.println("=============PublicMailPanel()==============");
		this.appReg = Registry.getRegistry(this);
		this.session = paramTCSession;

		initializePanel(pubMailInfoLst);
	}
	
	public void initializePanel(List<List<String>> pubMailInfoLst) {
				
		removeAll();
		
		setLayout(new BorderLayout());
		
		if (null == pubMailInfoLst) 
			pubMailInfoLst = new ArrayList<List<String>>();
		
		rowData = new Object[pubMailInfoLst.size()][4];
		
		for (int i = 0; i < pubMailInfoLst.size(); i++) {
			Object[] accounArr = new Object[4];
			accounArr[0] = pubMailInfoLst.get(i).get(0);
			accounArr[1] = pubMailInfoLst.get(i).get(1);
			accounArr[2] = "";
			accounArr[3] = "";
			
			if (m_rowMap != null && m_rowMap.size() >0) {
				String key = pubMailInfoLst.get(i).get(0)+pubMailInfoLst.get(i).get(1);
				if (m_rowMap.containsKey(key)) {
					accounArr[2] = m_rowMap.get(key).get(2);
					accounArr[3] = m_rowMap.get(key).get(3);					
				}
			}			
			
			rowData[i] = accounArr;
		}
		
		table = new JTable(new MyTableModel());

		table.setForeground(Color.BLACK);                   // 字体颜色
        table.setFont(new Font(null, Font.PLAIN, 12));      // 字体样式
        table.setSelectionForeground(Color.DARK_GRAY);      // 选中后字体颜色
        table.setSelectionBackground(Color.LIGHT_GRAY);     // 选中后字体背景
        table.setGridColor(Color.GRAY);                     // 网格颜色

        // 设置表头
        table.getTableHeader().setFont(new Font(null, Font.BOLD, 12));  // 设置表头名称字体样式
        table.getTableHeader().setForeground(Color.BLACK);                // 设置表头名称字体颜色
        table.getTableHeader().setResizingAllowed(true);               // 设置不允许手动改变列宽
        table.getTableHeader().setReorderingAllowed(false);             // 设置不允许拖动重新排序各列

		// 设置行高
		table.setRowHeight(30);

		// 列宽设置
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(50);
		table.getColumnModel().getColumn(2).setPreferredWidth(50);
		table.getColumnModel().getColumn(3).setPreferredWidth(150);

		// 列宽设置
		table.setPreferredScrollableViewportSize(new Dimension(400, 300));

		// 把 表格 放到 滚动面板 中（表头将自动添加到滚动面板顶部）
		JScrollPane scrollPane = new JScrollPane(table);

		// 添加 滚动面板 到 内容面板
		add(scrollPane, BorderLayout.CENTER);

		tableModel = table.getModel();
		tableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
//				int firstRow = e.getFirstRow();
//				int lastRow = e.getLastRow();
//				int column = e.getColumn();
//				int type = e.getType();

//				if (type == TableModelEvent.UPDATE) {
//					if (column < 1 || column > 3) {
//						return;
//					}
//					for (int row = firstRow; row <= lastRow; row++) {
//						Object chineseObj = tableModel.getValueAt(row, 1);
//						Object mathObj = tableModel.getValueAt(row, 2);
//						Object englishObj = tableModel.getValueAt(row, 3);
//
//						int chinese = 0;
//						try {
//							chinese = Integer.parseInt("" + chineseObj);
//						} catch (Exception ex) {
//							ex.printStackTrace();
//						}
//						int math = 0;
//						try {
//							math = Integer.parseInt("" + mathObj);
//						} catch (Exception ex) {
//							ex.printStackTrace();
//						}
//						int english = 0;
//						try {
//							english = Integer.parseInt("" + englishObj);
//						} catch (Exception ex) {
//							ex.printStackTrace();
//						}
//
//						int totalScore = chinese + math + english;
//						tableModel.setValueAt(totalScore, row, 3);
//					}
//				}
			}
		});

		ListSelectionModel selectionModel = table.getSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		selectionModel.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;

				int selectedRow = table.getSelectedRow();

				int selectedColumn = table.getSelectedColumn();
			}
		});
		
		// 创建单元格渲染器
        MyTableCellRenderer renderer = new MyTableCellRenderer();
        for (int i = 0; i < columnNames.length; i++) {
            TableColumn tableColumn = table.getColumn(columnNames[i]);
            tableColumn.setCellRenderer(renderer);
        }
	}

	/**
	 * 表格模型实现，表格显示数据时将调用模型中的相应方法获取数据进行表格内容的显示
	 */
	public static class MyTableModel extends AbstractTableModel {

		/**
		 * 返回总行数
		 */
		@Override
		public int getRowCount() {
			return rowData.length;
		}

		/**
		 * 返回总列数
		 */
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		/**
		 * 返回列名称（表头名称），AbstractTableModel 中对该方法的实现默认是以 大写字母 A
		 * 开始作为列名显示，所以这里需要重写该方法返回我们需要的列名。
		 */
		@Override
		public String getColumnName(int column) {
			return columnNames[column].toString();
		}

		/**
		 * 返回指定单元格的显示的值
		 */
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return rowData[rowIndex][columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
			rowData[rowIndex][columnIndex] = newValue;
			fireTableCellUpdated(rowIndex, columnIndex);
		}

	}

	public static class MyTableCellRenderer extends DefaultTableCellRenderer {        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {                                   
        	if (value != null)
        		setToolTipText(value.toString());            

            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

	protected static void clearData() {
		rowData = new Object[0][3];
	}
	
	protected List<List<String>> getTabelInfo() {
		List<List<String>> retLst = new ArrayList<List<String>>();
		
		int rowCount = table.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			String nodeName = tableModel.getValueAt(i, 0).toString();
			if (!"".equals(nodeName)) {
				List<String> rowLst = new ArrayList<String>(); 
				rowLst.add(tableModel.getValueAt(i, 0).toString());
				rowLst.add(tableModel.getValueAt(i, 1).toString());
				rowLst.add(tableModel.getValueAt(i, 2).toString());
				rowLst.add(tableModel.getValueAt(i, 3).toString());
				
				retLst.add(rowLst);
			}
		}
				
		return retLst;
	}
}
