package com.foxconn.mechanism.integrate.hhpnIntegrate;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentPseudoFolder;
import com.teamcenter.rac.util.Registry;
/**
 * 
 * @author wt00110
 *    料号集成 菜单入口
 */
public class HHPNIntegrateHandler extends AbstractHandler {
	AbstractAIFUIApplication app = null;
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {	
		Registry reg = null;
		app = AIFUtility.getCurrentApplication();
		TCComponent tCComponentFolder=null;
	
		reg = Registry.getRegistry("com.foxconn.mechanism.integrate.hhpnIntegrate.hhpnintegrate");
		InterfaceAIFComponent targetComponent = app.getTargetComponent();
		if(targetComponent!=null) {
			if(targetComponent instanceof TCComponent) {
				tCComponentFolder=(TCComponent)targetComponent;
			}
			
		}
		
		new InputInfoDialog(tCComponentFolder,app,AIFDesktop.getActiveDesktop().getShell(),reg);
		return null;
	}
	
	
}
