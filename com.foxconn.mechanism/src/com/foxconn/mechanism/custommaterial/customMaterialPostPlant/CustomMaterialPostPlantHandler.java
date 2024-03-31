package com.foxconn.mechanism.custommaterial.customMaterialPostPlant;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;
/**
 * 
 * @author wt00110
 *    物料轉廠
 */
public class CustomMaterialPostPlantHandler extends AbstractHandler {
	AbstractAIFUIApplication app = null;
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {	
		Registry reg = null;
		app = AIFUtility.getCurrentApplication();
		reg = Registry.getRegistry("com.foxconn.mechanism.custommaterial.customMaterialPostPlant.customMaterialPostPlant");
		InterfaceAIFComponent[] aifComs = app.getTargetComponents();
		List<TCComponentItemRevision> itemRevs=new ArrayList<TCComponentItemRevision>();
		for(InterfaceAIFComponent t:aifComs) {
			if(t instanceof TCComponentItemRevision ) {
				itemRevs.add((TCComponentItemRevision)t);
			}
		}
		if(itemRevs==null||itemRevs.size()<=0||itemRevs.size()>1) {
			MessageBox.post(AIFUtility.getActiveDesktop(), reg.getString("select.item"), "Error", MessageBox.ERROR);
		    return null;
		}
		new InputInfoDialog(app,new Shell(),reg,itemRevs.get(0));
		return null;
	}
	
	
}
