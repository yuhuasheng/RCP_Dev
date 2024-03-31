package com.foxconn.electronics.managementebom;

import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.UIUtilities;

public class ManagementEBOMAction extends AbstractAIFAction
{
    private AbstractPSEApplication app;
    private Registry               reg;
    private String                 url;

    public ManagementEBOMAction(AbstractPSEApplication var1, Registry var2, String var3)
    {
        super(var1, var2, "");
        this.app = var1;
        this.reg = var2;
        this.url = var3;
    }

    @Override
    public void run()
    {
        ManagementEBOMDialog dialog = new ManagementEBOMDialog(app, app.getDesktop().getShell(), reg, url);
        // UIUtilities.enableSwingDialogForSWTModality(dialog);
        // dialog.setVisible(true);
    }
}
