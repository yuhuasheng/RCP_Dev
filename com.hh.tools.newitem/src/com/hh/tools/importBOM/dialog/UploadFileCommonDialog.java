package com.hh.tools.importBOM.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.hh.tools.util.SelectFileFilter;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.iButton;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.util.iTextPane;

/**
 * 上传文件基础的弹框类
 * 
 * @author wangsf
 *
 */
public abstract class UploadFileCommonDialog extends AbstractAIFDialog implements ActionListener {

	// 弹框的标题
	private String dialogTitle;
	// 弹框的宽、高
	private int dialogWidth = 980;
	private int dialogHeight = 550;
	// 消息提醒的默认颜色(黑色)
	private final Color DEFAULT_MSG_WARN_CONTENT_COLOR_BLACK = Color.BLACK;
	// 消息提醒的默认字体大小(15)
    private final int DEFAULT_MSG_WARN_CONTENT_FONT_SIZE = 15;

    private JFileChooser selectFileChooser = null;

    // 表单面板
    protected JPanel formPanel = null;
    protected JLabel filePathLabel = null;
    protected iTextField filePathTxt = null;
    protected iButton selectFileBtn = null;

    // 解析文件消息提醒
    protected iTextPane msgWarnPane = null;
    protected JProgressBar progressBar = null;

    // 底部按钮
    protected iButton importFileBtn = null;
    protected iButton cancelImportBtn = null;

    public UploadFileCommonDialog(String dialogTitle) throws Exception {
        super(true);
        load(dialogTitle, null, null);
    }

    public UploadFileCommonDialog(String dialogTitle, Integer dialogWidth, Integer dialogHeight) throws Exception {
        super(true);
        load(dialogTitle, dialogWidth, dialogHeight);
    }

    private void load(String dialogTitle, Integer dialogWidth, Integer dialogHeight) {
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
	 */
    protected void initUI() {
        System.out.println("----------------- UploadFileCommonDialog exect initUI ------------------");
        setTitle(dialogTitle);
        // 设置弹框的布局 和高宽
     	setLayout(new BorderLayout(20, 0));
     	setSize(new Dimension(dialogWidth, dialogHeight));

     	// 文件路径的标签
     	this.filePathLabel = new JLabel("File Place:");

     	// 选择文件后的路径文本框
     	this.filePathTxt = new iTextField(40);
     	this.filePathTxt.setEditable(false);

     	// 选择文件的弹框按钮
     	this.selectFileBtn = new iButton("Select File");
     	this.selectFileBtn.addActionListener(this);

     	// 表单属性布局
     	formPanel = new JPanel(new PropertyLayout(5, 10, 10, 10, 10, 10));
     	formPanel.add("1.1.right.center.preferred.preferred", this.filePathLabel);
     	formPanel.add("1.2.left.center.preferred.preferred", this.filePathTxt);
     	formPanel.add("1.3.left.center.preferred.preferred", this.selectFileBtn);
     		
     	// 添加其他组件
     	addComponseToFormPanel();

     	// 上传文件时 内容提醒panel
        JPanel msgWarnPanel = new JPanel(new BorderLayout());
        JScrollPane msgWarnScrollPane = new JScrollPane();
        this.msgWarnPane = new iTextPane();
        this.msgWarnPane.setPreferredSize(new Dimension(dialogWidth, 180));
        this.msgWarnPane.setEditable(false);
        this.msgWarnPane.setBackground(Color.white);

        // 消息提醒的滚动条 添加线
        msgWarnScrollPane.setViewportView(this.msgWarnPane);
        LineBorder msgWarnLine = new LineBorder(Color.BLACK, 1, false);
        msgWarnScrollPane.setBorder(
                BorderFactory.createTitledBorder(msgWarnLine, "Info Warn", TitledBorder.LEFT, TitledBorder.TOP));

        msgWarnPanel.add(msgWarnScrollPane, "Center");
        this.progressBar = new JProgressBar();
        msgWarnPanel.add(this.progressBar, "South");

        // 底部panel
        JPanel southPanel = new JPanel();
        this.importFileBtn = new iButton("Import");
        this.importFileBtn.addActionListener(this);
        this.importFileBtn.setEnabled(false);
        southPanel.add(this.importFileBtn);

        this.cancelImportBtn = new iButton("Cancel");
        this.cancelImportBtn.addActionListener(this);
        southPanel.add(this.cancelImportBtn);

        // 添加布局面板
        getContentPane().add(formPanel, "North");
        getContentPane().add(msgWarnPanel, "Center");
        getContentPane().add(southPanel, "South");

        // 弹框居中和显示
        pack();
        centerToScreen();
        setVisible(true);

    }

    /**
	 * 打开选择excel文件的弹框
	 * 
	 * @param fileTypes 文件类型
	 */
    protected boolean selectExcelFile(String fileTypes) {
        System.out.println("----------------- UploadFileCommonDialog exect selectExcelFile start ------------------");
        boolean selectFileFlag = false;

        if (selectFileChooser == null) {
            SelectFileFilter selectFileFilter = new SelectFileFilter();
            if (null != fileTypes) {
                selectFileFilter.setFileFormat(fileTypes);
            }

            selectFileChooser = new JFileChooser();
            selectFileChooser.setFileSelectionMode(0);
            selectFileChooser.setFileFilter(selectFileFilter);
            selectFileChooser.addChoosableFileFilter(selectFileFilter);
        }

        int result = this.selectFileChooser.showOpenDialog(this);
        if (result == 0) {
            File selectFile = this.selectFileChooser.getSelectedFile();
            if (null != selectFile) {
            	// 文件是否存在的操作
                if (selectFile.exists()) {
                    this.filePathTxt.setText(selectFile.getAbsolutePath());
                    this.filePathTxt.updateUI();
                    selectFileFlag = true;
                } else {
                    this.filePathTxt.setText("");
                }

            } else {
                this.filePathTxt.setText("");
            }
        } else {
            this.filePathTxt.setText("");
        }

        System.out.println("----------------- UploadFileCommonDialog exect selectExcelFile end ------------------");
        return selectFileFlag;
    }

    /**
	 * 添加消息提醒内容
	 * 
	 * @param content  提醒内容
	 * @param color    颜色
	 * @param bold     加粗
	 * @param fontSize 字体大小
	 */
    protected void addMsgWarnContent(String content, Color color, boolean bold, Integer fontSize) {
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
            doc.insertString(doc.getLength(), content + "\n", attrSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
	 * 显示下载模板的按钮
	 */
    protected boolean showDownTemplateBtn() {
        return true;
    }

    /**
	 * 添加Form面板组件
	 */
    protected void addComponseToFormPanel() {
    }
}
