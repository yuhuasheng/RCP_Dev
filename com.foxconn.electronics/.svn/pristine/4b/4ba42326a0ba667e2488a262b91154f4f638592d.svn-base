package com.foxconn.electronics.document;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.util.TCUtil;
import com.plm.tc.httpService.TCHttp;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

public class DocPlaceOnFileHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		try {
			AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();	
			TCSession session = (TCSession) app.getSession();
			Shell shell = app.getDesktop().getShell();
			TCHttp http = TCHttp.startJHttp();			
	        http.addHttpController(new DocPlaceOnFileController(app));
	        String preference = TCUtil.getPreference(session, 4, "D9_SpringCloud_URL");
			String url = preference + "/AutoArchiving?port=" + http.getPort();
			System.out.println(url);
	        new DocPlaceOnFileDialog(shell, app, url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
