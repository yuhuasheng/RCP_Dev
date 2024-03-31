package com.teamcenter.rac.pse.dialogs;

import java.awt.Frame;
import java.util.HashMap;

import javax.swing.plaf.synth.SynthSplitPaneUI;

import com.hh.fx.rewrite.util.CheckUtil;
import com.hh.fx.rewrite.util.GetPreferenceUtil;
import com.hh.fx.rewrite.util.Utils;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentProcess;
import com.teamcenter.rac.kernel.TCComponentTask;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCProperty;

public class MyANoteDialog extends ANoteDialog{

	private String s1 = "";
	private String itemType = "";
	private TCComponentBOMLine bomline = null;
	public MyANoteDialog(Frame frame, TCComponentBOMLine tccomponentbomline, String s, String s1) {
		super(frame, tccomponentbomline, s, s1);
		this.s1 = s1;
		this.bomline = tccomponentbomline;
		System.out.println("s ==" + s);
		System.out.println("s1 ==" + s1);
		try {
			itemType = tccomponentbomline.getItemRevision().getType();
			System.out.println("itemType ==" +  itemType);
		} catch (TCException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		System.out.println("run");
		try {
			System.out.println("saveDisplayParameters");
			GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();
			HashMap<String, String> map = null;
			map = getPreferenceUtil.getHashMapPreference(Utils.getTCSession(),
					TCPreferenceService.TC_preference_site, "FX8_BOMAnnotationMaintain", "=");
			
//			String userName = map.get("UserName");
//			String[] userNames = null;
//			if (userName.contains(",")) {
//				userNames = userName.split(",");
//			} else {
//				userNames = new String[] { userName };
//			}
//			String processName = map.get("ProcessName");
//			String annotation = map.get("Annotation");
//			String processNoteName = map.get("ProcessNoteName");
//			System.out.println("userName ==" + userName);
//			System.out.println("processName ==" + processName);
//			System.out.println("annotation ==" + annotation);
//			System.out.println("processNoteName ==" + processNoteName);
			String loginUserName = Utils.getTCSession().getUserName();
			System.out.println("loginUserName ==" + loginUserName);
			TCComponentBOMLine topBOMline = null;
			topBOMline = bomline.getCachedWindow().getTopBOMLine();
			System.out.println("topBOMline ==" + topBOMline);
			TCComponentItemRevision itemRev = topBOMline.getItemRevision();
			AIFComponentContext[] context = itemRev.getPrimary();
			TCComponentTask task = null;
			for (int i = 0; i < context.length; i++) {
				System.out.println("context ==" + context[i]);
				TCComponent comp = (TCComponent) context[i].getComponent();
				if (comp != null && (comp.isTypeOf("EPMTask"))) {
					task = (TCComponentTask) comp;
					break;
				}

			}

			
//			boolean flag = false;
//			boolean loginFlag = false;
//			for (int i = 0; i < userNames.length; i++) {
//				String name = userNames[i];
//				System.out.println("==权限用户=" + name);
//				if(name.equals(loginUserName)){
//					loginFlag = true;
//				}
//			}
//			if (task != null) {
//				if (s1.equals(annotation)) {
//					flag = true;
//				}
//			}
			if (map != null && map.get(s1) != null) {
				System.out.println("======");
				CheckUtil.setByPass(true, Utils.getTCSession());
				super.run();
				CheckUtil.setByPass(false, Utils.getTCSession());
				bomline.getCachedWindow().save();
			} else {
				System.out.println("===else====");
				super.run();
			}
		} catch (TCException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void okAction() {
		System.out.println("okAction");
		super.okAction();
	}

	

	@Override
	public String getPersistentDisplayCookieFileName() {
		// TODO Auto-generated method stub
		return super.getPersistentDisplayCookieFileName();
	}

	@Override
	protected void saveDisplayParameters() {
		System.out.println("saveDisplayParameters");
		super.saveDisplayParameters();
	}

	@Override
	protected void okApplyAction() {
		System.out.println("okApplyAction");
		super.okApplyAction();
	}

	@Override
	protected void applyAction() {
		System.out.println("applyAction");
		super.applyAction();
	}
	
	
}
