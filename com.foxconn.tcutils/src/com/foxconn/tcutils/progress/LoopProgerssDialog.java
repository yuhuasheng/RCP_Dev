package com.foxconn.tcutils.progress;

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
					System.out.println("第" + (i + 1) + "个任务执行完毕");
					if (stopFlag.getFlag()) {
						System.out.println("被中断了");
						return;
					}
					try {
						Thread.sleep(1000);
					} catch (Throwable t) {

					}
				}
				stopFlag.setFlag(true);
				System.out.println("全部任务执行完毕");
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
		new Thread() { // 将runable的方法调用封装在一个新线程中，并将标识类传入
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
//			monitor.setTaskName("正在加载，请稍后...");			
			monitor.beginTask(message, 30);
			int i = 0;
			while (true) {
				if (monitor.isCanceled() || stopFlag.getFlag()) { // 监听是否单击了进度框的“取消”按钮，stopFlag则是监听后台任务是否停止
					stopFlag.setFlag(true); // 单击了"取消"按钮要设标志位为停止，好通知后台任务中断执行
					break; // 中断循环
				}
				if ((++i) == 50) { // i到50后清零。并将进度条重新来过
					i = 0;
					monitor.beginTask(message, 30);
				}
				try {
					Thread.sleep(99); // 进度条每前进一步休息一会, 不用太长或太短，时间可任意设
				} catch (Throwable t) {

				}
				monitor.worked(1); // 进度条前进一步
			}
			monitor.done(); // 进度条前进到完成
		}
	}
}
