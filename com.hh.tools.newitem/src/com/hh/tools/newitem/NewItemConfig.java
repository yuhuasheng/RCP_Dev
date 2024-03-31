package com.hh.tools.newitem;


import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.*;

/**
 * @author Handk
 */
public class NewItemConfig {

    private static Map<String, NewItemConfig> map = null;

    private static String FormType_Prefix = Utils.getPrefix1();
    private static String PropertyName_Prefix = Utils.getPrefix2();

    private static String FN_PropertyFormName = FormType_Prefix + "PropertyForm";

    private static String PN_ItemTypeName = PropertyName_Prefix + "ItemTypeName";
    private static String PN_IDRule = PropertyName_Prefix + "IDRule";
    private static String PN_DatasetRel = PropertyName_Prefix + "RevDatasetRel";
    private static String PN_DocTemplate = PropertyName_Prefix + "DocTemplate";
    private static String PN_Batch = PropertyName_Prefix + "IsBatch";
    private static String PN_StartSeq = PropertyName_Prefix + "StartSeq";

    //add by 李昂
  	private static String PN_Target = PropertyName_Prefix + "Target";  					//创建对象之前，需要点击哪个对象创建对象的属性
  	private static String PN_TargetRelate = PropertyName_Prefix + "TargetRel";		//创建完毕后，选择对象与创建对象关联关系属性
  	private static String PN_MapProp = PropertyName_Prefix + "MapProperty";					//创建完毕后，属性映射
  	private static String PN_CurrentRelate = PropertyName_Prefix + "CurrentRel";		//创建完毕后，创建对象与选中对象关联关系属性
    private static String PN_IsReleased = PropertyName_Prefix + "IsReleased";
    private String[] target = null;
    private String[] targetRelate = null;
    private String[] mapProps = null;
    private String[] currentRelate = null;
    private TCComponentItem targetItem = null;
    private TCComponentItemRevision targetItemRev = null;
    private TCComponentBOMLine targetBOMLine = null;
    private TCComponentFolder targetFolder = null;
    private TCComponentProject targetProject = null;
    private InterfaceAIFComponent[] targetObjects = null;
    static DBUtil dbUtil = new DBUtil();
    static Connection conn = null;
    private String dialogName = "";
    private boolean isReleased = false;
    //------------------

    /** 对象类型 */
    private String itemTypeName;

    /** 如果由文档模板，这个是创建后的对象文档模板所放的属性的名称 */
    private String datasetRelation;

    /** 是否允许批量创建 */
    private boolean batchCreate = false;
    private int startSeq = 0;
    private String idRule;
    private List<String> idRuleList;
    private List<DocumentTemplate> templates = new ArrayList<DocumentTemplate>();
    private static List<TCComponentDataset> datasetList = new ArrayList<>();
    private static TCSession session = null;

    private static FileStreamUtil fileStreamUtil = new FileStreamUtil();
    private static PrintStream printStream = null;


