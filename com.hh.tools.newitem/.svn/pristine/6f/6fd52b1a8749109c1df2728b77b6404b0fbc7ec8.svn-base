package com.hh.tools.customerPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.hh.tools.newitem.Utils;
import com.hh.tools.renderingHint.EDACompDataSheetProperty;
import com.hh.tools.renderingHint.EDACompDellSymbolProperty;
import com.hh.tools.renderingHint.EDACompFootprintProperty;
import com.hh.tools.renderingHint.EDACompPadProperty;
import com.hh.tools.renderingHint.EDACompSymbolProperty;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.viewedit.ViewEditHelper;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.stylesheet.AbstractRendering;
import com.teamcenter.rac.stylesheet.PropertyBeanContainer;
import com.teamcenter.rac.stylesheet.RenderingLoader;
import com.teamcenter.rac.stylesheet.XMLRendering;
import com.teamcenter.rac.util.Utilities;

public class EDACompRendering extends AbstractRendering {

    private static final long serialVersionUID = 1L;

    private TCSession session = null;
    private TCComponentItemRevision itemRev = null;
    private AbstractRendering hwRendering;
    private AbstractRendering layoutRendering;
    private ICSPannelRendering ceRendering;

    public EDACompRendering(TCComponent tcComponent) throws Exception {
        super(tcComponent);
        itemRev = (TCComponentItemRevision) tcComponent;
        session = itemRev.getSession();
        loadRendering();
    }

