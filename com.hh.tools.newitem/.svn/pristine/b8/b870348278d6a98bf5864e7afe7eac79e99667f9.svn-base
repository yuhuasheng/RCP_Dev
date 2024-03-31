package com.hh.tools.importBOM.util;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

/**
 * TC 搜索工具
 * 
 * @author wangsf
 *
 */
public class TcSearchUtil {

	/**
	 * 根据搜索名 获取最大版本对象
	 * @param searchName 搜索名
	 * @param keys 搜索字段
	 * @param values 搜索字段值
	 * @return
	 */
    public static TCComponentItemRevision searchMaxItemRev(String searchName, String[] keys, String[] values) {
        TCComponentItemRevision latestItemRevision = null;
        try {
            InterfaceAIFComponent[] resultComponents = TcSystemUtil.getTCSession().search(searchName, keys, values);
            if (null != resultComponents && resultComponents.length > 0) {
                latestItemRevision = (TCComponentItemRevision) resultComponents[0];
                latestItemRevision = latestItemRevision.getItem().getLatestItemRevision();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return latestItemRevision;
    }

}
