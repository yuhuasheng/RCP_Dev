package com.hh.tools.dashboard.action;

import com.hh.tools.dashboard.dialog.DashboardDialog;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.kernel.TCSession;

public class DashboardAction extends AbstractAIFAction {

    private AbstractAIFApplication app = null;
    private TCSession session = null;

    public DashboardAction(AbstractAIFApplication arg0, String arg1) {
        super(arg0, arg1);
        this.app = arg0;
        this.session = (TCSession) this.app.getSession();
    }

    @Override
    public void run() {
        try {
            new DashboardDialog(this.app, this.session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
