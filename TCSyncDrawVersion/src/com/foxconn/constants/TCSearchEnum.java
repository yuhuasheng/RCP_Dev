package com.foxconn.constants;

public enum TCSearchEnum {
	
	__WEB_FIND_USER("__WEB_find_user", new String[] { "User ID" });

	private final String queryName; // 查询名称
	private final String[] queryParams; // 查询参数名

	private TCSearchEnum(String queryName, String[] queryParams) {
		this.queryName = queryName;
		this.queryParams = queryParams;
	}

	public String queryName() {
		return queryName;
	}

	public String[] queryParams() {
		return queryParams;
	}
}
