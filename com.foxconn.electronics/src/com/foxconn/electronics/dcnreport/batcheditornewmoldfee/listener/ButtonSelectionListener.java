package com.foxconn.electronics.dcnreport.batcheditornewmoldfee.listener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.constant.NewMoldFeeConstant;
import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.dialog.BatchEditorNewMoldFeeDialog;
import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.domain.NewMoldFeeBean;
import com.foxconn.electronics.dcnreport.batcheditornewmoldfee.util.Tools;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCPropertes;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;
import com.teamdev.jxbrowser.deps.org.checkerframework.checker.units.qual.m;

public class ButtonSelectionListener extends SelectionAdapter {
	
	private TCSession session = null;
	private Shell shell = null;
	private TableViewer tv = null;
	private Registry reg = null;
	private Text filterText = null;
	private BatchEditorNewMoldFeeDialog dialog = null;
	
	
	public ButtonSelectionListener(Shell shell, TCSession session, TableViewer tv, Registry reg, Text filterText, BatchEditorNewMoldFeeDialog dialog) {
		super();
		this.shell = shell;
		this.session = session;
		this.tv = tv;		
		this.reg = reg;
		this.filterText = filterText;
		this.dialog = dialog;
		addListener();
	}



	@Override
	public void widgetSelected(SelectionEvent e) {
		Button button = (Button) e.getSource();
		String buttonName = button.getText();
		System.out.println("==>> buttonName: " + buttonName);
		if (reg.getString("SaveBtn.LABEL").equals(buttonName)) {
			save();
		} else if (reg.getString("CancelBtn.LABEL").equals(buttonName)) {
			cancel(e);
		}
	}
	
	private void save() {
		List<NewMoldFeeBean> data = checkData();
		if (CommonTools.isEmpty(data)) {
			return;
		}
		
		dialog.setUpStatusBar(null, reg.getString("ProgressBar2.TITLE"), true);
		
		dialog.updateProgressBarBySave(data);
	}
	
