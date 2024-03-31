package com.foxconn.sdebom.batcheditorebom.custtree;

import org.eclipse.swt.SWT; 

import org.eclipse.swt.layout.GridData; 

import org.eclipse.swt.layout.GridLayout; 

import org.eclipse.swt.widgets.Display; 

import org.eclipse.swt.widgets.ProgressBar; 

import org.eclipse.swt.widgets.Shell; 





public class ProgressBarExample { 

    public static void main(String[] args){ 

        Display display = new Display(); 

        Shell shell = new Shell(display); 

        shell.setLayout(new GridLayout()); 

        //添加平滑的进度条 

//        ProgressBar pb1 = new ProgressBar(shell,SWT.HORIZONTAL|SWT.SMOOTH); 
//
//        pb1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL)); 
//
//        //显示进度条的最小值 
//
//        pb1.setMinimum(0); 
//
//        //设置进度条的最大值 
//
//        pb1.setMaximum(30); 

        //添加自动递增的进度条 

        ProgressBar pb2 = new ProgressBar(shell,SWT.HORIZONTAL|SWT.INDETERMINATE); 

        //添加线程，在线程中处理长时间的任务，并最终反映在平滑进度条上 

//        new LongRunningOperation(display,pb1).start(); 

        shell.open(); 

        while(!shell.isDisposed()){ 

            if(!display.readAndDispatch()){ 

                display.sleep(); 

            } 

        } 

    } 

} 

class LongRunningOperation extends Thread{ 

    private Display display; 

    private ProgressBar progressBar; 

    

    public LongRunningOperation(Display display,ProgressBar progressBar){ 

        this.display = display; 

        this.progressBar = progressBar; 

    } 

    

    public void run(){ 

        //模仿长时间的任务 

        for(int i = 0;i<30;i++){ 

            try{ 

                Thread.sleep(1000); 

            }catch(InterruptedException e){ 

                

            } 

            display.asyncExec(new Runnable(){ 

                public void run(){ 

                    if(progressBar.isDisposed()) return; 

                    //进度条递增 

                    progressBar.setSelection(progressBar.getSelection()+1); 

                } 

            }); 

        } 

    } 

    

} 

