package com.hh.fx.rewrite.jtree;

/**
 * 树节点数据
 * 
 * @author wangsf
 *
 */
public class TreeNodeData {

	// 节点classId
	private String classId;
	// 节点名称
	private String nodeName;
	// 节点类型
	private String nodeType;
	// 是否为父节点
	private boolean parentFlag;
	// 子节点个数
	private Integer childNodeCount;

	public TreeNodeData() {
		super();
	}

	public TreeNodeData(String classId, String nodeName, String nodeType) {
		super();
		this.classId = classId;
		this.nodeName = nodeName;
		this.nodeType = nodeType;
	}

	public TreeNodeData(String classId, String nodeName, boolean parentFlag, Integer childNodeCount) {
		super();
		this.classId = classId;
		this.nodeName = nodeName;
		this.parentFlag = parentFlag;
		this.childNodeCount = childNodeCount;
	}
	
	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public boolean isParentFlag() {
		return parentFlag;
	}

	public void setParentFlag(boolean parentFlag) {
		this.parentFlag = parentFlag;
	}

	public Integer getChildNodeCount() {
		return childNodeCount;
	}

	public void setChildNodeCount(Integer childNodeCount) {
		this.childNodeCount = childNodeCount;
	}
	
}
