package com.foxconn.mechanism.digitalsignature;

import java.awt.Frame;

import com.foxconn.mechanism.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.handlers.DigitalSigningHandler;
import com.teamcenter.rac.common.services.DocumentManagementService;
import com.teamcenter.rac.common.services.util.AppLauncherServiceHelper;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.services.internal.rac.core.ICTService;
import com.teamcenter.services.internal.rac.core._2011_06.ICT.Arg;
import com.teamcenter.services.internal.rac.core._2011_06.ICT.InvokeICTMethodResponse;
import com.teamcenter.services.rac.core.DataManagementService;
//import com.teamcenter.services.rac.documentmanagement.LaunchDefinitionService;
//import com.teamcenter.services.rac.documentmanagement._2010_04.LaunchDefinition.LDSelectedInputInfo;
//import com.teamcenter.services.rac.documentmanagement._2010_04.LaunchDefinition.LaunchDefinitionResponse;
//import com.teamcenter.services.rac.documentmanagement._2010_04.LaunchDefinition.ServerInfo;
//import com.teamcenter.services.rac.documentmanagement._2010_04.LaunchDefinition.SessionInfo;
//import com.teamcenter.services.rac.documentmanagement._2010_04.LaunchDefinition.UserAgentDataInfo;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.services.internal.rac.core._2011_06.ICT.Structure;
import com.teamcenter.services.internal.rac.core._2011_06.ICT.Array;
import com.teamcenter.services.internal.rac.core._2011_06.ICT.Entry;
public class DigitalSignatureAction extends AbstractAIFAction {

	private AbstractAIFApplication app = null;
	private TCSession session = null;
	
	public DigitalSignatureAction(AbstractAIFApplication arg0, Frame arg1, String arg2) {
		super(arg0, arg1, arg2);
		this.app = arg0;
		this.session = (TCSession) app.getSession();
	
	}
	
	@Override
	public void run() {
		try {	
			TCComponentQueryType imancomponentquerytype = (TCComponentQueryType) session.getTypeComponent("ImanQuery");
			TCComponentQuery query = (TCComponentQuery) imancomponentquerytype.find("__D9_Find_Project");
			TCQueryClause[] elements = query.describe();
		
			for (TCQueryClause element : elements) {
			  System.out.println("queryAttributeDisplayNames:" + element.getUserEntryNameDisplay());
			
			}
				
			
			//获取选中的目标对象
			/*InterfaceAIFComponent aif = app.getTargetComponent();
			InterfaceAIFComponent com = aif;
		
			TCComponentDataset dataset = (TCComponentDataset) com;		
			String str = AppLauncherServiceHelper.getInstance().digitalSignWithAppLauncher(new TCComponentDataset[] {dataset});
			DocumentManagementService doc=new DocumentManagementService();
			   */      
		   /*
			LDSelectedInputInfo[] selectedInputInfos = new LDSelectedInputInfo[1];
			LDSelectedInputInfo selectedInputInfo= new LDSelectedInputInfo();
				 
			selectedInputInfo.id=dataset; 
		    selectedInputInfo.requestMode="SIGN";
		     
		    ServerInfo serverInfo=  new ServerInfo();
		    serverInfo.hostPath="http://10.203.163.245:7001/tc";		
		    
		    selectedInputInfos[0]=selectedInputInfo;
		    LaunchDefinitionService launchDefinitionService=LaunchDefinitionService.getService(session);
		    LaunchDefinitionResponse respose=launchDefinitionService.getLaunchDefinition("DigitalSign", selectedInputInfos, serverInfo, new SessionInfo (), new UserAgentDataInfo());
		    System.out.print(respose);
		    */
		      
		    
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

}
