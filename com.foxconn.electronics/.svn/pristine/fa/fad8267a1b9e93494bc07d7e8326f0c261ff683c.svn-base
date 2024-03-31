package com.foxconn.electronics.ccmanage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
import com.teamcenter.rac.util.Registry;
import com.teamdev.jxbrowser.browser.Browser;

public class ConnectorAndCableDialog extends Dialog{
	private static final double RATIO = 1D;
	private Shell parentShell;
	private Dimension dim;
	private String url;
	private Registry reg;
	private Browser browser;
	public Shell shell;
	
	public ConnectorAndCableDialog (Shell parentShell, Dimension dim, TCSession session, String url) {
		super(parentShell);
		this.parentShell = parentShell;
		this.dim = dim;
		this.url = url;
		shell = new Shell(parentShell,SWT.DIALOG_TRIM | SWT.CLOSE | SWT.MAX | SWT.MIN | SWT.RESIZE);
		this.reg = Registry.getRegistry("com.foxconn.electronics.ccmanage.ccmanage");
	}
	
	//构建界面
	public void initUI() {
		
		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		bounds.y += 30;
		bounds.width -= 130;
		bounds.x += 130/2;
		bounds.height -= 90;
		shell.setBounds(bounds);
		shell.setText(reg.getString("connectorAndCableManage"));
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
}
