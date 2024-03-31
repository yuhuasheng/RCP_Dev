package com.foxconn.electronics.matrixbom.handler;

import java.awt.Dimension;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.electronics.matrixbom.controller.MatrixBOMController;
import com.foxconn.electronics.matrixbom.controller.ProductLineBOMController;
import com.foxconn.electronics.matrixbom.controller.VariablesBOMController;
import com.foxconn.electronics.matrixbom.dialog.MatrixBOMDialog;
import com.foxconn.electronics.pamatrixbom.controller.PAMatrixBOMController;
import com.foxconn.electronics.pamatrixbom.controller.PAProductLineBOMController;
import com.foxconn.electronics.pamatrixbom.controller.PAVariablesBOMController;
import com.foxconn.electronics.pamatrixbom.dialog.PAMatrixBOMDialog;
import com.foxconn.tcutils.util.TCUtil;
import com.plm.tc.httpService.TCHttp;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.ui.common.RACUIUtil;

public class MatrixBOMHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		Shell shell = app.getDesktop().getShell();
		Dimension dim = app.getDesktop().getSize();
		try {
			String matrixType = app.getTargetComponent().getProperty("d9_MatrixType");
			if("MNT Packing Matrix".equals(matrixType)) {
				TCHttp http = TCHttp.startJHttp(8322);
				PAMatrixBOMController matrixBOMController = new PAMatrixBOMController(app);
				http.addHttpController(matrixBOMController);
				PAVariablesBOMController variablesBOMController = new PAVariablesBOMController(app);
				http.addHttpController(variablesBOMController);
				PAProductLineBOMController productLineBOMController = new PAProductLineBOMController(app);
				http.addHttpController(productLineBOMController);
				
				String preference = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
				String url = preference + "/MNTMatrixBOM?port=" + http.getPort()+"&uid="+app.getTargetComponent().getUid();
				//String url = "http://10.205.56.219:3000/MNTMatrixBOM?port="+ http.getPort() +"&uid="+app.getTargetComponent().getUid();
				System.out.println("==>> PAURL: " + url);
				new PAMatrixBOMDialog(shell, dim, url, matrixBOMController);
				
			} else {
				TCHttp http = TCHttp.startJHttp();
				MatrixBOMController matrixBOMController = new MatrixBOMController(app);
				http.addHttpController(matrixBOMController);
				VariablesBOMController variablesBOMController = new VariablesBOMController(app);
				http.addHttpController(variablesBOMController);
				http.addHttpController(new ProductLineBOMController(app));
				String preference = TCUtil.getPreference(RACUIUtil.getTCSession(), TCPreferenceService.TC_preference_site, "D9_SpringCloud_URL");
				String url = preference + "/MatrixBOM?port=" + http.getPort()+"&uid="+app.getTargetComponent().getUid();
				System.out.println("==>> url: " + url);
				new MatrixBOMDialog(shell, dim, url, matrixBOMController);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
