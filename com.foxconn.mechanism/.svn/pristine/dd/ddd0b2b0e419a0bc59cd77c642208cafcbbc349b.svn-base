package com.foxconn.mechanism.custommaterial.custommaterialbatchimport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foxconn.mechanism.Constants;
import com.foxconn.mechanism.hhpnmaterialapply.message.MessageShow;
import com.foxconn.mechanism.util.MsgBox;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevisionType;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentProjectType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import javax.swing.DefaultComboBoxModel;

public class CustomMaterialBatchImportDialog extends Dialog {

	private AbstractAIFUIApplication _app = null;
	private Shell _shell = null;
	private Frame _frame = null;
	private TCSession _session = null;
	private Registry _reg = null;
	private TCComponent _tcTargetComp = null;
	private JProgressBar _progressBar = null;
	private JButton _selectFileBtn = null;
	private JButton _selectTemplateBtn = null;
	private JTextField _textField = null;
	private JTextPane _textPane = null;
	private JButton _checkBtn = null;
	private JButton _importBtn = null;
	private JButton _cancelBtn = null;
	JComboBox<String> cmb_actualyUser;
	JComboBox<String> cmb_project;
	ArrayList<String> userList;
	TCComponentProject[] projectList;
	private String _filePath = "";

	private static final Logger _logger = LoggerFactory.getLogger(CustomMaterialBatchImportDialog.class);

	protected CustomMaterialBatchImportDialog(AbstractAIFUIApplication app, Shell parent) throws TCException {
		super(parent);
		_app = app;
		_shell = parent;
		_session = (TCSession) _app.getSession();

		_reg = Registry.getRegistry(
				"com.foxconn.mechanism.custommaterial.custommaterialbatchimport.custommaterialbatchimport");

		InterfaceAIFComponent aifComponent = app.getTargetComponent();
		if (null == aifComponent) {
			MessageShow.warningMsgBox(_reg.getString("noTargetErr.MSG"), _reg.getString("ERROR.MSG"));
			return;
		} else if (!(aifComponent instanceof TCComponentFolder)) {
			MessageShow.warningMsgBox(_reg.getString("noFolderErr.MSG"), _reg.getString("ERROR.MSG"));
			return;
		}

		_tcTargetComp = (TCComponent) aifComponent;

		initUI();

	}

	/**
	 * 界面初始化
	 * @throws TCException 
	 */
	private void initUI() throws TCException {
		_shell = new Shell(_shell, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL | SWT.MAX);
		_shell.setSize(1000, 500);
		_shell.setText(_reg.getString("Dialog.Title"));
		_shell.setLayout(new FillLayout());
		TCUtil.centerShell(_shell);
		Image image = getDefaultImage();
		if (image != null) {
			_shell.setImage(image);
		}

		Composite mainComposite = new Composite(_shell, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		_frame = SWT_AWT.new_Frame(mainComposite);
		_frame.setLayout(new BorderLayout());

		_progressBar = new JProgressBar();

		_checkBtn = new JButton(_reg.getString("Check.Label"));
		_checkBtn.setEnabled(false);
		_importBtn = new JButton(_reg.getString("Import.Label"));
		_importBtn.setEnabled(false);
		_cancelBtn = new JButton(_reg.getString("Cancel.Label"));

		JPanel buttonPanel = new JPanel();
//		buttonPanel.setLayout(null);
		
		
		
		cmb_actualyUser = new JComboBox<String>();
		cmb_actualyUser.addItem("");
		cmb_actualyUser.setEditable(true);
		cmb_actualyUser.addItemListener(new ItemListener() {
			
			private boolean noTrigger = false;
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(noTrigger) {
					return;
				}
				int stateChange = e.getStateChange();
				if(stateChange==1) {
					noTrigger = true;
					JComboBox<String> source = (JComboBox<String>) e.getSource();
					String text = (String) e.getItem();
					source.removeAllItems();					
					for(String user : userList) {
						if(user.toLowerCase().contains(text.toLowerCase())) {
							 source.addItem(user);							 
						}
					}
					if(source.getItemCount()==1) {
						source.setSelectedIndex(0);
					}else {
						source.showPopup();
					}
				}
				noTrigger = false;
			}
		});
		
		userList = com.foxconn.tcutils.util.TCUtil.getLovValues(_session, (TCComponentItemRevisionType) _session.getTypeComponent("ItemRevision"), "d9_ActualUserID");
		for(String user:userList) {
			cmb_actualyUser.addItem(user);			
		}
		
		cmb_project = new JComboBox<String>();
		cmb_project.addItem("");		
		cmb_project.setEditable(true);
		cmb_project.addItemListener(new ItemListener() {
			
			private boolean noTrigger = false;
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(noTrigger) {
					return;
				}
				int stateChange = e.getStateChange();
				if(stateChange==1) {
					noTrigger = true;
					JComboBox<String> source = (JComboBox<String>) e.getSource();
					String text = (String) e.getItem();
					source.removeAllItems();					
					for(TCComponentProject project : projectList) {
						String projectName = project.getProjectName();
						if(projectName.toLowerCase().contains(text.toLowerCase())) {
							 source.addItem(projectName);							 
						}
					}
					if(source.getItemCount()==1) {
						source.setSelectedIndex(0);
					}else {
						source.showPopup();
					}
				}
				noTrigger = false;
			}
		});
		
