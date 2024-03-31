package com.foxconn.mechanism.dgkpi;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
/**
 * 
 * pdf 導入
 */
public class ImportPdfHandler extends AbstractHandler {
	AbstractAIFUIApplication app = null;
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {	
		    Registry reg = null;
		    app = AIFUtility.getCurrentApplication();
		    reg = Registry.getRegistry("com.foxconn.mechanism.dgkpi.importpdf");
		    InterfaceAIFComponent aifCom = app.getTargetComponent();
	
			if(!(aifCom instanceof TCComponentItemRevision)) {		
				MessageBox.post(AIFUtility.getActiveDesktop(), reg.getString("error.select"), "Error", MessageBox.ERROR);
				return null;
			}
			TCComponentItemRevision itemRevision=(TCComponentItemRevision)aifCom;
			if(!("D9_MEDesignRevision".equalsIgnoreCase(itemRevision.getTypeObject().getName()))) {
				MessageBox.post(AIFUtility.getActiveDesktop(), reg.getString("error.select"), "Error", MessageBox.ERROR);
				return null;
			}
			
			TCSession session = (TCSession) this.app.getSession();
			Shell shell = app.getDesktop().getShell();
			new SelectFileDialog(app,shell,reg,(TCComponentItemRevision)aifCom);
		
		return null;
	}
	
	
}
