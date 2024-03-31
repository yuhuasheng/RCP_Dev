package com.foxconn.electronics.project;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.util.TCJxbrowser;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.Registry;
import com.teamdev.jxbrowser.browser.Browser;


public class ProjectDialog extends Dialog{
	
	private static final double RATIO = 0.7D;
	private Shell parentShell = null;
	private AbstractAIFUIApplication app = null;
	private Browser browser = null;
	private String url = null;
	
	public ProjectDialog (Shell parentShell, AbstractAIFUIApplication app,String url) {
		super(parentShell);
		this.parentShell = parentShell;
		this.app = app;
		this.url = url;
		initUI();
	}
	
	//构建界面
	private void initUI() {
		Dimension dim = app.getDesktop().getSize();
		Shell shell = new Shell(parentShell,SWT.DIALOG_TRIM | SWT.CLOSE | SWT.MAX | SWT.MIN | SWT.PRIMARY_MODAL | SWT.RESIZE);
		shell.setSize((int) (dim.getWidth() * RATIO), (int) (dim.getHeight() * RATIO));
		shell.setText("將物件指派給專案");
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
