package com.foxconn.mechanism.exportcreomodel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.json.JSONArray;
import com.foxconn.mechanism.exportcreomodel.constant.DatasetEnum;
import com.foxconn.mechanism.exportcreomodel.domain.ModelInfo;
import com.foxconn.mechanism.util.CommonTools;
import com.foxconn.mechanism.util.ProgressBarThread;
import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class ExportCeroModelDialog extends AbstractAIFDialog {

	private static final long serialVersionUID = 1L;
	private static DefaultTableModel dataSource;
	private JTable table;
	private TCSession session;
	private List<ModelInfo> list;
	private JButton exportButton;
	private JButton cancelButton;
	private JFileChooser jf = null;
	private Registry reg = null;
	private ProgressBarThread barThread = null;
	private String txtFileName;
	private String parentId; // 父ID
	private String parentVersion; // 父版本号
	private String modelType; // 模型名称
	private String creoExportAccountPreferenceName = "D9_Creo_Export_Account"; // Creo3D图档模型导出账号和密码

	private String creoPathPreferenceName = "D9_Creo_Path"; // Creo3D图档导出bat路径	

	private List<String> accountList = null;
	private String creoPathName = null;
	private String user = null;
	private String password = null;
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	private int DEFAULE_WIDTH = 1200;
	private int DEFAULE_HEIGH = 700;
	int Location_x = (int) (toolkit.getScreenSize().getWidth() - DEFAULE_WIDTH) / 2;
	int Location_y = (int) (toolkit.getScreenSize().getHeight() - DEFAULE_HEIGH) / 2;

	public ExportCeroModelDialog(TCSession session, List<ModelInfo> list, Registry reg) {
		super();
		this.session = session;
		this.list = list;
		this.reg = reg;
		barThread = new ProgressBarThread(reg.getString("Info.MSG"), reg.getString("processMsg.MSG"));
		// 界面初始化
		initUI();
	}

	/**
	 * 界面初始化
	 */
	private void initUI() {
		this.setModal(true);
		this.setAlwaysOnTop(true);
		this.setSize(DEFAULE_WIDTH, DEFAULE_HEIGH);
		setTitle(reg.getString("title.MSG"));
		setLayout(new BorderLayout());
//		setLayout(new FlowLayout(FlowLayout.CENTER));
		FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, FlowLayout.CENTER, 20);
		flowLayout.setVgap(5);
		flowLayout.setHgap(100);
//		buttonLayout.setAlignment(2);
		JPanel horPanel = new JPanel(flowLayout);
		horPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		/*
		 * mainPanel.setBounds(0, 0, 600, 500); mainPanel.setLayout(null);
		 * mainPanel.setBackground(Color.WHITE); this.add(mainPanel,
		 * BorderLayout.CENTER);
		 */

		JScrollPane scrollPane = new JScrollPane();

		scrollPane.setVerticalScrollBarPolicy(22);
		scrollPane.setHorizontalScrollBarPolicy(32);

		// scrollPane.setBounds(0, 0, 560, 400);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		table.setRowHeight(40);
		table.setDefaultRenderer(Object.class, new TableViewRenderer());

		scrollPane.setViewportView(table);
		table.setFillsViewportHeight(true);
		table.setColumnSelectionAllowed(true);

		// 获取table数据模型
		getTableModel();
		table.setModel(dataSource);

		// 设置表头居中
		((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

		// 设置单元格内容第一列居中
		DefaultTableCellRenderer render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);
		table.getTableHeader().getColumnModel().getColumn(0).setCellRenderer(render);

		// 设置列宽
		setColumnWidth();
		// 初始化数据
		initData();

		exportButton = new JButton(reg.getString("export.MSG"));
		horPanel.add(exportButton);

		cancelButton = new JButton(reg.getString("cancel.MSG"));
		horPanel.add(cancelButton);

		getContentPane().add(horPanel, BorderLayout.SOUTH);
		// 添加按钮时间监听
		addListen();
		this.setLocationRelativeTo((Component) AIFUtility.getActiveDesktop());
		this.setVisible(true);

		setLocation(Location_x, Location_y);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		try {
			for (int i = 0; i < list.size(); i++) {
				int k = i;
				ModelInfo info = list.get(i);
				String itemRevId = info.getItemRevId();
				String objectName = info.getObjectName();
				String newModelName = info.getNewModelName();
				dataSource.addRow(new String[] { String.valueOf(++k), itemRevId, objectName, newModelName });

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加按钮监听
	 */
	private void addListen() {
		exportButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					public void run() {
						if (null != table.getCellEditor()) {
							table.getCellEditor().stopCellEditing(); // 停止编辑
						}
						try {	
							dispose();
							// 获取导出到本地文件夹提示框
							File file = fileDialogExport();
							if (file == null) {
								new ExportCeroModelDialog(session, list, reg);
//								MessageBox.post(AIFUtility.getActiveDesktop(), reg.getString("exportDialogErr.MSG"),
//										reg.getString("ERROR.MSG"), MessageBox.ERROR);
								return;
							}
							String path = file.getAbsolutePath();
							// 判断输出路径是否包含空格
//							if (checkBlank(path)) {
//								MessageBox.post(AIFUtility.getActiveDesktop(), reg.getString("pathErr.MSG"), reg.getString("ERROR.WARNING"), MessageBox.WARNING);								
//								return;
//							}							
							System.out.println("Select absolutePath is : " + path);							
							barThread.start();

							// 校对模型是否已经存在
							/*
							 * List<String> list = checkNewModelName(); if (list != null && list.size() > 0)
							 * { new Thread() { public void run() { // barThread.stopBar(); new
							 * CheckDialog(list); // return; } }.start(); return; }
							 */
							// 获取父级ID和版本号
							getParentIdAndVersion();
							// 判断顶层图号是否含有装配图
							Boolean check = checkASM();
							if (check == false) {
								MessageBox.post(AIFUtility.getActiveDesktop(),
										reg.getString("itemId.MSG") + parentId + "," + reg.getString("version.MSG")
												+ parentVersion + "," + reg.getString("datasetErr.MSG"),
										reg.getString("WARNING.MSG"), MessageBox.WARNING);
								barThread.stopBar();
								return;
							}
							accountList = TCUtil.getArrayPreference(session, TCPreferenceService.TC_preference_site, creoExportAccountPreferenceName); // 获取Creo3D图档模型导出的用户和密码
							if (null == accountList || accountList.size() <= 0) {
								MessageBox.post(AIFUtility.getActiveDesktop(), creoExportAccountPreferenceName + reg.getString("Preference.MSG"), 
										reg.getString("Common.MSG"), MessageBox.WARNING);
								barThread.stopBar();
								return;
							}
							accountList.forEach(str -> {
								System.out.println(str);
								String[] split = str.split("=");
								if (str.contains("user")) {
									user = split[1];
								} else if (str.contains("password")) {
									password = split[1];
								}
							});
							
							// 导出Creo模型
							exportCreo(path);

							List<String> recordList = new ArrayList<String>();
							// 遍历table表格,下载drw和dxf文件
							traversalTable(path, recordList);
							// 记录新旧模型号的关系
							recordGenerate(path, recordList);
							// 重名名Creo
							/*
							 * Boolean flag = renameCreo(map); if (!flag) {
							 * MessageBox.post(AIFUtility.getActiveDesktop(), "Creo图纸导出失败", "ERROR",
							 * MessageBox.ERROR); return; }
							 */
							barThread.stopBar();
							MessageBox.post(AIFUtility.getActiveDesktop(), reg.getString("selecSucess.MSG"),
									reg.getString("INFORMATION.MSG"), MessageBox.INFORMATION);
						} catch (Exception e2) {
							e2.printStackTrace();
							barThread.stopBar();
							MessageBox.post(AIFUtility.getActiveDesktop(), reg.getString("selecErr1.MSG"),
									reg.getString("ERROR.MSG"), MessageBox.ERROR);

						}
					}
				}.start();
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

	/**
	 * 获取父级ID和版本号
	 */
	private void getParentIdAndVersion() {
		int rowCount = table.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			String itemRevId = (String) table.getValueAt(i, 1).toString().trim();
			if (i == 0) {
				parentId = itemRevId.split("/")[0];
				parentVersion = itemRevId.split("/")[1];
			}
		}
	}

	/**
	 * 判断顶层是否含有装配图
	 * 
	 * @return
	 * @throws TCException
	 */
	private Boolean checkASM() throws TCException {
		Boolean flag = false;
		TCComponentItemRevision itemRev = TCUtil.findItemRev(parentId, parentVersion);
		TCComponent[] specifications = itemRev.getRelatedComponents("IMAN_specification");
		TCComponent[] renderings = itemRev.getRelatedComponents("IMAN_Rendering");
		if ((null == specifications || specifications.length <= 0) && (null == renderings || renderings.length <= 0)) {
			return false;
		}
		List<TCComponent> specificationList = new ArrayList<>(Arrays.asList(specifications));
		List<TCComponent> renderingList = new ArrayList<>(Arrays.asList(renderings));
		List<TCComponent> resultlist = Stream.of(specificationList, renderingList).flatMap(x -> x.stream())
				.collect(Collectors.toList());
		for (TCComponent tcComponent : resultlist) {
			if (!(tcComponent instanceof TCComponentDataset)) {
				continue;
			}
			TCComponentDataset dataset = (TCComponentDataset) tcComponent;
			String objectType = dataset.getTypeObject().getName().toLowerCase();
			if (objectType.equals(DatasetEnum.PROASM.type())) { // 判断是否含含有装配数据集
				flag = true;
				modelType = "ProAsm";
				break;
			} else if (objectType.equals(DatasetEnum.PROPRT.type())) { // 判断是否含有零件数据集
				flag = true;
				modelType = "ProPrt";
//				break;
			}
		}
		if (flag) { // 代表含有装配图/零件图
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否含有空格
	 * 
	 * @param str
	 * @return
	 */
	private boolean checkBlank(String path) {
		if (path.contains(" ")) {
			System.out.println("文件保存路径:" + path + ", 含有空格");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 遍历table表格, 下载drw和dxf文件
	 * 
	 * @return
	 * @throws TCException
	 * @throws IOException
	 */
	private void traversalTable(String path, List<String> recordList) throws TCException, IOException {
		System.out.println("==========traversalTable start==========");
		int rowCount = table.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			String itemRevId = (String) table.getValueAt(i, 1).toString().trim();
//			if (i == 0) {
//				parentId = itemRevId.split("/")[0];
//				parentVersion = itemRevId.split("/")[1];
//			}
			String[] array = itemRevId.split("/");
			String itemId = array[0];
			System.out.println("==>> itemId: " + itemId);
			String version = array[1];
			System.out.println("==>> version: " + version);
			String newModelName = (String) table.getValueAt(i, 3).toString().trim();
			if (i == 0) {
				txtFileName = newModelName;
			}
			System.out.println("==>> newModelName: " + newModelName);
			TCComponentItemRevision itemRev = TCUtil.findItemRev(itemId, version);
			String itemRevObjectName = itemRev.getProperty("object_name");
			System.out.println("==>> itemRevObjectName: " + itemRevObjectName);
			TCComponent[] relatedComponents = itemRev.getRelatedComponents("IMAN_specification");
			TCComponent[] relatedComponents2 = itemRev.getRelatedComponents("IMAN_Rendering");
			if ((null == relatedComponents || relatedComponents.length <= 0)
					&& (null == relatedComponents2 || relatedComponents2.length <= 0)) {
				continue;
			}
			// 获取数据集名称
			getDatasetName(newModelName, relatedComponents, path, recordList, itemRevObjectName);
			// 获取数据集名称
			getDatasetName(newModelName, relatedComponents2, path, recordList, itemRevObjectName);

		}
		System.out.println("==========traversalTable ending==========");
	}

	/**
	 * 获取数据集名称
	 * 
	 * @param newModelName      新模型名称
	 * @param relatedComponents
	 * @param path              选中目录的路径
	 * @throws TCException
	 * @throws IOException
	 */
	private void getDatasetName(String newModelName, TCComponent[] relatedComponents, String path,
			List<String> recordList, String itemRevObjectName) throws TCException, IOException {
		for (TCComponent tcComponent : relatedComponents) {
			boolean flag = false;
			if (tcComponent instanceof TCComponentDataset) {
				TCComponentDataset dataset = (TCComponentDataset) tcComponent;
				dataset.refresh();
				String objectName = dataset.getProperty("object_name");
				String objectType = dataset.getTypeObject().getName().toLowerCase();
				String fileExtensions = ""; // 文件后缀
				if (objectType.equals(DatasetEnum.PROASM.type())) { // Creo装配图
					fileExtensions = ".asm";
					flag = true;
				} else if (objectType.equals(DatasetEnum.PROPRT.type())) { // Creo零件图
					fileExtensions = ".prt";
					flag = true;
				} else if (objectType.equals(DatasetEnum.DRW.type())) { // drw图纸
					fileExtensions = ".drw";
					// 下载数据集
//					exportFileToDir(dataset, path, true, fileExtensions, itemRevObjectName, false); // Drw图纸目前不需要重命名
					exportFileToDir(dataset, path, DatasetEnum.DRW.fileExtensions(), DatasetEnum.DRW.refName(),
							itemRevObjectName, false); // drw图纸目前不需要重命名
					// 下载Frm图框文件
					downloadFrameFile(dataset, path);
				} else if (objectType.equals(DatasetEnum.DXF.type())) {
					fileExtensions = ".dxf";
					// 下载数据集
//					exportFileToDir(dataset, path, true, fileExtensions, itemRevObjectName, true);
					exportFileToDir(dataset, path, DatasetEnum.DXF.fileExtensions(), DatasetEnum.DXF.refName(),
							itemRevObjectName, true);
				} else if (objectType.equals(DatasetEnum.PDF.type())) {
					fileExtensions = ".pdf";
					// 下载数据集
//					exportFileToDir(dataset, path, true, fileExtensions, itemRevObjectName, true);
					exportFileToDir(dataset, path, DatasetEnum.PDF.fileExtensions(), DatasetEnum.PDF.refName(),
							itemRevObjectName, true);
				}

				if ("".equals(fileExtensions)) {
					continue;
				}
				if (!flag) {
					continue;
				}

//				String fileName = objectName + fileExtensions;
				// 导出数据集到指定的目录
//				exportFileToDir(dataset, path, fileName);

				String content = objectName + " " + newModelName;
				if (!recordList.contains(content)) {
					recordList.add(content);
				}
			}
		}
	}

	/**
	 * 下载Frm图框文件
	 * 
	 * @param dataset 数据集
	 * @param path    文件夹路径
	 * @throws TCException
	 */
	private void downloadFrameFile(TCComponentDataset dataset, String path) throws TCException {
		TCComponent[] tcComponents = dataset.getRelatedComponents("Pro2_format");
		if (null == tcComponents || tcComponents.length <= 0) {
			return;
		}
		TCComponentItemRevision itemRevision = null;
		for (TCComponent tcComponent : tcComponents) {
			if (tcComponent instanceof TCComponentItemRevision) {
				itemRevision = (TCComponentItemRevision) tcComponent;
				break;
			}
		}
		if (null == itemRevision) {
			return;
		}
		TCComponent[] imanSpecifications = itemRevision.getRelatedComponents("IMAN_specification");
		if (null == imanSpecifications || imanSpecifications.length <= 0) {
			return;
		}
		for (TCComponent component : imanSpecifications) {
			if (!(component instanceof TCComponentDataset)) {
				continue;
			}
			TCComponentDataset tcComponentDataset = (TCComponentDataset) component;
			String objectType = tcComponentDataset.getTypeObject().getName().toLowerCase();
			if (objectType.equals(DatasetEnum.PROFRM.type())) { // 判断数据集类型是否为Frm文件
				// 下载数据集
				exportFileToDir(tcComponentDataset, path, DatasetEnum.PROFRM.fileExtensions(),
						DatasetEnum.PROFRM.refName(), "", false); // frm图框文件不需要重命名
			}
		}

	}

	/**
	 * 记录新旧模型号的关系
	 * 
	 * @param txtFilePath
	 * @param content
	 * @throws IOException
	 */
	private void recordGenerate(String path, List<String> recordList) throws IOException {
		String txtFilePath = path + "\\" + txtFileName + ".txt";
		File file = new File(txtFilePath);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		System.out.println("==========start writing txtFile==========");
		for (String content : recordList) {
			// 将内容写入到TXT文档
			write(txtFilePath, content);
		}
		System.out.println("==========ending writing txtFile==========");
	}

	/**
	 * 导出数据集到指定的目录
	 * 
	 * @param dataset 数据集
	 * @param dir     文件路径
	 * @param rename  重命名名称
	 * @return
	 */
	private String exportFileToDir(TCComponentDataset dataset, String dir, boolean isCopy, String fileExtensions,
			String itemRevObjectName, boolean rename) {
		String filePath = TCUtil.downloadFile(dataset, dir, isCopy, fileExtensions, session, itemRevObjectName, rename);
		System.out.println("==>> filePath: " + filePath);
		return "";
	}

	/**
	 * 导出数据集到指定目录
	 * 
	 * @param dataset           数据集
	 * @param dir               文件路径
	 * @param fileExtensions    后缀名
	 * @param refName           命名的引用
	 * @param itemRevObjectName 对象版本名称
	 * @param rename            是否需要重命名
	 * @return
	 */
	private String exportFileToDir(TCComponentDataset dataset, String dir, String fileExtensions, String refName,
			String itemRevObjectName, boolean rename) {
		String absoluteFilePath = TCUtil.downloadFile(dataset, dir, fileExtensions, refName, itemRevObjectName, rename);
		System.out.println("==>> absoluteFilePath: " + absoluteFilePath);
		return absoluteFilePath;
	}

	/**
	 * 讲内容写入到文件中
	 * 
	 * @param fileName
	 * @param content
	 */
	private void write(String fileName, String content) {
		System.out.println("==>> writing content is: " + content);
		try {
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content + "\r\n");
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 判断新模型名称是否在Teamcenter中已经存在
	 * 
	 * @param k
	 * @param newModelName
	 * @return
	 * @throws TCException
	 */
	private List<String> checkNewModelName() throws TCException {
		System.out.println("==========checkNewModelName start==========");
		int rowCount = table.getRowCount();
		List<String> resultList = new ArrayList<String>();
		for (int i = 0; i < rowCount; i++) {
			int k = i;
			String itemRevId = (String) table.getValueAt(i, 1).toString().trim();
			String[] array = itemRevId.split("/");
			String itemId = array[0];
			System.out.println("==>> itemId: " + itemId);
			String version = array[1];
			System.out.println("==>> version: " + version);
			String newModelName = (String) table.getValueAt(i, 3).toString().trim();
			System.out.println("==>> newModelName: " + newModelName);
			String msg = "";
			TCComponentDataset dataset = TCUtil.findDataset(newModelName);
			if (dataset != null) {
				msg = "第" + String.valueOf(++k) + "行，新模型名称为: " + newModelName + ",在Teamcenter中存在";
				if (!resultList.contains(msg)) {
					resultList.add(msg);
				}
			}
		}
		System.out.println("==========checkNewModelName ending==========");
		return resultList;
	}

	/**
	 * 重命名Creo
	 * 
	 * @param map
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private Boolean renameCreo(Map<String, String> map) throws IOException, InterruptedException {
		System.out.println("==========renameCreo start==========");
		String itemId = "";
		String objectName = "";
		String newModelName = "";
		String fileExtensions = "";
		try {
			List<Map<String, String>> paramsList = new ArrayList<Map<String, String>>();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				Map<String, String> paramsMap = new HashMap<String, String>();
				String key = entry.getKey();
				itemId = key.split("\\|")[0];
				System.out.println("==>> itemId: " + itemId);
				objectName = key.split("\\|")[1];
				System.out.println("==>> objectName: " + objectName);
				String value = entry.getValue();
				newModelName = value.split("\\|")[0];
				System.out.println("==>> newModelName: " + newModelName);
				fileExtensions = value.split("\\|")[1];
				System.out.println("==>> fileExtensions: " + fileExtensions);
				paramsMap.put("-item", itemId);
				paramsMap.put("-type", fileExtensions);
				paramsMap.put("-old_name", objectName);
				paramsMap.put("-new_name", newModelName);
				paramsList.add(paramsMap);
				/*
				 * String comandStr = "C:\\Siemens\\ipem1\\ipemrename.bat" + " " + "-u=MW00309"
				 * + " " + "-p=123456" + " " + "-item=" + itemId + " " + "-type=" +
				 * fileExtensions + " " + "-old_name=" + objectName + " " + "-new_name=" +
				 * newModelName + " " +
				 * "-autorename_item_name=true -autorename_item_id=false -autorename_other=false -no_gui"
				 * ; System.out.println("==>> command: " + comandStr);
				 */

				/*
				 * Process process = Runtime.getRuntime().exec("cmd /c start " + comandStr);
				 * System.out.println(process.getInputStream());
				 */

				// 后台执行bat文件
//					doCallBat(comandStr, "ipemrename.bat");

			}
			JSONArray jsonArray = new JSONArray(paramsList);
			String json = jsonArray.toString();
			json = json.replaceAll("\"", "\\\\\"");
			System.out.println("==>> json: " + json);

			String batPath = "C:\\Siemens\\ipem2\\ipemrename-creo.bat" + " " + "-u=MW00309" + " " + "-p=123456" + " "
					+ "-json=" + json;
			doCallBat(batPath, "ipemrename-creo.bat");
			System.out.println("=========renameCreo ending==========");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("rename failure: " + e.getMessage());
		}

		return false;
	}

	/**
	 * 导出Creo模型
	 * 
	 * @param map
	 * @param path 文件存放的路径
	 * @return
	 */
	private Boolean exportCreo(String path) {
		System.out.println("==========exportCreo start==========");
		String itemId = "";
		String newModelName = "";
		String fileExtensions = "";
//		for (Map.Entry<String, String> entry : map.entrySet()) {
		try {
			System.out.println("==>> parentId: " + parentId);
			System.out.println("==>> parentVersion: " + parentVersion);
			/*
			 * String comandStr = "C:\\Siemens\\ipem1\\ipemexport.bat" + " " + "-u=MW00309"
			 * + " " + "-p=123456" + " " + "-item_id=" + parentId + " " +
			 * "-item_revision_id=" + parentVersion + " " + "-export_dir=" + path;
			 */

//			String comandStr = "C:\\Siemens\\ipem1\\ipemexport.bat" + " " + "-u=infodba" + " " + "-p=infodba" + " "
//					+ "-item_id=" + parentId + " " + "-item_revision_id=" + parentVersion + " " + "-export_dir=" + path
//					+ " " + "-export=true -export_related=true -no_filters";

//			String comandStr = "C:\\Siemens\\ipem1\\ipemexport.bat" + " " + "-u=infodba" + " " + "-p=infodba" + " "
//					+ "-item_id=" + parentId + " " + "-item_revision_id=" + parentVersion + " " + "-export_dir=" + path
//					+ " " + "-export=true -export_related=true -delete_files";

			
			creoPathName = TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, creoPathPreferenceName); // 获取Creo3D图档模型导出bat路径
			if (CommonTools.isEmpty(creoPathName)) {
				MessageBox.post(AIFUtility.getActiveDesktop(), creoPathPreferenceName + reg.getString("Preference.MSG"), reg.getString("Common.MSG"), MessageBox.WARNING);
				return false;
			}
			
			String comandStr = creoPathName + "\\ipemexport.bat" + " " + "-u=" + user + " " + "-p=" + password + " "
					+ "-item_id=" + parentId + " " + "-item_revision_id=" + parentVersion + " " + "-model_type="
					+ modelType + " " + "-export_dir=\"" + path +"\"" +" " + "-export=true -export_related=true -delete_files";

			System.out.println("==>> comandStr: " + comandStr);
			doCallBat(comandStr, "ipemexport.bat");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(
					AIFUtility.getActiveDesktop(), reg.getString("item.MSG") + "ID:" + itemId + ","
							+ reg.getString("newModelName.MSG") + newModelName + reg.getString("renameErr.MSG"),
					reg.getString("ERROR.MSG"), MessageBox.ERROR);
			return false;
		}
//		}
		System.out.println("==========exportCreo ending==========");
		return true;
	}

	/**
	 * 后台执行bat文件
	 * 
	 * @param batPath
	 * @param batName
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void doCallBat(String batPath, String batName) throws IOException, InterruptedException {
		if (batName == null || "".equals(batName)) {
			batName = new File(batPath).getName();
		}
		List list = new ArrayList();
		ProcessBuilder processBuilder = null;
		Process process = null;
		String line = null;
		BufferedReader stdout = null;
		list.add("cmd");
		list.add("/c");
//		list.add("start");
		list.add(batPath);
		processBuilder = new ProcessBuilder(list);
		processBuilder.redirectErrorStream(true);
		process = processBuilder.start();
		stdout = new BufferedReader(new InputStreamReader(process.getInputStream(), "BIG5"));
		OutputStreamWriter os = new OutputStreamWriter(process.getOutputStream());
		while ((line = stdout.readLine()) != null) {
			System.out.println("【" + batName + "】:" + line);
			if ("ending".equals(line)) {
				break;
			}
		}
		int ret = process.waitFor();
		stdout.close();
		System.out.println("========bat File excute finsh=========");
	}

	/**
	 * 文件导出路径
	 * 
	 * @return
	 */
	private File fileDialogExport() {
		try {
			if (null == jf) {
				jf = new JFileChooser();
			}
			jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = jf.showOpenDialog(jf);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = jf.getSelectedFile();
				return file;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 设置列宽
	 */
	private void setColumnWidth() {
		// 设置第一列固定
		TableColumn column = table.getColumnModel().getColumn(0);
		column.setMinWidth(45);
		column.setMaxWidth(45);
		table.getColumnModel().getColumn(0).setPreferredWidth(45);

		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setPreferredWidth(250);
		table.getColumnModel().getColumn(3).setPreferredWidth(250);
	}

	/**
	 * 获取table数据模型
	 */
	private void getTableModel() {

		dataSource = new DefaultTableModel(new Object[0][],
				new Object[] { reg.getString("index.MSG"),
						reg.getString("item.MSG") + "ID/" + reg.getString("version.MSG"), reg.getString("itemName.MSG"),
						reg.getString("newModelName.MSG") }) {

			Class[] columnTypes = new Class[] { String.class, String.class, String.class, String.class };

			public Class getColumnClass(int columnIndex) {
				return this.columnTypes[columnIndex];
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				if (column < 3) {
					return false;
				}
				return true;
			}

		};

	}

	public void dispose(Thread t) {
		try {
			t.interrupt();				
			t = null;
		} catch (Exception e) {
		}
	}
}
