package com.foxconn.electronics.matrixbom.export;

import java.io.File;
import java.util.List;

import com.foxconn.tcutils.util.Pair;


public class MatrixBOMExportBean implements Comparable<MatrixBOMExportBean>{
	@MatrixExportProperty(tcProperty =  "bl_sequence_no")
	public String bomItem;
	public String code;
	@MatrixExportProperty(tcProperty = "item_id")
	public String parentNumber;
	public String hhpn;
	public String unit;
	public String programName;
	public String category;
	public String codeName;
	public String description;
	public String quantity;
	public String vendor;
	public String lenovoPN;
	public String vendorPN;
	public String coolerVendor;
	public String fruPN;
	public String coolerVendorPN;
	public String coolerFanVendor;
	public String coolerFanModelNo;
	public File specPic;
	public File screwPic;
	public String factory;
	public String remark;
	public String total;
	public String torqueIn;
	public String torqueOut;
	public String chassis;
	public String rating;
	public String type;
	public String revName;
	public String uom;
	public int layer;
	public boolean isSubstitutes;
	public List<Pair<String,Integer>> variablesList;
	public String varType;
	
	public List<MatrixBOMExportBean> substitutes;
	public List<MatrixBOMExportBean> packList;
	@Override
	public int compareTo(MatrixBOMExportBean o) {
		return this.bomItem.compareTo(o.bomItem);
	}
	
}
