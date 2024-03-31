package com.teamcenter.rac.workflow.commands.newperformsignoff;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.hh.fx.rewrite.jtree.MyTreeCellRenderer;
import com.hh.fx.rewrite.jtree.TreeNodeData;
import com.hh.fx.rewrite.jtree.TreeNodeOperation;
import com.hh.fx.rewrite.util.CheckUtil;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.classification.common.G4MUserAppContext;
import com.teamcenter.rac.classification.common.tree.G4MTree;
import com.teamcenter.rac.classification.common.tree.G4MTreeNode;
import com.teamcenter.rac.classification.icm.ClassificationService;
import com.teamcenter.rac.kernel.TCClassificationService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentICO;
import com.teamcenter.rac.kernel.TCComponentSignoff;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.ics.ICSApplicationObject;
import com.teamcenter.rac.kernel.ics.ICSFormat;
import com.teamcenter.rac.kernel.ics.ICSKeyLov;
import com.teamcenter.rac.kernel.ics.ICSProperty;
import com.teamcenter.rac.kernel.ics.ICSPropertyDescription;
import com.teamcenter.rac.kernel.ics.ICSView;
import com.teamcenter.rac.util.DateButton;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.VerticalLayout;

public class FX8ManagerItemCheckDecisionDialog extends DecisionDialog implements TreeSelectionListener{

	private TCComponent managerItemComponent;
	private G4MUserAppContext g4mUserAppContext = null;
	private ICSApplicationObject icsAppObj = null;
	// 存在的分类
	private TCComponentICO existComponentICO = null;

	private G4MTree treePane;
	private JPanel classPropertyPanel = new JPanel(new VerticalLayout());

	// root节点数据
	private JTree tree = null;
	private TreeNodeData selectedTreeNode = null;
	private DefaultMutableTreeNode rootTreeNodeData = null;

	public FX8ManagerItemCheckDecisionDialog(AIFDesktop paramAIFDesktop, TCComponentTask paramTCComponentTask,
			TCComponentSignoff paramTCComponentSignoff) {
		super(paramAIFDesktop, paramTCComponentTask, paramTCComponentSignoff);
		System.out.println("FX8ManagerItemCheckDecisionDialog");
	}

	public FX8ManagerItemCheckDecisionDialog(Dialog paramDialog, TCComponentTask paramTCComponentTask,
			TCComponentSignoff paramTCComponentSignoff) {
		super(paramDialog, paramTCComponentTask, paramTCComponentSignoff);
		System.out.println("FX8ManagerItemCheckDecisionDialog");
	}

	@Override
	public void initializeDialog() {
		System.out.println("FX8ManagerItemCheckDecisionDialog Override initializeDialog");
		super.initializeDialog();
		getContentPane().removeAll();
		getContentPane().setFocusable(false);
		initUI();
	}

