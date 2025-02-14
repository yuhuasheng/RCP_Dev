package com.foxconn.electronics.L10Ebom.dialog;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.foxconn.electronics.L10Ebom.constant.ApplyFormConstant;
import com.foxconn.electronics.L10Ebom.domain.EBOMApplyRowBean;
import com.foxconn.electronics.L10Ebom.util.TableTools;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.common.actions.CopyAction;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFnd0TableRow;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

public class ButtonSelectionListener extends SelectionAdapter {

	private Shell parent = null;
	private TCSession session = null;
	private Registry reg = null;
	private TableViewer tv = null;	
	private Composite allComposite = null;
	private Composite tableComposite = null;
	private static final int[] sortColumnIndexArray = {0}; // 记录需要重新排序的列
	private TCComponentItemRevision itemRev = null;
	private String tablePropName = null;
	private String tableRowType = null;
	private CustTableItem custTableItem = null;
	
	
	public ButtonSelectionListener(Composite allComposite, Composite tableComposite, TableViewer tv, Shell parent, TCSession session, Registry reg, TCComponentItemRevision itemRev, 
			String tablePropName, String tableRowType, CustTableItem custTableItem) {
		super();
		this.allComposite =  allComposite;	
		this.tableComposite = tableComposite;
		this.tv = tv;
		this.parent = parent;
		this.session = session;
		this.reg = reg;		
		this.itemRev = itemRev;
		this.tablePropName = tablePropName;
		this.tableRowType = tableRowType;	
		this.custTableItem = custTableItem;
		addListener();
	}


	@Override
	public void widgetSelected(SelectionEvent e) {
//		if (shell.isDisposed()) {                    
//            return; // Shell 已经被销毁，直接返回
//        }
		System.out.println("开始执行");
		Button button = (Button) e.getSource();
		String buttonName = button.getText();
		System.out.println("==>> buttonName: " + buttonName);
		if (reg.getString("SaveBtn.LABEL").equals(buttonName)) {
			save();
		} else if (reg.getString("CancelBtn.LABEL").equals(buttonName)) {
			cancel();
		} else if (reg.getString("AddBtn.LABEL").equals(buttonName)) {
			add();
		} else if (reg.getString("CopyBtn.LABEL").equals(buttonName)) {
			copy();
		} 
		else if (reg.getString("DeleteBtn.LABEL").equals(buttonName)) {
			delete();
		}
		
	}
	
	public void addListener() {
		compositListener();		
	}	

	
	/**
	 * 窗体组件的监听
	 */
	private void compositListener() {
		Table table = tv.getTable();
		
		addSortListener(table); // 排序分组监听
		
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {			
				
		        Point point = new Point(e.x, e.y); // 获取鼠标点击坐标
		        TableItem item = table.getItem(point);     
		        
		        if (item == null) {	// 判断是否点击了空白区域（没有选中的行）	            
		            table.deselectAll(); // 取消选择
		        }
			}
			
		});	
		
		
