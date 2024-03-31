package com.foxconn.mechanism.hhpnmaterialapply;

import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * 实现结构树每次展开(或收起)操作后，重新设置各列宽，让其文本可以完全显示
 * @author HuashengYu
 *
 */
public class TreeViewerAutoFitListener implements ITreeViewerListener {

	@Override
	public void treeCollapsed(TreeExpansionEvent event) {
		packColumns((TreeViewer) event.getSource());
	}

	@Override
	public void treeExpanded(TreeExpansionEvent event) {
		packColumns((TreeViewer) event.getSource());
	}
	
	
	private void packColumns(final TreeViewer treeViewer) {
		treeViewer.getControl().getShell().getDisplay().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				TreeColumn[] treeColumns = treeViewer.getTree().getColumns();
				for (TreeColumn treeColumn: treeColumns)  {
					if (treeColumn.getWidth() == 0) {
						continue;
					}
					treeColumn.pack();
				}
			}
		});
	}
}
