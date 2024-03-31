package com.foxconn.mechanism.dtpac.matmaintain.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LinkLovBean {

	private int index;
	
	private String value;
	
	private List<LinkLovBean> childs;
	
	public LinkLovBean() {
        childs = Collections.synchronizedList(new ArrayList<>());
    }

    public void addChild(LinkLovBean child) {
        this.childs.add(child);
    }
    
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<LinkLovBean> getChilds() {
		return childs;
	}

	public void setChilds(List<LinkLovBean> childs) {
		this.childs = childs;
	}
    
    
}
