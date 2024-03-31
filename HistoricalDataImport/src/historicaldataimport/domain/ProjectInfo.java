package historicaldataimport.domain;

public class ProjectInfo {
	
	private String projectId;//专案ID
	private String seriesId;//系列ID
	private String projectName;//专案名
	private String projectLevel;//专案等级
	private String projectPhase;//专案阶段
	private String productLineId;//产品线ID
	private String productLineName;//产品线名
	private int resultsCount;//查询结果的数量
	
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getSeriesId() {
		return seriesId;
	}
	public void setSeriesId(String seriesId) {
		this.seriesId = seriesId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectLevel() {
		return projectLevel;
	}
	public void setProjectLevel(String projectLevel) {
		this.projectLevel = projectLevel;
	}
	public String getProjectPhase() {
		return projectPhase;
	}
	public void setProjectPhase(String projectPhase) {
		this.projectPhase = projectPhase;
	}
	public String getProductLineId() {
		return productLineId;
	}
	public void setProductLineId(String productLineId) {
		this.productLineId = productLineId;
	}
	public String getProductLineName() {
		return productLineName;
	}
	public void setProductLineName(String productLineName) {
		this.productLineName = productLineName;
	}
	public int getResultsCount() {
		return resultsCount;
	}
	public void setResultsCount(int resultsCount) {
		this.resultsCount = resultsCount;
	}
	
	@Override
	public String toString() {
		return "ProjectInfo [projectId=" + projectId + ", seriesId=" + seriesId + ", projectName=" + projectName
				+ ", projectLevel=" + projectLevel + ", projectPhase=" + projectPhase + ", productLineId="
				+ productLineId + ", productLineName=" + productLineName + ", resultsCount=" + resultsCount + "]";
	}
	
}
