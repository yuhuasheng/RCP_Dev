package com.hh.tools.newitem;

public class RelationName {
	/** 外部技术资料文档 */
    final static public String OEMDOC = "FX8_OEMDocRel";
    /** PPAP */
    final static public String PPAP = "FX8_PPAPRel";
//	/** 关联的零件PPAP */
//	final static public String PARTPPAP = "FX8_PPAPRel";
    /** ECR问题零组件 */
    final static public String PROBLEMPART = "EC_problem_item_rel";
    /** ECN关联ECR */
    final static public String ECR = "EC_reference_item_rel";
    /** ECR关联ECN */
//	final static public String ECN = "CMImplementedBy";
    final static public String ECN = "EC_reference_item_rel";
//	/** PR */
//	final static public String PR = "FX8_PR_Rel";
    /** ECN改前数据（更改前零组件） */
    final static public String CHANGEBEFORE = "EC_problem_item_rel";
    /** 更改后零组件 */
    final static public String CHANGEAFTER = "EC_solution_item_rel";

    /** 关联DataSheet */
    final static public String DATASHEET = "FX8_DataSheetRel";
    /** Symbol */
    final static public String SYMBOL = "fx8_Symbol";
    /** 关联Symbol */
    final static public String SYMBOLREL = "FX8_SymbolRel";
    /** 关联BigHpSymbol */
    final static public String BIGSYMBOLREL = "FX8_HPBigSymbolRel";
    /** DellSymbol */
    final static public String DELLSYMBOL = "fx8_DellSymbol";
    /** 关联DellSymbol */
    final static public String DELLSYMBOLREL = "FX8_DellSymbolRel";
    /** 关联BigDellSymbol */
    final static public String DELLBIGSYMBOLREL = "FX8_DellBigSymbolRel";
    /** 关联FootPrint */
    final static public String FOOTPRINT = "FX8_FootprintRel";
    /** 关联PAD */
    final static public String PAD = "FX8_PadstackRel";
    /** 更改记录 */
    final static public String CHANGELIST = "FX8_ChangeListRel";
    /** checkList */
    final static public String CHECKLIST = "FX8_ChecklistRel";
    /** 关联承认 */
    final static public String APPROVEREL = "FX8_ApproveRel";
    /** 关联测试报告 */
    final static public String TESTREPORTREL = "FX8_TestReportRel";
    /** 关联仿真报告 */
    final static public String SIMULATIONREPORTREL = "FX8_SimulationReportRel";

    /** 关联环保认证表单(子表) */
    final static public String COMPLIANCES = "fx8_Comliances";

    /** 关联MCD文件 */
    final static public String MCDREL = "FX8_MCDRel";
    /** 关联MDD文件 */
    final static public String MDDREL = "FX8_MDDRel";
    /** 关联FMD文件 */
    final static public String FMDREL = "FX8_FMDRel";

    /** 关联mfr */
    final static public String MFRREL = "fx8_MfrRel";

 // 引用
    public final static String RELATION_REFERENCE = "IMAN_reference";

}
