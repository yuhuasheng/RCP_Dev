package com.hh.tools.newitem.wizardoperation;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.hh.tools.newitem.CreateObject;
import com.hh.tools.newitem.ItemTypeName;
import com.hh.tools.newitem.RelationName;
import com.hh.tools.newitem.Utils;
import com.hh.tools.renderingHint.EDACompDataSheetPropertyBean;
import com.hh.tools.renderingHint.EDACompDellSymbolPropertyBean;
import com.hh.tools.renderingHint.EDACompSymbolPropertyBean;
import com.hh.tools.renderingHint.MfgPropertyBean;
import com.hh.tools.util.DatasetTypeUtil;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.IRelationName;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;

/**
 * �����ϼ�����Item ������
 * 
 * @author wangsf
 *
 */
public class EDACompWizardOperation {
    private static EDACompWizardOperation edaCompWizardOperation = null;

    private EDACompWizardOperation() {
    }

    public static EDACompWizardOperation getInstance() {
        if (null == edaCompWizardOperation) {
            edaCompWizardOperation = new EDACompWizardOperation();
        }
        return edaCompWizardOperation;
    }

    /**
	 * ����ǰ��Ҫ���
	 * 
	 * @param propMap ����Map
	 * @return
	 * @throws TCException
	 */
    public boolean checkCreateing(Map<String, String> propMap) throws TCException {
    	System.out.println("�����ϼ�����ǰ��Ҫ���...");

        if (MfgPropertyBean.selectMfr == null) {
        	MessageBox.post("��ѡ��Mfr��", "Warn", MessageBox.WARNING);
            return false;
        }

        String[] keyArray = new String[]{"objectName"};

        Iterator<Entry<String, String>> iterator = propMap.entrySet().iterator();
        StringBuffer buffer = new StringBuffer();
        String mfr = MfgPropertyBean.selectMfr.getProperty("object_name");
        String mfrPN = "";
        while (iterator.hasNext()) {
            Entry<String, String> entry = iterator.next();
            if ("fx8_MfrPN".equals(entry.getKey())) {
                mfrPN = entry.getValue().toString();
                break;
            }
        }

        if (StringUtils.isBlank(mfrPN)) {
        	MessageBox.post("������MfrPN��", "Warn", MessageBox.WARNING);
            return false;
        }

        buffer.append(mfr + "_");
        buffer.append(mfrPN);
        String[] valueArray = new String[]{buffer.toString()};
        List<InterfaceAIFComponent> EDACompList = Utils.search("__FX_FindEDAComp", keyArray, valueArray);
        if (EDACompList.size() > 0) {
        	MessageBox.post("�Ѵ�����ͬ�����ϼ���", "Warn", MessageBox.WARNING);
            return false;
        }

        if (EDACompSymbolPropertyBean.uploadRelationFile == null && EDACompSymbolPropertyBean.relationDataset == null
                && EDACompDellSymbolPropertyBean.uploadRelationFile == null
                && EDACompDellSymbolPropertyBean.relationDataset == null) {
        	MessageBox.post("Symbol��Dell symbol����һ����ֵ��", "Warn", MessageBox.WARNING);
            return false;
        } else {
            return true;
        }
    }

    /**
	 * ����ǰ����
	 * 
	 * @return
	 */
    public boolean beforeCreate() {
    	System.out.println("�����ϼ�����ǰ����...");
		boolean verifyResultFlag = true;
		// ��ȡ���ݼ����͹���
        DatasetTypeUtil datastTypeUtil = DatasetTypeUtil.getInstance();

        // Symbol�ϴ��ļ���֤
        if (null != EDACompSymbolPropertyBean.uploadRelationFile) {
            verifyResultFlag = verifyDatasetFileType("FX8_Symbol", datastTypeUtil,
                    EDACompSymbolPropertyBean.uploadRelationFile);
        }

        // DellSymbol�ϴ��ļ���֤
        if (null != EDACompDellSymbolPropertyBean.uploadRelationFile) {
            verifyResultFlag = verifyDatasetFileType("FX8_DellSymbol", datastTypeUtil,
                    EDACompDellSymbolPropertyBean.uploadRelationFile);
        }

        // DataSheet�ϴ��ļ���֤
        if (null != EDACompDataSheetPropertyBean.uploadRelationFile) {
            verifyResultFlag = verifyDatasetFileType("FX8_DataSheet", datastTypeUtil,
                    EDACompDataSheetPropertyBean.uploadRelationFile);
        }

        return verifyResultFlag;
    }

