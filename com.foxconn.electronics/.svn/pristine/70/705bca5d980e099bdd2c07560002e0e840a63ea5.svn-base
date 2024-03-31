package com.foxconn.electronics.pamatrixbom.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import com.foxconn.electronics.pamatrixbom.controller.PAMatrixBOMController;
import com.foxconn.electronics.util.TCJxbrowser;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.util.Registry;
import com.teamdev.jxbrowser.browser.Browser;

import cn.hutool.core.util.ObjUtil;

public class PAMatrixBOMDialog extends Dialog {
	
	private static final double RATIO = 0.8D;
	private Dimension dim;
	private String url;
	private Registry reg;
	private Browser browser;
	public Shell shell;
	private PAMatrixBOMController matrixBOMController;
	
	public PAMatrixBOMDialog(Shell parent, Dimension dim, String url, PAMatrixBOMController matrixBOMController) {
		super(parent);
		this.shell = parent;
		this.dim = dim;
		this.url = url;
		this.matrixBOMController = matrixBOMController;
		this.reg = Registry.getRegistry("com.foxconn.electronics.matrixbom.matrixbom");
		initUI();
	}

	
	public void initUI() {
		shell = new Shell(shell,SWT.DIALOG_TRIM | SWT.CLOSE | SWT.MAX | SWT.MIN | SWT.RESIZE);
		shell.setSize((int) (dim.getWidth() * RATIO), (int) (dim.getHeight() * RATIO));
		shell.setText(reg.getString("MatrixbomMenu"));		
		shell.setLayout(new FillLayout());
		TCUtil.centerShell(shell);
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		
		Composite mainComposite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		Frame frame = SWT_AWT.new_Frame(mainComposite);
        frame.setLayout(new BorderLayout());
        TCJxbrowser jb = new TCJxbrowser(url);
        browser = jb.getBrowser();
        frame.add(jb.getBrowserView());
        shellListener(jb);
	
		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	
	private void shellListener(TCJxbrowser jb) {
		shell.addShellListener(new ShellAdapter() {
			// 监听关闭窗体事件
			public void shellClosed(ShellEvent e) {				
				MessageBox msgBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				msgBox.setText(reg.getString("messageBox.TITLE"));
				msgBox.setMessage(reg.getString("messageBox.MSG"));
				int rc = msgBox.open();
				e.doit = rc == SWT.YES;
				if (e.doit) {
					System.out.println("您单击了 “是” 按钮");
					shell.dispose();
					try {
						ExecutorService es = matrixBOMController.getExecutorService();
						if (ObjUtil.isNotEmpty(es)) {
							if (!es.awaitTermination(3, TimeUnit.SECONDS)) { // 线程池等待15秒，超时后执行线程关闭操作
								es.shutdown(); // 关闭线程池
							}
							
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
					jb.close();
				} else {
					System.out.println("您单击了 “否” 按钮");
				}
			}
		});
	}	
	
}
