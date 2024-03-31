package com.foxconn.electronics.domain;

public class ConnectorInfo extends CoCaInfo{

	private String type = "Connector";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ConnectorInfo [type=" + type + "]";
	}
	
}
