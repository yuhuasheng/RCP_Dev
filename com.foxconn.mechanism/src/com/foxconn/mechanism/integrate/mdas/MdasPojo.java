package com.foxconn.mechanism.integrate.mdas;

/**
 * @author wt0010
 *  调用后台接口 封装实体类
 */
public class MdasPojo {
	private String bu;//分类root节点
	private String category;//分类节点
	private String type;//分类
	private String subType;//分类属性 subType
	private String ecFlag;//同步状态  0删除   1 新增
	private String part; //prt dataset uid
	private String pic; //bmp dataset uid
	private String doc; //pdf dataset uid
	private String file; //ghp dataset uid 
	private String itemId; // 分类对象 item_id
	private String threadSize=""; //分类属性 有效圈数
	private String threadLen=""; //分类属性 长度
	private String vendor;
	
	
	
	

	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getThreadSize() {
		return threadSize;
	}
	public void setThreadSize(String threadSize) {
		this.threadSize = threadSize;
	}
	public String getThreadLen() {
		return threadLen;
	}
	public void setThreadLen(String threadLen) {
		this.threadLen = threadLen;
	}
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getEcFlag() {
		return ecFlag;
	}
	public void setEcFlag(String ecFlag) {
		this.ecFlag = ecFlag;
	}
	public String getBu() {
		return bu;
	}
	public void setBu(String bu) {
		this.bu = bu;
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	
	public String getPart() {
		return part;
	}
	public void setPart(String part) {
		this.part = part;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	public String getDoc() {
		return doc;
	}
	public void setDoc(String doc) {
		this.doc = doc;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	

}
