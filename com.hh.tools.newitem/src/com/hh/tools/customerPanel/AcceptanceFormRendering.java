package com.hh.tools.customerPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.viewedit.ViewEditHelper;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentEnvelope;
import com.teamcenter.rac.kernel.TCComponentEnvelopeType;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentFormType;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.stylesheet.AbstractRendering;
import com.teamcenter.rac.util.Utilities;

public class AcceptanceFormRendering extends AbstractRendering {

    private TCComponentForm form = null;
    private TCSession session = null;
    private JPanel contentPanel = new JPanel();
    private JComboBox<String> approvalStatusComboBox = null;
    private JTextArea approvalSheetTextArea = null;
    private JComboBox<String> compApprovalComboBox = null;
    private JTextArea descriptionTextArea = null;
    private JComboBox<String> HFStatusComboBox = null;
    private JTextArea HFPNTextArea = null;
    private JComboBox<String> onBoardtestComboBox = null;
    private JTextArea STDPHTextArea = null;

    private TCComponentItemRevision EDAItemRev = null;
    private boolean isIcsClass = false;

    public AcceptanceFormRendering(TCComponent arg0) throws Exception {
        super(arg0);
        form = (TCComponentForm) arg0;
        session = form.getSession();
        AIFComponentContext[] aifComponentContexts = form.whereReferenced();
        if (aifComponentContexts != null && aifComponentContexts.length != 0) {
            for (int i = 0; i < aifComponentContexts.length; i++) {
                InterfaceAIFComponent aifComponent = aifComponentContexts[i].getComponent();
                if (aifComponent instanceof TCComponentItemRevision) {
                    TCComponentItemRevision itemRev = (TCComponentItemRevision) aifComponent;
                    EDAItemRev = itemRev.getItem().getLatestItemRevision();
                    System.out.println("EDAItemRev == " + EDAItemRev);
                    String[] icss = EDAItemRev.getTCProperty("fnd0IcsClassNames").getStringArrayValue();
                    if (icss != null && icss.length != 0) {
                        isIcsClass = true;
                        System.out.println("isIcsClass == " + isIcsClass);
                    }
                }
            }
        }
        loadRendering();
    }

    @Override
    public void loadRendering() throws TCException {
        initUI();
    }

