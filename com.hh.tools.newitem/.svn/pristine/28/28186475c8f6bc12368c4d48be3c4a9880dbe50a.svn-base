package com.hh.tools.environmental.action;

import java.awt.Frame;
import java.util.List;

import com.hh.tools.environmental.dialog.EstablishDialog;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class EstablishAction extends AbstractAIFAction {
    private AbstractAIFUIApplication app = null;
    private TCSession session = null;
    private Registry reg = null;

    public EstablishAction(AbstractAIFUIApplication arg0, Frame arg1, String arg2) {
        super(arg0, arg1, arg2);
        this.app = arg0;
        this.session = (TCSession) this.app.getSession();
        this.reg = Registry.getRegistry("com.hh.tools.environmental.environmental");
    }

    @Override
    public void run() {
        InterfaceAIFComponent aifComponent = this.app.getTargetComponent();
        TCComponentItemRevision itemRev = null;
        try {
            if (aifComponent != null) {
                if (aifComponent instanceof TCComponentBOMLine) {
                    TCComponentBOMLine bomLine = (TCComponentBOMLine) aifComponent;
                    itemRev = bomLine.getItemRevision();
                } else if (aifComponent instanceof TCComponentItemRevision) {
                    itemRev = (TCComponentItemRevision) aifComponent;
                }
                if (itemRev == null || !"FX8_PCBEZBOMRevision".equals(itemRev.getType())) {
                    MessageBox.post(reg.getString("selecErr1.MSG"),
                            reg.getString("Warn.MSG"), MessageBox.WARNING);
                    return;
                }
            } else {
                MessageBox.post(reg.getString("selecErr1.MSG"),
                        reg.getString("Warn.MSG"), MessageBox.WARNING);
                return;
            }
            TCComponentBOMLine topBOMLine = Utils.createBOMLine(session, itemRev);
            AIFComponentContext[] context = topBOMLine.getChildren();
            if (context == null || context.length == 0) {
                MessageBox.post(reg.getString("selecErr2.MSG"),
                        reg.getString("Warn.MSG"), MessageBox.WARNING);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        EstablishDialog dlg = new EstablishDialog(session, itemRev);
    }


}
