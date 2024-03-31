package com.teamcenter.rac.workflow.commands.newperformsignoff;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.hh.fx.rewrite.GetPreferenceUtil;
import com.hh.fx.rewrite.util.CheckUtil;
import com.hh.fx.rewrite.util.FileStreamUtil;
import com.hh.fx.rewrite.util.HHDateButton;
import com.hh.fx.rewrite.util.HHTextField;
import com.hh.fx.rewrite.util.ProgressBarThread;
import com.hh.fx.rewrite.util.Utils;
import com.hh.tools.customerPanel.EDACompCustomerPanel;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.classification.common.G4MUserAppContext;
import com.teamcenter.rac.classification.common.tree.G4MTree;
import com.teamcenter.rac.classification.common.tree.G4MTreeNode;
import com.teamcenter.rac.classification.icm.ClassificationService;
import com.teamcenter.rac.common.lov.LOVComboBox;
import com.teamcenter.rac.kernel.TCClassificationService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentActionHandler;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentICO;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProject;
import com.teamcenter.rac.kernel.TCComponentSignoff;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.ics.ICSAdminClass;
import com.teamcenter.rac.kernel.ics.ICSApplicationObject;
import com.teamcenter.rac.kernel.ics.ICSFormat;
import com.teamcenter.rac.kernel.ics.ICSKeyLov;
import com.teamcenter.rac.kernel.ics.ICSProperty;
import com.teamcenter.rac.kernel.ics.ICSPropertyDescription;
import com.teamcenter.rac.kernel.ics.ICSView;
import com.teamcenter.rac.util.DateButton;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class EDACompDecisionDialog2 extends DecisionDialog {

	JCheckBox checkBox = null;
	JCheckBox checkBox1 = null;
	private static TCComponent edaComp = null;
	boolean flag = false;

	public static FileStreamUtil fileStreamUtil = new FileStreamUtil();
	public static PrintStream printStream = null;
	public static SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-M-dd HH:mm");
	public static JPanel contentPanel = new JPanel();
	public static TCComponentItemRevision itemRev = null;
	public static int height = 25;
	public static TCSession session = null;
	public static HHTextField idField;
	public static HHTextField revField;
	public static HHTextField nameField;
	// HW Property
	public static LOVComboBox categoryLov;
	public static LOVComboBox partTypeLov;
	public static HHTextField creatorField;
	public static HHTextField MfgPNField;
	public static LOVComboBox projectLov;
	public static HHDateButton updateTime;
	public static HHTextField MfgField;
	public static JButton MgfButton;
	public static HHTextField dataSheetField;
	public static JButton dataSheetButton;
	public static HHTextField HHPNField;
	public static HHTextField modifyCodeField;
	public static LOVComboBox itemTypeLov;
	public static HHTextField dellField;
	public static JButton dellChooseButton;
	public static JButton dellSelectButton;
	public static HHTextField symbolField;
	public static TCComponentProject oldProject = null;
	public static TCComponentDataset dataSheetDataset = null;
	public static TCComponentDataset symbolDataset = null;
	public static TCComponentDataset dellSymbolDataset = null;
	public static TCComponentDataset footprintDataset = null;
	public static TCComponentDataset PADDataset = null;
	public static JButton symbolChooseButton;
	public static JButton symbolSelectButton;

	public static HHTextField standardPNField;
	public static HHTextField modifyNameField;
	public static HHTextField functionField;
	public static HHTextField weightField;

	public static LOVComboBox footprintLov;
	public static HHTextField PADField;
	public static JButton PADChooseButton;

	public static HHTextField footprintField;
	public static JButton footprintChooseButton;
	public static JButton footprintSelectButton;
	private String psName = "";
	private String templateName = "";
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm");
	private JTextField commodifyNameCNtextField;
	private Map<String, String> mapClassify;
	private JButton saveButton = null;
	Registry reg = null;
	JPanel mainPanel = new JPanel();
	JPanel upPanel = new JPanel();
	JPanel classPropertyPanel = new JPanel();
	static JPanel panelRight = null;
	boolean isActionHandler1 = false;
	boolean isActionHandler2 = false;
	HashMap<String, String> editAreaMap = new HashMap<String, String>();
	boolean isClassEnable = true;
	private EDACompCustomerPanel EDAPanel = null;
	private ProgressBarThread bar = new ProgressBarThread("提示", "正在加载页面，请稍后");
	private ArrayList<JPanel> list = new ArrayList<>();
	private boolean edaDialog = false;

	public EDACompDecisionDialog2(AIFDesktop paramAIFDesktop, TCComponentTask paramTCComponentTask,
			TCComponentSignoff paramTCComponentSignoff) {
		super(paramAIFDesktop, paramTCComponentTask, paramTCComponentSignoff);
		System.out.println("EDACompDecisionDialog 1");
		initEdaComp();
	}

	public EDACompDecisionDialog2(Dialog paramDialog, TCComponentTask paramTCComponentTask,
			TCComponentSignoff paramTCComponentSignoff) {
		super(paramDialog, paramTCComponentTask, paramTCComponentSignoff);
		// TODO Auto-generated constructor stub
		System.out.println("EDACompDecisionDialog 2");
		initEdaComp();
	}

	private void initEdaComp() {
		try {
			TCComponent[] coms = psTask.getRelatedComponents("root_target_attachments");
			for (int i = 0; i < coms.length; i++) {
				System.out.println("对象类型 ==" + coms[i].getType());
				if (coms[i].isTypeOf("EDAComp Revision")) {
					edaComp = coms[i];
					itemRev = (TCComponentItemRevision) edaComp;
					break;
				}
			}
			TCComponentActionHandler[] actionHandlers = psTask.getActionHandlers(TCComponentTask.COMPLETE_ACTION);
			System.out.println("actionHandlers ==" + actionHandlers.length);
			if (actionHandlers.length < 1) {
				super.initializeDialog();
			} else {
				for (int i = 0; i < actionHandlers.length; i++) {
					String actionName = actionHandlers[i].getProperty("object_name");
					if (actionName.equals("FOXCONN_cust_signoff_page")
							|| actionName.equals("cust_automatic_Classification")) {
						edaDialog = true;
					}
				}
			}
			if(!edaDialog){
				super.initializeDialog();
				return;
			}else{
				bar.start();
				for (int i = 0; i < actionHandlers.length; i++) {
					System.out.println("actionHandlers == " + actionHandlers[i]);
					String actionName = actionHandlers[i].getProperty("object_name");
					System.out.println("actionName ==" + actionName);
//					if (!actionName.equals("FOXCONN_cust_signoff_page")) {
//						continue;
//					}
					isActionHandler1 = true;
					TCProperty prop = actionHandlers[i].getTCProperty("handler_arguments");
					System.out.println("prop ==" + prop);
					String[] values = prop.getStringArrayValue();
					for (int j = 0; j < values.length; j++) {
						if (!"".equals(values[j]) && values[j].contains("=")) {
							String[] temps = values[j].split("=");
							if (temps.length == 2) {
								if (temps[0].equals("-EditArea")) {
									editAreaMap.put(temps[1], temps[1]);
								}
							}
						}
					}

					if (actionName.equals("cust_automatic_Classification")) {
						isActionHandler2 = true;
					}
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void initializeDialog() {
		// TODO Auto-generated method stub
		if (isActionHandler1) {
			initUI();
			super.initializeDialog();
			getContentPane().removeAll();
			getContentPane().setFocusable(false);
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(upPanel, BorderLayout.CENTER);
			mainPanel.add(masterPanel, BorderLayout.SOUTH);
			getContentPane().add(mainPanel, BorderLayout.CENTER);
			centerToScreen();
			bar.stopBar();
			pack();
		} else {
			super.initializeDialog();
		}

	}

	private void initUI() {

		JScrollPane scPanel = new JScrollPane();

	    JPanel panelLeft = null;
		try {
			System.out.println("edaComp ==" + edaComp);
			EDAPanel = new EDACompCustomerPanel(itemRev);
			System.out.println("=======");
			panelLeft = EDAPanel.getEDACompPanel();
			EDAPanel.editButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("--编辑鸿海料号---");
					EDAPanel.HHPNField.setEnabled(true);
				}
			});
			// 全部不可编辑
			setEnableFalseAll();
			if (editAreaMap.containsKey("HW Property")) {
				setEnableHWProperty();
			}
			if (editAreaMap.containsKey("CE Property")) {
				setEnableCEProperty();
			}
			if (editAreaMap.containsKey("Layout Property")) {
				setEnableLayProperty();
			}

			if (!editAreaMap.containsKey("Library Property")) {
				isClassEnable = false;		
			}
			System.out.println("panelLeft ==" + panelLeft);
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		panelRight = new JPanel();
		upPanel.setPreferredSize(new Dimension(900, 400));
		upPanel.setLayout(new BoxLayout(upPanel, BoxLayout.Y_AXIS));
		{
			upPanel.add(panelLeft, BorderLayout.CENTER);
		}

		// 加载数据
		System.out.println("isActionHandler2 ==" + isActionHandler2);
		// 5 panel_4
		if (isActionHandler2) {

			upPanel.setPreferredSize(new Dimension(900, 600));

			panelRight.setPreferredSize(new Dimension(900, 500));
			panelRight.setBorder(
					new TitledBorder(null, "Library Property", TitledBorder.LEADING, TitledBorder.TOP, null, null));

			// 需要根据对象分类清空添加内容

			try {
//				String partType = edaComp.getProperty("fx8_PartType");
//				System.out.println("partType == " + partType);

				String category = edaComp.getProperty("fx8_Category");
				System.out.println("category == " + category);

				TCComponentICO[] icos = edaComp.getClassificationObjects();
				if (session == null) {
					session = edaComp.getSession();
				}
				TCClassificationService icsServer = session.getClassificationService();
				ICSApplicationObject icsAppObj = icsServer.newICSApplicationObject("ICM");
				if (icos != null && icos.length != 0) {

					String classId = icos[0].getProperty("object_type_id");
					System.out.println("classId == " + classId);

					icsAppObj.setView(classId);

					ICSView icsView = icsAppObj.getView();

					String viewName = icsView.getName();
					System.out.println("viewName == " + viewName);

					if (viewName.equals(category)) {
						System.out.println("----------category----------");
						ICSPropertyDescription icsPropertyDescription = null;

						ICSProperty[] icsProperties = icos[0].getICSProperties(true);
						for (int i = 0; i < icsProperties.length; i++) {

							int valueId = icsProperties[i].getId();
							String value = icsProperties[i].getValue();

							icsPropertyDescription = icsView.getPropertyDescription(valueId);
							String className = icsPropertyDescription.getName();

							System.out.println("value == " + value);
							System.out.println("valueId == " + valueId);
							System.out.println("className == " + className);

							classPropertyPanel
									.add(getICSPropDescInfo(icsPropertyDescription, value, valueId, className));
						}
					} else {
						System.out.println("--2--");
						addClasspropetyPanel(icsAppObj, category);
					}

				} else { // 根据parttype信息处理
					System.out.println("--3--");
					addClasspropetyPanel(icsAppObj, category);
				}
				for (int i = 0; i < list.size(); i++) {
					System.out.println("i ==" + i);
					if (i % 2 == 0) {
						panelRight.add((10 + i) + ".1.left.left.preferred.preferred", list.get(i));
					} else {
						panelRight.add((10 + i) + ".2.right.right.preferred.preferred", list.get(i));
					}
				}
				// 添加 HF Status栏位
				JScrollPane jScrollPane = new JScrollPane();
				jScrollPane.setViewportView(panelRight);
				upPanel.add(jScrollPane, BorderLayout.CENTER);// ,BorderLayout.CENTER

				JPanel southPanel = new JPanel();
				southPanel.setPreferredSize(new Dimension(900, 100));
				southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
				upPanel.add(southPanel, BorderLayout.SOUTH);// ,BorderLayout.CENTER
				saveButton = new JButton("SAVE CLASSIFY");
				saveButton.setEnabled(isClassEnable);
				saveButton.setPreferredSize(new Dimension(120, 27));
				saveButton.setActionCommand("SAVE CLASSIFY");
				southPanel.add(saveButton);
				saveButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// 保存分类
//						doSaveClass();
						try {
							sendItemOrItemRevisionToClassfication(edaComp);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	// 设置控件情况

	// 根据parttype信息处理
	public void addClasspropetyPanel(ICSApplicationObject icsAppObj, String category) {
		try {
			if (!"".equals(category)) {
				ClassificationService clafService = new ClassificationService();
				String partClassifyRootId = "ICM";
				G4MUserAppContext g4mUserAppContext = new G4MUserAppContext(clafService, partClassifyRootId);
				G4MTree g4mtree = new G4MTree(g4mUserAppContext);

				G4MTreeNode rootNode = g4mtree.findNode("ICM");
				System.out.println("rootNode == " + rootNode);
				g4mtree.setRootNode(rootNode, true);
				if (rootNode != null) {
					String classId = doSearchClassView(g4mtree, rootNode, category);

					// String classId = rootNode.getICSDescriptor().getId();
					System.out.println("classId == " + classId);
					if ("".equals(classId)) {
						return;
					}
					icsAppObj.setView(classId);

					ICSView icsView = icsAppObj.getView();
					ICSPropertyDescription[] icsPropertyDescriptions = icsView.getPropertyDescriptions();
					for (int i = 0; i < icsPropertyDescriptions.length; i++) {
						String className = icsPropertyDescriptions[i].getName();
						System.out.println("className == " + className);
						int valueId = icsPropertyDescriptions[i].getId();
						classPropertyPanel.add(getICSPropDescInfo(icsPropertyDescriptions[i], "", valueId, className));

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 查找分类节点
	public String doSearchClassView(G4MTree g4mtree, G4MTreeNode rootNode, String parentNodeName) {
		try {
			if (rootNode != null) {
				G4MTreeNode parentNode = null;
				G4MTreeNode childrenNode = null;

				parentNode = getParentNode(g4mtree, rootNode, parentNodeName);
				System.out.println("parentNode == " + parentNode);
				// 获取 parentNode id
				if (parentNode != null) {
					return parentNode.getICSDescriptor().getId();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private G4MTreeNode getParentNode(G4MTree g4mtree, G4MTreeNode rootNode, String parentNodeName) {
		int count = rootNode.getChildCount();
		G4MTreeNode parentNode = null;
		for (int i = 0; i < count; i++) {
			G4MTreeNode treeNode = (G4MTreeNode) rootNode.getChildAt(i);
			g4mtree.setRootNode(treeNode, true);
			String className = treeNode.getICSDescriptor().getName();
			System.out.println("className == " + className);

			if (parentNodeName.equals(className)) {
				parentNode = treeNode;
				return parentNode;
			}

			parentNode = getParentNode(g4mtree, treeNode, parentNodeName);
			if (parentNode != null) {
				return parentNode;
			}
		}
		return parentNode;
	}

	/**
	 * TC10 无keyLov，所以不做检查，TC11\TC12需要做检查
	 * 
	 * @param propDesc
	 * @param value
	 * @param valueId
	 * @return
	 * @return
	 */
	private ClassPanel getICSPropDescInfo(ICSPropertyDescription propDesc, String value, int valueId,
			String className) {
		// TODO Auto-generated method stub
		ICSKeyLov keyLov = null; // LOV

		ICSFormat icsFormat = null;

		try {
			boolean isNumeric = propDesc.isNumeric(); // 是否是數字
			System.out.println("isNumeric == " + isNumeric);
			keyLov = propDesc.getKeyLov(); // LOV

			icsFormat = propDesc.getFormat();

			int size = icsFormat.getSize();
			System.out.println("size == " + size);

			System.out.println("icsFormat == " + icsFormat);

			String dispStr = icsFormat.getDisplayString();
			System.out.println("dispStr == " + dispStr);

			int vlaLength = propDesc.getVLALength();
			System.out.println("vlaLength == " + vlaLength);

			System.out.println("加载ValueId ==" + valueId);
			boolean isReference = propDesc.isReference();
			System.out.println("isReference == " + isReference);

			String refType = propDesc.getRefType();
			String refObjectType = propDesc.getRefObjectType();
			String refOptions = propDesc.getRefOptions();
			System.out.println("refType == " + refType);
			System.out.println("refObjectType == " + refObjectType);
			System.out.println("refOptions == " + refOptions);

			// 控件相关
			final ClassPanel classPanel = new ClassPanel(new FlowLayout(FlowLayout.LEFT));
			JLabel label = new JLabel(className + ":", JLabel.RIGHT);
			label.setPreferredSize(new Dimension(200, 25));
			classPanel.add(label);
			if (icsFormat.getType() == ICSFormat.DATE) { // 日期
				SimpleDateFormat dateFormat = icsFormat.getDateFormatter(icsFormat.getFormat());
				Date date = null;
				if (value != null) {
					date = dateFormat.parse(value);
				}

				DateButton dateButton = new DateButton(dateFormat);
				dateButton.setDate(date);
				dateButton.setPreferredSize(new Dimension(200, 25));
				classPanel.add(dateButton);
				dateButton.setEnabled(!isReference);
				classPanel.setComponent(dateButton);
				if (!isReference) {
					dateButton.setEnabled(isClassEnable);
				}

				classPanel.setComponent(dateButton);
			} else if (icsFormat.getType() == ICSFormat.LIST) { // 下拉选项

				String[] keys = keyLov.getKeys();
				String[] values = keyLov.getValues();
				String[] keyValues = keyLov.getKeyValues();
				String[] displayValues = keyLov.getDisplayValues();
				JComboBox comboBox = new JComboBox();
				HashMap<String, String> lovMap = new HashMap<String, String>();
				for (int i = 0; i < keys.length; i++) {
					System.out.println("keys[i] == " + keys[i]);
					System.out.println("values[i] == " + values[i]);
					System.out.println("keyValues[i] == " + keyValues[i]);
					System.out.println("displayValues[i] == " + displayValues[i]);
					lovMap.put(values[i], keys[i]);
					comboBox.addItem(values[i]);
				}

				comboBox.setPreferredSize(new Dimension(200, 25));
				classPanel.add(comboBox);
				comboBox.setEditable(!isReference);
				comboBox.setSelectedItem(value);
				classPanel.setComponent(comboBox);
				classPanel.setLovMap(lovMap);
				if (!isReference) {
					comboBox.setEnabled(isClassEnable);
				}
				classPanel.setComponent(comboBox);

			} else { // 字符串
				final JTextField field = new JTextField();
				field.setPreferredSize(new Dimension(200, 25));
				classPanel.add(field);
				field.setEditable(!isReference);

				if ("".equals(refOptions)) {
					field.setText(value);
				} else {
					field.setText(edaComp.getProperty(refOptions));
				}
				if (!isReference) {
					field.setEditable(isClassEnable);
				}
				if (className.equals("PART_TYPE")) {
					System.out.println("className =========" + className);
					String fx8_Category = itemRev.getProperty("fx8_Category");
					String fx8_PartType = itemRev.getProperty("fx8_PartType");
					field.setText(fx8_Category + "\\" + fx8_PartType);
					field.setEnabled(false);
				}
				if (className.equals("SCHEMATIC_PART")) {
					if ("".equals(field.getText())) {
						field.setText("请从Symbol中获取");
						field.setForeground(new Color(204, 204, 204));
					}
					field.addFocusListener(new FocusListener() {

						@Override
						public void focusLost(FocusEvent e) {
							if (field.getText().length() < 1) {
								field.setText("请从Symbol中获取");
								field.setForeground(new Color(204, 204, 204));
							}

						}

						@Override
						public void focusGained(FocusEvent e) {
							if (field.getText().equals("请从Symbol中获取")) {
								field.setText("");
								field.setForeground(Color.BLACK);
							}

						}
					});
				}
				classPanel.setComponent(field);

			}

			classPanel.setICSType(icsFormat.getType());
			classPanel.setReference(isReference);
			classPanel.setValueId(valueId);
			list.add(classPanel);
			return classPanel;
		} catch (Exception e) {
			e.printStackTrace();

		} finally {

		}

		return null;
	}
	

	// 点击确定按钮后的操作
	@Override
	public void commitDecision() {
		try {
			// if (rbApprove.isSelected()) {
			if (isActionHandler1) {
				System.out.println("=====提交=========" + edaComp);
				new Thread(new Runnable() {

					@Override
					public void run() {
						EDAPanel.saveValue((TCComponentItemRevision) edaComp);
					}
				}).start();
			}
			System.out.println("isActionHandler2 ==" + isActionHandler2);
			System.out.println("isClassEnable ==" + isClassEnable);
			if (isActionHandler2) {
					System.out.println("=====保存分类=======");
//					doSaveClass();
					sendItemOrItemRevisionToClassfication(edaComp);
			}
			super.commitDecision();
		} catch (Exception e) {
			MessageBox.post(e);
		}

	}

	// 保存分类信息 (弃用 2021/1/11)
//	private void doSaveClass() {
//		// TODO Auto-generated method stub
//		try {
//			CheckUtil.setByPass(true, Utils.getTCSession());
//			System.out.println("doSaveClass");
//			String category = edaComp.getProperty("fx8_Category");
//			System.out.println("category ==" + category);
////			 String partType = edaComp.getProperty("fx8_PartType");
//			TCComponent symbolCom = edaComp.getRelatedComponent("FX8_SymbolRel");
//			TCComponent dellSymbolCom = edaComp.getRelatedComponent("FX8_DellSymbolRel");
//			System.out.println("symbolCom ==" + symbolCom);
//			System.out.println("dellSymbolCom ==" + dellSymbolCom);
//			if ("".equals(category)) {				
//				MessageBox.post(AIFUtility.getActiveDesktop(),"保存分类信息失败，没有category或partType信息","ERROR",MessageBox.ERROR);
//				System.out.println("保存分类信息失败，没有category或partType信息");
//				return;
//			}
//
//			if (symbolCom == null && dellSymbolCom == null) {
//				MessageBox.post(AIFUtility.getActiveDesktop(),"保存分类信息失败，没有category或partType信息","ERROR",MessageBox.ERROR);
//				System.out.println("保存分类信息失败，没有Dell和HP信息");
//				return;
//			}
//
//			// 查找对应的分类节点信息
//			TCComponentICO[] icos = edaComp.getClassificationObjects();
//			System.out.println("icos.length ==" + icos.length);
//			if (icos != null && icos.length != 0) {
//				// 删除分类信息
//				for (int i = 0; i < icos.length; i++) {
//					icos[i].delete();
//				}
//			}
//			edaComp.refresh();
//			System.out.println("edaComp.getClassificationObjects() ==" + edaComp.getClassificationObjects().length);
//			ClassificationService clafService = new ClassificationService();
//			String partClassifyRootId = "ICM";
//			G4MUserAppContext g4mUserAppContext = new G4MUserAppContext(clafService, partClassifyRootId);
//			G4MTree g4mtree = new G4MTree(g4mUserAppContext);
//			G4MTreeNode hpTreeNode = null;
//			G4MTreeNode dellTreeNode = null;
//
//			G4MTreeNode hpTreeNodeP = null;
//			G4MTreeNode dellTreeNodeP = null;
//
//			G4MTreeNode hpTreeNodeC = null;
//			G4MTreeNode dellTreeNodeC = null;
//
//			G4MTreeNode rootNode = g4mtree.findNode("ICM");
//			g4mtree.setRootNode(rootNode, true);
//
//			int count = rootNode.getChildCount();
//			for (int i = 0; i < count; i++) {
//
//				G4MTreeNode treeNode = (G4MTreeNode) rootNode.getChildAt(i);
//				g4mtree.setRootNode(treeNode, true);
//				if ("ICM01".equals(treeNode.getNodeName())) {
//					int ccount = treeNode.getChildCount();
//					for (int j = 0; j < ccount; j++) {
//						G4MTreeNode node = (G4MTreeNode) treeNode.getChildAt(j);
//						String name = node.getICSDescriptor().getName();
//						System.out.println("分类节点名称 name==" + name);
//						if (name.toUpperCase().equals("DELL")) {
//							dellTreeNode = node;
//						} else if (name.toUpperCase().equals("HP")) {
//							hpTreeNode = node;
//						}
//					}
//				}
//			}
//			System.out.println("hpTreeNode == " + hpTreeNode);
//			System.out.println("dellTreeNode == " + dellTreeNode);
//
//			// 查找HP节点ID
//			if (hpTreeNode != null && symbolCom != null) {
//				g4mtree.setRootNode(hpTreeNode, true);
//				hpTreeNodeP = getParentNode(g4mtree, hpTreeNode, category);
//				hpTreeNodeC = hpTreeNodeP;
//				System.out.println("hpTreeNodeP == " + hpTreeNodeP);
//			}
//
//			// 查找DELL节点ID
//			if (dellTreeNode != null && dellSymbolCom != null) {
//				g4mtree.setRootNode(dellTreeNode, true);
//				dellTreeNodeP = getParentNode(g4mtree, dellTreeNode, category);
//				dellTreeNodeC = dellTreeNodeP;
//				System.out.println("dellTreeNodeP == " + dellTreeNodeP);
//			}
//
//			System.out.println("dellTreeNodeC == " + dellTreeNodeC);
//			System.out.println("hpTreeNodeC == " + hpTreeNodeC);
//
//			if (dellTreeNodeC == null && hpTreeNodeC == null) {
//				System.out.println("保存分类信息失败，查找不到dell节点和hp节点");
//				MessageBox.post(AIFUtility.getActiveDesktop(),"保存分类信息失败，查找不到dell节点和hp节点","ERROR",MessageBox.ERROR);
//				return;
//			}
//
//			// 遍历分类信息
//			Component[] components = panelRight.getComponents();
//			System.out.println("components ==" + components.length);
//			ClassPanel classsPanel = null;
//
//			boolean isReference = false;
//			int valueId = -1;
//			Component component = null;
//			String value = "";
//			HashMap<String, String> lovMap = null;
//			Date date = null;
//			ArrayList<ICSProperty> icss = new ArrayList<ICSProperty>();
//			// 将电子料item_id写入分类part_numbers
//			for (int i = 0; i < components.length; i++) {
//				if (components[i] instanceof ClassPanel) {
//					classsPanel = (ClassPanel) components[i];
//					component = classsPanel.getComponent();
//					valueId = classsPanel.getValueId();
//					isReference = classsPanel.isReference();
//					if (isReference) {
//						continue;
//					}
//					value = "";
//					if (component instanceof JTextField) {
//						System.out.println("valueId ==" + valueId);
//						value = ((JTextField) component).getText();
//					} else if (component instanceof JComboBox) {
//						lovMap = classsPanel.getLovMap();
//						if (lovMap != null) {
//							if (lovMap.containsKey(((JComboBox) component).getSelectedItem().toString())) {
//								value = lovMap.get(((JComboBox) component).getSelectedItem().toString());
//							}
//						}
//
//					} else if (component instanceof DateButton) {
//						date = ((DateButton) component).getDate();
//						if (date != null) {
//							value = ((DateButton) component).getDateFormat().format(date);
//						}
//					} else {
//						value = "";
//						continue;
//					}
//					System.out.println("valueId ==" + valueId);
//					System.out.println("value ==" + value);
//					if (!"".equals(value) && !value.equals("请从Symbol中获取")) {
//						ICSProperty icsProperty = new ICSProperty(valueId, value);
//						icss.add(icsProperty);
//					}
//				}
//			}
//
//			TCClassificationService ics = this.session.getClassificationService();
//			ICSApplicationObject icsap = ics.newICSApplicationObject("ICM");
//			String wso_ics_id = ics.getTCComponentId(edaComp);
//			String wso_uid = edaComp.getUid();
//			System.out.println("wso_ics_id ==" + wso_ics_id);
//			System.out.println("wso_uid ==" + wso_uid);
//			icsap.clear();
//			icsap.create(wso_ics_id, wso_uid);
//			icsap.edit();
//			if (dellTreeNodeC != null) {
//				String classId = dellTreeNodeC.getICSDescriptor().getId();
//				System.out.println("DELL icsId == " + classId);
//				icsap.setView("Base", classId);
//				if (icss.size() != 0) {
//					icsap.setProperties(icss.toArray(new ICSProperty[icss.size()]));
//					icsap.save();
//				}
//			}
//
//			if (hpTreeNodeC != null) {
//				icsap.create(wso_ics_id, wso_uid);
//				icsap.edit();
//				String classId = hpTreeNodeC.getICSDescriptor().getId();
//				System.out.println("HP icsId == " + classId);
//				icsap.setView("Base", classId);
//				if (icss.size() != 0) {
//					icsap.setProperties(icss.toArray(new ICSProperty[icss.size()]));
//					icsap.save();
//				}
//			}
//			CheckUtil.setByPass(false, session);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 设置HW Property区域可编辑
	 */
	private void setEnableHWProperty() {
		EDAPanel.categoryLov.setEnabled(true);
		EDAPanel.partTypeLov.setEnabled(true);
		EDAPanel.MfgPNField.setEnabled(true);
		EDAPanel.projectLov.setEnabled(true);
		EDAPanel.updateTime.setEnabled(true);
		EDAPanel.MfgField.setEnabled(true);
		EDAPanel.MgfButton.setEnabled(true);
		EDAPanel.dataSheetField.setEnabled(true);
		EDAPanel.dataSheetButton.setEnabled(true);
		EDAPanel.dellField.setEnabled(true);
		EDAPanel.dellChooseButton.setEnabled(true);
		EDAPanel.dellSelectButton.setEnabled(true);
		EDAPanel.symbolField.setEnabled(true);
		EDAPanel.symbolChooseButton.setEnabled(true);
		EDAPanel.symbolSelectButton.setEnabled(true);
	}
	/**
	 * 保存分类信息
	 * @param comp 电子料版本对象
	 * @throws Exception
	 */
	public static void sendItemOrItemRevisionToClassfication(TCComponent comp) throws Exception 
	{
		String category = edaComp.getProperty("fx8_Category");
		System.out.println("category ==" + category);
		// String partType = edaComp.getProperty("fx8_PartType");
		TCComponent symbolCom = edaComp.getRelatedComponent("FX8_SymbolRel");
		TCComponent dellSymbolCom = edaComp.getRelatedComponent("FX8_DellSymbolRel");
		System.out.println("symbolCom ==" + symbolCom);
		System.out.println("dellSymbolCom ==" + dellSymbolCom);
		if ("".equals(category)) {
			MessageBox.post(AIFUtility.getActiveDesktop(), "保存分类信息失败，没有category或partType信息", "ERROR", MessageBox.ERROR);
			System.out.println("保存分类信息失败，没有category或partType信息");
			return;
		}

		if (symbolCom == null && dellSymbolCom == null) {
			MessageBox.post(AIFUtility.getActiveDesktop(), "保存分类信息失败，没有Symbol文件或者DellSymbol文件", "ERROR", MessageBox.ERROR);
			System.out.println("保存分类信息失败，没有Dell和HP信息");
			return;
		}
		CheckUtil.setByPass(true, Utils.getTCSession());
		TCComponentICO[] arrayICO = comp.getClassificationObjects();
		System.out.println("arrayICO ==" + arrayICO.length);
		for (int i = 0; i < arrayICO.length; i++) {
			arrayICO[i].delete();
		}

		// 遍历分类信息
		Component[] components = panelRight.getComponents();
		System.out.println("components ==" + components.length);
		ClassPanel classsPanel = null;

		boolean isReference = false;
		int valueId = -1;
		Component component = null;
		String value = "";
		HashMap<String, String> lovMap = null;
		Date date = null;
		ArrayList<ICSProperty> icss = new ArrayList<ICSProperty>();
		// 将电子料item_id写入分类part_numbers
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof ClassPanel) {
				classsPanel = (ClassPanel) components[i];
				component = classsPanel.getComponent();
				valueId = classsPanel.getValueId();
				isReference = classsPanel.isReference();
				if (isReference) {
					continue;
				}
				value = "";
				if (component instanceof JTextField) {
					System.out.println("valueId ==" + valueId);
					value = ((JTextField) component).getText();
				} else if (component instanceof JComboBox) {
					lovMap = classsPanel.getLovMap();
					if (lovMap != null) {
						if (lovMap.containsKey(((JComboBox) component).getSelectedItem().toString())) {
							value = lovMap.get(((JComboBox) component).getSelectedItem().toString());
						}
					}

				} else if (component instanceof DateButton) {
					date = ((DateButton) component).getDate();
					if (date != null) {
						value = ((DateButton) component).getDateFormat().format(date);
					}
				} else {
					value = "";
					continue;
				}
				System.out.println("valueId ==" + valueId);
				System.out.println("value ==" + value);
				if (!"".equals(value) && !value.equals("请从Symbol中获取")) {
					ICSProperty icsProperty = new ICSProperty(valueId, value);
					icss.add(icsProperty);
				}
			}
		}

		GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
		if(symbolCom != null){
			HashMap<String, String> map = getPreferenceUtil.getHashMapPreference(Utils.getTCSession(), TCPreferenceService.TC_preference_site, "FX_HPClassify","=");
			System.out.println("map ==" + map.size());
			String strClassId = map.get(category);
			System.out.println("HP strClassId ==" + strClassId);
			G4MUserAppContext G4 = new G4MUserAppContext(AIFUtility.getCurrentApplication(), strClassId);
			System.out.println("G4 ==" + G4);
			ICSApplicationObject icsAppObj = G4.getICSApplicationObject();
			TCClassificationService tccs = G4.getClassificationService();
			ICSAdminClass icsAdminClass = tccs.newICSAdminClass();
			icsAdminClass.load(strClassId);
			icsAppObj.setView(strClassId);
			icsAppObj.create(comp.getTCProperty("item_id").toString(), comp.getUid());
			icsAppObj.setProperties(icss.toArray(new ICSProperty[icss.size()]));
			icsAppObj.save();
		}
		if(dellSymbolCom != null){
			HashMap<String, String> map = getPreferenceUtil.getHashMapPreference(Utils.getTCSession(), TCPreferenceService.TC_preference_site, "FX_DELLClassify","=");
			System.out.println("map ==" + map.size());
			String strClassId = map.get(category);
			System.out.println("DELL strClassId ==" + strClassId);
			G4MUserAppContext G4 = new G4MUserAppContext(AIFUtility.getCurrentApplication(), strClassId);
			System.out.println("G4 ==" + G4);
			ICSApplicationObject icsAppObj = G4.getICSApplicationObject();
			TCClassificationService tccs = G4.getClassificationService();
			ICSAdminClass icsAdminClass = tccs.newICSAdminClass();
			icsAdminClass.load(strClassId);
			icsAppObj.setView(strClassId);
			icsAppObj.create(comp.getTCProperty("item_id").toString(), comp.getUid());
			icsAppObj.setProperties(icss.toArray(new ICSProperty[icss.size()]));
			icsAppObj.save();
		}
		CheckUtil.setByPass(false, Utils.getTCSession());
		MessageBox.post(AIFUtility.getActiveDesktop(),"保存分类成功","Info",MessageBox.INFORMATION);
	}

	/**
	 * 设置CE Property区域可编辑
	 */
	private void setEnableCEProperty() {
		EDAPanel.HHPNField.setEnabled(true);

	}

	/**
	 * 设置Layout Property区域可编辑
	 */
	private void setEnableLayProperty() {
		EDAPanel.footprintLov.setEnabled(true);
		EDAPanel.PADField.setEnabled(true);
		EDAPanel.PADChooseButton.setEnabled(true);
		EDAPanel.footprintField.setEnabled(true);
		EDAPanel.footprintChooseButton.setEnabled(true);
		EDAPanel.footprintSelectButton.setEnabled(true);
	}

	/**
	 * 设置所有区域不可编辑
	 */
	private void setEnableFalseAll() {

		EDAPanel.categoryLov.setEnabled(false);
		EDAPanel.categoryLov.setEnabled(false);
		EDAPanel.partTypeLov.setEnabled(false);
		EDAPanel.MfgPNField.setEnabled(false);
		EDAPanel.projectLov.setEnabled(false);
		EDAPanel.updateTime.setEnabled(false);
		EDAPanel.MfgField.setEnabled(false);
		EDAPanel.MgfButton.setEnabled(false);
		EDAPanel.dataSheetField.setEnabled(false);
		EDAPanel.dataSheetButton.setEnabled(false);
		EDAPanel.HHPNField.setEnabled(false);
		EDAPanel.dellField.setEnabled(false);
		EDAPanel.dellChooseButton.setEnabled(false);
		EDAPanel.dellSelectButton.setEnabled(false);
		EDAPanel.symbolField.setEnabled(false);
		EDAPanel.symbolChooseButton.setEnabled(false);
		EDAPanel.symbolSelectButton.setEnabled(false);
		EDAPanel.footprintLov.setEnabled(false);
		EDAPanel.PADField.setEnabled(false);
		EDAPanel.PADChooseButton.setEnabled(false);
		EDAPanel.footprintField.setEnabled(false);
		EDAPanel.footprintChooseButton.setEnabled(false);
		EDAPanel.footprintSelectButton.setEnabled(false);

	}

}