	/**
	 * 校验数据
	 * @return
	 */
	private List<NewMoldFeeBean> checkData() {
		@SuppressWarnings("unchecked")
		List<NewMoldFeeBean> input = (List<NewMoldFeeBean>) tv.getInput();
		if (CommonTools.isEmpty(input)) {
			TCUtil.warningMsgBox(reg.getString("SaveWarn1.MSG"), reg.getString("WARNING.MSG"));
			return null;
		}
		List<NewMoldFeeBean> totalList = new ArrayList<NewMoldFeeBean>();
		for (NewMoldFeeBean bean : input) {
			totalList.add(bean);
		}
		
		List<String> msgResult = checkRequired(totalList);
		if (CommonTools.isNotEmpty(msgResult)) {
			TCUtil.warningMsgBox(reg.getString(msgResult.stream().collect(Collectors.joining("\n"))), reg.getString("WARNING.MSG"));
			return null;
		}
		
		totalList.removeIf(bean -> !bean.isHasModify() && bean.getForm() != null); // 移除未发生修改内容，并且form表单存在
		if (CommonTools.isEmpty(totalList)) {
			TCUtil.warningMsgBox(reg.getString("SaveWarn3.MSG"), reg.getString("WARNING.MSG"));
			return null;
		}
		
		return totalList;
	}
	
	
	/**
	 * 校验必填项
	 * @param list
	 * @return
	 */
	private List<String> checkRequired(List<NewMoldFeeBean> list) {
		List<String> msgResult = new ArrayList<String>();
		try {
			for (int i = 0; i < list.size(); i++) {
				NewMoldFeeBean bean = list.get(i);
				Field[] fields = bean.getClass().getDeclaredFields();
				List<Field> fieldList = new ArrayList<Field>(Arrays.asList(fields));
				fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class) == null);
				fieldList.removeIf(field -> field.getType() == boolean.class);
				fieldList.removeIf(field -> field.getAnnotation(TCPropertes.class).required() == false); // 移除不是必填字段
				
				for (Field field : fieldList) {
					field.setAccessible(true);
					TCPropertes tcProp = field.getAnnotation(TCPropertes.class);
					
					Object value = field.get(bean); // 获取原始值
					if (value == null || "".equals(value)) {
						msgResult.add(reg.getString("SaveWarn2.MSG").split("&&")[0] + (i + 1) + reg.getString("SaveWarn2.MSG").split("&&")[1] + tcProp.columnName() + "," + reg.getString("SaveWarn2.MSG").split("&&")[2]);
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
	 * @param list
	 * @return
	 */
	public boolean saveData(List<NewMoldFeeBean> list) {
		boolean flag = true;
		TCUtil.setBypass(session);
		try {
			List<NewMoldFeeBean> filterList = list.stream().filter(bean -> {
				TCComponentForm form = bean.getForm();
				if (form != null) {
					try {
						form.setProperties(Tools.getStrPropMap(bean)); // 设置属性
					} catch (IllegalArgumentException | IllegalAccessException | TCException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					} 
					return false;
				} else {
					return true;
				}
			}).collect(Collectors.toList());
			
			
			if (CommonTools.isEmpty(filterList)) {
				return flag;
			}
			
			for (NewMoldFeeBean bean : filterList) {
				TCComponentItemRevision itemRev = bean.getItemRev();
				List<Map<String, Object>> newList = new ArrayList<Map<String,Object>>();
				Map<String, String> strPropMap = Tools.getStrPropMap(bean); // 获取属性
				Map<String, Object> paramsMap = new HashMap<String, Object>();
				strPropMap.forEach((key, value) -> {					
					paramsMap.put(key, value);					
				});
				
				newList.add(paramsMap);
				
				TCComponent[] createObjects = TCUtil.createObjects(session, NewMoldFeeConstant.D9_MoldInfo, newList);
				if (CommonTools.isNotEmpty(createObjects)) {
					itemRev.add(NewMoldFeeConstant.IMAN_REFERENCE, createObjects); // 将表单添加到对象版本下的引用文件夹下
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
			TCUtil.errorMsgBox(e.getLocalizedMessage(), reg.getString("ERROR.MSG"));
		}
		
		TCUtil.closeBypass(session); // 开启旁路
		return flag;
	}
	
	
  	private void addListener() {
		Table table = tv.getTable();
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				Point point = new Point(e.x, e.y); // 获取鼠标点击的坐标
				TableItem item = table.getItem(point); 
				if (item == null) { // 判断是否点击了空白区域(没有选中行)
					table.deselectAll(); // 取消选择
				}
			}
			
		});	
		
		table.addListener(SWT.EraseItem, event -> {
			if ((event.detail & SWT.SELECTED) != 0) {
				event.gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_SELECTION));
				event.gc.fillRectangle(event.getBounds());
				event.detail &= ~SWT.SELECTED;
			}
		});
		
		tv.addFilter(new ViewerFilter() {
			// viewer 是tableViewer对象
			// parentElement 是一个包含全部记录的list集合
			// element 当前传入的记录，需要判断是否过滤它
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof NewMoldFeeBean) {
					NewMoldFeeBean bean = (NewMoldFeeBean) element;
					String filter = filterText.getText().trim(); // 获取过滤文本框中的关键字
					if (filter.isEmpty()) {
		                return true; // 若关键字为空，则显示所有数据
		            }
//					System.out.println(bean.toString());
					return bean.getValue().toLowerCase().contains(filter.toLowerCase());
				}
				return true;
			}
		});
		
		filterText.addModifyListener(e -> { // 添加过滤文本框的事件监听器
			tv.refresh(); // 每次文本框内容变化时刷新表格
		});
		
		
		shell.addShellListener(new ShellAdapter() {

			@Override
			public void shellClosed(ShellEvent e) {
				if (dialog.isRunning()) {
					MessageDialog.openWarning(shell, reg.getString("WARNING.MSG"), reg.getString("SaveWarn4.MSG"));
					e.doit = false;
				} else {
					boolean flag = MessageDialog.openQuestion(shell, reg.getString("INFORMATION.MSG"), reg.getString("CloseTip.MSG"));
					System.out.println("==>> flag: " + flag);
					e.doit = flag == true;
					if (e.doit) {
						shell.dispose();
					}
				}
			}
			
		});
	}
  	
  	private void cancel(SelectionEvent e) {
  		if (dialog.isRunning()) {
  			MessageDialog.openWarning(shell, reg.getString("WARNING.MSG"), reg.getString("SaveWarn4.MSG"));
  			e.doit = false;
  		} else {
  			boolean flag = MessageDialog.openQuestion(shell, reg.getString("INFORMATION.MSG"), reg.getString("CloseTip.MSG"));
  			System.out.println("==>> flag: " + flag);
			e.doit = flag == true;
			if (e.doit) {
				shell.dispose();
			}
		}
  	}
}
