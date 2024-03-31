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
 * 系统工具类
 * 
 * @author wangsf
 *
 */
public class TcSystemUtil {

    public static TCSession getTCSession() {
        return RACUIUtil.getTCSession();
    }

    /**
	 * 获取组件ID
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
	 * 创建组件Item
	 * @param itemId 
	 * @param revId 版本
	 * @param itemTypeName 组件类型名称
	 * @param itemName 组件名称
	 * @return
	 * @throws TCException
	 */
    public static TCComponentItem createItem(String itemId, String revId, String itemTypeName, String itemName)
            throws TCException {
        TCComponentItem item = null;
        if (getTCSession().getTypeComponent(itemTypeName) == null) {
        	throw new TCException("无法获取名为" + itemTypeName + "的Item类型");
        }

        // 获取组件Item的类型对象
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
	 * 创建组件item 并添加到文件夹下
	 * @param itemName 名称
	 * @return 组件版本对象
	 * @throws Exception
	 */
    public static TCComponentItemRevision addCreateItemToNewStuffFolder(String itemTypeName, String itemName) throws Exception {
        TCComponentItem importBomItem = TcSystemUtil.createItem(null, null, itemTypeName, itemName);

        // 添加到文件夹下
        if (importBomItem != null) {
            getTCSession().getUser().getNewStuffFolder().add("contents", importBomItem);
        }
        return importBomItem.getLatestItemRevision();
    }

    /**
	 * 生成组件item的新版本
	 * @param item
	 * @return
	 * @throws Exception
	 */
    public static String generateNewRevID(TCComponentItem item) throws Exception {
    	// 获取组件Item的类型对象
        TCComponentItemType itemType = (TCComponentItemType) getTCSession().getTypeComponent(item.getType());
        return itemType.getNewRev(item);
    }

    /**
	 * 获取结构管理器
	 * 
	 * @return
	 */
    public static TCComponentBOMWindow getComponentBOMWindow(TCSession session) throws Exception {
    	// 结构管理器
        TCComponentRevisionRuleType imancomponentrevisionruletype = (TCComponentRevisionRuleType) session
                .getTypeComponent("RevisionRule");
        TCComponentRevisionRule imancomponentrevisionrule = imancomponentrevisionruletype.getDefaultRule();
        TCComponentBOMWindowType imancomponentbomwindowtype = (TCComponentBOMWindowType) session
                .getTypeComponent("BOMWindow");
        TCComponentBOMWindow imancomponentbomwindow = imancomponentbomwindowtype.create(imancomponentrevisionrule);
        return imancomponentbomwindow;
    }

    /**
	 * 获取首选项值
	 * 
	 * @param session
	 * @param scope
	 * @param preferenceName 首选项名称
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
	 * 导出文件弹出框
	 * 
	 * @param abstractAIFDialog 当前弹框类
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