    public NewItemConfig(TCComponentForm form) {
        try {
            this.itemTypeName = form.getProperty(PN_ItemTypeName);
            this.idRule = form.getProperty(PN_IDRule);
            this.idRuleList = analysisIDRule(idRule);
            this.datasetRelation = form.getProperty(PN_DatasetRel);
            this.batchCreate = form.getProperty(PN_Batch).equals("是") ? true : false;
            if (!Utils.isNull(form.getProperty(PN_StartSeq).trim()))
                this.setStartSeq(Integer.valueOf(form.getProperty(PN_StartSeq)));
            TCComponent[] tccs = form.getTCProperty(PN_DocTemplate).getReferenceValueArray();
            if (tccs != null && tccs.length > 0) {
                for (TCComponent tcc : tccs) {
                    if (tcc instanceof TCComponentForm) {
                        TCComponentForm temp = (TCComponentForm) tcc;
                        DocumentTemplate template = new DocumentTemplate(temp);
                        templates.add(template);
                    }
                }
            }
            this.session = form.getSession();

            TCProperty currentRelateP = form.getTCProperty(PN_CurrentRelate);
            this.currentRelate = currentRelateP.getStringArrayValue();

            TCProperty targetP = form.getTCProperty(PN_Target);
            this.target = targetP.getStringArrayValue();

            TCProperty targetRelateP = form.getTCProperty(PN_TargetRelate);
            this.targetRelate = targetRelateP.getStringArrayValue();

            TCProperty mapPropP = form.getTCProperty(PN_MapProp);
            this.mapProps = mapPropP.getStringArrayValue();

            TCProperty isReleasdP = form.getTCProperty(PN_IsReleased);
            this.isReleased = isReleasdP.getBoolValue();

            String fileName = fileStreamUtil.getTempPath("");
            printStream = fileStreamUtil.openStream(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public InterfaceAIFComponent[] getTargetObjects() {
        return targetObjects;
    }


    public void setTargetObjects(InterfaceAIFComponent[] targetObjects) {
        this.targetObjects = targetObjects;
    }


    public boolean isReleased() {
        return isReleased;
    }


    public String getDialogName() {
        return dialogName;
    }


    public void setDialogName(String dialogName) {
        this.dialogName = dialogName;
    }


    public TCComponentItem getTargetItem() {
        return targetItem;
    }


    public void setTargetItem(TCComponentItem targetItem) {
        this.targetItem = targetItem;
    }


    public TCComponentItemRevision getTargetItemRev() {
        return targetItemRev;
    }


    public List<TCComponentDataset> getDatasetList() {
        return datasetList;
    }


    public void setDatasetList(List<TCComponentDataset> datasetList) {
        this.datasetList = datasetList;
    }


    public void setTargetItemRev(TCComponentItemRevision targetItemRev) {
        this.targetItemRev = targetItemRev;
    }


    public TCComponentBOMLine getTargetBOMLine() {
        return targetBOMLine;
    }


    public void setTargetBOMLine(TCComponentBOMLine targetBOMLine) {
        this.targetBOMLine = targetBOMLine;
    }


    public TCComponentFolder getTargetFolder() {
        return targetFolder;
    }


    public TCComponentProject getTargetProject() {
        return targetProject;
    }


    public void setTargetProject(TCComponentProject targetProject) {
        this.targetProject = targetProject;
    }

    public void setTargetFolder(TCComponentFolder targetFolder) {
        this.targetFolder = targetFolder;
    }


    public String[] getTarget() {
        return target;
    }


    public String[] getTargetRelate() {
        return targetRelate;
    }


    public String[] getMapProps() {
        return mapProps;
    }


    public String[] getCurrentRelate() {
        return currentRelate;
    }


    public String getItemTypeName() {
        return this.itemTypeName;
    }

    public String getIDRule() {
        return this.idRule;
    }

    public List<String> getIDRuleList() {
        return this.idRuleList;
    }

    public String getDatasetRelation() {
        if (Utils.isNull(datasetRelation)) {
            return "IMAN_specification";
        }
        return this.datasetRelation;
    }

    public boolean getBatchCreate() {
        return this.batchCreate;
    }

    public List<DocumentTemplate> getDocumentTemplates() {
        return this.templates;
    }

    static public Map<String, NewItemConfig> getConfigMap() {
        initMap();
        return map;
    }

    static public NewItemConfig getConfigByName(String itemTypeName) {
        initMap();
        if (map.containsKey(itemTypeName)) {
            return map.get(itemTypeName);
        }
        return null;
    }

    static public void initMap() {
        if (map != null) {
            return;
        }
        try {
            map = new HashMap<String, NewItemConfig>(16);
            String searchName = "常规...";
            String[] keys = new String[]{Utils.getTextValue("Type")};
            String[] values = new String[]{FN_PropertyFormName};
            List<InterfaceAIFComponent> list = Utils.search(searchName, keys, values);
            System.out.println("list==" + list);
            if (list.size() < 1) {
                return;
            }
            for (InterfaceAIFComponent temp : list) {
                if (temp instanceof TCComponentForm) {
                    TCComponentForm form = (TCComponentForm) temp;
                    NewItemConfig config = new NewItemConfig(form);
                    String type = config.getItemTypeName();
                    System.out.println("type == " + type);
                    if (Utils.isNull(type)) {
                        continue;
                    }
                    if (!map.containsKey(type)) {
                        System.out.println("map add == " + type);
                        map.put(type, config);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static NewItemConfig findConfig(String type) {
        NewItemConfig config = null;
        System.out.println("map==" + map);
        if (map != null) {
            if (map.containsKey(type)) {
                config = map.get(type);
                return config;
            }
        } else {
            if (config == null) {
                String searchName = "__FX_Search_PropertyForm";
//				Utils.getTextValue(PN_ItemTypeName)
                String[] keys = new String[]{"ItemTypeName"};
                String[] values = new String[]{type};
                System.out.println(Arrays.toString(keys) + "==" + Arrays.toString(values));
                List<InterfaceAIFComponent> list = Utils.search(searchName, keys, values);

                System.out.println("list.size() ========== " + list.size());
                if (list.size() < 1) {
                    return null;
                }

                for (InterfaceAIFComponent temp : list) {
                    if (temp instanceof TCComponentForm) {
                        TCComponentForm form = (TCComponentForm) temp;
                        NewItemConfig tempConfig = new NewItemConfig(form);
                        String configType = tempConfig.getItemTypeName();
                        System.out.println("configType == " + configType);
                        if (Utils.isNull(configType)) {
                            continue;
                        }
                        if (type.equals(configType)) {
                            config = tempConfig;
                            try {
                                datasetList.clear();
                                TCComponent[] components = form.getRelatedComponents(NewItemConfig.PN_DocTemplate);
                                if (components != null && components.length > 0) {
                                    for (TCComponent tcComponent : components) {
                                        if (tcComponent instanceof TCComponentForm && "FX8_DocTemplateForm".equals(tcComponent.getType())) {
                                            TCComponent datasetCom = tcComponent.getRelatedComponent(DocumentTemplate.PN_Dataset);
                                            if (datasetCom != null && datasetCom instanceof TCComponentDataset) {
                                                System.out.println("datasetCom==" + datasetCom);
                                                datasetList.add((TCComponentDataset) datasetCom);
                                            }
                                        }
                                    }
                                }
                                if (datasetList.size() > 0) {
                                    config.setDatasetList(datasetList);
                                }
                            } catch (TCException e) {
                                e.printStackTrace();
                            }

                            break;
                        }
                    }
                }
            }
        }
        return config;
    }

    static public String getNewID(NewItemConfig config, String type, Map<String, Object> map) {
        if (config == null) {
            return null;
        }
//		Utils.print2Console("NewID ItemTypeName="+config.getItemTypeName());
//		fileStreamUtil.writeData(printStream, "NewID ItemTypeName="+config.getItemTypeName());
        if (map == null) {
            return null;
        }
        for (Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            Utils.print2Console("Key=" + key + ",Value=" + value);
            fileStreamUtil.writeData(printStream, "Key=" + key + ",Value=" + value);
        }

        String[] mapProps = config.getMapProps();
        System.out.println("mapProps==" + Arrays.toString(mapProps));
        HashMap<String, String> propMap = new HashMap<String, String>();
        TCComponentItemRevision targetItemRev = config.getTargetItemRev();
        TCComponentProject project = config.getTargetProject();

        TCComponentItem targetItem = null;
        try {
            if (targetItemRev != null) {
                targetItem = targetItemRev.getItem();
            }


            for (int i = 0; i < mapProps.length; i++) {
                String mapProp = mapProps[i];
                Utils.print2Console("mapProp == " + mapProp);
                if (mapProp.contains("=")) {
                    String tempValue1 = mapProp.split("=")[0];
                    String tempValue = mapProp.split("=")[1];
                    if (tempValue.contains(":")) {
                        String[] propNames1 = tempValue1.split(":");
                        Utils.print2Console("propNames1[0] == " + propNames1[0]);
                        Utils.print2Console("propNames1[1] == " + propNames1[1]);
                        String[] propNames = tempValue.split(":");
                        Utils.print2Console("propNames[0] == " + propNames[0]);
                        Utils.print2Console("propNames[1] == " + propNames[1]);
                        String propValue = "";
                        if ("revision".equals(propNames1[0])) {
                            if (targetItemRev != null) {
                                propValue = targetItemRev.getProperty(propNames1[1]);
                                //propMap.put(propNames[1], propValue);
                                if (propMap.containsKey(propNames[1])) {
                                    Utils.print2Console("propMap has " + propNames[1]);
                                    String tempPropValue = propMap.get(propNames[1]);
                                    Utils.print2Console("tempPropValue == " + tempPropValue);
                                    if ("".equals(tempPropValue)) {
                                        propMap.put(propNames[1], propValue);
                                        Utils.print2Console("propMap put  key == " + propNames[1] + ",value == " + propValue);
                                    }
                                } else {
                                    propMap.put(propNames[1], propValue);
                                    Utils.print2Console("propMap put  key == " + propNames[1] + ",value == " + propValue);
                                }
                            } else {
                                if (!propMap.containsKey(propNames[1])) {
                                    propMap.put(propNames[1], "");
                                    Utils.print2Console("propMap put  key == " + propNames[1] + ",value == " + propValue);
                                }
                            }

                        } else if ("item".equals(propNames1[0])) {
                            if (targetItem != null) {
                                propValue = targetItem.getProperty(propNames1[1]);
                                if (propMap.containsKey(propNames[1])) {
                                    Utils.print2Console("propMap has " + propNames[1]);
                                    String tempPropValue = propMap.get(propNames[1]);
                                    Utils.print2Console("tempPropValue == " + tempPropValue);
                                    if ("".equals(tempPropValue)) {
                                        propMap.put(propNames[1], propValue);
                                        Utils.print2Console("propMap put  key == " + propNames[1] + ",value == " + propValue);
                                    }
                                } else {
                                    propMap.put(propNames[1], propValue);
                                    Utils.print2Console("propMap put  key == " + propNames[1] + ",value == " + propValue);
                                }
                                //propMap.put(propNames[1], propValue);
                            } else {
                                if (!propMap.containsKey(propNames[1])) {
                                    propMap.put(propNames[1], "");
                                    Utils.print2Console("propMap put  key == " + propNames[1] + ",value == " + propValue);
                                }
                            }

                        } else if ("project".equals(propNames1[0])) {
                            if (project != null) {
                                propValue = project.getProperty(propNames1[1]);
                                if (propMap.containsKey(propNames[1])) {
                                    Utils.print2Console("propMap has " + propNames[1]);
                                    String tempPropValue = propMap.get(propNames[1]);
                                    Utils.print2Console("tempPropValue == " + tempPropValue);
                                    if ("".equals(tempPropValue)) {
                                        propMap.put(propNames[1], propValue);
                                        Utils.print2Console("propMap put  key == " + propNames[1] + ",value == " + propValue);
                                    }
                                } else {
                                    propMap.put(propNames[1], propValue);
                                    Utils.print2Console("propMap put  key == " + propNames[1] + ",value == " + propValue);
                                }

                            } else {
                                if (!propMap.containsKey(propNames[1])) {
                                    propMap.put(propNames[1], "");
                                    Utils.print2Console("propMap put  key == " + propNames[1] + ",value == " + propValue);
                                }

                            }

                        }
                        Utils.print2Console("propValue == " + propValue);
                    }
                }
            }
        } catch (TCException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        List<String> idRuleList = config.getIDRuleList();
        System.out.println("idRuleList.size() == " + idRuleList.size());
        if (idRuleList.size() == 0) {
            try {
                TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent("Item");
                return itemType.getNewID();
            } catch (TCException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        List<String> list = new ArrayList<String>();
        int index = -1;
        for (int i = 0; i < idRuleList.size(); i++) {
            String s = idRuleList.get(i);
            System.out.println("s==" + s);
            if (Utils.isNull(s)) {
                return null;
            }
            int l = s.length();
            if (s.startsWith("\"")) {
                if (l < 3) {
                    continue;
                }
                list.add(s.substring(1, l - 1));
            } else if (s.startsWith("[")) {
                if (l < 3) {
                    continue;
                }
                String propertyName = s.substring(1, l - 1);

                String propertyValue = "";
                if (propMap.containsKey(propertyName)) {
                    Utils.print2Console("propMap has == " + propertyName);
                    propertyValue = propMap.get(propertyName);
                }
                if ("".equals(propertyValue)) {
                    if (map.containsKey(propertyName)) {
                        fileStreamUtil.writeData(printStream, "map has == " + propertyName);
                        propertyValue = map.get(propertyName).toString();
                        fileStreamUtil.writeData(printStream, "prop value == " + propertyValue);
                        if (Utils.isNull(propertyValue)) {
                            list.add("");
                        } else {
                            list.add(propertyValue);
                        }
                    } else {
                    	// Utils.print2Console("创建界面中没有 " + propertyName + "
						// 属性,无法生成ID!", true);
                        try {
                            TCComponentItemType itemType = (TCComponentItemType) Utils.getTCSession()
                                    .getTypeComponent(config.getItemTypeName());
                            TCPropertyDescriptor propertyDescriptor = itemType.getPropDesc(propertyName);
                            if (propertyDescriptor == null) {
                                TCComponentItemRevisionType itemRevisionType = itemType.getItemRevisionType();
                                propertyDescriptor = itemRevisionType.getPropertyDescriptor(propertyName);
                                if (propertyDescriptor == null) {
                                	Utils.print2Console("对象类/版本类中,没有 " + propertyName + " 属性,无法生成ID!", true);
									fileStreamUtil.writeData(printStream, "11 对象类/版本类中没有 " + propertyName + " 属性,无法生成ID!");
                                    return null;
                                }
                            }
                            propertyValue = propertyDescriptor.getInitialValue();
                            if (Utils.isNull(propertyValue)) {
                            	Utils.print2Console("对象类/版本类中,没有 " + propertyName + " 属性,无法生成ID!", true);
								fileStreamUtil.writeData(printStream, "对象类/版本类中没有 " + propertyName + " 属性,无法生成ID!");
                                return null;
                            }
                            list.add(propertyValue);
                        } catch (TCException e) {
                            e.printStackTrace();
                            Utils.print2Console("创建界面中没有 " + propertyName + " 属性,无法生成ID!", true);
							fileStreamUtil.writeData(printStream, "创建界面中没有 " + propertyName + " 属性,无法生成ID!");
                            return null;
                        }
                    }
                } else {
                    list.add(propertyValue);
                }


            } else if (s.startsWith("{")) {
                if (l < 3) {
                    continue;
                }
                String formartString = s.substring(1, l - 1);
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat(formartString);
                list.add(dateFormat.format(date));

            } else if (s.startsWith("N")) {
                list.add(s);
                index = i;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
//			if (i == index) {
//				break;
//			}
            sb.append(list.get(i));
        }
        Utils.print2Console("NewItemID:" + sb.toString());
        fileStreamUtil.writeData(printStream, "NewItemID:" + sb.toString());
        if (index < 0) {
            return sb.toString();
        } else {
            String seq = getNextCode(config, type, sb.toString());
            System.out.println("seq==" + seq);
            int length = list.get(index).length();
            StringBuilder seqStringBuilder = new StringBuilder();

            while (seqStringBuilder.length() + seq.length() < length) {
                seqStringBuilder.append("0");
            }
            if ("0".equals(seq)) {
                seqStringBuilder.append("1");
            } else {
                seqStringBuilder.append(seq);
            }
            list.set(index, seqStringBuilder.toString());
        }
        System.out.println("list==" + list);
        sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
        }
        return sb.toString();
    }

    static public String getNewID(NewItemConfig config, String key) {
        if (config == null) {
            return null;
        }
        if (Utils.isNull(key)) {
            return null;
        }
        return null;
    }

    static public List<String> analysisIDRule(String rule) {
    	/*
		 * ID规则由四种类型组成;
		 * 1、由前后2个英文半角双引号引起来的部分;这种类型在生成ID的时候，将直接取双引号中间部分的内容.例如："RCQ-"、"."、"-";
		 * 2、由前后方括号包括起来的部分。此部分要求方括号中间的内容是属性的英文名称;这种类型在生成ID的时候，将替换为对应的属性的值.
		 * 例如：[ac8_classification];
		 * 3、由若干个大写的N组成的部分。此部分标识为流水码，由N的个数确定填充的位数，填充的值是0.如果实际的流水码超过了需要填充的位数，
		 * 则使用实际的流水码。例如： NNN、NN;
		 * 4、由前后两个大括号包括起来的部分。此部分里面要求是日期格式，使用SimpleDateFormat格式
		 * 
		 * 此方法用于解析按此规则配置的ID规则，返回的值当中，是包含双引号、方括号的。
		 */

        List<String> res = new ArrayList<String>();
        if (rule == null || rule.equals("")) {
            return res;
        }

        /** 在解析ID规则的时候，标识某个段的开头位置 */
        int startIndex = 0;

        /** 在解析ID规则的时候，标识某个段的结尾位置 */
        int endIndex = -1;

        /** 在解析ID规则的时候，标识已经找到了某个段的开头； */
        boolean found = false;
        char endChar = 0;
        for (int i = 0; i < rule.length(); i++) {
            char c = rule.charAt(i);
            if (found) {
                if (c != endChar) {
                    continue;
                }
                endIndex = i;
                if ((startIndex + 1) <= endIndex) {
                    String subRule = rule.substring(startIndex, endIndex + 1);
                    res.add(subRule);
                }
                found = false;
            } else {
                if (c == '"') {
                    found = true;
                    endChar = '"';
                    startIndex = i;
                } else if (c == '[') {
                    found = true;
                    endChar = ']';
                    startIndex = i;
                } else if (c == '{') {
                    found = true;
                    endChar = '}';
                    startIndex = i;
                } else if (c == 'N') {
                    int j = i;
                    c = rule.charAt(j);
                    while (c == 'N' && j < rule.length()) {
                        j++;
                        if (j == rule.length()) {
                            break;
                        }
                        c = rule.charAt(j);
                    }
                    String subString = null;
                    if (j == rule.length()) {
                        subString = rule.substring(i, rule.length());
                        res.add(subString);
                        break;
                    } else if (j < rule.length()) {
                        subString = rule.substring(i, j);
                        res.add(subString);
                        i = j - 1;
                        found = false;
                    }
                }
            }
        }
        return res;
    }

    public static void copyNewDataset(TCComponentItemRevision rev, NewItemConfig config) {
        if (rev == null || config == null) {
            return;
        }
        try {
            TCProperty tcp1 = null, tcp2 = null, tcp3 = null;
            try {
                tcp1 = rev.getTCProperty(DocumentTemplate.PN_Layout);
            } catch (Exception e) {
                tcp1 = null;
            }
            try {
                tcp2 = rev.getTCProperty(DocumentTemplate.PN_HorOrVer);
            } catch (Exception e) {
                tcp2 = null;
            }
            try {
                tcp3 = rev.getTCProperty(DocumentTemplate.PN_FileType);
            } catch (Exception e) {
                tcp3 = null;
            }

            String v1 = null, v2 = null, v3 = null;
            if (tcp1 != null) {
                v1 = tcp1.getStringValue();
            }
            if (tcp2 != null) {
                v2 = tcp2.getStringValue();
            }
            if (tcp3 != null) {
                v3 = tcp3.getStringValue();
            }
            System.out.println("V1=" + v1 + ",V2=" + v2 + ",V3=" + v3);
            TCComponentDataset dataset = null;

            LinkedHashMap<String, String> propertyValueMap = new LinkedHashMap<String, String>();
            propertyValueMap.put(DocumentTemplate.PN_Layout, v1);
            propertyValueMap.put(DocumentTemplate.PN_HorOrVer, v2);
            propertyValueMap.put(DocumentTemplate.PN_FileType, v3);

            dataset = config.getDataset(propertyValueMap);
            if (dataset != null) {
            	fileStreamUtil.writeData(printStream, "创建数据集");
                TCComponentDataset newdataset = Utils.copyDatasetWithNewName(dataset, rev.getProperty("item_id"),
                        rev.getProperty("object_name"));
                fileStreamUtil.writeData(printStream, "newdataset == " + newdataset);
                if (newdataset == null) {
                	Utils.infoMessage("从模板创建数据集失败!");
                } else {
                    rev.add(config.getDatasetRelation(), newdataset);
                }
            } else {
            	Utils.print2Console("系统的配置中没有数据集!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    static public String getNextCodeFromUserService(NewItemConfig config, String codingKey) {
        TCUserService userService = Utils.getTCSession().getUserService();
        try {
            Object result = userService.call("cust_getNextCode", new Object[]{codingKey, config.getStartSeq()});
            if (result != null) {
                String next = (String) result;
                Utils.print2Console(next);
                if (Utils.isNull(next)) {
                    return null;
                }
                if ("0".equals(next)) {
                    return null;
                }
                return next;
            }
        } catch (TCException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public String getNextCode(NewItemConfig config, String type, String codingKey) {
        String tableName = "customer_item_id";
        int re = 0;

        Statement stmt = null;
        ResultSet rs1 = null;
        ResultSet rs3 = null;
        try {
            GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();

            HashMap dbInfo = getPreferenceUtil.getHashMapPreference(session,
                    TCPreferenceService.TC_preference_site, "FX_DB_Info", "=");
            String ip = (String) dbInfo.get("IP");
            String username = (String) dbInfo.get("UserName");
            String password = (String) dbInfo.get("Password");
            String sid = (String) dbInfo.get("SID");
            String port = (String) dbInfo.get("Port");
            System.out.println("ip == " + ip);
            System.out.println("username == " + username);
            System.out.println("password == " + password);
            System.out.println("sid == " + sid);
            System.out.println("port == " + port);
            fileStreamUtil.writeData(printStream, "ip == " + ip);
            fileStreamUtil.writeData(printStream, "username == " + username);
            fileStreamUtil.writeData(printStream, "password == " + password);
            fileStreamUtil.writeData(printStream, "sid == " + sid);
            fileStreamUtil.writeData(printStream, "port == " + port);

            if (conn == null || conn.isClosed()) {
                conn = dbUtil.getConnection(ip, username, password, sid, port);
            }


            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            String sql;
            // 若记录不存在则新建记录
            codingKey = codingKey.toUpperCase(); //大小写区分
            fileStreamUtil.writeData(printStream, "codingKey == " + codingKey);
            sql = "select count(1) from " + tableName + " where code= '" + codingKey + "' and type= '" + type + "'";
            System.out.println("sql1 == " + sql);
//			fileStreamUtil.writeData(printStream, "sql1 == "+sql);
            rs1 = stmt.executeQuery(sql);

            if (rs1.next()) {
                if (rs1.getInt(1) == 0) {
                    sql = "insert into   " + tableName + " values ('" + type + "', '" + codingKey + "',0 )";
                    System.out.println("sql2 == " + sql);
//					fileStreamUtil.writeData(printStream, "sql2 == "+sql);
                    try { // 并发创建时，后创建者会因记录已存在而失败，但不影响后续代码运行，因此放入try中
                        stmt.execute(sql);
                    } catch (Exception e) {

                    }
                }
            }
            // sql = "select seq from " + table_name + " where code= '" +
            // codingKey + "' for update";
            sql = "select seq from " + tableName + " where code= '" + codingKey + "' and type= '" + type + "'";
            System.out.println("sql3 == " + sql);
//			fileStreamUtil.writeData(printStream, "sql3 == "+sql);
            rs3 = stmt.executeQuery(sql);
            if (rs3.next()) {
                re = rs3.getInt(1) + 1;
                if (config != null && re <= config.getStartSeq()) {
                    re = config.getStartSeq() + 1;
                }
                sql = "update " + tableName + " set seq=" + re + "  where code = '" + codingKey + "'  and type= '" + type + "'";
                System.out.println("sql4 == " + sql);
//				fileStreamUtil.writeData(printStream, "sql4 == "+sql);
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            e.printStackTrace(printStream);
        } finally {

            try {
                if (conn != null) {
                    conn.commit();
                }

            } catch (SQLException e1) {
                e1.printStackTrace();
                fileStreamUtil.close(printStream);
            }

            if (rs1 != null) {
                try {
                    rs1.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs3 != null) {
                try {
                    rs3.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (conn != null) {
//				try {
//					conn.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
            }
        }
        return Integer.toString(re);
    }

    public void closeDB() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getStartSeq() {
        return startSeq;
    }

    public void setStartSeq(int startSeq) {
        this.startSeq = startSeq;
    }

    public DocumentTemplate getDocumentTemplate(String v1, String v2) {
        DocumentTemplate res = null;
        for (DocumentTemplate temp : templates) {
            Utils.print2Console("Layout:" + temp.getLayout() + " Style:" + temp.getStyle());
            if (!v1.equalsIgnoreCase(temp.getLayout())) {
                continue;
            }
            if (!v2.equalsIgnoreCase(temp.getStyle())) {
                continue;
            }
            return temp;
        }
        return res;
    }

    public DocumentTemplate getDocumentTemplate(String v3) {
        DocumentTemplate defaultDocumentTemplate = null;

        for (DocumentTemplate temp : templates) {
            if (Utils.isNull(temp.getType())) {
                continue;
            }
            if ("*".equals(temp.getType())) {
                defaultDocumentTemplate = temp;
            }
            if (temp.getType().contains(v3)) {
                return temp;
            }
        }
        return defaultDocumentTemplate;
    }

    public TCComponentDataset getDataset(String v1, String v2) {
        TCComponentDataset res = null;
        for (DocumentTemplate temp : templates) {
            Utils.print2Console("Layout:" + temp.getLayout() + " Style:" + temp.getStyle());
            if (!v1.equalsIgnoreCase(temp.getLayout())) {
                continue;
            }
            if (!v2.equalsIgnoreCase(temp.getStyle())) {
                continue;
            }
            return temp.getTemplate();
        }
        return res;
    }

    public TCComponentDataset getDataset(LinkedHashMap<String, String> propertyValueMap) {
        for (DocumentTemplate temp : templates) {
            boolean noThisTemplate = false;

            for (Entry<String, String> entry : propertyValueMap.entrySet()) {
                String propertyName = entry.getKey();
                String propertyValue = entry.getValue();
                try {
                    TCProperty tcp = temp.getForm().getTCProperty(propertyName);
                    if (tcp == null) {
                        noThisTemplate = true;
                        break;
                    }
                    String value = tcp.getStringValue();
                    if (Utils.isNull(value) && Utils.isNull(propertyValue)) {
                        continue;
                    }

                    if (!Utils.isNull(value) && !Utils.isNull(propertyValue)) {
                        if (value.equalsIgnoreCase(propertyValue)) {
                            continue;
                        }
                    }

                    if (Utils.isNull(value) && !Utils.isNull(propertyValue)) {
                        noThisTemplate = true;
                        break;
                    }

                    if (!Utils.isNull(value) && Utils.isNull(propertyValue)) {
                        noThisTemplate = true;
                        break;
                    }

                } catch (TCException e) {
                    e.printStackTrace();
                }
            }
            if (!noThisTemplate) {
                return temp.getTemplate();
            }
        }
        return null;
    }

    public TCComponentDataset getDataset(String v3) {
        TCComponentDataset defaultDataset = null;

        for (DocumentTemplate temp : templates) {
            if (Utils.isNull(temp.getType())) {
                continue;
            }
            if (temp.getType().equals("*")) {
                defaultDataset = temp.getTemplate();
            }
            if (temp.getType().contains(v3)) {
                return temp.getTemplate();
            }
        }
        return defaultDataset;
    }

//	static public String getECNID(NewItemConfig config, Map<String, String> map) {
//		if (config == null) {
//			return null;
//		}	
//		if (map == null) {
//			return null;
//		}
//		for (Entry<String, String> entry : map.entrySet()) {
//			String key = entry.getKey();
//			String value = entry.getValue();
//			Utils.print2Console("Key=" + key + ",Value=" + value);
//			fileStreamUtil.writeData(printStream, "Key=" + key + ",Value=" + value);
//		}
//		
//		String[] mapProps = config.getMapProps();
//		System.out.println("mapProps=="+Arrays.toString(mapProps));
//		HashMap<String,String> propMap = new HashMap<String, String>();
//		TCComponentItemRevision targetItemRev = config.getTargetItemRev();
//		TCComponentProject project = config.getTargetProject();
//		
//			TCComponentItem targetItem  = null;
//			try {
//				if(targetItemRev != null) {
//					targetItem = targetItemRev.getItem();
//				}
//				
//			
//			for(int i=0;i<mapProps.length;i++) {
//				String mapProp = mapProps[i];
//				Utils.print2Console( "mapProp == "+mapProp);
//				if(mapProp.contains("=")) {
//					String tempValue1 = mapProp.split("=")[0];
//					String tempValue = mapProp.split("=")[1];
//					if(tempValue.contains(":")) {
//						String[] propNames1 = tempValue1.split(":");
//						Utils.print2Console( "propNames1[0] == "+propNames1[0]);
//						Utils.print2Console( "propNames1[1] == "+propNames1[1]);
//						String[] propNames = tempValue.split(":");
//						Utils.print2Console( "propNames[0] == "+propNames[0]);
//						Utils.print2Console( "propNames[1] == "+propNames[1]);
//						String propValue = "";
//						if("revision".equals(propNames1[0])) {
//							if(targetItemRev != null) {
//								propValue = targetItemRev.getProperty(propNames1[1]);
//								//propMap.put(propNames[1], propValue);
//								if(propMap.containsKey(propNames[1])) {
//									Utils.print2Console( "propMap has "+propNames[1]);
//									String tempPropValue = propMap.get(propNames[1]);
//									Utils.print2Console( "tempPropValue == "+tempPropValue);
//									if("".equals(tempPropValue)) {
//										propMap.put(propNames[1], propValue);
//										Utils.print2Console( "propMap put  key == "+propNames[1]+",value == "+propValue);
//									}
//								} else {
//									propMap.put(propNames[1], propValue);
//									Utils.print2Console( "propMap put  key == "+propNames[1]+",value == "+propValue);
//								}
//							} else {
//								if(!propMap.containsKey(propNames[1])) {
//									propMap.put(propNames[1], "");
//									Utils.print2Console( "propMap put  key == "+propNames[1]+",value == "+propValue);
//								}
//							}
//							
//						} else if("item".equals(propNames1[0])) {
//							if(targetItem != null) {
//								propValue = targetItem.getProperty(propNames1[1]);
//								if(propMap.containsKey(propNames[1])) {
//									Utils.print2Console( "propMap has "+propNames[1]);
//									String tempPropValue = propMap.get(propNames[1]);
//									Utils.print2Console( "tempPropValue == "+tempPropValue);
//									if("".equals(tempPropValue)) {
//										propMap.put(propNames[1], propValue);
//										Utils.print2Console( "propMap put  key == "+propNames[1]+",value == "+propValue);
//									}
//								} else {
//									propMap.put(propNames[1], propValue);
//									Utils.print2Console( "propMap put  key == "+propNames[1]+",value == "+propValue);
//								}
//								//propMap.put(propNames[1], propValue);
//							} else {
//								if(!propMap.containsKey(propNames[1])) {
//									propMap.put(propNames[1], "");
//									Utils.print2Console( "propMap put  key == "+propNames[1]+",value == "+propValue);
//								}
//							}
//							
//						} else if("project".equals(propNames1[0])) {
//							if(project != null) 
//							{
//								propValue = project.getProperty(propNames1[1]);
//								if(propMap.containsKey(propNames[1])) {
//									Utils.print2Console( "propMap has "+propNames[1]);
//									String tempPropValue = propMap.get(propNames[1]);
//									Utils.print2Console( "tempPropValue == "+tempPropValue);
//									if("".equals(tempPropValue)) {
//										propMap.put(propNames[1], propValue);
//										Utils.print2Console( "propMap put  key == "+propNames[1]+",value == "+propValue);
//									}
//								} else {
//									propMap.put(propNames[1], propValue);
//									Utils.print2Console( "propMap put  key == "+propNames[1]+",value == "+propValue);
//								}
//								
//							} else {
//								if(!propMap.containsKey(propNames[1])) {
//									propMap.put(propNames[1], "");
//									Utils.print2Console( "propMap put  key == "+propNames[1]+",value == "+propValue);
//								}
//								
//							}
//							
//						}
//						Utils.print2Console( "propValue == "+propValue);
//					}
//				}
//			}
//			} catch (TCException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		
//		
//
//		List<String> idRuleList = config.getIDRuleList();
//		System.out.println("idRuleList.size() == "+idRuleList.size());
//		if(idRuleList.size() == 0) {
//			try {
//				TCComponentItemType itemType = (TCComponentItemType) session.getTypeComponent("Item");
//				return itemType.getNewID();
//			} catch (TCException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		List<String> list = new ArrayList<String>();
//		int index = -1;
//		for (int i = 0; i < idRuleList.size(); i++) {
//			String s = idRuleList.get(i);
//			System.out.println("s=="+s);
//			if (Utils.isNull(s)) {
//				return null;
//			}
//			int l = s.length();
//			if (s.startsWith("\"")) {
//				if (l < 3) {
//					continue;
//				}
//				list.add(s.substring(1, l - 1));
//			} else if (s.startsWith("[")) {
//				if (l < 3) {
//					continue;
//				}
//				String propertyName = s.substring(1, l - 1);
//				
//				String propertyValue = "";
//				if(propMap.containsKey(propertyName)) {
//					Utils.print2Console( "propMap has == " + propertyName);
//					propertyValue = propMap.get(propertyName);
//				} 
//				if("".equals(propertyValue)) {
//					if (map.containsKey(propertyName)) {
//						fileStreamUtil.writeData(printStream, "map has == " + propertyName);
//						propertyValue = map.get(propertyName);
//						fileStreamUtil.writeData(printStream, "prop value == " + propertyValue);
//						if (Utils.isNull(propertyValue)) {
//							list.add("");
//						} else {
//							list.add(propertyValue);
//						}
//					} else {
//						// Utils.print2Console("创建界面中没有 " + propertyName + "
//						// 属性,无法生成ID!", true);
//						try {
//							TCComponentItemType itemType = (TCComponentItemType) Utils.getTCSession()
//									.getTypeComponent(config.getItemTypeName());
//							TCPropertyDescriptor propertyDescriptor = itemType.getPropDesc(propertyName);
//							if (propertyDescriptor == null) {
//								TCComponentItemRevisionType itemRevisionType = itemType.getItemRevisionType();
//								propertyDescriptor = itemRevisionType.getPropertyDescriptor(propertyName);
//								if (propertyDescriptor == null) {
//									Utils.print2Console("对象类/版本类中,没有 " + propertyName + " 属性,无法生成ID!", true);
//									fileStreamUtil.writeData(printStream, "11 对象类/版本类中没有 " + propertyName + " 属性,无法生成ID!");
//									return null;
//								}
//							}
//							propertyValue = propertyDescriptor.getInitialValue();
//							if (Utils.isNull(propertyValue)) {
//								Utils.print2Console("对象类/版本类中,没有 " + propertyName + " 属性,无法生成ID!", true);
//								fileStreamUtil.writeData(printStream, "对象类/版本类中没有 " + propertyName + " 属性,无法生成ID!");
//								return null;
//							}
//							list.add(propertyValue);
//						} catch (TCException e) {
//							e.printStackTrace();
//							Utils.print2Console("创建界面中没有 " + propertyName + " 属性,无法生成ID!", true);
//							fileStreamUtil.writeData(printStream, "创建界面中没有 " + propertyName + " 属性,无法生成ID!");
//							return null;
//						}
//					}
//				} else {
//					list.add(propertyValue);
//				}
//				
//				
//				
//			}else if(s.startsWith("{")){
//				if (l < 3) {
//					continue;
//				}
//				String formartString=s.substring(1, l - 1);
//				Date date=new Date();
//				SimpleDateFormat dateFormat=new SimpleDateFormat(formartString);
//				list.add(dateFormat.format(date));
//				
//			} else if (s.startsWith("N")) {
//				list.add(s);
//				index = i;
//			}
//		}
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < list.size(); i++) {
////			if (i == index) {
////				break;
////			}
//			sb.append(list.get(i));
//		}
//		Utils.print2Console("NewItemID:" + sb.toString());
//		fileStreamUtil.writeData(printStream, "NewItemID:" + sb.toString());
//		if (index < 0) {
//			return sb.toString();
//		} else {
//			Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
//			while (iterator.hasNext()) {
//				Entry<String, String> entry = iterator.next();
//				if("fx8_Plant".equals(entry.getKey())){
//					String plant = entry.getValue();
//					String seq = getNextECNCode(config, sb.toString(),plant);
//					System.out.println("seq=="+seq);
//					int length = list.get(index).length();
//					StringBuilder seqStringBuilder = new StringBuilder();
//
//					while (seqStringBuilder.length() + seq.length() < length) {
//						seqStringBuilder.append("0");
//					}
//					if("0".equals(seq)){
//						seqStringBuilder.append("1");
//					}else{
//						seqStringBuilder.append(seq);
//					}
//					list.set(index, seqStringBuilder.toString());
//					
//					
//					break;
//				}
//			}
//			
//		}
//		System.out.println("list=="+list);
//		sb = new StringBuilder();		
//		for (String s : list) {
//			sb.append(s);
//		}
//		return sb.toString();
//	}

    static public String getNextECNCode(NewItemConfig config, String codingKey, String plant) {
        String tableName = "customer_ecn_item_id";
        int re = 0;

        Statement stmt = null;
        ResultSet rs1 = null;
        ResultSet rs3 = null;
        try {
            GetPreferenceUtil getPreferenceUtil = new GetPreferenceUtil();

            HashMap dbInfo = getPreferenceUtil.getHashMapPreference(session,
                    TCPreferenceService.TC_preference_site, "FX_DB_Info", "=");
            String ip = (String) dbInfo.get("IP");
            String username = (String) dbInfo.get("UserName");
            String password = (String) dbInfo.get("Password");
            String sid = (String) dbInfo.get("SID");
            String port = (String) dbInfo.get("Port");
            System.out.println("ip == " + ip);
            System.out.println("username == " + username);
            System.out.println("password == " + password);
            System.out.println("sid == " + sid);
            System.out.println("port == " + port);
            fileStreamUtil.writeData(printStream, "ip == " + ip);
            fileStreamUtil.writeData(printStream, "username == " + username);
            fileStreamUtil.writeData(printStream, "password == " + password);
            fileStreamUtil.writeData(printStream, "sid == " + sid);
            fileStreamUtil.writeData(printStream, "port == " + port);

            if (conn == null || conn.isClosed()) {
                conn = dbUtil.getConnection(ip, username, password, sid, port);
            }


            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            String sql;
            // 若记录不存在则新建记录
            codingKey = codingKey.toUpperCase(); //大小写区分
            fileStreamUtil.writeData(printStream, "codingKey == " + codingKey);
            sql = "select count(1) from " + tableName + " where code= '" + codingKey + "' and plant= '" + plant + "'";
            System.out.println("sql1 == " + sql);
//			fileStreamUtil.writeData(printStream, "sql1 == "+sql);
            rs1 = stmt.executeQuery(sql);

            if (rs1.next()) {
                if (rs1.getInt(1) == 0) {
                    sql = "insert into   " + tableName + " values ('" + plant + "', '" + codingKey + "',0 )";
                    System.out.println("sql2 == " + sql);
//					fileStreamUtil.writeData(printStream, "sql2 == "+sql);
                    try { // 并发创建时，后创建者会因记录已存在而失败，但不影响后续代码运行，因此放入try中
                        stmt.execute(sql);
                    } catch (Exception e) {

                    }
                }
            }
            // sql = "select seq from " + table_name + " where code= '" +
            // codingKey + "' for update";
            sql = "select seq from " + tableName + " where code= '" + codingKey + "' and plant= '" + plant + "'";
            System.out.println("sql3 == " + sql);
//			fileStreamUtil.writeData(printStream, "sql3 == "+sql);
            rs3 = stmt.executeQuery(sql);
            if (rs3.next()) {
                re = rs3.getInt(1) + 1;
                if (config != null && re <= config.getStartSeq()) {
                    re = config.getStartSeq() + 1;
                }
                sql = "update " + tableName + " set seq=" + re + "  where code = '" + codingKey + "' and plant= '" + plant + "'";
                System.out.println("sql4 == " + sql);
//				fileStreamUtil.writeData(printStream, "sql4 == "+sql);
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            e.printStackTrace(printStream);
        } finally {

            try {
                if (conn != null) {
                    conn.commit();
                }

            } catch (SQLException e1) {
                e1.printStackTrace();
                fileStreamUtil.close(printStream);
            }

            if (rs1 != null) {
                try {
                    rs1.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (rs3 != null) {
                try {
                    rs3.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (conn != null) {
//				try {
//					conn.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
            }
        }
        return Integer.toString(re);
    }

}
