package com.foxconn.electronics.issuemanagement.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.util.TCJxbrowser;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.kernel.TCSession;
import com.teamdev.jxbrowser.browser.Browser;

public class IssueUpdatesDialog extends Dialog{
	private static final double RATIO = 1D;
	private Shell parentShell;
	private Dimension dim;
	private String url;
	private ResourceBundle reg;
	private Browser browser;
	public Shell shell;
	
	public IssueUpdatesDialog (Shell parentShell, Dimension dim, TCSession session, String url,ResourceBundle reg) {
		super(parentShell);
		this.parentShell = parentShell;
		this.dim = dim;
		this.url = url;
		shell = new Shell(parentShell,SWT.DIALOG_TRIM | SWT.CLOSE | SWT.MAX | SWT.MIN | SWT.RESIZE);
		this.reg = reg;
	}
	
	//构建界面
	public void initUI() {
		
		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		bounds.y += 30;
		bounds.width -= 250;
		bounds.x += 130/2;
		bounds.height -= 80;
		shell.setBounds(bounds);
		shell.setText("Issue Updates");
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
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosed(WindowEvent var1)
            {
                super.windowClosed(var1);
                browser.close();
            }
        });
	
		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	@Override
	public boolean close() {
		if(!shell.getDisplay().isDisposed()) {
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					shell.dispose();
				}
			};
			shell.getDisplay().asyncExec(runnable);
		}
		return true;
	}
	
	
}
