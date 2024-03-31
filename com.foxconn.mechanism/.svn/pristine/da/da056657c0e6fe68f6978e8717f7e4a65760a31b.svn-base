package com.foxconn.mechanism.batchDownloadDataset;

import java.awt.Component;
import java.awt.Dimension;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes.Name;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.AIFTreeNode;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.organization.OrgTreePanel;
import com.teamcenter.rac.common.organization.OrganizationTree;
import com.teamcenter.rac.common.organization.ProjectTeamSelectionPanel;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMView;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentEnvelope;
import com.teamcenter.rac.kernel.TCComponentEnvelopeType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.UserList;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Pair;
import com.teamcenter.rac.util.Registry;
//import com.teamcenter.soa.exceptions.NotLoadedException;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;

public class BatchDownloadDatasetDialog extends Dialog{
	
	private AbstractAIFUIApplication app = null;
	private TCSession session = null;
	private Registry reg = null;
	private Shell shell = null;
	private Shell parentShell = null;
	private TableLayout tLayout = null;
	private CheckboxTreeViewer treeViewer = null;
	
	ProgressDialog progressDialog = null;

	public BatchDownloadDatasetDialog (AbstractAIFUIApplication app, Shell parent, Registry reg) {
		super(parent);
		this.app = app;
		this.session = (TCSession) this.app.getSession();
		this.parentShell = parent;
		this.reg = reg;
		initUI();
	}
	
	//构建界面
	private void initUI() {
		shell = new Shell(parentShell,SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(1500, 600);
		shell.setText(reg.getString("dialogTitle"));
		shell.setLayout(new FillLayout());
		TCUtil.centerShell(shell);
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		
		SashForm sashForm = new SashForm(shell, SWT.BORDER);
		Composite treeComposite = new Composite(sashForm, SWT.BORDER);
		GridLayout layout = new GridLayout(2, true);
		treeComposite.setLayout(layout);
		
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		
		treeViewer = new CheckboxTreeViewer(treeComposite, SWT.V_SCROLL| SWT.H_SCROLL| SWT.FULL_SELECTION|SWT.BORDER | SWT.MULTI);
		Tree tree = treeViewer.getTree();
		treeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(final CheckStateChangedEvent event) {
				if (event.getChecked())
				treeViewer.setSubtreeChecked(event.getElement(), true);
				else
				treeViewer.setSubtreeChecked(event.getElement(), false);
			}
		});
		tLayout = new TableLayout();
		tree.setHeaderVisible(true);
		tree.setHeaderBackground(new Color(null, 211, 211, 211));
		tree.setLinesVisible(true);
		tree.setLayout(tLayout);
		tree.setLayoutData(data);
		
		createTreeColumn(treeViewer);
		
		BomLineContenetProvider bomLineContenetProvider = new BomLineContenetProvider();
		treeViewer.setContentProvider(bomLineContenetProvider);
		treeViewer.setLabelProvider(new BomLineTableLableProvider());
		treeViewer.setInput(app.getTargetComponent());
		treeViewer.setExpandedState(treeViewer.getTree().getItems()[0].getData(), true);
		treeViewer.getTree().setSelection(treeViewer.getTree().getItem(0));
		treeViewer.getTree().addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
			}
			
			@Override
			public void mouseDown(org.eclipse.swt.events.MouseEvent arg0) {
			}
			
			@Override
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent e) {
				if(e.button == MouseEvent.BUTTON3) {
					TreeItem[] selection = tree.getSelection();
					selection[0].setText(4, "");
				}
			}
		});



		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		
		Composite buttonComposite = new Composite(treeComposite, SWT.NONE);
		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(data);
		
		data = new GridData();
		data.horizontalAlignment = SWT.END;
		data.grabExcessHorizontalSpace = true;
		data.widthHint = 60;
		data.heightHint = 40;
		
		Button okButton = new Button(buttonComposite, SWT.PUSH);
		okButton.setText(reg.getString("confirmButton"));
		okButton.setLayoutData(data);

		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Object[] checkedElements = treeViewer.getCheckedElements();
				if(checkedElements.length==0) {
					MessageBox.post(reg.getString("promptInfo2"), reg.getString("prompt"), MessageBox.WARNING);
					return;
				}
				progressDialog = new ProgressDialog(shell,reg,checkedElements);	
			}
		});
		
		data = new GridData();
		data.horizontalAlignment = SWT.BEGINNING;
		data.grabExcessHorizontalSpace = true;
		data.widthHint = 60;
		data.heightHint = 40;
		
		Button cancelButton = new Button(buttonComposite, SWT.PUSH);
		cancelButton.setText(reg.getString("cancelButton"));
		cancelButton.setLayoutData(data);
		cancelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		
		List<BomLineTreeNode> list = bomLineContenetProvider.getAllElements();
		new ProgressDialog(shell,reg,list.toArray());	
		
		
		if(true) {
			return;
		}
		
		
		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	private void createTreeColumn(TreeViewer tv) {
		String[] titles = new String[] {reg.getString("treeColumnTitle1")};
		for (int index = 0; index < titles.length; index++) {
			TreeViewerColumn tvc = new TreeViewerColumn(tv, SWT.NONE, index);
			tvc.getColumn().setText(titles[index]);
			//tLayout.addColumnData(new ColumnWeightData(150));
			int columnWidth = 157;
			if(index == 0) {
				columnWidth = 1000;
			}
			tvc.getColumn().setWidth(columnWidth);
//			tvc.setEditingSupport(new MyEditingSupport(tv, index));
		}
	}
	
	
	
	
}



