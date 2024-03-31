package com.foxconn.mechanism.hhpnmaterialapply.domain;

import java.util.List;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.foxconn.mechanism.hhpnmaterialapply.CustTreeNode;
import com.foxconn.mechanism.hhpnmaterialapply.serial.SerialCloneable;
import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class BOMInfo extends SerialCloneable {	
	private static final long serialVersionUID = 1L;
	
	private String itemId; // 零组件ID	
	private String version; // 版本	
	private String objectName; // 名称
	private String title;
	private String objectType; // 类型
	private Image image; // 图标类型		
	private PropertiesInfo propertiesInfo;	
	private List<BOMInfo> child;
    private BOMInfo parent;
    private TCComponentItemRevision itemRevision;
    private Color color = null;
    private Color highLight = null;
    private Color beforeModifyColor = null; // 修改前的颜色
    private Boolean modify; // 判断是否有修改权, true为有修改权，false为没有修改权   
    private Boolean addFlag = true; // 是否可以添加到设计对象下的标识， true为可以添加，false为不可以添加
    private Boolean deleteFlag = false; // 是否为删除的标识  true代表已经删除， false代表 删除
    private Boolean isExist; // 判断料号对象是否存在TC中的标识， true代表已存在，false代表未存在
    private Boolean removeFlag = true; //用作右键是否可以显示移除按钮的判断标识，默认代表可以显示 
    private Boolean partRevModify = true; // 物料对象当前用户是否含有编辑权, 默认代表有
    private Boolean syncFlag = true; // 用做能否(将设计对象属性同步物料属性/物料属性同步设计对象属性)的标识, 默认是设置为可以同步
    private Boolean syncResult = false; // 用作是否已经同步结果的标识，用作下一步是否展开的标识
    private CustTreeNode custTreeNode; // 当前数据类绑定的节点类
    
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public PropertiesInfo getPropertiesInfo() {
		return propertiesInfo;
	}
	public void setPropertiesInfo(PropertiesInfo propertiesInfo) {
		this.propertiesInfo = propertiesInfo;
	}
	public List<BOMInfo> getChild() {
		return child;
	}
	public void setChild(List<BOMInfo> child) {
		this.child = child;
	}
	public BOMInfo getParent() {
		return parent;
	}
	public void setParent(BOMInfo parent) {
		this.parent = parent;
	}
	public TCComponentItemRevision getItemRevision() {
		return itemRevision;
	}
	public void setItemRevision(TCComponentItemRevision itemRevision) {
		this.itemRevision = itemRevision;
	}
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	
	public Color getBeforeModifyColor() {
		return beforeModifyColor;
	}
	public void setBeforeModifyColor(Color beforeModifyColor) {
		this.beforeModifyColor = beforeModifyColor;
	}
	public Boolean getModify() {
		return modify;
	}
	public void setModify(Boolean modify) {
		this.modify = modify;
	}
	public Boolean getAddFlag() {
		return addFlag;
	}
	public void setAddFlag(Boolean addFlag) {
		this.addFlag = addFlag;
	}
	public Boolean getDeleteFlag() {
		return deleteFlag;
	}
	public void setDeleteFlag(Boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	public Boolean getIsExist() {
		return isExist;
	}
	public void setIsExist(Boolean isExist) {
		this.isExist = isExist;
	}
	public Boolean getRemoveFlag() {
		return removeFlag;
	}
	public void setRemoveFlag(Boolean removeFlag) {
		this.removeFlag = removeFlag;
	}
	public Boolean getPartRevModify() {
		return partRevModify;
	}
	public void setPartRevModify(Boolean partRevModify) {
		this.partRevModify = partRevModify;
	}
	public Boolean getSyncFlag() {
		return syncFlag;
	}	
	public void updateSyncFlag(Boolean syncFlag) {
		this.syncFlag = syncFlag;
	}	
	public Boolean getSyncResult() {
		return syncResult;
	}	
	public void updateSyncResult(Boolean syncResult) {
		this.syncResult = syncResult;
	}
	public CustTreeNode getCustTreeNode() {
		return custTreeNode;
	}
	public void setCustTreeNode(CustTreeNode custTreeNode) {
		this.custTreeNode = custTreeNode;
	}
	public Color getHighLight() {
		return highLight;
	}
	public void setHighLight(Color highLight) {
		this.highLight = highLight;
	}	
	
	
}
