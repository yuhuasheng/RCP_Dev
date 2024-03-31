package com.foxconn.mechanism.batchChangePhase.window;

import java.awt.*;

import javax.swing.*;

import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;

import com.teamcenter.rac.util.Registry;
import com.foxconn.mechanism.batchChangePhase.window.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BatchTransformPhaseWindow {

	private JFrame frame;
	private JTable table;
	private String saveDir;

	private int step = 1;
	private final TransformPhaseService service;
	private JCheckBox chk_all;
	NotComplianceAdapter notComplianceAdapter;
	ComplianceAdapter complianceAdapter;
	private JCheckBox chk_inspect;
	JButton bt_next;
	private JButton bt_export;
	private JLabel txt_title;
	private JProgressBar pb_scan;
	private JPanel panel;
	private BlueprintAdapter blueprintAdapter;
	private Registry reg;
	private JButton bt_cancel;

	/**
	 * 
	 * 窗口调用示例
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
//                	Registry registry = new Registry();
					BatchTransformPhaseWindow window = new BatchTransformPhaseWindow(null, new TransformPhaseService() {
						@Override
						public void initData() {
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

						@Override
						public List<PhaseBean> getNotComplianceData() {
							List<PhaseBean> list = new ArrayList<>();
							list.add(new PhaseBean("123", "123", "123", "456", "已发布", "infodba", null));
							list.add(new PhaseBean("123", "123", "123", "456", "已发布", "infodba", null));
							list.add(new PhaseBean("123", "123", "123", "456", "已发布", "infodba", null));
							return list;
						}

						@Override
						public List<PhaseBean> getComplianceData() {
							ArrayList<PhaseBean> beans = new ArrayList<>();
							beans.add(new PhaseBean("456", "456", "123", "456", "已发布", "infodba", null));
							beans.add(new PhaseBean("456", "456", "123", "456", "已发布", "infodba", null));
							beans.add(new PhaseBean("456", "456", "123", "456", "已发布", "infodba", null));
							beans.add(new PhaseBean("456", "456", "123", "456", "已发布", "infodba", null));
							return beans;
						}

						@Override
						public void doTransformPhase(List<PhaseBean> data, Progress progress) {
							for (int i = 0; i < data.size(); i++) {

								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								System.out.println("执行");
								data.get(i).setResult("完成");
								progress.setProgress(i + 1);
							}
						}

						@Override
						public boolean checkModelType() {

							return false;
						}

						@Override
						public List<BlueprintBean> getBlueprintData() {
							ArrayList<BlueprintBean> beans = new ArrayList<>();
							beans.add(new BlueprintBean("456", "456", "456", "123"));
							beans.add(new BlueprintBean("456", "456", "456", "123"));
							beans.add(new BlueprintBean("456", "456", "456", "123"));
							return beans;
						}

						@Override
						public void doUpdatePhase() {
							System.out.println("执行");
						}

						@Override
						public void exportNotComplianceData(List<PhaseBean> data, String savePath) {
							for (PhaseBean datum : data) {
								System.out.println(datum);
							}
							System.out.println("保存文件成功：" + savePath);
						}

						@Override
						public void exportAfterTransformPhaseData(List<PhaseBean> data, String savePath) {
							for (PhaseBean datum : data) {
								System.out.println(datum);
							}
							System.out.println("保存文件成功：" + savePath);
						}

						@Override
						public void exportBlueprint(List<BlueprintBean> data, String savePath) {
							for (BlueprintBean datum : data) {
								System.out.println(datum);
							}
							System.out.println("保存文件成功：" + savePath);
						}
					});
					window.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BatchTransformPhaseWindow(Registry registry, TransformPhaseService service) {
		this.service = service;
		this.reg = registry;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle(reg.getString("phase.window.title"));
		frame.setBounds(100, 100, 761, 407);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				// 关闭窗体关闭触发
				int option = JOptionPane.showConfirmDialog(null, reg.getString("phase.window.close.content"),
						reg.getString("phase.window.close.title"), JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					frame.dispose();
				}
			}
		});
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane);

		table = new JTable();
		scrollPane.setViewportView(table);
		table.setRowSelectionAllowed(false);
		table.setModel(notComplianceAdapter = new NotComplianceAdapter(reg));
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();// ����table���ݾ���
		tcr.setHorizontalAlignment(SwingConstants.CENTER);
		table.setDefaultRenderer(Object.class, tcr);
		table.setRowHeight(20);
		table.setBackground(new Color(204, 255, 204));
		scrollPane.getViewport().setBackground(new Color(204, 255, 204));

		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		pb_scan = new JProgressBar();
		pb_scan.setIndeterminate(true);
		pb_scan.setVisible(false);
		pb_scan.setStringPainted(true);
		panel.add(pb_scan, BorderLayout.NORTH);
		pb_scan.setValue(50);

		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.CENTER);

		bt_next = new JButton(reg.getString("phase.window.next"));
		showProgressBar(true);
		bt_next.setVisible(false);
		pb_scan.setStringPainted(false);
		pb_scan.setPreferredSize(new Dimension(1, 21));
		bt_next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (step == 1 && chk_inspect.isSelected()) {
					notComplianceAdapter.addAll(service.getNotComplianceData());
					bt_export.setVisible(true);
					txt_title.setText(reg.getString("phase.window.notComplianceList"));
					step = 2;
					chk_inspect.setVisible(false);
				} else if (step == 2 || step == 1 && !chk_inspect.isSelected()) {
					txt_title.setText(reg.getString("phase.window.setp2"));
					bt_next.setText(reg.getString("phase.window.do"));
					chk_all.setVisible(true);
					bt_export.setVisible(false);
					complianceAdapter = new ComplianceAdapter(chk_all, reg);
					table.setModel(complianceAdapter);
					List<PhaseBean> complianceData = service.getComplianceData();
					if (complianceData == null || complianceData.isEmpty()) {
						bt_next.setText(reg.getString("phase.window.done"));
						MsgBox.display(reg.getString("phase.window.noComplianceTip"));
						bt_cancel.setVisible(false);
						step = 6;
						return;
					}
					complianceAdapter.addAll(complianceData);
					chk_inspect.setVisible(false);
					step = 3;
				} else if (step == 3) {
					List<PhaseBean> selectedList = complianceAdapter.getSelectedList();
					int size = selectedList.size();
					if (size == 0) {
						MsgBox.display(reg.getString("phase.window.notSelectedPart"));
						return;
					}
					showProgressBar(true);
					pb_scan.setMaximum(size);
					bt_next.setEnabled(false);
					table.setEnabled(false);
					chk_all.setVisible(false);
					complianceAdapter = new ComplianceAdapter(reg);
					table.setModel(complianceAdapter);
					complianceAdapter.addAll(selectedList);
					new Thread(new Runnable() {
						@Override
						public void run() {
							Progress executor = new Progress(pb_scan, complianceAdapter, new Executor() {
								@Override
								public void done() {
									bt_export.setVisible(true);
									bt_next.setText(reg.getString("phase.window.next"));
									bt_next.setEnabled(true);
								}
							}, size);

							service.doTransformPhase(complianceAdapter.getList(), executor);
						}
					}).start();
					step = 4;
				} else if (step == 4) {
					txt_title.setText(reg.getString("phase.window.setp3"));
					bt_next.setText(reg.getString("phase.window.do"));
					showProgressBar(false);
					blueprintAdapter = new BlueprintAdapter(reg);
					table.setModel(blueprintAdapter);
					List<BlueprintBean> blueprintData = service.getBlueprintData();
					blueprintAdapter.addAll(blueprintData);
					bt_export.setVisible(false);
					step = 5;
				} else if (step == 5) {
					bt_next.setEnabled(false);
					if (blueprintAdapter.list.isEmpty()) {
						MsgBox.display(reg.getString("phase.window.noBlueprintTip"));
						bt_cancel.setVisible(false);
					} else {
						if (!service.checkModelType()) { // 判断顶层图号是否含有装配图/零件图
							MsgBox.display(reg.getString("phase.window.noAsmOrPrtTip"));
						} else {
							service.doUpdatePhase();
							bt_export.setVisible(true);
						}						
					}
					bt_next.setText(reg.getString("phase.window.done"));
					bt_next.setEnabled(true);
					step = 6;
				} else {
					frame.dispose();
				}
			}
		});

		bt_cancel = new JButton(reg.getString("phase.window.cancel"));
		bt_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int option = JOptionPane.showConfirmDialog(null, reg.getString("phase.window.close.content"),
						reg.getString("phase.window.close.title"), JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					frame.dispose();
				}
			}
		});

		bt_export = new JButton(reg.getString("phase.window.export"));
		bt_export.setVisible(false);
		bt_export.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (step == 2) {
					String s = saveFileDialog();
					if (s != null) {
						service.exportNotComplianceData(notComplianceAdapter.getList(), s);
						MsgBox.display(reg.getString("phase.window.export.done") + s);
					}
				} else if (step == 4) {
					String s = saveFileDialog();
					if (s != null) {
						service.exportAfterTransformPhaseData(complianceAdapter.getList(), s);
						MsgBox.display(reg.getString("phase.window.export.done") + s);
					}
				} else {
					String s = saveFileDialog();
					if (s != null) {
						service.exportBlueprint(blueprintAdapter.getList(), s);
						MsgBox.display(reg.getString("phase.window.export.done") + s);
					}
				}
			}
		});
		panel_1.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		panel_1.add(bt_next);
		panel_1.add(bt_cancel);
		panel_1.add(bt_export);

		JPanel panel_2 = new JPanel();
		frame.getContentPane().add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new BorderLayout(0, 0));
		chk_all = new JCheckBox(reg.getString("phase.window.check.allSelected"));
		panel_2.add(chk_all, BorderLayout.WEST);
		chk_all.setSelected(true);

		txt_title = new JLabel(reg.getString("phase.window.setp1"));
		panel_2.add(txt_title, BorderLayout.CENTER);
		txt_title.setFont(new Font("����", Font.BOLD, 12));
		txt_title.setHorizontalAlignment(SwingConstants.CENTER);
		chk_inspect = new JCheckBox(reg.getString("phase.window.check.notCompliance"));
		panel_2.add(chk_inspect, BorderLayout.EAST);
		chk_inspect.setSelected(true);
		chk_all.setVisible(false);
		chk_all.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selected = ((JCheckBox) e.getSource()).isSelected();
				complianceAdapter.selectAll(selected);
			}
		});
	}

	private String saveFileDialog() {
		JFileChooser chooser = new JFileChooser(saveDir);
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				String name = file.getName();
				return name.toLowerCase().endsWith(".xls") || name.toLowerCase().endsWith(".xlsx")
						|| file.isDirectory();
			}

			@Override
			public String getDescription() {
				return "*.xlsx";
			}
		});
		int result = chooser.showSaveDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			String savePath;
			if (file.exists()) {
				int option = JOptionPane.showConfirmDialog(null,
						reg.getString("phase.window.overwrite.content.left") + file.getName()
								+ reg.getString("phase.window.overwrite.content.right"),
						reg.getString("phase.window.overwrite.title"), JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					if (file.delete()) {
						savePath = file.getAbsolutePath();
					} else {
						MsgBox.display(reg.getString("phase.window.overwrite.failure"));
						return null;
					}
				} else {
					return null;
				}
			} else {
				savePath = file.getAbsolutePath() + ".xlsx";
			}
			saveDir = file.getParent();
			return savePath;
		}
		return null;
	}

	public void show() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				service.initData();
				pb_scan.setVisible(false);
				pb_scan.setStringPainted(true);
				bt_next.setVisible(true);
				pb_scan.setIndeterminate(false);
			}
		}).start();
		frame.setVisible(true);
	}

	public void dispose() {
		frame.dispose();
	}

	private void showProgressBar(boolean ft) {
//        frame.setSize(1169, 582);
		pb_scan.setVisible(ft);
	}
}
