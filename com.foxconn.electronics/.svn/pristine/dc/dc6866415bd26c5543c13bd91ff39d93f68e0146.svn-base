package com.foxconn.electronics.explodebom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.explodebom.domain.BOMLineBean;
import com.foxconn.electronics.explodebom.service.ExportBOMFileService;
import com.foxconn.electronics.util.FileStreamUtil;
import com.foxconn.electronics.util.TCUtil;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.Registry;

public class ExportBOMFileDialog extends Dialog {

	private static final int           LIMIT_SIZE                        = 4;
    private static final int           PRE_SIZE                          = 6;
    private static final int           POST_SIZE                         = 0;
    private static final String        BOM_ATTR_SEPARATION               = ",";
    
	private AbstractPSEApplication pseApp = null;
	private TCSession session = null;
	private Registry reg = null;
	private Shell shell = null;
	
	private JTextPane _textPane = null;
	private PrintStream _printStream = null;
	private String _dirPath = "";	
	
	private static FileStreamUtil fileStreamUtil = null;
    static
    {
    	fileStreamUtil = new FileStreamUtil();    	
    }

	public ExportBOMFileDialog (AbstractPSEApplication app, Shell parent, Registry reg) {
		super(parent);
		this.pseApp = app;
		this.session = (TCSession) this.pseApp.getSession();
		this.shell = parent;
		this.reg = reg;
		initUI();
	}
	
