package com.foxconn.sdebom.batcheditorebom.dialog;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.foxconn.sdebom.batcheditorebom.constants.LovEnum;
import com.foxconn.sdebom.batcheditorebom.custtree.CustTreeNode;
import com.foxconn.sdebom.batcheditorebom.domain.SDEBOMBean;
import com.foxconn.sdebom.batcheditorebom.util.LazyTreeTools;
import com.foxconn.sdebom.batcheditorebom.util.TreeTools;
import com.foxconn.tcutils.progress.BooleanFlag;
import com.foxconn.tcutils.progress.IProgressDialogRunnable;
import com.foxconn.tcutils.progress.LoopProgerssDialog;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.Registry;

public class ButtonSelectionListener extends SelectionAdapter {
	
	private AbstractPSEApplication app = null;
	private TCSession session = null;
	private TreeViewer treeViewer = null;
	private Shell shell = null;
	private SDEBOMBean rootData = null;
	private Registry reg = null;
	private TreeTools treeTools = null;
	private Boolean saveFlag = null; // 保存标识,
	private static final String[] BOM_ATTRI = {"assemblyStatus"};
	
	public ButtonSelectionListener(AbstractPSEApplication app, TreeViewer treeViewer, Shell shell, SDEBOMBean rootData, Registry reg, TreeTools treeTools) {
		super();
		this.app = app;
		this.session = (TCSession) app.getSession();
		this.treeViewer = treeViewer;
		this.shell = shell;
		this.rootData = rootData;
		this.reg = reg;
		this.treeTools = treeTools;
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
			cancel();
		} else if (reg.getString("ExpandAllBtn.LABEL").equals(buttonName)) {
			expandAll();
		} else if (reg.getString("CollapseAllBtn.LABEL").equals(buttonName)) {
			collapseAll();
		}
	}
	
	/**
	 * 添加监听
	 */
	private void addListener() {
		shellListener();
	}
	
	private void save() {
		LoopProgerssDialog progerssDialog = new LoopProgerssDialog(shell, null, reg.getString("SaveProgressDialog2.TITLE"));
		progerssDialog.run(true, new IProgressDialogRunnable() {
			
			@Override
			public void run(BooleanFlag stopFlag) {
				System.out.println("********** 开始执行保存操作 **********");
				try {
					if (stopFlag.getFlag()) { // 监控是否要让停止后台任务
						System.out.println("被中断了");
						saveFlag = false;
						return;
					}
					
					TCUtil.setBypass(session); // 开启旁路					
					saveProperties(rootData); // 保存属性
					
					stopFlag.setFlag(true); // 执行完毕把标识位设置为停止，好通知给进度框
					saveFlag = true;
				} catch (Exception e) {
					e.printStackTrace();
					TCUtil.errorMsgBox(reg.getString("SaveError.MSG"), reg.getString("ERROR.MSG"));
					stopFlag.setFlag(true);
					saveFlag = false;
				}
				
				TCUtil.closeBypass(session); // 关闭旁路
				System.out.println("********** 执行保存结束 **********");
			}
		});
		
		if (saveFlag != null && saveFlag) {
//			expandAll();
			TCUtil.infoMsgBox(reg.getString("SaveSuccess.MSG"), reg.getString("INFORMATION.MSG"));
		}
	}
	
	/**
	 * 保存属性
	 * @param bean
	 * @throws Exception
	 */
	private void saveProperties(SDEBOMBean bean) throws Exception {		
		if (!treeTools.anyMatch(bean.getObjectType())) { // 对于是虚拟阶对象类型，不需要执行保存动作
			Map<String, String> propMap = TCUtil.getSavePropMap(bean, BOM_ATTRI);		
			Map<String, String> newPropMap = propMap.entrySet().stream().collect(Collectors.toMap((e) -> e.getKey(), (e) -> LazyTreeTools.getAssemblyStatusEnum(e.getValue(), false).actualValue()));	
			TCComponentBOMLine bomLine = bean.getBomLine();			
			try {	
				if (!bean.getSub()) { // 判断是否为替代料
					bomLine.setProperties(newPropMap);
				}				
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("==>> item_id: " + bean.getItemId());					
			}						
		}
		
		List<SDEBOMBean> childs = bean.getChilds();
		if (CommonTools.isEmpty(childs)) {
			return;
		}
		
		for (SDEBOMBean childBean : childs) {
			saveProperties(childBean);
		}		
	}	
	
	
 	private void cancel() {
		MessageBox msgBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		msgBox.setText(reg.getString("MessageBox.TITLE"));
		msgBox.setMessage(reg.getString("MessageBox.MSG"));
		
		int rc = msgBox.open();
		if (rc == SWT.YES) {
			System.out.println("您单击了 “是” 按钮");
			shell.dispose();
		} else if (rc == SWT.NO) {
			System.out.println("您单击了 “否” 按钮");
		}
	}
	
	private void expandAll() {
		TreeItem topItem = treeViewer.getTree().getItems()[0]; // 获取树的顶层		
		CustTreeNode topTreeNode = (CustTreeNode) topItem.getData();
		treeTools.expandAllTreeNode(treeViewer, topTreeNode);
//		treeTools.resetTreeColumn(treeViewer);
//		TCUtil.infoMsgBox(reg.getString("ExpandSuccess.MSG"), reg.getString("INFORMATION.MSG"));
	}
	
	
	/**
	 * 收缩所有结点
	 */
	private void collapseAll() {
		treeViewer.collapseAll();
	}
	
	
	private void shellListener() {
		shell.addShellListener(new ShellAdapter() {
			// 监听关闭窗体事件
			public void shellClosed(ShellEvent e) {
				MessageBox msgBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				msgBox.setText(reg.getString("MessageBox.TITLE"));
				msgBox.setMessage(reg.getString("MessageBox.MSG"));
	
				int rc = msgBox.open();
				e.doit = rc == SWT.YES;
				if (e.doit) {
					System.out.println("您单击了 “是” 按钮");
					shell.dispose();
				} else {
					System.out.println("您单击了 “否” 按钮");
				}
			}
		});
	}

	
}