    @Override
    public void loadRendering() throws TCException {
        this.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        XMLRendering.setCurrentApp(AIFUtility.getCurrentApplication());
        try {
            String hwFileName;
            String group = itemRev.getProperty("owning_group");
            if (group.contains("Printer")) {
                hwFileName = "HWRevisionPropPrinter";
            } else {
                hwFileName = "HWRevisionProp";
            }

            String hw = RenderingLoader.getXMLFileName(hwFileName, session);
            JPanel hwPanel = XMLRendering.parse(hw, itemRev.getTypeComponent(), itemRev, 1);
            hwRendering = new PropertyBeanContainer(hwPanel, itemRev);
            hwRendering.setPreferredSize(new Dimension(700, 700));
            hwRendering.setBackground(Color.WHITE);
//			hwRendering.setRenderingReadOnly();
            tabbedPane.add("HW", hwRendering);

            String layout = RenderingLoader.getXMLFileName("LayoutRevisionProp", session);
            JPanel layoutJPanel = XMLRendering.parse(layout, itemRev.getTypeComponent(), itemRev, 1);
            layoutRendering = new PropertyBeanContainer(layoutJPanel, itemRev);
            layoutRendering.setPreferredSize(new Dimension(700, 700));
            layoutRendering.setBackground(Color.WHITE);
//			layoutRendering.setRenderingReadOnly();
            tabbedPane.add("LAYOUT", layoutRendering);

//			String ce = RenderingLoader.getXMLFileName("CERevisionProp", session);
//			JPanel cePanel = XMLRendering.parse(ce, itemRev.getTypeComponent(), itemRev, 1);
            ceRendering = new ICSPannelRendering(itemRev);
//			ceRendering.setPreferredSize(new Dimension(700, 700));
            ceRendering.setBackground(Color.WHITE);
            ceRendering.setRenderingReadOnly();
            tabbedPane.add("CE", ceRendering);

            this.add(tabbedPane, BorderLayout.CENTER);
        } catch (Exception e) {
            System.out.println(e);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void updateRendering() {
        if (this.component == null) {
            return;
        }
        ViewEditHelper localViewEditHelper = new ViewEditHelper(this.component.getSession());
        ViewEditHelper.CKO localCKO = localViewEditHelper.getObjectState(this.component);
//		System.out.println("=======================localCKO=" + localCKO.name());
        switch (localCKO) {
            case CHECKED_OUT_SAME_USER_CANCEL_CHECKOUTABLE:
            case CHECKED_OUT_SAME_USER_NON_CANCEL_CHECKOUTABLE:
            case IMPLICITLY_CHECKOUTABLE:
                Utilities.invokeLater(new Runnable() {
                    public void run() {
                        System.err.println("IMPLICITLY_CHECKOUTABLE");
                        setModifiable(true);
                        setTab("HW");
//					setTab("LAYOUT");
//					setTab("ICS");
                    }
                });
                break;
            case CHECKED_IN:
            case NOT_CHECKOUTABLE:
                Utilities.invokeLater(new Runnable() {
                    public void run() {
                        System.err.println("CHECKED_IN");
                        setModifiable(false);
                        setTab("");
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isRenderingModified() {
        return this.modifiable;
    }

    public void saveRendering() {
        System.out.println("=========saveRendering()=========");
        try {
            Utils.byPass(true);
            save("HW");
//			save("LAYOUT");
//			save("ICS");
            Utils.byPass(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTab(String tabName) {
        System.out.println("=========setTab =========" + tabName);
        boolean isCan = true;
        if ("HW".equals(tabName)) {
            hwRendering.setRenderingReadWrite();
            EDACompDataSheetProperty.setFilePathEditable();
            EDACompSymbolProperty.setFilePathEditable();
            EDACompDellSymbolProperty.setFilePathEditable();
            layoutRendering.setRenderingReadOnly();
            ceRendering.setRenderingReadOnly();

            isCan = false;
            EDACompFootprintProperty.setCanUpload(isCan);
            EDACompPadProperty.setCanUpload(isCan);
            ceRendering.setCanUpload(isCan);

            isCan = true;
            EDACompDataSheetProperty.setCanUpload(isCan);
            EDACompSymbolProperty.setCanUpload(isCan);
            EDACompDellSymbolProperty.setCanUpload(isCan);
        } else if ("LAYOUT".equals(tabName)) {
            hwRendering.setRenderingReadOnly();
            layoutRendering.setRenderingReadWrite();
            EDACompFootprintProperty.setFilePathEditable();
            EDACompPadProperty.setFilePathEditable();
            ceRendering.setRenderingReadOnly();

            isCan = false;
            EDACompDataSheetProperty.setCanUpload(isCan);
            EDACompSymbolProperty.setCanUpload(isCan);
            EDACompDellSymbolProperty.setCanUpload(isCan);
            ceRendering.setCanUpload(isCan);

            isCan = true;
            EDACompFootprintProperty.setCanUpload(isCan);
            EDACompPadProperty.setCanUpload(isCan);

        } else if ("ICS".equals(tabName)) {
            hwRendering.setRenderingReadOnly();
            layoutRendering.setRenderingReadOnly();
            ceRendering.initRenderingReadWrite();

            isCan = false;
            EDACompDataSheetProperty.setCanUpload(isCan);
            EDACompSymbolProperty.setCanUpload(isCan);
            EDACompDellSymbolProperty.setCanUpload(isCan);
            EDACompFootprintProperty.setCanUpload(isCan);
            EDACompPadProperty.setCanUpload(isCan);

            isCan = true;
            ceRendering.setCanUpload(isCan);
        } else {
            hwRendering.setRenderingReadOnly();
            layoutRendering.setRenderingReadOnly();
            ceRendering.setRenderingReadOnly();

            isCan = false;
            EDACompDataSheetProperty.setCanUpload(isCan);
            EDACompSymbolProperty.setCanUpload(isCan);
            EDACompDellSymbolProperty.setCanUpload(isCan);
            EDACompFootprintProperty.setCanUpload(isCan);
            EDACompPadProperty.setCanUpload(isCan);
            ceRendering.setCanUpload(isCan);
        }
    }

    public void save(String tabName) {
        System.out.println("=========task save=========" + tabName);
        if ("HW".equals(tabName)) {
            hwRendering.saveRenderingAll();
            EDACompDataSheetProperty.clearStaticData();
            EDACompSymbolProperty.clearStaticData();
            EDACompDellSymbolProperty.clearStaticData();
        } else if ("LAYOUT".equals(tabName)) {
            layoutRendering.saveRenderingAll();
            EDACompFootprintProperty.clearStaticData();
            EDACompPadProperty.clearStaticData();
        } else if ("ICS".equals(tabName)) {
            ceRendering.saveRendering();
        }
    }
}
