package com.secondsource;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.d9.services.rac.cust.D9FoxconnSoaCustService;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;

public class SecondSourceHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		System.out.println("==>> SecondSourceHandler");
		AbstractPSEApplication app =  (AbstractPSEApplication) AIFUtility.getCurrentApplication();
		TCSession session = app.getSession();
		InterfaceAIFComponent aif = app.getTargetComponent();
		TCComponentBOMLine topBomLine = (TCComponentBOMLine) aif;
		String uid = topBomLine.getUid();
		D9FoxconnSoaCustService service = D9FoxconnSoaCustService.getService(session);
		
//		D9FoxconnSoaCustService d9Service = D9FoxconnSoaCustService.getService(session.getSoaConnection());		
		String result = service.callAwcMethod("get2ndSourceStruct", uid);
		System.out.println(result);
		
		return null;
	}

}
