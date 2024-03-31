package com.foxconn.electronics.managementebom;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.managementebom.createDCN.DCNController;
import com.foxconn.electronics.managementebom.secondsource.controller.Sync2ndsourceController;
import com.foxconn.electronics.managementebom.updatebom.controller.UpdateEBOMController;
import com.foxconn.electronics.util.TCUtil;
import com.plm.tc.httpService.TCHttp;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.Registry;

/**
 * 
 * @author Robert
 *
 */
public class ManagementEBOMHandler2 extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException
    {
        Registry reg = Registry.getRegistry("com.foxconn.electronics.managementebom.managementebom");
        AbstractPSEApplication app = (AbstractPSEApplication) AIFUtility.getCurrentApplication();
        Shell shell = app.getDesktop().getShell();
        try
        {
            if (app.getBOMWindow() == null)
            {
                MessageDialog.openWarning(shell, "Warn", reg.getString("selectError"));
                return null;
            }
            TCComponentBOMWindow bomWindow = app.getBOMWindow();
            TCHttp http = TCHttp.startJHttp();
            http.addHttpController(new UpdateEBOMController(app));
            http.addHttpController(new Sync2ndsourceController());
            http.addHttpController(new DCNController(bomWindow));
            // String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_EBOM_MgrWeb_Url") +
            // http.getPort();
            // String url = "http://10.203.65.85:9000/#/BomHome?port=" + "9001";
            String url = "http://10.203.163.43:9000/#/SingleBom?port=" + http.getPort();
            System.out.println("ebom url :: " + url);
            ManagementEBOMDialog dialog = new ManagementEBOMDialog(app, app.getDesktop().getShell(), reg, url);
            // new Thread(new ManagementEBOMAction(app, reg, url)).start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
