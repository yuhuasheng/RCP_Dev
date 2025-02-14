package com.foxconn.sdebom.batcheditorebom.dialog;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

import com.foxconn.sdebom.batcheditorebom.custtree.LazyCustTreeNode;
import com.foxconn.sdebom.batcheditorebom.domain.SDEBOMBean;
import com.foxconn.sdebom.batcheditorebom.util.LazyTreeTools;
import com.foxconn.tcutils.progress.BooleanFlag;
import com.foxconn.tcutils.progress.IProgressDialogRunnable;
import com.foxconn.tcutils.progress.LoopProgerssDialog;
import com.foxconn.tcutils.progress.ProgressBarThread;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.Registry;

public class LazyButtonSelectionListener extends SelectionAdapter {

	private AbstractPSEApplication app = null;
	private TCSession session = null;
	private TreeViewer treeViewer = null;
	private Shell shell = null;
	private SDEBOMBean rootData = null;
	private Registry reg = null;
	private Boolean saveFlag = null; // 保存标识,
	private static final String[] BOM_ATTRI = {"assemblyStatus"};
	private ExecutorService es = null;
	
	public LazyButtonSelectionListener(AbstractPSEApplication app, TreeViewer treeViewer, Shell shell, Registry reg, ExecutorService es) {
		super();
		this.app = app;
		this.session = (TCSession) app.getSession();
		this.treeViewer = treeViewer;
		this.shell = shell;		
		this.reg = reg;		
		this.es = es;
		addListener();
	}
	
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
		TreeItem topItem = treeViewer.getTree().getItems()[0]; // 获取树的顶层
		LazyCustTreeNode topTreeNode = (LazyCustTreeNode) topItem.getData();
		
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
					saveProperties(topTreeNode);
					
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
		
		if (saveFlag == true) {
//			expandAll();
			TCUtil.infoMsgBox(reg.getString("SaveSuccess.MSG"), reg.getString("INFORMATION.MSG"));
		}
	}
	
	/**
	 * 保存属性
	 * @param custTreeNode
	 * @throws Exception
	 */
	private void saveProperties(LazyCustTreeNode custTreeNode) throws Exception {
		SDEBOMBean bean = custTreeNode.getData();
		if (!LazyTreeTools.anyMatch(bean.getObjectType()) && bean.getHasModify() 
				&& !bean.getSub()) { // 执行保存动作的前提是当前节点不是虚拟阶，并且属性已经发生修改，不为替代来哦
			Map<String, String> propMap = TCUtil.getSavePropMap(bean, BOM_ATTRI);
			Map<String, String> newPropMap = propMap.entrySet().stream().collect(Collectors.toMap((e) -> e.getKey(), (e) -> LazyTreeTools.getAssemblyStatusEnum(e.getValue(), false).actualValue()));	
			TCComponentBOMLine bomLine = bean.getBomLine();	
			try {				
				bomLine.setProperties(newPropMap);	
				bomLine.refresh();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("==>> item_id: " + bean.getItemId() + ", 修改属性失败!");
			}
		}
		
		List<LazyCustTreeNode> childs = custTreeNode.getChilds();
		if (CommonTools.isEmpty(childs)) {
			return;
		}
		
		for (LazyCustTreeNode childCustTreeNode : childs) {
			saveProperties(childCustTreeNode);			
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
		LazyCustTreeNode topTreeNode = (LazyCustTreeNode) topItem.getData();
		LoopProgerssDialog progerssDialog = new LoopProgerssDialog(new Shell(), null, "正在加载，请稍后...");		
		progerssDialog.run(true, new IProgressDialogRunnable() {
			
			@Override
			public void run(BooleanFlag stopFlag) {
				 Display.getDefault().asyncExec(new Runnable() {
					 
					@Override
					public void run() {
						LazyTreeTools.customExpandAll(treeViewer, topTreeNode, es);							
					}
				});
				 
				stopFlag.setFlag(true); // 执行完毕把标识位设置为停止，好通知给进度框
			}
		});		
		
		
		
//		LazyTreeTools.customExpandAll(treeViewer, topTreeNode);
		
//		 thread = new Thread(new Runnable()
//		    {
//		        private int                 progress    = 0;
//		        private static final int    INCREMENT   = 30;
//
//		        @Override
//		        public void run()
//		        {
//		            while (!progressBar.isDisposed())
//		            {
//		                Display.getDefault().asyncExec(new Runnable()
//		                {
//		                    @Override
//		                    public void run()
//		                    {
//		                        if (!progressBar.isDisposed())
//		                            progressBar.setSelection((progress += INCREMENT) % (progressBar.getMaximum() + INCREMENT));
//		                        while (progressBar.getSelection() == 100) {
//									System.out.println(123);
//									LazyTreeTools.customExpandAll(treeViewer, topTreeNode);	
//									progressBar.dispose();								
////									thread.stop();
//									break;
//								}
//		                    }
//		                });
//
//		                try
//		                {
//		                    Thread.sleep(1000);
//		                }
//		                catch (InterruptedException e)
//		                {
//		                    e.printStackTrace();
//		                }
//		            }
//		        }
//		    });
//		    
//		    thread.start();
		    
//		    thread.destroy();
		
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
