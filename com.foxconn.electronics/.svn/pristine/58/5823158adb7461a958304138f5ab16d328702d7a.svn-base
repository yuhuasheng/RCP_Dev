package com.foxconn.electronics.ftereport.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
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

import com.foxconn.electronics.util.TCJxbrowser;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.util.Registry;
import com.teamdev.jxbrowser.browser.Browser;

public class NonFTEReportDialog extends Dialog {
	
	private static final double RATIO = 0.8D;
	private Browser browser;
	private AbstractAIFUIApplication app = null;
	private Shell shell;
	private Registry reg = null;
	private String url = null;
	
	public NonFTEReportDialog(AbstractAIFUIApplication app, Shell parent, String url) {
		super(parent);
		this.app = app;
		reg = Registry.getRegistry("com.foxconn.electronics.ftereport.ftereport");
		this.url = url;
		initUI();
	}
	
	private void initUI() {
		Dimension dim = app.getDesktop().getSize();
		shell = new Shell(app.getDesktop().getShell(),
				SWT.DIALOG_TRIM | SWT.CLOSE | SWT.MAX | SWT.MIN | SWT.PRIMARY_MODAL | SWT.RESIZE);
		shell.setSize((int) (dim.getWidth() * RATIO), (int) (dim.getHeight() * RATIO));
		shell.setText(reg.getString("FTEReport.TITLE"));
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
//		frame.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosed(WindowEvent e) {							
//				int flag = JOptionPane.showConfirmDialog(frame, "確定退出嗎?", "提示!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
//				if (JOptionPane.YES_OPTION == flag) {
//					super.windowClosed(e);	
//					jb.close();
//				} else {
//					return;
//				}				
//
//			}
//		});
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
					jb.close();
				} else {
					System.out.println("您单击了 “否” 按钮");
				}
			}
		});
	}

}
