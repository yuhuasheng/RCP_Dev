package com.foxconn.mechanism.batchChangePhase.window;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class Progress {

    int total;

    JProgressBar progressBar;

    AbstractTableModel adapter;

    Executor iExecutor;

    public Progress() {
    }

    public Progress(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public Progress(JProgressBar progressBar, AbstractTableModel adapter) {
        this.progressBar = progressBar;
        this.adapter = adapter;
    }

    public Progress(JProgressBar progressBar, AbstractTableModel adapter, Executor iExecutor) {
        this.progressBar = progressBar;
        this.adapter = adapter;
        this.iExecutor = iExecutor;
    }

    public Progress(JProgressBar progressBar, AbstractTableModel adapter, Executor iExecutor, int total) {
        this.total = total;
        this.progressBar = progressBar;
        this.adapter = adapter;
        this.iExecutor = iExecutor;
        progressBar.setMaximum(total);
        progressBar.setValue(0);
    }

    public void setProgress(int n) {
        if (progressBar != null) {
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(n);
                adapter.fireTableDataChanged();
                if(progressBar.getMaximum()==n){
                    if (iExecutor !=null) {
                        iExecutor.done();
                    }
                }
            });
        }
    }


}