	private void initUI() {
		initData();
		JPanel mainPanel = new JPanel(new BorderLayout());

		// 添加左边的树
		JPanel leftTreePanel = new JPanel();
		tree = new JTree(this.rootTreeNodeData);
		tree.addTreeSelectionListener(this);
		tree.setCellRenderer(new MyTreeCellRenderer());
		JScrollPane treeScrollPane = new JScrollPane(tree);
		leftTreePanel.add(treeScrollPane);

		// 添加右边的内容
		JScrollPane rightPropScrollPane = new JScrollPane(this.classPropertyPanel);

		mainPanel.add(leftTreePanel, BorderLayout.WEST);
		mainPanel.add(rightPropScrollPane, BorderLayout.CENTER);
		// 添加审核的布局
		mainPanel.add(super.masterPanel, BorderLayout.SOUTH);
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		centerToScreen();
		
		// 加载分类
		try {
			loadConmentByClassType();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 选中节点事件
	 */
	@Override
	public void valueChanged(TreeSelectionEvent event) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (node == null) {
			return;
		} 
		
		Object object = node.getUserObject();
		if (object instanceof TreeNodeData) {
			TreeNodeData treeNodeData = (TreeNodeData) object;
			if (!treeNodeData.isParentFlag()) {
				
				String classId = treeNodeData.getClassId();
				String existTypeClassId = null;
				try {
					icsAppObj.setView(classId);

					if (null != existComponentICO) {
						existTypeClassId = existComponentICO.getProperty("object_type_id");
					}

					selectedTreeNode = treeNodeData;
					ICSView icsView = icsAppObj.getView();
					classPropertyPanel.removeAll();

					// 判断是否选中的是 存在的classId
					if (null != existTypeClassId && classId.equals(existTypeClassId)) {
						ICSPropertyDescription icsPropertyDescription = null;

						ICSProperty[] icsProperties = existComponentICO.getICSProperties(true);
						for (int i = 0; i < icsProperties.length; i++) {
							int valueId = icsProperties[i].getId();
							String value = icsProperties[i].getValue();
							icsPropertyDescription = icsView.getPropertyDescription(valueId);
							String className = icsPropertyDescription.getName();

							classPropertyPanel.add("Attachmennt.Packing.HorizontalAlignment.VerticalAlignment",
									getICSPropDescInfo(icsPropertyDescription, value, valueId, className));
						}
					} else {
						ICSPropertyDescription[] icsPropertyDescriptions = icsView.getPropertyDescriptions();
						ICSPropertyDescription temPropertyDescription = null;
						for (int j = 0; j < icsPropertyDescriptions.length; j++) {
							temPropertyDescription = icsPropertyDescriptions[j];
							String className = temPropertyDescription.getName();
							int valueId = temPropertyDescription.getId();

							classPropertyPanel.add("Attachmennt.Packing.HorizontalAlignment.VerticalAlignment",
									getICSPropDescInfo(temPropertyDescription, "", valueId, className));

						}
					}

					classPropertyPanel.updateUI();

				} catch (TCException e) {
					e.printStackTrace();
				}
				
			}
		} 
	}
	
	private void initData() {
		System.out.println("FX8ManagerItemDecisionDialog initData");
		// 获取分类列表
		try {
			TCComponent[] coms = psTask.getRelatedComponents("root_target_attachments");
			for (int i = 0; i < coms.length; i++) {
				if (coms[i].isTypeOf("FX8_ManagerItemRevision")) {
					managerItemComponent = coms[i];
					// 获取session
					this.session = managerItemComponent.getSession();
					TCClassificationService icsServer = this.session.getClassificationService();
					icsAppObj = icsServer.newICSApplicationObject("ICM");
					break;
				}
			}

			// 获取G4MTree的数据
			ClassificationService clafService = new ClassificationService();
			String partClassifyRootId = "ICM";
			g4mUserAppContext = new G4MUserAppContext(clafService, partClassifyRootId);
			treePane = new G4MTree(g4mUserAppContext);

			// 根节点
			G4MTreeNode rootNode = treePane.findNode("ICM");
			treePane.setRootNode(rootNode, true);
			rootTreeNodeData = new DefaultMutableTreeNode(geTreeNodeData(rootNode));
			getNodeDataByG4MTree(treePane, rootNode, rootTreeNodeData);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 根据当前对象中的分类 默认选中tree 加载右边的组件
	private void loadConmentByClassType() throws Exception {
		TCComponentICO[] icos = managerItemComponent.getClassificationObjects();
		if (null != icos && icos.length > 0) {
			System.out.println("icos.length = " + icos.length);
			existComponentICO = icos[0];

			String classId = existComponentICO.getProperty("object_type_id");
			System.out.println("classId == " + classId);

			// 选中节点
			TreeNodeOperation treeNodeOperration = new TreeNodeOperation(this.tree, this.rootTreeNodeData);
			treeNodeOperration.selectedNode(classId);
		}
	}

	// 根据属性对象 生成表单panel
	private ClassPanel getICSPropDescInfo(ICSPropertyDescription propDesc, String value, int valueId,
			String className) {

		ICSKeyLov keyLov = null; // LOV
		ICSFormat icsFormat = null;

		try {
			boolean isNumeric = propDesc.isNumeric(); // 是否是數字
			System.out.println("isNumeric == " + isNumeric);
			keyLov = propDesc.getKeyLov(); // LOV
			icsFormat = propDesc.getFormat();
			boolean isReference = propDesc.isReference();
			String refOptions = propDesc.getRefOptions();

			// 控件相关
			ClassPanel classPanel = new ClassPanel(new FlowLayout(FlowLayout.LEFT));
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

			} else if (icsFormat.getType() == ICSFormat.LIST) { // 下拉选项

				String[] keys = keyLov.getKeys();
				String[] values = keyLov.getValues();
				String[] keyValues = keyLov.getKeyValues();
				String[] displayValues = keyLov.getDisplayValues();
				JComboBox comboBox = new JComboBox();
				HashMap<String, String> lovMap = new HashMap<String, String>();
				for (int i = 0; i < keys.length; i++) {
					lovMap.put(values[i], keys[i]);
					comboBox.addItem(values[i]);
				}

				comboBox.setPreferredSize(new Dimension(200, 25));
				classPanel.add(comboBox);
				comboBox.setEditable(!isReference);
				comboBox.setSelectedItem(value);
				classPanel.setComponent(comboBox);
				classPanel.setLovMap(lovMap);

			} else { // 字符串
				JTextField field = new JTextField();
				field.setPreferredSize(new Dimension(200, 25));
				classPanel.add(field);
				field.setEditable(!isReference);

				if ("".equals(refOptions)) {
					field.setText(value);
				} else {
					field.setText(managerItemComponent.getProperty(refOptions));
				}

				classPanel.setComponent(field);
			}

			classPanel.setICSType(icsFormat.getType());
			classPanel.setReference(isReference);
			classPanel.setValueId(valueId);
			return classPanel;
		} catch (Exception e) {
			e.printStackTrace();

		}

		return null;
	}

	// 保存分类
	private void saveClassType() {
		if (null != selectedTreeNode) {
			System.out.println(
					"选中的节点-->" + selectedTreeNode.getNodeName() + "-->" + selectedTreeNode.getClassId());
			// 遍历分类信息
			Component[] components = classPropertyPanel.getComponents();
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

					if (component instanceof JTextField) {
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
						continue;
					}

					if (!"".equals(value)) {
						ICSProperty icsProperty = new ICSProperty(valueId, value);
						icss.add(icsProperty);
					}
				}
			}

			try {

				// 查找对应的分类节点信息
				TCComponentICO[] icos = managerItemComponent.getClassificationObjects();
				if (icos != null && icos.length > 0) {
					// 删除分类信息
					for (int i = 0; i < icos.length; i++) {
						icos[i].delete();
					}
				}
				managerItemComponent.refresh();

				TCClassificationService ics = this.session.getClassificationService();
				ICSApplicationObject icsap = ics.newICSApplicationObject("ICM");
				String wso_ics_id = ics.getTCComponentId(managerItemComponent);
				String wso_uid = managerItemComponent.getUid();
				System.out.println("wso_ics_id ==" + wso_ics_id);
				System.out.println("wso_uid ==" + wso_uid);
				icsap.create(wso_ics_id, wso_uid);

				CheckUtil.setByPass(true, session);
				String classId = selectedTreeNode.getClassId();
				icsap.setView("Base", classId);
				if (icss.size() != 0) {
					icsap.setProperties(icss.toArray(new ICSProperty[icss.size()]));
				}
				icsap.save();
				CheckUtil.setByPass(false, session);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
	}

	// 点击确定按钮后的操作
	@Override
	public void commitDecision() {
		System.out.println("FX8ManagerItemCheckDecisionDialog commitDecision");
		try {
			super.commitDecision();
			saveClassType();

		} catch (Exception e) {
			MessageBox.post(e);
		}

	}

	// 组装节点数据
	private TreeNodeData geTreeNodeData(G4MTreeNode g4mTreeNode) {
		String rootClassId = g4mTreeNode.getICSDescriptor().getId();
		TreeNodeData treeNodeData = new TreeNodeData(rootClassId, g4mTreeNode.getNodeLabel(),
				g4mTreeNode.getNodeType());
		if (g4mTreeNode.getChildCount() > 0) {
			treeNodeData.setParentFlag(true);
			treeNodeData.setChildNodeCount(g4mTreeNode.getChildCount());
		}
		return treeNodeData;
	}

	// 把G4MTree数据 转换为 JTree数据
	private void getNodeDataByG4MTree(G4MTree gmTree, G4MTreeNode parentTreeNode, DefaultMutableTreeNode treeNodeList) {
		G4MTreeNode treeNode = null;
		DefaultMutableTreeNode treeNodeData = null;

		for (int i = 0; i < parentTreeNode.getChildCount(); i++) {
			treeNode = (G4MTreeNode) parentTreeNode.getChildAt(i);
			gmTree.setRootNode(treeNode, true);

			treeNodeData = new DefaultMutableTreeNode(geTreeNodeData(treeNode));
			treeNodeList.add(treeNodeData);

			if (treeNode.getChildCount() > 0) {
				getNodeDataByG4MTree(gmTree, treeNode, treeNodeData);
			}
		}

	}


}
