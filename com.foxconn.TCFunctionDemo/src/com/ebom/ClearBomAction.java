package com.ebom;

import java.awt.Frame;
import java.util.LinkedHashMap;
import java.util.Map;

import com.progress.ProgressBarThread;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.util.TCUtil;

public class ClearBomAction extends AbstractAIFAction {

	private AbstractPSEApplication app = null;
	private TCSession session = null;
	private ProgressBarThread barThread;
	
	public ClearBomAction(AbstractPSEApplication arg0, Frame arg1, String arg2) {
		super(arg0, arg1, arg2);
		this.app = arg0;
		this.session = (TCSession) app.getSession();		
	}

	@Override
	public void run() {
		try {
			barThread = new ProgressBarThread("提示", "正在执行,请稍后...");
			InterfaceAIFComponent aif = app.getTargetComponent();
			TCComponentBOMLine topLine = (TCComponentBOMLine) aif;
			
			barThread.start();
			
			TCUtil.setBypass(session);
			
			clearBomLineProp(topLine);			
			
			TCUtil.infoMsgBox("执行完成", "提示");
			barThread.stopBar();
		} catch (Exception e) {
			e.printStackTrace();
			barThread.stopBar();
			TCUtil.closeBypass(session);
			TCUtil.errorMsgBox("执行失败", "错误");	
		}
	}

	/**
	 * 获取同步替代料群组的BOM结构树
	 * 
	 * @param topLine
	 * @param altGroup
	 * @return
	 * @throws TCException
	 */
	public void clearBomLineProp(TCComponentBOMLine topLine) throws TCException {
		topLine.refresh();
		
		AIFComponentContext[] componmentContext = null;
		boolean hasChildren = topLine.hasChildren();
		if (hasChildren) {
			componmentContext = topLine.getChildren();
		}	
		
		if (componmentContext != null && componmentContext.length > 0) {
			for (AIFComponentContext e : componmentContext) {
				try {
					TCComponentBOMLine bomLine = (TCComponentBOMLine) e.getComponent();
					if (!bomLine.isSubstitute()) {	
						Map<String, String> propMap = new LinkedHashMap<String, String>();
						propMap.put("bl_occ_d9_Side", "");
						propMap.put("bl_occ_d9_Angle", "");
						propMap.put("bl_occ_d9_X_Coordinate", "");
						propMap.put("bl_occ_d9_Y_Coordinate", "");
						
						bomLine.setProperties(propMap);
//						bomLine.setProperty("bl_occ_d9_Side", "");						
//						bomLine.setProperty("bl_occ_d9_Angle", "");						
//						bomLine.setProperty("bl_occ_d9_X_Coordinate", "");						
//						bomLine.setProperty("bl_occ_d9_Y_Coordinate", "");
						clearBomLineProp(bomLine);							
					
					}
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
			}		

		}
	}
	
}
