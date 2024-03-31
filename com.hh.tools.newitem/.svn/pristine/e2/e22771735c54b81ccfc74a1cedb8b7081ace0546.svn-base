package com.hh.tools.newitem;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Handk
 */
public class DatasetReferenceName {

    private static Map<String, String> map = null;

    private static void init() {
        if (map != null) {
            return;
        }
        map = new HashMap<String, String>(4);
        map.put("MSWord", "word");
        map.put("MSWordX", "word");
        map.put("MSExcel", "excel");
        map.put("MSExcelX", "excel");
    }

    public static String getReferenceName(String datasetType) {
        init();
        if (!map.containsKey(datasetType)) {
            return null;
        }
        return map.get(datasetType);
    }
}
