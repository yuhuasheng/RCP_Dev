package com.hh.tools.newitem;

import java.util.ArrayList;
import java.util.List;

import com.teamcenter.rac.common.create.BOCreateDefinitionFactory;
import com.teamcenter.rac.common.create.CreateInstanceInput;
import com.teamcenter.rac.common.create.IBOCreateDefinition;
import com.teamcenter.rac.common.create.SOAGenericCreateHelper;
import com.teamcenter.rac.kernel.NamedReferenceContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinition;
import com.teamcenter.rac.kernel.TCComponentDatasetDefinitionType;
import com.teamcenter.rac.kernel.TCComponentDatasetType;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.TCTypeService;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2007_01.DataManagement;

public class CreateObject {

	//创建ITEM
    public static TCComponentItem createItem(TCSession session, String itemId, String itemName,
                                             String itemRev, String itemTypeName) {
        // TODO Auto-generated method stub
        try {

            itemTypeName = itemTypeName.replace("Revision", "");
            TCComponentItemType itemType = (TCComponentItemType) session
                    .getTypeComponent(itemTypeName);
            TCComponentItem item = itemType.create(itemId, itemRev,
                    itemTypeName, itemName, "", null);

            return item;
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    //
    public static void updateDataset(TCSession session, TCComponentDataset dataset, String filePath) {
        try {
            TCComponentDatasetDefinitionType dsdefType = (TCComponentDatasetDefinitionType) session
                    .getTypeComponent("DatasetType");
            TCComponentDatasetDefinition definition = dsdefType
                    .find(dataset.getType());
            NamedReferenceContext[] contexts = definition
                    .getNamedReferenceContexts();
            String namedReference = contexts[0].getNamedReference();
            System.out.println("namedReference==" + namedReference);

            dataset.removeNamedReference(namedReference);

            dataset.setFiles(
                    new String[]{filePath},
                    new String[]{namedReference});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //创建数据集
    public static TCComponentDataset createDataSet(TCSession session, String selectFilepath, String dsType, String dsName, String ref_type) {
        TCComponentDataset dataset = null;
        try {
            TCTypeService type = session.getTypeService();
            TCComponentDatasetType datasettype = (TCComponentDatasetType) type.getTypeComponent("Dataset");
            dataset = datasettype.create(dsName, "", dsType);
            String p[] = new String[1];
            String n[] = new String[1];
            p[0] = selectFilepath;
            System.out.println("selectFilepath == " + selectFilepath);
            n[0] = ref_type;
            System.out.println("ref_type == " + ref_type);
            dataset.setFiles(p, n);
        } catch (TCException e) {
        	MessageBox.post("数据集创建错！", "提示", 2);
            e.printStackTrace();
        }
        return dataset;
    }

    /**
	 * 创建table
	 * @param tableRowName table的名称
	 * @return
	 */
    public static TCComponent createTable(String tableRowName) {
        IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(Utils.getTCSession(),
                tableRowName);
        CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);
        ArrayList iputList = new ArrayList();
        iputList.add(createInstanceInput);
        TCComponent obj = null;
        List comps = null;
        try {
            comps = SOAGenericCreateHelper.create(Utils.getTCSession(), createDefinition, iputList);
            obj = (TCComponent) comps.get(0);
        } catch (TCException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static TCComponent createCom(TCSession session, String typeName) {
        // TODO Auto-generated method stub
        IBOCreateDefinition createDefinition = BOCreateDefinitionFactory
                .getInstance().getCreateDefinition(session,
                        typeName);
        CreateInstanceInput createInstanceInput = new CreateInstanceInput(
                createDefinition);
        ArrayList iputList = new ArrayList();

        iputList.add(createInstanceInput);
        TCComponent obj = null;
        List comps = null;
        try {

            comps = SOAGenericCreateHelper.create(session,
                    createDefinition, iputList);
            obj = (TCComponent) comps.get(0);
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return obj;
    }

    /**
	 * 创建文件夹
	 * Create by 郑良 2019.09.15
	 * @param session session会话	
	 * @param name	文件夹名
	 * @param type	文件夹类型
	 */	
    public static TCComponentFolder createFolder(TCSession session, String name, String type) {
        // TODO Auto-generated method stub
        ArrayList iputList = new ArrayList();
        // ICreateInstanceInput
        IBOCreateDefinition createDefinition = BOCreateDefinitionFactory.getInstance().getCreateDefinition(session,
                type);
        CreateInstanceInput createInstanceInput = new CreateInstanceInput(createDefinition);
        createInstanceInput.add("object_name", name);
        iputList.add(createInstanceInput);
        List comps = null;
        TCComponentFolder folder = null;
        try {
            comps = SOAGenericCreateHelper.create(session, createDefinition, iputList);
            folder = (TCComponentFolder) comps.get(0);
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return folder;
    }


    /**
	 * 创建表单
	 * Create by 郑良 2019.09.15
	 * @param session session会话	
	 * @param formTypeName	
	 */	
    public static TCComponentForm createTempForm(TCSession session, String formTypeName, String formName, boolean isSaveDB) {
        TCComponentForm itemRevMasterForm = null;
        if ((formTypeName != null) && (formTypeName.length() > 0)) {
            DataManagementService localObject3 = DataManagementService.getService(session);
            DataManagement.FormInfo[] localObject4 = new DataManagement.FormInfo[1];
            localObject4[0] = new DataManagement.FormInfo();
            localObject4[0].clientId = "1";
            localObject4[0].description = "";
            localObject4[0].name = formName;
            localObject4[0].formType = formTypeName;
            localObject4[0].saveDB = isSaveDB;
            DataManagement.CreateOrUpdateFormsResponse localCreateOrUpdateFormsResponse = localObject3
                    .createOrUpdateForms(localObject4);
            if ((localCreateOrUpdateFormsResponse != null)) {
                DataManagement.CreateFormsOutput[] localObject5 = localCreateOrUpdateFormsResponse.outputs;
                itemRevMasterForm = (TCComponentForm) localObject5[0].form;
                if ((itemRevMasterForm != null) && (itemRevMasterForm.getClientObject() == null)) {
                    itemRevMasterForm.setClientObject("Create_Context");
                }
            }
        }
        return itemRevMasterForm;
    }
}
