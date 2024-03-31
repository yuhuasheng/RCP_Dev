package com.foxconn.mechanism.pacmaterial;

import com.teamcenter.rac.kernel.TCComponentProject;

public class PACProjectBean {
	private String id;
	private String name;
	private TCComponentProject project;
	
	
	
	public TCComponentProject getProject() {
		return project;
	}
	public void setProject(TCComponentProject project) {
		this.project = project;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
//		return "ProjectInfo [id=" + id + ", name=" + name + "]";
		return "("+id+")"+name;
	}

}
