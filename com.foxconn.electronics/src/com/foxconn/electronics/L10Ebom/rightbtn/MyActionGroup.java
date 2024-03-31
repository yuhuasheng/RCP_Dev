package com.foxconn.electronics.L10Ebom.rightbtn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.actions.ActionGroup;

import com.foxconn.electronics.L10Ebom.constant.ApplyFormConstant;
import com.foxconn.electronics.L10Ebom.dialog.CustTableItem;
import com.foxconn.electronics.L10Ebom.domain.EBOMApplyRowBean;
import com.foxconn.electronics.L10Ebom.domain.FinishMatRowBean;
import com.foxconn.electronics.L10Ebom.util.TableTools;
import com.foxconn.tcutils.util.CommonTools;
import com.teamcenter.rac.util.Registry;

public class MyActionGroup extends ActionGroup {

	private TableViewer tv = null;
	private Registry reg = null;
	private List<EBOMApplyRowBean> custNodeList = null;
	private List<EBOMApplyRowBean> deleteList = null;
	private List<EBOMApplyRowBean> recoveryList = null;
	private CustTableItem custTableItem = null;
	private String tablePropName = null;
	
	public MyActionGroup(TableViewer tv, Registry reg, String tablePropName, CustTableItem custTableItem) {
		super();
		this.tv = tv;
		this.reg = reg;
		this.tablePropName = tablePropName;
		this.custTableItem = custTableItem;
	}
	
	public void fillContextMenu(IMenuManager mgr) {
		// 加入Action对象到菜单管理器
		MenuManager menuManager = (MenuManager) mgr; // 类型转换
		final AddAction addAction = new AddAction(); // 添加
		final DeleteAction deleteAction = new DeleteAction(); // 删除
		final RecoveryAction recoveryAction = new RecoveryAction(); // 恢复
		final CopyAction copyAction = new CopyAction(); // 复制		
		menuManager.setRemoveAllWhenShown(true); // 设置"在每次显示之前先删除全部老菜单"的属性为true
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager imenumanager) {
				custNodeList = TableTools.getCustNodes(tv);
				if (CommonTools.isEmpty(custNodeList)) {
					return;
				}
				
				menuManager.add(addAction);
				menuManager.add(copyAction);
				
				deleteList = TableTools.getList(custNodeList, false);
				if (CommonTools.isNotEmpty(deleteList)) {
					menuManager.add(deleteAction);
				}				
				
				recoveryList = TableTools.getList(custNodeList, true);
				if (CommonTools.isNotEmpty(recoveryList)) {
					menuManager.add(recoveryAction);
				}
			}
			
		});
		
		Table table = tv.getTable();
		Menu menu = menuManager.createContextMenu(table);
		table.setMenu(menu);
	}	
	
	private class AddAction extends Action {

		public AddAction() {
			setText(reg.getString("RightAddBtn.LABEL"));			
		}
		
		public void run() {			
			TableTools.addRow(tv, tablePropName); // 添加行		
		}		
		
	}
	
	
	private class DeleteAction extends Action {
		
		public DeleteAction() {
			setText(reg.getString("RightDeleteBtn.LABEL"));			
		}
		
		public void run() {
			TableTools.deleteRow(deleteList);
			tv.refresh();
		}
	}
	
	
	private class RecoveryAction extends Action {
		public RecoveryAction() {
			setText(reg.getString("RightRecoveryBtn.LABEL"));
		}
		
		
		public void run() {			
			TableTools.recoveryRow(recoveryList);
			tv.refresh();
		}
	}


	private class CopyAction extends Action {

		public CopyAction() {
			setText(reg.getString("RightCopyBtn.LABEL"));			
		}
		
		public void run() {
			TableTools.copyRow(tv, tablePropName, custNodeList);
		}
	}	
}
