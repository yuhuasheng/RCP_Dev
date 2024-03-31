package com.foxconn.electronics.managementebom.rfqbom;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.managementebom.ManagementEBOMDialog;
import com.foxconn.electronics.managementebom.createDCN.DCNController;
import com.foxconn.electronics.managementebom.secondsource.controller.Sync2ndsourceController;
import com.foxconn.electronics.managementebom.updatebom.controller.UpdateEBOMController;
import com.foxconn.electronics.util.TCUtil;
import com.plm.tc.httpService.TCHttp;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.Registry;

public class RrqBOMHandler extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException
    {
        Registry reg = Registry.getRegistry("com.foxconn.electronics.managementebom.managementebom");
        AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
        Shell shell = app.getDesktop().getShell();
        try
        {
            String springUrl = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
            String url = springUrl + "/RFQEBOM";
            System.out.println("ebom url :: " + url);
            RrqBOMDialog dialog = new RrqBOMDialog(app, app.getDesktop().getShell(), reg, url);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