    private void initUI() {
        this.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            JPanel mainPanel = new JPanel();
            contentPanel.add(mainPanel, BorderLayout.CENTER);
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBackground(Color.WHITE);
            {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                mainPanel.add(panel);
                JLabel label1 = new JLabel("Approval Status:", JLabel.RIGHT);
                panel.add(label1);
                label1.setPreferredSize(new Dimension(150, 25));

                approvalStatusComboBox = new JComboBox<String>();
                approvalStatusComboBox.setPreferredSize(new Dimension(200, 25));
                panel.add(approvalStatusComboBox);
            }
            {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                mainPanel.add(panel);
                JLabel label2 = new JLabel("Approval Sheet:", JLabel.RIGHT);
                panel.add(label2);
                label2.setPreferredSize(new Dimension(150, 25));

                approvalSheetTextArea = new JTextArea();
                approvalSheetTextArea.setLineWrap(true);
                JScrollPane descScrollPane1 = new JScrollPane();
                descScrollPane1.setPreferredSize(new Dimension(400, 3 * 25));
                descScrollPane1.setViewportView(approvalSheetTextArea);
                panel.add(descScrollPane1);
            }
            {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                mainPanel.add(panel);
                JLabel label3 = new JLabel("Comp Approval:", JLabel.RIGHT);
                panel.add(label3);
                label3.setPreferredSize(new Dimension(150, 25));

                compApprovalComboBox = new JComboBox<String>();
                compApprovalComboBox.setPreferredSize(new Dimension(200, 25));
                panel.add(compApprovalComboBox);
            }
            {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                mainPanel.add(panel);
                JLabel label5 = new JLabel("Description:", JLabel.RIGHT);
                panel.add(label5);
                label5.setPreferredSize(new Dimension(150, 25));

                descriptionTextArea = new JTextArea();
                descriptionTextArea.setLineWrap(true);
                JScrollPane descScrollPane1 = new JScrollPane();
                descScrollPane1.setPreferredSize(new Dimension(400, 3 * 25));
                descScrollPane1.setViewportView(descriptionTextArea);
                panel.add(descScrollPane1);
            }
            {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                mainPanel.add(panel);
                JLabel label6 = new JLabel("HF Status:", JLabel.RIGHT);
                panel.add(label6);
                label6.setPreferredSize(new Dimension(150, 25));

                HFStatusComboBox = new JComboBox<String>();
                HFStatusComboBox.setPreferredSize(new Dimension(200, 25));
                panel.add(HFStatusComboBox);
            }
            {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                mainPanel.add(panel);
                JLabel label8 = new JLabel("HFPN:", JLabel.RIGHT);
                panel.add(label8);
                label8.setPreferredSize(new Dimension(150, 25));

                HFPNTextArea = new JTextArea();
//				HFPNTextArea.setEditable(false);
                HFPNTextArea.setLineWrap(true);
                JScrollPane descScrollPane1 = new JScrollPane();
                descScrollPane1.setPreferredSize(new Dimension(400, 3 * 25));
                descScrollPane1.setViewportView(HFPNTextArea);
                panel.add(descScrollPane1);
            }
            {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                mainPanel.add(panel);
                JLabel label7 = new JLabel("Onboardtest:", JLabel.RIGHT);
                panel.add(label7);
                label7.setPreferredSize(new Dimension(150, 25));

                onBoardtestComboBox = new JComboBox<String>();
                onBoardtestComboBox.setPreferredSize(new Dimension(200, 25));
                panel.add(onBoardtestComboBox);
            }
            {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                mainPanel.add(panel);
                JLabel label8 = new JLabel("STDPH:", JLabel.RIGHT);
                panel.add(label8);
                label8.setPreferredSize(new Dimension(150, 25));

                STDPHTextArea = new JTextArea();
                STDPHTextArea.setLineWrap(true);
                JScrollPane descScrollPane1 = new JScrollPane();
                descScrollPane1.setPreferredSize(new Dimension(400, 3 * 25));
                descScrollPane1.setViewportView(STDPHTextArea);
                panel.add(descScrollPane1);
            }
        }

