package com.foxconn.electronics.L10Ebom.response;

public class OtherResultBean extends BaseResultBean{
    private String modelName;
	
	private String derivativeType;
	
	private String pacType;

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getDerivativeType() {
		return derivativeType;
	}

	public void setDerivativeType(String derivativeType) {
		this.derivativeType = derivativeType;
	}

	public String getPacType() {
		return pacType;
	}

	public void setPacType(String pacType) {
		this.pacType = pacType;
	}
	
	

}
