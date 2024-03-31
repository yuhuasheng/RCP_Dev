package com.hh.tools.checkList.action;

import java.awt.Frame;

import com.hh.tools.checkList.dialog.CheckListDialog;
import com.hh.tools.newitem.ItemTypeName;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentUser;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.workflow.commands.digitalsign.PerformTaskUtil;

public class CheckListAction extends AbstractAIFAction {
    private AbstractAIFUIApplication app = null;
    private TCSession session = null;
    private Registry reg = null;

    public CheckListAction(AbstractAIFUIApplication arg0, Frame arg1, String arg2) {
        super(arg0, arg1, arg2);
        this.app = arg0;
        this.session = (TCSession) this.app.getSession();
        this.reg = Registry.getRegistry("com.hh.tools.checkList.checkList");
    }

    @Override
    public void run() {
        InterfaceAIFComponent aifComponent = this.app.getTargetComponent();
        if (aifComponent instanceof TCComponentForm) {
            TCComponentForm form = (TCComponentForm) aifComponent;
            String type = form.getType();
            if (ItemTypeName.DGNRVWFORM.equals(type) || ItemTypeName.DGNRELEASEDFORM.equals(type)
                    || ItemTypeName.CUSTOMERRVWFORM.equals(type) || ItemTypeName.SAMPLERVWFORM.equals(type)) {

                CheckListDialog dlg = new CheckListDialog(session, form);
            } else {
                MessageBox.post(AIFUtility.getActiveDesktop(), reg.getString("Error.MSG"), "ERROR", MessageBox.ERROR);
                return;
            }
        }

    }


}
