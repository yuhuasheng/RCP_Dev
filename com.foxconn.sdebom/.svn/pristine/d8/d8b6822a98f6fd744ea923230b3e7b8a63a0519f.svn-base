package com.foxconn.sdebom.factory;

import com.foxconn.sdebom.constant.D9Constant;
import com.foxconn.sdebom.dtl5ebom.impl.DtL5Bom;
import com.foxconn.sdebom.service.BomService;
import com.teamcenter.rac.kernel.TCSession;

public class BomFactory{

	public static BomService getBomType(TCSession session, String bomType) throws Exception {
		if (bomType.equals(D9Constant.DT_L5)) {
			return new DtL5Bom(session);
		}
		return null;
	}
	
}