//		table.addListener(SWT.EraseItem, event -> { // 设置选中行的颜色
//			if ((event.detail & SWT.SELECTED) != 0) {
//		        event.gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION));
//		        event.gc.fillRectangle(event.getBounds());
//		        event.detail &= ~SWT.SELECTED;
//		    }
//			
////			TableItem item = (TableItem) event.item;
////        	EBOMApplyBean bean = (EBOMApplyBean) item.getData();
////        	if (!bean.isDeleteFlag()) {
////        		if ((event.detail & SWT.SELECTED) != 0 && table.isSelected(event.index)) {
////        	        // 如果是选中行，修改绘制的样式
////        	        event.gc.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
////        	        event.gc.setForeground(table.getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));        	        
////        	        event.detail &= ~SWT.SELECTED; // 取消默认的绘制效果
////        	    }
////			}
////	        if (bean.isDeleteFlag()) {
//////	        	 table.redraw(); // 需要更新删除线的显示状态，需要调用表格的redraw()方法来触发重绘
////	        	 event.detail &= ~SWT.HOT;
////	             event.detail &= ~SWT.SELECTED;
////	             Color redColor = Display.getCurrent().getSystemColor(SWT.COLOR_RED);	             
////	             event.gc.setForeground(redColor);
////	             event.gc.drawLine(event.x, event.y + event.height / 2,
////	                        event.x + event.width, event.y + event.height / 2);
////			} else {
////				table.redraw(); // 需要更新删除线的显示状态，需要调用表格的redraw()方法来触发重绘
////				event.detail &= ~SWT.HOT; // 取消删除线
////			}	        
//	        
//		});

		
		allComposite.addListener(SWT.MouseDown, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				if (event.button == 1) { // 左键被点击
                    System.out.println("左键被点击");
                } else if (event.button == 3) { // 右键被点击
                    System.out.println("右键被点击");
                }

                Point point = allComposite.toControl(event.x, event.y); // 获取鼠标点击坐标
                System.out.println("鼠标点击坐标: x=" + point.x + ", y=" + point.y);
                
                TableItem item = table.getItem(point); 
		        if (item == null) {	// 判断是否点击了空白区域（没有选中的行）	            
		        	table.deselectAll(); // 取消选择
		        }
			}
		});
		
		
		

		allComposite.addMouseMoveListener(new MouseMoveListener() {

		  @Override

		  public void mouseMove(MouseEvent e) { 
		    table.setRedraw(true); // 鼠标移动时设置setRedraw为true
		  }

		});

		 

		allComposite.addMouseTrackListener(new MouseTrackAdapter() {

		  @Override

		  public void mouseExit(MouseEvent e) {
		    table.setRedraw(false);  // 当鼠标离开table时设置setRedraw为false
		  }

		});		

		addPaintItemListener(table); // 添加PaintItem监听事件
		
	}
		
	
	/**
	 * 排序分组监听
	 * @param table
	 */
	public void addSortListener(Table table) {
		for (int columnIndex : sortColumnIndexArray) {
			TableColumn column = tv.getTable().getColumn(columnIndex);		
			column.addSelectionListener(new SelectionAdapter() {
			    @Override
			    public void widgetSelected(SelectionEvent event) {		    	
			        TableColumn clickedColumn = (TableColumn) event.widget; // 获取当前排序方向
			        boolean reverseOrder = tv.getTable().getSortColumn() == clickedColumn && tv.getTable().getSortDirection() == SWT.UP;		        
			        
			        sortData(reverseOrder); // 执行排序操作
			        
			        tv.getTable().setSortColumn(clickedColumn); // 更新表格排序方向
			        tv.getTable().setSortDirection(reverseOrder ? SWT.DOWN : SWT.UP);
			    }
			});				
		}
	}

	private void sortData(boolean reverseOrder) {		
		List<EBOMApplyRowBean> dataList =  (List<EBOMApplyRowBean>) tv.getInput();  // 获取表格绑定的数据列表
	    Comparator<EBOMApplyRowBean> comparator = (data1, data2) -> { // 使用自定义比较器进行排序
	        // 根据需要的列和排序顺序实现比较逻辑
	        // 这里假设 MyData 类有相应的 getter 方法来获取对应列的值
	        Comparable value1 = data1.getSequence();
	        Comparable value2 = data2.getSequence();
	        return reverseOrder ? value2.compareTo(value1) : value1.compareTo(value2);
	    };	    
	    
	    Collections.sort(dataList, comparator); // 对数据列表进行排序
	    
	    
	    tv.setInput(dataList); // 更新 TableViewer 的输入数据
	}

	
	
	
	
	private void addPaintItemListener(Table table) {
        table.addListener(SWT.PaintItem, event -> { 
//        	if ((event.detail & SWT.SELECTED) != 0) {
//		        event.gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION));
//		        event.gc.fillRectangle(event.getBounds());
//		        event.detail &= ~SWT.SELECTED;
//		    }
        	
        	TableItem item = (TableItem) event.item;
        	EBOMApplyRowBean bean = (EBOMApplyRowBean) item.getData();
//        	TableColumn column = item.getParent().getColumn(event.index); 
//            String text = column.getText(); // 标题绘制时处理
//            if (text.endsWith("*")) {
//            	event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_RED));
//                event.gc.drawString(text.substring(0, text.length() - 1), event.x, event.y);
//                event.gc.drawText("*", event.x + event.gc.stringExtent(text.substring(0, text.length() - 1)).x, event.y, true);
//			}            
			
        	if (bean == null) {
				return;
			}
        	if (!bean.isSelect()) {
				return;
			}
//        	table.redraw();
        	if (bean.isDeleteFlag()) {
//        		table.redraw(); // 需要更新删除线的显示状态，需要调用表格的redraw()方法来触发重绘
        		event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_RED));
                event.gc.setLineStyle(SWT.LINE_SOLID); 
                
                Rectangle bounds = item.getBounds();
                int y = bounds.y + bounds.height / 2;
                for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
                    Rectangle cellBounds = item.getBounds(columnIndex);
                    int x1 = cellBounds.x;
                    int y1 = y;
                    int x2 = cellBounds.x + cellBounds.width - 1;
                    int y2 = y;
                    event.gc.drawLine(x1, y1, x2, y2);
                }
        	} else { 
        		table.redraw();
        		event.detail &= ~SWT.HOT; // 取消删除线    
//				event.gc.setForeground(table.getForeground());
			}
        });        
        
	}
	
	
	private void add() {
		
		TableTools.addRow(tv, tablePropName); // 添加行		
	}
	
	
	private void copy() {
		List<EBOMApplyRowBean> copyList = TableTools.getCustNodes(tv);
		if (CommonTools.isEmpty(copyList)) {
			TCUtil.warningMsgBox(reg.getString("SelectWarn3.MSG"), reg.getString("WARNING.MSG"));
			return;
		}
		
		TableTools.copyRow(tv, tablePropName, copyList);
	}
	
	
	private void delete() {
		List<EBOMApplyRowBean> deleteList = TableTools.getList(TableTools.getCustNodes(tv), false);
		if (CommonTools.isEmpty(deleteList)) {
			TCUtil.warningMsgBox(reg.getString("SelectWarn2.MSG"), reg.getString("WARNING.MSG"));
			return;
		}
		
		TableTools.deleteRow(deleteList);
		
		Table table = tv.getTable();
		table.setRedraw(true);
		table.redraw(); // 强制重绘表格
		addPaintItemListener(table);
	}
	
	/**
	 * 保存table行
	 */
	private void save() {
		
		List<EBOMApplyRowBean> list = checkData();
		if (CommonTools.isEmpty(list)) {
			return;
		}
		
		Table table = tv.getTable();
		table.setRedraw(false); // 关闭绘制，避免闪烁
		
		custTableItem.setUpStatusBar(tableComposite, reg.getString("ProgressBar2.TITLE"), true);
		
		custTableItem.updateProgressBarBySave(allComposite, list);
		
		table.setRedraw(true); // 打开绘制，避免闪烁
	}
		
	
	
	
	
	/**
	 * 
	 * @return
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private List<EBOMApplyRowBean> checkData() {
		List<EBOMApplyRowBean> input = (List<EBOMApplyRowBean>) tv.getInput();
		if (CommonTools.isEmpty(input)) {
			TCUtil.warningMsgBox(reg.getString("SaveWarn2.MSG"), reg.getString("WARNING.MSG"));
			return null;
		}
		List<EBOMApplyRowBean> totalList = new ArrayList<EBOMApplyRowBean>();
		for (EBOMApplyRowBean bean : input) {
			totalList.add(bean);
		}
		
		List<String> msgResult = checkRequired(totalList);
		if (CommonTools.isNotEmpty(msgResult)) {
			TCUtil.warningMsgBox(reg.getString(msgResult.stream().collect(Collectors.joining("\n"))), reg.getString("WARNING.MSG"));
			return null;
		}
		if (CommonTools.isEmpty(totalList)) {
			TCUtil.warningMsgBox(reg.getString("SaveWarn2.MSG"), reg.getString("WARNING.MSG"));
			return totalList;
		}
		
		totalList.removeIf(bean -> bean.isAdd() && bean.isDeleteFlag()); // 移除是新增的记录，并且表示为待删除
		if (CommonTools.isEmpty(totalList)) {
			TCUtil.warningMsgBox(reg.getString("SaveWarn3.MSG"), reg.getString("WARNING.MSG"));
			return totalList;
		}
		
		totalList.removeIf(bean -> !bean.isHasModify()); // 移除是未发生修改内容的记录
		if (CommonTools.isEmpty(totalList)) {
			TCUtil.warningMsgBox(reg.getString("SaveWarn4.MSG"), reg.getString("WARNING.MSG"));
			return totalList;
		}
		
		return totalList;
	}
	
	
	/**
	 * 校验必填项
	 * @param list
	 * @return
	 */
	private List<String> checkRequired(List<EBOMApplyRowBean> list) {
		List<String> msgResult = new ArrayList<String>();
		try {
			for (int i = 0; i < list.size(); i++) {
				EBOMApplyRowBean bean = list.get(i);
				if (bean.isDeleteFlag()) { // 添加删除线的行不校验
					continue;
				}
				Field[] fields = bean.getClass().getDeclaredFields();
				List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
				fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
				fieldList.removeIf(field -> field.getType() == boolean.class);
				fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class).required() == false); // 移除不是必填字段
				for (Field field : fieldList) {
					field.setAccessible(true);
					TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
					
//					String val = "";
					Object value = field.get(bean); // 获取原始值					
					if (value == null || "".equals(value)) {
						msgResult.add(reg.getString("SaveWarn1.MSG").split("&&")[0] + (i + 1) + reg.getString("SaveWarn1.MSG").split("&&")[1] + tcProp.columnName() + "," + reg.getString("SaveWarn1.MSG").split("&&")[2]);
					}					
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return msgResult;
	}
	
	/**
	 * 保存数据
	 */
	public boolean saveData(List<EBOMApplyRowBean> totalList) {
		boolean flag = true;		
		TCUtil.setBypass(session); // 开启旁路
		try {
			
			TCUtil.checkIn(session, itemRev); // 判断是否需要签入对象版本			
			ListIterator listIterator = totalList.listIterator();
			while (listIterator.hasNext()) {
				EBOMApplyRowBean bean = (EBOMApplyRowBean) listIterator.next();
				TCComponentFnd0TableRow row = bean.getRow();				
				if (row != null) {
					if (bean.isDeleteFlag()) {
						itemRev.remove(tablePropName, row); // 移除行
					} else {
						row.setProperties(getStrProps(TableTools.getTCStrPropMap(bean))); // 更新属性值		
						saveSequenceProp(row, bean); // 更新序列号值						
					}
					listIterator.remove();
				} else {
					continue;
				}
				
			}
			
			itemRev.refresh();
			TCComponent[] relatedComponents = itemRev.getRelatedComponents(tablePropName);
			int length = relatedComponents.length;
			
			if (CommonTools.isEmpty(totalList)) {
				return flag;
			}
			List<Map<String, Object>> newList = new ArrayList<Map<String,Object>>();
			for (EBOMApplyRowBean newBean : totalList) {
				newList.add(TableTools.getTCStrPropMap(newBean));		
				
			}	
			// 批量创建Table Row对象
			TCComponent[] createObjs = TCUtil.createObjects(session, tableRowType, newList); // 批量创建Row对象
			if (CommonTools.isNotEmpty(createObjs)) {
				itemRev.add(tablePropName, createObjs, length); // 将记录追加到table属性中
				
			}		
		} catch (Exception e) {
			e.printStackTrace();			
			flag = false;
			TCUtil.errorMsgBox(e.getLocalizedMessage(), reg.getString("ERROR.MSG"));			
		}
		
		TCUtil.closeBypass(session); // 开启旁路
		return flag;
	}
	
	
	
	private Map<String, String> getStrProps(Map<String, Object> map) {
		Map<String, String> strMap = new HashMap<String, String>();
		map.forEach((key, value) -> {
			if (value instanceof String) {
				strMap.put(key, value == null ? "" : value.toString());
			} 
		});
		
		return strMap;
	}
	
	
	private void saveSequenceProp(TCComponentFnd0TableRow row, EBOMApplyRowBean bean) throws TCException, IllegalArgumentException, IllegalAccessException {
		Field[] fields = bean.getClass().getDeclaredFields();
		List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
		fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
		fieldList.removeIf(field -> field.getType() == boolean.class);
		fieldList.removeIf(field -> field.getType() != Integer.class);
		
		System.out.println("start -->>  saveSequenceProp");
		
		for (Field field : fieldList) {
			field.setAccessible(true);
			TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
			if (tcProp != null) {
				String propertyName = tcProp.tcProperty();
				Object value = field.get(bean);
				
				if (CommonTools.isNotEmpty(propertyName)) {
					if (row.isValidPropertyName(propertyName)) {
//						TCProperty tcProperty = row.getTCProperty(propertyName);
						 if (value != null && !value.equals("") ) {
								Integer intValue = (Integer) value;
//								intMap.put(key, BigInteger.valueOf(intValue));
//								tcProperty.setIntValue(BigInteger.valueOf(intValue));
//								tcProperty.setIntValue(intValue);
								row.setIntProperty(propertyName, intValue);
						}
						
					}
				}
			}
		}
	}
	
	
	/**
	 * 取消按钮
	 */
	private void cancel() {		
		parent.dispose();
	}
	
}
