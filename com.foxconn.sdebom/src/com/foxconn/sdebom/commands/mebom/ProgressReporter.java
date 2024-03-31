package com.foxconn.sdebom.commands.mebom;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;


public class ProgressReporter extends JWindow implements Runnable
{
	private static final long serialVersionUID = 1L;

	private JLabel messageLabel;

	private JProgressBar progressBar;

	private boolean requestCanceling;
	
	private int percent;

	public ProgressReporter()
	{

		setLayout(new BorderLayout());
		setAlwaysOnTop(true);

		initializeComponents();
		setTextPropertyForComponents();
		registerComponentsListeners();
		addComponents();

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}

	private void initializeComponents()
	{
		messageLabel = new JLabel(" ", JLabel.LEFT);
		progressBar = new JProgressBar();
	}

	private void setTextPropertyForComponents()
	{
		
	}

	private void registerComponentsListeners()
	{
	}

	private void addComponents()
	{
		add(messageLabel, BorderLayout.NORTH);
		add(progressBar, BorderLayout.SOUTH);
	}

	private void centerToClientScreen()
	{
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		int x = screen.width - getWidth();
		int y = screen.height - getHeight();

		setLocation(x / 2, y / 2);
	}

	public void reportProgressMessage(String message)
	{
		messageLabel.setText(message);
	}

	public void setProgressPercent(int percent)
	{
		this.percent = percent;
	}

	public void requestCanceling()
	{
		requestCanceling = true;
	}
	
	public void setTaskRange(int min, int max)
	{
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
	}

	public void run()
	{
		pack();
		setSize(400, getHeight());
		centerToClientScreen();
		setVisible(true);
		
		while (!requestCanceling)
		{
			if(percent > progressBar.getMaximum())
			{
				percent = 0;
			}
			
			progressBar.setValue(percent);

			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				continue;
			}
		}

		dispose();
	}
	
	public int getProgess()
	{
		return progressBar.getValue();
	}

	public boolean isRequestCanceling()
	{
		return requestCanceling;
	}
}
