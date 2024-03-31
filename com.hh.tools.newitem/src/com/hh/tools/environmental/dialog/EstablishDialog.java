package com.hh.tools.environmental.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.hh.tools.newitem.CreateObject;
import com.hh.tools.newitem.GetPreferenceUtil;
import com.hh.tools.newitem.ItemTypeName;
import com.hh.tools.newitem.RelationName;
import com.hh.tools.newitem.Utils;
import com.hh.tools.util.StateDialog;
import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.IRelationName;
import com.teamcenter.rac.kernel.ITypeName;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.ButtonLayout;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.combobox.iComboBox;

public class EstablishDialog extends AbstractAIFDialog {
    private int height = 25;
    private TCSession session;
    private TCComponentItemRevision pcbezbomItemRev;
    private static iComboBox ROHSComboBox;
    private static iComboBox MCDROHSComboBox;
    private static iComboBox HFComboBox;
    private static iComboBox MDDROHSComboBox;
    private static iComboBox customerComboBox;
    private StateDialog statDlg;
    private Registry reg = null;
    private List<TCComponentItemRevision> list = new ArrayList<>();

    public EstablishDialog(TCSession session, TCComponentItemRevision pcbezbomItemRev) {
        super(true);
        this.session = session;
        this.pcbezbomItemRev = pcbezbomItemRev;
        this.reg = Registry.getRegistry("com.hh.tools.environmental.environmental");
        initUI();
    }

