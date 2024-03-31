package com.foxconn.electronics.dbomconvertebom;

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
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.Registry;
import com.teamdev.jxbrowser.browser.Browser;

public class DBOMConvertEBOMDialog extends Dialog
{
    private static final double RATIO = 0.8D;
    private Browser             browser;

    public DBOMConvertEBOMDialog(AbstractPSEApplication app, Shell parent, Registry reg, String url)
    {
        super(parent);
        Dimension dim = app.getDesktop().getSize();
        Shell shell = new Shell(app.getDesktop().getShell(), SWT.DIALOG_TRIM | SWT.CLOSE | SWT.MAX | SWT.PRIMARY_MODAL | SWT.RESIZE);
        shell.setSize((int) (dim.getWidth() * RATIO), (int) (dim.getHeight() * RATIO));
        shell.setText(reg.getString("managementebommenu"));
        shell.setLayout(new FillLayout());
        TCUtil.centerShell(shell);
        Image image = getDefaultImage();
        if (image != null)
        {
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
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }
}
