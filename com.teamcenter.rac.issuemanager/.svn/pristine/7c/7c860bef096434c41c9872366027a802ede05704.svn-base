package com.teamcenter.rac.issuemanager.util.reviewissue.progress;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

public class LoopProgerssDialog {
	private ProgressMonitorDialog dialog;

	private BooleanFlag stopFlag = new BooleanFlag();

	private String title;

	private String message;

	public LoopProgerssDialog(Shell shell, String title, String message) {
		this.message = message;
		dialog = new ProgressMonitorDialog(shell);
	}

	public LoopProgerssDialog(Shell shell) {
		dialog = new ProgressMonitorDialog(shell);
	}

	public static void main(String[] args) {
		new LoopProgerssDialog(null, null, null).initUI();

	}

	public void initUI() {
//		final Display display = Display.getDefault();
//		final Shell shell = new Shell();
//		shell.setSize(500, 375);
//		shell.setText("SWT Application");
		this.run(true, new IProgressDialogRunnable() {
			@Override
			public void run(BooleanFlag stopFlag) {
				for (int i = 0; i < 10; i++) {
					if (stopFlag.getFlag()) {
						return;
					}
					try {
						Thread.sleep(1000);
					} catch (Throwable t) {

					}
				}
				stopFlag.setFlag(true);
			}
		});
//		shell.open();
//	    shell.layout();
//	    while (!shell.isDisposed()) {
//            if (!display.readAndDispatch())
//                display.sleep();
//        }
	}

	public void run(boolean cancelable, final IProgressDialogRunnable runnable) {
		new Thread() { 
			public void run() {
				runnable.run(stopFlag);
			}
		}.start();

//		runnable.run(stopFlag);
		try {
			dialog.run(true, cancelable, new LoopRunnable());
		} catch (Exception e) {

		}
	}

	private class LoopRunnable implements IRunnableWithProgress {

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask(message, 30);
			int i = 0;
			while (true) {
				if (monitor.isCanceled() || stopFlag.getFlag()) {
					stopFlag.setFlag(true);
					break; 
				}
				if ((++i) == 50) { 
					i = 0;
					monitor.beginTask(message, 30);
				}
				try {
					Thread.sleep(99);
				} catch (Throwable t) {

				}
				monitor.worked(1); 
			}
			monitor.done();
		}
	}
}
