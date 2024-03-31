package com.foxconn.mechanism.digitalsignature;

import java.awt.Frame;

import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.commands.namedreferences.NamedReferencesDialog;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCSession;
//import com.teamcenter.services.internal.rac.documentmanagement.DigitalSignatureService;

public class DigitalSignatureCancleAction extends AbstractAIFAction {

	private AbstractAIFApplication app = null;
	private TCSession session = null;
	
	public DigitalSignatureCancleAction(AbstractAIFApplication arg0, Frame arg1, String arg2) {
		super(arg0, arg1, arg2);
		this.app = arg0;
		this.session = (TCSession) app.getSession();		
	}
	
	@Override
	public void run() {
		try {
			InterfaceAIFComponent aif = app.getTargetComponent();
			InterfaceAIFComponent com = aif;
			TCComponentDataset dataset = (TCComponentDataset) com;
		 
//			DigitalSignatureService s=DigitalSignatureService.getService(session);
//		    s.cancelSign(new TCComponentDataset[] {dataset});
		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
