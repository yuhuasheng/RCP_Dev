package com.hh.tools.importBOM.util;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

/**
 * TC ��������
 * 
 * @author wangsf
 *
 */
public class TcSearchUtil {

	/**
	 * ���������� ��ȡ���汾����
	 * @param searchName ������
	 * @param keys �����ֶ�
	 * @param values �����ֶ�ֵ
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
