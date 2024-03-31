package com.hh.tools.newitem;

import com.teamcenter.rac.aif.AbstractAIFOperation;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.classification.common.*;
import com.teamcenter.rac.classification.common.form.G4MForm;
import com.teamcenter.rac.classification.common.tree.G4MTree;
import com.teamcenter.rac.kernel.*;
import com.teamcenter.rac.kernel.ics.*;
import com.teamcenter.rac.util.*;
import com.teamcenter.rac.util.log.Debug;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CusDataPane extends G4MDataPane implements
        InterfaceG4MInstanceListener, InterfaceG4MViewListener,
        InterfaceG4MModeListener, InterfaceG4MTabbedChild,
        InterfaceG4MResizeHandler, InterfaceG4MInstanceModifiedListener {

    private class G4MCenterPanel extends JPanel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private SplitPane m_viewerSplitPane;

        private G4MClassViewer m_g4mClassViewer;

        private G4MInstanceViewer m_g4mInstanceViewer;

        private double m_defaultViewerSplitterPosition;

        public G4MCenterPanel() {
            super(new BorderLayout());
            m_defaultViewerSplitterPosition = 0.0D;
            m_mainForm = new G4MForm(m_context, "MainForm", 1);
            m_currentForm = m_mainForm;
            m_currentForm.setMode(8);
            m_formBasePanel = new JPanel(new BorderLayout());
            m_formBasePanel.add(m_mainForm, "Center");
            m_formCenterPanel = new JPanel(new BorderLayout());
            m_formTitleBar = new G4MTitleBar(m_context, m_formCenterPanel,
                    CusDataPane.this, "g4mMainForm");
            m_formTitleBar.setMenu(new JPopupMenu());
            m_formTitleBar.setTitle(m_context.getRegistry().getString(
                    "g4mMainForm.titleBar.LABEL2", "Properties"));
            m_formTitleBar.setShowMenu(false);
            m_mainFormScrollPanel = new JScrollPane(m_formBasePanel);
            m_mainFormScrollPanel.getVerticalScrollBar().setUnitIncrement(20);
            m_formCenterPanel.add(m_formTitleBar, "North");
            m_formCenterPanel.add(m_mainFormScrollPanel, "Center");
            m_revRuleInfoPanel = new JPanel(new VerticalLayout(3, 2, 2, 5, 2));
            G4MRevisionRuleHyperlink g4mrevisionrulehyperlink = new G4MRevisionRuleHyperlink(
                    (G4MUserAppContext) m_context);
            m_context.getRevisionRuleHelper().setRevisionRuleHyperlink(
                    g4mrevisionrulehyperlink);
            m_context.getRevisionRuleHelper().setDefaultRevisionRule();
            m_revRuleInfoPanel.add("top.bind", new Separator());
            m_formBasePanel.add(m_revRuleInfoPanel, "North");
            m_g4mClassViewer = new G4MClassViewer(m_context, CusDataPane.this);
            m_g4mInstanceViewer = new G4MInstanceViewer(m_context,
                    CusDataPane.this);
            add("Center", m_formCenterPanel);
        }

        @SuppressWarnings("unused")
        public void addClassViewer() {
            m_viewerSplitPane.setTopComponent(m_g4mClassViewer);
            m_viewerSplitPane
                    .setDividerLocation(m_defaultViewerSplitterPosition);
        }

        @SuppressWarnings("unused")
        public void addInstanceViewer() {
            m_viewerSplitPane.setBottomComponent(m_g4mInstanceViewer);
            m_viewerSplitPane
                    .setDividerLocation(m_defaultViewerSplitterPosition);
        }

        public G4MClassViewer getClassViewer() {
            return m_g4mClassViewer;
        }

        public G4MInstanceViewer getInstanceViewer() {
            return m_g4mInstanceViewer;
        }
    }

    private class G4MHeaderPanel extends JPanel implements ActionListener {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        private G4MDragIcon m_dragIcon;

        private JTextField m_id;

        private JTextField m_revId;

        private JTextField m_objectName;

        private JLabel m_classNameLabel;

        private JLabel m_rev_name_sep;

        private String m_IDRevSep;

        private String m_revNameSep;

        private Registry m_appReg;

        private TCPreferenceService prefService;

        @SuppressWarnings("deprecation")
        public G4MHeaderPanel() {
            super(new HorizontalLayout(3, 5, 2, 2, 5));

            prefService = ((TCSession) m_context.getApplication().getSession())
                    .getPreferenceService();
            m_IDRevSep = prefService.getString(0,
                    "FLColumnCatIVFSeparatorPref", "/");
            m_revNameSep = prefService.getString(0,
                    "FLColumnCatObjSeparatorPref", "-");
            m_appReg = m_context.getRegistry();
            m_dragIcon = new G4MDragIcon(m_context);
            m_dragIcon.setEnabled(false);
            @SuppressWarnings("unused")
            JLabel jlabel = new JLabel(m_appReg.getString(
                    "g4mHeader.idLabel.NAME", "Object ID"));
            m_id = new JTextField(m_appReg.getInt("g4mHeader.id.SIZE", 20));
            m_id.setToolTipText(m_appReg
                    .getString(
                            "g4mHeader.id.TIP",
                            "Enter the exact Object ID, or partial Object ID and wildcard characters to search by Object ID"));
            m_id.setMargin(new Insets(0, 2, 0, 2));
            m_id.addActionListener(this);
            m_revId = new JTextField(m_appReg.getInt("g4mHeader.revId.SIZE", 3));
            m_revId.setMargin(new Insets(0, 2, 0, 2));
            m_revId.addActionListener(this);
            m_rev_name_sep = new JLabel(m_revNameSep);
            m_objectName = new JTextField(m_appReg.getInt(
                    "g4mHeader.name.SIZE", 31));
            m_objectName.setMargin(new Insets(0, 2, 0, 2));
            m_objectName.setBackground(m_rev_name_sep.getBackground());
            m_classNameLabel = new JLabel("< Class Name >");
            m_classNameLabel.setHorizontalAlignment(0);
            // add("left.nobind", m_dragIcon);
            // add("left.nobind", jlabel);
            // add("left.nobind", m_id);
            // add("left.nobind", m_id_rev_sep);
            // add("left.nobind", m_revId);
            // add("left.nobind", m_rev_name_sep);
            // add("left.nobind", m_objectName);
            // add("left.nobind", m_findIdButton.getButton());
        }

        @Override
        public void actionPerformed(ActionEvent actionevent) {
            final String s = m_id.getText();
            final String s1 = m_revId.getText();
            if (s != null && s.length() > 0)
                m_context.getSession().queueOperation(
                        new AbstractAIFOperation(s1) {

                            @Override
                            public void executeOperation() throws Exception {
                                System.out
                                        .println("=== foster test === 20100924==actionPerformed in");
                                try {
                                    int i = 0;
                                    ICSProperty aicsproperty[] = new ICSProperty[1];
                                    String s2 = s;
                                    if (s1 != null && s1.length() > 0)
                                        s2 = (new StringBuilder()).append(s2)
                                                .append(m_IDRevSep).append(s1)
                                                .toString();
                                    else
                                        s2 = (new StringBuilder()).append(s2)
                                                .append(" | ").append(s)
                                                .append(m_IDRevSep).append("*")
                                                .toString();
                                    aicsproperty[0] = new ICSProperty(-599, s2);
                                    G4MRevisionRuleHelper g4mrevisionrulehelper = m_context
                                            .getRevisionRuleHelper();
                                    if (g4mrevisionrulehelper
                                            .isRevisionRuleSearchEnabled()
                                            && (s1 == null || s1.length() == 0)) {
                                        ICSProperty aicsproperty1[] = new ICSProperty[2];
                                        String s3 = g4mrevisionrulehelper
                                                .getRevisionRuleSearchName();
                                        aicsproperty1[0] = new ICSProperty(
                                                -726, s3);
                                        System.arraycopy(aicsproperty, 0,
                                                aicsproperty1, 1,
                                                aicsproperty.length);
                                        aicsproperty = aicsproperty1;
                                    }
                                    i = m_context.search(aicsproperty, true);
                                    if (i == 0)
                                        MessageBox.post(
                                                (new StringBuilder())
                                                        .append(m_appReg
                                                                .getString(
                                                                        "g4mHeader.findIdMessage.MESSAGE",
                                                                        "ICO not found for entered object ID"))
                                                        .append(" \"")
                                                        .append(m_id.getText())
                                                        .append("\"")
                                                        .toString(),
                                                m_appReg.getString(
                                                        "g4mHeader.findIdMessage.TITLE",
                                                        "Find by ID"), 2);
                                    else
                                        m_context.read(1);
                                } catch (TCException tcexception) {
                                    MessageBox.post(m_appReg.getString(
                                            "g4mTCException.NAME",
                                            "TCException"), tcexception,
                                            m_appReg.getString(
                                                    "g4mHeader.findId.TITLE",
                                                    "find Id"), 1);
                                }
                            }

                        });
        }

        public G4MDragIcon getDragIcon() {
            return m_dragIcon;
        }

        @SuppressWarnings("unused")
        public void setId(String s) {
            m_id.setText(s);
        }

        public void setLabel(String s) {
            m_classNameLabel.setText(s);
        }

        @SuppressWarnings("unused")
        public void setMode(int i) {
            m_dragIcon.setMode(i);
        }

        /**
         * @param icsapplicationobject
         */
        public void setObject(ICSApplicationObject icsapplicationobject) {

        }

        @SuppressWarnings("unused")
        public void setObjectName(String s) {
            m_objectName.setText(s);
        }

        @SuppressWarnings("unused")
        public void setRevId(String s) {
            m_revId.setText(s);
        }

        @SuppressWarnings("unused")
        public void setType(String s) {
            m_dragIcon.setType(s);
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    protected G4MHeaderPanel m_header;
    private G4MCenterPanel m_centerPanel;
    private JPanel m_formBasePanel;
    private JPanel m_formCenterPanel;
    private G4MTitleBar m_formTitleBar;
    private JTabbedPane m_tabbedFormPane;
    private JScrollPane m_mainFormScrollPanel;
    private G4MForm m_mainForm;
    private G4MForm m_relatedForms[];
    private G4MForm m_currentForm;
    private G4MDefaultToolBar m_toolbar;
    private G4MTabbedPane m_tabbedContainer;
    private int m_tabbedContainerIndex;
    private Icon m_relatedBaseIcon;
    private Icon m_relatedObjectIcon;
    private JComponent m_maxPanel;
    protected boolean m_doStateChanged;
    private JPanel m_revRuleInfoPanel;
    private G4MSetActiveUnitMenu m_activeUnitMenu;
    private String m_metricText;
    private String m_nonmetricText;

    private String m_bothText;

    private G4MUnitSystemOptionButton m_unitSysOptionButton;

    public CusDataPane(AbstractG4MContext abstractg4mcontext) {
        super(abstractg4mcontext);
        m_maxPanel = null;
        m_activeUnitMenu = null;
        m_unitSysOptionButton = null;
        setLayout(new BorderLayout());
        m_header = new G4MHeaderPanel();
        m_centerPanel = new G4MCenterPanel();
//		 JTabbedPane jTabbedPane = createMainTabbedPane();
        m_toolbar = new G4MDefaultToolBar(m_context);
        // add("North", m_header);
        add("Center", m_centerPanel);
        // add("South", m_toolbar);
        add("South", m_header);
        m_context.setDataPane(this);
        m_context.addG4MModeListener(this);
        m_context.addG4MViewListener(this);
        m_context.addG4MInstanceListener(this);
        m_context.addG4MInstanceModifiedListener(this);
        m_relatedBaseIcon = m_context.getRegistry().getImageIcon(
                "relatedObjectBase.ICON");
        m_relatedObjectIcon = m_context.getRegistry().getImageIcon(
                "relatedObject.ICON");
        m_metricText = m_context.getRegistry().getString(
                "g4mActiveUnitMenuMetric.NAME", "metric");
        m_nonmetricText = m_context.getRegistry().getString(
                "g4mActiveUnitMenuNonmetric.NAME", "non-metric");
        m_bothText = m_context.getRegistry().getString(
                "g4mActiveUnitMenuBoth.NAME", "both");
        AbstractAIFUIApplication app = m_context.getApplication();
        System.out.println("app=" + app);

    }


    @Override
    public void clear() {
        setPropertiesActive();
        getMainForm().clear();
    }

    @Override
    public G4MForm getCurrentForm() {
        return m_currentForm;
    }

    @Override
    public G4MDragIcon getDragIcon() {
        return m_header.getDragIcon();
    }

    @Override
    public G4MForm getMainForm() {
        return m_currentForm;
    }

    @Override
    public G4MTitleBar getTitleBar() {
        return m_formTitleBar;
    }

    @Override
    public void instanceChanged(boolean flag) {

        Debug.println("G4M",
                (new StringBuilder()).append("G4MDataPane.instanceChanged( ")
                        .append(flag).append(" ).........").toString());
        final ICSApplicationObject icsAppObj = m_context
                .getICSApplicationObject();
        ICSBaseObject icsbaseobject = icsAppObj.getICSBaseObject();
        m_header.setObject(icsAppObj);

        ICSBaseObject aicsbaseobject[] = icsAppObj.getRelatedObjects();
        m_doStateChanged = true;
        if (aicsbaseobject != null && aicsbaseobject.length > 0) {
            System.out.println("G4M   icsObj > 0");
            if (m_tabbedFormPane == null) {
                System.out.println("G4M   no tabbed form");
                m_tabbedFormPane = new JTabbedPane(3);
                if (m_context.getRegistry().getBoolean("enableScrollableTabs",
                        false))
                    m_tabbedFormPane.setTabLayoutPolicy(1);
                m_tabbedFormPane.addTab("0", m_relatedBaseIcon, m_mainForm,
                        icsbaseobject.getClassId());
                for (int i = 0; i < aicsbaseobject.length; i++)
                    m_tabbedFormPane.addTab((new StringBuilder()).append("")
                                    .append(i + 1).toString(), m_relatedObjectIcon,
                            new JLabel(), aicsbaseobject[i].getClassId());

                m_formBasePanel.add(m_tabbedFormPane, "Center");
                m_tabbedFormPane.addChangeListener(new ChangeListener() {

                    @Override
                    public void stateChanged(ChangeEvent changeevent) {
                        if (m_tabbedFormPane == null)
                            return;
                        if (m_doStateChanged) {
                            int l = m_tabbedFormPane.getSelectedIndex();

                            ICSBaseObject icsbaseobject1 = null;
                            try {
                                icsbaseobject1 = icsAppObj.getRelatedObject(l);
                            } catch (TCException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            if (icsbaseobject1 != null) {
                                G4MForm g4mform;
                                if (m_tabbedFormPane.getComponentAt(l) instanceof JLabel) {
                                    g4mform = new G4MForm(m_context, "RelForm",
                                            2);
                                    // g4mform.s
                                    JScrollPane jscrollpane = new JScrollPane(
                                            g4mform);
                                    m_tabbedFormPane.setComponentAt(l,
                                            jscrollpane);
                                    g4mform.setView(icsbaseobject1
                                            .getView("Form"));
                                    m_relatedForms[l - 1] = g4mform;
                                } else if (l == 0)
                                    g4mform = m_mainForm;
                                else
                                    g4mform = m_relatedForms[l - 1];
                                m_currentForm = g4mform;
                                ICSView icsview;
                                if (l == 0) {
                                    icsview = m_mainForm.getView();
                                } else {
                                    icsview = icsbaseobject1.getView("Form");
                                }
                                m_header.setLabel(icsview.getName());
                                G4MTree g4mtree = m_context.getTree();
                                if (g4mtree != null)
                                    g4mtree.viewChanged(icsview.getViewID());
                            } else {
                                Debug.println(
                                        "ICS",
                                        (new StringBuilder())
                                                .append("TabbedForm.stateChanged   Error Can't get related Object ")
                                                .append(l).toString());
                            }
                        }
                    }
                });
            } else {
                System.out.println("G4M    tabbed form exists");
                m_doStateChanged = false;
                for (int j = m_tabbedFormPane.getTabCount() - 1; j > 0; j--)
                    m_tabbedFormPane.removeTabAt(j);

                m_doStateChanged = true;
                m_tabbedFormPane.setTitleAt(0, "0");
                m_tabbedFormPane
                        .setToolTipTextAt(0, icsbaseobject.getClassId());
                for (int k = 0; k < aicsbaseobject.length; k++)
                    m_tabbedFormPane.addTab((new StringBuilder()).append("")
                                    .append(k + 1).toString(), m_relatedObjectIcon,
                            new JLabel(), aicsbaseobject[k].getClassId());

            }
            m_relatedForms = new G4MForm[aicsbaseobject.length];
        } else {

            if (m_tabbedFormPane != null) {
                m_formBasePanel.remove(m_tabbedFormPane);
                m_tabbedFormPane = null;
                m_formBasePanel.add(m_mainForm, "Center");
                m_mainFormScrollPanel.setVisible(true);
                m_formBasePanel.revalidate();
            }
            m_relatedForms = null;

        }

        m_mainForm.setMode(8);
        if (!icsbaseobject.getView().getClassID()
                .equals(m_mainForm.getClassId())) {
            m_mainForm.setView(icsbaseobject.getView(), m_context.getMode());
        }

        if (m_context.getTree().getSelectedNode() == null) {
            m_context.getTree().expandToClass(
                    icsbaseobject.getView().getViewID());
        } else {
            if (!m_context.getTree().getSelectedNode().getNodeName()
                    .equals(icsbaseobject.getView().getViewID())) {
                m_context.getTree().expandToClass(
                        icsbaseobject.getView().getViewID());
            }
            if (m_mainForm.getMode() == 1) {
                m_mainForm.clear();
            } else {
                m_mainForm.setProperties(icsbaseobject.getProperties(m_context
                        .getActiveUnitSystem(), icsAppObj.getView()
                        .getPropertyDescriptions()));
            }
        }

        m_currentForm = m_mainForm;
        // m_centerPanel.validate();
    }

    @Override
    public void maximizeComponent(JComponent jcomponent) {
        Debug.println(
                "G4M",
                (new StringBuilder())
                        .append("G4MDataPane    maximizeComponent( ")
                        .append(jcomponent.getClass().getName()).append(" )")
                        .toString());
        if (m_maxPanel == null) {
            setVisible(false);
            remove(m_centerPanel);
            if (jcomponent == m_centerPanel.getClassViewer()) {
                remove(m_header);
                remove(m_toolbar);
                add(m_centerPanel.getClassViewer(), "Center");
            } else if (jcomponent == m_centerPanel.getInstanceViewer()) {
                remove(m_header);
                remove(m_toolbar);
                add(m_centerPanel.getInstanceViewer(), "Center");
            } else if (jcomponent == m_formCenterPanel) {
                m_centerPanel.getInstanceViewer().showImage(false);
                m_centerPanel.getClassViewer().showImage(false);
                m_formCenterPanel
                        .setBorder(BorderFactory.createEtchedBorder(0));
                add(m_formCenterPanel, "Center");
            }
            setVisible(true);
            m_maxPanel = jcomponent;
        }
    }

    @Override
    public void minimizeComponent(JComponent jcomponent) {
    }

    @Override
    public void setPropertiesActive() {
        if (m_tabbedFormPane != null)
            m_tabbedFormPane.setSelectedIndex(0);
    }

    @Override
    public void setTabbedContainerInfo(G4MTabbedPane g4mtabbedpane, int i) {
        m_tabbedContainer = g4mtabbedpane;
        m_tabbedContainerIndex = i;
        m_tabbedContainer.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent changeevent) {
                if (m_tabbedContainer.getSelectedIndex() == m_tabbedContainerIndex
                        && m_context.getInstanceViewerOwner() != this) {
                    G4MViewer g4mviewer = m_context.getInstanceViewer(this);
                    m_centerPanel.getInstanceViewer().setViewer(g4mviewer);
                }
            }

        });
    }

    @Override
    public void updateHeader() {
        m_header.setObject(m_context.getICSApplicationObject());
    }

    @Override
    public void viewChanged(String s) {
        ICSView icsview = m_context.getBaseView("Form");
        ICSProperty aicsproperty[] = null;
        if (m_context.getMode() == 4)
            try {
                aicsproperty = m_currentForm.getProperties();
            } catch (Exception exception) {
            }
        m_header.setLabel(icsview.getName());
        m_currentForm.setView(icsview, m_context.getMode());
        m_formTitleBar.setShowMenu(true);
        m_activeUnitMenu = m_context.getActiveUnitMenu();
        m_unitSysOptionButton = m_context.getUnitSystemOptionButton();
        int i = icsview.getClassUnitSystem();
        if (i == 0) {
            m_activeUnitMenu.setActiveUnitSystemIcon(i, true);
            m_context.setActiveUnitSystem(i);
            m_unitSysOptionButton.setUnitSystemSearch(m_metricText);
            m_context.setSearchInUnitSystem(i);
        }
        if (i == 1) {
            m_activeUnitMenu.setActiveUnitSystemIcon(i, true);
            m_context.setActiveUnitSystem(i);
            m_unitSysOptionButton.setUnitSystemSearch(m_nonmetricText);
            m_context.setSearchInUnitSystem(i);
        }
        if (i == 2) {
            m_activeUnitMenu.setActiveUnitSystemIcon(m_context.getActiveUnitSystem(),
                    true);
            m_context.getICSApplicationObject().getView()
                    .setActiveUnitsystem(m_context.getActiveUnitSystem());
            m_context.getActiveUnitMenu().loadCurrentForm();
            // if(m_context.m_UnitSystemSearch_Both)
            // if(true)
            // {
            m_unitSysOptionButton.setUnitSystemSearch(m_bothText);
            m_context.setSearchInUnitSystem(2);
            // } else
            // {
            // m_unitSysOptionButton.setUnitSystemSearch(m_context.getActiveUnitSystem()
            // != 0 ? m_nonmetricText : m_metricText);
            // m_context.setSearchInUnitSys(m_context.getActiveUnitSystem());
            // }
        }
        if (m_context.getMode() == 4 && aicsproperty != null)
            m_currentForm.setProperties(aicsproperty);
    }

    // public String getObjectId()
    // {
    // if(getClassifiedObject() != null)
    // {
    // String s = null;
    // try
    // {
    // String s1 =
    // m_session.getClassificationService().getTCComponentDisplayId(getClassifiedObject());
    // if(s1.equalsIgnoreCase(m_instanceId))
    // s = getClassifiedObject().getProperty("item_id");
    // else
    // s = s1;
    // }
    // catch(Exception exception)
    // {
    // s = m_instanceId;
    // }
    // return s;
    // }
    // if(m_instanceId == null)
    // return null;
    // else
    // return m_instanceId;
    // }
    //
    // public String getObjectRevId()
    // {
    // if(getClassifiedObject() != null)
    // {
    // String s = null;
    // try
    // {
    // s = getClassifiedObject().getProperty("item_revision_id");
    // }
    // catch(Exception exception)
    // {
    // s = "";
    // }
    // return s;
    // } else
    // {
    // return "";
    // }
    // }

    // public String getObjectName()
    // {
    // String s;
    // m_classifiedObjectUid= icsproperty.getValue();
    // if(m_classifiedObjectUid != null && m_classifiedObjectUid.length() > 0)
    // try
    // {
    // s = getClassifiedObject().getProperty("object_name");
    // }
    // catch(Exception exception)
    // {
    // s = "N/A";
    // }
    // else
    // s = "";
    // return s;
    // }

    // public String getObjectType()
    // {
    // String s;
    // if(m_classifiedObjectUid != null && m_classifiedObjectUid.length() > 0)
    // try
    // {
    // s = getClassifiedObject().getType();
    // }
    // catch(Exception exception)
    // {
    // s = "";
    // }
    // else
    // s = "";
    // return s;
    // }

    // public TCComponent getClassifiedObject()
    // {
    // TCComponent m_classifiedObject =
    // m_session.getClassificationService().getTCComponent(m_classifiedObjectUid);
    // if(m_classifiedObject == null)
    // try
    // {
    // m_classifiedObject =
    // m_session.getClassificationService().getTCComponent(m_classifiedObjectUid);
    // }
    // catch(TCException tcexception) { }
    // return m_classifiedObject;
    // }


}