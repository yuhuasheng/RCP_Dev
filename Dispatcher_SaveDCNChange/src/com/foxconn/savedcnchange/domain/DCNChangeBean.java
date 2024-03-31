package com.foxconn.savedcnchange.domain;

import java.util.Date;

import com.foxconn.savedcnchange.constant.DCNChangeConstant;
import com.foxconn.savedcnchange.serial.SerialCloneable;
import com.foxconn.savedcnchange.util.TCPropertes;

public class DCNChangeBean extends SerialCloneable {	
	
	private static final long serialVersionUID = 1L;

	private String dcnItemId;
	
//	@TCPropertes(tcProperty = "d9_ChangeType", type = DCNChangeConstant.MNT_DCN_FORM_TYPE)
	@TCPropertes(tcProperty = "d9_ChangeType", type = "Form")
	private String plantCode;	
	
	private String status;
	
	private Date validDate;
	
	@TCPropertes(tcProperty = "object_desc")
	private String description;
	
	@TCPropertes(tcProperty = "d9_ActualUserID")
	private String actualUserId;

	public String getDcnItemId() {
		return dcnItemId;
	}

	public void setDcnItemId(String dcnItemId) {
		this.dcnItemId = dcnItemId;
	}

	public String getPlantCode() {
		return plantCode;
	}

	public void setPlantCode(String plantCode) {
		this.plantCode = plantCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

	public Date getValidDate() {
		return validDate;
	}

	public void setValidDate(Date validDate) {
		this.validDate = validDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getActualUserId() {
		return actualUserId;
	}

	public void setActualUserId(String actualUserId) {
		this.actualUserId = actualUserId;
	}
	
	
}
