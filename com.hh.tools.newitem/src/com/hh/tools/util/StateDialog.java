package com.hh.tools.util;

import java.awt.Dialog;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import com.teamcenter.rac.aif.AbstractAIFDialog;

public class StateDialog extends AbstractAIFDialog {

    private static final long serialVersionUID = 1L;

    /**
     * Create the dialog.
     */
    public StateDialog(Dialog frame, String mess) {
        super(frame, false);
        initComponents(mess);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        startProgress();
        pack();
        setVisible(true);
    }

    public StateDialog(String mess) {
        super(false);
        initComponents(mess);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        startProgress();
        pack();
        setAlwaysOnTop(true);
        setVisible(true);
    }

    private void initComponents(String mess) {
        getContentPane().setLayout(null);
        setTitle(mess);
        setPreferredSize(new Dimension(313, 98));
        centerToScreen();
        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(10, 10, 284, 15);
        getContentPane().add(label);

        progressBar = new JProgressBar();
        progressBar.setBounds(10, 35, 284, 16);
        getContentPane().add(progressBar);

    }

    private void startProgress() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                int tempI = 0;
                while (flag) {
                    progressBar.setValue(tempI);
                    tempI++;
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (tempI >= 100) tempI = 0;
                }
            }
        });
        t.start();
    }

    public void setMessage(String message) {
        label.setText(message);
    }

    public void stopth() {
    	label.setText("Íê³É......");
        progressBar.setValue(99);
        flag = false;
    }

    private boolean flag = true;
    private JLabel label;
    private JProgressBar progressBar;

    public void setDisable() {
        // TODO Auto-generated method stub
        this.setVisible(false);
    }

    public void setEnable() {
        // TODO Auto-generated method stub
        this.setVisible(true);
    }
}
