package com.teamcenter.rac.workflow.commands.newperformsignoff;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.hh.fx.rewrite.GetPreferenceUtil;
import com.hh.fx.rewrite.util.CheckUtil;
import com.hh.fx.rewrite.util.Utils;
import com.hh.tools.customerPanel.EDACompRendering;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.classification.common.G4MUserAppContext;
import com.teamcenter.rac.classification.common.tree.G4MTree;
import com.teamcenter.rac.classification.common.tree.G4MTreeNode;
import com.teamcenter.rac.classification.icm.ClassificationService;
import com.teamcenter.rac.kernel.TCClassificationService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentICO;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentSignoff;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.ics.ICSAdminClass;
import com.teamcenter.rac.kernel.ics.ICSApplicationObject;
import com.teamcenter.rac.kernel.ics.ICSFormat;
import com.teamcenter.rac.kernel.ics.ICSKeyLov;
import com.teamcenter.rac.kernel.ics.ICSProperty;
import com.teamcenter.rac.kernel.ics.ICSPropertyDescription;
import com.teamcenter.rac.kernel.ics.ICSView;
import com.teamcenter.rac.util.DateButton;
import com.teamcenter.rac.util.MessageBox;

public class EDACompDecisionDialog extends DecisionDialog {
	private static final long serialVersionUID = 1L;

	private boolean isClassEnable = true;
	private TCComponent edaComp;
	private TCComponentItemRevision itemRev;
	private EDACompRendering EDAPanel = null;
	private String tabName = null;
	private ArrayList<JPanel> classPanelList = new ArrayList<>();
	private JPanel upPanel;
	private JPanel icsPanel = new JPanel();
	JPanel classPropertyPanel = new JPanel();

	public EDACompDecisionDialog(AIFDesktop paramAIFDesktop, TCComponentTask paramTCComponentTask,
			TCComponentSignoff paramTCComponentSignoff) {
		super(paramAIFDesktop, paramTCComponentTask, paramTCComponentSignoff);
		initEdaComp();
	}

	public EDACompDecisionDialog(Dialog paramDialog, TCComponentTask paramTCComponentTask,
			TCComponentSignoff paramTCComponentSignoff) {
		super(paramDialog, paramTCComponentTask, paramTCComponentSignoff);
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
//					System.out.println("版本类型 ==" + itemRev);
					break;
				}
			}
			String taskName = psTask.getName();
			GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
			Map<String, String> taskInfo = getPreferenceUtil.getHashMapPreference(session,
					TCPreferenceService.TC_preference_site, "FX_EDA_TASK_CONFIG", "=");

			System.out.println("name==========" + taskName);

			tabName = taskInfo.get(taskName);
			System.out.println("==========" + tabName);

			super.initializeDialog();
			if (tabName != null) {
				EDAPanel = new EDACompRendering(edaComp);
				EDAPanel.setTab(tabName);
				getContentPane().removeAll();
				getContentPane().setFocusable(false);

				upPanel = new JPanel();
				upPanel.setLayout(new BoxLayout(upPanel, BoxLayout.Y_AXIS));
				upPanel.add(EDAPanel, BorderLayout.CENTER);

				// 分类库
//				if ("ICS".equals(tabName)) {
//					createICSPanel();
//				}

				JPanel mainPanel = new JPanel(new BorderLayout());
				mainPanel.setLayout(new BorderLayout());
				mainPanel.add(upPanel, BorderLayout.CENTER);
				mainPanel.add(masterPanel, BorderLayout.SOUTH);
				getContentPane().add(mainPanel, BorderLayout.CENTER);
				centerToScreen();
				pack();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 点击确定按钮后的操作
	@Override
	public void commitDecision() {
		try {
			if (tabName != null) {
				EDAPanel.save(tabName);
			}
			super.commitDecision();
		} catch (Exception e) {
			MessageBox.post(e);
		}
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
			classPanelList.add(classPanel);
			return classPanel;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void createICSPanel() {
		JScrollPane jScrollPane = new JScrollPane();

		icsPanel.setBorder(
				new TitledBorder(null, "Library Property", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		icsPanel.setPreferredSize(new Dimension(900, 350));

		try {
			String category = edaComp.getProperty("fx8_Category");
			System.out.println("category == " + category);

			TCComponentICO[] icos = itemRev.getClassificationObjects();
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
						classPropertyPanel.add(getICSPropDescInfo(icsPropertyDescription, value, valueId, className));
					}
				} else {
					addClasspropetyPanel(icsAppObj, category);
				}

			} else {
				addClasspropetyPanel(icsAppObj, category);
			}
			for (int i = 0; i < classPanelList.size(); i++) {
				if (i % 2 == 0) {
					icsPanel.add((10 + i) + ".1.left.left.preferred.preferred", classPanelList.get(i));
				} else {
					icsPanel.add((10 + i) + ".2.right.right.preferred.preferred", classPanelList.get(i));
				}
			}
			// 添加 HF Status栏位
			jScrollPane.setViewportView(icsPanel);
			upPanel.add(jScrollPane, BorderLayout.CENTER);

			JPanel southPanel = new JPanel();
			southPanel.setPreferredSize(new Dimension(900, 30));
			southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
			upPanel.add(southPanel, BorderLayout.CENTER);
			JButton saveButton = new JButton("SAVE CLASSIFY");
			saveButton.setEnabled(isClassEnable);
			saveButton.setPreferredSize(new Dimension(120, 27));
			saveButton.setActionCommand("SAVE CLASSIFY");
			southPanel.add(saveButton);
			saveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// 保存分类
					try {
						sendItemOrItemRevisionToClassfication(edaComp);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
				G4MTreeNode parentNode = getParentNode(g4mtree, rootNode, parentNodeName);
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
	 * 保存分类信息
	 * 
	 * @param comp 电子料版本对象
	 * @throws Exception
	 */
	public void sendItemOrItemRevisionToClassfication(TCComponent comp) throws Exception {
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
			MessageBox.post(AIFUtility.getActiveDesktop(), "保存分类信息失败，没有Symbol文件或者DellSymbol文件", "ERROR",
					MessageBox.ERROR);
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
		Component[] components = icsPanel.getComponents();
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
		if (symbolCom != null) {
			HashMap<String, String> map = getPreferenceUtil.getHashMapPreference(Utils.getTCSession(),
					TCPreferenceService.TC_preference_site, "FX_HPClassify", "=");
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
		if (dellSymbolCom != null) {
			HashMap<String, String> map = getPreferenceUtil.getHashMapPreference(Utils.getTCSession(),
					TCPreferenceService.TC_preference_site, "FX_DELLClassify", "=");
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
		MessageBox.post(AIFUtility.getActiveDesktop(), "保存分类成功", "Info", MessageBox.INFORMATION);
	}

}
