package com.hh.tools.util;

import java.util.ArrayList;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCComponentQueryType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCQueryClause;
import com.teamcenter.rac.kernel.TCSession;

public class GetTemplateFile {
	/**
	 * �ṩ��ȡ�ļ�ģ��ľ�̬����
	 * 
	 */

	/**
	 * ��ȡģ����ļ���
	 * 
	 * @return TCComponent TCComponentģ����ļ��ж���
	 * @param TCSession
	 *            ��ǰ�Ự
	 */
    private static TCComponent getTemplateRootFolder(TCSession session) {

        TCComponent[] comps = null;

        comps = search(session, "__TemplateFolder", null, null);
        if (comps != null && comps.length > 0 && comps[0] != null) {
            return comps[0];
        }
        return null;

    }

    /**
	 * ������������ƣ�����ģ����ļ����±����ͬ������
	 * 
	 * @return TCComponent TCComponent����
	 * @param TCSession
	 *            ��ǰ�Ự
	 * @param String
	 *            �����������ļ�������
	 */
    public static TCComponent getTemplateFolder(TCSession session, String s) {
        TCComponent comp = null;
        TCComponent[] comps = null;
        int i;
        comp = getTemplateRootFolder(session);
        if (comp != null) {
            if (s == null || s.equalsIgnoreCase("")) {
                return comp;
            }
            try {
                comp.refresh();
                comps = comp.getRelatedComponents("contents");
                if (comps != null) {
                    comp = null;
                    for (i = 0; i < comps.length; i++) {
                        if (comps[i].getProperty("object_name").equals(s)) {
                            comp = comps[i];
                            return comp;
                        }
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                comp = null;
                e.printStackTrace();
            }
        }
        return comp;

    }

    /**
	 * ����������ļ������ƺ����ݼ����ơ����ͣ�����ģ���ļ����±����ͬ������
	 * 
	 * @return TCComponent TCComponent����
	 * @param TCSession
	 *            ��ǰ�Ự
	 * @param folder
	 *            �����������ļ�������
	 * @param da
	 *            ���������Ķ�������
	 * @param daType
	 *            ���������Ķ�������
	 */
    public static TCComponent getTemplateComponent(TCSession session,
                                                   String folder, String ds, String dsType) {
        TCComponent comp = null;
        TCComponent[] comps = null;
        int i;
        comp = getTemplateFolder(session, folder);
        if (comp != null) {
            try {
                comp.refresh();
                comps = comp.getRelatedComponents("contents");
                if (comps != null) {
                    comp = null;
                    for (i = 0; i < comps.length; i++) {
                        if (comps[i].getProperty("object_name").equals(ds)) {
                            if (dsType != null) {
                                if (comps[i].getType().equals(dsType)) {
                                    comp = comps[i];
                                    return comp;
                                }

                            } else {
                                comp = comps[i];
                                return comp;
                            }

                        }
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                comp = null;
                e.printStackTrace();
            }
        }
        return comp;

    }

    /**
	 * ʹ�ø�����ѯ�����Ҷ���
	 * 
	 * @return
	 * @param TCSession
	 *            ��ǰ�Ự
	 * @param String
	 *            ��ѯ����
	 * @param String
	 *            [] ��ѯ�ؼ�������
	 * @param String
	 *            [] ��ѯ����ֵ����
	 */
    public static TCComponent[] search(TCSession session, String s,
                                       String[] search1, String[] input1) {
        TCComponent atccomponent[] = null;
        TCComponentQueryType tccomponentquerytype = null;
        TCComponentQuery query = null;
        TCQueryClause tcqc[];
        ArrayList<String> keys;
        ArrayList<String> values;
        String key;
        String value;

        int i;
        try {
            tccomponentquerytype = (TCComponentQueryType) session
                    .getTypeComponent("ImanQuery");
            if (tccomponentquerytype != null) {
                query = (TCComponentQuery) tccomponentquerytype.find(s);
                tcqc = query.describe();
                if (search1 != null && input1 != null) {
                    atccomponent = query.execute(search1, input1);
                    return atccomponent;

                } else {
                    keys = new ArrayList<String>();
                    values = new ArrayList<String>();
                    for (i = 0; i < tcqc.length; i++) {
                        key = tcqc[i].getUserEntryNameDisplay();
                        value = tcqc[i].getDefaultValue();
                        if (key != null && key.length() > 0 && value != null
                                && value.length() > 0) {
                            keys.add(key);
                            values.add(value);
                        }
                    }
                    if (keys.size() > 0) {
                        atccomponent = query.execute(
                                keys.toArray(new String[keys.size()]),
                                values.toArray(new String[values.size()]));
                        return atccomponent;
                    }
                }
            }
        } catch (TCException e) {
            e.printStackTrace();
        }
        return atccomponent;
    }

}
