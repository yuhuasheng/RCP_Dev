package com.foxconn.electronics.issuemanagement.domain;

import java.util.Date;

import cn.hutool.core.date.DateUtil;

public class FileRes {
	private String fileName;
	private String createBy;
	private String createDate;
	private Long createTime;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public FileRes() {
		super();
	}
	public FileRes(String fileName, String createBy,Date createTime) {
		super();
		this.fileName = fileName;
		this.createBy = createBy;
		this.createTime = DateUtil.date(createTime).getTime();
		this.createDate = DateUtil.format(createTime, "yyyy-MM-dd HH:mm:ss");
	}

	
}
