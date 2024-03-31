package com.foxconn.mechanism.mntdcn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class CreatMNTDCNHandler  extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		Shell shell = app.getDesktop().getShell();
		TCSession session = (TCSession) app.getSession();
		try {
			TCUtil.setBypass(session);
			InterfaceAIFComponent[] aifCom = app.getTargetComponents();
			List<TCComponentItemRevision> list = new ArrayList<>();
			// 檢查選擇的必須是未發佈的
			for(int i=0;i<aifCom.length;i++) {
				InterfaceAIFComponent aif = aifCom[i];
				TCComponentItemRevision itemRev;
				if(aif instanceof TCComponentBOMLine) {					
					itemRev = ((TCComponentBOMLine) aif).getItemRevision();
				}else {
					itemRev = (TCComponentItemRevision) aif;
				}
				if(TCUtil.isReleased(itemRev)) {
					MessageBox.post(String.format("選擇的第%d個版本对象已發佈，不能執行操作",i+1), "檢查", MessageBox.INFORMATION);
					return null;
				}
				list.add(itemRev);
			}
			
			new CreateMNTDCNDialog(shell, list, session);
			InterfaceAIFComponent aif = aifCom[0];
			// 刷新結構管理
			if(aif instanceof TCComponentBOMLine) {
				TCComponentBOMLine bomline = (TCComponentBOMLine) aif;
				bomline.window().refresh();		        
			}
		}catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(e.toString(), "ERR", MessageBox.ERROR);
		}finally {
			TCUtil.setBypass(session);
		}
		return null;
	}

}
