package com.hh.tools.cm;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.cm.Messages;
import com.teamcenter.rac.cm.handlers.wizards.NewCCHandler;
import com.teamcenter.rac.cm.wizards.NewChangeModel;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.commands.create.bo.NewBOModel;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class NewCCHandle extends NewCCHandler {
    private Registry reg = Registry.getRegistry("com.hh.tools.cm.newCC");

    public Object execute(ExecutionEvent paramExecutionEvent) throws ExecutionException {
    	//检查是否具有写权限
    	System.out.println("检查是否具有写权限");
        TCSession session = (TCSession) AIFUtility.getCurrentApplication().getSession();
        InterfaceAIFComponent[] coms = AIFUtility.getCurrentApplication().getTargetComponents();
        try {
            if (coms != null && coms.length > 0) {
                for (InterfaceAIFComponent interfaceAIFComponent : coms) {
                    TCComponentItemRevision itemRev = (TCComponentItemRevision) interfaceAIFComponent;
                    boolean flag = Utils.hasWritePrivilege(session, itemRev.getItem());
                    if (!flag) {
//						Utils.infoMessage("您没有权限修订\""+itemRev.toDisplayString()+"\"数据！");
                        MessageBox.post(reg.getString("selecErr1.MSG") + itemRev.toDisplayString() + reg.getString("selecErr2.MSG"),
                                reg.getString("Warn.MSG"), MessageBox.WARNING);


                        return false;
                    }
                }
            }
//			List<TCComponentItemRevision> list = new ArrayList<>();
//			HashMap map = new GetPreferenceUtil().getHashMapPreference(session, TCPreferenceService.TC_preference_site, "CUST_ChangeNotice_Create", "=");
//			for (InterfaceAIFComponent tccomponent : coms) {	
//				String selectedType = tccomponent.getType();
//				Iterator<Entry<String, String>> it = map.entrySet().iterator();
//				while (it.hasNext()) {
//					Entry<String, String> entry = it.next();
//					String type = entry.getKey();
//					if(selectedType.equals(type)){
//						list.add((TCComponentItemRevision)tccomponent);
//					}
//				}
//			}			
//			if(list.size()>0){
//				for (TCComponentItemRevision tcComponentItemRevision : list) {
//					String type = tcComponentItemRevision.getType();
//					String revId = tcComponentItemRevision.getProperty("item_revision_id");
//					Iterator<Entry<String, String>> it = map.entrySet().iterator();
//					while (it.hasNext()) {
//						Entry<String, String> entry = it.next();
//						String key = entry.getKey();							
//						if(type.equals(key)){
//							String value = entry.getValue();
//							if(!revId.equals(value)){
//								Utils.infoMessage("选中对象版本不为"+value+"版，无法新建关联的变更!");
//								return false;
//							}
//						}
//						
//					}
//				}
//			}			
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.wizardId = "com.teamcenter.rac.cm.wizards.context.NewCCWizard";
        return super.execute(paramExecutionEvent);
    }

    public String getWizardTitle() {
        return Messages.getString("wizards.NewCC.TITLE");
    }

    protected NewBOModel getBOModel() {
        return new NewChangeModel(getWizard());
    }


}
