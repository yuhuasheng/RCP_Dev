package com.foxconn.electronics.convertebom.service;


import com.foxconn.electronics.domain.BOMPojo;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCSession;

public interface ConvertEBOMService {
	
	BOMPojo ConvertEBOM(TCSession session, TCComponentFolder selfPartFolder, BOMPojo dBOMpojo,BOMPojo eBOMpojo) throws Exception;
}
