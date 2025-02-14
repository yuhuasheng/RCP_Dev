package com.foxconn.mechanism.dtpac.matmaintain.rightbtn;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.ActionGroup;

import com.foxconn.mechanism.dtpac.matmaintain.dialog.PACMatMaintainDialog;
import com.foxconn.mechanism.dtpac.matmaintain.domain.MatMaintainBean;
import com.foxconn.mechanism.dtpac.matmaintain.util.TableTools;
import com.foxconn.tcutils.util.CommonTools;
import com.teamcenter.rac.util.Registry;

public class MyActionGroup extends ActionGroup {

	private TableViewer tv = null;
	private Registry reg = null;
	private PACMatMaintainDialog dialog = null;
	private List<MatMaintainBean> custNodeList = null;
	private List<MatMaintainBean> deleteList = null;
	private List<MatMaintainBean> recoveryList = null;
	private List<MatMaintainBean> cacheDataList = null;
	
	public MyActionGroup(TableViewer tv, Registry reg, PACMatMaintainDialog dialog) {
		super();
		this.tv = tv;
		this.reg = reg;
		this.dialog = dialog;
	}
	
	public void fillContextMenu(IMenuManager mgr) {
		// 加入Action对象到菜单管理器
		MenuManager menuManager = (MenuManager) mgr; // 类型转换
		final AddAction addAction = new AddAction(); // 添加
		final DeleteAction deleteAction = new DeleteAction(); // 删除
		final RecoveryAction recoveryAction = new RecoveryAction(); // 恢复
		menuManager.setRemoveAllWhenShown(true); // 设置"在每次显示之前先删除全部老菜单"的属性为true
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager imenumanager) {
				custNodeList = TableTools.getCustNodes(tv);
				if (CommonTools.isEmpty(custNodeList)) {
					return;
				}
				
				menuManager.add(addAction);
				
				deleteList = TableTools.getList(custNodeList, false);
				if (CommonTools.isNotEmpty(deleteList)) {
					menuManager.add(deleteAction);
				}				
				
				recoveryList = TableTools.getList(custNodeList, true);
				if (CommonTools.isNotEmpty(recoveryList)) {
					menuManager.add(recoveryAction);
				}
				
				cacheDataList = dialog.getCacheDataList();				
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
			TableTools.addRow(tv, dialog, reg);
			tv.refresh();
		}		
	}
	
	
	private class DeleteAction extends Action {
		
		public DeleteAction() {
			setText(reg.getString("RightDeleteBtn.LABEL"));			
		}
		
		public void run() {
			TableTools.deleteRow(deleteList);
			TableTools.updateCacheDataList(cacheDataList, deleteList);
			tv.refresh();
		}
	}
	
	private class RecoveryAction extends Action {
		public RecoveryAction() {
			setText(reg.getString("RightRecoveryBtn.LABEL"));
		}
		
		
		public void run() {		
			TableTools.recoveryRow(recoveryList);
			TableTools.updateCacheDataList(cacheDataList, recoveryList);
			tv.refresh();
		}
	}
}
