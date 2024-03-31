package com.hh.tools.report.action;

import java.awt.Frame;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class SearchApprovalSheetAction extends AbstractAIFAction {
    private AbstractAIFUIApplication app = null;
    private Registry reg = Registry.getRegistry("com.hh.tools.report.msg.message");

    public SearchApprovalSheetAction(AbstractAIFUIApplication arg0, Frame arg1, String arg2) {
        super(arg0, arg1, arg2);
        this.app = arg0;
    }

    @Override
    public void run() {
        try {
            InterfaceAIFComponent aifComponent = this.app.getTargetComponent();
            TCComponent targetCom = (TCComponent) aifComponent;

            String approvalSheet = targetCom.getProperty("fx8_Approvalsheet");

            System.out.println("fx8_Approvalsheet == " + approvalSheet);

            if ("".equals(approvalSheet)) {
                throw new Exception(reg.getString("NoUrl.NAME"));
            } else {
                Runtime.getRuntime().exec("explorer.exe " + approvalSheet);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageBox.post(e);
        }

    }

}
