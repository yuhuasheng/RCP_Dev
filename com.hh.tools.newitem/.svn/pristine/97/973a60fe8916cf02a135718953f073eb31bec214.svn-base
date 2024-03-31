package com.hh.tools.renderingHint;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.classification.common.G4MUserAppContext;
import com.teamcenter.rac.classification.common.tree.G4MTree;
import com.teamcenter.rac.classification.common.tree.G4MTreeNode;
import com.teamcenter.rac.classification.icm.ClassificationService;
import com.teamcenter.rac.common.controls.LOVComboBox;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCPropertyDescriptor;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.viewer.stylesheet.beans.AbstractPropertyBean;

public class EDACompCategoryPropertyBean extends AbstractPropertyBean<Object> {

    protected TCProperty tcProperty;
    protected TCPropertyDescriptor tcPropertyDescriptor;
    protected FormToolkit toolkit;
    protected Composite composite;
    protected Composite parentComposite;
    private TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();

    @SuppressWarnings("deprecation")
    static LOVComboBox categoryPropLov = null;
    Text categoryTextField = null;
    boolean isModify = false;

    // 是否第一次加载
    boolean firstLoadDataFlag = false;

    public EDACompCategoryPropertyBean(Control paramControl) {
        super(paramControl);
        this.parentComposite = (Composite) paramControl;
        loadPropertyPanel();
    }

    public EDACompCategoryPropertyBean(FormToolkit paramFormToolkit, Composite paramComposite, boolean paramBoolean,
                                       Map<?, ?> paramMap) {
        this.savable = true;
        this.toolkit = paramFormToolkit;
        this.parentComposite = paramComposite;
        loadPropertyPanel();
    }

    /**
	 * 加载属性面板
	 */
    @SuppressWarnings("deprecation")
    private void loadPropertyPanel() {
        this.composite = new Composite(this.parentComposite, SWT.NONE);

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.composite.setLayout(gridLayout);

        categoryPropLov = new LOVComboBox(composite, SWT.DROP_DOWN | SWT.BORDER);
        categoryPropLov.setSize(168, 25);
        categoryTextField = categoryPropLov.getTextField();
        // 加载下拉数据
        loadCategoryList();
        setControl(composite);

        categoryTextField.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent paramModifyEvent) {
                String selectedText = categoryTextField.getText();
                isModify = true;
                mandatory = true;
                setDirty(true);

                // 不是第一次加载 则清除级联数据
                if (!firstLoadDataFlag) {
                    // 清除数据
                    EDACompPartTypePropertyBean.clearPropLov();

                    if (null != selectedText && !"".equals(selectedText)) {
                        // 加载数据
                        EDACompPartTypePropertyBean.loadPartTypeList(selectedText.toUpperCase());
                        if ("Special".toUpperCase().equals((selectedText.toUpperCase()))) {
                            EDACompItemTypePropertyBean.setValue("Special");
                        } else {
                            EDACompItemTypePropertyBean.setValue("Standard");
                        }
                    }
                }

                // 当加载完成后的操作
                firstLoadDataFlag = false;
            }
        });
    }

    /**
	 * 加载下拉列表
	 * 
	 * @return
	 */
    private void loadCategoryList() {
        // 查询category
        try {
            String group = this.session.getGroup().toString();
            int index = 0;
            if (group.contains("Monitor")) {
                index = 1;
            } else if (group.contains("Printer")) {
                index = 2;
            } else if (group.contains("Dell")) {
                index = 3;
            } else if (group.contains("HP")) {
                index = 0;
            }

            ClassificationService clafService = new ClassificationService();
            String partClassifyRootId = "ICM";
            G4MUserAppContext g4mUserAppContext = new G4MUserAppContext(clafService, partClassifyRootId);
            G4MTree g4mtree = new G4MTree(g4mUserAppContext);
            g4mtree.setShowPopupMenu(false);
            G4MTreeNode rootNode = g4mtree.findNode("ICM");
            g4mtree.setRootNode(rootNode, true);

            int count = rootNode.getChildCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    G4MTreeNode treeNode = (G4MTreeNode) rootNode.getChildAt(i);
                    if ("ICM01".equals(treeNode.getNodeName())) {
                        g4mtree.setRootNode(treeNode, true);
                        G4MTreeNode hpTreeNode = (G4MTreeNode) treeNode.getChildAt(index);
                        g4mtree.setRootNode(hpTreeNode, true);
                        int categroyCount = hpTreeNode.getChildCount();
                        if (categroyCount > 0) {
                            for (int j = 0; j < categroyCount; j++) {
                                G4MTreeNode childNode = (G4MTreeNode) hpTreeNode.getChildAt(j);
                                String categroyStr = childNode.getICSDescriptor().getName();
                                categoryPropLov.addItem(categroyStr);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getValue() {
        if (null != categoryPropLov) {
//			System.out.println("categoryPropLov => " + categoryPropLov.getSelectedItem());
            return categoryPropLov.getSelectedItem().toString();
        }
        return null;
    }

    @Override
    public boolean isPropertyModified(TCProperty tcproperty) throws Exception {
        return isModify;
    }

    @Override
    public void setModifiable(boolean flag) {
        modifiable = flag;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object getEditableValue() {
        if (null != categoryPropLov) {
            return categoryPropLov.getSelectedObject();
        }
        return "";
    }

    @Override
    public void load(TCProperty tcproperty) throws Exception {
        if (null != categoryPropLov) {
            firstLoadDataFlag = true;
            String propValue = tcproperty.getStringValue();
            categoryPropLov.setText(propValue);
        }
        isModify = false;
        setDirty(false);
    }

    @Override
    public void load(TCPropertyDescriptor tcpropertydescriptor) throws Exception {
        setDirty(false);
        String defaultValue = tcpropertydescriptor.getDefaultValue();
        if (defaultValue != null && defaultValue.length() > 0 && null != categoryPropLov) {
            firstLoadDataFlag = true;
            categoryPropLov.setSelectedItem(defaultValue);
        }
        setDirty(false);
    }

    @Override
    public TCProperty getPropertyToSave(TCProperty tcproperty) throws Exception {
        Object valueObj = getEditableValue();
        if (null != valueObj) {
            tcproperty.setStringValue(valueObj.toString());
        }
        return tcproperty;
    }

    @Override
    public void setUIFValue(Object obj) {

    }

}