		TCComponentUser user = _session.getUser();
		TCComponentProjectType projectType = (TCComponentProjectType) _session.getTypeComponent(ITypeName.TC_Project);
        projectList = projectType.extent(user, true);
        for(TCComponentProject project : projectList) {
        	String projectName = project.getProperty("object_name");
        	cmb_project.addItem(projectName);
        }
		
		JLabel lab_must = new JLabel("*");
		lab_must.setForeground(Color.RED);
		JLabel lab_must_project = new JLabel("*");
		lab_must_project.setForeground(Color.RED);
		
		buttonPanel.add(_checkBtn);
		buttonPanel.add(_importBtn);
		buttonPanel.add(_cancelBtn);
		
		JLabel fileLabel = new JLabel(_reg.getString("File.Label"));
		fileLabel.setHorizontalAlignment(SwingConstants.LEFT);

		_textField = new JTextField();
		_textField.setColumns(50);

		_selectFileBtn = new JButton(_reg.getString("Select.Label"));
		_selectTemplateBtn = new JButton(_reg.getString("DownloadTemplate.Label"));
		
		
		

		JPanel actualUserPanel = new JPanel();
		actualUserPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
		actualUserPanel.add(new JLabel("專案名"));
		actualUserPanel.add(cmb_project);
		actualUserPanel.add(lab_must_project);
		actualUserPanel.add(new JLabel("   "));
		actualUserPanel.add(new JLabel("實際用戶"));
		actualUserPanel.add(cmb_actualyUser);
		actualUserPanel.add(lab_must);
		
		JPanel filePanel = new JPanel();
		filePanel.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
		filePanel.add(fileLabel);
		filePanel.add(_textField);
		filePanel.add(_selectFileBtn);
		filePanel.add(_selectTemplateBtn);
//		filePanel.add(new JLabel("實際用戶"));
//		filePanel.add(cmb_actualyUser);
//		filePanel.add(lab_must);
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.add(actualUserPanel,BorderLayout.NORTH);
		northPanel.add(filePanel,BorderLayout.SOUTH);
//		
//		BorderLayout fileLayout = ;
//		fileLayout.add(filePanel, BorderLayout.NORTH);
		
		_textPane = new JTextPane();

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(_textPane);

		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(new BorderLayout(0, 0));
		actionPanel.add(_progressBar, BorderLayout.NORTH);
		actionPanel.add(buttonPanel, BorderLayout.SOUTH);

		addListener();

		_frame.add(northPanel, BorderLayout.NORTH);
		_frame.add(scrollPane, BorderLayout.CENTER);
		_frame.add(actionPanel, BorderLayout.SOUTH);
		
		_shell.open();
		_shell.layout();
		Display display = _shell.getDisplay();
		while (!_shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}

	public TCSession getSession() {
		return _session;
	}

