/**
 * @file ProgressBar.java
 *
 * @brief Create progress bar
 * 
 * @author Yanghui
 * 
 * @history 
 * ================================================================
 * Date               Name                    Description of Change
 * 25-July-2008       Yanghui              this class is used to create
 * 										   progress bar.
 */
package com.foxconn.mechanism.hhpnmaterialapply.export.util;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.util.PropertyLayout;

public class ProgressBar extends AbstractAIFDialog implements ActionListener
{
    private static final long serialVersionUID = 1L;
    private JProgressBar      progressbar;
    private JLabel            label;
    private Timer             timer;
    private boolean           bool             = false;
    private String            showLable        = null;

    public ProgressBar(String showlable)
    {
        super(true);
        showLable = showlable;
        setAlwaysOnTop(true);
    }

    public void setBool(boolean bool)
    {
        this.bool = bool;
    }

    public void initUI()
    {
        Container container = getContentPane();
        JPanel mainPanel = new JPanel(new PropertyLayout());
        this.setLocationRelativeTo(null);
        label = new JLabel(showLable, JLabel.CENTER);
        progressbar = new JProgressBar();
        progressbar.setOrientation(JProgressBar.HORIZONTAL);
        progressbar.setMinimum(0);
        progressbar.setMaximum(100);
        progressbar.setValue(0);
        progressbar.setPreferredSize(new Dimension(200, 15));
        progressbar.setBorderPainted(true);
        timer = new Timer(50, (ActionListener) this);
        timer.setRepeats(false);
        mainPanel.add("1.1.center", new JLabel(" "));
        mainPanel.add("2.1.center", label);
        mainPanel.add("3.1.center", progressbar);
        mainPanel.add("4.1.center", new JLabel(" "));
        container.add(mainPanel);
        pack();
        setLocation(500, 200);
        TaskThread thread = new TaskThread(this);
        thread.start();
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                bool = true;
            }
        });
        int windowWidth = this.getWidth();
        int windowHeight = this.getHeight();
        Toolkit kit = Toolkit.getDefaultToolkit();
        int screenWidth = kit.getScreenSize().width;
        int screenHeight = kit.getScreenSize().height;
        this.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);// 设置窗口居中显示
        this.setModal(true);
        setVisible(true);
    }

    /**
     * @class TaskThread
     * @brief Create progressbar
     * 
     */
    class TaskThread extends Thread
    {
        private ProgressBar bar;

        public TaskThread(ProgressBar bar)
        {
            this.bar = bar;
        }

        public void run()
        {
            if (bool == false)
            {
                // Set Status is running.
                // session.setStatus(registry.getString("export Running"));
            }
            for (int i = 0; i < i + 1; i++)
            {
                timer.start();
                int value = progressbar.getValue();
                if (value < 100)
                {
                    value = value + 5;
                    progressbar.setValue(value);
                }
                else
                {
                    timer.stop();
                    progressbar.setValue(0);
                }
                try
                {
                    sleep(100);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                if (bool == true)
                {
                    bar.setVisible(false);
                    bar.dispose();
                    return;
                }
            }
        }
    }

    public void actionPerformed(ActionEvent arg0)
    {
    }
}
