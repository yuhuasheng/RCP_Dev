package com.hh.tools.newitem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.classification.common.G4MUserAppContext;
import com.teamcenter.rac.classification.common.tree.G4MTree;
import com.teamcenter.rac.classification.common.tree.G4MTreeNode;
import com.teamcenter.rac.classification.icm.ClassificationService;
import com.teamcenter.rac.kernel.TCClassificationService;
import com.teamcenter.rac.kernel.ics.ICSHierarchyNodeDescriptor;

public class CusClafSplitDialog extends AbstractAIFDialog {
    private JSplitPane clafSplitPane = null;
    private G4MUserAppContext g4mUserAppContext;
    private CusDataPane clafAttribute;
    private G4MTree clafTree;

    //	private JPanel contentPanel = new JPanel();
    public CusClafSplitDialog() {
        super(true);
        initUI();
    }

    private void initUI() {
    	this.setTitle("分类搜索对话框");
        Container dlgPanel = getContentPane();
        setBounds(100, 100, 680, 580);
        dlgPanel.setLayout(new BorderLayout());
        dlgPanel.setBackground(Color.white);


        clafSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        clafSplitPane.setDividerLocation(0.5D);
        try {
            ClassificationService clafService = new ClassificationService();

            String partClassifyRootId = "ICM";

            // 分类属性面板定义
            g4mUserAppContext = new G4MUserAppContext(clafService,
                    partClassifyRootId);

            // 获取分类的应用
            clafTree = new G4MTree(g4mUserAppContext);
            clafAttribute = new CusDataPane(g4mUserAppContext);

            // 获取零部件分类根节点
            TCClassificationService tcclassificationservice1 = g4mUserAppContext
                    .getClassificationService();
            ICSHierarchyNodeDescriptor icshierarchynodedescriptor1 = tcclassificationservice1
                    .describeNode(partClassifyRootId, 0);
//			if (icshierarchynodedescriptor1 == null) {
//				MessageBox.post(
//						(new StringBuilder())
//								.append(reg.getString("noClassFound1",
//										"!!!There was no class with ID"))
//								.append(" \"")
//								.append(partClassifyRootId)
//								.append("\" ")
//								.append(reg.getString("noClassFound2",
//										"!!!found!")).toString(), reg
//								.getString("noClassFound.TITLE",
//										"!!!No class found"), 2);
//				return;
//			}

            // 设置零部件分类的根节点
            G4MTreeNode g4mtreenode1 = new G4MTreeNode(clafTree,
                    icshierarchynodedescriptor1);
            clafTree.setRootNode(g4mtreenode1, true);
            dlgPanel.add(clafSplitPane);
            clafSplitPane.setLeftComponent(clafTree);
            clafSplitPane.setRightComponent(clafAttribute);
            setVisible(true);
            this.setLocationRelativeTo(null);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
