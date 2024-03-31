package com.foxconn.electronics.fw.action;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.foxconn.tcutils.progress.ProgressBarThread;
import com.foxconn.tcutils.util.CommonTools;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.Registry;

public class RelationFWAction extends AbstractAIFAction {

	private AbstractAIFApplication app = null;
	private TCSession session = null;
	private static final String D9_FWDESIGNREVISION = "D9_FWDesignRevision";	
	private static final String D9_VIRTUALPARTREVISION = "D9_VirtualPartRevision";
	private Registry reg = null;
	private ProgressBarThread barThread;
	
	public RelationFWAction(AbstractAIFApplication arg0, Frame arg1, String arg2) {
		super(arg0, arg1, arg2);
		this.app = arg0;
		this.session = (TCSession) app.getSession();
		reg = Registry.getRegistry("com.foxconn.electronics.fw.fw");
	}

	@Override
	public void run() {
		try {
			barThread = new ProgressBarThread(reg.getString("Wait.MSG"), reg.getString("Relation.TITLE") + reg.getString("Relation.MSG"));
			InterfaceAIFComponent[] targetComponents = app.getTargetComponents();
			List<TCComponentItemRevision> FWList = new ArrayList<TCComponentItemRevision>();
			List<TCComponentItemRevision> virtualPartList = new ArrayList<TCComponentItemRevision>();
			sortItemRevType(targetComponents, FWList, virtualPartList); // 分类对象版本类型
			
			if (CommonTools.isEmpty(FWList)) {
				TCUtil.warningMsgBox(reg.getString("SelectWarn1.MSG"), reg.getString("WARNING.MSG"));
				return;
			}
			
			FWList = FWList.stream().filter(CommonTools.distinctByKey(TCComponentItemRevision::getUid)).collect(Collectors.toList());
			
			if (CommonTools.isEmpty(virtualPartList)) {
				TCUtil.warningMsgBox(reg.getString("VirtualPartWarn.MSG"), reg.getString("WARNING.MSG"));
				return;
			}			
			
			virtualPartList = virtualPartList.stream().filter(CommonTools.distinctByKey(TCComponentItemRevision::getUid)).collect(Collectors.toList());			
			if (virtualPartList.size() > 1) {
				TCUtil.warningMsgBox(reg.getString("SelectWarn2.MSG"), reg.getString("WARNING.MSG"));
				return;
			}			
			
			barThread.start();
			TCUtil.setBypass(session); // 开启旁路
			
			TCComponentItemRevision virtualitemRev = virtualPartList.get(0);
			String virtualItemId = virtualitemRev.getProperty("item_id");
			System.out.println("==>> virtualItemId: " + virtualItemId);
			String virtualVersion = virtualitemRev.getProperty("item_revision_id");
			System.out.println("==>> virtualVersion: " + virtualVersion);
			
			
			for (TCComponentItemRevision FWItemRev : FWList) {
				FWItemRev.refresh();
				String FWItemId = FWItemRev.getProperty("item_id");
				System.out.println("==>> FWItemId: " + FWItemId);
				String FWVersion = FWItemRev.getProperty("item_revision_id");
				System.out.println("==>> FWVersion: " + FWVersion);
				try {
					virtualitemRev.add("TC_Is_Represented_By", FWItemRev);
					System.out.println("==>> ME图号为: " + FWItemId + ", 版本号为: " + FWVersion + ", " + "虚拟阶图号为: " + virtualItemId + ", 版本号为: " + virtualVersion + ", 关联成功");
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}				
			}		

			
			TCUtil.infoMsgBox(reg.getString("RelationSuccess.MSG"), reg.getString("INFORMATION.MSG"));			
		} catch (Exception e) {
			e.printStackTrace();
			TCUtil.errorMsgBox(e.getLocalizedMessage(), reg.getString("ERROR.MSG"));	
		} finally {			
			barThread.stopBar();
			TCUtil.closeBypass(session);
		}		
	}
	
	/**
	 * 分类对象版本类型
	 * @param targetComponents
	 * @param MEList
	 * @param virtualPartList
	 */
	private void sortItemRevType(InterfaceAIFComponent[] targetComponents, List<TCComponentItemRevision> FWList, List<TCComponentItemRevision> virtualPartList) {
		Stream.of(targetComponents).forEach(componment -> {
			if (componment instanceof TCComponentItemRevision) {				
				TCComponentItemRevision itemRev = (TCComponentItemRevision) componment;
				String objectType = itemRev.getTypeObject().getName();
				if (objectType.equalsIgnoreCase(D9_FWDESIGNREVISION)) {
					FWList.add(itemRev);
				} else if (objectType.equalsIgnoreCase(D9_VIRTUALPARTREVISION)) {
					virtualPartList.add(itemRev);
				}
			}
		});
	}

	
	/**
	 * 判断是否存在
	 * @param objs
	 * @param component
	 * @return
	 */
	private boolean checkExist(TCComponent[] objs, TCComponent component) {
		if (CommonTools.isEmpty(objs)) {
			return false;
		}
		return Stream.of(objs).anyMatch(obj -> obj.getUid().equals(component.getUid()));
	}
}
