package com.hh.tools.importBOM.util;

import java.io.File;

import javax.swing.JFileChooser;

import com.teamcenter.rac.aif.AbstractAIFDialog;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentBOMWindowType;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemType;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.TCComponentRevisionRuleType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.ui.common.RACUIUtil;

/**
 * ϵͳ������
 * 
 * @author wangsf
 *
 */
public class TcSystemUtil {

    public static TCSession getTCSession() {
        return RACUIUtil.getTCSession();
    }

    /**
	 * ��ȡ���ID
	 * 
	 * @param itemTypeName
	 * @return
	 * @throws Exception
	 */
    public static String newItemID(String itemTypeName) throws Exception {
        String itemID = "";
        TCComponentItemType tcc = (TCComponentItemType) getTCSession().getTypeComponent(itemTypeName);
        itemID = tcc.getNewID();
        return itemID;
    }

    /**
	 * �������Item
	 * @param itemId 
	 * @param revId �汾
	 * @param itemTypeName �����������
	 * @param itemName �������
	 * @return
	 * @throws TCException
	 */
    public static TCComponentItem createItem(String itemId, String revId, String itemTypeName, String itemName)
            throws TCException {
        TCComponentItem item = null;
        if (getTCSession().getTypeComponent(itemTypeName) == null) {
        	throw new TCException("�޷���ȡ��Ϊ" + itemTypeName + "��Item����");
        }

        // ��ȡ���Item�����Ͷ���
        TCComponentItemType itemType = (TCComponentItemType) getTCSession().getTypeComponent(itemTypeName);

        if (null == itemId || "".equals(itemId)) {
            itemId = itemType.getNewID();
        }

        if (null == revId || "".equals(revId)) {
            revId = itemType.getNewRev(null);
        }

        item = itemType.create(itemId, revId, itemTypeName, itemName, "", null);
        return item;
    }

    /**
	 * �������item ����ӵ��ļ�����
	 * @param itemName ����
	 * @return ����汾����
	 * @throws Exception
	 */
    public static TCComponentItemRevision addCreateItemToNewStuffFolder(String itemTypeName, String itemName) throws Exception {
        TCComponentItem importBomItem = TcSystemUtil.createItem(null, null, itemTypeName, itemName);

        // ��ӵ��ļ�����
        if (importBomItem != null) {
            getTCSession().getUser().getNewStuffFolder().add("contents", importBomItem);
        }
        return importBomItem.getLatestItemRevision();
    }

    /**
	 * �������item���°汾
	 * @param item
	 * @return
	 * @throws Exception
	 */
    public static String generateNewRevID(TCComponentItem item) throws Exception {
    	// ��ȡ���Item�����Ͷ���
        TCComponentItemType itemType = (TCComponentItemType) getTCSession().getTypeComponent(item.getType());
        return itemType.getNewRev(item);
    }

    /**
	 * ��ȡ�ṹ������
	 * 
	 * @return
	 */
    public static TCComponentBOMWindow getComponentBOMWindow(TCSession session) throws Exception {
    	// �ṹ������
        TCComponentRevisionRuleType imancomponentrevisionruletype = (TCComponentRevisionRuleType) session
                .getTypeComponent("RevisionRule");
        TCComponentRevisionRule imancomponentrevisionrule = imancomponentrevisionruletype.getDefaultRule();
        TCComponentBOMWindowType imancomponentbomwindowtype = (TCComponentBOMWindowType) session
                .getTypeComponent("BOMWindow");
        TCComponentBOMWindow imancomponentbomwindow = imancomponentbomwindowtype.create(imancomponentrevisionrule);
        return imancomponentbomwindow;
    }

    /**
	 * ��ȡ��ѡ��ֵ
	 * 
	 * @param session
	 * @param scope
	 * @param preferenceName ��ѡ������
	 * @return
	 */
    @SuppressWarnings("deprecation")
    public static String getStringPreference(TCSession session, Integer scope, String preferenceName) {
        int preferenceScope = TCPreferenceService.TC_preference_site;
        if (null != scope) {
            preferenceScope = scope;
        }

        TCPreferenceService preferenceService = session.getPreferenceService();
        String strValue = preferenceService.getString(preferenceScope, preferenceName);
        return strValue;

    }


    /**
	 * �����ļ�������
	 * 
	 * @param abstractAIFDialog ��ǰ������
	 * @return
	 */
    public static File fileDialogExport(AbstractAIFDialog abstractAIFDialog) {
        try {
            JFileChooser jf = new JFileChooser();
            jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = jf.showOpenDialog(abstractAIFDialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = jf.getSelectedFile();
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
