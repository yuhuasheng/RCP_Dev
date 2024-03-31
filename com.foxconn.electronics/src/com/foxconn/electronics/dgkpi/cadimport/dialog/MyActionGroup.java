package com.foxconn.electronics.dgkpi.cadimport.dialog;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.actions.ActionGroup;
import com.foxconn.tcutils.util.CommonTools;
import com.teamcenter.rac.util.Registry;

public class MyActionGroup extends ActionGroup {

	private StyledText styledText = null;
	private Registry reg = null;
	
	
	public MyActionGroup(StyledText styledText, Registry reg) {
		super();
		this.styledText = styledText;
		this.reg = reg;
	}


	@Override
	public void fillContextMenu(IMenuManager mgr) {
		// 加入Action对象到菜单管理器
		MenuManager menuManager = (MenuManager) mgr;
		final ClearEntryAction clearEntryAction = new ClearEntryAction();
		final SelectAllAction selectAllAction = new SelectAllAction();
		menuManager.setRemoveAllWhenShown(true); // 设置"在每次显示之前先删除全部老菜单"的属性为true
		menuManager.addMenuListener(new IMenuListener() { // 彈出式菜單觸發
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(selectAllAction);
				manager.add(clearEntryAction);
				if (CommonTools.isNotEmpty(styledText.getText())) {
					clearEntryAction.setEnabled(true);
					selectAllAction.setEnabled(true);
				} else {
					clearEntryAction.setEnabled(false);
					selectAllAction.setEnabled(false);
				}
			}
		});
		
		menuManager.add(new ClearEntryAction());		
		Menu menu = menuManager.createContextMenu(styledText);
		styledText.setMenu(menu);
	}
	
	private class SelectAllAction extends Action {
		
		public SelectAllAction() {
			setText(reg.getString("RightSelectAllBtn.LABEL"));			
		}
		
		public void run() {
			if (CommonTools.isNotEmpty(styledText.getText())) {
				styledText.selectAll();
			}
		}
	}
	
	private class ClearEntryAction extends Action {
		
		public ClearEntryAction() {
			setText(reg.getString("RightClearBtn.LABEL"));
		}
		
		public void run() {
			if (CommonTools.isNotEmpty(styledText.getText())) {
				styledText.setText("");
			}
		}
	}
}
