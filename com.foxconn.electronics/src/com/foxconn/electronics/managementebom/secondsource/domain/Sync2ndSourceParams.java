package com.foxconn.electronics.managementebom.secondsource.domain;

public class Sync2ndSourceParams {	
	
	private String projectName;
	
	private String projectID;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((projectID == null) ? 0 : projectID.hashCode());
		result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sync2ndSourceParams other = (Sync2ndSourceParams) obj;
		if (projectID == null) {
			if (other.projectID != null)
				return false;
		} else if (!projectID.equals(other.projectID))
			return false;
		if (projectName == null) {
			if (other.projectName != null)
				return false;
		} else if (!projectName.equals(other.projectName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Sync2ndSourceParams [projectName=" + projectName + ", projectID=" + projectID + "]";
	}
	
	
}