    /**
	 * 初始化制定环保等级的窗体 Create by 郑良 2020.05.10
	 */
    public void initUI() {
        setTitle(reg.getString("title.MSG"));
        JPanel mainPanel = new JPanel();
        getContentPane().setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        {
            JPanel panel = new JPanel(new PropertyLayout(5, 5, 5, 5, 5, 5));
            panel.setBackground(Color.white);
            mainPanel.add(panel);
            panel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            JLabel ROHSStatusLabel = new JLabel("ROHS2.0 Status:", JLabel.RIGHT);
            ROHSStatusLabel.setPreferredSize(new Dimension(150, height));

            ROHSComboBox = new iComboBox();
            ROHSComboBox.setPreferredSize(new Dimension(150, height));
            ROHSComboBox.addItems(getROHSList().toArray());

            JLabel MCDROHSStatusLabel = new JLabel("MCD ROHS Status:", JLabel.RIGHT);
            MCDROHSStatusLabel.setPreferredSize(new Dimension(150, height));

            MCDROHSComboBox = new iComboBox();
            MCDROHSComboBox.setPreferredSize(new Dimension(150, height));
            MCDROHSComboBox.addItems(getROHSList().toArray());

            JLabel HFStatusLabel = new JLabel("HF Status:", JLabel.RIGHT);
            HFStatusLabel.setPreferredSize(new Dimension(150, height));

            HFComboBox = new iComboBox();
            HFComboBox.setPreferredSize(new Dimension(150, height));
            HFComboBox.addItems(getHFList());

            JLabel MDDROHSStatusLabel = new JLabel("MDD ROHS Status:", JLabel.RIGHT);
            MDDROHSStatusLabel.setPreferredSize(new Dimension(150, height));

            MDDROHSComboBox = new iComboBox();
            MDDROHSComboBox.setPreferredSize(new Dimension(150, height));
            MDDROHSComboBox.addItems(getROHSList().toArray());

            JLabel customerLabel = new JLabel("Customer:", JLabel.RIGHT);
            customerLabel.setPreferredSize(new Dimension(150, height));

            customerComboBox = new iComboBox();
            customerComboBox.setPreferredSize(new Dimension(150, height));
            customerComboBox.addItems(getCustomerList());

            panel.add("1.1.right.center.preferred.preferred", ROHSStatusLabel);
            panel.add("1.2.left.center.preferred.preferred", ROHSComboBox);
            panel.add("1.3.right.center.preferred.preferred", MCDROHSStatusLabel);
            panel.add("1.4.left.center.preferred.preferred", MCDROHSComboBox);

            panel.add("2.1.right.center.preferred.preferred", HFStatusLabel);
            panel.add("2.2.left.center.preferred.preferred", HFComboBox);
            panel.add("2.3.right.center.preferred.preferred", MDDROHSStatusLabel);
            panel.add("2.4.left.center.preferred.preferred", MDDROHSComboBox);

            panel.add("3.1.right.center.preferred.preferred", customerLabel);
            panel.add("3.2.left.center.preferred.preferred", customerComboBox);

            JPanel buttonPanel = new JPanel(new ButtonLayout(ButtonLayout.HORIZONTAL, ButtonLayout.CENTER, 20));
            JButton okButton = new JButton(reg.getString("confirm.MSG"));
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new Thread(new Runnable() {
                        public void run() {
                            confirmAction();
                        }
                    }).start();
                }
            });
            okButton.setActionCommand("OK");
            buttonPanel.add(okButton);
            getRootPane().setDefaultButton(okButton);

            JButton cancelButton = new JButton(reg.getString("cancle.MSG"));
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            cancelButton.setActionCommand("Cancel");
            buttonPanel.add(cancelButton);
            mainPanel.add(buttonPanel, BorderLayout.PAGE_END);

            pack();
            setPreferredSize(new Dimension(750, 200));
            setResizable(true);
            centerToScreen();
            this.setVisible(true);
        }
    }

    private void confirmAction() {
        try {
            String reach = ROHSComboBox.getSelectedItem().toString();
            String mcdROHS = MCDROHSComboBox.getSelectedItem().toString();
            String hf = HFComboBox.getSelectedItem().toString();
            String mddROHS = MDDROHSComboBox.getSelectedItem().toString();
            String cus = customerComboBox.getSelectedItem().toString();
            if (Utils.isNull(reach) && Utils.isNull(mcdROHS) && Utils.isNull(hf) && Utils.isNull(mddROHS)
                    && Utils.isNull(cus)) {
                return;
            }
            Utils.setByPass(true, session);
			statDlg = new StateDialog(this, "信息");
			statDlg.setMessage(reg.getString("createInfo1.MSG"));
			// 电子料下如果没有环保认证则新建
            TCComponentBOMLine topBOMLine = Utils.createBOMLine(session, pcbezbomItemRev);
            List<TCComponentItemRevision> itemRevList = recursionBOMLine(topBOMLine);
            // 校验是否覆盖
         	// 如果当前替代料已经制定过环保认证则给出提示，是否覆盖环保认证等级
            boolean isCover = isCover(itemRevList);
            if (isCover) {
            	Object[] options = { "是", "否" };
				int m = JOptionPane.showOptionDialog(null, "当前替代料已制定过环保认证等级，是否覆盖？", "", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (m == JOptionPane.YES_OPTION) {
                    for (TCComponentItemRevision itemRev : itemRevList) {
                        System.out.println("itemRev==" + itemRev.getProperty("object_name"));
                        TCComponent[] coms = itemRev.getRelatedComponents("IMAN_specification");
                        TCComponentForm form = null;
                        if (coms != null && coms.length > 0) {
                            for (TCComponent tcComponent : coms) {
                                System.out.println("type==" + tcComponent.getType());
                                if (tcComponent instanceof TCComponentForm
                                        && ItemTypeName.PCOMPLIANCEFORM.equals(tcComponent.getType())) {
                                    form = (TCComponentForm) tcComponent;
                                    break;
                                }
                            }
                        }
                        if (form != null) {
                            TCProperty prop = form.getTCProperty(RelationName.COMPLIANCES);
                            TCComponent[] childEPIForms = prop.getReferenceValueArray();
                            if (childEPIForms != null) {
                                TCComponentForm currentPCBZBOMForm = null;
                                for (int i = 0; i < childEPIForms.length; i++) {
                                    TCComponent component = childEPIForms[i];
                                    TCComponent pcbzbom = component.getReferenceProperty("fx8_PCBEZBOM");
                                    if (pcbzbom.getProperty("item_id").equals(pcbezbomItemRev.getProperty("item_id"))) {
                                        currentPCBZBOMForm = (TCComponentForm) component;
                                        break;
                                    }
                                }
                                currentPCBZBOMForm.setProperty("fx8_PCBEZBOMCustomer", cus);
                                currentPCBZBOMForm.setProperty("fx8_PCBEZBOMHFStatus", hf);
                                currentPCBZBOMForm.setProperty("fx8_PCBEZBOMMCDRoHSStatus", mcdROHS);
                                currentPCBZBOMForm.setProperty("fx8_PCBEZBOMMDDRoHSStatus", mddROHS);
                                currentPCBZBOMForm.setProperty("fx8_PCBEZBOMFMDREACHStatus", reach);
                            }
                        }
                    }
                } else {
                    statDlg.dispose();
                    statDlg.stopth();
                    return;
                }
            } else {
                for (TCComponentItemRevision itemRev : itemRevList) {
                    TCComponent[] coms = itemRev.getRelatedComponents("IMAN_specification");
                    TCComponentForm form = null;
                    if (coms != null && coms.length > 0) {
                        for (TCComponent tcComponent : coms) {
                            System.out.println("2==type==" + tcComponent.getType());
                            if (tcComponent instanceof TCComponentForm
                                    && ItemTypeName.PCOMPLIANCEFORM.equals(tcComponent.getType())) {
                                form = (TCComponentForm) tcComponent;
                                break;
                            }
                        }
                    }
                    if (form == null) {
                    	form = CreateObject.createTempForm(session, ItemTypeName.PCOMPLIANCEFORM,
								itemRev.getProperty("item_id") + "_环保认证", true);
                        form.save();
                        itemRev.add(IRelationName.IMAN_specification, form);
                    }

                    // 创建电子料环保认证表单
                    TCComponentForm childEPIForm = CreateObject.createTempForm(session, ItemTypeName.EPIDENTIFICATION,
                            itemRev.getProperty("item_id") + "_" + getSeqFromEDAComp(itemRev), true);
                    childEPIForm.save();
                    childEPIForm.setProperty("fx8_PCBEZBOMCustomer", cus);
                    childEPIForm.setProperty("fx8_PCBEZBOMHFStatus", hf);
                    childEPIForm.setProperty("fx8_PCBEZBOMMCDRoHSStatus", mcdROHS);
                    childEPIForm.setProperty("fx8_PCBEZBOMMDDRoHSStatus", mddROHS);
                    childEPIForm.setProperty("fx8_PCBEZBOMFMDREACHStatus", reach);
                    childEPIForm.setReferenceProperty("fx8_PCBEZBOM", pcbezbomItemRev);

                    TCProperty prop = form.getTCProperty(RelationName.COMPLIANCES);
                    TCComponent[] childEPIForms = prop.getReferenceValueArray();
                    if (childEPIForms != null) {
                        TCComponent[] newchildForms = new TCComponent[childEPIForms.length + 1];
                        for (int i = 0; i < childEPIForms.length; i++) {
                            newchildForms[i] = childEPIForms[i];
                        }
                        newchildForms[childEPIForms.length] = childEPIForm;
                        prop.setReferenceValueArray(newchildForms);

                    } else {
                        prop.setReferenceValueArray(new TCComponent[]{childEPIForm});
                    }
                    System.out.println("保存环保认证信息");
                }
            }

            Utils.setByPass(false, session);
            statDlg.dispose();
            statDlg.stopth();
            MessageBox.post(reg.getString("createInfo2.MSG"), reg.getString("Info.MSG"), MessageBox.WARNING);
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isCover(List<TCComponentItemRevision> itemRevList) {
        try {
            for (TCComponentItemRevision itemRev : itemRevList) {
                System.out.println("itemRev==" + itemRev.getProperty("object_name"));
                TCComponent[] coms = itemRev.getRelatedComponents("IMAN_specification");
                TCComponentForm form = null;
                if (coms != null && coms.length > 0) {
                    for (TCComponent tcComponent : coms) {
                        System.out.println("type==" + tcComponent.getType());
                        if (tcComponent instanceof TCComponentForm
                                && ItemTypeName.PCOMPLIANCEFORM.equals(tcComponent.getType())) {
                            form = (TCComponentForm) tcComponent;
                            break;
                        }
                    }
                }
                if (form != null) {
                    TCProperty prop = form.getTCProperty(RelationName.COMPLIANCES);
                    TCComponent[] childEPIForms = prop.getReferenceValueArray();
                    if (childEPIForms != null) {
                        for (int i = 0; i < childEPIForms.length; i++) {
                            TCComponent component = childEPIForms[i];
                            TCComponent pcbzbom = component.getReferenceProperty("fx8_PCBEZBOM");
                            if (pcbzbom.getProperty("item_id").equals(pcbezbomItemRev.getProperty("item_id"))) {
                                return true;
                            }
                        }
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private List<String> getROHSList() {
        try {
            TCComponentListOfValuesType lovType = (TCComponentListOfValuesType) session
                    .getTypeComponent(ITypeName.ListOfValues);
            TCComponentListOfValues listOfValues = lovType.findLOVByName("FX8_MCDROHSStatusLOV");
            return Utils.getLOVList(listOfValues);
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    // 遍历BOM
    private List<TCComponentItemRevision> recursionBOMLine(TCComponentBOMLine bomLine) {
        try {
            AIFComponentContext[] aifComponentContexts = bomLine.getChildren();
            if (aifComponentContexts != null && aifComponentContexts.length > 0) {
                for (int i = 0; i < aifComponentContexts.length; i++) {
                    TCComponentBOMLine childrenLine = (TCComponentBOMLine) aifComponentContexts[i].getComponent();
                    TCComponentItemRevision childrenRev = childrenLine.getItemRevision();
                    if (!list.contains(childrenRev))
                        list.add(childrenRev);
                    if (childrenLine.hasChildren()) {
                        recursionBOMLine(childrenLine);
                    }
                }
            }
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }

    private String[] getHFList() {
        return new GetPreferenceUtil().getArrayPreference(session, TCPreferenceService.TC_preference_site,
                "FX_Get_HF_Values");
    }

    private String[] getCustomerList() {
        return new GetPreferenceUtil().getArrayPreference(session, TCPreferenceService.TC_preference_site,
                "FX_Get_Customer_Values");
    }

    private String getSeqFromEDAComp(TCComponentItemRevision itemRev) {
        List<String> list = new ArrayList<String>();
        StringBuilder seqStringBuilder = new StringBuilder();
        try {
            TCComponent[] coms = itemRev.getRelatedComponents(IRelationName.IMAN_specification);
            if (coms != null && coms.length > 0) {
                TCComponentForm PComplianceform = null;
                for (TCComponent tcComponent : coms) {
                    if (tcComponent instanceof TCComponentForm
                            && ItemTypeName.PCOMPLIANCEFORM.equals(tcComponent.getType())) {
                        PComplianceform = (TCComponentForm) tcComponent;
                        break;
                    }
                }
                System.out.println("PComplianceform==" + PComplianceform.getProperty("object_name"));
                TCProperty prop = PComplianceform.getTCProperty(RelationName.COMPLIANCES);
                TCComponent[] childForms = prop.getReferenceValueArray();
                if (childForms != null && childForms.length > 0) {
                    for (int i = 0; i < childForms.length; i++) {
                        String objectName = childForms[i].getProperty("object_name");
                        System.out.println("objectName==" + objectName);
                        String seq = objectName.substring(objectName.lastIndexOf("_") + 1, objectName.length());
                        System.out.println("seq==" + seq);
                        if (!"null".equals(seq))
                            list.add(seq);
                    }
                    Collections.sort(list, Collections.reverseOrder());
                    int seq = Integer.valueOf(list.get(0)) + 1;
                    while (seqStringBuilder.length() + String.valueOf(seq).length() < 4) {
                        seqStringBuilder.append("0");
                    }
                    seqStringBuilder.append(seq);
                } else {
                    return "0001";
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seqStringBuilder.toString();

    }

}
