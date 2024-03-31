package com.foxconn.savedcnchange.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.foxconn.savedcnchange.domain.BOMChangeBean;
import com.foxconn.savedcnchange.domain.DCNChangeBean;
import com.foxconn.savedcnchange.domain.MaterialBean;

public interface SaveDCNServiceMapper {
	
	void insertBeforeChange(BOMChangeBean bean);
	
	Integer getBeforeChangeRecords(BOMChangeBean bean);
	
	void updateBeforeChange(BOMChangeBean bean);
	
	void insertAfterChange(BOMChangeBean bean);
	
	Integer getAfterChangeRerords(BOMChangeBean bean);
	
	void updateAfterChange(BOMChangeBean bean);	
	
	void insertMaterial(MaterialBean bean);
	
	Integer getMaterialRerords(MaterialBean bean);
	
	void updateMaterial(MaterialBean bean);
	
	void insertDCNChange(DCNChangeBean bean);
	
	Integer getDCNChangeRecord(DCNChangeBean bean);
	
	void updateDCNChange(DCNChangeBean bean);
}
