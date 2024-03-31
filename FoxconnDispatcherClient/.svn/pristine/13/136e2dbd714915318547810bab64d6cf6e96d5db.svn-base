package com.teamcenter.ets.custom.util;

import com.teamcenter.soa.client.model.ErrorValue;
import com.teamcenter.soa.client.model.ServiceData;
import com.teamcenter.tstk.util.log.ITaskLogger;

public class Util {
	 protected ITaskLogger m_zTaskLogger;

	    public Util() {
	    }

	    protected void displayPartialErrors(ServiceData serviceData) {
	        for(int i = 0; i < serviceData.sizeOfPartialErrors(); ++i) {
	            ErrorValue[] errors = serviceData.getPartialError(i).getErrorValues();

	            for(int j = 0; j < errors.length; ++j) {
	                ErrorValue error = errors[j];
	                this.m_zTaskLogger.error("    Code: " + error.getCode() + ", " + error.getMessage());
	            }
	        }

	    }
}
