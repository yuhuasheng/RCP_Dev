package com.foxconn.mechanism.mntL5MgfEcnManagement;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.foxconn.mechanism.pacmaterial.PACImportBOMDialog;
import com.foxconn.mechanism.pacmaterial.PACImportBOMLoadingDialog;
import com.foxconn.tcutils.util.TCUtil;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AbstractAIFSession;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.cm.CMSoaHelper;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

public class MNTL5MgfEcnHandler  extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event){
		AbstractAIFUIApplication app = AIFUtility.getCurrentApplication();
		Shell shell = app.getDesktop().getShell();
		TCSession session = (TCSession) app.getSession();
		try {
			String action  = (String) event.getParameters().get("action");
			InterfaceAIFComponent[] targetComponent = app.getTargetComponents();
			if(targetComponent.length > 1) {
				MessageBox.post(shell, "只能選擇一個對象進行操作", "info", MessageBox.INFORMATION);
				return null;
			}
			InterfaceAIFComponent target = targetComponent[0];
			TCComponentItemRevision itemRev;
			if(target instanceof TCComponentBOMLine) {
				itemRev = ((TCComponentBOMLine) target).getItemRevision();
			}else if(target instanceof TCComponentItemRevision) {
				itemRev = (TCComponentItemRevision) target;
			}else {
				MessageBox.post(shell, "只能選擇版本對象進行操作", "info", MessageBox.INFORMATION);
				return null;
			}
			TCUtil.setBypass(session);
			if("create".equals(action)) {
				if(!TCUtil.isReleased(itemRev)) {
					TCComponent[] executeQuery = TCUtil.executeQuery(session, "__D9_Find_L5PartRel_ECN", new String[] { "D9_L5_PartRevision:CMHasSolutionItem.D9_L5_Part:items_tag.item_id" }, new String[]{itemRev.getProperty("item_id")});
					if(executeQuery.length!=0) {
						TCComponent tcComponent = executeQuery[0];
						String itemId = tcComponent.getProperty("item_id");
						String objectName = tcComponent.getProperty("object_name");
						MessageBox.post(shell, "該對象已在ECN:"+itemId+"/"+objectName+"objectName的解決方案下，請查看！", "info", MessageBox.INFORMATION);
						return null;
					}else {
						// 未發佈，創建ECN
						new CreateEcnDialog(shell,itemRev,session);
					}
				}else {
					// 已發佈，創建ECN
					new CreateEcnDialog(shell,itemRev,session);
				}
			}else {
				// 添加現有ECN
				if(!TCUtil.isReleased(itemRev)) {
					MessageBox.post(shell, "所選對象未發佈，請直接修改", "info", MessageBox.INFORMATION);
					return null;
				}else {
					new RelateExsitingECNDialog(shell,itemRev,session);					
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			MessageBox.post(shell, e.toString(), "ERR", MessageBox.ERROR);
		}finally {
			TCUtil.closeBypass(session);			
		}
		return null;
	}

}
