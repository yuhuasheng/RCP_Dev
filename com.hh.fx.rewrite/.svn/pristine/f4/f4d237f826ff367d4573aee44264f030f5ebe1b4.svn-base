package com.teamcenter.rac.workflow.commands.newprocess;

import com.hh.fx.rewrite.util.FileStreamUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentTaskTemplate;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

public class UserNewProcessDialog extends NewProcessDialog {
	private TCSession session = null;
	private FileStreamUtil fileStreamUtil = new FileStreamUtil();
	private PrintStream printStream = null;
	Registry reg = null;
	TCComponentTaskTemplate taskTemplate = null;
	private TCComponent target;
	AbstractAIFUIApplication app;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public UserNewProcessDialog(NewProcessCommand paramNewProcessCommand) {
		super(paramNewProcessCommand);

		System.out.println("UserNewProcessDialog");
		this.session = ((TCSession) AIFUtility.getCurrentApplication().getSession());
		String logFile = this.fileStreamUtil.getTempPath("UserNewProcessDialog");
		this.printStream = this.fileStreamUtil.openStream(logFile);
		this.app = AIFUtility.getCurrentApplication();
		this.target = ((TCComponent) this.app.getTargetComponent());
		System.out.println("paramNewProcessCommand ==" + paramNewProcessCommand);
		setProcessNameText();
	}

	@Override
	public void startCommandOperation() {
		// TODO Auto-generated method stub
		System.out.println("startCommandOperation");
		super.startCommandOperation();
	}

	private void setProcessNameText() {
		try {
			System.out.println("==target===" + this.target);
			String name = "";
			for (int i = 0; i < this.pasteTargets.length; i++) {
				System.out.println("pasteTargets ==" + this.pasteTargets[i]);
				if(pasteTargets[i] instanceof TCComponentItemRevision){
					TCComponentItemRevision rev = (TCComponentItemRevision) this.pasteTargets[i];
					name = name + "/" + rev.getItem().toString();
				}
			}
			System.out.println("pasteTargets ==" + this.pasteTargets[0]);
			if ((this.target.getType().equals("FX8_ManagerItemRevision"))
					&& (this.pasteTargets[0].getType().equals("EDAComp Revision"))) {
				this.processNameTextField.setText(name);
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
	}

	public TCComponent getCreatorTask() {
		System.out.println("super.getCreatorTask() ==" + super.getCreatorTask());
		return super.getCreatorTask();
	}

	public void getProcListAll() {
		super.getProcListAll();
	}

	public String getProcessDescription() {
		System.out.println("super.getProcessDescription() ==" + super.getProcessDescription());
		return super.getProcessDescription();
	}

	public String getProcessName() {
		System.out.println("super.getProcessName() ==" + super.getProcessName());
		return super.getProcessName();
	}

	public Object getProcessTemplate() {
		System.out.println("super.getProcessTemplate() ==" + super.getProcessTemplate());
		return super.getProcessTemplate();
	}
}
