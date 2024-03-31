package com.foxconn.mechanism.exportcreomodel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCSession;

public class CheckDialog extends AbstractAIFDialog {

	private List<String> resultList;
	private JTextArea jt;
	private JScrollPane jsp;

	public CheckDialog(List<String> resultList) {
		super();
		this.resultList = resultList;
		initUI();
	}

	public void initUI() {
		this.setTitle("校验结果如下");
		this.setModal(true);
		this.setLayout(new BorderLayout());
		this.setSize(620, 460);
		JPanel jp = new JPanel();
		jp.setLayout(null);
		this.add(jp, BorderLayout.CENTER);

		JLabel label = new JLabel("<html><font size= 2.3 color=red>以下新模型名称在<br/>系统中已存在, 请确认:</font></html>");
		label.setBounds(15, 10, 115, 75);
		jp.add(label);

		jt = new JTextArea();
		jt.setBackground(Color.WHITE);
		jt.setFont(new Font("黑体", Font.PLAIN, 14));
		jt.setEnabled(false);

		jsp = new JScrollPane(jt);
		jsp.setBounds(135, 20, 400, 300);
		jp.add(jsp);

		JButton button = new JButton("关闭");
		button.setBounds(270, 360, 80, 30);
		jp.add(button);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose(); // 退出当前界面的弹框
			}
		});

		String text = "";
		for (String message : resultList) {
			text = text + message + "\n";
		}
		
		jt.setText(text+"\n");
		this.setLocationRelativeTo(null);
		this.setAlwaysOnTop(true);
    	this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    	this.setVisible(true);
    	System.out.println(text);
	}
}