    /**
	 * ������ɺ���
	 * 
	 * @param session
	 * @param edaCompItemRev
	 */
    public void afterCreate(TCSession session, TCComponentItemRevision edaCompItemRev) {
    	System.out.println("�����ϼ�������ɺ���...");

        try {
        	// ��ȡ���ݼ����͹���
            DatasetTypeUtil datastTypeUtil = DatasetTypeUtil.getInstance();

            // Symbol���ݼ�����
            TCComponentDataset symbolsDatasetComp = null;
            if (null != EDACompSymbolPropertyBean.uploadRelationFile) {
            	// �������ݼ�
                symbolsDatasetComp = getDatasetByFile(session, datastTypeUtil,
                        EDACompSymbolPropertyBean.uploadRelationFile, null);
            }
            if (null != EDACompSymbolPropertyBean.relationDataset) {
                symbolsDatasetComp = EDACompSymbolPropertyBean.relationDataset;
            }
            if (symbolsDatasetComp != null) {
                edaCompItemRev.add(RelationName.SYMBOLREL, symbolsDatasetComp);
                edaCompItemRev.setProperty(RelationName.SYMBOL, symbolsDatasetComp.toDisplayString());
                String symbolName = symbolsDatasetComp.toDisplayString();
                edaCompItemRev.setProperty("fx8_SchematicPart", symbolName.substring(0, symbolName.lastIndexOf(".")));
            }

            // DellSymbol���ݼ�����
            TCComponentDataset dellSymbolsDatasetComp = null;
            if (null != EDACompDellSymbolPropertyBean.uploadRelationFile) {
            	// �������ݼ�
                dellSymbolsDatasetComp = getDatasetByFile(session, datastTypeUtil,
                        EDACompDellSymbolPropertyBean.uploadRelationFile, null);
            }
            if (null != EDACompDellSymbolPropertyBean.relationDataset) {
                dellSymbolsDatasetComp = EDACompDellSymbolPropertyBean.relationDataset;
            }
            if (dellSymbolsDatasetComp != null) {
                edaCompItemRev.add(RelationName.DELLSYMBOLREL, dellSymbolsDatasetComp);
                edaCompItemRev.setProperty(RelationName.DELLSYMBOL, dellSymbolsDatasetComp.toDisplayString());
                String symbolName = dellSymbolsDatasetComp.toDisplayString();
                edaCompItemRev.setProperty("fx8_DellSchematicPart",
                        symbolName.substring(0, symbolName.lastIndexOf(".")));
            }

            if (MfgPropertyBean.selectMfr != null) {
                edaCompItemRev.add(RelationName.MFRREL, MfgPropertyBean.selectMfr.getItem());
            }

            String mfr_mfrPn = edaCompItemRev.getProperty("fx8_Mfr") + "_" + edaCompItemRev.getProperty("fx8_MfrPN");
            edaCompItemRev.setProperty("fx8_PartNumber", mfr_mfrPn);
            edaCompItemRev.setProperty("fx8_Mfr_MfrPN", mfr_mfrPn);

            // DataSheet���ݼ�����
            TCComponentDataset dataSheetDatasetComp = null;
            if (null != EDACompDataSheetPropertyBean.uploadRelationFile) {
            	// �������ݼ�
                dataSheetDatasetComp = getDatasetByFile(session, datastTypeUtil,
                        EDACompDataSheetPropertyBean.uploadRelationFile, mfr_mfrPn);
                edaCompItemRev.add(RelationName.DATASHEET, dataSheetDatasetComp);
            }
            if (null != EDACompDataSheetPropertyBean.relationDataset) {
                dataSheetDatasetComp = EDACompDataSheetPropertyBean.relationDataset;
                edaCompItemRev.add(RelationName.DATASHEET, dataSheetDatasetComp);
            }

            // �������ϱ�
            TCComponentForm EPIForm = CreateObject.createTempForm(session, ItemTypeName.PCOMPLIANCEFORM,
            		edaCompItemRev.getProperty("item_id") + "_������֤", true);
            EPIForm.save();
            edaCompItemRev.add(IRelationName.IMAN_specification, EPIForm);

            TCComponentForm approveForm = CreateObject.createTempForm(session, ItemTypeName.APPROVESHEETFORM,
                    edaCompItemRev.getProperty("item_id") + "_�������", true);
            approveForm.save();
            approveForm.setProperty("fx8_ApprovalStatus", "N");
            edaCompItemRev.add(IRelationName.IMAN_specification, approveForm);
        } catch (TCException e) {
            e.printStackTrace();
        }

        // �����̬����
        MfgPropertyBean.selectMfr = null;
        EDACompSymbolPropertyBean.clearStaticData();
        EDACompDellSymbolPropertyBean.clearStaticData();
        EDACompDataSheetPropertyBean.clearStaticData();
    }

    /**
	 * �����ļ� ��ȡ���������ݼ�
	 * 
	 * @param session
	 * @param datastTypeUtil
	 * @param uploadFile     �ϴ��ļ�
	 * @return
	 */
    private TCComponentDataset getDatasetByFile(TCSession session, DatasetTypeUtil datastTypeUtil, File uploadFile,
                                                String fileName) {
        String relationPath = uploadFile.getAbsolutePath();
        String relationFileName = uploadFile.getName();
        String datasetTypeName = datastTypeUtil.getDatasetType(relationFileName);
        String dstDefintionType = datastTypeUtil.getDatasetDefinitionType(datasetTypeName);
        if (StringUtils.isNotEmpty(fileName)) {
            relationFileName = fileName + "." + datastTypeUtil.getFileSuffix(relationFileName);
        }
        // �������ݼ�
        return CreateObject.createDataSet(session, relationPath, datasetTypeName, relationFileName, dstDefintionType);
    }

    /**
	 * ��֤���ݼ����ļ�����
	 * 
	 * @param propName       ������
	 * @param datastTypeUtil
	 * @param uploadFile     �ϴ��ļ�
	 * @return
	 */
    private boolean verifyDatasetFileType(String propName, DatasetTypeUtil datastTypeUtil, File uploadFile) {
        String relationFileName = uploadFile.getName();
        String datasetTypeName = datastTypeUtil.getDatasetType(relationFileName);
        if (StringUtils.isEmpty(datasetTypeName)) {
            MessageBox.post(propName + ": Not Get Dataset Type Name!", "Warn...", MessageBox.WARNING);
            return false;
        }

        String dstDefintionType = datastTypeUtil.getDatasetDefinitionType(datasetTypeName);
        if (StringUtils.isEmpty(dstDefintionType)) {
            MessageBox.post(propName + ": Not Get Dataset Type Name!", "Warn...", MessageBox.WARNING);
            return false;
        }

        return true;
    }
}