	private void addListener() {
		try {
			_selectFileBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					_filePath = getFilePath();
					if (!TCUtil.isNull(_filePath)) {
						_checkBtn.setEnabled(true);
						_textField.setText(_filePath);
					}
				}
			});

			_selectTemplateBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread() {

						@Override
						public void run() {
							// 选择文件
							try {
								String fileName = "批量导入自编料号模板.xlsx";
								JFileChooser fileChooser = new JFileChooser();
								FileSystemView desk = FileSystemView.getFileSystemView();
								File comDisk = desk.getHomeDirectory();
								fileChooser.setCurrentDirectory(comDisk);
								fileChooser.setSelectedFile(new File(fileName));
								int option = fileChooser.showSaveDialog(null);
								if (option == JFileChooser.APPROVE_OPTION) {
									File newFile = fileChooser.getSelectedFile();
									
									String templateItemId = TCUtil.getPreference(_session, TCPreferenceService.TC_preference_site, "D9_Part_Import_Templates");
									TCComponentItemType templateItemType = (TCComponentItemType) TCUtil.getTCSession() .getTypeComponent(ITypeName.Item);
									TCComponentItem templateItem = templateItemType.find(templateItemId);
									if (templateItem != null) {
										TCComponentItemRevision templateItemRev = templateItem.getLatestItemRevision();
										if (templateItemRev != null) {
											TCComponentDataset tcDataset = TCUtil.findDataSet(templateItemRev, fileName);
											if (tcDataset != null) {
												TCComponentTcFile[] tcfiles = tcDataset.getTcFiles();
												if (tcfiles == null || tcfiles.length == 0) {
													return;
												}
												TCComponentTcFile tcfile = null;
												tcfile = tcfiles[0];												
												if (null == tcfile) {
													return;
												}
												// 下载文件
												File exportFile = tcfile.getFmsFile();
												FileUtil.copy(exportFile, newFile, true);
												MsgBox.display(_reg.getString("downloadTemplateSuccess.MSG"));												
											}
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								MsgBox.display(e);
							}
						}
					}.start();
				}
			});

			_checkBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					_checkBtn.setEnabled(false);
					_progressBar.setIndeterminate(true);
					String projectName = (String) cmb_project.getSelectedItem();
					if(StrUtil.isEmpty(projectName)) {
						writeLogMsg("专案名不能為空", new Color(255,0,0));
						_checkBtn.setEnabled(true);
						_progressBar.setIndeterminate(false);
						return;
					}
					String actualUser = (String) cmb_actualyUser.getSelectedItem();
					if(StrUtil.isEmpty(actualUser)) {
						writeLogMsg("實際用戶不能為空", new Color(255,0,0));
						_checkBtn.setEnabled(true);
						_progressBar.setIndeterminate(false);
						return;
					}					
					new Thread(new Runnable() {
						public void run() {
							try {
								writeLogMsg("检查Excel数据开始");
								TCComponentProject project = null;
								for(TCComponentProject p : projectList) {
						        	String pName = com.foxconn.tcutils.util.TCUtil.getProperty(p,"object_name");
						        	if(projectName.equals(pName)) {
						        		project = p;
						        		break;
						        	}				        	
							     }
								CustomMaterialBatchImportAction importActionCls = new CustomMaterialBatchImportAction(
										CustomMateriaBatchImportType.CHECK, _tcTargetComp, _filePath,
										new ICustomMaterialBatchImportLog() {

											@Override
											public void onLog(String sLog, Color color) {
												if (color != null)
													writeLogMsg(sLog, color);
												else
													writeLogMsg(sLog);
											}

											@Override
											public void onComplete() {
												_importBtn.setEnabled(true);
											}
										},actualUser,project);

								importActionCls.execute();
								writeLogMsg("检查Excel数据完成", Color.green);
							} catch (Exception e) {
								e.printStackTrace();
								writeLogMsg(TCUtil.getExceptionMsg(e), Color.red);

							} finally {
								_checkBtn.setEnabled(true);
								_progressBar.setIndeterminate(false);
							}
						}
					}).start();
				}
			});

			_importBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					_checkBtn.setEnabled(false);
					_importBtn.setEnabled(false);
					_progressBar.setIndeterminate(true);
					String actualUser = (String) cmb_actualyUser.getSelectedItem();
					new Thread(new Runnable() {
						public void run() {
							try {
								writeLogMsg("开始批量导入自编料号信息");
								TCComponentProject project = null;
								String projectName = (String) cmb_project.getSelectedItem();
								for(TCComponentProject p : projectList) {
							        	String pName = com.foxconn.tcutils.util.TCUtil.getProperty(p,"object_name");
							        	if(projectName.equals(pName)) {
							        		project = p;
							        		break;
							        	}				        	
							     }
								CustomMaterialBatchImportAction importActionCls = new CustomMaterialBatchImportAction(
										CustomMateriaBatchImportType.IMPORT, _tcTargetComp, _filePath,
										new ICustomMaterialBatchImportLog() {

											@Override
											public void onLog(String sLog, Color color) {
												if (color != null)
													writeLogMsg(sLog, color);
												else
													writeLogMsg(sLog);
											}
										},actualUser,project);
								importActionCls.execute();
								writeLogMsg("批量导入自编料号信息完成", Color.green);
							} catch (Exception e) {
								e.printStackTrace();
								writeLogMsg(TCUtil.getExceptionMsg(e), Color.red);

							} finally {
								_importBtn.setEnabled(true);
								_progressBar.setIndeterminate(false);
							}
						}
					}).start();
				}
			});

			_cancelBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							_shell.dispose();
						}
					});
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getFilePath() {
		try {
			JFileChooser jf = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel", "xls", "xlsx");
			jf.setFileFilter(filter);
			jf.setDialogTitle("选择文件");
			jf.setFileSelectionMode(JFileChooser.FILES_ONLY);

			int result = jf.showOpenDialog(_frame);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = jf.getSelectedFile();
				return file.getAbsolutePath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public void writeLogMsg(String msg) {
		_logger.info(msg);
		appendMsg(msg);
	}

	public void writeLogMsg(String msg, Color col) {
		_logger.error(msg);
		appendMsg(msg, col);
	}

	// 设置提示信息格式
	private void appendMsg(String msg) {
		setSAS(msg + "\n\r", Color.blue, false, 16);
	}

	// 设置提示信息格式
	private void appendMsg(String msg, Color col) {
		setSAS(msg + "\n\r", col, false, 16);
	}

	private void setSAS(String str, Color col, boolean bold, int fontSize) {
		SimpleAttributeSet attrSet = new SimpleAttributeSet();
		StyleConstants.setForeground(attrSet, col);
		if (bold == true) {
			StyleConstants.setBold(attrSet, true);
		}
		StyleConstants.setFontSize(attrSet, fontSize);
		outputMsg(str, attrSet);
	}

	private void outputMsg(String str, AttributeSet attrSet) {
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
