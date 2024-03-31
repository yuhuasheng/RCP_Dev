package com.teamcenter.rac.workflow.commands.newprocess;

import com.foxconn.decompile.service.CustomPnService;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;
import java.io.PrintStream;

public class UserNewProcessDialog extends UserExtNewProcessDialog {
	private TCSession session = null;
	private PrintStream printStream = null;
	Registry reg = null;
	TCComponentTaskTemplate taskTemplate = null;
	private TCComponent target;
	AbstractAIFUIApplication app;

	public UserNewProcessDialog(NewProcessCommand paramNewProcessCommand) {
		super(paramNewProcessCommand);

		System.out.println("UserNewProcessDialog");
		this.session = ((TCSession) AIFUtility.getCurrentApplication().getSession());
		this.app = AIFUtility.getCurrentApplication();
		this.target = ((TCComponent) this.app.getTargetComponent());
//		System.out.println("paramNewProcessCommand ==" + paramNewProcessCommand);
	}

	@Override
	public void startCommandOperation() {
		// TODO Auto-generated method stub
		System.out.println("startCommandOperation");
		//	try {
		//		getpubMailPanel();
		//	} catch (TCException e) {
		//		// TODO Auto-generated catch block
		//		e.printStackTrace();
		//	}
		super.startCommandOperation();
		
	}

	public TCComponent getCreatorTask() {
//		System.out.println("super.getCreatorTask() ==" + super.getCreatorTask());
		return super.getCreatorTask();
	}

	public void getProcListAll() {
		super.getProcListAll();
	}
	
	@Override
	public void endOperation() {
		super.endOperation();
		String s=getProcessName();
		//自编料号重抛流程
		/*if(s!=null&&s.startsWith("FXN37_MNT")) {
			try {
				InterfaceAIFComponent[] tmps=this.pasteTargets;
				TCComponent[] coms=new TCComponent[tmps.length];
				for(int i=0;i<tmps.length;i++) {
					coms[i]=(TCComponent)tmps[i];
				}
			    new CustomPnService().applyCustomPn(session,coms,null);
			}catch(Exception e) {}
		}*/
	
	}
	public String getProcessDescription() {
//		System.out.println("super.getProcessDescription() ==" + super.getProcessDescription());
		return super.getProcessDescription();
	}

	public String getProcessName() {
//		System.out.println("super.getProcessName() ==" + super.getProcessName());
		return super.getProcessName();
	}

	public Object getProcessTemplate() {
//		System.out.println("super.getProcessTemplate() ==" + super.getProcessTemplate());
		return super.getProcessTemplate();
	}

}
