package com.foxconn.sdebom.batcheditorebom.constants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SDEBOMConstant {
		public static final List<String> VIRTUALLIST = new ArrayList<String> () {
		private static final long serialVersionUID = 1L;

		{
			add("D9_BOMTopNodeRevision");
			add("D9_BOMColNodeRevision");
			add("D9_BOMOptNodeRevision");
			add("D9_BOMLtNodeRevision");
		}};
		
		public static List<String> EXPORTLIST = new ArrayList<String>() {{
			add(LovEnum.ShowLov.showValue());
			add(LovEnum.HidenLov.showValue());
		}};	
		
		
		public static final String[] ALL_PART_ATTRS = {"item_id", "item_revision_id", "d9_CustomerPN", "d9_CustomerPNDescription", "d9_EnglishDescription"};
		
		public static final String[] ALL_BOM_ATTRS = {"bl_sequence_no", "bl_indented_title", "fnd0bl_is_mono_override"};
		
}
