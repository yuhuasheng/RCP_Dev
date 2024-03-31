package com.foxconn.electronics.matrixbom.export.domain;

public interface DTThermalBean {

	public void setItem(Integer item);
	
	public void setType(String type);
	
	public String getType();
	
	public void setSub(boolean sub);
	
	public void setFindNum(Integer findNum);
	
	public Integer getFindNum();
}
