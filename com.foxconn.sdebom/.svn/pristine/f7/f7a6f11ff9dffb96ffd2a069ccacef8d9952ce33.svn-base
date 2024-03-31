package com.foxconn.sdebom.service;

import java.util.List;

import com.foxconn.sdebom.dtl5ebom.pojo.DtL5BomPojo;
import com.foxconn.sdebom.dtl5ebom.pojo.DtL5ItemRevPojo;
import com.foxconn.sdebom.pojo.BomLinePojo;
import com.foxconn.sdebom.pojo.ItemRevPojo;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

public interface BomService {
	
	List<DtL5ItemRevPojo> jsonConvertItemRevPojo(String json) throws Exception;
	
	void createItemRev(List<DtL5ItemRevPojo> dtL5ItemRevPojos) throws Exception;
	
	List<DtL5BomPojo> jsonConvertBomPojo(String json) throws Exception;

	BomLinePojo itemRevConvertBomPojo(TCComponentItemRevision itemRev) throws Exception;

	abstract void firstBuild(BomLinePojo b, DtL5ItemRevPojo dtL5ItemRevPojo) throws Exception;
	
	abstract void changeBuild(BomLinePojo b1, BomLinePojo b2) throws Exception;
	
}
