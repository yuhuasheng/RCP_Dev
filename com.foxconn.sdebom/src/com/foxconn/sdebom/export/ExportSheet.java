package com.foxconn.sdebom.export;

import java.util.ArrayList;
import java.util.List;

import com.teamcenter.rac.kernel.TCComponentBOMLine;

public class ExportSheet {
	
	public static int TYPE_BOM = 1;
	public static int TYPE_OPTION = 2;
	public static int TYPE_SREW = 3;
	
	public int type;
	public String name;
	public TCComponentBOMLine bomLine;	
	
	public List<ExportRow> rowList = new ArrayList<>();

}