	private void initUI() {
		shell = new Shell(shell,SWT.DIALOG_TRIM | SWT.CLOSE | SWT.PRIMARY_MODAL);
		shell.setSize(800, 500);
		shell.setText(reg.getString("exportBOMFileDialogTitle.Label"));
		shell.setLayout(new FillLayout());
		TCUtil.centerShell(shell);
		Image image = getDefaultImage();
		if (image != null) {
			shell.setImage(image);
		}
		
		Composite mainComposite = new Composite(shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		Frame frame = SWT_AWT.new_Frame(mainComposite);
		frame.setLayout(new BorderLayout());	
		
		//-------------------------------------------------------------------------------------
		JProgressBar progressBar = new JProgressBar();
		
		JButton exportBtn = new JButton(reg.getString("Export.Label"));		
		exportBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportBtn.setEnabled(false);
				progressBar.setIndeterminate(true);
				new Thread(new Runnable() {
					public void run() {
						try 
						{													
							_printStream = fileStreamUtil.openStream(new StringBuffer().append(_dirPath).append(File.separator).append("ExportBOMFile").append(fileStreamUtil.getNowTime()).append(".txt").toString());														
							
							writeLogMsg("开始获取BOM Line信息", false);
							TCComponentBOMLine topBOMLine = pseApp.getBOMWindow().getTopBOMLine();
//							System.out.println("topBOMLine revision type is : " + topBOMLine.getItemRevision().getType());
							switch (topBOMLine.getItemRevision().getType())
							{
								case "D9_EE_PCBARevision" :
								{
									List<TCComponentItemRevision> inputLst = new ArrayList<TCComponentItemRevision>();
									inputLst.add(topBOMLine.getItemRevision());
									Map<TCComponentBOMLine, List<BOMLineBean>> bomBeanMap = ExportBOMFileService.executeGenBomFile(session, inputLst);
									writeLogMsg("开始输出BOM File信息", false);
									
									// 导出bom file
									bomBeanMap.forEach((key, value) -> {
						                try
						                {
						                	String fileName = ExportBOMFileService.getBOMFileName(key);
						                	writeLogMsg("开始检查"+fileName+"数据信息", false);
						                	List<String> dataInfoLst = ExportBOMFileService.checkBOFFileData(value);
						                	if (!TCUtil.isNull(dataInfoLst.stream().collect(Collectors.joining("")))) 
						                	{
						                		for (String msg : dataInfoLst)
						                		{
						                			fileStreamUtil.writeData(_printStream, msg + "\n\r");
						                		}
						                								                		
						                		writeLogMsg("检查"+fileName+"数据信息出错,请查看日志", true);
						                	}
						                	else 
						                	{
						                		writeLogMsg("检查"+fileName+"数据信息完成", false);							                								                	
							                	writeLogMsg("开始输出"+fileName+"信息", false);
							                    ExportBOMFile((TCComponentItemRevision) key.getItemRevision(), fileName, value);
							                    writeLogMsg("输出"+fileName+"信息完成", false);
											}						                	
						                }
						                catch (TCException exp)
						                {
						                    exp.printStackTrace();
						                    writeLogMsg(TCUtil.getExceptionMsg(exp), true);
						                }
						            });
									writeLogMsg("输出BOM File信息完成", false);
									
									writeLogMsg("获取BOM Line信息完成", false);
									
									return;
								}
								case "D9_EE_PCBA_MutiRevision" :
								{		
									writeLogMsg("请选择正确的类型", true);
									return;
								}
								default:
								{	
									writeLogMsg("请选择正确的类型", true);
									return;								
								}
							}							
						} 
						catch (Exception exp) 
						{
							exp.printStackTrace();							
							writeLogMsg(TCUtil.getExceptionMsg(exp), true);
							
						} finally {
							exportBtn.setEnabled(true);
							progressBar.setIndeterminate(false);
							fileStreamUtil.close(_printStream);
						}
					}					
				}).start();
			}
		});
		exportBtn.setEnabled(false);
		
		JButton cancelBtn = new JButton(reg.getString("Cancel.Label"));
		cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Display.getDefault().syncExec(new Runnable() {					
					public void run() {
						shell.dispose();					
					}				
				});
			}
		});
						
		JPanel buttonPanel = new JPanel();
		FlowLayout fl_buttonPanel = (FlowLayout) buttonPanel.getLayout();
		fl_buttonPanel.setHgap(20);
		buttonPanel.add(exportBtn);
		buttonPanel.add(cancelBtn);
		
		//-------------------------------------------------------------------------------------
		JLabel fileLabel = new JLabel(reg.getString("File.Label"));
		fileLabel.setHorizontalAlignment(SwingConstants.LEFT);
		
		JTextField textField = new JTextField();		
		textField.setColumns(50);
		
		JButton selectBtn = new JButton(reg.getString("Select.Label"));
		selectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				_dirPath = getDirPath(frame);
				if (!TCUtil.isNull(_dirPath)) {
					exportBtn.setEnabled(true);
					textField.setText(_dirPath);
				}
			}
		});
		
		JPanel filePanel = new JPanel();		
		filePanel.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
		filePanel.add(fileLabel);
		filePanel.add(textField);				
		filePanel.add(selectBtn);						
		
		//-------------------------------------------------------------------------------------
		_textPane = new JTextPane();
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(_textPane);
		
		JPanel actionPanel = new JPanel();		
		actionPanel.setLayout(new BorderLayout(0, 0));			
		actionPanel.add(progressBar, BorderLayout.NORTH);
		actionPanel.add(buttonPanel, BorderLayout.SOUTH);
								
		frame.add(filePanel, BorderLayout.NORTH);
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.add(actionPanel, BorderLayout.SOUTH);
		
		shell.open();
		shell.layout();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
	}
	
	/**
     * 创建BOM File
     * 
     * @return
     */
    public void ExportBOMFile(TCComponentItemRevision itemRevComp, String fileName, List<BOMLineBean> bomLst)
    {
    	FileStreamUtil fileStreamUtil = new FileStreamUtil();
    	PrintStream printStream = fileStreamUtil.openStream(new StringBuffer().append(_dirPath).append(File.separator).append(fileName).append(".BOM").toString());    	
    	
        try
        {            
            WriteBOMFileHeader(fileStreamUtil, printStream);
            int iItemIndex = 0;
            List<Field> fieldLst = TCUtil.getOrderedField(new BOMLineBean());
            for (BOMLineBean bomlineBean : bomLst)
            {
                try
                {
                    iItemIndex++;
                    String sTextLine = "";
                    String sProcessInfo = TCUtil.getProcessInfo(bomlineBean);
                    List<String> lineLst = TCUtil.splitText(sProcessInfo, BOM_ATTR_SEPARATION, LIMIT_SIZE);
                    for (int i = 0; i < lineLst.size(); i++)
                    {
                        if (0 == i)
                        {
                            sTextLine = iItemIndex + "\t" + TCUtil.getBOMLineInfo(bomlineBean, fieldLst, lineLst.get(i));
                        }
                        else
                        {
                            List<String> bomLineLst = new ArrayList<String>();
                            for (int j = 0; j < PRE_SIZE; j++)
                            {
                                bomLineLst.add("");
                            }
                            bomLineLst.add(lineLst.get(i));
                            for (int j = 0; j < POST_SIZE; j++)
                            {
                                bomLineLst.add("");
                            }
                            sTextLine = bomLineLst.stream().collect(Collectors.joining("\t"));
                        }
                        fileStreamUtil.writeData(printStream, sTextLine);
                    }
                }
                catch (IllegalAccessException | TCException exp)
                {
                    exp.printStackTrace();
                }
            }                        
        }
        catch (TCException e)
        {
            e.printStackTrace();
        }
        finally 
        {
        	fileStreamUtil.close(printStream);
        }
    }

    /**
               * 写入BOM File信息
     * 
     * @return
     */
    public void WriteBOMFileHeader(FileStreamUtil fileStreamUtil, PrintStream printStream) throws TCException
    {
        fileStreamUtil.writeData(printStream, "Cover Page  Revised: " + new SimpleDateFormat("EEEE, MMMM, dd, yyyy", Locale.ENGLISH).format(new Date()));
        fileStreamUtil.writeData(printStream, "D10 DIAVEL&Carnage CML/RKL          Revision: X0A");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "Bill Of Materials       " + new SimpleDateFormat("MMMM, dd,yyyy      hh:mm:ss", Locale.ENGLISH).format(new Date()) + "	Page1");
        fileStreamUtil.writeData(printStream, "");
        fileStreamUtil.writeData(printStream, "Item	HH PN	STD PN	Description	Supplier	Supplier PN	Qty	Location	CCL	Remark	BOM	Package Type	Side	Sub System	Function");
        fileStreamUtil.writeData(printStream, "______________________________________________");
        fileStreamUtil.writeData(printStream, "");
    }
    
    private String getDirPath(Frame frame) {
		try {
			JFileChooser jf = new JFileChooser();
			jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int result = jf.showOpenDialog(frame);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = jf.getSelectedFile();
				return file.getAbsolutePath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
	
	public void writeLogMsg(String msg, boolean isError) {
		if (true == isError) 
		{
			appendErrorMsg(msg);
		}
		else 
		{
			appendRightMsg(msg);
		}
		fileStreamUtil.writeData(_printStream, msg + "\n\r");
	}
	
	// 设置错误提示信息格式
	public void appendErrorMsg(String msg) {
		setSAS(msg + "\n\r", Color.red, false, 16);
	}

	// 设置提示信息格式
	public void appendRightMsg(String msg) {
		setSAS(msg + "\n\r", Color.blue, false, 16);
	}

	public void setSAS(String str, Color col, boolean bold, int fontSize) {
		SimpleAttributeSet attrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(attrSet, col);
		if (bold == true) {
			StyleConstants.setBold(attrSet, true);
		}
		StyleConstants.setFontSize(attrSet, fontSize);
		outputMsg(str, attrSet);
	}
	
	public void outputMsg(String str, AttributeSet attrSet) {
		Document doc = _textPane.getDocument();
		str = "\r\n" + str;
		try {
			doc.insertString(doc.getLength(), str, attrSet);
		} catch (BadLocationException e) {
			System.out.println("BadLocationException: " + e);
		}
		_textPane.setCaretPosition(doc.getLength());

	}
	
}
