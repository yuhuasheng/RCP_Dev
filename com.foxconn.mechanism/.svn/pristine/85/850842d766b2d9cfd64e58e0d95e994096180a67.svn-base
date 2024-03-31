package com.foxconn.mechanism.batchChangePhase.window;

import com.teamcenter.rac.kernel.TCComponentItemRevision;

public class PhaseBean {
	// 不合规的数据
    //ID 版本 名称 数量 状态所有者
    Boolean selected = true;
    String id;//对象item_id
    String version;//对象版本
    String name;//对象名
    String count;//数量
    String status;//状态，是否已发行
    String owner;//所有者工号
    String result = "未执行";
    TCComponentItemRevision itemRevision; //升版前 版本对象
    TCComponentItemRevision newItemRevision;//升版后  版本对象
   
    public PhaseBean(String id, String version, String name, String count, String status, String owner,TCComponentItemRevision itemRevision) {
        this.id = id;
        this.version = version;
      
        this.name = name;
        this.count = count;
        this.status = status;
        this.owner = owner;
        this.itemRevision=itemRevision;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

	public TCComponentItemRevision getItemRevision() {
		return itemRevision;
	}

	public void setItemRevision(TCComponentItemRevision itemRevision) {
		this.itemRevision = itemRevision;
	}
    
	 public String getResult() {
	      return result;
	 }

	 public void setResult(String result) {
	    this.result = result;
	 }

	public TCComponentItemRevision getNewItemRevision() {
		return newItemRevision;
	}

	public void setNewItemRevision(TCComponentItemRevision newItemRevision) {
		this.newItemRevision = newItemRevision;
	}
    
    
}
