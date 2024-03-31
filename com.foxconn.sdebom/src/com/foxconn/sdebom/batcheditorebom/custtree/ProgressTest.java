package com.foxconn.sdebom.batcheditorebom.custtree;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ProgressTest {

	private static Shell        shell;
	private static ProgressBar  progressBar;
	private static Thread thread;
	
	public static void main(String[] args)
	{
	    Display display = new Display();
	    shell = new Shell(SWT.SHELL_TRIM);
	    shell.setText("StackOverflow");
	    shell.setLayout(new GridLayout(1, false));

	    setUpContent();
	    setUpStatusBar();

//	    updateProgressBar();
	    
//	    thread.stop();
	    shell.pack();
	    shell.setSize(400, 200);
	    shell.open();

	    while (!shell.isDisposed())
	    {
	        if (!display.readAndDispatch())
	        {
	            display.sleep();
	        }
	    }
//	    display.dispose();
	}

	private static void updateProgressBar()
	{
	    thread = new Thread(new Runnable()
	    {
	        private int                 progress    = 0;
	        private static final int    INCREMENT   = 30;

	        @Override
	        public void run()
	        {
	            while (!progressBar.isDisposed())
	            {
	                Display.getDefault().asyncExec(new Runnable()
	                {
	                    @Override
	                    public void run()
	                    {
	                        if (!progressBar.isDisposed())
	                            progressBar.setSelection((progress += INCREMENT) % (progressBar.getMaximum() + INCREMENT));
//	                        while (progressBar.getSelection() == 100) {
//								System.out.println(123);
//								progressBar.dispose();								
//								thread.stop();
//								break;
//							}
	                    }
	                });

	                try
	                {
	                    Thread.sleep(1000);
	                }
	                catch (InterruptedException e)
	                {
	                    e.printStackTrace();
	                }
	            }
	        }
	    });
	    
	    thread.start();
	}

	private static void setUpStatusBar()
	{
	    Composite statusBar = new Composite(shell, SWT.BORDER);
	    statusBar.setLayout(new GridLayout(2, false));
	    statusBar.setLayoutData(new GridData(SWT.FILL, SWT.END, true, false));

	    progressBar = new ProgressBar(statusBar, SWT.SMOOTH);
	    progressBar.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
	    progressBar.setMaximum(100);

	    Label status = new Label(statusBar, SWT.NONE);
	    status.setText("Some status message");
	    status.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
	}

	private static void setUpContent()
	{
	    Composite content = new Composite(shell, SWT.NONE);
	    content.setLayout(new GridLayout(2, false));
	    content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    Text text = new Text(content, SWT.BORDER | SWT.MULTI);
	    text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}
}
