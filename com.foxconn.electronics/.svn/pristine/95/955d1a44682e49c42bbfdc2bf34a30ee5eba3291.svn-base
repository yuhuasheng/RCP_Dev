package com.foxconn.electronics.tclicensereport.action;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.util.TCJxbrowser;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.Registry;

public class TcLicenseDialog extends Dialog {
	
	private Shell shell = null;	
	private TCSession session=null;
	public TcLicenseDialog(AbstractAIFUIApplication app,Shell parent) throws Exception {
		super(parent);
		 session = (TCSession) app.getSession();
//		this.shell = parent;
		createContents();
	}
	
	protected void createContents() throws Exception {
		
		shell = new Shell(shell,SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.CLOSE | SWT.PRIMARY_MODAL | SWT.MIN);
		Rectangle bounds = Display.getDefault().getPrimaryMonitor().getBounds();
		bounds.y += 30;
		bounds.width -= 130;
		bounds.x += 130/2;
		bounds.height -= 90;
		shell.setBounds(bounds);
		shell.setText("TC License 使用率报表");
		shell.setImage(Registry.getRegistry(AbstractAIFDialog.class).getImage("aifDesktop.ICON"));
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		Composite mainComposite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		Frame frame = SWT_AWT.new_Frame(mainComposite);
		frame.setLayout(new BorderLayout());
		String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL") +"/LicenseUtilizationRate";
		TCJxbrowser jb = new TCJxbrowser(url);
		frame.add(jb.getBrowserView());
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent var1) {
				super.windowClosed(var1);
				jb.close();
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
