package com.foxconn.electronics.benefitreport;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.ui.common.RACUIUtil;

/**
 * 
 * @author Robert
 *
 */
public class BenefitReportHandler extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException
    {
        AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
        try
        {
             String url = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL") + "/BenefitStatements";
//            String url = "http://10.203.163.43:8022/BenefitStatements";
//            String url = "http://10.203.65.85:8022/BenefitStatements";
            System.out.println("benefit report  url :: " + url);
            new BenefitReportDialog(app, app.getDesktop().getShell(), url);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
