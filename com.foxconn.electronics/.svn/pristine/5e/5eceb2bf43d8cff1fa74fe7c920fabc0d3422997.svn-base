package com.foxconn.electronics.dgkpi;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.managementebom.ManagementEBOMDialog;
import com.foxconn.electronics.util.TCUtil;
import com.plm.tc.httpService.TCHttp;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.rac.ui.common.RACUIUtil;
import com.teamcenter.rac.util.Registry;

/**
 * 
 * @author Robert
 *
 */
public class DGKPIHandler extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException
    {
        Registry reg = Registry.getRegistry("com.foxconn.electronics.dgkpi.dgkpi");
        AbstractPSEApplication app = (AbstractPSEApplication) AIFUtility.getCurrentApplication();
        Shell shell = app.getDesktop().getShell();
        try
        {
            if (app.getBOMWindow() == null)
            {
                MessageDialog.openWarning(shell, "Warn", reg.getString("selectError"));
                return null;
            }
            TCSession session = (TCSession) app.getSession();
            TCComponentBOMWindow bomWindow = app.getBOMWindow();
            TCHttp http = TCHttp.startJHttp();
         
            http.addHttpController(new UpActualUserController(bomWindow,session));
            String preference = TCUtil.getPreference(session, TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
			String url = preference + "/ModifyAuthorsAndProjects?port=" + http.getPort();
            KPIDialog dialog = new KPIDialog(app, app.getDesktop().getShell(), reg, url);
            
            
         }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
