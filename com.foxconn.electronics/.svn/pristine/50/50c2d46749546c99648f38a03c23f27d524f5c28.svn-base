package com.foxconn.electronics.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class TCBrowser
{
    static final List<String> urls      = new ArrayList<>();
    static boolean            canRemove = false;
    private Shell             shell;

    public TCBrowser(Shell parentShell, String url)
    {
        shell = new Shell(parentShell, SWT.RESIZE | SWT.CLOSE | SWT.MAX | SWT.PRIMARY_MODAL);
        shell.setLayout(new FillLayout());
        TCUtil.centerShell(shell);
        Browser browser = new Browser(shell, SWT.NONE);
        initialize(parentShell, browser);
        browser.setUrl(url);
    }

    public void setSize(Point size)
    {
        this.shell.setSize(size);
    }

    public void setTitle(String title)
    {
        this.shell.setText(title);
    }

    public void setImage(Image image)
    {
        this.shell.setImage(image);
    }

    public void open()
    {
        shell.open();
        Display display = shell.getDisplay();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }

    public static void centerShell(Shell shell)
    {
        int width = shell.getMonitor().getClientArea().width;
        int height = shell.getMonitor().getClientArea().height;
        int x = shell.getSize().x;
        int y = shell.getSize().y;
        if (x > width)
        {
            shell.getSize().x = width;
        }
        if (y > height)
        {
            shell.getSize().y = height;
        }
        shell.setLocation((width - x) / 2, (height - y) / 2);
    }

    void initialize(Shell parentShell, Browser browser)
    {
        browser.addListener(SWT.MouseDown, new Listener()
        {
            public void handleEvent(Event event)
            {
                System.out.println("event.time:" + event.time + " -- " + event.button);
                if (event.button == 3)
                {
                    browser.execute("document.oncontextmenu = function() {return false;}");
                }
            }
        });
        browser.addOpenWindowListener(new OpenWindowListener()
        {
            public void open(WindowEvent event)
            {
                // Embed the new window
                final Shell shell = new Shell(parentShell);
                // shell.setSize(900, 500);
                shell.setText("New Window");
                shell.setLayout(new FillLayout());
                final Browser browser = new Browser(shell, SWT.NONE);
                initialize(parentShell, browser);
                event.browser = browser;
                event.display.asyncExec(new Runnable()
                {
                    public void run()
                    {
                        String url = browser.getUrl();
                        System.out.println(url);
                        System.out.println(browser.getText());
                        if (urls.contains(url))
                        {
                            // flag to chek if the window is closed automatic
                            canRemove = false;
                            shell.close();
                        }
                        else
                        {
                            canRemove = true;
                            urls.add(url);
                        }
                    }
                });
            }
        });
        browser.addVisibilityWindowListener(new VisibilityWindowListener()
        {
            public void hide(WindowEvent event)
            {
                Browser browser = (Browser) event.widget;
                Shell shell = browser.getShell();
                shell.setVisible(false);
            }

            public void show(WindowEvent event)
            {
                Browser browser = (Browser) event.widget;
                Shell shell = browser.getShell();
                if (event.location != null)
                    shell.setLocation(event.location);
                if (event.size != null)
                {
                    Point size = event.size;
                    shell.setSize(shell.computeSize(size.x, size.y));
                }
                if (event.addressBar || event.menuBar || event.statusBar || event.toolBar)
                {
                    // Create widgets for the address bar, menu bar, status bar and/or tool bar
                    // leave enough space in the Shell to accomodate a Browser of the size
                    // given by event.size
                }
                shell.open();
            }
        });
        browser.addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent event)
            {
                System.out.println(" --> DisposeListener :: ");
                Browser browser = (Browser) event.widget;
                if (canRemove)
                    urls.remove(browser.getUrl());
                Shell shell = browser.getShell();
                // shell.close();
            }
        });
    }
}
