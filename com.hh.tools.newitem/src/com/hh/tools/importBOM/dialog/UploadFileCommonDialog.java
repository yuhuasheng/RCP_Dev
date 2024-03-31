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
 * �ϴ��ļ������ĵ�����
 * 
 * @author wangsf
 *
 */
public abstract class UploadFileCommonDialog extends AbstractAIFDialog implements ActionListener {

	// ����ı���
	private String dialogTitle;
	// ����Ŀ���
	private int dialogWidth = 980;
	private int dialogHeight = 550;
	// ��Ϣ���ѵ�Ĭ����ɫ(��ɫ)
	private final Color DEFAULT_MSG_WARN_CONTENT_COLOR_BLACK = Color.BLACK;
	// ��Ϣ���ѵ�Ĭ�������С(15)
    private final int DEFAULT_MSG_WARN_CONTENT_FONT_SIZE = 15;

    private JFileChooser selectFileChooser = null;

    // �����
    protected JPanel formPanel = null;
    protected JLabel filePathLabel = null;
    protected iTextField filePathTxt = null;
    protected iButton selectFileBtn = null;

    // �����ļ���Ϣ����
    protected iTextPane msgWarnPane = null;
    protected JProgressBar progressBar = null;

    // �ײ���ť
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
	 * ��ʼ���������
	 */
    protected void initUI() {
        System.out.println("----------------- UploadFileCommonDialog exect initUI ------------------");
        setTitle(dialogTitle);
        // ���õ���Ĳ��� �͸߿�
     	setLayout(new BorderLayout(20, 0));
     	setSize(new Dimension(dialogWidth, dialogHeight));

     	// �ļ�·���ı�ǩ
     	this.filePathLabel = new JLabel("File Place:");

     	// ѡ���ļ����·���ı���
     	this.filePathTxt = new iTextField(40);
     	this.filePathTxt.setEditable(false);

     	// ѡ���ļ��ĵ���ť
     	this.selectFileBtn = new iButton("Select File");
     	this.selectFileBtn.addActionListener(this);

     	// �����Բ���
     	formPanel = new JPanel(new PropertyLayout(5, 10, 10, 10, 10, 10));
     	formPanel.add("1.1.right.center.preferred.preferred", this.filePathLabel);
     	formPanel.add("1.2.left.center.preferred.preferred", this.filePathTxt);
     	formPanel.add("1.3.left.center.preferred.preferred", this.selectFileBtn);
     		
     	// ����������
     	addComponseToFormPanel();

     	// �ϴ��ļ�ʱ ��������panel
        JPanel msgWarnPanel = new JPanel(new BorderLayout());
        JScrollPane msgWarnScrollPane = new JScrollPane();
        this.msgWarnPane = new iTextPane();
        this.msgWarnPane.setPreferredSize(new Dimension(dialogWidth, 180));
        this.msgWarnPane.setEditable(false);
        this.msgWarnPane.setBackground(Color.white);

        // ��Ϣ���ѵĹ����� �����
        msgWarnScrollPane.setViewportView(this.msgWarnPane);
        LineBorder msgWarnLine = new LineBorder(Color.BLACK, 1, false);
        msgWarnScrollPane.setBorder(
                BorderFactory.createTitledBorder(msgWarnLine, "Info Warn", TitledBorder.LEFT, TitledBorder.TOP));

        msgWarnPanel.add(msgWarnScrollPane, "Center");
        this.progressBar = new JProgressBar();
        msgWarnPanel.add(this.progressBar, "South");

        // �ײ�panel
        JPanel southPanel = new JPanel();
        this.importFileBtn = new iButton("Import");
        this.importFileBtn.addActionListener(this);
        this.importFileBtn.setEnabled(false);
        southPanel.add(this.importFileBtn);

        this.cancelImportBtn = new iButton("Cancel");
        this.cancelImportBtn.addActionListener(this);
        southPanel.add(this.cancelImportBtn);

        // ��Ӳ������
        getContentPane().add(formPanel, "North");
        getContentPane().add(msgWarnPanel, "Center");
        getContentPane().add(southPanel, "South");

        // ������к���ʾ
        pack();
        centerToScreen();
        setVisible(true);

    }

    /**
	 * ��ѡ��excel�ļ��ĵ���
	 * 
	 * @param fileTypes �ļ�����
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
            	// �ļ��Ƿ���ڵĲ���
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
	 * �����Ϣ��������
	 * 
	 * @param content  ��������
	 * @param color    ��ɫ
	 * @param bold     �Ӵ�
	 * @param fontSize �����С
	 */
    protected void addMsgWarnContent(String content, Color color, boolean bold, Integer fontSize) {
        SimpleAttributeSet attrSet = new SimpleAttributeSet();
        if (null == color) {
            color = DEFAULT_MSG_WARN_CONTENT_COLOR_BLACK;
        }
        // ��ɫ
        StyleConstants.setForeground(attrSet, color);
        // �Ӵ�
        StyleConstants.setBold(attrSet, bold);

        if (null == fontSize) {
            fontSize = DEFAULT_MSG_WARN_CONTENT_FONT_SIZE;
        }
        // �����С
        StyleConstants.setFontSize(attrSet, fontSize);

        Document doc = this.msgWarnPane.getDocument();
        try {
            doc.insertString(doc.getLength(), content + "\n", attrSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
	 * ��ʾ����ģ��İ�ť
	 */
    protected boolean showDownTemplateBtn() {
        return true;
    }

    /**
	 * ���Form������
	 */
    protected void addComponseToFormPanel() {
    }
}
