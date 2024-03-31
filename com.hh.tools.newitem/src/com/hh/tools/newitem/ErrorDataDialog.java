package com.hh.tools.newitem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.util.iTextPane;

/**
 * 错误数据弹框
 * 
 * @author wangsf
 *
 */
public class ErrorDataDialog extends AbstractAIFDialog {

	// 弹框的标题
	private String dialogTitle;
	// 弹框的宽、高
	private int dialogWidth = 980;
	private int dialogHeight = 550;
	// 消息提醒的默认颜色(黑色)
	private final Color DEFAULT_MSG_WARN_CONTENT_COLOR_BLACK = Color.BLACK;
	// 消息提醒的默认字体大小(15)
	private final int DEFAULT_MSG_WARN_CONTENT_FONT_SIZE = 15;
		
	// 解析文件消息提醒
    protected JScrollPane msgWarnScrollPane = null;
    protected iTextPane msgWarnPane = null;

    public ErrorDataDialog(String dialogTitle) throws Exception {
        super(true);
        load(dialogTitle, null, null);
    }

    public ErrorDataDialog(String dialogTitle, Integer dialogWidth, Integer dialogHeight) throws Exception {
        super(true);
        load(dialogTitle, dialogWidth, dialogHeight);
    }

    private void load(String dialogTitle, Integer dialogWidth, Integer dialogHeight) {
        setAlwaysOnTop(true);
        this.dialogTitle = dialogTitle;
        if (null != dialogWidth) {
            this.dialogWidth = dialogWidth;
        }

        if (null != dialogHeight) {
            this.dialogHeight = dialogHeight;
        }
    }

    /**
	 * 初始化弹框界面
	 * @param wainMsg 
	 */
    public void initUI(String wainMsg) {
        System.out.println("----------------- ErrorDataDialog exect initUI ------------------");
        setTitle(dialogTitle);
        // 设置弹框的布局 和高宽
        setLayout(new BorderLayout(20, 0));
        setSize(new Dimension(dialogWidth, dialogHeight));

        // 错误数据 内容提醒panel
        JPanel msgWarnPanel = new JPanel(new BorderLayout());
        msgWarnScrollPane = new JScrollPane();
        this.msgWarnPane = new iTextPane();
        this.msgWarnPane.setPreferredSize(new Dimension(dialogWidth, 180));
        this.msgWarnPane.setEditable(false);
        this.msgWarnPane.setBackground(Color.white);

        // 消息提醒的滚动条 添加线
        msgWarnScrollPane.setViewportView(this.msgWarnPane);
        LineBorder msgWarnLine = new LineBorder(Color.BLACK, 1, false);
        msgWarnScrollPane.setBorder(BorderFactory.createTitledBorder(msgWarnLine, "Info Warn",
                TitledBorder.LEFT, TitledBorder.TOP));

        msgWarnPanel.add(msgWarnScrollPane, "Center");
        getContentPane().add(msgWarnPanel, "Center");

        addMsgWarnContent(wainMsg, Color.RED, false, null);

        // 弹框居中和显示
        pack();
        centerToScreen();
        setVisible(true);
    }

    /**
	 * 添加消息提醒内容
	 * @param content 提醒内容
	 * @param color 颜色
	 * @param bold 加粗
	 * @param fontSize 字体大小
	 */
    public void addMsgWarnContent(String content, Color color, boolean bold, Integer fontSize) {
        SimpleAttributeSet attrSet = new SimpleAttributeSet();
        if (null == color) {
            color = DEFAULT_MSG_WARN_CONTENT_COLOR_BLACK;
        }
        // 颜色
     	StyleConstants.setForeground(attrSet, color);
     	// 加粗
        StyleConstants.setBold(attrSet, bold);

        if (null == fontSize) {
            fontSize = DEFAULT_MSG_WARN_CONTENT_FONT_SIZE;
        }
        // 字体大小
        StyleConstants.setFontSize(attrSet, fontSize);

        Document doc = this.msgWarnPane.getDocument();
        try {
            doc.insertString(doc.getLength(), content, attrSet);
            this.msgWarnScrollPane.getVerticalScrollBar().setValue(this.msgWarnPane.getHeight() + 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