        try {
            TCComponentFormType formType = (TCComponentFormType) form.getTypeComponent();
            System.out.println("formType == " + formType);

            TCComponentListOfValues lov1 = formType.getPropertyDescriptor("fx8_ApprovalStatus").getLOV();
            ArrayList<String> lovValues1 = Utils.getLOVList(lov1);
            approvalStatusComboBox.addItem("");
            for (int i = 0; i < lovValues1.size(); i++) {
                approvalStatusComboBox.addItem(lovValues1.get(i));
            }

            TCComponentListOfValues lov2 = formType.getPropertyDescriptor("fx8_CompApproval").getLOV();
            ArrayList<String> lovValues2 = Utils.getLOVList(lov2);
            compApprovalComboBox.addItem("");
            for (int i = 0; i < lovValues2.size(); i++) {
                compApprovalComboBox.addItem(lovValues2.get(i));
            }

            HFStatusComboBox.addItem("");
            HFStatusComboBox.addItem("Y");
            HFStatusComboBox.addItem("N");
//			HFStatusComboBox.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					String hfStatus = HFStatusComboBox.getSelectedItem().toString();
//					String fx8_STDPN = STDPHTextArea.getText();
//					if (StringUtils.isNotBlank(fx8_STDPN)) {
//						if ("N".equalsIgnoreCase(hfStatus)) {
//							HFPNTextArea.setText(fx8_STDPN);
//						} else if ("Y".equalsIgnoreCase(hfStatus)) {
//							String flag = fx8_STDPN.substring(fx8_STDPN.length() - 1);
//							if ("G".equalsIgnoreCase(flag)) {
//								HFPNTextArea.setText(fx8_STDPN.substring(0, fx8_STDPN.length() - 1) + "H");
//							}
//						} else {
//							HFPNTextArea.setText("");
//						}
//					}
//				}
//			});

            TCComponentListOfValues lov4 = formType.getPropertyDescriptor("fx8_OnboardTest").getLOV();
            ArrayList<String> lovValues4 = Utils.getLOVList(lov4);
            onBoardtestComboBox.addItem("");
            for (int i = 0; i < lovValues4.size(); i++) {
                onBoardtestComboBox.addItem(lovValues4.get(i));
            }
//			onBoardtestComboBox.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					String compApprovalComboBoxValue = compApprovalComboBox.getSelectedItem().toString();
//					String onBoradTextValue = onBoardtestComboBox.getSelectedItem().toString();
//					if ("Y".equalsIgnoreCase(onBoradTextValue)) {
//						if ("Y".equalsIgnoreCase(compApprovalComboBoxValue)) {
//							approvalStatusComboBox.setSelectedItem("A1");
//						} else if ("N".equalsIgnoreCase(compApprovalComboBoxValue)) {
//							approvalStatusComboBox.setSelectedItem("A2");
//						} else if ("E".equalsIgnoreCase(compApprovalComboBoxValue)) {
//							approvalStatusComboBox.setSelectedItem("A5");
//						} else {
//							approvalStatusComboBox.setSelectedItem("");
//						}
//					} else if ("N".equalsIgnoreCase(onBoradTextValue)) {
//						if ("Y".equalsIgnoreCase(compApprovalComboBoxValue)) {
//							approvalStatusComboBox.setSelectedItem("A3");
//						} else if ("N".equalsIgnoreCase(compApprovalComboBoxValue)) {
//							approvalStatusComboBox.setSelectedItem("P");
//						} else if ("E".equalsIgnoreCase(compApprovalComboBoxValue)) {
//							approvalStatusComboBox.setSelectedItem("A4");
//						} else {
//							approvalStatusComboBox.setSelectedItem("");
//						}
//					} else if ("Reject".equals(onBoradTextValue)) {
//						if ("Fail".equalsIgnoreCase(compApprovalComboBoxValue)) {
//							approvalStatusComboBox.setSelectedItem("R");
//						} else {
//							approvalStatusComboBox.setSelectedItem("");
//						}
//					} else {
//						approvalStatusComboBox.setSelectedItem("");
//					}
//				}
//			});

            String fx8_ApprovalStatus = form.getProperty("fx8_ApprovalStatus");
            String fx8_ApprovalSheet = form.getProperty("fx8_Approvalsheet");
            String fx8_CompApproval = form.getProperty("fx8_CompApproval");
            String fx8_Description = form.getProperty("fx8_ObjectDesc");
            String fx8_HFStatus = form.getProperty("fx8_HFStatus");
            String fx8_HFPN = form.getProperty("fx8_HFPN");
            String fx8_OnboardTest = form.getProperty("fx8_OnboardTest");
            String fx8_STDPN = form.getProperty("fx8_STDPN");
            if (fx8_ApprovalStatus != null && !"".equals(fx8_ApprovalStatus)) {
                approvalStatusComboBox.setSelectedItem(fx8_ApprovalStatus);
            }
            if (fx8_ApprovalSheet != null && !"".equals(fx8_ApprovalSheet)) {
                approvalSheetTextArea.setText(fx8_ApprovalSheet);
            }
            if (fx8_CompApproval != null && !"".equals(fx8_CompApproval)) {
                compApprovalComboBox.setSelectedItem(fx8_CompApproval);
            }
            if (fx8_Description != null && !"".equals(fx8_Description)) {
                descriptionTextArea.setText(fx8_Description);
            }
            if (fx8_HFStatus != null && !"".equals(fx8_HFStatus)) {
                HFStatusComboBox.setSelectedItem(fx8_HFStatus);
            }
            if (fx8_HFPN != null && !"".equals(fx8_HFPN)) {
                HFPNTextArea.setText(fx8_HFPN);
            }
            if (fx8_OnboardTest != null && !"".equals(fx8_OnboardTest)) {
                onBoardtestComboBox.setSelectedItem(fx8_OnboardTest);
            }
            if (fx8_STDPN != null && !"".equals(fx8_STDPN)) {
                STDPHTextArea.setText(fx8_STDPN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initData() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateRendering() {
        if (this.component == null) {
            return;
        }
        ViewEditHelper localViewEditHelper = new ViewEditHelper(form.getSession());
        ViewEditHelper.CKO localCKO = localViewEditHelper.getObjectState(form);
        switch (localCKO) {
            case CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE:
            case CHECKED_OUT_SAME_USER_NON_CANCEL_CHECKOUTABLE:
            case IMPLICITLY_CHECKOUTABLE:
                System.err.println("checkOut!!!");
                Utilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
//					HFPNTextArea.setEditable(false);
                        String fx8_STDPN = STDPHTextArea.getText();
                        if (isIcsClass == false) {
                            System.out.println("fx8_STDPN == " + fx8_STDPN);
                            if (fx8_STDPN == null || "".equals(fx8_STDPN)) {
                                try {
                                    String fx8_HHPN = EDAItemRev.getProperty("fx8_HHPN");
                                    String description = EDAItemRev.getProperty("object_desc");
                                    STDPHTextArea.setText(fx8_HHPN);
                                    descriptionTextArea.setText(description);
                                } catch (TCException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            descriptionTextArea.setEnabled(false);
                            HFPNTextArea.setEnabled(false);
                            STDPHTextArea.setEnabled(false);
                        }
                    }
                });
                break;
            case CHECKED_IN:
            case NOT_CHECKOUTABLE:
                System.err.println("checkIn!!!");
                break;
            default:
                break;
        }
    }

    @Override
    public void saveRendering() {
        System.out.println("saveRendering");
        try {
            Utils.byPass(true);
            form.setProperty("fx8_ApprovalStatus", approvalStatusComboBox.getSelectedItem().toString());
            form.setProperty("fx8_Approvalsheet", approvalSheetTextArea.getText());
            form.setProperty("fx8_CompApproval", compApprovalComboBox.getSelectedItem().toString());
            form.setProperty("fx8_ObjectDesc", descriptionTextArea.getText());
            form.setProperty("fx8_HFStatus", HFStatusComboBox.getSelectedItem().toString());
            form.setProperty("fx8_HFPN", HFPNTextArea.getText());
            form.setProperty("fx8_OnboardTest", onBoardtestComboBox.getSelectedItem().toString());
            form.setProperty("fx8_STDPN", STDPHTextArea.getText());
            Utils.byPass(false);
            String value = approvalStatusComboBox.getSelectedItem().toString();
            if ("R".equals(value)) {
                ArrayList<TCComponentItemRevision> list = new ArrayList<TCComponentItemRevision>();
                TCComponent[] coms = EDAItemRev.whereUsed((short) 0);
                System.out.println("coms == " + coms.length);
                if (coms != null && coms.length != 0) {
                    for (int i = 0; i < coms.length; i++) {
                        TCComponent com = coms[i];
                        if (com instanceof TCComponentItemRevision) {
                            TCComponentItemRevision tempItemRev = (TCComponentItemRevision) com;
                            if (tempItemRev.isTypeOf("FX8_PartRevision")) {
                                list.add(tempItemRev);
                            }
                        }
                    }
                }

                for (int i = 0; i < list.size(); i++) {
                    TCComponentItemRevision partItemRev = list.get(i);
                    String partItemId = partItemRev.getProperty("item_id");
                    String EDAItemId = EDAItemRev.getProperty("item_id");
                    String mailName = "物料【" + EDAItemId + "】状态为【R】，请替换组件物料【" + partItemId + "】";
					String comment = "您负责的组件物料【" + partItemId + "】所使用的物料【" + EDAItemId + "】认证状态为【R】，请替换物料！";
                    TCComponentUser user = (TCComponentUser) partItemRev.getTCProperty("owning_user")
                            .getReferenceValue();
                    TCComponentEnvelopeType envelopeType = (TCComponentEnvelopeType) session
                            .getTypeComponent("Envelope");
                    TCComponentEnvelope envelope = envelopeType.create(mailName, comment, "");
                    envelope.addAttachments(new TCComponent[]{EDAItemRev});
                    envelope.addReceivers(new TCComponent[]{user});
                    envelope.send();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
