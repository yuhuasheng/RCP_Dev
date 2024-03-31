package com.hh.tools.l5Change.FBECN;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import com.hh.tools.newitem.CheckUtil;
import com.hh.tools.newitem.ItemTypeName;
import com.hh.tools.newitem.RelationName;
import com.hh.tools.newitem.Utils;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.common.actions.AbstractAIFAction;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.common.create.BOCreateDefinitionFactory;
import com.teamcenter.rac.common.create.CreateInstanceInput;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.common.create.SOAGenericCreateHelper;
import com.teamcenter.rac.kernel.NamedReferenceContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinition;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinitionType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Registry;

public class CreateAction extends AbstractAIFAction {
    private AbstractAIFUIApplication app = null;
    private TCSession session = null;
    private Registry reg = null;

    public CreateAction(AbstractAIFUIApplication arg0, Frame arg1, String arg2) {
        super(arg0, arg1, arg2);
        this.app = arg0;
        this.session = (TCSession) this.app.getSession();
        this.reg = Registry.getRegistry("com.hh.tools.l5Change.FBECN.I5Change");

    }

    @Override
    public void run() {
        InterfaceAIFComponent[] aifComponents = this.app.getTargetComponents();
        TCComponentItemRevision itemRev = null;
        try {
            if (aifComponents != null && aifComponents.length > 0) {
                if (aifComponents.length > 1) {
                    MessageBox.post(reg.getString("selecErr1.MSG"),
                            reg.getString("Warn.MSG"), MessageBox.WARNING);
                    return;
                } else {
                    boolean flag = false;
                    String type = aifComponents[0].getType();
                    if (ItemTypeName.L5ZZPROCESSREVISION.equals(type) || ItemTypeName.L5TZPROCESSREVISION.equals(type)
                            || ItemTypeName.L5CYPROCESSREVISION.equals(type) || ItemTypeName.L5CXPROCESSREVISION.equals(type)
                            || ItemTypeName.L5JYPROCESSREVISION.equals(type)) {
                        flag = true;
                        itemRev = (TCComponentItemRevision) aifComponents[0];
                    }
                    if (!flag) {
                        MessageBox.post(reg.getString("selecErr1.MSG"),
                                reg.getString("Warn.MSG"), MessageBox.WARNING);
                        return;
                    }
                }
            } else {
                MessageBox.post(reg.getString("selecErr1.MSG"),
                        reg.getString("Warn.MSG"), MessageBox.WARNING);
                return;
            }
            String itemRevId = itemRev.getProperty("item_revision_id");
            if (!itemRevId.toUpperCase().startsWith("X")) {
                MessageBox.post(reg.getString("selecErr2.MSG"),
                        reg.getString("Warn.MSG"), MessageBox.WARNING);
                return;
            }
            Object[] options = {reg.getString("yes.MSG"), reg.getString("no.MSG")};
            int m = JOptionPane.showOptionDialog(null, reg.getString("confirm.MSG"), "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (m == JOptionPane.YES_OPTION) {
                Utils.byPass(true);
//				TCComponentItemType itemType  = (TCComponentItemType)session.getTypeComponent(ItemTypeName.L5FBECN);
//				String itemId = itemType.getNewID();
                String itemName = itemRev.getProperty("item_id")+"_"+itemRev.getProperty("object_name")+"_发布更改";
//				TCComponentItem ECNItem = CreateObject.createItem(session, itemId, itemName, "A", ItemTypeName.L5FBECN);
				TCComponentItem ECNItem = createCom(session, ItemTypeName.PROCRELEASE, itemName, itemRev.getProperty("object_name")+"_发布更改");
//				TCComponentItemRevision ECNItemRev = ECNItem.getLatestItemRevision();
                ECNItem.add(RelationName.CHANGEBEFORE, itemRev);
                TCComponentItemRevision newItemRev = itemRev.saveAs("A01");
                Utils.removePDFFromRev(newItemRev, session, "PDF_Reference");
                newItemRev.add(RelationName.CHANGELIST, ECNItem);
                ECNItem.add(RelationName.CHANGEAFTER, newItemRev);
                Utils.byPass(false);
                Utils.setItem2Home(ECNItem);
                MessageBox.post(ECNItem.getProperty("object_name") + reg.getString("createInfo.MSG"),
                        reg.getString("Info.MSG"), MessageBox.INFORMATION);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private TCComponentItem createCom(TCSession session, String itemType, String objectName, String objectDesc) {
        IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session, itemType);
        CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);
        TCComponentItem obj = null;
        try {
            TCComponentItemType type = (TCComponentItemType) session.getTypeComponent(itemType);
            String itemID = type.getNewID();

            createInstanceInput.add("item_id", itemID);
            createInstanceInput.add("object_name", objectName);
            createInstanceInput.add("object_desc", objectDesc);
            IBOCreateDefinition createDefinitionRev = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session, itemType + "Revision");
            CreateInstanceInput createInstanceInputRev = new CreateInstanceInput(createDefinitionRev);

            ArrayList iputList = new ArrayList();

            createInstanceInputRev.add("item_revision_id", "A");
            createInstanceInputRev.add("object_name", objectName);
            createInstanceInputRev.add("object_desc", objectDesc);
            iputList.add(createInstanceInput);

            ArrayList list = new ArrayList(0);
            list.addAll(iputList);

            createInstanceInput.addSecondaryCreateInput(createDefinition.REVISION, createInstanceInputRev);

            List comps = SOAGenericCreateHelper.create(session, createDefinition, list);
            obj = (TCComponentItem) comps.get(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

}
