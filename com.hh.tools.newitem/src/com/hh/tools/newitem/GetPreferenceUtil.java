package com.hh.tools.newitem;

import java.util.HashMap;

import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;

public class GetPreferenceUtil {
    public GetPreferenceUtil() {

    }

    //根据分隔符获取hashmap值
    public HashMap getHashMapPreference(TCSession session, int scope,
                                        String preferenceName, String separate) {
        String[] strArray = getArrayPreference(session, scope,
                preferenceName);
        HashMap map = new HashMap();
        String key = "";
        String value = "";
        String[] tempArray = null;
        String temp = "";

        int length = 0;
        if (strArray == null || strArray.length == 0) {
            return null;
        }

        for (int i = 0; i < strArray.length; i++) {
            tempArray = strArray[i].split(separate);
            length = tempArray.length;
            if (length > 0) {
                if (length == 2) {
                    map.put(tempArray[0], tempArray[1]);
                } else {
                    temp = "";
                    for (int j = 0; j < length - 1; j++) {
                        temp = temp + tempArray[j + 1];
                    }
                    map.put(tempArray[0], temp);
                }
            } else {
                map.put(strArray[i], strArray[i]);
            }
        }
        return map;

    }

    //获取string首选项值
    public String getStringPreference(TCSession session, int scope,
                                      String preferenceName) {
//		int scope = 0;
//		if ("TC_preference_all".equals(strScope)) {
//			scope = TCPreferenceService.TC_preference_all;
//		} else if ("TC_preference_group".equals(strScope)) {
//			scope = TCPreferenceService.TC_preference_group;
//		} else if ("TC_preference_role".equals(strScope)) {
//			scope = TCPreferenceService.TC_preference_role;
//		} else if ("TC_preference_site".equals(strScope)) {
//			scope = TCPreferenceService.TC_preference_site;
//		} else if ("TC_preference_user".equals(strScope)) {
//			scope = TCPreferenceService.TC_preference_user;
//		} else {
//			scope = TCPreferenceService.TC_preference_site;
//		}

        TCPreferenceService preferenceService = session.getPreferenceService();
        String strValue = preferenceService.getString(scope, preferenceName);
        return strValue;

    }

    //获取数组类型首选项值
    public String[] getArrayPreference(TCSession session, int scope,
                                       String preferenceName) {
//		int scope = 0;
//		if ("TC_preference_all".equals(strScope)) {
//			scope = TCPreferenceService.TC_preference_all;
//		} else if ("TC_preference_group".equals(strScope)) {
//			scope = TCPreferenceService.TC_preference_group;
//		} else if ("TC_preference_role".equals(strScope)) {
//			scope = TCPreferenceService.TC_preference_role;
//		} else if ("TC_preference_site".equals(strScope)) {
//			scope = TCPreferenceService.TC_preference_site;
//		} else if ("TC_preference_user".equals(strScope)) {
//			scope = TCPreferenceService.TC_preference_user;
//		} else {
//			scope = TCPreferenceService.TC_preference_INVALIDSCOPE;
//		}

        TCPreferenceService preferenceService = session.getPreferenceService();
        String[] strArray = preferenceService.getStringArray(scope,
                preferenceName);
        return strArray;
    }
}
