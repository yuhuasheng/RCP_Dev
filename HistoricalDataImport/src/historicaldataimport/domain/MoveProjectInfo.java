package historicaldataimport.domain;

public class MoveProjectInfo {

	private String seriesId;
	private String seriesName;
	private String projectId;
	private String projectName;
	
	public String getSeriesId() {
		return seriesId;
	}
	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}
	public String getSeriesName() {
		return seriesName;
	}
	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	@Override
	public String toString() {
		return "MoveProjectInfo [seriesId=" + seriesId + ", seriesName=" + seriesName + ", projectId=" + projectId
				+ ", projectName=" + projectName + "]";
	} 
	
}
