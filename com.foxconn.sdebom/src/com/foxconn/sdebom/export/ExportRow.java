package com.foxconn.sdebom.export;

import java.io.File;

public class ExportRow {
	
	public String hhpin;//对象ITEM_ID
	public String hppin;//对象版本属性d9_CustomerPN
	public int layer;
	public int substituteIndex;
	public String hpDescription;//对象版本属性d9_CustomerPNDescription
	public String description;//对象版本属性d9_EnglishDescription
	public String assemblyCode;//d9_AssemblyCode
	public String qty;//来源BOMline.bl_quantity
	public String matl;//对象版本属性d9_MATL
	public String remark;//来源BOMline.D9_Remark
	public String ccl;//来源BOMline.bl_occ_d9_CCL
	public File picture;//来源当前对象关系文件夹“提供者”下对象缩略图，大小为原图大小的42%
	public String partName;//对象版本属性d9_EnglishDescription
	public String purpose;//来源BOMline.bl_occ_d9_Purpose
	public String headType;//对象版本属性d9_HeadType
	public String torque;//来源BOMline.bl_occ_d9_Torque
	public String assyBy;//来源BOMline.bl_occ_d9_AssyBy
	

}
