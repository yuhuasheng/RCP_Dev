package com.foxconn.electronics.L10Ebom.constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import com.foxconn.tcutils.util.TCUtil;

public class ApplyFormConstant {
	
	public static final String D9_CREATEMODE_LOV = "D9_CreateMode_LOV";
	
	public static final String D9_FINISHTYPE_LOV = "D9_FinishType_LOV";
	
	public static final String D9_YESNO_LOV = "D9_YesNo_LOV";
	
	public static final String D9_SEMITYPE_LOV = "D9_SemiType_LOV";
	
	public static final String D9_BOMREQ_L10A_REL = "D9_BOMReq_L10A_REL";	
	
	public static final String D9_BOMREQ_L10B_REL = "D9_BOMReq_L10B_REL";
	
	public static final String D9_BOMREQ_L10C_REL = "D9_BOMReq_L10C_REL";
	
	public static final String D9_BOMREQ_L10D_REL = "D9_BOMReq_L10D_REL";
	
	public static final String D9_BOMREQ_L10E_REL = "D9_BOMReq_L10E_REL";	
	
	public static final String D9_VIRTUALPART_REV = "D9_VirtualPartRevision";
	
	public static final String D9_FINISHEDPART_REV = "D9_FinishedPartRevision";
	
	public static final String D9_COMMONPART_REV = "D9_CommonPartRevision";
	
	public static final String D9_PCA_PART_REV = "D9_PCA_PartRevision";
	
	public static final String D9_L10EBOMREQ_REV = "D9_L10EBOMReqRevision";
	
	public static final String[] HALFMATARRAY = {"71407", "71408"};
	
	public static final String[] FINISHBOMARRAY = {"8", "X", "7148"};
	
	public static final String[] PANELMATARRAY = {"7604"};
	
	public static final String[] PACKASSYMATARRAY = {"713"};
	
	public static final String[] MEMATARRAY = {"71407"};
	
	public static final String[] EEMATARRAY = {"71408"};
	
	public static final String[] POWERMATARRAY = {"71408"};
	
	
	public static final String FIANL_ASSY = "FIANL ASSY";
	
	public static final String PANEL_ASSY = "PANEL ASSY";
	
	public static final String FINISHTYPE = "成品類別";
	
	public static final String ISNEWPKGBOM = "是否新建PackingASSYBOM";
	
	public static final String ISNEWPCBA = "PCBA是否新建";
	
	public static final String SEMITYPE = "半成品類別";	
	
	public static final String SHIP = "出貨地區";
	
	public static final String POWERLINETYPE = "電源線類型";
	
	public static final String PCBAINTERFACE = "PCBA接口";
	
	public static final String ISSPEAKER = "有無喇叭";
			
	public static final String COLOR = "颜色"	;
	
	public static final String WIRETYPE = "外部線材類型";
	
	public static final String SHIPSIZE = "出貨優選方式尺寸";
	
	public static final String SHIPTYPE = "出貨優選方式型式";
	
	
	
	public static final List<String> IsSpeakerList = new ArrayList<String>() {{
		add("有");
		add("無");
	}};
	
	public static String MENEWBOMWAY = "";
	public static String EENEWBOMWAY = "";
	public static String POWERNEWBOMWAY = "";
	
	
	public static final String D9_BOMREQTABLE_1 = "d9_BOMReqTable1";	
	public static final String D9_BOMREQTABLE_2 = "d9_BOMReqTable2";	
	public static final String D9_BOMREQTABLE_3 = "d9_BOMReqTable3";	
	public static final String D9_BOMREQTABLE_4 = "d9_BOMReqTable4";	
	public static final String D9_BOMREQTABLE_5 = "d9_BOMReqTable5";	
	public static final String D9_BOMREQTABLE_6 = "d9_BOMReqTable6";	
	public static final String D9_BOMREQTABLE_7 = "d9_BOMReqTable7";	
	public static final String D9_BOMREQTABLE_8 = "d9_BOMReqTable8";	
	public static final String D9_BOMREQTABLE_9 = "d9_BOMReqTable9";	
	
	
	public static Map<String, String[]> TABLEMAP = null;
	public static Object[] CREATEMODELOV = null;
	public static Object[] FINISHTYPELOV = null;
	public static Object[] YESNOLOV = null;
	public static Object[] SEMITYPELOV = null;
	
	static {
		
		CREATEMODELOV = TCUtil.getLovValues(D9_CREATEMODE_LOV);
		FINISHTYPELOV = TCUtil.getLovValues(D9_FINISHTYPE_LOV);
		YESNOLOV = TCUtil.getLovValues(D9_YESNO_LOV);
		
		ApplyFormConstant.MENEWBOMWAY = CREATEMODELOV[1].toString();
		ApplyFormConstant.EENEWBOMWAY = CREATEMODELOV[1].toString();
		ApplyFormConstant.POWERNEWBOMWAY = CREATEMODELOV[1].toString();
		
		TABLEMAP = new HashedMap<String, String[]>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		{
	        put(D9_BOMREQTABLE_1, new String[] {"項次*", "客户Model Name*", "Foxconn Model Name*", "成品料號描述*", "出貨地區*", "電源線類型*", "PCBA接口*", "有無喇叭*", "颜色*", "外部線材類型*", "其他", "出貨優選方式尺寸*", "出貨優選方式型式*", "參照BOM料號", "備註"});
	        put(D9_BOMREQTABLE_2, new String[] {"項次*", "半成品類別*", "半成品料號*", "描述*", "備註"});
	        put(D9_BOMREQTABLE_3, new String[] {"項次*", "成品類別*", "成品料號*", "描述*", "Panel料號*", "外部線材類型*", "其他", "出貨優選方式尺寸*", "出貨優選方式形式*", "参照BOM料號*", "備註"});
	        put(D9_BOMREQTABLE_4, new String[] {"項次*", "出貨地區*", "Packing ASSY料號*", "是否新建Packing ASSY BOM*", "備註"});
	        put(D9_BOMREQTABLE_5, new String[] {"項次*", "Panel類型*", "組立料號*", "備註"});
	        put(D9_BOMREQTABLE_6, new String[] {"項次*", "Panel類型*", "組立料號*", "PCBA是否新建*", "品名", "半成品料號","備註"});
	        put(D9_BOMREQTABLE_7, new String[] {"項次*", "Panel類型*", "組立料號*", "PCBA是否新建*", "品名", "半成品料號","備註"});        
	        put(D9_BOMREQTABLE_8, new String[] {"項次*", "出貨地區*", "沿用参考BOM料號*", "變更後料號", "變更後用量", "變更後供貨商", "備註"});
	        put(D9_BOMREQTABLE_9, new String[] {"項次*", "出貨地區*", "Panel類型*", "成品料號*", "外部線材類型*", "其他", "FINAL ASSY料號*", "Packing ASSY料號*", "BOM Release No.*"});
	    }};
	}	
	
}
